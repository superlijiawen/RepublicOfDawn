package data.hullmods;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import data.utils.api.RD_BaseHullMod;

import java.util.Set;

/**
 * 黎明高端引擎
 *
 * 只有黎明共和国的舰船才能改造
 *
 * buff:
 * 最大航速提升 25%
 *
 *
 * debuff_
 * 峰值时间减少 50%
 */
public class RD_HighEndEngine extends RD_BaseHullMod {

    private static final float FLEXIBILITY_MULT = 1.25f;
    private static final float CR_LOSS_MULT = 1.50f;
    private static final float ENGINE_DAMAGE_TAKEN_MULT = 2.0f;

    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getMaxSpeed().modifyMult(id, FLEXIBILITY_MULT);
        stats.getTurnAcceleration().modifyMult(id, FLEXIBILITY_MULT);
        stats.getMaxTurnRate().modifyMult(id, FLEXIBILITY_MULT);
        stats.getAcceleration().modifyMult(id, FLEXIBILITY_MULT);
        stats.getDeceleration().modifyMult(id, FLEXIBILITY_MULT);
        stats.getZeroFluxSpeedBoost().modifyMult(id, FLEXIBILITY_MULT);

        stats.getCRLossPerSecondPercent().modifyMult(id, CR_LOSS_MULT);
        stats.getEngineDamageTakenMult().modifyMult(id, ENGINE_DAMAGE_TAKEN_MULT);
    }

    @Override
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        switch (index){
            case 0:
                return "25%";
            case 1:
                return "150%";
            case 2:
                return "200%";
        }
        return null;
    }

    @Override
    public boolean isApplicableToShip(ShipAPI ship){
        if (ship.getHullSpec().getHullId().equals("rd_falcon_special")){
            return true;
        }

        Set<String> tags = ship.getHullSpec().getTags();
        return tags.contains("rd_basic_bp") || tags.contains("rd_high_bp");
    }

    @Override
    public String getUnapplicableReason(ShipAPI ship){
        return "只有黎明共和国的舰船才能安装此插件";
    }
}
