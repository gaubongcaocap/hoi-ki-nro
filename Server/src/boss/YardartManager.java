package boss;

/*
 *
 *
 * @author NTD
 */

public class YardartManager extends BossManager {

    private static YardartManager instance;

    public static YardartManager gI() {
        if (instance == null) {
            instance = new YardartManager();
        }
        return instance;
    }

}
