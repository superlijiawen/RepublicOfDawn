package data.hullmods;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import data.utils.api.RD_BaseHullMod;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 人性化目标定位系统
 * 特点： 根据舰船级别，提高实弹武器和能量武器 10 / 18 / 36 / 54 %射程 提高导弹射程 20%
 * 冲突：与目标定位系统、先进目标定位核心、专注型目标锁定核心冲突
 *
 */
public class RD_HumanizedTargetPositioning extends RD_BaseHullMod {

    private Map<ShipAPI.HullSize, Float> map = new HashMap<>();
    private static final float MISSILE_RANGE_BONUS = 20f;


    public RD_HumanizedTargetPositioning(){
        this.init0(map, 10f, 18f, 36f, 54F);
    }

    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getBallisticWeaponRangeBonus().modifyPercent(id, map.get(hullSize));
        stats.getBeamWeaponRangeBonus().modifyPercent(id, map.get(hullSize));
        stats.getMissileWeaponRangeBonus().modifyPercent(id, MISSILE_RANGE_BONUS);
    }

    @Override
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        switch (index){
            case 0:
                return this.addDesc(map.get(ShipAPI.HullSize.FRIGATE), true);
            case 1:
                return this.addDesc(map.get(ShipAPI.HullSize.DESTROYER), true);
            case 2:
                return this.addDesc(map.get(ShipAPI.HullSize.CRUISER), true);
            case 3:
                return this.addDesc(map.get(ShipAPI.HullSize.CAPITAL_SHIP), true);
            case 4:
                return this.addDesc(MISSILE_RANGE_BONUS, true);
            default:
                return null;
        }
    }

    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        Collection<String> hullMods = ship.getVariant().getHullMods();
        return !hullMods.contains("dedicated_targeting_core") && !hullMods.contains("advancedcore") && !hullMods.contains("targetingunit");
    }

    public String getUnapplicableReason(ShipAPI ship) {
        Collection<String> hullMods = ship.getVariant().getHullMods();
        if (hullMods.contains("dedicated_targeting_core")) {
            return "与专注型目标锁定核心冲突";
        }
        if (hullMods.contains("advancedcore")) {
            return "与先进目标定位核心冲突";
        }
        if (hullMods.contains("targetingunit")){
            return "与目标定位系统冲突";
        }
        return null;
    }

}
