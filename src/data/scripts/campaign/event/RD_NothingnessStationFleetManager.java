package data.scripts.campaign.event;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.impl.campaign.fleets.*;
import com.fs.starfarer.api.impl.campaign.ids.FleetTypes;
import com.fs.starfarer.api.util.IntervalUtil;
import data.scripts.campaign.fleet.RD_Wander;
import org.lazywizard.lazylib.MathUtils;

import java.util.*;

public class RD_NothingnessStationFleetManager extends BaseCampaignEventListener implements EveryFrameScript {

    private SectorEntityToken entity;
    private Map<Integer, CampaignFleetAPI> wanderFleets = new LinkedHashMap<>();
    private IntervalUtil tracker = new IntervalUtil(3.0f, 5.0f);
    private Integer counter = 0;

    public RD_NothingnessStationFleetManager(SectorEntityToken entity) {
        super(true);
        this.entity = entity;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean runWhilePaused() {
        return false;
    }

    @Override
    public void advance(float amount) {
        float days = Global.getSector().getClock().convertToDays(amount);
        this.tracker.advance(days);
        if (this.tracker.intervalElapsed()) {
            if (wanderFleets.size() < 3){
                this.spawnFleet();
            }
        }
    }

    public void reportFleetDespawned(CampaignFleetAPI fleet, FleetDespawnReason reason, Object param) {
        super.reportFleetDespawned(fleet, reason, param);

        Set<Integer> keySet = wanderFleets.keySet();
        Set<Integer> neededRemove = new LinkedHashSet<>();

        for (Integer key : keySet) {
            CampaignFleetAPI f = wanderFleets.get(key);
            if (f == null || !f.isAlive() || f.getFleetPoints() <= 30.0f){
                neededRemove.add(key);
            }
        }

        if (neededRemove.size() != 0){
            for (Integer integer : neededRemove) {
                this.wanderFleets.remove(integer);
            }
        }

        neededRemove.clear();
    }

    private void spawnFleet() {
        String factionId = this.pickFaction();
//        float combatPts = 100.0f;
        float combatPts = this.getCombatPts();

        FleetParamsV3 params = new FleetParamsV3(null, factionId, 1.0f, FleetTypes.MERC_ARMADA, combatPts, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        CampaignFleetAPI fleet = FleetFactoryV3.createFleet(params);

        if (fleet != null){
            String displayName = Global.getSector().getFaction(factionId).getDisplayName();
            fleet.setNoFactionInName(true);
            fleet.setName(displayName + "流浪军团");
            fleet.setFaction(factionId);
            fleet.setTransponderOn(false);
//            fleet.addAssignment(FleetAssignment.ORBIT_PASSIVE, this.entity, 100000000.0f);

            StarSystemAPI system = Global.getSector().getStarSystem(this.entity.getStarSystem().getId());
            system.spawnFleet(this.entity, this.entity.getLocation().x, this.entity.getLocation().y, fleet);

            fleet.setFaction("pirates");

            this.wanderFleets.put(counter, fleet);
            fleet.forceSync();
            fleet.addScript(new RD_Wander(fleet, this.entity, FleetAssignment.ORBIT_PASSIVE, 1000000f));
            this.counter ++;
        }
    }

    private float getCombatPts() {
        SectorAPI sector = Global.getSector();
        int points = sector.getPlayerFleet().getFleetPoints();
        if (points <= 100){
            return 100.0f;
        }else{
            float random = MathUtils.getRandomNumberInRange(1.0f, 1.3f);
            return random * points;
        }
    }

    private String pickFaction() {
        double random = Math.random();
        if (random <= 0.70f){
            return "pirates";
        }else if (random <= 0.80f){
            return "luddic_path";
        } else{
            List<FactionAPI> factions = Global.getSector().getAllFactions();
            List<FactionAPI> targets = new LinkedList<>();
            for (FactionAPI faction : factions) {
                if (faction.isShowInIntelTab() && !faction.isPlayerFaction()){
                    targets.add(faction);
                }
            }

            int size = targets.size();
            int index = MathUtils.getRandomNumberInRange(0, size - 1);
            FactionAPI factionAPI = targets.get(index);
            return factionAPI.getId();
        }
    }


}
