package data.scripts.campaign.abilities;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BattleAPI;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;

import com.fs.starfarer.api.impl.campaign.abilities.BaseToggleAbility;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.plugin.RepublicOfDawnModPluginAlt;
import data.utils.GlobalUtils;

import java.awt.*;
import java.util.List;
import java.util.Random;

/**
 * 技能
 * 休眠
 *
 * 使用前提：
 * 只有停下来（航速为0）才可以使用此技能 只有在星系中才可以使用此技能 附近不存在敌人
 * (建议在星球旁边休眠，方便恢复)
 *
 * buff:
 * 补给消耗将大大减少
 * 被探测的范围减少50%
 *
 * debuff:
 * 传感器扫描范围减少 50%
 * 随机失去1%~5%的货仓补给作为安置费
 *
 */

public class RD_Sleep extends BaseToggleAbility {

    private static final float SUPPLIES_CONSUME_MULT = RepublicOfDawnModPluginAlt.SUPPLIES_CONSUME_MULT;
    private static final float SENSOR_RANGE_MULT = 0.5f;
    private static final float DETECTED_RANGE_MULT = 0.5F;
    private static final float MAX_DISTANCE = 1000.0f;
    private static final Color POSITIVE_REMINDER = new Color(0xFF35C3);
    private static final Color NEGATIVE_REMINDER = new Color(0xFF0016);

    private static SleepStatus status = SleepStatus.OFF;
    private static boolean haveEnemy = false;
    private static boolean isInHyperspace = false;
    private static boolean zeroSpeed = false;
    private static String enemy = "";

    public RD_Sleep() {

    }

    protected String getActivationText() {
        return "休眠";
    }

    protected String getDeactivationText() {
        return "苏醒";
    }

    public void advance(float amount) {
        super.advance(amount);
    }

    @Override
    public boolean isUsable() {
        if (super.isUsable()){
            CampaignFleetAPI fleet = this.getFleet();
            if (fleet != null && status == SleepStatus.OFF){

                List<CampaignFleetAPI> fleets = Misc.getNearbyFleets(fleet, MAX_DISTANCE);
                for (CampaignFleetAPI f : fleets) {
                    boolean hostile = f.getFaction().isHostileTo("player");
                    if (hostile){
                        haveEnemy = true;
                        enemy = f.getFaction().getDisplayName();
                        return false;
                    }else{
                        haveEnemy = false;
                        enemy = "";
                        continue;
                    }
                }


                if (fleet.isInHyperspace()){
                    isInHyperspace = true;
                    return false;
                }else{
                    isInHyperspace = false;
                }

                float speed = fleet.getCurrBurnLevel();
//                float speed = fleet.getTravelSpeed();
                if (speed != 0){
                    zeroSpeed = false;
                    return false;
                }else{
                    zeroSpeed = true;
                }

                return true;
            }
        }

        return false;
    }

    @Override
    protected void activateImpl() {
        status = SleepStatus.ON;
        GlobalUtils.addSystemPrompt("你的舰队正在准备休眠", POSITIVE_REMINDER);
        CampaignFleetAPI fleet = this.getFleet();
        if (fleet != null && fleet.isPlayerFleet()){
            CargoAPI cargo = fleet.getCargo();
            float supplies = cargo.getSupplies();
            long minus = 0;
            int random = 0;

            random = (1+ new Random().nextInt(5));
            minus = Math.round(((float) random / 100.0) * supplies);
            cargo.removeCommodity(Commodities.SUPPLIES, minus);
            GlobalUtils.addSystemPrompt("你付出了" + minus + "单位补给作为安置费，你的舰队开始休眠了 zzz");
        }
    }



    private void updateSuppliesConsumeSpeed(boolean minus) {
        CampaignFleetAPI fleet = this.getFleet();
        if (fleet != null){
            List<FleetMemberAPI> fleets = fleet.getFleetData().getMembersListCopy();
            if (minus){
                for (FleetMemberAPI f : fleets) {
                    f.getStats().getSuppliesPerMonth().modifyMult(this.getModId(), SUPPLIES_CONSUME_MULT);
                }
            }else{
                for (FleetMemberAPI f : fleets) {
                    f.getStats().getSuppliesPerMonth().unmodify();
                }
            }
        }
    }


