package boss.boss_manifest.GoldenFrieza;

/*
 *
 *
 * @author NTD
 */
import boss.*;
import consts.ConstPlayer;
import item.Item;
import java.util.List;
import map.ItemMap;
import player.Player;
import services.EffectSkillService;
import services.Service;
import utils.Util;

import mob.Mob;
import network.Message;
import services.MapService;
import services.PlayerService;
import services.SkillService;
import services.func.ChangeMapService;
import utils.SkillUtil;
import utils.TimeUtil;

public class GoldenFrieza extends Boss {

    private int status;
    private long lastStatusChange;
    private int timeChanges;
    private boolean callDeathBeam;

    public GoldenFrieza() throws Exception {
        super(BossID.GOLDEN_FRIEZA, BossesData.GOLDEN_FRIEZA);
    }

    @Override
    public void reward(Player plKill) {
        if (Util.isTrue(10, 100)) { // 10% rơi item 1560, số lượng 1
            Service.gI().dropItemMap(this.zone, new ItemMap(this.zone, 1560, 1, this.location.x + 5,
                    this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24), plKill.id));
        } else if (Util.isTrue(20, 90)) { // 20% rơi item 457, số lượng ngẫu nhiên 10-15
            Service.gI().dropItemMap(this.zone, new ItemMap(this.zone, 457, Util.nextInt(10, 15), this.location.x + 5,
                    this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24), plKill.id));
        } else if (Util.isTrue(20, 70)) { // 20% rơi ngẫu nhiên item từ 555-567, số lượng 1
            int randomItem = Util.nextInt(555, 567);
            Service.gI().dropItemMap(this.zone, new ItemMap(this.zone, randomItem, 1, this.location.x + 5,
                    this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24), plKill.id));
        } else { // 50% giữ nguyên cách cũ
            ItemMap it = new ItemMap(this.zone, 629, 1, this.location.x + 5,
                    this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24), plKill.id);
            it.options.add(new Item.ItemOption(50, Util.nextInt(20, 30)));
            it.options.add(new Item.ItemOption(77, Util.nextInt(20, 40)));
            it.options.add(new Item.ItemOption(103, Util.nextInt(20, 40)));
            it.options.add(new Item.ItemOption(14, Util.nextInt(5, 8)));
            it.options.add(new Item.ItemOption(153, Util.nextInt(20, 65)));
            it.options.add(new Item.ItemOption(93, 7));
            Service.gI().dropItemMap(this.zone, it);
            for (int i = 0; i < Util.nextInt(3, 10); i++) {
                Service.gI().dropItemMap(this.zone, new ItemMap(zone, 76, Util.nextInt(20000, 30000),
                        this.location.x + i * 10, this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24), plKill.id));
            }
        }
    }

    @Override
    public void active() {
        super.active();
    }

    @Override
    public synchronized long injured(Player plAtt, long damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 1000)) {
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
            if (damage > 20_000_000) {
                damage = 20_000_000;
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
    public void autoLeaveMap() {
        if (!TimeUtil.is21H()) {
            this.leaveMap();
        }
    }

    @Override
    public void joinMap() {
        if (TimeUtil.is21H()) {
            this.name = this.data[this.currentLevel].getName() + " " + Util.nextInt(1, 100);
            super.joinMap();
            if (this.zone != null) {
                for (Mob mob : this.zone.mobs) {
                    mob.injured(this, 99999999, true);
                }
                this.zone.isGoldenFriezaAlive = true;
            }
        } else {
            this.changeStatus(BossStatus.REST);
        }
    }

    @Override
    public void attack() {
        if (Util.canDoWithTime(this.lastTimeAttack, 100) && this.typePk == ConstPlayer.PK_ALL) {
            this.lastTimeAttack = System.currentTimeMillis();
            if (Util.canDoWithTime(lastStatusChange, timeChanges)) {
                callDeathBeam = false;
                timeChanges = Util.nextInt(5000, 10000);
                lastStatusChange = System.currentTimeMillis();
                status = Util.nextInt(3);
            }
            try {
                switch (status) {
                    case 0:
                        setBom();
                        timeChanges = 5000;
                        break;
                    case 1:
                        if (callDeathBeam) {
                            boolean checkDeathBeamDie = true;
                            for (Boss boss : this.bossAppearTogether[this.currentLevel]) {
                                if (boss.bossStatus != BossStatus.REST) {
                                    checkDeathBeamDie = false;
                                }
                            }
                            if (checkDeathBeamDie) {
                                status = 2;
                                lastStatusChange = System.currentTimeMillis();
                                timeChanges = 30000;
                            }
                            return;
                        }
                        callDeathBeam = true;
                        for (Boss boss : this.bossAppearTogether[this.currentLevel]) {
                            if (boss.bossStatus == BossStatus.REST) {
                                boss.changeStatus(BossStatus.RESPAWN);
                            }
                        }
                        timeChanges = 15000;
                        break;
                    default:
                        timeChanges = 30000;
                        Player pl = getPlayerAttack();
                        if (pl == null || pl.isDie()) {
                            return;
                        }
                        this.playerSkill.skillSelect = this.playerSkill.skills.get(Util.nextInt(0, this.playerSkill.skills.size() - 1));
                        if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
                            if (Util.isTrue(5, 20)) {
                                if (SkillUtil.isUseSkillChuong(this)) {
                                    this.moveTo(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 200)),
                                            Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 70));
                                } else {
                                    this.moveTo(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(10, 40)),
                                            Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 50));
                                }
                            }
                            SkillService.gI().useSkill(this, pl, null, -1, null);
                            checkPlayerDie(pl);
                        } else {
                            if (Util.isTrue(1, 2)) {
                                this.moveToPlayer(pl);
                            }
                        }
                        break;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void setBom() {
        if (this.playerSkill.prepareTuSat) {
            return;
        }
        new Thread(() -> {
            if (!this.playerSkill.prepareTuSat) {
                this.playerSkill.prepareTuSat = true;
                this.playerSkill.lastTimePrepareTuSat = System.currentTimeMillis();
                Message msg;
                try {
                    msg = new Message(-45);
                    msg.writer().writeByte(7);
                    msg.writer().writeInt((int) this.id);
                    msg.writer().writeShort(104);
                    msg.writer().writeShort(2000);
                    Service.gI().sendMessAllPlayerInMap(this, msg);
                    msg.cleanup();
                } catch (Exception e) {
                }
            }

            while (this.playerSkill.prepareTuSat && this.zone != null) {
                if (Util.canDoWithTime(this.playerSkill.lastTimePrepareTuSat, 2500)) {
                    this.playerSkill.prepareTuSat = false;
                    List<Player> playersMap = this.zone.getNotBosses();
                    if (!MapService.gI().isMapOffline(this.zone.map.mapId)) {
                        for (Player pl : playersMap) {
                            if (!this.equals(pl)) {
                                pl.injured(this, 2_100_000_000, true, false);
//                            pl.setDie();
                                PlayerService.gI().sendInfoHpMpMoney(pl);
                                Service.gI().Send_Info_NV(pl);
                            }
                        }
                    }
                }
            }
        }).start();
    }

    @Override
    public void leaveMap() {
        this.zone.isGoldenFriezaAlive = false;
        ChangeMapService.gI().exitMap(this);
        this.lastZone = null;
        this.lastTimeRest = System.currentTimeMillis();
        this.changeStatus(BossStatus.REST);
    }
}
