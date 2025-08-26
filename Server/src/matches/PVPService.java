package matches;

/*
 *
 *
 * @author NTD
 */
import matches.pvp.TraThu;
import matches.pvp.ThachDau;
import consts.ConstNpc;
import consts.ConstTranhNgocNamek;
import map.Zone;
import player.Player;
import network.Message;
import server.Client;
import services.NpcService;
import services.Service;
import services.func.ChangeMapService;
import utils.Util;
import java.io.IOException;
import matches.pvp.LuyenTap;

public class PVPService {

    private static final int[] GOLD_CHALLENGE = {1000000, 10000000, 100000000};
    private String[] optionsGoldChallenge;
    //cmd controller
    private static final byte OPEN_GOLD_SELECT = 0;
    private static final byte ACCEPT_PVP = 1;

    private static PVPService instance;

    public static PVPService gI() {
        if (instance == null) {
            instance = new PVPService();
        }
        return instance;
    }

    public PVPService() {
        this.optionsGoldChallenge = new String[GOLD_CHALLENGE.length];
        for (int i = 0; i < GOLD_CHALLENGE.length; i++) {
            this.optionsGoldChallenge[i] = Util.numberToMoney(GOLD_CHALLENGE[i]) + " vàng";
        }
    }

    //**************************************************************************THÁCH ĐẤU
    public void controllerThachDau(Player player, Message message) {
        try {
            byte action = message.reader().readByte();
            byte type = message.reader().readByte();
            int playerId = message.reader().readInt();
            Player plMap = player.zone.getPlayerInMap(playerId);
            switch (type) {
                case 3:
                    switch (action) {
                        case OPEN_GOLD_SELECT:
                            if (Client.gI().getPlayer(playerId) == null) {
                                if (plMap != null && plMap.isBoss) {
                                    NpcService.gI().createMenuConMeo(player, ConstNpc.IGNORE_MENU,
                                            -1, plMap.name + " (sức mạnh " + Util.numberToMoney(plMap.nPoint.power)
                                            + ")\nBạn muốn cược bao nhiêu vàng?",
                                            this.optionsGoldChallenge);
                                    return;
                                }
                                Service.gI().sendThongBao(player, "Đối thủ đã thoát game");
                                return;
                            }
                            openSelectGold(player, plMap);
                            break;
                        case ACCEPT_PVP:
                            acceptPVP(player);
                            break;
                    }
                    break;
                case 4:
                    switch (action) {
                        case OPEN_GOLD_SELECT:
                            if (Client.gI().getPlayer(playerId) == null) {
                                Service.gI().sendThongBao(player, "Đối thủ đã thoát game");
                                return;
                            }
                            sendInvitePVP(player, plMap);
                            break;
                        case ACCEPT_PVP:
                            acceptPVP2(player);
                            break;
                    }
                    break;
            }
        } catch (IOException ex) {

        }
    }

    private void openSelectGold(Player pl, Player plMap) {
        if (pl == null || plMap == null) {
            return;
        }
        if (pl.pvp != null || plMap.pvp != null) {
            Service.gI().hideWaitDialog(pl);
            Service.gI().sendThongBao(pl, "Đang giao đấu không thể mời.");
            return;
        }
        pl.iDMark.setIdPlayThachDau(plMap.id);
        NpcService.gI().createMenuConMeo(pl, ConstNpc.MAKE_MATCH_PVP,
                -1, plMap.name + " (sức mạnh " + Util.numberToMoney(plMap.nPoint.power)
                + ")\nBạn muốn cược bao nhiêu vàng?",
                this.optionsGoldChallenge);
    }

