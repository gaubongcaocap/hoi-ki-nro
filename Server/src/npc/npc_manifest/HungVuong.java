/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package npc.npc_manifest;

import consts.ConstNpc;
import consts.ConstPlayer;
import item.Item;
import java.util.Calendar;
import npc.Npc;
import npc.specialnpc.Timedua;
import player.Player;
import services.InventoryService;
import services.ItemService;
import services.NpcService;
import services.Service;
import services.TaskService;
import shop.ShopService;

public class HungVuong extends Npc {

    public HungVuong(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (!canOpenNpc(player)) {
            return;
        }

        if (mapId == 19) {
            // Trồng dưa hấu
            if (!isSameDay(player.lastPlantTime, System.currentTimeMillis())) {
                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                        "Hãy trồng dưa hấu và mang quà đến gặp ta đổi quà",
                        "Trồng\ndưa hấu");
            } else {
                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                        "Bạn đã trồng dưa hấu hôm nay rồi!\nMai hãy quay lại tiếp nhé.",
                        "Đóng");
            }
        }

       if (mapId == 0 || mapId == 7 || mapId == 14) {
    this.createOtherMenu(player, ConstNpc.BASE_MENU,
        "Ngươi muốn dâng sính lễ nào?",
        "Ghép\nHộp Quà\nThường",
        "Ghép\nHộp Quà\nCao Cấp",
        "Đóng");
}

    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (!canOpenNpc(player)) {
            return;
        }
        if (player.iDMark.isBaseMenu()) {
            if (mapId == 19 && select == 0) {
                if (!isSameDay(player.lastPlantTime, System.currentTimeMillis())) {
                    Timedua.createTimedua(player);
                    player.lastPlantTime = System.currentTimeMillis();
                    Item item = ItemService.gI().createNewItem((short) 569); // Dưa hấu
                    InventoryService.gI().addItemBag(player, item);
                    InventoryService.gI().sendItemBag(player);
                    Service.gI().sendThongBao(player, "Bạn vừa nhận được " + item.template.name);
                } else {
                    Service.gI().sendThongBao(player, "Bạn đã trồng dưa hấu hôm nay rồi!");
                }
            }

            if ((mapId == 0 || mapId == 7 || mapId == 14)) {
                switch (select) {
                    case 0 -> { // Hộp Quà Thường
                        Item ngavoi = InventoryService.gI().findItemBag(player, 1220);
                        Item cuaga = InventoryService.gI().findItemBag(player, 1221);
                        Item hongmao = InventoryService.gI().findItemBag(player, 1222);

                        long gold = player.inventory.gold;

                        int countNgavoi = ngavoi != null ? ngavoi.quantity : 0;
                        int countCuaga = cuaga != null ? cuaga.quantity : 0;
                        int countHongmao = hongmao != null ? hongmao.quantity : 0;

                        String info = String.format(
                                "%sNgà voi: %d/9\n%sCựa gà: %d/9\n%sHồng mao: %d/9\n%sVàng: %d/1000000",
                                color(countNgavoi >= 9), countNgavoi,
                                color(countCuaga >= 9), countCuaga,
                                color(countHongmao >= 9), countHongmao,
                                color(gold >= 1_000_000), gold);

                        this.createOtherMenu(player, 100,
                                info,
                                "Đổi", "Đóng");
                    }

                    case 1 -> { // Hộp Quà Cao Cấp
                        Item ngavoi = InventoryService.gI().findItemBag(player, 1220);
                        Item cuaga = InventoryService.gI().findItemBag(player, 1221);
                        Item hongmao = InventoryService.gI().findItemBag(player, 1222);
                        int gem = player.inventory.gem;

                       int countNgavoi = ngavoi != null ? ngavoi.quantity : 0;
    int countCuaga = cuaga != null ? cuaga.quantity : 0;
    int countHongmao = hongmao != null ? hongmao.quantity : 0;

    String info = String.format(
            "%sNgà voi: %d/9\n%sCựa gà: %d/9\n%sHồng mao: %d/9\n%sNgọc: %d/10",
            color(countNgavoi >= 9), countNgavoi,
            color(countCuaga >= 9), countCuaga,
            color(countHongmao >= 9), countHongmao,
            color(gem >= 10), gem);

                        this.createOtherMenu(player, 101,
                                info,
                                "Đổi", "Đóng");
                    }
//                      case 2 -> showInfoBanhDay(player);
//        case 3 -> showInfoBanhChung(player);
                }
            }
        } else if (player.iDMark.getIndexMenu() == 100) {
            switch (select) {// Đổi hộp thường
                case 0:
                    tryGhepQuaThuong(player);
                    break;
            }

        } else if (player.iDMark.getIndexMenu() == 101) {
            switch (select) {
                case 0: // Đổi hộp cao cấp
                    tryGhepQuaCaoCap(player);
                    break;
            }
        } else if (player.iDMark.getIndexMenu() == 102) {
    if (select == 0) tryDoiBanhDay(player);
} else if (player.iDMark.getIndexMenu() == 103) {
    if (select == 0) tryDoiBanhChung(player);
}
    }
