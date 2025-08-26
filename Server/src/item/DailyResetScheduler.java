/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package item;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import map.ItemMap;

public class DailyResetScheduler {
    public static void startDailyReset() {
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            ItemMap.resetDailyItemCount();
        }, 0, 1, TimeUnit.DAYS); // Reset mỗi 24 giờ
    }
}
