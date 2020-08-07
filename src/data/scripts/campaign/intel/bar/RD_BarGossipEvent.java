package data.scripts.campaign.intel.bar;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.CoreReputationPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Personalities;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.BarEventManager;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.BaseBarEventWithPerson;
import com.fs.starfarer.api.impl.campaign.rulecmd.AddRemoveCommodity;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.util.Misc;
import data.scripts.campaign.econ.ids.RD_Commodities;
import data.scripts.campaign.intel.RD_DiscoveryAbandonedShipEvent;


import java.awt.*;

@SuppressWarnings("all")
public class RD_BarGossipEvent extends BaseBarEventWithPerson {

    protected static final String FACTION_ID = "republic_of_dawn";
    private static final Color DIALOG_COLOR = new Color(252, 50, 255);
    protected Boolean submitOnce = null;
    private MemoryAPI memory = Global.getSector().getMemory();
    private RD_DiscoveryAbandonedShipEvent intel = null;

    public RD_BarGossipEvent() {

    }

    @Override
    public boolean shouldShowAtMarket(MarketAPI market) {
        if (!super.shouldShowAtMarket(market)) {
            return false;
        }

        this.regen(market);

        if (this.market != null){
            String marketId = this.market.getId();
            String factionId = this.market.getFactionId();
            int level = Global.getSector().getPlayerStats().getLevel();
            float stabilityValue = this.market.getStabilityValue();

            if (FACTION_ID.equals(factionId)){
                return stabilityValue >= 6 && marketId.equals("solomon_planet_jerusalem_market") && level >= 10;
            }
        }

        return false;
    }

    @Override
    protected void regen(MarketAPI market) {
        if (this.market != market) {
            super.regen(market);
            this.submitOnce = false;
        }
    }

    @Override
    protected PersonAPI createPerson() {
        FactionAPI faction = Global.getSector().getFaction("republic_of_dawn");
        PersonAPI person = faction.createRandomPerson(FullName.Gender.MALE);

        person.setName(new FullName("约翰", "阿列克谢", FullName.Gender.MALE));
        person.setPostId(Ranks.POST_CITIZEN);
        person.setRankId(Ranks.CITIZEN);
        person.setPersonality(Personalities.STEADY);

        String personPortrait = this.getPersonPortrait();
        if (personPortrait != null) {
            person.setPortraitSprite(personPortrait);
        }

        return person;
    }

    protected String getPersonFaction() {
        return FACTION_ID;
    }


    @Override
    public boolean isDialogFinished() {
        return this.done;
    }


    @Override
    public void init(InteractionDialogAPI dialog) {
        super.init(dialog);
        this.done = false;
        this.noContinue = false;
        dialog.getVisualPanel().showPersonInfo(this.person, true);
        this.optionSelected((String)null, Option.INIT);
    }


    @Override
    public void addPromptAndOption(InteractionDialogAPI dialog) {
        super.addPromptAndOption(dialog);
        this.regen(dialog.getInteractionTarget().getMarket());
        TextPanelAPI text = dialog.getTextPanel();
        text.addPara("一名步履蹒跚的}" + this.getManOrWoman() + "{正在目不转晴地看着你。肩上挂了数枚奖章，甚至有领袖亲自奖励的英雄勋章。}" + "{他似乎有很多故事想对你述说}");
        dialog.getOptionPanel().addOption("{停下来和步履蹒跚的老头交流}", this, DIALOG_COLOR, (String)null);
    }

