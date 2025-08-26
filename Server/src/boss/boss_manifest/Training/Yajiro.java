package boss.boss_manifest.Training;

/*
 *
 *
 * @author NTD
 */

import boss.BossID;
import boss.BossStatus;
import boss.BossesData;
import static boss.BossType.PHOBAN;
import player.Player;
import services.func.ChangeMapService;
import utils.Util;

public class Yajiro extends TrainingBoss {

    public Yajiro(Player player) throws Exception {
        super(PHOBAN, BossID.YAJIRO, BossesData.YAJIRO);
        this.playerAtt = player;
    }

    @Override
    public void joinMap() {
        if (playerAtt.zone != null) {
            this.zone = playerAtt.zone;
            ChangeMapService.gI().changeMap(this, this.zone, 320, 408);
            this.changeStatus(BossStatus.CHAT_S);
        }
    }

    @Override
    public void afk() {
        if (Util.canDoWithTime(lastTimeAFK, 15000)) {
            this.changeStatus(BossStatus.LEAVE_MAP);
        }
    }

    @Override
    public boolean chatS() {
        if (Util.canDoWithTime(lastTimeChatS, timeChatS)) {
            if (this.doneChatS) {
                return true;
            }
            String textChat = this.data[this.currentLevel].getTextS()[playerAtt.isThachDau ? 1 : 0];
            int prefix = Integer.parseInt(textChat.substring(1, textChat.lastIndexOf("|")));
            textChat = textChat.substring(textChat.lastIndexOf("|") + 1);
            if (!this.chat(prefix, textChat)) {
                return false;
            }
            this.moveToPlayer(playerAtt);
            this.lastTimeChatS = System.currentTimeMillis();
            this.timeChatS = 100;
            doneChatS = true;
        }
        return false;
    }
}
