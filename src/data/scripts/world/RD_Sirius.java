package data.scripts.world;


import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.impl.campaign.procgen.StarAge;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.SalvageSpecialAssigner;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.ShipRecoverySpecial;
import com.fs.starfarer.api.impl.campaign.terrain.DebrisFieldTerrainPlugin;
import com.fs.starfarer.api.util.Misc;
import data.scripts.campaign.econ.ids.RD_Industries;
import data.scripts.campaign.econ.ids.RD_MarketConditions;
import data.utils.*;
import data.utils.api.RD_GlobalApi;

import java.util.ArrayList;
import java.util.Arrays;

import static data.utils.MarketUtils.addMarketplace;

/**
 * 天狼星系
 */
public class RD_Sirius implements RD_GlobalApi {

    private static final String SYSTEM_NAME = "sirius";
    private static final String MAIN_STAR_NAME = "sirius_alpha";
    private static final String COMPAION_STAR_NAME = "sirius_beta";

    @Override
    public void generate(SectorAPI sector) {
        //create a star system
        StarSystemAPI system = sector.createStarSystem(SYSTEM_NAME);

        system.getLocation().set(2400f, 10500f);

        //set background image
        system.setBackgroundTextureFilename("graphics/backgrounds/background6.jpg");

        //the star
        PlanetAPI star = system.initStar(MAIN_STAR_NAME, "star_orange", 650f, 150f);

        star.getLocation().set(0f, 0f);

        //background light color
        system.setLightColor(RD_Color.LIGHT3);

        //create compaion star
        PlanetAPI c_star = system.addPlanet(COMPAION_STAR_NAME, star,
                COMPAION_STAR_NAME, "rd_star_black_dwaf", 36, 150, 15000, 360f);


        //create entityToken 通讯中继站 导航浮标 传感器阵列
        SectorEntityToken si_relay = system.addCustomEntity("AN_Relay", "AN Relay", "comm_relay", DEFAULT_FACTION_ID);
        SectorEntityToken si_buoy = system.addCustomEntity("AN_Buoy", "AN Buoy", "nav_buoy", DEFAULT_FACTION_ID);

        si_relay.setCircularOrbitPointingDown(star, 120.0F, 3750.0F, 300.0F);
        si_buoy.setCircularOrbitPointingDown(star, 120.0F, 7200.0F, 300.0F);


        //add planets
        this.addPlanets(system, star, DEFAULT_FACTION_ID);
        this.addPlanets(system, star, c_star, null);

        // generates hyperspace destinations for in-system jump points
        system.autogenerateHyperspaceJumpPoints(true, true);


//      //add nebula 增加星云
        StarSystemGenerator.addSystemwideNebula(system, StarAge.OLD);

         //Debris 废墟
        DebrisFieldTerrainPlugin.DebrisFieldParams params = new DebrisFieldTerrainPlugin.DebrisFieldParams(
                250f, // field radius - should not go above 1000 for performance reasons
                1f, // density, visual - affects number of debris pieces
                10000000f, // duration in days
                0f); // days the field will keep generating glowing pieces
        params.source = DebrisFieldTerrainPlugin.DebrisFieldSource.MIXED;
        params.baseSalvageXP = 500; // base XP for scavenging in field
        SectorEntityToken debris = Misc.addDebrisField(system, params, StarSystemGenerator.random);
        SalvageSpecialAssigner.assignSpecialForDebrisField(debris);

        // makes the debris field always visible on map/sensors and not give any xp or notification on being discovered
        debris.setSensorProfile(null);
        debris.setDiscoverable(null);

        // makes it discoverable and give 200 xp on being found
        // sets the range at which it can be detected (as a sensor contact) to 2000 units
        // commented out.
        debris.setDiscoverable(true);
        debris.setDiscoveryXP(200f);
        debris.setSensorProfile(1f);
        debris.getDetectedRangeMod().modifyFlat("gen", 2000);
        debris.setCircularOrbit(c_star, 75 + 40, 600, 250);

        //add derelicts 增加遗弃舰
        SystemUtils.addDerelict(system, c_star, "rd_hammerhead_striker", ShipRecoverySpecial.ShipCondition.AVERAGE, 300.0f, true);
        SystemUtils.addDerelict(system, c_star, "onslaught_Standard", ShipRecoverySpecial.ShipCondition.BATTERED, 325.0f, false);
        SystemUtils.addDerelict(system, c_star, "conquest_Elite", ShipRecoverySpecial.ShipCondition.BATTERED, 350.0f, false);

        //Finally cleans up hyperspace
        SystemUtils.cleanup(system);

    }

