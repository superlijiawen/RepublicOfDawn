package data.scripts.world;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.procgen.StarAge;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import data.scripts.campaign.econ.ids.RD_Industries;
import data.scripts.campaign.econ.ids.RD_MarketConditions;
import data.utils.GlobalUtils;
import data.utils.I18nUtil;
import data.utils.RD_Color;
import data.utils.SystemUtils;
import data.utils.api.RD_GlobalApi;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

import static data.utils.MarketUtils.addMarketplace;

/**
 * 安纳克里昂 星系
 */
public class RD_Annacleon implements RD_GlobalApi {

    private static final String SYSTEM_NAME = "annacleon";
    private static final String STAR_NAME = "annacleon";

    @Override
    public void generate(SectorAPI sector) {
        //create a star system
        StarSystemAPI system = sector.createStarSystem(SYSTEM_NAME);

        system.getLocation().set(8500f, 14500f);

        //set background image
        system.setBackgroundTextureFilename("graphics/backgrounds/background5.jpg");

        //the star
        PlanetAPI star = system.initStar(STAR_NAME, "star_blue_giant", 950f, 350f);


        //background light color
        system.setLightColor(RD_Color.LIGHT1);

        //make asteroid belt surround it
        system.addAsteroidBelt(star, 100, 3600f, 200f, 180, 360, Terrain.ASTEROID_BELT, "");
        system.addAsteroidBelt(star, 100, 6400f, 200f, 180, 360, Terrain.ASTEROID_BELT, "");
        system.addAsteroidBelt(star, 120, 8000f, 400f, 180, 320, Terrain.ASTEROID_BELT, "");

        system.addRingBand(star, "misc", "rings_dust0", 512.0F, 0, Color.LIGHT_GRAY, 256.0F, 9600.0F, 360.0F);
        system.addRingBand(star, "misc", "rings_ice0", 2048.0F, 0, Color.LIGHT_GRAY, 256.0F, 12800.0F, 360.0F);

        //create entityToken 通讯中继站 导航浮标 传感器阵列
        SectorEntityToken so_relay = system.addCustomEntity("AN_Relay", "AN Relay", "comm_relay", DEFAULT_FACTION_ID);
        SectorEntityToken so_buoy = system.addCustomEntity("AN_Buoy", "AN Buoy", "nav_buoy", DEFAULT_FACTION_ID);
        SectorEntityToken so_array = system.addCustomEntity("AN_Array", "AN Relay", "sensor_array", "persean");

        so_relay.setCircularOrbitPointingDown(star, 120.0F, 3250.0F, 300.0F);
        so_buoy.setCircularOrbitPointingDown(star, 120.0F, 6900.0F, 300.0F);
        so_array.setCircularOrbitPointingDown(star, 120.F, 15000.0F, 300.0F);

        //create gate 奔溃的星门
        SectorEntityToken an_gate = system.addCustomEntity("AN_Gate", "安纳克里昂之门", "inactive_gate", (String)null);
        an_gate.setCircularOrbitPointingDown(star, 120.0F, 12750.0F, 300.0F);


        this.addPlanets(system, star, DEFAULT_FACTION_ID);

        // generates hyperspace destinations for in-system jump points
        system.autogenerateHyperspaceJumpPoints(true, true);


        //add nebula 增加星云
        StarSystemGenerator.addSystemwideNebula(system, StarAge.OLD);

        //Finally cleans up hyperspace
        SystemUtils.cleanup(system);

    }

