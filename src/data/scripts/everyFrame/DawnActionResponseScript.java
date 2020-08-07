package data.scripts.everyFrame;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.Script;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.FleetAssignment;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.ai.CampaignFleetAIAPI;
import com.fs.starfarer.api.campaign.ai.FleetAssignmentDataAPI;
import com.fs.starfarer.api.campaign.ai.ModularFleetAIAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;

import java.util.Iterator;
import java.util.List;

@Deprecated
public class DawnActionResponseScript implements EveryFrameScript {
    public static String RESPONSE_ASSIGNMENT = "response";
    private static String KEY = "$rd_dawn_action_response";
    protected IntervalUtil tracker = new IntervalUtil(0.05F, 0.15F);
    protected DawnActionResponseScript.DawnActionResponseParams params;
    protected float elapsed;

    public DawnActionResponseScript(DawnActionResponseScript.DawnActionResponseParams params) {
        this.params = params;
        this.addToResponseTotal();
        this.initiateResponse();
    }

    public void advance(float amount) {
        float days = Global.getSector().getClock().convertToDays(amount);
        this.tracker.advance(days);
        this.elapsed += days;
        if (this.tracker.intervalElapsed()) {
            this.initiateResponse();
        }

    }

    public void initiateResponse() {
        if (this.params.target.getContainingLocation() != null) {
            List<CampaignFleetAPI> fleets = this.params.target.getContainingLocation().getFleets();
            Iterator var3 = fleets.iterator();

            while(var3.hasNext()) {
                CampaignFleetAPI fleet = (CampaignFleetAPI)var3.next();
                this.seeIfFleetShouldRespond(fleet);
            }

        }
    }

    protected boolean isTemporarilyNotResponding(CampaignFleetAPI fleet) {
        if (fleet.getBattle() != null) {
            return true;
        } else if (fleet.getMemoryWithoutUpdate().getBoolean("$core_fleetBusy")) {
            return true;
        } else {
            FleetAssignmentDataAPI curr = fleet.getCurrentAssignment();
            if (curr != null && curr.getAssignment() == FleetAssignment.STANDING_DOWN) {
                return true;
            } else {
                MemoryAPI memory = fleet.getMemoryWithoutUpdate();
                return memory.getBoolean(KEY);
            }
        }
    }

    protected void seeIfFleetShouldRespond(CampaignFleetAPI fleet) {
        if (this.couldRespond(fleet)) {
            if (!this.isTemporarilyNotResponding(fleet)) {
                List<CampaignFleetAPI> fleets = this.params.target.getContainingLocation().getFleets();
                float potentialFP = 0.0F;
                float respondingFP = 0.0F;
                float closestDist = 3.4028235E38F;
                CampaignFleetAPI closestNonResponder = null;
                Iterator var8 = fleets.iterator();

                while(var8.hasNext()) {
                    CampaignFleetAPI other = (CampaignFleetAPI)var8.next();
                    if (this.couldRespond(other)) {
                        float fp = (float)other.getFleetPoints();
                        potentialFP += fp;
                        boolean responding = this.isResponding(other);
                        if (responding) {
                            respondingFP += fp;
                        }

                        if (!responding && !this.isTemporarilyNotResponding(other)) {
                            float distOther = Misc.getDistance(this.params.target, other);
                            if (distOther < closestDist) {
                                closestDist = distOther;
                                closestNonResponder = other;
                            }
                        }
                    }
                }

                float fraction = this.params.responseFraction / this.getResponseTotal();
                if (potentialFP > 0.0F && respondingFP / potentialFP < fraction && closestNonResponder == fleet) {
                    this.respond(fleet);
                }

            }
        }
    }

    protected void respond(CampaignFleetAPI fleet) {
        this.unrespond(fleet);
        Misc.setFlagWithReason(fleet.getMemoryWithoutUpdate(), KEY, this.params.responseReason, true, (1.5F + (float)Math.random()) * 0.2F);
        fleet.addAssignmentAtStart(FleetAssignment.PATROL_SYSTEM, this.params.target, 3.0F, this.params.actionText, (Script)null);
        FleetAssignmentDataAPI curr = fleet.getCurrentAssignment();
        if (curr != null) {
            curr.setCustom(RESPONSE_ASSIGNMENT);
        }

        float dist = Misc.getDistance(this.params.target, fleet);
        if (dist > 2000.0F) {
            fleet.addAssignmentAtStart(FleetAssignment.GO_TO_LOCATION, this.params.target, 3.0F, this.params.travelText, (Script)null);
            curr = fleet.getCurrentAssignment();
            if (curr != null) {
                curr.setCustom(RESPONSE_ASSIGNMENT);
            }
        }

    }

