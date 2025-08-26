package boss.boss_manifest.SnakeWay;

/*
 *
 *
 * @author NTD
 */

import EMTI.Functions;
import consts.ConstPlayer;
import boss.*;
import static boss.BossType.PHOBANCDRD;
import clan.Clan;
import network.Message;
import java.util.List;
import map.ItemMap;
import map.Zone;
import player.Player;
import services.EffectSkillService;
import services.MapService;
import services.PlayerService;
import skill.Skill;
import services.Service;
import services.func.ChangeMapService;
import utils.Util;

public class SAIBAMEN extends Boss {

    private Clan clan;
    private int idboss;

    private static final int[][] FULL_GALICK = new int[][]{{Skill.GALICK, 1}, {Skill.GALICK, 2}, {Skill.GALICK, 3}, {Skill.GALICK, 4}, {Skill.GALICK, 5}, {Skill.GALICK, 6}, {Skill.GALICK, 7}};

    public SAIBAMEN(Zone zone, Clan clan, int id, long dame, long hp) throws Exception {
        super(PHOBANCDRD, BossID.SAIBAMEN - id, new BossData(
                "Số " + id,
                ConstPlayer.XAYDA,
                new short[]{642, 643, 644, -1, -1, -1},
                ((10000 + dame)),
                new long[]{((500000 + hp))},
                new int[]{144},
                (int[][]) Util.addArray(FULL_GALICK),
                new String[]{},
                new String[]{},
                new String[]{},
                60
        ));
        this.zone = zone;
        this.clan = clan;
        this.idboss = id;
    }

    @Override
    public void reward(Player plKill) {
        if (Util.isTrue(100, 100)) {
            ItemMap it = new ItemMap(this.zone, 19, 1, this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), plKill.id);
            Service.gI().dropItemMap(this.zone, it);
        }
    }

    @Override
    public void afk() {
        if (this.clan == null || this.clan.ConDuongRanDoc == null) {
            this.leaveMap();
            return;
        }
        if (this.idboss == 1) {
            Player pl = getPlayerAttack();
            if (pl == null || pl.isDie()) {
                return;
            }

            this.changeToTypePK();
            Functions.sleep(1500);
            this.changeStatus(BossStatus.ACTIVE);
        } else if (9 - this.clan.ConDuongRanDoc.getNumBossAlive() == this.idboss) {
            this.changeStatus(BossStatus.ACTIVE);
        }
    }

    @Override
    public void joinMap() {
        ChangeMapService.gI().changeMap(this, this.zone, 420 + (this.idboss * 15), 342);
        this.changeStatus(BossStatus.AFK);
    }

    @Override
    public void die(Player plKill) {
        if (plKill != null) {
            reward(plKill);
        }
        this.changeStatus(BossStatus.DIE);
    }

    @Override
    public void leaveMap() {
        ChangeMapService.gI().exitMap(this);
        this.lastZone = null;
        this.lastTimeRest = System.currentTimeMillis();
        this.changeStatus(BossStatus.REST);
        SnakeWayManager.gI().removeBoss(this);
        this.dispose();
    }

    @Override
    public synchronized long injured(Player plAtt, long damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }
            damage = this.nPoint.subDameInjureWithDeff(damage );
            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = damage;
            }
            this.nPoint.subHP(damage);
            if (isDie()) {
                setBom(plAtt);
            }
            return damage;
        } else {
            return 0;
        }
    }

    @Override
    public void setBom(Player plAtt) {
        if (!this.playerSkill.prepareTuSat) {
            for (Player pl : this.zone.getPlayers()) {
                Service.gI().sendThongBao(pl, pl.name + " coi chừng đấy!");
            }
            Service.gI().chat(plAtt, "Trời ơi muộn mất rồi");
            EffectSkillService.gI().startStun(plAtt, System.currentTimeMillis(), 3500);
            //gồng tự sát
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
            this.nPoint.hp = 0;
        }
        Service.gI().chat(this, "He he he");
        while (this.playerSkill.prepareTuSat) {
            if (Util.canDoWithTime(this.playerSkill.lastTimePrepareTuSat, 2500)) {
//                EffectSkillService.gI().removeStun(plAtt);
                this.playerSkill.prepareTuSat = false;
                setDie(this);
                die(plAtt);
                long dame = (long) this.nPoint.hpMax * 100L;
                List<Player> playersMap = null;
                playersMap = this.zone.getNotBosses();
                if (!MapService.gI().isMapOffline(this.zone.map.mapId)) {
                    for (Player pl : playersMap) {
                        if (!this.equals(pl)) {
                            pl.injured(this, dame, false, false);
                            PlayerService.gI().sendInfoHpMpMoney(pl);
                            Service.gI().Send_Info_NV(pl);
                        }
                    }
                }
            }
        }
    }
}
