/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models.Combine.manifest;

import consts.ConstNpc;
import item.Item;
import item.Item.ItemOption;
import models.Combine.CombineService;
import player.Player;
import services.InventoryService;
import services.Service;
import utils.Util;

/**
 *
 * @author Administrator
 */
public class DapDoAoHoa {

   

    private static float getRatio(int star) {
        return switch (star) {
            case 0 ->
                50;
            case 1 ->
                20;
            case 2 ->
                10;
            case 3 ->
                5;
            case 4 ->
                3;
            case 5 ->
                2;
            case 6 ->
                1;
            case 7 ->
                0.5f;
            case 8 ->
                0.25f;
            default ->
                0;
        };
    }

    private static boolean isCoupleItemNangCapCheck(Item trangBi, Item daNangCap) {
        if (trangBi != null && daNangCap != null) {
            if (trangBi.template.type == 0 && daNangCap.template.id == 1718) {
                return true;
            } else if (trangBi.template.type == 1 && daNangCap.template.id == 1719) {
                return true;
            } else if (trangBi.template.type == 2 && daNangCap.template.id == 1720) {
                return true;
            } else if (trangBi.template.type == 3 && daNangCap.template.id == 1721) {
                return true;
            } else if (trangBi.template.type == 4 && daNangCap.template.id == 1722) {
                return true;

            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private static int getGold(int star) {
        return switch (star) {
            case 0 ->
                50_000_000;
            case 1 ->
                100_000_000;
            case 2 ->
                200_000_000;
            case 3 ->
                400_000_000;
            case 4 ->
                600_000_000;
            case 5 ->
                900_000_000;
            case 6 ->
                1_200_000_000;
            case 7 ->
                1_500_000_000;
            case 8 ->
                1_800_000_000;
            default ->
                0;
        };
    }

    private static int getCountDaQuy(int star) {
        return switch (star) {
            case 0 ->
                1;
            case 1 ->
                2;
            case 2 ->
                3;
            case 3 ->
                4;
            case 4 ->
                5;
            case 5 ->
                6;
            case 6 ->
                7;
            case 7 ->
                20;
            case 8 ->
                30;
            default ->
                0;
        };
    }

    private static int getCountDaBaoVe(int star) {
        return switch (star) {
            case 0 ->
                1;
            case 1 ->
                2;
            case 2 ->
                3;
            case 3 ->
                4;
            case 4 ->
                5;
            case 5 ->
                6;
            case 6 ->
                7;
            case 7 ->
                20;
            case 8 ->
                30;
            default ->
                0;
        };
    }

    public static void showInfoCombine(Player player) {
        if (player.combine.itemsCombine.size() >= 2 && player.combine.itemsCombine.size() < 4) {
            if (player.combine.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.template.type < 5).count() < 1) {
                CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Thiếu đồ ảo hóa (áo, quần, găng, giày, rada)", "Đóng");
                return;
            }
            if (player.combine.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.template.type == 14).count() < 1) {
                CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Thiếu đá quý (pha lê đỏ, pha lê xanh, pha lê hồng, pha lê tím)", "Đóng");
                return;
            }
            if (player.combine.itemsCombine.size() == 3 && player.combine.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.template.id == 987).count() < 1) {
                CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Thiếu đồ nâng cấp", "Đóng");
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
                    if (player.combine.itemsCombine.get(j).template.type < 5) {
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
                            "Cần đồ đã pha lê hóa 5 sao trở lên");
                    return;
                }
                if (!check2) {
                    CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                            "Cần đồ đã ép 5 sao pha lê trở lên");
                    return;
                }
                for (ItemOption iop : itemDo.itemOptions) {
                    if (iop != null) {
                        if (iop.optionTemplate.id == 102) {
                            if (iop.param < 5) {
                                CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                        "Cần đồ đã pha lê hóa 5 sao trở lên");
                                return;
                            }
                        }
                        if (iop.optionTemplate.id == 107) {
                            if (iop.param < 5) {
                                CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                        "Cần đồ đã ép 5 sao pha lê trở lên");
                                return;
                            }
                        }
                    }
                }
            }
            if (isCoupleItemNangCapCheck(itemDo, itemDNC)) {
                int level = 0;
                if (itemDo == null || itemDNC == null) {
                    Service.gI().sendThongBao(player, "Có lỗi xảy ra với đồ của bạn, vui lòng báo admin");
                    return;
                }
                for (Item.ItemOption io : itemDo.itemOptions) {
                    if (io.optionTemplate.id == 58) {
                        level = io.param;
                        break;
                    }
                }
                if (level < CombineService.MAX_LEVEL_ITEM) {
                    player.combine.goldCombine = getGold(level);
                    player.combine.ratioCombine = (float) getRatio(level);
                    player.combine.countDaQuy = getCountDaQuy(level);
                    player.combine.countDaBaoVe = (short) getCountDaBaoVe(level);
                    String npcSay = "|2|Hiện tại " + itemDo.template.name + " (+" + level + ")\n|0|";
                    for (Item.ItemOption io : itemDo.itemOptions) {
                        if (io.optionTemplate.id != 58) {
                            npcSay += io.getOptionString() + "\n";
                        }
                    }
                    String option = null;
                    int param = 0;
                    int paramsub = 0;
                    for (Item.ItemOption io : itemDo.itemOptions) {
                        if (io.optionTemplate.id == 14
                                || io.optionTemplate.id == 5
                                || io.optionTemplate.id == 101
                                || io.optionTemplate.id == 50
                                || io.optionTemplate.id == 77
                                || io.optionTemplate.id == 103) {
                            option = io.optionTemplate.name;
                            param = io.param + Math.max(1, io.param * 10 / 100 - 2);
                            paramsub = io.param - Math.max(1, io.param * 10 / 100);
                            break;
                        }
                    }
                    if (option == null || option.isEmpty()) {
                        CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                npcSay, "Không hỗ trợ item này");

                        return;
                    }
                    npcSay += "|2|Sau khi ảo hóa trang bị (+" + (level + 1) + ")\n|7|"
                            + option.replaceAll("#", String.valueOf(param))
                            + "\n|7|Tỉ lệ thành công: " + player.combine.ratioCombine + "%\n"
                            + (player.combine.countDaQuy > itemDNC.quantity ? "|7|" : "|1|")
                            + "Cần " + player.combine.countDaQuy + " " + itemDNC.template.name
                            + "\n" + (player.combine.goldCombine > player.inventory.gold ? "|7|" : "|1|")
                            + "Cần " + Util.numberToMoney(player.combine.goldCombine) + " vàng";

                    String daNPC = player.combine.itemsCombine.size() == 3 && itemDBV != null ? String.format("\nCần tốn %s đá bảo vệ", player.combine.countDaBaoVe) : "";
                    if ((level == 2 || level == 4 || level == 6 || level == 8) && !(player.combine.itemsCombine.size() == 3 && itemDBV != null)) {
                        npcSay += "\nNếu thất bại sẽ rớt xuống (+" + (level - 1) + ")\n|7|"
                                + option.replaceAll("#", String.valueOf(paramsub));
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
                    CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Trang bị của ngươi đã đạt cấp tối đa", "Đóng");
                }
            } else {
                CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hãy chọn 1 trang bị và 1 loại đá nâng quý phù hợp", "Đóng");
            }
        } else {
            if (player.combine.itemsCombine.size() > 3) {
                CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Cất đi con ta không thèm", "Đóng");
            }
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hãy chọn 1 trang bị và 1 loại đá nâng quý phù hợp", "Đóng");
        }
    }

    public static void startCombine(Player player) {
        int countDaNangCap = player.combine.countDaQuy;
        long gold = player.combine.goldCombine;
        short countDaBaoVe = player.combine.countDaBaoVe;
        if (player.combine.itemsCombine.size() >= 2 && player.combine.itemsCombine.size() < 4) {
            if (player.combine.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.template.type < 5).count() != 1) {
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
                    if (player.combine.itemsCombine.get(j).template.type < 5) {
                        itemDo = player.combine.itemsCombine.get(j);
                    } else {
                        itemDNC = player.combine.itemsCombine.get(j);
                    }
                }
            }
            if (isCoupleItemNangCapCheck(itemDo, itemDNC)) {

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
                    if (io.optionTemplate.id == 58) {
                        level = io.param;
                        optionLevel = io;
                        break;
                    }
                }
                if (level < CombineService.MAX_LEVEL_ITEM) {
                    player.inventory.gold -= gold;
                    Item.ItemOption option = null;
                    for (Item.ItemOption io : itemDo.itemOptions) {
                        if (io.optionTemplate.id == 5
                                || io.optionTemplate.id == 101
                                || io.optionTemplate.id == 50
                                || io.optionTemplate.id == 77
                                || io.optionTemplate.id == 103) {
                            option = io;
                        }
                    }
                    if (Util.isTrue(player.combine.ratioCombine, 100)) {
                        if (option == null) {
                            Service.gI().sendThongBao(player, "Chỉ số không hợp lệ để nâng cấp");
                        } else {
                            option.param += Math.max(1, option.param * 10 / 100 - 2);
                        }
                        if (optionLevel == null) {
                            itemDo.itemOptions.add(new Item.ItemOption(58, 1));
                        } else {
                            optionLevel.param++;
                        }
//   
                        CombineService.gI().sendEffectSuccessCombine(player);
                    } else {
                        if ((level == 2 || level == 4 || level == 6 || level == 8) && (player.combine.itemsCombine.size() != 3)) {
                            if (option == null) {
                                Service.gI().sendThongBao(player, "Chỉ số không hợp lệ để nâng cấp thêm");
                            } else {
                                option.param -= Math.max(1, option.param * 10 / 100);
                            }
                            if (optionLevel == null) {
                                itemDo.itemOptions.add(new Item.ItemOption(58, 1));
                            } else {
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
