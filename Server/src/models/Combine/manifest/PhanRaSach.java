package models.Combine.manifest;

import consts.ConstFont;
import consts.ConstNpc;
import item.Item;
import models.Combine.CombineService;
import player.Player;
import services.InventoryService;
import services.ItemService;
import services.Service;
import utils.Util;

public class PhanRaSach {

    public static void showInfoCombine(Player player) {
        if (player.combine.itemsCombine.size() != 1) {
            Service.gI().sendDialogMessage(player, "Không tìm thấy vật phẩm");
            return;
        }
        Item sachTuyetKy = null;
        for (Item item : player.combine.itemsCombine) {
            if (item.isSachTuyetKy() || item.isSachTuyetKy2()) {
                sachTuyetKy = item;
            }
        }
        if (sachTuyetKy == null) {
            Service.gI().sendDialogMessage(player, "Không tìm thấy vật phẩm");
            return;
        }
        StringBuilder text = new StringBuilder();
        text.append(ConstFont.BOLD_BLUE).append("Phân rã sách\n");
        text.append(ConstFont.BOLD_BLUE).append("Nhận lại 5 cuốn sách cũ\n");
        text.append(player.inventory.gold >= 10_000_000 ? ConstFont.BOLD_BLUE : ConstFont.BOLD_RED).append("Phí rã 10 triệu vàng");
        if (player.inventory.gold < 10_000_000) {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, text.toString(),
                    "Còn thiếu\n" + Util.numberToMoney(10_000_000 - player.inventory.gold) + " vàng");
            return;
        }
        CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, text.toString(),
                "Đồng ý", "Từ chối");
    }

    public static void phanRaSach(Player player) {
        if (player.combine.itemsCombine.size() != 1) {
            return;
        }
        Item sachTuyetKy = null;
        for (Item item : player.combine.itemsCombine) {
            if (item.isSachTuyetKy() || item.isSachTuyetKy2()) {
                sachTuyetKy = item;
            }
        }
        if (sachTuyetKy == null || player.inventory.gold < 10_000_000) {
            return;
        }
        InventoryService.gI().subQuantityItemsBag(player, sachTuyetKy, 1);
        Item cuonSachCu = ItemService.gI().createNewItem((short) 1283, 5);
        cuonSachCu.itemOptions.add(new Item.ItemOption(30, 0));
        InventoryService.gI().addItemBag(player, cuonSachCu);
        CombineService.gI().sendEffectSuccessCombine(player);
        Service.gI().sendMoney(player);
        InventoryService.gI().sendItemBag(player);
        CombineService.gI().reOpenItemCombine(player);
    }
}
