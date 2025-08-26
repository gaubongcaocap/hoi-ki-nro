package boss;

/*
 *
 *
 * @author NTD
 */
import EMTI.Functions;
import server.Maintenance;

public class GasDestroyManager extends BossManager {

    private static GasDestroyManager instance;

    public static GasDestroyManager gI() {
        if (instance == null) {
            instance = new GasDestroyManager();
        }
        return instance;
    }

    @Override
    public void run() {
        while (!Maintenance.isRunning) {
            try {
                long st = System.currentTimeMillis();
                for (int i = this.bosses.size() - 1; i >= 0; i--) {
                    if (i < this.bosses.size()) {
                        Boss boss = this.bosses.get(i);
                        try {
                            boss.update();
                        } catch (Exception e) {
                            e.printStackTrace();
                            try {
                                removeBoss(boss);
                            } catch (Exception ex) {
                            }
                        }
                    }
                }
//                if (150 - (System.currentTimeMillis() - st) > 0) {
//                    Thread.sleep(150 - (System.currentTimeMillis() - st));
//                }
                Functions.sleep(Math.max(150 - (System.currentTimeMillis() - st), 10));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
