/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models.Combine.manifest;

import java.util.Arrays;
import java.util.List;
import java.util.Arrays;
import java.util.List;

import consts.ConstNpc;
import item.Item;
import item.Item.ItemOption;
import models.Combine.CombineService;
import player.Player;
import services.InventoryService;
import services.ItemService;
import services.Service;
import utils.Util;

/**
 *
 * @author Administrator
 */
public class NangGiapLuyenTap {
    private static float getTileNangCapDo(int level) {
        switch (level) {
            case 0:
                return 50f;
            case 1:
                return 35f;
            case 2:
                return 25.5f;
            case 3:
                return 17.5f;
            case 4:
                return 10f;
            case 5:
                return 8f;
            case 6:
                return 3f;
            case 7: // 7 sao
                return 1f;
//            case 8:
//                return 0.3f;
//            case 9:
//                return 0.2f;
//            case 10:
//                return 0.1f;
        }
        return 0;
    }
 private static int getCountDaQuy(int level) {
        switch (level) {
            case 0:
                return 10;
            case 1:
                return 15;
            case 2:
                return 20;
            case 3:
                return 25;
            case 4:
                return 35;
            case 5:
                return 40;
            case 6:
                return 50;
            case 7:
                return 60;
//            case 8:
//                return 70;
//            case 9:
//                return 99;
//            case 10:
//                return 150;
        }
        return 0;
    }

    private static int getCountDaBaoVe(int level) {
        return level + 1;
    }

    private static int getGoldNangCapDo(int level) {
        switch (level) {
            case 0:
                return 100000;
            case 1:
                return 300000;
            case 2:
                return 700000;
            case 3:
                return 1500000;
            case 4:
                return 7000000;
            case 5:
                return 23000000;
            case 6:
                return 100000000;
            case 7:
                return 250000000;
//            case 8:
//                return 300000000;
//            case 9:
//                return 450000000;
        }
        return 0;
    }
    private static boolean isCoupleItemNangCapCheck3(Item trangBi, Item daNangCap) {
        if (trangBi != null && daNangCap != null) {
            if ((trangBi.template.id >= 529 && trangBi.template.id <= 531 || trangBi.template.id >= 534 && trangBi.template.id <= 536 || trangBi.template.id == 1792) && daNangCap.template.id == 1710) {
                return true;
            }
        } else {
            return false;
        }
        return false;
    }

