package data.hullmods;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import data.utils.api.RD_BaseHullMod;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * 共和国增强护盾A型
 * 减少50%护盾受到的伤害，同时护盾吸收降低5%
 *
 *
 */
public class RD_RepublicEnhancedShield_A extends RD_BaseHullMod {

    private static final float SHIELD_DAMAGE_TAKEN_MULT = 0.5f;
    private static final float SHIELP_ABSORPTION_MULT = 0.95f;

    private static final List<String> CONFLICT_LIST = Arrays.asList(
            "RD_enhanced_shield_b",
            "hardenedshieldemitter"
    );

    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getShieldDamageTakenMult().modifyMult(id, SHIELD_DAMAGE_TAKEN_MULT);
        stats.getShieldAbsorptionMult().modifyMult(id, SHIELP_ABSORPTION_MULT);
    }

    @Override
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        if (index == 0){
            return "50%";
        }
        if (index == 1){
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
            return "与共和国增强护盾B型冲突";
        }

        if (hullMods.contains(CONFLICT_LIST.get(1))){
            return "与强化护盾冲突";
        }

        return null;
    }
}
