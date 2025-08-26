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
import item.Item;
import map.ItemMap;
import map.Zone;
import player.Player;
import services.EffectSkillService;
import skill.Skill;
import services.Service;
import services.SkillService;
import services.func.ChangeMapService;
import utils.SkillUtil;
import utils.Util;

public class CADICH extends Boss {

    private Clan clan;

    private long lastTimeSkill;
    private long lastTimeSkillHD;
    private boolean gongBienKhi;
    private boolean bienKhi;

    public CADICH(Zone zone, Clan clan, long dame, long hp) throws Exception {
        super(PHOBANCDRD, BossID.CADICH, new BossData(
                "Cađích",
                ConstPlayer.XAYDA,
                new short[]{645, 646, 647, -1, -1, -1},
                ((10000 + dame)),
                new long[]{((500000 + hp))},
                new int[]{144},
                new int[][]{
                    {Skill.GALICK, 7, 1000},
                    {Skill.MASENKO, 1, 1000},
                    {Skill.ANTOMIC, 1, 1000},
                    {Skill.KAMEJOKO, 4, 1000},
                    {Skill.BIEN_KHI, 1, 1000}},//skill
                new String[]{"|-1|Vĩnh biệt chú mày nhé, Na đíc"},
                new String[]{},
                new String[]{"|-1|Tốt lắm phi thuyền đã đến đón ta"},
                60
        ));
        this.zone = zone;
        this.clan = clan;
    }

    @Override
    public void reward(Player plKill) {
        if (Util.isTrue(50, 100)) {
            ItemMap it = new ItemMap(this.zone, 459, 1, this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), plKill.id);
            it.options.add(new Item.ItemOption(112, 80));
            it.options.add(new Item.ItemOption(93, 90));
            it.options.add(new Item.ItemOption(20, Util.nextInt(10000)));
            Service.gI().dropItemMap(this.zone, it);
            ItemMap it2 = new ItemMap(this.zone, 706, 1, this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), plKill.id);
            Service.gI().dropItemMap(this.zone, it2);
        }
    }

    @Override
    public void afk() {
        if (this.clan == null || this.clan.ConDuongRanDoc == null) {
            this.leaveMap();
            return;
        }
        if (this.clan.ConDuongRanDoc.getNumBossAlive() < 2) {
            this.changeStatus(BossStatus.CHAT_S);
        }
    }

    @Override
    public void joinMap() {
        ChangeMapService.gI().changeMap(this, this.zone, 490, 312);
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
        ChangeMapService.gI().spaceShipArrive(this, (byte) 2, ChangeMapService.TENNIS_SPACE_SHIP);
        ChangeMapService.gI().exitMap(this);
        this.lastZone = null;
        this.lastTimeRest = System.currentTimeMillis();
        this.changeStatus(BossStatus.REST);
        SnakeWayManager.gI().removeBoss(this);
        if (this.clan.ConDuongRanDoc != null) {
            this.clan.ConDuongRanDoc.endCDRD = true;
        }
        this.dispose();
        this.clan = null;
    }

    @Override
    public synchronized long injured(Player plAtt, long damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }
            if (gongBienKhi) {
                return 0;
            }
            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = damage;
            }
            if (damage >= this.nPoint.hp) {
                this.effectSkill.removeSkillEffectWhenDie();
                this.changeToTypeNonPK();
                die(plAtt);
                return 0;
            }
            this.nPoint.subHP(damage);
            return damage;
        } else {
            return 0;
        }
    }

    @Override
    public void attack() {
        if (!gongBienKhi && !this.effectSkill.isCharging && Util.canDoWithTime(this.lastTimeAttack, 100) && this.typePk == ConstPlayer.PK_ALL) {
            this.lastTimeAttack = System.currentTimeMillis();
            try {
                Player pl = getPlayerAttack();
                if (pl == null || pl.isDie()) {
                    return;
                }
                if (this.nPoint.hp < this.nPoint.hpMax / 2 && !bienKhi) {
                    this.chat("Ha ha ha, ha ha ha");
                    this.bienKhi = true;
                    this.gongBienKhi = true;
                    EffectSkillService.gI().sendEffectMonkey(this);
                    Functions.sleep(2000);
                    this.chat("Thế nào " + pl.name + "? Mi đã thấy phép biến hình của người Xayda rồi chứ?");
                    this.gongBienKhi = false;
                    int timeMonkey = 100000;
                    this.effectSkill.isMonkey = true;
                    this.effectSkill.timeMonkey = timeMonkey;
                    this.effectSkill.lastTimeUpMonkey = System.currentTimeMillis();
                    this.effectSkill.levelMonkey = 1;
                    long hpmax = Util.maxIntValue(this.nPoint.hpMax * 2L);
                    this.nPoint.hpMax = hpmax;
                    this.nPoint.setHp(Util.maxIntValue(this.nPoint.hpMax));
                    EffectSkillService.gI().sendEffectMonkey(this);
                    Service.gI().Send_Caitrang(this);
                    Service.gI().point(this);
                    Service.gI().Send_Info_NV(this);
                    Service.gI().sendInfoPlayerEatPea(this);
                    return;
                }

                this.playerSkill.skillSelect = this.playerSkill.skills.get(Util.nextInt(0, 2));
                if (Util.canDoWithTime(this.lastTimeSkillHD, 3000)) {
                    this.playerSkill.skillSelect = this.playerSkill.skills.get(3);
                    this.lastTimeSkillHD = System.currentTimeMillis() + 99999999;
                }
                if (Util.isTrue(1, 20) && Util.canDoWithTime(lastTimeSkill, 10000)) {
                    EffectSkillService.gI().startStun(pl, System.currentTimeMillis(), 5000);
                    this.chat("Tuyệt chiêu hủy diệt của môn phái Xayda");
                    this.lastTimeSkill = System.currentTimeMillis();
                    this.lastTimeSkillHD = System.currentTimeMillis();
                    return;
                }
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
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
