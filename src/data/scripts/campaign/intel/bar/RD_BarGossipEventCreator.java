package data.scripts.campaign.intel.bar;

import com.fs.starfarer.api.impl.campaign.intel.bar.PortsideBarEvent;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.BaseBarEventCreator;
import data.scripts.campaign.intel.bar.RD_BarGossipEvent;

public class RD_BarGossipEventCreator extends BaseBarEventCreator {

    public RD_BarGossipEventCreator() {

    }

    @Override
    public PortsideBarEvent createBarEvent() {
        return new RD_BarGossipEvent();
    }

    @Override
    public float getBarEventActiveDuration() {
        return super.getBarEventActiveDuration();
    }

    @Override
    public float getBarEventFrequencyWeight() {
        return super.getBarEventFrequencyWeight();
    }

    @Override
    public float getBarEventTimeoutDuration() {
        return super.getBarEventTimeoutDuration();
    }

    @Override
    public float getBarEventAcceptedTimeoutDuration() {
        return super.getBarEventAcceptedTimeoutDuration();
    }

    @Override
    public boolean isPriority() {
        return super.isPriority();
    }
}
