package boss.boss_manifest.Broly;

/*
 *
 *
 * @author NTD
 */
import boss.Boss;
import boss.BrolyManager;
import boss.BossData;
import boss.BossID;
import boss.BossStatus;
import static boss.BossType.BROLY;
import boss.BossesData;
import consts.ConstPlayer;
import map.Zone;
import player.Player;
import services.PetService;
import services.PlayerService;
import services.Service;
import services.SkillService;
import services.func.ChangeMapService;
import skill.Skill;
import utils.SkillUtil;
import utils.Util;

public class SuperBroly extends Boss {

    public SuperBroly() throws Exception {
        super(BROLY, BossID.SUPER_BROLY, false, true, BossesData.SUPER_BROLY);
    }

    public SuperBroly(Zone zone, int x, int y) throws Exception {

        super(BROLY, BossID.SUPER_BROLY, false, true, new BossData(
                "Super Broly", // name
                ConstPlayer.XAYDA, // gender
                new short[] { 294, 295, 296, -1, -1, -1 }, // outfit {head, body, leg, bag, aura, eff}
                100, // dame
                new long[] { 1000 }, // hp
                new int[] { 13, 20, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38 }, // map join
                new int[][] {
                        { Skill.TAI_TAO_NANG_LUONG, 1, 1000 }, { Skill.TAI_TAO_NANG_LUONG, 2, 1000 },
                        { Skill.TAI_TAO_NANG_LUONG, 3, 1000 }, { Skill.TAI_TAO_NANG_LUONG, 4, 1000 },
                        { Skill.TAI_TAO_NANG_LUONG, 5, 1000 }, { Skill.TAI_TAO_NANG_LUONG, 6, 1000 },
                        { Skill.TAI_TAO_NANG_LUONG, 7, 1000 },
                        { Skill.DRAGON, 1, 1000 }, { Skill.DRAGON, 2, 1000 }, { Skill.DRAGON, 3, 1000 },
                        { Skill.DRAGON, 4, 1000 }, { Skill.DRAGON, 5, 1000 }, { Skill.DRAGON, 6, 1000 },
                        { Skill.DRAGON, 7, 1000 },
                        { Skill.DEMON, 1, 1000 }, { Skill.DEMON, 2, 1000 }, { Skill.DEMON, 3, 1000 },
                        { Skill.DEMON, 4, 1000 }, { Skill.DEMON, 5, 1000 }, { Skill.DEMON, 6, 1000 },
                        { Skill.DEMON, 7, 1000 },
                        { Skill.GALICK, 1, 1000 }, { Skill.GALICK, 2, 1000 }, { Skill.GALICK, 3, 1000 },
                        { Skill.GALICK, 4, 1000 }, { Skill.GALICK, 5, 1000 }, { Skill.GALICK, 6, 1000 },
                        { Skill.GALICK, 7, 1000 },
                        { Skill.KAMEJOKO, 1, 1000 }, { Skill.KAMEJOKO, 2, 1000 }, { Skill.KAMEJOKO, 3, 1000 },
                        { Skill.KAMEJOKO, 4, 1000 }, { Skill.KAMEJOKO, 5, 1000 }, { Skill.KAMEJOKO, 6, 1000 },
                        { Skill.KAMEJOKO, 7, 1000 },
                        { Skill.MASENKO, 1, 1000 }, { Skill.MASENKO, 2, 1000 }, { Skill.MASENKO, 3, 1000 },
                        { Skill.MASENKO, 4, 1000 }, { Skill.MASENKO, 5, 1000 }, { Skill.MASENKO, 6, 1000 },
                        { Skill.MASENKO, 7, 1000 },
                        { Skill.ANTOMIC, 1, 1000 }, { Skill.ANTOMIC, 2, 1000 }, { Skill.ANTOMIC, 3, 1000 },
                        { Skill.ANTOMIC, 4, 1000 }, { Skill.ANTOMIC, 5, 1000 }, { Skill.ANTOMIC, 6, 1000 },
                        { Skill.ANTOMIC, 7, 1000 }, }, // skill
                new String[] {}, // text chat 1
                new String[] { "|-1|Haha! ta sẽ giết hết các ngươi",
                        "|-1|Sức mạnh của ta là tuyệt đối",
                        "|-1|Vào hết đây!!!", }, // text chat 2
                new String[] { "|-1|Các ngươi giỏi lắm. Ta sẽ quay lại." }, // text chat 3
                600// type appear
        ));
        this.zone = zone;
        this.location.x = x;
        this.location.y = y;
    }

