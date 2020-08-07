package data.scripts.weapons;

import com.fs.starfarer.api.combat.*;
import org.lwjgl.util.vector.Vector2f;

public class RD_ThunderOnHitEffect implements OnHitEffectPlugin {

    private static final float BASE_EMP_BONUS = 1.0f;

    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, CombatEngineAPI engine) {
        ShipAPI ship = null;
        if (target instanceof ShipAPI){
            ship = (ShipAPI) target;
            if (!shieldHit){
                float currFlux = ship.getCurrFlux();
                float currentCR = ship.getCurrentCR();
                float hitpoints = ship.getHitpoints();

                float maxFlux = ship.getMaxFlux();
                float maxCR = 1.0f;
                float maxHitpoints = ship.getMaxHitpoints();

                float chance = 0.10f;

                chance += 0.10f * (currFlux / maxFlux);
                chance += 0.05f * ((maxCR - currentCR) / maxCR);
                chance += 0.05f * ((maxHitpoints - hitpoints) / maxHitpoints);

                double random = Math.random();

                if (random <= chance){

                    float damage = projectile.getDamageAmount();
                    float emp = damage * 0.25f;
                    float param = 0f;

                    WeaponAPI.WeaponSize size = projectile.getWeapon().getSize();
                    switch (size){
                        case SMALL:
                            param = 0.75f;
                        case MEDIUM:
                            param = 1.0f;
                        case LARGE:
                            param = 1.25f;

                    }

                    emp = emp * BASE_EMP_BONUS * param;

                    engine.spawnEmpArcPierceShields(projectile.getSource(), point, target, target,
                            DamageType.ENERGY, damage, emp, 100000.0f, "tachyon_lance_emp_impact",
                            20.0f, RD_EffectColor.FRIGE1, RD_EffectColor.THUNDER);


                    if (random <= (chance / 3.0f)){
                        boolean disabled = ship.getEngineController().isDisabled();
                        if (!disabled){
                            ship.getEngineController().forceFlameout();//强制熄火
                        }
                    }

                }
            }
        }
    }

}
