package data.plugin;

import com.fs.starfarer.api.PluginPick;
import com.fs.starfarer.api.campaign.*;

//TODO 待完善
public class RD_CampaignPlugin extends BaseCampaignPlugin {

    public RD_CampaignPlugin() {

    }

    public String getId() {
        return "RD_CampaignPlugin";
    }

    public boolean isTransient() {
        return true;
    }

    public PluginPick<InteractionDialogPlugin> pickInteractionDialogPlugin(SectorEntityToken interactionTarget) {
        return null;
    }

}
