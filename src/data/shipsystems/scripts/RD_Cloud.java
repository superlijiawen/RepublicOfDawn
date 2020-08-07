package data.shipsystems.scripts;

import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;

import java.awt.*;

/**
 * “云”式系统
 *
 * "云"级舰船具有两种状态
 */
public class RD_Cloud extends BaseShipSystemScript {

    private static final Color JITTER_COLOR = new Color(168, 203, 255);
    private static final Color JITTER_UNDER_COLOR = new Color(154, 103, 255);
    private static final float MAX_SPEED_BONUS = 30.0f;
    private static final float FLUX_DISSIPATION_MULT = 1.50f;
    private static final float TURN_SPEED_MULT = 2.0f;
    private static final float MAX_TURN_RATE_BONUS = 3.0F;

    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        ShipAPI ship = null;
        CombatEntityAPI entity = stats.getEntity();
        if (entity != null){
            if (entity instanceof ShipAPI){
                ship = (ShipAPI) entity;

                float range = ship.getSystem().getChargeUpDur();
                //void	setJitter(java.lang.Object source, java.awt.Color color, float intensity, int copies, float range)
                ship.setJitter(this, JITTER_COLOR, effectLevel, 3, 0.0f, 0.0f + range);
                ship.setJitterUnder(this, JITTER_UNDER_COLOR , effectLevel, 10, 0.0f, 5.0f + range);

                stats.getMaxSpeed().modifyFlat(id, MAX_SPEED_BONUS);
                stats.getFluxDissipation().modifyMult(id, FLUX_DISSIPATION_MULT);
                stats.getTurnAcceleration().modifyMult(id, TURN_SPEED_MULT);
                stats.getMaxTurnRate().modifyFlat(id, MAX_TURN_RATE_BONUS);
            }
        }
    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        stats.getMaxSpeed().unmodify(id);
        stats.getFluxDissipation().unmodify(id);
        stats.getTurnAcceleration().unmodify(id);
        stats.getMaxTurnRate().unmodify(id);
    }

    @Override
    public StatusData getStatusData(int index, State state, float effectLevel) {
        if(index == 0){
            return new StatusData("云 式系统已启动", false);
        }

        return null;
    }

}
