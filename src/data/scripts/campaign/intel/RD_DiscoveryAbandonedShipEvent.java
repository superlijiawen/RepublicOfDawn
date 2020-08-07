package data.scripts.campaign.intel;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.scripts.campaign.intel.bar.RD_BarGossipEvent;

import java.awt.*;
import java.util.*;

public class RD_DiscoveryAbandonedShipEvent extends BaseIntelPlugin {

    private SectorEntityToken entity;
    private RD_BarGossipEvent event;


    public RD_DiscoveryAbandonedShipEvent(SectorEntityToken entity, RD_BarGossipEvent event) {
        this.entity = entity;
        this.event = event;

        Global.getSector().addScript(this);
    }

    public void createIntelInfo(TooltipMakerAPI info, ListInfoMode mode) {
        Color h = Misc.getHighlightColor();
        float pad = 6.0f;
        info.addPara("前往 %s 星系一颗黑矮星探索", pad, h, "sirius");
    }

    public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
        Color h = Misc.getHighlightColor();
        float pad = 6.0f;
        info.addPara("前往 %s 星系一颗黑矮星探索", pad, h, "sirius");    }

    public void createLargeDescription(CustomPanelAPI panel, float width, float height) {
        TooltipMakerAPI desc = panel.createUIElement(width, height, true);
        Color h = Misc.getHighlightColor();
        float pad = 6.0f;

        desc.addPara("前往 %s 星系一颗黑矮星探索", pad, h, "sirius");
        panel.addUIElement(desc).inTL(0.0F, 0.0F);
    }


    public String getSortString() {
        return "Discovery";
    }

    public FactionAPI getFactionForUIColors() {
        return this.event.getPerson().getFaction();
    }

    public String getSmallDescriptionTitle() {
        return "探索遗迹";
    }

    public String getIcon() {
//        return this.event.getPerson().getPortraitSprite();
        return Global.getSettings().getSpriteName("intel", "rd_balck_dwarf");
    }

    public Set<String> getIntelTags(SectorMapAPI map) {
        Set<String> tags = super.getIntelTags(map);
        tags.add("Accepted");
        tags.add(this.getFactionForUIColors().getId());
        return tags;
    }


    protected void notifyEnded() {
        super.notifyEnded();
        Global.getSector().removeScript(this);
    }
}
