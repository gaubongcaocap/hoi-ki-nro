package boss.boss_manifest.Commeson;

/*
 *
 *
 * @author NTD
 */

import boss.Boss;
import boss.BossData;
import boss.BossManager;
import boss.BossStatus;
import consts.ConstPlayer;
import player.Player;
import server.ServerNotify;
import services.PlayerService;
import services.Service;
import services.SkillService;
import services.func.ChangeMapService;
import utils.Util;

public class PhanThan extends Boss {

    private Player playerAtt;
    private long timeJoinMap;
    protected long timeOutMap;

    public PhanThan(Player player, BossData bossData, int timeOutMap) throws Exception {
        super(Util.createIdBossClone((int) player.id) - 9999, bossData);
        this.playerAtt = player;
        this.isCopy = true;
        this.timeOutMap = timeOutMap;
    }

    @Override
    public void reward(Player plKill) {
    }

    @Override
    public void active() {
    }
    
    @Override
    public void update() {
        super.update();
        followMaster(60);
        PlayerService.gI().changeAndSendTypePK(this, ConstPlayer.NON_PK);
        if (timeJoinMap > 0) {
            if (Util.canDoWithTime(timeJoinMap, timeOutMap)) {
                leaveMap();
            }
        }
        if (super.zone != null && super.zone.findPlayerByID(playerAtt.id) == null) {
            leaveMap();
        }
    }
    
    private void followMaster(int dis) {
        if (super.zone != null) {
            if (super.zone.findPlayerByID(playerAtt.id) != null) {
                int mX = super.zone.findPlayerByID(playerAtt.id).location.x;
                int mY = super.zone.findPlayerByID(playerAtt.id).location.y;
                int disX = this.location.x - mX;
                if (Math.sqrt(Math.pow(mX - this.location.x, 2) + Math.pow(mY - this.location.y, 2)) >= dis) {
                    if (disX < 0) {
                        this.location.x = mX - 50;
                    } else {
                        this.location.x = mX + 50;
                    }
                    this.location.y = mY;
                    PlayerService.gI().playerMove(this, this.location.x, this.location.y);
                }
            }
        }
    }

    @Override
    public void joinMap() {
        this.zone = this.playerAtt.zone;
        ChangeMapService.gI().changeMap(this, this.zone,
                this.playerAtt.location.x + Util.nextInt(-200, 200), this.playerAtt.location.y);
        this.changeStatus(BossStatus.CHAT_S);
    }
    
    @Override
    public void attack() {
        try {
            this.playerSkill.skillSelect = this.playerSkill.skills.get(Util.nextInt(0, this.playerSkill.skills.size() - 1));

            if (this.playerSkill.skillSelect != null) {
                if (idSkillPlayer != -1) {
                    if (playertarget != null) {
                        SkillService.gI().useSkill(this, playertarget, null, -1, null);
                        playertarget = null;
                    } else if (mobTarget != null) {
                        SkillService.gI().useSkill(this, null, mobTarget, -1, null);
                        mobTarget = null;
                    }
                    idSkillPlayer = -1;
                }
            }
        } catch (Exception ex) {
        }
    }

    @Override
    public void die(Player plKill) {
        this.changeStatus(BossStatus.DIE);
    }

    @Override
    public void leaveMap() {
        ChangeMapService.gI().exitMap(this);
        this.lastZone = null;
        this.lastTimeRest = System.currentTimeMillis();
        this.changeStatus(BossStatus.REST);
        BossManager.gI().removeBoss(this);
        this.dispose();
    }
}
