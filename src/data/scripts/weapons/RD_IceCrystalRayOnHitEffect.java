package data.scripts.weapons;

import com.fs.starfarer.api.combat.*;
import org.lwjgl.util.vector.Vector2f;

import java.util.Random;

/**
 * 命中船体时，会根据自身辐能产生额外的emp伤害，伤害基础值为造成的伤害的一半乘于当前的辐能所占最大辐能的百分比，有10%~100%
 * 之间波动。最大可造成原伤害50%的额外emp伤害。
 *
 * 当自身幅能水平在85以上时，命中船体将有50%几率使对方发动机熄火
 */

public class RD_IceCrystalRayOnHitEffect implements OnHitEffectPlugin {

    private static final float ARC_DAMAGE = 1.0f;


    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, CombatEngineAPI engine) {
        if (!shieldHit && target instanceof ShipAPI){
            ShipAPI ship = projectile.getSource();
            if (ship != null && ship.isAlive()){
                float currFlux = ship.getCurrFlux();
                float maxFlux = ship.getMaxFlux();

                float emp = 0;
                float damage = projectile.getDamageAmount();
                emp = (float) (damage * 0.50  * (float) (currFlux / maxFlux) * (float) Math.max(0.1f, Math.random()));

                engine.spawnEmpArcPierceShields(ship, point, target, target,
                        DamageType.ENERGY, damage, emp, 100000.0f, "tachyon_lance_emp_impact",
                        20.0f, RD_EffectColor.FRIGE2, RD_EffectColor.CORE2);

                if (currFlux >= maxFlux * 0.85f){
                    boolean effect = new Random().nextBoolean();
                    if (effect){
                        ShipAPI targetShip = ((ShipAPI) target).getShipTarget();
                        if (targetShip != null && targetShip.isAlive()){
                            if (!targetShip.getEngineController().isDisabled())
                            //对方的引擎有50%几率熄火
                            targetShip.getEngineController().forceFlameout();
                        }
                    }
                }

            }

        }
    }

}
