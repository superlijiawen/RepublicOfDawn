package data.scripts.campaign.econ.industries;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BattleAPI;
import com.fs.starfarer.api.campaign.CampaignEventListener;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.listeners.FleetEventListener;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.MilitaryBase;
import com.fs.starfarer.api.impl.campaign.fleets.*;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import data.scripts.campaign.econ.ids.RD_Commodities;
import data.utils.api.RD_BaseIndustry;
import data.scripts.campaign.fleet.RD_DawnShield;
import data.utils.FleetUtils;

import java.util.Random;

//TODO 待完善
/**
 * 所罗门神像
 * 需求： 这个建筑需要消耗比较多的奢侈品 少量的食品 一些陆战队员 以及必备的先进补给
 * 功能： 稳定度过低时可以提供战时动员 若被占领则这个殖民地会自动加上一个“共和国革命”的标签 触发“共和国收复首都”事件
 * 特殊： 此建筑不能建造
 */
@SuppressWarnings("unchecked")
public class RD_SolomonIdol extends RD_BaseIndustry implements RouteManager.RouteFleetSpawner, FleetEventListener {

    protected IntervalUtil tracker = new IntervalUtil(Global.getSettings().getFloat("averagePatrolSpawnInterval") * 0.7F, Global.getSettings().getFloat("averagePatrolSpawnInterval") * 1.3F);
    protected float returningPatrolValue = 0.0F;
    private static final float BONUS = 0.2f;

    public RD_SolomonIdol() {

    }

    @Override
    public void apply() {
        super.apply();

        if (this.isRDOccupied()) {
            this.demand(Commodities.LUXURY_GOODS, size - 2, DESC);
            this.demand(Commodities.FOOD, size + 1, DESC);
            this.demand(RD_Commodities.ADVANCED_SUPPLIES, size + 1, DESC);
            this.demand(Commodities.CREW, size - 1, DESC);
        }

        //舰队质量提升
//        this.market.getStats().getDynamic().getMod("fleet_quality_mod").modifyMult(this.getModId(), 1.0F, this.getNameForModifier());
        this.market.getStats().getDynamic().getMod("ground_defenses_mod").modifyMult(this.getModId(), 1.0F + BONUS, this.getNameForModifier());


        MemoryAPI memory = this.market.getMemoryWithoutUpdate();

        Misc.setFlagWithReason(memory, "$patrol", this.getModId(), true, -1.0F);
        Misc.setFlagWithReason(memory, "$military", this.getModId(), true, -1.0F);

        if (!this.isFunctional()) {
            this.supply.clear();
            this.unapply();
        }

    }

    @Override
    public void unapply() {
        super.unapply();
        MemoryAPI memory = this.market.getMemoryWithoutUpdate();
        Misc.setFlagWithReason(memory, "$patrol", this.getModId(), false, -1.0F);
        Misc.setFlagWithReason(memory, "$military", this.getModId(), false, -1.0F);
    }

    @Override
    public void advance(float amount) {
        super.advance(amount);
        if (!Global.getSector().getEconomy().isSimMode()) {
            if (this.isFunctional()) {
                float days = Global.getSector().getClock().convertToDays(amount);

                this.init();
                float spawnRate = 0f;
                if (this.stabilityValue == 0){
                    return;
                }else if (this.stabilityValue <= 4){
                    spawnRate = 1.0f;
                }else if (this.stabilityValue <= 8){
                    spawnRate = 0.8f;
                }else {
                    spawnRate = 0.7f;
                }


                float extraTime = 0.0F;
                if (this.returningPatrolValue > 0.0F) {
                    float interval = this.tracker.getIntervalDuration();
                    extraTime = interval * days;
                    this.returningPatrolValue -= days;
                    if (this.returningPatrolValue < 0.0F) {
                        this.returningPatrolValue = 0.0F;
                    }
                }

                this.tracker.advance(days * spawnRate + extraTime);
                if (this.tracker.intervalElapsed()) {
                    String sid = this.getRouteSourceId();

                    WeightedRandomPicker<FleetFactory.PatrolType> picker = new WeightedRandomPicker();

                    picker.add(FleetFactory.PatrolType.HEAVY);

                    if (picker.isEmpty()) {
                        return;
                    }

                    FleetFactory.PatrolType type = picker.pick();
                    MilitaryBase.PatrolFleetData custom = new MilitaryBase.PatrolFleetData(type);
                    RouteManager.OptionalFleetData extra = new RouteManager.OptionalFleetData(this.market);
                    extra.fleetType = type.getFleetType();
                    RouteManager.RouteData route = RouteManager.getInstance().addRoute(sid, this.market, Misc.genRandomSeed(), extra, this, custom);

//                    float patrolDays = 120.0F;
                    float patrolDays = 60.0f;


//                    route.addSegment(new RouteManager.RouteSegment(patrolDays, this.market.getPrimaryEntity()));


                    route.addSegment(new RouteManager.RouteSegment(patrolDays, this.market.getPrimaryEntity()));
                }

            }
        }
    }


