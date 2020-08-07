package data.hullmods;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import data.utils.api.RD_BaseHullMod;

/**
 * "云"式舰船
 *
 * 2倍的emp伤害减免，与emp防护系统可叠加
 * 护盾被击穿概率减半
 */
public class RD_CloudShip extends RD_BaseHullMod {

    private static final float EMP_DAMAGE_TAKEN_MULT = 0.50f;
    private static final float PIERCE_MULT = 0.50f;


    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getDynamic().getStat(Stats.SHIELD_PIERCED_MULT).modifyMult(id, PIERCE_MULT);
        stats.getEmpDamageTakenMult().modifyMult(id, EMP_DAMAGE_TAKEN_MULT);

    }

    @Override
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        if (index == 0){
            return "2";
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
