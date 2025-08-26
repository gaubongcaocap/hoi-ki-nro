package services.func;

/*
 *
 *
 * @author NTD
 */
import Bot.*;
import clan.Clan;
import clan.ClanMember;
import jdbc.DBConnecter;
import consts.ConstNpc;
import consts.ConstTaskBadges;
import item.Item;
import item.Item.ItemOption;
import map.Zone;
import minigame.cost.LuckyNumberCost;
import minigame.LuckyNumber.LuckyNumberService;
import npc.Npc;
import npc.NpcManager;
import player.Player;
import network.Message;
import network.inetwork.ISession;
import server.Client;
import services.Service;
import models.GiftCode.GiftCodeService;
import services.InventoryService;
import services.ItemService;
import services.NpcService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jdbc.NDVResultSet;
import jdbc.daos.NDVSqlFetcher;
import jdbc.daos.PlayerDAO;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import player.Inventory;
import server.Manager;
import services.ClanService;
import task.Badges.BadgesTaskService;
import utils.Util;

public class Input {

    private static final Map<Integer, Object> PLAYER_ID_OBJECT = new HashMap<>();

    public static final int CHANGE_PASSWORD = 500;
    public static final int GIFT_CODE = 501;
    public static final int FIND_PLAYER = 502;
    public static final int CHANGE_NAME = 503;
    public static final int CHOOSE_LEVEL_BDKB = 504;
    public static final int NAP_THE = 505;
    public static final int CHANGE_NAME_BY_ITEM = 506;
    public static final int GIVE_IT = 507;
    public static final int GET_IT = 508;
    public static final int DANGKY = 509;
    public static final int CHOOSE_LEVEL_KGHD = 510;
    public static final int CHOOSE_LEVEL_CDRD = 511;
    public static final int DISSOLUTION_CLAN = 513;

    public static final int SELECT_LUCKYNUMBER = 514;

    public static final int DOI_VND = 515;
    public static final int DOI_THOI_VANG = 516;
    public static final int DOI_NGOC_XANH = 517;
    public static final int DOI_NGOC_HONG = 518;
    public static final int BUFFVND = 519;
    public static final int SEND_ITEM = 520;

    public static final int BOT_PEM = 521;
    public static final int BOT_ITEM = 522;
    public static final int BOT_BOSS = 523;

    public static final byte NUMERIC = 0;
    public static final byte ANY = 1;
    public static final byte PASSWORD = 2;
    public static final byte MBV = 23;
    public static final byte BANSLL = 24;
    public static final byte BANGHOI = 25;

    private static Input intance;

    private Input() {

    }

    public static Input gI() {
        if (intance == null) {
            intance = new Input();
        }
        return intance;
    }

