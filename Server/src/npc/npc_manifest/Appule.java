package npc.npc_manifest;

/**
 *
 * @author NTD
 */
import consts.ConstNpc;
import consts.ConstPlayer;
import npc.Npc;
import player.Player;
import services.NpcService;
import services.TaskService;
import shop.ShopService;

public class Appule extends Npc {

    public Appule(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                if (player.gender != 2) {
                    NpcService.gI().createTutorial(player, tempId, this.avartar, "Về hành tinh hạ đẳng của ngươi mà mua đồ cùi nhé. Tại đây ta chỉ bán đồ cho người Xayda thôi");
                } else if (!player.inventory.itemsDaBan.isEmpty()) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Ngươi cần trang bị gì cứ đến chỗ ta nhé",
                            "Cửa\nhàng",
                            "Mua lại\nvật phẩm\nđã bán [" + player.inventory.itemsDaBan.size() + "/20]");
                } else {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Ngươi cần trang bị gì cứ đến chỗ ta nhé", "Cửa\nhàng");
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
                        if (player.gender == ConstPlayer.XAYDA) {
                            ShopService.gI().opendShop(player, "APPULE", true);
                        } else {
                            this.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Về hành tinh hạ đẳng của ngươi mà mua đồ cùi nhé. Tại đây ta chỉ bán đồ cho người Xayda thôi", "Đóng");
                        }
                    }
                    case 1 -> {
                        if (!player.inventory.itemsDaBan.isEmpty()) {
                            ShopService.gI().opendShop(player, "ITEMS_DABAN", true);
                        }
                    }
                }
            }
        }
    }
}
