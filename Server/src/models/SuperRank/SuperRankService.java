package models.SuperRank;

/*
 *
 *
 * @author NTD
 */

import consts.ConstSuperRank;

import java.util.List;

import jdbc.daos.NDVSqlFetcher;
import jdbc.daos.SuperRankDAO;
import map.Map;
import map.Zone;
import network.Message;
import player.Player;
import server.Client;
import services.MapService;
import services.Service;
import utils.TimeUtil;
import utils.Util;

public class SuperRankService {

    private static SuperRankService instance;

    public static SuperRankService gI() {
        if (instance == null) {
            instance = new SuperRankService();
        }
        return instance;
    }

    public void competing(Player player, long id) {
        if (player.zone.map.mapId != 113 || id == -1) {
            return;
        }
        int menuType = player.iDMark.getMenuType();
        Player pl = loadPlayer(id);
        if (pl == null) {
            return;
        }
        if (SuperRankManager.gI().currentlyCompeting(player)) {
            Service.gI().sendThongBao(player, ConstSuperRank.TEXT_DANG_THI_DAU);
            return;
        } else if (SuperRankManager.gI().currentlyCompeting(pl)) {
            Service.gI().sendThongBao(player, ConstSuperRank.TEXT_DOI_THU_DANG_THI_DAU);
            return;
        } else if (SuperRankManager.gI().awaitingCompetition(player)) {
            Service.gI().sendThongBao(player, ConstSuperRank.TEXT_DANG_CHO);
            return;
        } else if (SuperRankManager.gI().awaitingCompetition(pl)) {
            Service.gI().sendThongBao(player, ConstSuperRank.TEXT_DOI_THU_CHO_THI_DAU);
            return;
        } else if (player.superRank.rank < pl.superRank.rank) {
            Service.gI().sendThongBao(player, ConstSuperRank.TEXT_DUOI_HANG);
            return;
        } else if (player.superRank.rank == pl.superRank.rank) {
            Service.gI().sendThongBao(player, ConstSuperRank.TEXT_CHINH_MINH);
            return;
        } else if (pl.superRank.rank < 10 && player.superRank.rank - pl.superRank.rank > 2) {
            Service.gI().sendThongBao(player, ConstSuperRank.TEXT_KHONG_THE_THI_DAU_TREN_2_HANG);
            return;
        } else if (player.superRank.ticket <= 0 && player.inventory.getGemAndRuby() < 1) {
            Service.gI().sendThongBao(player, "Bạn không đủ ngọc, còn thiếu 1 ngọc nữa");
            return;
        }
        switch (menuType) {
            case 0 -> {
                Service.gI().sendThongBao(player, ConstSuperRank.TEXT_TOP_100);
            }
            case 1 -> {
                if (SuperRankManager.gI().SPRCheck(player.zone)) {
                    Service.gI().sendThongBao(player, ConstSuperRank.TEXT_CHO_IT_PHUT);
                    SuperRankManager.gI().addWSPR(player.id, pl.id);
                } else {
                    SuperRankManager.gI().addSPR(new SuperRank(player, id, player.zone));
                }
            }
            case 2 -> {
                SuperRankManager.gI().addSPR(new SuperRank(player, id, getZone(113)));
            }
        }
    }