    public void optionSelected(String optionText, Object optionData) {
        if (optionData instanceof RD_BarGossipEvent.Option) {

            RD_BarGossipEvent.Option option = (RD_BarGossipEvent.Option)optionData;

            //get panel 获得面板
            OptionPanelAPI options = this.dialog.getOptionPanel();
            TextPanelAPI text = this.dialog.getTextPanel();
            options.clearOptions();

            //get cargo credits crew 获得货仓和金钱
            SectorAPI sector = Global.getSector();

            CargoAPI cargo = sector.getPlayerFleet().getCargo();

            float credits = cargo.getCredits().get();
            int crew = cargo.getCrew();
            float supplies = 0f;
            float alpha_core = 0f;

            Color highlightColor = Misc.getHighlightColor();
            Color negativeHighlightColor = Misc.getNegativeHighlightColor();

            submitOnce = false;//是否上交过一次

            switch (option){
                case INIT:
                    text.addPara("\"嘿，小伙子.\" 这个老头似乎在对面喊你.\"你愿意停下来听我讲故事或者做个交易吗?\"");
                    LabelAPI init_label = text.addPara("\"啧啧啧~~~，这个糟老头子或许真有啥好玩意？\"" + "你心想");

                    options.addOption("与他交谈", Option.BEGIN_TELL);

                    Object m1 = this.memory.get("$discovery_balck_dwarf");

                    if (m1 != null && m1 instanceof Boolean){
                        boolean missionOn = (boolean) m1;
                        if (missionOn){

                            float furnace = cargo.getCommodityQuantity(RD_Commodities.ANTIMATTER_FURNACE);
                            boolean canSubmit = furnace >= 1.0f;

                            options.addOption("上交反物质熔炉", Option.SUBMIT_FURNACE);
                            if (!canSubmit){
                                options.setEnabled(Option.SUBMIT_FURNACE, false);
                                options.setTooltip(Option.SUBMIT_FURNACE, "你的货仓没有发现 反物质熔炉");
                            }

                        }
                    }

                    options.addOption("离开", Option.LEAVE);

                    break;
                case SUBMIT_FURNACE:
                    text.addPara("\"感谢你为黎明共和国捞回了遗失的反物质熔炉\"");
                    cargo.removeCommodity(RD_Commodities.ANTIMATTER_FURNACE, 1.0F);
                    AddRemoveCommodity.addCommodityLossText(RD_Commodities.ANTIMATTER_FURNACE, 1, text);

                    this.adjustReputation(0.10f);

                    if (this.intel != null){
                        Global.getSector().getIntelManager().removeIntel(this.intel);
                        Global.getSector().getMemory().set("$discovery_balck_dwarf", false);
                    }

                    options.addOption("离开", Option.LEAVE);
                    break;
                case BEGIN_TELL:
                    text.addPara("\"老头说，你好，我是老约翰。退伍多年了，喜欢找年轻的小伙子唠叨。想当年我率领大军奇袭霸主jangla，故事说一个月也说不完。\"");
                    text.addPara("\"我去？这糟老头子怕不是在吹牛！\"");
                    text.addPara("\"你希望听我讲故事还是做个交易？\"");

                    Object m2 = memory.get("$discovery_balck_dwarf");
                    if (m2 == null){
                        options.addOption("听你的故事", Option.TELLING_A_STORY);
                    }

                    options.addOption("交易", Option.SUBMIT);

                    options.addOption("离开", Option.LEAVE);
                    break;
                case TELLING_A_STORY:
                    text.addPara("\"你知道星历200年发生的'破日之战'吗?\"");
                    text.addPara("\"这不是我在 Gatatina 学院老师告诉我们的吗?霸主大军击败了嚣张气焰的..(黎明共和国) \"");
                    text.addPara("\"哈哈，那只是霸主的一面之词，真实历史是霸主是作为侵略方来攻打我们的。\\");
                    text.addPara("\"好吧，看来我学了歪的历史。\"");
                    text.addPara("\"正如人类一句古训'历史是由胜利者书写的'，在遥远的 Sirius 星系，有颗神秘的黑矮星，你没有听错。" +
                            "据说那附近曾经发生过大规模战斗，双方死伤惨重。哈哈，想必你也知道是谁和谁打。小伙子，你愿不愿意帮我跑一趟，取个东西呢？\\");
                    text.addPara("\"据说黎明共和国曾研制出一种称为'反物质熔炉'的高科技装置，遗失多年，请问是去取它么？\"");
                    text.addPara("\"是的，不过附近有海盗出没，没多少人愿意冒这个风险，小伙子，你愿意冒险一试吗？\"");

                    options.addOption("接受任务", Option.ACCEPTED_MISSION);
                    options.addOption("离开", Option.LEAVE);

                    break;
                case ACCEPTED_MISSION:
                    text.addPara("\"前往 Sirius 星系一颗 黑矮星 附近探索 \"");

                    this.createMissionTarget();

                    Global.getSector().getMemory().set("$discovery_balck_dwarf", true);

                    options.addOption("离开", Option.LEAVE);

                    break;
                case SUBMIT:
                    if (submitOnce != null){
                        if (submitOnce == false){
                            text.addPara("\"真是个识相的小伙子！废话不多说，你可以上交Alpha核心，或者拿补给和我的稀有金属交换，你选择哪个？\"");
                        }
                    }

                    supplies = cargo.getSupplies();
                    alpha_core = cargo.getCommodityQuantity(Commodities.ALPHA_CORE);//阿尔法核心

                    boolean haveEnoughAdvancedSupplies = alpha_core >= 1;
                    boolean haveEnoughSupplies = supplies >= 10;

                    options.addOption("{上交1单位Alpha核心}", Option.SUBMIT_CORE_CONFIRM, new Color(0xFFFE03), "");
                    if (!haveEnoughAdvancedSupplies){
                        options.setEnabled(Option.SUBMIT_CORE_CONFIRM, false);
                        options.setTooltip(Option.SUBMIT_CORE_CONFIRM, "你没有足够的阿尔法核心");
                    }

                    Object m3 = memory.get("$rb_change");
                    if (m3 == null){
                        memory.set("$rb_change", 0);
                        m3 = memory.get("$rb_change");
                    }

                    if (((Integer)m3) < (int)3){

                        options.addOption("10补给交换30稀有金属", Option.SUBMIT_SUPPLIES);
                        if (!haveEnoughSupplies){
                            options.setEnabled(Option.SUBMIT_SUPPLIES_CONFIRM, false);
                            options.setTooltip(Option.SUBMIT_SUPPLIES_CONFIRM, "你没有足够的补给");
                        }
                    }

                    options.addOption("离开", Option.LEAVE);

                    break;
                case SUBMIT_CORE_CONFIRM:
                    options.addOption("确定", Option.SUBMIT_AS);
                    options.addOption("取消", Option.SUBMIT);
                    break;
                case SUBMIT_SUPPLIES_CONFIRM:
                    options.addOption("确定", Option.SUBMIT_SUPPLIES);
                case SUBMIT_AS:
                    text.addPara("你上交了Alpha核心，黎明共和国记住了你", highlightColor);

                    cargo.removeCommodity(Commodities.ALPHA_CORE, 1.0F);
                    AddRemoveCommodity.addCommodityLossText(Commodities.ALPHA_CORE, 1, text);
                    this.adjustReputation(0.05F);
                    this.submitOnce = true;

                    BarEventManager.getInstance().notifyWasInteractedWith(this);

                    options.addOption("继续", Option.SUBMIT_CONTINUE);
                    options.addOption("离开", Option.LEAVE);

                    break;
                case SUBMIT_SUPPLIES:
                    text.addPara("你上交了补给，黎明共和国记住了你", highlightColor);

                    Object m4 = memory.get("$rb_change");
                    if (m4 != null){
                        Integer num = (Integer) m4;
                        memory.set("$rb_change", num + (int)1);
                    }

                    cargo.removeCommodity("supplies", 10.0F);
                    AddRemoveCommodity.addCommodityLossText("supplies", 10, text);
                    cargo.addCommodity("rare_metals", 30.0F);
                    AddRemoveCommodity.addCommodityGainText("rare_metals", 30, text);
                    this.submitOnce = true;

                    BarEventManager.getInstance().notifyWasInteractedWith(this);

                    options.addOption("继续", Option.SUBMIT_CONTINUE);
                    options.addOption("离开", Option.LEAVE);

                    break;
                case SUBMIT_CONTINUE:
                    text.addPara("\"是否要继续交换？\"");

                    options.addOption("继续", Option.SUBMIT);

                    break;
                case LEAVE:
                    this.done = true;
                    this.noContinue = true;
                    break;
            }


        }
    }

