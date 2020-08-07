package data.scripts.campaign.econ.marketconditions;

import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FleetAssignment;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.impl.campaign.fleets.*;
import com.fs.starfarer.api.impl.campaign.ids.FleetTypes;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;

public class RD_EnhancedDefense extends BaseMarketConditionPlugin{

    private static final String FACTION_ID = "hegemony";
    private static final float STABILITY_BONUS = 2.0F;
    private static final float DEFENSE_BONUS = 1.50f;
    private static final float COMBAT_FLEET_BONUS = 1.20F;
    private static final String DESC = "霸主增强防御工事";

    public RD_EnhancedDefense() {

    }

    @Override
    public void apply(String id) {
        if (this.market.getFactionId().equals("hegemony")){
            this.market.getStability().modifyFlat(id, STABILITY_BONUS, DESC);
            this.market.getStats().getDynamic().getMod("ground_defenses_mod").modifyMult(id, DEFENSE_BONUS, DESC);
            this.market.getStats().getDynamic().getMod("combat_fleet_size_mult").modifyMult(id, COMBAT_FLEET_BONUS, DESC);
        }
    }

    @Override
    public void unapply(String id) {
        super.unapply(id);
        this.market.getStability().unmodifyFlat(id);
        this.market.getStats().getDynamic().getMod("ground_defenses_mod").unmodifyMult(id);
        this.market.getStats().getDynamic().getMod("combat_fleet_size_mult").unmodifyMult(id);

    }

    @Override
    public void advance(float amount) {
        super.advance(amount);

//        if (isActive()){
//            CampaignFleetAPI fleet = this.createHeavyFleet();
//            MarketAPI market = this.market;
//
//            if (fleet != null && fleet.isAlive()){
//                market.getStarSystem().spawnFleet(market.getPrimaryEntity(), market.getLocation().x, market.getLocationInHyperspace().y, fleet);
//            }
//        }
    }

    @Override
    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded) {
        Color color = this.market.getTextColorForFactionOrPlanet();
        tooltip.addTitle(this.condition.getName(), color);
        tooltip.addPara("%s", 10.0f, color, this.condition.getSpec().getDesc());

        super.createTooltipAfterDescription(tooltip, expanded);

        if (isActive()){
            tooltip.addPara("%s 稳定性", 10.0F, Misc.getHighlightColor(), new String[]{"+2"});
            tooltip.addPara("%s 地面防御", 10.0F, Misc.getHighlightColor(), new String[]{"x1.5"});
            tooltip.addPara("%s 舰队规模", 10.0F, Misc.getHighlightColor(), new String[]{"x1.2"});
        }else{
            tooltip.addPara("%s", 10.0F, Misc.getHighlightColor(), "已失效");
        }

    }



    private boolean isActive(){
        return this.market.getFactionId().equals(FACTION_ID);
    }

    private CampaignFleetAPI createHeavyFleet() {
        FleetParamsV3 params = new FleetParamsV3(this.market, FleetTypes.TASK_FORCE, 15, 4, 4, 0, 0, 0, 0);
        CampaignFleetAPI fleet = FleetFactoryV3.createFleet(params);
        fleet.addAssignment(FleetAssignment.DEFEND_LOCATION, this.market.getPrimaryEntity(), 100);
        fleet.setFaction(FACTION_ID);
        fleet.addScript(new PatrolAssignmentAI(fleet, new PatrolFleetManager.PatrolFleetData(fleet, FleetFactory.PatrolType.HEAVY)));
        return fleet;
    }

}
