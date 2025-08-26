package boss.boss_manifest.Nappa;

import java.util.Random;

/*
 *
 *
 * @author NTD
 */

import boss.Boss;
import boss.BossID;
import boss.BossStatus;
import boss.BossesData;
import clan.Clan;
import map.ItemMap;
import player.Player;
import services.Service;
import utils.Util;

public class Kuku extends Boss {

    private long st;

    public Kuku() throws Exception {
        super(BossID.KUKU, true, true, BossesData.KUKU);
    }

    @Override
    public void joinMap() {
        super.joinMap();
        st = System.currentTimeMillis();
    }

    @Override
    public void reward(Player plKill) {
        if (Util.isTrue(50, 100)) {
            int[] items = Util.isTrue(50, 100) ? new int[] { 18, 19, 20 }
                    : new int[] { 1066, 1067, 1068, 1069, 1070, 1229 };
            int randomItem = items[new Random().nextInt(items.length)];
            Service.gI().dropItemMap(this.zone, new ItemMap(this.zone, randomItem, 1,
                    this.location.x, this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24), plKill.id));
        }

        Service.gI().dropItemMap(this.zone, new ItemMap(this.zone, 1790, Util.nextInt(1, 5),
                this.location.x, this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24), plKill.id));
    }

    @Override
    public void autoLeaveMap() {
        if (Util.canDoWithTime(st, 900000)) {
            this.changeStatus(BossStatus.LEAVE_MAP);
        }
        // if (this.zone != null && this.zone.getNumOfPlayers() > 0) {
        // st = System.currentTimeMillis();
        // }
    }
}
