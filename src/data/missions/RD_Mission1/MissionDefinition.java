package data.missions.RD_Mission1;

import com.fs.starfarer.api.fleet.FleetGoal;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.ids.StarTypes;
import com.fs.starfarer.api.mission.FleetSide;
import com.fs.starfarer.api.mission.MissionDefinitionAPI;
import com.fs.starfarer.api.mission.MissionDefinitionPlugin;

/**
 * 黄昏之战(测试战役)
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
        api.setFleetTagline(FleetSide.PLAYER, "'搞事的家伙来了?'");
        api.setFleetTagline(FleetSide.ENEMY, "这次别想跑了，黎明共和国的坏家伙 'Mike'. ");

        // These show up as items in the bulleted list under
        // "Tactical Objectives" on the mission detail screen
        api.addBriefingItem("干掉霸主派来的暗杀小队");

        // Set up the player's fleet.  Variant names come from the
        // files in data/variants and data/variants/fighters
        api.addToFleet(FleetSide.PLAYER, "rd_falcon_attack", FleetMemberType.SHIP, "Mike", true);

        // Set up the enemy fleet.
        //风筝  伯劳鸟
//        api.addToFleet(FleetSide.ENEMY, "kite_Starting", FleetMemberType.SHIP, "Jack", true);
        api.addToFleet(FleetSide.ENEMY, "kite_Starting", FleetMemberType.SHIP, "Tom", true);
        api.addToFleet(FleetSide.ENEMY, "kite_Starting", FleetMemberType.SHIP, "Bob", false);
        api.addToFleet(FleetSide.ENEMY, "shrike_Attack", FleetMemberType.SHIP, "John", false);

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

        api.addPlanet(0, 0, 50f, StarTypes.RED_GIANT, 250f, true);
    }
}
