package boss.boss_manifest.Training;

/*
 *
 *
 * @author NTD
 */

import boss.BossID;
import boss.OtherBossManager;
import boss.BossStatus;
import boss.BossesData;
import static boss.BossType.PHOBAN;
import models.Training.TrainingService;
import player.Player;
import services.MapService;
import services.Service;
import services.func.ChangeMapService;
import utils.Util;

public class ThuongDe extends TrainingBoss {

    public long lastTimeJoinMap = System.currentTimeMillis();

    public ThuongDe(Player player) throws Exception {
        super(PHOBAN, BossID.THUONG_DE, BossesData.THUONG_DE);
        this.playerAtt = player;
    }

    @Override
    public void joinMap() {
        if (playerAtt.zone != null) {
            this.zone = playerAtt.zone;
            ChangeMapService.gI().changeMap(this, this.zone, 408, 436);
            this.changeStatus(BossStatus.CHAT_S);
        }
    }

    @Override
    public boolean chatS() {
        if (Util.canDoWithTime(lastTimeJoinMap, 4000)) {
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
                this.lastTimeChatS = System.currentTimeMillis();
                this.timeChatS = 3000;
                doneChatS = true;
            }
        }
        return false;
    }

    @Override
    public void afk() {
        if (Util.canDoWithTime(lastTimeAFK, 5000)) {
            this.changeStatus(BossStatus.LEAVE_MAP);
            if (!isPlayerDie) {
                ChangeMapService.gI().changeMap(playerAtt, MapService.gI().getMapCanJoin(playerAtt, 45, 0), 354, 408);
            }
        }
    }

    @Override
    public void leaveMap() {
        ChangeMapService.gI().exitMap(this);
        Player npc = TrainingService.gI().getNonInteractiveNPC(zone, (int) this.id);
        if (npc != null) {
            this.nPoint.hp = this.nPoint.hpMax;
            Service.gI().Send_Info_NV(this);
            this.goToPlayer(npc, false);
        } else {
            TrainingService.gI().luyenTapEnd(playerAtt, (int) this.id);
        }

        this.lastZone = null;
        this.lastTimeRest = System.currentTimeMillis();
        this.changeStatus(BossStatus.REST);
        OtherBossManager.gI().removeBoss(this);
        this.dispose();
    }
}
