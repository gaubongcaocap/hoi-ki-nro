package map;

/*
 *
 *
 * @author NTD
 */
import consts.ConstTask;
import boss.Boss;
import boss.BossID;
import item.Item;
import mob.Mob;
import npc.Npc;
import npc.NpcManager;
import player.Player;
import network.Message;
import boss.boss_manifest.Training.TrainingBoss;
import consts.ConstItem;
import consts.ConstMob;
import consts.ConstNpc;
import consts.ConstTranhNgocNamek;
import consts.cn;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import services.ItemMapService;
import services.ItemService;
import services.MapService;
import services.PlayerService;
import services.Service;
import services.TaskService;
import services.InventoryService;
import utils.FileIO;
import utils.Logger;
import utils.Util;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import models.DragonNamecWar.TranhNgoc;
import models.DragonNamecWar.TranhNgocService;
import npc.NonInteractiveNPC;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import power.CaptionManager;

public class Zone {

    public static final byte PLAYERS_TIEU_CHUAN_TRONG_MAP = 7;

    public int countItemAppeaerd = 0;

    public Map map;
    public int zoneId;
    public int maxPlayer;
    public int shenronType = -1;

    private final List<Player> noninteractivenpcs; //npc
    private final List<Player> humanoids; //player, boss, pet
    private final List<Player> notBosses; //player, pet
    private final List<Player> players; //player
    private final List<Player> bosses; //boss
    private final List<Player> pets; //pet

    public final List<Mob> mobs;
    public final List<ItemMap> items;

    public long lastTimeDropBlackBall;
    public boolean finishBlackBallWar;
    public boolean finishMapMaBu;

    public boolean isbulon1Alive = true;
    public boolean isbulon2Alive = true;
    public boolean isTUTAlive = true;
    public boolean isGoldenFriezaAlive;

    public boolean isCompeting;
    public String rankName1;
    public String rankName2;
    public int rank1;
    public int rank2;

    public List<TrapMap> trapMaps;
    public List<MaBuHold> maBuHolds;
    //tranh ngọc namek
    public int pointRed;
    public int pointBlue;
    private final List<Player> playersRed;
    private final List<Player> playersBlue;
    public long lastTimeStartTranhNgoc;
    public boolean startZoneTranhNgoc;
    public long lastTimeDropBall;

    public List<Player> getPlayersBlue() {
        return this.playersBlue;
    }

    public List<Player> getPlayersRed() {
        return this.playersRed;
    }

    public void addPlayersBlue(Player player) {
        synchronized (playersBlue) {
            if (!this.playersBlue.contains(player)) {
                this.playersBlue.add(player);
            }
        }
    }

    public void addPlayersRed(Player player) {
        synchronized (playersRed) {
            if (!this.playersRed.contains(player)) {
                this.playersRed.add(player);
            }
        }
    }

    public void removePlayersBlue(Player player) {
        synchronized (playersBlue) {
            if (this.playersBlue.contains(player)) {
                this.playersBlue.remove(player);
            }
        }
    }

    public void removePlayersRed(Player player) {
        synchronized (playersRed) {
            if (this.playersRed.contains(player)) {
                this.playersRed.remove(player);
            }
        }
    }

    @Setter
    @Getter
    public Player Npc;

    public boolean isFullPlayer() {
        return this.players.size() >= this.maxPlayer;
    }

    public boolean isFullPlayer1() {
        return this.players.size() >= this.maxPlayer - 5;
    }

    private void udMob() {
        for (int i = this.mobs.size() - 1; i >= 0; i--) {
            try {
                mobs.get(i).update();
            } catch (Exception e) {
                Logger.logException(Zone.class, e, "Lỗi update mobs");
            }
        }
    }

