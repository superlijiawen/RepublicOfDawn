package data.hullmods;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import data.utils.api.RD_BaseHullMod;

/**
 * Emp防护系统
 *
 *  获得4倍的emp伤害减免
 */
public class RD_EmpProtection extends RD_BaseHullMod {

    private static final float EMP_DAMAGE_TAKEN_MULT = 0.25f;

    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getEmpDamageTakenMult().modifyMult(id, EMP_DAMAGE_TAKEN_MULT);
    }

    @Override
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        return "4";
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
