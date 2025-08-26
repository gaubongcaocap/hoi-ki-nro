package services.func;

/*
 *
 *
 * @author NTD
 */
import boss.Boss;
import boss.BossID;
import boss.BossManager;
import boss.miniboss.SoiHecQuyn;
import consts.ConstItem;
import models.Combine.CombineService;
import models.ShenronEvent.ShenronEventService;
import models.Card.Card;
import models.Card.RadarService;
import models.Card.RadarCard;
import consts.ConstMap;
import item.Item;
import consts.ConstNpc;
import consts.ConstPlayer;
import consts.ConstTaskBadges;
import item.Item.ItemOption;
import jdbc.daos.PlayerDAO;
import map.Zone;
import player.Inventory;
import services.*;
import player.Player;
import skill.Skill;
import network.Message;
import utils.SkillUtil;
import utils.TimeUtil;
import utils.Util;
import server.io.MySession;
import utils.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import map.ItemMap;
import task.Badges.BadgesTaskService;

public class UseItem {

    private static final int ITEM_BOX_TO_BODY_OR_BAG = 0;
    private static final int ITEM_BAG_TO_BOX = 1;
    private static final int ITEM_BODY_TO_BOX = 3;
    private static final int ITEM_BAG_TO_BODY = 4;
    private static final int ITEM_BODY_TO_BAG = 5;
    private static final int ITEM_BAG_TO_PET_BODY = 6;
    private static final int ITEM_BODY_PET_TO_BAG = 7;

    private static final byte DO_USE_ITEM = 0;
    private static final byte DO_THROW_ITEM = 1;
    private static final byte ACCEPT_THROW_ITEM = 2;
    private static final byte ACCEPT_USE_ITEM = 3;

    private static UseItem instance;

    private UseItem() {

    }

    public static UseItem gI() {
        if (instance == null) {
            instance = new UseItem();
        }
        return instance;
    }

    public void getItem(MySession session, Message msg) {
        Player player = session.player;
        if (player == null) {
            return;
        }
        TransactionService.gI().cancelTrade(player);
        try {
            int type = msg.reader().readByte();
            int index = msg.reader().readByte();
            if (index == -1) {
                return;
            }
            switch (type) {
                case ITEM_BOX_TO_BODY_OR_BAG:
                    InventoryService.gI().itemBoxToBodyOrBag(player, index);
                    TaskService.gI().checkDoneTaskGetItemBox(player);
                    break;
                case ITEM_BAG_TO_BOX:
                    InventoryService.gI().itemBagToBox(player, index);
                    break;
                case ITEM_BODY_TO_BOX:
                    InventoryService.gI().itemBodyToBox(player, index);
                    break;
                case ITEM_BAG_TO_BODY:
                    InventoryService.gI().itemBagToBody(player, index);
                    break;
                case ITEM_BODY_TO_BAG:
                    InventoryService.gI().itemBodyToBag(player, index);
                    break;
                case ITEM_BAG_TO_PET_BODY:
                    InventoryService.gI().itemBagToPetBody(player, index);
                    break;
                case ITEM_BODY_PET_TO_BAG:
                    InventoryService.gI().itemPetBodyToBag(player, index);
                    break;
            }
            if (player.setClothes != null) {
                player.setClothes.setup();
            }
            if (player.pet != null) {
                player.pet.setClothes.setup();
            }
            player.setClanMember();
            Service.gI().sendFlagBag(player);
            Service.gI().point(player);
            Service.gI().sendSpeedPlayer(player, -1);
        } catch (Exception e) {
            Logger.logException(UseItem.class, e);

        }
    }

    public Item finditem(Player player, int iditem) {
        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && item.template.id == iditem) {
                return item;
            }
        }
        return null;
    }

    public void doItem(Player player, Message _msg) {
        TransactionService.gI().cancelTrade(player);
        Message msg = null;
        byte type;
        try {
            type = _msg.reader().readByte();
            int where = _msg.reader().readByte();
            int index = _msg.reader().readByte();
            switch (type) {
                case DO_USE_ITEM:
                    if (player != null && player.inventory != null) {
                        if (index != -1) {
                            if (index < 0) {
                                return;
                            }
                            Item item = player.inventory.itemsBag.get(index);
                            if (item.isNotNullItem()) {
                                if (item.template.type == 7) {
                                    msg = new Message(-43);
                                    msg.writer().writeByte(type);
                                    msg.writer().writeByte(where);
                                    msg.writer().writeByte(index);
                                    msg.writer().writeUTF("Bạn chắc chắn học "
                                            + player.inventory.itemsBag.get(index).template.name + "?");
                                    player.sendMessage(msg);
                                } else if (item.template.id == 570) {
                                    // if (!Util.isAfterMidnight(player.lastTimeRewardWoodChest)) {
                                    // Service.gI().sendThongBao(player, "Hãy chờ đến ngày mai");
                                    // return;
                                    // }
                                    msg = new Message(-43);
                                    msg.writer().writeByte(type);
                                    msg.writer().writeByte(where);
                                    msg.writer().writeByte(index);
                                    msg.writer().writeUTF("Bạn chắc muốn mở\n"
                                            + player.inventory.itemsBag.get(index).template.name + " ?");
                                    player.sendMessage(msg);
                                } else if (item.template.type == 22) {
                                    if (player.zone.items.stream()
                                            .filter(it -> it != null && it.itemTemplate.type == 22).count() > 2) {
                                        Service.gI().sendThongBaoOK(player, "Mỗi map chỉ đặt được 3 Vệ Tinh");
                                        return;
                                    }
                                    msg = new Message(-43);
                                    msg.writer().writeByte(type);
                                    msg.writer().writeByte(where);
                                    msg.writer().writeByte(index);
                                    msg.writer().writeUTF("Bạn chắc muốn dùng\n"
                                            + player.inventory.itemsBag.get(index).template.name + " ?");
                                    player.sendMessage(msg);
                                } else {
                                    UseItem.gI().useItem(player, item, index);
                                }
                            }
                        } else {
                            int iditem = _msg.reader().readShort();
                            Item item = finditem(player, iditem);
                            UseItem.gI().useItem(player, item, index);
                        }
                    }
                    break;
                case DO_THROW_ITEM:
                    if (!(player.zone.map.mapId == 21 || player.zone.map.mapId == 22 || player.zone.map.mapId == 23)) {
                        Item item = null;
                        if (index < 0) {
                            return;
                        }
                        if (where == 0) {
                            item = player.inventory.itemsBody.get(index);
                        } else {
                            item = player.inventory.itemsBag.get(index);
                        }

                        if (item.isNotNullItem() && item.template.id == 570) {
                            Service.gI().sendThongBao(player, "Không thể bỏ vật phẩm này.");
                            return;
                        }
                        if (!item.isNotNullItem()) {
                            return;
                        }
                        msg = new Message(-43);
                        msg.writer().writeByte(type);
                        msg.writer().writeByte(where);
                        msg.writer().writeByte(index);
                        msg.writer().writeUTF("Bạn chắc chắn muốn vứt " + item.template.name + "?");
                        player.sendMessage(msg);
                    } else {
                        Service.gI().sendThongBao(player, "Không thể thực hiện");
                    }
                    break;
                case ACCEPT_THROW_ITEM:
                    InventoryService.gI().throwItem(player, where, index);
                    Service.gI().point(player);
                    InventoryService.gI().sendItemBag(player);
                    break;
                case ACCEPT_USE_ITEM:
                    UseItem.gI().useItem(player, player.inventory.itemsBag.get(index), index);
                    break;
            }
        } catch (Exception e) {
            Logger.logException(UseItem.class, e);
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    private void useItem(Player pl, Item item, int indexBag) {
        if (item != null && item.isNotNullItem()) {
            if (item.template.id == 570) {
                int time = (int) TimeUtil.diffDate(new Date(), new Date(item.createTime), TimeUtil.DAY);
                if (time == 0) {
                    Service.gI().sendThongBao(pl, "Hãy chờ đến ngày mai");
                } else {
                    openRuongGo(pl);
                }
                return;
            }
            if (item.template.strRequire <= pl.nPoint.power) {
                switch (item.template.type) {
                    case 21:
                        InventoryService.gI().itemBagToBody(pl, indexBag);
                        PetService.Pet2(pl, pl.getHeadThuCung(), pl.getBodyThuCung(), pl.getLegThuCung());
                        Service.gI().point(pl);
                        break;
                    case 33: // card
                        UseCard(pl, item);
                        break;
                    case 7: // sách học, nâng skill
                        learnSkill(pl, item);
                        break;
                    case 6: // đậu thần
                        this.eatPea(pl);
                        break;
                    case 12: // ngọc rồng các loại
                        controllerCallRongThan(pl, item);
                        break;
                    case 23: // thú cưỡi mới
                    case 24: // thú cưỡi cũ
                        InventoryService.gI().itemBagToBody(pl, indexBag);
                        break;
                    case 11: // item bag
                        InventoryService.gI().itemBagToBody(pl, indexBag);

                        Service.gI().sendFlagBag(pl);

                        break;
                    case 75:
                        InventoryService.gI().itemBagToBody(pl, indexBag);
                        Service.gI().sendchienlinh(pl, (short) (item.template.iconID - 1));
                        break;
                    case 72: {
                        InventoryService.gI().itemBagToBody(pl, indexBag);
                        Service.gI().sendPetFollow(pl, (short) (item.template.iconID - 1));
                        break;
                    }
                    case 98: {
                        InventoryService.gI().itemBagToBody(pl, indexBag);
                        Service.gI().sendEffPlayer(pl);
                        break;
                    }
                    case 99: {
                        InventoryService.gI().itemBagToBody(pl, indexBag);
                        Service.gI().sendEffPlayer(pl);
                        break;
                    }
                    default:
                        switch (item.template.id) {
                            case 992: // Nhan thoi khong
                                pl.type = 2;
                                pl.maxTime = 5;
                                Service.gI().Transport(pl);
                                break;
                            case 361:
                                pl.idGo = (short) Util.nextInt(0, 6);
                                NgocRongNamecService.gI().menuCheckTeleNamekBall(pl);
                                InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                                InventoryService.gI().sendItemBag(pl);
                                break;

                            case 893:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 885, 886, 887);
                                Service.gI().point(pl);
                                break;
                            case 908:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 891, 892, 893);
                                Service.gI().point(pl);
                                break;
                            case 909:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 894, 895, 896);
                                Service.gI().point(pl);
                                break;
                            case 910:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 897, 898, 899);
                                Service.gI().point(pl);
                                break;
                            case 916:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 925, 926, 927);
                                Service.gI().point(pl);
                                break;
                            case 917:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 928, 929, 930);
                                Service.gI().point(pl);
                                break;
                            case 918:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 931, 932, 933);
                                Service.gI().point(pl);
                                break;
                            case 919:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 934, 935, 936);
                                Service.gI().point(pl);
                                break;
                            case 936:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 718, 719, 720);
                                Service.gI().point(pl);
                                break;
                            case 942:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 966, 967, 968);
                                Service.gI().point(pl);
                                break;
                            case 943:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 969, 970, 971);
                                Service.gI().point(pl);
                                break;
                            case 944:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 972, 973, 974);
                                Service.gI().point(pl);
                                break;
                            case 967:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1050, 1051, 1052);
                                Service.gI().point(pl);
                                break;
                            case 1008:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1074, 1075, 1076);
                                Service.gI().point(pl);
                                break;
                            case 1039:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1089, 1090, 1091);
                                Service.gI().point(pl);
                                break;
                            case 1040:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1092, 1093, 1094);
                                Service.gI().point(pl);
                                break;
                            case 1046:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, -1, -1, -1);
                                Service.gI().point(pl);
                                break;
                            case 1107:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1155, 1156, 1157);
                                Service.gI().point(pl);
                                break;
                            case 1114:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1158, 1159, 1160);
                                Service.gI().point(pl);
                                break;
                            case 1188:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1183, 1184, 1185);
                                Service.gI().point(pl);
                                break;
                            case 1202:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1201, 1202, 1203);
                                Service.gI().point(pl);
                                break;
                            case 1203:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1201, 1202, 1203);
                                Service.gI().point(pl);
                                break;
                            case 1207:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1077, 1078, 1079);
                                Service.gI().point(pl);
                                break;
                            case 1224:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1227, 1228, 1229);
                                Service.gI().point(pl);
                                break;
                            case 1225:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1233, 1234, 1235);
                                Service.gI().point(pl);
                                break;
                            case 1226:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1230, 1231, 1232);
                                Service.gI().point(pl);
                                break;
                            case 1243:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1245, 1246, 1247);
                                Service.gI().point(pl);
                                break;
                            case 1244:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1248, 1249, 1250);
                                Service.gI().point(pl);
                                break;
                            case 1256:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1267, 1268, 1269);
                                Service.gI().point(pl);
                                break;
                            case 1318:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1299, 1300, 1301);
                                Service.gI().point(pl);
                                break;
                            case 1347:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1302, 1303, 1304);
                                Service.gI().point(pl);
                                break;
                            case 1414:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1341, 1342, 1343);
                                Service.gI().point(pl);
                                break;
                            case 1435:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1347, 1348, 1349);
                                Service.gI().point(pl);
                                break;
                            case 1452:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1365, 1366, 1367);
                                Service.gI().point(pl);
                                break;
                            case 1458:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1368, 1369, 1370);
                                Service.gI().point(pl);
                                break;
                            case 1482:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1398, 1399, 1400);
                                Service.gI().point(pl);
                                break;
                            case 1497:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1401, 1402, 1403);
                                Service.gI().point(pl);
                                break;
                            case 1550:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1428, 1429, 1430);
                                Service.gI().point(pl);
                                break;
                            case 1551:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1425, 1426, 1427);
                                Service.gI().point(pl);
                                break;
                            case 1564:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1437, 1438, 1439);
                                Service.gI().point(pl);
                                break;
                            case 1568:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1443, 1444, 1445);
                                Service.gI().point(pl);
                                break;
                            case 1573:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1446, 1447, 1448);
                                Service.gI().point(pl);
                                break;
                            case 1596:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1473, 1474, 1475);
                                Service.gI().point(pl);
                                break;
                            case 1597:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1473, 1474, 1475);
                                Service.gI().point(pl);
                                break;
                            case 1611:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1488, 1494, 1495);
                                Service.gI().point(pl);
                                break;
                            case 1620:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1496, 1497, 1498);
                                Service.gI().point(pl);
                                break;
                            case 1621:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1496, 1497, 1498);
                                Service.gI().point(pl);
                                break;
                            case 1622:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1488, 1489, 1490);
                                Service.gI().point(pl);
                                break;
                            case 1629:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1505, 1506, 1507);
                                Service.gI().point(pl);
                                break;
                            case 1630:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1508, 1509, 1510);
                                Service.gI().point(pl);
                                break;
                            case 1631:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1513, 1516, 1517);
                                Service.gI().point(pl);
                                break;
                            case 1633:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1523, 1524, 1525);
                                Service.gI().point(pl);
                                break;
                            case 1654:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1526, 1529, 1530);
                                Service.gI().point(pl);
                                break;
                            case 1668:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1550, 1551, 1552);
                                Service.gI().point(pl);
                                break;

                            case 1682:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1558, 1559, 1560);
                                Service.gI().point(pl);
                                break;
                            case 1683:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1561, 1562, 1563);
                                Service.gI().point(pl);
                                break;
                            case 1686:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1572, 1573, 1574);
                                Service.gI().point(pl);
                                break;

                            case 1750:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1464, 1465, 1466);
                                Service.gI().point(pl);
                                break;
                            case 1762:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1665, 1666, 1667);
                                Service.gI().point(pl);
                                break;
                            case 211: // nho tím
                            case 212: // nho xanh
                                eatGrapes(pl, item);
                                break;
                            case 342:
                            case 343:
                            case 344:
                            case 345:
                                if (pl.zone.items.stream().filter(it -> it != null && it.itemTemplate.type == 22)
                                        .count() < 3) {
                                    Service.gI().dropSatellite(pl, item, pl.zone, pl.location.x, pl.location.y);
                                    InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                                } else {
                                    Service.gI().sendThongBaoOK(pl, "Mỗi map chỉ đặt được 3 Vệ Tinh");
                                }
                                break;
                            case 380: // cskb
                                openCSKB(pl, item);
                                break;
                            case 381: // cuồng nộ
                            case 382: // bổ huyết
                            case 383: // bổ khí
                            case 384: // giáp xên
                            case 385: // ẩn danh
                            case 379: // máy dò capsule
                            case 638: // commeson
                            case 2075: // rocket
                            case 1233: // Nồi cơm điện
                            case 579:
                            case 1701:
                            case 1045: // đuôi khỉ
                                // if (pl.itemTime.isUseDK) {
                                // Service.gI().sendThongBao(pl, "Chỉ được sử dụng 1 cái");
                                // break;
                                // }
                                useItemTime(pl, item);
                                break;
                            case 2160: // Nồi cơm điện
                                break;
                            case 663: // bánh pudding
                            case 664: // xúc xíc
                            case 665: // kem dâu
                            case 666: // mì ly
                            case 667: // sushi

                            case 1150:

                            case 1151:

                            case 1152:

                            case 1153:

                            case 1154:

                            case 1628:

                            case 764:

                            case 1731:

                            case 1727:

                            case 1728:

                            case 1729:
                            case 1730:
                                useItemTime(pl, item);
                                break;
                            case 1560:
                                if (InventoryService.gI().findItem(pl.inventory.itemsBag, 1561) != null) {
                                    UseItem.gI().openRuongNgocRong(pl, item);
                                } else {
                                    Service.gI().sendThongBao(pl, "Bạn không có chía khoá vàng!");
                                }
                                break;
                            case 880:
                            case 881:
                            case 882:
                                if (pl.itemTime.isEatMeal2) {
                                    Service.gI().sendThongBao(pl, "Chỉ được sử dụng 1 cái");
                                    break;
                                }
                                useItemTime(pl, item);
                                break;
                            case 899:
                            case 900:
                            case 902:
                            case 903:
                                if (pl.itemTime.isEatMeal3) {
                                    Service.gI().sendThongBao(pl, "Chỉ được sử dụng 1 cái");
                                    break;
                                }
                                useItemTime(pl, item);
                                break;
                            case 521: //tdlt
                                useTDLT(pl, item);
                                break;
                            case 454: //bông tai
                                UseItem.gI().usePorata(pl);
                                break;
                            case 921: //bông tai
                                UseItem.gI().usePorata2(pl);
