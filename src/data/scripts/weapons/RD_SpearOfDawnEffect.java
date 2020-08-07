package data.scripts.weapons;

import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.util.IntervalUtil;
import org.lwjgl.util.vector.Vector2f;

public class RD_SpearOfDawnEffect implements BeamEffectPlugin {

    private IntervalUtil tracker = new IntervalUtil(0.1F, 0.4F);
    private boolean wasZero = true;

    public RD_SpearOfDawnEffect() {

    }

    @Override
    public void advance(float amount, CombatEngineAPI engine, BeamAPI beam) {
        CombatEntityAPI target = beam.getDamageTarget();
        if (target instanceof ShipAPI){
            float dur = beam.getDamage().getDpsDuration();
            if (!this.wasZero) {
                dur = 0.0F;
            }

            this.wasZero = beam.getDamage().getDpsDuration() <= 0.0F;
            this.tracker.advance(dur);
            if (this.tracker.intervalElapsed()) {
                ShipAPI ship = (ShipAPI)target;
                boolean hitShield = target.getShield() != null && target.getShield().isWithinArc(beam.getTo());
                float pierceChance = ((ShipAPI)target).getHardFluxLevel() - 0.1F;
                pierceChance *= ship.getMutableStats().getDynamic().getValue("shield_pierced_mult");
                boolean piercedShield = hitShield && (float)Math.random() < pierceChance;
                if (!hitShield || piercedShield) {
                    Vector2f dir = Vector2f.sub(beam.getTo(), beam.getFrom(), new Vector2f());
                    if (dir.lengthSquared() > 0.0F) {
                        dir.normalise();
                    }

                    dir.scale(60.0F);
                    Vector2f point = Vector2f.sub(beam.getTo(), dir, new Vector2f());
                    float emp = beam.getWeapon().getDamage().getFluxComponent() * 0.15F;
                    float dam = beam.getWeapon().getDamage().getDamage() * 0.15F;
                    engine.spawnEmpArcPierceShields(beam.getSource(), point, beam.getDamageTarget(), beam.getDamageTarget(), DamageType.ENERGY, dam, emp, 100000.0F, "tachyon_lance_emp_impact", beam.getWidth() + 5.0F, beam.getFringeColor(), beam.getCoreColor());
                }
            }
        }
    }
}
