package boss.miniboss;

import boss.Boss;
import boss.BossData;
import boss.BossID;
import boss.BossStatus;
import consts.ConstPlayer;
import java.util.List;
import map.ItemMap;
  import player.Player;
import services.PlayerService;
import services.Service;
import services.SkillService;
import services.func.ChangeMapService;
import skill.Skill;
import utils.SkillUtil;
import utils.Util;

public class Odo extends Boss {

    private long lastTimeOdo;
    private long lastTimeHpRegen;

    public Odo() throws Exception {
        super(BossID.O_DO1, new BossData(
                "Ở Dơ " + Util.nextInt(1, 49),
                ConstPlayer.TRAI_DAT,
                new short[]{400, 401, 402, -1, -1, -1},
                1000,
                new long[]{500000},
                new int[]{5, 7, 0, 14},
                new int[][]{
                    {Skill.DRAGON, 7, 10000}},
                new String[]{}, //text chat 1
                new String[]{}, //text chat 2
                new String[]{},
                600000));
    }


    @Override
    public synchronized long injured(Player plAtt, long damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
//       
            if (damage >= 50000) {
                damage = 50000;
            }
//            this.nPoint.dame = (int) damage / Util.nextInt(500, 1000);
            this.nPoint.subHP(damage);
            return (int) damage;
        } else {
            return 0;
        }
    }

    private void updateOdo() {
        try {
            int param = 10;
            int randomTime = Util.nextInt(3000, 5000);
            if (Util.canDoWithTime(lastTimeOdo, randomTime)) {
                List<Player> playersMap = this.zone.getNotBosses();
                for (int i = playersMap.size() - 1; i >= 0; i--) {
                    Player pl = playersMap.get(i);
                    if (pl != null && pl.nPoint != null && !this.equals(pl) && !pl.isBoss && !pl.isDie()
                            && Util.getDistance(this, pl) <= 200) {
                        int subHp = (int) ((long) pl.nPoint.hpMax * param / 100);
                        if (subHp >= pl.nPoint.hp) {
                            subHp = (int) (pl.nPoint.hp - 1);
                        }
                        this.chat("Bùm Bùm");
//                        Service.gI().chat(pl, textOdo[Util.nextInt(0, textOdo.length - 1)]);
                        PlayerService.gI().sendInfoHpMpMoney(pl);
                        pl.injured(null, subHp, true, false);
                    }
                }
                this.lastTimeOdo = System.currentTimeMillis(); // Cập nhật thời gian của Odo
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void regenHp() {
        try {
            if (Util.canDoWithTime(lastTimeHpRegen, 30000)) {
                int regenPercentage = Util.nextInt(10, 20);
                int regenAmount = (int) (this.nPoint.hpMax * regenPercentage / 100);
                PlayerService.gI().hoiPhuc(this, regenAmount, 0);
                this.chat("Mùi Của Các Ngươi Thơm Quá!! HAHA");
                this.lastTimeHpRegen = System.currentTimeMillis();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void attack() {
        if (Util.canDoWithTime(this.lastTimeAttack, 100) && this.typePk == ConstPlayer.PK_ALL) {
            this.lastTimeAttack = System.currentTimeMillis();
            try {
                Player pl = this.getPlayerAttack();
                if (pl == null || pl.isDie()) {
                    return;
                }
                this.playerSkill.skillSelect = this.playerSkill.skills.get(Util.nextInt(0, this.playerSkill.skills.size() - 1));

                if (Util.getDistance(this, pl) <= 40) {
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
                    this.updateOdo();

                } else {
                    if (Util.isTrue(1, 2)) {
                        this.moveToPlayer(pl);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        this.regenHp();
    }

    @Override
    public void moveTo(int x, int y) {
        byte dir = (byte) (this.location.x - x < 0 ? 1 : -1);
        byte move = (byte) Util.nextInt(30, 40);
        PlayerService.gI().playerMove(this, this.location.x + (dir == 1 ? move : -move), y);
    }

    @Override
    public void reward(Player plKill) {
        for (byte i = 0; i < 5; i++) {
            ItemMap it = new ItemMap(this.zone, 457, (int) 5, this.location.x + i * 3, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), plKill.id);
            Service.gI().dropItemMap(this.zone, it);
        }
    }

    private long st;

    @Override
    public void active() {
        if (this.typePk == ConstPlayer.NON_PK) {
            this.changeToTypePK();
        }
        this.attack();
        if (Util.canDoWithTime(st, 900000)) {
            this.changeStatus(BossStatus.LEAVE_MAP);
        }
    }

    @Override
    public void joinMap() {
        super.joinMap();
        st = System.currentTimeMillis();
    }

    @Override
    public void leaveMap() {
        ChangeMapService.gI().exitMap(this);
        this.lastZone = null;
        this.lastTimeRest = System.currentTimeMillis();
        this.changeStatus(BossStatus.REST);
    }
}
