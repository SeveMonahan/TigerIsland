package tigerisland.build.rules;

import tigerisland.board.Location;
import tigerisland.build.BuildActionData;
import tigerisland.build.BuildActionResult;
import tigerisland.build.SettlementExpansionUtility;

public class EnoughVillagersToExpandRule implements BuildActionRule {

    private BuildActionResult successfulResult = new BuildActionResult(true);
    private BuildActionResult failedResult = new BuildActionResult(false, "Not enough villagers!!");

    private SettlementExpansionUtility settlementExpansionUtility;

    public EnoughVillagersToExpandRule(SettlementExpansionUtility settlementExpansionUtility) {
        this.settlementExpansionUtility  = settlementExpansionUtility;
    }

    @Override
    public BuildActionResult applyRule(BuildActionData buildActionData) {
        // We now have all the hexes we can expand to, and they all should be on the map.
        int villagersNeeded = settlementExpansionUtility.getVillagersNeededToExpand(buildActionData);
        if ( villagersNeeded <= buildActionData.getPlayer().getVillagerCount() ) {
            return successfulResult;
        } else {
            return failedResult;
        }
    }
}
