package data.hullmods;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import data.utils.api.RD_BaseHullMod;

/**
 * 共和国净化-进攻型
 * 内置插件 只能黎明共和国的船只使用
 * 实弹武器伤害、能量武器伤害、辐能耗散速率均提升5%
 *
 */
@SuppressWarnings("all")
public class RD_RepublicPurification_Attack extends RD_BaseHullMod {

    private static final float BOUNS = 1.05f;


    public RD_RepublicPurification_Attack(){

    }

    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getBallisticWeaponDamageMult().modifyMult(id, BOUNS);
        stats.getEnergyWeaponDamageMult().modifyMult(id, BOUNS);
        stats.getFluxDissipation().modifyMult(id, BOUNS);
    }

    @Override
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        if (index == 0){
            return "5%";
        }
        return null;
    }

}
