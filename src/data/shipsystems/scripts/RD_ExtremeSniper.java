package data.shipsystems.scripts;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;

/**
 * 极限狙击
 *
 *
 * buff:
 * 短时间内能量武器射程提高1000
 *
 * debuff:
 * 能量武器伤害增加25%
 * 能量武器辐能消耗降低25%
 */
public class RD_ExtremeSniper extends BaseShipSystemScript {

    private static final float ENERGY_WEAPON_RANGE_BONUS = 1000.0F;
    private static final float ENERGY_WEAPON_DAMAGE_MULT = 1.25f;
    private static final float ENERGY_WEAPON_FLUX_COST_MULT = 0.75f;

    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        stats.getEnergyWeaponRangeBonus().modifyFlat(id, ENERGY_WEAPON_RANGE_BONUS);
        stats.getEnergyWeaponDamageMult().modifyMult(id, ENERGY_WEAPON_DAMAGE_MULT);
        stats.getEnergyWeaponFluxCostMod().modifyFlat(id, ENERGY_WEAPON_FLUX_COST_MULT);

    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        stats.getEnergyWeaponRangeBonus().unmodify(id);
        stats.getEnergyWeaponDamageMult().unmodify(id);
        stats.getEnergyWeaponFluxCostMod().unmodify(id);
    }

    @Override
    public StatusData getStatusData(int index, State state, float effectLevel) {
        switch(index){
            case 0:
                return new StatusData("能量武器射程提高1000", false);
            case 1:
                return new StatusData("能量武器伤害增加25%", true);
            case 2:
                return new StatusData("能量武器辐能消耗降低25%", true);
            default:
                return null;
        }
    }
}
