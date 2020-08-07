package data.scripts.campaign.abilities;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.impl.campaign.CoreReputationPlugin;
import com.fs.starfarer.api.impl.campaign.abilities.BaseDurationAbility;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.utils.GlobalUtils;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * 技能
 * 伪装
 *
 * 能够伪装成其他势力的颜色
 *
 * buff
 * 伪装成其他势力的颜色，暂时设定为海盗
 *
 * debuff
 * 需要消耗一定量的补给，且伪装时间不能过长，如果附近100距离内出现舰队则伪装失败
 */
public class RD_Disguise extends BaseDurationAbility {

    private static final float MAX_DURATION_DAYS = 3f;

//    @Deprecated
//    private static Color originalColor = Global.getSector().getPlayerFaction().getColor();
//    @Deprecated
//    private static Color disguiseColor = Global.getSector().getFaction("pirates").getColor();

    private static boolean disguising = false;
    private static transient boolean isLeaked = false;
    private static Set<String> pirateEnemySet = new LinkedHashSet<>();
    private static Set<String> enemySet = new LinkedHashSet<>();

    public RD_Disguise(){

    }

    protected String getActivationText() {
        return "伪装";
    }


    @Override
    public boolean isUsable() {
        if (super.isUsable()){
            return !this.haveEnemyAside();
        }
        return false;
    }


    @Override
    protected void activateImpl() {
        this.entity.setTransponderOn(false);
    }

    @Override
    protected void applyEffect(float v, float v1) {
        if (!disguising){
            CampaignFleetAPI fleet = this.getFleet();
            if (fleet != null && fleet.isPlayerFleet()){
                adjustPiratesReputationAndPlayerColor(true);
                adjustOtherFactionsReputations(true);
                disguising = true;
            }
        }

        if (this.isLeaked()){
            isLeaked = true;
            this.deactivate();
        }
    }

    @Override
    protected void deactivateImpl() {
        this.cleanupImpl();
    }

    @Override
    protected void cleanupImpl() {
        CampaignFleetAPI fleet = this.getFleet();
        if (fleet != null && fleet.isPlayerFleet()){
            adjustPiratesReputationAndPlayerColor(false);
            adjustOtherFactionsReputations(false);
            disguising = false;

            if (isLeaked){
                isLeaked = false;
                GlobalUtils.addSystemPrompt("你的伪装行为已被暴露，伪装结束");
                return;
            }

            GlobalUtils.addSystemPrompt("你结束了伪装");
        }
    }

    public boolean hasTooltip() {
        return true;
    }

    public void fleetJoinedBattle(BattleAPI battle) {
        if (!battle.isPlayerInvolved()) {
            this.deactivate();
        }
    }


    @Override
    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded) {
        Color h = Misc.getHighlightColor();
        Color g = Misc.getGrayColor();
        float pad = 10.0F;

        tooltip.addTitle(this.spec.getName());
        LabelAPI title = tooltip.addTitle(this.spec.getName());
        title.setHighlightColor(g);

        tooltip.addPara("可以短时间伪装成海盗势力", pad);
        tooltip.addPara("条件是附近不能有敌人", pad);
        tooltip.addPara("伪装成功后，海盗将会认为你是他们中的一员，但是海盗的敌人可能会揍你", pad);
        tooltip.addPara("被靠近的敌人发现，将会暴露你的伪装行为，所以，即使伪装也请不要靠得太近", pad);
        this.addIncompatibleToTooltip(tooltip, expanded);
    }

    private boolean haveEnemyAside(){
        CampaignFleetAPI fleet = this.getFleet();
        if (fleet != null){
            float detectionRange = fleet.getDetectedRangeMod().getMult();
            GlobalUtils.addSystemPrompt("" + detectionRange);

            List<CampaignFleetAPI> fleets = Misc.getNearbyFleets(fleet, detectionRange);
            if (fleets.size() == 0){
                return false;
            }

            for (CampaignFleetAPI f : fleets) {
                if (f.getFaction().isHostileTo("player")){
                    return true;
                }
            }
        }

        return false;
    }

    private static void adjustPiratesReputationAndPlayerColor(boolean on){
        SectorAPI sector = Global.getSector();
        if (on){
//            sector.getPlayerFleet().
        }else {
            sector.adjustPlayerReputation(CoreReputationPlugin.RepActions.COMBAT_NORMAL, "pirates");
        }
    }

    private static void adjustOtherFactionsReputations(boolean on){
        if (on){
            List<FactionAPI> factions = Global.getSector().getAllFactions();
            if (factions.size() == 0){
                return;
            }

            for (FactionAPI faction : factions) {
                if (!faction.isPlayerFaction()){
                    if (!faction.getId().equals("pirates")){
                        if (faction.isShowInIntelTab()){
                            if (faction.isHostileTo("pirates")){
                                pirateEnemySet.add(faction.getId());
                                Global.getSector().adjustPlayerReputation(CoreReputationPlugin.RepActions.COMBAT_AGGRESSIVE, faction.getId());
                            }
                        }
                    }
                }
            }

            enemySet.addAll(pirateEnemySet);
            if (Global.getSector().getPlayerFaction().isHostileTo("pirates")){
                enemySet.add("pirates");
            }

        }else{
            if (pirateEnemySet.size() != 0){
                for (String factionId : pirateEnemySet) {
                    Global.getSector().adjustPlayerReputation(CoreReputationPlugin.RepActions.COMBAT_NORMAL, factionId);
                }
            }
        }

    }

    private boolean isLeaked(){
        CampaignFleetAPI fleet = this.getFleet();
        if (fleet != null && fleet.isPlayerFleet()){
            if (enemySet.size() == 0){
                return false;
            }else {
                List<CampaignFleetAPI> fleets = Misc.getNearbyFleets(fleet, fleet.getDetectedRangeMod().getMult() / 5);
                for (CampaignFleetAPI f : fleets) {
                    if (enemySet.contains(f.getFaction().getId())){
                        isLeaked = true;
                        return true;
                    }
                }
            }
        }

        return false;
    }

}
