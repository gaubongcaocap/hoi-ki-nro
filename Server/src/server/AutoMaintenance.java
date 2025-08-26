package server;

/*
 *
 *
 * @author NTD
 */

import EMTI.Functions;
import java.time.LocalTime;
import utils.Logger;

public class AutoMaintenance extends Thread {

    public static boolean AutoMaintenance = false; // Bật/tắt bảo trì tự động
    public static final int hours = 5; // Giờ bảo trì
    public static final int mins = 30; // Phút bảo trì
    private static AutoMaintenance instance;
    public static boolean isRunning;

    public static AutoMaintenance gI() {
        if (instance == null) {
            instance = new AutoMaintenance();
        }
        return instance;
    }

    @Override
    public void run() {
        while (!Maintenance.isRunning && !isRunning) {
            try {
                if (AutoMaintenance) {
                    LocalTime currentTime = LocalTime.now();
                    if (currentTime.getHour() == hours && currentTime.getMinute() == mins) {
                        Logger.log(Logger.PURPLE, "Đang tiến hành quá trình bảo trì tự động\n");
                        Maintenance.gI().start(60);
                        isRunning = true;
                        AutoMaintenance = false;
                    }
                }
                Functions.sleep(1000);
            } catch (Exception e) {
            }
        }
    }

}
