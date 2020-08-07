package data.utils.api;

import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.StarSystemAPI;

public interface RD_GlobalApi {

    public static final String DEFAULT_FACTION_ID = "republic_of_dawn";

    public void generate(SectorAPI sector);
    public void addPlanets(StarSystemAPI system, PlanetAPI star, String factionId);
    public void addPlanets(StarSystemAPI system, PlanetAPI star, PlanetAPI c_star, String factionId);

}
