package data.missions.RD_Mission3;

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
        api.addToFleet(FleetSide.PLAYER, "rd_conquest_attack", FleetMemberType.SHIP, "Mike", true);
        api.addToFleet(FleetSide.PLAYER, "rd_falcon_attack", FleetMemberType.SHIP, "John", false);
        api.addToFleet(FleetSide.PLAYER, "rd_hammerhead_Balanced", FleetMemberType.SHIP, "Teammate1", false);
        api.addToFleet(FleetSide.PLAYER, "rd_hammerhead_striker", FleetMemberType.SHIP, "Teammate2", false);
        api.addToFleet(FleetSide.PLAYER, "rd_falco_rusticolus_sniper", FleetMemberType.SHIP, "Teammate3", false);
        api.addToFleet(FleetSide.PLAYER, "rd_heron_support", FleetMemberType.SHIP, "Teammate4", false);
        api.addToFleet(FleetSide.PLAYER, "rd_heron_attack", FleetMemberType.SHIP, "Teammate5", false);
        api.addToFleet(FleetSide.PLAYER, "rd_brawler_attack", FleetMemberType.SHIP, "Teammate6",  false);
        api.addToFleet(FleetSide.PLAYER, "rd_cirrus_attack", FleetMemberType.SHIP, "Teammate7", false);
        api.addToFleet(FleetSide.PLAYER, "rd_stratocumulus_attack", FleetMemberType.SHIP, "Teammate8", false);
        api.addToFleet(FleetSide.PLAYER, "rd_brawler_attack", FleetMemberType.SHIP, "Teammate9", false);


        // Set up the enemy fleet.
        //大量
        api.addToFleet(FleetSide.ENEMY, "onslaught_Standard", FleetMemberType.SHIP, "Jack", true);
        api.addToFleet(FleetSide.ENEMY, "falcon_Attack", FleetMemberType.SHIP, "Sam", false);
        api.addToFleet(FleetSide.ENEMY, "eagle_Assault", FleetMemberType.SHIP, "enemy2", false);
        api.addToFleet(FleetSide.ENEMY, "dominator_Support", FleetMemberType.SHIP, "enemy3", false);
        api.addToFleet(FleetSide.ENEMY, "dominator_Support", FleetMemberType.SHIP, "enemy4", false);
        api.addToFleet(FleetSide.ENEMY, "condor_Attack", FleetMemberType.SHIP, "enemy5", false);
        api.addToFleet(FleetSide.ENEMY, "hammerhead_Balanced", FleetMemberType.SHIP, "enemy6", false);
        api.addToFleet(FleetSide.ENEMY, "hammerhead_Balanced", FleetMemberType.SHIP, "enemy7", false);
        api.addToFleet(FleetSide.ENEMY, "monitor_Escort", FleetMemberType.SHIP, "enemy8", false);
        api.addToFleet(FleetSide.ENEMY, "monitor_Escort", FleetMemberType.SHIP, "enemy9", false);
        api.addToFleet(FleetSide.ENEMY, "kite_Starting", FleetMemberType.SHIP, "enemy10", false);
        api.addToFleet(FleetSide.ENEMY, "kite_Starting", FleetMemberType.SHIP, "enemy11", false);
        api.addToFleet(FleetSide.ENEMY, "kite_Starting", FleetMemberType.SHIP, "enemy12", false);
        api.addToFleet(FleetSide.ENEMY, "shrike_Attack", FleetMemberType.SHIP, "enemy13", false);
        api.addToFleet(FleetSide.ENEMY, "shrike_Attack", FleetMemberType.SHIP, "enemy14", false);
        api.addToFleet(FleetSide.ENEMY, "shrike_Attack", FleetMemberType.SHIP, "enemy15", false);
        api.addToFleet(FleetSide.ENEMY, "falcon_d_CS", FleetMemberType.SHIP, "enemy16", false);
        api.addToFleet(FleetSide.ENEMY, "eagle_Assault", FleetMemberType.SHIP, "enemy17", false);
        api.addToFleet(FleetSide.ENEMY, "eagle_Assault", FleetMemberType.SHIP, "enemy18", false);
        api.addToFleet(FleetSide.ENEMY, "eagle_Assault", FleetMemberType.SHIP, "enemy19", false);

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
