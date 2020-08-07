package data.hullmods;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import data.utils.api.RD_BaseHullMod;

/**
 * 人性化后勤
 * 宇宙航速增加1 最大速度增加10%
 *
 */
public class RD_HumanizedLogistics extends RD_BaseHullMod {

    private static final float MAX_BURN_LEVEL_BONUS = 1f;
    private static final float MAX_SPEED = 1.10f;

    public RD_HumanizedLogistics(){
    }

    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getMaxBurnLevel().modifyFlat(id, MAX_BURN_LEVEL_BONUS);
        stats.getMaxSpeed().modifyMult(id, MAX_SPEED);
    }

    @Override
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        if (index == 0){
            return this.addDesc(MAX_BURN_LEVEL_BONUS, false);
        }else if (index == 1){
            return this.addDesc((MAX_SPEED - 1) * 100, true);
        }else{
            return null;
        }
    }

    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        return !ship.getVariant().getHullMods().contains("augmentedengines");
    }

    public String getUnapplicableReason(ShipAPI ship) {
        return !this.isApplicableToShip(ship) ? "与增强驱动力场冲突" :  null;
    }
}