    @Override
    public void addPlanets(StarSystemAPI system, PlanetAPI star, String factionId) {
        //------------> 黎明共和国故都  端点星 是一颗贫瘠类地行星，曾遭受过霸主轰炸。部分设施被迫降级 <-------------------

        //a new planet for people
        PlanetAPI endPoint = system.addPlanet("annacleon_planet_endpoint", star,
                I18nUtil.getStarSystemsString("annacleon_planet_endpoint"), "rd_barren_terran", 60, 135f, 4200f, 180f);

        //a new market for planet
        MarketAPI endPoint_market = addMarketplace(factionId, endPoint, null
                , endPoint.getName(), 5,
                new ArrayList<>(
                        Arrays.asList(
                                Conditions.POPULATION_5,
                                Conditions.MILD_CLIMATE,
                                Conditions.ORE_SPARSE,
                                Conditions.RARE_ORE_MODERATE,
                                Conditions.FARMLAND_POOR,
                                Conditions.WATER_SURFACE,
                                Conditions.LOW_GRAVITY,
                                Conditions.RUINS_WIDESPREAD,
                                Conditions.CLOSED_IMMIGRATION,
                                Conditions.POLLUTION,
                                RD_MarketConditions.HUMAN_RESUSCITATION
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
                                Industries.WAYSTATION,
                                Industries.BATTLESTATION_MID,
                                Industries.MILITARYBASE,
                                Industries.LIGHTINDUSTRY,
                                Industries.HEAVYBATTERIES,
                                RD_Industries.SOLAR_CATALYTIC
                        )),
                0.30F,
                false,
                true);
        //make a custom description which is specified in descriptions.csv
        endPoint.setCustomDescriptionId("annacleon_planet_endpoint");
        //设置AI核心
        endPoint_market.getIndustry(Industries.MILITARYBASE).setAICoreId(Commodities.BETA_CORE);

        //create jumpPoint
        JumpPointAPI endpoint_jp = Global.getFactory().createJumpPoint("Endpoint JumpPoint", "Endpoint 跳跃点");
        OrbitAPI en_orbit = Global.getFactory().createCircularOrbit(star, 42.0F, 5000.0F, 180.0F);
        endpoint_jp.setOrbit(en_orbit);
        endpoint_jp.setRelatedPlanet(endPoint);
        endpoint_jp.setStandardWormholeToHyperspaceVisual();
        system.addEntity(endpoint_jp);

        system.addAsteroidBelt(endPoint, 120, 600f, 100f, 180, 320, Terrain.ASTEROID_BELT, "");



        //------------> 霸主监视站  安娜 曾是黎明共和国的一颗矿产丰富的冰原行星，现被霸主占领 <-------------------

        //a new planet for people
        PlanetAPI anna = system.addPlanet("annacleon_planet_anna", star,
                I18nUtil.getStarSystemsString("annacleon_planet_anna"), "rocky_ice", 60, 95f, 10000f, 480f);

        //a new market for planet
        MarketAPI anna_market = addMarketplace("hegemony", anna, null
                , anna.getName(), 5,
                new ArrayList<>(
                        Arrays.asList(
                                Conditions.POPULATION_5,
                                Conditions.COLD,
                                Conditions.POOR_LIGHT,
                                Conditions.ORE_ABUNDANT,
                                Conditions.RARE_ORE_RICH,
                                Conditions.FARMLAND_POOR,
                                RD_MarketConditions.HUMAN_RIGHTS_DECLARATION,
                                RD_MarketConditions.ENHANCED_DEFENSE
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
                                Industries.WAYSTATION,
                                Industries.BATTLESTATION,
                                Industries.MILITARYBASE,
                                Industries.MINING,
                                Industries.LIGHTINDUSTRY,
                                Industries.GROUNDDEFENSES
                        )),
                0.30F,
                false,
                true);
        //make a custom description which is specified in descriptions.csv
        anna.setCustomDescriptionId("annacleon_planet_anna");


        //------------> 英仙座联盟殖民地 <-------------------

        //a new planet for people
        PlanetAPI susan = system.addPlanet("annacleon_planet_susan", star,
                I18nUtil.getStarSystemsString("annacleon_planet_susan"), "toxic_cold", 60, 105f, 14800f, 270f);

        //a new market for planet
        MarketAPI susan_market = addMarketplace("persean", susan, null
                , susan.getName(), 3,
                new ArrayList<>(
                        Arrays.asList(
                                Conditions.POPULATION_3,
                                Conditions.COLD,
                                Conditions.TOXIC_ATMOSPHERE,
                                Conditions.FARMLAND_POOR,
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
                                Industries.WAYSTATION,
                                Industries.ORBITALSTATION,
                                Industries.PATROLHQ,
                                Industries.LIGHTINDUSTRY,
                                Industries.GROUNDDEFENSES
                        )),
                0.30F,
                false,
                true);
        //make a custom description which is specified in descriptions.csv
        susan.setCustomDescriptionId("annacleon_planet_susan");


        //------------> 自由联盟殖民地 卡捷琳娜 霸主 英仙座联盟 黎明共和国 三方势力共同协商创建的殖民地  <-------------------

        //a new planet for people
        PlanetAPI katerina = system.addPlanet("annacleon_planet_katerina", star,
                I18nUtil.getStarSystemsString("annacleon_planet_katerina"), "tundra", 60, 115f, 7500f, 480f);

        //a new market for planet
        MarketAPI katerina_market = addMarketplace("independent", katerina, null
                , katerina.getName(), 4,
                new ArrayList<>(
                        Arrays.asList(
                                Conditions.POPULATION_4,
                                Conditions.MILD_CLIMATE,
                                Conditions.INIMICAL_BIOSPHERE,
                                Conditions.FARMLAND_RICH,
                                Conditions.ORE_MODERATE,
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
                                Industries.WAYSTATION,
                                Industries.ORBITALSTATION,
                                Industries.PATROLHQ,
                                Industries.MINING,
                                Industries.FARMING,
                                Industries.GROUNDDEFENSES
                        )),
                0.30F,
                false,
                true);
        //make a custom description which is specified in descriptions.csv
        katerina.setCustomDescriptionId("annacleon_planet_katerina");

        //create jumpPoint
        JumpPointAPI katerina_jp = Global.getFactory().createJumpPoint("Katerina JumpPoint", "Katerina 跳跃点");
        OrbitAPI ka_orbit = Global.getFactory().createCircularOrbit(star, 42.0F, 8400.0F, 480.0F);
        katerina_jp.setOrbit(ka_orbit);
        katerina_jp.setRelatedPlanet(endPoint);
        katerina_jp.setStandardWormholeToHyperspaceVisual();
        system.addEntity(katerina_jp);


        //set interaction image
        GlobalUtils.addInteractionImage(
                endPoint
        );

    }

    @Override
    public void addPlanets(StarSystemAPI system, PlanetAPI star, PlanetAPI c_star, String factionId) {

    }
}
