package npc.npc_manifest;

/**
 * @author NTD
 */
import consts.ConstNpc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import npc.Npc;
import player.Player;
import services.func.Input;
import shop.ShopService;

public class Santa extends Npc {

    public Santa(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            List<String> menu = new ArrayList<>(Arrays.asList(
                    "Cửa hàng",
                    "Mở rộng\nHành trang\nRương đồ",
                    // "Nhập mã\nquà tặng",
                    // "Cửa hàng\nHạn sử dụng",
                    // "Tiệm\nHớt tóc",
                    "Danh\nhiệu",
                    "Phụ kiện"
            ));

            if (!player.inventory.itemsDaBan.isEmpty()) {
                menu.add(4, "Mua lại\nvật phẩm\nđã bán [" + player.inventory.itemsDaBan.size() + "/20]");
            }

            String[] menus = menu.toArray(new String[0]);

            createOtherMenu(player, ConstNpc.BASE_MENU,
                    "Xin chào, ta có một số vật phẩm đặc biệt cậu có muốn xem không?", menus);
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (this.mapId == 5 || this.mapId == 13 || this.mapId == 20) {
                if (player.iDMark.isBaseMenu()) {
                    switch (select) {
                        case 0 -> // Cửa hàng
                            ShopService.gI().opendShop(player, "SANTA", false);
                        case 1 -> // Mở rộng hành trang
                            ShopService.gI().opendShop(player, "SANTA_MO_RONG_HANH_TRANG", false);
                        // case 2 -> // Nhập mã quà tặng
                        //     Input.gI().createFormGiftCode(player);
                        // case 3 -> // Cửa hàng hạn sử dụng
                        //     ShopService.gI().opendShop(player, "SANTA_HAN_SU_DUNG", false);
                        case 4 -> // Mua lại vật phẩm đã bán
                        {
                            if (!player.inventory.itemsDaBan.isEmpty()) {
                                ShopService.gI().opendShop(player, "ITEMS_DABAN", false);
                            } else {
                                ShopService.gI().opendShop(player, "SANTA_HEAD", false);
                            }
                        }
                        // case 5 -> // Tiệm hớt tóc
                        // {
                        //     if (!player.inventory.itemsDaBan.isEmpty()) {
                        //         ShopService.gI().opendShop(player, "SANTA_HEAD", false);
                        //     } else {
                        //         ShopService.gI().opendShop(player, "SANTA_DANH_HIEU", false);
                        //     }
                        // }
                        case 2 -> // Danh hiệu
                        {
                            if (!player.inventory.itemsDaBan.isEmpty()) {
                                ShopService.gI().opendShop(player, "SANTA_DANH_HIEU", false);
                            } else {
                                ShopService.gI().opendShop(player, "SANTA_PHUKIEN", false);
                            }
                        }
                        case 3 -> // shop phụ kiện
                            ShopService.gI().opendShop(player, "SANTA_PHUKIEN", false);
                    }
                }
            }
        }
    }
}
