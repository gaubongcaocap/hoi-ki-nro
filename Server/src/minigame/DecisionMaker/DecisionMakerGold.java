/*
 * Copyright by NTD
 */

package minigame.DecisionMaker;

import minigame.cost.DecisionMakerCost;
import minigame.cost.LuckyNumberCost;
import npc.Npc;
import npc.npc_manifest.LyTieuNuong;
import player.Player;
import services.Service;
import utils.Util;

public class DecisionMakerGold {

    public static void showMenuSelect(Npc npc, Player player) {
        long totalNormal = DecisionMakerService.getTotalMoney(DecisionMakerCost.VANG, true);
        long totalVIP = DecisionMakerService.getTotalMoney(DecisionMakerCost.VANG, false);
        npc.createOtherMenu(player, LyTieuNuong.ConstMiniGame.MENU_PLAY_DECISION_MAKER_GOLD,
                "Tổng giải thưởng: " + Util.mumberToLouis(totalNormal) + " vàng, cơ hội trúng của bạn là: " + DecisionMakerService.getPercent(player, DecisionMakerCost.VANG, true) + "%\n"
                        + "Tổng giải VIP: " + Util.mumberToLouis(totalVIP) + " vàng, cơ hội trúng của bạn là: " + DecisionMakerService.getPercent(player, DecisionMakerCost.VANG, false) + "%\n"
                        + "Thời gian còn lại: " + DecisionMakerCost.timeGame + " giây.",
                "Cập nhật",
                "Thường\n1 triệu\nvàng",
                "VIP\n10 triệu\nvàng",
                "Đóng"
        );
    }

    public static void selectPlay(Npc npc, Player player, boolean isNormal) {
        int money = isNormal ? DecisionMakerCost.COST_GOLD_NORMAL : DecisionMakerCost.COST_GOLD_VIP;
        if (player.inventory.gold < money) {
            Service.gI().sendThongBao(player, "Bạn không đủ vàng, còn thiếu " + (money - player.inventory.gold) + " vàng nữa");
            return;
        }
        player.inventory.gold -= money;
        Service.gI().sendMoney(player);
        DecisionMakerService.newData(player, money, DecisionMakerCost.VANG, isNormal);
        showMenuSelect(npc, player);
    }

}
