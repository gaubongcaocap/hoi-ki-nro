package models.Combine.manifest;

import consts.ConstFont;
import consts.ConstNpc;
import item.Item;
import models.Combine.CombineService;
import player.Player;
import server.ServerNotify;
import services.InventoryService;
import services.ItemService;
import services.Service;
import utils.Util;

public class CheTaoTrangBiThienSu {

    public static void showInfoCombine(Player player) {
        if (InventoryService.gI().getCountEmptyBag(player) == 0) {
            Service.gI().sendServerMessage(player, "Hành trang đã đầy, cần một ô trống trong hành trang");
            return;
        }
        long cCongThuc = player.combine.itemsCombine.stream().filter(item -> item.isNotNullItem() && (item.isCongThuc() || item.isCongThucVip())).count();
        long cManhThienSu = player.combine.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.isManhThienSu()).count();
        long cDaMayMan = player.combine.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.isDaMayMan()).count();
        long cDaNangCap = player.combine.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.isDaNangCapTS()).count();
        if (cCongThuc == 0) {
            Service.gI().sendDialogMessage(player, "Cần 1 công thức");
        } else if (cCongThuc > 1) {
            Service.gI().sendDialogMessage(player, "Chỉ cần 1 công thức");
        } else if (cManhThienSu == 0) {
            Service.gI().sendDialogMessage(player, "Cần 1 loại mảnh");
        } else if (cManhThienSu > 1) {
            Service.gI().sendDialogMessage(player, "Chỉ cần 1 loại mảnh");
        } else if (cDaNangCap > 1) {
            Service.gI().sendDialogMessage(player, "Chỉ cần 1 đá nâng cấp");
        } else if (cDaMayMan > 1) {
            Service.gI().sendDialogMessage(player, "Chỉ cần 1 đá may mắn");
        } else {
            Item congThuc = null, manhThienSu = null, daNangCap = null, daMayMan = null;
            for (Item item : player.combine.itemsCombine) {
                if (item.isCongThuc() || item.isCongThucVip()) {
                    congThuc = item;
                } else if (item.isManhThienSu()) {
                    manhThienSu = item;
                } else if (item.isDaNangCapTS()) {
                    daNangCap = item;
                } else if (item.isDaMayMan()) {
                    daMayMan = item;
                }
            }
            if (congThuc == null || !congThuc.isNotNullItem() || manhThienSu == null || !manhThienSu.isNotNullItem()) {
                return;
            }
            int ratioDNC = daNangCap != null ? (daNangCap.template.id - 1073) * 10 : 0;
            int ratioDMM = daMayMan != null ? (daMayMan.template.id - 1078) * 10 : 0;
            int ratioCT = congThuc.isCongThucVip() ? 35 : 25;
            Item doThienSu = ItemService.gI().getAngelItem(congThuc.template.gender, manhThienSu.typeManh());
            StringBuilder text = new StringBuilder();
            text.append(ConstFont.BOLD_GREEN).append("Chế tạo ").append(doThienSu.template.name).append(" ").append(doThienSu.getGenderName()).append("\nMạnh hơn trang bị Hủy Diệt từ 20% đến 35%\n");
            text.append(manhThienSu.quantity >= 999 ? ConstFont.BOLD_BLUE : ConstFont.BOLD_RED).append("Mảnh ghép ").append(manhThienSu.quantity).append("/999 (Thất bại -99 mảnh ghép)\n");
            text.append(ConstFont.BOLD_BLUE).append(daNangCap != null ? daNangCap.template.name : "Không dùng đá nâng cấp").append(" (thêm ").append(ratioDNC).append("% tỉ lệ thành công)\n");
            text.append(ConstFont.BOLD_BLUE).append(daMayMan != null ? daMayMan.template.name : "Không dùng đá may mắn").append(" (thêm ").append(ratioDMM).append("% tỉ lệ tối đa các chỉ số)\n");
            text.append(ConstFont.BOLD_BLUE).append("Tỉ lệ thành công: ").append(ratioDNC + ratioCT).append("%\n");
            text.append(player.inventory.gold >= 200_000_000 ? ConstFont.BOLD_BLUE : ConstFont.BOLD_RED).append("Phí nâng cấp: 200 triệu vàng");
            if (manhThienSu.quantity < 999 || player.inventory.gold < 200_000_000) {
                CombineService.gI().whis.createOtherMenu(player, ConstNpc.IGNORE_MENU, text.toString(), "Từ chối");
                return;
            }
            CombineService.gI().whis.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, text.toString(), "Đồng ý", "Từ chối");
        }
    }

    public static void cheTaoTrangBiThienSu(Player player) {
        if (InventoryService.gI().getCountEmptyBag(player) == 0) {
            return;
        }
        long cCongThuc = player.combine.itemsCombine.stream().filter(item -> item.isNotNullItem() && (item.isCongThuc() || item.isCongThucVip())).count();
        long cManhThienSu = player.combine.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.isManhThienSu()).count();
        long cDaMayMan = player.combine.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.isDaMayMan()).count();
        long cDaNangCap = player.combine.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.isDaNangCapTS()).count();
        if (cCongThuc == 1 && cManhThienSu == 1 && cDaNangCap <= 1 && cDaMayMan <= 1) {
            Item congThuc = null, manhThienSu = null, daNangCap = null, daMayMan = null;
            for (Item item : player.combine.itemsCombine) {
                if (item.isCongThuc() || item.isCongThucVip()) {
                    congThuc = item;
                } else if (item.isManhThienSu()) {
                    manhThienSu = item;
                } else if (item.isDaNangCapTS()) {
                    daNangCap = item;
                } else if (item.isDaMayMan()) {
                    daMayMan = item;
                }
            }
            if (congThuc == null || !congThuc.isNotNullItem() || manhThienSu == null || !manhThienSu.isNotNullItem()) {
                return;
            }
            int ratioDNC = daNangCap != null ? (daNangCap.template.id - 1073) * 10 : 0;
            int ratioDMM = daMayMan != null ? (daMayMan.template.id - 1078) * 10 : 0;
            int ratioCT = congThuc.isCongThucVip() ? 35 : 25;
            int ratio = ratioDNC + ratioCT;
            if (manhThienSu.quantity >= 999 && player.inventory.gold >= 200_000_000) {
                if (Util.isTrue(ratio, 100)) {
                    int bonus = Util.isTrue(ratioDMM, 100) ? Util.nextInt(0, 15 - Util.nextInt(6)) : Util.nextInt(0, 35 - Util.nextInt(11));
                    int bonus2 = Util.isTrue(ratioDMM, 100) ? Util.nextInt(0, 3 - Util.nextInt(4)) : Util.nextInt(0, 1 - Util.nextInt(2));
                    Item doThienSu = ItemService.gI().getAngelItem(congThuc.template.gender, manhThienSu.typeManh());
                    for (Item.ItemOption io : doThienSu.itemOptions) {
                        if (io.isOptionCanUpgrade()) {
                            io.param += io.param * bonus / 100;
                        }
                    }
                    if (Util.isTrue(95, 100) && bonus2 > 0) {
                        doThienSu.itemOptions.add(new Item.ItemOption(41, bonus2));
                        int[] ops = {50, 77, 103, 197, 198, 199, 200, 201, 202, 203, 204};
                        Util.shuffleArray(ops);
                        for (int i = 0; i < bonus2; i++) {
                            doThienSu.itemOptions.add(new Item.ItemOption(ops[i], Util.nextInt(1, 5)));
                        }

                        String message = "Whis: " + player.name + " đã chế tạo thành công " + doThienSu.template.name + " với " + bonus2 + " dòng chỉ số thưởng, mọi người đều kinh ngạc.";
                        ServerNotify.gI().notify(message);

                        //ChatGlobalService.gI().autoChatGlobal(player, "[Hệ thống] " + message);

                    } else {
                        String message = "Whis: " + player.name + " đã chế tạo thành công " + doThienSu.template.name + ", mọi người đều trầm trồ.";
                        ServerNotify.gI().notify(message);

                        //ChatGlobalService.gI().autoChatGlobal(player, "[Hệ thống] " + message);
                    }
                    InventoryService.gI().subQuantityItemsBag(player, manhThienSu, 999);
                    CombineService.gI().sendEffectCombineItem(player, (byte) 7, (short) doThienSu.template.iconID, (short) -1);
                    InventoryService.gI().addItemBag(player, doThienSu);
                } else {
                    InventoryService.gI().subQuantityItemsBag(player, manhThienSu, 99);
                    CombineService.gI().sendEffectCombineItem(player, (byte) 8, (short) -1, (short) -1);
                }
                InventoryService.gI().subQuantityItemsBag(player, congThuc, 1);
                if (daMayMan != null) {
                    InventoryService.gI().subQuantityItemsBag(player, daMayMan, 1);
                }
                if (daNangCap != null) {
                    InventoryService.gI().subQuantityItemsBag(player, daNangCap, 1);
                }
                player.inventory.gold -= 200_000_000;
                InventoryService.gI().sendItemBag(player);
                Service.gI().sendMoney(player);
                CombineService.gI().reOpenItemCombine(player);
            }
        }
    }

}
