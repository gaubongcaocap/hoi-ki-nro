package models.DragonNamecWar;

import consts.ConstTranhNgocNamek;
import item.Item;
import java.util.List;
import map.ItemMap;
import map.Zone;
import network.Message;
import player.Player;
import services.InventoryService;
import services.ItemMapService;
import services.ItemService;
import services.Service;
import utils.Util;

public class TranhNgocService {

    private static TranhNgocService instance;

    public static TranhNgocService getInstance() {
        if (instance == null) {
            instance = new TranhNgocService();
        }
        return instance;
    }

    public void sendCreatePhoBan(Player pl) {
        Message msg;
        try {
            msg = new Message(20);
            msg.writer().writeByte(0);
            msg.writer().writeByte(0);
            msg.writer().writeShort(ConstTranhNgocNamek.MAP_ID);
            msg.writer().writeUTF(ConstTranhNgocNamek.BLUE); // team 1
            msg.writer().writeUTF(ConstTranhNgocNamek.RED); // team 2
            msg.writer().writeInt(ConstTranhNgocNamek.MAX_LIFE);
            msg.writer().writeShort(ConstTranhNgocNamek.TIME_SECOND);
            msg.writer().writeByte(ConstTranhNgocNamek.MAX_POINT);
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendUpdateLift(Player pl) {
        Message msg;
        try {
            msg = new Message(20);
            msg.writer().writeByte(0);
            msg.writer().writeByte(1);
            msg.writer().writeInt((int) pl.zone.getPlayersBlue().stream().filter(p -> p != null && !p.isDie()).count());
            msg.writer().writeInt((int) pl.zone.getPlayersRed().stream().filter(p -> p != null && !p.isDie()).count());
            Service.gI().sendMessAllPlayerInMap(pl.zone, msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendEndPhoBan(Zone zone, byte type, boolean isFide) {
        Message msg;
        try {
            msg = new Message(20);
            msg.writer().writeByte(0);
            msg.writer().writeByte(2);
            msg.writer().writeByte(type);
            if (zone != null) {
                List<Player> players = isFide ? zone.getPlayersRed() : zone.getPlayersBlue();
                synchronized (players) {
                    for (Player pl : players) {
                        if (pl != null) {
                            pl.sendMessage(msg);
                        }
                    }
                }
                msg.cleanup();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendUpdateTime(Player pl, short second) {
        Message msg;
        try {
            msg = new Message(20);
            msg.writer().writeByte(0);
            msg.writer().writeByte(5);
            msg.writer().writeShort(second);
            Service.gI().sendMessAllPlayerInMap(pl.zone, msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendUpdatePoint(Player pl) {
        Message msg;
        try {
            msg = new Message(20);
            msg.writer().writeByte(0);
            msg.writer().writeByte(4);
            msg.writer().writeByte(pl.zone.pointBlue);
            msg.writer().writeByte(pl.zone.pointRed);
            Service.gI().sendMessAllPlayerInMap(pl.zone, msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void givePrice(List<Player> players, byte type, int point) {
        switch (type) {
            case ConstTranhNgocNamek.LOSE:
                int pointDiff = ConstTranhNgocNamek.MAX_POINT - point;
                for (Player pl : players) {
                    if (pl != null) {
                        Item mcl = InventoryService.gI().findItemBagByTemp(pl, ConstTranhNgocNamek.ITEM_TRANH_NGOC);
                        if (mcl != null) {
                            InventoryService.gI().subQuantityItemsBag(pl, mcl, pointDiff);
                            InventoryService.gI().sendItemBag(pl);
                            Service.gI().sendThongBao(pl, "Bạn đã thua và bị thu " + pointDiff + " " + ConstTranhNgocNamek.ITEM_TRANH_NGOC);
                        }
                        TranhNgoc.gI().removePlayersBlue(pl);
                        TranhNgoc.gI().removePlayersRed(pl);
                    }
                }
                break;
            case ConstTranhNgocNamek.WIN:
                for (Player pl : players) {
                    if (pl != null) {
                        Item mcl = ItemService.gI().createNewItem((short) ConstTranhNgocNamek.ITEM_TRANH_NGOC);
                        mcl.quantity = point;
                        mcl.itemOptions.add(new Item.ItemOption (30,1));
                        InventoryService.gI().addItemBag(pl, mcl);
                        InventoryService.gI().sendItemBag(pl);
                        Service.gI().sendThongBao(pl, "Bạn đã thắng và nhận " + point + " " + ConstTranhNgocNamek.ITEM_TRANH_NGOC);
                        TranhNgoc.gI().removePlayersBlue(pl);
                        TranhNgoc.gI().removePlayersRed(pl);
                    }
                }
                break;
            case ConstTranhNgocNamek.DRAW:
                for (Player pl : players) {
                    if (pl != null) {
                        Item mcl = ItemService.gI().createNewItem((short) ConstTranhNgocNamek.ITEM_TRANH_NGOC);
                        mcl.quantity = point / 2;
                         mcl.itemOptions.add(new Item.ItemOption (30,1));
                        InventoryService.gI().addItemBag(pl, mcl);
                        InventoryService.gI().sendItemBag(pl);
                        Service.gI().sendThongBao(pl, "2 bên hòa nhau và nhận " + (point / 2) + " " + ConstTranhNgocNamek.ITEM_TRANH_NGOC);
                        TranhNgoc.gI().removePlayersBlue(pl);
                        TranhNgoc.gI().removePlayersRed(pl);
                    }
                }
                break;
            default:
                break;
        }
    }

    public void pickBall(Player player, ItemMap item) {
        if (player.isHoldNamecBallTranhDoat || item.typeHaveBallTranhDoat == player.iDMark.getTranhNgoc()) {
             Service.gI().sendThongBao(player, "Lưng đeo rồi còn nhặt gì nữa ba");
            return;
        }
        if (item.typeHaveBallTranhDoat != -1 && item.typeHaveBallTranhDoat != player.iDMark.getTranhNgoc()) {
            if (player.iDMark.getTranhNgoc() == 1) {
                player.zone.pointBlue--;
            } else if (player.iDMark.getTranhNgoc() == 2) {
                player.zone.pointRed--;
            }
            sendUpdatePoint(player);
        }
        player.tempIdNamecBallHoldTranhDoat = item.itemTemplate.id;
        player.isHoldNamecBallTranhDoat = true;
        ItemMapService.gI().removeItemMapAndSendClient(item);
        Service.gI().sendFlagBag(player);
        Service.gI().sendThongBao(player, "Bạn đang giữ viên ngọc rồng Namek");
    }

    public void dropBall(Player player, byte a) {
        if (player.tempIdNamecBallHoldTranhDoat != -1) {
            player.isHoldNamecBallTranhDoat = false;
        }
        int x = Util.nextInt(20, player.zone.map.mapWidth);
        int y = player.zone.map.yPhysicInTop(x, player.zone.map.mapHeight / 2);
        ItemMap itemMap = new ItemMap(player.zone, player.tempIdNamecBallHoldTranhDoat, 1, x, y, -1);
        itemMap.isNamecBallTranhDoat = true;
        itemMap.typeHaveBallTranhDoat = a;
        itemMap.x = player.location.x;
        itemMap.y = player.location.y;
        Service.gI().dropItemMap(player.zone, itemMap);
        Service.gI().sendFlagBag(player);
        player.tempIdNamecBallHoldTranhDoat = -1;
        Service.gI().sendThongBao(player, "Đi thu thập tiếp về đây cho ta !!!");
    }
    
}
