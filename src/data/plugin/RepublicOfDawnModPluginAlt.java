package data.plugin;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CharacterDataAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import data.scripts.campaign.event.RD_DawnActionManager;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class RepublicOfDawnModPluginAlt {

    public static boolean enableSleep = false;
    public static float SUPPLIES_CONSUME_MULT = 0.5f;
    private static final String RD_SETTINGS_FILE = "RD_OPTIONS.ini";
    private static boolean isDawnActionOn = false;

    private static final String SLEEP = "sleep";
    private static final String AUTOMATIC_ARCHIVING = "auto_archiving";
    private static final String DISGUISE = "disguise";
    private static final String REQUEST_SUPPORT = "request_support";

    public RepublicOfDawnModPluginAlt(){

    }

    static {
        enableSleep = Global.getSettings().getBoolean("enableSleep");
        if (enableSleep){
            SUPPLIES_CONSUME_MULT = Global.getSettings().getFloat("sleep_supplies_consume_mult");
        }

        try {
            initConfig();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public static void addAbilities(){

        CharacterDataAPI data = Global.getSector().getCharacterData();
        //add new abilities
        if (enableSleep){
            data.addAbility(SLEEP);
        }

        data.addAbility(AUTOMATIC_ARCHIVING);
//        data.addAbility(DISGUISE);
//        data.addAbility(REQUEST_SUPPORT);
    }

    public static void removeAbilities(){
        CharacterDataAPI data = Global.getSector().getCharacterData();

        //remove new abilities
        if (enableSleep){
            data.removeAbility(SLEEP);
        }

        data.removeAbility(AUTOMATIC_ARCHIVING);
//        data.removeAbility(DISGUISE);
//        data.removeAbility(REQUEST_SUPPORT);
    }

    public static void loadAbilities(){
        try {
            CharacterDataAPI data = Global.getSector().getCharacterData();
            CampaignFleetAPI fleet = Global.getSector().getPlayerFleet();
            if (!fleet.hasAbility(SLEEP) && enableSleep){
                data.addAbility(SLEEP);
            }
            if (!fleet.hasAbility(AUTOMATIC_ARCHIVING)){
                data.addAbility(AUTOMATIC_ARCHIVING);
            }
//            if (!fleet.hasAbility(DISGUISE)){
//                data.addAbility(DISGUISE);
//            }
//            if (!fleet.hasAbility(REQUEST_SUPPORT)){
//                data.addAbility(REQUEST_SUPPORT);
//            }
        }catch (Exception e){
            Global.getLogger(RepublicOfDawnModPluginAlt.class).error("加载技能脚本出现错误！");
            throw new RuntimeException("加载技能脚本出现错误！");
        }
    }

    public static void loadScript(){
        SectorAPI sector = Global.getSector();
        if (isDawnActionOn){
            sector.addScript(new RD_DawnActionManager());
        }
    }

    public static void initConfig() throws IOException, JSONException {
        JSONObject config = Global.getSettings().loadJSON(RD_SETTINGS_FILE);
        isDawnActionOn = config.getBoolean("rd_dawn_action");
    }

}
