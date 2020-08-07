package data.scripts.world;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.HeavyIndustry;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.terrain.BaseTiledTerrain;
import data.scripts.campaign.econ.ids.RD_Industries;
import data.scripts.campaign.econ.ids.RD_Submarkets;
import data.scripts.campaign.econ.ids.RD_MarketConditions;
import data.utils.*;
import data.utils.api.RD_GlobalApi;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

import static data.utils.MarketUtils.addMarketplace;

public class RD_Solomon implements RD_GlobalApi {

    private static final String SYSTEM_NAME = "solomon";
    private static final String MAIN_STAR_NAME = "Solomon-alpah";
    private static final String COMPAION_STAR_NAME = "Solomon-beta";
    private static final String MAIN_STAR_TYPE = "rd_star_purple";

    private static final String FACTION_ID = "republic_of_dawn";
    private static final String SOLOMON_BACKGROUND = "graphics/backgrounds/RD_solomon.jpg";

    @Override
    public void generate(SectorAPI sector) {
        //create a star system
        StarSystemAPI system = sector.createStarSystem(SYSTEM_NAME);
//        //set its location
//        system.getLocation().set(12000f,8000f);
        system.getLocation().set(2000f, 16000f);

        //set background image
        system.setBackgroundTextureFilename(SOLOMON_BACKGROUND);

        //the star
        PlanetAPI star = system.initStar(MAIN_STAR_NAME, MAIN_STAR_TYPE, 850f, 300f);

        //set star's location
        star.getLocation().set(0f, 0f);

        //background light color
        system.setLightColor(RD_Color.LIGHT2);

        //make asteroid belt surround it
        system.addAsteroidBelt(star, 100, 1800f, 100f, 180, 180, Terrain.ASTEROID_BELT, "");
        system.addAsteroidBelt(star, 200, 2800f, 100f, 180, 180, Terrain.ASTEROID_BELT, "");
        system.addAsteroidBelt(star, 400, 3800f, 100f, 180, 180, Terrain.ASTEROID_BELT, "");

        system.addRingBand(star, "misc", "rings_ice0", 256.0F, 0, RD_Color.RING_BAND2, 256.0F, 7200.0F, 90.0F);
        system.addRingBand(star, "misc", "rings_asteroids0", 256.0F, 0, RD_Color.RING_BAND1, 256.0F, 7600.0F, 90.0F);
        system.addRingBand(star, "misc", "rings_dust0", 256.0F, 0, RD_Color.RING_BAND1, 256.0F, 8000.0F, 90.0F);
        system.addRingBand(star, "misc", "rings_asteroids0", 256.0F, 0, RD_Color.RING_BAND1, 256.0F, 8400.0F, 90.0F);
        system.addRingBand(star, "misc", "rings_dust0", 256.0F, 0, RD_Color.RING_BAND1, 256.0F, 8800.0F, 90.0F);

        system.addAsteroidBelt(star, 400, 12000f, 200f, 180, 360, Terrain.ASTEROID_BELT, "");
        system.addAsteroidBelt(star, 400, 13500f, 200f, 180, 360, Terrain.ASTEROID_BELT, "");
        system.addAsteroidBelt(star, 400, 15000f, 200f, 180, 360, Terrain.ASTEROID_BELT, "");

        //create compaion star
        PlanetAPI c_star = system.addPlanet(COMPAION_STAR_NAME, star,
                COMPAION_STAR_NAME, "star_white", 36, 160, 18000, 1200f);

        //create entityToken 通讯中继站 导航浮标 传感器阵列
        SectorEntityToken so_relay = system.addCustomEntity("SO_Relay", "黎明共和国 通讯中继站", "comm_relay", FACTION_ID);
        SectorEntityToken so_buoy = system.addCustomEntity("SO_Buoy", "黎明共和国 导航浮标", "nav_buoy", FACTION_ID);
        SectorEntityToken so_array = system.addCustomEntity("SO_Array", "黎明共和国 传感器阵列", "sensor_array", FACTION_ID);

        so_relay.setCircularOrbitPointingDown(star, 120.0F, 4050.0F, 300.0F);
        so_buoy.setCircularOrbitPointingDown(star, 120.0F, 7550.0F, 300.0F);
        so_array.setCircularOrbitPointingDown(c_star, 120.0F, 1750.0F, 300.0F);

        //create gate 奔溃的星门
        SectorEntityToken so_gate = system.addCustomEntity("SO_Gate", "所罗门之门", "inactive_gate", (String)null);
        so_gate.setCircularOrbitPointingDown(star, 120.0F, 9750.0F, 300.0F);

        //add stellar shade 增加恒星罩
        SectorEntityToken shade = system.addCustomEntity("stellar_shade",
                "Solomon 恒星罩", "stellar_shade", FACTION_ID);

        shade.setCircularOrbitPointingDown(system.getEntityById(MAIN_STAR_NAME), 15, 1200, 30);

        //add planets for star
        this.addPlanets(system, star, c_star, FACTION_ID);

        //add  mini-nebulas 迷你星云
        SectorEntityToken solomon_nebula = system.addTerrain(Terrain.NEBULA, new BaseTiledTerrain.TileParams(
                "  x   " +
                        "  xx x" +
                        "xxxxx " +
                        " xxx  " +
                        " x  x " +
                        "   x  ",
                6, 6, // size of the nebula grid, should match above string
                "terrain", "RD_quasarnebula", 4, 4, null));
        solomon_nebula.setId("solomon_nebula");

        solomon_nebula.setCircularOrbit(star, 200, 10000, 150);

        // generates hyperspace destinations for in-system jump points
        system.autogenerateHyperspaceJumpPoints(true, true);

        //add nebula 增加星云
//        StarSystemGenerator.addSystemwideNebula(system, StarAge.OLD);

//        String tile = "xx xx x x  xxx xx x x xxx  xxx   xx";
//        StringBuilder builder = new StringBuilder();
//
//        for (int i = 0; i < 128; i++) {
//            builder.append(tile);
//        }

//        int weight = 128;
//        int height = 128;
//        StringBuilder builder = new StringBuilder();
//
//        String tile = "xx x x x     xx xx x  xxx  xx   xx";
//
//        for (int i = 0; i < 128; i++) {
//            builder.append(tile);
//        }

//        //增加星云
//        SectorEntityToken nebula = system.addTerrain("nebula", new BaseTiledTerrain.TileParams(builder.toString(),
//                weight, height, "terrain", "RD_quasarnebula", 4, 4, "Quasar nebula"));
//        nebula.getLocation().set(star.getLocation().x + 10000.0F, star.getLocation().y + 10000.0F);
//        nebula.setCircularOrbit(star, 160.0F, 5200.0F, 520.0F);
//        NebulaTerrainPlugin nebulaPlugin = (NebulaTerrainPlugin)((CampaignTerrainAPI)nebula).getPlugin();
//        NebulaEditor editor = new NebulaEditor(nebulaPlugin);
//        editor.regenNoise();
//        editor.noisePrune(0.75F);
//        editor.regenNoise();
//        nebula.addTag("radar_nebula");

//        SectorEntityToken nebula = Misc.addNebulaFromPNG("data/campaign/terrain/eos_nebula.png", 0.0F, 0.0F, system, "terrain", "RD_quasarnebula", 4, 4, "RD_quasarnebula", StarAge.OLD);
//        nebula.addTag("radar_nebula");

        //add special nebula
//        SectorEntityToken quasar_nebula = Misc.addNebulaFromPNG("data/campaign/terrain/eos_nebula.png",
//                star.getLocation().x, star.getLocation().y, system,
//                "terrain", "RD_quasarnebula", 128, 128, "RD_quasarnebula", StarAge.OLD);
//
//        quasar_nebula.addTag("radar_nebula");

        //Finally cleans up hyperspace
        SystemUtils.cleanup(system);
    }

