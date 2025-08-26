package services.func;

/*
 *
 *
 * @author NTD
 */
import EMTI.Functions;
import Bot.Bot;
import jdbc.DBConnecter;
import jdbc.daos.PlayerDAO;
import player.Player;
import network.Message;
import server.Client;
import server.Maintenance;
import services.Service;
import utils.Logger;
import utils.TimeUtil;
import utils.Util;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import server.ServerManager;

public class TransactionService implements Runnable {

    private static final int TIME_DELAY_TRADE = 30000;

    static final Map<Player, Trade> PLAYER_TRADE = new HashMap<>();

    private static final byte SEND_INVITE_TRADE = 0;
    private static final byte ACCEPT_TRADE = 1;
    private static final byte ADD_ITEM_TRADE = 2;
    private static final byte CANCEL_TRADE = 3;
    private static final byte LOCK_TRADE = 5;
    private static final byte ACCEPT = 7;

    private static TransactionService i;

    private TransactionService() {
    }

   public static TransactionService gI() {
    if (i == null) {
        i = new TransactionService();
        Thread.startVirtualThread(i);
    }
    return i;
}


    public void controller(Player pl, Message msg) {
        try {
            byte action = msg.reader().readByte();
            int playerId = -1;
            Player plMap = null;
            Trade trade = PLAYER_TRADE.get(pl);
            if (pl.baovetaikhoan) {
                Service.gI().sendThongBao(pl, "Chức năng bảo vệ đã được bật. Bạn vui lòng kiểm tra lại");
                return;
            }
            if (!pl.getSession().actived) {
                Service.gI().sendThongBao(pl, "Vui lòng kích hoạt thành viên");
                return;
            }
            if (action == SEND_INVITE_TRADE) {
                pl.iDMark.setTransactionWP(false);
                pl.iDMark.setTransactionWVP(false);
            }
            switch (action) {
                case SEND_INVITE_TRADE:
                case ACCEPT_TRADE:
                    // Kiểm tra trạng thái session của người chơi
                    if (!pl.getSession().actived) {
                        Service.gI().sendThongBao(pl, "Truy Cập: " + ServerManager.DOMAIN + "\n Để Mở Thành Viên");
                        return;
                    }

                    // Kiểm tra sức mạnh của người chơi phải đạt ít nhất 340k (power)
                    if (pl.nPoint.power < 340000) {
                        Service.gI().sendThongBao(pl, "Bạn cần đạt vệ binh để thực hiện giao dịch");
                        return;
                    }

                    // Lấy ID người chơi từ message
                    playerId = msg.reader().readInt();
                    plMap = pl.zone.getPlayerInMap(playerId);

                    // Kiểm tra nếu người chơi tồn tại và có phải là người chơi hợp lệ
                    if (plMap != null && plMap.isPl()) {
                        // Kiểm tra nếu người chơi không có trạng thái Trade WVP
                        if (plMap.tradeWVP) {
                            return;
                        }

                        // Kiểm tra tình trạng trade của hai người chơi
                        trade = PLAYER_TRADE.get(pl);
                        if (trade == null) {
                            trade = PLAYER_TRADE.get(plMap);
                        }

                        // Nếu không có giao dịch nào đang diễn ra
                        if (trade == null) {
                            if (action == SEND_INVITE_TRADE) {
                                // Kiểm tra thời gian giữa các lần giao dịch
                                if (Util.canDoWithTime(pl.iDMark.getLastTimeTrade(), TIME_DELAY_TRADE)
                                        && Util.canDoWithTime(plMap.iDMark.getLastTimeTrade(), TIME_DELAY_TRADE)) {

                                    boolean checkLogout1 = false;
                                    boolean checkLogout2 = false;

                                    // Kiểm tra tình trạng đăng xuất của người chơi
                                    try ( Connection con = DBConnecter.getConnectionServer()) {
                                        checkLogout1 = PlayerDAO.checkLogout(con, pl);
                                        checkLogout2 = PlayerDAO.checkLogout(con, plMap);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    // Nếu một trong hai người chơi đã đăng xuất, ngắt kết nối
                                    if (checkLogout1) {
                                        Client.gI().kickSession(pl.getSession());
                                        break;
                                    }
                                    if (checkLogout2) {
                                        Client.gI().kickSession(plMap.getSession());
                                        break;
                                    }

                                    // Cập nhật thời gian giao dịch
                                    pl.iDMark.setLastTimeTrade(System.currentTimeMillis());
                                    pl.iDMark.setPlayerTradeId((int) plMap.id);

                                    // Gửi lời mời giao dịch
                                    sendInviteTrade(pl, plMap);
                                } else {
                                    // Nếu chưa đủ thời gian, thông báo cho người chơi
                                    Service.gI().sendThongBao(pl, "Thử lại sau "
                                            + TimeUtil.getTimeLeft(Math.max(pl.iDMark.getLastTimeTrade(), plMap.iDMark.getLastTimeTrade()), TIME_DELAY_TRADE / 1000));
                                }
                            } else {
                                // Nếu người chơi chấp nhận giao dịch, mở tab giao dịch
                                if (plMap.iDMark.getPlayerTradeId() == pl.id) {
                                    trade = new Trade(pl, plMap);
                                    trade.openTabTrade();
                                }
                            }

                        } else {
                            // Nếu có giao dịch đang diễn ra, không thể thực hiện giao dịch mới
                            Service.gI().sendThongBao(pl, "Không thể thực hiện");
                        }
                    }
                    break;

                case ADD_ITEM_TRADE:
                    if (trade != null) {
                        byte index = msg.reader().readByte();
                        int quantity = msg.reader().readInt();
                        if (quantity < 0) {
                            Service.gI().sendThongBao(pl, "Không thể thực hiện");
                            trade.cancelTrade();
                            break;
                        }
                        if (quantity == 0) {//do
                            quantity = 1;
                        }
                        if (index != -1 && quantity > Trade.QUANLITY_MAX) {
                            Service.gI().sendThongBao(pl, "Đã quá giới hạn giao dịch...");
                            trade.cancelTrade();
                            break;
                        }
                        trade.addItemTrade(pl, index, quantity);
                    }
                    break;
                case CANCEL_TRADE:
                    if (trade != null) {
                        trade.cancelTrade();
                    }
                    break;
                case LOCK_TRADE:
                    if (Maintenance.isRunning) {
                        trade.cancelTrade();
                        break;
                    }
                    if (trade != null) {
                        trade.lockTran(pl);
                    }
                    break;
                case ACCEPT:
                    if (Maintenance.isRunning) {
                        trade.cancelTrade();
                        break;
                    }
                    if (trade != null) {
                        trade.acceptTrade();
                        if (trade.accept == 1) {
                            Service.gI().sendThongBao(pl, "Xin chờ đối phương đồng ý");
                        } else if (trade.accept == 2) {
                            trade.dispose();
                        }
                    }
                    break;
            }
        } catch (Exception e) {
            Logger.logException(this.getClass(), e);
        }
    }


    /**
     * Mời giao dịch
     */
    private void sendInviteTrade(Player plInvite, Player plReceive) {
        if(plReceive.isBot){
        ((Bot)plReceive).shop.activeTraDe(plInvite);
        }
        Message msg = null;
        try {
            msg = new Message(-86);
            msg.writer().writeByte(0);
            msg.writer().writeInt((int) plInvite.id);
            plReceive.sendMessage(msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    /**
     * Hủy giao dịch
     *
     * @param player
     */
    public void cancelTrade(Player player) {
        Trade trade = PLAYER_TRADE.get(player);
        if (trade != null) {
            trade.cancelTrade();
        }
    }

    public boolean check(Player player) {
        return PLAYER_TRADE.get(player) != null;
    }

    @Override
    public void run() {
        while (!Maintenance.isRunning) {
            try {
                long start = System.currentTimeMillis();
                Set<Map.Entry<Player, Trade>> entrySet = PLAYER_TRADE.entrySet();
                for (Map.Entry entry : entrySet) {
                    ((Trade) entry.getValue()).update();
                }
                Functions.sleep(Math.max(300 - (System.currentTimeMillis() - start), 10));
            } catch (Exception e) {
            }
        }
    }
}
