package boss.boss_manifest.Cell;

/*
 *
 *
 * @author NTD
 */
import EMTI.Functions;
import consts.ConstPlayer;
import boss.Boss;
import boss.BossID;
import boss.BossManager;
import boss.BossStatus;
import boss.BossesData;
import consts.ConstTaskBadges;
import static event.EventManager.LUNNAR_NEW_YEAR;
import item.Item;
import java.util.List;
import map.ItemMap;
import player.Player;
import server.Manager;
import services.*;
import utils.Util;

import java.util.Random;
import task.Badges.BadgesTaskService;

public class SieuBoHung extends Boss {

    private long st;
    public boolean callCellCon;
    private long lastTimeHapThu;
    private int timeHapThu;

    private final String text[] = {"Thưa quý vị và các bạn, đây đúng là trận đấu trời long đất lở",
        "Vượt xa mọi dự đoán của chúng tôi",
        "Eo ơi toàn thân lão Xên bốc cháy kìa"};
    private long lastTimeChat;
    private long lastTimeMove;
    private int indexChat = 0;

    public SieuBoHung() throws Exception {
        super(BossID.SIEU_BO_HUNG, BossesData.SIEU_BO_HUNG_1, BossesData.SIEU_BO_HUNG_2);
    }

    @Override
    protected void resetBase() {
        super.resetBase();
        this.callCellCon = false;
    }

    public void callCellCon() {
     Thread.startVirtualThread(() -> {
            try {
                this.changeStatus(BossStatus.AFK);
                this.changeToTypeNonPK();
                this.recoverHP();
                this.callCellCon = true;
                this.chat("Hãy đấu với 7 đứa con của ta, chúng đều là siêu cao thủ");
                //Thread.sleep(2000);
                Functions.sleep(2000);
                this.chat("Cứ chưởng tiếp đi haha");
                //Thread.sleep(2000);
                Functions.sleep(2000);
                this.chat("Liệu mà giữ mạng đấy");
                //Thread.sleep(2000);
                Functions.sleep(2000);
                for (Boss boss : this.bossAppearTogether[this.currentLevel]) {
                    switch ((int) boss.id) {
                        case BossID.XEN_CON_1 ->
                            boss.changeStatus(BossStatus.RESPAWN);
                        case BossID.XEN_CON_2 ->
                            boss.changeStatus(BossStatus.RESPAWN);
                        case BossID.XEN_CON_3 ->
                            boss.changeStatus(BossStatus.RESPAWN);
                        case BossID.XEN_CON_4 ->
                            boss.changeStatus(BossStatus.RESPAWN);
                        case BossID.XEN_CON_5 ->
                            boss.changeStatus(BossStatus.RESPAWN);
                        case BossID.XEN_CON_6 ->
                            boss.changeStatus(BossStatus.RESPAWN);
                        case BossID.XEN_CON_7 ->
                            boss.changeStatus(BossStatus.RESPAWN);
                    }
                }
            } catch (Exception e) {
            }
        });
    }

    public void recoverHP() {
        PlayerService.gI().hoiPhuc(this, this.nPoint.hpMax, 0);
    }

