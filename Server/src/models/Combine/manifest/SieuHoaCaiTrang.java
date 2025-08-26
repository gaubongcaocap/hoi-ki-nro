/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models.Combine.manifest;

import java.util.Arrays;
import java.util.List;
import player.Player;

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
public class SieuHoaCaiTrang {
 private static int getDiemNangcapSieuHoa(int star) {
        switch (star) {
            case 0:
                return 10;
            case 1:
                return 12;
            case 2:
                return 14;
            case 3:
                return 16;
            case 4:
                return 18;
            case 5:
                return 20;
            case 6:
                return 22;
            case 7:
                return 24;
//            case 8:
//                return 26;
//            case 9:
//                return 28;
//            case 10:
//                return 30;
//            case 11:
//                return 32;
//            case 12:
//                return 34;
//            case 13:
//                return 36;
//            case 14:
//                return 38;
//            case 15:
//                return 40;
//            case 16:
//                return 42;
//            case 17:
//                return 44;
//            case 18:
//                return 46;
//            case 19:
//                return 48;
//            case 20:
//                return 50;
        }
        return 0;
    }

    private static int getDaNangcapSieuHoa(int star) {
        switch (star) {
            case 0:
                return 10;
            case 1:
                return 12;
            case 2:
                return 14;
            case 3:
                return 16;
            case 4:
                return 18;
            case 5:
                return 20;
            case 6:
                return 22;
            case 7:
                return 24;
//            case 8:
//                return 26;
//            case 9:
//                return 28;
//            case 10:
//                return 30;
//            case 11:
//                return 32;
//            case 12:
//                return 34;
//            case 13:
//                return 36;
//            case 14:
//                return 38;
//            case 15:
//                return 40;
//            case 16:
//                return 42;
//            case 17:
//                return 44;
//            case 18:
//                return 46;
//            case 19:
//                return 48;
//            case 20:
//                return 50;
        }
        return 0;
    }