//                                UseItem.gI().usePorataGogeta(pl);
                                break;
                            case 193: //gói 10 viên capsule
                                openCapsuleUI(pl);
                                InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                            case 194: //capsule đặc biệt
                                openCapsuleUI(pl);
                                break;
                            case 401: //đổi đệ tử
                                changePet(pl, item);
                                break;
                            case 402: //sách nâng chiêu 1 đệ tử
                            case 403: //sách nâng chiêu 2 đệ tử
                            case 404: //sách nâng chiêu 3 đệ tử
                            case 759: //sách nâng chiêu 4 đệ tử
                                upSkillPet(pl, item);
                                break;
                            case 726:
                                UseItem.gI().ItemManhGiay(pl, item);
                                break;
                            case 727:
                            case 728:
                                UseItem.gI().ItemSieuThanThuy(pl, item);
                                break;
                            case 648:
                                ItemService.gI().OpenItem648(pl, item);
                                break;
                            case 736:
                                ItemService.gI().OpenItem736(pl, item);
                                break;
                            case 987:
                                Service.gI().sendThongBao(pl, "Bảo vệ trang bị không bị rớt cấp"); // đá bảo vệ
                                break;
                            case 2006:
                                Input.gI().createFormChangeNameByItem(pl);
                                break;
                            case 1732: //tiền
                                BossManager.gI().showListBoss(pl,item);
                                break;
                            case 1733:
                                UseItem.gI().hailit(pl,item);
                                break;
                            case 1734:
                                UseItem.gI().motlit(pl,item);
                                break;
                            case 1735:
                                UseItem.gI().namchuc(pl,item);
                                break;
                            case 1736:
                                UseItem.gI().haichuc(pl,item);
                                break;
                            case 1737:
                                UseItem.gI().motchuc(pl,item);
                                break;
                            case 1738:
                                UseItem.gI().namcanh(pl,item);
                                break;
                            case 1739:
                                UseItem.gI().haicanh(pl,item);
                                break;
                            case 1740: //tiền
                                UseItem.gI().motcanh(pl,item);
                                break;
                            case 1741://túi mù
                                UseItem.gI().openRandomItem(pl, item);
                                break;
                            case 1743:// túi sự kiện mở sv
                                if (InventoryService.gI().getCountEmptyBag(pl) < 1) {
                                    Service.gI().sendThongBaoOK(pl, "Cần 1 ô hành trang trống");
                                    return;
                                }
                                int[] rewardItems = {1744, 1741, 674, 14, 15, 16, 17, 1142, 956, 457, 1045};
                                int[] probabilities = {54, 5, 5, 1, 2, 3, 5, 5, 5, 10, 5}; // Tỷ lệ %

                                int randomPercent = Util.nextInt(1, 100);
                                int cumulative = 0;
                                int receivedItemId = 1744;

                                for (int i = 0; i < rewardItems.length; i++) {
                                    cumulative += probabilities[i];
                                    if (randomPercent <= cumulative) {
                                        receivedItemId = rewardItems[i];
                                        break;
                                    }
                                }
                                Item rewardItem = ItemService.gI().createNewItem((short) receivedItemId);

                                if (receivedItemId == 457) {
                                    rewardItem.quantity = 10;
                                }

                                rewardItem.itemOptions.add(new Item.ItemOption(30, 1));
                                InventoryService.gI().addItemBag(pl, rewardItem);
                                InventoryService.gI().sendItemBag(pl);
                                InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                                Service.gI().sendThongBao(pl, "Bạn đã nhận được " + rewardItem.template.name);
                                break;

                            case 1623:
                                TaskService.gI().sendNextTaskMain(pl);
                                break;
                            case 1228:
                                NpcService.gI().createMenuConMeo(pl, ConstNpc.HOP_QUA_THAN_LINH, -1,
                                        "Chọn hành tinh của đồ thần linh muốn nhận.",
                                        "Trái đất", "Namek", "Xayda");
                                break;
                            case 460:
                                Gwen_XuongCho(pl, item);
                                break;
                            case 1626: {
                                int[] listItem = { 856, 943, 942 };
                                if (InventoryService.gI().getCountEmptyBag(pl) == 0) {
                                    Service.gI().sendThongBaoOK(pl, "Cần 1 ô hành trang để mở");
                                    return;
                                }
                                Item phuKien = ItemService.gI().createNewItem((short) listItem[Util.nextInt(2)]);
                                if (phuKien.template.id == 856) {
                                    phuKien.itemOptions.add(new Item.ItemOption(50, 10));
                                    phuKien.itemOptions.add(new Item.ItemOption(77, 10));
                                    phuKien.itemOptions.add(new Item.ItemOption(103, 10));
                                } else if (phuKien.template.id == 943) {
                                    phuKien.itemOptions.add(new Item.ItemOption(50, 10));
                                } else if (phuKien.template.id == 942) {
                                    phuKien.itemOptions.add(new Item.ItemOption(77, 10));
                                    phuKien.itemOptions.add(new Item.ItemOption(103, 10));
                                }
                                if (Util.isTrue(95, 100)) {
                                    phuKien.itemOptions.add(new Item.ItemOption(93, Util.nextInt(1, 5)));
                                }
                                InventoryService.gI().addItemBag(pl, phuKien);
                                InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                                InventoryService.gI().sendItemBag(pl);
                                Service.gI().sendThongBao(pl, "Bạn đã nhận được " + phuKien.template.name);
                            }
                            break;
