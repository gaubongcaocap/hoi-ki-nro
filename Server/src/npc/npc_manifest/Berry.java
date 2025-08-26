package npc.npc_manifest;

import consts.ConstNpc;
import consts.ConstPlayer;
import consts.ConstTask;
import npc.Npc;
import player.Player;
import services.*;
import shop.ShopService;

public class Berry extends Npc {

    public Berry(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            if (TaskService.gI().getIdTask(player) == ConstTask.TASK_31_5) {
                this.createOtherMenu(player, ConstNpc.BASE_MENU, "Bạn sẽ mang tôi về Bardock thật sao", "OK");
            } else if (mapId == 5) {
                createOtherMenu(player, ConstNpc.BASE_MENU,
                        "Trông ngươi thật là mạnh, ngươi muốn mua gì\n",
                        "Trang Bị","Kĩ năng", "Hướng dẫn", "Từ chối");
            } else {
                super.openBaseMenu(player); // Gọi phương thức của lớp cha nếu không phải TASK_31_5
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            switch (this.mapId) {
                case 5 -> {
                    switch (player.iDMark.getIndexMenu()) {
                        case ConstNpc.BASE_MENU -> {
                            switch (select) {
                                case 0 ->  {
                                    if (InventoryService.gI().fullSetThienSu(player)) {
                                        createOtherMenu(player, 2,
                                                            "Trang bị của ta chỉ có người đủ bản lĩnh mới có thể mặc được\nNgươi chịu nổi không",
                                                            "OK", "Từ chối");
                                    } else {
                                        createOtherMenu(player, 2,
                                                            "Cần trang bị đủ set Thiên Sứ và thu thâp ngọc bội để chế tạo trang bị mới",
                                                            "OK");
                                    }
                                }
                                case 1 ->  {
                                    switch (player.gender) {
                                        case ConstPlayer.XAYDA:
                                            ShopService.gI().opendShop(player, "SKILL_NEW_XD", true);
                                            break;
                                        case ConstPlayer.NAMEC:
                                            ShopService.gI().opendShop(player, "SKILL_NEW_NM", true);
                                            break;
                                        case ConstPlayer.TRAI_DAT:
                                            ShopService.gI().opendShop(player, "SKILL_NEW_TD", true);
                                            break;
                                        default:
                                            break;
                                    }
                                }
                                case 2 ->
                                    NpcService.gI().createTutorial(player, tempId, this.avartar, ConstNpc.HUONG_DAN_BERRY);
                            }
                        }
                        case 2 -> {
                            if (select == 0 && InventoryService.gI().canOpenBerryShop(player)) {
                                ShopService.gI().opendShop(player, "BERRY", true);
                            }
                            break;
                        }
                    }
                }
                default -> {
                    switch (select) {
                        case 0 ->
                            TaskService.gI().checkDoneTask31(player);
                        default -> {
                        }
                    }
                }
            }
        }
    }
}
