package data.shipsystems.scripts;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;

/**
 * 人类起源之歌
 *  5秒蓄能后
 *  开启护盾,护盾展开速度提升至200%，且护盾获得4倍的免伤效果。
 *
 *  辐能耗散速率提高值200%
 *
 *
 *
 *  技能开启时将停用武器、停用战机、停用强制耗散
 *
 */
@Deprecated
public class RD_SongOfHumanOrigin extends BaseShipSystemScript {

    private static final float SHIELD_DAMAGE_TAKEN_MULT = 0.25f;
    private static final float SHIELD_UNFOLD_RATE_MULT = 2.0f;
    private static final float FLUX_DISSIPATION_MULT = 2.0f;

    @Override
    public void apply(MutableShipStatsAPI stats, String id, ShipSystemStatsScript.State state, float effectLevel) {
        if (stats.getEntity() != null && stats.getEntity() instanceof ShipAPI){
            ShipAPI ship = (ShipAPI) stats.getEntity();
            if (ship != null){
                stats.getShieldDamageTakenMult().modifyMult(id, SHIELD_DAMAGE_TAKEN_MULT);
                stats.getShieldUnfoldRateMult().modifyMult(id, SHIELD_UNFOLD_RATE_MULT);
                stats.getFluxDissipation().modifyMult(id, FLUX_DISSIPATION_MULT);
            }

        }
    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        if (stats.getEntity() != null && stats.getEntity() instanceof ShipAPI){
            ShipAPI ship = (ShipAPI) stats.getEntity();
            if (ship != null && ship.isAlive()){
                stats.getShieldUnfoldRateMult().unmodify(id);
                stats.getShieldDamageTakenMult().unmodify(id);
                stats.getFluxDissipation().unmodify(id);
            }
        }
    }

    @Override
    public ShipSystemStatsScript.StatusData getStatusData(int index, ShipSystemStatsScript.State state, float effectLevel) {
        switch(index){
            case 0:
                return new StatusData("护盾获得4倍的免伤效果", false);
            case 1:
                return new StatusData("护盾展开速率提升至200%", false);
            case 2:
                return new StatusData("辐能耗散速率提升至200%", false);
            default:
                return null;
        }
    }

}
