package models.Consign;

/*
 *
 *
 * @author NTD
 */
import consts.ConstNpc;
import item.Item;
import item.Item.ItemOption;
import player.Player;
import network.Message;
import services.InventoryService;
import services.ItemService;
import services.NpcService;
import services.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import jdbc.daos.NDVSqlFetcher;
import utils.Logger;

public class ConsignShopService {

    private static ConsignShopService instance;

    public static ConsignShopService gI() {
        if (instance == null) {
            instance = new ConsignShopService();
        }
        return instance;
    }

    private List<ConsignItem> getItemKyGui(byte tab, int... max) {
        List<ConsignItem> its = new ArrayList<>();
        List<ConsignItem> listSort = new ArrayList<>();
        List<ConsignItem> listSort2 = new ArrayList<>();
        ConsignShopManager.gI().listItem.stream().filter((it) -> (it != null && it.tab == tab && !it.isBuy)).forEachOrdered((it) -> {
            its.add(it);
        });
        its.stream().filter(i -> i != null).sorted(Comparator.comparing(i -> i.lasttime, Comparator.reverseOrder())).forEach(i -> listSort.add(i));
        if (max.length == 2) {
            if (listSort.size() > max[1]) {
                for (int i = max[0]; i < max[1]; i++) {
                    if (listSort.get(i) != null) {
                        listSort2.add(listSort.get(i));
                    }
                }
            } else {
                for (int i = max[0]; i < listSort.size(); i++) {
                    if (listSort.get(i) != null) {
                        listSort2.add(listSort.get(i));
                    }
                }
            }
            return listSort2;
        }
        if (max.length == 1 && listSort.size() > max[0]) {
            for (int i = 0; i < max[0]; i++) {
                if (listSort.get(i) != null) {
                    listSort2.add(listSort.get(i));
                }
            }
            return listSort2;
        }
        return listSort;
    }

    private List<ConsignItem> getItemKyGui() {
        List<ConsignItem> its = new ArrayList<>();
        List<ConsignItem> listSort = new ArrayList<>();
        ConsignShopManager.gI().listItem.stream().filter((it) -> (it != null && !it.isBuy)).forEachOrdered((it) -> {
            its.add(it);
        });
        its.stream().filter(i -> i != null).sorted(Comparator.comparing(i -> i.lasttime, Comparator.reverseOrder())).forEach(i -> listSort.add(i));
        return listSort;
    }

    private boolean SubThoiVang(Player pl, int quatity) {
        Iterator var3 = pl.inventory.itemsBag.iterator();

        Item item;
        do {
            if (!var3.hasNext()) {
                return false;
            }

            item = (Item) var3.next();
        } while (!item.isNotNullItem() || item.template.id != 457 || item.quantity < quatity);

        InventoryService.gI().subQuantityItemsBag(pl, item, quatity);
        return true;
    }

    public void buyItem(Player pl, int id) {
        if (pl.getSession().actived && pl.nPoint.power < 17000000000L) {
            Service.gI().sendThongBao(pl, "Yêu cầu Kích hoạt tài khoản và sức mạnh lớn hơn 17 tỷ");
            this.openShopKyGui(pl);
            return;
        }
        ConsignItem it = getItemBuy(id);
        if (it == null || it.isBuy) {
            Service.gI().sendThongBao(pl, "Vật phẩm không tồn tại hoặc đã được bán");
            return;
        }
        if (it.player_sell == pl.id) {
            Service.gI().sendThongBao(pl, "Không thể mua vật phẩm bản thân đăng bán");
            openShopKyGui(pl);
            return;
        }
        boolean isBuy = false;
        if (it.goldSell > 0) {
            if (pl.inventory.gold >= it.goldSell) {
                pl.inventory.gold -= it.goldSell;
                isBuy = true;
            } else {
                Service.gI().sendThongBao(pl, "Bạn Không Đủ Vàng Để Mua Vật Phẩm");
                isBuy = false;
            }
        } else if (it.gemSell > 0) {
            if (pl.inventory.gem >= it.gemSell) {
                pl.inventory.gem -= it.gemSell;
                isBuy = true;
            } else {
                Service.gI().sendThongBao(pl, "Bạn không đủ hồng ngọc để mua vật phẩm này!");
                isBuy = false;
            }
        }
        Service.gI().sendMoney(pl);
        if (isBuy) {
            Item item = ItemService.gI().createNewItem(it.itemId);
            item.quantity = it.quantity;
            item.itemOptions.clear();
            item.itemOptions.addAll(it.options);
            it.isBuy = true;
            if (it.isBuy) {
                InventoryService.gI().addItemBag(pl, item);
                InventoryService.gI().sendItemBag(pl);
                Service.gI().sendThongBao(pl, "Bạn đã nhận được " + item.template.name);
                openShopKyGui(pl);
            }
        }
    }

