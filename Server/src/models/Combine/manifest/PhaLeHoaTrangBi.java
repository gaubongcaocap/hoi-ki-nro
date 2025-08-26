package models.Combine.manifest;

import consts.ConstFont;
import consts.ConstNpc;
import item.Item;
import models.Combine.CombineService;
import player.Player;
import server.ServerNotify;
import services.ChatGlobalService;
import services.InventoryService;
import services.Service;
import utils.Util;

public class PhaLeHoaTrangBi {

    private static float getRatio(int star) {
        return switch (star) {
            case 0 ->
                80;
            case 1 ->
                50;
            case 2 ->
                20;
            case 3 ->
                10;
            case 4 ->
                5;
            case 5 ->
                2;
            case 6 ->
                1f;
            case 7 ->
                0.5f;
            case 8 ->
                0.1f;
            case 9 ->
                0.07f;
            default ->
                0;
        };
    }

    private static String getRatioStr(int star) {
        int ratio = (int) getRatio(star);
        if (ratio < 1) {
            ratio = 1;
        }
        return String.valueOf(ratio);
    }

    private static int getGold(int star) {
        return switch (star) {
            case 0 ->
                5_000_000;
            case 1 ->
                10_000_000;
            case 2 ->
                20_000_000;
            case 3 ->
                40_000_000;
            case 4 ->
                60_000_000;
            case 5 ->
                90_000_000;
            case 6 ->
                120_000_000;
            case 7 ->
                150_000_000;
            case 8 ->
                180_000_000;
            default ->
                0;
        };
    }

    private static int getGem(int star) {
        return switch (star) {
            case 0 ->
                5;
            case 1 ->
                7;
            case 2 ->
                9;
            case 3 ->
                11;
            case 4 ->
                13;
            case 5 ->
                15;
            case 6 ->
                17;
            case 7 ->
                20;
            case 8 ->
                30;
            default ->
                0;
        };
    }

    public static void showInfoCombine(Player player) {
        if (player.combine.itemsCombine.size() != 1) {
            Service.gI().sendDialogMessage(player, "Trang bị không phù hợp");
            return;
        }
        Item item = player.combine.itemsCombine.get(0);
        if (item == null || !item.isNotNullItem()) {
            return;
        }
        if (item.isHaveOption(93)) {
            Service.gI().sendDialogMessage(player, "Trang bị có hạn sử dụng, không thể thực hiện");
            return;
        }
        if (!item.canPhaLeHoa()) {
            Service.gI().sendDialogMessage(player, "Trang bị không phù hợp");
            return;
        }
        int star = item.getOptionParam(107);
        int gem = getGem(star);
        int gold = getGold(star);
        if (star >= CombineService.MAX_STAR_ITEM) {
            Service.gI().sendDialogMessage(player, "Đã đạt số pha lê tối đa");
            return;
        }
        StringBuilder text = new StringBuilder();
        text.append(ConstFont.BOLD_BLUE).append(item.template.name).append("\n");
        text.append(ConstFont.BOLD_DARK).append(item.getOptionInfo()).append("\n");
        text.append(ConstFont.BOLD_GREEN).append(star + 1).append(" ô Sao Pha Lê\n");
        text.append(ConstFont.BOLD_BLUE).append("Tỉ lệ thành công: ").append(getRatioStr(star)).append("%\n");
        text.append(player.inventory.gold < gold ? ConstFont.BOLD_RED : ConstFont.BOLD_BLUE).append("Cần ").append(Util.numberToMoney(gold)).append(" vàng");
        if (player.inventory.gold < gold) {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, text.toString(),
                    "Còn thiếu\n" + Util.numberToMoney(gold - player.inventory.gold) + " vàng");
            return;
        }
        CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, text.toString(),
                "Nâng cấp\n500 lần","Nâng cấp\n100 lần", "Nâng cấp\n10 lần", "Nâng cấp\n1 lần", "Từ chối");
    }

    public static void phaLeHoa(Player player, int... numm) {
        int n = 1;
        if (numm.length > 0) {
            n = numm[0];
        }
        if (!player.combine.itemsCombine.isEmpty()) {
            Item item = player.combine.itemsCombine.get(0);
            if (item == null || !item.isNotNullItem() || item.isHaveOption(93) || !item.canPhaLeHoa()) {
                Service.gI().sendDialogMessage(player, "Vui lòng chọn vật phẩm có thể pha lê hóa");
                return;
            }
            int star = item.getOptionParam(107);
            if (star >= CombineService.MAX_STAR_ITEM) {
                Service.gI().sendDialogMessage(player, "Trang bị đã đạt giới hạn, không thể pha lê hóa");
                return;
            }
            int gold = getGold(star);
            int gem = getGem(star);
            if (n == 1) {
                if (player.inventory.gold < gold) {
                    return;
                } else if (player.inventory.getGemAndRuby() < gem) {
                    Service.gI().sendServerMessage(player, "Bạn không đủ ngọc, còn thiếu " + (gem - player.inventory.getGemAndRuby()) + " ngọc nữa");
                    return;
                }
            }
            int num = 0;
            boolean success = false;
            for (int i = 0; i < n; i++) {
                num = i + 1;
                if (player.inventory.getGemAndRuby() < gem) {
                    Service.gI().sendServerMessage(player, "Sau " + i + " lần nâng cấp thất bại, bạn không đủ ngọc để tiếp tục.");
                    break;
                }
                if (player.inventory.gold < gold) {
                    Service.gI().sendServerMessage(player, "Sau " + i + " lần nâng cấp thất bại, bạn không đủ vàng để tiếp tục.");
                    break;
                }
                player.inventory.gold -= gold;
                player.inventory.subGemAndRuby(gem);
                if (Util.isTrue(getRatio(star), 150)) {
                    success = true;
                    break;
                }
            }

            if (success) {
                item.addOptionParam(107, 1);
                if (star + 1 == 9) {
                    String message = "Chúc mừng " + player.name + " vừa pha lê hóa thành công "
                            + item.template.name + " lên " + (star + 1) + " sao pha lê. Mọi người đều ngưỡng mộ";

                    // Gửi thông báo server
                    ServerNotify.gI().notify(message);

                    // Gửi chat thế giới
                    //ChatGlobalService.gI().autoChatGlobal(player, "[Hệ thống] " + message);
                }
                if (n > 1) {
                    Service.gI().sendServerMessage(player, "Thành công sau " + num + " lần nâng cấp.");
                }
                CombineService.gI().sendEffectSuccessCombine(player);
            } else {
                CombineService.gI().sendEffectFailCombine(player);
            }
            InventoryService.gI().sendItemBag(player);
            Service.gI().sendMoney(player);
            CombineService.gI().reOpenItemCombine(player);
        }
    }

}
