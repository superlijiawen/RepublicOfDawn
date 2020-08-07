package data.shipsystems.scripts;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;

/**
 * 追踪者系统
 * 效果：
 * 短时间内大幅度提高航速及转向性
 * 提升最大速度 加速度 转向速度 至 150%
 * 辐能耗散速率提升至 150%
 *
 * dubuff
 * 使用期间禁用护盾
 * cr损失率提升至300%
 */
public class RD_Tracker extends BaseShipSystemScript {

    private static final float MAX_SPEED_BONUS = 70.0f;
    private static final float ACCLERATION_MULT = 1.5f;
    private static final float TURN_ACCELERATION = 1.5f;
    private static final float FLUX_DISSIPATION = 1.5F;
    private static final float CR_LOSS_MULT = 3.0f;


    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        if (state != State.OUT){
            stats.getMaxSpeed().modifyFlat(id, MAX_SPEED_BONUS);
            stats.getAcceleration().modifyMult(id, ACCLERATION_MULT);
            stats.getTurnAcceleration().modifyMult(id, TURN_ACCELERATION);
            stats.getFluxDissipation().modifyMult(id, FLUX_DISSIPATION);
            stats.getCRLossPerSecondPercent().modifyMult(id, CR_LOSS_MULT);
        }else {
            stats.getMaxSpeed().unmodify(id);
            stats.getAcceleration().unmodify(id);
            stats.getTurnAcceleration().unmodify(id);
            stats.getFluxDissipation().unmodify(id);
            stats.getCRLossPerSecondPercent().unmodify(id);
        }
    }



    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        stats.getMaxSpeed().unmodify(id);
        stats.getAcceleration().unmodify(id);
        stats.getTurnAcceleration().unmodify(id);
        stats.getFluxDissipation().unmodify(id);
        stats.getCRLossPerSecondPercent().unmodifyFlat(id);
    }

    @Override
    public StatusData getStatusData(int index, State state, float effectLevel) {
        if (index == 0){
            return new StatusData("最大速度增加70", false);
        }else if (index == 1){
            return new StatusData("加速度、转向速度提升至150%", false) ;
        }else if (index == 2){
            return new StatusData("辐能耗散速率提升至150%", false);
        }else if (index == 3){
            return new StatusData("CR损失率提升至300%", true);
        }

        return null;
    }
}
