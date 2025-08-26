package models.SuperRank;

/*
 *
 *
 * @author NTD
 */

import EMTI.Functions;
import boss.Boss;
import boss.BossStatus;
import boss.boss_manifest.SuperRank.Rival;
import consts.ConstPlayer;
import consts.ConstSuperRank;
import jdbc.daos.SuperRankDAO;
import lombok.Data;
import map.Zone;
import matches.pvp.DHVT;
import player.Player;
import server.Maintenance;
import server.ServerNotify;
import services.ChatGlobalService;
import services.PlayerService;
import services.Service;
import services.func.ChangeMapService;
import utils.TimeUtil;
import utils.Util;
 
@Data
public final class SuperRank implements Runnable {

    private Zone zone;
    private boolean isCompeting;
    private long playerId;
    private long rivalId;
    private Player player;
    private Boss rival;
    public int timeUp;
    public int timeDown;
    public int rankWin;
    public int rankLose;
    public boolean win;
    public int error;

    public SuperRank(Player player, long rivalId, Zone zone) {
        try {
            this.playerId = player.id;
            this.rivalId = rivalId;
            this.zone = zone;
            this.player = player;
            this.player.isPKDHVT = true;
            this.rankLose = player.superRank.rank;
            Player riv = SuperRankService.gI().loadPlayer(rivalId);
            this.rankWin = riv.superRank.rank;
            riv.nPoint.calPoint();
            this.rival = new Rival(player, riv);
            this.zone.isCompeting = true;
            this.zone.rank1 = player.superRank.rank;
            this.zone.rank2 = riv.superRank.rank;
            this.zone.rankName1 = player.name;
            this.zone.rankName2 = riv.name;
            init();
        } catch (Exception e) {
            dispose();
        }
    }

    public void init() {
        timeUp = 0;
        timeDown = 180;
        rankLose = player.superRank.rank;
        isCompeting = true;
        win = false;
        if (player.zone.zoneId != zone.zoneId) {
            ChangeMapService.gI().changeZone(player, zone.zoneId);
        }
        Thread.ofVirtual()
                .name("Super Rank")
                .start(this);

    }

    @Override
    public void run() {
        while (!Maintenance.isRunning && isCompeting) {
            long startTime = System.currentTimeMillis();
            try {
                update();
            } catch (Exception e) {
                if (error < 5) {
                    error++;
                    System.err.println(e);
                }
            }
            long elapsedTime = System.currentTimeMillis() - startTime;
            long sleepTime = 1000 - elapsedTime;
            if (sleepTime > 0) {
                Functions.sleep(sleepTime);
            }
        }
    }

    public void update() {
        if (win) {
            return;
        }
        if (timeUp < 5) {
            switch (timeUp) {
                case 0 -> {
                    Service.gI().sendThongBao(player, "Trận đấu bắt đầu");
                    Service.gI().setPos0(player, 334, 264);
                    Service.gI().setPos0(rival, 434, 264);
                }
                case 2 -> {
                    Service.gI().chat(rival, ConstSuperRank.TEXT_SAN_SANG_CHUA);
                }
                case 3 -> {
                    Service.gI().chat(player, ConstSuperRank.TEXT_SAN_SANG);
                    PlayerService.gI().changeAndSendTypePK(player, ConstPlayer.PK_PVP);
                    PlayerService.gI().changeAndSendTypePK(rival, ConstPlayer.PK_PVP);
                    Service.gI().sendTypePK(player, rival);
                    new DHVT(player, rival);
                }
                case 4 -> {
                    rival.changeStatus(BossStatus.ACTIVE);
                }
            }
            timeUp++;
            return;
        }
        if (timeDown > 0) {
            timeDown--;
            if (player != null && player.isPKDHVT && !player.lostByDeath && player.location != null && !player.isDie() && player.zone != null && player.zone.equals(zone)) {
                if (rival == null || rival.zone == null || rival.isDie()) {
                    win();
                }
            } else {
                lose();
            }
        } else {
            lose();
        }
    }

