package models.Combine.manifest;

import consts.ConstFont;
import consts.ConstNpc;
import item.Item;
import models.Combine.CombineService;
import player.Player;
import services.InventoryService;
import services.Service;
import utils.Util;

public class NangChiSoBongTai {

    public static void showInfoCombine(Player player) {
        if (player.combine.itemsCombine.size() != 3) {
            Service.gI().sendDialogMessage(player, "Cần 1 bông tai cấp 2, 99 mảnh hồn porata và 1 đá xanh lam.");
            return;
        }
        Item bongTai = null;
        Item manhHonBongTai = null;
        Item daXanhLam = null;
        for (Item item : player.combine.itemsCombine) {
            if (item.isNotNullItem()) {
                switch (item.template.id) {
                    case 921 ->
                        bongTai = item;
                    case 934 ->
                        manhHonBongTai = item;
                    case 935 ->
                        daXanhLam = item;
                }
            }
        }

        if (bongTai == null || manhHonBongTai == null || daXanhLam == null) {
            Service.gI().sendDialogMessage(player, "Cần 1 bông tai cấp 2, 99 mảnh hồn porata và 1 đá xanh lam.");
            return;
        }

        StringBuilder text = new StringBuilder();
        text.append(ConstFont.BOLD_BLUE).append("Bông tai Porata [+2]\n\n");
        text.append(ConstFont.BOLD_BLUE).append("Tỉ lệ thành công: 50%\n");
        text.append(manhHonBongTai.quantity >= 99 ? ConstFont.BOLD_BLUE : ConstFont.BOLD_RED).append("Cần 99 Mảnh hồn bông tai\n");
        text.append(daXanhLam.quantity >= 1 ? ConstFont.BOLD_BLUE : ConstFont.BOLD_RED).append("Cần 1 Đá xanh lam\n");
        text.append(player.inventory.getGemAndRuby() >= 250 ? ConstFont.BOLD_BLUE : ConstFont.BOLD_RED).append("Cần 250 ngọc\n");
        text.append(ConstFont.BOLD_GREEN).append("+1 Chỉ số ngẫu nhiên\n");
        if (player.inventory.getGemAndRuby() < 250) {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, text.toString(), "Còn thiếu\n" + Util.numberToMoney(250 - player.inventory.getGemAndRuby()) + " ngọc");
            return;
        }
        if (daXanhLam.quantity < 1) {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, text.toString(), "Còn thiếu\nĐá xanh lam");
            return;
        }
        if (manhHonBongTai.quantity < 99) {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, text.toString(), "Còn thiếu\n" + (99 - manhHonBongTai.quantity) + " Mảnh hồn bông tai");
            return;
        }
        CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, text.toString(), "Nâng cấp\n250 ngọc", "Từ chối");
    }

    public static void nangChiSoBongTai(Player player) {
        if (player.combine.itemsCombine.size() != 3) {
            return;
        }
        Item bongTai = null;
        Item manhHonBongTai = null;
        Item daXanhLam = null;
        for (Item item : player.combine.itemsCombine) {
            if (item.isNotNullItem()) {
                switch (item.template.id) {
                    case 921 ->
                        bongTai = item;
                    case 934 ->
                        manhHonBongTai = item;
                    case 935 ->
                        daXanhLam = item;
                }
            }
        }

        if (bongTai == null || manhHonBongTai == null || daXanhLam == null || player.inventory.getGemAndRuby() < 250 || daXanhLam.quantity < 1 || manhHonBongTai.quantity < 99) {
            return;
        }
        if (Util.isTrue(50, 100)) {
            int[] options = {77, 103, 50, 108, 94, 14, 80, 81, 175, 5};
            int option = options[Util.nextInt(options.length)];
            int param = option == 94 || option == 14 ? Util.nextInt(3, 10) : Util.nextInt(5, 15);
            bongTai.itemOptions.clear();
            bongTai.itemOptions.add(new Item.ItemOption(option, param));
            bongTai.itemOptions.add(new Item.ItemOption(38, 0));
            CombineService.gI().sendEffectSuccessCombine(player);
        } else {
            CombineService.gI().sendEffectFailCombine(player);
        }
        InventoryService.gI().subQuantityItemsBag(player, manhHonBongTai, 99);
        InventoryService.gI().subQuantityItemsBag(player, daXanhLam, 1);
        InventoryService.gI().sendItemBag(player);
        CombineService.gI().reOpenItemCombine(player);
    }

}
