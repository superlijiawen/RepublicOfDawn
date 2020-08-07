package data.shipsystems.scripts;


import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;

import java.util.*;

/**
 * 导弹再生工厂
 * 导弹伤害提升25%
 * 导弹数量提升50%
 *
 * 如果导弹数量为0,则增加1/6的最大导弹数量，最少为1颗
 */
@Deprecated
public class RD_MissileRecyclingFactory extends BaseShipSystemScript {


    private static final float MISSLE_DAMAGE_MULT = 1.25f;
    private static final float MISSLE_AMMO_BONUS_MULT = 1.5f;

    private Map<WeaponAPI, Integer> missileMap = new LinkedHashMap<>();

    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        ShipAPI ship = (ShipAPI) stats.getEntity();
        if (ship != null){
            //增加导弹数量
            List<WeaponAPI> weapons = ship.getAllWeapons();
            if (weapons.size() != 0){
                int ammo = 0;
                for (WeaponAPI weapon : weapons) {
                    if (weapon.getType().equals(WeaponAPI.WeaponType.MISSILE)){
                        missileMap.put(weapon, weapon.getAmmo());

                        if (weapon.getAmmo() == 0){
                            weapon.setAmmo(Math.max(1, weapon.getMaxAmmo() / 6));
                        }

                        ammo = Math.round(weapon.getAmmo() * MISSLE_AMMO_BONUS_MULT);
                        weapon.setAmmo(ammo);
                    }
                }
            }

            //增加导弹伤害
            stats.getMissileWeaponDamageMult().modifyFlat(id, MISSLE_DAMAGE_MULT);

        }
    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        if (missileMap.size() != 0){
            Set<WeaponAPI> weaponSet = missileMap.keySet();
            for (WeaponAPI weapon : weaponSet) {
                weapon.setAmmo(missileMap.get(weapon));
            }
        }

        stats.getMissileWeaponDamageMult().unmodify(id);
    }

    @Override
    public ShipSystemStatsScript.StatusData getStatusData(int index, ShipSystemStatsScript.State state, float effectLevel) {
        if (index == 0){
            return new StatusData("导弹伤害提升25%", false);
        }else if (index == 1){
            return new StatusData("导弹数量提升50%", false);
        }
        return null;
    }
}
