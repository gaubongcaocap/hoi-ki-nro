package shop;

/*
 *
 *
 * @author NTD
 */
import consts.ConstAchievement;
import consts.ConstNpc;
import consts.ConstTask;
import item.Item;
import npc.specialnpc.MagicTree;
import player.Inventory;
import player.Player;
import network.Message;
import jdbc.daos.PlayerDAO;
import item.Item.ItemOption;

import java.util.ArrayList;

import player.badges.BadgesService;
import player.badges.BagesTemplate;
import server.Manager;
import services.InventoryService;
import services.ItemService;
import services.Service;
import utils.Logger;
import utils.Util;

import java.util.List;

import models.Achievement.AchievementService;
import services.NpcService;
import services.TaskService;
import services.func.Input;
import services.func.VatPhamDaBan;
import utils.SkillUtil;
import utils.TimeUtil;

public class ShopService {

    private static final byte COST_GOLD = 0;
    private static final byte COST_GEM = 1;
    private static final byte COST_RUBY = 3;
    private static final byte COST_COUPON = 4;

    private static final byte NORMAL_SHOP = 0;
    private static final byte SPEC_SHOP = 3;
    private static final byte KINANG_SHOP = 1;
    private static ShopService I;

    public static ShopService gI() {
        if (ShopService.I == null) {
            ShopService.I = new ShopService();
        }
        return ShopService.I;
    }

