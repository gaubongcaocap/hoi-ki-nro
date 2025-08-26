package npc.npc_manifest;

import consts.ConstNpc;
import event.event_manifest.LunarNewYear;
import item.Item;
import npc.Npc;
import player.Player;
import services.InventoryService;
public class CayNeu extends Npc {

    private static final int DOI_THUONG_MENU = 1;
    private static final int DOI_VIP_MENU = 2;

    public CayNeu(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            if (this.mapId == 0) {
                // Nếu không chờ, hiển thị menu trang trí bình thường
                this.createOtherMenu(player, ConstNpc.BASE_MENU, "|2|Bạn Muốn Trang Trì Cây Nêu Phải không", "Trang Trí Thường", "Trang Trí VIP");
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            // Kiểm tra nếu mapId hợp lệ (0, 7, hoặc 14)
            if (this.mapId == 0 || this.mapId == 7 || this.mapId == 14) {
                // Kiểm tra xem người chơi có menu cơ bản hay không
                if (player.iDMark.isBaseMenu()) {
                    switch (select) {
                        case 0: 
                            xuLiTrangTriThuong(player);
                            break;
                        case 1: 
                            xuLiTrangTriVIP(player);
                            break;
                    }
                } 
                else if (player.iDMark.getIndexMenu() == DOI_THUONG_MENU) {
                    if (select == 0) {
                        LunarNewYear.gI().cayNeuQuaThuong(player); // Phần thưởng khi trang trí thường
                    }
                } 
                else if (player.iDMark.getIndexMenu() == DOI_VIP_MENU) {
                    if (select == 0) {
                        LunarNewYear.gI().cayNeuQuaVip(player); // Phần thưởng khi trang trí VIP
                    }
                }
            }
        }
    }

    
    private void xuLiTrangTriThuong(Player player) {
  
        Item DayPhao = InventoryService.gI().findItemBag(player, 1472); // Dây pháo
        Item CauDoi = InventoryService.gI().findItemBag(player, 1473); // Câu đối
        Item DayTreoBanh = InventoryService.gI().findItemBag(player, 1474); // Dây treo bánh
        Item DenLongTreoCay = InventoryService.gI().findItemBag(player, 1475); // Lồng đèn treo cây

        int xDayPhao = checkItemQuantity(DayPhao, 2);
        int xCauDoi = checkItemQuantity(CauDoi, 4);
        int xDayTreoBanh = checkItemQuantity(DayTreoBanh, 20);
        int xDenLongTreoCay = checkItemQuantity(DenLongTreoCay, 1);

        long goldRequired = 5000000; // 5 triệu vàng
        boolean isGoldSufficient = player.inventory.gold >= goldRequired;

        boolean isMissingItems = (xDayPhao == 7 || xCauDoi == 7 || xDayTreoBanh == 7 || xDenLongTreoCay == 7);

        if (!isGoldSufficient || isMissingItems) {
            taoMenuVatPhamThieu(player, DayPhao, CauDoi, DayTreoBanh, DenLongTreoCay, xDayPhao, xCauDoi, xDayTreoBanh, xDenLongTreoCay);
        } else {
            taoMenuVatPhamDayDu(player, DayPhao, CauDoi, DayTreoBanh, DenLongTreoCay, xDayPhao, xCauDoi, xDayTreoBanh, xDenLongTreoCay);
        }
    }

    private void taoMenuVatPhamThieu(Player player, Item DayPhao, Item CauDoi, Item DayTreoBanh, Item DenLongTreoCay, int xDayPhao, int xCauDoi, int xDayTreoBanh, int xDenLongTreoCay) {
        long goldRequired = 5000000;
        long currentGold = player.inventory.gold; 
        String currentGoldFormatted = formatGold(currentGold);
        String goldRequiredFormatted = formatGold(goldRequired);
        int x = (currentGold < goldRequired) ? 7 : 2;
        String menuContent = "|1|Để trang trí thường cần:\n"
                + "|" + xDayPhao + "|" + (DayPhao != null ? DayPhao.quantity : 0) + "/2 dây pháo\n"
                + "|" + xCauDoi + "|" + (CauDoi != null ? CauDoi.quantity : 0) + "/4 câu đối\n"
                + "|" + xDayTreoBanh + "|" + (DayTreoBanh != null ? DayTreoBanh.quantity : 0) + "/20 dây treo bánh\n"
                + "|" + xDenLongTreoCay + "|" + (DenLongTreoCay != null ? DenLongTreoCay.quantity : 0) + "/1 lồng đèn treo cây\n"
                + "|x|Số vàng hiện có: " + currentGoldFormatted + "/" + goldRequiredFormatted;

        menuContent = menuContent.replace("|x|", "|" + x + "|");

        this.createOtherMenu(player, 14, menuContent, "Đóng");
    }

