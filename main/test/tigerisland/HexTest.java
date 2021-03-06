package tigerisland; /**
 * Created by christinemoore on 3/14/17.
 */

import org.junit.*;
import tigerisland.hex.Hex;
import tigerisland.terrains.Rocky;
import tigerisland.terrains.Terrain;


public class HexTest {
    public static Hex customHex;

    @BeforeClass
    public static void makeCustomHex(){
        int tileID = 5;
        int settlementID = 6;
        Terrain terrain = Rocky.getInstance();
        int level = 1;

        customHex = new Hex(tileID, settlementID, terrain, level);
    }

    @Test
    public void makeDefaultTile(){
        Hex defaultHex = new Hex(0);

        Assert.assertTrue(defaultHex instanceof Hex);
    }

    @Test
    public void tileIDSetCorrectly(){
        int tileID = customHex.getTileID();

        Assert.assertTrue(tileID == 5);
    }

    @Test
    public void terrainSetCorrectly(){
        Terrain setTerrain = customHex.getTerrain();

        Assert.assertTrue(setTerrain instanceof Rocky);
    }


    @Test
    public void levelSetCorrectly(){
        int level = 1;

        Assert.assertTrue(level == customHex.getLevel());
    }
}
