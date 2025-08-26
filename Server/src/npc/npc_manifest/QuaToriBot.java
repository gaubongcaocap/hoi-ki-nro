/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package npc.npc_manifest;

import item.Item;
import player.Player;
import services.InventoryService;
import services.ItemService;

/**
 *
 * @author admin
 */
public class QuaToriBot {

    public static void Qua_1(Player player) {
        Item ThoiVang = ItemService.gI().createNewItem((short) 457, 12);
        Item PhieuGiamGia = ItemService.gI().createNewItem((short) 459, 10);
        Item DaBaoVe = ItemService.gI().createNewItem((short) 987, 5);
        Item CaiTrang = ItemService.gI().createNewItem((short) 1732, 1); // 1732 Black Goku | 1731 Black Goku Rose
        Item PhuKien = ItemService.gI().createNewItem((short) 1721, 1); // Cá Koi Zombie 1721  | Cánh 1722
        Item Pet = ItemService.gI().createNewItem((short) 1654, 1);  // Chó 1654 | Capybara 1629
        // Option ThoiVang
        ThoiVang.itemOptions.add(new Item.ItemOption(100, 1));

        // Option PhieuGiamGia
        PhieuGiamGia.itemOptions.add(new Item.ItemOption(73, 1));

        // Option DaBaoVe
        DaBaoVe.itemOptions.add(new Item.ItemOption(73, 1));

        // Option CaiTrang
        CaiTrang.itemOptions.add(new Item.ItemOption(50, 25));
        CaiTrang.itemOptions.add(new Item.ItemOption(103, 21));
        CaiTrang.itemOptions.add(new Item.ItemOption(5, 11));
        CaiTrang.itemOptions.add(new Item.ItemOption(14, 7));
        CaiTrang.itemOptions.add(new Item.ItemOption(97, 30));

        // Option PhuKien
        PhuKien.itemOptions.add(new Item.ItemOption(50, 5));
        PhuKien.itemOptions.add(new Item.ItemOption(77, 5));
        PhuKien.itemOptions.add(new Item.ItemOption(103, 5));
        PhuKien.itemOptions.add(new Item.ItemOption(101, 5));

        // Option Pet
        Pet.itemOptions.add(new Item.ItemOption(50, 5));
        Pet.itemOptions.add(new Item.ItemOption(77, 5));
        Pet.itemOptions.add(new Item.ItemOption(103, 5));
        Pet.itemOptions.add(new Item.ItemOption(101, 5));

        // Cập Nhật & Gửi Item Đến Balo Player
        InventoryService.gI().addItemBag(player, ThoiVang);
        InventoryService.gI().addItemBag(player, PhieuGiamGia);
        InventoryService.gI().addItemBag(player, DaBaoVe);
        InventoryService.gI().addItemBag(player, CaiTrang);
        InventoryService.gI().addItemBag(player, PhuKien);
        InventoryService.gI().addItemBag(player, Pet);
        InventoryService.gI().sendItemBag(player);

    }

