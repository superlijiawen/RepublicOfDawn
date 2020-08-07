package data.scripts.weapons;

import com.fs.starfarer.api.combat.*;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class RD_ApertureEffect implements OnHitEffectPlugin {

    private static final Color PARTICLE = new Color(200, 34, 200);
    private static final Color FRIGE_COLOR = new Color(200, 105, 146);
    private static final Color CORE_COLOR = new Color(200, 0, 0);

    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, CombatEngineAPI engine) {
        if (point != null){
            if (!shieldHit){
                Vector2f center = target.getLocation();
                float radius = target.getCollisionRadius();
                float duration = MathUtils.getRandomNumberInRange(0.4f, 1.0f);

                Vector2f vel = MathUtils.getRandomPointOnCircumference(center, radius);

                int num = MathUtils.getRandomNumberInRange(4, 8);

                for (int i = 0; i < num ; i++){
                    engine.addHitParticle(point, vel, 6.0f, 0.6f, duration, PARTICLE);
                }

                double random = Math.random();
                if (random >= 0.86f){
                    float damage = projectile.getDamageAmount();
                    ShipAPI ship = projectile.getSource();
                    float rate = ship.getHitpoints() / ship.getMaxHitpoints();
                    float emp = projectile.getDamageAmount() * MathUtils.getRandomNumberInRange(0.33f, 0.67f) * rate;

                    engine.spawnEmpArc(projectile.getSource(), point, target, target, DamageType.ENERGY, damage, emp, 100000.0f, "tachyon_lance_emp_impact", 20.0f, FRIGE_COLOR, CORE_COLOR);
                }
            }
        }
    }
}
