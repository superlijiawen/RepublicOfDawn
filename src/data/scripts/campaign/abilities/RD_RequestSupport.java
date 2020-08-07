package data.scripts.campaign.abilities;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FleetAssignment;
import com.fs.starfarer.api.campaign.RepLevel;
import com.fs.starfarer.api.impl.campaign.abilities.BaseDurationAbility;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.campaign.fleet.CampaignFleet;
import data.utils.GlobalUtils;
import sun.org.mozilla.javascript.internal.ast.Assignment;

import java.awt.*;
import java.util.*;
import java.util.List;

import static com.fs.starfarer.api.campaign.FleetAssignment.*;

/**
 * 技能
 * 请求支援
 *
 * buff
 * 向附近的友军求援，好感度必须达到50才会有友军来帮助
 *
 * 消耗
 * 暂无
 *
 */
public class RD_RequestSupport extends BaseDurationAbility {

    private static final Color POSITIVE_REMINDER = new Color(0xFF35C3);
    private static final Color NEGATIVE_REMINDER = new Color(0xFF0016);
    private static final float EFFECTIVE_TIME = 5f;
    private static Set<FleetAssignment> assignments_whiteList = new HashSet<>();

    private static float currentDay = 0.0f;
    private static float tempDay = 0.0f;
    private List<CampaignFleetAPI> allies = new LinkedList<>();
    private CampaignFleetAPI oneFleet = null;
    private CampaignFleetAPI twoFleet = null;
    private boolean done = false;
    private boolean success = false;

    static {
        Collections.addAll(assignments_whiteList,
                PATROL_SYSTEM);
    }

    public RD_RequestSupport(){

    }

    protected String getActivationText() {
        return "呼叫援军";
    }


    @Override
    public boolean isUsable() {
        return true;
    }

    @Override
    protected void activateImpl() {
        CampaignFleetAPI fleet = this.getFleet();
        if (fleet != null){
            this.checkFleetsNearby();
            if (this.allies.size() == 0){
                GlobalUtils.addSystemPrompt("你发出了求援信号，可惜附近没有可能会帮助你的援军", NEGATIVE_REMINDER);
                this.done = true;
                this.success = false;
            }else{
                currentDay = Global.getSector().getClock().getDay();

                //必定有一支舰队会前来支援你
                int size = this.allies.size();
                int index = new Random().nextInt(size);
                CampaignFleetAPI f = this.allies.get(index);
                f.clearAssignments();
                f.addAssignment(FOLLOW, fleet, 5f, "正在向你靠近");
                this.oneFleet = f;
                GlobalUtils.addSystemPrompt("你发出了求援信号" + f.getFaction().getDisplayName() + "的援军正在向你赶来", POSITIVE_REMINDER);

                this.done = true;
                this.success = true;

                this.allies.remove(index);

                //如果附近存在多支舰队，则有33%几率会有第二支舰队前来支援你
                if (this.allies.size() != 0){
                    if (Math.random() >= 0.66f){
                        int r= new Random().nextInt(this.allies.size());
                        CampaignFleetAPI f2 = this.allies.get(r);
                        f2.clearAssignments();
                        f2.addAssignment(FOLLOW, fleet, 5f, "正在向你靠近");
                        twoFleet = f2;
                        GlobalUtils.addSystemPrompt("你发出了求援信号" + f2.getFaction().getDisplayName() + "的援军正在向你赶来", POSITIVE_REMINDER);
                    }
                }
            }
        }

    }

    @Override
    protected void applyEffect(float v, float v1) {
        if (this.done){
            if (!this.success){
                this.done = false;
                this.deactivate();
            }else{
                tempDay = Global.getSector().getClock().getDay();

                if ((tempDay - currentDay) >= EFFECTIVE_TIME){
                    this.done = false;
                    this.deactivate();
                }
            }
        }
    }

    @Override
    protected void deactivateImpl() {

    }

    @Override
    protected void cleanupImpl() {

        if (this.success){
            GlobalUtils.addSystemPrompt("5天期限过去了，援军将会离开");
        }

        this.done = false;
        this.success = false;
        this.allies.clear();


        if (this.oneFleet != null && this.oneFleet.isAlive()){
            oneFleet.clearAssignments();
            oneFleet = null;
        }
        if (this.twoFleet != null && this.twoFleet.isAlive()){
            twoFleet.clearAssignments();
            twoFleet = null;
        }

    }

    public boolean hasTooltip() {
        return true;
    }


    @Override
    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded) {
        Color h = Misc.getHighlightColor();
        Color g = Misc.getGrayColor();
        float pad = 10.0F;

        tooltip.addTitle(this.spec.getName());
        LabelAPI title = tooltip.addTitle(this.spec.getName());
        title.setHighlightColor(g);

        String msg1 = "向附近的友军发出特定频率的信号波，等待友军支援";
        String msg2 = "注意，只有关系值达到 %s* 的势力才可能会帮助你，且该舰队当前不在执行任务";
        String msg3 = "发出信号波后，如果附近存在可以帮助你的舰队，将会有1~2支舰队前来支援";
        String msg4 = "发出信号波可能会被敌对势力截取，这将提前暴露你的敌对行为";
        tooltip.addPara(msg1, pad);
        tooltip.addPara(msg2, pad, h, "50");
        tooltip.addPara(msg3, pad);
        tooltip.addPara(msg4, pad);
        this.addIncompatibleToTooltip(tooltip, expanded);

    }


    private void checkFleetsNearby(){
        CampaignFleetAPI fleet = this.getFleet();
        if (fleet != null){
            float bonusMult = fleet.getDetectedRangeMod().getBonusMult();
            float flatBonus = fleet.getDetectedRangeMod().getFlatBonus();
            GlobalUtils.addSystemPrompt(bonusMult + "");//1.0
            GlobalUtils.addSystemPrompt(flatBonus + "");//1000.0
            float mult = fleet.getDetectedRangeMod().getMult();
            GlobalUtils.addSystemPrompt(mult + "");
            List<CampaignFleetAPI> fleets = Misc.getNearbyFleets(fleet, fleet.getDetectedRangeMod().getFlatBonus());

            if (fleets.size() == 0){
                return;
            }

            float rep = 0.0f;
            for (CampaignFleetAPI f : fleets) {
                if (f.getCurrentAssignment() == null){
                    rep = f.getFaction().getRelationship("player");
                    if (rep >= RepLevel.FRIENDLY.getMin()){
                        allies.add(f);
                    }
                }
            }
        }
    }

}
