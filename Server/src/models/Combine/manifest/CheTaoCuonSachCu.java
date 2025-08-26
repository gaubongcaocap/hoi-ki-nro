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

public class CheTaoCuonSachCu {

    public static void showCombine(Player player) {
        Item trangSachCu = InventoryService.gI().findItemBag(player, 1281);
        Item biaSach = InventoryService.gI().findItemBag(player, 1282);
        int quantityTrangSachCu = trangSachCu != null ? trangSachCu.quantity : 0;
        int quantityBiaSach = biaSach != null ? biaSach.quantity : 0;
        StringBuilder text = new StringBuilder();
        text.append(ConstFont.BOLD_GREEN).append("Chế tạo Cuốn sách cũ\n");
        text.append(quantityTrangSachCu >= 9999 ? ConstFont.BOLD_BLUE : ConstFont.BOLD_RED).append("Trang sách cũ ").append(quantityTrangSachCu).append("/9999\n");
        text.append(quantityBiaSach >= 1 ? ConstFont.BOLD_BLUE : ConstFont.BOLD_RED).append("Bìa sách ").append(quantityBiaSach).append("/1\n");
        text.append(quantityTrangSachCu < 9999 || quantityBiaSach < 1 ? ConstFont.BOLD_RED : ConstFont.BOLD_BLUE).append("Tỉ lệ thành công: 20%\n");
        text.append(ConstFont.BOLD_RED).append("Thất bại mất 99 trang sách và 1 bìa sách");
        if (quantityTrangSachCu < 9999 || quantityBiaSach < 1) {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, text.toString(), "Từ chối");
            return;
        }
        CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.DONG_THANH_SACH_CU, text.toString(), "Đồng ý", "Từ chối");
    }

    public static void cheTaoCuonSachCu(Player player) {
        if (InventoryService.gI().getCountEmptyBag(player) == 0 && InventoryService.gI().findItemBag(player, 1283) == null) {
            Service.gI().sendThongBao(player, "Cần 1 ô trống trong hành trang.");
            return;
        }
        Item trangSachCu = InventoryService.gI().findItemBag(player, 1281);
        Item biaSach = InventoryService.gI().findItemBag(player, 1282);
        int quantityTrangSachCu = trangSachCu != null ? trangSachCu.quantity : 0;
        int quantityBiaSach = biaSach != null ? biaSach.quantity : 0;
        if (quantityTrangSachCu < 9999 || quantityBiaSach < 1) {
            return;
        }
        CombineService.gI().sendAddItemCombine(player, ConstNpc.BA_HAT_MIT, trangSachCu, biaSach);
        int subTrangSach;
        if (Util.isTrue(20, 100)) {
            subTrangSach = 9999;
            Item cuonSachCu = ItemService.gI().createNewItem((short) 1283);
            cuonSachCu.itemOptions.add(new Item.ItemOption(30, 0));
            InventoryService.gI().addItemBag(player, cuonSachCu);
            CombineService.gI().sendEffSuccessVip(player, cuonSachCu.template.iconID);
            Util.setTimeout(() -> {
                Service.gI().sendServerMessage(player, "Bạn nhận được " + cuonSachCu.template.name);
                CombineService.gI().baHatMit.npcChat(player, "Chúc mừng con nhé");
            }, 2000);
        } else {
            subTrangSach = 99;
            CombineService.gI().sendEffFailVip(player);
            Util.setTimeout(() -> {
                CombineService.gI().baHatMit.npcChat(player, "Chúc con may mắn lần sau, đừng buồn con nhé");
            }, 2000);
        }
        InventoryService.gI().subQuantityItemsBag(player, trangSachCu, subTrangSach);
        InventoryService.gI().subQuantityItemsBag(player, biaSach, 1);
        InventoryService.gI().sendItemBag(player);
    }

}
