/*
 * Copyright by NTD
 */
package minigame.RockPaperScissors;

import consts.ConstFont;
import npc.Npc;
import npc.npc_manifest.LyTieuNuong;
import player.Player;
import services.Service;
import utils.Util;

public class RockPaperScissorsService {

    public static void loseKeoBuaBao(Npc npc, Player player) {
        String ketQuaPlayer = convertNumberToString(player.iDMark.getKeoBuaBaoPlayer());
        String ketQuaServer = convertNumberToString(player.iDMark.getKeoBuaBaoServer());
        String money = Util.numberFormatLouis(player.iDMark.getMoneyKeoBuaBao());
        npc.createOtherMenu(player, LyTieuNuong.ConstMiniGame.MENU_PLAY_KEO_BUA_BAO,
                ConstFont.BOLD_RED + "Bạn ra cái <" + ketQuaPlayer + ">\n"
                + "Tôi ra cái <" + ketQuaServer + ">\n"
                + ConstFont.BOLD_DARK + "Tôi thắng nhé hihi\n"
                + ConstFont.BOLD_RED + "Bạn bị trừ " + money + " vàng",
                "Kéo", "Búa", "Bao", "Đổi\nmức cược", "Nghỉ chơi");
        player.inventory.gold -= player.iDMark.getMoneyKeoBuaBao();
        Service.gI().sendMoney(player);
    }

    public static void winKeoBuaBao(Npc npc, Player player) {
        String ketQuaPlayer = convertNumberToString(player.iDMark.getKeoBuaBaoPlayer());
        String ketQuaServer = convertNumberToString(player.iDMark.getKeoBuaBaoServer());
        String money = Util.numberFormatLouis(player.iDMark.getMoneyKeoBuaBao());
        npc.createOtherMenu(player, LyTieuNuong.ConstMiniGame.MENU_PLAY_KEO_BUA_BAO,
                ConstFont.BOLD_GREEN + "Bạn ra cái <" + ketQuaPlayer + ">\n"
                + "Tôi ra cái <" + ketQuaServer + ">\n"
                + ConstFont.BOLD_DARK + "Bạn thắng rồi huhu\n"
                + ConstFont.BOLD_GREEN + "Bạn nhận được " + money + " vàng",
                "Kéo", "Búa", "Bao", "Đổi\nmức cược", "Nghỉ chơi");
        player.inventory.gold += player.iDMark.getMoneyKeoBuaBao();
        Service.gI().sendMoney(player);
    }

    public static void hoaKeoBuaBao(Npc npc, Player player) {
        String ketQuaPlayer = convertNumberToString(player.iDMark.getKeoBuaBaoPlayer());
        String ketQuaServer = convertNumberToString(player.iDMark.getKeoBuaBaoServer());
        String money = Util.numberFormatLouis(player.iDMark.getMoneyKeoBuaBao());
        npc.createOtherMenu(player, LyTieuNuong.ConstMiniGame.MENU_PLAY_KEO_BUA_BAO,
                ConstFont.BOLD_BLUE + "Bạn ra cái <" + ketQuaPlayer + ">\n"
                + "Tôi ra cái <" + ketQuaServer + ">\n"
                + ConstFont.BOLD_YELLOW + "Hoà nhau nhé haha",
                "Kéo", "Búa", "Bao", "Đổi\nmức cược", "Nghỉ chơi");
    }

    public static String convertNumberToString(int i) {
        switch (i) {
            case 0:
                return "Kéo";
            case 1:
                return "Búa";
            case 2:
                return "Bao";
        }
        return "";
    }

    public static int checkWinLose(Player player) { // 1 là win, 2 là thua, 3 là hoà
        if (player.iDMark.getKeoBuaBaoPlayer() == player.iDMark.getKeoBuaBaoServer()) {
            return 3;
        }
        switch (player.iDMark.getKeoBuaBaoPlayer()) {
            case RockPaperScissors.KEO:
                if (player.iDMark.getKeoBuaBaoServer() == RockPaperScissors.BUA) {
                    return 2;
                } else if (player.iDMark.getKeoBuaBaoServer() == RockPaperScissors.BAO) {
                    return 1;
                }
                break;
            case RockPaperScissors.BUA:
                if (player.iDMark.getKeoBuaBaoServer() == RockPaperScissors.KEO) {
                    return 1;
                } else if (player.iDMark.getKeoBuaBaoServer() == RockPaperScissors.BAO) {
                    return 2;
                }
                break;
            case RockPaperScissors.BAO:
                if (player.iDMark.getKeoBuaBaoServer() == RockPaperScissors.KEO) {
                    return 2;
                } else if (player.iDMark.getKeoBuaBaoServer() == RockPaperScissors.BUA) {
                    return 1;
                }
                break;
        }
        return 2;
    }
}