    public void sendInvitePVP(Player pl, byte selectGold) {
        if (pl == null) {
            return;
        }
        // Chưa thể thách đấu hoặc tập luyện lúc này
        Player plMap = pl.zone.getPlayerInMap(pl.iDMark.getIdPlayThachDau());
        if (plMap == null) {
            Service.gI().hideWaitDialog(pl);
            if (Client.gI().getPlayer(pl.iDMark.getIdPlayThachDau()) == null) {
                Service.gI().sendThongBao(pl, "Đối thủ đã thoát game");
                return;
            }
            Service.gI().sendThongBao(pl, "Đối thủ đã rời map");
            return;
        }
        if (plMap.getSession() == null || !plMap.getSession().actived) {
            Service.gI().sendThongBao(pl, "Đối thủ chưa kích hoạt tài khoản");
            return;
        }
        int goldThachDau = GOLD_CHALLENGE[selectGold];
        if (pl.inventory.gold < goldThachDau) {
            Service.gI().sendThongBao(pl, "Bạn chỉ có " + pl.inventory.gold + " vàng, không đủ tiền cược");
            return;
        }
        if (plMap.inventory.gold < goldThachDau) {
            Service.gI().sendThongBao(pl, "Đối thủ chỉ có " + plMap.inventory.gold + " vàng, không đủ tiền cược");
            return;
        }

        plMap.iDMark.setIdPlayThachDau(pl.id);
        plMap.iDMark.setGoldThachDau(goldThachDau);

        //Gửi message
        Message msg = null;
        try {
            msg = new Message(-59);
            msg.writer().writeByte(3);
            msg.writer().writeInt((int) pl.id);
            msg.writer().writeInt(goldThachDau);
            msg.writer().writeUTF(pl.name + " (sức mạnh " + Util.numberToMoney(pl.nPoint.getFullTN()) + ") muốn thách đấu bạn với mức cược " + goldThachDau);
            plMap.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void sendInvitePVP(Player pl, Player plMap) {
        if (pl == null) {
            return;
        }
        if (plMap == null) {
            Service.gI().sendThongBao(pl, "Đối thủ đã rời map");
            return;
        }
        if (plMap.getSession() == null || !plMap.getSession().actived) {
            Service.gI().sendThongBao(pl, "Đối thủ chưa kích hoạt tài khoản");
            return;
        }

        if (Service.gI().getCurrLevel(pl) != Service.gI().getCurrLevel(plMap)) {
            Service.gI().sendThongBao(pl, "Luyện tập cùng đối thủ ở cấp " + Service.gI().getCurrStrLevel(pl) + " để đạt hiệu quả tốt nhất");
        }

        plMap.iDMark.setIdPlayThachDau(pl.id);

        //Gửi message
        Message msg = null;
        try {
            msg = new Message(-59);
            msg.writer().writeByte(4);
            msg.writer().writeInt((int) pl.id);
            msg.writer().writeInt(0);
            msg.writer().writeUTF(pl.name + " (sức mạnh " + Util.numberToMoney(pl.nPoint.power) + ") muốn luyện tập với bạn");
            plMap.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    private void acceptPVP(Player pl) {
        if (pl == null) {
            return;
        }
        Player plMap = pl.zone.getPlayerInMap(pl.iDMark.getIdPlayThachDau());

        if (plMap == null) {
            Service.gI().hideWaitDialog(pl);
            if (Client.gI().getPlayer(pl.iDMark.getIdPlayThachDau()) == null) {
                Service.gI().sendThongBao(pl, "Đối thủ đã thoát game");
                return;
            }
            Service.gI().sendThongBao(pl, "Đối thủ đã rời map");
            return;
        }
        if (pl.pvp != null || plMap.pvp != null) {
            Service.gI().hideWaitDialog(pl);
            Service.gI().sendThongBao(pl, "Đang giao đấu không thể mời.");
            return;
        }
        int goldThachDau = pl.iDMark.getGoldThachDau();

        if (pl.inventory.gold < goldThachDau) {
            Service.gI().hideWaitDialog(pl);
            Service.gI().sendThongBao(pl, "Bạn chỉ có " + pl.inventory.gold + " vàng, không đủ tiền cược");
            return;
        }
        if (plMap.inventory.gold < goldThachDau) {
            Service.gI().hideWaitDialog(pl);
            Service.gI().sendThongBao(pl, "Đối thủ chỉ có " + plMap.inventory.gold + " vàng, không đủ tiền cược");
            return;
        }
        ThachDau thachDau = new ThachDau(pl, plMap, goldThachDau);
    }

    private void acceptPVP2(Player pl) {
        if (pl == null) {
            return;
        }
        Player plMap = pl.zone.getPlayerInMap(pl.iDMark.getIdPlayThachDau());
        if (plMap == null) {
            Service.gI().hideWaitDialog(pl);
            Service.gI().sendThongBao(pl, "Đối thủ đã rời map");
            return;
        }
        if (pl.pvp != null || plMap.pvp != null) {
            Service.gI().hideWaitDialog(pl);
            Service.gI().sendThongBao(pl, "Đang giao đấu không thể mời.");
            return;
        }
        LuyenTap luyenTap = new LuyenTap(pl, plMap);
    }

    //**************************************************************************TRẢ THÙ
    public void openSelectRevenge(Player pl, long idEnemy) {
        Player enemy = Client.gI().getPlayer(idEnemy);
        if (enemy == null) {
            Service.gI().hideWaitDialog(pl);
            Service.gI().sendThongBao(pl, "Đang offline");
            return;
        }

        pl.iDMark.setIdEnemy(idEnemy);
        if (!Util.canDoWithTime(pl.iDMark.getLastTimeRevenge(), 300000)) {
            acceptRevenge(pl);
            return;
        }
        NpcService.gI().createMenuConMeo(pl, ConstNpc.REVENGE,
                -1, "Bạn muốn đến ngay chỗ hắn, phí là 1 ngọc\nvà được tìm thoải mái trong 5 phút nhé", "OK", "Từ chối");
    }

    public void acceptRevenge(Player pl) {
        if (Util.canDoWithTime(pl.iDMark.getLastTimeRevenge(), 300000)) {
            if (pl.inventory.getGemAndRuby() < 0) {
                Service.gI().sendThongBao(pl, "Bạn không đủ ngọc, còn thiếu 1 ngọc nữa");
                return;
            }
            pl.iDMark.setLastTimeRevenge(System.currentTimeMillis());
            pl.inventory.subGemAndRuby(1);
            Service.gI().sendMoney(pl);
        }
        Player enemy = Client.gI().getPlayer(pl.iDMark.getIdEnemy());
        if (enemy == null) {
            Service.gI().hideWaitDialog(pl);
            Service.gI().sendThongBao(pl, "Đang offline");
            return;
        }
        if (pl.zone.map.mapId == ConstTranhNgocNamek.MAP_ID) {
            Service.gI().sendPopUpMultiLine(pl, 0, 7184, "Không thể thực hiện");
            return;
        }
        if (pl.pvp != null || enemy.pvp != null) {
            Service.gI().hideWaitDialog(pl);
            Service.gI().sendThongBao(pl, "Chưa thể đến lúc này, vui lòng thử lại sau ít phút");
            return;
        }
        Zone mapGo = enemy.zone;
        if (enemy.zone.map.mapId == ConstTranhNgocNamek.MAP_ID) {
            Service.gI().sendPopUpMultiLine(enemy, 0, 7184, "Không thể thực hiện");
            return;
        }
        if ((mapGo = ChangeMapService.gI().checkMapCanJoin(pl, mapGo)) == null || mapGo.isFullPlayer()) {
            Service.gI().hideWaitDialog(pl);
            Service.gI().sendThongBao(pl, "Chưa thể đến lúc này, vui lòng thử lại sau ít phút");
            return;
        }
        TraThu traThu = new TraThu(pl, enemy);
    }
}
