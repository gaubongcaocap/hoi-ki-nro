package npc.npc_manifest;

/**
 *
 * @author NTD
 */
import consts.ConstNpc;
import consts.ConstPlayer;
import item.Item;
import npc.Npc;
import player.Player;
import services.InventoryService;
import services.ItemService;
import services.NpcService;
import services.TaskService;
import shop.ShopService;

public class BulmaTho extends Npc {

    public BulmaTho(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {

            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                    "|0|Em muốn được tặng hoa , anh nào tặng hoa cho em đi\n|4|"
                    + "[Hoa hồng mọc dại quanh các làng, đảo , vách núi, ....]", "Tặng hoa", "Tỉa hoa", "Phụ\nkiện");

        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (player.iDMark.isBaseMenu()) {
                switch (select) {
                    case 0 -> {
                        Item boHoaHong1 = InventoryService.gI().findItemBag(player, 1388);
                        Item boHoaHong2 = InventoryService.gI().findItemBag(player, 1395);
                        int[] idCT = {464, 452};
                        int[] idPet = {892, 893};
                        int[] idDeoLung = {1515, 1520, 1531};
                        int[] idVatPham = {457, 1440, 1229};
                        int[] money = {utils.Util.nextInt(333333, 999999), utils.Util.nextInt(50, 100), utils.Util.nextInt(200, 300)};
                        if (boHoaHong1 == null && boHoaHong2 == null) {
                            services.Service.gI().sendThongBaoOK(player, "Anh kiếm hoa tặng em đi <3 yêu yêu");
                            return;
                        }
                        if (InventoryService.gI().getCountEmptyBag(player) < 1) {
                            services.Service.gI().sendThongBaoOK(player, "Cần 1 ô hành trang trống trở lên anh yêu!!!");
                            return;
                        }
                        InventoryService.gI().subQuantityItemsBag(player, boHoaHong2 == null ? boHoaHong1 : boHoaHong2, 1);
                        Item caiTrang = ItemService.gI().createNewItem((short) idCT[utils.Util.nextInt(0, idCT.length - 1)]);
                        Item thuCung = ItemService.gI().createNewItem((short) idPet[utils.Util.nextInt(0, idPet.length - 1)]);
                        Item deoLung = ItemService.gI().createNewItem((short) idDeoLung[utils.Util.nextInt(0, idDeoLung.length - 1)]);
                        Item vatPham = ItemService.gI().createNewItem((short) idVatPham[utils.Util.nextInt(0, idVatPham.length - 1)]);
                        if (utils.Util.isTrue(5, 100)) {
                            caiTrang.addOptionParam(50, utils.Util.nextInt(20, 30));
                            caiTrang.addOptionParam(77, utils.Util.nextInt(20, 30));
                            caiTrang.addOptionParam(103, utils.Util.nextInt(20, 30));
                            caiTrang.addOptionParam(utils.Util.isTrue(5, 100) ? 117 : utils.Util.isTrue(5, 100) ? 14 : 5, utils.Util.nextInt(8, 12));
                            if (utils.Util.isTrue(99, 100)) {
                                caiTrang.addOptionParam(93, utils.Util.nextInt(3, 7));
                            }
                            caiTrang.addOptionParam(30, 1);
                            InventoryService.gI().addItemBag(player, caiTrang);
                            services.Service.gI().sendThongBao(player, "Yêu anh, tặng anh " + caiTrang.template.name);
                        } else if (utils.Util.isTrue(25, 100)) {
                            thuCung.addOptionParam(50, utils.Util.nextInt(8, 12));
                            thuCung.addOptionParam(77, utils.Util.nextInt(8, 12));
                            thuCung.addOptionParam(103, utils.Util.nextInt(8, 12));
                            thuCung.addOptionParam(utils.Util.isTrue(5, 100) ? 117 : utils.Util.isTrue(5, 100) ? 14 : 5, utils.Util.nextInt(3, 5));
                            if (utils.Util.isTrue(99, 100)) {
                                thuCung.addOptionParam(93, utils.Util.nextInt(3, 7));
                            }
                            thuCung.addOptionParam(30, 1);
                            InventoryService.gI().addItemBag(player, thuCung);
                            services.Service.gI().sendThongBao(player, "Yêu anh, tặng anh " + thuCung.template.name);
                        } else if (utils.Util.isTrue(25, 100)) {
                            deoLung.addOptionParam(50, utils.Util.nextInt(8, 12));
                            deoLung.addOptionParam(77, utils.Util.nextInt(8, 12));
                            deoLung.addOptionParam(103, utils.Util.nextInt(8, 12));
                            if (utils.Util.isTrue(99, 100)) {
                                deoLung.addOptionParam(93, utils.Util.nextInt(3, 7));
                            }
                            deoLung.addOptionParam(30, 1);
                            InventoryService.gI().addItemBag(player, deoLung);
                            services.Service.gI().sendThongBao(player, "Yêu anh, tặng anh " + deoLung.template.name);
                        } else if (utils.Util.isTrue(25, 100)) {
                            vatPham.addOptionParam(30, 1);
                            vatPham.quantity = vatPham.template.id == 457 ? utils.Util.nextInt(1, 3) : 1;
                            InventoryService.gI().addItemBag(player, vatPham);
                            services.Service.gI().sendThongBao(player, "Yêu anh, tặng anh " + vatPham.template.name);
                        } else if (utils.Util.isTrue(80, 100)) {
                            player.inventory.gold += money[0];
                            services.Service.gI().sendThongBao(player, "Yêu anh, tặng anh " + money[0] + " vàng");
                        } else if (utils.Util.isTrue(10, 100)) {
                            player.inventory.ruby += money[1];
                            services.Service.gI().sendThongBao(player, "Yêu anh, tặng anh " + money[1] + " ngọc hồng");
                        } else {
                            player.inventory.gem += money[2];
                            services.Service.gI().sendThongBao(player, "Yêu anh, tặng anh " + money[2] + " ngọc xanh");
                        }
                        services.Service.gI().sendMoney(player);
                        InventoryService.gI().sendItemBag(player);
                    }
                    case 1 -> {
                        int[] idBoHoaHong = {1388, 1395};
                        Item keoTiaHoa = InventoryService.gI().findItemBag(player, 1387);
                        Item hoaHong = InventoryService.gI().findItemBag(player, 1530);

                        if (keoTiaHoa == null) {
                            services.Service.gI().sendThongBao(player, "Tìm mua cho em cái kéo để cắt hoa đi");
                            return;
                        }

                        if (hoaHong == null) {
                            services.Service.gI().sendThongBao(player, "Anh làm gì có bông hồng nào mà đưa em");
                            return;
                        }
                        if (InventoryService.gI().getCountEmptyBag(player) < 1) {
                            services.Service.gI().sendThongBaoOK(player, "Cần 1 ô hành trang trống trở lên nha anh yêu!!!");
                            return;
                        }
                        if (hoaHong.quantity > 10) {
                            InventoryService.gI().subQuantityItemsBag(player, keoTiaHoa, 1);
                            InventoryService.gI().subQuantityItemsBag(player, hoaHong, utils.Util.nextInt(7, 10));
                            Item boHoaHong = ItemService.gI().createNewItem((short) idBoHoaHong[utils.Util.nextInt(0, idBoHoaHong.length - 1)]);
                            InventoryService.gI().addItemBag(player, boHoaHong);
                            services.Service.gI().sendThongBao(player, " " + boHoaHong.template.name + " đẹp quá, anh có thể tặng em được không?");
                            services.Service.gI().sendMoney(player);
                            InventoryService.gI().sendItemBag(player);
                        } else {
                            services.Service.gI().sendThongBao(player, "Em cần khoàng 7 đến 10 bông hoa hồng");
                        }

                    }
                    case 2 -> {

                        ShopService.gI().opendShop(player, "BUNMA_PHU_KIEN", false);

                    }
                }
            }
        }
    }
}