    public void win() {
        win = true;
        try {
            finish();
            Player plWin = SuperRankService.gI().loadPlayer(playerId);
            Player plLose = SuperRankService.gI().loadPlayer(rivalId);
            plWin.superRank.win++;
            plLose.superRank.lose++;
            if (plWin.superRank.ticket == 0 && plWin.inventory.getGemAndRuby() > 0) {
                plWin.inventory.subGemAndRuby(1);
            }
            plWin.superRank.rank = rankWin;
            plWin.superRank.history("Hạ " + plLose.name + "[" + rankLose + "]", System.currentTimeMillis());
            SuperRankDAO.updatePlayer(plWin);
            plLose.superRank.rank = rankLose;
            plLose.superRank.history("Thua " + plWin.name + "[" + rankWin + "]", System.currentTimeMillis());
            SuperRankDAO.updatePlayer(plLose);
            if (rankWin <= 10) {
                String message = ConstSuperRank.TEXT_TOP_10.replaceAll("%1", plWin.name).replaceAll("%2", rankWin + "");
                ServerNotify.gI().notify(message);
                ChatGlobalService.gI().autoChatGlobal(plWin, "[Hệ thống] " + message);
            }
            if (player != null && player.zone != null) {
                player.superRank.win++;
                if (player.superRank.ticket == 0 && player.inventory.getGemAndRuby() > 0) {
                    player.inventory.subGemAndRuby(1);
                    Service.gI().sendMoney(player);
                }
                player.superRank.rank = rankWin;
                player.superRank.history("Hạ " + plLose.name + "[" + rankLose + "]", System.currentTimeMillis());
                Service.gI().chat(player, ConstSuperRank.TEXT_THANG.replaceAll("%1", rankWin + ""));
            }
            Player rv = SuperRankService.gI().getPlayer(rivalId);
            if (rv != null && rv.zone != null) {
                rv.superRank.lose++;
                rv.superRank.rank = rankLose;
                rv.superRank.history("Thua " + plWin.name + "[" + rankWin + "]", System.currentTimeMillis());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        dispose();
    }

    public void lose() {
        try {
            finish();
            Player plWin = SuperRankService.gI().loadPlayer(rivalId);
            Player plLose = SuperRankService.gI().loadPlayer(playerId);
            plWin.superRank.win++;
            plLose.superRank.lose++;
            if (plLose.superRank.ticket > 0) {
                plLose.superRank.ticket--;
            } else if (plLose.inventory.getGemAndRuby() > 0) {
                plLose.inventory.subGemAndRuby(1);
                Service.gI().sendMoney(plLose);
            }
            plWin.superRank.rank = rankWin;
            plWin.superRank.history("Hạ " + plLose.name + "[" + rankLose + "]", System.currentTimeMillis());
            SuperRankDAO.updatePlayer(plWin);
            plLose.superRank.rank = rankLose;
            plLose.superRank.history("Thua " + plWin.name + "[" + rankWin + "]", System.currentTimeMillis());
            SuperRankDAO.updatePlayer(plLose);
            if (player != null && player.zone != null) {
                player.superRank.lose++;
                if (player.superRank.ticket > 0) {
                    player.superRank.ticket--;
                } else if (player.inventory.getGemAndRuby() > 0) {
                    player.inventory.subGemAndRuby(1);
                    Service.gI().sendMoney(player);
                }
                player.superRank.rank = rankLose;
                player.superRank.history("Thua " + plWin.name + "[" + rankWin + "]", System.currentTimeMillis());
                Service.gI().chat(player, ConstSuperRank.TEXT_THUA);
            }
            Player rv = SuperRankService.gI().getPlayer(rivalId);
            if (rv != null && rv.zone != null) {
                rv.superRank.win++;
                rv.superRank.rank = rankWin;
                rv.superRank.history("Hạ " + plLose.name + "[" + rankLose + "]", System.currentTimeMillis());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        dispose();
    }

    public void finish() {
        if (rival != null) {
            rival.leaveMap();
        }
        if (player != null && player.zone != null && player.zone.equals(zone)) {
            if (player.isDie()) {
                Service.gI().hsChar(player, Util.maxIntValue(player.nPoint.hpMax), Util.maxIntValue(player.nPoint.mpMax));
            }
            PlayerService.gI().changeAndSendTypePK(player, ConstPlayer.NON_PK);
            Service.gI().sendPlayerVS(player, null, (byte) 0);
        }
    }

    public void dispose() {
        if (player != null && player.location != null) {
            Service.gI().setPos(player, Util.nextInt(250, 450), 360);
        }
        if (this.zone != null) {
            this.zone.isCompeting = false;
            this.zone.rank1 = -1;
            this.zone.rank2 = -1;
            this.zone.rankName1 = null;
            this.zone.rankName2 = null;
        }
        isCompeting = false;
        if (player != null) {
            player.isPKDHVT = false;
            player = null;
        }
        if (rival != null) {
            rival.dispose();
        }
        rival = null;
        zone = null;
        playerId = -1;
        rivalId = -1;
        rankWin = -1;
        rankLose = -1;
        SuperRankManager.gI().removeSPR(this);
    }

}
