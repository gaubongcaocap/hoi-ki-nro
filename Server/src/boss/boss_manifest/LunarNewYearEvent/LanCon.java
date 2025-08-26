package boss.boss_manifest.LunarNewYearEvent;

/*
 *
 *
 * @author NTD
 */

import boss.*;
import static boss.BossType.TET_EVENT;
import java.util.ArrayList;
import java.util.List;
import map.Zone;
import player.Player;
import server.Client;
import services.EffectSkillService;
import services.Service;
import services.func.ChangeMapService;
import utils.Logger;
import utils.Util;

public class LanCon extends Boss {

    private long st;
    private int timeLeave;
    private long lastTimeAtt;
    private long playerId;
    private boolean afk;

    public LanCon() throws Exception {
        super(TET_EVENT, BossID.LAN_CON - Util.nextInt(1000000), BossesData.LAN_CON);
    }

    @Override
    public void joinMap() {
        if (zoneFinal != null) {
            joinMapByZone(zoneFinal);
            // this.notifyJoinMap();
            this.changeStatus(BossStatus.CHAT_S);
            this.wakeupAnotherBossWhenAppear();
            return;
        }
        if (this.zone == null) {
            if (this.parentBoss != null) {
                this.zone = parentBoss.zone;
            } else if (this.lastZone == null) {
                this.zone = getMapJoin();
            } else {
                this.zone = this.lastZone;
            }
        }
        if (this.zone != null) {
            try {
                // Chọn một khu vực ngẫu nhiên trong danh sách zones của bản đồ
                int zoneid = Util.nextInt(0, this.zone.map.zones.size() - 1);
                this.zone = this.zone.map.zones.get(zoneid);  // Chuyển boss đến khu vực ngẫu nhiên

                // Thực hiện thay đổi bản đồ
                ChangeMapService.gI().changeMap(this, this.zone, Util.nextInt(100, 500), this.zone.map.yPhysicInTop(this.location.x,
                        this.location.y - 24));
                this.changeStatus(BossStatus.ACTIVE);
                st = System.currentTimeMillis();
                timeLeave = Util.nextInt(1000000, 3000000);
            } catch (Exception e) {
                Logger.error(this.data[0].getName() + ": Lỗi đang tiến hành REST\n");
                this.changeStatus(BossStatus.REST);
            }
        } else {
            Logger.error(this.data[0].getName() + ": Lỗi map đang tiến hành RESPAWN\n");
            this.changeStatus(BossStatus.RESPAWN);
        }
    }

    @Override
    public void chatM() {
        if (this.data[this.currentLevel].getTextM().length == 0) {
            return;
        }
        if (!Util.canDoWithTime(this.lastTimeChatM, this.timeChatM)) {
            return;
        }
        String textChat = this.data[this.currentLevel].getTextM()[Util.nextInt(0, this.data[this.currentLevel].getTextM().length - 1)];
        int prefix = Integer.parseInt(textChat.substring(1, textChat.lastIndexOf("|")));
        textChat = textChat.substring(textChat.lastIndexOf("|") + 1);
        this.chat(prefix, textChat);
        this.lastTimeChatM = System.currentTimeMillis();
        this.timeChatM = Util.nextInt(3000, 20000);
    }

    @Override
    public void autoLeaveMap() {
        if (Util.canDoWithTime(st, timeLeave)) {
            Player pl = Client.gI().getPlayer(playerId);
            if (pl == null || !this.zone.equals(pl.zone)) {
                this.leaveMapNew(); 
            }
        }
    }

    @Override
    public void leaveMap() {
        ChangeMapService.gI().exitMap(this);
        this.lastZone = null;
        this.playerId = -1;
        this.lastTimeRest = System.currentTimeMillis();
        this.changeStatus(BossStatus.REST);
    }

    @Override
    public void attack() {
        if (Util.canDoWithTime(this.lastTimeAttack, 250)) {
            this.lastTimeAttack = System.currentTimeMillis();
            try {
                Player pl = getPlayerAttack();
                if (pl == null || pl.location == null || pl.isDie()) {
                    return;
                }
                int dis = Util.getDistance(this, pl);
                if (dis > 450) {
                    move(pl.location.x - 24, pl.location.y);
                } else if (dis > 100) {
                    int dir = (this.location.x - pl.location.x < 0 ? 1 : -1);
                    int move = Util.nextInt(50, 100);
                    move(this.location.x + (dir == 1 ? move : -move), pl.location.y);
                } else {
                    if (Util.canDoWithTime(lastTimeAtt, 30000) && this.nPoint.hp < this.nPoint.hpMax) {
                        if (Util.isTrue(10, 100)) {
                            Service.gI().moveFast(pl, this.location.x, this.location.y);
                            pl.setDie();
                            Service.gI().sendThongBao(pl, "Bạn đã bị Lân con húc chết!");
                        }
                        lastTimeAtt = System.currentTimeMillis();
                    }
                }
            } catch (Exception ex) {
            }
        }
    }

    @Override
    public void afk() {
        if (Util.canDoWithTime(this.lastTimeAttack, 500)) {
            this.lastTimeAttack = System.currentTimeMillis();
            Player pl = Client.gI().getPlayer(playerId);
            if (pl == null || pl.zone == null) {
                return;
            }
            if (pl.haveReward) {
                pl.haveReward = false;
                this.leaveMap();
                return;
            }
            if (this.zone.equals(pl.zone)) {
                int dis = Util.getDistance(this, pl);
                if (dis <= 300) {
                    if (dis > 50) {
                        int dir = (this.location.x - pl.location.x < 0 ? 1 : -1);
                        int move = Util.nextInt(50, 100);
                        move(this.location.x + (dir == 1 ? move : -move), pl.location.y);
                        st = System.currentTimeMillis();
                    }
                    afk = false;
                    pl.canReward = true;
                } else {
                    afk = true;
                    pl.canReward = false;
                }
            } else if (!afk) {
                if (pl.changeMapVIP) {
                    pl.changeMapVIP = false;
                    pl.canReward = false;
                    afk = true;
                    return;
                }
                ChangeMapService.gI().changeMap(this, pl.zone, pl.location.x + Util.nextInt(-10, 10), pl.location.y);
            }
        }
    }

    @Override
    public synchronized long injured(Player plAtt, long damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(100, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }
            damage = this.nPoint.subDameInjureWithDeff(damage);
            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = 1;
            }
            if (damage > 50_000) {
                damage = 50_000;
            }

            // Kiểm tra nếu damage >= HP hoặc người chơi chat "thang"
            if (damage >= this.nPoint.hp) {
                this.changeToTypeNonPK();
                this.playerId = Math.abs(plAtt.id);
                Service.gI().chat(plAtt, "Đi thôi lân con!");
                this.nPoint.hp = this.nPoint.hpMax;
                this.changeStatus(BossStatus.AFK);
                return 0;
            }
            this.nPoint.subHP(damage);
            return (int) damage;
        } else {
            return 0;
        }
    }

    @Override
    public void reward(Player plKill) {
    }
}