    private void udNonInteractiveNPC() {
        if (this.noninteractivenpcs.isEmpty()) {
            return;
        }
        try {
            for (int i = this.getNonInteractiveNPCs().size() - 1; i >= 0; i--) {
                if (i < this.getNonInteractiveNPCs().size()) {
                    Player pl = this.getNonInteractiveNPCs().get(i);
                    if (pl != null && pl.zone != null) {
                        pl.update();
                    }
                }
            }
        } catch (Exception e) {
            Logger.logException(Zone.class, e, "Lỗi update npcs");
        }
    }

    private void udItem() {
        if (this.items.isEmpty()) {
            return;
        }
        try {
            for (int i = this.items.size() - 1; i >= 0; i--) {
                try {
                    if (i < this.items.size()) {
                        ItemMap item = this.items.get(i);
                        if (item != null && item.itemTemplate != null) {
                            item.update();
                        } else {
                            items.remove(i);
                            System.err.println("Remove item " + i);
                        }
                    }
                } catch (Exception e) {
                    Logger.logException(Zone.class, e, "Lỗi item");
                }
            }
        } catch (Exception e) {
            Logger.logException(Zone.class, e, "Lỗi update items");
        }

    }

    public void update() {
        udMob();
        udItem();
        udNonInteractiveNPC();
        updateZoneTranhNgoc();
    }

    public Zone(Map map, int zoneId, int maxPlayer) {
        this.map = map;
        this.zoneId = zoneId;
        this.maxPlayer = maxPlayer;
        this.noninteractivenpcs = new ArrayList<>();
        this.humanoids = new ArrayList<>();
        this.notBosses = new ArrayList<>();
        this.players = new ArrayList<>();
        this.bosses = new ArrayList<>();
        this.pets = new ArrayList<>();
        this.mobs = new ArrayList<>();
        this.items = new ArrayList<>();
        this.trapMaps = new ArrayList<>();
        this.maBuHolds = new ArrayList<>();
        this.playersRed = new ArrayList<>();
        this.playersBlue = new ArrayList<>();
    }

    public int getNumOfPlayers() {
        return this.players.size();
    }

    public int getNumOfBosses() {
        return this.bosses.size();
    }

    public boolean isBossCanJoin(Boss boss) {
        for (Player b : this.bosses) {
            if (b.id == boss.id) {
                return false;
            }
        }
        return true;
    }

    public List<Player> getNotBosses() {
        return this.notBosses;
    }

    public List<Player> getPlayers() {
        return this.players;
    }

    public List<Player> getNonInteractiveNPCs() {
        return this.noninteractivenpcs;
    }

    public List<Player> getHumanoids() {
        return this.humanoids;
    }

    public List<Player> getBosses() {
        return this.bosses;
    }

    public void addPlayer(Player player) {
        if (player != null) {
            if (!this.humanoids.contains(player)) {
                this.humanoids.add(player);
            }

            if (player instanceof NonInteractiveNPC) {
                this.noninteractivenpcs.add(player);
            }

            if (!player.isBoss && !this.notBosses.contains(player) && !player.isNewPet && !(player instanceof NonInteractiveNPC)) {
                this.notBosses.add(player);
            }

            if (!player.isBoss && !player.isNewPet && !player.isPet && !this.players.contains(player) && !(player instanceof NonInteractiveNPC)) {
                this.players.add(player);
            }

            if (player.isBoss) {
                this.bosses.add(player);
            }
            if (player.isPet || player.isNewPet) {
                this.pets.add(player);
            }

        }
    }

    public void removePlayer(Player player) {
        this.noninteractivenpcs.remove(player);
        this.humanoids.remove(player);
        this.notBosses.remove(player);
        this.players.remove(player);
        this.bosses.remove(player);
        this.pets.remove(player);
    }

    public ItemMap getItemMapByItemMapId(int itemId) {
        for (ItemMap item : this.items) {
            if (item != null && item.itemMapId == itemId) {
                return item;
            }
        }
        return null;
    }

    public ItemMap getItemMapByTempId(int tempId) {
        for (ItemMap item : this.items) {
            if (item.itemTemplate.id == tempId) {
                return item;
            }
        }
        return null;
    }

