/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package NTDManger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import jdbc.daos.PlayerDAO;

import server.Client;
import utils.Logger;
import utils.TimeUtil;

/**
 *
 * @author Lucy An Trom
 */
public class NTDManager {

    private static NTDManager instance = null;

    // Static method
    // Static method to create instance of Singleton class
    public static synchronized NTDManager getInstance() {
        if (instance == null) {
            instance = new NTDManager();
        }
        return instance;
    }

    private ScheduledExecutorService scheduler;

    public void startAutoSave() {
        scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {
            try {
                handleAutoSave();
            } catch (Exception e) {
                System.out.println("[AutoSaveManager] start autosave error: " + e.getLocalizedMessage());
            }
        }, 60, 90, TimeUnit.SECONDS);
    }

    public void handleAutoSave() {
         
        Client.gI().getPlayers().forEach(player -> {
            long st = System.currentTimeMillis();
            PlayerDAO.updatePlayer(player);
            Logger.success(TimeUtil.getCurrHour() + "h" + TimeUtil.getCurrMin() + "m: Tự động lưu dữ liệu người chơi thành công! " + (System.currentTimeMillis() - st) + "ms\n");

        });
    }

}
