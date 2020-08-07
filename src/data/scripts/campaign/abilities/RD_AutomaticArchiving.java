package data.scripts.campaign.abilities;

import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.abilities.BaseDurationAbility;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.utils.GlobalUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 技能
 * 自动封存
 *
 * 自动对CR低于30的舰船进行封存
 */
public class RD_AutomaticArchiving extends BaseDurationAbility {

    private static final float CR_TOLERANCE = 0.3f;
    private static final Color POSITIVE_REMINDER = new Color(0xFF7FA8);
    private static final Color NEGATIVE_REMINDER = new Color(0xFF0016);
    private static boolean done = false;

    @Deprecated
    private static boolean success = false;

    @Override
    public boolean isUsable() {
        if (!super.isUsable()){
            return false;
        }else if (this.getFleet() == null){
            return false;
        }else if (!this.getFleet().isPlayerFleet()){
            return false;
        }else {
            return true;
        }
    }


    @Override
    protected void activateImpl() {
    }

    @Override
    protected void applyEffect(float v, float v1) {
        if (!done){
            this.autoArchiving();
            done = true;
            this.deactivate();
        }
    }

    @Override
    protected void deactivateImpl() {
        this.cleanupImpl();
    }

    @Override
    protected void cleanupImpl() {
        success = false;
        done = false;
    }

    public boolean hasTooltip() {
        return true;
    }

    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded){
        Color h = Misc.getHighlightColor();
        Color g = Misc.getGrayColor();
        float pad = 10.0F;

        tooltip.addTitle(this.spec.getName());
        LabelAPI title = tooltip.addTitle(this.spec.getName());
        title.setHighlightColor(g);

        tooltip.addPara("自动封存cr低于 %s* 的舰船", pad, h, (int)(CR_TOLERANCE * 100) + "%");
    }

    private void autoArchiving() {
        CampaignFleetAPI fleet = this.getFleet();
        if (fleet != null){
            float maxCapacity = fleet.getCargo().getMaxCapacity();//最大货仓容积
            float currentCapacity = fleet.getCargo().getSpaceUsed();//当前使用货仓容量
            float maxCrew = fleet.getCargo().getMaxPersonnel();//最大船员容量
            float currentCrew = (float)fleet.getCargo().getCrew();//当前船员容量
            float maxFuel = fleet.getCargo().getMaxFuel();//最大燃料容量
            float currentFuel = fleet.getCargo().getFuel();//当前燃料容量

            float tempCapacity = maxCapacity;
            float tempCrew = maxCrew;
            float tempFuel = maxFuel;

            List<FleetMemberAPI> fleets = fleet.getFleetData().getMembersListCopy();

            List<FleetMemberAPI> needList = new ArrayList<>();
            for (FleetMemberAPI f : fleets) {
                float cr = f.getRepairTracker().getCR();
                float baseCR = f.getRepairTracker().getBaseCR();
                float maxCR = f.getRepairTracker().getMaxCR();

                if (cr <= CR_TOLERANCE){
                    needList.add(f);
                    tempCapacity -= f.getCargoCapacity();
                    tempCrew -= f.getMaxCrew();
                    tempFuel -= f.getFuelCapacity();
                }

//                GlobalUtils.addSystemPrompt(cr + "" + 1);
//                GlobalUtils.addSystemPrompt(baseCR + "" + 2);
//                GlobalUtils.addSystemPrompt(maxCR + "" + 3);
            }

            if (needList.size() == 0){
                success = false;
                GlobalUtils.addSystemPrompt("没有船可以封存", NEGATIVE_REMINDER);
                return;
            }

            if (tempCapacity <= currentCapacity){
                success = false;
                GlobalUtils.addSystemPrompt("自动封存失败，当前货仓空间不充裕，请手动进行封存操作", NEGATIVE_REMINDER);
                return;
            }

            if (tempCrew <= currentCrew){
                success = false;
                GlobalUtils.addSystemPrompt("自动封存失败，当前船员空间不充裕，请手动进行封存操作", NEGATIVE_REMINDER);
                return;
            }

            if (tempFuel <= currentFuel){
                success = false;
                GlobalUtils.addSystemPrompt("自动封存失败，当前燃料空间不充裕，请手动进行封存操作", NEGATIVE_REMINDER);
                return;
            }


            //对船进行封存操作
            for (FleetMemberAPI f : needList) {
                f.getRepairTracker().setMothballed(true);
                GlobalUtils.addSystemPrompt("自动封存了" + f.getShipName(), POSITIVE_REMINDER);
            }

            success = true;
        }
    }
}