    private void taoMenuVatPhamDayDu(Player player, Item DayPhao, Item CauDoi, Item DayTreoBanh, Item DenLongTreoCay, int xDayPhao, int xCauDoi, int xDayTreoBanh, int xDenLongTreoCay) {
        long goldRequired = 5000000; 
        long currentGold = player.inventory.gold;
        String currentGoldFormatted = formatGold(currentGold);
        String goldRequiredFormatted = formatGold(goldRequired);
        int x = (currentGold < goldRequired) ? 7 : 2;
        String menuContent = "|1|Để trang trí thường cần:\n"
                + "|" + xDayPhao + "|" + (DayPhao != null ? DayPhao.quantity : 0) + "/2 dây pháo\n"
                + "|" + xCauDoi + "|" + (CauDoi != null ? CauDoi.quantity : 0) + "/4 câu đối\n"
                + "|" + xDayTreoBanh + "|" + (DayTreoBanh != null ? DayTreoBanh.quantity : 0) + "/20 dây treo bánh\n"
                + "|" + xDenLongTreoCay + "|" + (DenLongTreoCay != null ? DenLongTreoCay.quantity : 0) + "/1 lồng đèn treo cây\n"
                + "|x|Số vàng hiện có: " + currentGoldFormatted + "/" + goldRequiredFormatted;

        // Thay thế |x| bằng giá trị của x
        menuContent = menuContent.replace("|x|", "|" + x + "|");

        this.createOtherMenu(player, DOI_THUONG_MENU, menuContent, "Trang Trí Thường");
    }

    private void xuLiTrangTriVIP(Player player) {
        // Kiểm tra vật phẩm
        Item DayPhao = InventoryService.gI().findItemBag(player, 1472); // Dây pháo
        Item CauDoi = InventoryService.gI().findItemBag(player, 1473); // Câu đối
        Item DayTreoBanh = InventoryService.gI().findItemBag(player, 1474); // Dây treo bánh
        Item DenLongTreoCay = InventoryService.gI().findItemBag(player, 1475); // Lồng đèn treo cây

        // Kiểm tra đủ vật phẩm
        int xDayPhao = checkItemQuantity(DayPhao, 2);
        int xCauDoi = checkItemQuantity(CauDoi, 4);
        int xDayTreoBanh = checkItemQuantity(DayTreoBanh, 20);
        int xDenLongTreoCay = checkItemQuantity(DenLongTreoCay, 1);

        // Kiểm tra số vàng
        long goldRequired = 5000000; // 5 triệu vàng
        boolean isGoldSufficient = player.inventory.gold >= goldRequired;

        // Kiểm tra số gem
        int requiredGems = 50; // Cần ít nhất 5 gem
        boolean isGemsSufficient = player.inventory.getGem() >= requiredGems;

        // Kiểm tra nếu thiếu vật phẩm, vàng hoặc gem
        boolean isMissingItems = (xDayPhao == 7 || xCauDoi == 7 || xDayTreoBanh == 7 || xDenLongTreoCay == 7);
        boolean isMissingGems = !isGemsSufficient;

        if (!isGoldSufficient || isMissingItems || isMissingGems) {
            // Nếu thiếu vật phẩm, thiếu vàng hoặc thiếu gem, tạo menu thông báo thiếu
            taoMenuVatPhamThieu1(player, DayPhao, CauDoi, DayTreoBanh, DenLongTreoCay, xDayPhao, xCauDoi, xDayTreoBanh, xDenLongTreoCay);
        } else {
            // Nếu đủ cả vật phẩm, vàng và gem, tạo menu đầy đủ
            taoMenuVatPhamDayDu1(player, DayPhao, CauDoi, DayTreoBanh, DenLongTreoCay, xDayPhao, xCauDoi, xDayTreoBanh, xDenLongTreoCay);
        }
    }

    private int checkItemQuantity(Item item, int requiredQuantity) {
        return (item != null && item.quantity >= requiredQuantity) ? 2 : 7;
    }

    private String formatGold(long gold) {
        if (gold >= 1000000000) { // Nếu vàng lớn hơn hoặc bằng tỷ
            return gold / 1000000000 + " tỷ";
        } else if (gold >= 1000000) { // Nếu vàng lớn hơn hoặc bằng triệu
            return gold / 1000000 + " triệu";
        } else {
            return String.valueOf(gold); // Nếu vàng nhỏ hơn triệu
        }
    }

