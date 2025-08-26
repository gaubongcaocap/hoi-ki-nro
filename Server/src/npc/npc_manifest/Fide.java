package npc.npc_manifest;
//that ra la con poc o dao kame
/**
 *
 * @author NTD
 */
import consts.ConstNpc;
import consts.ConstTranhNgocNamek;
import models.DragonNamecWar.TranhNgocService;
import npc.Npc;
import player.Player;
import services.InventoryService;
import services.NpcService;
import services.Service;
import services.TaskService;
import services.func.ChangeMapService;
import shop.ShopService;
import utils.Util;

public class Fide extends Npc {

    public Fide(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            TaskService.gI().checkDoneTaskTalkNpc(player, this);
            if (mapId == 5) {
                createOtherMenu(player, ConstNpc.BASE_MENU,
                        "Ta bán đồ cho người giàu, người có tiền không đó ???\n"
                        + "Số tiền ngươi đang có: " + player.getSession().cash + " Coin",
                        "Cửa hàng", "Từ chối");
            } else if (mapId == ConstTranhNgocNamek.MAP_ID) {
                if (player.iDMark.getTranhNgoc() == 1) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Phắn đê !Ta không nói chuyện với sinh vật hạ đẳng", "Đóng");
                    return;
                }
                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                        "Hãy mang ngọc rồng về cho ta", "Đưa ngọc", "Đóng");

            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (player.iDMark.getIndexMenu() == ConstNpc.BASE_MENU) {
                if (this.mapId == 5) {
                    switch (select) {
                        case 0 -> {
                            ShopService.gI().opendShop(player, "SHOP_VND", false);
                        }

                    }

                } else if (this.mapId == ConstTranhNgocNamek.MAP_ID) {
                    switch (select) {
                        case 0 -> {
                            if (player.iDMark.getTranhNgoc() == 2 && player.isHoldNamecBallTranhDoat) {
                                if (!Util.canDoWithTime(player.lastTimePickItem, 20000)) {
                                    Service.gI().sendThongBao(player, "Vui lòng đợi " + ((player.lastTimePickItem + 20000 - System.currentTimeMillis()) / 1000) + " giây để có thể trả");
                                    return;
                                }
                                TranhNgocService.getInstance().dropBall(player, (byte) 2);
                                player.zone.pointRed++;
                                if (player.zone.pointRed > ConstTranhNgocNamek.MAX_POINT) {
                                    player.zone.pointRed = ConstTranhNgocNamek.MAX_POINT;
                                }
                                TranhNgocService.getInstance().sendUpdatePoint(player);
                            }
                        }

                    }

                }

            }

        }
    }

}
