package npc.npc_manifest;

/**
 *
 * @author YourSoulMatee
 */
import consts.ConstNpc;
import consts.ConstPlayer;
import npc.Npc;
import player.Player;
import services.NpcService;
import services.Service;
import services.TaskService;
import shop.ShopService;

public class DuaHau extends Npc {

    public DuaHau(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

     @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                 if (this.mapId == (21 + player.gender)) {
                    player.timedua.sendTimedua();
                    if (player.timedua.getSecondDone() != 0) {
                        this.createOtherMenu(player, ConstNpc.CAN_NOT_OPEN_DUA, "Thu hoạch dưa hấu nhận 15000 Hồng ngọc",
                                "Hủy bỏ\nDưa hấu", "Đóng");
                    } else {
                        this.createOtherMenu(player, ConstNpc.CAN_OPEN_DUA, "Dưa chín rồi nè", "Thu hoạch", "Hủy bỏ\nDưa hấu", "Đóng");
                    }
                }
            }}

       @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    switch (player.iDMark.getIndexMenu()) {
                        case ConstNpc.CAN_NOT_OPEN_DUA:
                            if (select == 0) {
                                this.createOtherMenu(player, ConstNpc.CONFIRM_DESTROY_DUA,
                                        "Bạn có chắc chắn muốn hủy bỏ Dưa hấu?", "Đồng ý", "Từ chối");
                            }
                            break;
                        case ConstNpc.CAN_OPEN_DUA:
                            switch (select) {
                                case 0:
                                    this.createOtherMenu(player, ConstNpc.CONFIRM_OPEN_DUA,
                                            "Bạn có chắc chắn THU HOẠCH DƯA?\n"
                                            + "Sẽ nhận được 15000 hồng ngọc",
                                            "Thu hoạch");
                                    break;
                                case 1:
                                    this.createOtherMenu(player, ConstNpc.CONFIRM_DESTROY_DUA,
                                            "Bạn có chắc chắn muốn hủy bỏ dưa hấu?", "Đồng ý", "Từ chối");
                                    break;
                            }
                            break;
                        case ConstNpc.CONFIRM_OPEN_DUA:
                            switch (select) {
                                case 0:
                                    player.inventory.ruby += 15000;
                                    Service.gI().sendMoney(player);
                                    this.npcChat(player, "Bạn nhận được 15000 hồng ngọc");
                                    break;

                            }

                        case ConstNpc.CONFIRM_DESTROY_DUA:
                            if (select == 0) {
                                player.timedua.destroydua();
                            }
                            break;
                    }
                }
            }
}
