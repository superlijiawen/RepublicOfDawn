package data.scripts.campaign.fleet;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FleetAssignment;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.impl.campaign.procgen.themes.BaseAssignmentAI;
import org.lazywizard.lazylib.MathUtils;

import java.util.List;

public class RD_Wander extends BaseAssignmentAI {

    protected SectorEntityToken entity;
    protected FleetAssignment fleetAssignment;
    protected float maxDurationDays;

    public RD_Wander(CampaignFleetAPI fleet, SectorEntityToken entity, FleetAssignment fleetAssignment, float maxDurationInDays){
        this.fleet = fleet;
        this.entity = entity;
        this.fleetAssignment = fleetAssignment;
        this.maxDurationDays = maxDurationInDays;
    }

    public void advance(float amount) {
        if (this.fleet.getCurrentAssignment() == null){
            this.fleet.addAssignment(this.fleetAssignment, this.entity, 1.0f);
        }else{
            this.pickNext();
        }
    }

    //void addAssignment(FleetAssignment assignment, 舰队任务
    //                   SectorEntityToken target, entity
    //                   float maxDurationInDays) 最大时间
    protected void pickNext() {
        this.giveInitialAssignments();
    }

    @Override
    protected void giveInitialAssignments() {
//        FleetAssignment assignment = this.chooseAssignment();
//        if (assignment != null){
//            StarSystemAPI system = this.pickSystem();
//            if (system != null){
//                List<SectorEntityToken> entities = system.getAllEntities();
//                SectorEntityToken target = null;
//                if (entities != null && entities.size() != 0){
//                    int index = MathUtils.getRandomNumberInRange(0, entities.size() - 1);
//                    target = entities.get(index);
//                    this.fleet.addAssignment(assignment, target, this.maxDurationDays);
//                }
//            }else{
//                this.fleet.clearAssignments();
//                this.fleet.addAssignment(FleetAssignment.ORBIT_PASSIVE, this.entity, this.maxDurationDays);
//            }
//        }
        this.fleet.addAssignment(FleetAssignment.PATROL_SYSTEM, this.entity, this.maxDurationDays);
    }


    @Deprecated
    private FleetAssignment chooseAssignment() {
        int random = MathUtils.getRandomNumberInRange(1, 100);
        FleetAssignment assignment = null;
        if (random <= 20){
            assignment = FleetAssignment.RAID_SYSTEM;
        } else{
            assignment = FleetAssignment.PATROL_SYSTEM;
        }

        return assignment;
    }

    @Deprecated
    private StarSystemAPI pickSystem() {
        List<StarSystemAPI> systems = Global.getSector().getEconomy().getStarSystemsWithMarkets();
        if (systems.size() != 0){
            int size = systems.size();
            return systems.get(MathUtils.getRandomNumberInRange(0, size - 1));
        }

        return null;
    }

}
