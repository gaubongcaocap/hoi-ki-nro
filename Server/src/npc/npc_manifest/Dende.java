package npc.npc_manifest;

/**
 *
 * @author NTD
 */

import consts.ConstNpc;
import consts.ConstPlayer;
import npc.Npc;
import player.Player;
import services.NgocRongNamecService;
import services.NpcService;
import services.TaskService;
import services.func.SummonDragonNamek;
import shop.ShopService;
import utils.TimeUtil;
import utils.Util;

public class Dende extends Npc {

    public Dende(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                if (player.idNRNM != -1) {
                    if (player.zone.map.mapId == 7) {
                        this.createOtherMenu(player, 1, "Ồ, ngọc rồng namếc, bạn thật là may mắn\nnếu tìm đủ 7 viên sẽ được Rồng Thiêng Namếc ban cho điều ước", "Hướng\ndẫn\nGọi Rồng", "Gọi rồng", "Từ chối");
                    }
                } else {
                    if (player.gender != 1) {
                        NpcService.gI().createTutorial(player, tempId, this.avartar, "Xin lỗi anh, em chỉ bán đồ cho dân tộc Namếc");
                    } else if (!player.inventory.itemsDaBan.isEmpty()) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Anh cần trang bị gì cứ đến chỗ em nhé",
                                "Cửa\nhàng",
                                "Mua lại\nvật phẩm\nđã bán [" + player.inventory.itemsDaBan.size() + "/20]");
                    } else {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Anh cần trang bị gì cứ đến chỗ em nhé", "Cửa\nhàng");
                    }
                }
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (player.iDMark.isBaseMenu()) {
                switch (select) {
                    case 0 -> {
                        //Shop
                        if (player.gender == ConstPlayer.NAMEC) {
                            ShopService.gI().opendShop(player, "DENDE", true);
                        } else {
                            this.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Xin lỗi anh, em chỉ bán đồ cho dân tộc Namếc", "Đóng");
                        }
                    }
                    case 1 -> {
                        if (!player.inventory.itemsDaBan.isEmpty()) {
                            ShopService.gI().opendShop(player, "ITEMS_DABAN", true);
                        }
                    }
                }
            } else if (player.iDMark.getIndexMenu() == 1) {
                switch (select) {
                    case 0 ->
                        NpcService.gI().createTutorial(player, tempId, this.avartar, ConstNpc.HUONG_DAN_NRNM);
                    case 1 -> {
                        if (player.zone.map.mapId == 7 && player.idNRNM != -1) {
                            if (player.idNRNM != 353) {
                                NpcService.gI().createTutorial(player, tempId, this.avartar, "Anh phải có viên Ngọc Rồng Namek 1 sao");
                                return;
                            }
                            if (TimeUtil.getCurrHour() > 22 || TimeUtil.getCurrHour() < 8) {
                                NpcService.gI().createTutorial(player, tempId, this.avartar, "Xin lỗi mấy anh, em đang bận buôn bán nên chỉ rảnh gọi Rồng vào khoảng 8h đến 22h");
                                return;
                            }
                            if (!Util.canDoWithTime(player.lastTimePickNRNM, 600000)) {
                                NpcService.gI().createTutorial(player, tempId, this.avartar, "Ngọc bẩn quá, xin chờ em " + TimeUtil.getTimeLeft(player.lastTimePickNRNM, 600) + " nữa để lau bóng ngọc, gọi Rồng mới hiển linh");
                                return;
                            }
                            if (!NgocRongNamecService.gI().canCallDragonNamec(player)) {
                                NpcService.gI().createTutorial(player, tempId, this.avartar, "Hãy gom đủ 7 viên Ngọc Rồng tại đây");
                                return;
                            }
                            NgocRongNamecService.gI().tOpenNrNamec = System.currentTimeMillis() + 86400000;
                            NgocRongNamecService.gI().firstNrNamec = true;
                            NgocRongNamecService.gI().timeNrNamec = 0;
                            NgocRongNamecService.gI().doneDragonNamec();
                            NgocRongNamecService.gI().initNgocRongNamec((byte) 1);
                            NgocRongNamecService.gI().reInitNrNamec((long) 86399000);
                            SummonDragonNamek.gI().summonNamec(player);
                        }
                    }
                }
            }
        }
    }
}
