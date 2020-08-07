package data.hullmods;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import data.utils.api.RD_BaseHullMod;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * 共和国增强护盾B型
 *  buff:
 *  减少60%维持护盾产生的软辐能，但不会影响硬辐能
 *  debuff:
 *  减少5%护盾转向角
 *
 */
public class RD_RepublicEnhancedShield_B extends RD_BaseHullMod {

    private static final float SHIELD_UPKEEP_MULT = 0.4f;
    private static final float SHIELD_TURN_RATE_MULT = 0.95f;

    private static final List<String> CONFLICT_LIST = Arrays.asList(
            "RD_enhanced_shield_a",
            "stabilizedshieldemitter"
    );

    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getShieldUpkeepMult().modifyMult(id, SHIELD_UPKEEP_MULT);
        stats.getShieldTurnRateMult().modifyMult(id, SHIELD_TURN_RATE_MULT);
    }

    @Override
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        if (index == 0){
            return "60%";
        }else if (index == 1){
            return "5%";
        }
        return null;
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
            return "与共和国增强护盾A型冲突";
        }

        if (hullMods.contains(CONFLICT_LIST.get(1))) {
            return "与稳定护盾冲突";
        }

        return null;
    }
}
