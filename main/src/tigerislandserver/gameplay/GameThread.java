package tigerislandserver.gameplay;

import tigerisland.datalogger.DataLogger;
import tigerisland.datalogger.LoggerFactory;
import tigerisland.datalogger.SQLiteLogger;
import tigerisland.game.GameManager;
import tigerisland.player.PlayerID;
import tigerisland.player.Player;
import tigerisland.tile.*;
import tigerisland.score.*;
import tigerislandserver.adapter.OutputAdapter;
import tigerislandserver.server.TournamentPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameThread extends Thread{


    Map<Integer, String> playersIdToUserName;
    private int cid;
    private DataLogger logger;
    private ArrayList<Tile> gameTiles;
    private ArrayList<TournamentPlayer> playersInGame;
    private int activePlayerIndex;
    private char gameID;
    private TournamentScoreboard scoreboard;
    private TournamentScoreboardData tourneyDataPlayer1;
    private TournamentScoreboardData tourneyDataPlayer2;
    private boolean gameNotEnded;
    private GameManager gameManager;
    private String endGameMessage;
    private AtomicBoolean hasturnWaitingp = new AtomicBoolean(false);

    public GameThread(TournamentPlayer player1, TournamentPlayer player2, ArrayList<Tile> tiles, char gameLetter, int cid, TournamentScoreboard scoreboard, long matchID){
        playersInGame = new ArrayList<TournamentPlayer>();
        playersInGame.add(player1);
        playersInGame.add(player2);

        activePlayerIndex = 0;
        gameTiles = tiles;
        gameID = gameLetter;
        this.cid=cid;
        this.scoreboard=scoreboard;

        gameNotEnded = true;

        ArrayList<Player> gamePlayers=new ArrayList<Player>();

        for(TournamentPlayer tp: playersInGame)
        {
            gamePlayers.add(new Player(tp.getID()));
        }
        int matchId = (int)matchID;

        playersIdToUserName = new HashMap<>();
        playersIdToUserName.put(player1.getID().getId(), player1.getUsername());
        playersIdToUserName.put(player2.getID().getId(), player2.getUsername());
        logger = LoggerFactory.getLogger(gameLetter, cid, matchId, playersIdToUserName);

        gameManager = new GameManager(gamePlayers, logger );
    }

    public ScoreManager getScoreManager(){
        return gameManager.getScoreManager();
    }

    public int getPlayerFinalScore(int playerIndex){
        TournamentPlayer player1 = playersInGame.get(playerIndex);
        PlayerID pID = player1.getID();

        return gameManager.getScoreManager().getPlayerScore(pID);
    }

    // to hold the data the tournament scoreboard needs
    // there is a new class that has been made
    public ArrayList<TournamentScoreboardData> makeTournamentScoreboardDataList(){
        int player1Index = 0;
        int player2Index = 1;
        TournamentScoreboardData player1Data = makeTournamentScoreboardData(player1Index);
        TournamentScoreboardData player2Data = makeTournamentScoreboardData(player2Index);

        ArrayList<TournamentScoreboardData> playerData = new ArrayList<>();
        playerData.add(player1Data);
        playerData.add(player2Data);

        return playerData;
    }

    public TournamentScoreboardData makeTournamentScoreboardData(int playerIndex){
        Player player = getPlayerFromPID(playersInGame.get(playerIndex).getID());

        int finalScore = getPlayerFinalScore(playerIndex);

        TournamentScoreboardData data = new TournamentScoreboardData(player, finalScore);
        return data;
    }


    public Player getPlayerFromPID(PlayerID pID){
        Player player = gameManager.getPlayer(pID);
        return player;
    }

    public void sendStartGameMessage(){
        // to send starting game info that dave is probably going to make us send
        // needs to utilize the commands int he connection classes
        //player1
        //player2
        logger.writeGameStarted(playersInGame.get(0).getID(), playersInGame.get(1).getID());
    }

    public int getActivePlayerIndex(){
        return activePlayerIndex;
    }

    public ArrayList<Tile> getGameTiles(){
        return gameTiles;
    }



    public boolean makeBuildMove(){

        return false;
    }


    public void generateEndGameMessage(){
        TournamentPlayer p1 = playersInGame.get(0);
        TournamentPlayer p2 = playersInGame.get(1);
        ScoreManager sm =gameManager.getScoreManager();
        endGameMessage = OutputAdapter.returnEndGameMessage(p1, p2, gameID, ""+sm.getPlayerScore(p1.getID()), ""+sm.getPlayerScore(p2.getID()));
        gameNotEnded=false;
    }

    public long getGameID(){
        return gameID;
    }


    // CHRISTINE this is where we log the individual game scores as well, gross but its gonna be here
    // grab the game scoremanager from the game manager and grab those scores
    public void run(){
        sendStartGameMessage();

        //Total Moves between both players that have happened
        // not turn number relative to a single player.
        int moveNumber = 0;

        while(gameNotEnded)
        {
            int playerTurnNumber = moveNumber/2;
            Tile tile = gameTiles.get(moveNumber);

            playersInGame.get(activePlayerIndex).requestMove(this, gameID, playerTurnNumber, tile);

            if (gameEndedWithValidWin()){
                ArrayList<TournamentScoreboardData> playerData = makeTournamentScoreboardDataList();
                scoreboard.updateTournamentScoresForValidWin(playerData);
                PlayerID player1ID = playersInGame.get(0).getID();
                PlayerID player2ID = playersInGame.get(1).getID();

                SQLiteLogger sqlLogger = LoggerFactory.getSQLLogger('Z',-1,-1, playersIdToUserName);
                sqlLogger.setPlayerScore(cid, player1ID, scoreboard.getPlayerScore(player1ID));
                sqlLogger.setPlayerScore(cid, player2ID, scoreboard.getPlayerScore(player2ID));

                endGame();
                generateEndGameMessage();
            }

            moveNumber++;

            // CHRISTINE this is where we would get the score of the game at the end of the turn
            // and the move number

            logger.nextTurn();
            activePlayerIndex = (activePlayerIndex + 1) % playersInGame.size();
        }

        // if this is triggered its because the game did not end with a valid win
        PlayerID player1ID = playersInGame.get(0).getID();
        PlayerID player2ID = playersInGame.get(1).getID();
        SQLiteLogger sqlLogger = LoggerFactory.getSQLLogger('Z',-1,-1, playersIdToUserName);
        sqlLogger.setPlayerScore(cid, player1ID, scoreboard.getPlayerScore(player1ID));
        sqlLogger.setPlayerScore(cid, player2ID, scoreboard.getPlayerScore(player2ID));
        logger.writeGameEnded(playersInGame.get(0).getID(), playersInGame.get(1).getID(), endGameMessage);
    }

    public boolean playerUsedAllOfTwoTiles(PlayerID pID){
        ArrayList<Player> players = gameManager.getPlayers();
        Player player1 = players.get(0);
        Player player2 = players.get(1);

        Player player = new Player();

        if(player1.getId() == pID){
            player = player1;
        }
        else if(player2.getId() == pID){
            player = player2;
        }

        if((player.getTigerCount() == 0) && (player.getTotoroCount() == 0)){
            return true;
        }
        else if((player.getTigerCount() == 0) && (player.getVillagerCount() == 0)){
            return true;
        }
        else if((player.getTotoroCount() == 0) && (player.getVillagerCount() == 0)){
            return true;
        }

        return false;
    }

    public boolean gameEndedWithValidWin(){
        //there are two ways the game ends validly
        // Player has only one type of piece left
        // Player wins tie
        TournamentPlayer player = playersInGame.get(activePlayerIndex);
        PlayerID pID = player.getID();

        boolean usedAllOfTwo = playerUsedAllOfTwoTiles(pID);
        boolean allTilesDrawn = noMoreTilesAreLeftToPlace();

        return usedAllOfTwo || allTilesDrawn;
    }

    public boolean noMoreTilesAreLeftToPlace(){
        if(gameManager.getTilesDrawn() == 48){
            return true;
        }

        return false;
    }

    public ArrayList<TournamentPlayer> generatePlayerToReturnToScoreboard(TournamentPlayer player){
        ArrayList<TournamentPlayer> players = new ArrayList<>();

        TournamentPlayer firstPlayer = playersInGame.get(0);
        TournamentPlayer secondPlayer =playersInGame.get(1);


        if (player.getID() == firstPlayer.getID()){
            players.add(firstPlayer);
            players.add(secondPlayer);
        }
        else if(player.getID() == secondPlayer.getID()){
            players.add(secondPlayer);
            players.add(firstPlayer);
        }

        return players;
    }

    public boolean isGameDone()
    {
        return !gameNotEnded;
    }

    public void endGame()
    {
        gameNotEnded = false;
    }

    // pass botht the players so you can immediately add in the score

    public void timeout(TournamentPlayer tournamentPlayer)
    {
        endGameMessage = OutputAdapter.returnEndGameMessage(tournamentPlayer, otherPlayer(tournamentPlayer), gameID, "FORFEITED", "WIN");
   //     OutputAdapter.returnEndGameMessage(otherPlayer(tournamentPlayer), tournamentPlayer, gameID, "WIN", "FORFEITED");

        gameNotEnded=false;
        ArrayList<TournamentPlayer> players = generatePlayerToReturnToScoreboard(tournamentPlayer);

        scoreboard.playerTimedOut(players);
    }

    public void unableToBuild(TournamentPlayer tournamentPlayer)
    {
        endGameMessage = OutputAdapter.returnEndGameMessage(tournamentPlayer, otherPlayer(tournamentPlayer), gameID, "FORFEITED", "WIN");
 //       OutputAdapter.returnEndGameMessage(otherPlayer(tournamentPlayer), tournamentPlayer, gameID, "WIN", "FORFEITED");

        gameNotEnded=false;
        ArrayList<TournamentPlayer> players = generatePlayerToReturnToScoreboard(tournamentPlayer);

        boolean didTheyPlaceTigerOrTotoro = gameManager.totoroOrTigerPlaced(tournamentPlayer.getID());

        if (didTheyPlaceTigerOrTotoro){
            scoreboard.playerWasUnableToBuildAndPlacedSpecialPiece(players);
        }
        else {
            scoreboard.playerWasUnableToBuildAndPlacedNoSpecialPieces(players);
        }
    }

    public void invalidTilePlacement(TournamentPlayer tournamentPlayer)
    {
        endGameMessage = OutputAdapter.returnEndGameMessage(tournamentPlayer, otherPlayer(tournamentPlayer), gameID, "FORFEITED", "WIN");
   //     OutputAdapter.returnEndGameMessage(otherPlayer(tournamentPlayer), tournamentPlayer, gameID, "WIN", "FORFEITED");
        gameNotEnded=false;
        ArrayList<TournamentPlayer> players = generatePlayerToReturnToScoreboard(tournamentPlayer);

        scoreboard.playerPlacedInvalidTile(players);
    }

    public void invalidBuild(TournamentPlayer tournamentPlayer)
    {
        endGameMessage = OutputAdapter.returnEndGameMessage(tournamentPlayer, otherPlayer(tournamentPlayer), gameID, "FORFEITED", "WIN");
 //       OutputAdapter.returnEndGameMessage(otherPlayer(tournamentPlayer), tournamentPlayer, gameID, "WIN", "FORFEITED");
        gameNotEnded=false;
        ArrayList<TournamentPlayer> players = generatePlayerToReturnToScoreboard(tournamentPlayer);
        scoreboard.playerMadeInvalidBuild(players);
    }

    public GameManager getGameManager()
    {
        return gameManager;
    }

    public ArrayList<TournamentPlayer> getPlayersInGame()
    {
        return playersInGame;
    }

    private TournamentPlayer otherPlayer(TournamentPlayer tp)
    {
        return playersInGame.get((playersInGame.indexOf(tp)+1)%2);
    }

    public void sendEndGameMessage()
    {
        OutputAdapter.sendMessage(playersInGame, endGameMessage);
    }

    public boolean hasTurnWaiting() {

        return hasturnWaitingp.get();
    }

    public void makeMove() {
        hasturnWaitingp.set(false);
    }

    public void enableTurnWaiting() {
        // This should only be called by requestMove in tourny player, bad bad interface atm!!
        hasturnWaitingp.set(true);
    }
}
