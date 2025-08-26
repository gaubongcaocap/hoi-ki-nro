package models.Combine.manifest;

import consts.ConstFont;
import consts.ConstNpc;
import item.Item;
import models.Combine.CombineService;
import player.Player;
import services.InventoryService;
import services.Service;
import utils.Util;

public class CuongHoaLoSaoPhaLe {

    private static float getRatio(int star) {
        return switch (star) {
            case 0 ->
                50;
            case 1 ->
                20;
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

    public static void showInfoCombine(Player player) {
        if (player.combine.itemsCombine.size() != 3) {
            Service.gI().sendDialogMessage(player, "Cần 1 trang bị có ô sao pha lê thứ 8 trở lên chưa cường hóa\n1 đá Hematite\n1 dùi đục");
            return;
        }
        Item hematite = null;
        Item duiDuc = null;
        Item trangBi = null;

        for (Item item : player.combine.itemsCombine) {
            if (item.template.type < 5||  item.template.type == 32) {
                trangBi = item;
            } else if (item.template.id == 1438) {
                duiDuc = item;
            } else if (item.template.id == 1423 || item.template.id == 1441) {
                hematite = item;
            }
        }

        if (trangBi == null || duiDuc == null || hematite == null) {
            Service.gI().sendDialogMessage(player, "Cần 1 trang bị có ô sao pha lê thứ 8 trở lên chưa cường hóa\n1 đá Hematite\n1 dùi đục");
            return;
        }

        int star = trangBi.getOptionParam(107);
        int starCuongHoa = trangBi.getOptionParam(228);

        if (star < 8 || star == starCuongHoa) {
            Service.gI().sendDialogMessage(player, "Cần 1 trang bị có ô sao pha lê thứ 8 trở lên chưa cường hóa\n1 đá Hematite\n1 dùi đục");
            return;
        }
        starCuongHoa++;
        if (starCuongHoa < 8) {
            starCuongHoa = 8;
        }
        StringBuilder text = new StringBuilder();
        text.append(ConstFont.BOLD_BLUE).append("Cường hóa\n");
        text.append(ConstFont.BOLD_BLUE).append("Ô Sao Pha lê thứ ").append(starCuongHoa).append("\n");
        text.append(ConstFont.BOLD_GREEN).append("Cần 1 Hematite\n");
        text.append(ConstFont.BOLD_GREEN).append(trangBi.template.name).append("\n");
        text.append(ConstFont.BOLD_BLUE).append("Tỉ lệ thành công: ").append(getRatioStr(star)).append("%\n");
        text.append(player.inventory.getGemAndRuby() >= 50 ? ConstFont.BOLD_BLUE : ConstFont.BOLD_RED).append("Cần 50 ngọc");
        if (player.inventory.getGemAndRuby() < 50) {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, text.toString(), "Còn thiếu\n" + Util.numberToMoney(50 - player.inventory.getGemAndRuby()) + " ngọc");
            return;
        }
        CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, text.toString(), "Cường hóa", "Từ chối");
    }

    public static void cuongHoaLoSaoPhaLe(Player player) {
        if (player.combine.itemsCombine.size() != 3) {
            return;
        }
        Item hematite = null;
        Item duiDuc = null;
        Item trangBi = null;

        for (Item item : player.combine.itemsCombine) {
            if (item.template.type < 5 ||  item.template.type == 32) {
                trangBi = item;
            } else if (item.template.id == 1438) {
                duiDuc = item;
            } else if (item.template.id == 1423 || item.template.id == 1441) {
                hematite = item;
            }
        }

        if (trangBi == null || duiDuc == null || hematite == null) {
            return;
        }

        int star = trangBi.getOptionParam(107);
        int starCuongHoa = trangBi.getOptionParam(228);

        if (star < 8 || star == starCuongHoa) {
            return;
        }
        starCuongHoa++;
        if (starCuongHoa < 8) {
            starCuongHoa = 8;
        }

        if (starCuongHoa > 8) {
            starCuongHoa = 9;
        }

        if (player.inventory.getGemAndRuby() < 50) {
            return;
        }
        CombineService.gI().baHatMit.npcChat(player, "Bá zô mà núc...");
        if (Util.isTrue(getRatio(star), 100)) {
            if (starCuongHoa >= 8 && star == 8) {
                trangBi.addOptionParam(228, 8);
            } else if (starCuongHoa > 8 && star == 9) {
                trangBi.removeOption(228);
                trangBi.addOptionParam(228,9);
            }
            CombineService.gI().sendEffectCombineItem(player, (byte) 7, (short) trangBi.template.iconID, (short) -1);
            Util.setTimeout(() -> {
                CombineService.gI().baHatMit.npcChat(player, "Chúc mừng con nhé");
                // CombineService.gI().reOpenItemCombine(player);
            }, 2000);
        } else {
            CombineService.gI().sendEffectCombineItem(player, (byte) 8, (short) -1, (short) -1);
            Util.setTimeout(() -> {
                CombineService.gI().baHatMit.npcChat(player, "Con còn hơi non nên đừng buồn con nhé");
                // CombineService.gI().reOpenItemCombine(player);
            }, 2000);
        }
        // Sau khi trừ gem, xóa nguyên liệu khỏi túi và gửi item bag:
        player.inventory.subGemAndRuby(50);
        InventoryService.gI().subQuantityItemsBag(player, hematite, 1);
        InventoryService.gI().subQuantityItemsBag(player, duiDuc, 1);
        InventoryService.gI().sendItemBag(player);
        Service.gI().sendMoney(player);
        CombineService.gI().reOpenItemCombine(player);
    }
}
