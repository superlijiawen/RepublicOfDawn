package data.scripts.campaign.intel.bar;

import com.fs.starfarer.api.impl.campaign.intel.bar.PortsideBarEvent;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.BaseBarEventCreator;

public class RD_BarRewardEventCreator extends BaseBarEventCreator {

    public RD_BarRewardEventCreator() {

    }

    @Override
    public PortsideBarEvent createBarEvent() {
        return new RD_BarRewardEvent();
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
