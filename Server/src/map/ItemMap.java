package map;

/*
 *
 *
 * @author NTD
 */
import java.util.ArrayList;
import java.util.List;
import models.Template.ItemTemplate;
import item.Item.ItemOption;
import java.util.HashMap;
import player.Player;
import utils.Util;
import services.ItemMapService;
import services.ItemService;
import services.MapService;
import services.PlayerService;
import services.Service;

public class ItemMap {
    private static java.util.Map<Long, java.util.Map<Integer, Integer>> dailyItemReceiveCount = new HashMap<>();
    public Zone zone;
    public int itemMapId;
    public ItemTemplate itemTemplate;
    public int quantity;

    public int x;
    public int y;
    public long playerId;
    public List<ItemOption> options;

    public long createTime;

    public int clanId = -1;
    public boolean isNamecBallTranhDoat;
    public byte typeHaveBallTranhDoat = -1;
    public boolean isBlackBall;
    public boolean isNamecBall;
    public boolean isPickedUp;

    public ItemMap(Zone zone, int tempId, int quantity, int x, int y, long playerId) {
        this.zone = zone;
        this.itemMapId = zone.countItemAppeaerd++;
        if (zone.countItemAppeaerd >= 2000000000) {
            zone.countItemAppeaerd = 0;
        }
        this.itemTemplate = ItemService.gI().getTemplate((short) tempId);
        this.quantity = quantity;
        this.x = x;
        this.y = y;
        this.playerId = playerId != -1 ? Math.abs(playerId) : playerId;
        this.createTime = System.currentTimeMillis();
        this.options = new ArrayList<>();
        this.isBlackBall = ItemMapService.gI().isBlackBall(this.itemTemplate.id);
        this.isNamecBall = ItemMapService.gI().isNamecBall(this.itemTemplate.id);
        this.lastTimeMoveToPlayer = System.currentTimeMillis();
        this.zone.addItem(this);
    }

    public ItemMap(Zone zone, ItemTemplate temp, int quantity, int x, int y, long playerId) {
        this.zone = zone;
        this.itemMapId = zone.countItemAppeaerd++;
        if (zone.countItemAppeaerd >= 2000000000) {
            zone.countItemAppeaerd = 0;
        }
        this.itemTemplate = temp;
        this.quantity = quantity;
        this.x = x;
        this.y = y;
        this.playerId = playerId != -1 ? Math.abs(playerId) : playerId;
        this.createTime = System.currentTimeMillis();
        this.options = new ArrayList<>();
        this.isBlackBall = ItemMapService.gI().isBlackBall(this.itemTemplate.id);
        this.isNamecBall = ItemMapService.gI().isNamecBall(this.itemTemplate.id);
        this.lastTimeMoveToPlayer = System.currentTimeMillis();
        this.zone.addItem(this);
    }

    public ItemMap(Zone zone, int tempId, int quantity, int x, int y, long playerId, int maxDaily) {
        this.zone = zone;

        if (!canReceiveItem(playerId, tempId, maxDaily)) {

            return;
        }

        this.itemMapId = zone.countItemAppeaerd++;
        if (zone.countItemAppeaerd >= 2000000000) {
            zone.countItemAppeaerd = 0;
        }
        this.itemTemplate = ItemService.gI().getTemplate((short) tempId);
        this.quantity = quantity;
        this.x = x;
        this.y = y;
        this.playerId = playerId != -1 ? Math.abs(playerId) : playerId;
        this.createTime = System.currentTimeMillis();
        this.options = new ArrayList<>();
        this.isBlackBall = ItemMapService.gI().isBlackBall(this.itemTemplate.id);
        this.isNamecBall = ItemMapService.gI().isNamecBall(this.itemTemplate.id);
        this.lastTimeMoveToPlayer = System.currentTimeMillis();

        updateDailyItemCount(playerId, tempId, quantity);

        this.zone.addItem(this);
    }

    /**
     *
     * @param playerId 
     * @param tempId   
     * @param quantity
     */
    private void updateDailyItemCount(long playerId, int tempId, int quantity) {
        dailyItemReceiveCount.putIfAbsent(playerId, new HashMap<>());
        java.util.Map<Integer, Integer> playerItems = dailyItemReceiveCount.get(playerId);
        int currentCount = playerItems.getOrDefault(tempId, 0);
        playerItems.put(tempId, currentCount + quantity);
    }


    public static void resetDailyItemCount() {
        dailyItemReceiveCount.clear();
        System.out.println("Đã reset vật phẩm trong ngày");
    }

    private boolean canReceiveItem(long playerId, int tempId, int maxDaily) {
        java.util.Map<Integer, Integer> playerItems = dailyItemReceiveCount.getOrDefault(playerId, new HashMap<>());
        int receivedCount = playerItems.getOrDefault(tempId, 0);
        return receivedCount < maxDaily;
    }

