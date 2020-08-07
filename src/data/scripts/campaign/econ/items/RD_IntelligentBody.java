package data.scripts.campaign.econ.items;

import com.fs.starfarer.api.campaign.CargoTransferHandlerAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.campaign.impl.items.BaseSpecialItemPlugin;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;

/**
 * 智子
 * 作用未知
 */

//TODO 待完善
public class RD_IntelligentBody extends BaseSpecialItemPlugin {
    public RD_IntelligentBody() {

    }

    public int getPrice(MarketAPI market, SubmarketAPI submarket) {
        return super.getPrice(market, submarket);
    }

    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, CargoTransferHandlerAPI transferHandler, Object stackSource) {
        super.createTooltip(tooltip, expanded, transferHandler, stackSource, false);

        float pad = 3.0F;
        float opad = 10.0F;
        float small = 5.0F;
        Color h = Misc.getHighlightColor();
        Color g = Misc.getGrayColor();
        Color b = Misc.getButtonTextColor();

    }

}