    public void opendShop(Player player, String tagName, boolean allGender) {
        if (tagName.equals("ITEMS_LUCKY_ROUND")) {
            openShopType4(player, tagName, player.inventory.itemsBoxCrackBall);
            return;
        } else if (tagName.equals("ITEMS_MAIL_BOX")) {
            openShopType4(player, tagName, player.inventory.itemsMailBox);
            return;
        } else if (tagName.equals("ITEMS_DABAN")) {
            openShopType8(player, tagName, player.inventory.itemsDaBan);
            return;
        }
        try {
            Shop shop = this.getShop(tagName);
            for (TabShop tabShop : shop.tabShops) {
                for (ItemShop item : tabShop.itemShops) {
                    switch (item.temp.id) {
                        case 1627:// hành trang
                            if (player.inventory.itemsBag.size() >= 35) {
                                item.cost = ((player.inventory.itemsBag.size() - 35) + 1) * 2;
                            } else {
                                item.cost = 1;
                            }
                            break;
                    }
                }
            }
            shop = this.resolveShop(player, shop, allGender);
            switch (shop.typeShop) {
                case KINANG_SHOP:
                    openShopType1(player, shop);
                    break;
                case NORMAL_SHOP:
                    openShopType0(player, shop);
                    break;
                case SPEC_SHOP:
                    openShopType3(player, shop);
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Service.gI().sendThongBao(player, ex.getMessage());
        }
    }

    private void openShopType1(Player player, Shop shop) {
        if (shop != null) {
            player.iDMark.setShopOpen(shop);
            player.iDMark.setTagNameShop(shop.tagName);
            Message msg = null;
            try {
                msg = new Message(-44);
                msg.writer().writeByte(KINANG_SHOP);
                msg.writer().writeByte(shop.tabShops.size());
                for (TabShop tab : shop.tabShops) {
                    msg.writer().writeUTF(tab.name);
                    msg.writer().writeByte(tab.itemShops.size());
                    for (ItemShop itemShop : tab.itemShops) {
                        msg.writer().writeShort(itemShop.temp.id);
                        String[] subName = itemShop.temp.name.split("");
                        byte level = Byte.parseByte(subName[subName.length - 1]);

                        var skillTemplateId = SkillUtil.getTempSkillSkillByItemID(itemShop.temp.id);
                        var template = SkillUtil.findSkillTemplate(skillTemplateId);
                        long costPotential = 0;
                        if (template != null) {
                            costPotential = template.skillss.stream()
                                    .filter(s -> s.point == level)
                                    .findFirst()
                                    .map(s -> (int) s.powRequire)
                                    .orElse(0);
                        }
                        msg.writer().writeLong(costPotential);

                        msg.writer().writeByte(itemShop.options.size());
                        for (Item.ItemOption option : itemShop.options) {
                            msg.writer().writeByte(option.optionTemplate.id);
                            msg.writer().writeShort(option.param);
                        }
                        msg.writer().writeByte(itemShop.isNew ? 1 : 0);

                        msg.writer().writeByte(0);

                    }
                }
                player.sendMessage(msg);
            } catch (Exception e) {
                Logger.logException(ShopService.class, e);
            } finally {
                if (msg != null) {
                    msg.cleanup();
                }
            }
        }
    }

    private void learnKyNang(Player pl, ItemShop is) {

        if (pl.nPoint.power < is.temp.strRequire) {
            Service.gI().sendThongBao(pl, "Sức mạnh của bạn không đủ");
            return;
        }
        if (pl.nPoint.tiemNang < is.cost) {
            Service.gI().sendThongBao(pl, "Bạn không đủ tiềm năng để học chiêu thức này");
            return;
        }
        var skillPlayer = pl.playerSkill.getSkillbyId(SkillUtil.getSkillByItemID(pl, is.temp.id).template.id);
        String[] subName = is.temp.name.split("");
        byte level = Byte.parseByte(subName[subName.length - 1]);
        if (skillPlayer != null) {

            if (skillPlayer.point >= level) {// neu ki nang hien tai cao hon hoac bang skill hoc
                Service.gI().sendThongBao(pl, "Bạn đã học kỹ năng này rồi");
                Logger.log("Ban da hoc ky nang nay roi, skillPlayer: " + skillPlayer.point + " >= " + level);
                return;

            }
            if (level - skillPlayer.point != 1) {
                Service.gI().sendThongBao(pl, "Bạn chưa thể học kỹ năng này");
                return;
            }
        }
        if (pl.BoughtSkill.contains(is.temp.id)) {
            Service.gI().sendThongBao(pl, "Bạn đã học kỹ năng này rồi");
            return;
        }
        ArrayList<String> menu = new ArrayList<>();
        menu.add("Yes");
        menu.add("No");
        String[] menus = menu.toArray(String[]::new);
        long[] time = new long[] { 900000, 1800000, 3600000, 86400000, 259200000, 604800000, 1296000000 };
        var timeStudy = "";
        var timeLong = time[level - 1];
        switch (level) {
            case 0:
            case 1:
            case 2:
                timeStudy = TimeUtil.convertMillisecondToMinute(timeLong);
                break;
            case 3:
                timeStudy = TimeUtil.convertMillisecondToHour(timeLong);
                break;
            default:
                timeStudy = TimeUtil.convertMillisecondToDay(timeLong);
                break;
        }
        var skillTemplateId = SkillUtil.getTempSkillSkillByItemID(is.temp.id);

        var potential = SkillUtil.findSkillTemplate(skillTemplateId).skillss.stream()
                .filter(s -> s.point == level)
                .findFirst()
                .map(s -> (int) s.powRequire) // Ép kiểu Long -> int
                .orElse(0); // Giá trị mặc định là int
        String text = "Con có muốn học kỹ năng "
                + SkillUtil.findSkillTemplate(SkillUtil.getTempSkillSkillByItemID(is.temp.id)).name + " cấp " + level
                + "\nCần " + potential + " điểm tiềm năng và thời gian học là " + timeStudy;
        pl.LearnSkill.ItemTemplateSkillId = is.temp.id;
        pl.LearnSkill.Time = -1;
        pl.LearnSkill.Potential = potential;

        NpcService.gI().createMenuConMeo(pl, 671, NpcService.gI().getAvatar(13 + pl.gender), text, menus);
    }

    private Shop getShop(String tagName) throws Exception {
        for (Shop s : Manager.SHOPS) {
            if (s.tagName != null && s.tagName.equals(tagName)) {
                return s;
            }
        }
        throw new Exception("Shop " + tagName + " không tồn tại!");
    }

    private Shop resolveShop(Player player, Shop shop, boolean allGender) {
        if (shop.tagName != null
                && (shop.tagName.equals("BUA_1H") || shop.tagName.equals("BUA_8H") || shop.tagName.equals("BUA_1M"))) {
            return this.resolveShopBua(player, new Shop(shop));
        }
        return allGender ? new Shop(shop) : new Shop(shop, player);
    }

    private Shop resolveShopBua(Player player, Shop s) {
        for (TabShop tabShop : s.tabShops) {
            for (ItemShop item : tabShop.itemShops) {
                long min = 0;
                switch (item.temp.id) {
                    case 213:
                        long timeTriTue = player.charms.tdTriTue;
                        long current = System.currentTimeMillis();
                        min = (timeTriTue - current) / 60000;

                        break;
                    case 214:
                        min = (player.charms.tdManhMe - System.currentTimeMillis()) / 60000;
                        break;
                    case 215:
                        min = (player.charms.tdDaTrau - System.currentTimeMillis()) / 60000;
                        break;
                    case 216:
                        min = (player.charms.tdOaiHung - System.currentTimeMillis()) / 60000;
                        break;
                    case 217:
                        min = (player.charms.tdBatTu - System.currentTimeMillis()) / 60000;
                        break;
                    case 218:
                        min = (player.charms.tdDeoDai - System.currentTimeMillis()) / 60000;
                        break;
                    case 219:
                        min = (player.charms.tdThuHut - System.currentTimeMillis()) / 60000;
                        break;
                    case 522:
                        min = (player.charms.tdDeTu - System.currentTimeMillis()) / 60000;
                        break;
                    case 671:
                        min = (player.charms.tdTriTue3 - System.currentTimeMillis()) / 60000;
                        break;
                    case 672:
                        min = (player.charms.tdTriTue4 - System.currentTimeMillis()) / 60000;
                        break;
                }
                if (min > 0) {
                    item.options.clear();
                    if (min >= 1440) {
                        item.options.add(new Item.ItemOption(63, (int) min / 1440));
                    } else if (min >= 60) {
                        item.options.add(new Item.ItemOption(64, (int) min / 60));
                    } else {
                        item.options.add(new Item.ItemOption(65, (int) min));
                    }
                }
            }
        }
        return s;
    }

    private void openShopType0(Player player, Shop shop) {
        if (shop != null) {
            player.iDMark.setShopOpen(shop);
            player.iDMark.setTagNameShop(shop.tagName);
            Message msg = null;
            try {
                msg = new Message(-44);
                msg.writer().writeByte(NORMAL_SHOP);
                msg.writer().writeByte(shop.tabShops.size());
                for (TabShop tab : shop.tabShops) {
                    msg.writer().writeUTF(tab.name);
                    msg.writer().writeByte(tab.itemShops.size());
                    for (ItemShop itemShop : tab.itemShops) {
                        msg.writer().writeShort(itemShop.temp.id);
                        if (itemShop.typeSell == COST_GOLD) {
                            msg.writer().writeInt(itemShop.cost);
                            msg.writer().writeInt(0);
                        } else if (itemShop.typeSell == COST_GEM) {
                            msg.writer().writeInt(0);
                            msg.writer().writeInt(itemShop.cost);
                        } else if (itemShop.typeSell == COST_RUBY) {
                            msg.writer().writeInt(0);
                            msg.writer().writeInt(itemShop.cost);
                        } else if (itemShop.typeSell == COST_COUPON) {
                            msg.writer().writeInt(0);
                            msg.writer().writeInt(itemShop.cost);
                        }
                        msg.writer().writeByte(itemShop.options.size());
                        for (Item.ItemOption option : itemShop.options) {
                            msg.writer().writeByte(option.optionTemplate.id);
                            msg.writer().writeShort(option.param);
                        }
                        msg.writer().writeByte(itemShop.isNew ? 1 : 0);
                        if (itemShop.temp.type == 5) {
                            msg.writer().writeByte(1);
                            msg.writer().writeShort(itemShop.temp.head);
                            msg.writer().writeShort(itemShop.temp.body);
                            msg.writer().writeShort(itemShop.temp.leg);
                            msg.writer().writeShort(-1);
                        } else {
                            msg.writer().writeByte(0);
                        }
                    }
                }
                player.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (msg != null) {
                    msg.cleanup();
                }
            }
        }
    }

    private void openShopType3(Player player, Shop shop) {
        player.iDMark.setShopOpen(shop);
        player.iDMark.setTagNameShop(shop.tagName);
        if (shop != null) {
            Message msg = null;
            try {
                msg = new Message(-44);
                msg.writer().writeByte(SPEC_SHOP);
                msg.writer().writeByte(shop.tabShops.size());
                for (TabShop tab : shop.tabShops) {
                    msg.writer().writeUTF(tab.name);
                    msg.writer().writeByte(tab.itemShops.size());
                    for (ItemShop itemShop : tab.itemShops) {
                        msg.writer().writeShort(itemShop.temp.id);
                        msg.writer()
                                .writeShort(ItemService.gI().createNewItem((short) itemShop.iconSpec).template.iconID);
                        msg.writer().writeInt(itemShop.cost);
                        msg.writer().writeByte(itemShop.options.size());
                        for (Item.ItemOption option : itemShop.options) {
                            msg.writer().writeByte(option.optionTemplate.id);
                            msg.writer().writeShort(option.param);
                        }
                        msg.writer().writeByte(itemShop.isNew ? 1 : 0);
                        if (itemShop.temp.type == 5) {
                            msg.writer().writeByte(1);
                            msg.writer().writeShort(itemShop.temp.head);
                            msg.writer().writeShort(itemShop.temp.body);
                            msg.writer().writeShort(itemShop.temp.leg);
                            msg.writer().writeShort(-1);
                        } else {
                            msg.writer().writeByte(0);
                        }
                    }
                }
                player.sendMessage(msg);
            } catch (Exception e) {
                Logger.logException(ShopService.class, e);
            } finally {
                if (msg != null) {
                    msg.cleanup();
                }
            }
        }
    }

    private void openShopType4(Player player, String tagName, List<Item> items) {
        if (items == null) {
            return;
        }
        player.iDMark.setTagNameShop(tagName);
        Message msg = null;
        try {
            msg = new Message(-44);
            msg.writer().writeByte(4);
            msg.writer().writeByte(1);
            msg.writer().writeUTF(items.size() + "Vật\nphẩm");
            msg.writer().writeByte(items.size());
            for (Item item : items) {
                msg.writer().writeShort(item.template.id);
                msg.writer().writeUTF("LUCKY DRAGON BALL");
                msg.writer().writeByte(item.itemOptions.size() + 1);
                for (Item.ItemOption io : item.itemOptions) {
                    msg.writer().writeByte(io.optionTemplate.id);
                    msg.writer().writeShort(io.param);
                }
                if (item.quantity > 1) {
                    msg.writer().writeByte(31);
                    msg.writer().writeShort(item.quantity);
                } else {
                    msg.writer().writeByte(73);
                    msg.writer().writeShort(0);
                }
                msg.writer().writeByte(1);
                if (item.template.type == 5) {
                    msg.writer().writeByte(1);
                    msg.writer().writeShort(item.template.head);
                    msg.writer().writeShort(item.template.body);
                    msg.writer().writeShort(item.template.leg);
                    msg.writer().writeShort(-1);
                } else {
                    msg.writer().writeByte(0);
                }
            }
            player.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    private void openShopType8(Player player, String tagName, List<Item> items) {
        if (items == null) {
            return;
        }
        player.iDMark.setTagNameShop(tagName);
        Message msg = null;
        try {
            msg = new Message(-44);
            msg.writer().writeByte(8);
            msg.writer().writeByte(1);
            msg.writer().writeUTF("Mua lại\n[" + items.size() + "/20]");
            msg.writer().writeByte(items.size());
            for (Item item : items) {
                int giamualaingoc = item.template.gem / 2;
                int giamualaivang = giamualaingoc == 0
                        ? (int) item.template.gold / 2 > 0 ? (int) item.template.gold / 2 : item.quantity * 100
                        : 0;
                msg.writer().writeShort(item.template.id);
                msg.writer().writeInt(giamualaivang);
                msg.writer().writeInt(giamualaingoc);
                msg.writer().writeInt(item.quantity);
                msg.writer().writeByte(item.itemOptions.size());
                for (Item.ItemOption io : item.itemOptions) {
                    msg.writer().writeByte(io.optionTemplate.id);
                    msg.writer().writeShort(io.param);
                }
                msg.writer().writeByte(0);
                if (item.template.type == 5) {
                    msg.writer().writeByte(1);
                    msg.writer().writeShort(item.template.head);
                    msg.writer().writeShort(item.template.body);
                    msg.writer().writeShort(item.template.leg);
                    msg.writer().writeShort(-1);
                } else {
                    msg.writer().writeByte(0);
                }
            }
            player.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public void takeItem(Player player, byte type, int tempId) {
        String tagName = player.iDMark.getTagNameShop();
        if (tagName == null || tagName.length() <= 0) {
            return;
        }
        if (tagName.equals("ITEMS_LUCKY_ROUND")) {
            getItemSideBoxLuckyRound(player, player.inventory.itemsBoxCrackBall, type, tempId);
            return;
        } else if (tagName.equals("ITEMS_MAIL_BOX")) {
            getItemSideMailsBox(player, player.inventory.itemsMailBox, type, tempId);
            return;
        } else if (tagName.equals("ITEMS_REWARD")) {

            return;
        } else if (tagName.equals("ITEMS_DABAN")) {
            buyItemDaBan(player, player.inventory.itemsDaBan, type, tempId);
            return;
        } else if (tagName.equals("BILL")) {
            buyItemHD(player, tempId);
            return;
        } else if  (tagName.equals("BILL")) {
            // learnSkill(player, getItemShop(player.iDMark.getShopId(), tempId));
            return;
        }
        if (player.iDMark.getShopOpen() == null) {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
            return;
        }
        if (tagName.equals("BUA_1H") || tagName.equals("BUA_8H") || tagName.equals("BUA_1M")) {
            buyItemBua(player, tempId);
        } else if (tagName.equals("SHOP_VND")) {
            buyItemVND(player, tempId);
        } else if (tagName.equals("SHOP_NHS")) {
            buyItemNHS(player, tempId);
        } else if (tagName.equals("SHOP_BHM")) {
            buyItemBHM(player, tempId);
        } else if (tagName.equals("SHOP_QUY_LAO")) {
            buyItemQuyLao(player, tempId);
        } else if (tagName.equals("SANTA_HEAD")) {
            Item itS = ItemService.gI().createNewItem((short) tempId);
            player.head = (short) itS.template.head;
            Service.gI().Send_Caitrang(player);
            Service.gI().sendThongBao(player, "Đổi kiểu tóc thành công");
        } else {
            buyItem(player, tempId);
        }
        Service.gI().sendMoney(player);
    }

    private boolean subMoneyByItemShop(Player player, ItemShop is) {
        int gold = 0;
        int gem = 0;
        int ruby = 0;
        int coupon = 0;
        switch (is.typeSell) {
            case COST_GOLD ->
                gold = is.cost;
            case COST_GEM ->
                gem = is.cost;
            case COST_RUBY ->
                ruby = is.cost;
            case COST_COUPON ->
                coupon = is.cost;

        }
        if (player.inventory.gold < gold) {
            Service.gI().sendThongBao(player, "Bạn không có đủ vàng");
            return false;
        } else if (player.inventory.gem < gem) {
            Service.gI().sendThongBao(player, "Bạn không có đủ ngọc");
            return false;
        } else if (player.inventory.ruby < ruby) {
            Service.gI().sendThongBao(player, "Bạn không có đủ hồng ngọc");
            return false;
        } else if (player.inventory.coupon < coupon) {
            Service.gI().sendThongBao(player, "Bạn không có đủ điểm");
            return false;
        }
        player.inventory.gold -= gold;
        player.inventory.gem -= gem;
        player.inventory.ruby -= ruby;
        player.inventory.coupon -= coupon;
        return true;
    }

    private boolean subMoneyByItemShopV2(Player player, ItemShop is) {
        int gold = 0;
        int gem = 0;
        int ruby = 0;
        int coupon = 0;
        switch (is.typeSell) {
            case COST_GOLD ->
                gold = is.cost;
            case COST_GEM ->
                gem = is.cost;
            case COST_RUBY ->
                ruby = is.cost;
            case COST_COUPON ->
                coupon = is.cost;

        }
        if (player.inventory.gold < gold) {
            Service.gI().sendThongBaoOK(player,
                    "Bạn không đủ vàng, còn thiếu " + Util.numberToMoney(player.inventory.gold - gold));
            return false;
        } else if (player.inventory.gem < gem) {
            Service.gI().sendThongBaoOK(player,
                    "Bạn không đủ ngọc, còn thiếu " + Util.numberToMoney(player.inventory.gem - gem));
            return false;
        } else if (player.inventory.ruby < ruby) {
            Service.gI().sendThongBaoOK(player,
                    "Bạn không đủ hồng ngọc, còn thiếu " + Util.numberToMoney(player.inventory.ruby - ruby));
            return false;
        } else if (player.inventory.coupon < coupon) {
            Service.gI().sendThongBaoOK(player,
                    "Bạn không đủ điểm, còn thiếu " + Util.numberToMoney(player.inventory.coupon - coupon));
            return false;
        }
        player.inventory.gold -= gold;
        player.inventory.gem -= gem;
        player.inventory.ruby -= ruby;
        player.inventory.coupon -= coupon;
        Service.gI().sendMoney(player);
        return true;
    }

    /**
     * Mua bùa
     *
     * @param player người chơi
     * @param itemTempId id template vật phẩm
     */
    private void buyItemBua(Player player, int itemTempId) {
        Shop shop = player.iDMark.getShopOpen();
        ItemShop is = shop.getItemShop(itemTempId);
        if (is == null) {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
            return;
        }
        if (!subMoneyByItemShop(player, is)) {
            return;
        }
        InventoryService.gI().addItemBag(player, ItemService.gI().createItemFromItemShop(is));
        InventoryService.gI().sendItemBag(player);
        opendShop(player, shop.tagName, true);
    }

    private void buyItemVND(Player player, int itemTempId) {
        Shop shop = player.iDMark.getShopOpen();
        ItemShop is = shop.getItemShop(itemTempId);
        int pointExchange = 0;
        int evPoint = player.getSession().cash;
        if (is == null) {
            Service.gI().sendThongBao(player, "Item shop bị lỗi vui lòng báo admin");
            return;
        }
        if (InventoryService.gI().getCountEmptyBag(player) == 0) {
            Service.gI().sendThongBao(player, "Hành trang đầy rồi dọn bớt đi");
            return;
        }
        for (ItemOption io : is.options) {
            if (io.optionTemplate.id == 249) {
                pointExchange = io.param;
            }
        }
        if (pointExchange > 0) {
            if (evPoint >= pointExchange * 1000) {
                PlayerDAO.subcash(player, pointExchange * 1000);
                InventoryService.gI().addItemBag(player, ItemService.gI().createItemFromItemShop(is));
                InventoryService.gI().sendItemBag(player);
                Service.gI().sendThongBao(player,
                        "Bạn đã đổi thành công " + ItemService.gI().createItemFromItemShop(is).template.name);
                opendShop(player, shop.tagName, true);
            } else {
                Service.gI().sendThongBao(player, "Bạn còn thiếu " + (pointExchange * 1000 - evPoint) + " Coin");
            }
        }
    }

    private void buyItemNHS(Player player, int itemTempId) {
        Shop shop = player.iDMark.getShopOpen();
        ItemShop is = shop.getItemShop(itemTempId);
        int pointExchange = 0;
        int evPoint = player.event.getEventPointNHS();
        if (is == null) {
            Service.gI().sendThongBao(player, "Item shop bị lỗi vui lòng báo admin");
            return;
        }
        if (InventoryService.gI().getCountEmptyBag(player) == 0) {
            Service.gI().sendThongBao(player, "Hành trang đầy rồi dọn bớt đi");
            return;
        }
        for (ItemOption io : is.options) {
            if (io.optionTemplate.id == 76) {
                pointExchange = io.param;
            }
        }
        if (pointExchange > 0) {
            if (evPoint >= pointExchange) {
                player.event.subEventPointNHS(pointExchange);
                InventoryService.gI().addItemBag(player, ItemService.gI().createItemFromItemShop(is));
                InventoryService.gI().sendItemBag(player);
                Service.gI().sendThongBao(player,
                        "Bạn đã đổi thành công " + ItemService.gI().createItemFromItemShop(is).template.name);
                opendShop(player, shop.tagName, true);
            } else {
                Service.gI().sendThongBao(player, "Bạn còn thiếu " + (pointExchange - evPoint) + " điểm");
            }
        }
    }

    private void buyItemBHM(Player player, int itemTempId) {
        Shop shop = player.iDMark.getShopOpen();
        ItemShop is = shop.getItemShop(itemTempId);
        int pointExchange = 0;
        int evPoint = player.event.getEventPointBHM();
        if (is == null) {
            Service.gI().sendThongBao(player, "Item shop bị lỗi vui lòng báo admin");
            return;
        }
        if (InventoryService.gI().getCountEmptyBag(player) == 0) {
            Service.gI().sendThongBao(player, "Hành trang đầy rồi dọn bớt đi");
            return;
        }
        for (ItemOption io : is.options) {
            if (io.optionTemplate.id == 76) {
                pointExchange = io.param;
            }
        }
        if (pointExchange > 0) {
            if (evPoint >= pointExchange) {
                player.event.subEventPointBHM(pointExchange);
                InventoryService.gI().addItemBag(player, ItemService.gI().createItemFromItemShop(is));
                InventoryService.gI().sendItemBag(player);
                Service.gI().sendThongBao(player,
                        "Bạn đã đổi thành công " + ItemService.gI().createItemFromItemShop(is).template.name);
                opendShop(player, shop.tagName, true);
            } else {
                Service.gI().sendThongBao(player, "Bạn còn thiếu " + (pointExchange - evPoint) + " điểm");
            }
        }
    }

    private void buyItemQuyLao(Player player, int itemTempId) {
        Shop shop = player.iDMark.getShopOpen();
        ItemShop is = shop.getItemShop(itemTempId);
        int pointExchange = 0;
        int evPoint = player.event.getEventPointQuyLao();
        if (is == null) {
            Service.gI().sendThongBao(player, "Item shop bị lỗi vui lòng báo admin");
            return;
        }
        if (InventoryService.gI().getCountEmptyBag(player) == 0) {
            Service.gI().sendThongBao(player, "Hành trang đầy rồi dọn bớt đi");
            return;
        }
        for (ItemOption io : is.options) {
            if (io.optionTemplate.id == 76) {
                pointExchange = io.param;
            }
        }
        if (pointExchange > 0) {
            if (evPoint >= pointExchange) {
                player.event.subEventPointQuyLao(pointExchange);
                InventoryService.gI().addItemBag(player, ItemService.gI().createItemFromItemShop(is));
                InventoryService.gI().sendItemBag(player);
                Service.gI().sendThongBao(player,
                        "Bạn đã đổi thành công " + ItemService.gI().createItemFromItemShop(is).template.name);
                opendShop(player, shop.tagName, true);
            } else {
                Service.gI().sendThongBao(player, "Bạn còn thiếu " + (pointExchange - evPoint) + " điểm");
            }
        }
    }

    /**
     * Mua vật phẩm trong cửa hàng
     *
     * @param player     người chơi
     * @param itemTempId id template vật phẩm
     */
    public void buyItem(Player player, int itemTempId) {
        Shop shop = player.iDMark.getShopOpen();
        ItemShop is = shop.getItemShop(itemTempId);
        int[][] listDauThan = { { 13, 293 }, { 60, 294 }, { 61, 295 }, { 62, 296 }, { 63, 297 }, { 64, 298 },
                { 65, 299 }, { 352, 596 }, { 523, 597 } };
        if (is == null) {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
            return;
        }
        if (is.tabShop.id == 30) {
            Item pGG = InventoryService.gI().findItem(player.inventory.itemsBag, 459);
            if (pGG != null) {
                if (player.inventory.gem >= COST_GEM) {
                    player.inventory.gem -= COST_GEM;
                    Service.gI().sendMoney(player);
                    Item item = ItemService.gI().createItemFromItemShop(is);
                    InventoryService.gI().addItemBag(player, item);
                    InventoryService.gI().sendItemBag(player);
                    InventoryService.gI().subQuantityItemsBag(player, pGG, 1);
                    Service.gI().sendThongBao(player, "Mua thành công " + is.temp.name);
                } else {
                    Service.gI().sendThongBao(player, "Bạn không đủ ngọc để thực hiện giao dịch.");
                }
            } else {
                Service.gI().sendThongBao(player, "Bạn không có phiếu giảm giá để thực hiện giao dịch.");
            }
        }
        if (is.tabShop.id == 20) {
            if (TaskService.gI().getIdTask(player) < ConstTask.TASK_28_0) {
                Service.gI().sendThongBao(player, "Bạn chưa đủ điều kiện để mua item này.");
                return;
            }
        }
        if (InventoryService.gI().getCountEmptyBag(player) == 0) {
            Service.gI().sendThongBao(player, "Hành trang đã đầy");
            return;
        }
        if (itemTempId == 711 && !InventoryService.gI().findItemSkinQuyLaoKame(player)) {
            Service.gI().sendThongBao(player, "Bạn phải có cải trang thành Quy Lão Kame mới có thể đổi.");
            return;
        }
        if (buyMoRongHanhTrang(player, is)) {
            return;
        }
        if (is.tabShop.id == 28) {
            buyDanhHieu(player);
            return;
        }
        if (is.tabShop.id == 29) {
            changeDanhHieu(player, is);
            return;
        }
        if (shop.typeShop == ShopService.KINANG_SHOP) {
            learnKyNang(player, is);
            return;
        }
        if (itemTempId == 1524 || itemTempId == 1523 || itemTempId == 521) {
            if (!checkAutoTrainPurchase(player, itemTempId)) {
                return;
            }
        }
        if (shop.typeShop == ShopService.NORMAL_SHOP) {
            if (!subMoneyByItemShop(player, is)) {
                return;
            }
        } else if (shop.typeShop == ShopService.SPEC_SHOP) {
            if (!this.subIemByItemShop(player, is)) {
                return;
            }
        } else if (shop.tagName.equals("SHOP_VND")) {
            if (!subMoneyByItemShop(player, is)) {
                return;
            }
        } else if (shop.tagName.equals("SHOP_NHS")) {
            if (!subMoneyByItemShop(player, is)) {
                return;
            }
        } else if (shop.tagName.equals("SHOP_BHM")) {
            if (!subMoneyByItemShop(player, is)) {
                return;
            }
        } else if (shop.tagName.equals("SHOP_QUY_LAO")) {
            if (!subMoneyByItemShop(player, is)) {
                return;
            }
        }

        // Mua item bình thường (không phải shop giảm giá)
        Item item = ItemService.gI().createItemFromItemShop(is);
        item = buyMagicPean(player, listDauThan, item);
        if (item.template.id == 1523 || item.template.id == 1524) {
            item = ItemService.gI().createNewItem((short) 521);
            item.itemOptions.addAll(is.options);
        }
        InventoryService.gI().addItemBag(player, item);

        InventoryService.gI().sendItemBag(player);
        Service.gI().sendThongBao(player, "Mua thành công " + is.temp.name);

        // Cập nhật trạng thái mua Tự động luyện tập
        if (itemTempId == 1523 || itemTempId == 1524 || itemTempId == 521) {
            updateAutoTrainPurchase(player, itemTempId);
            Service.gI().sendThongBao(player, "Mua thành công " + is.temp.name);
        }
    }

    private boolean checkAutoTrainPurchase(Player player, int itemTempId) {
        int autoTrainState = player.autoTrainState;

        if (itemTempId == 1524 && autoTrainState != 2) {
            Service.gI().sendThongBao(player, "Bạn cần mua Tự động luyện tập 2 trước!");
            return false;
        } else if (itemTempId == 1523 && autoTrainState != 1) { // sửa lại thành 'else if'
            Service.gI().sendThongBao(player, "Bạn cần mua Tự động luyện tập 1 trước!");
            return false;
        }

        return true;
    }

    private void updateAutoTrainPurchase(Player player, int itemTempId) {
        if (itemTempId == 1524) {
            player.autoTrainState = 0;
        } else if (itemTempId == 521) {
            if (player.autoTrainState != 2) {
                player.autoTrainState = 1;
            }
        } else if (itemTempId == 1523) {
            player.autoTrainState = 2;
        }
    }

    private void buyDanhHieu(Player pl) {
        Service.gI().sendThongBao(pl, "Bạn chưa mở khoá danh hiệu này");
    }

    private void changeDanhHieu(Player pl, ItemShop is) {
        if (pl.lastTimeChangeBadges - System.currentTimeMillis() > 0) {
            Service.gI().sendThongBao(pl,
                    "Vui lòng đợi " + (pl.lastTimeChangeBadges - System.currentTimeMillis()) / 1000 + " giây nữa");
            return;
        }
        if (pl.badges.idBadges == BagesTemplate.fineIdEffectbyIdItem(is.temp.id)) {
            Service.gI().sendThongBao(pl, "Danh hiệu đang được sữ dụng, hãy chọn danh hiệu khác");
            pl.lastTimeChangeBadges = System.currentTimeMillis() + 30000;
            return;
        }
        BadgesService.turnOnBadges(pl, BagesTemplate.fineIdEffectbyIdItem(is.temp.id));
        Service.gI().sendThongBao(pl, "Đã đổi danh hiệu sang " + is.temp.name);
        pl.lastTimeChangeBadges = System.currentTimeMillis() + 30000;
    }

    private boolean buyMoRongHanhTrang(Player player, ItemShop itemShop) {
        boolean isBuy = false;
        if (itemShop.temp.id == 518 || itemShop.temp.id == 517 || itemShop.temp.id == 1627) {
            if (itemShop.temp.id == 1627 && player.inventory.itemsBag.size() >= 150) {
                Service.gI().sendThongBao(player, "Đã đạt mức tối đa");
                Service.gI().sendMoney(player);
                return true;
            }
            if (itemShop.temp.id == 517 && player.inventory.itemsBag.size() >= 100) {
                Service.gI().sendThongBao(player, "Đã đạt mức tối đa");
                Service.gI().sendMoney(player);
                return true;
            }
            if (itemShop.temp.id == 518 && player.inventory.itemsBox.size() >= 100) {
                Service.gI().sendThongBao(player, "Đã đạt mức tối đa");
                Service.gI().sendMoney(player);
                return true;
            }
            if (subMoneyByItemShop(player, itemShop)) {
                Item item = ItemService.gI().createItemFromItemShop(itemShop);
                InventoryService.gI().addItemBag(player, item);
                InventoryService.gI().sendItemBag(player);
                opendShop(player, itemShop.tabShop.shop.tagName, true);
                Service.gI().sendThongBao(player, "Bạn đã mua thành công");
            }
            isBuy = true;
        }
        return isBuy;
    }

    private Item buyMagicPean(Player player, int[][] listDauThan, Item item) {
        for (int i = 0; i < listDauThan.length; i++) {
            if (item.template.id == listDauThan[i][1]) {
                item = ItemService.gI().createNewItem((short) listDauThan[i][0]);
                item.itemOptions.add(new Item.ItemOption(player.magicTree.level - 1 > 1 ? 2 : 48,
                        MagicTree.PEA_PARAM[player.magicTree.level - 1]));
                item.quantity = 30;
                return item;
            }
        }
        return item;
    }

    private boolean subIemByItemShop(Player pl, ItemShop itemShop) {
        boolean isBuy = false;
        short itSpec = (short) itemShop.iconSpec;
        int buySpec = itemShop.cost;
        Item itS = ItemService.gI().createNewItem((short) itemShop.iconSpec);
        switch (itS.template.id) {
            case 76:
            case 188:
            case 189:
            case 190:
                if (pl.inventory.gold >= buySpec) {
                    pl.inventory.gold -= buySpec;
                    isBuy = true;
                } else {
                    Service.gI().sendThongBao(pl, "Bạn Không Đủ Vàng Để Mua Vật Phẩm");
                    isBuy = false;
                }
                break;
            case 861:
                if (pl.inventory.ruby >= buySpec) {
                    pl.inventory.ruby -= buySpec;
                    isBuy = true;
                } else {
                    Service.gI().sendThongBao(pl, "Bạn Không Đủ Hồng Ngọc Để Mua Vật Phẩm");
                    isBuy = false;
                }
                break;
            default:
                if (InventoryService.gI().findItemBag(pl, itSpec) == null
                        || !InventoryService.gI().findItemBag(pl, itSpec).isNotNullItem()) {
                    Service.gI().sendThongBao(pl, "Không tìm thấy " + itS.template.name);
                    isBuy = false;
                } else if (InventoryService.gI().findItemBag(pl, itSpec).quantity < buySpec) {
                    Service.gI().sendThongBao(pl, "Bạn không có đủ " + buySpec + " " + itS.template.name);
                    isBuy = false;
                } else {
                    InventoryService.gI().subQuantityItemsBag(pl, InventoryService.gI().findItemBag(pl, itSpec),
                            buySpec);
                    isBuy = true;
                }
                break;
        }
        return isBuy;
    }

    public void showConfirmSellItem(Player pl, int where, int index) {
        Item item = null;
        if (where == 0) {
            if (index < 0) {
                Service.gI().sendThongBao(pl, "Không thể thực hiện");
                return;
            }
            item = pl.inventory.itemsBody.get(index);
        } else {
            // if (pl.getSession().version < 220) {
            // index -= (pl.inventory.itemsBody.size() - 7);
            // }
            item = pl.inventory.itemsBag.get(index);
        }
        if (item != null && item.isNotNullItem()) {
            if (item.template.id == 570) {
                Service.gI().sendThongBao(pl, "Bạn không thể bán vật phẩm này");
                return;
            }
            int quantity = item.quantity;
            int cost = item.template.gold;
            if (item.template.id == 457) {
                if (quantity > 1) {
                    Input.gI().createFormBanSLL(pl);
                    return;
                }
                quantity = 1;
            } else {
                cost /= 4;
            }
            if (cost == 0) {
                cost = 1;
            }
            cost *= quantity;

            String text = "Bạn có muốn bán\nx" + quantity
                    + " " + item.template.name + "\nvới giá là " + Util.numberToMoney(cost) + " vàng?";
            Message msg = null;
            try {
                msg = new Message(7);
                msg.writer().writeByte(where);
                msg.writer().writeShort(index);
                msg.writer().writeUTF(text);
                pl.sendMessage(msg);
            } catch (Exception e) {
            } finally {
                if (msg != null) {
                    msg.cleanup();
                }
            }
        }
    }

    public void sellItem(Player pl, int where, int index) {
        if (pl.iDMark.getShopOpen() == null || pl.iDMark.getTagNameShop() == null) {
            Service.gI().sendThongBao(pl, "Không thể thực hiện");
            return;
        }
        if (index < 0) {
            Service.gI().sendThongBao(pl, "Không thể thực hiện");
            return;
        }
        Item item = null;
        if (where == 0) {
            item = pl.inventory.itemsBody.get(index);
        } else {
            item = pl.inventory.itemsBag.get(index);
        }
        if (item != null) {
            if (item.template.id == 570) {
                Service.gI().sendThongBao(pl, "Bạn không thể bán vật phẩm này");
                return;
            }
            if (InventoryService.gI().getParam(pl, 93, item.template.id) > 0) {
                Service.gI().sendThongBao(pl, "Bạn không thể bán vật phẩm có hạn sử dụng");
                return;
            }
            int quantity = item.quantity;
            int cost = item.template.gold;
            if (item.template.id == 457) {
                quantity = 1;
            } else {
                cost /= 4;
            }
            if (cost == 0) {
                cost = 1;
            }
            cost *= quantity;

            if (pl.inventory.gold + cost > Inventory.LIMIT_GOLD) {
                Service.gI().sendThongBao(pl, "Vàng sau khi bán vượt quá giới hạn");
                return;
            }
            pl.inventory.gold += cost;
            Service.gI().sendMoney(pl);
            Service.gI().sendThongBao(pl, "Đã bán " + item.template.name
                    + " thu được " + Util.numberToMoney(cost) + " vàng");

            // Add vật phẩm đã bán
            if (item.template.id != 457) {
                VatPhamDaBan.gI().addItem(pl, item);
            }
            if (where == 0) {
                InventoryService.gI().subQuantityItemsBody(pl, item, quantity);
                InventoryService.gI().sendItemBody(pl);
                Service.gI().Send_Caitrang(pl);
            } else {
                InventoryService.gI().subQuantityItemsBag(pl, item, quantity);
                InventoryService.gI().sendItemBag(pl);
            }
            if ("BUNMA".equals(pl.iDMark.getTagNameShop())
                    || "DENDE".equals(pl.iDMark.getTagNameShop())
                    || "APPULE".equals(pl.iDMark.getTagNameShop())) {
                AchievementService.gI().checkDoneTask(pl, ConstAchievement.TRUM_NHAT_VE_CHAI);
            }
        } else {
            Service.gI().sendThongBao(pl, "Không thể thực hiện");
        }
    }

    private void getItemSideBoxLuckyRound(Player player, List<Item> items, byte type, int index) {
        if (items == null) {
            return;
        }
        if (index < 0 || index >= items.size()) {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
            return;
        }
        Item item = items.get(index);
        switch (type) {
            case 0: // nhận
                if (item.isNotNullItem()) {
                    if (InventoryService.gI().getCountEmptyBag(player) != 0) {
                        InventoryService.gI().addItemBag(player, item);
                        Service.gI().sendThongBao(player,
                                "Bạn nhận được " + (item.template.id == 189
                                        ? Util.numberToMoney(item.quantity) + " vàng"
                                        : item.template.name));
                        InventoryService.gI().sendItemBag(player);
                        items.remove(index);
                    } else {
                        Service.gI().sendThongBao(player, "Hành trang đã đầy");
                    }
                } else {
                    Service.gI().sendThongBao(player, "Không thể thực hiện");
                }
                break;
            case 1: // xóa
                items.remove(index);
                Service.gI().sendThongBao(player, "Xóa vật phẩm thành công");
                break;
            case 2: // nhận hết
                for (int i = items.size() - 1; i >= 0; i--) {
                    item = items.get(i);
                    if (InventoryService.gI().addItemBag(player, item)) {
                        Service.gI().sendThongBao(player,
                                "Bạn nhận được " + (item.template.id == 189
                                        ? Util.numberToMoney(item.quantity) + " vàng"
                                        : item.template.name));
                        items.remove(i);
                    }
                }
                InventoryService.gI().sendItemBag(player);
                break;
        }
        openShopType4(player, player.iDMark.getTagNameShop(), items);
    }

    private void getItemSideMailsBox(Player player, List<Item> items, byte type, int index) {
        if (items == null) {
            return;
        }
        if (index < 0 || index >= items.size()) {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
            return;
        }
        Item item = items.get(index);
        switch (type) {
            case 0: // nhận
                if (item.isNotNullItem()) {
                    if (InventoryService.gI().getCountEmptyBag(player) != 0) {
                        InventoryService.gI().addItemBag(player, item);
                        Service.gI().sendThongBao(player,
                                "Bạn nhận được " + (item.template.id == 189
                                        ? Util.numberToMoney(item.quantity) + " vàng"
                                        : item.template.name));
                        InventoryService.gI().sendItemBag(player);
                        items.remove(index);
                    } else {
                        Service.gI().sendThongBao(player, "Hành trang đã đầy");
                    }
                } else {
                    Service.gI().sendThongBao(player, "Không thể thực hiện");
                }
                break;
            case 1: // xóa
                items.remove(index);
                Service.gI().sendThongBao(player, "Xóa vật phẩm thành công");
                break;
            case 2: // nhận hết
                for (int i = items.size() - 1; i >= 0; i--) {
                    item = items.get(i);
                    if (InventoryService.gI().addItemBag(player, item)) {
                        Service.gI().sendThongBao(player,
                                "Bạn nhận được " + (item.template.id == 189
                                        ? Util.numberToMoney(item.quantity) + " vàng"
                                        : item.template.name));
                        items.remove(i);
                    }
                }
                InventoryService.gI().sendItemBag(player);
                break;
        }
        openShopType4(player, player.iDMark.getTagNameShop(), items);
    }

    private void buyItemDaBan(Player player, List<Item> items, byte type, int index) {
        if (items == null) {
            return;
        }
        if (index >= items.size()) {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
            return;
        }
        Item item = items.get(index);
        int giamualaingoc = item.template.gem / 2;
        int giamualaivang = giamualaingoc == 0
                ? (int) item.template.gold / 2 > 0 ? (int) item.template.gold / 2 : item.quantity * 100
                : 0;
        if (giamualaivang > 0 && player.inventory.gold < giamualaivang) {
            Service.gI().sendThongBao(player, "Bạn không có đủ vàng!");
            return;
        }
        if (giamualaingoc > 0 && player.inventory.gem < giamualaingoc) {
            Service.gI().sendThongBao(player, "Bạn không có đủ ngọc xanh!");
            return;
        }
        player.inventory.gem -= giamualaingoc;
        player.inventory.gold -= giamualaivang;
        Service.gI().sendMoney(player);
        if (item.isNotNullItem()) {
            if (InventoryService.gI().getCountEmptyBag(player) != 0) {
                InventoryService.gI().addItemBag(player, item);
                Service.gI().sendThongBao(player,
                        "Bạn nhận được " + (item.template.id == 189
                                ? Util.numberToMoney(item.quantity) + " vàng"
                                : item.template.name));
                InventoryService.gI().sendItemBag(player);
                items.remove(index);
            } else {
                Service.gI().sendThongBao(player, "Hành trang đã đầy");
            }
        } else {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
        }
        openShopType8(player, player.iDMark.getTagNameShop(), items);
    }

    private void buyItemHD(Player player, int itemTempId) {
        Shop shop = player.iDMark.getShopOpen();
        ItemShop is = shop.getItemShop(itemTempId);
        if (is == null) {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
            return;
        }
        Item item = ItemService.gI().createItemFromItemShop(is);
        if (InventoryService.gI().getCountEmptyBag(player) < 1) {
            Service.gI().sendThongBao(player, "Hành trang đã đầy, không thể chứa thêm.");
            return;
        }
        if (!subMoneyByItemShopV2(player, is)) {
            return;
        }
        if (item.template.level == 14) {
            Item doAn = player.inventory.itemsBag.stream()
                    .filter(it -> it != null && it.template != null
                            && (it.template.id == 663 || it.template.id == 664 || it.template.id == 665
                                    || it.template.id == 666 || it.template.id == 667)
                            && it.quantity >= 99)
                    .findFirst().orElse(null);
            if (doAn != null) {
                InventoryService.gI().subQuantityItemsBag(player, doAn, 99);
            } else {
                Service.gI().sendThongBao(player, "Không có đủ thức ăn");
                return;
            }
        }
        if (player.inventory.itemsBody.get(0) != null || player.inventory.itemsBody.get(1) != null
                || player.inventory.itemsBody.get(2) != null || player.inventory.itemsBody.get(3) != null
                || player.inventory.itemsBody.get(4) != null || player.inventory.itemsBody.get(5) != null) {
            Item dothan = player.inventory.itemsBody.stream()
                    .filter(it -> it != null && it.template != null && it.template.level == 13).findFirst()
                    .orElse(null);
            if (dothan == null) {
                Service.gI().sendThongBao(player, "Không có đủ set thần");
                return;
            }
        }
        int param = 0;
        if (item.template.level == 14) {
            if (Util.isTrue(25, 100)) {
                param = Util.nextInt(11, 15);
            } else if (Util.isTrue(25, 75)) {
                param = Util.nextInt(5, 10);
            } else {
                param = Util.nextInt(0, 4);
            }
        }
        List<ItemOption> itemoptions = new ArrayList<>();
        if (!item.itemOptions.isEmpty()) {
            for (ItemOption ios : item.itemOptions) {
                if (item.template.level == 14 && InventoryService.gI().optionCanUpgrade(ios.optionTemplate.id)
                        && param > 0) {
                    int id = ios.optionTemplate.id;
                    int param1 = ios.param + (ios.param * param) / 100;
                    itemoptions.add(new ItemOption(id, param1));
                } else if (ios.optionTemplate.id != 164) {
                    itemoptions.add(new ItemOption(ios.optionTemplate.id, ios.param));
                }
            }
        } else {
            itemoptions.add(new ItemOption(73, (short) 0));
        }
        item.itemOptions.clear();
        item.itemOptions.addAll(itemoptions);
        InventoryService.gI().addItemBag(player, item);
        InventoryService.gI().sendItemBag(player);
        Service.gI().sendThongBao(player, "Mua thành công " + is.temp.name);
    }
}
