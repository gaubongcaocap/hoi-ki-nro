package models.MajinBuu;

/*
 *
 *
 * @author NTD
 */

import EMTI.Functions;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import map.Zone;
import map.MaBuHold;
import player.Player;
import server.Maintenance;
import services.MapService;
import services.func.ChangeMapService;
import utils.TimeUtil;

@Data
public final class MajinBuu14H implements Runnable {

    public static final int AVAILABLE = 7;
    public int id;
    public final List<Zone> zones;

    public MajinBuu14H(int id) {
        this.id = id;
        this.zones = new ArrayList<>();
        this.init();
    }

    public void init() {
        Thread.ofVirtual()
                .name("MajinBuu 14H - Id : " + id)
                .start(this);
    }

    @Override
    public void run() {
        while (!Maintenance.isRunning) {
            try {
                long startTime = System.currentTimeMillis();
                if (!TimeUtil.isMabu14HOpen()) {
                    finish();
                    return;
                }
                update();
                long elapsedTime = System.currentTimeMillis() - startTime;
                long sleepTime = 150 - elapsedTime;
                if (sleepTime > 0) {
                    Functions.sleep(sleepTime);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void update() {
        if (!TimeUtil.isMabu14HOpen()) {
            finish();
            return;
        }
        for (int j = zones.size() - 1; j >= 0; j--) {
            Zone zone = zones.get(j);
            for (MaBuHold hold : zone.maBuHolds) {
                if (hold.player != null && hold.player.maBuHold == null && hold.player.zone != null) {
                    hold.player = null;
                }
            }
        }
    }

    public MaBuHold getMaBuHold() {
        for (Zone zone : this.zones) {
            if (zone.map.mapId == 128) {
                for (MaBuHold hold : zone.maBuHolds) {
                    if (hold.player == null) {
                        return hold;
                    }
                }
            }
        }
        return null;
    }

    public Zone getMapById(int mapId) {
        for (Zone zone : this.zones) {
            if (zone.map.mapId == mapId) {
                return zone;
            }
        }
        return null;
    }

    private void finish() {
        for (int j = zones.size() - 1; j >= 0; j--) {
            Zone zone = zones.get(j);
            for (int i = zone.getPlayers().size() - 1; i >= 0; i--) {
                if (i < zone.getPlayers().size()) {
                    Player pl = zone.getPlayers().get(i);
                    kickOut(pl);
                }
            }
        }
    }

    private void kickOut(Player player) {
        if (MapService.gI().isMapMabu2H(player.zone.map.mapId) && !player.isAdmin()) {
            ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, -1, 336);
        }
    }

}
