package data.hullmods;


import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import data.utils.api.RD_BaseHullMod;

import java.util.*;

/**
 * 护盾最大角度提升至120%
 * 护盾展开速率提升至200%
 * 护盾转向速度提升至200%
 */
public class RD_RepublicEnhancedShield_C extends RD_BaseHullMod {

    private static final float SHIELD_ARC_BOUNS_MULT = 1.2f;
    private static final float SHIELD_UNFOLD_RATE_MULT = 2.0f;
    private static final float SHIELD_TURN_RATE_MULT = 2.0f;

    private static final List<String> CONFLICT_LIST = Arrays.asList(
            "adaptiveshields",
            "frontshield",
            "frontemitter",
            "advancedshieldemitter"
    );

    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getShieldArcBonus().modifyMult(id, SHIELD_ARC_BOUNS_MULT);
        stats.getShieldUnfoldRateMult().modifyMult(id, SHIELD_UNFOLD_RATE_MULT);
        stats.getShieldTurnRateMult().modifyMult(id, SHIELD_TURN_RATE_MULT);
    }

    @Override
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        switch(index){
            case 0:
                return "120%";
            case 1:
            case 2:
                return "200%";
            default:
                return null;
        }
    }

    public boolean isApplicableToShip(ShipAPI ship) {
        return this.isHaveShieldAndNoConflict(ship, CONFLICT_LIST);
    }

    public String getUnapplicableReason(ShipAPI ship) {
        if (ship == null || ship.getShield() == null){
            return "该舰没有护盾";
        }

        Collection<String> hullMods = ship.getVariant().getHullMods();

        if (hullMods.contains(CONFLICT_LIST.get(0))) {
            return "与全角护盾发生器冲突";
        }

        if (hullMods.contains(CONFLICT_LIST.get(1))) {
            return "与定向护盾生成器冲突";
        }

        if (hullMods.contains(CONFLICT_LIST.get(2))) {
            return "与固化护盾发生器冲突";
        }

        if (hullMods.contains(CONFLICT_LIST.get(3))) {
            return "与敏捷护盾冲突";
        }

        return null;

    }

}
