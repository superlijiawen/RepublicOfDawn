package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import data.utils.api.RD_BaseHullMod;

import java.util.HashMap;
import java.util.Map;


/**
 * 共和国装甲
 * 根据舰船级别增加 250 375 500 装甲值（护卫舰不可装配）
 * 但同时牺牲机动性 加速度、减速度、转向速度降低20%， 最大转向角度降低15%，货仓容量根据舰船等级降低 40 80 120
 */
@Deprecated
public class RD_RepublicArmor extends RD_BaseHullMod {

    private Map<ShipAPI.HullSize, Float> map = new HashMap<>();
    private static final float ACCELERATION_PUNISH = 20f;
    private static final float MAX_TURN_RATE_PUNISH = 15f;

    public RD_RepublicArmor() {
        map.put(ShipAPI.HullSize.FRIGATE, 0f);
        map.put(ShipAPI.HullSize.DESTROYER, 250f);
        map.put(ShipAPI.HullSize.CRUISER, 375f);
        map.put(ShipAPI.HullSize.CAPITAL_SHIP, 500f);
    }

    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {

        if (stats.getArmorBonus() != null){
            stats.getArmorBonus().modifyFlat(id, map.get(hullSize));
        }else{
            Global.getLogger(RD_RepublicArmor.class).error("异常捕获处理");
        }

        stats.getAcceleration().modifyMult(id, 1f - ACCELERATION_PUNISH * 0.01f);
        stats.getDeceleration().modifyMult(id, 1f - ACCELERATION_PUNISH * 0.01f);
        stats.getTurnAcceleration().modifyMult(id, 1f - ACCELERATION_PUNISH * 0.01f);
        stats.getMaxTurnRate().modifyMult(id, 1f - MAX_TURN_RATE_PUNISH * 0.01f);

        switch (hullSize){
            case DESTROYER://驱逐舰
                stats.getCargoMod().modifyFlat(id, -40);
                break;
            case CRUISER://巡洋舰
                stats.getCargoMod().modifyFlat(id, -80);
                break;
            case CAPITAL_SHIP://战列舰
                stats.getCargoMod().modifyFlat(id, -120);
                break;
        }
    }

    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {

        switch (index){
            case 0:
                return this.addDesc(map.get(ShipAPI.HullSize.DESTROYER), false);
            case 1:
                return this.addDesc(map.get(ShipAPI.HullSize.CRUISER), false);
            case 2:
                return this.addDesc(map.get(ShipAPI.HullSize.CAPITAL_SHIP), false);
            case 3:
                return "40";
            case 4:
                return "80";
            case 5:
                return "120";
            case 6:
                return this.addDesc(ACCELERATION_PUNISH, true);
            case 7:
                return this.addDesc(MAX_TURN_RATE_PUNISH, true);
            default:
                return null;
        }

    }

    public boolean isApplicableToShip(ShipAPI ship) {

        ShipAPI.HullSize hullSize = ship.getHullSize();
        if (hullSize != ShipAPI.HullSize.FRIGATE){
            float cargo = this.getCargo(ship);

            switch(hullSize){
                case DESTROYER:
                    return cargo >= 40;
                case CRUISER:
                    return cargo >= 80;
                case CAPITAL_SHIP:
                    return cargo >= 120;
            }

        }

        return true;
    }

    public String getUnapplicableReason(ShipAPI ship) {
        if(ship.getHullSize() == ShipAPI.HullSize.FRIGATE){
            return "护卫舰不可安装";
        }
        return !this.isApplicableToShip(ship) ? "货仓不足，无法安装" : "其他原因";
    }
}
