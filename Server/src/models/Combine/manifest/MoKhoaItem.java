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
import models.Combine.CombineService;
import player.Player;
import services.InventoryService;
import services.Service;
import utils.Util;

/**
 *
 * @author Administrator
 */
public class MoKhoaItem {

    public static void showInfoCombine(Player player) {
       if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                    if (player.combine.itemsCombine.size() == 2) {
                        Item dahoangkim = null;
                        Item itemkhoagd = null;
                        for (Item item_ : player.combine.itemsCombine) {
                            System.out.println("Item type: " + item_.template.type);
                            if (item_.template.id == 1723) {
                                dahoangkim = item_;
                            } else if (item_.isTrangBiKhoaGd()) {
                                itemkhoagd = item_;
                            }
                        }

                        if (dahoangkim == null) {
                            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Cần có Đá Hoàng Kim", "Đóng");
                            return;
                        }
                        if (itemkhoagd == null) {
                            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Cần có Item bị khóa giao dịch3", "Đóng");
                            return;
                        }

                        String npcSay = "|2|Hiện tại " + itemkhoagd.template.name + "\n|0|";
                        for (Item.ItemOption io : itemkhoagd.itemOptions) {
                            if (io.optionTemplate.id != 72) {
                                npcSay += io.getOptionString() + "\n";
                            }
                        }
                        npcSay += "|2|Sau khi mở khóa Item của bạn sẽ thành Item gd được \n|7|"
                                + "\n|7|Tỉ lệ thành công: " + 30 + "%\n"
                                + "Cần " + Util.numberToMoney(2000) + " hồng ngọc";

                        CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE,
                                npcSay, "Mở Khóa\n" + Util.numberToMoney(2000) + " hồng ngọc", "Từ chối");
                    } else {
                        CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Cần có Item bị khóa gd và Đá Hoàng Kim", "Đóng");
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
        if (player.combine.itemsCombine.stream().filter(item -> item.isNotNullItem() && (item.isTrangBiKhoaGd())).count() != 1) {
            Service.gI().sendThongBao(player, "Thiếu Item khóa giao dịch1");
            return;
        }
        if (player.combine.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.template.id == 1723).count() != 1) {
            Service.gI().sendThongBao(player, "Cần Đá Hoàng Kim mua tại Bà Hạt Mít");
            return;
        }
        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
            if (player.inventory.ruby < 2000) {
                Service.gI().sendThongBao(player, "Con cần 2k hồng ngọc để tẩy...");
                return;
            }
            player.inventory.ruby -= 2000;
            Item dahoangkim = player.combine.itemsCombine.stream().filter(item -> item.template.id == 1723).findFirst().get();
            Item trangBiKhoagd = player.combine.itemsCombine.stream().filter((item -> item.isTrangBiKhoaGd())).findFirst().get();
            if (dahoangkim == null) {
                Service.gI().sendThongBao(player, "Cần Đá Hoàng Kim mua tại Bà Hạt Mít");
                return;
            }
            if (trangBiKhoagd == null) {
                Service.gI().sendThongBao(player, "Cần Item khóa giao dịch2");
                return;
            }

            if (Util.isTrue(30, 100)) {
                 CombineService.gI().sendEffectSuccessCombine(player);
                List<Integer> idOptionHacHoa = Arrays.asList(30);

                ItemOption option_30 = new ItemOption();

                for (ItemOption itopt : trangBiKhoagd.itemOptions) {
                    if (itopt.optionTemplate.id == 30) {
                        System.out.println("30 _ mở khoa gd");
                        option_30 = itopt;
                    }
                }
                if (option_30 != null) {
                    trangBiKhoagd.itemOptions.add(new Item.ItemOption(73, 0));
                    trangBiKhoagd.itemOptions.remove(option_30);
                }
                player.combine.ratioCombine = 0;
                Service.gI().sendThongBao(player, "Bạn đã mở khóa gd thành công");
                InventoryService.gI().sendItemBag(player);
            } else {
                Service.gI().sendThongBao(player, "Bạn đã mở khóa gd thất bại");
                CombineService.gI().sendEffectFailCombine(player);
            }
            InventoryService.gI().subQuantityItemsBag(player, dahoangkim, 1);
            InventoryService.gI().sendItemBag(player);
            Service.gI().sendMoney(player);
            player.combine.itemsCombine.clear();
            CombineService.gI().reOpenItemCombine(player);
        }
    }
    
}
