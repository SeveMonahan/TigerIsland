package tigerisland.cucumbertest.tile;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.Assert;
import tigerisland.hex.Hex;
import tigerisland.terrains.Jungle;
import tigerisland.terrains.Rocky;
import tigerisland.terrains.Terrain;
import tigerisland.terrains.Volcano;
import tigerisland.tile.Orientation;
import tigerisland.tile.Tile;

public class CucumberTileStepper {
    public Tile tile = null;
    @Given("^We can examine a tile$")
    public void weCanExamineATile() throws Throwable {
       Terrain lTerrain = Rocky.getInstance();
       Terrain rTerrain = Jungle.getInstance();
       tile = new Tile(0, new Hex(Volcano.getInstance()), new Hex(lTerrain), new Hex(rTerrain));
    }

    @When("^We examine the left and right portions of said tile$")
    public void weExamineTheLeftAndRightPortionsOfSaidTile() throws Throwable {
        // Wish I could word this test better, but I want something running.
    }

    @Then("^We find two non volcano terrain types$")
    public void weFindTwoNonVolcanoTerrainTypes() throws Throwable {
       Assert.assertTrue(tile.getLeftHex().getTerrain() != Volcano.getInstance());
       Assert.assertTrue(tile.getRightHex().getTerrain() != Volcano.getInstance());
    }

    @Then("^We find one volcano terrain types$")
    public void weFindOneVolcanoTerrainTypes() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        Assert.assertTrue(tile.getReferenceHex().getTerrain() == Volcano.getInstance());
    }
}
