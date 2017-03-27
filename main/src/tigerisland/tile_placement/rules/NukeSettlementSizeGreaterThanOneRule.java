package tigerisland.tile_placement.rules;

import tigerisland.board.Location;
import tigerisland.hex.Hex;
import tigerisland.settlement.Settlement;
import tigerisland.settlement.SettlementBoard;
import tigerisland.tile_placement.exceptions.NukeSettlementSizeException;

import java.util.Map;
import java.util.Set;

public class NukeSettlementSizeGreaterThanOneRule implements NukePlacementRule {

    private SettlementBoard settlementBoard;


    public NukeSettlementSizeGreaterThanOneRule(SettlementBoard settlementBoard) {
        this.settlementBoard = settlementBoard;
    }


    @Override
    public void applyRule(Map<Location, Hex> hexes) throws Exception {
        if (checkSettlementEradication(hexes.keySet())) {
            throw new NukeSettlementSizeException();
        }
    }

    private boolean checkSettlementEradication(Set<Location> locations) {


        for (Location location : locations) {
            Settlement settlement = settlementBoard.getSettlement(location);
            //Null settlement
            if (settlement.settlementSize()==0) continue;
            Set<Location> settlementLocs = settlement.getConnectedLocations();
            settlementLocs.removeAll(locations);
            if ( settlementLocs.size() == 0 ) return true;
        }

        return false;
    }

}