    public void topList(Player player, int type) {
        long st = System.currentTimeMillis();
        player.iDMark.setMenuType(type);
        Message msg = null;
        try {
            List<Long> list = type == 0
                    ? SuperRankDAO.getPlayerListInRank(player.superRank.rank, 100)
                    : player.superRank.rank <= 10 ? SuperRankDAO.getPlayerListInRank(player.superRank.rank, 11) : SuperRankDAO.getPlayerListInRankRange(player.superRank.rank, 11);
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top 100 Cao Thủ");
            msg.writer().writeByte(list.size());
            for (int i = 0; i < list.size(); i++) {
                Player pl = loadPlayer(list.get(i));
                msg.writer().writeInt(pl.superRank.rank);
                msg.writer().writeInt((int) pl.id);
                msg.writer().writeShort(pl.getHead());
                if (player.getSession().version >= 214) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(pl.getBody());
                msg.writer().writeShort(pl.getLeg());
                msg.writer().writeUTF(pl.name);
                msg.writer().writeUTF(textStatus(pl));
                msg.writer().writeUTF(textInfoNew(pl));
            }
            player.sendMessage(msg);
            msg.cleanup();
            list.clear();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public Player loadPlayer(long id) {
        Player player = NDVSqlFetcher.loadById(id);
        SuperRankManager.gI().put(player);
        return player;
    }

    public Player getPlayer(long id) {
        return Client.gI().getPlayer(id);
    }

    public String textInfo(Player pl) {
        pl.setClothes.setup();
        if (pl.pet != null) {
            pl.pet.setClothes.setup();
        }
        pl.nPoint.calPoint();
        String text = "HP " + Util.chiaNho(pl.nPoint.hpMax) + "\n";
        text += "Sức đánh " + Util.chiaNho(pl.nPoint.dame) + "\n";
        text += "Giáp " + Util.chiaNho(pl.nPoint.def) + "\n";
        text += pl.superRank.win + ":" + pl.superRank.lose;
        return text;
    }

    public String textInfoNew(Player pl) {
        if (pl == null || pl.nPoint == null) {
            return "Không xác định!";
        }
//        pl.setClothes.setup();
//        if (pl.pet != null) {
//            pl.pet.setClothes.setup();
//        }
        pl.nPoint.calPoint();
        String text = "HP: " + Util.chiaNho(pl.nPoint.hpMax) + "\n";
        text += "Sức đánh: " + Util.chiaNho(pl.nPoint.dame) + "\n";
        text += "Giáp: " + Util.chiaNho(pl.nPoint.def) + "\n";
        if (pl.superRank.history.isEmpty()) {
            text += "Thắng/Thua: " + pl.superRank.win + "/" + pl.superRank.lose;
        } else {
            text += pl.superRank.win + ":" + pl.superRank.lose;
        }
        for (int i = 0; i < pl.superRank.history.size(); i++) {
            String history = pl.superRank.history.get(i);
            long lastTime = pl.superRank.lastTime.get(i);
            text += "\n" + history + " " + TimeUtil.getTimeLeft(lastTime);
        }
        return text;
    }

    public String textStatus(Player pl) {
        if (SuperRankManager.gI().awaitingCompetition(pl)) {
            return ConstSuperRank.TEXT_DANG_CHO;
        } else if (SuperRankManager.gI().currentlyCompeting(pl)) {
            return SuperRankManager.gI().getCompeting(pl);
        }
        return textReward(pl.superRank.rank);
    }

    // Phương thức chung để tính phần thưởng ngọc
    private int getRewardAmount(int rank) {
        if (rank == 1) {
            return 1000; // Top 1 nhận 1000 ngọc
        } else if (rank >= 2 && rank <= 10) {
            return 100; // Top 2 đến Top 10 nhận 100 ngọc
        } else if (rank >= 11 && rank <= 100) {
            return 20; // Top 11 đến Top 100 nhận 20 ngọc
        } else if (rank >= 101 && rank <= 199) {
            return 10; // Top 101 đến Top 199 nhận 10 ngọc
        }
        return 0; 
    }


    public String textReward(int rank) {
        int rewardAmount = getRewardAmount(rank);
        if (rewardAmount > 0) {
            return "+" + rewardAmount + " ngọc mỗi ngày";
        } else {
            return "Không có phần thưởng";
        }
    }
    public int reward(int rank) {
        return getRewardAmount(rank);
    }

    public Zone getZone(int mapId) {
        Map map = MapService.gI().getMapById(mapId);
        try {
            if (map != null) {
                int zoneId = 0;
                while (zoneId < map.zones.size()) {
                    Zone zonez = map.zones.get(zoneId);
                    if (!SuperRankManager.gI().SPRCheck(zonez)) {
                        return zonez;
                    }
                    zoneId++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
