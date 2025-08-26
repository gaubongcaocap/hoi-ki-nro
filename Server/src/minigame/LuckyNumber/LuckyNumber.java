/*
 * Copyright by NTD
 */
package minigame.LuckyNumber;

import EMTI.Functions;
import consts.ConstNpc;
import jdbc.daos.NDVSqlFetcher;
import minigame.cost.LuckyNumberCost;
import npc.Npc;
import npc.npc_manifest.LyTieuNuong;
import player.Player;
import server.Client;
import server.Maintenance;
import services.Service;
import utils.Util;

import java.util.*;

public class LuckyNumber implements Runnable {

    private static LuckyNumber instance;

    public static LuckyNumber gI() {
        if (instance == null) {
            instance = new LuckyNumber();
        }
        return instance;
    }

    public static boolean spinGame;
    public static int RESULT = Util.nextInt(0, 99);

    public static List<LuckNumberData> players = new ArrayList<>();

    static {
        LuckyNumberCost.timeGame = LuckyNumberCost.timeGameDefaule;
        LuckyNumberCost.timeDelay = 0;
        spinGame = true;
    }

    public static final List<Integer> DATA_RESULT = new ArrayList<>(); // xử lý lưu kết quả
    public static List<String> DATA_PLAYER_RESULT = new ArrayList<>(); // xử lý lưu tên người thắng

    public static final List<LuckNumberData.LuckyNumberResul> DATA_REWARD_PLAYER_WIN = new ArrayList<>();

    public static boolean isOpen() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        return (hour >= 8 && hour < 22);
    }

    @Override
    public void run() {
        while (!Maintenance.isRunning) {
            try {
                if (isOpen()) {
                    if (LuckyNumberCost.timeGame > 0) { // xử lý đếm ngươc thời gian về 0
                        LuckyNumberCost.timeGame--;
                        rewardPlayerWin();
                    }
                    if (LuckyNumberCost.timeGame == 0 && spinGame) {
                        spinGame();
                    }
                    if (LuckyNumberCost.timeDelay > 0) {
                        LuckyNumberCost.timeDelay--;
                        if (LuckyNumberCost.timeDelay == 0) {
                            rewardWinGame();
                        }
                    }
                   Functions.sleep(1000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void showMenu(Npc npc, Player player, boolean isGem) {
        if (!isOpen()) {
            npc.createOtherMenu(player, LyTieuNuong.ConstMiniGame.MENU_LUCKY_NUMBER,
                    "Thời gian từ 8h đến hết 21h59 hằng ngày\n"
                    + "Mỗi lượt đợc chọn 10 con số từ 0 đến 99\n"
                    + "Thời gian mỗi lượt là 5 phút.",
                    "Cập nhật", "Đóng");
        } else {
            if (isGem) { // nếu chọn là ngọc thì xử lý ở đây
                LuckyNumberGem.showMenuCSMM(npc, player);
            } else { // nếu chọn là vàng thì xử lý ở đây
                LuckyNumberGold.showMenuCSMM(npc, player);
            }
        }
    }

    public static void showMenuTutorials(Npc npc, Player player) {
        npc.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                "Thời gian từ 8h đến hết 21h59 hằng ngày\n"
                + "Mỗi lượt đợc chọn 10 con số từ 0 đến 99\n"
                + "Thời gian mỗi lượt là 5 phút.",
                "Đóng");
    }

    public static void spinGame() {
        LuckyNumberCost.timeDelay = 10;
        spinGame = false;
        players.forEach((g) -> {
            Player player = Client.gI().getPlayerByID((int) g.id);
            if (player != null) {
//                LuckyNumberService.spinNumber(player, String.valueOf(RESULT), "Con số may mắn là " + RESULT);
                Service.gI().showYourNumber(player, "cccccc", String.valueOf(RESULT) + "", "Con số may mắn là " + RESULT, 1);
                checkPlayerWin(g.id);
            }
        });
    }

    public static void rewardWinGame() {
        DATA_PLAYER_RESULT.clear();
        resetGame();
    }

    public static void checkPlayerWin(long id) {
        String finish = "Con số chúng thưởng là " + RESULT + " chúc bạn may mắn lần sau";
        for (LuckNumberData g : players) {
            Player player = NDVSqlFetcher.loadById(g.id);
            if (id == g.id && g.number == RESULT && !g.isReward && player != null) {
                if (g.isGem) {
                    finish = "Chúc mừng " + player.name + " đã thắng " + LuckyNumberCost.costGem + " ngọc với con số may mắn " + RESULT;
                    LuckyNumberService.addDataResul(player, g.number, LuckyNumberCost.costGem, finish);
                    g.isReward = true;
                } else {
                    finish = "Chúc mừng " + player.name + " đã thắng " + Util.mumberToLouis(LuckyNumberCost.costGold) + " vàng với con số may mắn " + RESULT;
                    LuckyNumberService.addDataResul(player, g.number, LuckyNumberCost.costGold, finish);
                    g.isReward = true;
                }
                DATA_PLAYER_RESULT.add(player.name);
                Service.gI().sendMoney(player);
                return;
            }
        }
    }

    public static void rewardPlayerWin() {
        if (!DATA_REWARD_PLAYER_WIN.isEmpty()) {
            for (int i = DATA_REWARD_PLAYER_WIN.size() - 1; i >= 0; i--) {
                LuckNumberData.LuckyNumberResul g = DATA_REWARD_PLAYER_WIN.get(i);
                System.out.println("id: " + DATA_REWARD_PLAYER_WIN.get(i).id);
                Player player = Client.gI().getPlayer(g.id);
                if (player != null) {
                    System.out.println("DONE");
                    if (g.money == 450) {
                        player.inventory.gem += g.money;
                    } else {
                        player.inventory.gold += g.money;
                    }
                    Service.gI().sendThongBao(player, g.text);
                    Service.gI().sendMoney(player);
                    DATA_REWARD_PLAYER_WIN.remove(g);
                }
            }
        }
    }

    public static void resetGame() {
        LuckyNumberCost.timeGame = LuckyNumberCost.timeGameDefaule;
        spinGame = true;
        DATA_RESULT.add(RESULT);
        players.clear();
        RESULT = Util.nextInt(0, 99);
    }
}
