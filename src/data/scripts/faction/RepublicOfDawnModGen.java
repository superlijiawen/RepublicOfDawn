package data.scripts.faction;

import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorGeneratorPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.shared.SharedData;
import data.scripts.world.RD_Annacleon;
import data.scripts.world.RD_Sirius;
import data.scripts.world.RD_Solomon;
import data.scripts.world.RD_Ton615;

public class RepublicOfDawnModGen implements SectorGeneratorPlugin {

    @Override
    public void generate(SectorAPI sector) {

        FactionAPI republic_of_dawn = sector.getFaction("republic_of_dawn");

        //Generate your system 星系
        new RD_Solomon().generate(sector);
        new RD_Annacleon().generate(sector);
        new RD_Sirius().generate(sector);
        new RD_Ton615().generate(sector);

        //Add faction to bounty system 赏金
        SharedData.getData().getPersonBountyEventData().addParticipatingFaction("republic_of_dawn");

        this.setRelationship(republic_of_dawn);
        this.setModRelationship(republic_of_dawn);
    }

    private void setRelationship(FactionAPI factionId){

        //set relationship 关系
        factionId.setRelationship(Factions.HEGEMONY, -0.65f);
        factionId.setRelationship(Factions.TRITACHYON, 0.35f);
        factionId.setRelationship(Factions.INDEPENDENT, 0.35f);
        factionId.setRelationship(Factions.PERSEAN, 0.65f);
        factionId.setRelationship(Factions.DIKTAT, -0.25f);
        factionId.setRelationship(Factions.LIONS_GUARD, -0.25f);
        factionId.setRelationship(Factions.LUDDIC_CHURCH, -0.35f);
        factionId.setRelationship(Factions.LUDDIC_PATH, -0.55f);
        factionId.setRelationship(Factions.PIRATES, -0.55f);
        factionId.setRelationship(Factions.REMNANTS, -0.50f);
        factionId.setRelationship(Factions.PLAYER, 0f);

    }


    private void setModRelationship(FactionAPI factionId){

        factionId.setRelationship("approlight", -0.10F);//趋光议会
        factionId.setRelationship("blackrock_driveyards", -0.15F);//黑石船坞
        factionId.setRelationship("cabal", 0.15F);//星光结社
        factionId.setRelationship("dassault_mikoyan", -0.10F);//达索米高扬
        factionId.setRelationship("diableavionics", 0.10F);//暗影船坞
        factionId.setRelationship("exigency", -0.10F);
        factionId.setRelationship("famous_bounty", 0.0F);//ibb
        factionId.setRelationship("immortallight", -0.50F);//血腥无政府主义者
        factionId.setRelationship("interstellarimperium", -0.50F);//星际帝国
        factionId.setRelationship("junk_pirates", -0.10F);//垃圾海盗
        factionId.setRelationship("ORA", 0.35F);//外环带联盟
        factionId.setRelationship("pack", 0.25F);//pack
        factionId.setRelationship("SCY", 0.25F);//世界树
        factionId.setRelationship("syndicate_asp", 0.25F);//asp
        factionId.setRelationship("sun_ice", 0.0F);
        factionId.setRelationship("sun_ici", 0.0F);
        factionId.setRelationship("tiandong", 0.10F);//田东
        factionId.setRelationship("shadow_industry", -0.10F);//暗影重建局
        factionId.setRelationship("fob", 0.35F);//博尔肯基金会
        factionId.setRelationship("neutrinocorp", 0.10F);//中微子
        factionId.setRelationship("mayorate", 0.10f);//城邦结合体
        factionId.setRelationship("al_ars", -0.50f);//救济协会
        factionId.setRelationship("HMI", -0.10f);//HMI
        factionId.setRelationship("fang", -0.55f);//尖牙氏族
        factionId.setRelationship("draco", -0.55f);//德拉科集团
        factionId.setRelationship("brighton", 0.25f);//布莱顿联盟
        factionId.setRelationship("valkyrian", -0.15f);//女武神组织
        factionId.setRelationship("sylphon", 0.25f);//希尔芬研究所
        factionId.setRelationship("ae_ixbattlegroup", -0.25f);//第九军团
        factionId.setRelationship("Gudalanmu", 0.10f);//古达兰慕
        factionId.setRelationship("templars", -0.55f);//圣殿骑士团

    }
}
