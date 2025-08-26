package npc.npc_manifest;

/**
 * @author NTD
 */
import consts.ConstNpc;

import java.util.ArrayList;

import models.MajinBuu.MajinBuu14HService;
import models.MajinBuu.MajinBuuService;
import npc.Npc;
import player.Player;
import services.ItemTimeService;
import services.NpcService;
import services.Service;
import services.TaskService;
import services.func.ChangeMapService;
import shop.ShopService;
import utils.TimeUtil;
import utils.Util;

public class Osin extends Npc {

    public Osin(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            TaskService.gI().checkDoneTaskTalkNpc(player, this);
            switch (this.mapId) {
                case 50 -> {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                            "Đến\nKaio", "Đến\nhành tinh\nBill", "Từ chối");
                }
                case 154 ->
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                            "Cửa\nhàng", "Đến\nhành tinh\nngục tù", "Từ chối");
                case 155 ->
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                            "Quay về", "Từ chối");
                case 52 -> {
                    player.fightMabu.clear();
                    if (this.mapId == 52) {
                        if (TimeUtil.isMabu14HOpen()) {
                            this.createOtherMenu(player, ConstNpc.MENU_OPEN_MMB, "Mabư đã thoát khỏi vỏ bọc\nmau đi cùng ta ngăn chặn hắn lại\ntrước khi hắn tàn phá trái đất này",
                                    "OK", "Từ chối");
                        } else if (TimeUtil.isMabuOpen()) {
                            this.createOtherMenu(player, ConstNpc.MENU_OPEN_MMB, "Bây giờ tôi sẽ bí mật...\nđuổi theo 2 tên đồ tể...\nQuý vị nào muốn đi theo thì xin mời !",
                                    "OK", "Từ chối");
                        } else {
                            this.createOtherMenu(player, ConstNpc.MENU_NOT_OPEN_MMB,
                                    "Vào lúc " + MajinBuuService.HOUR_OPEN_MAP_MABU + "h tôi sẽ bí mật...\nđuổi theo 2 tên đồ tể...\nQuý vị nào muốn đi theo thì xin mời !", "OK", "Từ chối");
                        }
                    }
                }
                case 114, 115, 117, 118, 119, 120 -> {
                    if (player.cFlag != 9) {
                        NpcService.gI().createTutorial(player, tempId, avartar, "Ngươi hãy về phe của mình mà thể hiện");
                        return;
                    }
                    String npcSay = "Đừng vội xem thường Babiđây, ngay đến cha hắn là thần ma đạo sĩ Bibiđây khi còn sống cũng phải sợ hắn đấy!";
                    ArrayList<String> menuAL = new ArrayList<>();
                    menuAL.add("Hướng\ndẫn\nthêm");
                    if (!player.itemTime.isUseGTPT) {
                        menuAL.add("Giải trừ\nphép thuật\n1 ngọc");
                    }
                    if (player.fightMabu.pointMabu >= player.fightMabu.POINT_MAX && this.mapId != 120) {
                        menuAL.add("Xuống\nTầng dưới");
                    }
                    String[] menus = menuAL.toArray(String[]::new);
                    this.createOtherMenu(player, ConstNpc.GO_UPSTAIRS_MENU, npcSay, menus);
                }
                case 127 -> {
                    String npcSay = player.isPhuHoMapMabu ? "Ta có thể giúp gì cho ngươi ?" : "Ta sẽ phù hộ ngươi bằng\nnguồn sức mạnh của Thần Kaiô\n+1 triệu HP, +1 triệu KI, +10k Sức đánh\nLưu ý: sức mạnh này sẽ biến mất khi ngươi rời khỏi đây";
                    ArrayList<String> menuAL = new ArrayList<>();
                    if (!player.isPhuHoMapMabu) {
                        menuAL.add("Phù hộ\n10 ngọc");
                    }
                    menuAL.add("Từ chối");
                    menuAL.add("Về\nĐại Hội\nVõ Thuật");
                    String[] menus = menuAL.toArray(String[]::new);
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, npcSay, menus);
                }
                default ->
                    super.openBaseMenu(player);
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            switch (this.mapId) {
                case 50 -> {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0 ->
                                ChangeMapService.gI().changeMap(player, 48, -1, 354, 240);
                            case 1 ->
                                
                                ChangeMapService.gI().changeMap(player, 154, -1, 200, 312);
                        }
                    }
                }
                case 154 -> {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0 ->
                                ShopService.gI().opendShop(player, "OSIN", false);
                            case 1 -> {
                                if (player.nPoint.power <= 80_000_000_000L) {
                                    Service.gI().sendThongBao(player, "Yêu cầu sức mạnh đạt 80 tỉ");
                                    return;
                                }
                                ChangeMapService.gI().changeMap(player, 155, -1, 111, 792);
                            }
                        }
                    }
                }
                case 155 -> {
                    if (player.iDMark.isBaseMenu()) {
                        if (select == 0) {
                            ChangeMapService.gI().changeMap(player, 154, -1, 200, 312);
                        }
                    }
                }
                case 52 -> {
                    switch (player.iDMark.getIndexMenu()) {
                        case ConstNpc.MENU_OPEN_MMB -> {
                            if (select == 0) {
                                if (TimeUtil.isMabu14HOpen()) {
                                    MajinBuu14HService.gI().joinMaBu2H(player);
                                } else if (TimeUtil.isMabuOpen()) {
                                    ChangeMapService.gI().changeMap(player, 114, -1, Util.nextInt(100, 500), 336);
                                }
                            }
                        }

                    }
                }
                case 114, 115, 117, 118, 119, 120 -> {
                    if (player.cFlag != 9) {
                        return;
                    }
                    switch (select) {
                        case 0 ->
                            NpcService.gI().createTutorial(player, tempId, 4388, ConstNpc.HUONG_DAN_MAP_MA_BU);
                        case 1 -> {
                            if (!player.itemTime.isUseGTPT) {
                                player.itemTime.lastTimeUseGTPT = System.currentTimeMillis();
                                player.itemTime.isUseGTPT = true;
                                ItemTimeService.gI().sendAllItemTime(player);
                                Service.gI().sendThongBao(player, "Phép thuật đã được giải trừ, sức đánh của bạn đã tăng theo điểm tích lũy");
                            } else if (player.fightMabu.pointMabu >= player.fightMabu.POINT_MAX && this.mapId != 120) {
                                ChangeMapService.gI().changeMap(player, this.map.mapIdNextMabu((short) this.mapId), -1, this.cx, this.cy);
                            }
                        }
                        case 2 -> {
                            if (!player.itemTime.isUseGTPT && player.fightMabu.pointMabu >= player.fightMabu.POINT_MAX && this.mapId != 120) {
                                ChangeMapService.gI().changeMap(player, this.map.mapIdNextMabu((short) this.mapId), -1, this.cx, this.cy);
                            }
                        }
                    }
                }
                case 127 -> {
                    switch (select) {
                        case 0 -> {
                            if (!player.isPhuHoMapMabu) {
                                if (player.inventory.getGemAndRuby() < 10) {
                                    Service.gI().sendThongBao(player, "Bạn không có đủ ngọc");
                                } else {
                                    player.inventory.subGemAndRuby(10);
                                    player.isPhuHoMapMabu = true;
                                    player.nPoint.calPoint();
                                    player.nPoint.setHp(Util.maxIntValue(player.nPoint.hpMax));
                                    player.nPoint.setMp(Util.maxIntValue( player.nPoint.mpMax));
                                    Service.gI().point(player);
                                    Service.gI().Send_Info_NV(player);
                                    Service.gI().Send_Caitrang(player);
                                }
                            }
                        }
                        case 1 -> {
                            if (player.isPhuHoMapMabu) {
                                ChangeMapService.gI().changeMap(player, 52, -1, Util.nextInt(100, 300), 336);
                            }
                        }
                        case 2 -> {
                            if (!player.isPhuHoMapMabu) {
                                ChangeMapService.gI().changeMap(player, 52, -1, Util.nextInt(100, 300), 336);
                            }
                        }
                    }
                }
            }
        }
    }
}
