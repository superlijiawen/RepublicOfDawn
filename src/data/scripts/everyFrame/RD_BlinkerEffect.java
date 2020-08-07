package data.scripts.everyFrame;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import com.fs.starfarer.api.combat.WeaponAPI;

public class RD_BlinkerEffect implements EveryFrameWeaponEffectPlugin {

    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        weapon.getSprite().setAdditiveBlend();
        if (weapon.getShip() != null && weapon.getShip().isAlive()){
            weapon.getAnimation().setAlphaMult(1.0f);
        }else{
            weapon.getAnimation().setAlphaMult(0f);
        }
    }
}
