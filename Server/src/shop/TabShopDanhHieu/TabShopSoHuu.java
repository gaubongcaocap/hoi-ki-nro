package shop.TabShopDanhHieu;

import item.Item;
import player.Player;
import player.badges.BagesTemplate;
import shop.ItemShop;
import shop.TabShop;
import task.Badges.BadgesTaskService;

import java.util.ArrayList;
import java.util.List;

public class TabShopSoHuu extends TabShop {

    public TabShopSoHuu(TabShop tabShop, Player player) {
        this.itemShops = new ArrayList<>();
        this.shop = tabShop.shop;
        this.id = tabShop.id;
        this.name = tabShop.name + BagesTemplate.listEffect(player).size();

        for (ItemShop itemShop : tabShop.itemShops) {
            if (itemShop.temp.gender == player.gender || itemShop.temp.gender > 2) {
                boolean shouldAdd = false;
                for (Integer i : BagesTemplate.listEffect(player)) {
                    if (itemShop.temp.id == i) {
                        shouldAdd = true;
                        break;
                    }
                }
                if (shouldAdd) {
                    for (Item.ItemOption option : itemShop.options) {
                        if (option.optionTemplate.id == 93) {
                            option.param = BadgesTaskService.sendDay(player, BagesTemplate.fineIdEffectbyIdItem(itemShop.temp.id));
                            break;
                        }
                    }
                    this.itemShops.add(new ItemShop(itemShop));
                }
            }
        }
    }
}
