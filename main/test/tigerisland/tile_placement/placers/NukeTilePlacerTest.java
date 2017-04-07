package tigerisland.tile_placement.placers;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import tigerisland.board.Board;
import tigerisland.board.HexBoard;
import tigerisland.board.Location;
import tigerisland.hex.Hex;
import tigerisland.tile.Tile;
import tigerisland.tile_placement.exceptions.InvalidTilePlacementException;
import tigerisland.tile_placement.placers.FirstTilePlacer;
import tigerisland.tile_placement.placers.InvalidTilePlacer;
import tigerisland.tile_placement.placers.NukeTilePlacer;
import tigerisland.tile_placement.placers.TilePlacement;
import tigerisland.tile_placement.rules.NukeCoverHexesLevelRule;
import tigerisland.tile_placement.rules.NukePlacementRule;

public class NukeTilePlacerTest {

    Board board;

    TilePlacement invalidTilePlacement;
    NukePlacementRule nukeCoverHexesLevelRule;
    NukeTilePlacer nukeTilePlacer;
    FirstTilePlacer firstTilePlacer;


    @Before
    public void setup() {
        this.board = new HexBoard();

        invalidTilePlacement = new InvalidTilePlacer();
        nukeCoverHexesLevelRule  = new NukeCoverHexesLevelRule(board);

        nukeTilePlacer = new NukeTilePlacer(
                board,
                nukeCoverHexesLevelRule);

        nukeTilePlacer.setNextTilePlacement(invalidTilePlacement);

        firstTilePlacer = new FirstTilePlacer(board);
        firstTilePlacer.setNextTilePlacement(nukeTilePlacer);

    }

    @Test(expected = InvalidTilePlacementException.class)
    public void test_ShouldCallNextTilePlaceWhenBoardIsEmpty() throws Throwable {

        // Arrange
        Tile tile = new Tile();
        Location location = new Location(0, 0, 0);

        // Act
        this.nukeTilePlacer.placeTile(tile, location);

        // Assert
        int expectedBoardSize = 0;
        Assert.assertEquals(expectedBoardSize, board.getSize());
    }

    @Test()
    public void test_ShouldPlace3HexesOnBoardWithLevel2() throws Throwable {

        // Arrange
        int tileId = 1;
        Tile tile = new Tile(tileId);
        Location location = new Location(0, 0, 0);

        firstTilePlacer.placeTile(tile, location);

        // Act
        nukeTilePlacer.placeTile(tile, location);

        // Assert
        int expectedBoardSize = 3;
        int expectedHexLevel = 2;
        Hex boardHex = board.getHex(location);

        Assert.assertEquals(expectedBoardSize, board.getSize());
        Assert.assertEquals(expectedHexLevel, boardHex.getLevel());
    }
}
