package npc.npc_manifest;

/**
 * @author NTD
 */
import consts.ConstDailyGift;
import consts.ConstMenu;
import consts.ConstNpc;
import item.Item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import models.Combine.Combine;

import models.Combine.CombineService;
import models.Combine.manifest.CheTaoCuonSachCu;
import models.Combine.manifest.DoiSachTuyetKy;
import models.Combine.manifest.NangCapVatPham;
import models.DeathOrAliveArena.DeathOrAliveArena;
import models.DeathOrAliveArena.DeathOrAliveArenaManager;
import models.DeathOrAliveArena.DeathOrAliveArenaService;
import npc.Npc;
import player.Player;
import player.dailyGift.DailyGiftService;
import services.InventoryService;
import services.ItemService;
import services.Service;
import services.func.ChangeMapService;
import services.func.TopService;
import shop.ShopService;
import utils.Util;

public class BaHatMit extends Npc {

    public BaHatMit(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            switch (this.mapId) {
                case 5 ->
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ngươi tìm ta có việc gì?", "Chức năng\nPha lê", "Nâng SKH", "SHOP\nBà Hạt Mít","Đập Đồ");
//                case 112 -> {
//                    if (Util.isAfterMidnight(player.lastTimePKVoDaiSinhTu)) {
//                        player.haveRewardVDST = false;
//                        player.thoiVangVoDaiSinhTu = 0;
//                    }
//                    if (player.haveRewardVDST) {
//                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Đây là phần thưởng cho con.", "1 ngọc bí\nbất kì", "1 bí ngô");
//                        return;
//                    }
//                    if (DeathOrAliveArenaManager.gI().getVDST(player.zone) != null) {
//                        if (DeathOrAliveArenaManager.gI().getVDST(player.zone).getPlayer().equals(player)) {
//                            this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ngươi muốn hủy đăng ký thi đấu võ đài?", "Top 100", "Đồng ý\n" + player.thoiVangVoDaiSinhTu + " thỏi vàng", "Từ chối", "Về\nđảo rùa");
//                            return;
//                        }
//                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ngươi muốn đăng ký thi đấu võ đài?\nnhiều phần thưởng giá trị đang đợi ngươi đó", "Top 100", "Bình chọn", "Đồng ý\n" + player.thoiVangVoDaiSinhTu + " thỏi vàng", "Từ chối", "Về\nđảo rùa");
//                        return;
//                    }
//                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ngươi muốn đăng ký thi đấu võ đài?\nnhiều phần thưởng giá trị đang đợi ngươi đó", "Top 100", "Đồng ý\n" + player.thoiVangVoDaiSinhTu + " thỏi vàng", "Từ chối", "Về\nđảo rùa");
//                }
                case 174 ->
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ngươi tìm ta có việc gì?", "Quay về", "Từ chối");
                case 181 ->
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ngươi tìm ta có việc gì?", "Quay về", "Từ chối");
                default -> {
                    List<String> menu = new ArrayList<>(Arrays.asList("Sách\nTuyệt Kỹ", "Cửa hàng\nBùa", "Nâng cấp\nVật phẩm", "Làm phép\nNhập đá", "Nhập\nNgọc Rồng"));
                    if (InventoryService.gI().findItem(player, 454) || InventoryService.gI().findItem(player, 921)) {
                        menu = new ArrayList<>(Arrays.asList("Sách\nTuyệt Kỹ", "Cửa hàng\nBùa", "Nâng cấp\nVật phẩm", InventoryService.gI().findItemBongTaiCap2(player) ? "Mở chỉ số\nBông tai\nPorata cấp\n2" : "Nâng cấp\nBông tai\nPorata", "Làm phép\nNhập đá", "Nhập\nNgọc Rồng"));
                    }
                    if (DailyGiftService.checkDailyGift(player, ConstDailyGift.NHAN_BUA_MIEN_PHI)) {
                        menu.add(0, "Thưởng\nBùa 1h\nngẫu nhiên");
                    }
                    String[] menus = menu.toArray(new String[0]);
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ngươi tìm ta có việc gì?", menus);
                }
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            switch (this.mapId) {
                case 5 -> {
                    switch (player.iDMark.getIndexMenu()) {
                        case ConstNpc.BASE_MENU -> {
                            switch (select) {
                                case 0 ->
                                    createOtherMenu(player, ConstMenu.MENU_PHA_LE, "Ta có thể giúp gì cho ngươi ?", "Ép sao\ntrang bị", "Pha lê\nhoá\ntrang bị", "Nâng cấp\nSao pha lê", "Đánh bóng\nSao pha lê", "Cường hoá\nLỗ sao\npha lê", "Tạo đá Hematite");
                                case 1 -> //Chuyển hoá trang bị
                                    createOtherMenu(player, ConstMenu.MENU_NANG_CAP_TRANG_BI, "Ta sẽ biến trang bị của ngươi thành trang bị Kích Hoạt", "Kích hoạt\nThường", "Kích hoạt\nVIP");
//                                case 1 ->
//                                    ChangeMapService.gI().changeMapNonSpaceship(player, 112, 200 + Util.nextInt(-100, 100), 408);
                                case 3 ->
                                    createOtherMenu(player, ConstMenu.CHUC_NANG_BHM_KHAC, "Ngươi tìm ta có việc gì?\n",
                                            "Đập đồ\nẢo Hóa",
                                            "Build Đồ",
                                            "Pháp sư hoá",
                                            "Siêu hóa\n Cải trang",
                                            "Tinh ấn\ntrang bị",
                                            "Tinh thạch\ntrang bị",
                                            "Nâng cấp\nGiáp LT");
                                case 2 ->
                                    this.createOtherMenu(player, ConstMenu.SHOP_BHM, "|7|CHÂN MỆNH"
                                            + "\n\n|1|Bạn đang có: " + (player.event.getEventPointBHM()) + " Điểm Săn Boss"
                                            + "\n|3| Lưu ý: Chỉ được nhận Chân mệnh 1 lần (Hành trang chỉ tồn tại 1 Chân mệnh)"
                                            + "\nNếu đã có Chân mệnh. Ta sẽ giúp ngươi nâng cấp bậc lên với các dòng chỉ số cao hơn",
                                            player.event.getEventPointBHM() >= 1000 ? "Nhận Chân mệnh" : "Cần 1000 điểm\n để nhận", "Nâng cấp Chân mệnh", "Shop\nĐIỂM", "Shop\nBà Hạt Mít"
                                    );
                            }
                        }
                        case ConstMenu.CHUC_NANG_BHM_KHAC -> {
                            switch (select) {
                                case 0: //Đập đồ ảo hóa
                                    CombineService.gI().openTabCombine(player, CombineService.DAP_DO_AO_HOA);
                                    break;
                                case 1: //Build Đồ
                                    this.createOtherMenu(player, ConstMenu.BUILD_DO_BHM, "|7|\bChi Tiết:\n|0|"
                                            + "\nMở Khóa GD: tỉ lệ thành công 30%"
                                            + "\nGia hạn Vật Phẩm: tỉ lệ thành công 30% + 3 - 7 ngày, 70% + 1 ngày"
                                            + "\nTẩy đồ: tẩy sao pha lê, chỉ số đặc biệt một số trang bị",
                                            "Mở Khóa GD", "Gia hạn\n Vật Phẩm", "Tẩy Đồ"
                                    );
                                    break;  
                                case 2: //Pháp sư hóa trang bị
                                    CombineService.gI().openTabCombine(player, CombineService.PS_HOA_TRANG_BI);
                                    break;
                                case 3: //Siêu Hóa
                                    CombineService.gI().openTabCombine(player, CombineService.SIEU_HOA);
                                    break;
                                case 4:
                                    CombineService.gI().openTabCombine(player, CombineService.AN_TRANG_BI);
                                    break; 
                                case 5:
                                    CombineService.gI().openTabCombine(player, CombineService.TINH_THACH_HOA);
                                    break;  
                                case 6:
                                    CombineService.gI().openTabCombine(player, CombineService.NANG_GIAP_LUYEN_TAP);
                                    break; 
                            }
                        }
                        case ConstMenu.BUILD_DO_BHM -> {
                            switch (select) {
                                case 0: //Mở khóa Item
                                    CombineService.gI().openTabCombine(player, CombineService.MO_KHOA_ITEM);
                                    break;
                                case 1: //Gia hạn vật phẩm
                                    CombineService.gI().openTabCombine(player, CombineService.GIA_HAN_VAT_PHAM);
                                    break;
                                case 2: //Tẩy đồ
                                    CombineService.gI().openTabCombine(player, CombineService.TAY_PS_HOA_TRANG_BI);
                                    break;
                            }
                        }
                        case ConstMenu.SHOP_BHM -> {
                            switch (select) {
                               case 0: //Nhận chân mệnh
                                   if (player.event.getEventPointBHM() >= 1000) {
//                                         Item item
                                   }
                                   Service.gI().sendThongBao(player, "Chưa mở");
                                   break;
                               case 1: //Nâng cấp chân mệnh
                                   CombineService.gI().openTabCombine(player, CombineService.NANG_CAP_CHAN_MENH);
                                   break;
                                case 2: //Tẩy đồ
                                    ShopService.gI().opendShop(player, "SHOP_BHM", false);
                                    break;
                                case 3: //Tẩy đồ
                                    ShopService.gI().opendShop(player, "SHOP_THOI_BHM", false);
                                    break;
                            }
                        }
                        case ConstMenu.MENU_PHA_LE -> {
                            switch (select) {
                                case 0: //Ép sao trang bị
                                    CombineService.gI().openTabCombine(player, CombineService.EP_SAO_TRANG_BI);
                                    break;
                                case 1: //Pha lê hoá trang bị
                                    createOtherMenu(player, ConstMenu.MENU_PHA_LE_HOA_TRANG_BI, "Ngươi muốn pha lê hoá trang bị bằng cách nào?", "Bằng ngọc", "Từ chối");
                                    break;
                                case 2: //Nâng cấp sao pha lê
                                    CombineService.gI().openTabCombine(player, CombineService.NANG_CAP_SAO_PHA_LE);
                                    break;
                                case 3: //Đánh bóng sao pha lê
                                    CombineService.gI().openTabCombine(player, CombineService.DANH_BONG_SAO_PHA_LE);
                                    break;
                                case 4: //Cường hoá lỗ sao pha lê
                                    CombineService.gI().openTabCombine(player, CombineService.CUONG_HOA_LO_SAO_PHA_LE);
                                    break;
                                case 5: //Tạo đá Hematite
                                    CombineService.gI().openTabCombine(player, CombineService.TAO_DA_HEMATITE);
                                    break;
                            }
                        }
                        case ConstMenu.MENU_NANG_CAP_TRANG_BI -> {
                            switch (select) {
                                case 0:
                                    CombineService.gI().openTabCombine(player, CombineService.NANG_CAP_KICH_HOAT);
                                    break;
                                case 1:
                                    CombineService.gI().openTabCombine(player, CombineService.NANG_CAP_KICH_HOAT_VIP);
                                    break;
                            }
                        }
                        case ConstMenu.MENU_CHUYEN_HOA_TRANG_BI -> {
                            switch (select) {
                                case 0 -> {
                                    CombineService.gI().openTabCombine(player, CombineService.CHUYEN_HOA_TRANG_BI_DUNG_VANG);
                                }
                                case 1 -> {
                                    CombineService.gI().openTabCombine(player, CombineService.CHUYEN_HOA_TRANG_BI_DUNG_NGOC);
                                }
                            }
                        }
                        case ConstMenu.MENU_PHA_LE_HOA_TRANG_BI -> {
                            if (select == 0) {
                                CombineService.gI().openTabCombine(player, CombineService.PHA_LE_HOA_TRANG_BI);
                            }
                        }
                        case ConstNpc.MENU_START_COMBINE -> {
                            switch (player.combine.typeCombine) {
                                case CombineService.PHA_LE_HOA_TRANG_BI -> {
                                    switch (select) {
                                        case 0 ->
                                            CombineService.gI().startCombine(player, 500);
                                        case 1 ->
                                            CombineService.gI().startCombine(player, 100);
                                        case 2 ->
                                            CombineService.gI().startCombine(player, 10);
                                        case 3 ->
                                            CombineService.gI().startCombine(player, 1);
                                        case 4 ->
                                            CombineService.gI().startCombine(player);
                                    }
                                }

                                case CombineService.EP_SAO_TRANG_BI -> {
                                    switch (select) {
                                        case 0 ->
                                            CombineService.gI().startCombine(player, 7);
                                        case 1 ->
                                            CombineService.gI().startCombine(player, 1);
                                    }
                                }

                                case CombineService.NANG_CAP_KICH_HOAT_VIP,
                                        CombineService.NANG_CAP_KICH_HOAT,
                                        CombineService.NANG_CAP_SAO_PHA_LE,
                                        CombineService.DANH_BONG_SAO_PHA_LE,
                                        CombineService.CUONG_HOA_LO_SAO_PHA_LE,
                                        CombineService.TAO_DA_HEMATITE,
                                        CombineService.DAP_DO_AO_HOA,
                                        CombineService.PS_HOA_TRANG_BI,
                                        CombineService.TAY_PS_HOA_TRANG_BI,
                                        CombineService.SIEU_HOA,
                                        CombineService.AN_TRANG_BI,
                                        CombineService.TINH_THACH_HOA,
                                        CombineService.NANG_GIAP_LUYEN_TAP,
                                        CombineService.MO_KHOA_ITEM,
                                        CombineService.GIA_HAN_VAT_PHAM,
                                        CombineService.NANG_CAP_CHAN_MENH -> {
                                    switch (select) {
                                        case 0:
                                            CombineService.gI().startCombine(player);
                                            break;
                                    }
                                }
                            }
                        }
                    }
                }
                case 112 -> {
                    if (player.iDMark.isBaseMenu()) {
                        if (player.haveRewardVDST) {
                            switch (select) {
                                case 0 -> {
                                    if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                                        Item item = ItemService.gI().createNewItem((short) (Util.nextInt(705, 708)));
                                        item.itemOptions.add(new Item.ItemOption(93, 30));
                                        InventoryService.gI().addItemBag(player, item);
                                        InventoryService.gI().sendItemBag(player);
                                        Service.gI().sendThongBao(player, "Bạn nhận được " + item.template.name);
                                        player.haveRewardVDST = false;
                                    } else {
                                        Service.gI().sendThongBao(player, "Hành trang không còn chỗ trống, không thể nhặt thêm");
                                    }
                                }
                                case 1 -> {
                                    if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                                        Item item = ItemService.gI().createNewItem((short) 585);
                                        item.itemOptions.add(new Item.ItemOption(93, 30));
                                        InventoryService.gI().addItemBag(player, item);
                                        InventoryService.gI().sendItemBag(player);
                                        Service.gI().sendThongBao(player, "Bạn nhận được " + item.template.name);
                                        player.haveRewardVDST = false;
                                    } else {
                                        Service.gI().sendThongBao(player, "Hành trang không còn chỗ trống, không thể nhặt thêm");
                                    }
                                }
                            }
                            return;
                        }
                        if (DeathOrAliveArenaManager.gI().getVDST(player.zone) != null) {
                            if (DeathOrAliveArenaManager.gI().getVDST(player.zone).getPlayer().equals(player)) {
                                switch (select) {
                                    case 0 -> {
                                        TopService.showListTop(player, 5);
                                    }
                                    case 1 ->
                                        this.npcChat("Không thể thực hiện");
                                    case 2 -> {
                                    }
                                    case 3 ->
                                        ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 1156);
                                }
                                return;
                            }
                            switch (select) {
                                case 0 -> {
                                    TopService.showListTop(player, 5);
                                }
                                case 1 ->
                                    this.createOtherMenu(player, ConstNpc.DAT_CUOC_HAT_MIT, "Phí bình chọn là 1 triệu vàng\nkhi trận đấu kết thúc\n90% tổng tiền bình chọn sẽ chia đều cho phe bình chọn chính xác", "Bình chọn cho " + DeathOrAliveArenaManager.gI().getVDST(player.zone).getPlayer().name + " (" + DeathOrAliveArenaManager.gI().getVDST(player.zone).getCuocPlayer() + ")", "Bình chọn cho hạt mít (" + DeathOrAliveArenaManager.gI().getVDST(player.zone).getCuocBaHatMit() + ")");
                                case 2 ->
                                    DeathOrAliveArenaService.gI().startChallenge(player);
                                case 3 -> {
                                }
                                case 4 ->
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 1156);
                            }
                            return;
                        }
                        switch (select) {
                            case 0 -> {
                                TopService.showListTop(player, 5);
                            }
                            case 1 ->
                                DeathOrAliveArenaService.gI().startChallenge(player);
                            case 2 -> {
                            }
                            case 3 ->
                                ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 1156);
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.DAT_CUOC_HAT_MIT) {
                        if (DeathOrAliveArenaManager.gI().getVDST(player.zone) != null) {
                            switch (select) {
                                case 0 -> {
                                    if (player.inventory.gold >= 1_000_000) {
                                        DeathOrAliveArena vdst = DeathOrAliveArenaManager.gI().getVDST(player.zone);
                                        vdst.setCuocPlayer(vdst.getCuocPlayer() + 1);
                                        vdst.addBinhChon(player);
                                        player.binhChonPlayer++;
                                        player.zoneBinhChon = player.zone;
                                        player.inventory.gold -= 1_000_000;
                                        Service.gI().sendMoney(player);
                                    } else {
                                        Service.gI().sendThongBao(player, "Bạn không đủ vàng, còn thiếu " + Util.numberToMoney(1_000_000 - player.inventory.gold) + " vàng nữa");
                                    }
                                }
                                case 1 -> {
                                    if (player.inventory.gold >= 1_000_000) {
                                        DeathOrAliveArena vdst = DeathOrAliveArenaManager.gI().getVDST(player.zone);
                                        vdst.setCuocBaHatMit(vdst.getCuocBaHatMit() + 1);
                                        vdst.addBinhChon(player);
                                        player.binhChonHatMit++;
                                        player.zoneBinhChon = player.zone;
                                        player.inventory.gold -= 1_000_000;
                                        Service.gI().sendMoney(player);
                                    } else {
                                        Service.gI().sendThongBao(player, "Bạn không đủ vàng, còn thiếu " + Util.numberToMoney(1_000_000 - player.inventory.gold) + " vàng nữa");
                                    }
                                }
                            }
                        }
                    }
                }
                case 174, 181 -> {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0 ->
                                ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 1156);
                        }
                    }
                }
                case 42, 43, 44, 84 -> {
                    if (player.iDMark.isBaseMenu()) {
                        if (!DailyGiftService.checkDailyGift(player, ConstDailyGift.NHAN_BUA_MIEN_PHI)) {
                            select++;
                        }
                        if (!InventoryService.gI().findItem(player, 454) && !InventoryService.gI().findItem(player, 921)) {
                            if (select >= 4) {
                                select++;
                            }
                        }
                        switch (select) {
                            case 0:
                                if (DailyGiftService.checkDailyGift(player, ConstDailyGift.NHAN_BUA_MIEN_PHI)) {
                                    int idItem = Util.nextInt(213, 219);
                                    player.charms.addTimeCharms(idItem, 60);
                                    Item bua = ItemService.gI().createNewItem((short) idItem);
                                    Service.gI().sendThongBao(player, "Bạn vừa nhận thưởng " + bua.template.name);
                                    DailyGiftService.updateDailyGift(player, ConstDailyGift.NHAN_BUA_MIEN_PHI);
                                } else {
                                    Service.gI().sendThongBao(player, "Hôm nay bạn đã nhận bùa miễn phí rồi!!!");
                                }
                                break;
                            case 1:
                                createOtherMenu(player, ConstNpc.MENU_SACH_TUYET_KY, "Ta có thể giúp gì cho ngươi ?",
                                        "Đóng thành\nSách cũ",
                                        "Đổi Sách\nTuyệt kỹ",
                                        "Giám định\nSách",
                                        "Tẩy\nSách",
                                        "Nâng cấp\nSách\nTuyệt kỹ",
                                        "Hồi phục\nSách",
                                        "Phân rã\nSách");
                                break;
                            case 2:
                                createOtherMenu(player, ConstNpc.MENU_OPTION_SHOP_BUA, "Bùa của ta rất lợi hại, nhìn ngươi yếu đuối thế này, chắc muốn mua bùa để " + "mạnh mẽ à, mua không ta bán cho, xài rồi lại thích cho mà xem.",
                                        "Bùa\n1 giờ",
                                        "Bùa\n8 giờ",
                                        "Bùa\n1 tháng", "Đóng");
                                break;
                            case 3:
                                CombineService.gI().openTabCombine(player, CombineService.NANG_CAP_VAT_PHAM);
                                break;
                            case 4:
                                if (InventoryService.gI().findItemBongTaiCap2(player)) {
                                    CombineService.gI().openTabCombine(player, CombineService.NANG_CHI_SO_BONG_TAI);
                                } else {
                                    CombineService.gI().openTabCombine(player, CombineService.NANG_CAP_BONG_TAI);
                                }
                                break;
                            case 5:
                                CombineService.gI().openTabCombine(player, CombineService.LAM_PHEP_NHAP_DA);
                                break;
                            case 6:
                                CombineService.gI().openTabCombine(player, CombineService.NHAP_NGOC_RONG);
                                break;
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_SACH_TUYET_KY) {
                        switch (select) {
                            case 0:
                                CheTaoCuonSachCu.showCombine(player);
                                break;
                            case 1:
                                DoiSachTuyetKy.showCombine(player);
                                break;
                            case 2:
                                CombineService.gI().openTabCombine(player, CombineService.GIAM_DINH_SACH);
                                break;
                            case 3:
                                CombineService.gI().openTabCombine(player, CombineService.TAY_SACH);
                                break;
                            case 4:
                                CombineService.gI().openTabCombine(player, CombineService.NANG_CAP_SACH_TUYET_KY);
                                break;
                            case 5:
                                CombineService.gI().openTabCombine(player, CombineService.HOI_PHUC_SACH);
                                break;
                            case 6:
                                CombineService.gI().openTabCombine(player, CombineService.PHAN_RA_SACH);
                                break;
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.DONG_THANH_SACH_CU) {
                        CheTaoCuonSachCu.cheTaoCuonSachCu(player);
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.DOI_SACH_TUYET_KY) {
                        DoiSachTuyetKy.doiSachTuyetKy(player);
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_SHOP_BUA) {
                        switch (select) {
                            case 0 ->
                                ShopService.gI().opendShop(player, "BUA_1H", true);
                            case 1 ->
                                ShopService.gI().opendShop(player, "BUA_8H", true);
                            case 2 ->
                                ShopService.gI().opendShop(player, "BUA_1M", true);
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
                        switch (player.combine.typeCombine) {
                            case CombineService.NANG_CAP_BONG_TAI, CombineService.NANG_CHI_SO_BONG_TAI, CombineService.LAM_PHEP_NHAP_DA, CombineService.NHAP_NGOC_RONG, CombineService.GIAM_DINH_SACH, CombineService.TAY_SACH, CombineService.NANG_CAP_SACH_TUYET_KY, CombineService.HOI_PHUC_SACH, CombineService.PHAN_RA_SACH -> {
                                if (select == 0) {
                                    CombineService.gI().startCombine(player);
                                }
                            }
                            case CombineService.NANG_CAP_VAT_PHAM -> {
                                if (select == 0) {
                                    CombineService.gI().startCombine(player);
                                } else if (select == 1) {
                                    NangCapVatPham.nangCapVatPham(player, true);
                                }
                            }
                        }
                    }
                }
                default -> {
                }
            }
        }
    }
}
