package data.shipsystems.scripts;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;


/**
 * 璀璨光辉
 *
 * 能量武器伤害提升125%
 * 能量武器射程提升120%
 * 能量武器辐能消耗降低至50%
 */
@SuppressWarnings("all")
public class RD_Brilliance extends BaseShipSystemScript {

    private static float ENERGY_DAMAGE_MULT = 1.25f;
    private static float ENERGY_RANGE_BOUNS = 1.25f;
    private static float ENERGY_FLUX_COST = 0.50f;

    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {

        stats.getEnergyWeaponDamageMult().modifyMult(id, ENERGY_DAMAGE_MULT);
        stats.getEnergyWeaponRangeBonus().modifyMult(id, ENERGY_RANGE_BOUNS);
        stats.getEnergyWeaponFluxCostMod().modifyMult(id, ENERGY_FLUX_COST);


    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        stats.getEnergyWeaponDamageMult().unmodify(id);
        stats.getEnergyWeaponRangeBonus().unmodify(id);
        stats.getEnergyWeaponFluxCostMod().unmodifyFlat(id);
    }

    @Override
    public StatusData getStatusData(int index, State state, float effectLevel) {
        switch(index){
            case 0:
                return new StatusData("能量武器伤害提升至125%", false);
            case 1:
                return new StatusData("能量武器射程提升至125%", false);
            case 2:
                return new StatusData("能量武器辐能消耗降低至50%", false);
            default:
                return null;
        }
    }


}
