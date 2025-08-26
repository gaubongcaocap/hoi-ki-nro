package event.event_manifest;

/*
 *
 *
 * @author NTD
 */

import boss.BossID;
import event.Event;

public class TrungThu extends Event {

    @Override
    public void boss() {
        createBoss(BossID.KHIDOT, 10);
        createBoss(BossID.NGUYET_THAN, 10);
    }
}
