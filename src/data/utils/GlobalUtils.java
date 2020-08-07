package data.utils;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.econ.EconomyAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.PersonAPI;

import java.awt.*;
import java.util.*;

/**
 * 全局工具类
 */
@SuppressWarnings("all")
public class GlobalUtils {

    private static final String PORTRAIT_PATH = "graphics/portraits/";
    public static final String BP_FACTION_ID = "republic_of_dawn";

    @Deprecated
    public static final Set<String> FACTION_WHITELIST = new HashSet<>();//市场势力白名单
    public static final String INTERACTION_IMAGE = "RD_urban";

    static{
        Collections.addAll(FACTION_WHITELIST,
                "persean",
                "hegemony",
                "independent",
                "pirates");
    }

    private GlobalUtils(){

    }

    public static final class SystemName{
        public static final String SOLOMON = "solomon";

        private SystemName(){

        }
    }

    /**
     *  给市场增加管理员
     * @param marketId 市场ID
     * @param postId 职位
     * @param randId 职位
     * @param lastName 名字
     * @param pngName 图片
     * @param skillLevelsMap 技能map集合
     * @return 市场
     */
    public static MarketAPI addAdmin(String marketId, String postId, String randId, FullName fullName, String pngName, Map<String, Float> skillLevelsMap){
        EconomyAPI economy = Global.getSector().getEconomy();

        MarketAPI market = economy.getMarket(marketId);

        if (market != null) {
            PersonAPI admin = market.getFaction().createRandomPerson();
            admin.setPostId(postId);
            admin.setRankId(randId);
            admin.setName(fullName);
            admin.setPortraitSprite(PORTRAIT_PATH + pngName);


            if (skillLevelsMap != null){
                Set<String> set = skillLevelsMap.keySet();
                for (String key : set) {
                    Float value = skillLevelsMap.get(key);

                    if (value < 0){
                        value = 1f;
                    }

                    if (value > 3){
                        value = 3f;
                    }
                    admin.getStats().setSkillLevel(key, value);

                }
            }

            market.setAdmin(admin);
            market.getCommDirectory().addPerson(admin, 0);
            market.addPerson(admin);
        }
        return market;

    }

    public static void addSystemPrompt(String message){
        Global.getSector().getCampaignUI().addMessage(message);
    }

    /**
     *
     * @param message
     * @param color
     * 添加系统描述信息
     */
    public static void addSystemPrompt(String message, Color color){
        Global.getSector().getCampaignUI().addMessage(message, color);
    }


    /**
     * 添加交互图片
     */
    public static void addInteractionImage(PlanetAPI... planets){
        for (PlanetAPI planet : planets) {
            planet.setInteractionImage("illustrations", INTERACTION_IMAGE);
        }
    }


}
