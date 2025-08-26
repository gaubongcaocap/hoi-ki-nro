package models.GiftCode;

/*
 *
 *
 * @author NTD
 */
import consts.ConstNpc;
import item.Item;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Set;

import jdbc.DBConnecter;
import jdbc.NDVDB;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import player.Player;
import services.InventoryService;
import services.ItemService;
import services.NpcService;
import services.Service;
import shop.ItemShop;
import shop.Shop;
import utils.Util;

public class GiftCodeService {

    private static GiftCodeService instance;

    public static GiftCodeService gI() {
        if (instance == null) {
            instance = new GiftCodeService();
        }
        return instance;
    }

    public void updateGiftCode() {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try ( Connection con2 = DBConnecter.getConnectionServer();) {
            GiftCodeManager.gI().listGiftCode.clear();
            ps = con2.prepareStatement("SELECT * FROM giftcode");
            rs = ps.executeQuery();
            while (rs.next()) {
                GiftCode giftcode = new GiftCode();
                giftcode.code = rs.getString("code");
                giftcode.id = rs.getInt("id");
                giftcode.countLeft = rs.getInt("count_left");
                if (giftcode.countLeft == -1) {
                    giftcode.countLeft = 999999999;
                }
                giftcode.datecreate = rs.getTimestamp("datecreate");
                giftcode.dateexpired = rs.getTimestamp("expired");
                JSONArray jar = (JSONArray) JSONValue.parse(rs.getString("detail"));
                if (jar != null) {
                    for (int i = 0; i < jar.size(); ++i) {
                        JSONObject jsonObj = (JSONObject) jar.get(i);

                        int id = Integer.parseInt(jsonObj.get("temp_id").toString());
                        int quantity = Integer.parseInt(jsonObj.get("quantity").toString());

                        JSONArray option = (JSONArray) jsonObj.get("options");
                        ArrayList<Item.ItemOption> optionList = new ArrayList<>();

                        if (option != null) {
                            for (int u = 0; u < option.size(); u++) {
                                JSONObject jsonobject = (JSONObject) option.get(u);
                                int optionId = Integer.parseInt(jsonobject.get("id").toString());
                                int param = Integer.parseInt(jsonobject.get("param").toString());
                                optionList.add(new Item.ItemOption(optionId, param));
                            }
                        }
                        giftcode.option.put(id, optionList);
                        giftcode.detail.put(id, quantity);
                    }
                }
                giftcode.type = rs.getInt("type");
                GiftCodeManager.gI().listGiftCode.add(giftcode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void giftCode(Player player, String code) {
        updateGiftCode();
        GiftCode giftcode = GiftCodeManager.gI().checkUseGiftCode(player, code);

        if (giftcode == null) {
            Service.gI().sendThongBao(player, "GiftCode đã được sử dụng hoặc không tồn tại.");
        } else if (giftcode.timeCode()) {
            Service.gI().sendThongBao(player, "Code đã hết hạn");
        } else {
            Set<Integer> keySet = giftcode.detail.keySet();
            String textGift = "\n|7|Bạn Nhận Được:\b";
            for (Integer key : keySet) {
                int idItem = key;
                int quantity = giftcode.detail.get(key);

                switch (idItem) {
                    case -1 -> {
                        player.inventory.gold = Math.min(player.inventory.gold + (long) quantity, 2000000000L);
                        textGift += "|2|" + quantity + " vàng\b";
                    }
                    case -2 -> {
                        player.inventory.gem = Math.min(player.inventory.gem + quantity, 20000);
                        textGift += "|3|" + quantity + " ngọc\b";
                    }
                    case -3 -> {
                        player.inventory.ruby = Math.min(player.inventory.ruby + quantity, 20000);
                        textGift += "|4|" + quantity + " ngọc khóa\b";
                    }
                    default -> {
                        Item itemGiftTemplate = ItemService.gI().createNewItem((short) idItem);
                        if (itemGiftTemplate != null) {
                            Item itemGift = new Item((short) idItem);
                            itemGift.itemOptions = giftcode.option.get(key);
                            itemGift.quantity = quantity;
                            InventoryService.gI().addItemBag(player, itemGift);
                            textGift += "|2|" + quantity + " " + itemGift.template.name + "\b";
                            Service.gI().sendThongBao(player, "Bạn nhận được " + itemGift.template.name);
                        }
                    }
                }
            }
            InventoryService.gI().sendItemBag(player);
            NpcService.gI().createMenuConMeo(player, ConstNpc.IGNORE_MENU, -1, textGift, "OK");
        }
    }

}
