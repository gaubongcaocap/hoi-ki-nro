package services.func;

/*
 *
 *
 * @author NTD
 */

import item.Item;
import player.Player;
import services.ItemService;
import shop.ShopService;

public class VatPhamDaBan {

    private static final byte MAX_ITEM_IN_BOX = 100;

    private static VatPhamDaBan i;

    public static VatPhamDaBan gI() {
        if (i == null) {
            i = new VatPhamDaBan();
        }
        return i;
    }

    public void addItem(Player player, Item item) {
        if (player.inventory.itemsDaBan.size() + 1 > MAX_ITEM_IN_BOX) {
            player.inventory.itemsDaBan.remove(0);
        }
        Item itemmua = ItemService.gI().copyItem(item);
        player.inventory.itemsDaBan.add(itemmua);
        if (player.iDMark != null && player.iDMark.getTagNameShop().equals("ITEMS_DABAN")) {
            ShopService.gI().opendShop(player, "ITEMS_DABAN", true);
        }
    }
}
