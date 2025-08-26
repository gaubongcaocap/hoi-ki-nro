package npc.npc_manifest;

/**
 *
 * @author NTD
 */
import consts.ConstNpc;
import consts.ConstTranhNgocNamek;
import models.DragonNamecWar.TranhNgocService;
import npc.Npc;
import player.Player;
import services.Service;
import utils.Util;

public class QuocVuongTranhNgoc extends Npc {

    public QuocVuongTranhNgoc(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        switch (mapId) {
            case ConstTranhNgocNamek.MAP_ID:
                if (player.iDMark.getTranhNgoc() == 2) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Đi đi cu!! Chém giờ", "Đóng");
                    return;
                }
                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                        "Hãy mang ngọc rồng về cho ta", "Đưa ngọc", "Đóng");
                break;
            default:
                break;
        }

    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {

            if (player.iDMark.isBaseMenu()) {
                switch (this.mapId) {
                    case ConstTranhNgocNamek.MAP_ID -> {
                        switch (select) {
                            case 0:
                                if (player.iDMark.getTranhNgoc() == 1 && player.isHoldNamecBallTranhDoat) {
                                    if (!Util.canDoWithTime(player.lastTimePickItem, 20000)) {
                                        Service.gI().sendThongBao(player, "Vui lòng đợi " + ((player.lastTimePickItem + 20000 - System.currentTimeMillis()) / 1000) + " giây để có thể trả");
                                        return;
                                    }
                                    TranhNgocService.getInstance().dropBall(player, (byte) 1);
                                    player.zone.pointBlue++;
                                    if (player.zone.pointBlue > ConstTranhNgocNamek.MAX_POINT) {
                                        player.zone.pointBlue = ConstTranhNgocNamek.MAX_POINT;
                                    }
                                    TranhNgocService.getInstance().sendUpdatePoint(player);
                                }
                                break;
                            case 1:
                                break;
                        }

                    }
                }

            }
        }
    }
}
