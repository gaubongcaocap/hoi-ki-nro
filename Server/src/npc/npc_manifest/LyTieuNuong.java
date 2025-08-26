package npc.npc_manifest;

/**
 * @author NTD
 */

import minigame.DecisionMaker.DecisionMaker;
import minigame.DecisionMaker.DecisionMakerGem;
import minigame.DecisionMaker.DecisionMakerGold;
import minigame.DecisionMaker.DecisionMakerRuby;
import minigame.LuckyNumber.LuckyNumber;
import minigame.LuckyNumber.LuckyNumberService;
import minigame.RockPaperScissors.RockPaperScissors;
import npc.Npc;
import player.Player;
import services.TaskService;
import services.func.Input;

public class LyTieuNuong extends Npc {

    public class ConstMiniGame {
        public static final byte MENU_CHINH = 0;
        public static final byte MENU_KEO_BUA_BAO = 1;
        public static final byte MENU_CON_SO_MAY_MAN_VANG = 2;
        public static final byte MENU_CON_SO_MAY_MAN_NGOC = 3;
        public static final byte MENU_CHON_AI_DAY = 4;

        public static final byte MENU_PLAY_KEO_BUA_BAO = 5;

        public static final byte MENU_LUCKY_NUMBER = 6;
        public static final byte MENU_PLAY_LUCKY_NUMBER_GOLD = 7;
        public static final byte MENU_PLAY_LUCKY_NUMBER_GEM = 8;

        public static final byte MENU_PLAY_DECISION_MAKER_GOLD = 9;
        public static final byte MENU_PLAY_DECISION_MAKER_RUBY = 10;
        public static final byte MENU_PLAY_DECISION_MAKER_GEM = 11;
        public static final byte MENU_WAIT_NEW_GAME = 12;
    }

    public LyTieuNuong(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
//        services.Service.gI().sendThongBaoOK(player, "Chức năng tạm đóng");
        if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
            createOtherMenu(player, ConstMiniGame.MENU_CHINH, "Mini game.", "Kéo\nBúa\nBao", "Con số\nmay mắn\nvàng", "Con số\nmay mắn\nngọc xanh", "Chọn ai đây", "Đóng");
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            switch (player.iDMark.getIndexMenu()) {
                case ConstMiniGame.MENU_CHINH:
                    switch (select) {
                        case 0: // Kéo búa bao
                            createOtherMenu(player, ConstMiniGame.MENU_KEO_BUA_BAO, "Hãy chọn mức cược.", "100 Tr vàng", "500 Tr vàng", "1 Tỷ vàng");
                            break;
                        case 1: // Con số may mắn vàng
                            LuckyNumber.showMenu(this, player, false);
                            player.iDMark.setGemCSMM(false);
                            break;
                        case 2: // Con số may mắn ngọc
                            LuckyNumber.showMenu(this, player, true);
                            player.iDMark.setGemCSMM(true);
                            break;
                        case 3: // Chọn ai đây
                            DecisionMaker.gI().showMenu(this, player);
                            break;
                        default:
                            break;
                    }
                    break;
                case ConstMiniGame.MENU_KEO_BUA_BAO:
                    RockPaperScissors.confirmMenu(this, player, select);
                    break;
                case ConstMiniGame.MENU_PLAY_KEO_BUA_BAO:
                    if (player.iDMark.getTimePlayKeoBuaBao() - System.currentTimeMillis() > 0) {
                        RockPaperScissors.confirmPlay(this, player, select);
                    } else {
                        createOtherMenu(player, ConstMiniGame.MENU_KEO_BUA_BAO, "Hãy chọn mức cược.", "100 Tr vàng", "500 Tr vàng", "1 Tỷ vàng");
                    }
                    break;
                case ConstMiniGame.MENU_CON_SO_MAY_MAN_VANG:

                    break;
                case ConstMiniGame.MENU_CON_SO_MAY_MAN_NGOC:

                    break;
                case ConstMiniGame.MENU_CHON_AI_DAY:
                    switch (select) {
                        case 0 -> DecisionMaker.gI().showTutorial(this, player);
                        case 1 -> DecisionMakerGold.showMenuSelect(this, player);
                        case 2 -> DecisionMakerRuby.showMenuSelect(this, player);
                        case 3 -> DecisionMakerGem.showMenuSelect(this, player);
                    }
                    break;
                case ConstMiniGame.MENU_LUCKY_NUMBER:
                    if (select == 0) {
                        LuckyNumber.showMenu(this, player, player.iDMark.isGemCSMM());
                    }
                    break;
                case ConstMiniGame.MENU_PLAY_LUCKY_NUMBER_GOLD, ConstMiniGame.MENU_PLAY_LUCKY_NUMBER_GEM:
                    switch (select) {
                        case 0:
                            LuckyNumber.showMenu(this, player, player.iDMark.isGemCSMM());
                            break;
                        case 1:
                            Input.gI().createFormSelectOneNumberLuckyNumber(player, player.iDMark.isGemCSMM());
                            break;
                        case 2:
                            LuckyNumberService.addOneNumber(player, true);
                            break;
                        case 3:
                            LuckyNumberService.addOneNumber(player, false);
                            break;
                        case 4:
                            LuckyNumber.showMenuTutorials(this, player);
                            break;
                        default:
                            break;
                    }
                    break;
                case ConstMiniGame.MENU_PLAY_DECISION_MAKER_GOLD:
                    switch (select) {
                        case 0 -> DecisionMakerGold.showMenuSelect(this, player);
                        case 1 -> DecisionMakerGold.selectPlay(this, player, true);
                        case 2 -> DecisionMakerGold.selectPlay(this, player, false);
                    }
                    break;
                case ConstMiniGame.MENU_PLAY_DECISION_MAKER_GEM:
                    switch (select) {
                        case 0 -> DecisionMakerGem.showMenuSelect(this, player);
                        case 1 -> DecisionMakerGem.selectPlay(this, player, true);
                        case 2 -> DecisionMakerGem.selectPlay(this, player, false);
                    }
                    break;
                case ConstMiniGame.MENU_PLAY_DECISION_MAKER_RUBY:
                    switch (select) {
                        case 0 -> DecisionMakerRuby.showMenuSelect(this, player);
                        case 1 -> DecisionMakerRuby.selectPlay(this, player, true);
                        case 2 -> DecisionMakerRuby.selectPlay(this, player, false);
                    }
                    break;
                case LyTieuNuong.ConstMiniGame.MENU_WAIT_NEW_GAME:
                    if (select == 0) {
                        DecisionMaker.gI().showTutorial(this, player);
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