    protected String getPersonRank() {
        return Ranks.CITIZEN;
    }

    protected String getPersonPost() {
        return Ranks.POST_CITIZEN;
    }

    protected String getPersonPortrait() {
        return null;
    }

    protected FullName.Gender getPersonGender() {
        return FullName.Gender.MALE;
    }

    public String getHisOrHer() {
        return super.getHisOrHer();
    }

    private void adjustReputation(float amount) {
        CoreReputationPlugin.CustomRepImpact impact = new CoreReputationPlugin.CustomRepImpact();
        impact.delta = amount;
        CoreReputationPlugin.RepActionEnvelope envelope = new CoreReputationPlugin.RepActionEnvelope(CoreReputationPlugin.RepActions.CUSTOM, impact, this.dialog.getTextPanel());
        Global.getSector().adjustPlayerReputation(envelope, FACTION_ID);
    }

    private void createMissionTarget() {
        StarSystemAPI system = Global.getSector().getStarSystem("sirius");
        SectorEntityToken entity = system.getEntityById("sirius_beta");

        if (entity != null){
            if (entity instanceof PlanetAPI){
                PlanetAPI star = (PlanetAPI) entity;
                if (system.getEntityById("sirius_dust") == null){
                    SectorEntityToken dust = system.addCustomEntity("sirius_dust", "遗弃之地", "rd_abandoned_land", "neutral");
                    dust.setCircularOrbitPointingDown(entity, 15, 500, 160);
                    dust.setDiscoverable((Boolean)null);
                    dust.setDiscoveryXP((Float)null);
                    dust.setSensorProfile((Float)null);
                    dust.addTag("expires");

//                    SalvageEntityGenDataSpec.DropData drop = new SalvageEntityGenDataSpec.DropData();
//                    drop.addCommodity("rd_antimatter_furnace", 100.0f);
//                    dust.addDropValue(drop);

//                    MemoryAPI memory = entity.getMemoryWithoutUpdate();
//                    long seed = memory.getLong("$salvageSeed");
//                    Random random = Misc.getRandom(seed, 100);
//                    CargoAPI salvage = SalvageEntity.generateSalvage(random, 1.0F, 1.0F, 1.0F, 1.0F, entity.getDropValue(), entity.getDropRandom());
//                    salvage.addCommodity(RD_Commodities.ANTIMATTER_FURNACE, 1.0f);


//
//
//
//                    MemoryAPI memory = debris.getMemoryWithoutUpdate();
//                    long seed = memory.getLong("$salvageSeed");
//                    Random random = Misc.getRandom(seed, 100);
//                    CargoAPI salvage = SalvageEntity.generateSalvage(random, 1.0F, 1.0F, 1.0F, 1.0F, entity.getDropValue(), entity.getDropRandom());
//                    salvage.addCommodity(RD_Commodities.ANTIMATTER_FURNACE, 1.0f);


                    this.intel = new RD_DiscoveryAbandonedShipEvent(dust, this);

                    CargoAPI cargo = Global.getSector().getPlayerFleet().getCargo();
                    TextPanelAPI text = this.dialog.getTextPanel();

                    Global.getSector().getIntelManager().addIntel(intel, false, text);
                }

            }
        }
    }


    public static enum Option {
        INIT,
        BEGIN_TELL,
        TELLING_A_STORY,
        ACCEPTED_MISSION,
        SUBMIT_FURNACE,
        SUBMIT,
        SUBMIT_CORE_CONFIRM,
        SUBMIT_SUPPLIES_CONFIRM,
        SUBMIT_AS,
        SUBMIT_SUPPLIES,
        SUBMIT_CONTINUE,
        LEAVE;
    }
}
