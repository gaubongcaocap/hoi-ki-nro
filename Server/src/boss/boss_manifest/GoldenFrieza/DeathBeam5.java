package boss.boss_manifest.GoldenFrieza;

/*
 *
 *
 * @author NTD
 */

import boss.Boss;
import boss.BossID;
import boss.BossStatus;
import boss.BossesData;
import static boss.BossType.SKILLSUMMONED;
import player.Player;
import services.MapService;
import services.Service;
import services.func.ChangeMapService;
import utils.Util;

public class DeathBeam5 extends Boss {

    private long st;

    private Player playerAtt;
    public Player playerUseSkill;
    private boolean leaveMap;

    private long lastTimeMove;
    private boolean playerKill;
    private long lastTimeUpdate;

    public DeathBeam5() throws Exception {
        super(SKILLSUMMONED, BossID.DEATH_BEAM_5, BossesData.DEATH_BEAM);
    }

    @Override
    public void joinMap() {
        st = System.currentTimeMillis();
        this.zone = this.parentBoss.zone;
        ChangeMapService.gI().changeMap(this, this.zone,
                this.parentBoss.location.x + Util.nextInt(-100, 100), 300);
        Service.gI().sendFlagBag(this);
        playerAtt = this.getPlayerAttack();
        leaveMap = false;
        playerKill = false;
        this.changeStatus(BossStatus.ACTIVE);
    }

    @Override
    public void active() {
        this.attack();
    }

    @Override
    public void afk() {
        if (Util.canDoWithTime(lastTimeUpdate, 3000)) {
            if (Util.isTrue(1, 2)) {
                this.playerAtt = this.getPlayerAttack();
                this.changeStatus(BossStatus.ACTIVE);
            }
            lastTimeUpdate = System.currentTimeMillis();
        }
    }

    @Override
    public void autoLeaveMap() {
        if (!leaveMap) {
            if (Util.canDoWithTime(st, 15000)) {
                leaveMap = true;
            }
        } else {
            if (Util.canDoWithTime(lastTimeMove, 500)) {
                this.location.y -= 30;
                this.moveTo(this.location.x, this.location.y);
                if (this.location.y < 0) {
                    this.leaveMap();
                }

            }
        }
    }

    @Override
    public void moveTo(int x, int y) {
        this.location.x = x;
        this.location.y = y;
        MapService.gI().sendPlayerMove(this);
    }

    @Override
    public void moveToPlayer(Player pl) {
        if (pl.location != null) {
            int move = Math.abs(this.location.x - playerAtt.location.x);
            int dir = this.location.x - playerAtt.location.x > 0 ? -1 : 1;
            int x = this.location.x + dir * 30;
            if (move < 30) {
                x = pl.location.x;
            }
            moveTo(x, pl.location.y);
        }
    }

    @Override
    public void attack() {
        if (leaveMap) {
            return;
        }

        if (playerAtt == null || playerAtt.location == null || playerAtt.isDie() || !playerAtt.zone.equals(this.zone)) {
            this.changeStatus(BossStatus.AFK);
            return;
        }
        if (Util.canDoWithTime(lastTimeMove, 500)) {
            this.moveToPlayer(playerAtt);
        }
        if (Math.abs(this.location.x - playerAtt.location.x) < 5 && Util.canDoWithTime(st, 500)) {
            Service.gI().setPos(this, playerAtt.location.x, playerAtt.location.y);
            if (!playerKill) {
                setDie();
                playerKill = true;
                leaveMap = true;
            }
        }
    }

    @Override
    public void leaveMap() {
        ChangeMapService.gI().exitMap(this);
        this.lastZone = null;
        this.lastTimeRest = System.currentTimeMillis();
        this.changeStatus(BossStatus.REST);
    }

    @Override
    public synchronized long injured(Player plAtt, long damage, boolean piercing, boolean isMobAttack) {
        return 0;
    }

    @Override
    public void setDie() {
//        playerAtt.setDie();
        playerAtt.injured(this.playerUseSkill, 2_100_000_000, true, false);
    }
}
