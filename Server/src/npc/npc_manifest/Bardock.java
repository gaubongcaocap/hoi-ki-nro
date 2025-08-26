package npc.npc_manifest;

import consts.ConstNpc;
/**
 *
 * @author NTD
 */
import consts.ConstTask;
import consts.cn;
import item.Item;
import jdbc.daos.PlayerDAO;
import npc.Npc;
import player.Player;
import services.InventoryService;
import services.NpcService;
import services.PetService;
import services.Service;
import services.TaskService;
import services.func.Input;
import shop.ShopService;

public class Bardock extends Npc {

    public Bardock(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            Item ThucAn = InventoryService.gI().findItem(player.inventory.itemsBag, 993);
            int soLuong = 0;
            if (ThucAn != null) {
                soLuong = ThucAn.quantity;
            }
            int currentTask = TaskService.gI().getIdTask(player);

            // Xử lý đối thoại riêng cho từng nhiệm vụ
            if (currentTask == ConstTask.TASK_31_2) {
                this.createOtherMenu(player, 1,
                        "Tên tôi là Badock, người Xayda\n"
                                + "Hành tinh của tôi vừa bị Fide phá hủy\n"
                                + "Tôi không biết tại sao tôi thoát chết...và xuất hiện tại nơi này nữa\n"
                                + "Tôi đang bị thương, cậu có thể giúp tôi hạ đám lính ngoài kia không?",
                        "OK!");
                return;
            }
            if (currentTask == ConstTask.TASK_31_4) {
                this.createOtherMenu(player, 2,
                        "Cảm ơn cậu đã giúp đỡ\n"
                                + "Lúc rơi xuống đây tôi có gặp một cậu bé tên Berry\n"
                                + "Nhưng do đám lính kia mà chúng tôi dã lạc nhau\n"
                                + "Cậu có thể giúp tôi tìm Bery không?",
                        "OK!");
                return;
            }
            if (currentTask == ConstTask.TASK_31_6) {
                this.createOtherMenu(player, 8,
                        "Mơn cậu lần nữa\n"
                                + "Hiện tại trong hang không còn gì để ăn\n"
                                + "Cậu có thể giúp tôi kiếm thêm một chút lương thực được không",
                        "OK!");
                return;
            }

            if (currentTask == ConstTask.TASK_31_7 && soLuong >= 99) {
                this.createOtherMenu(player, 2,
                        "Mơn cậu thêm lần nữa\n"
                                + "Với số thức ăn này tôi sẽ sớm bình phục\n"
                                + "Ngoài kia bọn lính đang ức hiếp cư dân hành tinh này\n"
                                + "Mong cậu có thể ra sức cứu giúp họ thêm lần nữa",
                        "OK!");
                return;
            }

            switch (mapId) {
                case 0, 7, 14 ->
                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Đây là nơi ngươi có tiền là có thể đổi bất cứ thứ gì"
                                    + "\nNạp tiền hãy truy cập vào trang web: http://javhd.pro"
                                    + " \nNgươi đang có " + player.getSession().cash + " Coin",
                            "Shop\nLinh tinh",
                            "Nạp Tiền",
                            "Mở thành\nViên",
                            "Đổi đệ VIP",
                            "Đổi thỏi\nVàng",
                            "Đổi skill\nĐệ tử");
                default ->
                    super.openBaseMenu(player);
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            int menuId = player.iDMark.getIndexMenu();
            if (menuId == 1 || menuId == 4 || menuId == 8) {
                TaskService.gI().checkDoneTask31(player);
            }
            if (menuId == 2) {
                TaskService.gI().checkDoneTask31_7(player);
            }
            if (player.iDMark.isBaseMenu()) {
                switch (select) {
                    case 0:
                        ShopService.gI().opendShop(player, "BARDOCK_SHOP", false);
                        break;
                    case 4:
                        if (player.getSession() != null) {
                            this.createOtherMenu(player, 777,
                                    "Có tiền rồi đổi thôi!\n"
                                            + "Có thể nhận mốc nạp khi nạp ở quy lão Kame nha"
                                            + " \nBạn đang có :" + player.getSession().cash + " Coin",
                                    // "Đổi thỏi vàng");
                                    "Đổi thỏi vàng", "Đổi ngọc xanh", "Đổi hồng ngọc");
                        }
                        break;
                    case 1:
                        NpcService.gI().createBigMessage(player, avartar, "Nhớ đăng nhập xong sau đó bấm NẠP!!!",
                                (byte) 1, "NẠP", "http://javhd.pro");
                        break;
                    case 3:
                        if (player.getSession() != null) {
                            this.createOtherMenu(player, 13000,
                                    "Đổi đệ bằng điểm Thành Tích\n"
                                            + "Bạn đang có " + player.event.getKillBossPoint() + " Điểm Thành Tích"
                                    + "\nChỉ số hợp thể đệ hiện tại :\n"
                                    + "HP: " + player.pointfusion.getHpFusion() + "%, KI: " + player.pointfusion.getMpFusion() + "%, Sức Đánh: " + player.pointfusion.getDameFusion() + "%",
                                    "Thông tin\nĐệ tử",
                                    cn.petBuuNhiNm + "\n" + cn.de38 + " Điểm",
                                    cn.petFideNhiNm + "\n" + cn.de39 + " Điểm",
                                    cn.petCellNhiNm + "\n" + cn.de40 + " Điểm",
                                    cn.petAndroidNm + "\n" + cn.de41 + " Điểm",
                                    cn.petBuuGayNm + "\n" + cn.de42 + " Điểm",
                                    cn.petBerrusNhiNm + "\n" + cn.de43 + " Điểm",
                                    cn.petBlackNm + "\n" + cn.de44 + " Điểm",
                                    cn.petBerrusNm + "\n" + cn.de45 + " Điểm"
                                    );
                        }
                        break;
                    case 2:
                        if (player.getSession() != null) {
                            this.createOtherMenu(player, 782,
                                    "Mở thành viên free khi bạn nạp tiền  \nBạn đã nạp :"
                                            + "" + player.getSession().cash + " Coin\n",
                                    "Mở", "Đóng");
                        }
                        break;
                    case 5:
                        if (player.getSession() != null) {
                            this.createOtherMenu(player, 888,
                                    "Lưu ý: Đổi Skill đệ bằng tiền nạp sẽ mất Coin\n"
                                            + "\nBạn có: " + player.getSession().cash + " Coin",
                                    // Menu CHọn
                                    "Đổi skill 2-3\n " + cn.skill23 + " Coin",
                                    "Đổi skill 2-4\n " + cn.skill24 + " Coin");
                        }
                        break;
                }
            } else if (player.iDMark.getIndexMenu() == 13000) {
                switch (select) {
                    case 0:
                        NpcService.gI().createTutorial(player, tempId, this.avartar, ConstNpc.HUONG_DAN_DOI_DETU_VIP);
                        break;
                    case 1:
                        ProcessBuyPet(player, cn.de38, select, cn.petBuuNhiNm);
                        break;
                    case 2:
                        ProcessBuyPet(player, cn.de39, select, cn.petFideNhiNm);
                        break;
                    case 3:
                        ProcessBuyPet(player, cn.de40, select, cn.petCellNhiNm);
                        break;
                    case 4:
                        ProcessBuyPet(player, cn.de42, select, cn.petBuuGayNm);
                        break;
                    case 5:
                        ProcessBuyPet(player, cn.de43, select, cn.petAndroidNm);
                        break;
                    case 6:
                        ProcessBuyPet(player, cn.de43, select, cn.petBerrusNhiNm);
                        break;
                    case 7:
                        ProcessBuyPet(player, cn.de44, select, cn.petBlackNm);
                    case 8:
                        ProcessBuyPet(player, cn.de45, select, cn.petBerrusNm);
                        break;
                }
            } else if (player.iDMark.getIndexMenu() == 888) {
                switch (select) {
                    case 0: // thay chiêu 2-3 đệ tử
                        if (player.getSession() != null && player.getSession().cash < cn.skill23) {
                            Service.gI().sendThongBao(player, "Bạn không đủ " + cn.skill23 + " Coin");
                            return;
                        }

                        if (PlayerDAO.subcash(player, cn.skill23)) {
                            if (player.pet != null) {
                                if (player.pet.playerSkill.skills.get(1).skillId != -1) {
                                    player.pet.openSkill2();
                                    if (player.pet.playerSkill.skills.get(2).skillId != -1) {
                                        player.pet.openSkill3();
                                    }
                                    Service.gI().sendThongBao(player, "Đổi skill 2-3 đệ thành công");
                                } else {
                                    Service.gI().sendThongBao(player, "Ít nhất đệ tử ngươi phải có chiêu 2 chứ!");

                                }
                            } else {
                                Service.gI().sendThongBao(player, "Ngươi làm gì có đệ tử?");

                            }
                        }
                        break;
                    case 1: // thay chiêu 2-4 đệ tử
                        if (player.getSession() != null && player.getSession().cash < cn.skill24) {
                            Service.gI().sendThongBao(player, "Bạn không đủ " + cn.skill24 + " Coin");
                            return;
                        }

                        if (PlayerDAO.subcash(player, cn.skill24)) {
                            if (player.pet != null) {
                                if (player.pet.playerSkill.skills.get(1).skillId != -1) {
                                    player.pet.openSkill2();
                                    if (player.pet.playerSkill.skills.get(3).skillId != -1) {
                                        player.pet.openSkill4();
                                    }
                                    Service.gI().sendThongBao(player, "Đổi skill 2-4 đệ thành công");

                                } else {
                                    Service.gI().sendThongBao(player, "Ít nhất đệ tử ngươi phải có chiêu 2 chứ!");

                                }
                            } else {
                                Service.gI().sendThongBao(player, "Ngươi làm gì có đệ tử?");

                            }
                        }
                        break;

                }
            } else if (player.iDMark.getIndexMenu() == 777) {
                switch (select) {
                    case 0:
                        Input.gI().createFormDoiThoiVang(player);
                        break;
                    case 1:
                        Input.gI().createFormDoiNgocXanh(player);
                        break;
                    case 2:
                        Input.gI().createFormDoiNgocHong(player);
                        break;
                }
            } else if (player.iDMark.getIndexMenu() == 782) {
                switch (select) {
                    case 0:
                        if (player.getSession() != null && player.getSession().actived) {
                            Service.gI().sendThongBao(player, "Bạn đã mở thành viên rồi");
                            return;
                        }
                        if (player.getSession() != null && player.getSession().danap < 1000) {
                            NpcService.gI().createBigMessage(player, avartar,
                                    "Bạn chưa nạp 1K, bạn có muốn nạp để mở thành viên không ?!!!", (byte) 1, "NẠP",
                                    "http://160.191.244.129/");
                            return;
                        }
                        if (PlayerDAO.updateActive(player, 1)) {
                            Service.gI().sendThongBao(player, "Bạn đã mở thành viên thành công");
                        } else {
                            Service.gI().sendThongBao(player,
                                    "Đã có lỗi xẩy ra khi kích hoạt tài khoản, vui long liên hệ admin nếu bị trừ tiền mà không kích hoạt được, chụp lại thông báo này");
                        }

                        break;
                    case 1:

                        break;

                }
            } else if (player.iDMark.getIndexMenu() == 0) {
                switch (mapId) {
                }
            }

        }
    }

    private void ProcessBuyPet(Player player, int point, int typePet, String petNm) {
        if (player.pet == null) {
            Service.gI().sendThongBao(player, "Mày cần phải có đệ mới sử dụng được chức năng này");
            return;
        }
        for (Item item : player.pet.inventory.itemsBody) {
            if (item.isNotNullItem()) {
                Service.gI().sendThongBao(player, "Cần bỏ đồ đệ tử đang mặc để sử dụng chức năng");
                return;
            }
        }
        if (player.getSession() != null && player.getSession().cash < point) {
            Service.gI().sendThongBao(player, "Mày không đủ " + point + " điểm, lười săn boss mà đòi có điểm à");
            return;
        }

        int killPoint = player.event.getKillBossPoint();

        if (point > 0) {
            if (killPoint >= point) {
                player.event.subKillBossPoint(point);
                switch (typePet) {
                    case 1:
                        PetService.gI().createPetBuuNhi(player, player.pet != null, player.gender);
                        break;
                    case 2:
                        PetService.gI().createPetFideNhi(player, player.pet != null, player.gender);
                        break;
                    case 3:
                        PetService.gI().createPetCellNhi(player, player.pet != null, player.gender);
                        break;
                    case 4:
                        PetService.gI().createPetAdrBeach(player, player.pet != null, player.gender);
                    case 5:
                        PetService.gI().createPetMabuGay(player, player.pet != null, player.gender);
                        break;
                    case 6:
                        PetService.gI().createPetBerrusNhi(player, player.pet != null, player.gender);
                        break;
                    case 7:
                        PetService.gI().createBlackPet(player, player.pet != null, player.gender);
                        break;
                    case 8:
                        PetService.gI().createBeerusPet(player, player.pet != null, player.gender);
                        break;
                    default:
                        break;
                }

                Service.gI().sendThongBao(player, "Mày đã cứu được " + petNm);

            } else {
                Service.gI().sendThongBao(player, "Bạn còn thiếu " + (point - killPoint) + " điểm");
            }
        }
    }
}
