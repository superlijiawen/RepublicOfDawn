package data.scripts.weapons;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class RD_SpecialParticle implements OnHitEffectPlugin {

    private static final Color COLOR = new Color(200, 34, 200);

    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, CombatEngineAPI engine) {
        if (point != null){

            Vector2f center = target.getLocation();
            float radius = target.getCollisionRadius();
            float duration = MathUtils.getRandomNumberInRange(0.4f, 1.0f);

            Vector2f vel = MathUtils.getRandomPointOnCircumference(center, radius);

            int num = MathUtils.getRandomNumberInRange(4, 8);

            for (int i = 0; i < num ; i++){
                engine.addHitParticle(point, vel, 6.0f, 0.6f, duration, COLOR);
            }
        }
    }
}
