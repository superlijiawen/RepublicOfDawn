package data.scripts.campaign.econ.industries;


import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import data.scripts.campaign.econ.ids.RD_Commodities;
import data.utils.api.RD_BaseIndustry;

public class RD_SolarCatalytic extends RD_BaseIndustry {

    public RD_SolarCatalytic(){

    }

    @Override
    public void apply() {
        super.apply();

        this.demand(Commodities.HEAVY_MACHINERY, size -1, DESC);
        this.demand(Commodities.RARE_METALS, size - 2, DESC);

        if (this.isInWhiteList()){
            this.supply(Commodities.SUPPLIES, size - 1, DESC);
            this.supply(RD_Commodities.ADVANCED_SUPPLIES, size - 1, DESC);
        }else{
            this.supply(Commodities.SUPPLIES, size + 1, DESC);
        }

        if (!this.isFunctional()) {
            this.supply.clear();
        }

//        this.market.getStats().getDynamic().getMod("production_quality_mod").modifyMult(this.getModId(), 1.0F, this.getNameForModifier());
//        this.market.getStats().getDynamic().getMod("fleet_quality_mod").modifyMult(this.getModId(), 1.0F, this.getNameForModifier());
    }

    public void unapply() {
        super.unapply();
    }

    public boolean isAvailableToBuild() {
        return this.isRadiusFit() && this.isAuthorized();
    }


    public String getUnavailableReason() {
        if (!isRadiusFit()){
            return "请确保您的行星处于可开采太阳能的合适范围内。";
        }
        if (isAvailableToBuild()){
           return "必须和黎明共和国关系达到50，且奥古斯都属于黎明共和国控制下才能建造";
        }

        return "";
    }

    private boolean isRadiusFit(){
        SectorEntityToken entity = this.market.getPrimaryEntity();
        if (entity != null){
            float orbitRadius = entity.getCircularOrbitRadius();
            return orbitRadius >= 2000 && orbitRadius <= 8000;
        }else{
            return false;
        }
    }


}
