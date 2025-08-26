package npc.npc_manifest;
//hang nga map 5 dao kame
/**
 *
 * @author NTD
 */
import consts.ConstNpc;
import consts.ConstTranhNgocNamek;
import item.Item;
import models.DragonNamecWar.TranhNgoc;
import npc.Npc;
import player.NPoint;
import player.Player;
import services.InventoryService;
import services.OpenPowerService;
import services.Service;
import services.func.TopService;
import shop.ShopService;
import utils.Util;

public class QuocVuong extends Npc {

    public QuocVuong(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        switch (mapId) {
            case 5:
                Item mcl = InventoryService.gI().findItemBagByTemp(player, ConstTranhNgocNamek.ITEM_TRANH_NGOC);
                int slMCL = (mcl == null) ? 0 : mcl.quantity;
                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                        "Đang diễn ra sự kiện đua top thu thập đồng xu\n"
                                + "Truy cập http://javhd.pro để biết thêm thông tin sự kiện",
                        "Tham gia", "Đổi điểm\nThưởng\n[" + slMCL + "]", "Bảng\nxếp hạng", "Từ chối");
                // "Bảng\nxếp hạng", "Từ chối");
                break;

            default:
                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                        "Con muốn nâng giới hạn sức mạnh cho bản thân hay đệ tử?",
                        "Bản thân", "Đệ tử", "Từ chối");
                break;
        }

    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (player.iDMark.isBaseMenu()) {
                switch (this.mapId) {
                    case 5 -> {
                        switch (select) {
                            case 0 -> {
                                if (TranhNgoc.gI().isTimeRegisterWar()) {
                                    if (player.iDMark.getTranhNgoc() == -1) {
                                        this.createOtherMenu(player, ConstNpc.REGISTER_TRANH_NGOC,
                                                "|0|Ngọc rồng Namếc đang bị 2 thế lực tranh giành\nHãy chọn cấp độ tham gia tùy theo sức mạnh bản thân\n|2|"
                                                        + "Phe Xanh: " + TranhNgoc.gI().getPlayersBlue().size()
                                                        + "\n|7|"
                                                        + "Phe Đỏ: " + TranhNgoc.gI().getPlayersRed().size() + "\n|4|"
                                                        + "Chú ý: Đăng kí xong phải online cho tới lúc tranh đấu, out gaem thì phải đăng kí lại!",
                                                "Tham gia phe Xanh", "Tham gia phe Đỏ", "Đóng");
                                    } else {
                                        this.createOtherMenu(player, ConstNpc.LOG_OUT_TRANH_NGOC,
                                                "|0|Ngọc rồng Namếc đang bị 2 thế lực tranh giành\nHãy chọn cấp độ tham gia tùy theo sức mạnh bản thân\n|2|"
                                                        + "Phe Xanh: " + TranhNgoc.gI().getPlayersBlue().size()
                                                        + "\n|7|"
                                                        + "Phe Đỏ: " + TranhNgoc.gI().getPlayersRed().size() + "\n|4|"
                                                        + "Chú ý: Đăng kí xong phải online cho tới lúc tranh đấu, out game thì phải đăng kí lại!",
                                                "Hủy\nĐăng Ký", "Đóng");
                                    }
                                    return;
                                }
                                Service.gI().sendPopUpMultiLine(player, 0, 4335,
                                        "Sự kiện sẽ mở đăng ký vào lúc " + TranhNgoc.HOUR_REGISTER + ":"
                                                + TranhNgoc.MIN_REGISTER + "\nSự kiện sẽ bắt đầu vào "
                                                + TranhNgoc.HOUR_OPEN + ":" + TranhNgoc.MIN_OPEN + " và kết thúc vào "
                                                + TranhNgoc.HOUR_CLOSE + ":" + TranhNgoc.MIN_CLOSE);
                            }
                            case 1 -> // Shop
                                ShopService.gI().opendShop(player, "TRUONG_LAO", false);
                            case 2 ->
                                Service.gI().sendThongBaoOK(player, TopService.getTopQuocVuong());
                        }
                    }
                    default -> {
                        switch (select) {
                            case 0 -> {
                                if (player.nPoint.limitPower < NPoint.MAX_LIMIT) {
                                    this.createOtherMenu(player, ConstNpc.OPEN_POWER_MYSEFT,
                                            "Ta sẽ truyền năng lượng giúp con mở giới hạn sức mạnh của bản thân lên "
                                                    + Util.numberToMoney(player.nPoint.getPowerNextLimit()),
                                            // "Nâng\ngiới hạn\nsức mạnh",
                                            "Nâng ngay\n"
                                                    + Util.numberToMoney(OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER)
                                                    + " vàng",
                                            "Đóng");
                                } else {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                            "Sức mạnh của con đã đạt tới giới hạn",
                                            "Đóng");
                                }
                            }
                            case 1 -> {
                                if (player.pet != null) {
                                    if (player.pet.nPoint.limitPower < NPoint.MAX_LIMIT) {
                                        this.createOtherMenu(player, ConstNpc.OPEN_POWER_PET,
                                                "Ta sẽ truyền năng lượng giúp con mở giới hạn sức mạnh của đệ tử lên "
                                                        + Util.numberToMoney(player.pet.nPoint.getPowerNextLimit()),
                                                "Nâng ngay\n" + Util.numberToMoney(
                                                        OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER) + " vàng",
                                                "Đóng");
                                    } else {
                                        this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                                "Sức mạnh của đệ con đã đạt tới giới hạn",
                                                "Đóng");
                                    }
                                } else {
                                    Service.gI().sendThongBao(player, "Không thể thực hiện");
                                }
                                // giới hạn đệ tử
                            }
                        }
                    }
                }

                return;
            }

            switch (player.iDMark.getIndexMenu()) {
                case ConstNpc.REGISTER_TRANH_NGOC -> {
                    switch (select) {
                        case 0:
                            if (!player.getSession().actived) {
                                Service.gI().sendThongBao(player,
                                        "Vui lòng kích hoạt tài khoản để sửa dụng chức năng này!");
                                return;
                            }
                            player.iDMark.setTranhNgoc((byte) 1);
                            TranhNgoc.gI().addPlayersBlue(player);
                            Service.gI().sendThongBao(player, "Đăng ký vào phe Xanh thành công");
                            break;
                        case 1:
                            if (!player.getSession().actived) {
                                Service.gI().sendThongBao(player,
                                        "Vui lòng kích hoạt tài khoản để sửa dụng chức năng này!");
                                return;
                            }
                            player.iDMark.setTranhNgoc((byte) 2);
                            TranhNgoc.gI().addPlayersRed(player);
                            Service.gI().sendThongBao(player, "Đăng ký vào phe Đỏ thành công");
                            break;
                    }
                }
                case ConstNpc.LOG_OUT_TRANH_NGOC -> {
                    switch (select) {
                        case 0:
                            player.iDMark.setTranhNgoc((byte) -1);
                            TranhNgoc.gI().removePlayersBlue(player);
                            TranhNgoc.gI().removePlayersRed(player);
                            Service.gI().sendThongBao(player, "Hủy đăng ký thành công");
                            break;
                    }

                }

                case ConstNpc.OPEN_POWER_MYSEFT -> {
                    switch (select) {
                        // case 0 ->
                        // OpenPowerService.gI().openPowerBasic(player);
                        case 0 -> {
                            if (player.inventory.gold >= OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER) {
                                if (OpenPowerService.gI().openPowerSpeed(player)) {
                                    player.inventory.gold -= OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER;
                                    Service.gI().sendMoney(player);
                                }
                            } else {
                                Service.gI().sendThongBao(player,
                                        "Bạn không đủ vàng để mở, còn thiếu "
                                                + Util.numberToMoney((OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER
                                                        - player.inventory.gold))
                                                + " vàng");
                            }
                        }
                    }
                }
                case ConstNpc.OPEN_POWER_PET -> {
                    if (select == 0) {
                        if (player.inventory.gold >= OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER) {
                            if (OpenPowerService.gI().openPowerSpeed(player.pet)) {
                                player.inventory.gold -= OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER;
                                Service.gI().sendMoney(player);
                            }
                        } else {
                            Service.gI().sendThongBao(player,
                                    "Bạn không đủ vàng để mở, còn thiếu "
                                            + Util.numberToMoney((OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER
                                                    - player.inventory.gold))
                                            + " vàng");
                        }
                    }
                }
            }
        }
    }
}
