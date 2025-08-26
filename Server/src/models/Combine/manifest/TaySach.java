package models.Combine.manifest;

import consts.ConstFont;
import consts.ConstNpc;
import item.Item;
import models.Combine.CombineService;
import player.Player;
import services.InventoryService;
import services.Service;
import utils.Util;

public class TaySach {

    public static void showInfoCombine(Player player) {
        if (player.combine.itemsCombine.size() != 1) {
            Service.gI().sendDialogMessage(player, "Cần Sách Tuyệt Kỹ để tẩy.");
        }
        Item sachTuyetKy = player.combine.itemsCombine.get(0);
        if (sachTuyetKy == null || !sachTuyetKy.isSachTuyetKy() && !sachTuyetKy.isSachTuyetKy2()) {
            Service.gI().sendDialogMessage(player, "Cần Sách Tuyệt Kỹ để tẩy.");
            return;
        }
        CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, ConstFont.BOLD_BLUE + "Tẩy Sách Tuyệt Kỹ ?", "Đồng ý", "Từ chối");
    }

    public static void taySach(Player player) {
        if (player.combine.itemsCombine.size() != 1) {
        }
        Item sachTuyetKy = player.combine.itemsCombine.get(0);
        if (sachTuyetKy == null || !sachTuyetKy.isSachTuyetKy() && !sachTuyetKy.isSachTuyetKy2()) {
            return;
        }
        if (sachTuyetKy.getOptionParam(219) <= 0 || sachTuyetKy.isHaveOption(217)) {
            Service.gI().sendServerMessage(player, "Không thể thực hiện");
            return;
        }
        for (int i = 0; i < sachTuyetKy.itemOptions.size(); i++) {
            Item.ItemOption io = sachTuyetKy.itemOptions.get(i);
            if (io.optionTemplate.id == 21) {
                break;
            }
            sachTuyetKy.itemOptions.set(i, new Item.ItemOption(217, 0));
        }
        sachTuyetKy.subOptionParam(219, 1);
        CombineService.gI().sendEffectSuccessCombine(player);
        InventoryService.gI().sendItemBag(player);
        CombineService.gI().reOpenItemCombine(player);
    }

}
