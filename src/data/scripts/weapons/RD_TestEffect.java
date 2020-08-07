package data.scripts.weapons;

import com.fs.starfarer.api.combat.*;
import org.lwjgl.util.vector.Vector2f;

public class RD_TestEffect implements OnHitEffectPlugin {


    //applyDamage
    //void applyDamage(CombatEntityAPI entity,
    //                 Vector2f point,
    //                 float damageAmount,
    //                 DamageType damageType,
    //                 float empAmount,
    //                 boolean bypassShields,
    //                 boolean dealsSoftFlux,
    //                 java.lang.Object source,
    //                 boolean playSound)
    //Parameters:
    //entity -
    //point - Location the damage is dealt at, in absolute engine coordinates (i.e. *not* relative to the ship). MUST fall within the sprite of a ship, given its current location and facing, for armor to properly be taken into account.
    //damageAmount -
    //damageType -
    //empAmount -
    //bypassShields - Whether shields are ignored completely.
    //dealsSoftFlux - Whether damage dealt to shields results in soft flux.
    //source - Should be a ShipAPI if the damage ultimately attributed to it. Can also be null.
    //playSound - Whether a sound based on the damage dealt should be played.

    //CombatEntityAPI spawnEmpArcPierceShields(ShipAPI damageSource,
    //                                         Vector2f point,
    //                                         CombatEntityAPI pointAnchor,
    //                                         CombatEntityAPI empTargetEntity,
    //                                         DamageType damageType,
    //                                         float damAmount,
    //                                         float empDamAmount,
    //                                         float maxRange,
    //                                         java.lang.String impactSoundId,
    //                                         float thickness,
    //                                         java.awt.Color fringe,
    //                                         java.awt.Color core


    //void addHitParticle(Vector2f loc,
    //                    Vector2f vel,
    //                    float size,
    //                    float brightness,
    //                    float duration,
    //                    java.awt.Color color)
    //Particle with a somewhat brighter middle.
    //Parameters:
    //brightness - from 0 to 1
    //duration - in seconds

    //addSmoothParticle
    //void addSmoothParticle(Vector2f loc,
    //                       Vector2f vel,
    //                       float size,
    //                       float brightness,
    //                       float duration,
    //                       java.awt.Color color)
    //Standard glowy particle.
    //Parameters:
    //brightness - from 0 to 1
    //duration - in seconds

    //spawnExplosion
    //void spawnExplosion(Vector2f loc,
    //                    Vector2f vel,
    //                    java.awt.Color color,
    //                    float size,
    //                    float maxDuration)
    //Purely visual.

    //spawnEmpArc
    //CombatEntityAPI spawnEmpArc(ShipAPI damageSource,
    //                            Vector2f point,
    //                            CombatEntityAPI pointAnchor,
    //                            CombatEntityAPI empTargetEntity,
    //                            DamageType damageType,
    //                            float damAmount,
    //                            float empDamAmount,
    //                            float maxRange,
    //                            java.lang.String impactSoundId,
    //                            float thickness,
    //                            java.awt.Color fringe,
    //                            java.awt.Color core)
    //Parameters:
    //damageSource - Ship that's ultimately responsible for dealing the damage of this EMP arc. Can be null.
    //point - starting point of the EMP arc, in absolute engine coordinates.
    //pointAnchor - The entity the starting point should move together with, if any.
    //empTargetEntity - Target of the EMP arc. If it's a ship, it will randomly pick an engine nozzle/weapon to arc to. Can also pass in a custom class implementing CombatEntityAPI to visually target the EMP at a specific location (and not do any damage).
    //damageType -
    //damAmount -
    //empDamAmount -
    //maxRange - Maximum range the arc can reach (useful for confining EMP arc targets to the area near point)
    //impactSoundId - Can be null.
    //thickness - Thickness of the arc (visual).
    //fringe -
    //core -
    //Returns:

    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, CombatEngineAPI engine) {
        if (!shieldHit && target instanceof ShipAPI) {
            ShipAPI ship = projectile.getSource();
            if (ship != null && ship.isAlive()) {


//                engine.applyDamage(target, point, // where to apply damage
//                        explosionDamage(), // amount of damage
//                        DamageType.HIGH_EXPLOSIVE, // damage type
//                        EXPLOSION_EMP, // amount of EMP damage
//                        false, // does this bypass shields? (no)
//                        false, // does this deal soft flux? (no)
//                        projectile.getSource());
//
//                }

                float damage = projectile.getDamageAmount();
                float emp = projectile.getEmpAmount();


//                engine.applyDamage(
//                        target, point,
//                );
            }
        }

//    // @Inline
//    private static float explosionDamage()
//    {
//        return (float) (rng.nextInt(
//                (EXPLOSION_DAMAGE_MAX - EXPLOSION_DAMAGE_MIN) + 1)
//                + EXPLOSION_DAMAGE_MIN);
//    }

    }

}
