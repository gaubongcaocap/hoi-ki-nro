package models.Combine.manifest;

import consts.ConstFont;
import consts.ConstNpc;
import item.Item;
import models.Combine.CombineService;
import player.Player;
import server.Manager;
import services.InventoryService;
import services.ItemService;
import services.RewardService;
import services.Service;
import utils.Util;

/**
 *
 * @author Admin
 */
public class NangCapKichHoatVip {

    public static boolean isDoThienSu(Item item) {
        return item.template.id >= 1048 && item.template.id <= 1062;
    }

    public static void showInfoCombine(Player player) {
        if (player.combine != null && player.combine.itemsCombine != null && player.combine.itemsCombine.size() == 2) {
            Item trangBiThienSu = null;
            Item daKichHoatVip = null;
            for (Item item : player.combine.itemsCombine) {
                if (isDoThienSu(item)) {
                    trangBiThienSu = item;
                } else if (item.template.id == 1777) {
                    daKichHoatVip = item;
                }
            }
            player.combine.goldCombine = 500_000_000;
            int goldCombie = player.combine.goldCombine;
            if (trangBiThienSu != null && daKichHoatVip != null) {
                String npcSay = "Sau khi cường hoá, sẽ được nâng cấp trang bị Thiên Sứ thành trang bị Kích hoạt Vip";
                CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay,
                        "Cường hoá\n" + Util.numberToMoney(goldCombie) + " vàng", "Từ chối");
            } else {
                Service.gI().sendThongBaoOK(player, "Cần 1 trang bị Thiên Sứ và 1 viên đá kích hoạt vip");
            }
        } else {
            Service.gI().sendThongBaoOK(player, "Cần 1 trang bị Thiên Sứ và 1 viên đá kích hoạt vip");
        }
    }

    public static void startCombine(Player player) {
        if (player.combine.itemsCombine.size() == 2) {
            int gold = player.combine.goldCombine;
            if (player.inventory.gold < gold) {
                Service.gI().sendThongBao(player, "Bạn không đủ vàng, còn thiếu " + Util.numberToMoney(gold - player.inventory.gold) + " vàng nữa");
                Service.gI().sendMoney(player);
                return;
            }
            Item trangBiThienSu = null;
            Item daKichHoatVip = null;
            for (Item item : player.combine.itemsCombine) {
                if (isDoThienSu(item)) {
                    trangBiThienSu = item;
                } else if (item.template.id == 1777) {
                    daKichHoatVip = item;
                }
            }
            int gender = trangBiThienSu.template.gender;
            int playerGender = player.gender;
            int[] maleOptions = {129, 141, 127, 139, 128, 140};
            int[] femaleOptions = {251, 254, 132, 144, 131, 143, 130, 142};
            int[] otherOptions = {135, 138, 133, 136, 134, 137};
            int[] selectedOptions;
            if (gender == 0 || gender == 3 && playerGender == 0) {
                selectedOptions = maleOptions;
            } else if (gender == 1 || gender == 3 && playerGender == 1) {
                selectedOptions = femaleOptions;
            } else {
                selectedOptions = otherOptions;
            }
            Item newItem = null;
            if (trangBiThienSu.template.type == 4) {
                newItem = ItemService.gI().createNewItem((short) 561);
            } else {
                newItem = ItemService.gI().createNewItem(Manager.trangBiKichHoatVip[gender][trangBiThienSu.template.type]);
            }
            RewardService.gI().initBaseOptionClothes(newItem.template.id, newItem.template.type, newItem.itemOptions);
            if (Util.isTrue(15, 100)) {
                newItem.itemOptions.add(new Item.ItemOption(selectedOptions[0], 0));
                newItem.itemOptions.add(new Item.ItemOption(selectedOptions[1], 0));
            } else {
                if (Util.isTrue(75, 100)) {
                    newItem.itemOptions.add(new Item.ItemOption(selectedOptions[2], 0));
                    newItem.itemOptions.add(new Item.ItemOption(selectedOptions[3], 0));
                } else {
                    newItem.itemOptions.add(new Item.ItemOption(selectedOptions[4], 0));
                    newItem.itemOptions.add(new Item.ItemOption(selectedOptions[5], 0));
                }
            }
            InventoryService.gI().addItemBag(player, newItem);
            InventoryService.gI().subQuantityItemsBag(player, trangBiThienSu, 1);
            InventoryService.gI().subQuantityItemsBag(player, daKichHoatVip, 1);
            CombineService.gI().sendEffectSuccessCombine(player);
            InventoryService.gI().sendItemBag(player);
            Service.gI().sendMoney(player);
            CombineService.gI().reOpenItemCombine(player);
        }
    }
}
