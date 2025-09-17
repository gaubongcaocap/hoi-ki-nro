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
            // Chỉnh lại menu: Hướng dẫn, Mua bán Ký gửi (thường), Shop P2P, Đăng bán P2P, Từ chối
            createOtherMenu(player, 0,
                    "Cửa hàng chúng tôi chuyên mua bán hàng hiệu, hàng độc, cảm ơn bạn đã ghé thăm.",
                    "Hướng\ndẫn\nthêm", "Ký gửi\nthường", "Shop\nP2P", "Đăng\nbán P2P", "Từ chối");
        }
    }

    @Override
    public void confirmMenu(Player pl, int select) {
        if (canOpenNpc(pl)) {
            switch (select) {
                case 0 -> {
                    // Hướng dẫn
                    NpcService.gI().createTutorial(pl, tempId, this.avartar, ConstNpc.KY_GUI_P2P);
                }
                case 1 -> {
                    // Ký gửi thường (gọi shop cũ)
                    // ConsignShopService.gI().openShopKyGui(pl);
                }
                case 2 -> {
                    // Mở shop P2P để xem các bài đăng
                    // P2PShopService.gI().openShop(pl);
                }
                case 3 -> {
                    // Đăng bán P2P: phía client sẽ gửi yêu cầu với danh sách item
                    // Tạm thời chỉ gửi thông báo hướng dẫn, logic đăng bán thực hiện ở client
                    // NpcService.gI().createTutorial(pl, tempId, this.avartar, ConstNpc.KY_GUI_P2P);
                }
                default -> {
                    // Các lựa chọn khác: từ chối
                }
            }
        }
    }
}
