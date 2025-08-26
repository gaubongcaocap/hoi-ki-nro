package boss.boss_manifest.SuperRank;

/*
 *
 *
 * @author NTD
 */

import boss.BossData;
import static boss.BossType.PHOBAN;
import java.util.ArrayList;
import java.util.List;
import player.Player;
import skill.Skill;

public class Rival extends SuperRank {

    public Rival(Player player, Player rival) throws Exception {
        super(PHOBAN, (int) -rival.id, bossData(rival));
        this.player = rival;
        this.playerAtt = player;
    }

    public static BossData bossData(Player player) {
        List<Skill> skillList = new ArrayList<>();
        for (byte i = 0; i < player.playerSkill.skills.size(); i++) {
            Skill skill = player.playerSkill.skills.get(i);
            if (skill.point > 0 && skill.template.id != Skill.TU_SAT && skill.template.id != Skill.QUA_CAU_KENH_KHI && skill.template.id != Skill.MAKANKOSAPPO && skill.template.id != Skill.TRI_THUONG) {
                skillList.add(skill);
            }
        }
        int[][] skillTemp = new int[skillList.size()][3];
        for (byte i = 0; i < skillList.size(); i++) {
            Skill skill = skillList.get(i);
            if (skill.point > 0 && skill.template.id != Skill.TU_SAT && skill.template.id != Skill.QUA_CAU_KENH_KHI && skill.template.id != Skill.MAKANKOSAPPO && skill.template.id != Skill.TRI_THUONG) {
                skillTemp[i][0] = skill.template.id;
                skillTemp[i][1] = skill.point;
                skillTemp[i][2] = skill.coolDown;
            }
        }
        return new BossData(
                player.name,
                player.gender,
                new short[]{player.getHead(), player.getBody(), player.getLeg(), player.getFlagBag(), player.getAura(), player.getEffFront()},
                player.nPoint.dameg,
                new long[]{player.nPoint.hpg},
                new int[]{113},
                skillTemp,
                new String[]{}, //text chat 1
                new String[]{}, //text chat 2
                new String[]{}, //text chat 3
                60
        );
    }

}
