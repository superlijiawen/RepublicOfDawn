package data.shipsystems.scripts;

import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;

/**
 * 曙光爆发者
 *
 * 增加5%实弹武器基础伤害，实弹武器辐能使用降低25%。带有额外的伤害增益效果
 *
 * 特殊：
 * 自身结构值越低，增益值越大。当结构值是10%时，增益值可达50%
 * 当结构值低于10%时，增益效果突增为200%,即实弹武器伤害为300%
 */
public class RD_DawnBreaker extends BaseShipSystemScript {

    private static final float BASE_BONUS = 0.05f;
    private static final float FLUX_COST_MULT = 0.75f;

    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        CombatEntityAPI entity = stats.getEntity();
        if (entity instanceof ShipAPI){
            ShipAPI ship = (ShipAPI) entity;
            float hitpoints = ship.getHitpoints();
            float maxHitpoints = ship.getMaxHitpoints();
            float ratio = hitpoints / maxHitpoints;
            float mult = 1 + BASE_BONUS + (1.0f - ratio) * 0.5f;

            stats.getBallisticWeaponFluxCostMod().modifyMult(id, FLUX_COST_MULT);

            if (ratio > 0.1f){
                stats.getBallisticWeaponDamageMult().modifyMult(id, mult);
            }else {
                stats.getBallisticWeaponDamageMult().modifyMult(id, 3.0f);
            }

        }
    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        stats.getBallisticWeaponDamageMult().unmodify(id);
        stats.getBallisticWeaponFluxCostMod().unmodify(id);
    }

    @Override
    public StatusData getStatusData(int index, State state, float effectLevel) {
        if (index == 0){
            return new StatusData("开启爆发者模式", false);
        }
        return null;
    }

}
