package boss.boss_manifest.Kimetsu;

import java.util.Random;

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
import services.Service;
import utils.Util;

public class Nezuko extends Boss {

    private long st;

    public Nezuko() throws Exception {
        super(BossID.NEZUKO, false, false, BossesData.NEZUKO);
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
        Service.gI().dropItemMap(this.zone, new ItemMap(this.zone, 1790, Util.nextInt(1, 5),
                this.location.x, this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24), plKill.id));
                
        if (Util.isTrue(50, 100)) {
            int[] items = Util.isTrue(50, 100) ? new int[] { 18, 19, 20 }
                    : new int[] { 1066, 1067, 1068, 1069, 1070, 1229 };
            int randomItem = items[new Random().nextInt(items.length)];
            Service.gI().dropItemMap(this.zone, new ItemMap(this.zone, randomItem, 1,
                    this.location.x, this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24), plKill.id));
        }

        short itTemp = 1091;
        ItemMap it2 = new ItemMap(zone, itTemp, 1, this.location.x + Util.nextInt(-50, 50),
                this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24), plKill.id);
        it2.options.add(new Item.ItemOption(77, Util.nextInt(10, 30)));
        it2.options.add(new Item.ItemOption(103, Util.nextInt(10, 30)));
        it2.options.add(new Item.ItemOption(50, Util.nextInt(10, 30)));
        it2.options.add(new Item.ItemOption(101, Util.nextInt(5, 200)));
        it2.options.add(new Item.ItemOption(179, 0));
        it2.options.add(new Item.ItemOption(30, 0));
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
            if ((boss.id == BossID.INOSUKE_PIG || boss.id == BossID.ZENITSU || boss.id == BossID.INOSUKE)
                    && !boss.isDie()) {
                return;
            }
        }
        this.parentBoss.changeStatus(BossStatus.ACTIVE);
    }
}
