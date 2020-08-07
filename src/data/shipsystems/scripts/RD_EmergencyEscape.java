package data.shipsystems.scripts;

import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;

/**
 *  紧急逃逸系统
 *
 *  武器熄火、战机熄火、不允许强制耗散辐能
 *
 *  最大速度提高120
 *  最大加速度、转向速度提高至250%,cr耗散速率提高至400%
 *  有一定几率失败（引擎熄火）,当前CR值越低。失败几率越高。例如当前CR为0时，将有50%几率失败
 *
 */
public class RD_EmergencyEscape extends BaseShipSystemScript {

    private static final float MAX_SPEED_BONUS = 120.0f;
    private static final float ACCELERATION_MULT = 2.5f;
    private static final float TURN_ACCELERATION_MULT = 2.5f;

    private static final float CR_LOSS_MULT = 4.0f;

    //故障几率
    private static final float BASE_MALFUNCTION_CHANCE = 0.50f;


    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        stats.getMaxSpeed().modifyFlat(id, MAX_SPEED_BONUS);
        stats.getAcceleration().modifyMult(id, ACCELERATION_MULT);
        stats.getTurnAcceleration().modifyMult(id, TURN_ACCELERATION_MULT);

        stats.getCRLossPerSecondPercent().modifyMult(id ,CR_LOSS_MULT);

        double probability = Math.random();

        CombatEntityAPI entity = stats.getEntity();
        if (entity != null){
            if (entity instanceof ShipAPI){
                ShipAPI ship = (ShipAPI) entity;
                float currentCR = ship.getCurrentCR();
                float maxCR = 1.0f;

                probability *= (1 - (currentCR / maxCR));

                //当前cr越低，引擎熄火几率越高
                if (probability >= BASE_MALFUNCTION_CHANCE ){
                    ship.getEngineController().forceFlameout();
                }
            }
        }
    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        stats.getMaxSpeed().unmodify(id);
        stats.getAcceleration().unmodify(id);
        stats.getTurnAcceleration().unmodify(id);
        stats.getCRLossPerSecondPercent().unmodify(id);
    }

    @Override
    public StatusData getStatusData(int index, State state, float effectLevel) {
        switch (index){
            case 0:
                return new StatusData("最大速度提升" + (int)MAX_SPEED_BONUS, false);
            case 1:
                return new StatusData("加速度提升至" + (int)(ACCELERATION_MULT * 100f) + "%", false);
            case 2:
                return new StatusData("转向速度提升至" + (int)(TURN_ACCELERATION_MULT * 100f) + "%", false);
            case 3:
                return new StatusData("每秒CR消耗增加至" + (int)(CR_LOSS_MULT * 100f) + "%", true);
            default:
                return null;
        }
    }
}