private String color(boolean condition) {
    return condition ? "|2|" : "|1|";
}
private void showInfoBanhDay(Player player) {
    Item nep = InventoryService.gI().findItemBag(player, 1546);       // Nếp
    Item botgao = InventoryService.gI().findItemBag(player, 1547);    // Bột Gạo
    Item muoitieu = InventoryService.gI().findItemBag(player, 1545);  // Muối Tiêu
    Item chalua = InventoryService.gI().findItemBag(player, 1544);    // Chả Lụa

    int nepCount = nep != null ? nep.quantity : 0;
    int botgaoCount = botgao != null ? botgao.quantity : 0;
    int muoitieuCount = muoitieu != null ? muoitieu.quantity : 0;
    int chaluaCount = chalua != null ? chalua.quantity : 0;

    long gold = player.inventory.gold;

    String info = String.format(
        "%sNếp: %d/99\n%sBột Gạo: %d/5\n%sMuối Tiêu: %d/2\n%sChả Lụa: %d/1\n%sVàng: %d/1000000",
        color(nepCount >= 99), nepCount,
        color(botgaoCount >= 5), botgaoCount,
        color(muoitieuCount >= 2), muoitieuCount,
        color(chaluaCount >= 1), chaluaCount,
        color(gold >= 1_000_000), gold
    );

    this.createOtherMenu(player, 102, info, "Đổi", "Đóng");
}
private void showInfoBanhChung(Player player) {
    Item nep = InventoryService.gI().findItemBag(player, 1546);         // Nếp
    Item dauxanh = InventoryService.gI().findItemBag(player, 1548);     // Đậu Xanh
    Item thittuoi = InventoryService.gI().findItemBag(player, 1549);    // Thịt Tươi

    int nepCount = nep != null ? nep.quantity : 0;
    int dauxanhCount = dauxanh != null ? dauxanh.quantity : 0;
    int thittuoiCount = thittuoi != null ? thittuoi.quantity : 0;

    long gold = player.inventory.gold;

    String info = String.format(
        "%sNếp: %d/99\n%sĐậu Xanh: %d/2\n%sThịt Tươi: %d/2\n%sVàng: %d/5000000",
        color(nepCount >= 99), nepCount,
        color(dauxanhCount >= 2), dauxanhCount,
        color(thittuoiCount >= 2), thittuoiCount,
        color(gold >= 5_000_000), gold
    );

    this.createOtherMenu(player, 103, info, "Đổi", "Đóng");
}
private void tryDoiBanhDay(Player player) {
    if (InventoryService.gI().getCountEmptyBag(player) <= 0) {
        Service.gI().sendThongBao(player, "Bạn cần ít nhất 1 ô trống trong hành trang");
        return;
    }

    Item nep = InventoryService.gI().findItemBag(player, 1546);       // Nếp
    Item botgao = InventoryService.gI().findItemBag(player, 1547);    // Bột Gạo
    Item muoitieu = InventoryService.gI().findItemBag(player, 1545);  // Muối Tiêu
    Item chalua = InventoryService.gI().findItemBag(player, 1544);    // Chả Lụa

    if (nep != null && botgao != null && muoitieu != null && chalua != null
        && nep.quantity >= 99
        && botgao.quantity >= 5
        && muoitieu.quantity >= 2
        && chalua.quantity >= 1
        && player.inventory.gold >= 1_000_000) {

        InventoryService.gI().subQuantityItemsBag(player, nep, 99);
        InventoryService.gI().subQuantityItemsBag(player, botgao, 5);
        InventoryService.gI().subQuantityItemsBag(player, muoitieu, 2);
        InventoryService.gI().subQuantityItemsBag(player, chalua, 1);
        player.inventory.gold -= 1_000_000;
        Service.gI().sendMoney(player);

        Item banh = ItemService.gI().createNewItem((short) 1542); // ID Bánh Dầy
        InventoryService.gI().addItemBag(player, banh);
        InventoryService.gI().sendItemBag(player);
        Service.gI().sendThongBao(player, "Bạn nhận được Bánh Dầy");
    } else {
        Service.gI().sendThongBao(player, "Không đủ nguyên liệu để đổi Bánh Dầy!");
    }
}
private void tryDoiBanhChung(Player player) {
    if (InventoryService.gI().getCountEmptyBag(player) <= 0) {
        Service.gI().sendThongBao(player, "Bạn cần ít nhất 1 ô trống trong hành trang");
        return;
    }

   Item nep = InventoryService.gI().findItemBag(player, 1546);         // Nếp
    Item dauxanh = InventoryService.gI().findItemBag(player, 1548);     // Đậu Xanh
    Item thittuoi = InventoryService.gI().findItemBag(player, 1549);    // Thịt Tươi

    if (nep != null && dauxanh != null && thittuoi != null
        && nep.quantity >= 99
        && dauxanh.quantity >= 2
        && thittuoi.quantity >= 2
        && player.inventory.gold >= 5_000_000) {

        InventoryService.gI().subQuantityItemsBag(player, nep, 99);
        InventoryService.gI().subQuantityItemsBag(player, dauxanh, 2);
        InventoryService.gI().subQuantityItemsBag(player, thittuoi, 2);
        player.inventory.gold -= 5_000_000;
        Service.gI().sendMoney(player);

        Item banh = ItemService.gI().createNewItem((short) 1556); // ID Bánh Chưng Lang Liêu
        InventoryService.gI().addItemBag(player, banh);
        InventoryService.gI().sendItemBag(player);
        Service.gI().sendThongBao(player, "Bạn nhận được Bánh Chưng Lang Liêu");
    } else {
        Service.gI().sendThongBao(player, "Không đủ nguyên liệu để đổi Bánh Chưng!");
    }
}

    private void tryGhepQuaThuong(Player player) {
        if (InventoryService.gI().getCountEmptyBag(player) <= 0) {
            Service.gI().sendThongBao(player, "Bạn cần ít nhất 1 ô trống trong hành trang để nhận phần thưởng");
            return;
        }
        Item ngavoi = InventoryService.gI().findItemBag(player, 1220);
        Item hongmao = InventoryService.gI().findItemBag(player, 1222);
        Item cuaga = InventoryService.gI().findItemBag(player, 1221);
        if (ngavoi != null
                && // Ngà voi
                hongmao != null
                && // Hồng mao
                cuaga != null
                && // Cựa gà
                player.inventory.gold >= 1_000_000) {

            if (ngavoi.quantity >= 9
                    && hongmao.quantity >= 9
                    && cuaga.quantity >= 9) {

                // Trừ nguyên liệu
                InventoryService.gI().subQuantityItemsBag(player, ngavoi, 9);
                InventoryService.gI().subQuantityItemsBag(player, hongmao, 9);
                InventoryService.gI().subQuantityItemsBag(player, cuaga, 9);
                player.inventory.gold -= 1_000_000;
                Service.gI().sendMoney(player);

                // Nhận hộp
                Item hopQua = ItemService.gI().createNewItem((short) 1823); // Hộp Quà Thường
                InventoryService.gI().addItemBag(player, hopQua);
                InventoryService.gI().sendItemBag(player);

                Service.gI().sendThongBao(player, "Bạn nhận được Hộp Quà Thường Giỗ Tổ 2025");
            } else {
                Service.gI().sendThongBao(player, "Bạn chưa đủ nguyên liệu để ghép hộp quà!");
            }
        } else {
            Service.gI().sendThongBao(player, "Thiếu nguyên liệu hoặc vàng!");
        }
    }

    private void tryGhepQuaCaoCap(Player player) {
        if (InventoryService.gI().getCountEmptyBag(player) <= 0) {
            Service.gI().sendThongBao(player, "Bạn cần ít nhất 1 ô trống trong hành trang để nhận phần thưởng");
            return;
        }

        Item ngavoi = InventoryService.gI().findItemBag(player, 1220);
        Item cuaga = InventoryService.gI().findItemBag(player, 1221);
        Item hongmao = InventoryService.gI().findItemBag(player, 1222);

        if (ngavoi != null && cuaga != null && hongmao != null
                && ngavoi.quantity >= 9
                && cuaga.quantity >= 9
                && hongmao.quantity >= 9
                && player.inventory.gem >= 10) { // Ngọc thường

            // Trừ nguyên liệu
            InventoryService.gI().subQuantityItemsBag(player, ngavoi, 9);
            InventoryService.gI().subQuantityItemsBag(player, cuaga, 9);
            InventoryService.gI().subQuantityItemsBag(player, hongmao, 9);
            player.inventory.gem -= 10;
            Service.gI().sendMoney(player);
            InventoryService.gI().sendItemBag(player);

            // Nhận hộp cao cấp
            Item hopQua = ItemService.gI().createNewItem((short) 1824); // Hộp Quà Cao Cấp
            InventoryService.gI().addItemBag(player, hopQua);
            InventoryService.gI().sendItemBag(player);

            Service.gI().sendThongBao(player, "Bạn nhận được Hộp Quà Cao Cấp Giỗ Tổ 2025");
        } else {
            Service.gI().sendThongBao(player, "Bạn chưa đủ nguyên liệu hoặc ngọc để ghép hộp cao cấp!");
        }
    }

    private boolean isSameDay(long time1, long time2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(time1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTimeInMillis(time2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

}
