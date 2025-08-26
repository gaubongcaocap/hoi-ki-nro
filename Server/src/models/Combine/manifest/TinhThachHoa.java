/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models.Combine.manifest;

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
public class TinhThachHoa {
 private static int getDaNangcapTinhThach(int star) {
        switch (star) {
            case 0:
                return 1;
            case 1:
                return 2;
            case 2:
                return 3;
            case 3:
                return 4;
            case 4:
                return 5;
            case 5:
                return 6;
            case 6:
                return 7;
            case 7:
                return 8;
//            case 8:
//                return 9;
//            case 9:
//                return 10;

        }
        return 0;
    }

    private static float getTiLeNangcapTinhThach(int star) {
        switch (star) {
            case 0:
                return 90f;
            case 1:
                return 50f;
            case 2:
                return 30f;
            case 3:
                return 25f;
            case 4:
                return 15f;
            case 5:
                return 8f;
            case 6:
                return 5f;
            case 7:
                return 2f;
//            case 8:
//                return 10f;
//            case 9:
//                return 5f;
        }
        return 0;
    }
    public static void showInfoCombine(Player player) {
    if (player.combine.itemsCombine.size() == 2) {
                    Item caiTrang = null;
                    Item manhVo = null;
                    int star = 0;
                    for (Item item : player.combine.itemsCombine) {
                        if (item.template.type == 21 ||item.template.type == 23||item.template.type == 24 || item.template.type == 72 || item.template.type == 11) {
                            caiTrang = item;
                        } else if (item.template.type == 93 && item.template.id >= 1711 && item.template.id <= 1717) {
                            manhVo = item;
                        }
                    }
                    if (caiTrang != null) {
                        for (Item.ItemOption io : caiTrang.itemOptions) {
                            if (io.optionTemplate.id == 114||io.optionTemplate.id == 80||io.optionTemplate.id == 81||io.optionTemplate.id == 153||io.optionTemplate.id == 155||io.optionTemplate.id == 156||io.optionTemplate.id == 162) {
                                if (io.param >= CombineService.MAX_LEVEL_ITEM) {
                                    CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                            "Vật phẩm đã nâng tinh thạch cấp tối đa", "Đóng");
                                    return;
                                }
                                star = io.param;
                                break;
                            }
                        }

                    }
                    player.combine.DaNangcap = getDaNangcapTinhThach(star);
                    player.combine.TileNangcap = getTiLeNangcapTinhThach(star);
                    if (caiTrang != null && manhVo != null) {
                        String npcSay = caiTrang.template.name + "\n|2|";
                        for (Item.ItemOption io : caiTrang.itemOptions) {
                            npcSay += io.getOptionString() + "\n";
                        }
                        npcSay += "|7|Tỉ lệ thành công: " + getTiLeNangcapTinhThach(star) + "%" + "\n";
                        npcSay += "|1|Cần " + Util.numberToMoney(getDaNangcapTinhThach(star)) + " Tinh Thạch ";
                        CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay,
                                "Nâng cấp");

                    } else {
                        CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Cần 1 Pet, Linh Thú hoặc Vật phẩm đeo lưng và loại đá tinh thạch", "Đóng");
                    }
                } else {
                    CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                            "Cần 1 Pet, Linh Thú hoặc Vật phẩm đeo lưng và loại đá tinh thạch", "Đóng");
                } }

    public static void startCombine(Player player) {
      float tiLe = player.combine.TileNangcap;

        if (player.combine.itemsCombine.size() == 2) {
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
                if (item.template.type == 21 || item.template.type == 72 || item.template.type == 11) {
                    caiTrang = item;
                } else if (item.template.type == 93 && item.template.id >= 1711 && item.template.id <= 1717) {
                    manhCaiTrang = item;
                }
            }

            int star = 0;
            if (caiTrang != null) {
                for (Item.ItemOption io : caiTrang.itemOptions) {
                    if (io.optionTemplate.id == 114||io.optionTemplate.id == 80||io.optionTemplate.id == 81||io.optionTemplate.id == 153||io.optionTemplate.id == 155||io.optionTemplate.id == 156||io.optionTemplate.id == 162) {
                        star = Math.max(star, io.param);
                    }
                }
            }
            if (star >= 10) {
                Service.gI().sendThongBao(player, "Đã max cấp");
                return;
            }
            if (caiTrang != null && manhCaiTrang != null && manhCaiTrang.quantity > player.combine.DaNangcap) {
                player.inventory.gold -= gold;
                player.inventory.gem -= gem;
                InventoryService.gI().subQuantityItemsBag(player, manhCaiTrang, player.combine.DaNangcap);
                if (Util.isTrue(tiLe, 100)) {
                    Item newCaiTrang = ItemService.gI().createNewItem((short) (caiTrang.template.id));

                    // Copy các chỉ số từ bông tai cũ sang bông tai mới
                    for (Item.ItemOption io : caiTrang.itemOptions) {
                        newCaiTrang.itemOptions.add(new ItemOption(io.optionTemplate.id, io.param));
                    }

                    // Khai báo randomOptionId ở ngoài
                    int randomOptionId = -1;

                    // Xác định giá trị randomOptionId dựa trên manhCaiTrang.template.id
                    switch (manhCaiTrang.template.id) {
                        case 1711:
                            randomOptionId = 114;// tốc độ chạy
                            break;
                        case 1712:
                            randomOptionId = 80;//hp/s
                            break;
                        case 1713:
                            randomOptionId = 81;//ki/s
                            break;
                        case 1714:
                            randomOptionId = 153;// %tỉ lệ phát nổ khi die
                            break;
                        case 1715:
                            randomOptionId = 155;//giảm 50% sd
                            break;
                        case 1716:
                            randomOptionId = 156;// cộng 1% khi dùng chiêu đấm
                            break;
                        case 1717:
                            randomOptionId = 162;// hồi ki cho bản thân xung quanh
                            break;
                        default:
                            break;
                    }

                    // Kiểm tra nếu newCaiTrang đã có loại tinh thạch khác
                    for (Item.ItemOption io : newCaiTrang.itemOptions) {
                        if (io.optionTemplate.id != randomOptionId && (io.optionTemplate.id == 114||io.optionTemplate.id == 80||io.optionTemplate.id == 81||io.optionTemplate.id == 153||io.optionTemplate.id == 155||io.optionTemplate.id == 156||io.optionTemplate.id == 162)) {
                            Service.gI().sendThongBao(player, "Đã nâng một loại tinh thạch khác rồi, không thể nâng tiếp");
                            return;
                        }
                    }

                    // Nếu không có loại tinh thạch khác, tiếp tục thêm hoặc nâng cấp
                    boolean hasOption = false;
                    for (Item.ItemOption io : newCaiTrang.itemOptions) {
                        if (io.optionTemplate.id == randomOptionId) {
                            io.param = Math.min(io.param + 1, CombineService.MAX_LEVEL_ITEM);
                            hasOption = true;
                            break;
                        }
                    }

                    if (!hasOption) {
                        newCaiTrang.itemOptions.add(new ItemOption(randomOptionId, 1));
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
                Service.gI().sendThongBao(player, "Thiếu vật phẩm cần để nâng");
            }
        }}
    
}