    public static void Qua_2(Player player) {
        Item ThoiVang = ItemService.gI().createNewItem((short) 457, 24);
        Item PhieuGiamGia = ItemService.gI().createNewItem((short) 459, 10);
        Item DaBaoVe = ItemService.gI().createNewItem((short) 987, 10);
        Item CaiTrang = ItemService.gI().createNewItem((short) 1732, 1); // 1732 Black Goku | 1731 Black Goku Rose
        Item PhuKien = ItemService.gI().createNewItem((short) 1721, 1); // Cá Koi Zombie 1721  | Cánh 1722
        Item Pet = ItemService.gI().createNewItem((short) 1654, 1);  // Chó 1654 | Capybara 1629
        Item TheTieuDoiTruongVang = ItemService.gI().createNewItem((short) 1204, 10); // Đội Trưởng Vàng 957 | Namek 1204

        // Option ThoiVang
        ThoiVang.itemOptions.add(new Item.ItemOption(100, 1));

        // Option PhieuGiamGia
        PhieuGiamGia.itemOptions.add(new Item.ItemOption(73, 1));

        // Option DaBaoVe
        DaBaoVe.itemOptions.add(new Item.ItemOption(73, 1));

        // Option CaiTrang
        CaiTrang.itemOptions.add(new Item.ItemOption(50, 27));
        CaiTrang.itemOptions.add(new Item.ItemOption(103, 24));
        CaiTrang.itemOptions.add(new Item.ItemOption(5, 12));
        CaiTrang.itemOptions.add(new Item.ItemOption(14, 9));

        // Option PhuKien
        PhuKien.itemOptions.add(new Item.ItemOption(50, 5));
        PhuKien.itemOptions.add(new Item.ItemOption(77, 5));
        PhuKien.itemOptions.add(new Item.ItemOption(103, 5));
        PhuKien.itemOptions.add(new Item.ItemOption(101, 5));

        // Option Pet
        Pet.itemOptions.add(new Item.ItemOption(50, 5));
        Pet.itemOptions.add(new Item.ItemOption(77, 5));
        Pet.itemOptions.add(new Item.ItemOption(103, 5));
        Pet.itemOptions.add(new Item.ItemOption(101, 5));

        // Option TheDoiTruongVang
        TheTieuDoiTruongVang.itemOptions.add(new Item.ItemOption(73, 0));
        // Cập Nhật & Gửi Item Đến Balo Player
        InventoryService.gI().addItemBag(player, ThoiVang);
        InventoryService.gI().addItemBag(player, PhieuGiamGia);
        InventoryService.gI().addItemBag(player, DaBaoVe);
        InventoryService.gI().addItemBag(player, CaiTrang);
        InventoryService.gI().addItemBag(player, PhuKien);
        InventoryService.gI().addItemBag(player, Pet);
        InventoryService.gI().addItemBag(player, TheTieuDoiTruongVang);
        InventoryService.gI().sendItemBag(player);
    }

    public static void Qua_3(Player player) {
        Item ThoiVang = ItemService.gI().createNewItem((short) 457, 64);
        Item PhieuGiamGia = ItemService.gI().createNewItem((short) 459, 10);
        Item DaBaoVe = ItemService.gI().createNewItem((short) 987, 20);
        Item CaiTrang = ItemService.gI().createNewItem((short) 1731, 1); // 1732 Black Goku | 1731 Black Goku Rose
        Item PhuKien = ItemService.gI().createNewItem((short) 1722, 1); // Cá Koi Zombie 1721  | Cánh 1722
        Item Pet = ItemService.gI().createNewItem((short) 1629, 1);  // Chó 1654 | Capybara 1629
        Item TheTieuDoiTruongVang = ItemService.gI().createNewItem((short) 956, 10); // Đội Trưởng Vàng 957 | Namek 1204
//        Item CapsuleThanLinh = ItemService.gI().createNewItem((short) 1951, 1);

        // Option ThoiVang
        ThoiVang.itemOptions.add(new Item.ItemOption(100, 1));

        // Option PhieuGiamGia
        PhieuGiamGia.itemOptions.add(new Item.ItemOption(73, 1));

        // Option DaBaoVe
        DaBaoVe.itemOptions.add(new Item.ItemOption(73, 1));

        // Option CaiTrang
        CaiTrang.itemOptions.add(new Item.ItemOption(50, 30));
        CaiTrang.itemOptions.add(new Item.ItemOption(103, 27));
        CaiTrang.itemOptions.add(new Item.ItemOption(5, 15));
        CaiTrang.itemOptions.add(new Item.ItemOption(14, 11));
        CaiTrang.itemOptions.add(new Item.ItemOption(93, 30));

        // Option PhuKien
        PhuKien.itemOptions.add(new Item.ItemOption(50, 5));
        PhuKien.itemOptions.add(new Item.ItemOption(77, 5));
        PhuKien.itemOptions.add(new Item.ItemOption(103, 5));
        PhuKien.itemOptions.add(new Item.ItemOption(101, 5));

        // Option Pet
        Pet.itemOptions.add(new Item.ItemOption(50, 5));
        Pet.itemOptions.add(new Item.ItemOption(77, 5));
        Pet.itemOptions.add(new Item.ItemOption(103, 5));
        Pet.itemOptions.add(new Item.ItemOption(101, 5));

        // Option TheDoiTruongVang
        TheTieuDoiTruongVang.itemOptions.add(new Item.ItemOption(73, 0));
        // Cập Nhật & Gửi Item Đến Balo Player
        InventoryService.gI().addItemBag(player, ThoiVang);
        InventoryService.gI().addItemBag(player, PhieuGiamGia);
        InventoryService.gI().addItemBag(player, DaBaoVe);
        InventoryService.gI().addItemBag(player, CaiTrang);
        InventoryService.gI().addItemBag(player, PhuKien);
        InventoryService.gI().addItemBag(player, Pet);
        InventoryService.gI().addItemBag(player, TheTieuDoiTruongVang);
//        InventoryService.gI().addItemBag(player, CapsuleThanLinh);
        InventoryService.gI().sendItemBag(player);
    }