    public List<ItemMap> getItemMapsForPlayer(Player player) {
        List<ItemMap> list = new ArrayList<>();
        for (ItemMap item : items) {
            if (item.itemTemplate.id == 78) {
                if (TaskService.gI().getIdTask(player) != ConstTask.TASK_3_1) {
                    continue;
                }
            }
            if (item.itemTemplate.id == 74) {
                if (TaskService.gI().getIdTask(player) < ConstTask.TASK_3_0) {
                    continue;
                }
            }
            if (item.itemTemplate.id == 726 && item.playerId != player.id) {
                continue;
            }
            list.add(item);
        }
        return list;
    }

    public Player getPlayerInMap(long idPlayer) {
        for (Player pl : humanoids) {
            if (pl != null && pl.id == idPlayer) {
                return pl;
            }
        }
        return null;
    }

    public Player getPlayerInMapOffline(Player player, long idPlayer) {
        for (Player pl : bosses) {
            if (pl.id == idPlayer && pl instanceof TrainingBoss && ((TrainingBoss) pl).playerAtt.equals(player)) {
                return pl;
            }
        }
        return null;
    }

    private void updateZoneTranhNgoc() {
        if (!TranhNgoc.gI().isTimeStartWar() && startZoneTranhNgoc) {
            startZoneTranhNgoc = false;
            playersBlue.clear();
            playersRed.clear();
            pointBlue = 0;
            pointRed = 0;
            return;
        }
        if (startZoneTranhNgoc) {
            if (Util.canDoWithTime(this.lastTimeStartTranhNgoc, ConstTranhNgocNamek.TIME)) {
                startZoneTranhNgoc = false;
                if (pointBlue > pointRed) {
                    TranhNgocService.getInstance().sendEndPhoBan(this, ConstTranhNgocNamek.WIN, false);
                    TranhNgocService.getInstance().sendEndPhoBan(this, ConstTranhNgocNamek.LOSE, true);
                    TranhNgocService.getInstance().givePrice(getPlayersBlue(), ConstTranhNgocNamek.WIN, pointBlue);
                    TranhNgocService.getInstance().givePrice(getPlayersRed(), ConstTranhNgocNamek.LOSE, pointRed);
                } else if (pointRed > pointBlue) {
                    TranhNgocService.getInstance().sendEndPhoBan(this, ConstTranhNgocNamek.WIN, true);
                    TranhNgocService.getInstance().sendEndPhoBan(this, ConstTranhNgocNamek.LOSE, false);
                    TranhNgocService.getInstance().givePrice(getPlayersRed(), ConstTranhNgocNamek.WIN, pointRed);
                    TranhNgocService.getInstance().givePrice(getPlayersBlue(), ConstTranhNgocNamek.LOSE, pointBlue);
                } else {
                    TranhNgocService.getInstance().sendEndPhoBan(this, ConstTranhNgocNamek.DRAW, true);
                    TranhNgocService.getInstance().sendEndPhoBan(this, ConstTranhNgocNamek.DRAW, false);
                }
                items.clear();
                playersBlue.clear();
                playersRed.clear();
                pointBlue = 0;
                pointRed = 0;
            } else {
                if (pointBlue == 7) {
                    startZoneTranhNgoc = false;
                    TranhNgocService.getInstance().sendEndPhoBan(this, ConstTranhNgocNamek.WIN, false);
                    TranhNgocService.getInstance().sendEndPhoBan(this, ConstTranhNgocNamek.LOSE, true);
                    TranhNgocService.getInstance().givePrice(getPlayersBlue(), ConstTranhNgocNamek.WIN, pointBlue);
                    TranhNgocService.getInstance().givePrice(getPlayersRed(), ConstTranhNgocNamek.LOSE, pointRed);
                    items.clear();
                    playersBlue.clear();
                    playersRed.clear();
                    pointBlue = 0;
                    pointRed = 0;
                } else if (pointRed == 7) {
                    startZoneTranhNgoc = false;
                    TranhNgocService.getInstance().sendEndPhoBan(this, ConstTranhNgocNamek.WIN, true);
                    TranhNgocService.getInstance().sendEndPhoBan(this, ConstTranhNgocNamek.LOSE, false);
                    TranhNgocService.getInstance().givePrice(getPlayersRed(), ConstTranhNgocNamek.WIN, pointRed);
                    TranhNgocService.getInstance().givePrice(getPlayersBlue(), ConstTranhNgocNamek.LOSE, pointBlue);
                    items.clear();
                    playersBlue.clear();
                    playersRed.clear();
                    pointBlue = 0;
                    pointRed = 0;
                }
            }
            if (Util.canDoWithTime(lastTimeDropBall, ConstTranhNgocNamek.LAST_TIME_DROP_BALL)) {
                int id = Util.nextInt(ConstItem.NGOC_RONG_NAMEK_1_SAO, ConstItem.NGOC_RONG_NAMEK_7_SAO);//ngoc rong namek day
                ItemMap it = this.getItemMapByTempId(id);
                if (it == null && !findPlayerHaveBallTranhDoat(id)) {
                    lastTimeDropBall = System.currentTimeMillis();
                    int x = Util.nextInt(20, map.mapWidth);
                    int y = map.yPhysicInTop(x, Util.nextInt(20, map.mapHeight - 200));
                    ItemMap itemMap = new ItemMap(this, id, 1, x, y, -1);
                    itemMap.isNamecBallTranhDoat = true;
                    Service.gI().dropItemMap(this, itemMap);
                }
            }
        }
    }

