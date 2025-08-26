package npc.npc_manifest;

/**
 * @author NTD
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import consts.ConstDataEventNAP;
import consts.ConstDataEventSM;
import consts.ConstMenu;
import npc.Npc;
import java.util.logging.Logger;
import player.Player;
import server.Manager;
import services.NpcService;
import services.Service;
import java.util.logging.Level;
import java.sql.SQLException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.sql.ResultSet;
import static java.time.temporal.TemporalQueries.zone;
import jdbc.DBConnecter;
import org.json.simple.JSONValue;
import player.ArchivementSucManh;
import services.ItemService;
import services.func.ChangeMapService;
import services.func.TopService;
import utils.Util;

public class DaiThienSu extends Npc {

    public DaiThienSu(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        this.createOtherMenu(player, ConstMenu.MENU_SHOW,
                //                "|2|Sự kiện đua Top\n"
                //                + "diễn ra từ " + Manager.timeStartDuaTop + " đến " + Manager.timeEndDuaTop + "\n"
                //                + "Giải thưởng khủng chưa từng có, xem chi tiết tại diễn đàn, fanpage\n"
                //                + Manager.demTimeSuKien(),
                "Đang diễn ra sự kiện đua top mùa 1\n"
                + "Thể lệ đua top mùa 1:\n"
                + "- Thời gian tạo acc tại thời điểm sự kiện diễn ra\n"
                + "- Quà sẽ được gửi về hộp thư tại nhà hoặc quy lão kame sau khi sự kiện hết thúc!",
                "Đua Top\n Sức mạnh", "Đua top\n Nạp", "Top Nhiệm Vụ");
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
//            switch (select) {

            switch (player.iDMark.getIndexMenu()) {
                case ConstMenu.MENU_SHOW:
                    switch (select) {
                        case 0:
                            if (ConstDataEventSM.isRunningSK == false) {
                                Service.gI().sendThongBao(player, "Sự kiện top Sức mạnh đã kết thúc");
                                return;
                            }
                            this.createOtherMenu(player, 1115, "|0|Đua top sức mạnh", "Xem Top", "Xem quà top", "Đóng");

                            break;
                        case 1:
                            if (ConstDataEventNAP.isRunningSK == false) {
                                Service.gI().sendThongBao(player, "Sự kiện top Nạp đã kết thúc");
                                return;
                            }
                            this.createOtherMenu(player, 1116, "|0|Đua Top Nạp", "Xem Top", "Xem quà", "Đóng");
                            break;
                        case 2:
                            TopService.showListTop(player, 0);
                            break;
                    }
                    break;
                case 1115:

                    switch (select) {
                        case 0:
                          TopService.showListTop(player, 2);
                            break;
                        case 1: {
                            StringBuilder sb = new StringBuilder();
                            sb.append("|0|꧁TOP SỨC MẠNH:\n"
                                    + "Chỉ dành cho những tài khoản tạo từ thời điểm " + ConstDataEventSM.HOUR_OPEN + "H" + ConstDataEventSM.MIN_OPEN + " ngày " + ConstDataEventSM.DATE_OPEN + "/" + ConstDataEventSM.MONTH_OPEN + "/2024\n"
                                    + "Nhận quà vào ngày " + ConstDataEventSM.HOUR_END + "H" + ConstDataEventSM.MIN_END + " ngày " + ConstDataEventSM.DATE_END + "/" + ConstDataEventSM.MONTH_END + "/2024, OPEN quà sẽ về hộp thư người chơi꧂\n");

                            try (Connection con2 = DBConnecter.getConnectionServer(); PreparedStatement ps = con2.prepareStatement("SELECT * FROM moc_suc_manh_top"); ResultSet rs = ps.executeQuery()) {

                                while (rs.next()) {
                                    JSONArray dataArray = (JSONArray) JSONValue.parse(rs.getString("detail"));
                                    sb.append("TOP ").append(rs.getInt("id")).append(": ");
                                    for (Object itemObj : dataArray) {
                                        JSONObject dataObject = (JSONObject) itemObj;
                                        int tempid = Integer.parseInt(String.valueOf(dataObject.get("temp_id")));
                                        int quantity = Integer.parseInt(String.valueOf(dataObject.get("quantity")));
                                        sb.append("x").append(quantity).append(" ").append(ItemService.gI().getTemplate(tempid).name).append(", ");

                                        JSONArray optionsArray = (JSONArray) dataObject.get("options");
                                        if (optionsArray != null) {
                                            for (Object optionObj : optionsArray) {
                                                JSONObject optionObject = (JSONObject) optionObj;
                                                String formattedOption = ItemService.gI().getItemOptionTemplate(
                                                        Integer.parseInt(String.valueOf(optionObject.get("id")))).name.replace("#", String.valueOf(optionObject.get("param")));
                                                sb.append(formattedOption).append(", ");
                                            }
                                        }
                                    }
                                    if (sb.charAt(sb.length() - 1) == ' ') {
                                        sb.setLength(sb.length() - 1);
                                    }
                                    sb.append("\n|0|");
                                }
                            } catch (SQLException ex) {
                                Logger.getLogger(DaiThienSu.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            NpcService.gI().createTutorial(player, tempId, this.avartar, sb.toString());
                            break;
                        }

                        case 2: {
                            JSONArray dataArray;
                            JSONObject dataObject;
                            PreparedStatement ps = null;
                            ResultSet rs = null;
                            StringBuilder sb = new StringBuilder();
                            sb.append("|0|꧁__Reset mỗi thứ 2 đầu tuần__꧂\n");
                            try (Connection con2 = DBConnecter.getConnectionServer()) {
                                ps = con2.prepareStatement("SELECT * FROM moc_suc_manh");
                                rs = ps.executeQuery();

                                while (rs.next()) {
                                    dataArray = (JSONArray) JSONValue.parse(rs.getString("detail"));
                                    sb.append("◥_____________________◤\n|7|");
                                    sb.append("✎▶Mốc Sức Mạnh ").append(Util.numberToMoney(ArchivementSucManh.POWERGIFT[rs.getInt("id") - 1]))
                                            .append("◀\n|0|");
                                    sb.append("◢¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯◣\n|0|");

                                    for (int i = 0; i < dataArray.size(); i++) {
                                        dataObject = (JSONObject) JSONValue.parse(String.valueOf(dataArray.get(i)));
                                        int tempid = Integer.parseInt(String.valueOf(dataObject.get("temp_id")));
                                        int quantity = Integer.parseInt(String.valueOf(dataObject.get("quantity")));
                                        JSONArray optionsArray = (JSONArray) dataObject.get("options");

                                        sb.append("▷ x").append(quantity).append(" ")
                                                .append(ItemService.gI().getTemplate(tempid).name).append("\n|4|");

                                        if (optionsArray != null) {
                                            for (int j = 0; j < optionsArray.size(); j++) {
                                                JSONObject optionObject = (JSONObject) optionsArray.get(j);
                                                int optionId = Integer.parseInt(String.valueOf(optionObject.get("id")));
                                                int param = Integer.parseInt(String.valueOf(optionObject.get("param")));

                                                String optionTemplateName = ItemService.gI().getItemOptionTemplate(optionId).name;
                                                String formattedOption = optionTemplateName.replace("#", String.valueOf(param));

                                                sb.append(formattedOption).append("\n");
                                            }
                                        }
                                        sb.append("\n|0|");
                                    }
                                }
                            } catch (SQLException ex) {
                                Logger.getLogger(DaiThienSu.class.getName()).log(Level.SEVERE, null, ex);
                            }

                            Service.gI().sendThongBaoFromAdmin(player, sb.toString());
                        }
                        break;
                        case 3:               
                                ArchivementSucManh.gI().getAchievement(player);
                                                        break;
                    }
                    break;
                case 1116:
                    switch (select) {
                        case 0:
                            Service.gI().sendThongBaoOK(player, TopService.getTopNap());
                            break;
                        case 1: {
                            JSONArray dataArray;
                            JSONObject dataObject;
                            PreparedStatement ps = null;
                            ResultSet rs = null;
                            StringBuilder sb = new StringBuilder();
                            sb.append("|0|꧁TOP NẠP:\n"
                                    + "Tài khoản cần nạp 100K trở lên để có thể vô top\n"
                                    + "Nhận quà vào ngày " + ConstDataEventNAP.HOUR_END + "H" + ConstDataEventNAP.MIN_END + " ngày " + ConstDataEventNAP.DATE_END + "/" + ConstDataEventNAP.MONTH_END + "/2024, OPEN quà sẽ về hộp thư người chơi꧂\n");
                            try (Connection con2 = DBConnecter.getConnectionServer()) {
                                ps = con2.prepareStatement("SELECT * FROM moc_nap_top");
                                rs = ps.executeQuery();

                                while (rs.next()) {
                                    dataArray = (JSONArray) JSONValue.parse(rs.getString("detail"));
                                    sb.append("◥_____________________◤\n|7|");
                                    sb.append("✎▶TOP ").append(rs.getInt("id"))
                                            .append("◀\n|0|");
                                    sb.append("◢¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯◣\n|0|");

                                    for (int i = 0; i < dataArray.size(); i++) {
                                        dataObject = (JSONObject) JSONValue.parse(String.valueOf(dataArray.get(i)));
                                        int tempid = Integer.parseInt(String.valueOf(dataObject.get("temp_id")));
                                        int quantity = Integer.parseInt(String.valueOf(dataObject.get("quantity")));
                                        JSONArray optionsArray = (JSONArray) dataObject.get("options");

                                        sb.append("▷ x").append(quantity).append(" ")
                                                .append(ItemService.gI().getTemplate(tempid).name).append("\n|4|");

                                        if (optionsArray != null) {
                                            for (int j = 0; j < optionsArray.size(); j++) {
                                                JSONObject optionObject = (JSONObject) optionsArray.get(j);
                                                int optionId = Integer.parseInt(String.valueOf(optionObject.get("id")));
                                                int param = Integer.parseInt(String.valueOf(optionObject.get("param")));

                                                String optionTemplateName = ItemService.gI().getItemOptionTemplate(optionId).name;
                                                String formattedOption = optionTemplateName.replace("#", String.valueOf(param));

                                                sb.append(formattedOption).append("\n");
                                            }
                                        }
                                        sb.append("\n|0|");
                                    }
                                }
                            } catch (SQLException ex) {
                                Logger.getLogger(DaiThienSu.class.getName()).log(Level.SEVERE, null, ex);
                            }

                            Service.gI().sendThongBaoFromAdmin(player, sb.toString());
                        }
                        break;
                    }
                    break;

            }
        }
    }
}
