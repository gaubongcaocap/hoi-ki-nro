/*
 * Copyright by NTD
 */

package minigame.DecisionMaker;

import minigame.cost.DecisionMakerCost;
import npc.Npc;
import npc.npc_manifest.LyTieuNuong;
import player.Player;
import services.Service;
import utils.Util;

public class DecisionMakerGem {
    public static void showMenuSelect(Npc npc, Player player) {
        long totalNormal = DecisionMakerService.getTotalMoney(DecisionMakerCost.NGOC_XANH, true);
        long totalVIP = DecisionMakerService.getTotalMoney(DecisionMakerCost.NGOC_XANH, false);
        npc.createOtherMenu(player, LyTieuNuong.ConstMiniGame.MENU_PLAY_DECISION_MAKER_GEM,
                "Tổng giải thưởng: " + Util.mumberToLouis(totalNormal) + " hồng ngọc, cơ hội trúng của bạn là: " + DecisionMakerService.getPercent(player, DecisionMakerCost.NGOC_XANH, true) + "%\n"
                        + "Tổng giải VIP: " + Util.mumberToLouis(totalVIP) + " hồng ngọc, cơ hội trúng của bạn là: " + DecisionMakerService.getPercent(player, DecisionMakerCost.NGOC_XANH, false) + "%\n"
                        + "Thời gian còn lại: " + DecisionMakerCost.timeGame + " giây.",
                "Cập nhật",
                "Thường\n10 ngọc\nxanh",
                "VIP\n100 ngọc\nxanh",
                "Đóng"
        );
    }

    public static void selectPlay(Npc npc, Player player, boolean isNormal) {
        int money = isNormal ? DecisionMakerCost.COST_GEM_NORMAL : DecisionMakerCost.COST_GEM_VIP;
        if (player.inventory.gem < money) {
            Service.gI().sendThongBao(player, "Bạn không đủ ngọc, còn thiếu " + (money - player.inventory.gem) + " ngọc nữa");
            return;
        }
        player.inventory.gem -= money;
        Service.gI().sendMoney(player);
        DecisionMakerService.newData(player, money, DecisionMakerCost.NGOC_XANH, isNormal);
        showMenuSelect(npc, player);
    }
}
