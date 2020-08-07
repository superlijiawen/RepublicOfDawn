package data.hullmods;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.StatBonus;
import data.utils.api.RD_BaseHullMod;

/**
 * 航母净化
 *  pd点防御武器射程提高20%
 *  战机整备速率提升20%
 *  补给每日消耗提升20%
 *  完全修复成本提高20%
 *
 */
public class RD_AircraftCarriePurification extends RD_BaseHullMod {

    private static final float PD_RANGE_BONUS_MULT = 1.20f;
    private static final float FIGHTER_REFIT_TIME_MULT = 1.20f;
    private static final float SUPPLIES_CONSUME_MULT = 1.20f;
    private static final float SUPPLIES_RECOVER = 1.2f;

    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        StatBonus bonus = stats.getBeamPDWeaponRangeBonus();
        if (bonus != null){
            stats.getBeamPDWeaponRangeBonus().modifyMult(id, PD_RANGE_BONUS_MULT);
        }

        stats.getFighterRefitTimeMult().modifyMult(id, FIGHTER_REFIT_TIME_MULT);
        stats.getSuppliesPerMonth().modifyMult(id, SUPPLIES_CONSUME_MULT);
        stats.getSuppliesToRecover().modifyMult(id, SUPPLIES_RECOVER);
    }

    @Override
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        if (index == 0 || index == 1 || index == 2 || index == 3){
            return "20%";
        }

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
