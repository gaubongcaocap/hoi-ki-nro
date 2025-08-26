package boss.boss_manifest.MajinBuu12H;

/*
 *
 *
 * @author NTD
 */

import boss.Boss;
import boss.BossID;
import boss.BossStatus;
import boss.BossesData;
import boss.AppearType;
import static boss.BossType.FINAL;
import consts.ConstPlayer;
import item.Item;
import map.ItemMap;
import player.Player;
import server.Manager;
import services.Service;
import utils.Util;

import java.util.Random;
import models.MajinBuu.MajinBuuService;
import services.EffectSkillService;
import services.ItemService;
import services.ItemTimeService;
import services.SkillService;
import services.TaskService;
import services.func.ChangeMapService;
import utils.SkillUtil;

public class Mabu extends Boss {

    private long lastTimePetrify;

    private int percent;

    public Mabu() throws Exception {
        super(FINAL, BossID.MABU_12H, BossesData.MABU_12H);
    }

    @Override
    public void reward(Player plKill) {
        if (plKill.isPl()) {
            plKill.goHome = true;
            plKill.timeGohome = 30;
        }
        for (int i = 0; i < Util.nextInt(2, 3); i++) {
            ItemMap itemMap = new ItemMap(zone, 521, 1, this.location.x + (Util.nextInt(-50, 50) * i), this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), plKill.id);
            int param = plKill.fightMabu.pointPercent + 30;
            itemMap.options.add(new Item.ItemOption(1, param));
            Service.gI().dropItemMap(this.zone, itemMap);
        }
        plKill.fightMabu.changePoint((byte) 25);
        TaskService.gI().checkDoneTaskKillBoss(plKill, this);
        if (Util.isTrue(3, 100)) {
            ItemMap it = ItemService.gI().randQuanThanLinh(this.zone, 1, this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), plKill.id);
            Service.gI().dropItemMap(this.zone, it);
        }
    }

    @Override
    public void joinMap() {
        if (zoneFinal != null) {
            this.zone = zoneFinal;
        }
        ChangeMapService.gI().changeMap(this, this.zone, Util.nextInt(300, 400), 336);
        this.changeStatus(BossStatus.CHAT_S);
        MajinBuuService.gI().getNpcBabiday(this.zone).npcChat(this.zone, "Mabư ! Hãy theo lệnh ta, giết hết bọn chúng đi");
    }

    private void petrifyPlayersInTheMap() {
        for (Player pl : this.zone.getNotBosses()) {
            if (Util.isTrue(1, 10)) {
                EffectSkillService.gI().setIsStone(pl, 22000);
            } else if (Util.isTrue(1, 5)) {
                this.chat("Úm ba la xì bùa");
                EffectSkillService.gI().setSocola(pl, System.currentTimeMillis(), 30000);
                Service.gI().Send_Caitrang(pl);
                ItemTimeService.gI().sendItemTime(pl, 4133, 30);
            }
        }
    }

    @Override
    public void attack() {
        if (Util.canDoWithTime(this.lastTimeAttack, 100) && this.typePk == ConstPlayer.PK_ALL) {
            if (Util.canDoWithTime(lastTimePetrify, 30000)) {
                petrifyPlayersInTheMap();
                this.lastTimePetrify = System.currentTimeMillis();
            }
            this.lastTimeAttack = System.currentTimeMillis();
            try {
                Player pl = getPlayerAttack();
                if (pl == null || pl.isDie()) {
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
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void autoLeaveMap() {
    }

    @Override
    public void rest() {
        int nextLevel = this.currentLevel + 1;
        if (nextLevel >= this.data.length) {
            nextLevel = 0;
        }
        if (this.data[nextLevel].getTypeAppear() == AppearType.DEFAULT_APPEAR
                && Util.canDoWithTime(lastTimeRest, secondsRest * 1000)) {
            this.changeStatus(BossStatus.RESPAWN);
        }
        long currentTimeMillis = System.currentTimeMillis();
        long elapsedTime = currentTimeMillis - lastTimeRest;

        this.percent = (int) (elapsedTime * 100 / ((secondsRest - 3) * 1000));
        if (percent <= 100) {
            Service.gI().SendMabu(this.zoneFinal, this.percent);
        }
    }

    @Override
    public synchronized long injured(Player plAtt, long damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(20, 100)) {
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

            if (damage >= 50000000) {
                damage = 50000000 + Util.nextInt(-10000, 10000);
            }

            this.nPoint.subHP(damage);

            if (isDie()) {
                this.setDie(plAtt);
                die(plAtt);
            }

            return damage;
        } else {
            return 0;
        }
    }

    @Override
    public void leaveMap() {
        ChangeMapService.gI().exitMap(this);
        this.lastZone = null;
        this.lastTimeRest = System.currentTimeMillis();
        this.changeStatus(BossStatus.REST);
        for (Boss boss : this.bossAppearTogether[this.currentLevel]) {
            boss.changeStatus(BossStatus.RESPAWN);
        }
    }
}
