package data.scripts.campaign.fleet;



import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FleetAssignment;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.util.container.Pair;
import data.utils.api.RD_BaseAssignmentAI;
import data.utils.MarketUtils;
import org.lazywizard.lazylib.MathUtils;

import java.util.*;

/**
 * 黎明之盾
 */
public class RD_DawnShield extends RD_BaseAssignmentAI {


    protected RD_DawnShield(CampaignFleetAPI fleet, SectorEntityToken entity, FleetAssignment fleetAssignment, float maxDurationInDays) {
        super(fleet, entity, fleetAssignment, maxDurationInDays);
    }

    public RD_DawnShield(CampaignFleetAPI fleet, SectorEntityToken entity){
        super(fleet, entity, FleetAssignment.ORBIT_PASSIVE, 1f);
    }

    public void advance(float amount) {
        if (this.fleet.getCurrentAssignment() == null){
            this.fleet.addAssignment(this.fleetAssignment, this.entity, this.maxDurationDays);
        }else{
            this.pickNext();
        }
    }

    protected void pickNext() {
        this.giveInitialAssignments();
    }

    @Override
    protected void giveInitialAssignments() {
        SectorEntityToken entityToken;
        entityToken = this.chooseEntity();
        if (entityToken == null){
            entityToken = this.entity;
        }

        this.fleet.addAssignment(FleetAssignment.GO_TO_LOCATION, entityToken, 30f, entityToken.getName());

        float distance = MathUtils.getDistance(entityToken.getLocation(), this.fleet.getLocation());

        if (distance >= 4000 && this.fleet.getCurrentAssignment().getAssignment() == FleetAssignment.ORBIT_AGGRESSIVE){
            this.fleet.addAssignment(FleetAssignment.ORBIT_PASSIVE, entityToken, 30f, "正在守卫" + entityToken.getName());
        }
    }

    private SectorEntityToken chooseEntity() {
        List<SectorEntityToken> selfEntities = new ArrayList<>();

        List<SectorEntityToken> entities = this.entity.getStarSystem().getAllEntities();
        Iterator<SectorEntityToken> var3 = entities.iterator();
        while (var3.hasNext()){
            SectorEntityToken entity = var3.next();
            if (entity.getMarket() != null && entity.getFaction().getId().equals(FACTION_ID)){
                selfEntities.add(entity);
            }
        }

        //选出
        List<Pair<SectorEntityToken, Float>> pairList = MarketUtils.buildEntityValuePair(selfEntities);
        return MarketUtils.pickEntity(pairList);
    }


}
