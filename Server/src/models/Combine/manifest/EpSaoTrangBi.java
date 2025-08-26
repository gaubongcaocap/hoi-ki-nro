package models.Combine.manifest;

import consts.ConstFont;
import consts.ConstNpc;
import item.Item;
import models.Combine.CombineService;
import player.Player;
import server.ServerNotify;
import services.InventoryService;
import services.Service;

public class EpSaoTrangBi {

    public static int getGem(int star) {
        return switch (star) {
            case 7 ->
                200;
            case 8 ->
                300;
            case 9 ->
                500;
            default ->
                10;
        };
    }

    public static void showInfoCombine(Player player) {
        if (player.combine.itemsCombine.size() != 2) {
            Service.gI().sendDialogMessage(player, "Cần 1 trang bị có lỗ sao pha lê và 1 loại ngọc để ép vào.");
            return;
        }
        Item trangBi = null;
        Item daPhaLe = null;
        for (Item item : player.combine.itemsCombine) {
            if (item.canPhaLeHoa()) {
                trangBi = item;
            } else if (item.isDaPhaLeEpSao()) {
                daPhaLe = item;
            }
        }
        if (trangBi == null || !trangBi.isNotNullItem() || daPhaLe == null || !daPhaLe.isNotNullItem()) {
            Service.gI().sendDialogMessage(player, "Cần 1 trang bị có lỗ sao pha lê và 1 loại ngọc để ép vào.");
            return;
        }
        int star = trangBi.getOptionParam(102);
        int starEmpty = trangBi.getOptionParam(107);
        int cuongHoa = trangBi.getOptionParam(228);
        if (star < 7 && daPhaLe.isDaPhaLeMoi()) {
            Service.gI().sendDialogMessage(player, "Sao pha lê cấp 2 hoặc lấp lánh chỉ dùng cho ô thứ 8 đã cường hóa trở lên.");
            return;
        }

        if (star < 7 && daPhaLe.isNgocRongVIP()) {
            Service.gI().sendDialogMessage(player, "Ngọc rồng VIP chỉ dùng cho ô thứ 8 đã cường hóa trở lên.");
            return;
        }

        if (star >= 7 && daPhaLe.isDaPhaLeCu()) {
            Service.gI().sendDialogMessage(player, "Chỉ có thể nạm Sao pha lê mới.");
            return;
        }
        if (star >= starEmpty) {
            Service.gI().sendDialogMessage(player, "Cần 1 trang bị có lỗ sao pha lê và 1 loại ngọc để ép vào.");
            return;
        }
        if (star == 7 && cuongHoa == 0 || star >= 8 && cuongHoa < star + 1) {
            Service.gI().sendDialogMessage(player, "Cần cường hóa ô sao pha lê này trước");
            return;
        }
        StringBuilder text = new StringBuilder();
        text.append(ConstFont.BOLD_BLUE).append(trangBi.template.name).append("\n");
        text.append(ConstFont.BOLD_DARK).append(star >= 7 ? trangBi.getOptionInfoCuongHoa(daPhaLe) : trangBi.getOptionInfo(daPhaLe)).append("\n");
        text.append(player.inventory.getGemAndRuby() < getGem(star) ? ConstFont.BOLD_RED : ConstFont.BOLD_BLUE).append("Cần 10 ngọc");
        if (player.inventory.getGemAndRuby() < getGem(star)) {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, text.toString(), "Còn thiếu\n" + (getGem(star) - player.inventory.getGemAndRuby()) + " ngọc");
            return;
        }
        // CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, text.toString(), "Nâng cấp\n" + getGem(star) + " ngọc", "Từ chối");
        CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, text.toString(),"Nâng cấp\n7 lần", "Nâng cấp\n1 lần", "Từ chối");
    }

    public static void epSaoTrangBi(Player player, int... numm) {
        int n = 1;
        if (numm.length > 0) {
            n = numm[0];
        }

        Item trangBi = null;
        Item daPhaLe = null;
        for (Item item : player.combine.itemsCombine) {
            if (item.canPhaLeHoa()) {
                trangBi = item;
            } else if (item.isDaPhaLeEpSao()) {
                daPhaLe = item;
            }
        }
        if (trangBi == null || !trangBi.isNotNullItem() || daPhaLe == null || !daPhaLe.isNotNullItem() || player.combine.itemsCombine.size() != 2) {
            Service.gI().sendDialogMessage(player, "Cần 1 trang bị có lỗ sao pha lê và 1 loại ngọc để ép vào.");
            return;
        }
        int star = trangBi.getOptionParam(102);
        int starEmpty = trangBi.getOptionParam(107);
        int cuongHoa = trangBi.getOptionParam(228);
        int gem = getGem(star);
        int playerGem = player.inventory.getGemAndRuby();

        if (star >= starEmpty || star < 7 && daPhaLe.isDaPhaLeMoi() || star >= 7 && daPhaLe.isDaPhaLeCu()
                || star == 7 && cuongHoa == 0 || star >= 8 && cuongHoa < star + 1) {
            return;
        }

        if (n == 1 && playerGem < gem) {
            Service.gI().sendServerMessage(player, "Bạn không đủ ngọc, còn thiếu " + (gem - player.inventory.getGemAndRuby()) + " ngọc nữa");
            return;
        }

        int num = 0;
        boolean success = true;

        for (int i = 0; i < n; i++) {
            if (playerGem < gem) {
                success = false;
                break;
            }

            if ((star + i) >= 7 && daPhaLe.isDaPhaLeCu()) {
                break;
            }

            if ((star + i) == 9)  {
                break;
            }
            
            player.inventory.subGemAndRuby(getGem(star + i));
            num++;
        }

        if(num > 0) {
            if (num > 0 && star == 7) {
                trangBi.itemOptions.add(new Item.ItemOption(218, 0));
            }
            
            if (star >= 7) {
                trangBi.itemOptions.add(new Item.ItemOption(daPhaLe.getOptionDaPhaLe().optionTemplate.id, daPhaLe.getOptionDaPhaLe().param  * num));
            } else {
                trangBi.addOptionParam(daPhaLe.getOptionDaPhaLe().optionTemplate.id, daPhaLe.getOptionDaPhaLe().param  * num);
            }
            trangBi.addOptionParam(102,  num);
            if (success) {
                Service.gI().sendServerMessage(player, "Ép thành công " + num + " sao pha lê.");
            } else {
                Service.gI().sendServerMessage(player, "Ép thành công " + num + " sao pha lê, bạn không đủ ngọc, còn thiếu " + (gem - player.inventory.getGemAndRuby()) + " ngọc nữa");
            }
            InventoryService.gI().subQuantityItemsBag(player, daPhaLe, num);
            CombineService.gI().sendEffectSuccessCombine(player);
            InventoryService.gI().sendItemBag(player);
            Service.gI().sendMoney(player);
            CombineService.gI().reOpenItemCombine(player);
        } else {
            Service.gI().sendServerMessage(player, "Trang bị không thể nâng cấp");
        }
    }
}
