package data.scripts.campaign.fleet;


import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import data.utils.api.RD_BaseAssignmentAI;
import data.utils.FleetUtils;
import org.lazywizard.lazylib.MathUtils;

import java.util.List;

/**
 * 黎明之刃
 */
public class RD_DawnBlade extends RD_BaseAssignmentAI {

    protected RD_DawnBlade(CampaignFleetAPI fleet, SectorEntityToken entity, FleetAssignment fleetAssignment, float maxDurationInDays) {
        super(fleet, entity, fleetAssignment, maxDurationInDays);
    }

    public RD_DawnBlade(CampaignFleetAPI fleet, SectorEntityToken entity){
        super(fleet, entity, FleetAssignment.STANDING_DOWN, 90);
    }

    public void advance(float amount) {
        super.advance(amount);
    }

    protected void pickNext() {
        super.pickNext();

        float distance = MathUtils.getDistance(this.entity.getLocation(), this.fleet.getLocation());

        if (distance >= 200 && this.fleet.getCurrentAssignment().getAssignment() == FleetAssignment.STANDING_DOWN){
            this.giveInitialAssignments();
        }
    }

    protected void giveInitialAssignments() {
        if (this.fleet != null && this.fleet.isAlive() && this.fleet.getCurrentAssignment().getAssignment() != FleetAssignment.STANDING_DOWN){
            CampaignFleetAPI targetFleet = FleetUtils.getNearbyFleetToAttack(fleet, 1500);
            if (targetFleet == null){
                this.fleet.addAssignment(FleetAssignment.PATROL_SYSTEM, this.entity, 60);
            }else {
                this.fleet.addAssignment(FleetAssignment.ATTACK_LOCATION, targetFleet, 5);
                if (!targetFleet.isAlive()){
                    this.fleet.addAssignment(FleetAssignment.PATROL_SYSTEM, this.entity, 60);
                }
            }
        }
    }

    @Deprecated
    private StarSystemAPI pickEnemySystem(){
        SectorAPI sector = Global.getSector();
        StarSystemAPI targetSystem = null;

        FactionAPI selfFaction = sector.getFaction(FACTION_ID);
        List<FactionAPI> factionList = sector.getAllFactions();

        for (FactionAPI faction : factionList) {
            boolean isHostile = selfFaction.getRelationshipLevel(faction).isAtWorst(RepLevel.HOSTILE);
            float weight = 0f;
            FactionAPI targetFaction = null;

            return null;
        }

        return null;
    }


}
