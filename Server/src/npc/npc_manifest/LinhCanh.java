package npc.npc_manifest;

/**
 *
 * @author NTD
 */
import consts.ConstNpc;
import models.RedRibbonHQ.RedRibbonHQ;
import models.RedRibbonHQ.RedRibbonHQService;
import npc.Npc;
import player.Player;
import services.NpcService;
import utils.TimeUtil;
import utils.Util;

public class LinhCanh extends Npc {

    public LinhCanh(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            if (player.clan == null) {
                NpcService.gI().createTutorial(player, tempId, this.avartar,
                        "Chỉ tiếp các bang hội, miễn tiếp khách vãng lai");
                return;
            }
            if (player.clan.getMembers().size() < RedRibbonHQ.N_PLAYER_CLAN) {
                NpcService.gI().createTutorial(player, tempId, this.avartar,
                        "Bang hội phải có ít nhất 5 thành viên mới có thể tham gia");
                return;
            }
            if (player.clanMember.getNumDateFromJoinTimeToToday() < 1) {
                NpcService.gI().createTutorial(player, tempId, this.avartar,
                        "Gia nhập bang hội trên 2 ngày mới được tham gia");
                return;
            }
            if (player.clan.doanhTrai != null) {
                createOtherMenu(player, ConstNpc.MENU_JOIN_DOANH_TRAI,
                        "Bang hội của ngươi đang đánh trại độc nhãn\nThời gian còn lại là "
                        + TimeUtil.getTimeLeft(player.clan.doanhTrai.getLastTimeOpen(), RedRibbonHQ.TIME_DOANH_TRAI / 1000)
                        + ". Ngươi có muốn tham gia không?",
                        "Tham gia", "Không", "Hướng\ndẫn\nthêm");
                return;
            }
            int nPlSameClan = 0;
            for (Player pl : player.zone.getPlayers()) {
                if (!pl.equals(player) && pl.clan != null
                        && pl.clan.equals(player.clan) && pl.location.x >= 1285
                        && pl.location.x <= 1645) {
                    nPlSameClan++;
                }
            }
            if (nPlSameClan < RedRibbonHQ.N_PLAYER_MAP) {
                createOtherMenu(player, ConstNpc.IGNORE_MENU,
                        "Ngươi phải có ít nhất " + RedRibbonHQ.N_PLAYER_MAP + " đồng đội cùng bang đứng gần mới có thể vào\n"
                        + "tuy nhiên ta khuyên ngươi nên đi cùng với 3-4 người để khỏi chết. "
                        + "Hahaha.", "OK", "Hướng\ndẫn\nthêm");
                return;
            }
            if (player.clan.haveGoneDoanhTrai && !Util.isAfterMidnight(player.clan.lastTimeOpenDoanhTrai)) {
                if (!Util.isAfterMidnight(player.lastTimeJoinDT)) {
                    NpcService.gI().createTutorial(player, tempId, this.avartar,
                            "Hôm nay bạn đã tham gia doanh trại rồi, hẹn gặp bạn vào ngày mai");
                    return;
                }
                createOtherMenu(player, ConstNpc.IGNORE_MENU,
                        "Bang hội của ngươi ngày hôm nay đã vào 1 lần rồi (thành viên " + player.clan.playerOpenDoanhTrai.name + ") lúc " + TimeUtil.formatTime(player.clan.lastTimeOpenDoanhTrai, "HH:mm") + "\n"
                        + "Nên ngươi không thể vào được nữa.\n"
                        + "Hãy chờ đến ngày mai để có thể vào miễn phí", "OK", "Hướng\ndẫn\nthêm");
                return;
            }
            createOtherMenu(player, ConstNpc.MENU_JOIN_DOANH_TRAI,
                    "Hôm nay bang hội của ngươi chưa vào trại lần nào. Ngươi có muốn vào\nkhông?\nĐể vào, ta khuyên ngươi nên có 3-4 người cùng bang đi cùng.",
                    "Vào\n(miễn phí)", "Không", "Hướng\ndẫn\nthêm");
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            switch (player.iDMark.getIndexMenu()) {
                case ConstNpc.MENU_JOIN_DOANH_TRAI -> {
                    if (select == 0) {
                        RedRibbonHQService.gI().joinDoanhTrai(player);
                    } else if (select == 2) {
                        NpcService.gI().createTutorial(player, tempId, this.avartar, ConstNpc.HUONG_DAN_DOANH_TRAI);
                    }
                }
                case ConstNpc.IGNORE_MENU -> {
                    if (select == 1) {
                        NpcService.gI().createTutorial(player, tempId, this.avartar, ConstNpc.HUONG_DAN_DOANH_TRAI);
                    }
                }
            }
        }
    }
}
