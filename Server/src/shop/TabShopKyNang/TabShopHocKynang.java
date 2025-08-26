package shop.TabShopKyNang;

import java.util.ArrayList;
import java.util.List;
import shop.ItemShop;
import shop.TabShop;
import player.Player;

public class TabShopHocKynang extends TabShop {
    public TabShopHocKynang(TabShop tabShop, Player player) {
        this.itemShops = new ArrayList<>();
        this.shop = tabShop.shop;
        this.id = tabShop.id;
        this.name = tabShop.name;
        for (ItemShop itemShop : tabShop.itemShops) {
            // chỉ hiện skill đúng giới tính và chưa mua
            if (itemShop.temp.gender == player.gender || itemShop.temp.gender > 2) {
                boolean shouldAdd = true;
                for (Integer id : player.BoughtSkill) {
                    if (itemShop.temp.id == id) {
                        shouldAdd = false;
                        break;
                    }
                }
                if (shouldAdd) {
                    this.itemShops.add(new ItemShop(itemShop));
                }
            }
        }
    }
}
