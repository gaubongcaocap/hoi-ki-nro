package boss.boss_manifest.SuperRank;


import consts.ConstRatio;
import boss.Boss;
import boss.BossData;
import boss.OtherBossManager;
import boss.BossStatus;
import boss.BossType;
import consts.ConstSuperRank;
import player.Player;
import services.EffectSkillService;
import services.PlayerService;
import services.Service;
import services.SkillService;
import services.func.ChangeMapService;
import services.func.UseItem;
import utils.SkillUtil;
import utils.Util;
import skill.Skill;

public abstract class SuperRank extends Boss {

    protected Player playerAtt;
    protected Player player;
    protected long timeJoinMap;
    private long lastTimeEatPea;

    public SuperRank(BossType NroLegion, int id, BossData data) throws Exception {
        super(NroLegion, id, data);
        this.bossStatus = BossStatus.RESPAWN;
    }

    @Override
    public void checkPlayerDie(Player pl) {

    }

    @Override
    public void afk() {
        if (!(playerAtt != null && playerAtt.location != null && playerAtt.zone != null && this.zone != null && this.zone.equals(playerAtt.zone))) {
            this.leaveMap();
        }
    }

    protected void goToXY(int x, int y, boolean isTeleport) {
        if (!isTeleport) {
            byte dir = (byte) (this.location.x - x < 0 ? 1 : -1);
            byte move = (byte) Util.nextInt(50, 100);
            PlayerService.gI().playerMove(this, this.location.x + (dir == 1 ? move : -move), y);
        } else {
            ChangeMapService.gI().changeMapYardrat(this, this.zone, x, y);
        }
    }

