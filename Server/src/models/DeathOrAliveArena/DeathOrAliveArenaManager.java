package models.DeathOrAliveArena;

/*
 *
 *
 * @author NTD
 */
import EMTI.Functions;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import map.Zone;
import server.Maintenance;
import utils.Util;

public class DeathOrAliveArenaManager implements Runnable {

    private static DeathOrAliveArenaManager instance;
    private volatile long lastUpdate;
    private static final List<DeathOrAliveArena> list = new ArrayList<>();

    public static DeathOrAliveArenaManager gI() {
        if (instance == null) {
            instance = new DeathOrAliveArenaManager();
        }
        return instance;
    }

    @Override
    public void run() {
        while (!Maintenance.isRunning) {
            try {
//                long start = System.currentTimeMillis();
//                update();
//                long timeUpdate = System.currentTimeMillis() - start;
//                if (1000 - timeUpdate > 0) {
//                    Thread.sleep(1000 - timeUpdate);
//                }
                long start = System.currentTimeMillis();
                update();
                Functions.sleep(Math.max(1000 - (System.currentTimeMillis() - start), 10));

            } catch (Exception ex) {
            }
        }
    }

    public void update() {
        if (Util.canDoWithTime(lastUpdate, 1000)) {
            lastUpdate = System.currentTimeMillis();
            for (int i = list.size() - 1; i >= 0; i--) {
                if (i < list.size()) {
                    list.get(i).update();
                }
            }
        }
    }

    public void add(DeathOrAliveArena vdst) {
        list.add(vdst);
    }

    public void remove(DeathOrAliveArena vdst) {
        list.remove(vdst);
    }

    public DeathOrAliveArena getVDST(@NonNull Zone zone) {
        for (DeathOrAliveArena vdst : list) {
            if (vdst.getZone().equals(zone)) {
                return vdst;
            }
        }
        return null;
    }
}
