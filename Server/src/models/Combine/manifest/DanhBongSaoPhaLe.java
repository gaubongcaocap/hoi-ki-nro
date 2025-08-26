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

public class DanhBongSaoPhaLe {

    public static void showInfoCombine(Player player) {
        if (InventoryService.gI().getCountEmptyBag(player) == 0) {
            Service.gI().sendThongBao(player, "Cần 1 ô trống trong hành trang.");
            Service.gI().hideWaitDialog(player);
            return;
        }
        if (player.combine.itemsCombine.size() != 2) {
            Service.gI().sendDialogMessage(player, "Cần 2 viên sao pha lê cấp 2 cùng màu và 1 đá mài");
            return;
        }
        Item daMai = null;
        Item saoPhaLeC2 = null;

        for (Item item : player.combine.itemsCombine) {
            if (item.template.id == 1439) {
                daMai = item;
            } else if (item.isDaPhaLeC2()) {
                saoPhaLeC2 = item;
            }
        }

        if (daMai == null || saoPhaLeC2 == null) {
            Service.gI().sendDialogMessage(player, "Cần 2 viên sao pha lê cấp 2 cùng màu và 1 đá mài");
            return;
        }
        StringBuilder text = new StringBuilder();
        text.append(ConstFont.BOLD_BLUE).append("Đánh bóng sao pha lê cấp 2\n");
        text.append(saoPhaLeC2.quantity >= 2 ? ConstFont.BOLD_GREEN : ConstFont.BOLD_RED).append("Cần 2 ").append(saoPhaLeC2.template.name).append("\n");
        text.append(ConstFont.BOLD_GREEN).append("Cần 1 Đá mài\n");
        text.append(ConstFont.BOLD_GREEN).append("Tỉ lệ thành công: 30%\n");
        text.append(player.inventory.gold >= 100_000_000 ? ConstFont.BOLD_BLUE : ConstFont.BOLD_RED).append("Cần 100 Tr vàng\n");
        if (player.inventory.gold < 100_000_000) {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, text.toString(), "Còn thiếu\n" + Util.numberToMoney(100_000_000 - player.inventory.gold) + " vàng");
            return;
        }
        if (saoPhaLeC2.quantity < 2) {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, text.toString(), "Còn thiếu\n" + Util.numberToMoney(2 - saoPhaLeC2.quantity) + " " + saoPhaLeC2.template.name);
            return;
        }
        CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, text.toString(), "Đánh bóng", "Từ chối");
    }

    public static void danhBongSaoPhaLe(Player player) {
        if (InventoryService.gI().getCountEmptyBag(player) == 0) {
            return;
        }
        if (player.combine.itemsCombine.size() != 2) {
            return;
        }
        Item daMai = null;
        Item saoPhaLeC2 = null;

        for (Item item : player.combine.itemsCombine) {
            if (item.template.id == 1439) {
                daMai = item;
            } else if (item.isDaPhaLeC2()) {
                saoPhaLeC2 = item;
            }
        }

        if (daMai == null || saoPhaLeC2 == null) {
            return;
        }
        if (saoPhaLeC2.quantity < 2 || player.inventory.gold < 100_000_000) {
            return;
        }
        CombineService.gI().baHatMit.npcChat(player, "Bư cô lô, ba cô la, bư ra bư zô...");
        if (Util.isTrue(30, 100)) {
            Item saoPhaLeLapLanh = saoPhaLeC2.cloneItem();
            saoPhaLeLapLanh.quantity = 1;
            int tempId = saoPhaLeC2.template.id == 964 ? 1433 : saoPhaLeC2.template.id == 965 ? 1434 : saoPhaLeC2.template.id + 10;
            saoPhaLeLapLanh.template = ItemService.gI().getTemplate(tempId);
            for (Item.ItemOption io : saoPhaLeLapLanh.itemOptions) {
                if (io.optionTemplate.id != 30 && io.optionTemplate.id != 87) {
                    io.param++;
                }
            }
            CombineService.gI().sendEffectCombineItem(player, (byte) 7, (short) saoPhaLeLapLanh.template.iconID, (short) -1);
            InventoryService.gI().addItemBag(player, saoPhaLeLapLanh);
            Util.setTimeout(() -> {
                Service.gI().sendServerMessage(player, "Bạn nhận được " + saoPhaLeLapLanh.template.name);
                CombineService.gI().baHatMit.npcChat(player, "Chúc mừng con nhé");
            }, 2000);
        } else {
            CombineService.gI().sendEffectCombineItem(player, (byte) 8, (short) -1, (short) -1);
            Util.setTimeout(() -> {
                CombineService.gI().baHatMit.npcChat(player, "Chúc con may mắn lần sau, đừng buồn con nhé");
            }, 2000);
        }
        player.inventory.gold -= 100_000_000;
        InventoryService.gI().subQuantityItemsBag(player, daMai, 1);
        InventoryService.gI().subQuantityItemsBag(player, saoPhaLeC2, 2);
        InventoryService.gI().sendItemBag(player);
        Service.gI().sendMoney(player);
        CombineService.gI().reOpenItemCombine(player);
    }
}
