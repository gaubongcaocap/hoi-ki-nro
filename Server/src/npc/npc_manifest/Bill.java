package npc.npc_manifest;

/**
 *
 * @author NTD
 */

import consts.ConstNpc;
import npc.Npc;
import player.Player;
import services.InventoryService;
import services.NpcService;
import services.TaskService;
import services.func.ChangeMapService;
import shop.ShopService;

public class Bill extends Npc {

    public Bill(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            TaskService.gI().checkDoneTaskTalkNpc(player, this);
            if (mapId == 154) {
                createOtherMenu(player, ConstNpc.BASE_MENU,
                        "...",
                        "Về\nthánh địa\nKaio", "Từ chối");
            }
            if (this.mapId == 5) {
                createOtherMenu(player, ConstNpc.BASE_MENU,
                        "|5|zalo: @Huyền Sumo\n"
                                + "\nNâng cấp lục căn "
                        // + "\nLuyện Khí đan: " + player.TamkjllDauLaDaiLuc[0]
                        // + "\nTrúc Cơ Đan: " + player.TamkjllDauLaDaiLuc[1]
                        // + "\nNguyên Anh Đan: " + player.TamkjllDauLaDaiLuc[2]
                        // + "\nHoá Thần Đan: " + player.TamkjllDauLaDaiLuc[3]
                        // + "\nHư Vô Đan: " + player.TamkjllDauLaDaiLuc[4]
                        // + "\nTiên Tôn Đan: "
                        // + player.TamkjllDauLaDaiLuc[5]
                        // + "\nChí Tôn Đan: "
                        // + player.TamkjllDauLaDaiLuc[6]
                        , "Thông Tin\n Lục Căn",
                        "Thông Tin \n Hồn Cốt\nSở Hữu",
                        "Truy Tìm \n Hồn Cốt",
                        "Nâng Hồn\n Cốt",
                        "Thông tin\ncác loại lục căn");
            }

            else {
                createOtherMenu(player, ConstNpc.BASE_MENU,
                        "Mày tìm tao có chuyện gì",
                        "Shop\nHủy Diệt","ĐHVT\n Liên Bang", "Từ chối");
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            switch (this.mapId) {
                case 5 -> {
                    createOtherMenu(player, ConstNpc.BASE_MENU,
                            "|5|zalo: @Huyền Sumo\n"
                                    + "\nNâng cấp lục căn "
                            // + "\nLuyện Khí đan: " + player.TamkjllDauLaDaiLuc[0]
                            // + "\nTrúc Cơ Đan: " + player.TamkjllDauLaDaiLuc[1]
                            // + "\nNguyên Anh Đan: " + player.TamkjllDauLaDaiLuc[2]
                            // + "\nHoá Thần Đan: " + player.TamkjllDauLaDaiLuc[3]
                            // + "\nHư Vô Đan: " + player.TamkjllDauLaDaiLuc[4]
                            // + "\nTiên Tôn Đan: "
                            // + player.TamkjllDauLaDaiLuc[5]
                            // + "\nChí Tôn Đan: "
                            // + player.TamkjllDauLaDaiLuc[6]
                            , "Thông Tin\n Lục Căn",
                            "Thông Tin \n Hồn Cốt\nSở Hữu",
                            "Truy Tìm \n Hồn Cốt",
                            "Nâng Hồn\n Cốt",
                            "Thông tin\ncác loại lục căn");
                }
                case 48 -> {
                    switch (player.iDMark.getIndexMenu()) {
                        case ConstNpc.BASE_MENU -> {
                            switch (select) {
                                case 0 -> {
                                    if (InventoryService.gI().canOpenBillShop(player)) {
                                        createOtherMenu(player, 2,
                                                "Đói bụng quá...ngươi mang cho ta 99 phần đồ ăn\nta sẽ cho một món đồ Hủy Diệt.\nNếu tâm trạng ta vui ngươi có thể nhận trang bị tăng đến 15%",
                                                "OK", "Từ chối");
                                    } else {
                                        createOtherMenu(player, 2,
                                                "Ngươi trang bị đủ bộ 5 món trang bị Thần\nvà mang 99 phần đồ ăn tới đây...\nrồi ta nói chuyện tiếp.",
                                                "OK");
                                    }
                                }
                                case 1 ->
                                    NpcService.gI().createTutorial(player, tempId, this.avartar,
                                            ConstNpc.HUONG_DAN_BILL);
                            }
                        }
                        case 2 -> {
                            if (select == 0 && InventoryService.gI().canOpenBillShop(player)) {
                                ShopService.gI().opendShop(player, "BILL", true);
                                break;
                            }
                        }

                    }
                }
                case 154 -> {
                    if (select == 0) {
                        ChangeMapService.gI().changeMap(player, 50, -1, 318, 336);
                        break;
                    }
                }
            }
        }
    }
}