    public boolean findPlayerHaveBallTranhDoat(int id) {
        for (Player pl : this.getPlayers()) {
            if (pl != null && pl.isHoldNamecBallTranhDoat && pl.tempIdNamecBallHoldTranhDoat == id) {
                return true;
            }
        }
        return false;
    }

    public void pickItem(Player player, int itemMapId) {
        ItemMap itemMap = getItemMapByItemMapId(itemMapId);
        if (itemMap != null && itemMap.itemTemplate != null) {
            if (itemMap.itemTemplate.type == 22) {
                return;
            }
            if (itemMap.isNamecBallTranhDoat) {
                TranhNgocService.getInstance().pickBall(player, itemMap);
                return;
            }
            int playerId = Math.abs(itemMap.playerId > 100_000_000 ? 1_000_000_000 - (int) itemMap.playerId : (int) itemMap.playerId);
            if (playerId == player.id || itemMap.playerId == player.id || itemMap.playerId == -1) {
                Item item = ItemService.gI().createItemFromItemMap(itemMap);
                if (item.template.id == 648) {
                    if (!InventoryService.gI().findItemTatVoGiangSinh(player)) {
                        Service.gI().sendThongBao(player, "Cần thêm Tất,vớ giáng sinh");
                        return;
                    }
                }

                if (InventoryService.gI().addItemBag(player, item)) {
                    int itemType = item.template.type;
                    Message msg;
                    try {
                        msg = new Message(-20);
                        msg.writer().writeShort(itemMapId);
                        switch (itemType) {
                            case 9, 10, 34 -> {
                                msg.writer().writeUTF(item.quantity > Short.MAX_VALUE ? "Bạn vừa nhận được " + Util.chiaNho(item.quantity) + " " + item.template.name : "");
                                PlayerService.gI().sendInfoHpMpMoney(player);
                            }
                            default -> {
                                switch (item.template.id) {
                                    case 73 ->
                                        msg.writer().writeUTF("");
                                    case 74 ->
                                        msg.writer().writeUTF("Bạn mới vừa ăn " + item.template.name);
                                    case 78 ->
                                        msg.writer().writeUTF("Wow, một cậu bé dễ thương!");
                                    default -> {
//                                        if (item.template.type >= 0 && item.template.type < 5) {
//                                            msg.writer().writeUTF(item.template.name + " ngon...");
//                                        } else {
                                        msg.writer().writeUTF("Bạn nhận được " + item.template.name);
//                                        }
                                        if (item.template.id == 648) {
                                            InventoryService.gI().subQuantityItemsBag(player, InventoryService.gI().findItemBag(player, 649), 1);
                                        }
                                        InventoryService.gI().sendItemBag(player);
                                    }
                                }
                            }

                        }
                        msg.writer().writeShort(item.quantity > Short.MAX_VALUE ? 9999 : item.quantity);
                        player.sendMessage(msg);
                        msg.cleanup();
                        Service.gI().sendToAntherMePickItem(player, itemMapId);
                        if (!(this.map.mapId >= 21 && this.map.mapId <= 23
                                && itemMap.itemTemplate != null && itemMap.itemTemplate.id == 74
                                || this.map.mapId >= 42 && this.map.mapId <= 44
                                && itemMap.itemTemplate != null && itemMap.itemTemplate.id == 78)) {
                            removeItemMap(itemMap);
                        }
                    } catch (Exception e) {
                        Logger.logException(Zone.class, e);
                    }
                } else {
                    if (!ItemMapService.gI().isBlackBall(item.template.id) && !ItemMapService.gI().isNamecBall(item.template.id) && !ItemMapService.gI().isNamecBallStone(item.template.id)) {
                        String text = "Hành trang không còn chỗ trống, không thể nhặt thêm";
                        Service.gI().sendThongBao(player, text);
                        return;
                    }
                }
//                if (!picked) {
//                    ItemMap itm = new ItemMap(itemMap);
//                    itm.x = player.location.x + Util.nextInt(-20, 20);
//                    itm.y = itm.zone.map.yPhysicInTop(itm.x, player.location.y);
//                    Service.gI().dropItemMap(player.zone, itm);
//                }
            } else {
                Service.gI().sendThongBao(player, "Không thể nhặt vật phẩm của người khác");
                return;
            }
            TaskService.gI().checkDoneTaskPickItem(player, itemMap);
            TaskService.gI().checkDoneSideTaskPickItem(player, itemMap);
            TaskService.gI().checkDoneClanTaskPickItem(player, itemMap);
//        } else {
//            Service.gI().sendThongBao(player, "Không thể thực hiện 11111");
        }
    }

