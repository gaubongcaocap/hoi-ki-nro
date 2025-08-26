package models.ShenronEvent;

/*
 *
 *
 * @author NTD
 */
import EMTI.Functions;
import utils.Util;

import java.util.ArrayList;
import java.util.List;
import server.Maintenance;

public class ShenronEventManager implements Runnable {

    private static ShenronEventManager instance;
    private long lastUpdate;
    private static final List<ShenronEvent> list = new ArrayList<>();

    ;

    public static ShenronEventManager gI() {
        if (instance == null) {
            instance = new ShenronEventManager();
        }
        return instance;
    }

    @Override
    public void run() {
        while (!Maintenance.isRunning) {
            try {
                long start = System.currentTimeMillis();
                update();
                long timeUpdate = System.currentTimeMillis() - start;
                Functions.sleep(Math.max(1000 - timeUpdate, 10));
            } catch (Exception ex) {
            }
        }
    }

    public void update() {
        if (Util.canDoWithTime(lastUpdate, 1000)) {
            lastUpdate = System.currentTimeMillis();
            List<ShenronEvent> listCopy = new ArrayList<>();
            for (ShenronEvent se : list) {
                listCopy.add(se);
            }

            for (ShenronEvent se : listCopy) {
                try {
                    se.update();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            listCopy.clear();
        }
    }

    public void add(ShenronEvent se) {
        list.add(se);
    }

    public void remove(ShenronEvent se) {
        list.remove(se);
    }

}
