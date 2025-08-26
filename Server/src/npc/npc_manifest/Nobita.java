package npc.npc_manifest;
import consts.ConstFont;
import java.time.LocalDate;
import jdbc.daos.PlayerDAO;
import npc.Npc;
import player.Player;
import server.ServerManager;
import services.InventoryService;
import services.PetService;
import services.Service;
import shop.ShopService;
import utils.Util;

public class Nobita extends Npc {
    public Nobita(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            if (this.mapId == 5 ) {
                this.createOtherMenu(player, 100, "Hệ thống sổ sứ mệnh VIP\n"
                        + "Nâng cấp VIP để nhận nhiều ưu đãi đặc biệt.\n"
                        + "Mỗi lần nâng VIP có hiệu lực trong 30 ngày.\n"
                        + "Sau 30 ngày, bạn cần nâng cấp lại để duy trì quyền lợi.\n"
                        + "Lưu ý: Bạn chỉ có thể nâng cấp VIP 1 lần duy nhất.\n",
                        "Vip 1", "Vip 2", "Vip 3", "Vip 4", "Status", "Cửa hàng\nTrân Dị", "Đóng");
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            switch (player.iDMark.getIndexMenu()) {
                case 100 -> { // Đây là menu chọn cấp VIP
                    switch (select) {
                        case 4 -> { // Tình Trạng VIP
                            this.createOtherMenu(player, 3422,
                                    ConstFont.BOLD_GREEN + "VIP STATUS"
                                            + (player.vip == 1 ? "\n|7|Trạng Thái VIP : VIP 1"
                                                    : player.vip == 2 ? "\n|7|Trạng Thái VIP : VIP 2"
                                                            : player.vip == 3 ? "\n|7|Trạng Thái VIP : VIP 3"
                                                                    : player.vip == 4 ? "\n|7|Trạng Thái VIP : VIP 4"
                                                                            : player.vip == 0
                                                                                    ? "\n|7|Trạng Thái VIP : Không có"
                                                                                    : "")
                                            + "\nCảm Ơn Đã Ủng Hộ Ngọc Rồng by Tấn Đạt"
                                            + ((player.vip != 0 && player.timevip != null)
                                                    ? "\nHạn còn : " + Util.soNgayConLai(player.timevip)
                                                    : "")
                                            + ((player.vip != 0 && player.timevip != null)
                                                    ? "\nNgày mua VIP: " + player.timevip.toString() // Hiển thị ngày
                                                                                                     // mua VIP nếu
                                                                                                     // không null
                                                    : ""),
                                    "Đóng");
                        }

                        case 0 -> { // Chọn VIP1
                            this.createOtherMenu(player, 223,
                                    "Nâng Cấp VIP 1: 200.000 Điểm Mùa\n"
                                            + "- Tặng 1 Đệ Tử\n"
                                            + "- 16 Thỏi Vàng\n"
                                            + "- 10 Phiếu Giảm Giá 80%\n"
                                            + "- 5 Đá Bảo Vệ\n"
                                            + "- Cải Trang Black Goku 30 Ngày\n"
                                            + "- Cá Zombie 30 Ngày\n"
                                            + "- Pet Chó 3 Đầu Địa Ngục 30 Ngày\n",
                                    "200.000 VND", "Đóng");
                        }
                        case 1 -> { // Chọn VIP2
                            this.createOtherMenu(player, 224,
                                    "Nâng Cấp VIP 2: 500.000 Điểm Mùa\n"
                                            + "- Tặng 1 Đệ Tử\n"
                                            + "- 32 Thỏi Vàng\n"
                                            + "- 10 Phiếu Giảm Giá 80%\n"
                                            + "- 10 Thẻ Rồng Thần Namek\n"
                                            + "- 10 Đá Bảo Vệ\n"
                                            + "- Cải Trang Black Goku Vĩnh Viễn\n"
                                            + "- Cá Zombie Vĩnh Viễn\n"
                                            + "- Pet Chó 3 Đầu Địa Ngục Vĩnh Viễn\n",
                                    "500.000 VND", "Đóng");
                        }
                        case 2 -> { // Chọn VIP3
                            this.createOtherMenu(player, 225,
                                    "Nâng Cấp VIP 3: 800.000 Điểm Mùa\n"
                                            + "- Tặng 1 Đệ Tử\n"
                                            + "- 64 Thỏi Vàng\n"
                                            + "- 10 Phiếu Giảm Giá 80%\n"
                                            + "- 20 Đá Bảo Vệ\n"
                                            + "- 10 Thẻ Tiểu Đội Trưởng Vàng\n"
                                            + "- Cải Trang Black Goku Rose 30 Ngày\n"
                                            + "- 1 Capsule Thần Linh\n"
                                            + "- Cánh Thiên Thần - Ác Quỷ 30 Ngày\n"
                                            + "- Pet Capybara 30 Ngày\n",
                                    "800.000 VND", "Đóng");
                        }
                        case 3 -> { // Chọn SVIP
                            this.createOtherMenu(player, 226,
                                    "Nâng Cấp VIP 4: 1.000.000 Điểm Mùa\n"
                                            + "- Tặng 1 Đệ Tử\n"
                                            + "- 128 Thỏi Vàng\n"
                                            + "- 10 Phiếu Giảm Giá 80%\n"
                                            + "- 50 Đá Bảo Vệ\n"
                                            + "- 20 Thẻ Tiểu Đội Trưởng Vàng & Namek\n"
                                            + "- Cải Trang Black Goku Rose 30 Ngày\n"
                                            + "- 2 Capsule Đồ Thần Linh\n"
                                            + "- Cánh Thiên Thần - Ác Quỷ Vĩnh Viễn\n"
                                            + "- Pet Capybara Vĩnh Viễn\n",
                                    "1.000.000 VND", "Đóng");
                        }
                        case 5 -> {
                            Service.gI().sendThongBao(player, "Đang cập nhật, đừng vội huhu");
                            ShopService.gI().opendShop(player, "TORI_BOT", true);
                        }
                    }
                }
                case 223 -> { // Kích Hoạt VIP1
                    if (select == 0) {
                        if (player.vip > 5) {
                            this.npcChat(player, "Bạn đã có VIP, không thể mua thêm.");
                            return;
                        }
                        if (player.getSession().cash >= 200_000) {
                            if (InventoryService.gI().getCountEmptyBag(player) < 7) {
                                this.npcChat(player, "cần 7 ô trống hành trang");
                                return;
                            }
                            // Reset hoặc kích hoạt VIP mới
                            player.vip = 1;
                            player.timevip = LocalDate.now();
                            QuaToriBot.Qua_1(player);
                            if (player.pet == null) {
                                PetService.gI().createNormalPet(player, Util.nextInt(0, 2));
                            }
                            // Trừ điểm và subvip
                            PlayerDAO.subcash(player, 200_000);
                            this.npcChat(player, "Kích hoạt thành công: VIP 1");
                        } else {
                            Service.gI().sendThongBao(player,
                                    "điểm tích lũy chưa đủ.\nTruy Cập: " + ServerManager.DOMAIN + "\n để nạp thêm");
                        }
                    }
                    break;
                }
                case 224 -> { // Kích Hoạt VIP2
                    if (select == 0) {
                        if (select == 0) {
                            if (player.vip > 0) {
                                this.npcChat(player, "Bạn đã có VIP, không thể mua thêm.");
                                return;
                            }
                        }
                        if (player.getSession().cash >= 500_000) {
                            if (InventoryService.gI().getCountEmptyBag(player) < 8) {
                                this.npcChat(player, "cần 8 ô trống hành trang");
                                return;
                            }
                            // Reset hoặc kích hoạt VIP mới
                            player.vip = 2;
                            player.timevip = LocalDate.now();
                            QuaToriBot.Qua_2(player);
                            if (player.pet == null) {
                                PetService.gI().createNormalPet(player, Util.nextInt(0, 2));
                            }
                            // Trừ điểm và subvip
                            PlayerDAO.subcash(player, 500_000);
                            this.npcChat(player, "Kích hoạt thành công: VIP 2");
                        } else {
                            Service.gI().sendThongBaoOK(player,
                                    "điểm tích lũy chưa đủ.\nTruy Cập: " + ServerManager.DOMAIN + "\n để nạp thêm");
                        }
                    }
                    break;
                }
                case 225 -> { // Kích Hoạt VIP3
                    if (select == 0) {
                        if (select == 0) {
                            if (player.vip > 0) {
                                this.npcChat(player, "Bạn đã có VIP, không thể mua thêm.");
                                return;
                            }
                        }
                        if (player.getSession().cash >= 800_000) {
                            if (InventoryService.gI().getCountEmptyBag(player) < 8) {
                                this.npcChat(player, "cần 8 ô trống hành trang");
                                return;
                            }
                            // Reset hoặc kích hoạt VIP mới
                            player.vip = 3;
                            player.timevip = LocalDate.now();
                            QuaToriBot.Qua_3(player);

                            if (player.pet == null) {
                                PetService.gI().createNormalPet(player, Util.nextInt(0, 2));
                            }

                            // Trừ điểm và subvip
                            PlayerDAO.subcash(player, 800_000);
                            this.npcChat(player, "Kích hoạt thành công: VIP 3");
                        } else {
                            Service.gI().sendThongBaoOK(player,
                                    "điểm tích lũy chưa đủ.\nTruy Cập: " + ServerManager.DOMAIN + "\n để nạp thêm");
                        }
                    }
                    break;
                }
                case 226 -> { // Kích Hoạt VIP4
                    if (select == 0) {
                        if (select == 0) {
                            if (player.vip > 0) {
                                this.npcChat(player, "Bạn đã có VIP, không thể mua thêm.");
                                return;
                            }
                        }
                        if (player.getSession().cash >= 1_000_000) {
                            if (InventoryService.gI().getCountEmptyBag(player) < 9) {
                                this.npcChat(player, "cần 9 ô trống hành trang");
                                return;
                            }
                            // Reset hoặc kích hoạt VIP mới
                            player.vip = 4;
                            player.timevip = LocalDate.now();
                            QuaToriBot.Qua_4(player);
                            PetService.gI().createNormalPet(player, Util.nextInt(0, 2));

                            // Trừ điểm và subvip
                            PlayerDAO.subcash(player, 1_000_000);
                            this.npcChat(player, "Kích hoạt thành công: VIP 4");
                        } else {
                            Service.gI().sendThongBaoOK(player,
                                    "điểm tích lũy chưa đủ.\nTruy Cập: " + ServerManager.DOMAIN + "\n để nạp thêm");
                        }
                    }
                    break;
                }
            }
        }
    }
}