    protected void unrespond(CampaignFleetAPI fleet) {
        Misc.setFlagWithReason(fleet.getMemoryWithoutUpdate(), KEY, this.params.responseReason, false, 0.0F);
        Iterator var3 = fleet.getAI().getAssignmentsCopy().iterator();

        while(var3.hasNext()) {
            FleetAssignmentDataAPI curr = (FleetAssignmentDataAPI)var3.next();
            if (RESPONSE_ASSIGNMENT.equals(curr.getCustom())) {
                fleet.getAI().removeAssignment(curr);
            }
        }

    }

    protected boolean isResponding(CampaignFleetAPI fleet) {
        return Misc.flagHasReason(fleet.getMemoryWithoutUpdate(), KEY, this.params.responseReason);
    }

    protected boolean couldRespond(CampaignFleetAPI fleet) {
        if (fleet.getFaction() != this.params.faction) {
            return false;
        } else if (fleet.getAI() == null) {
            return false;
        } else if (fleet.isPlayerFleet()) {
            return false;
        } else if (fleet.isStationMode()) {
            return false;
        } else {
            if (fleet.getAI() instanceof ModularFleetAIAPI) {
                ModularFleetAIAPI ai = (ModularFleetAIAPI)fleet.getAI();
                if (ai.getAssignmentModule().areAssignmentsFrozen()) {
                    return false;
                }
            }

            if (fleet.getCurrentAssignment() != null && fleet.getCurrentAssignment().getAssignment() == FleetAssignment.GO_TO_LOCATION_AND_DESPAWN) {
                return false;
            } else {
                MemoryAPI memory = fleet.getMemoryWithoutUpdate();
                boolean patrol = memory.getBoolean("$isPatrol");
                boolean warFleet = memory.getBoolean("$isWarFleet");
                boolean pirate = memory.getBoolean("$isPirate");
                boolean noMilitary = memory.getBoolean("$core_fleetNoMilitaryResponse");
                return (patrol || warFleet || pirate) && !noMilitary;
            }
        }
    }

    protected String getResponseTotalKey() {
        return "$mrs_" + this.params.responseReason;
    }

    protected void addToResponseTotal() {
        MemoryAPI memory = this.params.faction.getMemoryWithoutUpdate();
        String key = this.getResponseTotalKey();
        float curr = memory.getFloat(key);
        memory.set(key, curr + this.params.responseFraction, 60.0F);
    }

    protected void removeFromResponseTotal() {
        MemoryAPI memory = this.params.faction.getMemoryWithoutUpdate();
        String key = this.getResponseTotalKey();
        float curr = memory.getFloat(key);
        if (curr > this.params.responseFraction) {
            memory.set(key, Math.max(0.0F, curr - this.params.responseFraction), 60.0F);
        } else {
            memory.unset(key);
        }

    }

    protected float getResponseTotal() {
        MemoryAPI memory = this.params.faction.getMemoryWithoutUpdate();
        String key = this.getResponseTotalKey();
        float curr = memory.getFloat(key);
        if (curr < this.params.responseFraction) {
            curr = this.params.responseFraction;
        }

        if (curr < 1.0F) {
            curr = 1.0F;
        }

        return curr;
    }



    public boolean isDone() {
        if (this.params != null && this.elapsed < this.params.responseDuration) {
            return false;
        } else {
            this.removeFromResponseTotal();
            this.params = null;
            return true;
        }
    }

    public boolean runWhilePaused() {
        return false;
    }

    public DawnActionResponseScript.DawnActionResponseParams getParams() {
        return this.params;
    }

    public void setElapsed(float elapsed) {
        this.elapsed = elapsed;
    }

    public static class DawnActionResponseParams {
        public CampaignFleetAIAPI.ActionType type;
        public String responseReason;
        public FactionAPI faction;
        public SectorEntityToken actor;
        public SectorEntityToken target;
        public float responseFraction;
        public float responseDuration;
        public String travelText;
        public String actionText;

        public DawnActionResponseParams(CampaignFleetAIAPI.ActionType type, String responseReason, FactionAPI faction, SectorEntityToken target, float responseFraction, float responseDuration) {
            this.type = type;
            this.responseReason = responseReason;
            this.faction = faction;
            this.target = target;
            this.responseFraction = responseFraction;
            this.responseDuration = responseDuration;
        }
    }


}
