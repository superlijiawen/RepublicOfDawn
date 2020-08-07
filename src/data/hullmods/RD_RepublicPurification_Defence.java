package data.hullmods;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import data.utils.api.RD_BaseHullMod;

/**
 * 共和国净化——防御型（内置插件）
 * 护盾伤害获得伤害减免5%,护盾维持消耗、护盾被 EMP 电弧 及其他电磁武器击穿的概率也相应减少。
 */
public class RD_RepublicPurification_Defence extends RD_BaseHullMod {

    private static final float SHIELD_DAMAGE_TAKEN_MULT = 0.95f;
    private static final float SHIELD_UPKEEP_MULT = 0.95f;
    private static final float SHIELD_PIERCED_MULT = 0.95f;
//    private static final float SHIELD_MAX_SPEED = 0.95f;

    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getShieldDamageTakenMult().modifyMult(id, SHIELD_DAMAGE_TAKEN_MULT);
        stats.getShieldUpkeepMult().modifyMult(id, SHIELD_UPKEEP_MULT);
        stats.getDynamic().getStat(Stats.SHIELD_PIERCED_MULT).modifyMult(id, SHIELD_PIERCED_MULT);
//        stats.getMaxSpeed().modifyMult(id, SHIELD_MAX_SPEED);
    }

    @Override
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        if (index == 0){
            return "5%";
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