    public ConsignItem getItemBuy(int id) {
        for (ConsignItem it : getItemKyGui()) {
            if (it != null && it.id == id) {
                return it;
            }
        }
        return null;
    }

    public ConsignItem getItemBuy(Player pl, int id) {
        for (ConsignItem it : ConsignShopManager.gI().listItem) {
            if (it != null && it.id == id && it.player_sell == pl.id) {
                return it;
            }
        }
        return null;
    }

    public ConsignItem getItemBuy(Player pl, ConsignItem itk) {
        for (ConsignItem it : ConsignShopManager.gI().listItem) {
            if (it != null && it.player_sell == pl.id && it == itk) {
                return it;
            }
        }
        return null;
    }

    public void openShopKyGui(Player pl, byte index, int page) {
        if (page > getItemKyGui(index).size()) {
            return;
        }
        Message msg = null;
        try {
            msg = new Message(-100);
            msg.writer().writeByte(index);
            List<ConsignItem> items = getItemKyGui(index);
            List<ConsignItem> itemsSend = getItemKyGui(index, (int) (page * 20), (int) (page * 20 + 20));
            int cTab = (int) Math.ceil((double) items.size() / 20);
            byte tab = (byte) (cTab > 0 ? cTab : 1);
            msg.writer().writeByte(tab); // max page
            msg.writer().writeByte(page);
            msg.writer().writeByte(itemsSend.size());
            for (int j = 0; j < itemsSend.size(); j++) {
                ConsignItem itk = itemsSend.get(j);
                Item it = ItemService.gI().createNewItem(itk.itemId);
                it.itemOptions.clear();
                if (itk.options.isEmpty()) {
                    it.itemOptions.add(new ItemOption(73, 0));
                } else {
                    it.itemOptions.addAll(itk.options);
                }
                msg.writer().writeShort(it.template.id);
                msg.writer().writeShort(itk.id);
                msg.writer().writeInt(itk.goldSell);
                msg.writer().writeInt(itk.gemSell);
                msg.writer().writeByte(0); // buy type
                if (pl.getSession().version >= 222) {
                    msg.writer().writeInt(itk.quantity);
                } else {
                    msg.writer().writeByte(itk.quantity);
                }
                msg.writer().writeByte(itk.player_sell == pl.id ? 1 : 0); // isMe
                msg.writer().writeByte(it.itemOptions.size());
                for (int a = 0; a < it.itemOptions.size(); a++) {
                    msg.writer().writeByte(it.itemOptions.get(a).optionTemplate.id);
                    msg.writer().writeShort(it.itemOptions.get(a).param);
                }
                msg.writer().writeByte(0);
                //////////////////////////
                msg.writer().writeByte(0);
//                if (pl.getSession().version >= 237) {
//                    try {
//                        Player plSell = NDVSqlFetcher.loadById(itk.player_sell);
//                        if (plSell != null) {
//                            msg.writer().writeUTF(plSell.name);
//                        } else {
//                            msg.writer().writeUTF("");
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
            }
            pl.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void upItemToTop(Player pl, int id) {
        ConsignItem it = getItemBuy(id);
        if (it == null || it.isBuy) {
            Service.gI().sendThongBao(pl, "Vật phẩm không tồn tại hoặc đã được bán");
            return;
        }
        if (it.player_sell != pl.id) {
            Service.gI().sendThongBao(pl, "Vật phẩm không thuộc quyền sở hữu");
            openShopKyGui(pl);
            return;
        }
        pl.iDMark.setIdItemUpTop(id);
        NpcService.gI().createMenuConMeo(pl, ConstNpc.UP_TOP_ITEM, -1, "Bạn có muốn đưa vật phẩm '" + ItemService.gI().createNewItem(it.itemId).template.name + "' của bản thân lên trang đầu?\nYêu cầu 5tr vàng.", "Đồng ý", "Từ Chối");
    }

    public void claimOrDel(Player pl, byte action, int id) {
        ConsignItem it = getItemBuy(pl, id);
        switch (action) {
            case 1: // hủy vật phẩm
                if (it == null || it.isBuy) {
                    Service.gI().sendThongBao(pl, "Vật phẩm không tồn tại hoặc đã được bán");
                    return;
                }
                if (it.player_sell != pl.id) {
                    Service.gI().sendThongBao(pl, "Vật phẩm không thuộc quyền sở hữu");
                    openShopKyGui(pl);
                    return;
                }
                Item item = ItemService.gI().createNewItem(it.itemId);
                item.quantity = it.quantity;
                item.itemOptions.clear();
                item.itemOptions.addAll(it.options);
                if (ConsignShopManager.gI().listItem.remove(it)) {
                    InventoryService.gI().addItemBag(pl, item);
                    InventoryService.gI().sendItemBag(pl);
                    Service.gI().sendMoney(pl);
                    Service.gI().sendThongBao(pl, "Hủy bán vật phẩm thành công");
                    openShopKyGui(pl);
                }
                break;
            case 2: // nhận tiền
                if (it == null || !it.isBuy) {
                    Service.gI().sendThongBao(pl, "Vật phẩm không tồn tại hoặc chưa được bán");
                    return;
                }
                if (it.player_sell != pl.id) {
                    Service.gI().sendThongBao(pl, "Vật phẩm không thuộc quyền sở hữu");
                    openShopKyGui(pl);
                    return;
                }
                if (it.goldSell > 0) {
                    pl.inventory.gold += it.goldSell - it.goldSell * 5 / 100;
                } else if (it.gemSell > 0) {
                    pl.inventory.gem += it.gemSell - it.gemSell * 5 / 100;
                }
                if (ConsignShopManager.gI().listItem.remove(it)) {
                    Service.gI().sendMoney(pl);
                    Service.gI().sendThongBao(pl, "Bạn đã bán vật phẩm thành công");
                    openShopKyGui(pl);
                }
                break;
        }
    }

    public List<ConsignItem> getItemCanKiGui(Player pl) {
        List<ConsignItem> its = new ArrayList<>();
        ConsignShopManager.gI().listItem.stream().filter((it) -> (it != null && it.player_sell == pl.id)).forEachOrdered((it) -> {
            its.add(it);
        });
        pl.inventory.itemsBag.stream().filter((it) -> (itemCanConsign(it))).forEachOrdered((it) -> {
            its.add(new ConsignItem(InventoryService.gI().getIndexBag(pl, it), it.template.id, (int) pl.id, (byte) 4, -1, -1, it.quantity, (byte) -1, it.itemOptions, false));
        });
        return its;
    }

    public boolean itemCanConsign(Item it) {
        if (it != null && it.template != null) {
            if (it.itemOptions.stream().anyMatch(op -> op.optionTemplate.id == 86)
                    || it.itemOptions.stream().anyMatch(op -> op.optionTemplate.id == 87)
                    || it.template.type == 14
                    || it.template.type == 15
                    || it.template.type == 6
                    || it.template.id >= 14 && it.template.id <= 20) {
                return true;
            }
        }
        return false;
    }

    public int getMaxId() {
        try {
            List<Integer> id = new ArrayList<>();
            ConsignShopManager.gI().listItem.stream().filter((it) -> (it != null)).forEachOrdered((it) -> {
                id.add(it.id);
            });
            return Collections.max(id);
        } catch (Exception e) {
            return 0;
        }
    }

    public byte getTabKiGui(Item it) {
        if (it.template.type >= 0 && it.template.type <= 2) {
            return 0;
        } else if ((it.template.type >= 3 && it.template.type <= 4)) {
            return 1;
        } else if (it.template.type == 29) {
            return 2;
        } else {
            return 3;
        }
    }

    public void KiGui(Player pl, int id, int money, byte moneyType, int quantity) {
        try {
            if (pl.inventory.gem < 5) {
                Service.gI().sendThongBao(pl, "Bạn cần có ít nhất 5 ngọc xanh để làm phí đăng bán");
                return;
            }
            if (pl.inventory.itemsBag.size() < id) {
                Service.gI().sendThongBao(pl, "Không thể thực hiện");
                return;
            }
            Item it = ItemService.gI().copyItem(pl.inventory.itemsBag.get(id));
            if (money <= 0 || quantity > it.quantity) {
                Service.gI().sendThongBao(pl, "Có lỗi xảy ra");
                openShopKyGui(pl);
                return;
            }
            if (quantity > 99) {
                Service.gI().sendThongBao(pl, "Ký gửi tối đa x99");
                openShopKyGui(pl);
                return;
            }
            if (!ConsignShopService.gI().itemCanConsign(it)) {
                Service.gI().sendThongBao(pl, "Không thể thực hiện");
                return;
            }
            pl.inventory.gem -= 5;
            switch (moneyType) {
                case 0:// vàng
                    InventoryService.gI().subQuantityItemsBag(pl, pl.inventory.itemsBag.get(id), quantity);
                    ConsignShopManager.gI().listItem.add(new ConsignItem(getMaxId() + 1, it.template.id, (int) pl.id, getTabKiGui(it), money, -1, quantity, System.currentTimeMillis(), it.itemOptions, false));
                    InventoryService.gI().sendItemBag(pl);
                    openShopKyGui(pl);
                    Service.gI().sendMoney(pl);
                    Service.gI().sendThongBao(pl, "Đăng bán thành công");
                    break;
                case 1:// hồng ngọc
                    InventoryService.gI().subQuantityItemsBag(pl, pl.inventory.itemsBag.get(id), quantity);
                    ConsignShopManager.gI().listItem.add(new ConsignItem(getMaxId() + 1, it.template.id, (int) pl.id, getTabKiGui(it), -1, money, quantity, System.currentTimeMillis(), it.itemOptions, false));
                    InventoryService.gI().sendItemBag(pl);
                    openShopKyGui(pl);
                    Service.gI().sendMoney(pl);
                    Service.gI().sendThongBao(pl, "Đăng bán thành công");
                    break;
                default:
                    Service.gI().sendThongBao(pl, "Có lỗi xảy ra");
                    openShopKyGui(pl);
                    break;
            }
        } catch (Exception e) {
            Logger.logException(ConsignShopService.class, e, "Lỗi ký gủi: ");
        }
    }

    public void openShopKyGui(Player pl) {
        Message msg = null;
        try {
            msg = new Message(-44);
            msg.writer().writeByte(2);
            msg.writer().writeByte(5);
            for (byte i = 0; i < 5; i++) {
                if (i == 4) {
                    msg.writer().writeUTF(ConsignShopManager.gI().tabName[i]);
                    msg.writer().writeByte(0);
                    msg.writer().writeByte(getItemCanKiGui(pl).size());
                    for (int j = 0; j < getItemCanKiGui(pl).size(); j++) {
                        ConsignItem itk = getItemCanKiGui(pl).get(j);
                        if (itk == null) {
                            continue;
                        }
                        Item it = ItemService.gI().createNewItem(itk.itemId);
                        it.itemOptions.clear();
                        if (itk.options.isEmpty()) {
                            it.itemOptions.add(new ItemOption(73, 0));
                        } else {
                            it.itemOptions.addAll(itk.options);
                        }
                        msg.writer().writeShort(it.template.id);
                        msg.writer().writeShort(itk.id);
                        msg.writer().writeInt(itk.goldSell);
                        msg.writer().writeInt(itk.gemSell);
                        if (getItemBuy(pl, itk) == null) {
                            msg.writer().writeByte(0); // buy type
                        } else if (itk.isBuy) {
                            msg.writer().writeByte(2);
                        } else {
                            msg.writer().writeByte(1);
                        }
                        if (pl.getSession().version >= 222) {
                            msg.writer().writeInt(itk.quantity);
                        } else {
                            msg.writer().writeByte(itk.quantity);
                        }
                        msg.writer().writeByte(1); // isMe
                        msg.writer().writeByte(it.itemOptions.size());
                        for (int a = 0; a < it.itemOptions.size(); a++) {
                            msg.writer().writeByte(it.itemOptions.get(a).optionTemplate.id);
                            msg.writer().writeShort(it.itemOptions.get(a).param);
                        }
                        msg.writer().writeByte(0);
                        ////////////////////////////
                        msg.writer().writeByte(0);
//                        int type = pl.getSession().version >= 237 ? 2 : 0;
//                        msg.writer().writeByte(type);
//                        if (type == 2) {
//                            try {
//                                Player plSell = NDVSqlFetcher.loadById(itk.player_sell);
//                                if (plSell != null) {
//                                    msg.writer().writeUTF(plSell.name);
//                                } else {
//                                    msg.writer().writeUTF("");
//                                }
//                            } catch (Exception e) {
//                            }
//                        }
                    }
                } else {
                    List<ConsignItem> items = getItemKyGui(i);
                    List<ConsignItem> itemsSend = getItemKyGui(i, (byte) 20);
                    msg.writer().writeUTF(ConsignShopManager.gI().tabName[i]);
                    int cTab = (int) Math.ceil((double) items.size() / 20);
                    byte tab = (byte) (cTab > 0 ? cTab : 1);
                    msg.writer().writeByte(tab); // max page
                    msg.writer().writeByte(itemsSend.size());
                    for (int j = 0; j < itemsSend.size(); j++) {
                        ConsignItem itk = itemsSend.get(j);
                        Item it = ItemService.gI().createNewItem(itk.itemId);
                        it.itemOptions.clear();
                        if (itk.options.isEmpty()) {
                            it.itemOptions.add(new ItemOption(73, 0));
                        } else {
                            it.itemOptions.addAll(itk.options);
                        }
                        msg.writer().writeShort(it.template.id);
                        msg.writer().writeShort(itk.id);
                        msg.writer().writeInt(itk.goldSell);
                        msg.writer().writeInt(itk.gemSell);
                        msg.writer().writeByte(0); // buy type
                        if (pl.getSession().version >= 222) {
                            msg.writer().writeInt(itk.quantity);
                        } else {
                            msg.writer().writeByte(itk.quantity);
                        }
                        msg.writer().writeByte(itk.player_sell == pl.id ? 1 : 0); // isMe
                        msg.writer().writeByte(it.itemOptions.size());
                        for (int a = 0; a < it.itemOptions.size(); a++) {
                            msg.writer().writeByte(it.itemOptions.get(a).optionTemplate.id);
                            msg.writer().writeShort(it.itemOptions.get(a).param);
                        }
                        msg.writer().writeByte(0); // new item
                        //////////////////////////////////  
                        msg.writer().writeByte(0);
//                        int type = pl.getSession().version >= 237 ? 2 : 0;
//                        msg.writer().writeByte(type);
//                        if (type == 2) {
//                            try {
//                                Player plSell = NDVSqlFetcher.loadById(itk.player_sell);
//                                if (plSell != null) {
//                                    msg.writer().writeUTF(plSell.name);
//                                } else {
//                                    msg.writer().writeUTF("");
//                                }
//                            } catch (Exception e) {
//                            }
//                        }
                    }
                }
            }
            pl.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }
}
