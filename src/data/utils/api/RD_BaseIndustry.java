package data.utils.api;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;

import java.util.HashSet;
import java.util.Set;

public class RD_BaseIndustry extends BaseIndustry {

    protected static final String DESC = "殖民地规模";
    protected static final String FACTION_ID = "republic_of_dawn";
    protected static final float MIN_REP = 50.0f;
    protected int size = 0;
    protected int stabilityValue = 0;

    protected static final Set<String> WHITE_LIST = new HashSet<>();

    static{
        WHITE_LIST.add("republic_of_dawn");
        WHITE_LIST.add("persean");
        WHITE_LIST.add("independent");
    }

    public RD_BaseIndustry() {

    }

    @Override
    public void apply() {
        super.apply(true);
        this.init();
    }

    protected boolean isInWhiteList(){
        return WHITE_LIST.contains(this.market.getFactionId());
    }

    protected boolean isAuthorized(){
        SectorAPI sector = Global.getSector();

        float relationship = sector.getFaction("player").getRelationship(FACTION_ID);

        SectorEntityToken entity = sector.getEntityById("solomon_planet_augustus");
        if (entity != null){
            String factionId = entity.getFaction().getId();
            //奥古斯丁是
            return relationship >= MIN_REP && isRDOccupied() && FACTION_ID.equals(factionId);
        }else{
            return false;
        }

    }

    protected void init(){
        this.size = this.market.getSize();
        this.stabilityValue = (int) this.market.getStabilityValue();
    }

    protected boolean isRDOccupied(){
        return this.market.getFactionId().equals(FACTION_ID);
    }
}
