package data.shipsystems.scripts;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;


/**
 * 曙光护盾
 *
 * 护盾获得 2 倍 展开速度
 * 辐能耗散速率提高100%
 * 护盾获得 5 倍 的伤害减免
 */
public class RD_RepublicShield extends BaseShipSystemScript {

    private static final float SHIELD_UNFOLD_RATE_MULT = 2.0f;
//    private static final float SHIELD_UPKEEP_MULT = 0.5f;
    private static final float SHIELD_DAMAGE_TAKEN_MULT = 0.25f;
    private static final float FLUX_DISSIPATION_MULT = 1.50f;

    /**
     *     MutableStat getShieldUpkeepMult();
     *      护盾维持修正
     *     MutableStat getShieldAbsorptionMult();
     *      护盾吸收修正
     *     MutableStat getShieldTurnRateMult();
     *      护盾转向角修正
     *     MutableStat getShieldUnfoldRateMult();
     *      护盾展开角修正
     *     MutableStat getShieldDamageTakenMult();
     *      护盾伤害减免修正
     *
     *     StatBonus getShieldArcBonus();
     *      护盾弧度bonus
     *     MutableStat getShieldMalfunctionChance();
     *      护盾故障几率
     *     MutableStat getShieldMalfunctionFluxLevel();
     *      护盾故障辐能等级？？
     */

    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
//        stats.getShieldAbsorptionMult()
//        stats.getShieldDamageTakenMult().modifyMult(id, 1f - .9f * effectLevel);
//        stats.getShieldUpkeepMult().modifyMult(id, 0f);

        stats.getShieldUnfoldRateMult().modifyMult(id, SHIELD_UNFOLD_RATE_MULT);
        stats.getShieldDamageTakenMult().modifyMult(id, SHIELD_DAMAGE_TAKEN_MULT);
        stats.getFluxDissipation().modifyMult(id, FLUX_DISSIPATION_MULT);
//        stats.getShieldUpkeepMult().modifyMult(id, SHIELD_UPKEEP_MULT);
    }

    public void unapply(MutableShipStatsAPI stats, String id) {
        stats.getShieldUnfoldRateMult().unmodify(id);
        stats.getShieldDamageTakenMult().unmodify(id);
//        stats.getShieldUpkeepMult().unmodify(id);
        stats.getFluxDissipation().unmodify(id);
    }

    public StatusData getStatusData(int index, State state, float effectLevel) {
//        if (index == 0) {
//            return new StatusData("护盾获得 10 倍 伤害减免", false);
//        }
//        return null;
        switch(index){
            case 0:
                return new StatusData("护盾获得 2 倍 展开速度", false);
//            case 1:
//                return new StatusData("护盾维持消耗辐能速率降低 50%", false);
            case 1:
                return new StatusData("辐能耗散速率提高50%", false);
            case 2:
                return new StatusData("护盾获得 4 倍 的伤害减免", false);
            default:
                return null;
        }
    }
}
