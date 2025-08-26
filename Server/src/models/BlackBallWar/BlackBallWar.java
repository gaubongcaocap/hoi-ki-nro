package models.BlackBallWar;

/*
 *
 *
 * @author NTD
 */

import EMTI.Functions;
import jdbc.daos.NDVSqlFetcher;
import jdbc.daos.PlayerDAO;
import player.Player;
import services.MapService;
import services.Service;
import services.func.ChangeMapService;
import utils.TimeUtil;
import utils.Util;

import lombok.Data;
import map.Zone;
import server.Client;
import server.Maintenance;

@Data
public class BlackBallWar implements Runnable {

    public static final int TIME_CAN_PICK_BLACK_BALL_AFTER_DROP = 5000;

    public static final byte X3 = 3;
    public static final byte X5 = 5;
    public static final byte X7 = 7;

    public static final int COST_X3 = 100000000;
    public static final int COST_X5 = 300000000;
    public static final int COST_X7 = 500000000;

    public static final byte HOUR_OPEN = 20;
    public static final byte MIN_OPEN = 0;
    public static final byte SECOND_OPEN = 0;

    public static final byte HOUR_CAN_PICK_DB = 20;
    public static final byte MIN_CAN_PICK_DB = 30;
    public static final byte SECOND_CAN_PICK_DB = 0;

    public static final byte HOUR_CLOSE = 21;
    public static final byte MIN_CLOSE = 0;
    public static final byte SECOND_CLOSE = 0;

    public static final int AVAILABLE = 5;
    private static final int TIME_WIN = 300000;

    public Zone zone;

    public BlackBallWar(Zone zone) {
        this.zone = zone;
        start();
    }

    private void start() {
    	Thread.ofVirtual()
        .name("Update Black Ball War Map " + zone.map.mapName + " Zone " + zone.zoneId);
    }

    @Override
    public void run() {
        while (!Maintenance.isRunning) {
            try {
                long startTime = System.currentTimeMillis();
                update();
                long elapsedTime = System.currentTimeMillis() - startTime;
                long sleepTime = 1000 - elapsedTime;
                if (sleepTime > 0) {
                    Functions.sleep(sleepTime);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void update() {
        if (!TimeUtil.isBlackBallWarOpen()) {
            zone.finishBlackBallWar = false;
        }
        for (int i = zone.getNumOfPlayers() - 1; i >= 0; i--) {
            try {
                updatePlayer(zone.getPlayers().get(i));
            } catch (Exception e) {
            }
        }
    }

    public void updatePlayer(Player player) {
        if (player.zone == null || !MapService.gI().isMapBlackBallWar(player.zone.map.mapId)) {
            return;
        }
        if (!TimeUtil.isBlackBallWarOpen()) {
            kickOutOfMap(player);
            return;
        }

        if (player.iDMark.isHoldBlackBall()) {
            if (Util.canDoWithTime(player.iDMark.getLastTimeHoldBlackBall(), TIME_WIN)) {
                win(player);
            } else if (Util.canDoWithTime(player.iDMark.getLastTimeNotifyTimeHoldBlackBall(), 10000)) {
                Service.gI().sendThongBao(player, "Cố giữ ngọc thêm "
                        + TimeUtil.getSecondLeft(player.iDMark.getLastTimeHoldBlackBall(), TIME_WIN / 1000)
                        + " giây nữa sẽ thắng");
                player.iDMark.setLastTimeNotifyTimeHoldBlackBall(System.currentTimeMillis());
            }
        }
    }

    private void win(Player player) {
        player.zone.finishBlackBallWar = true;
        int star = player.iDMark.getTempIdBlackBallHold() - 371;
        player.rewardBlackBall.reward((byte) star);
        Service.gI().sendThongBao(player, "Chúc mừng bạn đã "
                + "dành được Ngọc rồng " + star + " sao đen cho bang");
        if (player.clan != null) {
            player.clan.members.forEach(m -> {
                if (Client.gI().getPlayer(m.id) != null) {
                    Player p = Client.gI().getPlayer(m.id);
                    if (p != null) {
                        p.rewardBlackBall.reward((byte) star);
                    }
                } else {
                    Player p = NDVSqlFetcher.loadById(m.id);
                    if (p != null) {
                        p.rewardBlackBall.reward((byte) star);
                        PlayerDAO.updatePlayer(p);
                    }
                }
            });
        }

        kickAllPlayersOutOfMap(player.zone);
    }

    private void kickOutOfMap(Player player) {
        if (player.cFlag == 8) {
            Service.gI().changeFlag(player, Util.nextInt(1, 7));
        }

        Service.gI().sendThongBao(player, "Trò chơi tìm ngọc hôm nay đã kết thúc, hẹn gặp lại vào 20h ngày mai");

        ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 24, -1, 250);
    }

    private void kickAllPlayersOutOfMap(Zone zone) {
        for (int i = zone.getPlayers().size() - 1; i >= 0; i--) {
            if (i < zone.getPlayers().size()) {
                Player pl = zone.getPlayers().get(i);
                kickOutOfMap(pl);
            }
        }
    }

}
