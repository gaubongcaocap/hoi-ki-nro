package services;

/*
 *
 *
 * @author NTD
 */

import consts.ConstItem;
import item.Item;
import map.ItemMap;
import mob.Mob;
import player.Player;
import utils.Util;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RewardService {

    //id option set kich hoat (tên set, hiệu ứng set, tỉ lệ, type tỉ lệ)
    private static final int[][][] ACTIVATION_SET = {
        {{129, 141, 1, 1000}, {127, 139, 1, 1000}, {128, 140, 1, 1000}}, //songoku - thien xin hang - kirin
        {{131, 143, 1, 1000}, {132, 144, 1, 1000}, {130, 142, 1, 1000}}, //oc tieu - pikkoro daimao - picolo
        {{135, 138, 1, 1000}, {133, 136, 1, 1000}, {134, 137, 1, 1000}} //kakarot - cadic - nappa
    };

    private static RewardService I;

    private RewardService() {

    }

    public static RewardService gI() {
        if (RewardService.I == null) {
            RewardService.I = new RewardService();
        }
        return RewardService.I;
    }

    public static class RatioStar {

        public byte numStar;
        public int ratio;
        public int typeRatio;

        public RatioStar(byte numStar, int ratio, int typeRatio) {
            this.numStar = numStar;
            this.ratio = ratio;
            this.typeRatio = typeRatio;
        }
    }

    //========================LUCKY ROUND========================
    public List<Item> getListItemLuckyRound(Player player, int num, boolean vip) {
        List<Item> list = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            Item it = ItemService.gI().createNewItem((short) 189); // Vàng mặc định
            it.quantity = Util.nextInt(5, 15) * 1000;
            boolean success = Util.isTrue(1, 2);

            if (vip) {
                // VIP - Thay thế vật phẩm nếu thỏa mãn điều kiện
                if (Util.isTrue(1, 200)) {
                    // Thay thế cải trang Mị Nương
                    it = ItemService.gI().createNewItem((short) 860); // Mị Nương
                    it.itemOptions.clear();
                    it.itemOptions.add(new Item.ItemOption(50, 24)); // Sức đánh
                    it.itemOptions.add(new Item.ItemOption(117, 20)); // Đẹp
                    it.itemOptions.add(new Item.ItemOption(114, 25)); // Tốc độ chạy
                    it.itemOptions.add(new Item.ItemOption(77, 25)); // HP
                    it.itemOptions.add(new Item.ItemOption(154, 0)); // Không bán lại

                    // Xác suất 99% thêm option 93
                    if (!Util.isTrue(1, 150)) {
                        it.itemOptions.add(new Item.ItemOption(93, Util.nextInt(3, 15))); // HSD
                    }

                    it.quantity = 1;
//                } else if (Util.isTrue(1, 7000)) {
//                    // Thay thế cải trang Frieren
//                    it = ItemService.gI().createNewItem((short) 1567); // Frieren
//                    it.itemOptions.clear();
//                    it.itemOptions.add(new Item.ItemOption(50, 23)); // Sức đánh
//                    it.itemOptions.add(new Item.ItemOption(77, 23)); // HP
//                    it.itemOptions.add(new Item.ItemOption(103, 23)); // Ki
//                    it.itemOptions.add(new Item.ItemOption(5, 20)); // Sd chis mangj
//                    it.itemOptions.add(new Item.ItemOption(80, 5)); //hp/30s
//                    it.itemOptions.add(new Item.ItemOption(226, 17)); // Đẹp
//                    it.itemOptions.add(new Item.ItemOption(154, 0)); // Không bán lại
//                    // Xác suất 99% thêm option 93
//                    if (!Util.isTrue(1, 300)) {
//                        it.itemOptions.add(new Item.ItemOption(93, Util.nextInt(3, 15))); // HSD
//                    }
//                    it.quantity = 1;
//                } else if (Util.isTrue(1, 80)) {
//                    // Thay thế cải trang Ả Rập
//                    it = ItemService.gI().createNewItem((short) 1309); // Ả Rập
//                    it.itemOptions.clear();
//                    it.itemOptions.add(new Item.ItemOption(50, 24)); // Sức đánh
//                    it.itemOptions.add(new Item.ItemOption(77, 24)); // HP
//                    it.itemOptions.add(new Item.ItemOption(103, 24)); // Ki
//                    it.itemOptions.add(new Item.ItemOption(226, 18)); // Đẹp
//                    it.itemOptions.add(new Item.ItemOption(154, 0)); // Không bán lại
//                    // Xác suất 99% thêm option 93
//                    if (!Util.isTrue(1, 300)) {
//                        it.itemOptions.add(new Item.ItemOption(93, Util.nextInt(3, 15))); // HSD
//                    }
//                    it.quantity = 1;
                } else if (Util.isTrue(1, 50)) {
                    // Thay thế bó hoa hồng 1 (ID 954)
                    it = ItemService.gI().createNewItem((short) 954); // Bó hoa hồng 1
                    it.itemOptions.clear();
                    it.itemOptions.add(new Item.ItemOption(50, 22)); // Sức đánh
                    it.itemOptions.add(new Item.ItemOption(77, 15)); // HP
                    it.itemOptions.add(new Item.ItemOption(103, 15)); // Ki
                    // Xác suất 99% thêm option 93
                    if (!Util.isTrue(1, 200)) {
                        it.itemOptions.add(new Item.ItemOption(93, Util.nextInt(3, 15))); // HSD
                    }
                    it.quantity = 1;
                } else if (Util.isTrue(1, 25)) {
                    // Thay thế bó hoa hồng 1 (ID 954)
                    it = ItemService.gI().createNewItem((short) 955); // Bó hoa hồng 1
                    it.itemOptions.clear();
                    it.itemOptions.add(new Item.ItemOption(50, 15)); // Sức đánh
                    it.itemOptions.add(new Item.ItemOption(77, 10)); // HP
                    it.itemOptions.add(new Item.ItemOption(103, 10)); // Ki
                    // Xác suất 99% thêm option 93
                    if (!Util.isTrue(1, 200)) {
                        it.itemOptions.add(new Item.ItemOption(93, Util.nextInt(3, 15))); // HSD
                    }
                    it.quantity = 1;
                } else if (Util.isTrue(1, 20)) {
                    short[] validIds = {1301, 1360, 1254, 1344, 1519}; // Các ID hợp lệ cho vật phẩm
                    short randomId = validIds[Util.nextInt(0, validIds.length)]; // Random một ID từ danh sách
                    it = ItemService.gI().createNewItem(randomId); // Tạo vật phẩm Minison
                    it.itemOptions.clear();
                    it.itemOptions.add(new Item.ItemOption(50, Util.nextInt(5, 11))); // Sức đánh (5-10)
                    it.itemOptions.add(new Item.ItemOption(77, Util.nextInt(7, 13))); // HP (7-12)
                    it.itemOptions.add(new Item.ItemOption(103, Util.nextInt(7, 13))); // Ki (7-12)
                    if (!Util.isTrue(1, 100)) {
                        it.itemOptions.add(new Item.ItemOption(93, Util.nextInt(3, 16))); // HSD (3-15)
                    }
                    it.quantity = 1;
//                } else if (Util.isTrue(1, 500)) {
//                    // MInison
//                    it = ItemService.gI().createNewItem((short) 1680); //monion
//                    it.itemOptions.clear();
//                    it.itemOptions.add(new Item.ItemOption(50, 19)); // Sức đánh
//                    it.itemOptions.add(new Item.ItemOption(5, 10)); // HPw
//                    it.itemOptions.add(new Item.ItemOption(103, 20)); // Ki
//                    it.itemOptions.add(new Item.ItemOption(30, 0)); // Ki
//                    // Xác suất 99% thêm option 93
//                    if (!Util.isTrue(1, 100)) {
//                        it.itemOptions.add(new Item.ItemOption(93, Util.nextInt(3, 15))); // HSD
//                    }
//                    it.quantity = 1;
                } else if (Util.isTrue(1, 90)) {
                    it = ItemService.gI().createNewItem((short) 987);// Đá bảo vệ
                    it.itemOptions.clear();
                    it.quantity = 1;
                } else if (Util.isTrue(1, 90)) {
                    it = ItemService.gI().createNewItem((short) 596);// Đá bảo vệ
                    it.itemOptions.clear();
                    it.quantity = 1;
                }

                // Nếu không chọn được vật phẩm từ danh sách VIP thì gọi itemRand để lấy vật phẩm mặc định
                if (it.quantity == 0) {
                    it = itemRand(it, success);
                }
            } else {
                // Không VIP
                if (Util.isTrue(1, 2)) {
                    // Các item mặc định khi không có item VIP
                    int[] itemId = {467, 468, 469, 470, 471, 741, 745, 800, 801, 803, 804, 1000};
                    int itemid = itemId[Util.nextInt(itemId.length)];
                    if (Util.isTrue(20, 100)) {
                        int[] itemId2 = {467, 468, 469, 470, 471, 741, 745, 800, 801, 803, 804, 999, 1000, 1001};
                        itemid = itemId2[Util.nextInt(itemId2.length)];
                    }
                    byte[] option = {77, 80, 81, 103, 50, 94, 5};
                    byte optionid;
                    byte param;
                    Item vpdl = ItemService.gI().createNewItem((short) itemid);
                    vpdl.itemOptions.clear();
                    optionid = option[Util.nextInt(0, 6)];
                    param = (byte) Util.nextInt(5, 10);
                    vpdl.itemOptions.add(new Item.ItemOption(optionid, param));
                    vpdl.itemOptions.add(new Item.ItemOption(30, 0));
                    vpdl.itemOptions.add(new Item.ItemOption(93, Util.nextInt(1, 30)));
                    it = vpdl;
                    it.quantity = 1;
                } else if (Util.isTrue(1, 100)) {
                    // Thay thế với các item ngẫu nhiên khác
                    it = ItemService.gI().createNewItem((short) Util.nextInt(18, 20));
                    it.quantity = Util.nextInt(1, 5);
                } else if (Util.isTrue(1, 30)) {
                    // Các vật phẩm từ 220 đến 224
                    it = ItemService.gI().createNewItem((short) Util.nextInt(220, 224));
                    it.quantity = Util.nextInt(1, 5);
                } else if (Util.isTrue(1, 100)) {
                    // Các vật phẩm từ 828 đến 842
                    it = ItemService.gI().createNewItem((short) Util.nextInt(828, 842));
                    it.quantity = Util.nextInt(1, 5);
                }

                // Nếu không chọn được vật phẩm từ danh sách không VIP thì gọi itemRand
                it = itemRand(it, success);
            }

            list.add(it);
        }
        return list;
    }

    public Item itemRand(Item item, boolean success) {
        if (!success) {

            int randomChance = Util.nextInt(1, 100);

            if (randomChance <= 5) { // 5% cơ hội

                short randomItemId = (short) Util.nextInt(220, 225);
                item = ItemService.gI().createNewItem(randomItemId, 1); // Tạo 1 item với ID ngẫu nhiên
            } else {
                // Trả về item vàng mặc định
                item = ItemService.gI().createNewItem((short) 189, Util.nextInt(5, 12) * 1000);
            }
        }
        return item;
    }

    public void rewardLancon(Player player) {
        // Kiểm tra hành trang trống
        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
            player.canReward = false;
            player.haveReward = true;

            // Tạo các tỷ lệ rơi vật phẩm
            int random = Util.nextInt(1, 100);

            if (random <= 30) { // 30% nhận item ID 190, số lượng 10-30 * 1000
                int dropId = 190;
                int quantity = Util.nextInt(10, 30) * 1000;
                dropItem(player, dropId, quantity);

            } else if (random <= 50) { // 20% nhận item ID 77, số lượng 5-15
                int dropId = 77;
                int quantity = Util.nextInt(5, 15);
                dropItem(player, dropId, quantity);

            } else if (random <= 75) { // 25% nhận item ID từ 1150 đến 1153, số lượng 1
                int[] items = {1150, 1151, 1152, 1153};
                int dropId = items[Util.nextInt(0, items.length - 1)];
                dropItem(player, dropId, 1);

            } else { // 25% nhận item ID từ 1074 đến 1083, số lượng 1
                int[] items = {1074, 1075, 1076, 1077, 1078, 1079, 1080, 1081, 1082, 1083};
                int dropId = items[Util.nextInt(0, items.length - 1)];
                dropItem(player, dropId, 1);
            }
        } else {
            // Thông báo hành trang đầy
            Service.gI().sendThongBao(player, "Cần 1 ô hành trang trống");
        }
    }