    public static void Qua_4(Player player) {
        Item ThoiVang = ItemService.gI().createNewItem((short) 457, 64);
        Item PhieuGiamGia = ItemService.gI().createNewItem((short) 459, 10);
        Item DaBaoVe = ItemService.gI().createNewItem((short) 987, 20);
        Item CaiTrang = ItemService.gI().createNewItem((short) 1731, 1); // 1732 Black Goku | 1731 Black Goku Rose
        Item PhuKien = ItemService.gI().createNewItem((short) 1722, 1); // Cá Koi Zombie 1721  | Cánh 1722
        Item Pet = ItemService.gI().createNewItem((short) 1629, 1);  // Chó 1654 | Capybara 1629
        Item TheTieuDoiTruongVang = ItemService.gI().createNewItem((short) 956, 20); // Đội Trưởng Vàng 956 | Namek 1204
//        Item CapsuleThanLinh = ItemService.gI().createNewItem((short) 1951, 1);
        Item TheRongThanNamek = ItemService.gI().createNewItem((short) 1204, 20);

        // Option ThoiVang
        ThoiVang.itemOptions.add(new Item.ItemOption(100, 1));

        // Option PhieuGiamGia
        PhieuGiamGia.itemOptions.add(new Item.ItemOption(73, 1));

        // Option DaBaoVe
        DaBaoVe.itemOptions.add(new Item.ItemOption(73, 1));

        // Option CaiTrang
        CaiTrang.itemOptions.add(new Item.ItemOption(50, 33));
        CaiTrang.itemOptions.add(new Item.ItemOption(103, 29));
        CaiTrang.itemOptions.add(new Item.ItemOption(5, 19));
        CaiTrang.itemOptions.add(new Item.ItemOption(14, 15));

        // Option PhuKien
        PhuKien.itemOptions.add(new Item.ItemOption(50, 5));
        PhuKien.itemOptions.add(new Item.ItemOption(77, 5));
        PhuKien.itemOptions.add(new Item.ItemOption(103, 5));
        PhuKien.itemOptions.add(new Item.ItemOption(101, 5));

        // Option Pet
        Pet.itemOptions.add(new Item.ItemOption(50, 5));
        Pet.itemOptions.add(new Item.ItemOption(77, 5));
        Pet.itemOptions.add(new Item.ItemOption(103, 5));
        Pet.itemOptions.add(new Item.ItemOption(101, 5));

        // Option TheDoiTruongVang
        TheTieuDoiTruongVang.itemOptions.add(new Item.ItemOption(73, 0));

        // Option TheRongThanNamek
        TheRongThanNamek.itemOptions.add(new Item.ItemOption(73, 0));

        // Cập Nhật & Gửi Item Đến Balo Player
        InventoryService.gI().addItemBag(player, ThoiVang);
        InventoryService.gI().addItemBag(player, PhieuGiamGia);
        InventoryService.gI().addItemBag(player, DaBaoVe);
        InventoryService.gI().addItemBag(player, CaiTrang);
        InventoryService.gI().addItemBag(player, PhuKien);
        InventoryService.gI().addItemBag(player, Pet);
        InventoryService.gI().addItemBag(player, TheTieuDoiTruongVang);
//        InventoryService.gI().addItemBag(player, CapsuleThanLinh);
        InventoryService.gI().addItemBag(player, TheRongThanNamek);
        InventoryService.gI().sendItemBag(player);
    }
}
