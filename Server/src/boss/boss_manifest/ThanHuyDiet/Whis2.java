package boss.boss_manifest.ThanHuyDiet;

/*
 *
 *
 * @author YourSoulMatee
 */

import boss.Boss;
import boss.BossID;
import boss.BossStatus;
import boss.BossesData;
import item.Item;
import jdbc.daos.PlayerDAO;
import map.ItemMap;
import player.Player;
import services.ChatGlobalService;
import services.ItemService;
import services.Service;
import utils.Util;

public class Whis2 extends Boss {

    private long st;

    public Whis2() throws Exception {
        super(BossID.WHIS2, false, false, BossesData.WHIS2);
    }

    @Override
    public void moveTo(int x, int y) {
        if (this.currentLevel == 1) {
            return;
        }
        super.moveTo(x, y);
    }

    @Override
    public void reward(Player plKill) {
        // int itemId = 0;
        // int qty = 0;
        // int sotien = 0;

        // if (Util.isTrue(70, 100)) {
        //     qty = 15;
        //     if (Util.isTrue(60, 100)) {
        //         itemId = 1794;
        //         sotien = 1_000 * qty;
        //     } else {
        //         itemId = 1795;
        //         sotien = 2_000 * qty;
        //     }
        // } else if (Util.isTrue(60, 100)) {
        //     qty = 10;
        //     if (Util.isTrue(60, 100)) {
        //         itemId = 1796;
        //         sotien = 5_000 * qty;
        //     } else if (Util.isTrue(50, 100)) {
        //         itemId = 1797;
        //         sotien = 10_000 * qty;
        //     } else {
        //         qty = 7;
        //         itemId = 1798;
        //         sotien = 20_000 * qty;
        //     }
        // } else if (Util.isTrue(50, 100)) {
        //     if (Util.isTrue(70, 100)) {
        //         itemId = 1799;
        //         qty = 5;
        //         sotien = 50_000 * qty;
        //     } else {
        //         qty = 3;
        //         itemId = 1800;
        //         sotien = 100_000 * qty;
        //     }
        // } else {
        //     if (Util.isTrue(80, 100)) {
        //         qty = 3;
        //         itemId = 1801;
        //         sotien = 200_000 * qty;
        //     } else if (Util.isTrue(50, 100)) {
        //         qty = 2;
        //         itemId = 1802;
        //         sotien = 500_000 * qty;
        //     }
        // }

        // PlayerDAO.sd(plKill, sotien);

        // for (int i = 0; i < Util.nextInt(3, 3); i++) {
        //     ItemMap it = new ItemMap(zone, itemId, qty, this.location.x + i * 10, this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24), plKill.id);
        //     it.options.add(new Item.ItemOption(30, 0));
        //     Service.gI().dropItemMap(this.zone, it);
        // };

        // if (itemId == 1802) {
        //     ChatGlobalService.gI().autoChatGlobal(plKill,  "[ Hệ Thống ] " + plKill.name + " đã tiêu diệt được Zenitsu và nhặt được 500k VNĐ");
        // }

        if (Util.isTrue(20, 100)) {
            int x = this.location.x;
            int y = this.zone.map.yPhysicInTop(x, this.location.y - 24);
            ItemMap it3 = new ItemMap(zone, 1732, 1, x, y, plKill.id);
            it3.options.add(new Item.ItemOption(30, 0));
            it3.options.add(new Item.ItemOption(93, 1));
            Service.gI().dropItemMap(zone, it3);
        }

        if (Util.isTrue(5, 100) || plKill.isAdmin()) {
            ItemMap it = ItemService.gI().randDoTLBoss(this.zone, 1, this.location.x,
                    this.zone.map.yPhysicInTop(this.location.x,
                            this.location.y - 24),
                    plKill.id);
            Service.gI().dropItemMap(this.zone, it);
        }
    }

    @Override
    protected void notifyJoinMap() {
        if (this.currentLevel == 1) {
            return;
        }
        super.notifyJoinMap();
    }

    @Override
    public void joinMap() {
        super.joinMap();
        st = System.currentTimeMillis();
    }

    @Override
    public void autoLeaveMap() {
        if (Util.canDoWithTime(st, 900000)) {
            this.leaveMapNew();
        }
        if (this.zone != null && this.zone.getNumOfPlayers() > 0) {
            st = System.currentTimeMillis();
        }
    }

    @Override
    public void doneChatE() {
        if (this.parentBoss == null || this.parentBoss.bossAppearTogether == null
                || this.parentBoss.bossAppearTogether[this.parentBoss.currentLevel] == null) {
            return;
        }
        for (Boss boss : this.parentBoss.bossAppearTogether[this.parentBoss.currentLevel]) {
            if (boss.id == BossID.BILL2) {
                return;
            }
        }
        this.parentBoss.changeStatus(BossStatus.ACTIVE);
    }
}