//                            case 1628: {
//                                Player player = pl;
//                                if (player.pet != null) {
//                                    if (player.pet.playerSkill.skills.get(1).skillId != -1) {
//                                        player.pet.openSkill2();
//                                    } else {
//                                        Service.gI().sendThongBao(player, "Ít nhất đệ tử ngươi phải có chiêu 2 chứ!");
//                                        return;
//                                    }
//                                } else {
//                                    Service.gI().sendThongBao(player, "Ngươi làm gì có đệ tử?");
//                                    return;
//                                }
//                            }
//                            break;
                            // case 1629: {
                            //     Player player = pl;
                            //     if (player.pet != null) {
                            //         if (player.pet.playerSkill.skills.get(2).skillId != -1) {
                            //             player.pet.openSkill3();
                            //         } else {
                            //             Service.gI().sendThongBao(player, "Ít nhất đệ tử ngươi phải có chiêu 3 chứ!");
                            //             return;
                            //         }
                            //     } else {
                            //         Service.gI().sendThongBao(player, "Ngươi làm gì có đệ tử?");
                            //         return;
                            //     }
                            // }
                            // break;
                            // case 1630: {
                            //     Player player = pl;
                            //     if (player.pet != null) {
                            //         if (player.pet.playerSkill.skills.get(3).skillId != -1) {
                            //             player.pet.openSkill4();
                            //         } else {
                            //             Service.gI().sendThongBao(player, "Ít nhất đệ tử ngươi phải có chiêu 4 chứ!");
                            //             return;
                            //         }
                            //     } else {
                            //         Service.gI().sendThongBao(player, "Ngươi làm gì có đệ tử?");
                            //         return;
                            //     }
                            // }
                            // break;
                            case 628: {
                                int ct = Util.nextInt(618, 626);
                                Item caiTrangHaiTac = ItemService.gI().createNewItem((short) ct);
                                caiTrangHaiTac.itemOptions.add(new Item.ItemOption(93, 30));
                                caiTrangHaiTac.itemOptions.add(new Item.ItemOption(50, 15));
                                caiTrangHaiTac.itemOptions.add(new Item.ItemOption(77, 15));
                                caiTrangHaiTac.itemOptions.add(new Item.ItemOption(103, 15));
                                caiTrangHaiTac.itemOptions.add(new Item.ItemOption(149, 1));
                                caiTrangHaiTac.itemOptions.add(new Item.ItemOption(93, 30));
                                InventoryService.gI().addItemBag(pl, caiTrangHaiTac);
                                InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                                Service.gI().sendThongBao(pl,
                                        "Bạn đã nhận được cải trang " + caiTrangHaiTac.template.name);
                            }
                            break;
                            case 1440: {
                                try {

                                    short spl = (short) (Util.nextInt(0, 100) < 70 ? 441 : 1416); // 70% là 441, 30% là
                                                                                                  // 1416
                                    int rand = Util.nextInt(0, 6);
                                    Item caiTrangHaiTac = ItemService.gI().createNewItem((short) (spl + rand));
                                    caiTrangHaiTac.itemOptions
                                            .add(new Item.ItemOption(95 + rand, (rand == 3 || rand == 4) ? 3 : 5));
                                    caiTrangHaiTac.itemOptions.add(new Item.ItemOption(30, 1));
                                    InventoryService.gI().addItemBag(pl, caiTrangHaiTac);
                                    InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                                    Service.gI().sendThongBao(pl, "Bạn đã nhận được " + caiTrangHaiTac.template.name);
                                } catch (Exception e) {
                                    Logger.error("Lỗi khi tạo vật phẩm Hải Tặc!\n");
                                }
                            }
                            break;
                            case 1453: {
                                int ct = Util.nextInt(1416, 1422);
                                Item caiTrangHaiTac = ItemService.gI().createNewItem((short) ct);
                                caiTrangHaiTac.itemOptions.add(new Item.ItemOption(93, 30));
                                InventoryService.gI().addItemBag(pl, caiTrangHaiTac);
                                InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                                Service.gI().sendThongBao(pl, "Bạn đã nhận được " + caiTrangHaiTac.template.name);
                            }
                            break;
                            case 1536: {
                            }
                            break;
                            case 1703://set tl kh
                                UseItem.gI().Hopdothanlinh(pl, item);
                                break;                                                                 
                            case 1704://set hd kh
                                UseItem.gI().Hopdohuydiet(pl, item);
                                break;    
                            case 1793://set hd kh
                                ItemService.gI().setHoaThan(pl);
                                break;      
                            case 1794://Bia Tiger
                                if (pl.itemTime.isDrinkBia333) {
                                    Service.gI().sendThongBao(pl, "Chỉ được sử dụng 1 cái");
                                    break;
                                }
                                useItemTime(pl, item);
                                break;
                            case 1796://Bia 333
                                if (pl.itemTime.isDrinkBiaTiger) {
                                    Service.gI().sendThongBao(pl, "Chỉ được sử dụng 1 cái");
                                    break;
                                }
                                useItemTime(pl, item);
                                break;
                            case 1798://Bia Henkiken
                                if (pl.itemTime.isDrinkBiaTiger || pl.itemTime.isDrinkBia333) {
                                    Service.gI().sendThongBao(pl, "Chỉ được sử dụng 1 cái");
                                    break;
                                }
                                useItemTime(pl, item);
                                break;
                        }
                        break;
                }
                TaskService.gI().checkDoneTaskUseItem(pl, item);
                InventoryService.gI().sendItemBag(pl);
            } else {
                Service.gI().sendThongBaoOK(pl, "Sức mạnh không đủ yêu cầu");
            }
        }
    }

    public void openRuongGo(Player player) {
        // Tìm kiếm rương gỗ trong hành trang của người chơi
        Item ruongGo = InventoryService.gI().findItemBag(player, 570);
        if (ruongGo != null) {
            int level = InventoryService.gI().getParam(player, 72, 570);

            // Tính toán số ô trống cần thiết cho phần thưởng
            int requiredSlots = calculateRequiredEmptySlots(level);

            // Kiểm tra số lượng ô trống trong hành trang người chơi
            if (InventoryService.gI().getCountEmptyBag(player) < requiredSlots) {
                Service.gI().sendThongBao(player,
                        "Cần ít nhất " + (requiredSlots - InventoryService.gI().getCountEmptyBag(player))
                                + " ô trống trong hành trang");
            } else {
                player.itemsWoodChest.clear(); // Xóa các item trong danh sách phần thưởng trước khi mở rương

                // Phần thưởng khi cấp độ = 0
                if (level == 0) {
                    InventoryService.gI().subQuantityItemsBag(player, ruongGo, 1);
                    InventoryService.gI().sendItemBag(player);

                    // Tạo item vàng (ID 190) cho phần thưởng
                    Item item = ItemService.gI().createNewItem((short) 190);
                    item.quantity = 1; // Số lượng vàng ở level 0 là 1
                    InventoryService.gI().addItemBag(player, item);
                    InventoryService.gI().sendItemBag(player);

                    Service.gI().sendThongBao(player, "reward");
                    return; // Thoát ra nếu cấp độ = 0
                }

                // Tính toán số lượng vàng thưởng dựa trên cấp độ
                int baseGoldAmount = 100 * level; // Tính số lượng vàng cơ bản
                int randomFactor = Util.nextInt(-15, 15); // Tạo một yếu tố ngẫu nhiên để biến động số lượng vàng
                int goldAmount = baseGoldAmount + (baseGoldAmount * randomFactor / 100);

                Item itemGold = ItemService.gI().createNewItem((short) 190);
                itemGold.quantity = goldAmount * 1000; // Số lượng vàng thưởng (đơn vị là vàng)
                player.itemsWoodChest.add(itemGold); // Thêm vàng vào phần thưởng
                // Kiểm tra nếu cấp độ > 9
                if (level >= 9) {
                    // Tính số lượng item ID 77, bắt đầu từ 100 và tăng 20 mỗi cấp
                    int quantity = 100 + (level - 9) * 20;

                    // Tạo item với ID 77
                    Item item77 = ItemService.gI().createNewItem((short) 77);
                    item77.quantity = quantity;

                    // Thêm item vào danh sách phần thưởng
                    player.itemsWoodChest.add(item77);
                }

                // Phần thưởng đồ tại rương
                int clothesCount = 1;
                if (level >= 5 && level <= 8) {
                    clothesCount = 2; // Nếu cấp độ từ 5 đến 8, thưởng 2 món đồ
                } else if (level >= 10 && level <= 12) {
                    clothesCount = 3; // Nếu cấp độ từ 10 đến 12, thưởng 3 món đồ
                }

                // Tạo đồ thưởng (clothes) và thêm vào phần thưởng
                for (int i = 0; i < clothesCount; i++) {
                    int randItemId = randClothes(level); // Lấy ID ngẫu nhiên của món đồ
                    Item rewardItem = ItemService.gI().createNewItem((short) randItemId);
                    List<Item.ItemOption> ops = ItemService.gI().getListOptionItemShop((short) randItemId);
                    if (ops != null && !ops.isEmpty()) {
                        rewardItem.itemOptions.addAll(ops); // Thêm thuộc tính item
                    }
                    rewardItem.quantity = 1; // Số lượng món đồ là 1
                    player.itemsWoodChest.add(rewardItem); // Thêm món đồ vào phần thưởng
                }

                // Phần thưởng item ngẫu nhiên (từ rewardItems)
                int[] rewardItems = { 17, 18, 19, 20, 380, 381, 382, 383, 384, 385, 1229 };
                int rewardCount = 2; // Số lượng item mặc định

                // Thay đổi số lượng phần thưởng tùy theo cấp độ
                if (level >= 5 && level <= 8) {
                    rewardCount = 3; // Nếu cấp độ từ 5 đến 8, thưởng 3 item ngẫu nhiên
                } else if (level >= 10 && level <= 12) {
                    rewardCount = 4; // Nếu cấp độ từ 10 đến 12, thưởng 4 item ngẫu nhiên
                }

                // Thêm item ngẫu nhiên vào phần thưởng
                Set<Integer> selectedItems = new HashSet<>();
                while (selectedItems.size() < rewardCount) {
                    int randItemId = rewardItems[Util.nextInt(0, rewardItems.length - 1)];
                    if (!selectedItems.contains(randItemId)) {
                        selectedItems.add(randItemId);
                        Item rewardItem = ItemService.gI().createNewItem((short) randItemId);
                        rewardItem.quantity = Util.nextInt(1, level); // Số lượng item phụ thuộc vào cấp độ
                        player.itemsWoodChest.add(rewardItem); // Thêm item vào phần thưởng
                    }
                }

                // Phần thưởng sao pha lê (nâng cấp)
                int saoPhaLeCount = (level > 9) ? 2 : 1; // Nếu cấp độ > 9, thêm 2 sao phá lệ
                for (int i = 0; i < saoPhaLeCount; i++) {
                    int rand = Util.nextInt(0, 6);
                    Item level1 = ItemService.gI().createNewItem((short) (441 + rand));
                    level1.itemOptions.add(new Item.ItemOption(95 + rand, (rand == 3 || rand == 4) ? 3 : 5));
                    level1.quantity = Util.nextInt(1, 3); // Số lượng sao phá lệ
                    player.itemsWoodChest.add(level1); // Thêm sao phá lệ vào phần thưởng
                }

                // Phần thưởng đá nâng cấp
                int dncCount = (level > 9) ? 2 : 1; // Nếu cấp độ > 9, có 2 đá nâng cấp
                for (int i = 0; i < dncCount; i++) {
                    int rand = Util.nextInt(0, 4);
                    Item dnc = ItemService.gI().createNewItem((short) (220 + rand));
                    dnc.itemOptions.add(new Item.ItemOption(71 - rand, 0));
                    dnc.quantity = Util.nextInt(1, level * 2); // Số lượng đá nâng cấp phụ thuộc vào cấp độ
                    player.itemsWoodChest.add(dnc); // Thêm đá nâng cấp vào phần thưởng
                }

                // Trừ 1 rương gỗ
                InventoryService.gI().subQuantityItemsBag(player, ruongGo, 1);
                InventoryService.gI().sendItemBag(player);

                // Thêm các phần thưởng vào hành trang
                for (Item it : player.itemsWoodChest) {
                    InventoryService.gI().addItemBag(player, it);
                }
                InventoryService.gI().sendItemBag(player);

                // Cập nhật chỉ số rương gỗ
                player.indexWoodChest = player.itemsWoodChest.size() - 1;
                int i = player.indexWoodChest;
                if (i < 0) {
                    return;
                }
                Item itemWoodChest = player.itemsWoodChest.get(i);
                player.indexWoodChest--;
                String info = "|1|" + itemWoodChest.template.name;
                if (itemWoodChest.quantity > 1) {
                    info += " (x" + itemWoodChest.quantity + ")";
                }

                String info2 = "\n|2|";
                if (!itemWoodChest.itemOptions.isEmpty()) {
                    for (Item.ItemOption io : itemWoodChest.itemOptions) {
                        if (io.optionTemplate.id != 102 && io.optionTemplate.id != 73) {
                            info2 += io.getOptionString() + "\n";
                        }
                    }
                }
                info = (info2.length() > "\n|2|".length() ? (info + info2).trim() : info.trim()) + "\n|0|"
                        + itemWoodChest.template.description;
                NpcService.gI().createMenuConMeo(player, ConstNpc.RUONG_GO, -1, "Bạn nhận được\n"
                        + info.trim(), "OK" + (i > 0 ? " [" + i + "]" : ""));
            }
        }
    }

    public int calculateRequiredEmptySlots(int level) {
        // Khởi tạo số ô trống cần thiết
        int requiredSlots = 0;

        // Tính số lượng vàng
        int baseGoldAmount = 100 * level;
        int randomFactor = Util.nextInt(-15, 15);
        int goldAmount = baseGoldAmount + (baseGoldAmount * randomFactor / 100);

        // Vàng có ID 190, không tính vào số ô trống yêu cầu
        if (goldAmount > 0) {
            requiredSlots++;
        }

        // Tính phần thưởng quần áo
        int clothesCount = 1;
        if (level >= 5 && level <= 8) {
            clothesCount = 2;
        } else if (level >= 10 && level <= 12) {
            clothesCount = 3;
        }
        // Đếm số phần thưởng quần áo
        requiredSlots += clothesCount;

        // Tính phần thưởng item hỗ trợ
        int[] rewardItems = { 17, 18, 19, 20, 380, 381, 382, 383, 384, 385, 1229 };
        int rewardCount = 2;

        if (level >= 5 && level <= 8) {
            rewardCount = 3;
        } else if (level >= 10 && level <= 12) {
            rewardCount = 4;
        }
        // Đếm phần thưởng item hỗ trợ
        requiredSlots += rewardCount;

        // Tính sao pha lê (Số lượng 2 nếu level > 9)
        int saoPhaLeCount = (level > 9) ? 2 : 1;
        requiredSlots += saoPhaLeCount;

        // Tính đá nâng cấp (Số lượng 2 nếu level > 9)
        int dncCount = (level > 9) ? 2 : 1;
        requiredSlots += dncCount;

        // Trả về tổng số ô trống cần thiết
        return requiredSlots;
    }

    private int randClothes(int level) {
        int result = level - Util.nextInt(2, 4);
        if (result < 1) {
            result = 1;
        }
        return ConstItem.LIST_ITEM_CLOTHES[Util.nextInt(0, 2)][Util.nextInt(0, 4)][result];
    }

    private void changePet(Player player, Item item) {
        if (player.pet != null) {
            int gender = player.pet.gender + 1;
            if (gender > 2) {
                gender = 0;
            }
            PetService.gI().changeNormalPet(player, gender);
            InventoryService.gI().subQuantityItemsBag(player, item, 1);
        } else {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
        }
    }

    private void eatGrapes(Player pl, Item item) {
        int percentCurrentStatima = pl.nPoint.stamina * 100 / pl.nPoint.maxStamina;
        if (percentCurrentStatima > 50) {
            Service.gI().sendThongBao(pl, "Thể lực vẫn còn trên 50%");
            return;
        } else if (item.template.id == 211) {
            pl.nPoint.stamina = pl.nPoint.maxStamina;
            Service.gI().sendThongBao(pl, "Thể lực của bạn đã được hồi phục 100%");
        } else if (item.template.id == 212) {
            pl.nPoint.stamina += (pl.nPoint.maxStamina * 20 / 100);
            Service.gI().sendThongBao(pl, "Thể lực của bạn đã được hồi phục 20%");
        }
        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
        InventoryService.gI().sendItemBag(pl);
        PlayerService.gI().sendCurrentStamina(pl);
    }

    private void openCSKB(Player pl, Item item) {
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            short[] temp = { 76, 188, 189, 190, 381, 382, 383, 384, 385 };
            int[][] gold = { { 5000, 10000000 } };
            byte index = (byte) Util.nextInt(0, temp.length - 1);
            short[] icon = new short[2];
            icon[0] = item.template.iconID;
            if (index <= 3) {
                pl.inventory.gold += Util.nextInt(gold[0][0], gold[0][1]);
                if (pl.inventory.gold > Inventory.LIMIT_GOLD) {
                    pl.inventory.gold = Inventory.LIMIT_GOLD;
                }
                PlayerService.gI().sendInfoHpMpMoney(pl);
                icon[1] = 930;
            } else {
                Item it = ItemService.gI().createNewItem(temp[index]);
                it.itemOptions.add(new ItemOption(73, 0));
                InventoryService.gI().addItemBag(pl, it);
                icon[1] = it.template.iconID;
            }
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBag(pl);

            CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy");
        }
    }

    private void useItemTime(Player pl, Item item) {
        switch (item.template.id) {
            case 1731:
                if (pl.itemTime.isUseLoX5 == true || pl.itemTime.isUseLoX7 == true || pl.itemTime.isUseLoX10 == true || pl.itemTime.isUseLoX15 == true) {
                    Service.gI().sendThongBao(pl, "Bạn đang sử dụng nước thánh rồi");
                    return;
                }
                pl.itemTime.lastTimeLoX2 = System.currentTimeMillis();
                pl.itemTime.isUseLoX2 = true;
                break;
            case 1727:
                if (pl.itemTime.isUseLoX2 == true || pl.itemTime.isUseLoX7 == true || pl.itemTime.isUseLoX10 == true || pl.itemTime.isUseLoX15 == true) {
                    Service.gI().sendThongBao(pl, "Bạn đang sử dụng nước thánh rồi");
                    return;
                }
                pl.itemTime.lastTimeLoX5 = System.currentTimeMillis();
                pl.itemTime.isUseLoX5 = true;
                break;
            case 1728:
                if (pl.itemTime.isUseLoX5 == true || pl.itemTime.isUseLoX2 == true || pl.itemTime.isUseLoX10 == true || pl.itemTime.isUseLoX15 == true) {
                    Service.gI().sendThongBao(pl, "Bạn đang sử dụng nước thánh rồi");
                    return;
                }
                pl.itemTime.lastTimeLoX7 = System.currentTimeMillis();
                pl.itemTime.isUseLoX7 = true;
                break;
            case 1729:
                if (pl.itemTime.isUseLoX5 == true || pl.itemTime.isUseLoX7 == true || pl.itemTime.isUseLoX2 == true || pl.itemTime.isUseLoX15 == true) {
                    Service.gI().sendThongBao(pl, "Bạn đang sử dụng nước thánh rồi");
                    return;
                }
                pl.itemTime.lastTimeLoX10 = System.currentTimeMillis();
                pl.itemTime.isUseLoX10 = true;
                break;
            case 1730:
                if (pl.itemTime.isUseLoX5 == true || pl.itemTime.isUseLoX7 == true || pl.itemTime.isUseLoX10 == true || pl.itemTime.isUseLoX2 == true) {
                    Service.gI().sendThongBao(pl, "Bạn đang sử dụng nước thánh rồi");
                    return;
                }
                pl.itemTime.lastTimeLoX15 = System.currentTimeMillis();
                pl.itemTime.isUseLoX15 = true;
                break;
            case 764:
                pl.itemTime.lastTimeKhauTrang = System.currentTimeMillis();
                pl.itemTime.isUseKhauTrang = true;
                break;
            case 1628:
                pl.itemTime.lastTimeBuax2DeTu = System.currentTimeMillis();
                pl.itemTime.isUseBuax2DeTu = true;
                break;


           case 1701: // Bánh Chưng
                pl.itemTime.lastTimeBanhChung = System.currentTimeMillis();
                pl.itemTime.isUserBanhChung = true;
                Service.gI().point(pl);
                break;
            case 382: // bổ huyết
                if (pl.itemTime.isUseBoHuyet2) {
                    Service.gI().sendThongBao(pl, "Chỉ có thể sự dụng cùng lúc 1 vật phẩm bổ trợ cùng loại");
                    return;
                }
                pl.itemTime.lastTimeBoHuyet = System.currentTimeMillis();
                pl.itemTime.isUseBoHuyet = true;
                Service.gI().point(pl);
                break;
            case 383: // bổ khí
                if (pl.itemTime.isUseBoKhi2) {
                    Service.gI().sendThongBao(pl, "Chỉ có thể sự dụng cùng lúc 1 vật phẩm bổ trợ cùng loại");
                    return;
                }
                pl.itemTime.lastTimeBoKhi = System.currentTimeMillis();
                pl.itemTime.isUseBoKhi = true;
                Service.gI().point(pl);
                break;
            case 384: // giáp xên
                if (pl.itemTime.isUseGiapXen2) {
                    Service.gI().sendThongBao(pl, "Chỉ có thể sự dụng cùng lúc 1 vật phẩm bổ trợ cùng loại");
                    return;
                }
                pl.itemTime.lastTimeGiapXen = System.currentTimeMillis();
                pl.itemTime.isUseGiapXen = true;
                Service.gI().point(pl);
                break;
            case 385: // ẩn danh
                pl.itemTime.lastTimeAnDanh = System.currentTimeMillis();
                pl.itemTime.isUseAnDanh = true;

                break;
            case 1150: // cuồng nộ 2
                if (pl.itemTime.isUseCuongNo) {
                    Service.gI().sendThongBao(pl, "Chỉ có thể sự dụng cùng lúc 1 vật phẩm bổ trợ cùng loại");
                    return;
                }
                pl.itemTime.lastTimeCuongNo2 = System.currentTimeMillis();
                pl.itemTime.isUseCuongNo2 = true;
                Service.gI().point(pl);
                break;

            case 1151: // bổ khí 2
                if (pl.itemTime.isUseBoKhi) {
                    Service.gI().sendThongBao(pl, "Chỉ có thể sự dụng cùng lúc 1 vật phẩm bổ trợ cùng loại");
                    return;
                }
                pl.itemTime.lastTimeBoKhi2 = System.currentTimeMillis();
                pl.itemTime.isUseBoKhi2 = true;
                Service.gI().point(pl);
                break;

            case 1152: // bổ huyết 2
                if (pl.itemTime.isUseBoHuyet) {
                    Service.gI().sendThongBao(pl, "Chỉ có thể sự dụng cùng lúc 1 vật phẩm bổ trợ cùng loại");
                    return;
                }
                pl.itemTime.lastTimeBoHuyet2 = System.currentTimeMillis();
                pl.itemTime.isUseBoHuyet2 = true;
                Service.gI().point(pl);
                break;

            case 1153: // giáp xên 2
                if (pl.itemTime.isUseGiapXen) {
                    Service.gI().sendThongBao(pl, "Chỉ có thể sự dụng cùng lúc 1 vật phẩm bổ trợ cùng loại");
                    return;
                }
                pl.itemTime.lastTimeGiapXen2 = System.currentTimeMillis();
                pl.itemTime.isUseGiapXen2 = true;
                Service.gI().point(pl);
                break;

            case 1154: // an danh
                pl.itemTime.lastTimeAnDanh2 = System.currentTimeMillis();
                pl.itemTime.isUseAnDanh2 = true;
                break;

            case 379: // máy dò capsule
                pl.itemTime.lastTimeUseMayDo = System.currentTimeMillis();
                pl.itemTime.isUseMayDo = true;
                break;

            case 638: // Commeson
                pl.itemTime.lastTimeUseCMS = System.currentTimeMillis();
                pl.itemTime.isUseCMS = true;
                break;
            case 1233: // Nồi cơm điện
                pl.itemTime.lastTimeUseNCD = System.currentTimeMillis();
                pl.itemTime.isUseNCD = true;
                break;
            case 579:
            case 1045: // Đuôi khỉ
                pl.itemTime.lastTimeUseDK = System.currentTimeMillis();
                pl.itemTime.isUseDK = true;
                ItemTimeService.gI().removeItemTime(pl, pl.itemTime.iconDK);
                pl.itemTime.iconDK = item.template.iconID;
                break;
            case 1016: // Thuốc mỡ
                if (pl.itemTime.isEatThuocMo2) {
                    pl.itemTime.lastTimeEatThuocMo = System.currentTimeMillis();
                    pl.itemTime.isEatThuocMo = true;
                    ItemTimeService.gI().removeItemTime(pl, pl.itemTime.iconThuocMo);
                    pl.itemTime.iconThuocMo = item.template.iconID;
                } else {
                    Service.gI().sendThongBao(pl, "Chỉ có thể sự dụng cùng lúc 1 vật phẩm bổ trợ cùng loại");
                }

            case 1017: // Thuốc mỡ 2
                if (!pl.itemTime.isEatThuocMo) {
                    pl.itemTime.lastTimeEatThuocMo2 = System.currentTimeMillis();
                    pl.itemTime.isEatThuocMo2 = true;
                    ItemTimeService.gI().removeItemTime(pl, pl.itemTime.iconThuocMo2);
                    pl.itemTime.iconThuocMo2 = item.template.iconID;
                    break;
                } else {
                    Service.gI().sendThongBao(pl, "Chỉ có thể sự dụng cùng lúc 1 vật phẩm bổ trợ cùng loại");
                }

            case 663: // bánh pudding
            case 664: // xúc xíc
            case 665: // kem dâu
            case 666: // mì ly
            case 667: // sushi
                pl.itemTime.lastTimeEatMeal = System.currentTimeMillis();
                pl.itemTime.isEatMeal = true;
                ItemTimeService.gI().removeItemTime(pl, pl.itemTime.iconMeal);
                pl.itemTime.iconMeal = item.template.iconID;
                break;
            case 880:
            case 881:
            case 882:
                pl.itemTime.lastTimeEatMeal2 = System.currentTimeMillis();
                pl.itemTime.isEatMeal2 = true;
                ItemTimeService.gI().removeItemTime(pl, pl.itemTime.iconMeal2);
                pl.itemTime.iconMeal2 = item.template.iconID;
                break;
            case 889:
            case 900:
            case 902:
            case 903:
                pl.itemTime.lastTimeEatMeal3 = System.currentTimeMillis();
                pl.itemTime.isEatMeal3 = true;
                ItemTimeService.gI().removeItemTime(pl, pl.itemTime.iconMeal3);
                pl.itemTime.iconMeal3 = item.template.iconID;
                break;
            case 1115: // máy dò đồ
                pl.itemTime.lastTimeUseMayDo2 = System.currentTimeMillis();
                pl.itemTime.isUseMayDo2 = true;
                break;
            case 1794: // Tiger
                pl.itemTime.lastTimeDrinkBiaTiger = System.currentTimeMillis();
                pl.itemTime.isDrinkBiaTiger = true;
                break;
            case 1796: // 333
                pl.itemTime.lastTimeDrinkBia333 = System.currentTimeMillis();
                pl.itemTime.isDrinkBia333 = true;
                break;
            case 1798: // Heniken
                pl.itemTime.lastTimeDrinkHeniken = System.currentTimeMillis();
                pl.itemTime.isDrinkHeniken = true;
                break;
            case 1999: // Đuôi khỉ
                if (!pl.effectSkill.isMonkey) {
                    int timeMonkey = 120000; // 2 phút
                    pl.effectSkill.isMonkey = true;
                    pl.effectSkill.timeMonkey = timeMonkey;
                    pl.effectSkill.lastTimeUpMonkey = System.currentTimeMillis();
                    pl.effectSkill.levelMonkey = 7;
                    pl.nPoint.setHp(Util.maxIntValue(pl.nPoint.hp * 2));
                    Service.gI().Send_Caitrang(pl);
                    InventoryService.gI().subQuantityItemsBag(pl, item, 0);

                    Service.gI().sendThongBao(pl, "Biến khỉ thành công");
                } else {
                    Service.gI().sendThongBao(pl, "Biến cl lắm thế");
                }
                break;
        }
        Service.gI().point(pl);
        ItemTimeService.gI().sendAllItemTime(pl);
        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
        InventoryService.gI().sendItemBag(pl);
    }

    private void controllerCallRongThan(Player pl, Item item) {
        int tempId = item.template.id;
        if (tempId >= SummonDragon.NGOC_RONG_1_SAO && tempId <= SummonDragon.NGOC_RONG_7_SAO) {
            switch (tempId) {
                case SummonDragon.NGOC_RONG_1_SAO:
                case SummonDragon.NGOC_RONG_2_SAO:
                case SummonDragon.NGOC_RONG_3_SAO:
                    SummonDragon.gI().openMenuSummonShenron(pl, (byte) (tempId - 13));
                    break;
                default:
                    NpcService.gI().createMenuConMeo(pl, ConstNpc.TUTORIAL_SUMMON_DRAGON,
                            -1, "Bạn chỉ có thể gọi rồng từ ngọc 3 sao, 2 sao, 1 sao", "Hướng\ndẫn thêm\n(mới)", "OK");
                    break;
            }
        } else if (tempId >= ShenronEventService.NGOC_RONG_1_SAO && tempId <= ShenronEventService.NGOC_RONG_7_SAO) {
            ShenronEventService.gI().openMenuSummonShenron(pl, 0);
        }
    }

    private void learnSkill(Player pl, Item item) {
        Message msg;
        try {
            if (item.template.gender == pl.gender || item.template.gender == 3) {
                String[] subName = item.template.name.split("");
                byte level = Byte.parseByte(subName[subName.length - 1]);
                Skill curSkill = SkillUtil.getSkillByItemID(pl, item.template.id);
                if (curSkill.point == 7) {
                    Service.gI().sendThongBao(pl, "Kỹ năng đã đạt tối đa!");
                } else {
                    if (curSkill.point == 0) {
                        if (level == 1) {// Hoc skill moi
                            curSkill = SkillUtil.createSkill(SkillUtil.getTempSkillSkillByItemID(item.template.id),
                                    level);
                            SkillUtil.setSkill(pl, curSkill);
                            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                            msg = Service.gI().messageSubCommand((byte) 23);
                            msg.writer().writeShort(curSkill.skillId);
                            pl.sendMessage(msg);
                            msg.cleanup();
                        } else { // neu chua hoc ma hoc lv cao
                            Skill skillNeed = SkillUtil
                                    .createSkill(SkillUtil.getTempSkillSkillByItemID(item.template.id), level);
                            Service.gI().sendThongBao(pl,
                                    "Vui lòng học " + skillNeed.template.name + " cấp " + skillNeed.point + " trước!");
                        }
                    } else {
                        if (curSkill.point + 1 == level) {
                            curSkill = SkillUtil.createSkill(SkillUtil.getTempSkillSkillByItemID(item.template.id),
                                    level);
                            pl.BoughtSkill.add((int) item.template.id);
                            // System.out.println(curSkill.template.name + " - " + curSkill.point);
                            SkillUtil.setSkill(pl, curSkill);
                            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                            msg = Service.gI().messageSubCommand((byte) 62);
                            msg.writer().writeShort(curSkill.skillId);
                            pl.sendMessage(msg);
                            msg.cleanup();
                        } else {
                            Service.gI().sendThongBao(pl, "Vui lòng học " + curSkill.template.name + " cấp "
                                    + (curSkill.point + 1) + " trước!");
                        }
                    }
                    InventoryService.gI().sendItemBag(pl);
                }
            } else {
                Service.gI().sendThongBao(pl, "Không thể thực hiện");
            }
        } catch (Exception e) {
            Logger.logException(UseItem.class, e);
        }
    }

    private void learnSkillNew2(Player pl, Item item) {
        Message msg;
        try {
            if (item.template.gender == pl.gender || item.template.gender == 3) {
                byte level = SkillUtil.getLevelSkillByItemID(item.template.id);
                
                Skill curSkill = SkillUtil.getSkillByItemID(pl, item.template.id);
                if (curSkill == null) {
                    SkillService.gI().learSkillSpecial(pl,
                            (byte) SkillUtil.getSkillByItemID(pl, item.template.id).skillId);
                    InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                    return;
                } else {
                    if (curSkill.point == 7) {
                        Service.gI().sendThongBao(pl, "Kỹ năng đã đạt tối đa!");
                    } else {
                        if (curSkill.point == 0) {
                            if (level == 1) {
                                curSkill = SkillUtil.createSkill(SkillUtil.getTempSkillSkillByItemID(item.template.id),
                                        level);
                                SkillUtil.setSkill(pl, curSkill);
                                InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                                msg = Service.gI().messageSubCommand((byte) 23);
                                msg.writer().writeShort(curSkill.skillId);
                                pl.sendMessage(msg);
                                msg.cleanup();
                                if (curSkill.template.id == Skill.SUPER_TRANFORMATION) {
                                    curSkill = SkillUtil.createSkill(Skill.EVOLUTION, level);
                                    SkillUtil.setSkill(pl, curSkill);
                                    InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                                    msg = Service.gI().messageSubCommand((byte) 23);
                                    msg.writer().writeShort(curSkill.skillId);
                                    pl.sendMessage(msg);
                                    msg.cleanup();
                                }
                            } else {
                                Skill skillNeed = SkillUtil
                                        .createSkill(SkillUtil.getTempSkillSkillByItemID(item.template.id), level);
                                Service.gI().sendThongBao(pl, "Vui lòng học " + skillNeed.template.name + " cấp "
                                        + skillNeed.point + " trước!");
                            }
                        } else {
                            if (curSkill.point + 1 == level) {
                                curSkill = SkillUtil.createSkill(SkillUtil.getTempSkillSkillByItemID(item.template.id),
                                        level);
                                // System.out.println(curSkill.template.name + " - " + curSkill.point);
                                SkillUtil.setSkill(pl, curSkill);
                                InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                                msg = Service.gI().messageSubCommand((byte) 62);
                                msg.writer().writeShort(curSkill.skillId);
                                pl.sendMessage(msg);
                                msg.cleanup();
                            } else {
                                Service.gI().sendThongBao(pl, "Vui lòng học " + curSkill.template.name + " cấp "
                                        + (curSkill.point + 1) + " trước!");
                            }
                        }
                        InventoryService.gI().sendItemBag(pl);
                    }
                }
            } else {
                Service.gI().sendThongBao(pl, "Mua nhầm sách rồi cu");
            }
        } catch (Exception e) {

        }
    }

    private void learnSkillSuperNew(Player pl, Item item) {
        Message msg;
        try {
            if (item.template.gender == pl.gender || item.template.gender == 3) {
                String[] subName = item.template.name.split("");
                byte level = Byte.parseByte(subName[subName.length - 1]);
                Skill curSkill = SkillUtil.getSkillByItemID(pl, item.template.id);
                if(curSkill == null) {
                    Service.gI().sendThongBao(pl, "Không thể thực hiện");
                    return;
                }
                if (curSkill.point == 6) {
                    Service.gI().sendThongBao(pl, "Kỹ năng đã đạt tối đa!");
                } else {
                    if (curSkill.point == 0) {
                        if (level == 1) {
                            curSkill = SkillUtil.createSkill(SkillUtil.getTempSkillSkillByItemID(item.template.id),
                                    level);
                            SkillUtil.setSkill(pl, curSkill);
                            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                            msg = Service.gI().messageSubCommand((byte) 23);
                            msg.writer().writeShort(curSkill.skillId);
                            pl.sendMessage(msg);
                            msg.cleanup();
                            SkillService.gI().learSkillSpecial(pl, (byte) 30);
                        } else {
                            Skill skillNeed = SkillUtil
                                    .createSkill(SkillUtil.getTempSkillSkillByItemID(item.template.id), level);
                            if (level > 1) {
                                Item itemNew = ItemService.gI().createNewItem((short) (item.template.id - 1));
                                String name = itemNew.template.name;
                                String desiredName = name.substring(5);
                                Service.gI().sendThongBao(pl, "Vui lòng học " + desiredName + " trước!");
                            } else {
                                Service.gI().sendThongBao(pl, "Vui lòng học " + skillNeed.template.name + " trước!");
                            }
                        }
                    } else {
                        if (curSkill.point + 1 == level) {
                            curSkill = SkillUtil.createSkill(SkillUtil.getTempSkillSkillByItemID(item.template.id),
                                    level);
                            SkillUtil.setSkill(pl, curSkill);
                            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                            msg = Service.gI().messageSubCommand((byte) 62);
                            msg.writer().writeShort(curSkill.skillId);
                            pl.sendMessage(msg);
                            msg.cleanup();
                        } else {
                            if (level > 1) {
                                Item itemNew = ItemService.gI().createNewItem((short) (item.template.id - 1));
                                String name = itemNew.template.name;
                                String desiredName = name.substring(5);
                                Service.gI().sendThongBao(pl, "Vui lòng học " + desiredName + " trước!");
                            } else {
                                Service.gI().sendThongBao(pl, "Vui lòng học " + curSkill.template.name + " trước!");
                            }
                        }
                    }
                    InventoryService.gI().sendItemBag(pl);
                }
            } else {
                Service.gI().sendThongBao(pl, "Không thể thực hiện");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void useTDLT(Player pl, Item item) {
        if (pl.itemTime.isUseTDLT) {
            ItemTimeService.gI().turnOffTDLT(pl, item);
        } else {
            ItemTimeService.gI().turnOnTDLT(pl, item);
        }
    }

    // private void usePorataGogeta(Player pl) {
    //     if (pl.pet == null || pl.fusion.typeFusion == 4) {
    //         Service.gI().sendThongBao(pl, "Không thể thực hiện");
    //     } else {
    //         if (pl.fusion.typeFusion == ConstPlayer.NON_FUSION) {
    //             pl.pet.fusionGogeta(true);
    //         } else {
    //             pl.pet.unFusion();
    //         }
    //     }
    // }

    private void usePorata2(Player pl) {
        if (pl.pet == null || pl.fusion.typeFusion == 4) {
            Service.gI().sendThongBao(pl, "Không thể thực hiện");
        } else {
            if (pl.fusion.typeFusion == ConstPlayer.NON_FUSION) {
                pl.pet.fusion2(true);
            } else {
                pl.pet.unFusion();
            }
        }
    }

    private void usePorata(Player pl) {
        if (pl.pet == null || pl.fusion.typeFusion == 4) {
            Service.gI().sendThongBao(pl, "Không thể thực hiện");
        } else {
            if (pl.fusion.typeFusion == ConstPlayer.NON_FUSION) {
                pl.pet.fusion(true);
            } else {
                pl.pet.unFusion();
            }
        }
    }

    private void openCapsuleUI(Player pl) {
        pl.iDMark.setTypeChangeMap(ConstMap.CHANGE_CAPSULE);
        ChangeMapService.gI().openChangeMapTab(pl);
    }

    private void openRuongNgocRong(Player pl, Item item) {
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            // Số ngẫu nhiên từ 0 đến 100 để quyết định tỷ lệ
            int random = Util.nextInt(0, 100);

            int itemwhis;

            // 85% xuất hiện item 20, 19, 18, 17
            if (random < 85) {
                int[] itemList = { 20, 19, 18, 17 }; // Các item có tỉ lệ xuất hiện 85%
                itemwhis = itemList[Util.nextInt(0, itemList.length - 1)];
            } // 10% xuất hiện item 16
            else if (random < 95) {
                itemwhis = 16; // Item 16 có tỉ lệ 10%
            } // 5% xuất hiện item 14 hoặc 15
            else {
                itemwhis = Util.nextInt(14, 15); // Item 14 hoặc 15 có tỉ lệ 5%
            }

            // Tạo vật phẩm mới từ ID đã chọn
            Item it = ItemService.gI().createNewItem((short) itemwhis);

            // Kiểm tra nếu người chơi có item 1561 (chìa khóa)
            Item item1561 = InventoryService.gI().findItem(pl.inventory.itemsBag, 1561);
            if (item1561 != null) {
                // Trừ vật phẩm và thêm vật phẩm mới vào túi đồ
                InventoryService.gI().subQuantityItemsBag(pl, item, 1); // Trừ 1 Item Box
                InventoryService.gI().subQuantityItemsBag(pl, item1561, 1); // Trừ 1 Item Box
                InventoryService.gI().addItemBag(pl, it); // Thêm item vào túi
                InventoryService.gI().sendItemBag(pl); // Gửi cập nhật túi đồ
                Service.gI().sendThongBao(pl, "Bạn vừa nhận được " + it.template.name);
            } else {
                Service.gI().sendThongBao(pl, "Bạn không có chìa khoá vàng");
            }
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy");
        }
    }

    public void choseMapCapsule(Player pl, int index) {

        if (pl.idNRNM != -1) {
            Service.gI().sendThongBao(pl, "Không thể mang ngọc rồng này lên Phi thuyền");
            Service.gI().hideWaitDialog(pl);
            return;
        }

        int zoneId = -1;
        if (index > pl.mapCapsule.size() - 1 || index < 0) {
            Service.gI().sendThongBao(pl, "Không thể thực hiện");
            Service.gI().hideWaitDialog(pl);
            return;
        }
        Zone zoneChose = pl.mapCapsule.get(index);
        // Kiểm tra số lượng người trong khu

        if (zoneChose.getNumOfPlayers() > 25
                || MapService.gI().isMapDoanhTrai(zoneChose.map.mapId)
                || MapService.gI().isMapMaBu(zoneChose.map.mapId)
                || MapService.gI().isMapHuyDiet(zoneChose.map.mapId)) {
            Service.gI().sendThongBao(pl, "Hiện tại không thể vào được khu!");
            return;
        }
        if (index != 0 || zoneChose.map.mapId == 21
                || zoneChose.map.mapId == 22
                || zoneChose.map.mapId == 23) {
            pl.mapBeforeCapsule = pl.zone;
        } else {
            zoneId = pl.mapBeforeCapsule != null ? pl.mapBeforeCapsule.zoneId : -1;
            pl.mapBeforeCapsule = null;
        }
        pl.changeMapVIP = true;
        ChangeMapService.gI().changeMapBySpaceShip(pl, pl.mapCapsule.get(index).map.mapId, zoneId, -1);
    }

    public void eatPea(Player player) {
        if (!Util.canDoWithTime(player.lastTimeEatPea, 1000)) {
            return;
        }
        player.lastTimeEatPea = System.currentTimeMillis();
        Item pea = null;
        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && item.template.type == 6) {
                pea = item;
                break;
            }
        }
        if (pea != null) {
            long hpKiHoiPhuc = 0;
            int lvPea = Integer.parseInt(pea.template.name.substring(13));
            for (Item.ItemOption io : pea.itemOptions) {
                if (io.optionTemplate.id == 2) {
                    hpKiHoiPhuc = io.param * 1000;
                    break;
                }
                if (io.optionTemplate.id == 48) {
                    hpKiHoiPhuc = io.param;
                    break;
                }
            }
            player.nPoint.setHp(Util.maxIntValue(player.nPoint.hp + hpKiHoiPhuc));
            player.nPoint.setMp(Util.maxIntValue(player.nPoint.mp + hpKiHoiPhuc));
            PlayerService.gI().sendInfoHpMp(player);
            Service.gI().sendInfoPlayerEatPea(player);
            if (player.pet != null && player.zone.equals(player.pet.zone) && !player.pet.isDie()) {
                int statima = 100 * lvPea;
                player.pet.nPoint.stamina += statima;
                if (player.pet.nPoint.stamina > player.pet.nPoint.maxStamina) {
                    player.pet.nPoint.stamina = player.pet.nPoint.maxStamina;
                }
                player.pet.nPoint.setHp(Util.maxIntValue(player.pet.nPoint.hp + hpKiHoiPhuc));
                player.pet.nPoint.setMp(Util.maxIntValue(player.pet.nPoint.mp + hpKiHoiPhuc));
                Service.gI().sendInfoPlayerEatPea(player.pet);
                Service.gI().chatJustForMe(player, player.pet, "Cám ơn sư phụ");
            }

            InventoryService.gI().subQuantityItemsBag(player, pea, 1);
            InventoryService.gI().sendItemBag(player);
        }
    }

    private void upSkillPet(Player pl, Item item) {
        if (pl.pet == null) {
            Service.gI().sendThongBao(pl, "Không thể thực hiện");
            return;
        }
        try {
            switch (item.template.id) {
                case 402: // skill 1
                    if (SkillUtil.upSkillPet(pl.pet.playerSkill.skills, 0)) {
                        Service.gI().chatJustForMe(pl, pl.pet, "Cám ơn sư phụ");
                        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                    } else {
                        Service.gI().sendThongBao(pl, "Không thể thực hiện");
                    }
                    break;
                case 403: // skill 2
                    if (SkillUtil.upSkillPet(pl.pet.playerSkill.skills, 1)) {
                        Service.gI().chatJustForMe(pl, pl.pet, "Cám ơn sư phụ");
                        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                    } else {
                        Service.gI().sendThongBao(pl, "Không thể thực hiện");
                    }
                    break;
                case 404: // skill 3
                    if (SkillUtil.upSkillPet(pl.pet.playerSkill.skills, 2)) {
                        Service.gI().chatJustForMe(pl, pl.pet, "Cám ơn sư phụ");
                        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                    } else {
                        Service.gI().sendThongBao(pl, "Không thể thực hiện");
                    }
                    break;
                case 759: // skill 4
                    if (SkillUtil.upSkillPet(pl.pet.playerSkill.skills, 3)) {
                        Service.gI().chatJustForMe(pl, pl.pet, "Cám ơn sư phụ");
                        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                    } else {
                        Service.gI().sendThongBao(pl, "Không thể thực hiện");
                    }
                    break;

            }

        } catch (Exception e) {
            Service.gI().sendThongBao(pl, "Không thể thực hiện");
        }
    }

    private void ItemManhGiay(Player pl, Item item) {
        if (pl.winSTT && !Util.isAfterMidnight(pl.lastTimeWinSTT)) {
            Service.gI().sendThongBao(pl, "Hãy gặp thần mèo Karin để sử dụng");
            return;
        } else if (pl.winSTT && Util.isAfterMidnight(pl.lastTimeWinSTT)) {
            pl.winSTT = false;
            pl.callBossPocolo = false;
            pl.zoneSieuThanhThuy = null;
        }
        NpcService.gI().createMenuConMeo(pl, item.template.id, 564,
                "Đây chính là dấu hiệu riêng của...\nĐại Ma Vương Pôcôlô\nĐó là một tên quỷ dữ đội lốt người, một kẻ đại gian ác\ncó sức mạnh vô địch và lòng tham không đáy...\nĐối phó với hắn không phải dễ\nCon có chắc chắn muốn tìm hắn không?",
                "Đồng ý", "Từ chối");
    }

    private void ItemSieuThanThuy(Player pl, Item item) {
        long tnsm = 5_000_000;
        int n = 0;
        switch (item.template.id) {
            case 727:
                n = 2;
                break;
            case 728:
                n = 10;
                break;
        }
        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
        InventoryService.gI().sendItemBag(pl);
        if (Util.isTrue(50, 100)) {
            Service.gI().sendThongBao(pl, "Bạn đã bị chết vì độc của thuốc tăng lực siêu thần thủy.");
            pl.setDie();
        } else {
            for (int i = 0; i < n; i++) {
                Service.gI().addSMTN(pl, (byte) 2, tnsm, true);
            }
        }
    }

    private void Hopdothanlinh(Player pl, Item item) {// hop qua do thần linh
        NpcService.gI().createMenuConMeo(pl, item.template.id, -1, "Chọn hành tinh của Bạn đi", "Set trái đất",
                "Set namec", "Set xayda", "Từ chổi");
    }

    private void namlit(Player pl, Item item) {
        int sotien = 500_000;
        PlayerDAO.sd(pl, sotien);
        PlayerDAO.sds(pl, sotien);
        Service.gI().sendThongBao(pl, "bạn nhận được 500K Coin");
        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
    }

    private void hailit(Player pl, Item item) {
        int sotien = 200_000;
        PlayerDAO.sd(pl, sotien);
        PlayerDAO.sds(pl, sotien);
        Service.gI().sendThongBao(pl, "bạn nhận được 200K Coin");
        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
    }

    private void motlit(Player pl, Item item) {
        int sotien = 100_000;
        PlayerDAO.sd(pl, sotien);
        PlayerDAO.sds(pl, sotien);
        Service.gI().sendThongBao(pl, "bạn nhận được 100K Coin");
        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
    }

    private void namchuc(Player pl, Item item) {
        int sotien = 50_000;
        PlayerDAO.sd(pl, sotien);
        PlayerDAO.sds(pl, sotien);
        Service.gI().sendThongBao(pl, "bạn nhận được 50K Coin");
        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
    }

    private void haichuc(Player pl, Item item) {
        int sotien = 20_000;
        PlayerDAO.sd(pl, sotien);
        PlayerDAO.sds(pl, sotien);
        Service.gI().sendThongBao(pl, "bạn nhận được 20K Coin");
        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
    }

    private void motchuc(Player pl, Item item) {
        int sotien = 10_000;
        PlayerDAO.sd(pl, sotien);
        PlayerDAO.sds(pl, sotien);
        Service.gI().sendThongBao(pl, "bạn nhận được 10K Coin");
        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
    }

    private void namcanh(Player pl, Item item) {
        int sotien = 5_000;
        PlayerDAO.sd(pl, sotien);
        PlayerDAO.sds(pl, sotien);
        Service.gI().sendThongBao(pl, "bạn nhận được 5K Coin");
        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
    }

    private void haicanh(Player pl, Item item) {
        int sotien = 2_000;
        PlayerDAO.sd(pl, sotien);
        PlayerDAO.sds(pl, sotien);
        Service.gI().sendThongBao(pl, "bạn nhận được 2K Coin");
        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
    }

    private void motcanh(Player pl, Item item) {
        int sotien = 1_000;
        PlayerDAO.sd(pl, sotien);
        PlayerDAO.sds(pl, sotien);
        Service.gI().sendThongBao(pl, "bạn nhận được 1K Coin");
        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
    }

    // mở túi mù
    private void openRandomItem(Player pl, Item item) {
        if (InventoryService.gI().getCountEmptyBag(pl) < 1) {
            Service.gI().sendThongBaoOK(pl, "Cần 1 ô hành trang trống");
            return;
        }
        int[] listItem = { 1732, 1733, 1734, 1735, 1736, 1737, 1738, 1739, 1740 };
        int[] probabilities = { 0, 0, 1, 2, 5, 10, 12, 30, 40 };
        int randomValue = Util.nextInt(1, 100);
        int cumulativeProbability = 0;
        int selectedItemID = 1732;
        for (int i = 0; i < listItem.length; i++) {
            cumulativeProbability += probabilities[i];
            if (randomValue <= cumulativeProbability) {
                selectedItemID = listItem[i];
                break;
            }
        }
        Item newItem = ItemService.gI().createNewItem((short) selectedItemID);
        InventoryService.gI().addItemBag(pl, newItem);
        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
        InventoryService.gI().sendItemBag(pl);
        Service.gI().sendThongBao(pl, "Bạn đã nhận được " + newItem.template.name + "!");
    }

    private void Hopdohuydiet(Player pl, Item item) {// hop qua do huy diet
        NpcService.gI().createMenuConMeo(pl, item.template.id, -1, "Hành tinh của mày là gì", "Trái đất", "Da xanh",
                "Xayda", "Từ chổi");
    }

    public void UseCard(Player pl, Item item) {
        RadarCard radarTemplate = RadarService.gI().RADAR_TEMPLATE.stream().filter(c -> c.Id == item.template.id)
                .findFirst().orElse(null);
        if (radarTemplate == null) {
            return;
        }
        if (radarTemplate.Require != -1) {
            RadarCard radarRequireTemplate = RadarService.gI().RADAR_TEMPLATE.stream()
                    .filter(r -> r.Id == radarTemplate.Require).findFirst().orElse(null);
            if (radarRequireTemplate == null) {
                return;
            }
            Card cardRequire = pl.Cards.stream().filter(r -> r.Id == radarRequireTemplate.Id).findFirst().orElse(null);
            if (cardRequire == null || cardRequire.Level < radarTemplate.RequireLevel) {
                Service.gI().sendThongBao(pl, "Bạn cần sưu tầm " + radarRequireTemplate.Name + " ở cấp độ "
                        + radarTemplate.RequireLevel + " mới có thể sử dụng thẻ này");
                return;
            }
        }
        Card card = pl.Cards.stream().filter(r -> r.Id == item.template.id).findFirst().orElse(null);
        if (card == null) {
            Card newCard = new Card(item.template.id, (byte) 1, radarTemplate.Max, (byte) -1, radarTemplate.Options);
            if (pl.Cards.add(newCard)) {
                RadarService.gI().RadarSetAmount(pl, newCard.Id, newCard.Amount, newCard.MaxAmount);
                RadarService.gI().RadarSetLevel(pl, newCard.Id, newCard.Level);
                InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                InventoryService.gI().sendItemBag(pl);
            }
        } else {
            if (card.Level >= 2) {
                Service.gI().sendThongBao(pl, "Thẻ này đã đạt cấp tối đa");
                return;
            }
            card.Amount++;
            if (card.Amount >= card.MaxAmount) {
                card.Amount = 0;
                if (card.Level == -1) {
                    card.Level = 1;
                } else {
                    card.Level++;
                }
                Service.gI().point(pl);
            }
            RadarService.gI().RadarSetAmount(pl, card.Id, card.Amount, card.MaxAmount);
            RadarService.gI().RadarSetLevel(pl, card.Id, card.Level);
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBag(pl);
        }
    }

    private void Gwen_XuongCho(Player pl, Item item) {
        List<Player> bosses = pl.zone.getBosses();
        boolean checkSoi = false;

        synchronized (bosses) {
            for (Player bossPlayer : bosses) {
                if (bossPlayer.id == BossID.SOI_HEC_QUYN1 && !pl.isDie()) {
                    checkSoi = true;
                }
            }
        }

        if (!checkSoi) {
            Service.gI().sendThongBao(pl, "Không tìm thấy Sói hẹc quyn");
            return;
        }

        synchronized (bosses) {
            for (Player bossPlayer : bosses) {
                if (bossPlayer.id == BossID.SOI_HEC_QUYN1) {
                    Boss soihecQuyn = (Boss) bossPlayer;
                    if (soihecQuyn != null) {
                        if (((SoiHecQuyn) soihecQuyn).Gwen_KiemTraNhatXuong()) {
                            Service.gI().sendThongBao(pl, "Sói đã no rồi");
                            continue;
                        } else {
                            ((SoiHecQuyn) soihecQuyn).NhatXuong();
                            Service.gI().chat(soihecQuyn, "Ê, Cục xương ngon quá");
                        }

                        ItemMap itemMap = null;
                        int x = pl.location.x;
                        if (x < 0 || x >= pl.zone.map.mapWidth) {
                            return;
                        }
                        int y = pl.zone.map.yPhysicInTop(x, pl.location.y - 24);
                        itemMap = new ItemMap(pl.zone, 460, 1, x, y, pl.id);
                        itemMap.isPickedUp = true;
                        itemMap.createTime -= 23000;
                        if (itemMap != null) {
                            Service.gI().dropItemMap(pl.zone, itemMap);
                        }

                        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                        InventoryService.gI().sendItemBag(pl);

                        if (Util.nextInt(4) < 3) { // 75% cơ hội
                            int rand = Util.nextInt(0, 6); // Random từ 0 đến 6
                            short idItem = (short) (rand + 441); // Item 441 + rand
                            Item it = ItemService.gI().createNewItem(idItem);
                            it.itemOptions.add(new Item.ItemOption(95 + rand, (rand == 3 || rand == 4) ? 3 : 5));

                            if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
                                InventoryService.gI().addItemBag(pl, it);
                                Service.gI().sendThongBao(pl, "Bạn vừa nhận được " + it.template.name);
                            } else {
                                Service.gI().sendThongBao(pl, "Hành trang không đủ chỗ trống.");
                            }
                        } else {
                            short idItem = 459; // Item 459
                            Item it = ItemService.gI().createNewItem(idItem);
                            it.itemOptions.add(new Item.ItemOption(112, 80));
                            it.itemOptions.add(new Item.ItemOption(93, 90));
                            it.itemOptions.add(new Item.ItemOption(20, Util.nextInt(10000)));
                            if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
                                InventoryService.gI().addItemBag(pl, it);

                                Service.gI().sendThongBao(pl, "Bạn vừa nhận được " + it.template.name);
                            } else {
                                Service.gI().sendThongBao(pl, "Hành trang không đủ chỗ trống.");
                            }
                        }

                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            // Handle exception
                        }

                        ItemMapService.gI().removeItemMapAndSendClient(itemMap);
                        ((SoiHecQuyn) soihecQuyn).leaveMapNew();
                    }

                }
            }
            BadgesTaskService.updateCountBagesTask(pl, ConstTaskBadges.KE_THAO_TUNG_SOI, 10);
            InventoryService.gI().sendItemBag(pl);
        }

    }

    private void Gokudayvip(Player pl, Item item) {
        try {

            List<Short> itemList = Arrays.asList((short) 1587, (short) 1588, (short) 1589, (short) 1590, (short) 1593,
                    (short) 1595);

            short selectedItemId = itemList.get(Util.nextInt(0, itemList.size()));

            Item selectedItem = ItemService.gI().createNewItem(selectedItemId);
            switch (selectedItemId) {
                case 1587:
                    selectedItem.itemOptions.add(new Item.ItemOption(50, 24));
                    selectedItem.itemOptions.add(new Item.ItemOption(77, 24));
                    selectedItem.itemOptions.add(new Item.ItemOption(103, 24));
                    selectedItem.itemOptions.add(new Item.ItemOption(210, 3));
                    if (Util.nextInt(0, 100) < 99) {
                        int randValue = Util.nextInt(1, 14);
                        selectedItem.itemOptions.add(new Item.ItemOption(93, randValue));
                    }
                    break;
                case 1588: // Item 1589
                    selectedItem.itemOptions.add(new Item.ItemOption(50, 21));
                    selectedItem.itemOptions.add(new Item.ItemOption(77, 21));
                    selectedItem.itemOptions.add(new Item.ItemOption(103, 21));
                    selectedItem.itemOptions.add(new Item.ItemOption(210, 1));
                    if (Util.nextInt(0, 100) < 99) {
                        int randValue = Util.nextInt(1, 14);
                        selectedItem.itemOptions.add(new Item.ItemOption(93, randValue));
                    }
                    break;
                case 1589: // Item 1589
                    selectedItem.itemOptions.add(new Item.ItemOption(50, 22));
                    selectedItem.itemOptions.add(new Item.ItemOption(77, 22));
                    selectedItem.itemOptions.add(new Item.ItemOption(103, 22));
                    selectedItem.itemOptions.add(new Item.ItemOption(210, 1));
                    if (Util.nextInt(0, 100) < 99) {
                        int randValue = Util.nextInt(1, 14);
                        selectedItem.itemOptions.add(new Item.ItemOption(93, randValue));
                    }
                    break;
                case 1590: // Item 1590
                    selectedItem.itemOptions.add(new Item.ItemOption(50, 27));
                    selectedItem.itemOptions.add(new Item.ItemOption(77, 27));
                    selectedItem.itemOptions.add(new Item.ItemOption(103, 27));
                    selectedItem.itemOptions.add(new Item.ItemOption(210, 4));
                    if (Util.nextInt(0, 100) < 99) {
                        int randValue = Util.nextInt(1, 14);
                        selectedItem.itemOptions.add(new Item.ItemOption(93, randValue));
                    }
                    break;
                case 1593: // Item 1593
                    selectedItem.itemOptions.add(new Item.ItemOption(50, 25));
                    selectedItem.itemOptions.add(new Item.ItemOption(77, 25));
                    selectedItem.itemOptions.add(new Item.ItemOption(103, 25));
                    selectedItem.itemOptions.add(new Item.ItemOption(210, Util.nextInt(3, 4)));
                    if (Util.nextInt(0, 100) < 99) {
                        int randValue = Util.nextInt(1, 14);
                        selectedItem.itemOptions.add(new Item.ItemOption(93, randValue));
                    }
                    break;
                case 1595: // Item 1595
                    selectedItem.itemOptions.add(new Item.ItemOption(50, 23));
                    selectedItem.itemOptions.add(new Item.ItemOption(77, 23));
                    selectedItem.itemOptions.add(new Item.ItemOption(103, 23));
                    selectedItem.itemOptions.add(new Item.ItemOption(210, Util.nextInt(2, 3)));
                    if (Util.nextInt(0, 100) < 99) {
                        int randValue = Util.nextInt(1, 14);
                        selectedItem.itemOptions.add(new Item.ItemOption(93, randValue));
                    }
                    break;
            }
            if (InventoryService.gI().getCountEmptyBag(pl) > 0) {

                InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                InventoryService.gI().addItemBag(pl, selectedItem);

                Service.gI().sendThongBao(pl, "Bạn đã nhận được " + selectedItem.template.name);
            } else {
                Service.gI().sendThongBao(pl, "Hành trang của bạn đã đầy, không thể nhận vật phẩm!");
            }
        } catch (Exception e) {
            Logger.error("Lỗi khi tạo vật phẩm Gokudayvip: " + e.getMessage());
        }
    }

    private void Cadicvip(Player pl, Item item) {
        try {

            List<Short> itemList = Arrays.asList((short) 1741, (short) 1742, (short) 1743, (short) 1744, (short) 1745,
                    (short) 1746);

            short selectedItemId = itemList.get(Util.nextInt(0, itemList.size()));

            Item selectedItem = ItemService.gI().createNewItem(selectedItemId);

            switch (selectedItemId) {
                case 1741: // Item 1741
                    selectedItem.itemOptions.add(new Item.ItemOption(50, 21)); // Sức đánh
                    selectedItem.itemOptions.add(new Item.ItemOption(77, 21)); // HP
                    selectedItem.itemOptions.add(new Item.ItemOption(103, 21)); // KI
                    selectedItem.itemOptions.add(new Item.ItemOption(210, 1)); // Tùy chọn khác
                    if (Util.nextInt(0, 100) < 99) {
                        int randValue = Util.nextInt(1, 14);
                        selectedItem.itemOptions.add(new Item.ItemOption(93, randValue));
                    }
                    break;

                case 1742: // Item 1742
                    selectedItem.itemOptions.add(new Item.ItemOption(50, 22)); // Sức đánh
                    selectedItem.itemOptions.add(new Item.ItemOption(77, 22)); // HP
                    selectedItem.itemOptions.add(new Item.ItemOption(103, 22)); // KI
                    selectedItem.itemOptions.add(new Item.ItemOption(210, 2)); // Tùy chọn khác
                    if (Util.nextInt(0, 100) < 99) {
                        int randValue = Util.nextInt(1, 14);
                        selectedItem.itemOptions.add(new Item.ItemOption(93, randValue));
                    }
                    break;

                case 1743: // Item 1743
                    selectedItem.itemOptions.add(new Item.ItemOption(50, 23)); // Sức đánh
                    selectedItem.itemOptions.add(new Item.ItemOption(77, 23)); // HP
                    selectedItem.itemOptions.add(new Item.ItemOption(103, 23)); // KI
                    selectedItem.itemOptions.add(new Item.ItemOption(210, 3)); // Tùy chọn khác
                    // Tạo ID 1743 giống 1744
                    Item.ItemOption optionFor1744 = new Item.ItemOption(93, 30); // Chỉ số 1743 = 1744
                    selectedItem.itemOptions.add(optionFor1744);
                    break;

                case 1744: // Item 1744
                    selectedItem.itemOptions.add(new Item.ItemOption(50, 23)); // Sức đánh
                    selectedItem.itemOptions.add(new Item.ItemOption(77, 23)); // HP
                    selectedItem.itemOptions.add(new Item.ItemOption(103, 23)); // KI
                    selectedItem.itemOptions.add(new Item.ItemOption(210, 3)); // Tùy chọn khác
                    // Tạo ID 1743 giống 1744
                    Item.ItemOption optionFor1743 = new Item.ItemOption(93, 30); // Chỉ số 1743 = 1744
                    selectedItem.itemOptions.add(optionFor1743);
                    break;

                case 1745: // Item 1745
                    selectedItem.itemOptions.add(new Item.ItemOption(50, 25)); // Sức đánh
                    selectedItem.itemOptions.add(new Item.ItemOption(77, 25)); // HP
                    selectedItem.itemOptions.add(new Item.ItemOption(103, 25)); // KI
                    selectedItem.itemOptions.add(new Item.ItemOption(210, 3));
                    if (Util.nextInt(0, 100) < 99) {
                        int randValue = Util.nextInt(1, 14);
                        selectedItem.itemOptions.add(new Item.ItemOption(93, randValue));
                    } // Tùy chọn khác
                    break;

                case 1746: // Item 1746
                    selectedItem.itemOptions.add(new Item.ItemOption(50, 27)); // Sức đánh
                    selectedItem.itemOptions.add(new Item.ItemOption(77, 27)); // HP
                    selectedItem.itemOptions.add(new Item.ItemOption(103, 27)); // KI
                    selectedItem.itemOptions.add(new Item.ItemOption(210, 4)); // Tùy chọn khác
                    if (Util.nextInt(0, 100) < 99) {
                        int randValue = Util.nextInt(1, 14);
                        selectedItem.itemOptions.add(new Item.ItemOption(93, randValue));
                    }
                    break;

            }

            // Kiểm tra hành trang của người chơi có đủ chỗ không
            if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
                // Giảm số lượng item cũ trong hành trang
                InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                // Thêm vật phẩm mới vào hành trang
                InventoryService.gI().addItemBag(pl, selectedItem);
                // Gửi thông báo cho người chơi
                Service.gI().sendThongBao(pl, "Bạn đã nhận được " + selectedItem.template.name);
            } else {
                // Thông báo nếu hành trang đầy
                Service.gI().sendThongBao(pl, "Hành trang của bạn đã đầy, không thể nhận vật phẩm!");
            }
        } catch (Exception e) {
            Logger.error("Lỗi khi tạo vật phẩm Cadicvip: " + e.getMessage());
        }
    }

}
