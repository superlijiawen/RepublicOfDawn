package data.utils;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.EconomyAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.util.container.Pair;

import java.util.*;

@SuppressWarnings("all")
public class MarketUtils {

    //Shorthand function for adding a market

    /**
     *
     * @param factionID 势力ID
     * @param primaryEntity 入口
     * @param connectedEntities ？
     * @param name 市场名称
     * @param size 大小
     * @param marketConditions 市场条件
     * @param subMarkets 附属市场
     * @param industries 工业设施
     * @param tariff 关税
     * @param freePort 是否是自由港
     * @param withJunkAndChatter mod兼容
     * @return 市场
     */
    public static MarketAPI addMarketplace(String factionID, SectorEntityToken primaryEntity, ArrayList<SectorEntityToken> connectedEntities, String name,
                                           int size, ArrayList<String> marketConditions, ArrayList<String> subMarkets, ArrayList<String> industries, float tariff,
                                           boolean freePort, boolean withJunkAndChatter) {
        EconomyAPI globalEconomy = Global.getSector().getEconomy();
        String planetID = primaryEntity.getId();
        String marketID = planetID + "_market";

        MarketAPI newMarket = Global.getFactory().createMarket(marketID, name, size);
        newMarket.setFactionId(factionID);
        newMarket.setPrimaryEntity(primaryEntity);
        newMarket.getTariff().modifyFlat("generator", tariff);

        //Adds subMarkets
        if (null != subMarkets) {
            for (String market : subMarkets) {
                newMarket.addSubmarket(market);
            }
        }

        //Adds market conditions
        for (String condition : marketConditions) {
            newMarket.addCondition(condition);
        }

        //Adds market industries
        for (String industry : industries) {
            newMarket.addIndustry(industry);
        }

        //Sets us to a free port, if we should
        newMarket.setFreePort(freePort);

        //Adds our connected entities, if any
        if (null != connectedEntities) {
            for (SectorEntityToken entity : connectedEntities) {
                newMarket.getConnectedEntities().add(entity);
            }
        }

        globalEconomy.addMarket(newMarket, withJunkAndChatter);
        primaryEntity.setMarket(newMarket);
        primaryEntity.setFaction(factionID);

        if (null != connectedEntities) {
            for (SectorEntityToken entity : connectedEntities) {
                entity.setMarket(newMarket);
                entity.setFaction(factionID);
            }
        }

        //Finally, return the newly-generated market
        return newMarket;
    }

    /**
     *
     * @param primaryEntity 市场入口
     * @param name 市场名称（传入星球名称）
     * @param marketConditions 市场条件
     * @return 市场
     */
    public static MarketAPI addMarketConditions(SectorEntityToken primaryEntity, String name, ArrayList<String> marketConditions){
        EconomyAPI globalEconomy = Global.getSector().getEconomy();
        String planetID = primaryEntity.getId();
        String marketID = planetID + "_market";

        MarketAPI market = Global.getFactory().createMarket(marketID, name, 0);
        market.setPrimaryEntity(primaryEntity);

        for (String condition : marketConditions) {
            market.addCondition(condition);
        }
        return market;
    }

    @Deprecated
    public static MarketAPI addIndustries(SectorEntityToken primaryEntity, String name, int size, ArrayList<String> industries, boolean freePort, float tariff){
        EconomyAPI globalEconomy = Global.getSector().getEconomy();
        String planetID = primaryEntity.getId();
        String marketID = planetID + "_market";

        MarketAPI market = Global.getFactory().createMarket(marketID, name, size);
        market.setPrimaryEntity(primaryEntity);

        //Adds market industries
        for (String industry : industries) {
            market.addIndustry(industry);
        }

        //add submarkets
        market.addSubmarket(Submarkets.SUBMARKET_OPEN);
        market.addSubmarket(Submarkets.SUBMARKET_BLACK);
        market.addSubmarket(Submarkets.SUBMARKET_STORAGE);

        //Sets us to a free port, if we should
        market.setFreePort(freePort);
        market.getTariff().modifyFlat("generator", tariff);

        return market;
    }



    public static List<Pair<SectorEntityToken, Float>> buildEntityValuePair(List<SectorEntityToken> entityTokens) {
        SectorEntityToken primaryEntity = null;
        float stabilityValue = 0;

        List<Pair<SectorEntityToken, Float>> resultList = new ArrayList<>();
        for (SectorEntityToken entityToken : entityTokens) {
            stabilityValue = Math.max(0, entityToken.getMarket().getStabilityValue());
            resultList.add(new Pair<SectorEntityToken, Float>(entityToken, stabilityValue));
        }

        return resultList;
    }

    /**
     *  根据市场的稳定度选择市场，优先抛出市场稳定低于6和低于4的市场，如果有多个随机抛出
     * @param sourceList list
     * @return entity
     */
    public static SectorEntityToken pickEntity(List<Pair<SectorEntityToken, Float>> sourceList){
        if (sourceList.size() == 0){
            return null;
        }

        SectorEntityToken entityToken = null;
        float value = 0f;
        Map<Float, SectorEntityToken> resultMap = new HashMap<>();

        for (Pair<SectorEntityToken, Float> pair : sourceList) {
            value = pair.two;

            if (value <= 6){
                resultMap.put(pair.two, pair.one);
            }
        }

        if (resultMap.size() == 0){
            entityToken = sourceList.get((int) randomInt(sourceList.size())).one;
        }else{
            entityToken = resultMap.get((float)randomInt(resultMap.size()));
        }

        return entityToken;
    }

    private static int randomInt(int x){
        return new Random().nextInt(x);
    }

}