    public static void showInfoCombine(Player player) {
        if (player.combine.itemsCombine.size() >= 2 && player.combine.itemsCombine.size() < 4) {
            if (player.combine.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.template.type == 32).count() < 1) {
                CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Thiếu giáp luyện tập", "Đóng");
                return;
            }
            if (player.combine.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.template.type == 14).count() < 1) {
                CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Thiếu đá hổ phách", "Đóng");
                return;
            }
            if (player.combine.itemsCombine.size() == 3 && player.combine.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.template.id == 987).count() < 1) {
                CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Thiếu đá bảo vệ", "Đóng");
                return;
            }
            Item itemDo = null;
            Item itemDNC = null;
            Item itemDBV = null;
            for (int j = 0; j < player.combine.itemsCombine.size(); j++) {
                if (player.combine.itemsCombine.get(j).isNotNullItem()) {
                    if (player.combine.itemsCombine.size() == 3 && player.combine.itemsCombine.get(j).template.id == 987) {
                        itemDBV = player.combine.itemsCombine.get(j);
                        continue;
                    }
                    if (player.combine.itemsCombine.get(j).template.type == 32) {
                        itemDo = player.combine.itemsCombine.get(j);
                    } else {
                        itemDNC = player.combine.itemsCombine.get(j);
                    }
                }
            }
            if (itemDo != null) {
                boolean check1 = false;
                boolean check2 = false;
                if (InventoryService.gI().haveOption(itemDo, 102)) {
                    check1 = true;
                }
                if (InventoryService.gI().haveOption(itemDo, 107)) {
                    check2 = true;
                }
                if (!check1) {
                    CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                            "Cần giáp luyện tập đã pha lê hóa 5 sao trở lên");
                    return;
                }
                if (!check2) {
                    CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                            "Cần giáp luyện tập đã ép 5 sao pha lê trở lên");
                    return;
                }
                for (ItemOption iop : itemDo.itemOptions) {
                    if (iop != null) {
                        if (iop.optionTemplate.id == 102) {
                            if (iop.param < 5) {
                                CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                        "Cần giáp luyện tập đã pha lê hóa 5 sao trở lên");
                                return;
                            }
                        }
                        if (iop.optionTemplate.id == 107) {
                            if (iop.param < 5) {
                                CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                        "Cần giáp luyện tập đã ép 5 sao pha lê trở lên");
                                return;
                            }
                        }
                    }
                }
            }
            if (isCoupleItemNangCapCheck3(itemDo, itemDNC)) {
                int level = 0;
                if (itemDo == null || itemDNC == null) {
                    Service.gI().sendThongBao(player, "Có lỗi xảy ra với đồ của bạn, vui lòng báo admin");
                    return;
                }
                for (Item.ItemOption io : itemDo.itemOptions) {
                    if (io.optionTemplate.id == 72) {
                        level = io.param;
                        break;
                    }
                }
                if (level < CombineService.MAX_LEVEL_ITEM) {
                    player.combine.goldCombine = getGoldNangCapDo(level);
                    player.combine.ratioCombine = (float) getTileNangCapDo(level);
                    player.combine.countDaQuy = getCountDaQuy(level);
                    player.combine.countDaBaoVe = (short) getCountDaBaoVe(level);
                    String npcSay = "|2|Hiện tại " + itemDo.template.name + " (+" + level + ")\n|0|";
                    for (Item.ItemOption io : itemDo.itemOptions) {
                        if (io.optionTemplate.id != 72) {
                            npcSay += io.getOptionString() + "\n";
                        }
                    }
                    String option = null;
                    int param = 0;
                    int paramsub = 0;
                    for (Item.ItemOption io : itemDo.itemOptions) {
                        if (io.optionTemplate.id == 2
                                || io.optionTemplate.id == 8
                                || io.optionTemplate.id == 19
                                || io.optionTemplate.id == 27
                                || io.optionTemplate.id == 28
                                || io.optionTemplate.id == 16
                                || (io.optionTemplate.id >= 94
                                && io.optionTemplate.id <= 101)
                                || io.optionTemplate.id == 108
                                || io.optionTemplate.id == 109
                                || io.optionTemplate.id == 114
                                || io.optionTemplate.id == 117
                                || io.optionTemplate.id == 153
                                || io.optionTemplate.id == 156
                                || io.optionTemplate.id == 3) {
                            option = io.optionTemplate.name;
                            param = io.param + 1;
                            paramsub = io.param - 1;
                            break;
                        }
                    }

                    npcSay += "|2|Sau khi nâng giáp luyện tập (+" + (level + 1) + ")\n|7|"
                            + (option != null ? option.replaceAll("#", String.valueOf(param)) : "Ngẫu nhiên nhận 1 chỉ số đặc biệt" + "\n")
                            + "\n|7|Tỉ lệ thành công: " + player.combine.ratioCombine + "%\n"
                            + (player.combine.countDaQuy > itemDNC.quantity ? "|7|" : "|1|")
                            + "Cần " + player.combine.countDaQuy + " " + itemDNC.template.name + "\n"
                            + (player.combine.goldCombine > player.inventory.gold ? "|7|" : "|1|")
                            + "Cần " + Util.numberToMoney(player.combine.goldCombine) + " vàng";

                    String daNPC = player.combine.itemsCombine.size() == 3 && itemDBV != null ? String.format("\nCần tốn %s đá bảo vệ", player.combine.countDaBaoVe) : "";
                    if ((level == 2 || level == 4 || level == 6 || level == 8) && !(player.combine.itemsCombine.size() == 3 && itemDBV != null)) {
                        npcSay += "\nNếu thất bại sẽ rớt xuống (+" + (level - 1) + ")\n|7|"
                                + (option != null ? option.replaceAll("#", String.valueOf(paramsub)) : "Chưa có chỉ số đặc biệt nào" + "\n");
                    }
                    if (player.combine.countDaQuy > itemDNC.quantity) {
                        CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                npcSay, "Còn thiếu\n" + (player.combine.countDaQuy - itemDNC.quantity) + " " + itemDNC.template.name);
                    } else if (player.combine.goldCombine > player.inventory.gold) {
                        CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                npcSay, "Còn thiếu\n" + Util.numberToMoney((player.combine.goldCombine - player.inventory.gold)) + " vàng");
                    } else if (player.combine.itemsCombine.size() == 3 && itemDBV != null && itemDBV.quantity < player.combine.countDaBaoVe) {
                        CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                npcSay, "Còn thiếu\n" + (player.combine.countDaBaoVe - itemDBV.quantity) + " đá bảo vệ");
                    } else {
                        CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE,
                                npcSay, "Nâng cấp\n" + Util.numberToMoney(player.combine.goldCombine) + " vàng" + daNPC, "Từ chối");
                    }
                } else {
                    CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Giáp luyện tập của ngươi đã đạt cấp tối đa", "Đóng");
                }
            } else {
                CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hãy chọn 1 giáp luyện tập và đá hổ phách", "Đóng");
            }
        } else {
            if (player.combine.itemsCombine.size() > 3) {
                CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Cất đi con ta không thèm", "Đóng");
                return;
            }
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hãy chọn 1 giáp luyện tập và đá hổ phách", "Đóng");
        }
    }

    public static void startCombine(Player player) {
        int countDaNangCap = player.combine.countDaQuy;
        long gold = player.combine.goldCombine;
        short countDaBaoVe = player.combine.countDaBaoVe;
        if (player.combine.itemsCombine.size() >= 2 && player.combine.itemsCombine.size() < 4) {
            if (player.combine.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.template.type == 32).count() != 1) {
                return;
            }
            if (player.combine.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.template.type == 14).count() != 1) {
                return;
            }
            if (player.combine.itemsCombine.size() == 3 && player.combine.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.template.id == 987).count() != 1) {
                return;//admin
            }
            Item itemDo = null;
            Item itemDNC = null;
            Item itemDBV = null;
            for (int j = 0; j < player.combine.itemsCombine.size(); j++) {
                if (player.combine.itemsCombine.get(j).isNotNullItem()) {
                    if (player.combine.itemsCombine.size() == 3 && player.combine.itemsCombine.get(j).template.id == 987) {
                        itemDBV = player.combine.itemsCombine.get(j);
                        continue;
                    }
                    if (player.combine.itemsCombine.get(j).template.type == 32) {
                        itemDo = player.combine.itemsCombine.get(j);
                    } else {
                        itemDNC = player.combine.itemsCombine.get(j);
                    }
                }
            }
            if (isCoupleItemNangCapCheck3(itemDo, itemDNC)) {

                if (player.inventory.gold < gold) {
                    Service.gI().sendThongBao(player, "Không đủ vàng để thực hiện");
                    return;
                }
                if (player.combine.itemsCombine.size() == 3) {
                    if (itemDBV == null) {
                        return;
                    }
                    if (itemDBV.quantity < countDaBaoVe) {
                        return;
                    }
                }

                int level = 0;
                Item.ItemOption optionLevel = null;
                if (itemDo == null) {
                    Service.gI().sendThongBao(player, "Có lỗi xảy ra vui lòng báo admin");
                    return;
                }
                for (Item.ItemOption io : itemDo.itemOptions) {
                    if (io.optionTemplate.id == 72) {
                        level = io.param;
                        optionLevel = io;
                        break;
                    }
                }
// Kiểm tra xem item có option nào từ danh sách không
                Item.ItemOption option = null;
                for (Item.ItemOption io : itemDo.itemOptions) {
                    if (io.optionTemplate.id == 2
                            || io.optionTemplate.id == 8
                            || io.optionTemplate.id == 19
                            || io.optionTemplate.id == 27
                            || io.optionTemplate.id == 28
                            || io.optionTemplate.id == 16
                            || (io.optionTemplate.id >= 94
                            && io.optionTemplate.id <= 101)
                            || io.optionTemplate.id == 108
                            || io.optionTemplate.id == 109
                            || io.optionTemplate.id == 114
                            || io.optionTemplate.id == 117
                            || io.optionTemplate.id == 153
                            || io.optionTemplate.id == 156
                            || io.optionTemplate.id == 3) {
                        option = io; // Option đã tồn tại
                        break;
                    }
                }
                if (level < CombineService.MAX_LEVEL_ITEM) {
                    player.inventory.gold -= gold;
                    if (Util.isTrue(player.combine.ratioCombine, 100)) {
                        if (option != null) {
                            option.param += 1;
                        } else {
                            List<Integer> optionIds = Arrays.asList(2, 8, 19, 27, 28, 16, 94, 95, 96, 97, 98, 99, 100, 101, 108, 109, 114, 117, 153, 156, 3);
                            int randomOptionId = optionIds.get(Util.nextInt(0, optionIds.size() - 1));
                            itemDo.itemOptions.add(new ItemOption(randomOptionId, 1));
                        }
                        if (optionLevel != null) {
                            optionLevel.param++;
                        } else {
                            itemDo.itemOptions.add(new Item.ItemOption(72, 1));
                        }

                        CombineService.gI().sendEffectSuccessCombine(player);
                    } else {
                        if ((level == 2 || level == 4 || level == 6 || level == 8) && (player.combine.itemsCombine.size() != 3)) {
                            if (option != null) {
                                option.param -= 1;
                            }
                            if (optionLevel != null) {
                                optionLevel.param--;
                            }
                        }
                        CombineService.gI().sendEffectFailCombine(player);
                    }
                    if (player.combine.itemsCombine.size() == 3) {
                        InventoryService.gI().subQuantityItemsBag(player, itemDBV, countDaBaoVe);
                    }
                    InventoryService.gI().subQuantityItemsBag(player, itemDNC, countDaNangCap);
                    InventoryService.gI().sendItemBag(player);
                    Service.gI().sendMoney(player);
                    CombineService.gI().reOpenItemCombine(player);
                }
            }
        }
    }

}
