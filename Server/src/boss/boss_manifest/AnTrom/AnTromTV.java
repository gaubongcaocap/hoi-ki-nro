package boss.boss_manifest.AnTrom;

import boss.Boss;
import boss.BossData;
import boss.BossID;
import boss.BossStatus;
import static boss.BossType.ANTROM;
import consts.ConstPlayer;
import item.Item;
import map.ItemMap;
import map.Zone;
import player.Player;
import services.InventoryService;
import services.MapService;
import services.PlayerService;
import services.Service;
import services.SkillService;
import services.func.ChangeMapService;
import skill.Skill;
import utils.Util;

public class AnTromTV extends Boss {

    private long goldAnTrom;
    private long lastTimeAnTrom;

    public AnTromTV() throws Exception {
        super(ANTROM, BossID.AN_TROM_TV, new BossData(
                //            "Ăn trộm TV",
                "Ăn Trộm TV",
                ConstPlayer.TRAI_DAT,
                new short[]{618, 619, 620, 132, -1, -1},
                //            new short[]{657, 658, 659, 50, -1, 5},
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
            if (damage > nPoint.hpMax / 100) {
                damage = nPoint.hpMax / 100;
            }
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

                Item thoivang = InventoryService.gI().findItemBag(pl, 457);
                if (thoivang == null) {

                    return;
                }

                if (Util.getDistance(this, pl) <= 40) {
                    if (!Util.canDoWithTime(this.lastTimeAnTrom, 10000) || goldAnTrom > 20) {
                        return;
                    }
                    if (pl.isPl()) {
                        int stolenGold = Util.nextInt(1, 3);
                        InventoryService.gI().subQuantityItemsBag(pl, thoivang, stolenGold);
                        Service.gI().sendMoney(pl);
                        InventoryService
                                .gI().sendItemBag(pl);
                        InventoryService.gI().addQuantityItemsBag(this, thoivang, stolenGold);
                        InventoryService.gI().sendItemBag(this);
                        if (thoivang.quantity > 0) {
                            goldAnTrom += stolenGold;
                            Service.gI().stealMoney(pl, -stolenGold);
                            ItemMap itemMap = new ItemMap(this.zone, 457, stolenGold, (this.location.x + pl.location.x) / 2, this.location.y, this.id);
                            Service.gI().dropItemMap(this.zone, itemMap);
                            Service.gI().sendToAntherMePickItem(this, itemMap.itemMapId);
                            this.zone.removeItemMap(itemMap);
                            this.lastTimeAnTrom = System.currentTimeMillis();
                        }
                    }
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
            int goldReward = (int) (goldAnTrom * 1 / 10);
            for (byte i = 0; i < 5; i++) {
                ItemMap it = new ItemMap(this.zone, 457, goldReward, this.location.x + i * 3, this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24), plKill.id);
                it.options.add(new Item.ItemOption(30, 1));
                Service.gI().dropItemMap(this.zone, it);
            }
        }

        goldAnTrom = 0;
    }

    @Override
    public void joinMap() {
        this.name = "Ăn Trộm " + Util.nextInt(50, 100);
        this.nPoint.hpMax = Util.nextInt(100, 150);
        this.nPoint.hp = this.nPoint.hpMax;
        this.nPoint.dameg = this.nPoint.hpMax / 10;
        goldAnTrom = 0;
        this.joinMap2(); //To change body of generated methods, choose Tools | Templates.
        st = System.currentTimeMillis();
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
