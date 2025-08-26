package boss.boss_manifest.MajinBuu14H;

/*
 *
 *
 * @author NTD
 */

import boss.Boss;
import boss.FinalBossManager;
import boss.BossID;
import boss.BossStatus;
import boss.BossesData;
import static boss.BossType.FINAL;
import java.util.ArrayList;
import java.util.List;
import map.ItemMap;
import player.Player;
import server.Manager;
import services.*;
import utils.Util;

import java.util.Random;
import server.ServerNotify;
import services.func.ChangeMapService;
import utils.SkillUtil;

public class SuperBu extends Boss {

    private long lastTimeUseSkill;
    private long timeUseSkill;

    public SuperBu() throws Exception {
        super(FINAL, BossID.SUPERBU, BossesData.SUPER_BU_BUNG);
    }

    @Override
    public void joinMap() {
        if (zoneFinal != null) {
            this.zone = zoneFinal;
        }
        ChangeMapService.gI().changeMap(this, this.zone, -1, -1);
        this.changeStatus(BossStatus.ACTIVE);
    }

    @Override
    public void attack() {
        if (Util.canDoWithTime(this.lastTimeAttack, 100)) {
            this.lastTimeAttack = System.currentTimeMillis();
            try {
                Player pl = getPlayerAttack();
                if (pl == null || pl.isDie()) {
                    return;
                }
                if (Util.canDoWithTime(lastTimeUseSkill, timeUseSkill)) {
                    Service.gI().sendMabuAttackSkill(this);
                    lastTimeUseSkill = System.currentTimeMillis();
                    timeUseSkill = Util.nextInt(5000, 10000);
                    return;
                }
                this.playerSkill.skillSelect = this.playerSkill.skills.get(Util.nextInt(0, this.playerSkill.skills.size() - 1));
                if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
                    if (Util.isTrue(5, 20)) {
                        if (SkillUtil.isUseSkillChuong(this)) {
                            this.moveTo(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 200)), pl.location.y);
                        } else {
                            this.moveTo(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(10, 40)), pl.location.y);
                        }
                    }
                    SkillService.gI().useSkill(this, pl, null, -1, null);
                    checkPlayerDie(pl);
                } else {
                    if (Util.isTrue(1, 2)) {
                        this.moveToPlayer(pl);
                    }
                }
            } catch (Exception ex) {
            }
        }
    }

    @Override
    public void reward(Player plKill) {
        TaskService.gI().checkDoneTaskKillBoss(plKill, this);
        if (Util.isTrue(15, 100)) {
            ItemMap it = ItemService.gI().randDoTL(this.zone, 1, this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), -1);
            Service.gI().dropItemMap(this.zone, it);
        }
        if (Util.isTrue(3, 100)) {
            ItemMap it = ItemService.gI().randQuanThanLinh(this.zone, 1, this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), plKill.id);
            Service.gI().dropItemMap(this.zone, it);
        }
    }

    @Override
    public synchronized long injured(Player plAtt, long damage, boolean piercing, boolean isMobAttack) {
        Boss boss = FinalBossManager.gI().getBossById(BossID.MABU, 127, this.zone.zoneId);
        if (boss != null) {
            boss.injured(plAtt, damage, piercing, isMobAttack);
        }
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(10, 100)) {
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

            if (damage >= 30000000) {
                damage = 30000000 + Util.nextInt(-10000, 10000);
            }

            this.nPoint.subHP(damage);

            if (isDie()) {
                this.setDie(plAtt);
                die(plAtt);
            }

            return (int) damage;
        } else {
            return 0;
        }
    }

    @Override
    public void die(Player plKill) {
        if (plKill != null) {
            List<Player> pls = new ArrayList<>();
            Boss boss = FinalBossManager.gI().getBossById(BossID.MABU, 127, this.zone.zoneId);
            if (boss != null) {
                List<Player> players = ((Mabu2H) boss).maBuEat;
                for (Player pl : players) {
                    pls.add(pl);
                }
                for (Player pl : pls) {
                    if (pl.zone != null && pl.zone.map.mapId == 128) {
                        ChangeMapService.gI().changeMap(pl, 127, this.zone.zoneId, -1, 312);
                    }
                }
                players.clear();
            }
            reward(plKill);
            ServerNotify.gI().notify(plKill.name + ": Đã tiêu diệt được " + this.name + " mọi người đều ngưỡng mộ.");
        }
        this.changeStatus(BossStatus.DIE);
    }

}
