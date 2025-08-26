package npc.npc_manifest;

import consts.ConstNpc;
import npc.Npc;
import player.Player;
import services.Service;
import services.func.ChangeMapService;


public class BillBiNgo extends Npc {

    public BillBiNgo(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                    "Ngươi muốn đi tới Địa Ngục hay Nghĩa Địa? Ta có thể đưa ngươi đến đó!",
                    "Đi tới Địa Ngục", "Đi tới Nghĩa Địa", "Hủy bỏ");
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (player.iDMark.isBaseMenu()) {
                switch (select) {
                    case 0 -> { // Lựa chọn đi tới Địa Ngục
                        if (player.nPoint.power <= 10_000_000_000L) {
                            Service.gI().sendThongBao(player, "Yêu cầu sức mạnh đạt 10 tỉ");
                            return;
                        }
                        ChangeMapService.gI().changeMapNonSpaceship(player, 174, 149, 408); // Map Địa Ngục
                    }
                    case 1 -> { // Lựa chọn đi tới Nghĩa Địa
                        if (player.nPoint.power <= 40_000_000_000L) {
                            Service.gI().sendThongBao(player, "Yêu cầu sức mạnh đạt 40 tỉ");
                            return;
                        }
                        // Map Nghĩa Địa (ID 181, tọa độ X 1567, Y 264)
                        ChangeMapService.gI().changeMapNonSpaceship(player, 181, 1567, 264);
                    }
                    default -> {
                        // Xử lý khi không có lựa chọn hợp lệ
                    }
                }
            }
        }
    }
}
