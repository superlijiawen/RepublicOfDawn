package data.hullmods;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import data.utils.api.RD_BaseHullMod;

/**
 * 共和国净化——均衡型（内置插件）
 * 辐能容量、辐能耗散速率、护盾伤害减免、护盾转向速度、护盾最大角度均提升5%。
 */
public class RD_RepublicPurification_Balanced extends RD_BaseHullMod {

    private static final float BONUS = 0.05f;

    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getFluxCapacity().modifyMult(id, 1 + BONUS);
        stats.getFluxDissipation().modifyMult(id, 1 + BONUS);
        stats.getShieldDamageTakenMult().modifyMult(id, 1 - BONUS);
        stats.getShieldTurnRateMult().modifyMult(id, 1 + BONUS);
        stats.getShieldArcBonus().modifyMult(id,1 + BONUS);
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
