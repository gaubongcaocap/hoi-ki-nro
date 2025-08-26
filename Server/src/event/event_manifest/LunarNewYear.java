package event.event_manifest;

/**
 *
 * @author YourSoulMatee
 */
import boss.BossID;
import event.Event;
import item.Item;
import java.util.Random;
import player.Player;
import services.InventoryService;
import services.ItemService;
import services.Service;
import utils.Util;

public class LunarNewYear extends Event {

    private static LunarNewYear I;

    public static LunarNewYear gI() {
        if (LunarNewYear.I == null) {
            LunarNewYear.I = new LunarNewYear();
        }
        return LunarNewYear.I;
    }

    @Override
    public void npc() {
        createNpc(0, 79, 816, 432);
    }

    @Override
    public void boss() {
        createBoss(BossID.LAN_CON, 20);
    }

    public void cayNeuQuaThuong(Player player) {
        Item DayPhao = null;
        Item CauDoi = null;
        Item DayTreoBanh = null;
        Item DenLongTreoCay = null;

        try {
            DayPhao = InventoryService.gI().findItemBag(player, 1472);  // Dây Pháo
            CauDoi = InventoryService.gI().findItemBag(player, 1473);   // Câu Đối
            DayTreoBanh = InventoryService.gI().findItemBag(player, 1474);  // Dây Treo Bánh
            DenLongTreoCay = InventoryService.gI().findItemBag(player, 1475);  // Lồng Đèn Treo Cây
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (DayPhao == null || CauDoi == null || DayTreoBanh == null || DenLongTreoCay == null) {
            Service.gI().sendThongBao(player, "Bạn còn thiếu nguyên liệu");
            return;
        }

        if (DayPhao.quantity < 2 || CauDoi.quantity < 4 || DayTreoBanh.quantity < 20 || DenLongTreoCay.quantity < 1) {
            Service.gI().sendThongBao(player, "Số nguyên liệu trong hành trang của ngươi không đủ");
            return;
        }

        // Kiểm tra ô trống trong hành trang
        if (InventoryService.gI().getCountEmptyBag(player) <= 0) {
            Service.gI().sendThongBao(player, "Bạn cần ít nhất 1 ô trống trong hành trang để nhận phần thưởng");
            return;
        }

        long goldRequired = 5000000; // 5 triệu vàng
        if (player.inventory.gold < goldRequired) {
            Service.gI().sendThongBao(player, "Bạn không đủ vàng để nhận phần thưởng");
            return;
        }

        player.inventory.gold -= goldRequired;
        Service.gI().sendMoney(player);

        InventoryService.gI().subQuantityItemsBag(player, DayPhao, 2);
        InventoryService.gI().subQuantityItemsBag(player, CauDoi, 4);
        InventoryService.gI().subQuantityItemsBag(player, DayTreoBanh, 20);
        InventoryService.gI().subQuantityItemsBag(player, DenLongTreoCay, 1);

        Item qua = getRandomCayNeuThuong(player);
        InventoryService.gI().addItemBag(player, qua);
        InventoryService.gI().sendItemBag(player);

        // Thêm thông báo chi tiết về tên vật phẩm
        Service.gI().sendThongBao(player, "Bạn nhận được " + qua.template.name + "!");
    }

    public void cayNeuQuaVip(Player player) {
        Item DayPhao = null;  // Dây Pháo
        Item CauDoi = null;   // Câu Đối
        Item DayTreoBanh = null;  // Dây Treo Bánh
        Item DenLongTreoCay = null;  // Lồng Đèn Treo Cây

        try {
            DayPhao = InventoryService.gI().findItemBag(player, 1472);  // Dây Pháo
            CauDoi = InventoryService.gI().findItemBag(player, 1473);   // Câu Đối
            DayTreoBanh = InventoryService.gI().findItemBag(player, 1474);  // Dây Treo Bánh
            DenLongTreoCay = InventoryService.gI().findItemBag(player, 1475);  // Lồng Đèn Treo Cây
        } catch (Exception e) {
            Service.gI().sendThongBao(player, "Lỗi khi kiểm tra vật phẩm: " + e.getMessage());
            return;
        }

        if (DayPhao == null || CauDoi == null || DayTreoBanh == null || DenLongTreoCay == null) {
            Service.gI().sendThongBao(player, "Bạn còn thiếu nguyên liệu");
            return;
        }

        if (DayPhao.quantity < 2 || CauDoi.quantity < 4 || DayTreoBanh.quantity < 20 || DenLongTreoCay.quantity < 1) {
            Service.gI().sendThongBao(player, "Số nguyên liệu trong hành trang của ngươi không đủ");
            return;
        }

        // Kiểm tra ô trống trong hành trang
        if (InventoryService.gI().getCountEmptyBag(player) <= 0) {
            Service.gI().sendThongBao(player, "Bạn cần ít nhất 1 ô trống trong hành trang để nhận phần thưởng");
            return;
        }

        long goldRequired = 5000000; // 5 triệu vàng
        if (player.inventory.gold < goldRequired) {
            Service.gI().sendThongBao(player, "Bạn không đủ vàng để nhận phần thưởng");
            return;
        }

        int gemRequired = 5;  // 5 gem
        if (player.inventory.gem < gemRequired) {
            Service.gI().sendThongBao(player, "Bạn không đủ gem để nhận phần thưởng");
            return;
        }

        try {
            // Trừ vàng và gem
            player.inventory.gold -= goldRequired;
            player.inventory.gem -= gemRequired;
            Service.gI().sendMoney(player);

            // Trừ nguyên liệu
            InventoryService.gI().subQuantityItemsBag(player, DayPhao, 2);
            InventoryService.gI().subQuantityItemsBag(player, CauDoi, 4);
            InventoryService.gI().subQuantityItemsBag(player, DayTreoBanh, 20);
            InventoryService.gI().subQuantityItemsBag(player, DenLongTreoCay, 1);

            // Lấy phần thưởng ngẫu nhiên
            Item qua1 = getRandomCayNeuVip(player);
            InventoryService.gI().addItemBag(player, qua1);
            InventoryService.gI().sendItemBag(player);

            // Thông báo phần thưởng
            Service.gI().sendThongBao(player, "Bạn nhận được " + qua1.template.name + "!");
        } catch (Exception e) {
            Service.gI().sendThongBao(player, "Lỗi trong quá trình giao dịch: " + e.getMessage());
        }
    }

    private Item getRandomCayNeuThuong(Player player) {
        int randomChoice = Util.nextInt(1, 100); // Chọn ngẫu nhiên từ 1 đến 100

        Item item = null;
        if (randomChoice <= 15) {
            item = ItemService.gI().createNewItem((short) Util.nextInt(381, 385));
        } // Nếu randomChoice từ 16-35 -> Chọn Sao pha lê cấp 1 (441-447)
        else if (randomChoice <= 35) {
            int rand = Util.nextInt(0, 6); // Random từ 0 đến 6
            item = ItemService.gI().createNewItem((short) (441 + rand));
            item.itemOptions.add(new Item.ItemOption(95 + rand, (rand == 3 || rand == 4) ? 3 : 5)); // Tùy chỉnh option cho Sao pha lê
        } // Nếu randomChoice từ 36-55 -> Chọn Đá đập cấp (220-224)
        else if (randomChoice <= 55) {
            int rand = Util.nextInt(0, 4); // Random từ 0 đến 4
            item = ItemService.gI().createNewItem((short) (220 + rand));
            item.itemOptions.add(new Item.ItemOption(71 - rand, 0)); // Option cơ bản cho Đá đập cấp
        } // Nếu randomChoice từ 56-70 -> Chọn Cải trang Kimono [hạn sử dụng, vĩnh viễn]
        else if (randomChoice <= 70) {
            int hanhtinh = player.gender;
            short itemId = (short) (Util.nextInt(0, 3) == 2 ? (hanhtinh == 0 ? 1174 : hanhtinh == 1 ? 1175 : 1176) : 0);
            item = ItemService.gI().createNewItem(itemId);

            if (hanhtinh == 0) {
                itemId = 1174;
            } else if (hanhtinh == 1) {
                itemId = 1175;
            } else if (hanhtinh == 2) {
                itemId = 1176;
            } else {
                itemId = 0;  // Không có vật phẩm nếu giới tính không hợp lệ
            }

            item = ItemService.gI().createNewItem(itemId);

            item.itemOptions.add(new Item.ItemOption(50, 23)); // Sức đánh
            item.itemOptions.add(new Item.ItemOption(77, 22)); // HP
            item.itemOptions.add(new Item.ItemOption(94, 19)); // Giảm sát thương
            item.itemOptions.add(new Item.ItemOption(97, 3));  // Phản sát thương
            item.itemOptions.add(new Item.ItemOption(148, 20)); // Hạn sử dụng

            if (Util.isTrue(95, 100)) {
                int quantity = Util.nextInt(0, 3) == 0 ? 3 : (Util.nextInt(0, 3) == 1 ? 7 : (Util.nextInt(0, 3) == 2 ? 14 : 30));
                item.itemOptions.add(new Item.ItemOption(93, quantity)); // Hạn sử dụng
            }
        } // Nếu randomChoice từ 71-85 -> Chọn Pet Mèo đen đuôi vàng (1188)
        else if (randomChoice <= 85) {
            item = ItemService.gI().createNewItem((short) 1188);
            // Thêm các option cho Pet Mèo đen đuôi vàng
            item.itemOptions.add(new Item.ItemOption(50, 13));  // Sức đánh
            item.itemOptions.add(new Item.ItemOption(77, 13));  // HP
            item.itemOptions.add(new Item.ItemOption(103, 13)); // KI
            item.itemOptions.add(new Item.ItemOption(80, 10));  // HP+#%/30s

            if (Util.isTrue(95, 100)) {
                int quantity = Util.nextInt(0, 3) == 0 ? 3 : (Util.nextInt(0, 3) == 1 ? 7 : (Util.nextInt(0, 3) == 2 ? 14 : 30));
                item.itemOptions.add(new Item.ItemOption(93, quantity)); // Hạn sử dụng
            } else {
                item = ItemService.gI().createNewItem((short) 1197);

                item.itemOptions.add(new Item.ItemOption(50, 14));  // Sức đánh
                item.itemOptions.add(new Item.ItemOption(77, 14));  // HP
                item.itemOptions.add(new Item.ItemOption(103, 14)); // KI
                item.itemOptions.add(new Item.ItemOption(14, 7));   // Chí mạng

                // 95% có option 93 (Hạn sử dụng)
                if (Util.isTrue(95, 100)) {
                    int quantity = Util.nextInt(0, 3) == 0 ? 3 : (Util.nextInt(0, 3) == 1 ? 7 : (Util.nextInt(0, 3) == 2 ? 14 : 30));
                    item.itemOptions.add(new Item.ItemOption(93, quantity)); // Hạn sử dụng
                }
            }

        }
        return item;
    }

    private Item getRandomCayNeuVip(Player player) {
        int randomChoice = Util.nextInt(1, 13);

        Item item = null;

        switch (randomChoice) {

            case 1:

                // Chọn ngẫu nhiên giữa 1550, 1551, và 1762
                short selectedItemId1 = Util.nextInt(0, 2) == 0 ? (short) 1550 : (Util.nextInt(0, 2) == 1 ? (short) 1551 : (short) 1762);

                item = ItemService.gI().createNewItem(selectedItemId1);

                item.itemOptions.add(new Item.ItemOption(50, 13));  // Sức đánh
                item.itemOptions.add(new Item.ItemOption(77, 13));  // HP
                item.itemOptions.add(new Item.ItemOption(103, 13)); // KI
                item.itemOptions.add(new Item.ItemOption(80, 10));  // HP+#%/30s

                // 95% có option 93 (Hạn sử dụng), 5% không có
                if (Util.isTrue(99, 100)) {
                    int quantity = Util.nextInt(0, 3) == 0 ? 3 : (Util.nextInt(0, 3) == 1 ? 7 : (Util.nextInt(0, 3) == 2 ? 14 : 30));
                    item.itemOptions.add(new Item.ItemOption(93, quantity)); // Hạn sử dụng
                }
                break;

            case 2:
                item = ItemService.gI().createNewItem((short) 1201);
                item.itemOptions.add(new Item.ItemOption(50, 24)); // Sức đánh
                item.itemOptions.add(new Item.ItemOption(77, 22)); // HP
                item.itemOptions.add(new Item.ItemOption(103, 22)); // KI
                item.itemOptions.add(new Item.ItemOption(80, 20)); // HP/30s
                item.itemOptions.add(new Item.ItemOption(148, 20)); // Tùy chỉnh              
                // 95% có option 93 (Hạn sử dụng), 5% không có
                if (Util.isTrue(99, 100)) {
                    int quantity = Util.nextInt(0, 3) == 0 ? 3 : (Util.nextInt(0, 3) == 1 ? 7 : (Util.nextInt(0, 3) == 2 ? 14 : 30));
                    item.itemOptions.add(new Item.ItemOption(93, quantity)); // Hạn sử dụng
                }
                break;

            case 3:
                // Danh sách các ID vật phẩm cần random
                int[] items = {987, 1071, 1072, 1073};
                // Chọn ngẫu nhiên một ID từ danh sách
                int dropId = items[Util.nextInt(0, items.length - 1)];
                // Tạo vật phẩm với ID được chọn
                item = ItemService.gI().createNewItem((short) dropId);

                // Kiểm tra nếu vật phẩm là một trong các ID 1071, 1072, 1073
                if (dropId == 1071 || dropId == 1072 || dropId == 1073) {
                    // Thêm Option 30 vào vật phẩm
                    item.itemOptions.add(new Item.ItemOption(30, 0));
                }

                // Thêm vật phẩm vào hành trang
                InventoryService.gI().addItemBag(player, item);
                // Gửi thông báo
                Service.gI().sendThongBao(player, "Bạn vừa nhận được vật phẩm: " + item.template.name);
                break;

            case 4:
                item = ItemService.gI().createNewItem((short) Util.nextInt(1071, 1086));
                // Không có option
                break;

            case 5:
                int hanhtinh = player.gender;
                short itemId;

                // Chọn ID item dựa trên giới tính và thêm các ID mới
                if (hanhtinh == 0) {
                    itemId = (short) ((Util.nextInt(0, 3) == 2) ? 1174 : 1484);
                } else if (hanhtinh == 1) {
                    itemId = (short) ((Util.nextInt(0, 3) == 2) ? 1175 : 1485);
                } else if (hanhtinh == 2) {
                    itemId = (short) ((Util.nextInt(0, 3) == 2) ? 1176 : 1486);
                } else {
                    itemId = 0; // Trường hợp không xác định
                }

                item = ItemService.gI().createNewItem(itemId);

                // Thêm các thuộc tính (options) cho item
                item.itemOptions.add(new Item.ItemOption(50, 23)); // Sức đánh
                item.itemOptions.add(new Item.ItemOption(77, 22)); // HP
                item.itemOptions.add(new Item.ItemOption(94, 19)); // Giảm sát thương
                item.itemOptions.add(new Item.ItemOption(97, 3));  // Phản sát thương
                item.itemOptions.add(new Item.ItemOption(148, 20)); // Tùy chỉnh

                // 95% có option 93 (Hạn sử dụng)
                if (Util.isTrue(99, 100)) {
                    int quantity = Util.nextInt(0, 3) == 0 ? 3 : (Util.nextInt(0, 3) == 1 ? 7 : (Util.nextInt(0, 3) == 2 ? 14 : 30));
                    item.itemOptions.add(new Item.ItemOption(93, quantity)); // Hạn sử dụng
                }
                break;
            case 6:
                Random random = new Random();
                short[] possibleItemIds = {1363, 1272, 1273, 1676, 1677, 1678};
                short selectedItemId = possibleItemIds[random.nextInt(possibleItemIds.length)];
                item = ItemService.gI().createNewItem(selectedItemId);
                item.itemOptions.add(new Item.ItemOption(50, 10));
                if (Util.isTrue(99, 100)) {
                    int quantity = random.nextInt(3) == 0 ? 3
                            : (random.nextInt(3) == 1 ? 7
                            : (random.nextInt(3) == 2 ? 14 : 30));
                    item.itemOptions.add(new Item.ItemOption(93, quantity)); // Hạn sử dụng
                }
                break;
            case 7:
                short[] itemIds = {1478, 1479, 1485, 1486, 1571};
                int itemId1 = itemIds[Util.nextInt(0, itemIds.length - 1)];
                item = ItemService.gI().createNewItem((short) itemId1);
                item.itemOptions.add(new Item.ItemOption(50, 14));  // Sức đánh
                item.itemOptions.add(new Item.ItemOption(77, 14));  // HP
                item.itemOptions.add(new Item.ItemOption(103, 14)); // KI
                item.itemOptions.add(new Item.ItemOption(17, 5));   // 100% có option 17

                if (Util.isTrue(99, 100)) {
                    int quantity = Util.nextInt(0, 3) == 0 ? 3 : (Util.nextInt(0, 3) == 1 ? 7 : (Util.nextInt(0, 3) == 2 ? 14 : 30));
                    item.itemOptions.add(new Item.ItemOption(93, quantity)); // Hạn sử dụng
                }
                break;

            case 8:
                item = ItemService.gI().createNewItem((short) 1440);

                break;
            case 9:
                item = ItemService.gI().createNewItem((short) 1476);
                item.itemOptions.add(new Item.ItemOption(50, 23)); // Sức đánh
                item.itemOptions.add(new Item.ItemOption(77, 22)); // HP
                item.itemOptions.add(new Item.ItemOption(94, 19)); // Giảm sát thương
                item.itemOptions.add(new Item.ItemOption(97, 3));  // Phản sát thương
                item.itemOptions.add(new Item.ItemOption(148, 20)); // Tùy chỉnh
                if (Util.isTrue(99, 100)) {
                    int quantity = Util.nextInt(0, 3) == 0 ? 3 : (Util.nextInt(0, 3) == 1 ? 7 : (Util.nextInt(0, 3) == 2 ? 14 : 30));
                    item.itemOptions.add(new Item.ItemOption(93, quantity)); // Hạn sử dụng
                }
                break;
            case 10:
                item = ItemService.gI().createNewItem((short) 1567);
                item.itemOptions.add(new Item.ItemOption(50, 23)); // Sức đánh
                item.itemOptions.add(new Item.ItemOption(77, 23)); // HP
                item.itemOptions.add(new Item.ItemOption(103, 23));
                item.itemOptions.add(new Item.ItemOption(5, 20));
                item.itemOptions.add(new Item.ItemOption(80, 5));
                item.itemOptions.add(new Item.ItemOption(226, 17));
                item.itemOptions.add(new Item.ItemOption(154, 0));
                if (Util.isTrue(99, 100)) {
                    int quantity = Util.nextInt(0, 3) == 0 ? 3 : (Util.nextInt(0, 3) == 1 ? 7 : (Util.nextInt(0, 3) == 2 ? 14 : 30));
                    item.itemOptions.add(new Item.ItemOption(93, quantity)); // Hạn sử dụng
                }
                break;
            case 11:
                int[] items11 = {1438, 1439, 1440, 1423};
                int dropId11 = items11[Util.nextInt(0, items11.length - 1)];
                item = ItemService.gI().createNewItem((short) dropId11);
                InventoryService.gI().addItemBag(player, item);

                Service.gI().sendThongBao(player, "Bạn vừa nhận được vật phẩm: " + item.template.name);
                break;
            case 12:
            case 13:
                int[] items12 = {1592, 1757};
                int dropId12 = items12[Util.nextInt(0, items12.length - 1)];
                item = ItemService.gI().createNewItem((short) dropId12);
                InventoryService.gI().addItemBag(player, item);

                Service.gI().sendThongBao(player, "Bạn vừa nhận được vật phẩm: " + item.template.name);
                break;
        }

        return item;
    }
}
