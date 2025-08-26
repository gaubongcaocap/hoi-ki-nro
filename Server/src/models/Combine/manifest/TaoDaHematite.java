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

public class TaoDaHematite {

    public static void showInfoCombine(Player player) {
        if (InventoryService.gI().getCountEmptyBag(player) == 0) {
            Service.gI().sendThongBao(player, "Cần 1 ô trống trong hành trang.");
            Service.gI().hideWaitDialog(player);
            return;
        }
        if (player.combine.itemsCombine.size() != 1) {
            Service.gI().sendDialogMessage(player, "Cần 5 viên sao pha lê cấp 2 cùng màu");
            return;
        }
        Item saoPhaLeC2 = player.combine.itemsCombine.get(0);

        if (saoPhaLeC2 == null || !saoPhaLeC2.isNotNullItem() || !saoPhaLeC2.isDaPhaLeC2()) {
            Service.gI().sendDialogMessage(player, "Cần 5 viên sao pha lê cấp 2 cùng màu");
            return;
        }
        StringBuilder text = new StringBuilder();
        text.append(ConstFont.BOLD_BLUE).append("Ta sẽ phù phép\n");
        text.append(ConstFont.BOLD_BLUE).append("tạo đá Hematite\n");
        text.append(saoPhaLeC2.quantity >= 5 ? ConstFont.BOLD_GREEN : ConstFont.BOLD_RED).append("Cần 5 Sao pha lê cấp 2 cùng màu\n");
        text.append(player.inventory.gold >= 1_000_000 ? ConstFont.BOLD_BLUE : ConstFont.BOLD_RED).append("Cần 1 Tr vàng\n");
        if (saoPhaLeC2.quantity < 5) {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, text.toString(), "Còn thiếu\n" + (5 - saoPhaLeC2.quantity) + " " + saoPhaLeC2.template.name);
            return;
        }
        if (player.inventory.gold < 1_000_000) {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, text.toString(), "Còn thiếu\n" + Util.numberToMoney(1_000_000 - player.inventory.gold) + " vàng");
            return;
        }
        CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, text.toString(), "Tạo đá\nHematite", "Từ chối");
    }

    public static void taoDaHematite(Player player) {
        if (InventoryService.gI().getCountEmptyBag(player) == 0) {
            return;
        }
        if (player.combine.itemsCombine.size() != 1) {
            Service.gI().sendDialogMessage(player, "Cần 5 viên sao pha lê cấp 2 cùng màu");
            return;
        }
        Item saoPhaLeC2 = player.combine.itemsCombine.get(0);

        if (saoPhaLeC2 == null || !saoPhaLeC2.isNotNullItem() || !saoPhaLeC2.isDaPhaLeC2()) {
            return;
        }
        if (player.inventory.gold < 1_000_000 || saoPhaLeC2.quantity < 5) {
            return;
        }
        CombineService.gI().baHatMit.npcChat(player, "Bư cô lô, ba cô la, bư ra bư zô...");
        Item daHematite = ItemService.gI().createNewItem((short) 1423);
        daHematite.itemOptions.add(new Item.ItemOption(30, 0));
        daHematite.itemOptions.add(new Item.ItemOption(87, 0));
        CombineService.gI().sendEffectCombineItem(player, (byte) 7, (short) daHematite.template.iconID, (short) -1);
        InventoryService.gI().addItemBag(player, daHematite);
        Service.gI().sendServerMessage(player, "Bạn nhận được " + daHematite.template.name);
        Util.setTimeout(() -> {
            CombineService.gI().baHatMit.npcChat(player, "Chúc mừng con nhé");
        }, 2000);
        player.inventory.gold -= 1_000_000;
        InventoryService.gI().subQuantityItemsBag(player, saoPhaLeC2, 1);
        InventoryService.gI().sendItemBag(player);
        Service.gI().sendMoney(player);
        CombineService.gI().reOpenItemCombine(player);
    }
}