    public void addItem(ItemMap itemMap) {
        if (itemMap != null && !items.contains(itemMap)) {
            items.add(0, itemMap);
        }
    }

    public void removeItemMap(ItemMap itemMap) {
        this.items.remove(itemMap);
    }

    public Player getRandomPlayerInMap() {
        List<Player> plNotVoHinh = new ArrayList();

        //Lỗi
        for (Player pl : this.notBosses) {
            if (pl != null && (pl.effectSkin == null || !pl.effectSkin.isVoHinh) && pl.maBuHold == null && !pl.isMabuHold) {
                plNotVoHinh.add(pl);
            }
        }

        if (!plNotVoHinh.isEmpty()) {
            return plNotVoHinh.get(Util.nextInt(0, plNotVoHinh.size() - 1));
        }

        return null;
    }

    public void load_Me_To_Another(Player player) { //load thông tin người chơi cho những người chơi khác
        try {
            if (player.zone != null) {
                if (MapService.gI().isMapOffline(this.map.mapId)) {
                    //Load boss
                    if (player instanceof TrainingBoss || player instanceof NonInteractiveNPC) {
                        for (int i = players.size() - 1; i >= 0; i--) {
                            Player pl = players.get(i);
                         

                            if (!player.equals(pl) && (player instanceof NonInteractiveNPC
                                    || (player instanceof TrainingBoss && ((TrainingBoss) player).playerAtt.equals(pl)))) {
                           
                                infoPlayer(pl, player);
                            }
                        }
                    } else {
                    
                    }
                } else {

                    for (int i = players.size() - 1; i >= 0; i--) {
                        Player pl = players.get(i);
                        if (!player.equals(pl)) {
                       
                            infoPlayer(pl, player);
                        }
                    }
                }
            } else {
            }
        } catch (Exception e) {
            Logger.logException(MapService.class, e);
        }
    }