    private void taoMenuVatPhamThieu1(Player player, Item DayPhao, Item CauDoi, Item DayTreoBanh, Item DenLongTreoCay, int xDayPhao, int xCauDoi, int xDayTreoBanh, int xDenLongTreoCay) {
        long goldRequired = 5000000; // 5 triệu vàng
        long currentGold = player.inventory.gold; // Số vàng hiện tại của người chơi
        int currentGems = player.inventory.getGem(); // Số ngọc hiện tại của người chơi

        // Chia số vàng hiện có thành triệu hoặc tỷ
        String currentGoldFormatted = formatGold(currentGold);
        String goldRequiredFormatted = formatGold(goldRequired);

        // Kiểm tra nếu không đủ vàng, gán x = 7 (màu đỏ)
        int xGold = (currentGold < goldRequired) ? 7 : 2;

        // Số ngọc yêu cầu
        int requiredGems = 50; // Cần ít nhất 5 ngọc
        int xGems = (currentGems < requiredGems) ? 7 : 2; // Kiểm tra số ngọc

        // Tạo menu với số vàng, ngọc hiện có và số vàng, ngọc yêu cầu
        String menuContent = "|1|Để trang trí [VIP] cần:\n"
                + "|" + xDayPhao + "|" + (DayPhao != null ? DayPhao.quantity : 0) + "/2 dây pháo\n"
                + "|" + xCauDoi + "|" + (CauDoi != null ? CauDoi.quantity : 0) + "/4 câu đối\n"
                + "|" + xDayTreoBanh + "|" + (DayTreoBanh != null ? DayTreoBanh.quantity : 0) + "/20 dây treo bánh\n"
                + "|" + xDenLongTreoCay + "|" + (DenLongTreoCay != null ? DenLongTreoCay.quantity : 0) + "/1 lồng đèn treo cây\n"
                + "|xGold|Số vàng hiện có: " + currentGoldFormatted + "/" + goldRequiredFormatted + "\n"
                + "|xGems|Số ngọc hiện có: " + currentGems + "/50";

        // Thay thế |xGold| và |xGems| bằng giá trị của xGold và xGems
        menuContent = menuContent.replace("|xGold|", "|" + xGold + "|");
        menuContent = menuContent.replace("|xGems|", "|" + xGems + "|");

        // Tạo menu
        this.createOtherMenu(player, 14, menuContent, "Đóng");
    }

    private void taoMenuVatPhamDayDu1(Player player, Item DayPhao, Item CauDoi, Item DayTreoBanh, Item DenLongTreoCay, int xDayPhao, int xCauDoi, int xDayTreoBanh, int xDenLongTreoCay) {
        long goldRequired = 5000000; // 5 triệu vàng
        long currentGold = player.inventory.gold; // Số vàng hiện tại của người chơi
        int currentGems = player.inventory.getGem(); // Số ngọc hiện tại của người chơi

        // Chia số vàng hiện có thành triệu hoặc tỷ
        String currentGoldFormatted = formatGold(currentGold);
        String goldRequiredFormatted = formatGold(goldRequired);

        // Kiểm tra nếu không đủ vàng, gán x = 7 (màu đỏ)
        int xGold = (currentGold < goldRequired) ? 7 : 2;

        // Kiểm tra số ngọc
        int requiredGems = 50; // Cần ít nhất 5 ngọc
        int xGems = (currentGems < requiredGems) ? 7 : 2; // Kiểm tra số ngọc

        // Tạo menu với số vàng, ngọc hiện có và số vàng, ngọc yêu cầu
        String menuContent = "|1|Để trang trí [VIP] cần:\n"
                + "|" + xDayPhao + "|" + (DayPhao != null ? DayPhao.quantity : 0) + "/2 dây pháo\n"
                + "|" + xCauDoi + "|" + (CauDoi != null ? CauDoi.quantity : 0) + "/4 câu đối\n"
                + "|" + xDayTreoBanh + "|" + (DayTreoBanh != null ? DayTreoBanh.quantity : 0) + "/20 dây treo bánh\n"
                + "|" + xDenLongTreoCay + "|" + (DenLongTreoCay != null ? DenLongTreoCay.quantity : 0) + "/1 lồng đèn treo cây\n"
                + "|xGold|Số vàng hiện có: " + currentGoldFormatted + "/" + goldRequiredFormatted + "\n"
                + "|xGems|Số ngọc hiện có: " + currentGems + "/50";

        // Thay thế |xGold| và |xGems| bằng giá trị của xGold và xGems
        menuContent = menuContent.replace("|xGold|", "|" + xGold + "|");
        menuContent = menuContent.replace("|xGems|", "|" + xGems + "|");

        // Tạo menu
        this.createOtherMenu(player, DOI_VIP_MENU, menuContent, "Trang Trí Vip");
    }

}
