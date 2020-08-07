package data.scripts.campaign.econ.submarkets;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.submarkets.BaseSubmarketPlugin;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import org.lazywizard.lazylib.MathUtils;

import java.util.*;

/**
 * 曙光市场
 */
//TODO 基本完善
public class RD_DawnSubmarketPlugin extends BaseSubmarketPlugin {

    private static final RepLevel REP_LEVEL_REQUIRED = RepLevel.NEUTRAL;
    private static final String FACTION_ID = "republic_of_dawn";
    private float stabilityValue = 0f;
    private float playerRep = 0f;
    private String occupyingFactionId = "republic_of_dawn";

    private List<FactionAPI> factionWhiteList = new LinkedList<>();
    private final static float UPDATE_DAY = 14.0f;
    private final static float STAND_REP_VALUE = 0f;
    private boolean needUpdate = true;

    private float enableDuration = 0.0F;


    public RD_DawnSubmarketPlugin(){
    }


    public void advance(float amount) {
        float days = Global.getSector().getClock().convertToDays(amount);
        this.sinceSWUpdate += days;
        this.sinceLastCargoUpdate += days;
        this.enableDuration += days;
        if (this.enableDuration >= UPDATE_DAY) {
            this.enableDuration = 0.0F;
            this.needUpdate = !this.needUpdate;
        }

        boolean empty = this.getCargo().isEmpty();
        if (empty){
            this.updateCargoPrePlayerInteraction();
        }
    }

    private void updateFactionWhiteList (){
        if (this.factionWhiteList.size() != 0){
            this.factionWhiteList.clear();
        }

        FactionAPI dawn = Global.getSector().getFaction(FACTION_ID);

        List<FactionAPI> factionList = Global.getSector().getAllFactions();
        for (FactionAPI f : factionList) {
            if (f.isShowInIntelTab() && !f.getId().equals(FACTION_ID)){
                if (f.getRelationship(dawn.getId())  >= STAND_REP_VALUE){
                    this.factionWhiteList.add(f);
                }
            }
        }
    }

    public void init(SubmarketAPI submarket) {
        super.init(submarket);

        this.stabilityValue = this.market.getStabilityValue();
        this.playerRep = Global.getSector().getPlayerFaction().getRelationship(FACTION_ID);
        this.occupyingFactionId = this.market.getFactionId();
    }

    public void updateCargoPrePlayerInteraction() {
        float seconds = Global.getSector().getClock().convertToSeconds(this.sinceLastCargoUpdate);
        this.addAndRemoveStockpiledResources(seconds, false, true, true);
        this.sinceLastCargoUpdate = 0.0F;
        if (this.okToUpdateShipsAndWeapons()) {
            this.sinceSWUpdate = 0.0F;

            //重新刷新一次数据
            this.reloadData();

            //设置武器权重
            WeightedRandomPicker<String> factionPicker = new WeightedRandomPicker<>();
            WeightedRandomPicker<String> weaponPicker = new WeightedRandomPicker<>();

            factionPicker.add(FACTION_ID, 10.0f);
            weaponPicker.add(FACTION_ID, 10.0f);


            //迭代遍历势力列表，对市场白名单的势力判断是否存在，根据关系进行权重设置
//            Iterator<FactionAPI> iterator = Global.getSector().getAllFactions().iterator();

            if (this.factionWhiteList.size() != 0){
                Iterator<FactionAPI> iterator = this.factionWhiteList.iterator();

                while(iterator.hasNext()){
                    FactionAPI faction = iterator.next();
                    String factionId = faction.getId();
                    float weight = 0f;

                    RepLevel rep = Global.getSector().getFaction(FACTION_ID).getRelationshipLevel(factionId);

                    switch(rep){
                        case COOPERATIVE://合作
                            weight = 10.0f;
                            break;
                        case WELCOMING://欢迎
                            weight = 8.0f;
                            break;
                        case FAVORABLE://良好
                            weight = 6.0f;
                            break;
                        case FRIENDLY://友好
                            weight = 4.0f;
                            break;
                        case NEUTRAL://中立
                            weight = 2.0f;
                            break;
//                        case SUSPICIOUS://怀疑
//                            weight = 0.5f;
//                            break;
//                        case INHOSPITABLE://冷淡
//                            weight = 0.25f;
//                            break;
//                        case HOSTILE://敌对
//                            weight = 0.1f;
//                            break;
//                        case VENGEFUL://仇恨
//                            weight = 0f;
//                            break;
                    }

                    factionPicker.add(factionId, weight);
                    factionPicker.add(FACTION_ID, weight * 0.1f);
                    weaponPicker.add(factionId, weight);
                    weaponPicker.add(FACTION_ID, weight *0.1f);
                }

                this.pruneWeapons(0.0F);

                int size = this.market.getSize();
                //商品数量
                //武器数量和本市场的规模和稳定值有关，6级规模10稳定度会刷新14。
                int weapons = Math.max(0,(size  * 2 + (int)stabilityValue - 8));
                //战机数量和本市场的规模和稳定值有关，6级规模10稳定度会刷新11
                int fighters = Math.max(0, (size + (int)stabilityValue - 5));

                //向市场中添加武器和战机
                this.addWeapons(weapons * 2 - 1, weapons * 2 + 1, 3, (String) weaponPicker.pick());
                this.addFighters(fighters, fighters + 2, 3, (String) factionPicker.pick());

                float sMult = Math.max(0.1F, this.stabilityValue / 10.0F);

                this.getCargo().getMothballedShips().clear();//封存飞船

                String factionId = factionPicker.pick();
                FactionAPI faction = Global.getSector().getFaction(factionId);
                FactionDoctrineAPI doctrine = faction.getDoctrine().clone();
                this.addShips(factionId, MathUtils.getRandomNumberInRange(20.0f, 80.0f) * sMult,
                        MathUtils.getRandomNumberInRange(0.0f, 20.0f),
                        MathUtils.getRandomNumberInRange(0.0f, 20.0f),
                        MathUtils.getRandomNumberInRange(0.0f, 20.0f),
                        MathUtils.getRandomNumberInRange(0.0f, 20.0f),
                        10.0F, (Float)null, 0.0F, FactionAPI.ShipPickMode.ALL, doctrine);


            }

        }

        this.getCargo().sort();
    }




