/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models.Combine.manifest;

import player.Player;

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
public class GiaHanVatPham {

    public static void showInfoCombine(Player player) {
        if (player.combine.itemsCombine.size() == 2) {
            Item thegh = null;
            Item itemGiahan = null;
            for (Item item_ : player.combine.itemsCombine) {
                if (item_.template.id == 1723) {
                    thegh = item_;
                } else if (item_.isTrangBiHSD()) {
                    itemGiahan = item_;
                }
            }
            if (thegh == null) {
                Service.gI().sendThongBaoOK(player, "Cần 1 trang bị có hạn sử dụng và 1 Đá hoàng Kim");
                return;
            }
            if (itemGiahan == null) {
                Service.gI().sendThongBaoOK(player, "Cần 1 trang bị có hạn sử dụng và 1 Đá hoàng Kim");
                return;
            }
            for (ItemOption itopt : itemGiahan.itemOptions) {
                if (itopt.optionTemplate.id != 93) {
                    Service.gI().sendThongBaoOK(player, "Trang bị này không phải trang bị có Hạn Sử Dụng");
                    return;

                }
            }
            String npcSay = "Trang bị được gia hạn \"" + itemGiahan.template.name + "\"";
            npcSay += itemGiahan.template.name + "\n|2|";
            for (Item.ItemOption io : itemGiahan.itemOptions) {
                npcSay += io.getOptionString() + "\n";
            }
            npcSay += "\n|0|Sau khi gia hạn + ~ 3 - 7 ngày\n";

            npcSay += "|0|Tỉ lệ thành công: 100%" + "\n";
            if (player.inventory.ruby > 1000) {
                npcSay += "|2|Cần 1000 hồng ngọc";
                CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay,
                        "Gia hạn", "Từ chối");

            } else if (player.inventory.ruby < 1000) {
                int SoVangThieu2 = (int) (1000 - player.inventory.ruby);
                CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Bạn còn thiếu " + SoVangThieu2 + " Hong Ngoc");
            } else {
                CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Cần 1 trang bị có hạn sử dụng và 1 Đá hoàng Kim");
            }
        } else {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hành trang cần ít nhất 1 chỗ trống");
        }
    }

    public static void startCombine(Player player) {
        if (player.combine.itemsCombine.size() != 2) {
            Service.gI().sendThongBao(player, "Thiếu nguyên liệu");
            return;
        }
        if (player.combine.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.isTrangBiHSD()).count() != 1) {
            Service.gI().sendThongBao(player, "Thiếu trang bị HSD");
            return;
        }
        if (player.combine.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.template.id == 1723).count() != 1) {
            Service.gI().sendThongBao(player, "Thiếu Đá hoàng Kim");
            return;
        }
        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
            Item thegh = player.combine.itemsCombine.stream().filter(item -> item.template.id == 1723).findFirst().get();
            Item tbiHSD = player.combine.itemsCombine.stream().filter(Item::isTrangBiHSD).findFirst().get();
//              Item thegh = player.combine.itemsCombine.get(0);
//                Item tbiHSD = player.combine.itemsCombine.get(1);
            if (thegh == null) {
                Service.gI().sendThongBao(player, "Thiếu Đá hoàng Kim");
                return;
            }
            if (tbiHSD == null) {
                Service.gI().sendThongBao(player, "Thiếu trang bị HSD");
                return;
            }
            if (tbiHSD != null) {
                for (ItemOption itopt : tbiHSD.itemOptions) {
                    if (itopt.optionTemplate.id == 93) {
                        if (itopt.param < 0 || itopt == null) {
                            Service.gI().sendThongBao(player, "Không Phải Trang Bị Có HSD");
                            return;
                        }
                    }
                }
            }
            if (Util.isTrue(50, 100)) {
                for (ItemOption itopt : tbiHSD.itemOptions) {
                    if (itopt.optionTemplate.id == 93) {
                        itopt.param += Util.nextInt(3, 7);
                        break;
                    }
                }
            } else {
                for (ItemOption itopt : tbiHSD.itemOptions) {
                    if (itopt.optionTemplate.id == 93) {
                        itopt.param += 1;
                        break;
                    }
                }
            }
            CombineService.gI().sendEffectSuccessCombine(player);
            InventoryService.gI().subQuantityItemsBag(player, thegh, 1);
            InventoryService.gI().sendItemBag(player);
            Service.gI().sendMoney(player);
            CombineService.gI().reOpenItemCombine(player);
        } else {
            Service.gI().sendThongBao(player, "Bạn phải có ít nhất 1 ô trống hành trang");
        }
    }

}