    @Override
    public void addPlanets(StarSystemAPI system, PlanetAPI star, String factionId) {

    }

    @Override
    public void addPlanets(StarSystemAPI system, PlanetAPI star, PlanetAPI c_star, String factionId) {

        //------------> 黎明共和国行政首都  奥古斯都 <-------------------

        //a new planet for people
        PlanetAPI solomon_planet_augustus = system.addPlanet("solomon_planet_augustus", star,
                I18nUtil.getStarSystemsString("solomon_planet_augustus"), "rd_super_terran", 60, 185f, 4200f, 280f);

        //a new market for planet
        MarketAPI augustus_market = addMarketplace(factionId, solomon_planet_augustus, null
                , solomon_planet_augustus.getName(), 7,
                new ArrayList<>(
                        Arrays.asList(
                                Conditions.POPULATION_7,
                                Conditions.HABITABLE,
                                Conditions.MILD_CLIMATE,
                                Conditions.REGIONAL_CAPITAL,
                                Conditions.LOW_GRAVITY,
                                RD_MarketConditions.HUMAN_RESUSCITATION
                        )),
                new ArrayList<>(
                        Arrays.asList(
                                Submarkets.GENERIC_MILITARY,
                                Submarkets.SUBMARKET_BLACK,
                                Submarkets.SUBMARKET_OPEN,
                                Submarkets.SUBMARKET_STORAGE
                        )),
                new ArrayList<>(
                        Arrays.asList(
                                Industries.POPULATION,
                                Industries.MEGAPORT,
                                Industries.STARFORTRESS_MID,
                                Industries.HIGHCOMMAND,
                                Industries.ORBITALWORKS,
                                Industries.WAYSTATION,
                                Industries.MINING,
                                Industries.HEAVYBATTERIES,
                                RD_Industries.REPUBLIC_EMBASSY
                        )),
                0.30F,
                false,
                true);
        //make a custom description which is specified in descriptions.csv
        solomon_planet_augustus.setCustomDescriptionId("solomon_planet_augustus");

        //and give it a nanoforge 纳米锻造炉 全新的纳米熔炉
        ((HeavyIndustry) augustus_market.getIndustry(Industries.ORBITALWORKS)).setNanoforge(new SpecialItemData(Items.PRISTINE_NANOFORGE, null));

        //设置AI核心
        augustus_market.getIndustry(Industries.STARFORTRESS_MID).setAICoreId(Commodities.ALPHA_CORE);

        solomon_planet_augustus.getSpec().setAtmosphereColor(new Color(0xFF1340));
        solomon_planet_augustus.getSpec().setCloudColor(new Color(0x6185C8));
        solomon_planet_augustus.applySpecChanges();

        //create jumpPoint
        JumpPointAPI solomon_one_jp = Global.getFactory().createJumpPoint("Augustus JumpPoint", "Augustus 跳跃点");
        OrbitAPI au_orbit = Global.getFactory().createCircularOrbit(star, 42.0F, 4800.0F, 385.0F);
        solomon_one_jp.setOrbit(au_orbit);
        solomon_one_jp.setRelatedPlanet(solomon_planet_augustus);
        solomon_one_jp.setStandardWormholeToHyperspaceVisual();
        system.addEntity(solomon_one_jp);


        //------------> 黎明共和国 唐 <-------------------

        //a new planet for people
        PlanetAPI solomon_planet_tang = system.addPlanet("solomon_planet_tang", star,
                I18nUtil.getStarSystemsString("solomon_planet_tang"), "arid", 45, 135f, 8000f, 365f);

        //a new market for planet
        MarketAPI tang_market = addMarketplace(factionId, solomon_planet_tang, null
                , solomon_planet_tang.getName(), 6,
                new ArrayList<>(
                        Arrays.asList(
                                Conditions.POPULATION_6,
                                Conditions.FARMLAND_POOR,
                                Conditions.COLD,
                                Conditions.FRONTIER,
                                Conditions.LOW_GRAVITY,
                                Conditions.URBANIZED_POLITY,
                                RD_MarketConditions.HUMAN_RIGHTS_DECLARATION
                        )),
                new ArrayList<>(
                        Arrays.asList(
                                RD_Submarkets.DAWN_HIGH_MARKET,
                                Submarkets.SUBMARKET_OPEN,
                                Submarkets.SUBMARKET_STORAGE
                        )),
                new ArrayList<>(
                        Arrays.asList(
                                Industries.POPULATION,
                                Industries.SPACEPORT,
                                Industries.FUELPROD,
                                Industries.WAYSTATION,
                                Industries.HEAVYINDUSTRY,
                                Industries.PATROLHQ,
                                Industries.BATTLESTATION_MID,
                                Industries.HEAVYBATTERIES,
                                RD_Industries.SOLAR_CATALYTIC
                        )),
                0.30F,
                false,
                true);
        //make a custom description which is specified in descriptions.csv
        solomon_planet_tang.setCustomDescriptionId("solomon_planet_tang");

        tang_market.getIndustry(RD_Industries.SOLAR_CATALYTIC).setAICoreId(Commodities.BETA_CORE);
        tang_market.getIndustry(Industries.FUELPROD).setSpecialItem(new SpecialItemData(Items.SYNCHROTRON, null));

        //------------> 无人行星 奥克勃 <-------------------
        //a new planet for people
        PlanetAPI solomon_planet_okobe = system.addPlanet("solomon_planet_okober", star,
                I18nUtil.getStarSystemsString("solomon_planet_okobe"), "gas_giant", 75, 255f, 12800f, 735f);

        MarketUtils.addMarketConditions(star, solomon_planet_okobe.getName(),
                new ArrayList<String>(
                        Arrays.asList(
                                Conditions.VOLATILES_ABUNDANT,
                                Conditions.HIGH_GRAVITY,
                                Conditions.EXTREME_TECTONIC_ACTIVITY,
                                Conditions.EXTREME_WEATHER,
                                Conditions.RUINS_SCATTERED
                        )
                )
        );

//        system.addRingBand(solomon_planet_okobe, "misc", "rings_dust0", 256.0F, 0, Color.GREEN, 256.0F, 500.0F, 60.0F);
        system.addRingBand(solomon_planet_okobe, "misc", "rings_special0", 256.0F, 0, Color.GREEN, 256.0F, 500.0F, 60.0F);


        //------------>  黎明共和国 自由港 耶路撒冷 <-------------------

        //a new planet for people
        PlanetAPI solomon_planet_jerusalem = system.addPlanet("solomon_planet_jerusalem", solomon_planet_okobe,
                I18nUtil.getStarSystemsString("solomon_planet_jerusalem"), "frozen2", 45, 75f, 1000f, 75f);

        //a new market for planet
        MarketAPI jerusalem_market = addMarketplace(FACTION_ID, solomon_planet_jerusalem, null
                , solomon_planet_jerusalem.getName(), 4,
                new ArrayList<>(
                        Arrays.asList(
                                Conditions.POPULATION_4,
                                Conditions.FARMLAND_POOR,
                                Conditions.VERY_COLD,
                                Conditions.METEOR_IMPACTS,
                                Conditions.ORE_ULTRARICH,
                                RD_MarketConditions.HUMAN_RIGHTS_DECLARATION
                        )),
                new ArrayList<>(
                        Arrays.asList(
                                Submarkets.SUBMARKET_BLACK,
                                Submarkets.SUBMARKET_OPEN,
                                Submarkets.SUBMARKET_STORAGE
                        )),
                new ArrayList<>(
                        Arrays.asList(
                                Industries.POPULATION,
                                Industries.SPACEPORT,
                                Industries.FARMING,
                                Industries.WAYSTATION,
                                Industries.LIGHTINDUSTRY,
                                Industries.PATROLHQ,
                                Industries.ORBITALSTATION,
                                Industries.GROUNDDEFENSES,
                                RD_Industries.SOLOMON_IDOL
                        )),
                0.20F,
                true,
                true);
        //make a custom description which is specified in descriptions.csv
        solomon_planet_jerusalem.setCustomDescriptionId("solomon_planet_jerusalem");

        solomon_planet_jerusalem.getSpec().setAtmosphereColor(new Color(0xC8C334));
        solomon_planet_jerusalem.applySpecChanges();

        //create jumpPoint
        JumpPointAPI je_jp = Global.getFactory().createJumpPoint("Jerusalem JumpPoint", "Jerusalem 跳跃点");
        OrbitAPI je_orbit = Global.getFactory().createCircularOrbit(star, 42.0F, 13000.0F, 735.0F);
        je_jp.setOrbit(je_orbit);
        je_jp.setRelatedPlanet(solomon_planet_jerusalem);
        je_jp.setStandardWormholeToHyperspaceVisual();
        system.addEntity(je_jp);


        //所罗门之殇


//        SectorEntityToken tem_station = system.addCustomEntity("solomon_station1",
//                "所罗门之殇", "station_side06", factionId);
//
//        tem_station.setCircularOrbitPointingDown(system.getEntityById(COMPAION_STAR_NAME), 15, 3000, 160);
//        tem_station.setInteractionImage("illustrations", "pirate_station");

//        MarketUtils.addIndustries(tem_station, "solomon_station1", 3,
//                new ArrayList<String>(
//                        Arrays.asList(
//                                Industries.POPULATION,
//                                Industries.SPACEPORT,
//                                Industries.ORBITALSTATION
//                        )
//                ),
//                true,
//                0.06f);
//        tem_station.setCustomDescriptionId("solomon_station1");



        //set interaction image
        GlobalUtils.addInteractionImage(
                solomon_planet_augustus,
                solomon_planet_tang,
                solomon_planet_jerusalem
        );



    }


}
    
    
    
    

