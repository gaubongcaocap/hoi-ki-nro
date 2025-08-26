package models.ShenronEvent;

/*
 *
 *
 * @author NTD
 */

import consts.ConstNpc;
import item.Item;
import player.Player;
import services.InventoryService;
import services.ItemService;
import services.NpcService;
import services.Service;
import utils.Util;

public class ShenronEventService {

    private static ShenronEventService instance;

    public static final short NGOC_RONG_1_SAO = 702;
    public static final short NGOC_RONG_2_SAO = 703;
    public static final short NGOC_RONG_3_SAO = 704;
    public static final short NGOC_RONG_4_SAO = 705;
    public static final short NGOC_RONG_5_SAO = 706;
    public static final short NGOC_RONG_6_SAO = 707;
    public static final short NGOC_RONG_7_SAO = 708;

    public static ShenronEventService gI() {
        if (instance == null) {
            instance = new ShenronEventService();
        }
        return instance;
    }

    public void openMenuSummonShenron(Player pl, int type) {
        pl.iDMark.setShenronType(type);
        NpcService.gI().createMenuConMeo(pl, ConstNpc.SUMMON_SHENRON_EVENT, -1, "Bạn có muốn gọi Rồng Xương không ?",
                "Đồng ý", "Từ chối");
    }

    public void summonShenron(Player player) {
        if (player.zone.map.mapId != 0 && player.zone.map.mapId != 7 && player.zone.map.mapId != 14) {
            if (checkShenronBall(player)) {
                if (player.isShenronAppear || player.shenronEvent != null) {
                    Service.gI().sendThongBao(player, "Không thể thực hiện");
                    return;
                }

                if (Util.canDoWithTime(player.lastTimeShenronAppeared, ShenronEvent.timeResummonShenron)) {
                    for (int i = NGOC_RONG_1_SAO; i <= NGOC_RONG_7_SAO; i++) {
                        try {
                            InventoryService.gI().subQuantityItemsBag(player, InventoryService.gI().findItemBag(player, i), 1);
                        } catch (Exception ex) {
                        }
                    }
                    InventoryService.gI().sendItemBag(player);
                    ShenronEvent shenron = new ShenronEvent();
                    shenron.setPlayer(player);
                    ShenronEventManager.gI().add(shenron);
                    player.shenronEvent = shenron;
                    shenron.setZone(player.zone);
                    shenron.activeShenron(true, ShenronEvent.DRAGON_EVENT);
                    shenron.sendWhishesShenron();
                } else {
                    int timeLeft = (int) ((ShenronEvent.timeResummonShenron - (System.currentTimeMillis() - player.lastTimeShenronAppeared)) / 1000);
                    Service.gI().sendThongBao(player, "Vui lòng đợi " + (timeLeft < 7200 ? timeLeft + " giây" : timeLeft / 60 + " phút") + " nữa");
                }
            }
        } else {
            Service.gI().sendThongBao(player, "Không thể gọi rồng ở đây");
        }
    }

    private boolean checkShenronBall(Player pl) {
        for (int i = NGOC_RONG_1_SAO; i <= NGOC_RONG_7_SAO; i++) {
            if (!InventoryService.gI().isExistItemBag(pl, i)) {
                Item it = ItemService.gI().createNewItem((short) i);
                Service.gI().sendThongBao(pl, "Bạn còn thiếu 1 viên " + it.template.name);
                return false;
            }
        }
        return true;
    }
}