    @Override
    public void addPlanets(StarSystemAPI system, PlanetAPI star, String factionId) {

        //------------> 黎明共和国自由港  塔塔尔星 是一颗丛林行星，有黎明共和国专远古时期留下的建筑 曙光生态养殖场 <-------------------

        //a new planet for people
        PlanetAPI sirius_tatar = system.addPlanet("sirius_planet_tatar", star,
                I18nUtil.getStarSystemsString("sirius_planet_tatar"), "jungle", 60, 140f, 3800f, 180f);

        //a new market for planet
        MarketAPI tatar_market = addMarketplace(factionId, sirius_tatar, null
                , sirius_tatar.getName(), 4,
                new ArrayList<>(
                        Arrays.asList(
                                Conditions.POPULATION_4,
                                Conditions.MILD_CLIMATE,
                                Conditions.ORE_SPARSE,
                                Conditions.RARE_ORE_MODERATE,
                                Conditions.WATER_SURFACE,
                                Conditions.LOW_GRAVITY,
                                Conditions.RURAL_POLITY,
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
                                Industries.ORBITALSTATION_MID,
                                Industries.PATROLHQ,
                                Industries.FARMING,
                                Industries.GROUNDDEFENSES,
                                RD_Industries.DAWNING_ECOLOGICAL_BREEDING_BASE
                        )),
                0.20F,
                true,
                true);
        //make a custom description which is specified in descriptions.csv
        sirius_tatar.setCustomDescriptionId("sirius_planet_tatar");


        //create jumpPoint
        JumpPointAPI sirius_one_jp = Global.getFactory().createJumpPoint("Tatar JumpPoint", "Tatar 跳跃点");
        OrbitAPI ta_orbit = Global.getFactory().createCircularOrbit(star, 42.0F, 5000.0F, 180.0F);
        sirius_one_jp.setOrbit(ta_orbit);
        sirius_one_jp.setRelatedPlanet(sirius_tatar);
        sirius_one_jp.setStandardWormholeToHyperspaceVisual();
        system.addEntity(sirius_one_jp);


        //------------> 速子科技  阿森塔纳  <-------------------

        //a new planet for people
        PlanetAPI sirius_asentana = system.addPlanet("sirius_planet_asentana", star,
                I18nUtil.getStarSystemsString("sirius_planet_asentana"), "frozen", 60, 180f, 9600f, 360f);

        //a new market for planet
        MarketAPI b_market = addMarketplace("tritachyon", sirius_asentana, null
                , sirius_asentana.getName(), 4,
                new ArrayList<>(
                        Arrays.asList(
                                Conditions.POPULATION_4,
                                Conditions.COLD,
                                Conditions.VOLATILES_PLENTIFUL,
                                Conditions.ORE_ABUNDANT,
                                Conditions.RARE_ORE_RICH,
                                Conditions.FARMLAND_POOR,
                                Conditions.POOR_LIGHT
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
                                Industries.BATTLESTATION_HIGH,
                                Industries.PATROLHQ,
                                Industries.MINING,
                                Industries.LIGHTINDUSTRY,
                                Industries.GROUNDDEFENSES
                        )),
                0.30F,
                false,
                true);
        //make a custom description which is specified in descriptions.csv
        sirius_asentana.setCustomDescriptionId("sirius_planet_asentana");

        //create jumpPoint
        JumpPointAPI as_jp = Global.getFactory().createJumpPoint("Asentana JumpPoint", "Asentana 跳跃点");
        OrbitAPI as_orbit = Global.getFactory().createCircularOrbit(star, 42.0F, 10800.0F, 360.0F);
        as_jp.setOrbit(as_orbit);
        as_jp.setRelatedPlanet(sirius_asentana);
        as_jp.setStandardWormholeToHyperspaceVisual();
        system.addEntity(as_jp);



        //庞贝 无主之地
        PlanetAPI solomon_planet_pompeii = system.addPlanet("sirius_planet_pompeii", star,
                I18nUtil.getStarSystemsString("sirius_planet_pompeii"), "lava_minor", 75, 255f, 8800f, 735f);


        MarketUtils.addMarketConditions(star, solomon_planet_pompeii.getName(),
                new ArrayList<String>(
                        Arrays.asList(
                                Conditions.VOLATILES_ABUNDANT,
                                Conditions.EXTREME_TECTONIC_ACTIVITY,
                                Conditions.EXTREME_WEATHER,
                                Conditions.ORGANICS_TRACE
                        )
                )
        );

        //set interaction image
        GlobalUtils.addInteractionImage(
                sirius_tatar
        );

    }

    @Override
    public void addPlanets(StarSystemAPI system, PlanetAPI star, PlanetAPI c_star, String factionId) {
        //------------> 海盗  布瑞克  <-------------------

        //a new planet for people
        PlanetAPI sirius_brick = system.addPlanet("sirius_planet_brick", c_star,
                I18nUtil.getStarSystemsString("sirius_planet_brick"), "barren", 60, 100f, 3000f, 100f);

        //a new market for planet
        MarketAPI brick_market = addMarketplace("pirates", sirius_brick, null
                , sirius_brick.getName(), 3,
                new ArrayList<>(
                        Arrays.asList(
                                Conditions.POPULATION_3,
                                Conditions.COLD,
                                Conditions.FARMLAND_POOR,
                                Conditions.POOR_LIGHT
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
                                Industries.GROUNDDEFENSES
                        )),
                0.20F,
                true,
                true);
        //make a custom description which is specified in descriptions.csv
        sirius_brick.setCustomDescriptionId("sirius_planet_brick");
    }
}
