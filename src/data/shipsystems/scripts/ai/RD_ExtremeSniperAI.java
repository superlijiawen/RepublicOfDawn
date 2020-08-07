package data.shipsystems.scripts.ai;

import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.util.IntervalUtil;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.util.List;

public class RD_ExtremeSniperAI implements ShipSystemAIScript {

    private ShipAPI ship;
    private ShipSystemAPI system;

    private IntervalUtil tracker = new IntervalUtil(0.5f, 1f);


    @Override
    public void init(ShipAPI ship, ShipSystemAPI system, ShipwideAIFlags flags, CombatEngineAPI engine) {
        this.ship = ship;
        this.system = system;
    }


    @Override
    public void advance(float amount, Vector2f missileDangerDir, Vector2f collisionDangerDir, ShipAPI target) {
        tracker.advance(amount);

        if (this.tracker.intervalElapsed() && !this.system.isActive()){

            if (this.ship == null){
                return;
            }

            if (this.system.isCoolingDown()){
                return;
            }

            if (target == null){
                return;
            }


            //得到能量武器最大射程
            List<WeaponAPI> weapons = this.ship.getAllWeapons();
            float maxRange = 0.0f;
            float currentRange;
            for (WeaponAPI weapon : weapons) {
                if (weapon.getType().equals(WeaponAPI.WeaponType.ENERGY)){
                    currentRange = weapon.getRange();
                    if (currentRange > maxRange){
                        maxRange = currentRange;
                    }
                }
            }

            if (maxRange == 0f){
                return;
            }

            Vector2f self = this.ship.getLocation();
            Vector2f enemy = target.getLocation();

            float distance = MathUtils.getDistance(self, enemy);

            if (distance >= maxRange * 1.5f){
                this.ship.useSystem();
            }

        }
    }
}
