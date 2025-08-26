package boss.boss_manifest.AnTrom;

import boss.Boss;
import boss.BossData;
import boss.BossID;
import boss.BossStatus;
import static boss.BossType.ANTROM;
import boss.BossesData;
import consts.ConstPlayer;
import map.ItemMap;
import map.Zone;
import player.Player;
import server.Client;
import services.MapService;
import services.PlayerService;
import services.Service;
import services.SkillService;
import services.func.ChangeMapService;
import skill.Skill;
import utils.Util;

public class AnTrom extends Boss {

    private long goldAnTrom;
    private long lastTimeAnTrom;
    private long lastTimeJoinMap;
    private static final long timeChangeMap = 1000; // thời gian đổi map 1 lần

    public AnTrom() throws Exception {
        super(ANTROM, BossID.AN_TROM, new BossData(
                //            "Ăn trộm TV",
                "Ăn Trộm ",
                ConstPlayer.TRAI_DAT,
                new short[]{618, 619, 620, 133, -1, -1},
     
                1,
                new long[]{100},
                new int[]{5, 7, 0, 14},
                new int[][]{
                    {Skill.DRAGON, 7, 1000},
                    {Skill.GALICK, 7, 1000}, {Skill.LIEN_HOAN, 7, 1000},
                    {Skill.THOI_MIEN, 3, 50000},
                    {Skill.DICH_CHUYEN_TUC_THOI, 3, 50000}},
                new String[]{"|-1|Tới giờ làm việc, lụm lụm", "|-1|Cảm giác mình vào phải khu người nghèo :))"}, //text chat 1
                new String[]{"|-1|Ái chà vàng vàng", "|-1|Không làm vẫn có ăn :))", "|-2|Giám ăn trộm giữa ban ngày thế à", "|-2|Cút ngay không là ăn đòn"}, //text chat 2
                new String[]{"|-1|Híc lần sau ta sẽ cho ngươi phá sản",
                    "|-2|Chừa thói ăn trộm nghe chưa"}, //text chat 3
                600));

    }

    @Override
    public Zone getMapJoin() {
        int mapId = this.data[this.currentLevel].getMapJoin()[Util.nextInt(0, this.data[this.currentLevel].getMapJoin().length - 1)];
        return MapService.gI().getMapById(mapId).zones.get(0);
    }

    @Override
    public Player getPlayerAttack() {
        return super.getPlayerAttack();
    }

    @Override
    public long injured(Player plAtt, long damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            damage = 1;
            this.nPoint.subHP(damage);
            if (isDie()) {
                this.setDie(plAtt);
                die(plAtt);
            }
            this.playerSkill.skillSelect = this.playerSkill.skills.get(Util.nextInt(0, this.playerSkill.skills.size() - 1));
            SkillService.gI().useSkill(this, plAtt, null, -1, null);
            return damage;
        } else {
            return 0;
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

                if (Util.getDistance(this, pl) <= 40) {
                    if (!Util.canDoWithTime(this.lastTimeAnTrom, 500) || goldAnTrom > 10_000_000_000L) {
                        return;
                    }
                    int gold = 0;
                    if (pl.isPl()) {
                        if (pl.inventory.gold >= 100000000) {//nếu số vàng trong túi của người chơi lớn hơn hoặc bằng 100tr
                            gold = Util.nextInt(20000, 30000);// thì boss sẽ ăn trộm từ 20k đến 30k gold
                        } else if (pl.inventory.gold >= 10000000) {
                            gold = Util.nextInt(4000, 5000);

                        } else if (pl.inventory.gold >= 1000000) {
                            gold = Util.nextInt(1000, 2000);
                        }
//                    this.chat("Á đù Hốc đc " + Util.numberToMoney(gold) + " Vàng roài");

                        if (gold > 0) {
                            pl.inventory.gold -= gold;
                            goldAnTrom += gold;
                            Service.gI().stealMoney(pl, -gold);
                            ItemMap itemMap = new ItemMap(this.zone, 190, gold, (this.location.x + pl.location.x) / 2, this.location.y, this.id);
                            Service.gI().dropItemMap(this.zone, itemMap);
                            Service.gI().sendToAntherMePickItem(this, itemMap.itemMapId);
                            this.zone.removeItemMap(itemMap);
                            this.lastTimeAnTrom = System.currentTimeMillis();

                        }
                    }
                } else {
//                    System.out.println("moveTo");
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
    public void moveTo(int x, int y) {
        byte dir = (byte) (this.location.x - x < 0 ? 1 : -1);
        byte move = (byte) Util.nextInt(30, 40);
        PlayerService.gI().playerMove(this, this.location.x + (dir == 1 ? move : -move), y);
    }

    @Override
    public void die(Player plKill) {
        this.reward(plKill);
        this.changeStatus(BossStatus.DIE);
    }

    @Override
    public void reward(Player plKill) {
        if (goldAnTrom != 0) {
            goldAnTrom = goldAnTrom * 8 / 10;
            for (byte i = 0; i < 5; i++) {
                ItemMap it = new ItemMap(this.zone, 190, (int) (goldAnTrom / 5), this.location.x + i * 3, this.zone.map.yPhysicInTop(this.location.x,
                        this.location.y - 24), plKill.id);
                Service.gI().dropItemMap(this.zone, it);
            }
        }
    }

//    @Override
//    public void joinMap() {
//        super.joinMap();
//        lastTimeJoinMap = System.currentTimeMillis() + timeChangeMap;
//        goldAnTrom = 0;
//    }
//@Override
//    public void joinMap() {
//        super.joinMap();
//        st = System.currentTimeMillis();
//    }
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
        this.name = "Ăn Trộm " + Util.nextInt(1, 49);
        this.nPoint.hpMax = Util.nextInt(100, 150);
        this.nPoint.hp = this.nPoint.hpMax;
        this.nPoint.dameg = this.nPoint.hpMax / 10;
        goldAnTrom = 0;
        this.joinMap2(); //To change body of generated methods, choose Tools | Templates.
        st = System.currentTimeMillis();
    }

    public void joinMap2() {
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
                int zoneid = 0;
                this.zone = this.zone.map.zones.get(zoneid);
                ChangeMapService.gI().changeMap(this, this.zone, -1, -1);

                this.changeStatus(BossStatus.CHAT_S);
            } catch (Exception e) {
                this.changeStatus(BossStatus.REST);
            }
        } else {
            this.changeStatus(BossStatus.RESPAWN);
        }
    }

    @Override
    public void leaveMap() {
        ChangeMapService.gI().exitMap(this);
        this.lastZone = null;
        this.lastTimeRest = System.currentTimeMillis();
        this.changeStatus(BossStatus.REST);
    }

}
