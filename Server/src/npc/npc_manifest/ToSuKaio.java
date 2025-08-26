package npc.npc_manifest;

/**
 *
 * @author NTD
 */

import boss.BossID;
import consts.ConstNpc;
import models.Training.TrainingService;
import npc.Npc;
import player.Player;
import services.NpcService;
import utils.Util;

public class ToSuKaio extends Npc {

    public ToSuKaio(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            this.createOtherMenu(player, ConstNpc.BASE_MENU, "Tập luyện với Tổ sư Kaio sẽ tăng " + Util.chiaNho(TrainingService.gI().getTnsmMoiPhut(player)) + " sức mạnh mỗi phút, có thể tăng giảm tùy vào khả năng đánh quái của con",
                    player.dangKyTapTuDong ? "Hủy đăng\nký tập\ntự động" : "Đăng ký\ntập\ntự động", "Đồng ý\nluyện tập", "Không\nđồng ý", "Nâng\nGiới hạn\nSức mạnh");
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (player.iDMark.isBaseMenu()) {
                switch (select) {
                    case 0 -> {
                        if (player.dangKyTapTuDong) {
                            player.dangKyTapTuDong = false;
                            NpcService.gI().createTutorial(player, tempId, avartar, "Con đã hủy thành công đăng ký tập tự động\ntừ giờ con muốn tập Offline hãy tự đến đây trước");
                            return;
                        }
                        this.createOtherMenu(player, 2001, "Đăng ký để mỗi khi Offline quá 30 phút, con sẽ được tự động luyện tập với tốc độ " + TrainingService.gI().getTnsmMoiPhut(player) + " sức mạnh mỗi phút",
                                "Hướng\ndẫn\nthêm", "Đồng ý\n1 ngọc\nmỗi lần", "Không\nđồng ý");
                    }
                    case 1 ->
                        TrainingService.gI().callBoss(player, BossID.TO_SU_KAIO, false);
                    default -> {
                    }
                }
            } else if (player.iDMark.getIndexMenu() == 2001) {
                switch (select) {
                    case 0 ->
                        NpcService.gI().createTutorial(player, tempId, avartar, ConstNpc.TAP_TU_DONG);
                    case 1 -> {
                        player.mapIdDangTapTuDong = mapId;
                        player.dangKyTapTuDong = true;
                        NpcService.gI().createTutorial(player, tempId, avartar, "Từ giờ, quá 30 phút Offline con sẽ được tự động luyện tập");
                    }
                }
            }
        }
    }
}