    @Override
    public boolean isHidden() {
        return !this.market.getFactionId().equals(FACTION_ID);
    }

    @Override
    public boolean isAvailableToBuild() {
        return false;
    }

    public boolean showWhenUnavailable() {
        return false;
    }

    public boolean isFunctional() {
        return super.isFunctional() && this.isRDOccupied();
    }


    @Override
    public void reportFleetDespawnedToListener(CampaignFleetAPI fleet, CampaignEventListener.FleetDespawnReason reason, Object o) {
        if (this.isFunctional()) {
            if (reason == CampaignEventListener.FleetDespawnReason.REACHED_DESTINATION) {
                RouteManager.RouteData route = RouteManager.getInstance().getRoute(this.getRouteSourceId(), fleet);
                if (route.getCustom() instanceof MilitaryBase.PatrolFleetData) {
                    MilitaryBase.PatrolFleetData custom = (MilitaryBase.PatrolFleetData)route.getCustom();
                    if (custom.spawnFP > 0) {
                        float fraction = (float)(fleet.getFleetPoints() / custom.spawnFP);
                        this.returningPatrolValue += fraction;
                    }
                }
            }

        }
    }

    @Override
    public void reportBattleOccurred(CampaignFleetAPI campaignFleetAPI, CampaignFleetAPI campaignFleetAPI1, BattleAPI battleAPI) {

    }

    @Override
    public CampaignFleetAPI spawnFleet(RouteManager.RouteData route) {
        //1 初始化数据
        MilitaryBase.PatrolFleetData custom  = (MilitaryBase.PatrolFleetData) route.getCustom();
        FleetFactory.PatrolType type = custom.type;
        Random random = route.getRandom();
        String fleetType = type.getFleetType();

        //2 舰船数量设置
        float combat = 0f;
        float freighter = 0f;
        float tanker = 0;

        combat = 20f;
        freighter = 3f;
        tanker = 3f;


        //3 设置舰队参数
        FleetParamsV3 params = FleetUtils.addCombatTypeFleetParams(this.market, route.getQualityOverride(), fleetType, combat, freighter, tanker);

        params.timestamp = route.getTimestamp();
        params.random = random;

        //4 设置舰队属性
        CampaignFleetAPI fleet = FleetUtils.createFleet(params, type, this.market);

        if (fleet != null){
            fleet.setName("黎明之盾");
            //add script ai
            fleet.addScript(new RD_DawnShield(fleet, this.market.getPlanetEntity()));
        }

        //5 设置舰队行为
        CampaignFleetAPI campaignFleetAPI = FleetUtils.addActions(fleet, market, this, new PatrolAssignmentAIV4(fleet, route), route);
        if (campaignFleetAPI != null){
            campaignFleetAPI.addScript(new RD_DawnShield(fleet, this.market.getPlanetEntity()));
        }
        return campaignFleetAPI;
    }

    @Override
    public boolean shouldCancelRouteAfterDelayCheck(RouteManager.RouteData routeData) {
        return false;
    }

    @Override
    public boolean shouldRepeat(RouteManager.RouteData routeData) {
        return false;
    }

    @Override
    public void reportAboutToBeDespawnedByRouteManager(RouteManager.RouteData routeData) {

    }


    public String getRouteSourceId() {
        return this.getMarket().getId() + "_" + FACTION_ID;
    }

}
