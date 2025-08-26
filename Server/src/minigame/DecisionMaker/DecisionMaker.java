/*
 * Copyright by NTD
 */
package minigame.DecisionMaker;

import EMTI.Functions;
import consts.ConstNpc;
import jdbc.DBConnecter;
import jdbc.NDVResultSet;
import jdbc.daos.NDVSqlFetcher;
import jdbc.daos.PlayerDAO;
import npc.Npc;
import npc.npc_manifest.LyTieuNuong;
import player.Player;
import server.Client;
import server.Maintenance;
import server.ServerManager;
import minigame.cost.*;
import utils.Util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DecisionMaker implements Runnable {

    private static DecisionMaker instance;

    public static DecisionMaker gI() {
        if (instance == null) {
            instance = new DecisionMaker();
        }
        return instance;
    }

    public static boolean spinGame;
    public static boolean delayNewGame;

    static {
        DecisionMakerCost.timeGame = DecisionMakerCost.timeGameDefalue;
        spinGame = true;
        delayNewGame = false;
    }

    public List<DecisionMakerData> listPlayer = new ArrayList<>();
    public List<DecisionMakerData.resulPlayer> listResulPlayer = new ArrayList<>();

    @Override
    public void run() {
        while (!Maintenance.isRunning) {
            try {
                if (DecisionMakerCost.timeGame > 0) {
                    DecisionMakerCost.timeGame--;
                }
                if (DecisionMakerCost.timeGame == 0 && spinGame) {
                    spinGame();
                }
                if (DecisionMakerCost.timeDelay > 0) {
                    DecisionMakerCost.timeDelay--;
                    if (DecisionMakerCost.timeDelay == 0) {
                        resetNewGame();
                    }
                }
                Functions.sleep(1000);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void spinGame() { // xử lý random người chơi win
        playerWin(DecisionMakerCost.VANG, true);
        playerWin(DecisionMakerCost.VANG, false);

        playerWin(DecisionMakerCost.NGOC_XANH, true);
        playerWin(DecisionMakerCost.NGOC_XANH, false);

        playerWin(DecisionMakerCost.HONG_NGOC, true);
        playerWin(DecisionMakerCost.HONG_NGOC, false);
        spinGame = false;
        delayNewGame = true;
        DecisionMakerCost.timeDelay = 60;
    }

    public void resetNewGame() {
        DecisionMakerCost.timeGame = DecisionMakerCost.timeGameDefalue;
        spinGame = true;
        delayNewGame = false;
    }

    public void playerWin(byte TYPE, boolean isNormal) {
        List<DecisionMakerData> listPl = new ArrayList<>();
        if (!listPlayer.isEmpty()) {
            listPlayer.sort(Comparator.comparingInt(o -> Math.toIntExact(o.money)));
            for (DecisionMakerData player : listPlayer) {
                if (player.type == TYPE && player.isNormal == isNormal) {
                    listPl.add(player);
                }
            }
        }
        if (!listPl.isEmpty()) {
            int index = Util.nextInt(0, listPl.size() - 1);
            long playerId = listPl.get(index).id;
            Player player = NDVSqlFetcher.loadById(playerId);
            DecisionMakerService.newDataResul(player, TYPE, listPl.get(index).money);
        }
    }

    public void showMenuWaitNewGame(Npc npc, Player player) {
        String npcSay = "Chúc mừng các bạn may mắn được chọn lần trước là";
        for (DecisionMakerData.resulPlayer pl : listResulPlayer) {
            String giatri = (pl.type == DecisionMakerCost.VANG ? " vàng" : " hồng ngọc");
            npcSay += "\n" + pl.name + " +" + Util.mumberToLouis(pl.money) + giatri;
        }
        npcSay += "\nTrò chơi sẽ bắt đầu sau: " + DecisionMakerCost.timeDelay + " giây nữa.";
        npc.createOtherMenu(player, LyTieuNuong.ConstMiniGame.MENU_WAIT_NEW_GAME, npcSay, "Thể lệ", "OK");
    }

    public void showMenu(Npc npc, Player player) {
        if (DecisionMaker.delayNewGame && DecisionMakerCost.timeDelay > 0) {
            DecisionMaker.gI().showMenuWaitNewGame(npc, player);
            return;
        }
        npc.createOtherMenu(player, LyTieuNuong.ConstMiniGame.MENU_CHON_AI_DAY,
                "Trò chơi Chọn Ai Đây đang được diễn ra, nếu bạn tin tưởng mình đang tràn đầy may mắn thì có thể tham gia thử.",
                "Thể lệ",
                "Chọn\nVàng",
                "Chọn\nhồng ngọc",
                "Chọn\nngọc xanh");
    }

    public void showTutorial(Npc npc, Player player) {
        npc.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                "Mỗi lượt chơi có 6 giải thưởng\n"
                + "Được chọn tối đa 10 lần mỗi giải\n"
                + "Thời gian 1 lượt chọn là 5 phút\n"
                + "Khi hết giờ, hệ thống sẽ ngẫu nhiên chọn ra 1 người may mắn\n"
                + "của từng giải và trao thưởng.\n"
                + "Lưu ý: Nếu tham gia trò chơi bằng Ngọc xanh hoặc Hồng ngọc\n"
                + "thì người thắng sẽ nhận được là hồng ngọc.",
                "OK");
    }

}
