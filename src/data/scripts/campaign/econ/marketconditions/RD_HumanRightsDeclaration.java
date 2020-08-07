package data.scripts.campaign.econ.marketconditions;

import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;

public class RD_HumanRightsDeclaration extends BaseMarketConditionPlugin {

    private static final float STABILITY_BONUS = 1.0F;
    private static final float ACCESSIBILITY_BONUS = 0.10F;
    private static final float DEFENSE_BONUS = 1.10F;
    private static final float COMBAT_FLEET_BONUS = 1.10F;

    private static final float STABILITY_PUNISH = -1.0F;

    @Override
    public void apply(String id) {
        String factionId = this.getFactionId();

        if (factionId != null){
            switch (factionId){
                case "republic_of_dawn":
                    this.market.getStability().modifyFlat(id, STABILITY_BONUS + 1, "人权宣言");
                    this.market.getAccessibilityMod().modifyFlat(id, ACCESSIBILITY_BONUS, "人权宣言");
                    this.market.getStats().getDynamic().getMod("ground_defenses_mod").modifyMult(id, DEFENSE_BONUS + 0.1F, "人权宣言");
                    this.market.getStats().getDynamic().getMod("combat_fleet_size_mult").modifyMultAlways(id, COMBAT_FLEET_BONUS + 0.05F, "人权宣言");
                    break;
                case "persean":
                    this.market.getStability().modifyFlat(id, STABILITY_BONUS, "人权宣言");
                    this.market.getAccessibilityMod().modifyFlat(id, ACCESSIBILITY_BONUS, "人权宣言");
                    this.market.getStats().getDynamic().getMod("ground_defenses_mod").modifyMult(id, DEFENSE_BONUS, "人权宣言");
                    this.market.getStats().getDynamic().getMod("combat_fleet_size_mult").modifyMultAlways(id, COMBAT_FLEET_BONUS, "人权宣言");
                    break;
                case "independent":
                    this.market.getStability().modifyFlat(id, STABILITY_BONUS, "人权宣言");
                    this.market.getAccessibilityMod().modifyFlat(id, ACCESSIBILITY_BONUS + 0.1F, "人权宣言");
                    break;
                default:
                    this.market.getStability().modifyFlat(id, STABILITY_PUNISH, "人权宣言惩罚");
                    this.market.getAccessibilityMod().modifyFlat(id, -this.getAccessibilityPunish(), "人权宣言惩罚");
            }
        }

    }

    @Override
    public void unapply(String id) {
        super.unapply(id);
        this.market.getStability().unmodifyFlat(id);
        this.market.getAccessibilityMod().unmodifyFlat(id);
        this.market.getStats().getDynamic().getMod("ground_defenses_mod").unmodify(id);
        this.market.getStats().getDynamic().getMod("combat_fleet_size_mult").unmodify(id);
    }

    @Override
    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded) {
        Color color = this.market.getTextColorForFactionOrPlanet();
        tooltip.addTitle(this.condition.getName(), color);
        tooltip.addPara("%s", 10.0f, color, this.condition.getSpec().getDesc());

        super.createTooltipAfterDescription(tooltip, expanded);

        String factionId = this.getFactionId();

        if (factionId != null){
            switch (factionId){
                case "republic_of_dawn":
                    tooltip.addPara("%s 稳定性", 10.0F, Misc.getHighlightColor(), new String[]{"+2"});
                    tooltip.addPara("%s 流通性", 10.0F, Misc.getHighlightColor(), new String[]{"+10%"});
                    tooltip.addPara("%s 地面防御", 10.0F, Misc.getHighlightColor(), new String[]{"x1.2"});
                    tooltip.addPara("%s 舰队规模", 10.0F, Misc.getHighlightColor(), new String[]{"x1.1"});;
                    break;
                case "persean":
                    tooltip.addPara("%s 稳定性", 10.0F, Misc.getHighlightColor(), new String[]{"+1"});
                    tooltip.addPara("%s 流通性", 10.0F, Misc.getHighlightColor(), new String[]{"+10%"});
                    tooltip.addPara("%s 地面防御", 10.0F, Misc.getHighlightColor(), new String[]{"x1.1"});
                    tooltip.addPara("%s 舰队规模", 10.0F, Misc.getHighlightColor(), new String[]{"x1.1"});;
                    break;
                case "independent":
                    tooltip.addPara("%s 稳定性", 10.0F, Misc.getHighlightColor(), new String[]{"+1"});
                    tooltip.addPara("%s 流通性", 10.0F, Misc.getHighlightColor(), new String[]{"+20%"});
                    break;
                default:
                    tooltip.addPara("%s 稳定性", 10.0F, Misc.getHighlightColor(), new String[]{"-1"});
                    tooltip.addPara("%s 流通性", 10.0F, Misc.getHighlightColor(), new String[]{"-" +(int)(this.getAccessibilityPunish() * 100) + "%"});
            }
        }

    }

    private String getFactionId(){
        return this.market.getFactionId();
    }

    private float getAccessibilityPunish(){
        int size = this.market.getSize();
        float accessibility_punish = 0;

        if (size == 3){
            accessibility_punish = 0.05f;
        }else if (size > 3 && size <= 5){
            accessibility_punish = 0.02f * size;
        }else if (size > 5){
            accessibility_punish = 0.03f * size;
        }

        return accessibility_punish;
    }
}
