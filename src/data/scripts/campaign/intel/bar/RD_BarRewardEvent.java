package data.scripts.campaign.intel.bar;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.ids.Personalities;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.BaseBarEventWithPerson;
import com.fs.starfarer.api.impl.campaign.rulecmd.AddRemoveCommodity;
import com.fs.starfarer.api.util.Misc;
import data.scripts.campaign.econ.ids.RD_Commodities;
import data.utils.GlobalUtils;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class RD_BarRewardEvent extends BaseBarEventWithPerson {

    protected static final String FACTION_ID = "republic_of_dawn";
    private MemoryAPI memory = Global.getSector().getMemory();

    private static List<Boolean> lockedList = new LinkedList<>();

    private static final Color DIALOG_COLOR = new Color(252, 50, 255);
    private static final Color POSITIVE_COLOR = new Color(255, 34, 154);

    private static final String HULLMOD_ID = "RD_highend_engine";
    private static final String VARIANT_ID = "rd_falcon_special_attack";

    static {
        lockedList.add(false);
        lockedList.add(false);
        lockedList.add(false);
    }

    public RD_BarRewardEvent() {

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
            float rep = Global.getSector().getPlayerFaction().getRelationship(FACTION_ID);

            if (lockedList.get(0) && lockedList.get(1) && lockedList.get(2)){
                return false;
            }

            if (FACTION_ID.equals(factionId)){
                return stabilityValue >= 6 && marketId.equals("solomon_planet_augustus_market") && level >= 10 && rep >= 0.50f;
            }
        }

        return false;
    }

    @Override
    protected void regen(MarketAPI market) {
        if (this.market != market) {
            super.regen(market);
        }
    }

    @Override
    protected PersonAPI createPerson() {
        FactionAPI faction = Global.getSector().getFaction("republic_of_dawn");
        PersonAPI person = faction.createRandomPerson(FullName.Gender.MALE);

        person.setName(new FullName("亚历山大", "奥斯曼", FullName.Gender.MALE));
        person.setPostId(Ranks.POST_FLEET_COMMANDER);
        person.setRankId(Ranks.SPACE_ADMIRAL);
        person.setPersonality(Personalities.STEADY);

        String personPortrait = this.getPersonPortrait();
        if (personPortrait != null) {
            person.setPortraitSprite(personPortrait);
        }

        return person;
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
        this.optionSelected((String)null, RD_BarRewardEvent.Option.INIT);
    }

    @Override
    public void addPromptAndOption(InteractionDialogAPI dialog) {
        super.addPromptAndOption(dialog);
        this.regen(dialog.getInteractionTarget().getMarket());
        TextPanelAPI text = dialog.getTextPanel();
        text.addPara("一名容光焕发的}" + this.getManOrWoman() + "{正在坐在首席座位上。肩上挂满了奖章，看起来一定是某位英雄级人物。}" + "{他看起来正在休息打坐。}");
        dialog.getOptionPanel().addOption("{停下来和容光焕发的英雄交流}", this, DIALOG_COLOR, (String)null);
    }

    public void optionSelected(String optionText, Object optionData) {
        if (optionData instanceof RD_BarRewardEvent.Option) {

            RD_BarRewardEvent.Option option = (RD_BarRewardEvent.Option)optionData;

            //get panel 获得面板
            OptionPanelAPI options = this.dialog.getOptionPanel();
            TextPanelAPI text = this.dialog.getTextPanel();
            options.clearOptions();

            CargoAPI cargo = Global.getSector().getPlayerFleet().getCargo();

            switch(option){
                case INIT:
                    text.addPara("\"您好，您是来领取共和国的奖励吗？\"");
                    text.addPara("\"是的\"");

                    float rep = Global.getSector().getPlayerFaction().getRelationship(FACTION_ID);
                    if (rep >= 0.50f){
                        if (!lockedList.get(0)){
                            options.addOption("获得反物质熔炉", Option.GAIN_ANTIMATTER_FURNACE);
                        }

                        if (rep >= 0.75f){
                            if (!lockedList.get(1)){
                                options.addOption("获得特殊船插", Option.GAIN_HULLMOD);
                            }

                            if (rep == 1.0f && !lockedList.get(2)){
                                options.addOption("获得特殊舰船", Option.GAIN_SHIP);
                            }
                        }
                    }

                    options.addOption("离开", Option.LEAVE);
                    break;
                case GAIN_ANTIMATTER_FURNACE:
                    text.addPara("\"感谢您为黎明共和国做出的贡献，这是应得的奖励\"");
                    cargo.addCommodity(RD_Commodities.ANTIMATTER_FURNACE, 1.0F);
                    AddRemoveCommodity.addCommodityGainText(RD_Commodities.ANTIMATTER_FURNACE, 1, text);

                    lockedList.set(0, true);

                    options.addOption("离开", Option.LEAVE);

                    break;
                case GAIN_HULLMOD:
                    text.addPara("\"感谢您为黎明共和国做出的贡献，这是应得的奖励\"");

                    cargo.addHullmods(HULLMOD_ID, 1);
//                    Set<String> knownHullMods = Global.getSector().getPlayerFaction().getKnownHullMods();
//                    knownHullMods.add(HULLMOD_ID);

                    GlobalUtils.addSystemPrompt("解锁了 黎明高端引擎 船插", POSITIVE_COLOR);
                    lockedList.set(1, true);

                    options.addOption("离开", Option.LEAVE);

                    break;
                case GAIN_SHIP:
                    text.addPara("\"感谢您为黎明共和国做出的贡献，这是应得的奖励\"");

                    FleetMemberAPI fleetMember = Global.getFactory().createFleetMember(FleetMemberType.SHIP, VARIANT_ID);
                    Global.getSector().getPlayerFleet().getFleetData().addFleetMember(fleetMember);

                    GlobalUtils.addSystemPrompt("获得舰船 绝杀猎鹰(典藏)" , POSITIVE_COLOR);
                    lockedList.set(2, true);

                    options.addOption("离开", Option.LEAVE);

                    break;
                case LEAVE:
                    this.done = true;
                    this.noContinue = true;
                    break;
            }

        }

    }

    protected String getPersonRank() {
        return Ranks.SPACE_ADMIRAL;
    }

    protected String getPersonPost() {
        return Ranks.POST_FLEET_COMMANDER;
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


    public static enum Option {
        INIT,
        GAIN_ANTIMATTER_FURNACE,
        GAIN_HULLMOD,
        GAIN_SHIP,
        LEAVE;
    }

}
