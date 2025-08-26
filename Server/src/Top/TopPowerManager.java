package Top;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
import services.ItemService;

/**
 *
 * @author Admin
 */
public class TopPowerManager {

    @Getter
    private List<Player> list = new ArrayList<>();
    private static final TopPowerManager INSTANCE = new TopPowerManager();

    public static TopPowerManager getInstance() {
        return INSTANCE;
    }

    public void load() {
        list.clear();
        try (Connection con = DBConnecter.getConnectionServer(); PreparedStatement ps = con.prepareStatement(
                "SELECT id, name, data_point FROM player ORDER BY CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(data_point, ',', 2), ',', -1) AS UNSIGNED) DESC LIMIT 100");
             ResultSet rs = ps.executeQuery()) {
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
    
    player.head = (short) rs.getInt("head"); 
    player.gender = (byte) rs.getInt("gender");
    player.nPoint.power = rs.getLong("power");
    
    extractDataPoint(rs.getString("data_point"), player);
    extractItemsBody(rs.getString("items_body"), player);
    
    return player;
}


    private void extractDataPoint(String dataPoint, Player player) {
        String[] data = dataPoint.replace("[", "").replace("]", "").split(",");
        if (data.length >= 2) {
            player.nPoint.power = Long.parseLong(data[1]); 
        }
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
        JSONValue jv = new JSONValue();
        JSONArray dataObject = (JSONArray) jv.parse(itemData);
        short tempId = Short.parseShort(String.valueOf(dataObject.get(0)));
        Item item;
        if (tempId != -1) {
            item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataObject.get(1))));
            JSONArray options = (JSONArray) jv.parse(String.valueOf(dataObject.get(2)).replaceAll("\"", ""));

            for (Object option : options) {
                JSONArray opt = (JSONArray) jv.parse(String.valueOf(option));
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

