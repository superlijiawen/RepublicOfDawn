package data.utils;

import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.FleetEventListener;
import com.fs.starfarer.api.impl.campaign.econ.impl.MilitaryBase;
import com.fs.starfarer.api.impl.campaign.fleets.*;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.procgen.themes.RouteFleetAssignmentAI;
import com.fs.starfarer.api.util.Misc;
import com.sun.istack.internal.NotNull;
import org.lwjgl.util.vector.Vector2f;

import java.util.Iterator;

public class FleetUtils {

    private static final String DEFAULT_FACTION_ID = "republic_of_dawn";

    //public FleetParamsV3(MarketAPI source, 市场
    //                     Vector2f locInHyper, 坐标
    //                     java.lang.String factionId, 势力ID
    //                     java.lang.Float qualityOverride, 质量
    //                     java.lang.String fleetType, 舰队类型
    //                     float combatPts, 战斗船pts
    //                     float freighterPts, 货轮pts
    //                     float tankerPts, 油船pts
    //                     float transportPts, 运输船pts
    //                     float linerPts, 游轮pts
    //                     float utilityPts,  实用性船pts
    //                     float qualityMod) 质量mod
    public static FleetParamsV3 addFleetParams(MarketAPI market, String factionId, Float qualityOverride, String fleetType,
                               float combat, float freighter, float tanker, float transport, float liner, float utility, float qualityMod){

        return new FleetParamsV3(market, null, factionId, qualityOverride, fleetType, combat, freighter, tanker, transport, liner, utility, qualityMod);
    }


    //战斗型舰队
    public static FleetParamsV3 addCombatTypeFleetParams(MarketAPI market, Float qualityOverride, String fleetType, float combat, float freighter, float tanker){
        return addFleetParams(market, DEFAULT_FACTION_ID, qualityOverride, fleetType, combat, freighter, tanker ,0, 0, 0, 0);
    }



    //舰队属性设置
    public static CampaignFleetAPI createFleet(@NotNull FleetParamsV3 params, FleetFactory.PatrolType type, MarketAPI market){
        CampaignFleetAPI fleet = FleetFactoryV3.createFleet(params);

        if (fleet != null && !fleet.isEmpty()){
            //舰队行为
            if (!fleet.getFaction().getCustomBoolean("patrolsHaveNoPatrolMemoryKey")) {
                fleet.getMemoryWithoutUpdate().set("$isPatrol", true);
                if (type == FleetFactory.PatrolType.FAST || type == FleetFactory.PatrolType.COMBAT) {
                    fleet.getMemoryWithoutUpdate().set("$isCustomsInspector", true);
                }
            } else if (fleet.getFaction().getCustomBoolean("pirateBehavior")) {
                fleet.getMemoryWithoutUpdate().set("$isPirate", true);
                if (market != null && market.isHidden()) {
                    fleet.getMemoryWithoutUpdate().set("$isRaider", true);
                }
            }

            //职位等级
            String postId = Ranks.POST_PATROL_COMMANDER;
            String rankId = Ranks.SPACE_LIEUTENANT;
            switch(type) {
                case FAST:
                    rankId = Ranks.SPACE_LIEUTENANT;//中尉
                    break;
                case COMBAT:
                    rankId = Ranks.SPACE_COMMANDER;//中校
                    break;
                case HEAVY:
                    rankId = Ranks.SPACE_ADMIRAL;//上将
            }

            fleet.getCommander().setPostId(postId);
            fleet.getCommander().setRankId(rankId);

            return fleet;
        }else{
            return null;
        }

    }


    /**
     *
     * @param fleet 舰队
     * @param market 市场
     * @param listener 舰队时间监听器
     * @param aiScript ai脚本
     * @param route route
     * @return 舰队类型
     */
    public static CampaignFleetAPI addActions(CampaignFleetAPI fleet, MarketAPI market, FleetEventListener listener, RouteFleetAssignmentAI aiScript, RouteManager.RouteData route){
        if (fleet != null && !fleet.isEmpty()) {
            fleet.addEventListener(listener);
            market.getContainingLocation().addEntity(fleet);
            fleet.setFacing((float)Math.random() * 360.0F);
            fleet.setLocation(market.getPrimaryEntity().getLocation().x, market.getPrimaryEntity().getLocation().x);
            fleet.addScript(new PatrolAssignmentAIV4(fleet, route));

            MilitaryBase.PatrolFleetData custom = (MilitaryBase.PatrolFleetData)route.getCustom();
            if (custom.spawnFP <= 0) {
                custom.spawnFP = fleet.getFleetPoints();
            }

            return fleet;
        } else {
            return null;
        }
    }


    /**
     *
     * @param fleet 舰队
     * @param maxDist 最大距离
     * @return
     */
    public static CampaignFleetAPI getNearbyFleetToAttack(CampaignFleetAPI fleet, float maxDist) {
        Iterator<CampaignFleetAPI> fleets = Misc.getNearbyFleets(fleet, maxDist).iterator();

        CampaignFleetAPI targetFleet = null;

        while(fleets.hasNext()){
            targetFleet = fleets.next();
            if (fleet.getLocation() != null && targetFleet.getLocation() != null){
                if (fleet.getFaction().isHostileTo(targetFleet.getFaction())){
                    if (targetFleet.getFleetSizeCount() > fleet.getFleetSizeCount()){
                        continue;
                    }
                    return targetFleet;
                }
            }
        }

        return null;
    }




}
