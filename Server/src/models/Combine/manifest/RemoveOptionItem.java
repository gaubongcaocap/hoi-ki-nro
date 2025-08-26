/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models.Combine.manifest;

import player.Player;
import consts.ConstNpc;
import item.Item;
import item.Item.ItemOption;
import java.util.Arrays;
import java.util.List;
import models.Combine.Combine;
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
public class RemoveOptionItem {

    public static void showInfoCombine(Player player) {
        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
            if (player.combine.itemsCombine.size() == 2) {
                Item daHacHoa = null;
                Item itemHacHoa = null;
                for (Item item_ : player.combine.itemsCombine) {
                    if (item_.template.id == 1708) {
                        daHacHoa = item_;
                    } else if (item_.isTrangBiHacHoa()) {
                        itemHacHoa = item_;
                    }
                }
                if (daHacHoa == null) {
                    CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Bạn còn thiếu ngọc tẩy", "Đóng");
                    return;
                }
                if (itemHacHoa == null) {
                    CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Cần trang bị có sao pha lê hoặc chỉ số đặc biệt\n[áo , quần , găng, giày, rada, pet, linh thú, vpdl, glt,....]", "Đóng");
                    return;
                }

                String npcSay = "|2|Hiện tại " + itemHacHoa.template.name + "\n|0|";
                for (Item.ItemOption io : itemHacHoa.itemOptions) {
                    if (io.optionTemplate.id != 72) {
                        npcSay += io.getOptionString() + "\n";
                    }
                }

                npcSay += ("|2|Sau khi tẩy sẽ xoá hết các chỉ số phụ và đặc biệt của đồ \n|7|")
                        + "\n|7|Tỉ lệ thành công: " + 100 + "%\n"
                        + "Cần " + Util.numberToMoney(2000) + " hồng ngọc";

                CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE,
                        npcSay, "Tẩy trang bị\n" + Util.numberToMoney(2000) + " hồng ngọc", "Từ chối");
            } else {
                CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Cần có trang bị và ngọc tẩy", "Đóng");
            }
        } else {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hành trang cần ít nhất 1 chỗ trống", "Đóng");
        }
    }

    public static void startCombine(Player player) {
        if (player.combine.itemsCombine.size() != 2) {
            return;
        }
        if (player.combine.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.isTrangBiHacHoa()).count() != 1) {
            return;
        }
        if (player.combine.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.template.id == 1708).count() != 1) {
            return;
        }
        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
            if (player.inventory.ruby < 2000) {
                Service.gI().sendThongBao(player, "Con cần 2k hồng ngọc để tẩy...");
                return;
            }
            player.inventory.ruby -= 2000;
            Item buagiaihachoa = player.combine.itemsCombine.stream().filter(item -> item.template.id == 1708).findFirst().get();
            Item trangBiHacHoa = player.combine.itemsCombine.stream().filter(Item::isTrangBiHacHoa).findFirst().get();
            if (buagiaihachoa == null) {
                Service.gI().sendThongBao(player, "Thiếu trang bị cần tẩy");
                return;
            }
            if (trangBiHacHoa == null) {
                Service.gI().sendThongBao(player, "Thiếu ngọc tẩy");
                return;
            }

            if (Util.isTrue(100, 100)) {
                CombineService.gI().sendEffectSuccessCombine(player);

                ItemOption option_6 = new ItemOption();
                ItemOption option_7 = new ItemOption();
                ItemOption option_0 = new ItemOption();
                ItemOption option_47 = new ItemOption();
                ItemOption option_102 = new ItemOption();

                ItemOption option_50 = new ItemOption();
                ItemOption option_103 = new ItemOption();
                ItemOption option_77 = new ItemOption();

                ItemOption option_80 = new ItemOption();
                ItemOption option_81 = new ItemOption();

                ItemOption option_5 = new ItemOption();
                ItemOption option_2 = new ItemOption();
                ItemOption option_8 = new ItemOption();
                ItemOption option_19 = new ItemOption();
                ItemOption option_27 = new ItemOption();
                ItemOption option_28 = new ItemOption();
                ItemOption option_16 = new ItemOption();
                ItemOption option_94_101 = new ItemOption();
                ItemOption option_108 = new ItemOption();
                ItemOption option_109 = new ItemOption();
                ItemOption option_114 = new ItemOption();
                ItemOption option_117 = new ItemOption();
                ItemOption option_153 = new ItemOption();
                ItemOption option_156 = new ItemOption();
                ItemOption option_3 = new ItemOption();
                ItemOption option_72 = new ItemOption();
                for (ItemOption itopt : trangBiHacHoa.itemOptions) {
                    if (trangBiHacHoa.template.type < 5) {
                        switch (itopt.optionTemplate.id) {
                            case 2:
                                System.out.println("2");
                                option_2 = itopt;
                                break;
                            case 8:
                                System.out.println("8");
                                option_8 = itopt;
                                break;
                            case 19:
                                System.out.println("19");
                                option_19 = itopt;
                                break;
                            case 27:
                                System.out.println("27");
                                option_27 = itopt;
                                break;
                            case 28:
                                System.out.println("28");
                                option_28 = itopt;
                                break;
                            case 16:
                                System.out.println("16");
                                option_16 = itopt;
                                break;
                            case 6:
                                System.out.println("op6");
                                option_6 = itopt;
                                break;
                            case 7:
                                System.out.println("op7");
                                option_7 = itopt;
                                break;
                            case 0:
                                System.out.println("op0");
                                option_0 = itopt;
                                break;
                            case 47:
                                System.out.println("op47");
                                option_47 = itopt;
                                break;
                            case 102:
                                System.out.println("op102");
                                option_102 = itopt;
                                break;
                            case 50:
                                System.out.println("50");
                                option_50 = itopt;
                                break;
                            case 103:
                                System.out.println("103");
                                option_103 = itopt;
                                break;
                            case 77:
                                System.out.println("77");
                                option_77 = itopt;
                                break;
                            case 5:
                                System.out.println("5");
                                option_5 = itopt;
                                break;
                            case 108:
                                System.out.println("108");
                                option_108 = itopt;
                                break;
                            case 109:
                                System.out.println("109");
                                option_109 = itopt;
                                break;
                            case 114:
                                System.out.println("114");
                                option_114 = itopt;
                                break;
                            case 117:
                                System.out.println("117");
                                option_117 = itopt;
                                break;
                            case 153:
                                System.out.println("153");
                                option_153 = itopt;
                                break;
                            case 156:
                                System.out.println("156");
                                option_156 = itopt;
                                break;
                            case 3:
                                System.out.println("3");
                                option_3 = itopt;
                                break;
                            case 72:
                                System.out.println("72");
                                option_72 = itopt;
                                break;
                            case 80,81,94,95,96,97,98,99,100,101:
                                System.out.println("94-101");
                                option_94_101 = itopt;
                                break;

                        }
                    } else if (trangBiHacHoa.template.type == 5 ||trangBiHacHoa.template.type == 32 || trangBiHacHoa.template.type == 21 || trangBiHacHoa.template.type == 23 || trangBiHacHoa.template.type == 24 || trangBiHacHoa.template.type == 72 || trangBiHacHoa.template.type == 11) {
                        switch (itopt.optionTemplate.id) {
                            case 2:
                                System.out.println("2");
                                option_2 = itopt;
                                break;
                            case 8:
                                System.out.println("8");
                                option_8 = itopt;
                                break;
                            case 19:
                                System.out.println("19");
                                option_19 = itopt;
                                break;
                            case 27:
                                System.out.println("27");
                                option_27 = itopt;
                                break;
                            case 28:
                                System.out.println("28");
                                option_28 = itopt;
                                break;
                            case 16:
                                System.out.println("16");
                                option_16 = itopt;
                                break;
                            case 6:
                                System.out.println("op6");
                                option_6 = itopt;
                                break;
                            case 7:
                                System.out.println("op7");
                                option_7 = itopt;
                                break;
                            case 0:
                                System.out.println("op0");
                                option_0 = itopt;
                                break;
                            case 47:
                                System.out.println("op47");
                                option_47 = itopt;
                                break;
                            case 102:
                                System.out.println("op102");
                                option_102 = itopt;
                                break;
                            case 108:
                                System.out.println("108");
                                option_108 = itopt;
                                break;
                            case 109:
                                System.out.println("109");
                                option_109 = itopt;
                                break;
                            case 114:
                                System.out.println("114");
                                option_114 = itopt;
                                break;
//                            case 117:
//                                System.out.println("117");
//                                option_117 = itopt;
//                                break;
                            case 153:
                                System.out.println("153");
                                option_153 = itopt;
                                break;
                            case 156:
                                System.out.println("156");
                                option_156 = itopt;
                                break;
                            case 3:
                                System.out.println("3");
                                option_3 = itopt;
                                break;
                            case 72:
                                System.out.println("72");
                                option_72 = itopt;
                                break;
                            case 80,81,94,95,96,97,98,99,100,101:
                                System.out.println("94-101");
                                option_94_101 = itopt;
                                break;

                        }
                    }
                }
                ItemOption[] options = {
                    option_102, option_6, option_7,
                    option_47, option_0, option_80, option_81,
                    option_50, option_103,
                    option_77, option_5, option_2, option_8, option_19,
                    option_27, option_28, option_16, option_94_101, option_72,
                    option_108,//option_117,
                    option_109, option_114, option_153, option_156,
                    option_3
                };

                for (ItemOption option : options) {
                    if (option != null) {
                        trangBiHacHoa.itemOptions.remove(option);
                    }
                }

                player.combine.ratioCombine = 0;
                Service.gI().sendThongBao(player, "Bạn đã tẩy thành công");
                InventoryService.gI().sendItemBag(player);
            } else {
                CombineService.gI().sendEffectFailCombine(player);
            }
            InventoryService.gI().subQuantityItemsBag(player, buagiaihachoa, 1);
            InventoryService.gI().sendItemBag(player);
            Service.gI().sendMoney(player);
            player.combine.itemsCombine.clear();
            CombineService.gI().reOpenItemCombine(player);
        }
    }

}
