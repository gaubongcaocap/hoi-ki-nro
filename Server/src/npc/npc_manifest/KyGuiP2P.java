package npc.npc_manifest;

import consts.ConstNpc;
/**
 *
 * @author NTD
 */
import models.Consign.ConsignShopService;
import npc.Npc;
import player.Player;
import services.NpcService;

public class KyGuiP2P extends Npc {

    public KyGuiP2P(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
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
                    NpcService.gI().createTutorial(pl, tempId, this.avartar, ConstNpc.KY_GUI_P2P);
                case 1 -> {
                    ConsignShopService.gI().openShopKyGui(pl);
                }
            }
        }
    }
}
