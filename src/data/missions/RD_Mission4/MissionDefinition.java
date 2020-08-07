package data.missions.RD_Mission4;

import com.fs.starfarer.api.fleet.FleetGoal;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.ids.StarTypes;
import com.fs.starfarer.api.mission.FleetSide;
import com.fs.starfarer.api.mission.MissionDefinitionAPI;
import com.fs.starfarer.api.mission.MissionDefinitionPlugin;

/**
 * 云计划(测试战役)
 */
public class MissionDefinition implements MissionDefinitionPlugin {


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
        api.setFleetTagline(FleetSide.PLAYER, "'必须让霸主知道我们的厉害?'");
        api.setFleetTagline(FleetSide.ENEMY, "菜鸟们，大爷来了!");

        // These show up as items in the bulleted list under
        // "Tactical Objectives" on the mission detail screen
        api.addBriefingItem("干掉霸主派来的舰队");

        // Set up the player's fleet.  Variant names come from the
        // files in data/variants and data/variants/fighters
        api.addToFleet(FleetSide.PLAYER, "rd_cumulonimbus_elite", FleetMemberType.SHIP, "Mike", true);
        api.addToFleet(FleetSide.PLAYER, "rd_stratocumulus_elite", FleetMemberType.SHIP, "Teammate1", false);
        api.addToFleet(FleetSide.PLAYER, "rd_cirrus_attack", FleetMemberType.SHIP, "Teammate2", false);
        api.addToFleet(FleetSide.PLAYER, "rd_morning_glow_destory", FleetMemberType.SHIP, "Teammate3", false);

        // Set up the enemy fleet.
        //攻势x1 统治者x1 秃鹰x1 压迫着x1 征伐x2
        api.addToFleet(FleetSide.ENEMY, "onslaught_Standard", FleetMemberType.SHIP, "Jack", true);
        api.addToFleet(FleetSide.ENEMY, "dominator_Assault", FleetMemberType.SHIP, "enemy2", false);
        api.addToFleet(FleetSide.ENEMY, "enforcer_Assault", FleetMemberType.SHIP, "enemy3", false);
        api.addToFleet(FleetSide.ENEMY, "condor_Support", FleetMemberType.SHIP, "enemy4", false);
        api.addToFleet(FleetSide.ENEMY, "lasher_Standard", FleetMemberType.SHIP, "enemy5", false);
        api.addToFleet(FleetSide.ENEMY, "lasher_Standard", FleetMemberType.SHIP, "enemy6", false);

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

        api.addPlanet(0, 0, 50f, StarTypes.BLUE_GIANT, 250f, true);
    }
}
