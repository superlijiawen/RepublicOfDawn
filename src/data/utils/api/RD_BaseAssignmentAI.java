package data.utils.api;

import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FleetAssignment;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.impl.campaign.procgen.themes.BaseAssignmentAI;

public class RD_BaseAssignmentAI extends BaseAssignmentAI {

    protected static final String FACTION_ID = "republic_of_dawn";

    protected SectorEntityToken entity;
    protected FleetAssignment fleetAssignment;
    protected float maxDurationDays;

    protected RD_BaseAssignmentAI(CampaignFleetAPI fleet,SectorEntityToken entity, FleetAssignment fleetAssignment, float maxDurationInDays){
        this.fleet = fleet;
        this.entity = entity;
        this.fleetAssignment = fleetAssignment;
        this.maxDurationDays = maxDurationInDays;
    }

    public void advance(float amount) {

    }

    //void addAssignment(FleetAssignment assignment, 舰队任务
    //                   SectorEntityToken target, entity
    //                   float maxDurationInDays) 最大时间
    protected void pickNext() {
    }

    @Override
    protected void giveInitialAssignments() {

    }


}
