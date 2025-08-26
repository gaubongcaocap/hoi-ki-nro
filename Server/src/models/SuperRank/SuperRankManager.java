package models.SuperRank;


import EMTI.Functions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import map.Zone;
import models.Template.WaitSuperRank;
import player.Player;
import server.Client;
import server.Maintenance;

public class SuperRankManager implements Runnable {

    private final List<WaitSuperRank> waitList;

    private final List<SuperRank> list;

    private static SuperRankManager instance;

    private final Map<Long, Player> players_id;

    public static SuperRankManager gI() {
        if (instance == null) {
            instance = new SuperRankManager();
        }
        return instance;
    }

    public SuperRankManager() {
        waitList = new ArrayList<>();
        list = new ArrayList<>();
        players_id = new HashMap<>();
    }

    public void put(Player player) {
        this.players_id.put(player.id, player);
    }

    public Player getPlayer(long playerId) {
        return this.players_id.get(playerId);
    }

    public Map<Long, Player> getPlayers() {
        return this.players_id;
    }

    @Override
    public void run() {
        while (!Maintenance.isRunning) {
            long startTime = System.currentTimeMillis();
            try {
                Iterator<WaitSuperRank> iterator = waitList.iterator();
                while (iterator.hasNext()) {
                    WaitSuperRank wsp = iterator.next();
                    Player wPl = Client.gI().getPlayer(wsp.playerId);
                    if (wPl != null && wPl.zone != null && wPl.zone.map.mapId == 113) {
                        if (!SPRCheck(wPl.zone)) {
                            list.add(new SuperRank(wPl, wsp.rivalId, wPl.zone));
                            iterator.remove();
                        }
                    } else {
                        iterator.remove();
                    }
                }

            } catch (Exception ex) {
            }

            long elapsedTime = System.currentTimeMillis() - startTime;
            long sleepTime = 500 - elapsedTime;
            if (sleepTime > 0) {
                Functions.sleep(sleepTime);
            }
        }
    }

    public boolean canCompete(Player player) {
        return !currentlyCompeting(player) && !awaitingCompetition(player);
    }

    public boolean currentlyCompeting(Player player) {
        for (int i = list.size() - 1; i >= 0; i--) {
            SuperRank spr = list.get(i);
            if (spr.getPlayerId() == player.id || spr.getRivalId() == player.id) {
                return true;
            }
        }
        return false;
    }

    public boolean awaitingCompetition(Player player) {
        for (int i = waitList.size() - 1; i >= 0; i--) {
            WaitSuperRank wspr = waitList.get(i);
            if (wspr.playerId == player.id || wspr.rivalId == player.id) {
                return true;
            }
        }
        return false;
    }

    public boolean awaiting(Player player) {
        for (int i = waitList.size() - 1; i >= 0; i--) {
            WaitSuperRank wspr = waitList.get(i);
            if (wspr.playerId == player.id) {
                return true;
            }
        }
        return false;
    }

    public boolean SPRCheck(@NonNull Zone zone) {
        for (int i = list.size() - 1; i >= 0; i--) {
            SuperRank spr = list.get(i);
            if (spr.getZone().equals(zone)) {
                return true;
            }
        }
        return false;
    }

    public int ordinal(long id) {
        for (int i = 0; i < waitList.size(); i++) {
            if (waitList.get(i).playerId == id) {
                return i + 1;
            }
        }
        return -1;
    }

    public String getCompeting(Player pl) {
        for (int i = list.size() - 1; i >= 0; i--) {
            SuperRank spr = list.get(i);
            if (spr.getPlayerId() == pl.id) {
                return "VS " + spr.getRival().name + " kv: " + spr.getZone().zoneId;
            } else if (spr.getRivalId() == pl.id) {
                return "VS " + spr.getPlayer().name + " kv: " + spr.getZone().zoneId;
            }
        }
        return "";
    }

    public void addSPR(SuperRank spr) {
        list.add(spr);
    }

    public void removeSPR(SuperRank spr) {
        list.remove(spr);
    }

    public void addWSPR(long playerId, long rivalId) {
        waitList.add(new WaitSuperRank(playerId, rivalId));
    }

}
