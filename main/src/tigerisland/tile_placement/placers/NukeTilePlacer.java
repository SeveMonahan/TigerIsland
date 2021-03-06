package tigerisland.tile_placement.placers;

import tigerisland.board.Board;
import tigerisland.board.Location;
import tigerisland.hex.Hex;
import tigerisland.piece.PieceBoard;
import tigerisland.tile.Tile;
import tigerisland.tile.TileUnpacker;
import tigerisland.tile_placement.exceptions.TilePlacementException;
import tigerisland.tile_placement.rules.NukePlacementRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NukeTilePlacer  implements  TilePlacement, TilePlacementChain{

    TilePlacement nextTilePlacement;
    List<NukePlacementRule> nukeTilePlacementRules;
    Board board;
    PieceBoard pieceBoard;

    public NukeTilePlacer(Board board, PieceBoard pieceBoard, NukePlacementRule... rules) {

        this.board = board;
        this.pieceBoard = pieceBoard;
        nukeTilePlacementRules = new ArrayList<>();
        for(NukePlacementRule rule : rules) {
            nukeTilePlacementRules.add(rule);
        }
    }
    
    @Override
    public void placeTile(Tile tile, Location location) throws TilePlacementException {

        Map<Location, Hex> hexes = TileUnpacker.getTileHexes(tile, location);

        if ( aHexIsUnUsed(hexes) ) {
            nextTilePlacement(tile, location);
        }
        else {

            applyNukeRules(hexes);

            removePiecesFromBoard(hexes);

            placeHexesOnBoard(hexes);
        }
    }

    private void applyNukeRules(Map<Location, Hex> hexes) throws TilePlacementException {
        for(NukePlacementRule rule : nukeTilePlacementRules) {
                rule.applyRule(hexes);
        }
    }

    private boolean aHexIsUnUsed(Map<Location, Hex> hexes) {

        for(Location location : hexes.keySet()) {

            if (this.board.isLocationUsed(location) == false) {
                return true;
            }
        }
        return false;
    }

    private void placeHexesOnBoard(Map<Location, Hex> hexes) {
        hexes.forEach( (location, hex) -> {

           Hex mapHex = this.board.getHex(location);
           hex.setLevel(mapHex.getLevel()+1);
           this.board.placeHex(location, hex);

        });
    }

    private void removePiecesFromBoard(Map<Location, Hex> hexes) {
         hexes.forEach( (location, hex) -> {
             this.pieceBoard.removePiece(location);
        });
    }


    @Override
    public void setNextTilePlacement(TilePlacement tilePlacement) {
        this.nextTilePlacement = tilePlacement;
    }

    @Override
    public void nextTilePlacement(Tile tile, Location location) throws TilePlacementException {
        this.nextTilePlacement.placeTile(tile, location);
    }
}
