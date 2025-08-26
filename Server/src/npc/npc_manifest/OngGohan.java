package npc.npc_manifest;

/**
 * @author NTD
 */
import consts.ConstNpc;
import consts.ConstTask;
import consts.ConstTaskBadges;
import item.Item;

import java.util.ArrayList;
import java.util.List;

import jdbc.daos.PlayerDAO;
import npc.Npc;
import player.Player;
import services.InventoryService;
import services.ItemService;
import services.NpcService;
import services.PetService;
import services.Service;
import services.TaskService;
import services.func.Input;
import shop.ShopService;
import task.Badges.BadgesTaskService;
import utils.Util;

public class OngGohan extends Npc {

    public OngGohan(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    int costNapVang = 1;

    int[][] napVang = { { 20000, 40 }, { 50000, 110 }, { 100000, 250 }, { 500000, 2500 }, { 1000000, 5000 },
            { 2000000, 10000 }, { 5000000, 30000 } };

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                        "\nXin chào ta có thể giúp gì cho con!!!"
                                + "\nServer Free 100% lên web để mở thành viên."
                                + "\nCon đang có :" + player.getSession().cash + " Coin"
                                + "\nGIFTCODE: test1 ~ test16 ",
                        "GiftCode",
                        // "Xóa đệ",
                        "Nhận ngọc",
                        "Hỗ trợ\nNhiệm vụ",
                        // "Hộp thư",
                        // "Nhận đệ tử",
                        "Từ chối");
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (player.iDMark.isBaseMenu()) {
                switch (select) {
                    case 0: // mã quà tặng
                        Input.gI().createFormGiftCode(player);
                        break;
                    // case 1: // nạp tiền
                    //     String npcSay = "Số dư của con là: " + Util.mumberToLouis(player.getSession().cash)
                    //             + " Coin dùng để nạp qua đơn vị khác\n"
                    //             + "Ta đang giữ giúp con " + Util.mumberToLouis(player.getSession().goldBar)
                    //             + " thỏi vàng";
                    //     createOtherMenu(player, ConstNpc.NAP_TIEN, npcSay,
                    //             "Nạp Coin",
                    //             "Nạp vàng",
                    //             "Nhận\nThỏi vàng",
                    //             "Đóng");
                    //     break;
                    // case 1:// xóa đệ
                    //     this.createOtherMenu(player, 12456,
                    //             "|0|Bạn muốn xóa đệ với giá 10K Coin ?",
                    //             "Đồng ý", "Không");
                    //     break;

                    case 1: // nhận ngọc
                        if (player.inventory.gem >= 10000 || player.inventory.ruby >= 10000) {
                            Service.gI().sendThongBao(player, "Tiêu hết đi đã r nhận cu");
                            return;
                        }
                        int soLuongNgoc = 10_000;
                        player.inventory.gem += soLuongNgoc;
                        Service.gI().sendMoney(player);
                        Service.gI().sendThongBao(player,
                                "Bạn vừa nhận " + Util.mumberToLouis(soLuongNgoc) + " ngọc xanh");
                        break;
                    // case 6: // nhận đệ
                    //     if (!player.getSession().actived) {
                    //         Service.gI().sendThongBao(player, "Vui lòng mở thành viên trước");
                    //         return;
                    //     }
                    //     if (player.pet == null) {
                    //         PetService.gI().createNormalPet(player);
                    //         Service.gI().sendThongBao(player, "Bạn vừa nhận được đệ tử");
                    //     } else {
                    //         this.npcChat(player, "Bú ít thôi con, giấu số đá còn lại ở đâu r ");
                    //     }

                    //     break;
                    case 2:
                        if (TaskService.gI().getIdTask(player) >= ConstTask.TASK_9_0
                                && TaskService.gI().getIdTask(player) < ConstTask.TASK_11_0) {
                            player.playerTask.taskMain.id = 10;
                            player.playerTask.taskMain.index = 0;
                            TaskService.gI().sendNextTaskMain(player);
                            Service.gI().sendThongBao(player, "Bạn đã được hỗ trợ nhiệm vụ thành công");
                        } 
                        // else if (TaskService.gI().getIdTask(player) >= ConstTask.TASK_12_0
                        //         && TaskService.gI().getIdTask(player) < ConstTask.TASK_14_0) {
                        //     player.playerTask.taskMain.id = 13;
                        //     player.playerTask.taskMain.index = 0;
                        //     TaskService.gI().sendNextTaskMain(player);
                        //     Service.gI().sendThongBao(player, "Bạn đã được hỗ trợ nhiệm vụ thành công");
                        // } 
                        // else if (TaskService.gI().getIdTask(player) >= ConstTask.TASK_15_0
                        //         && TaskService.gI().getIdTask(player) < ConstTask.TASK_16_0) {
                        //     player.playerTask.taskMain.id = 16;
                        //     player.playerTask.taskMain.index = 0;
                        //     TaskService.gI().sendNextTaskMain(player);
                        //     Service.gI().sendThongBao(player, "Bạn đã được hỗ trợ nhiệm vụ thành công");
                        // } 
                        else if (TaskService.gI().getIdTask(player) >= ConstTask.TASK_16_0
                                && TaskService.gI().getIdTask(player) < ConstTask.TASK_17_0) {
                            player.playerTask.taskMain.id = 17;
                            player.playerTask.taskMain.index = 0;
                            TaskService.gI().sendNextTaskMain(player);
                            Service.gI().sendThongBao(player, "Bạn đã được hỗ trợ nhiệm vụ thành công");
                        } else if (TaskService.gI().getIdTask(player) >= ConstTask.TASK_18_0
                                && TaskService.gI().getIdTask(player) < ConstTask.TASK_19_0) {
                            player.playerTask.taskMain.id = 19;
                            player.playerTask.taskMain.index = 0;
                            TaskService.gI().sendNextTaskMain(player);
                            Service.gI().sendThongBao(player, "Bạn đã được hỗ trợ nhiệm vụ thành công");
                        } else {
                            Service.gI().sendThongBao(player,
                                    "Chỉ hỗ trợ nhiệm vụ DHVT, nhiệm vụ Thách đấu và Trung úy trắng");
                        }
                        break;
                    // case 3:
                    //     this.createOtherMenu(player, ConstNpc.MAIL_BOX,
                    //             "Ta đang giữ hòm thư của con.\n"
                    //                     + "Hãy xem và xử lý các món đồ bên trong.",
                    //             "Hòm Thư\n(" + (player.inventory.itemsMailBox.size()
                    //                     - InventoryService.gI().getCountEmptyListItem(player.inventory.itemsMailBox))
                    //                     + " món)",
                    //             "Xóa Hết\nHòm Thư", "Đóng");
                    //     break;

                }
            } else if (player.iDMark.getIndexMenu() == ConstNpc.MAIL_BOX) {
                switch (select) {
                    case 0:
                        // player.inventory.itemsMailBox.clear();
                        // player.inventory.itemsMailBox.addAll(GodGK.getMailBox(player));
                        ShopService.gI().opendShop(player, "ITEMS_MAIL_BOX", true);
                        break;
                    case 1:
                        NpcService.gI().createMenuConMeo(player,
                                ConstNpc.CONFIRM_REMOVE_ALL_ITEM_MAIL_BOX, this.avartar,
                                "|3|Bạn chắc muốn xóa hết vật phẩm trong hòm thư?\n"
                                        + "|7|Sau khi xóa sẽ không thể khôi phục!",
                                "Đồng ý", "Hủy bỏ");
                        break;
                    case 2:
                        break;
                }
            } else if (player.iDMark.getIndexMenu() == ConstNpc.NAP_TIEN) {
                switch (select) {
                    case 0:
                        NpcService.gI().createBigMessage(player, avartar, "Nhớ đăng nhập xong sau đó bấm NẠP!!!",
                                (byte) 1, "NẠP", "http://javhd.pro");
                        break;
                    case 1:
                        List<String> menu = new ArrayList<>();
                        for (int i = 0; i < napVang.length; i++) {
                            menu.add(i, Util.mumberToLouis(napVang[i][0]) + "\n"
                                    + Util.mumberToLouis(napVang[i][1] * costNapVang) + " Thỏi\nvàng");
                        }
                        String[] menus = menu.toArray(new String[0]);
                        createOtherMenu(player, ConstNpc.NAP_VANG, "Ta sẽ giữ giúp con\n"
                                + "Nếu con cần dùng tới hãy quay lại đây gặp ta!", menus);
                        break;
                    case 2:
                        if (player.getSession().goldBar > 0) {
                            List<Item> listItem = new ArrayList<>();
                            Item thoiVang = ItemService.gI().createNewItem((short) 457, player.getSession().goldBar);
                            listItem.add(thoiVang);
                            if (InventoryService.gI().getCountEmptyBag(player) < listItem.size()) {
                                Service.gI().sendThongBao(player,
                                        "Cần ít nhất " + listItem.size() + " ô trống trong hành trang");
                            }
                            for (Item it : listItem) {
                                InventoryService.gI().addItemBag(player, it);
                                InventoryService.gI().sendItemBag(player);
                            }
                            Service.gI().sendThongBao(player,
                                    "Bạn đã nhận được " + player.getSession().goldBar + " thỏi vàng");
                            PlayerDAO.subGoldBar(player, -(napVang[select][1] * costNapVang));
                            PlayerDAO.subGoldBar(player, player.getSession().goldBar);
                        }
                        break;
                }
            } else if (player.iDMark.getIndexMenu() == ConstNpc.NAP_VANG) {
                if (player.getSession().cash >= napVang[select][0]) {
                    List<Item> listItem = new ArrayList<>();
                    if (InventoryService.gI().getCountEmptyBag(player) < listItem.size()) {
                        Service.gI().sendThongBao(player,
                                "Cần ít nhất " + listItem.size() + " ô trống trong hành trang");
                    }
                    for (Item it : listItem) {
                        InventoryService.gI().addItemBag(player, it);
                        InventoryService.gI().sendItemBag(player);
                    }
                    PlayerDAO.subcash(player, napVang[select][0]);
                    BadgesTaskService.updateCountBagesTask(player, ConstTaskBadges.DAI_GIA_MOI_NHU, napVang[select][0]);
                    PlayerDAO.subGoldBar(player, -(napVang[select][1] * costNapVang));
                    Service.gI().sendThongBao(player,
                            "Bạn có thêm " + Util.mumberToLouis(napVang[select][1] * costNapVang) + " thỏi vàng");
                } else {
                    Service.gI().sendThongBao(player, "Không đủ số dư");
                }
            } else if (player.iDMark.getIndexMenu() == 12456) {
                switch (select) {
                    case 0:
                        if (player.getSession().cash < 10000) {
                            Service.gI().sendThongBao(player, "Xóa đệ cần 10K Coin");
                            return;
                        }
                        if (player.pet != null) {
                            PetService.gI().deletePet(player);
                        }
                        break;
                    case 1:
                        break;
                }
            }
        }
    }
}
