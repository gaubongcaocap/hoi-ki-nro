package npc.npc_manifest;

/**
 *
 * @author NTD
 */
import models.Consign.ConsignShopService;
import npc.Npc;
import player.Player;
import services.NpcService;

public class KyGui extends Npc {

    public KyGui(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
//            if (true) {
//                services.Service.gI().sendThongBaoOK(player, "Chức năng tạm đóng");
//                return;
//            }
            createOtherMenu(player, 0,
                    "Cửa hàng chúng tôi chuyên mua bán hàng hiệu, hàng độc, cảm ơn bạn đã ghé thăm.",
                    "Hướng\ndẫn\nthêm", "Mua bán\nKý gửi", "Từ chối");
        }
    }

    @Override
    public void confirmMenu(Player pl, int select) {
        if (canOpenNpc(pl)) {
            switch (select) {
                case 0 ->
                    NpcService.gI().createTutorial(pl, tempId, avartar,
                            "Cửa hàng chuyên nhận ký gửi mua bán vật phẩm\bChỉ với 5 hồng ngọc\bGiá trị ký gửi 10k-200Tr vàng hoặc 2-2k ngọc\bMột người bán, vạn người mua, mại dô, mại dô");
                case 1 -> {
                    ConsignShopService.gI().openShopKyGui(pl);
                }
            }
        }
    }
}
