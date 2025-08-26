package boss;

/*
 *
 *
 * @author NTD
 */

public class TrungThuEventManager extends BossManager {

    private static TrungThuEventManager instance;

    public static TrungThuEventManager gI() {
        if (instance == null) {
            instance = new TrungThuEventManager();
        }
        return instance;
    }

}
