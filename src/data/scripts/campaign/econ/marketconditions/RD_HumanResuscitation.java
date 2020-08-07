package data.scripts.campaign.econ.marketconditions;

import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;

public class RD_HumanResuscitation extends BaseMarketConditionPlugin {

    private static final String FACTION_ID = "republic_of_dawn";

    private static final float STABILITY_BONUS = 2.0F;
    private static final float ACCESSIBILITY_BONUS = 0.10F;
//    private static final float DEFENSE_BONUS = 1.2F;
//    private static final float COMBAT_FLEET_BONUS = 1.1F;

    private static final float STABILITY_PUNISH = -1.0F;
    private static final float ACCESSIBILITY_PUNISH = -0.10F;

    public RD_HumanResuscitation() {

    }

    @Override
    public void apply(String id) {

        if (isRDOccupied() || isFriendOccupied()){
            this.market.getStability().modifyFlat(id, STABILITY_BONUS, "人性复苏");
            this.market.getAccessibilityMod().modifyFlat(id, ACCESSIBILITY_BONUS, "人性复苏");
//            this.market.getStats().getDynamic().getMod("ground_defenses_mod").modifyMult(id, DEFENSE_BONUS, "人性复苏");
//            this.market.getStats().getDynamic().getMod("combat_fleet_size_mult").modifyMultAlways(id, COMBAT_FLEET_BONUS, "人性复苏");
        }else{
            this.market.getStability().modifyFlat(id, STABILITY_PUNISH, "人性复苏惩罚");
            this.market.getAccessibilityMod().modifyFlat(id, ACCESSIBILITY_PUNISH, "人性复苏惩罚");
        }
    }

    @Override
    public void unapply(String id) {
        super.unapply(id);
        this.market.getStability().unmodifyFlat(id);
        this.market.getAccessibilityMod().unmodifyFlat(id);
//        this.market.getStats().getDynamic().getMod("ground_defenses_mod").unmodifyFlat(id);
//        this.market.getStats().getDynamic().getMod("combat_fleet_size_mult").unmodifyFlat(id);
    }

    @Override
    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded) {
        Color color = this.market.getTextColorForFactionOrPlanet();
        tooltip.addTitle(this.condition.getName(), color);
        tooltip.addPara("%s", 10.0f, color, this.condition.getSpec().getDesc());

        super.createTooltipAfterDescription(tooltip, expanded);

        if (isRDOccupied()){
            tooltip.addPara("%s 稳定性", 10.0F, Misc.getHighlightColor(), new String[]{"+2"});
            tooltip.addPara("%s 流通性", 10.0F, Misc.getHighlightColor(), new String[]{"+10%"});
//            tooltip.addPara("%s 地面防御", 10.0F, Misc.getHighlightColor(), new String[]{"x1.2"});
//            tooltip.addPara("%s 舰队规模", 10.0F, Misc.getHighlightColor(), new String[]{"x1.1"});
        }else{
            tooltip.addPara("%s 稳定性", 10.0F, Misc.getHighlightColor(), new String[]{"-1"});
            tooltip.addPara("%s 流通性", 10.0F, Misc.getHighlightColor(), new String[]{"-10%"});
        }
    }

    private boolean isRDOccupied(){
        String factionId = this.market.getFactionId();
        return FACTION_ID.equals(factionId);
    }

    private boolean isFriendOccupied(){
        String factionId = this.market.getFactionId();
        return "independent".equals(factionId) || "persean".equals(factionId);
    }
}
