package Top;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import jdbc.DBConnecter;
import lombok.Getter;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import player.Player;
import item.Item;
import server.Manager;
import services.ItemService;
import task.TaskMain;

/**
 *
 * @author Admin
 */
public class TopTaskManager {

    @Getter
    private List<Player> list = new ArrayList<>();
    private static final TopTaskManager INSTANCE = new TopTaskManager();

    public static TopTaskManager getInstance() {
        return INSTANCE;
    }

    public void load() {
        list.clear();
        try (Connection con = DBConnecter.getConnectionServer(); java.sql.PreparedStatement ps = con.prepareStatement(
                "SELECT *, "
                + "CAST(JSON_EXTRACT(data_task, '$[1]') AS UNSIGNED) AS indexNhiemVu, "
                + "CAST(JSON_EXTRACT(data_task, '$[0]') AS UNSIGNED) AS idNhiemVu, "
                + "CAST(JSON_EXTRACT(data_task, '$[2]') AS SIGNED) AS countNhiemVu "
                + "FROM player "
                + "ORDER BY idNhiemVu DESC, countNhiemVu DESC, indexNhiemVu DESC "
                + "LIMIT 100"); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Player player = processPlayerResultSet(rs);
                list.add(player);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Player processPlayerResultSet(ResultSet rs) throws SQLException {
        Player player = new Player();
        player.id = rs.getInt("id");
        player.name = rs.getString("name");
        player.head = rs.getShort("head");
        player.gender = rs.getByte("gender");
        extractDataPoint(rs.getString("data_point"), player);
        extractItemsBody(rs.getString("items_body"), player);

        return player;
    }

    private void extractDataPoint(String dataPoint, Player player) {
        JSONArray dataArray = (JSONArray) JSONValue.parse(dataPoint);
        player.nPoint.power = Long.parseLong(dataArray.get(11).toString());
        dataArray.clear();
    }

    private void extractItemsBody(String itemsBody, Player player) {
        JSONArray dataArray = (JSONArray) JSONValue.parse(itemsBody);

        for (Object itemDataObject : dataArray) {
            Item item = createItemFromDataObject(itemDataObject.toString());
            player.inventory.itemsBody.add(item);
        }

        dataArray.clear();
    }

    private Item createItemFromDataObject(String itemData) {
        JSONArray dataObject = (JSONArray) JSONValue.parse(itemData);
        short tempId = Short.parseShort(String.valueOf(dataObject.get(0)));
        Item item;
        if (tempId != -1) {
            item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataObject.get(1))));
            JSONArray options = (JSONArray) JSONValue.parse(String.valueOf(dataObject.get(2)).replaceAll("\"", ""));

            for (Object option : options) {
                JSONArray opt = (JSONArray) JSONValue.parse(String.valueOf(option));
                item.itemOptions.add(new Item.ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                        Integer.parseInt(String.valueOf(opt.get(1)))));
            }
            item.createTime = Long.parseLong(String.valueOf(dataObject.get(3)));
            if (ItemService.gI().isOutOfDateTime(item)) {
                item = ItemService.gI().createItemNull();
            }
        } else {
            item = ItemService.gI().createItemNull();
        }

        return item;
    }
}
