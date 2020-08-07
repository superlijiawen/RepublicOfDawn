package data.plugin;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.BarEventManager;
import com.thoughtworks.xstream.XStream;
import data.scripts.campaign.econ.marketconditions.RD_EnhancedDefense;
import data.scripts.campaign.event.RD_NothingnessStationFleetManager;
import data.scripts.campaign.intel.RD_DiscoveryAbandonedShipEvent;
import data.scripts.campaign.intel.bar.RD_BarGossipEventCreator;
import data.scripts.campaign.econ.marketconditions.RD_HumanResuscitation;
import data.scripts.campaign.econ.marketconditions.RD_HumanRightsDeclaration;
import data.scripts.campaign.econ.submarkets.RD_DawnSubmarketPlugin;
import data.scripts.campaign.intel.bar.RD_BarRewardEvent;
import data.scripts.campaign.intel.bar.RD_BarRewardEventCreator;
import data.scripts.faction.RepublicOfDawnModGen;
import data.utils.GlobalUtils;
import exerelin.campaign.SectorManager;

import java.util.HashMap;
import java.util.Map;

public class RepublicOfDawnModPlugin extends BaseModPlugin {

    public RepublicOfDawnModPlugin(){

    }

    @Override
    public void onApplicationLoad() throws ClassNotFoundException {
        String message;
        try {
            Global.getSettings().getScriptClassLoader().loadClass("org.lazywizard.lazylib.ModUtils");
        } catch (ClassNotFoundException e) {
            message = System.lineSeparator() + System.lineSeparator() + "黎明共和国需要LazyLib这个前置mod" + System.lineSeparator() + System.lineSeparator() + "You can download LazyLib at http://fractalsoftworks.com/forum/index.php?topic=5444" + System.lineSeparator();
            throw new ClassNotFoundException(message);
        }

        try {
            Global.getSettings().getScriptClassLoader().loadClass("data.scripts.plugins.MagicTrailPlugin");
        } catch (ClassNotFoundException e) {
            message = System.lineSeparator() + System.lineSeparator() + "黎明共和国需要MagicLib这个前置mod" + System.lineSeparator() + System.lineSeparator() + "You can download MagicLib at http://fractalsoftworks.com/forum/index.php?topic=13718" + System.lineSeparator();
            throw new ClassNotFoundException(message);
        }

    }

    @Override
    public void onNewGame() {
        if (!Global.getSettings().getModManager().isModEnabled("nexerelin") || SectorManager.getCorvusMode()) {

            new RepublicOfDawnModGen().generate(Global.getSector());

            //load scripts 加載腳本
            RepublicOfDawnModPluginAlt.loadScript();
        }

    }



    @Override
    public void onNewGameAfterEconomyLoad() {
        //set administrator for colony
        String marketId1 = "solomon_planet_augustus_market";

        Map<String, Float> map1 = new HashMap<>();
        map1.put("planetary_operations", 3f);
        map1.put("industrial_planning", 3f);

        GlobalUtils.addAdmin(marketId1, Ranks.POST_ADMINISTRATOR, Ranks.SPACE_ADMIRAL, new FullName("Benjamin", "Lawrence", FullName.Gender.MALE), "RD_SoulKnight.png", map1);


        String marketId2 = "solomon_planet_jerusalem_market";

        Map<String, Float> map2 = new HashMap<>();
        map2.put("planetary_operations", 3f);

        GlobalUtils.addAdmin(marketId2, Ranks.POST_ADMINISTRATOR, Ranks.SPACE_ADMIRAL, new FullName("Ottoman", "Emir", FullName.Gender.MALE), "RD_Ottoman.png", map2);
    }

    @Override
    public void onGameLoad(boolean newGame) {
        //add bar event
        BarEventManager manager = BarEventManager.getInstance();
        if (!manager.hasEventCreator(RD_BarGossipEventCreator.class)){
            manager.addEventCreator(new RD_BarGossipEventCreator());
        }
        if (!manager.hasEventCreator(RD_BarRewardEvent.class)){
            manager.addEventCreator(new RD_BarRewardEventCreator());
        }

        //加载技能脚本
        RepublicOfDawnModPluginAlt.loadAbilities();

    }

    @Override
    public void afterGameSave() {
        RepublicOfDawnModPluginAlt.addAbilities();
    }

    @Override
    public void beforeGameSave() {
        RepublicOfDawnModPluginAlt.removeAbilities();
    }

    @Override
    public void configureXStream(XStream x) {
        x.alias("RD_DawnSubmarketPlugin", RD_DawnSubmarketPlugin.class);
        x.alias("RD_HumanResuscitation", RD_HumanResuscitation.class);
        x.alias("RD_HumanRightsDeclaration", RD_HumanRightsDeclaration.class);
        x.alias("RD_EnhancedDefense", RD_EnhancedDefense.class);
//        x.alias("RD_DawnActionManager", RD_DawnActionManager.class);
//        x.alias("RD_DawnActionIntel", RD_DawnActionIntel.class);
        x.alias("RD_DiscoveryAbandonedShipEvent", RD_DiscoveryAbandonedShipEvent.class);
        x.alias("RD_NothingnessStationFleetManager", RD_NothingnessStationFleetManager.class);
    }


    
    
    
}