    private static float getTiLeNangcapSieuHoa(int star) {
        switch (star) {
            case 0:
                return 100f;
            case 1:
                return 50f;
            case 2:
                return 30f;
            case 3:
                return 20f;
            case 4:
                return 10f;
            case 5:
                return 5f;
            case 6:
                return 3f;
            case 7:
                return 2f;
//            case 8:
//                return 40f;
//            case 9:
//                return 35f;
//            case 10:
//                return 30f;
//            case 11:
//                return 29f;
//            case 12:
//                return 28f;
//            case 13:
//                return 25f;
//            case 14:
//                return 22f;
//            case 15:
//                return 18f;
//            case 16:
//                return 16f;
//            case 17:
//                return 12f;
//            case 18:
//                return 10f;
//            case 19:
//                return 9f;
//            case 20:
//                return 8f;
        }
        return 0;
    }
    public static void showInfoCombine(Player player) {
     if (player.combine.itemsCombine.size() == 2) {
                    Item caiTrang = null;
                    Item manhVo = null;
                    int star = 0;
                    for (Item item : player.combine.itemsCombine) {
                        if (item.template.type == 5) {
                            caiTrang = item;
                        } else if (item.template.id == 1709) {
                            manhVo = item;
                        }
                    }
                    if (caiTrang != null) {
                        for (Item.ItemOption io2 : caiTrang.itemOptions) {
                            if (io2.optionTemplate.id == 72) {
                                if (io2.param >= CombineService.MAX_LEVEL_ITEM) {
                                    CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                            "Cải Trang đã siêu hóa cấp tối đa", "Đóng");
                                    return;
                                }
                                star = io2.param;
                                break;
                            }
                        }

                    }
                    player.combine.DiemNangcap = getDiemNangcapSieuHoa(star);
                    player.combine.DaNangcap = getDaNangcapSieuHoa(star);
                    player.combine.TileNangcap = getTiLeNangcapSieuHoa(star);
                    if (caiTrang != null && manhVo != null) {
                        String npcSay = caiTrang.template.name + "\n|2|";
                        for (Item.ItemOption io : caiTrang.itemOptions) {
                            npcSay += io.getOptionString() + "\n";
                        }
                        npcSay += "|7|Tỉ lệ thành công: " + getTiLeNangcapSieuHoa(star) + "%" + "\n";
                        if (getDiemNangcapSieuHoa(star) <= player.event.getEventPointNHS()) {
                            npcSay += "|1|Cần " + Util.numberToMoney(getDiemNangcapSieuHoa(star)) + " Điểm Ngũ Hành Sơn";
                            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay,
                                    "Nâng cấp\ncần " + Util.numberToMoney(getDaNangcapSieuHoa(star)) + " Đá Siêu Hóa");
                        } else {
                            npcSay += "Còn thiếu " + Util.numberToMoney(getDiemNangcapSieuHoa(star) - player.event.getEventPointNHS()) + " Điểm Ngũ Hành Sơn";
                            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, npcSay, "Đóng");
                        }
                    } else {
                        CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Cần 1 Cải Trang và Đá Siêu Hóa", "Đóng");
                    }
                } else {
                    CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                            "Cần 1 Cải Trang và Đá Siêu Hóa", "Đóng");
                } }

    public static void startCombine(Player player) {
        if (player.combine.itemsCombine.size() == 2) {
            float tiLe = player.combine.TileNangcap;
            int diem = player.combine.DiemNangcap;
            if (player.event.getEventPointNHS() < diem) {
                Service.gI().sendThongBao(player, "Không đủ Điểm Ngũ Hành Sơn để thực hiện");
                return;
            }
            long gold = player.combine.goldCombine;
            if (player.inventory.gold < gold) {
                Service.gI().sendThongBao(player, "Không đủ vàng để thực hiện");
                return;
            }
            int gem = player.combine.gemCombine;
            if (player.inventory.gem < gem) {
                Service.gI().sendThongBao(player, "Không đủ ngọc để thực hiện");
                return;
            }

            Item caiTrang = null;
            Item manhCaiTrang = null;
            for (Item item : player.combine.itemsCombine) {
                if (item.template.type == 5) {
                    caiTrang = item;
                } else if (item.template.id == 1709) {
                    manhCaiTrang = item;
                }
            }
            int star = 0;
            if (caiTrang != null) {
                for (Item.ItemOption io : caiTrang.itemOptions) {
                    if (io.optionTemplate.id == 72) {
                        star = Math.max(star, io.param);
                    }
                }
            }
            if (star >= 10) {
                Service.gI().sendThongBao(player, "Đã max cấp");
                return;
            }

            if (caiTrang != null && manhCaiTrang != null && manhCaiTrang.quantity > player.combine.DaNangcap && star < CombineService.MAX_LEVEL_ITEM) {
                player.inventory.gold -= gold;
                player.inventory.gem -= gem;
                player.event.subEventPointNHS(diem);

                InventoryService.gI().subQuantityItemsBag(player, manhCaiTrang, player.combine.DaNangcap);
                if (Util.isTrue(tiLe, 100)) {
                    Item newCaiTrang = ItemService.gI().createNewItem((short) (caiTrang.template.id));

                    // Copy các chỉ số từ bông tai cũ sang bông tai mới
                    for (Item.ItemOption io : caiTrang.itemOptions) {
                        newCaiTrang.itemOptions.add(new ItemOption(io.optionTemplate.id, io.param));
                    }

                    // Random chỉ số mới và thêm vào cải trang mới
                    List<Integer> optionIds = Arrays.asList(0, 6, 7, 47);
                    int randomOptionId = optionIds.get(Util.nextInt(0, optionIds.size() - 1));
                    boolean hasOption = false;

                    for (Item.ItemOption io : newCaiTrang.itemOptions) {
                        if (io.optionTemplate.id == randomOptionId) {
                            io.param += Util.nextInt(500, 3000);
                            hasOption = true;
                            break;
                        }

                    }

                    // Tăng option ID 72, 107, 102 lên 1, tối đa là 30
                    boolean foundOption72_107_102 = false;
                    for (Item.ItemOption io : newCaiTrang.itemOptions) {
                        if (io.optionTemplate.id == 72) {
                            io.param = Math.min(io.param + 1, CombineService.MAX_LEVEL_ITEM);
                            foundOption72_107_102 = true;
                        }
                    }
                    if (!foundOption72_107_102) {
                        newCaiTrang.itemOptions.add(new ItemOption(72, 1));
                    }
                    if (!hasOption) {
                        newCaiTrang.itemOptions.add(new ItemOption(randomOptionId, Util.nextInt(500, 3000)));
                    }
                    CombineService.gI().sendEffectSuccessCombine(player);
                    InventoryService.gI().subQuantityItemsBag(player, caiTrang, 1);
                    InventoryService.gI().addItemBag(player, newCaiTrang);

                } else {
                    CombineService.gI().sendEffectFailCombine(player);
                }
                InventoryService.gI().sendItemBag(player);
                Service.gI().sendMoney(player);
                CombineService.gI().reOpenItemCombine(player);
            } else {
                Service.gI().sendThongBao(player, "Thiếu vật phầm để nâng");
            }
        }}
    
}