    @Override
    public void reward(Player plKill) {
        if (plKill.pet == null) {
            PetService.gI().createNormalPet(plKill, this.pet.gender, this.pet.typePet);
            Service.gI().sendThongBao(plKill, "Bạn đã nhận được đệ tử\n Vui lòng thoát game vào lại");
        }
    }

    @Override
    public void active() {
        super.active();
    }

    @Override
    public void joinMap() {
        this.name = "Super Broly " + Util.nextInt(10, 100);
        this.nPoint.hpMax = Util.nextInt(1_000_000, 16_070_777);
        this.nPoint.hp = this.nPoint.hpMax;
        this.nPoint.dame = this.nPoint.hpMax / 100;
        this.nPoint.crit = Util.nextInt(50);

        if (Util.isTrue(3, 5) && this.zone != null) {
            ChangeMapService.gI().changeMap(this, this.zone, this.location.x, this.location.y);
            this.changeStatus(BossStatus.CHAT_S);
            this.notifyJoinMap();
        } else {
            super.joinMap();
        }
        if (this.pet == null) {
            PetService.gI().createNormalPet(this, Util.nextInt(0, 2), (byte) Util.nextInt(0, 4));
        }
        // PlayerService.gI().changeAndSendTypePK(this.pet, ConstPlayer.PK_ALL);
        st = System.currentTimeMillis();
    }

    private long st;

    @Override
    public void autoLeaveMap() {
        if (Util.canDoWithTime(st, 900000)) {
            this.leaveMap();
        }
        if (this.zone != null && this.zone.getNumOfPlayers() > 0) {
            st = System.currentTimeMillis();
        }
    }

    @Override
    public synchronized long injured(Player plAtt, long damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }
            if (Util.isTrue(1, 30)) {
                this.playerSkill.skillSelect = this.playerSkill.skills.get(Util.nextInt(0, 6));
                this.tangChiSo();
                SkillService.gI().useSkill(this, null, null, -1, null);
            }
            damage = this.nPoint.subDameInjureWithDeff(damage);
            if (!piercing && plAtt.playerSkill.skillSelect.template.id != Skill.TU_SAT
                    && damage > this.nPoint.hpMax / 100) {
                damage = this.nPoint.hpMax / 100;
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

    private long lastTimeAttack;

    @Override
    public void attack() {

        if (Util.canDoWithTime(this.lastTimeAttack, 100) && this.typePk == ConstPlayer.PK_ALL) {

            this.lastTimeAttack = System.currentTimeMillis();
            try {
                Player pl = getPlayerAttack();
                if (pl == null || pl.isDie()) {
                    return;
                }
                this.playerSkill.skillSelect = this.playerSkill.skills
                        .get(Util.nextInt(7, this.playerSkill.skills.size() - 1));
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
                    if (Util.isTrue(1, 100)) {
                        this.playerSkill.skillSelect = this.playerSkill.skills.get(Util.nextInt(0, 6));
                        this.tangChiSo();
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

    private void tangChiSo() {
        long hpMax = this.nPoint.hpMax;
        int rand = Util.nextInt(80, 100);
        hpMax = hpMax + hpMax / rand < 16_070_777 ? hpMax + hpMax / rand : 16_070_777;
        this.nPoint.hpMax = hpMax;
        this.nPoint.dame = hpMax / 10;
    }

    @Override
    public void leaveMap() {
        ChangeMapService.gI().exitMap(this);
        if (this.pet != null) {
            ChangeMapService.gI().exitMap(this.pet);
        }
        this.lastZone = null;
        this.lastTimeRest = System.currentTimeMillis();
        this.changeStatus(BossStatus.REST);
        BrolyManager.gI().removeBoss(this);
        this.dispose();
    }
}
