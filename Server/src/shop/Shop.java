package shop;

/*
 *
 *
 * @author NTD
 */

import player.Player;
import shop.TabShopDanhHieu.TabShopDanhHieu;
import shop.TabShopDanhHieu.TabShopSoHuu;
import shop.TabShopKyNang.TabShopHocKynang;

import java.util.ArrayList;
import java.util.List;

public class Shop {

    public int id;

    public byte npcId;

    public List<TabShop> tabShops;

    public String tagName;

    public byte typeShop;

    public Shop() {
        this.tabShops = new ArrayList<>();
    }

    public Shop(Shop shop, Player player) {
        this.tabShops = new ArrayList<>();
        this.id = shop.id;
        this.npcId = shop.npcId;
        this.tagName = shop.tagName;
        this.typeShop = shop.typeShop;
        for (TabShop tabShop : shop.tabShops) {
            if (tabShop.id == 13) {
                this.tabShops.add(new TabShopUron(tabShop, player));
            } else if (tabShop.id == 28) {
                this.tabShops.add(new TabShopDanhHieu(tabShop, player));
            } else if (tabShop.id == 29) {
                this.tabShops.add(new TabShopSoHuu(tabShop, player));
            } else if (tabShop.id == 53) {
                this.tabShops.add(new TabShopHocKynang(tabShop, player));
            } else {
                this.tabShops.add(new TabShop(tabShop, player.gender));
            }
        }
    }

    public Shop(Shop shop) {
        this.tabShops = new ArrayList<>();
        this.id = shop.id;
        this.npcId = shop.npcId;
        this.tagName = shop.tagName;
        this.typeShop = shop.typeShop;
        for (TabShop tabShop : shop.tabShops) {
            this.tabShops.add(new TabShop(tabShop));
        }
    }

    public ItemShop getItemShop(int temp) {
        for (TabShop tab : this.tabShops) {
            for (ItemShop is : tab.itemShops) {
                if (is.temp.id == temp) {
                    return is;
                }
            }
        }
        return null;
    }

    public void dispose() {
        if (this.tabShops != null) {
            for (TabShop ts : this.tabShops) {
                ts.dispose();
            }
            this.tabShops.clear();
        }
        this.tabShops = null;
    }

}
