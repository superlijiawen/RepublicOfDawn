package data.scripts.campaign.terrain;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.abilities.EmergencyBurnAbility;
import com.fs.starfarer.api.impl.campaign.terrain.HyperStormBoost;
import com.fs.starfarer.api.impl.campaign.terrain.HyperspaceTerrainPlugin;
import com.fs.starfarer.api.impl.combat.BattleCreationPluginImpl;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.WeightedRandomPicker;

import java.awt.*;
import java.util.Iterator;
import java.util.List;

//TODO 类星体风暴
public class RD_QuasarNebulaTerrainPlugin extends HyperspaceTerrainPlugin implements BattleCreationPluginImpl.NebulaTextureProvider {

    public static float STORM_MIN_STRIKE_DAMAGE = 0.33F;
    public static float STORM_MAX_STRIKE_DAMAGE = 0.99F;

    public RD_QuasarNebulaTerrainPlugin(){
        super();
    }


    @Override
    public void applyEffect(SectorEntityToken entity, float days) {
        super.applyEffect(entity, days);
    }

    @Override
    public int getNumMapSamples() {
        return 10;
    }

    @Override
    public Color getRenderColor() {
        return Color.GRAY;
    }

    @Override
    protected void applyStormStrikes(HyperspaceTerrainPlugin.CellStateTracker cell, CampaignFleetAPI fleet, float days) {
        if (cell.flicker != null && cell.flicker.getWait() > 0.0F) {
            cell.flicker.setNumBursts(0);
            cell.flicker.setWait(0.0F);
            cell.flicker.newBurst();
        }

        if (cell.flicker != null && cell.flicker.isPeakFrame()) {
            fleet.addScript(new HyperStormBoost(cell, fleet));
            String key = "$stormStrikeTimeout";
            MemoryAPI mem = fleet.getMemoryWithoutUpdate();
            if (!mem.contains(key)) {
                mem.set(key, true, (float)((double)STORM_MIN_TIMEOUT + (double)(STORM_MAX_TIMEOUT - STORM_MIN_TIMEOUT) * Math.random()));
                List<FleetMemberAPI> members = fleet.getFleetData().getMembersListCopy();
                if (!members.isEmpty()) {
                    float totalValue = 0.0F;

                    FleetMemberAPI member;
                    for(Iterator var9 = members.iterator(); var9.hasNext(); totalValue += member.getStats().getSuppliesToRecover().getModifiedValue()) {
                        member = (FleetMemberAPI)var9.next();
                    }

                    if (totalValue > 0.0F) {
                        float strikeValue = totalValue * STORM_DAMAGE_FRACTION * (0.5F + (float)Math.random() * 0.5F);
                        float ebCostThresholdMult = 4.0F;
                        WeightedRandomPicker<FleetMemberAPI> picker = new WeightedRandomPicker();
                        WeightedRandomPicker<FleetMemberAPI> preferNotTo = new WeightedRandomPicker();
                        Iterator var13 = members.iterator();

                        FleetMemberAPI member2;
                        float suppliesPerDep;
                        float strikeDamage;
                        while(var13.hasNext()) {
                            member2 = (FleetMemberAPI)var13.next();
                            suppliesPerDep = 1.0F;
                            if (member2.isMothballed()) {
                                suppliesPerDep *= 0.1F;
                            }

                            strikeDamage = EmergencyBurnAbility.getCRCost(member2, fleet);
                            if (strikeDamage * ebCostThresholdMult > member2.getRepairTracker().getCR()) {
                                preferNotTo.add(member2, suppliesPerDep);
                            } else {
                                picker.add(member2, suppliesPerDep);
                            }
                        }

                        if (picker.isEmpty()) {
                            picker.addAll(preferNotTo);
                        }

                        member2 = (FleetMemberAPI)picker.pick();
                        if (member2 != null) {
                            float crPerDep = member2.getDeployCost();
                            suppliesPerDep = member2.getStats().getSuppliesToRecover().getModifiedValue();
                            if (suppliesPerDep > 0.0F && crPerDep > 0.0F) {
                                strikeDamage = crPerDep * strikeValue / suppliesPerDep;
                                if (strikeDamage < STORM_MIN_STRIKE_DAMAGE) {
                                    strikeDamage = STORM_MIN_STRIKE_DAMAGE;
                                }

                                float resistance = member2.getStats().getDynamic().getValue("corona_resistance");
                                strikeDamage *= resistance;
                                if (strikeDamage > STORM_MAX_STRIKE_DAMAGE) {
                                    strikeDamage = STORM_MAX_STRIKE_DAMAGE;
                                }

                                float currCR = member2.getRepairTracker().getBaseCR();
                                float crDamage = Math.min(currCR, strikeDamage);
                                float ebCost = EmergencyBurnAbility.getCRCost(member2, fleet);
                                if (currCR >= ebCost * ebCostThresholdMult) {
                                    crDamage = Math.min(currCR - ebCost * 1.5F, crDamage);
                                }

                                if (crDamage > 0.0F) {
                                    member2.getRepairTracker().applyCREvent(-crDamage, "hyperstorm", "类星体风暴袭击");
                                }

                                float hitStrength = member2.getStats().getArmorBonus().computeEffective(member2.getHullSpec().getArmorRating());
                                hitStrength *= strikeDamage / crPerDep;
                                member2.getStatus().applyDamage(hitStrength);
                                if (member2.getStatus().getHullFraction() < 0.01F) {
                                    member2.getStatus().setHullFraction(0.01F);
                                }

                                if (fleet.isPlayerFleet()) {
                                    Global.getSector().getCampaignUI().addMessage(member2.getShipName() + " 遭受到类星体风暴的破坏", Misc.getNegativeHighlightColor());
                                    Global.getSector().getCampaignUI().showHelpPopupIfPossible("chmHyperStorm");
                                }

                            }
                        }
                    }
                }
            }
        }
    }
}
