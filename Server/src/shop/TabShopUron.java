package shop;

import player.Player;
import services.InventoryService;

import java.util.ArrayList;
import java.util.List;

public class TabShopUron extends TabShop {

    private final int[] listDauThan = {293, 294, 295, 296, 297, 298, 299, 596, 597, 598};

    public TabShopUron(TabShop tabShop, Player player) {
        this.itemShops = new ArrayList<>();
        this.shop = tabShop.shop;
        this.id = tabShop.id;
        this.name = tabShop.name;

        int dauCanBuyId = idDauCanBuy(player);

        boolean hasBongTai = InventoryService.gI().findItemBongTai(player);

        for (ItemShop itemShop : tabShop.itemShops) {
            if (itemShop.temp.gender == player.gender || itemShop.temp.gender > 2) {
                boolean isInListDauThan = false;
                for (int id : listDauThan) {
                    if (itemShop.temp.id == id) {
                        isInListDauThan = true;
                        break;
                    }
                }

                if (player.haveTennisSpaceShip && itemShop.temp.id == 453) {
                    continue;
                }

                if (hasBongTai && itemShop.temp.id == 454) {
                    continue;
                }

                if (!isInListDauThan || itemShop.temp.id == dauCanBuyId) {
                    this.itemShops.add(new ItemShop(itemShop));
                }
            }
        }
    }


    public int idDauCanBuy(Player player) {
        int level = player.magicTree.level;
        if (level == 10) {
            return listDauThan[9];
        } else if (level >= 1 && level <= 9) {
            return listDauThan[level - 1];
        }
        throw new IllegalArgumentException("Invalid magic tree level: " + level);
    }
}
