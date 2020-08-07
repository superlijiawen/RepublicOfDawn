package data.scripts.weapons;

import com.fs.starfarer.api.combat.*;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.util.Arrays;
import java.util.List;

/**
 * 蹂躏者激光炮
 * 当命中的船体时战机、护卫舰、驱逐舰时
 * 当命中船体或装甲时，有{50%}的概率产生和武器命中目标时产生的伤害等值的额外护盾穿透伤害(仅限战机、护卫舰、驱逐舰)
 *
 */
public class RD_RavagerLaserCannonOnHitEffect implements OnHitEffectPlugin {

    private static final List<ShipAPI.HullSize> TYPES = Arrays.asList(
            ShipAPI.HullSize.FIGHTER,
            ShipAPI.HullSize.FRIGATE,
            ShipAPI.HullSize.DESTROYER
    );

    public RD_RavagerLaserCannonOnHitEffect(){

    }

    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, CombatEngineAPI engine) {
//        if ((float)Math.random() > 0.75F && !shieldHit && target instanceof ShipAPI) {
//            float emp = projectile.getEmpAmount();
//            float dam = projectile.getDamageAmount();
//            engine.spawnEmpArc(projectile.getSource(), point, target, target, DamageType.ENERGY, dam, emp, 100000.0F, "tachyon_lance_emp_impact", 20.0F, new Color(25, 100, 155, 255), new Color(255, 255, 255, 255));
//        }

        if (shieldHit && target instanceof ShipAPI){
            ShipAPI.HullSize hullSize = ((ShipAPI) target).getHullSize();

            float damage = projectile.getDamageAmount();
            if (TYPES.contains(hullSize)){
                float random = MathUtils.getRandomNumberInRange(0, 1.0f);
                if (random <= 0.50f){
                    engine.spawnEmpArcPierceShields(projectile.getSource(), point, target, target,
                            DamageType.ENERGY, damage, damage, 100000.0f, "tachyon_lance_emp_impact",
                            20.0f, RD_EffectColor.FRIGE1, RD_EffectColor.CORE1);
                }
            }else{
                engine.applyDamage(target, point, 0.25f * damage, DamageType.ENERGY, 0, false, false, projectile.getSource());
            }
        }
    }
}
