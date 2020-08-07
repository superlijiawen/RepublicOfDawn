package data.scripts.campaign.econ.industries;

import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import data.scripts.campaign.econ.ids.RD_Commodities;
import data.utils.api.RD_BaseIndustry;


/**
 * 曙光生态养殖基地
 * 需求：
 * 产出：
 * 备注：
 */
public class RD_DawningEcologicalBreedingBase extends RD_BaseIndustry {

    public RD_DawningEcologicalBreedingBase(){

    }

    @Override
    public void apply() {
        super.apply();

        this.demand(Commodities.FOOD, size + 1, DESC);
        this.demand(Commodities.CREW, size - 2);
        this.demand(RD_Commodities.ADVANCED_SUPPLIES, size - 1, DESC);

        this.supply(Commodities.DOMESTIC_GOODS, size + 1);
        this.supply(Commodities.ORGANICS, size + 1);
        this.supply(Commodities.ORGANS, size - 2);

        if (!this.isFunctional()) {
            this.supply.clear();
        }
    }

    public void unapply() {
        super.unapply();
    }

    public boolean isAvailableToBuild() {
        return false;
    }

    public String getUnavailableReason() {
        return "只有黎明共和国才能建造";
    }

    @Override
    public boolean isHidden(){
        return !this.isRDOccupied();
    }

}
