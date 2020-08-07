package data.scripts.world;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.impl.campaign.procgen.StarAge;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.ShipRecoverySpecial;
import com.fs.starfarer.api.util.Misc;
import data.scripts.campaign.event.RD_NothingnessStationFleetManager;
import data.utils.SystemUtils;
import data.utils.api.RD_GlobalApi;
import org.lazywizard.lazylib.MathUtils;

import java.util.*;

public class RD_Ton615 implements RD_GlobalApi {

    private static final String SYSTEM_NAME = "Ton615";
    private static final String MAIN_STAR_NAME = "Ton615";
    private static final String MAIN_STAR_TYPE = "black_hole";
    private static final String TON615_BACKGROUND = "graphics/backgrounds/RD_ton615.jpg";

    private static List<String> destroy_list = new LinkedList<>();
    private static List<String> cruiser_list = new LinkedList<>();
    private static List<String> captain_list = new LinkedList<>();

    static {
        Collections.addAll(destroy_list,
                "hammerhead_d_CS", "sunder_d_Assault", "enforcer_d_pirates_Strike", "shrike_Attack"
        );

        Collections.addAll(cruiser_list,
                "falcon_CS", "eagle_Assault", "dominator_Support", "heron_Strike"
        );

        Collections.addAll(captain_list,
                "conquest_Elite", "onslaught_Standard", "odyssey_Balanced"
        );
    }

    @Override
    public void generate(SectorAPI sector) {
        //create a star system
        StarSystemAPI system = sector.createStarSystem(SYSTEM_NAME);
//        //set its location
//        system.getLocation().set(12000f,8000f);
        float randomX = MathUtils.getRandomNumberInRange(-1600f, 1600f);
        float randomY = MathUtils.getRandomNumberInRange(-2000f, 4000f);
        system.getLocation().set(1200f + randomX, 28000f + randomY);

        //set background image
        system.setBackgroundTextureFilename(TON615_BACKGROUND);

        //the star
        PlanetAPI star = system.initStar(MAIN_STAR_NAME, MAIN_STAR_TYPE, 200f, 300f);

        //add dyson ball 增加戴森球 虚无空间站
        SectorEntityToken nothingness_station = system.addCustomEntity("nothingness_station", (String)null, "dyson_ball", "neutral");
        nothingness_station.setCircularOrbit(star, 120.0f, 2700.0f, 30.0f);
        nothingness_station.setCustomDescriptionId("dyson_ball");

        //add special nebula
        SectorEntityToken quasar_nebula = Misc.addNebulaFromPNG("data/campaign/terrain/eos_nebula.png",
                star.getLocation().x, star.getLocation().y, system,
                "terrain", "RD_quasarnebula", 7, 3, "RD_quasarnebula", StarAge.OLD);

        quasar_nebula.addTag("radar_nebula");
        quasar_nebula.setCustomDescriptionId("RD_quasarnebula");

        //科技藏匿点
        SectorEntityToken dust = system.addCustomEntity("ton615_station", "Ton615 科研站", "technology_cache", "neutral");
        dust.setCircularOrbitPointingDown(nothingness_station, 15, 400, 160);
        dust.setDiscoverable((Boolean)null);
        dust.setDiscoveryXP((Float)null);
        dust.setSensorProfile((Float)null);
        dust.addTag("expires");

        // generates hyperspace destinations for in-system jump points
        system.autogenerateHyperspaceJumpPoints(true, true);

        //警告标识
        LocationAPI hyperspace = Global.getSector().getHyperspace();
        CustomCampaignEntityAPI warning = (CustomCampaignEntityAPI) hyperspace.addCustomEntity("ton615_warning", "警告航标", "warning_beacon", "neutral");
        warning.setLocation(1200f + randomX - 200f, 28000f + randomY - 200f);

//        String factionId = "pirates";
//        float combatPts = 10.0f;
//        FleetParamsV3 params = new FleetParamsV3(null, factionId, 1.0f, FleetTypes.MERC_ARMADA, combatPts, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
//        CampaignFleetAPI fleet = FleetFactoryV3.createFleet(params);
//
//        String displayName = Global.getSector().getFaction(factionId).getDisplayName();
//        fleet.setName(displayName + "流亡舰队");
//        fleet.setTransponderOn(false);
//        fleet.addAssignment(FleetAssignment.ORBIT_PASSIVE, nothingness_station, 60.0f);
//
//        system.spawnFleet(nothingness_station, nothingness_station.getLocation().x, nothingness_station.getLocation().y, fleet);

        nothingness_station.addScript(new RD_NothingnessStationFleetManager(nothingness_station));

        Random random = new Random();
        String destroy = destroy_list.get(random.nextInt(destroy_list.size()));
        String cruiser = cruiser_list.get(random.nextInt(cruiser_list.size()));
        String captain = captain_list.get(random.nextInt(captain_list.size()));

        //add derelicts 增加遗弃舰
        SystemUtils.addDerelict(system, star, destroy, ShipRecoverySpecial.ShipCondition.BATTERED, MathUtils.getRandomNumberInRange(200.0f, 2000.0f), true);
        SystemUtils.addDerelict(system, star, cruiser, ShipRecoverySpecial.ShipCondition.BATTERED, MathUtils.getRandomNumberInRange(2000.0f, 6000.0f), true);
        SystemUtils.addDerelict(system, star, captain, ShipRecoverySpecial.ShipCondition.BATTERED, MathUtils.getRandomNumberInRange(6000.0f, 12000.0f), true);

        //Finally cleans up hyperspace
        SystemUtils.cleanup(system);
    }

    @Override
    public void addPlanets(StarSystemAPI system, PlanetAPI star, String factionId) {

    }

    @Override
    public void addPlanets(StarSystemAPI system, PlanetAPI star, PlanetAPI c_star, String factionId) {

    }
}