    public void load_Another_To_Me(Player player) { //load những player trong map và gửi cho player vào map
        try {
            if (MapService.gI().isMapOffline(this.map.mapId)) {
                //Load boss
                for (int i = this.humanoids.size() - 1; i >= 0; i--) {
                    Player pl = this.humanoids.get(i);
                    if (pl != null && (pl instanceof NonInteractiveNPC
                            || pl instanceof TrainingBoss && ((TrainingBoss) pl).playerAtt.equals(player))) {
                        infoPlayer(player, pl);
                    }
                }
            } else {
                for (int i = this.humanoids.size() - 1; i >= 0; i--) {
                    Player pl = this.humanoids.get(i);
                    if (pl != null && !player.equals(pl)) {
                        infoPlayer(player, pl);
                    }
                }
            }
        } catch (Exception e) {
            Logger.logException(MapService.class, e);
        }
    }

    public void loadBoss(Boss boss) {
        try {
            if (MapService.gI().isMapOffline(this.map.mapId)) {
                //Load boss
                for (Player pl : this.bosses) {
                    if (!boss.equals(pl) && !pl.isPl() && !pl.isPet && !pl.isNewPet) {
                        infoPlayer(boss, pl);
                        infoPlayer(pl, boss);
                    }
                }
            } else {
                for (Player pl : this.bosses) {
                    if (!boss.equals(pl)) {
                        infoPlayer(boss, pl);
                        infoPlayer(pl, boss);
                    }
                }
            }
        } catch (Exception e) {
            Logger.logException(MapService.class, e);
        }
    }

