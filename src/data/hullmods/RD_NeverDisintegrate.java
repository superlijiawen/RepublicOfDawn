package data.hullmods;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import data.utils.api.RD_BaseHullMod;

/**
 * 永不解体
 */
public class RD_NeverDisintegrate extends RD_BaseHullMod {

    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getBreakProb().modifyMult(id, 0);
    }

    @Override
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        return null;
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
