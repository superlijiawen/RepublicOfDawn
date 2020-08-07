package data.hullmods;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import data.utils.api.RD_BaseHullMod;

/**
 * 共和国燃料舱扩容
 * 内置插件
 *
 * 燃料容量提升25%
 * 船员要求提升25%
 *
 */
public class RD_RepublicFuelExpansion extends RD_BaseHullMod {

    private static final float FUEL_CAPACITY_MULT = 1.25f;
    private static final float MIN_CREW_MULT = 1.25f;

    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getFuelMod().modifyMult(id, FUEL_CAPACITY_MULT);
        stats.getMinCrewMod().modifyMult(id, MIN_CREW_MULT);
    }

    @Override
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        if (index == 0){
            return "25%";
        } else if (index == 1){
            return "25%";
        }
        return null;
    }

    @Override
    public boolean isApplicableToShip(ShipAPI ship){
        return true;
    }

    @Override
    public String getUnapplicableReason(ShipAPI ship){
        return null;
    }
}
