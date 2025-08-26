
/*
 * Copyright by NTD
 */
package minigame.RockPaperScissors;

import consts.ConstFont;
import npc.Npc;
import npc.npc_manifest.LyTieuNuong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import player.Player;
import services.ItemTimeService;
import services.Service;
import utils.Util;

public class RockPaperScissors {

    public static final byte KEO = 0;
    public static final byte BUA = 1;
    public static final byte BAO = 2;
    private static final Logger log = LoggerFactory.getLogger(RockPaperScissors.class);

    public static long timePlay = 15; // có 15 giây để chơi

    public static int COST_0 = 100_000_000;
    public static int COST_1 = 500_000_000;
    public static int COST_2 = 1_000_000_000;

    public static void confirmMenu(Npc npc, Player player, int select) { // xử lý chọn menu => chuyển qua một menu mới
        int tiendatcuoc = (select == 0 ? COST_0 : select == 1 ? COST_1 : COST_2);
        String money = Util.numberFormatLouis(tiendatcuoc);
        player.iDMark.setMoneyKeoBuaBao(tiendatcuoc);
        player.iDMark.setTimePlayKeoBuaBao(System.currentTimeMillis() + (timePlay * 1000));
        ItemTimeService.gI().sendTextTimeKeoBuaBao(player, (int) timePlay);
        npc.createOtherMenu(player, LyTieuNuong.ConstMiniGame.MENU_PLAY_KEO_BUA_BAO,
                ConstFont.BOLD_GREEN + "Mức vàng cược: " + money + "\n"
                + ConstFont.BOLD_DARK + "Hãy chọn Kéo, Búa hoặc Bao\n"
                + ConstFont.BOLD_RED + "Thời gian " + timePlay + " giây bắt đầu",
                "Kéo", "Búa", "Bao", "Đổi\nmức cược", "Nghỉ chơi");
    }

    public static void confirmPlay(Npc npc, Player player, int select) {
        switch (select) {
            case 0, 1, 2:
                if (player.inventory.gold < player.iDMark.getMoneyKeoBuaBao()) {
                    long soVangConThieu = player.iDMark.getMoneyKeoBuaBao() - player.inventory.gold;
                    Service.gI().sendThongBao(player, "Bạn không đủ vàng, còn thiếu " + Util.numberToMoney(soVangConThieu) + " vàng nữa");
                    return;
                }
                player.iDMark.setKeoBuaBaoPlayer((byte) select);
                player.iDMark.setKeoBuaBaoServer((byte) Util.nextInt(2));
                if (RockPaperScissorsService.checkWinLose(player) == 1) { // xử lý thắng
                    RockPaperScissorsService.winKeoBuaBao(npc, player);
                } else if (RockPaperScissorsService.checkWinLose(player) == 2) { // xử lý thua
                    RockPaperScissorsService.loseKeoBuaBao(npc, player);
                } else { // xử lý hoà
                    RockPaperScissorsService.hoaKeoBuaBao(npc, player);
                }
                break;
            case 3:
                npc.createOtherMenu(player, LyTieuNuong.ConstMiniGame.MENU_KEO_BUA_BAO,
                        "Hãy chọn mức cược.",
                        "100 Tr vàng",
                        "500 Tr vàng",
                        "1 Tỷ vàng");
                break;
            default:
                break;
        }
    }
}
