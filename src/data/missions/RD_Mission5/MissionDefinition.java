package data.missions.RD_Mission5;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.fleet.FleetGoal;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.ids.StarTypes;
import com.fs.starfarer.api.mission.FleetSide;
import com.fs.starfarer.api.mission.MissionDefinitionAPI;
import com.fs.starfarer.api.mission.MissionDefinitionPlugin;
import com.fs.starfarer.campaign.CampaignEngine;

import java.util.*;

/**
 * 随机战斗
 */
public class MissionDefinition implements MissionDefinitionPlugin {

    private static List<String> RD_SHIPS = new LinkedList<>();

    static{
        Collections.addAll(RD_SHIPS,
                "rd_hammerhead_striker",
                "rd_brawler_assault",
                "rd_monitor_strike" ,
                "rd_cirrus_strike" ,
                "rd_cumulonimbus_elite",
                "rd_morning_glow_destory",
                "rd_stratocumulus_elite",
                "rd_conquest_attack",
                "rd_onslaught_elite",
                "rd_falco_rusticolus_sniper",
                "rd_heron_support",
                "rd_falcon_attack",
                "rd_sunder_standard",
                "rd_dawn_standard");
    }


    @Override
    public void defineMission(MissionDefinitionAPI api) {
        // Set up the fleets so we can add ships and fighter wings to them.
        // In this scenario, the fleets are attacking each other, but
        // in other scenarios, a fleet may be defending or trying to escape
        api.initFleet(FleetSide.PLAYER, "RD", FleetGoal.ATTACK, false);
        api.initFleet(FleetSide.ENEMY, "ISS", FleetGoal.ATTACK, true);

//		api.getDefaultCommander(FleetSide.PLAYER).getStats().setSkillLevel(Skills.COORDINATED_MANEUVERS, 3);
//		api.getDefaultCommander(FleetSide.PLAYER).getStats().setSkillLevel(Skills.ELECTRONIC_WARFARE, 3);

        // Set a small blurb for each fleet that shows up on the mission detail and
        // mission results screens to identify each side.
        api.setFleetTagline(FleetSide.PLAYER, "'敌人未知，小心点?'");
        api.setFleetTagline(FleetSide.ENEMY, "菜鸟们，大爷来了!");

        // These show up as items in the bulleted list under
        // "Tactical Objectives" on the mission detail screen
        api.addBriefingItem("干掉霸主派来的舰队");

        // Set up the player's fleet.  Variant names come from the
        // files in data/variants and data/variants/fighters

        List<String> variantIds = Global.getSector().getAllEmptyVariantIds();

        Random random = new Random();
        int size = RD_SHIPS.size();
        int totalSize = variantIds.size();

        api.addToFleet(FleetSide.PLAYER, RD_SHIPS.get(random.nextInt(size)), FleetMemberType.SHIP, "Mike", true);

        // Set up the enemy fleet.
        api.addToFleet(FleetSide.ENEMY, variantIds.get(random.nextInt(totalSize)), FleetMemberType.SHIP, "Jack", true);

        api.defeatOnShipLoss("Mike");

        // Set up the map.
        float width = 12000f;
        float height = 12000f;
        api.initMap((float)-width/2f, (float)width/2f, (float)-height/2f, (float)height/2f);

        float minX = -width/2;
        float minY = -height/2;

        // Add an asteroid field
        api.addAsteroidField(minX, minY + height / 2, 0, 8000f,
                20f, 70f, 100);

        api.addPlanet(0, 0, 50f, StarTypes.BLACK_HOLE, 250f, true);
    }
}
