package data.scripts.campaign.intel;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.ai.CampaignFleetAIAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.FleetEventListener;
import com.fs.starfarer.api.impl.campaign.MilitaryResponseScript;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.scripts.everyFrame.DawnActionResponseScript;
import org.apache.log4j.Logger;

import java.awt.*;
import java.util.Iterator;
import java.util.Set;

//TODO
@Deprecated
public class RD_DawnActionIntel extends BaseIntelPlugin implements EveryFrameScript, FleetEventListener {

    public static Logger logger = Global.getLogger(RD_DawnActionIntel.class);
    private static final float MAX_DURATION = 90.0f;
    protected MarketAPI market;
    protected LocationAPI location;
    protected float elapsedDays = 0f;
    protected float duration;
    protected float baseBounty;
    protected FactionAPI faction;
    protected FactionAPI enemyFaction;
    protected DawnActionResult latestResult;
//    protected DawnActionResponseScript script;
    protected MilitaryResponseScript script;

    private static final String CONFIG = "rd_dawn_action_bounty";


    public RD_DawnActionIntel(MarketAPI market) {
        this.duration = MAX_DURATION;
        this.baseBounty = 0.0f;
        this.enemyFaction = null;
        this.market = market;
        this.location = market.getContainingLocation();
        this.faction = Global.getSector().getFaction("republic_of_dawn");

        if (isActive()){
            this.baseBounty = Global.getSettings().getFloat(CONFIG);
            float marketSize = (float)market.getSize();
            this.baseBounty *= (marketSize + 5.0F) / 10.0F;
            float highStabilityMult = BaseMarketConditionPlugin.getHighStabilityBonusMult(market);
            highStabilityMult = 1.0F + (highStabilityMult - 1.0F) * 0.5F;
            this.baseBounty *= highStabilityMult;
            this.baseBounty = (float)((int)this.baseBounty);
            logger.info(String.format("市场方发布的赏金 [%s], %d 星币每艘护卫舰，为黎明共和国付出吧！", market.getName(), (int)this.baseBounty));
            this.updateLikelyCauseFaction();
            Global.getSector().getIntelManager().queueIntel(this);
            Global.getSector().getListenerManager().addListener(this);
//            DawnActionResponseScript.DawnActionResponseParams params = new DawnActionResponseScript.DawnActionResponseParams(CampaignFleetAIAPI.ActionType.HOSTILE, "dawn_action" + market.getId(), this.getFactionForUIColors(), market.getPrimaryEntity(), 0.75F, this.duration);
            MilitaryResponseScript.MilitaryResponseParams params = new MilitaryResponseScript.MilitaryResponseParams(CampaignFleetAIAPI.ActionType.HOSTILE, "dawn_action" + market.getId(), this.getFactionForUIColors(), market.getPrimaryEntity(), 0.75F, this.duration);
//            this.script = new DawnActionResponseScript(params);
            this.script = new MilitaryResponseScript(params);
            this.location.addScript(this.script);
        }else{
            this.endImmediately();
        }
    }


    public void reportMadeVisibleToPlayer() {
        if (!this.isEnding() && !this.isEnded()) {
            this.duration = Math.max(this.duration * 0.5F, Math.min(this.duration * 2.0F, MAX_DURATION));
        }

    }


    public MarketAPI getMarket() {
        return this.market;
    }


    public void reset() {
        this.elapsedDays = 0.0F;
        this.endingTimeRemaining = null;
        this.ending = null;
        this.ended = null;
        this.script.setElapsed(0.0F);
        if (!Global.getSector().getListenerManager().hasListener(this)) {
            Global.getSector().getListenerManager().addListener(this);
        }

    }



    private boolean isActive(){
        return "republic_of_dawn".equals(this.market.getFactionId());
    }

    private void updateLikelyCauseFaction() {
        int maxSize = 0;
        MarketAPI maxOther = null;
        Iterator var4 = Misc.getNearbyMarkets(this.market.getLocationInHyperspace(), 0.0F).iterator();

        while(var4.hasNext()) {
            MarketAPI other = (MarketAPI)var4.next();
            if (this.market.getFaction() != other.getFaction() && this.market.getFaction().isHostileTo(other.getFaction())) {
                int size = other.getSize();
                if (size > maxSize) {
                    maxSize = size;
                    maxOther = other;
                }
            }
        }

        if (maxOther != null) {
            this.enemyFaction = maxOther.getFaction();
        } else {
            this.enemyFaction = Global.getSector().getFaction("pirates");
        }

    }

    protected void advanceImpl(float amount) {
        float days = Global.getSector().getClock().convertToDays(amount);
        this.elapsedDays += days;
        boolean current;
        if (this.elapsedDays >= this.duration && !this.isDone()) {
            this.endAfterDelay();
            current = this.market.getContainingLocation() == Global.getSector().getCurrentLocation();
            this.sendUpdateIfPlayerHasIntel(new Object(), !current);
        } else if (this.faction != this.market.getFaction() || !this.market.isInEconomy()) {
            this.endAfterDelay();
            current = this.market.getContainingLocation() == Global.getSector().getCurrentLocation();
            this.sendUpdateIfPlayerHasIntel(new Object(), !current);
        }
    }

    public float getTimeRemainingFraction() {
        return 1.0F - this.elapsedDays / this.duration;
    }

    protected void notifyEnding() {
        super.notifyEnding();
        logger.info(String.format("市场方发布的黎明行动已经结束 [%s]", this.market.getName()));
        Global.getSector().getListenerManager().removeListener(this);
        this.location.removeScript(this.script);
    }

    public void reportFleetDespawnedToListener(CampaignFleetAPI fleet, CampaignEventListener.FleetDespawnReason reason, Object param) {
    }

    @Override
    public void reportBattleOccurred(CampaignFleetAPI campaignFleetAPI, CampaignFleetAPI campaignFleetAPI1, BattleAPI battleAPI) {

    }


    public boolean runWhilePaused() {
        return false;
    }



    public void createIntelInfo(TooltipMakerAPI info, ListInfoMode mode) {
        Color c = this.getTitleColor(mode);
        info.addPara(this.getName(), c, 0.0F);
    }

    public String getSortString() {
        return "黎明行动";
    }

    public String getName() {
        String name = this.market.getName();
        StarSystemAPI system = this.market.getStarSystem();
        if (system != null) {
            name = system.getBaseName();
        }

        return this.isEnding() ? "行动结束 - " + name : "黎明行动 - " + name;
    }

    public FactionAPI getFactionForUIColors() {
        return this.faction;
    }

    public String getSmallDescriptionTitle() {
        return this.getName();
    }



    public String getIcon() {
        return Global.getSettings().getSpriteName("intel", "dawn_action");
    }

    public Set<String> getIntelTags(SectorMapAPI map) {
        Set<String> tags = super.getIntelTags(map);
        tags.add("DawnAction");
        tags.add(this.faction.getId());
        return tags;
    }

    public SectorEntityToken getMapLocation(SectorMapAPI map) {
        return this.market.getPrimaryEntity();
    }



    public static class DawnActionResult{
        public int payment;
        public float fraction;
        public ReputationActionResponsePlugin.ReputationAdjustmentResult rep;

        public DawnActionResult(int payment, float fraction, ReputationActionResponsePlugin.ReputationAdjustmentResult rep) {
            this.payment = payment;
            this.fraction = fraction;
            this.rep = rep;
        }
    }

}