    private void infoPlayer(Player plReceive, Player plInfo) {
        Message msg;
        try {
            msg = new Message(-5);
            msg.writer().writeInt((int) plInfo.id);
            if (plInfo.clan != null) {
                msg.writer().writeInt(plInfo.clan.id);
            } else if (plInfo.isBoss && (plInfo.id == BossID.MABU || plInfo.id == BossID.SUPERBU)) {
                msg.writer().writeInt(-100);
            } else if (plInfo.isCopy) {
                msg.writer().writeInt(-2);
            } else {
                msg.writer().writeInt(-1);
            }
            msg.writer().writeByte(CaptionManager.getInstance().getLevel(plInfo));
            msg.writer().writeBoolean(false);
            msg.writer().writeByte(plInfo.typePk);
            msg.writer().writeByte(plInfo.gender);
            msg.writer().writeByte(plInfo.gender);
            msg.writer().writeShort(plInfo.getHead());
            msg.writer().writeUTF(Service.gI().name(plInfo));
            msg.writeLongByEmti(Util.maxIntValue(plInfo.nPoint.hp),cn.readInt);
            msg.writeLongByEmti(Util.maxIntValue(plInfo.nPoint.hpMax),cn.readInt);
            msg.writer().writeShort(plInfo.getBody());
            msg.writer().writeShort(plInfo.getLeg());
            int flagbag = plInfo.getFlagBag();
            if (plReceive.isPl() && plReceive.getSession() != null && plReceive.getSession().version >= 228) {
                switch (flagbag) {
                    case 83:
                        flagbag = 205;
                        break;
                }
            }
            msg.writer().writeByte(flagbag); //bag
            msg.writer().writeByte(-1);
            msg.writer().writeShort(plInfo.location.x);
            msg.writer().writeShort(plInfo.location.y);
            msg.writer().writeShort(0); // effbuffhp
            msg.writer().writeShort(0); // effbuffmp

            msg.writer().writeByte(0); // num eff

            //byte templateId, int timeStart, int timeLenght, short param
            msg.writer().writeByte(plInfo.iDMark.getIdSpaceShip());

            msg.writer().writeByte(plInfo.effectSkill != null && plInfo.effectSkill.isMonkey ? 1 : 0);
            msg.writer().writeShort(plInfo.getMount());
            msg.writer().writeByte(plInfo.cFlag);

            msg.writer().writeByte(0);
            msg.writer().writeShort(plInfo.getAura()); //idauraeff
            msg.writer().writeByte(plInfo.getEffFront()); //seteff
            msg.writer().writeShort(plInfo.getHat()); //id hat
            plReceive.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Service.gI().sendFlagPlayerToMe(plReceive, plInfo);
        try {
            if (plInfo.isPl()) {
                if (plInfo.effectSkill != null && plInfo.effectSkill.isChibi) {
                    Service.gI().sendChibiFollowToMe(plReceive, plInfo);
                } else {
                    Service.gI().sendPetFollowToMe(plReceive, plInfo);
                }
            }
        } catch (Exception e) {
        }

        try {
            if (plInfo.isDie()) {
                msg = new Message(-8);
                msg.writer().writeInt((int) plInfo.id);
                msg.writer().writeByte(0);
                msg.writer().writeShort(plInfo.location.x);
                msg.writer().writeShort(plInfo.location.y);
                plReceive.sendMessage(msg);
                msg.cleanup();
            }
        } catch (Exception e) {

        }
    }

    public void mapInfo(Player pl) {
        Message msg;
        try {
            msg = new Message(-24);
            msg.writer().writeByte(this.map.mapId);
            msg.writer().writeByte(this.map.planetId);
            msg.writer().writeByte(this.map.tileId);
            msg.writer().writeByte(this.map.bgId);
            msg.writer().writeByte(this.map.type);
            msg.writer().writeUTF(this.map.mapName);
            msg.writer().writeByte(this.zoneId);

            msg.writer().writeShort(pl.location.x);
            msg.writer().writeShort(pl.location.y);

            // waypoint
            try {
                List<WayPoint> wayPoints = this.map.wayPoints;
                msg.writer().writeByte(wayPoints.size());
                for (WayPoint wp : wayPoints) {
                    msg.writer().writeShort(wp.minX);
                    msg.writer().writeShort(wp.minY);
                    msg.writer().writeShort(wp.maxX);
                    msg.writer().writeShort(wp.maxY);
                    msg.writer().writeBoolean(wp.isEnter);
                    msg.writer().writeBoolean(wp.isOffline);
                    msg.writer().writeUTF(wp.name);
                }
            } catch (Exception e) {
                msg.writer().writeByte(0);
            }

            // mob
            try {
                List<Mob> mobs = new ArrayList<>();
                for (Mob mob : this.mobs) {
                    if (mob.isBigBoss() && mob.tempId != 70 && mob.isDie()) {
                        continue;
                    }
                    mobs.add(mob);
                }
                msg.writer().writeByte(mobs.size());
                for (Mob mob : mobs) {
                    msg.writer().writeBoolean(false); //is disable
                    msg.writer().writeBoolean(false); //is dont move
                    msg.writer().writeBoolean(false); //is fire
                    msg.writer().writeBoolean(false); //is ice
                    msg.writer().writeBoolean(false); //is wind
                    msg.writer().writeByte(mob.tempId);
                    msg.writer().writeByte(0); // sys
                    msg.writeLongByEmti(Util.maxIntValue(mob.point.gethp()),cn.readInt);
                    msg.writer().writeByte(mob.level);
                    msg.writeLongByEmti(Util.maxIntValue(mob.point.getHpFull()),cn.readInt);
                    msg.writer().writeShort(mob.location.x);
                    msg.writer().writeShort(mob.location.y);
                    msg.writer().writeByte(mob.status);
                    msg.writer().writeByte(mob.lvMob);
                    msg.writer().writeBoolean(mob.tempId == ConstMob.GAU_TUONG_CUOP || mob.tempId == ConstMob.KONG || mob.tempId == ConstMob.GOZILLA || mob.tempId >= ConstMob.VOI_CHIN_NGA && mob.tempId <= ConstMob.PIANO); //is bigboss
                }
            } catch (Exception e) {
                msg.writer().writeByte(0);
            }

            msg.writer().writeByte(0);

            // npc
            try {
                List<Npc> npcs = NpcManager.getNpcsByMapPlayer(pl);
                msg.writer().writeByte(npcs.size());
                for (Npc npc : npcs) {
                    msg.writer().writeByte(npc.status);
                    msg.writer().writeShort(npc.cx);
                    msg.writer().writeShort(npc.cy);
                    msg.writer().writeByte(npc.tempId);
                    msg.writer().writeShort(npc.avartar);
                }
            } catch (Exception e) {
                msg.writer().writeByte(0);
            }

            // item
            try {
                List<ItemMap> itemsMap = this.getItemMapsForPlayer(pl);
                msg.writer().writeByte(itemsMap.size());
                for (ItemMap it : itemsMap) {
                    msg.writer().writeShort(it.itemMapId);
                    msg.writer().writeShort(it.itemTemplate.id);
                    msg.writer().writeShort(it.x);
                    msg.writer().writeShort(it.y);
                    msg.writer().writeInt((int) it.playerId);
                }
            } catch (Exception e) {
                msg.writer().writeByte(0);
            }

            // bg item
            try {
                final byte[] bgItem = FileIO.readFile("data/map/item_bg_map_data/" + this.map.mapId);
                msg.writer().write(bgItem);
            } catch (Exception e) {
                msg.writer().writeShort(0);
            }

/// eff item
            try {
                List<EffectMap> effectList = MapManager.getEffMap(this.map.mapId);
                msg.writer().writeShort(effectList.size());
                for (EffectMap e : effectList) {
                    msg.writer().writeUTF(e.getKey());
                    msg.writer().writeUTF(e.getValue());
                }
            } catch (Exception e) {
                msg.writer().writeShort(0);
            }

            msg.writer().writeByte(this.map.bgType);
            msg.writer().writeByte(pl.iDMark.getIdSpaceShip());
            msg.writer().writeByte(this.map.mapId == 148 ? 1 : 0);
            pl.sendMessage(msg);

            msg.cleanup();

        } catch (Exception e) {
            Logger.logException(Service.class, e);
        }
    }

    public TrapMap isInTrap(Player player) {
        for (TrapMap trap : this.trapMaps) {
            if (player.location.x >= trap.x && player.location.x <= trap.x + trap.w
                    && player.location.y >= trap.y && player.location.y <= trap.y + trap.h) {
                return trap;
            }
        }
        return null;
    }

    public void sendBigBoss(Player player) {
        for (Mob mob : this.mobs) {
            if (!mob.isDie() && mob.tempId == ConstMob.HIRUDEGARN) {
                if (mob.lvMob >= 1) {
                    Service.gI().sendBigBoss2(player, 6, mob);
                }
                if (mob.lvMob >= 2) {
                    Service.gI().sendBigBoss2(player, 5, mob);
                }
                break;
            }
        }
    }

    public MaBuHold getMaBuHold() {
        for (MaBuHold hold : MapService.gI().getMapById(128).zones.get(this.zoneId).maBuHolds) {
            if (hold.player == null) {
                return hold;
            }
        }
        return null;
    }

    public void setMaBuHold(int slot, int zoneId, Player player) {
        MapService.gI().getMapById(128).zones.get(zoneId).maBuHolds.set(slot, new MaBuHold(slot, player));
    }

    public Player findPlayerByID(long id) {
        for (Player p : this.players) {
            if (p.id == id) {
                return p;
            }
        }
        return null;
    }

    public boolean isKhongCoTrongTaiTrongKhu() {
        boolean no = true;
        for (Player pl : players) {
            if (pl.name.compareTo("Trọng Tài") == 0) {
                no = false;
                break;
            }
            if (pl.zone.map.mapId >= 21 && pl.zone.map.mapId <= 23) {
                no = false;
            }
        }
        return no;
    }

    public boolean hasBot() {
    for (Player p : this.players) {
        if (p.isBot) {
            return true; // Nếu có ít nhất 1 bot trong khu này thì trả về true
        }
    }
    return false; // Không có bot nào trong khu này
}

}
