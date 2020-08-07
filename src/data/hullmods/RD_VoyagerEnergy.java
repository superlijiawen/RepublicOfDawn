package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.util.IntervalUtil;
import data.scripts.campaign.econ.ids.RD_Commodities;
import data.utils.api.RD_BaseHullMod;
import org.lazywizard.lazylib.MathUtils;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * 远行者能源系统
 *
 * buff:
 *
 *  内置了燃料工厂，每天随机生产2~4个燃料,如果当舰队当前的燃料值低于5天的燃料消耗总量，则会生产4个燃料（基础值）
 *  燃料工厂会根据舰船等级提供额外增益，"圣徒"级生产值为基础值，"圣使"级生产值为基础值+2,"圣使"级生产值为基础值+4
 *
 *
 */
public class RD_VoyagerEnergy extends RD_BaseHullMod {

    private static Map<FleetMemberAPI, IntervalUtil> map = new WeakHashMap<FleetMemberAPI, IntervalUtil>();

    public RD_VoyagerEnergy(){

    }

    @Override
    public void advanceInCampaign(FleetMemberAPI member, float amount) {
        IntervalUtil timer = (IntervalUtil) map.get(member);
        if (timer == null) {
            timer = new IntervalUtil(1.0F, 1.0F);
            map.put(member, timer);
        }

        float days = Global.getSector().getClock().convertToDays(amount);
        timer.advance(days);

        if (timer.intervalElapsed()) {
            if (member.getFleetData().getFleet() != null && member.getFleetData().getFleet().isPlayerFleet()){
                CargoAPI cargo = member.getFleetData().getFleet().getCargo();


                float fuel = cargo.getFuel();
                float maxFuel = cargo.getMaxFuel();
                if (fuel >= maxFuel){
                    return;//如果当前燃料已经足够了，则不会生产燃料
                }

                float fuelUsePerDay = member.getFleetData().getFleet().getStats().getFuelUseHyperMult().getBaseValue();

                float number = 0f;

                if (fuel <= fuelUsePerDay * 5){
                    number = 4.0f;
                }else{
                    number = MathUtils.getRandomNumberInRange(2.0f, 4.0f);
                }

                String hullId = member.getHullId();

                switch(hullId){
                    case "rd_holy_spirt":
                        number = number + 4;
                        break;
                    case "rd_holy_envoy":
                        number = number + 2;
                        break;
                    case "rd_saint":
                        number = number + 0;
                        break;
                    default:
                        number = 0;
                }

                float furnace = cargo.getCommodityQuantity(RD_Commodities.ANTIMATTER_FURNACE);
                if (furnace >= 0f){
                    number += 2;
                }

                cargo.addCommodity(Commodities.FUEL, number);
            }
        }
    }

    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {

    }

    @Override
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        switch (index){
            case 0:
                return "2";
            case 1:
                return "4";
            case 2:
                return "5";
            case 3:
                return "4";
            case 4:
                return "2";
            case 5:
                return "4";
            default:
                return null;
        }
    }

    @Override
    public boolean isApplicableToShip(ShipAPI ship){
        return false;
    }

    @Override
    public String getUnapplicableReason(ShipAPI ship){
        return null;
    }
}
