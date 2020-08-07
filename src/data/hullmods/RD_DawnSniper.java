package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import data.utils.api.RD_BaseHullMod;

import java.util.Collection;
import java.util.List;

/**
 * 黎明狙击
 *
 * 能量武器射程提高 5%
 *
 * 每安装一个 "黎明共和国" 的武器或者插件，都会提高 1% 射程及能量武器伤害
 */
public class RD_DawnSniper extends RD_BaseHullMod {


    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        super.advanceInCombat(ship, amount);

        CombatEngineAPI combatEngine = Global.getCombatEngine();
        if (!combatEngine.getCustomData().containsKey("RD_DawnSniper")){
            combatEngine.getCustomData().put("RD_DawnSniper", ship);
        }

        if(ship.isAlive() && combatEngine.isPaused()){
            List<WeaponAPI> weapons = ship.getAllWeapons();

            String shipId = "";
            String suffix = "RD";
            int bonus = 5;
//            Set<WeaponAPI> energyWeapons = new LinkedHashSet<>();

            for (WeaponAPI weapon : weapons) {
                shipId = weapon.getId();
                if (shipId.startsWith(suffix)){
                    bonus ++;
                }
//                if (weapon.getType().equals(WeaponAPI.WeaponType.ENERGY)){
//                    energyWeapons.add(weapon);
//                }
            }

            String hullModId = "";

            Collection<String> hullMods = ship.getVariant().getHullMods();
            for (String hullMod : hullMods) {
                hullModId = hullMod;
                if (hullModId.contains(suffix)){
                    bonus ++;
                }
            }

            final float MULT = 1.0f + ((float) bonus) / 100;

            ship.getMutableStats().getEnergyWeaponRangeBonus().modifyMult("RD_DawnSniper", MULT);
            ship.getMutableStats().getEnergyWeaponDamageMult().modifyMult("RD_DawnSniper", MULT);
//            for (WeaponAPI weapon : energyWeapons) {
//            }
        }
    }

    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {

    }

    @Override
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        if (index == 0){
            return "5%";
        }else if (index == 1){
            return "1%";
        }

        return null;
    }

    @Override
    public boolean isApplicableToShip(ShipAPI ship){
        return false;
    }

    @Override
    public String getUnapplicableReason(ShipAPI ship){
        return null;
    }
}
