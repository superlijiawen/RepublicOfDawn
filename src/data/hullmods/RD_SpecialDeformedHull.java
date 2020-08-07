package data.hullmods;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import data.utils.api.RD_BaseHullMod;

/**
 * 特殊畸形船体（小船蹂躏者）
 *
 * buff
 * 对战机、护卫舰的伤害提升至166%
 * 对驱逐舰的伤害提升值133%
 * 对巡洋舰主力舰几乎没有伤害提升
 *
 * debuff
 * 转向性能(转向速度、最大转向角、护盾转向速度)降低10%
 * 补给消耗增加25%
 */
public class RD_SpecialDeformedHull extends RD_BaseHullMod {

    private static final float DAMAGE_MULT = 1.66f;
    private static final float PUNISH = 0.1f;
    private static final float SUPPLIES_CONSUME = 1.25f;

    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getDamageToFighters().modifyMult(id ,DAMAGE_MULT);
        stats.getDamageToFrigates().modifyMult(id, DAMAGE_MULT);
        stats.getDamageToDestroyers().modifyMult(id, DAMAGE_MULT - 0.33f);
        stats.getTurnAcceleration().modifyMult(id, 1 - PUNISH);
        stats.getMaxTurnRate().modifyMult(id, 1 - PUNISH);
        stats.getShieldTurnRateMult().modifyMult(id , 1 -PUNISH);
        stats.getSuppliesPerMonth().modifyMult(id, SUPPLIES_CONSUME);
    }

    @Override
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        if (index == 0){
            return "166%";
        }else if (index == 1){
            return "133%";
        }else if (index == 2){
            return "10%";
        }else if (index == 3){
            return "50%";
        }

        return null;
    }

}