// Phương thức rơi vật phẩm
    private void dropItem(Player player, int itemId, int quantity) {
        Item item = ItemService.gI().createNewItem((short) itemId);
        item.quantity = quantity;
        InventoryService.gI().addItemBag(player, item);
        InventoryService.gI().sendItemBag(player);
        Service.gI().sendThongBao(player, "Bạn vừa nhận được " + item.template.name + " x" + quantity);
    }

    public Item rewardCapsuleTet(Player player) {
        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
            if (Util.isTrue(40, 100)) {
                int[] items = {734, 920, 849, 743, 733};
                Item item = ItemService.gI().createNewItem((short) items[Util.nextInt(items.length)]);
                byte[] option = {77, 80, 81, 103, 50, 94, 5};
                byte[] option_v2 = {14, 16, 17, 19, 27, 28, 47, 87};
                if (Util.isTrue(5, 100)) {
                    item.itemOptions.add(new Item.ItemOption(option[Util.nextInt(option.length)], Util.nextInt(1, 5)));
                    item.itemOptions.add(new Item.ItemOption(option_v2[Util.nextInt(option_v2.length)], Util.nextInt(1, 2)));
                } else {
                    item.itemOptions.add(new Item.ItemOption(option[Util.nextInt(option.length)], Util.nextInt(1, 10)));
                    if (Util.isTrue(1, 10)) {
                        item.itemOptions.add(new Item.ItemOption(option_v2[Util.nextInt(option_v2.length)], Util.nextInt(1, 10)));
                    }
                    item.itemOptions.add(new Item.ItemOption(93, Util.nextInt(1, 30)));
                }
                item.itemOptions.add(new Item.ItemOption(89, 0));
                item.itemOptions.add(new Item.ItemOption(30, 0));
                return item;
            } else if (Util.isTrue(50, 100)) {
                int[] items = {942, 943, 944};
                Item item = ItemService.gI().createNewItem((short) items[Util.nextInt(items.length)]);
                byte[] option = {77, 80, 81, 103, 50, 94, 5};
                byte[] option_v2 = {14, 16, 17, 19, 27, 28, 47, 87};
                if (Util.isTrue(5, 100)) {
                    item.itemOptions.add(new Item.ItemOption(option[Util.nextInt(option.length)], Util.nextInt(1, 5)));
                    item.itemOptions.add(new Item.ItemOption(option_v2[Util.nextInt(option_v2.length)], Util.nextInt(1, 2)));
                } else {
                    item.itemOptions.add(new Item.ItemOption(option[Util.nextInt(option.length)], Util.nextInt(1, 10)));
                    if (Util.isTrue(1, 10)) {
                        item.itemOptions.add(new Item.ItemOption(option_v2[Util.nextInt(option_v2.length)], Util.nextInt(1, 10)));
                    }
                    item.itemOptions.add(new Item.ItemOption(93, Util.nextInt(1, 30)));
                }
                item.itemOptions.add(new Item.ItemOption(30, 0));
                return item;
            } else {
                Item it = ItemService.gI().createNewItem((short) Util.nextInt(2148, 2152));
                it.quantity = 1;
                if (Util.isTrue(5, 100)) {
                    it.itemOptions.add(new Item.ItemOption(77, Util.nextInt(10, 30)));
                    it.itemOptions.add(new Item.ItemOption(103, Util.nextInt(20, 30)));
                    it.itemOptions.add(new Item.ItemOption(50, Util.nextInt(10, 30)));
                    it.itemOptions.add(new Item.ItemOption(94, Util.nextInt(20, 30)));
                    it.itemOptions.add(new Item.ItemOption(14, Util.nextInt(2, 12)));
                    it.itemOptions.add(new Item.ItemOption(108, Util.nextInt(2, 12)));
                    if (Util.isTrue(5, 30)) {
                        it.itemOptions.add(new Item.ItemOption(5, Util.nextInt(1, 14)));
                    }
                    it.itemOptions.add(new Item.ItemOption(154, 0));
                } else {
                    it.itemOptions.add(new Item.ItemOption(77, Util.nextInt(10, 30)));
                    it.itemOptions.add(new Item.ItemOption(103, Util.nextInt(20, 30)));
                    if (Util.isTrue(5, 30)) {
                        it.itemOptions.add(new Item.ItemOption(5, Util.nextInt(1, 12)));
                    }
                    it.itemOptions.add(new Item.ItemOption(50, Util.nextInt(10, 30)));
                    it.itemOptions.add(new Item.ItemOption(94, Util.nextInt(20, 30)));
                    it.itemOptions.add(new Item.ItemOption(14, Util.nextInt(2, 12)));
                    it.itemOptions.add(new Item.ItemOption(93, Util.nextInt(1, 15)));
                }
                byte[] option = {80, 81, 103, 50, 94, 5};
                byte[] option_v2 = {14, 16, 17, 19, 27, 28, 47, 87};
                if (Util.isTrue(20, 100)) {
                    it.itemOptions.add(new Item.ItemOption(option[Util.nextInt(option.length)], Util.nextInt(1, 5)));
                }
                if (Util.isTrue(20, 100)) {
                    it.itemOptions.add(new Item.ItemOption(108, Util.nextInt(1, 12)));
                }
                if (Util.isTrue(20, 100)) {
                    it.itemOptions.add(new Item.ItemOption(option_v2[Util.nextInt(option_v2.length)], Util.nextInt(1, 2)));
                }
                return it;
            }
        } else {
            Service.gI().sendThongBao(player, "Cần 1 ô hành trang trống");
            return null;
        }
    }

    private void initQuantityGold(ItemMap item) {
        switch (item.itemTemplate.id) {
            case 76:
                item.quantity = Util.nextInt(1000, 5000);
                break;
            case 188:
                item.quantity = Util.nextInt(5000, 10000);
                break;
            case 189:
                item.quantity = Util.nextInt(10000, 20000);
                break;
            case 190:
                item.quantity = Util.nextInt(20000, 30000);
                break;
        }
    }

    //chỉ số cơ bản: hp, ki, hồi phục, sđ, crit
    public void initBaseOptionClothes(int tempId, int type, List<Item.ItemOption> list) {
        int[][] option_param = {{-1, -1}, {-1, -1}, {-1, -1}, {-1, -1}, {-1, -1}};
        switch (type) {
            case 0: //áo
                option_param[0][0] = 47; //giáp
                switch (tempId) {
                    case 0:
                        option_param[0][1] = 2;
                        break;
                    case 33:
                        option_param[0][1] = 4;
                        break;
                    case 3:
                        option_param[0][1] = 8;
                        break;
                    case 34:
                        option_param[0][1] = 16;
                        break;
                    case 136:
                        option_param[0][1] = 24;
                        break;
                    case 137:
                        option_param[0][1] = 40;
                        break;
                    case 138:
                        option_param[0][1] = 60;
                        break;
                    case 139:
                        option_param[0][1] = 90;
                        break;
                    case 230:
                        option_param[0][1] = 200;
                        break;
                    case 231:
                        option_param[0][1] = 250;
                        break;
                    case 232:
                        option_param[0][1] = 300;
                        break;
                    case 233:
                        option_param[0][1] = 400;
                        break;
                    case 1:
                        option_param[0][1] = 2;
                        break;
                    case 41:
                        option_param[0][1] = 4;
                        break;
                    case 4:
                        option_param[0][1] = 8;
                        break;
                    case 42:
                        option_param[0][1] = 16;
                        break;
                    case 152:
                        option_param[0][1] = 24;
                        break;
                    case 153:
                        option_param[0][1] = 40;
                        break;
                    case 154:
                        option_param[0][1] = 60;
                        break;
                    case 155:
                        option_param[0][1] = 90;
                        break;
                    case 234:
                        option_param[0][1] = 200;
                        break;
                    case 235:
                        option_param[0][1] = 250;
                        break;
                    case 236:
                        option_param[0][1] = 300;
                        break;
                    case 237:
                        option_param[0][1] = 400;
                        break;
                    case 2:
                        option_param[0][1] = 3;
                        break;
                    case 49:
                        option_param[0][1] = 5;
                        break;
                    case 5:
                        option_param[0][1] = 10;
                        break;
                    case 50:
                        option_param[0][1] = 20;
                        break;
                    case 168:
                        option_param[0][1] = 30;
                        break;
                    case 169:
                        option_param[0][1] = 50;
                        break;
                    case 170:
                        option_param[0][1] = 70;
                        break;
                    case 171:
                        option_param[0][1] = 100;
                        break;
                    case 238:
                        option_param[0][1] = 230;
                        break;
                    case 239:
                        option_param[0][1] = 280;
                        break;
                    case 240:
                        option_param[0][1] = 330;
                        break;
                    case 241:
                        option_param[0][1] = 450;
                        break;
                    case 555: //áo thần trái đất
                        option_param[2][0] = 21; //yêu cầu sức mạnh

                        option_param[0][1] = 800;
                        option_param[2][1] = 15;
                        break;
                    case 557: //áo thần namếc
                        option_param[2][0] = 21; //yêu cầu sức mạnh

                        option_param[0][1] = 800;
                        option_param[2][1] = 15;
                        break;
                    case 559: //áo thần xayda
                        option_param[2][0] = 21; //yêu cầu sức mạnh

                        option_param[0][1] = 800;
                        option_param[2][1] = 15;
                        break;
                }
                break;
            case 1: //quần
                option_param[0][0] = 6; //hp
                option_param[1][0] = 27; //hp hồi/30s
                switch (tempId) {
                    case 6:
                        option_param[0][1] = 30;
                        break;
                    case 35:
                        option_param[0][1] = 150;
                        option_param[1][1] = 12;
                        break;
                    case 9:
                        option_param[0][1] = 300;
                        option_param[1][1] = 40;
                        break;
                    case 36:
                        option_param[0][1] = 600;
                        option_param[1][1] = 120;
                        break;
                    case 140:
                        option_param[0][1] = 1400;
                        option_param[1][1] = 280;
                        break;
                    case 141:
                        option_param[0][1] = 3000;
                        option_param[1][1] = 600;
                        break;
                    case 142:
                        option_param[0][1] = 6000;
                        option_param[1][1] = 1200;
                        break;
                    case 143:
                        option_param[0][1] = 10000;
                        option_param[1][1] = 2000;
                        break;
                    case 242:
                        option_param[0][1] = 14000;
                        option_param[1][1] = 2500;
                        break;
                    case 243:
                        option_param[0][1] = 18000;
                        option_param[1][1] = 3000;
                        break;
                    case 244:
                        option_param[0][1] = 22000;
                        option_param[1][1] = 3500;
                        break;
                    case 245:
                        option_param[0][1] = 26000;
                        option_param[1][1] = 4000;
                        break;
                    case 7:
                        option_param[0][1] = 20;
                        break;
                    case 43:
                        option_param[0][1] = 25;
                        option_param[1][1] = 10;
                        break;
                    case 10:
                        option_param[0][1] = 120;
                        option_param[1][1] = 28;
                        break;
                    case 44:
                        option_param[0][1] = 250;
                        option_param[1][1] = 100;
                        break;
                    case 156:
                        option_param[0][1] = 600;
                        option_param[1][1] = 240;
                        break;
                    case 157:
                        option_param[0][1] = 1200;
                        option_param[1][1] = 480;
                        break;
                    case 158:
                        option_param[0][1] = 2400;
                        option_param[1][1] = 960;
                        break;
                    case 159:
                        option_param[0][1] = 4800;
                        option_param[1][1] = 1800;
                        break;
                    case 246:
                        option_param[0][1] = 13000;
                        option_param[1][1] = 2200;
                        break;
                    case 247:
                        option_param[0][1] = 17000;
                        option_param[1][1] = 2700;
                        break;
                    case 248:
                        option_param[0][1] = 21000;
                        option_param[1][1] = 3200;
                        break;
                    case 249:
                        option_param[0][1] = 25000;
                        option_param[1][1] = 3700;
                        break;
                    case 8:
                        option_param[0][1] = 20;
                        break;
                    case 51:
                        option_param[0][1] = 20;
                        option_param[1][1] = 8;
                        break;
                    case 11:
                        option_param[0][1] = 100;
                        option_param[1][1] = 20;
                        break;
                    case 52:
                        option_param[0][1] = 200;
                        option_param[1][1] = 80;
                        break;
                    case 172:
                        option_param[0][1] = 500;
                        option_param[1][1] = 200;
                        break;
                    case 173:
                        option_param[0][1] = 1000;
                        option_param[1][1] = 400;
                        break;
                    case 174:
                        option_param[0][1] = 2000;
                        option_param[1][1] = 800;
                        break;
                    case 175:
                        option_param[0][1] = 4000;
                        option_param[1][1] = 1600;
                        break;
                    case 250:
                        option_param[0][1] = 12000;
                        option_param[1][1] = 2100;
                        break;
                    case 251:
                        option_param[0][1] = 16000;
                        option_param[1][1] = 2600;
                        break;
                    case 252:
                        option_param[0][1] = 20000;
                        option_param[1][1] = 3100;
                        break;
                    case 253:
                        option_param[0][1] = 24000;
                        option_param[1][1] = 3600;
                        break;
                    case 556: //quần thần trái đất
                        option_param[0][0] = 22; //hp
                        option_param[2][0] = 21; //yêu cầu sức mạnh

                        option_param[0][1] = 52;
                        option_param[1][1] = 10000;
                        option_param[2][1] = 15;
                        break;
                    case 558: //quần thần namếc
                        option_param[0][0] = 22; //hp
                        option_param[2][0] = 21; //yêu cầu sức mạnh

                        option_param[0][1] = 50;
                        option_param[1][1] = 10000;
                        option_param[2][1] = 15;
                        break;
                    case 560: //quần thần xayda
                        option_param[0][0] = 22; //hp
                        option_param[2][0] = 21; //yêu cầu sức mạnh

                        option_param[0][1] = 48;
                        option_param[1][1] = 10000;
                        option_param[2][1] = 15;
                        break;
                }
                break;
            case 2: //găng
                option_param[0][0] = 0; //sđ
                switch (tempId) {
                    case 21:
                        option_param[0][1] = 4;
                        break;
                    case 24:
                        option_param[0][1] = 7;
                        break;
                    case 37:
                        option_param[0][1] = 14;
                        break;
                    case 38:
                        option_param[0][1] = 28;
                        break;
                    case 144:
                        option_param[0][1] = 55;
                        break;
                    case 145:
                        option_param[0][1] = 110;
                        break;
                    case 146:
                        option_param[0][1] = 220;
                        break;
                    case 147:
                        option_param[0][1] = 530;
                        break;
                    case 254:
                        option_param[0][1] = 680;
                        break;
                    case 255:
                        option_param[0][1] = 1000;
                        break;
                    case 256:
                        option_param[0][1] = 1500;
                        break;
                    case 257:
                        option_param[0][1] = 2200;
                        break;
                    case 22:
                        option_param[0][1] = 3;
                        break;
                    case 46:
                        option_param[0][1] = 6;
                        break;
                    case 25:
                        option_param[0][1] = 12;
                        break;
                    case 45:
                        option_param[0][1] = 24;
                        break;
                    case 160:
                        option_param[0][1] = 50;
                        break;
                    case 161:
                        option_param[0][1] = 100;
                        break;
                    case 162:
                        option_param[0][1] = 200;
                        break;
                    case 163:
                        option_param[0][1] = 500;
                        break;
                    case 258:
                        option_param[0][1] = 630;
                        break;
                    case 259:
                        option_param[0][1] = 950;
                        break;
                    case 260:
                        option_param[0][1] = 1450;
                        break;
                    case 261:
                        option_param[0][1] = 2150;
                        break;
                    case 23:
                        option_param[0][1] = 5;
                        break;
                    case 53:
                        option_param[0][1] = 8;
                        break;
                    case 26:
                        option_param[0][1] = 16;
                        break;
                    case 54:
                        option_param[0][1] = 32;
                        break;
                    case 176:
                        option_param[0][1] = 60;
                        break;
                    case 177:
                        option_param[0][1] = 120;
                        break;
                    case 178:
                        option_param[0][1] = 240;
                        break;
                    case 179:
                        option_param[0][1] = 560;
                        break;
                    case 262:
                        option_param[0][1] = 700;
                        break;
                    case 263:
                        option_param[0][1] = 1050;
                        break;
                    case 264:
                        option_param[0][1] = 1550;
                        break;
                    case 265:
                        option_param[0][1] = 2250;
                        break;
                    case 562: //găng thần trái đất
                        option_param[2][0] = 21; //yêu cầu sức mạnh

                        option_param[0][1] = 3700;
                        option_param[2][1] = 17;
                        break;
                    case 564: //găng thần namếc
                        option_param[2][0] = 21; //yêu cầu sức mạnh

                        option_param[0][1] = 3500;
                        option_param[2][1] = 17;
                        break;
                    case 566: //găng thần xayda
                        option_param[2][0] = 21; //yêu cầu sức mạnh

                        option_param[0][1] = 3800;
                        option_param[2][1] = 17;
                        break;
                }
                break;
            case 3: //giày
                option_param[0][0] = 7; //ki
                option_param[1][0] = 28; //ki hồi /30s
                switch (tempId) {
                    case 27:
                        option_param[0][1] = 10;
                        break;
                    case 30:
                        option_param[0][1] = 25;
                        option_param[1][1] = 5;
                        break;
                    case 39:
                        option_param[0][1] = 120;
                        option_param[1][1] = 24;
                        break;
                    case 40:
                        option_param[0][1] = 250;
                        option_param[1][1] = 50;
                        break;
                    case 148:
                        option_param[0][1] = 500;
                        option_param[1][1] = 100;
                        break;
                    case 149:
                        option_param[0][1] = 1200;
                        option_param[1][1] = 240;
                        break;
                    case 150:
                        option_param[0][1] = 2400;
                        option_param[1][1] = 480;
                        break;
                    case 151:
                        option_param[0][1] = 5000;
                        option_param[1][1] = 1000;
                        break;
                    case 266:
                        option_param[0][1] = 9000;
                        option_param[1][1] = 1500;
                        break;
                    case 267:
                        option_param[0][1] = 14000;
                        option_param[1][1] = 2000;
                        break;
                    case 268:
                        option_param[0][1] = 19000;
                        option_param[1][1] = 2500;
                        break;
                    case 269:
                        option_param[0][1] = 24000;
                        option_param[1][1] = 3000;
                        break;
                    case 28:
                        option_param[0][1] = 15;
                        break;
                    case 47:
                        option_param[0][1] = 30;
                        option_param[1][1] = 6;
                        break;
                    case 31:
                        option_param[0][1] = 150;
                        option_param[1][1] = 30;
                        break;
                    case 48:
                        option_param[0][1] = 300;
                        option_param[1][1] = 60;
                        break;
                    case 164:
                        option_param[0][1] = 600;
                        option_param[1][1] = 120;
                        break;
                    case 165:
                        option_param[0][1] = 1500;
                        option_param[1][1] = 300;
                        break;
                    case 166:
                        option_param[0][1] = 3000;
                        option_param[1][1] = 600;
                        break;
                    case 167:
                        option_param[0][1] = 6000;
                        option_param[1][1] = 1200;
                        break;
                    case 270:
                        option_param[0][1] = 10000;
                        option_param[1][1] = 1700;
                        break;
                    case 271:
                        option_param[0][1] = 15000;
                        option_param[1][1] = 2200;
                        break;
                    case 272:
                        option_param[0][1] = 20000;
                        option_param[1][1] = 2700;
                        break;
                    case 273:
                        option_param[0][1] = 25000;
                        option_param[1][1] = 3200;
                        break;
                    case 29:
                        option_param[0][1] = 10;
                        break;
                    case 55:
                        option_param[0][1] = 20;
                        option_param[1][1] = 4;
                        break;
                    case 32:
                        option_param[0][1] = 100;
                        option_param[1][1] = 20;
                        break;
                    case 56:
                        option_param[0][1] = 200;
                        option_param[1][1] = 40;
                        break;
                    case 180:
                        option_param[0][1] = 400;
                        option_param[1][1] = 80;
                        break;
                    case 181:
                        option_param[0][1] = 1000;
                        option_param[1][1] = 200;
                        break;
                    case 182:
                        option_param[0][1] = 2000;
                        option_param[1][1] = 400;
                        break;
                    case 183:
                        option_param[0][1] = 4000;
                        option_param[1][1] = 800;
                        break;
                    case 274:
                        option_param[0][1] = 8000;
                        option_param[1][1] = 1300;
                        break;
                    case 275:
                        option_param[0][1] = 13000;
                        option_param[1][1] = 1800;
                        break;
                    case 276:
                        option_param[0][1] = 18000;
                        option_param[1][1] = 2300;
                        break;
                    case 277:
                        option_param[0][1] = 23000;
                        option_param[1][1] = 2800;
                        break;
                    case 563: //giày thần trái đất
                        option_param[0][0] = 23;
                        option_param[2][0] = 21; //yêu cầu sức mạnh

                        option_param[0][1] = 48;
                        option_param[1][1] = 10000;
                        option_param[2][1] = 14;
                        break;
                    case 565: //giày thần namếc
                        option_param[0][0] = 23;
                        option_param[2][0] = 21; //yêu cầu sức mạnh

                        option_param[0][1] = 48;
                        option_param[1][1] = 10000;
                        option_param[2][1] = 14;
                        break;
                    case 567: //giày thần xayda
                        option_param[0][0] = 23;
                        option_param[2][0] = 21; //yêu cầu sức mạnh

                        option_param[0][1] = 46;
                        option_param[1][1] = 10000;
                        option_param[2][1] = 14;
                        break;
                }
                break;
            case 4: //rada
                option_param[0][0] = 14; //crit
                switch (tempId) {
                    case 12:
                        option_param[0][1] = 1;
                        break;
                    case 57:
                        option_param[0][1] = 2;
                        break;
                    case 58:
                        option_param[0][1] = 3;
                        break;
                    case 59:
                        option_param[0][1] = 4;
                        break;
                    case 184:
                        option_param[0][1] = 5;
                        break;
                    case 185:
                        option_param[0][1] = 6;
                        break;
                    case 186:
                        option_param[0][1] = 7;
                        break;
                    case 187:
                        option_param[0][1] = 8;
                        break;
                    case 278:
                        option_param[0][1] = 9;
                        break;
                    case 279:
                        option_param[0][1] = 10;
                        break;
                    case 280:
                        option_param[0][1] = 11;
                        break;
                    case 281:
                        option_param[0][1] = 12;
                        break;
                    case 561: //nhẫn thần linh
                        option_param[2][0] = 21; //yêu cầu sức mạnh

                        option_param[0][1] = 15;
                        option_param[2][1] = 18;
                        break;
                }
                break;
        }

        for (int i = 0; i < option_param.length; i++) {
            if (option_param[i][0] != -1 && option_param[i][1] != -1) {
                list.add(new Item.ItemOption(option_param[i][0], (option_param[i][1] + Util.nextInt(-(option_param[i][1] * 10 / 100), option_param[i][1] * 10 / 100))));
            }
        }
    }

    private void initBaseOptionSaoPhaLe(ItemMap item) {
        int optionId = -1;
        switch (item.itemTemplate.id) {
            case 441: //hút máu
                optionId = 95;
                break;
            case 442: //hút ki
                optionId = 96;
                break;
            case 443: //phản sát thương
                optionId = 97;
                break;
            case 444:
                break;
            case 445:
                break;
            case 446: //vàng
                optionId = 100;
                break;
            case 447: //tnsm
                optionId = 101;
                break;
        }
        item.options.add(new Item.ItemOption(optionId, 5));
    }

    public void initBaseOptionSaoPhaLe(Item item) {
        int optionId = -1;
        int param = 5;
        switch (item.template.id) {
            case 441: //hút máu
                optionId = 95;
                break;
            case 442: //hút ki
                optionId = 96;
                break;
            case 443: //phản sát thương
                optionId = 97;
                break;
            case 444:
                param = 3;
                optionId = 98;
                break;
            case 445:
                param = 3;
                optionId = 99;
                break;
            case 446: //vàng
                optionId = 100;
                break;
            case 447: //tnsm
                optionId = 101;
                break;
        }
        if (optionId != -1) {
            item.itemOptions.add(new Item.ItemOption(optionId, param));
        }
    }

    //sao pha lê
    public void initStarOption(ItemMap item, RatioStar[] ratioStars) {
        RatioStar ratioStar = ratioStars[Util.nextInt(0, ratioStars.length - 1)];
        if (Util.isTrue(ratioStar.ratio, ratioStar.typeRatio)) {
            item.options.add(new Item.ItemOption(107, ratioStar.numStar));
        }
    }

    public void initStarOption(Item item, RatioStar[] ratioStars) {
        RatioStar ratioStar = ratioStars[Util.nextInt(0, ratioStars.length - 1)];
        if (Util.isTrue(ratioStar.ratio, ratioStar.typeRatio)) {
            item.itemOptions.add(new Item.ItemOption(107, ratioStar.numStar));
        }
    }

    //vật phẩm sự kiện
    private void initEventOption(ItemMap item) {
        switch (item.itemTemplate.id) {
            case 2013:
                item.options.add(new Item.ItemOption(74, 0));
                break;
            case 2014:
                item.options.add(new Item.ItemOption(74, 0));
                break;
            case 2015:
                item.options.add(new Item.ItemOption(74, 0));
                break;
        }
    }

    //hạn sử dụng
    private void initExpiryDateOption(ItemMap item) {

    }

    //vật phẩm không thể giao dịch
    private void initNotTradeOption(ItemMap item) {
        switch (item.itemTemplate.id) {
            case 2009:
                item.options.add(new Item.ItemOption(30, 0));
                break;

        }
    }

    //vật phẩm ký gửi
    private void initDepositOption(ItemMap item) {

    }

    //set kích hoạt
    public void initActivationOption(int gender, int type, List<Item.ItemOption> list) {
        if (type <= 4) {
            int[] idOption = ACTIVATION_SET[gender][Util.nextInt(0, 2)];
            list.add(new Item.ItemOption(idOption[0], 1)); //tên set
            list.add(new Item.ItemOption(idOption[1], 1)); //hiệu ứng set
            list.add(new Item.ItemOption(30, 7)); //không thể giao dịch
        }
    }

    private byte getMaxStarOfItemReward(ItemMap itemMap) {
        switch (itemMap.itemTemplate.id) {
            case 232:
            case 233:
            case 244:
            case 245:
            case 256:
            case 257:
            case 268:
            case 269:
            case 280:
            case 281:
            case 236:
            case 237:
            case 248:
            case 249:
            case 260:
            case 261:
            case 272:
            case 273:
            case 240:
            case 241:
            case 252:
            case 253:
            case 264:
            case 265:
            case 276:
            case 277:
                // đồ thần
            case 555:
            case 556:
            case 562:
            case 563:
            case 557:
            case 558:
            case 564:
            case 565:
            case 559:
            case 560:
            case 566:
            case 567:
            case 561:
                return 7;
            default:
                return 3;
        }
    }


    public void rewardFirstTimeLoginPerDay(Player player) {
        if (Util.compareDay(Date.from(Instant.now()), player.firstTimeLogin)) {
            Item item = ItemService.gI().createNewItem((short) 649);
            item.quantity = 1;
            item.itemOptions.add(new Item.ItemOption(74, 0));
            item.itemOptions.add(new Item.ItemOption(30, 0));
            InventoryService.gI().addItemBag(player, item);
            Service.gI().sendThongBao(player, "Quà đăng nhập hàng ngày: \nBạn nhận được " + item.template.name + " số lượng : " + item.quantity);
            player.firstTimeLogin = Date.from(Instant.now());
        }
    }
}
