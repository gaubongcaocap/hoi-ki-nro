/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models.Combine.manifest;

import consts.ConstNpc;
import item.Item;
import item.Item.ItemOption;
import java.util.Arrays;
import java.util.List;
import models.Combine.Combine;
import models.Combine.CombineService;
import player.Player;
import services.InventoryService;
import services.Service;
import utils.Util;

/**
 *
 * @author Administrator
 */
public class PhapSuHoa {

    public static void showInfoCombine(Player player) {
        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                    if (player.combine.itemsCombine.size() == 2) {
                        Item daHacHoa = null;
                        Item itemHacHoa = null;
                        for (Item item_ : player.combine.itemsCombine) {
                            if (item_.template.id == 1707) {
                                daHacHoa = item_;
                            } else if (item_.isTrangBiPSH()) {
                                itemHacHoa = item_;
                            }
                        }
                        if (daHacHoa == null) {
                            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Bạn còn thiếu đá pháp sư", "Đóng");
                            return;
                        }
                        if (itemHacHoa == null) {
                            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Bạn còn thiếu trang bị", "Đóng");
                            return;
                        }
                        if (itemHacHoa != null) {
                            for (ItemOption itopt : itemHacHoa.itemOptions) {
                                if (itopt.optionTemplate.id == 57) {
                                    if (itopt.param >= CombineService.MAX_LEVEL_ITEM) {
                                        Service.gI().sendThongBao(player, "Trang bị đã đạt tới giới hạn pháp sư");
                                        return;
                                    }
                                }
                            }
                        }
                        String npcSay = "|2|Hiện tại " + itemHacHoa.template.name + "\n|0|";
                        for (Item.ItemOption io : itemHacHoa.itemOptions) {
                            if (io.optionTemplate.id != 57) {
                                npcSay += io.getOptionString() + "\n";
                            }
                        }
                        player.combine.ratioCombine = 30;
                        npcSay += "|2|Sau khi nâng cấp sẽ cộng 1 chỉ số pháp sư ngẫu nhiên \n|7|"
                                + "\n|7|Tỉ lệ thành công: " + player.combine.ratioCombine + "%\n"
                                + "Cần " + Util.numberToMoney(2000000000) + " vàng";

                        CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE,
                                npcSay, "Nâng cấp\n" + Util.numberToMoney(2000000000) + " vàng", "Từ chối");
                    } else {
                        CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Cần có trang bị có thể pháp sư và đá pháp sư", "Đóng");
                    }
                } else {
                    CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hành trang cần ít nhất 1 chỗ trống", "Đóng");
                }

    }

    public static void startCombine(Player player) {
         if (player.combine.itemsCombine.size() != 2) {
            Service.gI().sendThongBao(player, "Thiếu nguyên liệu");
            return;
        }
        if (player.combine.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.isTrangBiPSH()).count() != 1) {
            Service.gI().sendThongBao(player, "Thiếu trang bị pháp sư");
            return;
        }
        if (player.combine.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.template.id == 1707).count() != 1) {
            Service.gI().sendThongBao(player, "Thiếu đá pháp sư");
            return;
        }
        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
            if (player.inventory.gold < 2000000000) {
                Service.gI().sendThongBao(player, "Con cần 2 tỉ vàng để đổi...");
                return;
            }
            player.inventory.gold -= 2000000000;
            Item daHacHoa = player.combine.itemsCombine.stream().filter(item -> item.template.id == 1707).findFirst().get();
            Item trangBiHacHoa = player.combine.itemsCombine.stream().filter(Item::isTrangBiPSH).findFirst().get();
            if (daHacHoa == null) {
                Service.gI().sendThongBao(player, "Thiếu đá pháp sư");
                return;
            }
            if (trangBiHacHoa == null) {
                Service.gI().sendThongBao(player, "Thiếu trang bị pháp sư");
                return;
            }

//            if (trangBiHacHoa != null) {
            for (ItemOption itopt : trangBiHacHoa.itemOptions) {
                if (itopt.optionTemplate.id == 57) {
                    if (itopt.param >= CombineService.MAX_LEVEL_ITEM) {

                        Service.gI().sendThongBao(player, "Trang bị đã đạt tới giới hạn pháp sư");
                        return;
                    }
                }
            }
//            }

            if (Util.isTrue(player.combine.ratioCombine, 100)) {
                CombineService.gI().sendEffectSuccessCombine(player);
                List<Integer> idOptionHacHoa = Arrays.asList(0, 6, 7, 47);
                int randomOption = idOptionHacHoa.get(Util.nextInt(0, 3));
                if (!trangBiHacHoa.haveOption(57)) {
                    trangBiHacHoa.itemOptions.add(new ItemOption(57, 1));
                } else {
                    for (ItemOption itopt : trangBiHacHoa.itemOptions) {
                        if (itopt.optionTemplate.id == 57) {
                            itopt.param += 1;
                            break;
                        }
                    }
                }
                if (!trangBiHacHoa.haveOption(randomOption)) {
                    trangBiHacHoa.itemOptions.add(new ItemOption(randomOption, 500));
                } else {
                    for (ItemOption itopt : trangBiHacHoa.itemOptions) {
                        if (itopt.optionTemplate.id == randomOption) {
                            itopt.param += Util.nextInt(100, 500);
                            break;
                        }
                    }
                }
                player.combine.ratioCombine = 0;
                Service.gI().sendThongBao(player, "Bạn đã nâng cấp thành công");
            } else {

                CombineService.gI().sendEffectFailCombine(player);

            }
            InventoryService.gI().subQuantityItemsBag(player, daHacHoa, 1);
            InventoryService.gI().sendItemBag(player);
            Service.gI().sendMoney(player);
            player.combine.itemsCombine.clear();
            CombineService.gI().reOpenItemCombine(player);
        } else {
            Service.gI().sendThongBao(player, "Bạn phải có ít nhất 1 ô trống hành trang");
        }
    }
    
}
