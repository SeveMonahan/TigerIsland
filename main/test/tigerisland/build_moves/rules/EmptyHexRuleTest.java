package tigerisland.build_moves.rules;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import tigerisland.board.Location;
import tigerisland.build_moves.builds.BuildActionData;
import tigerisland.build_moves.builds.BuildActionResult;
import tigerisland.build_moves.rules.BuildActionRule;
import tigerisland.build_moves.rules.EmptyHexRule;
import tigerisland.piece.*;
import tigerisland.player.Player;

public class EmptyHexRuleTest {

    private BuildActionRule emptyHexRule;
    private PieceBoard pieceBoard;

    @Before
    public void setup() {

        this.pieceBoard = new PieceBoardImpl();
        this.emptyHexRule = new EmptyHexRule(pieceBoard);
    }

    @Test
    public void test_ShouldReturnUnsuccessfulWhenPieceBoardAtALocationIsUsed() {

        // Arrange
        final String EMPTY_HEX_ERROR_MESSAGE = "Hex is NOT empty";

        Player player = new Player();
        Location hexLocation = new Location(0, 0, 0);

        BuildActionData buildActionData = new BuildActionData.Builder()
                .withPlayer(player)
                .withHexLocation(hexLocation)
                .build();

        Piece villager = new Villager();
        pieceBoard.addPiece(villager, hexLocation, player.getId());

        // Act
        BuildActionResult result = emptyHexRule.applyRule(buildActionData);

        // Assert
        Assert.assertFalse(result.successful);
        Assert.assertEquals(EMPTY_HEX_ERROR_MESSAGE, result.errorMessage);
    }

    @Test
    public void test_ShouldReturnSuccessfulWhenPieceBoardAtLocationIsNotUsed() {

        // Arrange
        Player player = new Player();
        Location hexLocation = new Location(0, 0, 0);

        BuildActionData buildActionData = new BuildActionData.Builder()
                .withPlayer(player)
                .withHexLocation(hexLocation)
                .build();

        // Act
        BuildActionResult result = emptyHexRule.applyRule(buildActionData);

        // Assert
        Assert.assertTrue(result.successful);
    }

}