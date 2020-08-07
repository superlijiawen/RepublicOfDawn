package data.shipsystems.scripts;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.sun.org.glassfish.external.probe.provider.StatsProviderManagerDelegate;

/**
 * 黎明无畏火力系统
 */
public class RD_IntrepidFire extends BaseShipSystemScript {

    private static final float BALLISTIC_ROF_MULT = 1.25f;//实弹武器射速
    private static final float BALLISTIC_FLUX_COST_MULT = 0.75f;//实弹武器消耗辐能
    private static final float BALLISTIC_DAMAGE_MULT = 1.25f;

//    private static final float MISSLE_ROF_MULT = 1.5f;
//    private static final float MISSLE_FLUE_COST_MULT = 0.75f;

    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        stats.getBallisticRoFMult().modifyMult(id, BALLISTIC_ROF_MULT);
        stats.getBallisticWeaponFluxCostMod().modifyMult(id, BALLISTIC_FLUX_COST_MULT);
        stats.getBallisticWeaponDamageMult().modifyFlat(id, BALLISTIC_DAMAGE_MULT);

//        stats.getMissileRoFMult().modifyMult(id, MISSLE_ROF_MULT);
//        stats.getMissileWeaponFluxCostMod().modifyMult(id, MISSLE_FLUE_COST_MULT);
    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        stats.getBallisticRoFMult().unmodify(id);
        stats.getBallisticWeaponFluxCostMod().unmodify(id);
        stats.getBallisticWeaponDamageMult().unmodify(id);
//        stats.getMissileRoFMult().unmodify(id);
//        stats.getMissileWeaponFluxCostMod();
    }


    @Override
    public StatusData getStatusData(int index, State state, float effectLevel) {
        switch(index){
            case 0:
                return new StatusData("实弹武器射速提高至 125%", false);
            case 1:
                return new StatusData("实弹武器辐能消耗降低 25%", false);
            case 2:
                return new StatusData("实弹武器伤害提高值 120%", false);
            default:
                return null;
        }
    }


}
