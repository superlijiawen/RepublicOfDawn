package data.shipsystems.scripts;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;

@Deprecated
public class RD_Template extends BaseShipSystemScript {

    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        super.apply(stats, id, state, effectLevel);
    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        super.unapply(stats, id);
    }

    @Override
    public StatusData getStatusData(int index, State state, float effectLevel) {
        return super.getStatusData(index, state, effectLevel);
    }
}