    public boolean isEnabled(CoreUIAPI ui) {
        //潜行状态下无法使用
        if (ui.getTradeMode() == CampaignUIAPI.CoreUITradeMode.SNEAK){
            return false;
        }else{
            this.reloadData();
            if (this.occupyingFactionId != null){
                //该市场必须被黎明共和国占领且稳定值大于2关系值大于0才能使用
                if (this.occupyingFactionId.equals(FACTION_ID)){
                    if (this.stabilityValue >= 2){
                        return this.playerRep >= REP_LEVEL_REQUIRED.getMin();
                    }
                }
            }
        }

        return false;
    }


    public String getTooltipAppendix(CoreUIAPI ui) {
        if (!this.isEnabled(ui)) {
            return "要求: " + this.submarket.getFaction().getDisplayName() + " - " + REP_LEVEL_REQUIRED.getDisplayName().toLowerCase();
        } else {
            return ui.getTradeMode() == CampaignUIAPI.CoreUITradeMode.SNEAK ? "需要: 授权正常停靠（开启应答器）" : null;
        }
    }


    public String getName() {
        this.occupyingFactionId = this.market.getFactionId();
        if (!this.occupyingFactionId.equals(FACTION_ID)){
            return "关闭的曙光市场";
        }
        return "曙光市场";
    }

    @Override
    public float getTariff() {
        RepLevel rep = Global.getSector().getPlayerFaction().getRelationshipLevel(FACTION_ID);
        switch(rep){
            case COOPERATIVE:
                return 0.30f;
            case WELCOMING:
                return 0.36f;
            case FAVORABLE:
                return 0.42f;
            case FRIENDLY:
                return 0.48f;
            case NEUTRAL:
                return 0.54f;
            case SUSPICIOUS:
                return 0.60f;
            default:
                return 1.0f;
        }

    }

    public boolean isBlackMarket() {
        return false;
    }


    public boolean isIllegalOnSubmarket(CargoStackAPI stack, TransferAction action) {
        return action == TransferAction.PLAYER_SELL;
    }

    public boolean isIllegalOnSubmarket(String commodityId, TransferAction action) {
        return action == TransferAction.PLAYER_SELL;
    }

    public boolean isIllegalOnSubmarket(FleetMemberAPI member, TransferAction action) {
        return action == TransferAction.PLAYER_SELL;
    }

    public String getIllegalTransferText(FleetMemberAPI member, TransferAction action) {
        return "正不是垃圾场!";
    }

    public String getIllegalTransferText(CargoStackAPI stack, TransferAction action) {
        return "这不是垃圾场!";
    }

    private void reloadData(){
        this.stabilityValue = this.market.getStabilityValue();
        this.playerRep = Global.getSector().getPlayerFaction().getRelationship(FACTION_ID);
        this.occupyingFactionId = submarket.getFaction().getId();

        if (needUpdate){
            this.updateFactionWhiteList();
        }
    }


}
