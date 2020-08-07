package data.hullmods;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import data.utils.api.RD_BaseHullMod;

/**
 * 共和国货仓扩容
 *
 * 货仓容量提升25%
 * 船员需求提升25%
 */
public class RD_RepublicCargoExpansion extends RD_BaseHullMod {

    private static final float CARGO_CAPACITY_MULT = 1.25f;
    private static final float MIN_CREW_MULT = 1.25f;

    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getCargoMod().modifyMult(id, CARGO_CAPACITY_MULT);
        stats.getMinCrewMod().modifyMult(id, MIN_CREW_MULT);
    }

    @Override
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        if (index == 0){
            return "25%";
        }else if (index == 1){
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
