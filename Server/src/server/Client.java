package server;

/*
 *
 *
 * @author Entidi (NTD - Tấn Đạt)
 */
import EMTI.Functions;
import jdbc.DBConnecter;
import jdbc.daos.PlayerDAO;
import map.ItemMap;
import player.Player;
import network.SessionManager;
import network.inetwork.ISession;
import server.io.MySession;
import services.Service;
import services.func.ChangeMapService;
import services.func.SummonDragon;
import services.func.TransactionService;
import services.NgocRongNamecService;
import utils.Logger;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import models.DragonNamecWar.TranhNgoc;
import services.func.SummonDragonNamek;
import utils.Util;

public class Client implements Runnable {

    private static Client instance;

    private final Map<Long, Player> players_id = new HashMap<>();
    private final Map<Integer, Player> players_userId = new HashMap<>();
    private final Map<String, Player> players_name = new HashMap<>();
    private final List<Player> players = new ArrayList<>();

    private Client() {
        Thread.startVirtualThread(this);
    }

    public List<Player> getPlayers() {
        return this.players;
    }

    public static Client gI() {
        if (instance == null) {
            instance = new Client();
        }
        return instance;
    }

    public void put(Player player) {
        if (!players_id.containsKey(player.id)) {
            this.players_id.put(player.id, player);
        }
        if (!players_name.containsValue(player)) {
            this.players_name.put(player.name, player);
        }
        if (!players_userId.containsValue(player)) {
            this.players_userId.put(player.getSession().userId, player);
        }
        if (!players.contains(player)) {
            this.players.add(player);
        }

    }

    // In Client.java
    private void remove(MySession session) {
        String ipAddress = null;
        Player playerToRemove = null;
        int userId = -1;

        if (session != null) {
            playerToRemove = session.player;
            userId = session.userId;
            ipAddress = session.ipAddress; // Lấy trực tiếp từ biến thành viên
            if (ipAddress != null && (ipAddress.isEmpty() || ipAddress.equalsIgnoreCase("UNKNOWN"))) {
                ipAddress = null; // Coi IP không hợp lệ là null
            }
        } else {
            return; // Thoát nếu session null
        }

        if (playerToRemove != null) {
            try {
                this.remove(playerToRemove);
            } catch (Exception e) {
                Logger.logException(Client.class, e, "Error removing player object for " + playerToRemove.name);
            }
            try {
                playerToRemove.dispose();
            } catch (Exception e) {
                Logger.logException(Client.class, e, "Error disposing player " + playerToRemove.name);
            }
            if (session.player == playerToRemove) { // Chỉ gán null nếu nó thực sự là player đó
                session.player = null;
            }
        }

        if (session.joinedGame) {
            session.joinedGame = false;
            try {
                if (userId != -1) {
                    DBConnecter.executeUpdate("update account set last_time_logout = ? where id = ?", new Timestamp(System.currentTimeMillis()), userId);
                }
            } catch (Exception e) {
                Logger.logException(Client.class, e, "Error updating last_time_logout for userId " + userId);
            }
        }

        if (ipAddress != null) {
            try {
                ServerManager.gI().disconnect(ipAddress); 
            } catch (Exception e) {
                Logger.logException(ServerManager.class, e, "Error calling ServerManager.disconnect for IP " + ipAddress);
            }
        }
    }

    private void remove(Player player) {
        this.players_id.remove(player.id);
        this.players_name.remove(player.name);
        this.players_userId.remove(player.getSession().userId);
        this.players.remove(player);
        if (!player.beforeDispose) {
            player.beforeDispose = true;
            player.mapIdBeforeLogout = player.zone.map.mapId;
            TranhNgoc.gI().removePlayersBlue(player);
            TranhNgoc.gI().removePlayersRed(player);
            if (player.idNRNM != -1) {
                ItemMap itemMap = new ItemMap(player.zone, player.idNRNM, 1, player.location.x, player.location.y, -1);
                Service.gI().dropItemMap(player.zone, itemMap);
                NgocRongNamecService.gI().pNrNamec[player.idNRNM - 353] = "";
                NgocRongNamecService.gI().idpNrNamec[player.idNRNM - 353] = -1;
                player.idNRNM = -1;
            }
            ChangeMapService.gI().exitMap(player);
            TransactionService.gI().cancelTrade(player);
            if (player.clan != null) {
                player.clan.removeMemberOnline(null, player);
            }
            if (SummonDragon.gI().playerSummonShenron != null
                    && SummonDragon.gI().playerSummonShenron.id == player.id) {
                SummonDragon.gI().isPlayerDisconnect = true;
            }
            if (SummonDragonNamek.gI().playerSummonShenron != null
                    && SummonDragonNamek.gI().playerSummonShenron.id == player.id) {
                SummonDragonNamek.gI().isPlayerDisconnect = true;
            }
            if (player.shenronEvent != null) {
                player.shenronEvent.isPlayerDisconnect = true;
            }
            if (player.mobMe != null) {
                player.mobMe.mobMeDie();
            }
            if (player.pet != null) {
                if (player.pet.mobMe != null) {
                    player.pet.mobMe.mobMeDie();
                }
                ChangeMapService.gI().exitMap(player.pet);
            }
        }
        PlayerDAO.updatePlayer(player);
    }

    public void kickSession(MySession session) {
        if (session != null) {
            session.disconnect();
            this.remove(session);
        }
    }

    public Player getPlayer(long playerId) {
        return this.players_id.get(playerId);
    }

    public Player getRandPlayer() {
        if (this.players.isEmpty()) {
            return null;
        }
        return this.players.get(Util.nextInt(players.size()));
    }

    public Player getPlayerByUser(int userId) {
        return this.players_userId.get(userId);
    }

    public Player getPlayer(String name) {
        return this.players_name.get(name);
    }

    public Player getPlayerByID(int playerId) {
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            if (player != null && player.id == playerId) {
                return player;
            }
        }
        return null;
    }

    public void close() {
        Logger.log(Logger.YELLOW, "BEGIN KICK OUT SESSION " + players.size() + "\n");
        while (!players.isEmpty()) {
            Player pl = players.remove(0);
            if (pl != null && pl.getSession() != null) {
                this.kickSession((MySession) pl.getSession());
            }
        }
        Logger.success("SUCCESSFUL\n");
    }

    private void update() {
        for (int i = SessionManager.gI().getSessions().size() - 1; i >= 0; i--) {
            ISession s = SessionManager.gI().getSessions().get(i);
            MySession session = (MySession) s;
            if (session == null) {
                SessionManager.gI().getSessions().remove(i);
                continue;
            }
            if (session.timeWait > 0) {
                session.timeWait--;
                if (session.timeWait == 0) {
                    kickSession(session);
                }
            }
        }
    }

    @Override
    public void run() {
        while (ServerManager.isRunning) {
            long st = System.currentTimeMillis();
            try {
                update();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Functions.sleep(Math.max(1000 - (System.currentTimeMillis() - st), 10));
        }
    }

    public void show(Player player) {
        String txt = "";
        txt += "sessions: " + SessionManager.gI().getNumSession() + "\n";
        txt += "players_id: " + players_id.size() + "\n";
        txt += "players_userId: " + players_userId.size() + "\n";
        txt += "players_name: " + players_name.size() + "\n";
        txt += "players: " + players.size() + "\n";
        Service.gI().sendThongBao(player, txt);
    }
}