    @Override
    protected void applyEffect(float amount, float level) {
        if (status == SleepStatus.ON){
            CampaignFleetAPI fleet = Global.getSector().getPlayerStats().getFleet();

            if (fleet != null){
                if (fleet.isInHyperspace()){
                    status = SleepStatus.OFF;
                    isInHyperspace = true;
                    this.deactivate();
                }else if (fleet.getCurrBurnLevel() != 0){
                    status = SleepStatus.OFF;
                    zeroSpeed = false;
                    this.deactivate();
                }else{
                    this.updateSuppliesConsumeSpeed(true);
                    fleet.getStats().getSensorRangeMod().modifyMult(this.getModId(), SENSOR_RANGE_MULT);
                    fleet.getStats().getDetectedRangeMod().modifyMult(this.getModId(), DETECTED_RANGE_MULT);
                }

                haveEnemy = false;
                enemy = "";
            }
        }

    }

    @Override
    public void pressButton() {
        super.pressButton();
    }


    @Override
    public void deactivate() {
        super.deactivate();
    }

    @Override
    protected void deactivateImpl() {
        this.cleanupImpl();
    }

    @Override
    protected void cleanupImpl() {
        CampaignFleetAPI fleet = this.getFleet();
        if (fleet != null){
            fleet.getStats().getSensorRangeMod().unmodify();
            fleet.getStats().getDetectedRangeMod().unmodify();

            if (haveEnemy || isInHyperspace || !zeroSpeed){
                GlobalUtils.addSystemPrompt("提前结束了休眠", NEGATIVE_REMINDER);
            }else{
                GlobalUtils.addSystemPrompt("你的舰队结束了休眠", POSITIVE_REMINDER);
            }

            this.updateSuppliesConsumeSpeed(false);
            status = SleepStatus.OFF;

        }
    }


    @Override
    public void cleanup() {

    }

    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded) {
        Color h = Misc.getHighlightColor();
        Color g = Misc.getGrayColor();
        String button = " (关)";
        if (this.turnedOn) {
            button = " (开)";
        }
        tooltip.addTitle(this.spec.getName());
        LabelAPI title = tooltip.addTitle(this.spec.getName() + button);
        title.setHighlightColor(g);
        float pad = 10.0F;

        tooltip.addPara("舰队休眠必须满足三个条件： (1)在星系中  (2)附近1000单位范围内没有敌人 (3)当前航速为零 ", pad);
        String[] message = new String[]{"" + SENSOR_RANGE_MULT * 100 + "%", "" + DETECTED_RANGE_MULT * 100 + "%"};
        tooltip.addPara("降低传感器范围  %s* , 降低被探勘的范围  %s* ", pad, h, message);
        tooltip.addPara("整个舰队补给消耗速率降低  %s* ", pad, h, new String[]{"" + (int)((1 - SUPPLIES_CONSUME_MULT) * 100) + "%"});


        String msg1 = null;
        String msg2 = null;
        String msg3 = null;
        String msg4 = null;

        if (haveEnemy){
            msg1 = "附近有敌人";
        }else{
            msg1 = "附近没有发现敌人";
        }

        if (isInHyperspace){
            msg2 = "当前在超空间中漂泊";
        }else{
            msg2 = "当前在星系中漂泊";
        }

        if (zeroSpeed){
            msg3 = "当前速度为零";
        }else{
            msg3 = "当前速度不为零";
        }
        tooltip.addPara("检查现在的状态:" , pad, h, new String[]{});

        if (haveEnemy){
            tooltip.addPara("%s*", pad, h, new String[]{msg1});
            tooltip.addPara(" < %s* >", pad, NEGATIVE_REMINDER, enemy);
        }else{
            tooltip.addPara("%s*", pad, h, msg1);
        }
        tooltip.addPara("%s*", pad, h, new String[]{msg2});
        tooltip.addPara("%s*", pad, h, new String[]{msg3});

        if (haveEnemy || isInHyperspace || !zeroSpeed){
            msg4 = "当前不可休眠";
        }else{
            msg4 = "当前可以休眠";
        }

        tooltip.addPara("%s*", pad, h, new String[]{msg4});
        this.addIncompatibleToTooltip(tooltip, expanded);
    }

    public boolean hasTooltip() {
        return true;
    }

    public boolean showProgressIndicator() {
        return super.showProgressIndicator();
    }

    public boolean showActiveIndicator() {
        return this.isActive();
    }

    public void fleetJoinedBattle(BattleAPI battle) {
        if (!battle.isPlayerInvolved()) {
            this.deactivate();
        }
    }

    public boolean runWhilePaused() {
        return this.getFleet() != null && this.getFleet().isPlayerFleet();
    }


    public static enum SleepStatus{
        ON,
        OFF;

        private SleepStatus(){

        }
    }


}
