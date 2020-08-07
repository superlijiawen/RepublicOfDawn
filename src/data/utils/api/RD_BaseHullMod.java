package data.utils.api;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShipAPI;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RD_BaseHullMod extends BaseHullMod {

    public RD_BaseHullMod(){
    }

    protected void init0(Map<ShipAPI.HullSize, Float> map ,float var1, float var2, float var3, float var4) {
            map.put(ShipAPI.HullSize.FRIGATE, var1);
            map.put(ShipAPI.HullSize.DESTROYER, var2);
            map.put(ShipAPI.HullSize.CRUISER, var3);
            map.put(ShipAPI.HullSize.CAPITAL_SHIP, var4);
    }

    protected void init(Map<ShipAPI.HullSize, Float> map, float var1, float var2, float var3,float var4) {
        if (var1 != 0){
            map.put(ShipAPI.HullSize.FRIGATE, var1);
        }
        if (var2 != 0){
            map.put(ShipAPI.HullSize.DESTROYER, var2);
        }
        if (var3 != 0){
            map.put(ShipAPI.HullSize.CRUISER, var3);
        }
        if (var4 != 0){
            map.put(ShipAPI.HullSize.CAPITAL_SHIP, var4);
        }
    }

    protected float getCargo(ShipAPI ship){
        return ship.getMutableStats().getCargoMod().computeEffective(ship.getHullSpec().getCargo());
    }

    protected String addDesc(Float data, boolean havePercent){
        if (havePercent){
            return "" + data.intValue() + "%";
        }else{
            return "" + data.intValue();
        }
    }

    protected ShipAPI.HullSize numToHullSize(Float x){
        int s = x.intValue();
        switch(s){
            case 0:
                return ShipAPI.HullSize.FRIGATE;
            case 1:
                return ShipAPI.HullSize.DESTROYER;
            case 2:
                return ShipAPI.HullSize.CRUISER;
            case 3:
                return ShipAPI.HullSize.CAPITAL_SHIP;
            default:
                return null;
        }
    }

    protected boolean isHaveShieldAndNoConflict(ShipAPI ship, List<String> conflictList){
        if (ship != null && ship.getShield() != null){
            Collection<String> hullMods = ship.getVariant().getHullMods();
            for (String hullMod : conflictList) {
                if (hullMods.contains(hullMod)){
                    return false;
                }
            }

            return true;
        }

        return false;
    }

}