    @Override
    public void attack() {
        try {
            if (playerAtt != null && playerAtt.location != null && playerAtt.zone != null && this.zone != null && this.zone.equals(playerAtt.zone)) {
                if (this.isDie() || playerAtt.lostByDeath) {
                    return;
                }
                if (Util.isTrue(1, 20)) {
                    goToPlayer(playerAtt, false);
                }
                this.nPoint.mp = this.nPoint.mpMax;
                if (Util.canDoWithTime(lastTimeEatPea, 5000) && this.nPoint.hp < this.nPoint.hpMax) {
                    UseItem.gI().eatPea(this);
                    lastTimeEatPea = System.currentTimeMillis();
                }
                this.playerSkill.skillSelect = this.playerSkill.skills.get(Util.nextInt(0, this.playerSkill.skills.size() - 1));
                if (playerAtt.effectSkill.isHaveEffectSkill()) {
                    switch (this.playerSkill.skillSelect.template.id) {
                        case Skill.KHIEN_NANG_LUONG, Skill.THAI_DUONG_HA_SAN, Skill.THOI_MIEN, Skill.TROI ->
                            this.playerSkill.skillSelect = this.playerSkill.skills.get(0);
                    }
                }
                if (this.effectSkill.isShielding && this.nPoint.hp > this.nPoint.hpMax / 100 * 50) {
                    switch (this.playerSkill.skillSelect.template.id) {
                        case Skill.THAI_DUONG_HA_SAN, Skill.THOI_MIEN, Skill.TROI ->
                            this.playerSkill.skillSelect = this.playerSkill.skills.get(0);
                    }
                }
                if (Util.getDistance(this, playerAtt) <= this.getRangeCanAttackWithSkillSelect()) {
                    if (Util.isTrue(15, ConstRatio.PER100) && SkillUtil.isUseSkillChuong(this)) {
                        goToXY(playerAtt.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 80)), Util.nextInt(10) % 2 == 0 ? playerAtt.location.y : playerAtt.location.y - Util.nextInt(0, 50), false);
                    }
                    SkillService.gI().useSkill(this, playerAtt, null, -1, null);
                    checkPlayerDie(playerAtt);
                } else {
                    SkillService.gI().useSkill(this, playerAtt, null, -1, null);
                    goToPlayer(playerAtt, false);
                }
            } else {
                this.leaveMap();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected void goToPlayer(Player pl, boolean isTeleport) {
        goToXY(pl.location.x, pl.location.y, isTeleport);
    }

    @Override
    public void joinMap() {
        if (playerAtt.zone != null) {
            this.zone = playerAtt.zone;
            this.pet = player.pet;
            this.itemTime = player.itemTime;
            this.inventory = player.inventory;
            this.iDMark = player.iDMark;
            this.effectSkill = player.effectSkill;
            this.effectSkill.setPlayer(this);
            this.effectSkin = player.effectSkin;
            this.effectSkin.setPlayer(this);
            this.fusion = player.fusion;
            this.playerIntrinsic = player.playerIntrinsic;
            this.rewardBlackBall = player.rewardBlackBall;
            this.setClothes = player.setClothes;
            this.setClothes.setup();
            if (this.pet != null) {
                this.pet.setClothes.setup();
            }
            this.nPoint = player.nPoint;
            this.nPoint.setPlayer(this);
            this.fusion = player.fusion;
            this.fusion.setPlayer(this);
            this.nPoint.calPoint();
            ChangeMapService.gI().changeMap(this, this.zone, 434, 264);
        }
    }

    protected void immortalHpMp() {
        this.nPoint.hp = this.nPoint.hpMax;
        this.nPoint.mp = this.nPoint.mpMax;
    }

    @Override
    public void update() {
        try {
            super.updateInfo();
            if ((this.effectSkill != null && this.effectSkill.isHaveEffectSkill()) || (this.newSkill != null && this.newSkill.isStartSkillSpecial)) {
                return;
            }
            switch (this.bossStatus) {
                case RESPAWN:
                    this.respawn();
                    this.changeStatus(BossStatus.JOIN_MAP);
                case JOIN_MAP:
                    joinMap();
                    if (this.zone != null) {
                        changeStatus(BossStatus.AFK);
                        timeJoinMap = System.currentTimeMillis();
                        this.immortalHpMp();
                        Service.gI().Send_Info_NV(this);
                    }
                    break;
                case AFK:
                    afk();
                    break;
                case ACTIVE:
                    if (this.playerSkill.prepareTuSat || this.playerSkill.prepareLaze || this.playerSkill.prepareQCKK) {
                        break;
                    } else {
                        this.attack();
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void notifyPlayeKill(Player player) {

    }

    @Override
    public synchronized long injured(Player plAtt, long damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (plAtt != null && plAtt.effectSkill != null && plAtt.effectSkill.isBinh
                    && !Util.canDoWithTime(plAtt.effectSkill.lastTimeUpBinh, 3000)) {
                return 0;
            }
            if (plAtt != null && plAtt.isPl() && this.maBuHold != null && this.zone != null && this.zone.map.mapId == 128) {
                this.precentMabuHold++;
                damage = 1;
            }
            if (plAtt != null && this.nPoint.islinhthuydanhbac) {
                Service.gI().sendThongBao(plAtt, "Không thể tấn công! Vì người chơi này đã nạp lần đầu!");
                return 0;
            }

            if (plAtt != null && plAtt.playerSkill.skillSelect != null) {
                switch (plAtt.playerSkill.skillSelect.template.id) {
                    case Skill.KAMEJOKO:
                    case Skill.MASENKO:
                    case Skill.ANTOMIC:
                        if (this.nPoint.voHieuChuong > 0) {
                            services.PlayerService.gI().hoiPhuc(this, 0, (int) (damage * this.nPoint.voHieuChuong / 100));
                            return 0;
                        }
                        break;
                }
            }

            int tlGiap = this.nPoint.tlGiap;
            int tlNeDon = this.nPoint.tlNeDon;

            if (plAtt != null && !isMobAttack && plAtt.playerSkill.skillSelect != null) {
                switch (plAtt.playerSkill.skillSelect.template.id) {
                    case Skill.KAMEJOKO:
                    case Skill.MASENKO:
                    case Skill.ANTOMIC:
                    case Skill.DRAGON:
                    case Skill.DEMON:
                    case Skill.GALICK:
                    case Skill.LIEN_HOAN:
                    case Skill.KAIOKEN:
                    case Skill.QUA_CAU_KENH_KHI:
                    case Skill.MAKANKOSAPPO:
                    case Skill.DICH_CHUYEN_TUC_THOI:
                        tlNeDon -= plAtt.nPoint.tlchinhxac;
                        break;
                    default:
                        tlNeDon = 0;
                        break;
                }

                switch (plAtt.playerSkill.skillSelect.template.id) {
                    case Skill.KAMEJOKO:
                    case Skill.MASENKO:
                    case Skill.ANTOMIC:
                        if (tlGiap - plAtt.nPoint.tlxgc >= 0) {
                            tlGiap -= plAtt.nPoint.tlxgc;
                        } else {
                            tlGiap = 0;
                        }
                        break;
                    case Skill.DRAGON:
                    case Skill.DEMON:
                    case Skill.GALICK:
                    case Skill.LIEN_HOAN:
                    case Skill.KAIOKEN:
                        if (tlGiap - plAtt.nPoint.tlxgcc >= 0) {
                            tlGiap -= plAtt.nPoint.tlxgcc;
                        } else {
                            tlGiap = 0;
                        }
                        break;
                }
            }

            if (piercing) {
                tlGiap = 0;
            }

            if (tlNeDon > 90) {
                tlNeDon = 90;
            }
            if (tlGiap > 86) {
                tlGiap = 86;
            }

            if (Util.isTrue(tlNeDon, 100)) {
                return 0;
            }

            damage -= ((damage / 100) * tlGiap);

            if (!piercing) {
                damage = this.nPoint.subDameInjureWithDeff(damage);
            }

            boolean isUseGX = false;
            if (!piercing && plAtt != null && plAtt.playerSkill.skillSelect != null) {
                switch (plAtt.playerSkill.skillSelect.template.id) {
                    case Skill.KAMEJOKO:
                    case Skill.MASENKO:
                    case Skill.ANTOMIC:
                    case Skill.DRAGON:
                    case Skill.DEMON:
                    case Skill.GALICK:
                    case Skill.LIEN_HOAN:
                    case Skill.KAIOKEN:
                    case Skill.QUA_CAU_KENH_KHI:
                    case Skill.MAKANKOSAPPO:
                    case Skill.DICH_CHUYEN_TUC_THOI:
                        isUseGX = true;
                        break;
                }
            }
            if (isUseGX && this.itemTime != null) {
                if (this.itemTime.isUseGiapXen && !this.itemTime.isUseGiapXen2) {
                    damage /= 2;
                }
                if (this.itemTime.isUseGiapXen2) {
                    damage = damage / 100 * 40;
                }
            }

            if (!piercing && effectSkill.isShielding && !isMobAttack) {
                if (this.iDMark != null) {
                    this.iDMark.setDamePST(damage);
                }
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = 1;
            }
//            damage = Math.min(damage, 2_000_000_000L);
            this.nPoint.subHP(Util.maxIntValue(damage));
            if (plAtt != null && isDie()) {
                setDie(plAtt);
            }

            return damage;
        } else {
            return 0;
        }
    }

    @Override
    public void die(Player plKill) {
        this.changeStatus(BossStatus.DIE);
    }

    @Override
    public void leaveMap() {
        if (playerAtt.location != null && playerAtt != null && playerAtt.zone != null && this.zone != null && this.zone.equals(playerAtt.zone) && !playerAtt.lostByDeath) {
            Service.gI().chat(this, ConstSuperRank.TEXT_CLONE_THUA);
        } else {
            Service.gI().chat(this, ConstSuperRank.TEXT_CLONE_THANG);
        }
        ChangeMapService.gI().exitMap(this);
        if (this.player != null) {
            this.player.dispose();
        }
        this.lastZone = null;
        this.lastTimeRest = System.currentTimeMillis();
        this.changeStatus(BossStatus.REST);
        OtherBossManager.gI().removeBoss(this);
        this.dispose();
    }
}
