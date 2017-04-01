package tigerislandserver.gameplay;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class RoundRobinMatchupTest {
    Scheduler scheduler;
    RoundRobinMatchup matchmaker;

    @Before
    public void setUp() throws Exception {
        int numOfPlayers = 9;
        scheduler = new Scheduler(numOfPlayers);
        matchmaker = new RoundRobinMatchup(scheduler, numOfPlayers);
    }

    @Test
    public void getMatchups() throws Exception {
        ArrayList<Matchup> newMatchups = matchmaker.getMatchups(3);
        assertEquals(4, newMatchups.size());

        Matchup firstMatchup, thirdMatchup;

        firstMatchup = newMatchups.get(0);
        thirdMatchup = newMatchups.get(2);

        assertEquals(8, firstMatchup.getPlayer1Index());
        assertEquals(7, firstMatchup.getPlayer2Index());
        assertEquals(1, thirdMatchup.getPlayer1Index());
        assertEquals(5, thirdMatchup.getPlayer2Index());
    }

    @Test
    public void getTotalRounds() throws Exception {
        assertEquals(9, matchmaker.getTotalRounds());
    }

    @Test
    public void getTotalMatches() throws Exception {
        assertEquals(36, matchmaker.getTotalMatches());
    }

}