    @Override
    public void reward(Player plKill) {
        BadgesTaskService.updateCountBagesTask(plKill, ConstTaskBadges.TRUM_SAN_BOSS, 1);
        int x = this.location.x;
        int y = this.zone.map.yPhysicInTop(x, this.location.y - 24);
        int drop = 190; // 100% rơi item ID 190
        int quantity = Util.nextInt(20000, 30000);
        // Tạo itemMap cho item ID 190
        ItemMap itemMap = new ItemMap(this.zone, drop, quantity, x, y, plKill.id);
        Item item = ItemService.gI().createNewItem((short) drop);
        Service.gI().dropItemMap(zone, itemMap);

        if (LUNNAR_NEW_YEAR) {
            int dropLunar = 1475; 
            int quantityLunar = 1; 
            ItemMap lunarItemMap = new ItemMap(this.zone, dropLunar, quantityLunar, x, y, plKill.id);
            Service.gI().dropItemMap(zone, lunarItemMap);
        }
        // 30% xác suất để rơi đồ
        if (Util.isTrue(30, 100)) {
            int group = Util.nextInt(1, 100) <= 70 ? 0 : 1;  // 70% chọn Áo Quần Giày (group = 0), 30% chọn Găng Rada (group = 1)

            // Các vật phẩm rơi từ nhóm Áo Quần Giày và Găng Rada
            int[][] drops = {
                {230, 231, 232, 234, 235, 236, 238, 239, 240, 242, 243, 244, 246, 247, 248, 250, 251, 252, 266, 267, 268, 270, 271, 272, 274, 275, 276}, // Áo Quần Giày
                {254, 255, 256, 258, 259, 260, 262, 263, 264, 278, 279, 280} // Găng Rada
            };
            // Chọn vật phẩm ngẫu nhiên từ nhóm đã chọn
            int dropOptional = drops[group][Util.nextInt(0, drops[group].length - 1)];
            // Tạo vật phẩm và thêm chỉ số shop
            ItemMap optionalItemMap = new ItemMap(this.zone, dropOptional, 1, x, y, plKill.id);
            Item optionalItem = ItemService.gI().createNewItem((short) dropOptional);
            List<Item.ItemOption> optionalOps = ItemService.gI().getListOptionItemShop((short) dropOptional);
            optionalOps.forEach(option -> option.param = (int) (option.param * Util.nextInt(100, 115) / 100.0));
            optionalItemMap.options.addAll(optionalOps);
            // Thêm chỉ số sao pha lê (80% từ 1-3 sao, 17% từ 4-5 sao, 3% sao 6)
            int rand = Util.nextInt(1, 100);
            int value = 0;
            if (rand <= 80) {
                value = Util.nextInt(1, 3); // 80% xác suất: sao từ 1 đến 3
            }
            optionalItemMap.options.add(new Item.ItemOption(107, value));
            // Drop vật phẩm tùy chọn xuống bản đồ
            Service.gI().dropItemMap(zone, optionalItemMap);
        }
        // 80% xác suất rơi ngọc rồng hoặc item cấp 2
        if (Util.isTrue(80, 100)) {
            int[] items = Util.isTrue(50, 100) ? new int[] { 18, 19, 20 }
                    : new int[] { 1066, 1067, 1068, 1069, 1070, 1229 };
            int randomItem = items[new Random().nextInt(items.length)];
            Service.gI().dropItemMap(this.zone, new ItemMap(this.zone, randomItem, 1,
                    this.location.x, this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24), plKill.id));
        }

        Service.gI().dropItemMap(this.zone, new ItemMap(this.zone, 1790, Util.nextInt(1, 5),
                this.location.x, this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24), plKill.id));
        
        TaskService.gI().checkDoneTaskKillBoss(plKill, this);
    }

    @Override
    public void active() {
        if (this.typePk == ConstPlayer.NON_PK) {
            this.changeToTypePK();
        }
        this.attack();
    }

    @Override
    public synchronized long injured(Player plAtt, long damage, boolean piercing, boolean isMobAttack) {
        if (prepareBom) {
            return 0;
        }
        if (!this.callCellCon && damage >= this.nPoint.hp) {
            this.callCellCon();
            return 0;

        }
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }
            damage = this.nPoint.subDameInjureWithDeff(damage / 3);
            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = damage / 2;
            }
            this.nPoint.subHP(damage);
            if (isDie()) {
                setBom(plAtt);
                return 0;
            }
            return damage;
        } else {
            return 0;
        }

    }

    @Override
    public void joinMap() {
        super.joinMap();
        st = System.currentTimeMillis();
    }

    @Override
    public void autoLeaveMap() {
        this.mc();
        if (this.currentLevel > 0) {
            if (this.bossStatus == BossStatus.AFK) {
                this.changeStatus(BossStatus.ACTIVE);
            }
        }
        if (Util.canDoWithTime(st, 900000)) {
            this.leaveMapNew();
        }
        if (this.zone != null && this.zone.getNumOfPlayers() > 0) {
            st = System.currentTimeMillis();
        }
    }

    public void mc() {
        Player mc = zone.getNpc();
        if (mc != null) {
            if (Util.canDoWithTime(lastTimeChat, 3000)) {
                String textchat = text[indexChat];
                Service.gI().chat(mc, textchat);
                indexChat++;
                if (indexChat == text.length) {
                    indexChat = 0;
                    lastTimeChat = System.currentTimeMillis() + 7000;
                } else {
                    lastTimeChat = System.currentTimeMillis();
                }
            }

            if (Util.canDoWithTime(lastTimeMove, 15000)) {
                if (Util.isTrue(2, 3)) {
                    int x = this.location.x + Util.nextInt(-100, 100);
                    int y = x > 156 && x < 611 ? 288 : 312;
                    PlayerService.gI().playerMove(mc, x, y);
                }
                lastTimeMove = System.currentTimeMillis();
            }
        }
    }

}
