package consts;

import EMTI.Functions;
import item.Item;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.simple.JSONObject;
import services.ItemService;
import services.Service;
import jdbc.DBConnecter;

import jdbc.daos.NDVSqlFetcher;
import java.util.ArrayList;
import java.util.Calendar;

import java.util.List;
import jdbc.NDVResultSet;
import player.Player;
import utils.Logger;

// su kien 1/6
public class ConstDataEventLunaNewYear {//Zalo: 0385436233
    public static ConstDataEventLunaNewYear gI;
    public static ConstDataEventLunaNewYear gI() {//Zalo: 0385436233
        if (gI == null) {//Zalo: 0385436233
            gI = new ConstDataEventLunaNewYear();
        }
        return gI;
    }
    public static boolean isEventActive() {//Zalo: 0385436233
        return false;
    }
    public static boolean isTraoQua = false;
    public static Calendar startEvent;
    public static Calendar endEvents;
    public static boolean initsukien = false;
    public static final byte MONTH_OPEN = 1;
    public static final byte DATE_OPEN = 12;
    public static final byte HOUR_OPEN = 12;
    public static final byte MIN_OPEN = 00;
    public static final byte MONTH_END = 3;
    public static final byte DATE_END = 15;
    public static final byte HOUR_END = 13;
    public static final byte MIN_END = 15;

    public static boolean isActiveEvent() {
        if (!initsukien) {
            initsukien = true;
            startEvent = Calendar.getInstance();
            startEvent.set(2025, MONTH_OPEN - 1, DATE_OPEN, HOUR_OPEN, MIN_OPEN);
            System.out.println("Ngày bắt đầu sự kiện t: " + startEvent.getTime());
            endEvents = Calendar.getInstance();
            endEvents.set(2025, MONTH_END - 1, DATE_END, HOUR_END, MIN_END);
            System.out.println("Ngày kết thúc sự kiện tết: " + endEvents.getTime());
        }

        Calendar currentTime = Calendar.getInstance();
        if (System.currentTimeMillis() >= startEvent.getTimeInMillis() && System.currentTimeMillis() <= endEvents.getTimeInMillis()) {
            if (isTraoQua && System.currentTimeMillis() + 60000 >= endEvents.getTimeInMillis()) {
                String sql = "SELECT  player.id as plid, player.name as name, account.danap FROM account, player WHERE account.id = player.account_id AND account.create_time > '2025-" + MONTH_OPEN + "-" + DATE_OPEN + " " + HOUR_OPEN + ":" + MIN_OPEN + ":00' AND account.danap >= 100000 ORDER BY account.danap DESC LIMIT 10;";
                List<Integer> AccIdPlayer = new ArrayList<>();
                NDVResultSet rs = null;
                try {
                    rs = DBConnecter.executeQuery(sql);
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        AccIdPlayer.add(id);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < AccIdPlayer.size(); i++) {
                    Player player = NDVSqlFetcher.loadPlayerByID(AccIdPlayer.get(i));
                    TraoQuaSuKien(player, i + 1);
                    Logger.error("Đã trao quà nạp top " + (i + 1) + " cho: " + player.name + "\n");
                    try {
                        Functions.sleep(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                isTraoQua = false;
            }
            return true;
        } else {

            return false;
        }
    }

    public static boolean isRunningSK = isActiveEvent();

    public static void TraoQuaSuKien(Player pl, int rank) {
        Item item = null;
        JSONArray dataArray;
        JSONObject dataObject;
        try (Connection con2 = DBConnecter.getConnectionServer(); PreparedStatement ps = con2.prepareStatement("SELECT detail FROM moc_event_tet WHERE id = ?")) {

            ps.setInt(1, rank);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    dataArray = (JSONArray) JSONValue.parse(rs.getString("detail"));
                    for (int i = 0; i < dataArray.size(); i++) {
                        dataObject = (JSONObject) JSONValue.parse(String.valueOf(dataArray.get(i)));
                        int tempid = Integer.parseInt(String.valueOf(dataObject.get("temp_id")));
                        int quantity = Integer.parseInt(String.valueOf(dataObject.get("quantity")));
                        item = ItemService.gI().createNewItem((short) tempid);
                        item.quantity = quantity;
                        JSONArray optionsArray = (JSONArray) dataObject.get("options");
                        for (int j = 0; j < optionsArray.size(); j++) {
                            JSONObject optionObject = (JSONObject) optionsArray.get(j);
                            int param = Integer.parseInt(String.valueOf(optionObject.get("param")));
                            int optionId = Integer.parseInt(String.valueOf(optionObject.get("id")));
                            item.itemOptions.add(new Item.ItemOption(optionId, param));
                        }
                        pl.inventory.itemsMailBox.add(item);
                    }
                    if (NDVSqlFetcher.updateMailBox(pl)) {
                        Service.gI().sendThongBao(pl, "Bạn vừa nhận quà về mail thành công");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
