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
import skill.Skill;
import utils.SkillUtil;

public class Mabu2H extends Boss {

    private long lastTimeEat;

    private long lastTimeUseSkill;
    private long timeUseSkill;
    public List<Player> maBuEat = new ArrayList<>();

    public Mabu2H() throws Exception {
        super(FINAL, BossID.MABU, BossesData.MABU, BossesData.SUPER_BU, BossesData.BU_TENK, BossesData.BU_HAN, BossesData.KID_BU);
    }

    @Override
    public void joinMap() {
        if (zoneFinal != null) {
            this.zone = zoneFinal;
        }
        ChangeMapService.gI().changeMap(this, this.zone, this.location.x, this.location.y);
        this.changeStatus(BossStatus.ACTIVE);
    }

    private void eatPlayersInTheMap() {
        int numPlayers = 0;
        for (Player pl : this.zone.getPlayers()) {
            if (Util.isTrue(1, 5)) {
                pl.isMabuHold = true;
                Service.gI().sendMabuEat(this, pl);
                this.maBuEat.add(pl);
                numPlayers++;
            }
        }
        if (numPlayers > 0) {
            this.chat("Măm măm");
        }
    }

    private void petrifyPlayersInTheMap() {
        for (Player pl : this.zone.getNotBosses()) {
            if (Util.isTrue(1, 5)) {
                this.chat("Úm ba la xì bùa");
                EffectSkillService.gI().setSocola(pl, System.currentTimeMillis(), 30000);
                Service.gI().Send_Caitrang(pl);
                ItemTimeService.gI().sendItemTime(pl, 4133, 30);
            }
        }
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
                if (Util.canDoWithTime(lastTimeEat, 10000)) {
                    eatPlayersInTheMap();
                    if (this.currentLevel == 0) {
                        petrifyPlayersInTheMap();
                    }
                    this.lastTimeEat = System.currentTimeMillis();
                }
                if (this.currentLevel > 0) {
                    if (Util.canDoWithTime(lastTimeUseSkill, timeUseSkill)) {
                        Service.gI().sendMabuAttackSkill(this);
                        lastTimeUseSkill = System.currentTimeMillis();
                        timeUseSkill = Util.nextInt(5000, 10000);
                        return;
                    }
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
      if (Util.isTrue(3, 100)) {
            ItemMap it = ItemService.gI().randQuanThanLinh(this.zone, 1, this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), plKill.id);
            Service.gI().dropItemMap(this.zone, it);
        }
    }

    @Override
    public synchronized long injured(Player plAtt, long damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(10, 100)) {
                this.chat("Xí hụt");
                return 0;
            }

            if (plAtt.isPl() && Util.isTrue(1, 5)) {
                plAtt.fightMabu.changePercentPoint((byte) 1);
            }

            damage = this.nPoint.subDameInjureWithDeff(damage);

            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = 1;
            }

            if (this.currentLevel == this.data.length - 1) {
                if (plAtt.playerSkill.skillSelect.template.id != Skill.QUA_CAU_KENH_KHI) {
                    damage = damage >= this.nPoint.hp ? 0 : damage;
                }
            }

            if (damage >= 30000000) {
                damage = 30000000 + Util.nextInt(-10000, 10000);
            }

            this.nPoint.subHP(damage);

            if (isDie()) {
                this.setDie(plAtt);
                Boss boss = FinalBossManager.gI().getBossById(BossID.SUPERBU, 128, this.zone.zoneId);
                if (boss != null) {
                    boss.changeStatus(BossStatus.DIE);
                }
                die(plAtt);
            }

            return damage;
        } else {
            return 0;
        }
    }

    @Override
    public void die(Player plKill) {
        if (plKill != null) {
            List<Player> pls = new ArrayList<>();
            List<Player> players = this.maBuEat;
            for (Player pl : players) {
                pls.add(pl);
            }
            for (Player pl : pls) {
                if (pl.zone != null && pl.zone.map.mapId == 128) {
                    ChangeMapService.gI().changeMap(pl, 127, this.zone.zoneId, -1, 312);
                }
            }
            players.clear();
            reward(plKill);
            ServerNotify.gI().notify(plKill.name + ": Đã tiêu diệt được " + this.name + " mọi người đều ngưỡng mộ.");
        }
        this.changeStatus(BossStatus.DIE);
    }

}
