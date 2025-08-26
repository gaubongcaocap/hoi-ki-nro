package models.Consign;

/*
 *
 *
 * @author NTD
 */

import jdbc.DBConnecter;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONValue;

public class ConsignShopManager {

    private static ConsignShopManager instance;

    public static ConsignShopManager gI() {
        if (instance == null) {
            instance = new ConsignShopManager();
        }
        return instance;
    }

    public long lastTimeUpdate;

    public String[] tabName = {"Trang bị", "Phụ kiện", "Hỗ trợ", "Linh tinh", ""};

    public List<ConsignItem> listItem = new ArrayList<>();

public void save() {
    try (Connection con = DBConnecter.getConnectionServer(); 
         Statement s = con.createStatement()) {

        // Xóa dữ liệu cũ
        s.execute("TRUNCATE shop_ky_gui");

        for (ConsignItem it : this.listItem) {
            if (it != null) {
                // Tạo câu lệnh SQL
                String sql = String.format(
                    "INSERT INTO `shop_ky_gui` (`player_id`, `player_name`, `tab`, `item_id`, `gold`, `gem`, `quantity`, `itemOption`, `lastTime`, `isBuy`) " +
                    "VALUES ('%d', '%s', '%d', '%d', '%d', '%d', '%d', '%s', '%d', '%d')",
                    it.player_sell, 
                    it.player_sell,  // Thêm xử lý cho dấu nháy đơn trong chuỗi
                    it.tab,
                    it.itemId,
                    it.goldSell,
                    it.gemSell,
                    it.quantity,
                    JSONValue.toJSONString(it.options).equals("null") ? "[]" : JSONValue.toJSONString(it.options),
                    it.lasttime,
                    it.isBuy ? 1 : 0
                );

                // In câu lệnh SQL ra màn hình để kiểm tra
                System.out.println("Executing SQL: " + sql);

                // Thực thi câu lệnh SQL
                s.execute(sql);
            }
        }
    } catch (Exception e) {
        e.printStackTrace(); // In ra lỗi nếu có
    }
}


}
