package jdbc.daos;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import jdbc.DBConnecter;
import jdbc.NDVResultSet;
import org.json.simple.JSONArray;
import player.Inventory;
import player.Player;
import utils.Util;

public class SuperRankDAO {

    // Lấy rank cao nhất từ bảng player
    public static int getHighestRank() {
        NDVResultSet rs = null;
        try {
            rs = DBConnecter.executeQuery("SELECT rank FROM player ORDER BY rank DESC LIMIT 1");
            if (rs.next()) {
                return rs.getInt("rank");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Lấy danh sách người chơi trong khoảng rank
    public static List<Long> getPlayerListInRankRange(int rank, int limit) {
        List<Long> list = new ArrayList<>();
        NDVResultSet rs = null;

        try {
            rs = DBConnecter.executeQuery(
                "SELECT id FROM player WHERE rank <= ? AND rank > 0 ORDER BY rank DESC LIMIT ?", rank, limit
            );
            while (rs.next()) {
                list.add((long) rs.getInt("id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Thêm một ID ngẫu nhiên dựa trên rank
        int rand = random(rank);
        if (rand != -1) {
            try {
                rs = DBConnecter.executeQuery("SELECT id FROM player WHERE rank = ? LIMIT 1", rand);
                if (rs.next()) {
                    list.add((long) rs.getInt("id"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Collections.reverse(list);
        return list;
    }

    // Lấy danh sách người chơi theo rank
    public static List<Long> getPlayerListInRank(int rank, int limit) {
        List<Long> list = new ArrayList<>();
        NDVResultSet rs = null;

        try {
            rs = DBConnecter.executeQuery("SELECT id FROM player WHERE rank > 0 ORDER BY rank ASC LIMIT ?", limit);
            while (rs.next()) {
                list.add((long) rs.getInt("id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (rank > 100) {
            try {
                rs = DBConnecter.executeQuery(
                    "SELECT id FROM player WHERE rank > ? AND rank < ? ORDER BY rank ASC LIMIT 4",
                    rank - 3, rank + 2
                );
                while (rs.next()) {
                    list.add((long) rs.getInt("id"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    // Hàm tạo giá trị ngẫu nhiên dựa trên rank
    public static int random(int rank) {
        if (rank > 10000) {
            return Util.nextInt(6666, 10000);
        } else if (rank > 6666) {
            return Util.nextInt(3333, 6666);
        } else if (rank > 3333) {
            return Util.nextInt(1000, 3333);
        } else if (rank > 1000) {
            return Util.nextInt(666, 1000);
        } else if (rank > 666) {
            return Util.nextInt(333, 666);
        } else if (rank > 333) {
            return Util.nextInt(100, 333);
        }
        System.err.println("Rank too low to generate random value: " + rank);
        return -1;
    }

    // Cập nhật rank của người chơi
    public static void updateRank(Player player) {
        try {
            String query = "UPDATE player SET rank = ? WHERE id = ?";
            DBConnecter.executeUpdate(query, player.superRank.rank, player.id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Cập nhật thông tin người chơi
    public static void updatePlayer(Player player) {
        if (player != null && player.iDMark.isLoadedAllDataPlayer()) {
            try {
                // Tạo dữ liệu inventory
                JSONArray dataArray = new JSONArray();
                dataArray.add(Math.min(player.inventory.gold, Inventory.LIMIT_GOLD));
                dataArray.add(player.inventory.gem);
                dataArray.add(player.inventory.ruby);
                dataArray.add(player.inventory.coupon);
                dataArray.add(player.inventory.event);
                String inventory = dataArray.toJSONString();

                // Tạo dữ liệu super rank
                String dataSuperRank = createSuperRankData(player);

                // Cập nhật vào database
                String query = "UPDATE player SET data_inventory = ?, rank = ?, data_super_rank = ? WHERE id = ?";
                DBConnecter.executeUpdate(query, inventory, player.superRank.rank, dataSuperRank, player.id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Tạo dữ liệu super rank dưới dạng JSON
    private static String createSuperRankData(Player player) {
        try {
            JsonObject jsonObject = new JsonObject();
            JsonArray stringArray = new JsonArray();
            for (String str : player.superRank.history) {
                stringArray.add(str);
            }
            JsonArray longArray = new JsonArray();
            for (Long value : player.superRank.lastTime) {
                longArray.add(value);
            }
            jsonObject.add("history", stringArray);
            jsonObject.add("lasttime", longArray);
            return new Gson().toJson(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            return "{}";
        }
    }
}
