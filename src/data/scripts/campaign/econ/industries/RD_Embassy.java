package data.scripts.campaign.econ.industries;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.scripts.campaign.econ.ids.RD_Commodities;
import data.utils.api.RD_BaseIndustry;

import java.awt.*;
import java.util.List;

/**
 * 共和国大使馆
 *
 * 增加流通性
 */
public class RD_Embassy extends RD_BaseIndustry {

    private static final float BASE_BONUS = 0.30F;
    private static final float STABILITY_VALUE_BONUS = 1.0F;

    public RD_Embassy(){

    }

    @Override
    public void apply() {
        super.apply();

        if (this.market.getFactionId().equals(FACTION_ID)){
            this.demand(Commodities.LUXURY_GOODS, size - 1);
            this.demand(Commodities.CREW, size - 2);
            this.demand(Commodities.DOMESTIC_GOODS, size - 2);
            this.demand(RD_Commodities.ADVANCED_SUPPLIES, size - 2);

            float value = this.getValueToAdd();
            this.market.getAccessibilityMod().modifyFlat(this.getModId(0), value, "大使馆加成");
            this.market.getStability().modifyFlat(this.getModId(0), STABILITY_VALUE_BONUS, "大使馆加成");
        }

        if (!this.isFunctional()) {
            this.supply.clear();
        }

    }

    public void unapply() {
        super.unapply();
    }

    protected void addPostDemandSection(TooltipMakerAPI tooltip, boolean hasDemand, IndustryTooltipMode mode) {
        if (mode != IndustryTooltipMode.NORMAL || this.isFunctional()) {
            Color h = Misc.getHighlightColor();
            float opad = 10.0F;
            tooltip.addPara("稳定性奖励: + %s", opad, h, String.valueOf((int)STABILITY_VALUE_BONUS));
        }
    }

    public boolean isAvailableToBuild() {
        return false;
    }


    public String getUnavailableReason() {
        return null;
    }

    public float getValueToAdd() {
        SectorAPI sector = Global.getSector();

        List<FactionAPI> factions = sector.getAllFactions();

        int friend = 0 ;
        int total = 0;
        for (FactionAPI faction : factions) {
            if (!faction.getId().equals(FACTION_ID) && faction.isShowInIntelTab()){
                if (faction.getRelationship(FACTION_ID) >= 0f){
                    friend ++;
                }
                total ++;
            }
        }

        if (total == 0){
            return BASE_BONUS;
        }else{
            return BASE_BONUS * ((float)friend / (float) total);
        }
    }

    @Override
    public void createTooltip(IndustryTooltipMode mode, TooltipMakerAPI tooltip, boolean expanded) {
        super.createTooltip(mode, tooltip, expanded);
    }

}