    public void doInput(Player player, Message msg) {
        try {
            String[] text = new String[msg.reader().readByte()];
            for (int i = 0; i < text.length; i++) {
                text[i] = msg.reader().readUTF();
            }
            switch (player.iDMark.getTypeInput()) {
//                case DOI_VND: {
//                    int vnd = Integer.parseInt(text[0]);
//                    int coin = vnd * 9 / 10;
//                    if (player.getSession() != null && player.getSession().cash < vnd) {
//                        Service.gI().sendThongBao(player, "Bạn không đủ " + vnd + " VND");
//                        return;
//                    }
//                    if (vnd < 0) {
//                        Service.gI().sendThongBao(player, "Bạn không được phép nhập số âm ");
//                        return;
//                    }
//
//                    if (vnd >= 20000 && vnd <= 100000000) {
//                        PlayerDAO.subcash(player, vnd);
//                        PlayerDAO.addvnd(player, coin);
//                        Service.gI().sendThongBao(player, "Bạn đã nhận được " + coin + " VND");
//                    } else {
//                        Service.gI().sendThongBao(player, "Chọn 1 con số từ 20000 đến 100000000");
//                    }
//                }
//                break;
                case SEND_ITEM: {
                    String itemIds = text[1];
                    String option = text[2];
                    int slItemBuff = Integer.parseInt(text[3]);
                    if (slItemBuff > 999) {
                        Service.gI().sendThongBaoOK(player, "Buff vượt số lượng giới hạn vui lòng để tối đa sl 999");
                        return;
                    }
                    String plName = text[0].trim();
                    if (plName.equals("all")) {
                        Thread.startVirtualThread(() -> {
                            List<Player> allPlayer = NDVSqlFetcher.getAllPlayer();
                            for (Player pBuffItem : allPlayer) {
                                if (pBuffItem != null) {
                                    String[] itemIdsArray = itemIds.split(",");
                                    for (String itemId : itemIdsArray) {
                                        int idItemBuff = Integer.parseInt(itemId);
                                        Item itembuff = ItemService.gI().createNewItem((short) idItemBuff, slItemBuff);

                                        if (option != null) {
                                            String[] Option = option.split(",");
                                            if (Option.length > 0) {
                                                for (int i = 0; i < Option.length; i++) {
                                                    String[] optItem = Option[i].split("-");
                                                    int optID = Integer.parseInt(optItem[0]);
                                                    int param = Integer.parseInt(optItem[1]);
                                                    itembuff.itemOptions.add(new ItemOption(optID, param));
                                                }
                                            }
                                        }
                                        pBuffItem.inventory.itemsMailBox.add(itembuff);

                                        if (NDVSqlFetcher.updateMailBox(pBuffItem)) {
                                            Service.gI().sendThongBao(player, "Bạn vừa gửi " + itembuff.template.name + " thành công cho " + pBuffItem.name);
                                        }
                                    }
                                } else {
                                    Service.gI().sendThongBao(player, "Player không tồn tại");
                                }
                            }
                        }).start();
                    } else {
                        Player pBuffItem = NDVSqlFetcher.loadPlayerByName(text[0].trim());
                        if (pBuffItem != null) {
                            String[] itemIdsArray = itemIds.split(",");
                            for (String itemId : itemIdsArray) {
                                int idItemBuff = Integer.parseInt(itemId);
                                Item itembuff = ItemService.gI().createNewItem((short) idItemBuff, slItemBuff);
                                if (option != null) {
                                    String[] Option = option.split(",");
                                    if (Option.length > 0) {
                                        for (int i = 0; i < Option.length; i++) {
                                            String[] optItem = Option[i].split("-");
                                            int optID = Integer.parseInt(optItem[0]);
                                            int param = Integer.parseInt(optItem[1]);
                                            itembuff.itemOptions.add(new ItemOption(optID, param));
                                        }
                                    }
                                }
                                pBuffItem.inventory.itemsMailBox.add(itembuff);
                                if (NDVSqlFetcher.updateMailBox(pBuffItem)) {
                                    Service.gI().sendThongBao(player, "Bạn vừa gửi " + itembuff.template.name + " thành công cho " + pBuffItem.name);
                                }
                            }
                        } else {
                            Service.gI().sendThongBao(player, "Player không tồn tại");
                        }
                    }
                    break;
                }
                case BUFFVND: {
                    try {
                        int idacc = Integer.parseInt(text[0].trim());
                        int addcash = Integer.parseInt(text[1].trim());
                        if (PlayerDAO.addcash(idacc, addcash)) {
                            Service.gI().sendThongBao(player, "Bạn đã buff cho " + idacc + " " + addcash + " Coin");
                            if (Client.gI().getPlayerByUser(idacc) != null) {
                                Client.gI().getPlayerByUser(idacc).getSession().cash += addcash;
                                Service.gI().sendThongBao(Client.gI().getPlayerByUser(idacc),
                                        "Bạn vừa được cộng " + addcash + "COIN bởi " + player.name);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Service.gI().sendThongBao(player, "Đã có lỗi xảy ra");
                    }
                    break;
                }
                case DOI_THOI_VANG: {
                    int coin = Integer.parseInt(text[0]);
                    int sl = coin / 200;
                    if (player.getSession() != null && player.getSession().cash < coin) {
                        Service.gI().sendThongBao(player, "Bạn không đủ " + coin + " Coin");
                        return;
                    }
                    if (coin < 0) {
                        Service.gI().sendThongBao(player, "Bạn không được phép nhập số âm ");
                        return;
                    }
                    if (coin >= 20000 && coin <= 100000000) {
                        PlayerDAO.subcash(player, coin);
                        Item thoiVang = ItemService.gI().createNewItem((short) 457, sl);
                        InventoryService.gI().addItemBag(player, thoiVang);
                        InventoryService.gI().sendItemBag(player);
                        BadgesTaskService.updateCountBagesTask(player, ConstTaskBadges.DAI_GIA_MOI_NHU, coin);
                        Service.gI().sendThongBao(player, "bạn nhận được " + sl
                                + " " + thoiVang.template.name);
                    } else {
                        Service.gI().sendThongBao(player, "Chọn 1 con số từ 20000 đến 100000000");
                    }
                }
                break;
                case DOI_NGOC_XANH: {
                    int coin = Integer.parseInt(text[0]);
                    int sl = coin;
                    if (player.getSession() != null && player.getSession().cash < coin) {
                        Service.gI().sendThongBao(player, "Bạn không đủ " + coin + " Coin");
                        return;
                    }
                    if (coin < 0) {
                        Service.gI().sendThongBao(player, "Bạn không được phép nhập số âm ");
                        return;
                    }
                    if (coin >= 20000 && coin <= 100000000) {
                        PlayerDAO.subcash(player, coin);
                        Item thoiVang = ItemService.gI().createNewItem((short) 77, sl);
                        InventoryService.gI().addItemBag(player, thoiVang);
                        InventoryService.gI().sendItemBag(player);
                        Service.gI().sendThongBao(player, "bạn nhận được " + sl
                                + " " + thoiVang.template.name);
                    } else {
                        Service.gI().sendThongBao(player, "Chọn 1 con số từ 20000 đến 100000000");
                    }
                }
                break;
                case DOI_NGOC_HONG: {
                    int coin = Integer.parseInt(text[0]);
                    int sl = coin;
                    if (player.getSession() != null && player.getSession().cash < coin) {
                        Service.gI().sendThongBao(player, "Bạn không đủ " + coin + " Coin");
                        return;
                    }
                    if (coin < 0) {
                        Service.gI().sendThongBao(player, "Bạn không được phép nhập số âm ");
                        return;
                    }
                    if (coin >= 20000 && coin <= 100000000) {
                        PlayerDAO.subcash(player, coin);
                        Item thoiVang = ItemService.gI().createNewItem((short) 861, sl);
                        InventoryService.gI().addItemBag(player, thoiVang);
                        InventoryService.gI().sendItemBag(player);
                        Service.gI().sendThongBao(player, "bạn nhận được " + sl
                                + " " + thoiVang.template.name);
                    } else {
                        Service.gI().sendThongBao(player, "Chọn 1 con số từ 20000 đến 100000000");
                    }
                }
                break;

                case GIVE_IT:
                    String name = text[0];
                    int id = Integer.parseInt(text[1]);
                    int op = Integer.parseInt(text[2]);
                    int pr = Integer.parseInt(text[3]);
                    int q = Integer.parseInt(text[4]);

                    if (Client.gI().getPlayer(name) != null) {
                        Item item = ItemService.gI().createNewItem(((short) id));
                        List<Item.ItemOption> ops = ItemService.gI().getListOptionItemShop((short) id);
                        if (!ops.isEmpty()) {
                            item.itemOptions = ops;
                        }
                        item.quantity = q;
                        item.itemOptions.add(new Item.ItemOption(op, pr));
                        InventoryService.gI().addItemBag(Client.gI().getPlayer(name), item);
                        InventoryService.gI().sendItemBag(Client.gI().getPlayer(name));
                        String thongBao = "Nhận " + item.template.name + " từ " + player.name;
                        Service.gI().sendThongBao(Client.gI().getPlayer(name), thongBao);

                        // Ghi thông báo vào file
                        Util.FileManager.writeToFile("thongbao.txt", thongBao);

                    } else {
                        Service.gI().sendThongBao(player, "Không online");
                    }
                    break;
                case GET_IT:
                    id = Integer.parseInt(text[0]);
                    op = Integer.parseInt(text[1]);
                    pr = Integer.parseInt(text[2]);
                    q = Integer.parseInt(text[3]);

                    if (player.isAdmin()) {
                        Item item = ItemService.gI().createNewItem(((short) id));
                        List<Item.ItemOption> ops = ItemService.gI().getListOptionItemShop((short) id);
                        if (!ops.isEmpty() && item.template.id != 933) {
                            item.itemOptions = ops;
                        }
                        item.quantity = q;
                        item.itemOptions.add(new Item.ItemOption(op, pr));
                        InventoryService.gI().addItemBag(player, item);
                        InventoryService.gI().sendItemBag(player);
                        String thongBao = "Nhận " + item.template.name + "! Từ " + player.name;
                        Service.gI().sendThongBao(player, thongBao);

                        // Ghi thông báo vào file
                        Util.FileManager.writeToFile("thongbao.txt", thongBao);

                    } else {
                        Service.gI().sendThongBao(player, "Không đủ quyền hạn!");
                    }
                    break;
                case CHANGE_PASSWORD:
                    Service.gI().changePassword(player, text[0], text[1], text[2]);
                    break;
                case GIFT_CODE:
                    GiftCodeService.gI().giftCode(player, text[0]);
//                    String textLevel = text[0];
//                    Input.gI().addItemGiftCodeToPlayer(player, textLevel);
                    break;
                case FIND_PLAYER:
                    Player pl = Client.gI().getPlayer(text[0]);
                    if (pl != null) {
                        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_FIND_PLAYER, -1, "Ngài muốn..?",
                                new String[]{"Đi tới\n" + pl.name, "Gọi " + pl.name + "\ntới đây", "Đổi tên", "Ban", "Kick"},
                                pl);
                    } else {
                        Service.gI().sendThongBao(player, "Người chơi không tồn tại hoặc đang offline");
                    }
                    break;
                case CHANGE_NAME: {
                    Player plChanged = (Player) PLAYER_ID_OBJECT.get((int) player.id);
                    if (plChanged != null) {
                        if (DBConnecter.executeQuery("select * from player where name = ?", text[0]).next()) {
                            Service.gI().sendThongBao(player, "Tên nhân vật đã tồn tại");
                        } else {
                            plChanged.name = text[0];
                            DBConnecter.executeUpdate("update player set name = ? where id = ?", plChanged.name, plChanged.id);
                            Service.gI().player(plChanged);
                            Service.gI().Send_Caitrang(plChanged);
                            Service.gI().sendFlagBag(plChanged);
                            Zone zone = plChanged.zone;
                            ChangeMapService.gI().changeMap(plChanged, zone, plChanged.location.x, plChanged.location.y);
                            Service.gI().sendThongBao(plChanged, "Chúc mừng bạn đã có cái tên mới đẹp đẽ hơn tên ban đầu");
                            Service.gI().sendThongBao(player, "Đổi tên người chơi thành công");
                        }
                    }
                }
                break;
                case CHANGE_NAME_BY_ITEM: {
                    if (player != null) {
                        if (DBConnecter.executeQuery("select * from player where name = ?", text[0]).next()) {
                            Service.gI().sendThongBao(player, "Tên nhân vật đã tồn tại");
                            createFormChangeNameByItem(player);
                        } else if (Util.haveSpecialCharacter(text[0])) {
                            Service.gI().sendThongBaoOK(player, "Tên nhân vật không được chứa ký tự đặc biệt");
                        } else if (text[0].length() < 5) {
                            Service.gI().sendThongBaoOK(player, "Tên nhân vật quá ngắn");
                        } else if (text[0].length() > 10) {
                            Service.gI().sendThongBaoOK(player, "Tên nhân vật chỉ đồng ý các ký tự a-z, 0-9 và chiều dài từ 5 đến 10 ký tự");
                        } else {
                            Item theDoiTen = InventoryService.gI().findItem(player.inventory.itemsBag, 2006);
                            if (theDoiTen == null) {
                                Service.gI().sendThongBao(player, "Không tìm thấy thẻ đổi tên");
                            } else {
                                InventoryService.gI().subQuantityItemsBag(player, theDoiTen, 1);
                                player.name = text[0].toLowerCase();
                                DBConnecter.executeUpdate("update player set name = ? where id = ?", player.name, player.id);
                                Service.gI().player(player);
                                Service.gI().Send_Caitrang(player);
                                Service.gI().sendFlagBag(player);
                                Zone zone = player.zone;
                                ChangeMapService.gI().changeMap(player, zone, player.location.x, player.location.y);
                                Service.gI().sendThongBao(player, "Chúc mừng bạn đã có cái tên mới đẹp đẽ hơn tên ban đầu");
                            }
                        }
                    }
                }
                break;
                case CHOOSE_LEVEL_BDKB:
                    int level = Integer.parseInt(text[0]);
                    if (level >= 1 && level <= 110) {
                        Npc npc = NpcManager.getByIdAndMap(ConstNpc.QUY_LAO_KAME, player.zone.map.mapId);
                        if (npc != null) {
                            npc.createOtherMenu(player, ConstNpc.MENU_ACCEPT_GO_TO_BDKB,
                                    "Con có chắc muốn đến\nhang kho báu cấp độ " + level + " ?",
                                    new String[]{"Đồng ý", "Từ chối"}, level);
                        }
                    } else {
                        Service.gI().sendThongBao(player, "Không thể thực hiện");
                    }

                    break;
                case CHOOSE_LEVEL_KGHD:
                    level = Integer.parseInt(text[0]);
                    if (level >= 1 && level <= 110) {
                        Npc npc = NpcManager.getByIdAndMap(ConstNpc.MR_POPO, player.zone.map.mapId);
                        if (npc != null) {
                            npc.createOtherMenu(player, 2,
                                    "Cậu có chắc muốn đến\nDestron Gas cấp độ " + level + " ?",
                                    new String[]{"Đồng ý", "Từ chối"}, level);
                        }
                    }
                    break;
                case CHOOSE_LEVEL_CDRD:
                    level = Integer.parseInt(text[0]);
                    if (level >= 1 && level <= 110) {
                        Npc npc = NpcManager.getByIdAndMap(ConstNpc.THAN_VU_TRU, player.zone.map.mapId);
                        if (npc != null) {
                            npc.createOtherMenu(player, 3,
                                    "Con có chắc muốn đến\ncon đường rắn độc cấp độ " + level + " ?",
                                    new String[]{"Đồng ý", "Từ chối"}, level);
                        }
                    }
                    break;
                case MBV:
                    int mbv = Integer.parseInt(text[0]);
                    int nmbv = Integer.parseInt(text[1]);
                    int rembv = Integer.parseInt(text[2]);
                    if ((mbv + "").length() != 6 || (nmbv + "").length() != 6 || (rembv + "").length() != 6) {
                        Service.gI().sendThongBao(player, "Trêu bố mày à?");
                    } else if (player.mbv == 0) {
                        Service.gI().sendThongBao(player, "Bạn chưa cài mã bảo vệ!");
                    } else if (player.mbv != mbv) {
                        Service.gI().sendThongBao(player, "Mã bảo vệ không đúng");
                    } else if (nmbv != rembv) {
                        Service.gI().sendThongBao(player, "Mã bảo vệ không trùng khớp");
                    } else {
                        player.mbv = nmbv;
                        Service.gI().sendThongBao(player, "Đổi mã bảo vệ thành công!");
                    }
                    break;
                case BANSLL:
                    int sltv = Integer.parseInt(text[0]);
                    long cost = (long) sltv * 36900000;
                    if (sltv < 0) {
                        Service.gI().sendThongBao(player, "Có cái dái");
                        return;
                    }
                    Item ThoiVang = InventoryService.gI().findItemBag(player, 457);
                    if (ThoiVang != null) {
                        if (ThoiVang.quantity < sltv) {
                            Service.gI().sendThongBao(player, "Bạn chỉ có " + ThoiVang.quantity + " Thỏi vàng");
                        } else {
                            if (player.inventory.gold + cost > Inventory.LIMIT_GOLD) {
                                int slban = (int) ((Inventory.LIMIT_GOLD - player.inventory.gold) / 36900000);
                                if (slban < 1) {
                                    Service.gI().sendThongBao(player, "Vàng sau khi bán vượt quá giới hạn");
                                } else if (slban < 2) {
                                    Service.gI().sendThongBao(player, "Bạn chỉ có thể bán 1 Thỏi vàng");
                                } else {
                                    Service.gI().sendThongBao(player, "Số lượng trong khoảng 1 tới " + slban);
                                }
                            } else {
                                InventoryService.gI().subQuantityItemsBag(player, ThoiVang, sltv);
                                InventoryService.gI().sendItemBag(player);
                                player.inventory.gold += cost;
                                Service.gI().sendMoney(player);
                                Service.gI().sendThongBao(player, "Đã bán " + sltv + " Thỏi vàng thu được " + Util.numberToMoney(cost) + " vàng");
                                TransactionService.gI().cancelTrade(player);
                            }
                        }
                    }
                    break;
                case BANGHOI:
                    Clan clan = player.clan;
                    if (clan != null) {
                        ClanMember cm = clan.getClanMember((int) player.id);
                        if (clan.isLeader(player)) {
                            if (clan.canUpdateClan(player)) {
                                String tenvt = text[0];
                                if (!Util.haveSpecialCharacter(tenvt) && tenvt.length() > 1 && tenvt.length() < 5) {
                                    clan.name2 = tenvt;
                                    clan.update();
                                    Service.gI().sendThongBao(player, "[" + tenvt + "] OK");
                                } else {
                                    Service.gI().sendThongBaoOK(player, "Chỉ chấp nhận các ký tự a-z, 0-9 và chiều dài từ 2 đến 4 ký tự");
                                }
                            }
                        }
                    }
                    break;
                case DISSOLUTION_CLAN:
                    String xacNhan = text[0];
                    if (xacNhan.equalsIgnoreCase("OK")) {
                        clan = player.clan;
                        if (clan.isLeader(player)) {
                            clan.deleteDB(clan.id);
                            Manager.CLANS.remove(clan);
                            player.clan = null;
                            player.clanMember = null;
                            ClanService.gI().sendMyClan(player);
                            ClanService.gI().sendClanId(player);
                            Service.gI().sendThongBao(player, "Bang hội đã giải tán thành công.");
                        }
                    }
                    break;
                case SELECT_LUCKYNUMBER: {
                    int number = Integer.parseInt(text[0]);
                    LuckyNumberService.addNumber(player, number);
                }
                break;
                case BOT_ITEM:
                    int slot = Integer.parseInt(text[0]);
                    int idBan = Integer.parseInt(text[1]);
                    int idTraoDoi = Integer.parseInt(text[2]);
                    int slot_TraoDoi = Integer.parseInt(text[3]);
                    ShopBot bs = new ShopBot(idBan, idTraoDoi, slot_TraoDoi);
                    Thread.startVirtualThread(() -> {
                        NewBot.gI().runBot(1, bs, slot);
                    });
                    break;
                case BOT_BOSS:
                    slot = Integer.parseInt(text[0]);
                    Thread.startVirtualThread(() -> {
                        NewBot.gI().runBot(2, null, slot);
                    });
                    break;
                case BOT_PEM:
                    slot = Integer.parseInt(text[0]);
                    Thread.startVirtualThread(() -> {
                        NewBot.gI().runBot(0, null, slot);
                    });
                    break;
            }
        } catch (Exception e) {
        }
    }

    public void createForm(Player pl, int typeInput, String title, SubInput... subInputs) {
        pl.iDMark.setTypeInput(typeInput);
        Message msg = null;
        try {
            msg = new Message(-125);
            msg.writer().writeUTF(title);
            msg.writer().writeByte(subInputs.length);
            for (SubInput si : subInputs) {
                msg.writer().writeUTF(si.name);
                msg.writer().writeByte(si.typeInput);
            }
            pl.sendMessage(msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public void createForm(ISession session, int typeInput, String title, SubInput... subInputs) {
        Message msg = null;
        try {
            msg = new Message(-125);
            msg.writer().writeUTF(title);
            msg.writer().writeByte(subInputs.length);
            for (SubInput si : subInputs) {
                msg.writer().writeUTF(si.name);
                msg.writer().writeByte(si.typeInput);
            }
            session.sendMessage(msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }
    public void createFormBotQuai(Player pl) {
        createForm(pl, BOT_PEM, "Buff Bot Quái",
                new SubInput("số lượng bot", NUMERIC));
    }
    
    
    public void createFormBotBoss(Player pl) {
        createForm(pl, BOT_BOSS, "Buff Bot Boss",
                new SubInput("số lượng bot", NUMERIC));
    }
    
    public void createFormBotItem(Player pl) {
        createForm(pl, BOT_ITEM, "Buff Bot Item",
                new SubInput("số lượng bot", NUMERIC),
                new SubInput("id item cần bán", NUMERIC),
                new SubInput("id item trao đổi", NUMERIC),
                new SubInput("số lượng yêu cầu trao đổi", NUMERIC));
    }

    public void createFormChangePassword(Player pl) {
        createForm(pl, CHANGE_PASSWORD, "Đổi mật khẩu", new SubInput("Mật khẩu cũ", PASSWORD),
                new SubInput("Mật khẩu mới", PASSWORD),
                new SubInput("Nhập lại mật khẩu mới", PASSWORD));
    }

    public void createFormGiveItem(Player pl) {
        createForm(pl, GIVE_IT, "Tặng vật phẩm", new SubInput("Tên", ANY), new SubInput("Id Item", ANY), new SubInput("ID OPTION", ANY), new SubInput("PARAM", ANY), new SubInput("Số lượng", ANY));
    }

    public void createFormGetItem(Player pl) {
        createForm(pl, GET_IT, "Get vật phẩm", new SubInput("Id Item", ANY), new SubInput("ID OPTION", ANY), new SubInput("PARAM", ANY), new SubInput("Số lượng", ANY));
    }

    public void createFormGiftCode(Player pl) {
        createForm(pl, GIFT_CODE, "GiftCode", new SubInput("Giftcode", ANY));
    }

    public void createFormMBV(Player pl) {
        createForm(pl, MBV, "Đồ ngu! Đồ ăn hại! Cút mẹ mày đi!", new SubInput("Nhập Mã Bảo Vệ Đã Quên", NUMERIC), new SubInput("Nhập Mã Bảo Vệ Mới", NUMERIC), new SubInput("Nhập Lại Mã Bảo Vệ Mới", NUMERIC));
    }

    public void createFormBangHoi(Player pl) {
        createForm(pl, BANGHOI, "Nhập tên viết tắt bang hội", new SubInput("Tên viết tắt từ 2 đến 4 kí tự", ANY));
    }

    public void createFormFindPlayer(Player pl) {
        createForm(pl, FIND_PLAYER, "Tìm kiếm người chơi", new SubInput("Tên người chơi", ANY));
    }

    public void createFormNapThe(Player pl, byte loaiThe) {
        pl.iDMark.setLoaiThe(loaiThe);
        createForm(pl, NAP_THE, "Nạp thẻ", new SubInput("Mã thẻ", ANY), new SubInput("Seri", ANY));
    }

    public void createFormChangeName(Player pl, Player plChanged) {
        PLAYER_ID_OBJECT.put((int) pl.id, plChanged);
        createForm(pl, CHANGE_NAME, "Đổi tên " + plChanged.name, new SubInput("Tên mới", ANY));
    }

    public void createFormChangeNameByItem(Player pl) {
        createForm(pl, CHANGE_NAME_BY_ITEM, "Đổi tên " + pl.name, new SubInput("Tên mới", ANY));
    }

    public void createFormChooseLevelBDKB(Player pl) {
        createForm(pl, CHOOSE_LEVEL_BDKB, "Hãy chọn cấp độ hang kho báu từ 1-110", new SubInput("Cấp độ", NUMERIC));
    }

    public void createFormChooseLevelCDRD(Player pl) {
        createForm(pl, CHOOSE_LEVEL_CDRD, "Hãy chọn cấp độ từ 1-110", new SubInput("Cấp độ", NUMERIC));
    }

    public void createFormChooseLevelKGHD(Player pl) {
        createForm(pl, CHOOSE_LEVEL_KGHD, "Hãy chọn cấp độ từ 1-110", new SubInput("Cấp độ", NUMERIC));
    }

    public void createFormBanSLL(Player pl) {
        createForm(pl, BANSLL, "Bạn muốn bán bao nhiêu [Thỏi vàng] ?", new SubInput("Số lượng", NUMERIC));
    }

    public void createFormGiaiTanBangHoi(Player pl) {
        createForm(pl, DISSOLUTION_CLAN, "Nhập OK để xác nhận giải tán bang hội.", new SubInput("", ANY));
    }

    public void createFormDoiVND(Player pl) {

        createForm(pl, DOI_VND, "Đổi Coin --> Coin < Coin x 0.9 >",
                new SubInput("Nhập số lượng Coin muốn đổi ra Coin", NUMERIC));
    }

    public void createFormDoiThoiVang(Player pl) {

        createForm(pl, DOI_THOI_VANG, "Đổi Coin --> Thỏi vàng < Mỗi 20K được 100 thỏi >",
                new SubInput("Nhập số lượng Coin muốn đổi ra thỏi vàng", NUMERIC));
    }

    public void createFormDoiNgocXanh(Player pl) {

        createForm(pl, DOI_NGOC_XANH, "Đổi Coin --> Ngọc xanh < Mỗi 20K được 20.000 ngọc xanh >",
                new SubInput("Nhập số lượng Coin muốn đổi ra ngọc xanh", NUMERIC));
    }

    public void createFormDoiNgocHong(Player pl) {

        createForm(pl, DOI_NGOC_HONG, "Đổi Coin --> Ngọc hồng < Mỗi 20K được 20.000 ngọc hồng >",
                new SubInput("Nhập số lượng Coin muốn đổi ra ngọc hồng", NUMERIC));
    }

    public void createFormSelectOneNumberLuckyNumber(Player pl, boolean isGem) {
        String text = "";
        if (isGem) {
            text = "Hãy chọn 1 số từ 0 đến 99 giá " + Util.numberFormatLouis(LuckyNumberCost.costPlayGem) + " ngọc";
        } else {
            text = "Hãy chọn 1 số từ 0 đến 99 giá " + Util.numberFormatLouis(LuckyNumberCost.costPlayGold) + " vàng";
        }
        createForm(pl, SELECT_LUCKYNUMBER, text, new SubInput("Số bạn chọn", NUMERIC));
    }

    public void createFromMailBox(Player pl) {
        createForm(pl, SEND_ITEM, "Hộp thư gửi đến người chơi",
                new SubInput("Tên người chơi", ANY),
                new SubInput("ID Trang Bị", ANY),
                new SubInput("Chuỗi option", ANY),
                new SubInput("Số lượng", NUMERIC));
    }

    public void createFormBuffVND(Player player) {
        createForm(player, BUFFVND, "Buff Coin",
                new SubInput("id acc người chơi", NUMERIC),
                new SubInput("Coin CẦN BUFF", ANY));
    }

    public static class SubInput {

        private String name;
        private byte typeInput;

        public SubInput(String name, byte typeInput) {
            this.name = name;
            this.typeInput = typeInput;
        }
    }

//    public void addItemGiftCodeToPlayer(Player p, final String giftcode) {
//        try {
//            final NDVResultSet red = DBConnecter.executeQuery("SELECT * FROM `giftcode` WHERE `code` LIKE '" + Util.strSQL(giftcode) + "' LIMIT 1;");
//            if (red.first()) {
//                String text = "Mã quà tặng" + ": " + giftcode + "\b- " + "Phần quà của bạn là:" + "\b";
//                final byte type = red.getByte("type");
//                int limit = red.getInt("limit");
//                final boolean isDelete = red.getBoolean("Delete");
//                final boolean isCheckbag = red.getBoolean("bagCount");
//                final JSONArray listUser = (JSONArray) JSONValue.parseWithException(red.getString("listUser"));
//                final JSONArray listItem = (JSONArray) JSONValue.parseWithException(red.getString("listItem"));
//                final JSONArray option = (JSONArray) JSONValue.parseWithException(red.getString("itemoption"));
//                if (limit == 0) {
//                    NpcService.gI().createTutorial(p, 24, "Số lượng mã quà tặng này đã hết.");
//                } else {
//                    if (type == 1) {
//                        for (int i = 0; i < listUser.size(); ++i) {
//                            final int playerId = Integer.parseInt(listUser.get(i).toString());
//                            if (playerId == p.id) {
//                                NpcService.gI().createTutorial(p, 24, "Mỗi tài khoản chỉ được phép sử dụng mã quà tặng này 1 lần duy nhất.");
//                                return;
//                            }
//                        }
//                    } else if (type == 2) {
//                        if (!p.getSession().actived) { // Giả sử bạn có một hàm kiểm tra trạng thái mở thành viên
//                            NpcService.gI().createTutorial(p, 24, "Bạn cần mở thành viên để có thể sử dụng code này.");
//                            return;
//                        }
//                    }
//                    if (isCheckbag && listItem.size() > InventoryService.gI().getCountEmptyBag(p)) {
//                        NpcService.gI().createTutorial(p, 24, "Hành trang cần phải có ít nhất " + listItem.size() + " ô trống để nhận vật phẩm");
//                    } else {
//                        for (int i = 0; i < listItem.size(); ++i) {
//                            final JSONObject item = (JSONObject) listItem.get(i);
//                            final int idItem = Integer.parseInt(item.get("id").toString());
//                            final int quantity = Integer.parseInt(item.get("quantity").toString());
//
//                            if (idItem == -1) {
//                                p.inventory.gold = Math.min(p.inventory.gold + (long) quantity, Inventory.LIMIT_GOLD);
//                                text += quantity + " vàng\b";
//                            } else if (idItem == -2) {
//                                p.inventory.gem = Math.min(p.inventory.gem + quantity, 2000000000);
//                                text += quantity + " ngọc\b";
//                            } else if (idItem == -3) {
//                                p.inventory.ruby = Math.min(p.inventory.ruby + quantity, 2000000000);
//                                text += quantity + " ngọc khóa\b";
//                            } else {
//                                Item itemGiftTemplate = ItemService.gI().createNewItem((short) idItem);
//                                itemGiftTemplate.quantity = quantity;
//                                if (option != null) {
//                                    for (int u = 0; u < option.size(); u++) {
//                                        JSONObject jsonobject = (JSONObject) option.get(u);
//                                        itemGiftTemplate.itemOptions.add(new Item.ItemOption(Integer.parseInt(jsonobject.get("id").toString()), Integer.parseInt(jsonobject.get("param").toString())));
//
//                                    }
//
//                                }
//                                text += "x" + quantity + " " + itemGiftTemplate.template.name + "\b";
//                                InventoryService.gI().addItemBag(p, itemGiftTemplate);
//                                InventoryService.gI().sendItemBag(p);
//                            }
//
//                            if (i < listItem.size() - 1) {
//                                text += "";
//                            }
//                        }
//                        if (limit != -1) {
//                            --limit;
//                        }
//                        listUser.add(p.id);
//                        DBConnecter.executeUpdate("UPDATE `giftcode` SET `limit` = " + limit + ", `listUser` = '" + listUser.toJSONString() + "' WHERE `code` LIKE '" + Util.strSQL(giftcode) + "';");
//                        NpcService.gI().createTutorial(p, 24, text);
//                    }
//                }
//            } else {
//                NpcService.gI().createTutorial(p, 24, "Mã quà tặng không tồn tại hoặc đã được sử dụng");
//            }
//        } catch (Exception e) {
//
//            NpcService.gI().createTutorial(p, 24, "Có lỗi sảy ra  hãy báo ngay cho QTV để khắc phục.");
//            e.printStackTrace();
//        }
//    }
}