    public ItemMap(ItemMap itemMap) {
        this.zone = itemMap.zone;
        this.itemMapId = itemMap.itemMapId;
        this.itemTemplate = itemMap.itemTemplate;
        this.quantity = itemMap.quantity;
        this.x = itemMap.x;
        this.y = itemMap.y;
        this.playerId = itemMap.playerId;
        this.options = itemMap.options;
        this.isBlackBall = itemMap.isBlackBall;
        this.isNamecBall = itemMap.isNamecBall;
        this.lastTimeMoveToPlayer = itemMap.lastTimeMoveToPlayer;
        this.createTime = System.currentTimeMillis();
        this.zone.addItem(this);
    }

    public void update() {
        if (isNotNullItem()) {
            if (this.isBlackBall) {
                if (Util.canDoWithTime(lastTimeMoveToPlayer, timeMoveToPlayer)) {
                    if (this.zone != null && !this.zone.getPlayers().isEmpty()) {
                        Player player = this.zone.getPlayers().get(0);
                        if (player.zone != null && player.zone.equals(this.zone)) {
                            this.x = player.location.x;
                            this.y = this.zone.map.yPhysicInTop(this.x, player.location.y - 24);
                            reAppearItem();
                            this.lastTimeMoveToPlayer = System.currentTimeMillis();
                        }
                    }
                }
                return;
            }

            // ========================SATELLITE========================
            if (this.itemTemplate.type == 22) {
                satelliteUpdate();
            }
            if (Util.canDoWithTime(createTime, 45000)) {
                if (this.itemTemplate.type != 22 && this.itemTemplate.id != 726 && this.itemTemplate.id != 992) {
                    this.playerId = -1;
                }
            }
            if ((Util.canDoWithTime(createTime, 50000) && isNotNullItem() && itemTemplate.type != 22
                    || Util.canDoWithTime(createTime, 1800000)) && !this.isNamecBall) {
                if (this.zone != null && this.zone.map.mapId != 21 && this.zone.map.mapId != 22
                        && this.zone.map.mapId != 23 && this.itemTemplate.id != 78
                        && this.itemTemplate.id != 726 && !(MapService.gI().isMapDoanhTrai(this.zone.map.mapId)
                                && this.itemTemplate.id >= 14 && this.itemTemplate.id <= 20)) {
                    ItemMapService.gI().removeItemMapAndSendClient(this);
                }
            }
            // ========================DHVT ITEM 726========================
            if (this.zone != null && this.zone.map.mapId == 52 && isNotNullItem() && this.itemTemplate.id == 726) {
                if (!findPlayerByID(this.playerId)) {
                    ItemMapService.gI().removeItemMapAndSendClient(this);
                }
            }
            if (this.zone != null && isNotNullItem() && this.itemTemplate.id == 460 && this.playerId == 123456789
                    && Util.canDoWithTime(createTime, 5000)) {
                ItemMapService.gI().removeItemMapAndSendClient(this);
            }
        }
    }

    private boolean findPlayerByID(long id) {
        for (Player pl : this.zone.getPlayers()) {
            if (pl.id == id) {
                return true;
            }
        }
        return false;
    }

    private void satelliteUpdate() {
        for (Player pl : this.zone.getPlayers()) {
            if (!pl.isDie() && Util.getDistance(pl.location.x, pl.location.y, x, y) < 200 && pl.satellite != null
                    && (pl.id == this.playerId || this.clanId != -1 && pl.clan != null && pl.clan.id == this.clanId)) {
                switch (this.itemTemplate.id) {
                    case 342 -> {
                        if (!pl.satellite.isMP) {
                            pl.satellite.isMP = true;
                            pl.satellite.lastMPTime = System.currentTimeMillis();
                            if (pl.nPoint.mp < pl.nPoint.mpMax) {
                                pl.nPoint.addMp(pl.nPoint.mpMax / 10);
                                PlayerService.gI().sendInfoMp(pl);
                            }
                        }
                    }
                    case 343 -> {
                        if (!pl.satellite.isIntelligent) {
                            pl.satellite.isIntelligent = true;
                            pl.satellite.lastIntelligentTime = System.currentTimeMillis();
                        }
                    }
                    case 344 -> {
                        if (!pl.satellite.isDefend) {
                            pl.satellite.isDefend = true;
                            pl.satellite.lastDefendTime = System.currentTimeMillis();
                        }
                    }
                    case 345 -> {
                        if (!pl.satellite.isHP) {
                            pl.satellite.isHP = true;
                            pl.satellite.lastHPTime = System.currentTimeMillis();
                            if (pl.nPoint.hp < pl.nPoint.hpMax) {
                                pl.nPoint.addHp(pl.nPoint.hpMax / 10);
                                PlayerService.gI().sendInfoHp(pl);
                                Service.gI().Send_Info_NV(pl);
                            }
                        }
                    }
                }
            }
        }
    }

    private final int timeMoveToPlayer = 10000;
    private long lastTimeMoveToPlayer;

    private void reAppearItem() {
        ItemMapService.gI().sendItemMapDisappear(this);
        Service.gI().dropItemMap(this.zone, this);
    }

    public boolean isNotNullItem() {
        return itemTemplate != null;
    }

    public void dispose() {
        this.zone = null;
        this.itemTemplate = null;
        this.options = null;
    }
}
