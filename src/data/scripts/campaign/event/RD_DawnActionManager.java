package data.scripts.campaign.event;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.RepLevel;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.intel.BaseEventManager;
import com.fs.starfarer.api.util.Pair;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import data.scripts.campaign.intel.RD_DawnActionIntel;
import org.apache.log4j.Logger;

import java.util.*;


//TODO
@Deprecated
public class RD_DawnActionManager extends BaseEventManager {

    private static final String KEY = "$rd_dawn_action";
    private static final int MIN_PLAYER_LEVEL = 1;
    private static Logger logger = Global.getLogger(RD_DawnActionManager.class);
    private static boolean event_switch = false;
    private static float duration = 0f;
    protected long start = 0L;

    private static Set<String> specialSet = new HashSet<>();
    private static Set<String> entitySet = new HashSet<>();

    static {
        specialSet.add("pirates");
        specialSet.add("luddic_path");

        Collections.addAll(entitySet,
                "solomon_planet_augustus",
                "solomon_planet_hashes",
                "annacleon_planet_endpoint"
                );
    }


    public static RD_DawnActionManager getInstance() {
        Object test = Global.getSector().getMemoryWithoutUpdate().get(KEY);
        return (RD_DawnActionManager) test;
    }

    public RD_DawnActionManager(){
        event_switch = this.isEventActive();
        Global.getSector().getMemoryWithoutUpdate().set(KEY, this);
        this.start = Global.getSector().getClock().getTimestamp();
    }


    @Override
    protected int getMinConcurrent() {
        return 1;
    }

    @Override
    protected int getMaxConcurrent() {
        return 1;
    }

    @Override
    protected EveryFrameScript createEvent() {
        if (event_switch){
            logger.info("Attempting to create dawn action event");
            MarketAPI market = this.pickMarket();

            if (market != null){
                logger.info("Create dawn action event successful!");
                return new RD_DawnActionIntel(market);
            }else{
                return null;
            }
        }

        return null;
    }

//    protected float getIntervalRateMult() {
//        return Global.getSettings().getFloat("nex_conquestMissionIntervalRateMult");
//    }

    protected MarketAPI pickMarket() {
        SectorAPI sector = Global.getSector();
        FactionAPI faction = sector.getFaction("republic_of_dawn");
        if (faction.isShowInIntelTab()){
            String sourceEntity = null;
//            for (String entity : entitySet) {
//
//                //随机选择市场的算法
//                double random = Math.random();
//
//            }

            sourceEntity = "solomon_planet_augustus";
            return sector.getEntityById(sourceEntity).getMarket();

        }else{
            return null;
        }
    }

    protected FactionAPI pickTargetFaction() {
        WeightedRandomPicker<String> picker = new WeightedRandomPicker<String>();


        for (Pair<String, Float> result : this.getWeightByFaction()) {
            picker.add(result.one, (Float) result.two);
        }

        String factionId = (String)picker.pick();
        return factionId == null ? null : Global.getSector().getFaction(factionId);
    }

    private List<Pair<String, Float>> getWeightByFaction() {
        List<Pair<String, Float>> weightList = new ArrayList<>();
        SectorAPI sector = Global.getSector();
        FactionAPI selfFaction = sector.getFaction("republic_of_dawn");

        Iterator<FactionAPI> var3 = sector.getAllFactions().iterator();
        FactionAPI faction;
        String factionId;

        while (var3.hasNext()){
            faction = (FactionAPI) var3.next();
            factionId = faction.getId();
            float weight = 0f;
            RepLevel level = RepLevel.NEUTRAL;

            if (faction.isShowInIntelTab()){
                if (!factionId.equals("republic_of_dawn")){

                    level = selfFaction.getRelationshipLevel(faction);

                    if (specialSet.contains(factionId)){
                        weight = 3.0f;
                    }else{
                        switch (level){
                            case SUSPICIOUS://怀疑
                                weight = 1.0f;
                            case HOSTILE://敌对
                                weight = 4.0f;
                            case VENGEFUL://仇恨
                                weight = 5.0f;
                        }
                    }

                    weightList.add(new Pair<String, Float>(factionId, weight));

                }
            }

        }

        return weightList;
    }

    private boolean isEventActive() {
        SectorAPI sector = Global.getSector();
        int level = sector.getPlayerStats().getLevel();
        boolean isAlive = sector.getFaction("republic_of_dawn").isShowInIntelTab();

        return level >= MIN_PLAYER_LEVEL && isAlive;
    }
}
