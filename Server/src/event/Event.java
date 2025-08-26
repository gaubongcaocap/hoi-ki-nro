package event;

/**
 *
 * @author NTD
 */

import boss.BossManager;
import event.ievent.IEvent;
import npc.NpcFactory;
import services.MapService;
import utils.Logger;

public abstract class Event implements IEvent {

    @Override
    public void init() {
        npc();
        boss();
        itemMap();
        itemBoss();
    }

    @Override
    public void npc() {

    }

    @Override
    public void createNpc(int mapId, int npcId, int x, int y) {
        MapService.gI().getMapById(mapId).npcs.add(NpcFactory.createNPC(mapId, 1, x, y, npcId));
    }

    @Override
    public void boss() {

    }

    @Override
    public void createBoss(int bossId, int... total) {
        int len = 1;
        if (total.length > 0) {
            len = total[0];
        }
        try {
            for (int i = 0; i < len; i++) {
                BossManager.gI().createBoss(bossId);
            }
        } catch (Exception e) {
            Logger.error(e + "\n");
        }
    }

    @Override
    public void itemMap() {

    }

    @Override
    public void itemBoss() {

    }
}
