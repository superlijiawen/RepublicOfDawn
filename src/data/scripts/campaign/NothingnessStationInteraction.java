package data.scripts.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.combat.EngagementResultAPI;

import java.awt.*;
import java.util.Map;

//TODO
public class NothingnessStationInteraction implements InteractionDialogPlugin {

    private InteractionDialogAPI dialog;
    private TextPanelAPI textPanel;
    private OptionPanelAPI options;
    private VisualPanelAPI visual;
    private CampaignFleetAPI playerFleet;
    private SectorEntityToken station;
    private static final Color HIGHLIGHT_COLOR = Global.getSettings().getColor("buttonShortcut");
    private EngagementResultAPI lastResult = null;
    private NothingnessStationInteraction.OptionId lastOptionMousedOver = null;


    @Override
    public void init(InteractionDialogAPI interactionDialogAPI) {
        this.dialog = dialog;
        this.textPanel = dialog.getTextPanel();
        this.options = dialog.getOptionPanel();
        this.visual = dialog.getVisualPanel();
        this.playerFleet = Global.getSector().getPlayerFleet();
        this.station = dialog.getInteractionTarget();
        this.visual.setVisualFade(0.25F, 0.25F);
        dialog.setOptionOnEscape("离开", NothingnessStationInteraction.OptionId.LEAVE);
        this.optionSelected((String)null, NothingnessStationInteraction.OptionId.INIT);
    }

    @Override
    public void optionSelected(String s, Object o) {

    }

    @Override
    public void optionMousedOver(String s, Object o) {

    }

    @Override
    public void advance(float v) {

    }

    @Override
    public void backFromEngagement(EngagementResultAPI engagementResultAPI) {

    }

    @Override
    public Object getContext() {
        return null;
    }

    @Override
    public Map<String, MemoryAPI> getMemoryMap() {
        return null;
    }

    private static enum OptionId {
        INIT,
        INIT_NO_TEXT,
        TRADE_CARGO,
        TRADE_SHIPS,
        REFIT,
        REPAIR_ALL,
        LEAVE;

        private OptionId() {
        }
    }
}
