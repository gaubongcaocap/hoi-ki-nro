package services;

/*
 *
 *
 * @author NTD
 */
import item.Item;
import item.Item.ItemOption;
import npc.specialnpc.MabuEgg;
import player.Inventory;
import player.Pet;
import player.Player;
import network.Message;
import services.func.ChangeMapService;

import java.util.ArrayList;
import java.util.List;

import models.BlackBallWar.BlackBallWarService;
import npc.specialnpc.Timedua;

public class InventoryService {

    private static InventoryService I;

    public static InventoryService gI() {
        if (InventoryService.I == null) {
            InventoryService.I = new InventoryService();
        }
        return InventoryService.I;
    }

    private void __________________Tìm_kiếm_item_____________________________() {
        //**********************************************************************
    }

    public Item findItem(List<Item> list, int tempId) {
        try {
            for (Item item : list) {
                if (item.isNotNullItem() && item.template.id == tempId) {
                    return item;
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    public boolean haveOption(Item it, int idOption) {
        if (it != null && it.isNotNullItem()) {
            return it.itemOptions.stream().anyMatch(op -> op != null && op.optionTemplate.id == idOption);
        }
        return false;
    }

    public Item findItemBody(Player player, int tempId) {
        return this.findItem(player.inventory.itemsBody, tempId);
    }

    public Item findItemBag(Player player, int tempId) {
        return this.findItem(player.inventory.itemsBag, tempId);
    }

    public Item findItemBox(Player player, int tempId) {
        return this.findItem(player.inventory.itemsBox, tempId);
    }

    public boolean isExistItem(List<Item> list, int tempId) {
        try {
            return this.findItem(list, tempId) != null;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isExistItemBody(Player player, int tempId) {
        return this.isExistItem(player.inventory.itemsBody, tempId);
    }

    public boolean isExistItemBag(Player player, int tempId) {
        return this.isExistItem(player.inventory.itemsBag, tempId);
    }

    public boolean isExistItemBox(Player player, int tempId) {
        return this.isExistItem(player.inventory.itemsBox, tempId);
    }

    private void __________________Sao_chép_danh_sách_item__________________() {
        //**********************************************************************
    }

    public List<Item> copyList(List<Item> items) {
        List<Item> list = new ArrayList<>();
        for (Item item : items) {
            list.add(ItemService.gI().copyItem(item));
        }
        return list;
    }

    public List<Item> copyItemsBody(Player player) {
        return copyList(player.inventory.itemsBody);
    }

    public List<Item> copyItemsBag(Player player) {
        return copyList(player.inventory.itemsBag);
    }

    public List<Item> copyItemsBox(Player player) {
        return copyList(player.inventory.itemsBox);
    }

    private void __________________Vứt_bỏ_item______________________________() {
        //**********************************************************************
    }

    public void throwItem(Player player, int where, int index) {
        Item itemThrow = null;
        if (where == 0) {
            itemThrow = player.inventory.itemsBody.get(index);
            removeItemBody(player, index);
            sendItemBody(player);
            Service.gI().Send_Caitrang(player);
        } else if (where == 1) {
            itemThrow = player.inventory.itemsBag.get(index);
            if (itemThrow.template != null && itemThrow.template.id == 570) {
                Service.gI().sendThongBao(player, "Không thể bỏ vật phẩm này.");
                return;
            }
            removeItemBag(player, index);
            sortItems(player.inventory.itemsBag);
            sendItemBag(player);
        }
        if (itemThrow == null) {
            return;
        }
    }

    private void __________________Xoá_bỏ_item______________________________() {
        //**********************************************************************
    }

    public void removeItem(List<Item> items, int index) {
        Item item = ItemService.gI().createItemNull();
        items.set(index, item);
    }

    public void removeItem(List<Item> items, Item item) {
        if (item == null) {
            return;
        }
        Item it = ItemService.gI().createItemNull();
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).equals(item)) {
                items.set(i, it);
                item.dispose();
                break;
            }
        }
    }

    public void removeItemBag(Player player, int index) {
        this.removeItem(player.inventory.itemsBag, index);
    }

    public void removeItemBag(Player player, Item item) {
        this.removeItem(player.inventory.itemsBag, item);
    }

    public void removeItemBody(Player player, int index) {
        this.removeItem(player.inventory.itemsBody, index);
    }

    public void removeItemPetBody(Player player, int index) {
        this.removeItemBody(player.pet, index);
    }

    public void removeItemBox(Player player, int index) {
        this.removeItem(player.inventory.itemsBox, index);
    }

    private void __________________Giảm_số_lượng_item_______________________() {
        //**********************************************************************
    }

    public void subQuantityItemsBag(Player player, Item item, int quantity) {
        subQuantityItem(player.inventory.itemsBag, item, quantity);
    }

    public void subQuantityItemsBody(Player player, Item item, int quantity) {
        subQuantityItem(player.inventory.itemsBody, item, quantity);
    }

    public void subQuantityItemsBox(Player player, Item item, int quantity) {
        subQuantityItem(player.inventory.itemsBox, item, quantity);
    }

    public void subQuantityItem(List<Item> items, Item item, int quantity) {
        if (item != null) {
            for (Item it : items) {
                if (item.equals(it)) {
                    it.quantity -= quantity;
                    if (it.quantity <= 0) {
                        this.removeItem(items, item);
                    }
                    break;
                }
            }
        }
    }

    private void __________________Sắp_xếp_danh_sách_item___________________() {
        //**********************************************************************
    }

    public void sortItems(List<Item> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            if (!list.get(i).isNotNullItem()) {
                int indexSwap = -1;
                for (int j = i + 1; j < list.size(); j++) {
                    if (list.get(j).isNotNullItem()) {
                        indexSwap = j;
                        break;
                    }
                }
                if (indexSwap != -1) {
                    Item sItem = ItemService.gI().createItemNull();
                    list.set(i, list.get(indexSwap));
                    list.set(indexSwap, sItem);
                } else {
                    break;
                }
            }
        }
    }

    public void sortItemv2(List<Item> items) {
        int index = 0;
        for (Item item : items) {
            if (item != null && item.quantity > 0) {
                items.set(index, item);
                index++;
            }
        }
        for (int i = index; i < items.size(); i++) {
            items.set(i, null);
        }
    }

    private void __________________Thao_tác_tháo_mặc_item___________________() {
        //**********************************************************************
    }

    private Item putItemBag(Player player, Item item) {
        for (int i = 0; i < player.inventory.itemsBag.size(); i++) {
            if (!player.inventory.itemsBag.get(i).isNotNullItem()) {
                player.inventory.itemsBag.set(i, item);
                Item sItem = ItemService.gI().createItemNull();
                return sItem;
            }
        }
        return item;
    }

    private Item putItemBox(Player player, Item item) {
        for (int i = 0; i < player.inventory.itemsBox.size(); i++) {
            if (!player.inventory.itemsBox.get(i).isNotNullItem()) {
                player.inventory.itemsBox.set(i, item);
                Item sItem = ItemService.gI().createItemNull();
                return sItem;
            }
        }
        return item;
    }

    private Item putItemBody(Player player, Item item) {
        Item sItem = item;
        if (!item.isNotNullItem()) {
            return sItem;
        }
        switch (item.template.type) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 32:
            case 23:
            case 24:
            case 11:
//            case 27:
            case 21:
                break;
            default:
                Service.gI().sendThongBaoOK(player.isPet ? ((Pet) player).master : player, "Trang bị không phù hợp!");
                return sItem;
        }
        if (item.template.gender < 3 && item.template.gender != player.gender) {
            Service.gI().sendThongBaoOK(player.isPet ? ((Pet) player).master : player, "Trang bị không phù hợp!");
            return sItem;
        }
        long powerRequire = item.template.strRequire;
        for (Item.ItemOption io : item.itemOptions) {
            if (io.optionTemplate.id == 21) {
                powerRequire = io.param * 1000000000L;
                break;
            }
        }
        if (player.nPoint.power < powerRequire) {
            Service.gI().sendThongBaoOK(player.isPet ? ((Pet) player).master : player, "Sức mạnh không đủ yêu cầu!");
            return sItem;
        }
        handleOption210(item);
        checkOption231(item);
        int index = -1;
        switch (item.template.type) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                index = item.template.type;
                break;
            case 32:
                index = 6;
                break;
            case 21:
//            case 27:
                index = 7;
                break;
            case 11:
                index = 8;
                break;
            case 23:
            case 24:
                index = 9;
                break;
//            case 75:
//            case 72:
//                index = 10;
//                break;
//            case 99:
//                index = 11;
//                break;
//            case 98:
//                index = 12;
//                break;                
        }

        sItem = player.inventory.itemsBody.get(index);
        if (index == 8) {
            if (sItem.isNotNullItem()) {
                Service.gI().removeEffPlayer(player, sItem.template.part);
            }
        }
        player.inventory.itemsBody.set(index, item);
        return sItem;
    }

    public void itemBagToBody(Player player, int index) {
        if (index < 0) {
            Service.gI().sendThongBao(player, "Hành trang đầy rồi");
            return;
        }
        Item item = player.inventory.itemsBag.get(index);
        if (item.isNotNullItem()) {
            player.inventory.itemsBag.set(index, putItemBody(player, item));
            sendItemBag(player);
            sendItemBody(player);
            Service.gI().point(player);
            Service.gI().Send_Caitrang(player);
        }
    }

    public void itemBodyToBag(Player player, int index) {
        if (index > 11) {
            Service.gI().sendThongBao(player, "Hành trang đầy rồi, bỏ bớt ra ba");
            System.err.println(index + "Hành trang đầy");
            return;
        }
        Item item = player.inventory.itemsBody.get(index);
        if (item.isNotNullItem()) {
            if (index == 10) {
                Service.gI().sendPetFollow(player, (short) 0);
            }
            if (index == 7 && !player.isPet) {
                if (player.newPet != null) {
                    ChangeMapService.gI().exitMap(player.newPet);
                    player.newPet.dispose();
                    player.newPet = null;
                }
            }
            player.inventory.itemsBody.set(index, putItemBag(player, item));
            sendItemBag(player);
            sendItemBody(player);
            if (index == 8 || index == 11 || index == 12) {
                if (item.isNotNullItem()) {
                    Service.gI().removeEffPlayer(player, item.template.part);
                }
            }
            Service.gI().point(player);
            Service.gI().Send_Caitrang(player);
        }
    }

    public void itemBagToPetBody(Player player, int index) {
        try {
            if (player.pet != null && player.pet.nPoint.power >= 1500000) {
                Item item = player.inventory.itemsBag.get(index);
                if (item.isNotNullItem()) {
                    Item itemSwap = putItemBody(player.pet, item);
                    player.inventory.itemsBag.set(index, itemSwap);
                    sendItemBag(player);
                    sendItemBody(player);
                    if (!itemSwap.equals(item)) {
                        Service.gI().point(player);
                        Service.gI().showInfoPet(player);
                    }
                    Service.gI().Send_Caitrang(player.pet);
                    Service.gI().Send_Caitrang(player);
                }
            } else {
                Service.gI().sendThongBao(player, "Đệ tử phải đạt 1tr5 sức mạnh mới có thể mặc");
            }
        } catch (Exception E) {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
        }
    }

    public void itemPetBodyToBag(Player player, int index) {
        Item item = player.pet.inventory.itemsBody.get(index);
        if (item.isNotNullItem()) {
            player.pet.inventory.itemsBody.set(index, putItemBag(player, item));
            sendItemBag(player);
            sendItemBody(player);
            Service.gI().point(player);
            Service.gI().Send_Caitrang(player.pet);
            Service.gI().Send_Caitrang(player);
            Service.gI().showInfoPet(player);
        }
    }

    public void itemBoxToBodyOrBag(Player player, int index) {
        if (index < 0) {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
            return;
        }
        Item item = player.inventory.itemsBox.get(index);
        if (item.isNotNullItem()) {
            boolean done = false;
            if (item.template.type >= 0 && item.template.type <= 5 || item.template.type == 32) {
                Item itemBody = player.inventory.itemsBody.get(item.template.type == 32 ? 6 : item.template.type);
                if (!itemBody.isNotNullItem()) {
                    if (item.template.gender == player.gender || item.template.gender == 3) {
                        long powerRequire = item.template.strRequire;
                        for (Item.ItemOption io : item.itemOptions) {
                            if (io.optionTemplate.id == 21) {
                                powerRequire = io.param * 1000000000L;
                                break;
                            }
                        }
                        if (powerRequire <= player.nPoint.power) {
                            player.inventory.itemsBody.set(item.template.type == 32 ? 6 : item.template.type, item);
                            player.inventory.itemsBox.set(index, itemBody);
                            done = true;

                            sendItemBody(player);
                            Service.gI().point(player);
                            Service.gI().Send_Caitrang(player);
                        }
                    }
                }
            }
            if (!done) {
                if (addItemBag(player, item)) {

                    if (item.quantity == 0) {
                        Item sItem = ItemService.gI().createItemNull();
                        player.inventory.itemsBox.set(index, sItem);
                    }
                    sendItemBag(player);
                }
            }
            sendItemBox(player);
        }
    }

    public void itemBagToBox(Player player, int index) {
        if (index < 0) {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
            return;
        }
        Item item = player.inventory.itemsBag.get(index);
        if (item != null && item.isNotNullItem()) {
            if (item.template.id == 457) {
                Service.gI().sendThongBao(player, "Không thể cất vàng vào rương");
                return;
            }
            if (addItemBox(player, item)) {
                if (item.quantity == 0) {
                    Item sItem = ItemService.gI().createItemNull();
                    player.inventory.itemsBag.set(index, sItem);
                }
                sortItems(player.inventory.itemsBag);
                sendItemBag(player);
                sendItemBox(player);
            }
        }
    }

    public void itemBodyToBox(Player player, int index) {
        Item item = player.inventory.itemsBody.get(index);
        if (item.isNotNullItem()) {
            player.inventory.itemsBody.set(index, putItemBox(player, item));
            sortItems(player.inventory.itemsBag);
            sendItemBody(player);
            sendItemBox(player);
            Service.gI().point(player);
            Service.gI().Send_Caitrang(player);
        }
    }

    private void __________________Gửi_danh_sách_item_cho_người_chơi________() {
        //**********************************************************************
    }

    public void sendItemBag(Player player) {
        sortItems(player.inventory.itemsBag);
        Message msg;
        try {
            msg = new Message(-36);
            msg.writer().writeByte(0);
            msg.writer().writeByte(player.inventory.itemsBag.size());
            for (int i = 0; i < player.inventory.itemsBag.size(); i++) {
                Item item = player.inventory.itemsBag.get(i);
                if (!item.isNotNullItem()) {
                    continue;
                }
                msg.writer().writeShort(item.template.id);
                msg.writer().writeInt(item.quantity);
                msg.writer().writeUTF(item.getInfo());
                msg.writer().writeUTF(item.getContent());
                msg.writer().writeByte(item.itemOptions.size()); //options
                for (int j = 0; j < item.itemOptions.size(); j++) {
                    if (item.itemOptions.get(j).optionTemplate.id == 213) {
                        int opId = 213;
                        int param = item.itemOptions.get(j).param;
                        if (param > 1_000_000) {
                            opId = 223;
                            param /= 1_000_000;
                        } else if (param > 1000) {
                            opId = 222;
                            param /= 1000;
                        }
                        msg.writer().writeByte(opId);
                        msg.writer().writeShort(param);
                    } else {
                        msg.writer().writeByte(item.itemOptions.get(j).optionTemplate.id);
                        msg.writer().writeShort(item.itemOptions.get(j).param);
                    }
                }
            }

            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void sendItemBody(Player player) {
        Message msg;
        try {
            msg = new Message(-37);
            msg.writer().writeByte(0);
            msg.writer().writeShort(player.getHead());
            msg.writer().writeByte(player.inventory.itemsBody.size());
            for (Item item : player.inventory.itemsBody) {
                if (!item.isNotNullItem()) {
                    msg.writer().writeShort(-1);
                } else {
                    msg.writer().writeShort(item.template.id);
                    msg.writer().writeInt(item.quantity);
                    msg.writer().writeUTF(item.getInfo());
                    msg.writer().writeUTF(item.getContent());
                    List<Item.ItemOption> itemOptions = item.itemOptions;
                    msg.writer().writeByte(itemOptions.size());
                    for (Item.ItemOption itemOption : itemOptions) {
                        if (itemOption.optionTemplate.id == 213) {
                            int opId = 213;
                            int param = itemOption.param;
                            if (param > 1_000_000) {
                                opId = 223;
                                param /= 1_000_000;
                            } else if (param > 1000) {
                                opId = 222;
                                param /= 1000;
                            }
                            msg.writer().writeByte(opId);
                            msg.writer().writeShort(param);
                        } else {
                            msg.writer().writeByte(itemOption.optionTemplate.id);
                            msg.writer().writeShort(itemOption.param);
                        }
                    }
                }
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
        Service.gI().Send_Caitrang(player);
    }

    public void sendItemBox(Player player) {
        Message msg;
        try {
            msg = new Message(-35);
            msg.writer().writeByte(0);
            msg.writer().writeByte(player.inventory.itemsBox.size());
            for (Item it : player.inventory.itemsBox) {
                msg.writer().writeShort(it.isNotNullItem() ? it.template.id : -1);
                if (it.isNotNullItem()) {
                    msg.writer().writeInt(it.quantity);
                    msg.writer().writeUTF(it.getInfo());
                    msg.writer().writeUTF(it.getContent());
                    msg.writer().writeByte(it.itemOptions.size());
                    for (Item.ItemOption io : it.itemOptions) {
                        if (io.optionTemplate.id == 213) {
                            int opId = 213;
                            int param = io.param;
                            if (param > 1_000_000) {
                                opId = 223;
                                param /= 1_000_000;
                            } else if (param > 1000) {
                                opId = 222;
                                param /= 1000;
                            }
                            msg.writer().writeByte(opId);
                            msg.writer().writeShort(param);
                        } else {
                            msg.writer().writeByte(io.optionTemplate.id);
                            msg.writer().writeShort(io.param);
                        }
                    }
                }
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
        this.openBox(player);
    }

    public void openBox(Player player) {
        Message msg;
        try {
            msg = new Message(-35);
            msg.writer().writeByte(1);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    private void __________________Thêm_vật_phẩm_vào_danh_sách______________() {
        //**********************************************************************
    }

    private boolean addItemSpecial(Player player, Item item) {
        //bùa
        if (item.template.type == 13) {
            int min = 0;
            try {
                String tagShopBua = player.iDMark.getShopOpen().tagName;
                if (tagShopBua.equals("BUA_1H")) {
                    min = 60;
                } else if (tagShopBua.equals("BUA_8H")) {
                    min = 60 * 8;
                } else if (tagShopBua.equals("BUA_1M")) {
                    min = 60 * 24 * 30;
                }
            } catch (Exception e) {
            }
            player.charms.addTimeCharms(item.template.id, min);
            return true;
        }

        switch (item.template.id) {
            case 568: //quả trứng
                if (player.mabuEgg == null) {
                    MabuEgg.createMabuEgg(player);
                }
                return true;
            case 569: //dưa hấu
                if (player.timedua == null) {
                    Timedua.createTimedua(player);
                    Service.gI().sendThongBao(player, "Cây dưa hấu vừa được trông tại nhà bạn");
                }
                return true;
            case 453: //tàu tennis
                player.haveTennisSpaceShip = true;
                return true;
            case 74: //đùi gà nướng
                player.nPoint.setFullHpMp();
                PlayerService.gI().sendInfoHpMp(player);
                return true;
        }
        return false;
    }

    public boolean addItemBag(Player player, Item item) {
        //ngọc rồng đen
        if (ItemMapService.gI().isBlackBall(item.template.id)) {
            return BlackBallWarService.gI().pickBlackBall(player, item);
        }
        //ngọc rồng namek
        if (ItemMapService.gI().isNamecBall(item.template.id) || ItemMapService.gI().isNamecBallStone(item.template.id)) {
            return NgocRongNamecService.gI().pickNamekBall(player, item);
        }
        if (addItemSpecial(player, item)) {
            return true;
        }
        //gold, gem, ruby
        switch (item.template.type) {
            case 9:
                int actualGold = item.quantity;
                if (player.inventory.gold + item.quantity <= Inventory.LIMIT_GOLD) {
                    if (player.effectSkill.isChibi && player.typeChibi == 0) {
                        player.inventory.gold += item.quantity;
                    }
                    if (player.nPoint.tlGold > 0) {
                        actualGold += (item.quantity * player.nPoint.tlGold / 100); // Tính vàng cộng thêm
                    }
                    if (player.playerIntrinsic.intrinsic.id == 23) {
                        player.inventory.gold += calPercent(actualGold, player.playerIntrinsic.intrinsic.param1);
                    } else {
                        player.inventory.gold += actualGold;
                    }

                    player.inventory.gold += actualGold;
                    Service.gI().sendMoney(player);
                    return true;
                } else {
                    Service.gI().sendThongBao(player, "Vàng sau khi nhặt quá giới hạn cho phép");
                    return false;
                }
            case 10:
                long gem = (long) player.inventory.gem + (long) item.quantity;
                if (gem > Integer.MAX_VALUE) {
                    gem = Integer.MAX_VALUE;
                }
                player.inventory.gem = (int) gem;
                Service.gI().sendMoney(player);
                return true;
            case 34:
                long ruby = (long) player.inventory.ruby + (long) item.quantity;
                if (ruby > Integer.MAX_VALUE) {
                    ruby = Integer.MAX_VALUE;
                }
                player.inventory.ruby = (int) ruby;
                Service.gI().sendMoney(player);
                return true;
        }

        if (item.template.id == 1627) {
            player.inventory.itemsBag.add(ItemService.gI().createItemNull());
            Service.gI().sendThongBaoOK(player, "Hành trang của bạn đã được mở rộng thêm 1 ô");
            return true;
        } else if (item.template.id == 517) {
            player.inventory.itemsBag.add(ItemService.gI().createItemNull());
            Service.gI().sendThongBaoOK(player, "Hành trang của bạn đã được mở rộng thêm 1 ô");
            return true;
        } else if (item.template.id == 518) {
            player.inventory.itemsBox.add(ItemService.gI().createItemNull());
            Service.gI().sendThongBaoOK(player, "Rương đồ của bạn đã được mở rộng thêm 1 ô");
            return true;
        }
        return addItemList(player.inventory.itemsBag, item);
    }

    public boolean addItemBox(Player player, Item item) {
        return addItemList(player.inventory.itemsBox, item);
    }

    public boolean addItemList(List<Item> items, Item itemAdd) {
        //nếu item ko có option, add option rỗng vào
        if (itemAdd.itemOptions.isEmpty()) {
            itemAdd.itemOptions.add(new Item.ItemOption(73, 0));
        }

        //item cộng thêm chỉ số param: tự động luyện tập - bí kiếp
        int[] idParam = isItemIncrementalOption(itemAdd);
        if (idParam[0] != -1) {
            for (Item it : items) {
                if (it.isNotNullItem() && it.template.id == itemAdd.template.id) {
                    for (Item.ItemOption io : it.itemOptions) {
                        if (io.optionTemplate.id == idParam[0]) {
                            io.param += idParam[1];
                        }
                    }
                    itemAdd.quantity = 0;
                    return true;
                }
            }
        }

        //item tăng số lượng
        if (itemAdd.template.isUpToUp) {
            for (Item it : items) {
                if (!it.isNotNullItem() || it.template.id != itemAdd.template.id || (!checkListsEqual(it.itemOptions, itemAdd.itemOptions) && itemAdd.template.id != 2074 && !itemAdd.isDaNangCap()) || it.quantity >= 100_000_000) {
                    continue;
                }

                //========================ITEM TĂNG SỐ LƯỢNG========================
                if (itemAdd.template.id == 1744 || itemAdd.template.id == 457|| itemAdd.template.id == 1741 || itemAdd.template.id == 1045||itemAdd.template.id == 1142) {
                    it.quantity += itemAdd.quantity;
                    itemAdd.quantity = 0;
                    return true;
                }

                if (it.quantity < 9999) {
                    int add = 9999 - it.quantity;
                    if (itemAdd.quantity <= add) {
                        it.quantity += itemAdd.quantity;
                        itemAdd.quantity = 0;
                        return true;
                    } else {
                        it.quantity = 9999;
                        itemAdd.quantity -= add;
                    }
                }
            }
        }

        //add item vào ô mới
        if (itemAdd.quantity > 0) {
            for (int i = 0; i < items.size(); i++) {
                if (!items.get(i).isNotNullItem()) {
                    items.set(i, ItemService.gI().copyItem(itemAdd));
                    itemAdd.quantity = 0;
                    return true;
                }
            }
        }
        return false;
    }

    public long calPercent(long param, int percent) {
        return param * percent / 100;
    }

    public static boolean checkListsEqual(List<ItemOption> list1, List<ItemOption> list2) {
        if (list1.size() != list2.size()) {
            return false;
        }

        for (int i = 0; i < list1.size(); i++) {
            if (list1.get(i).optionTemplate.id != list2.get(i).optionTemplate.id || list1.get(i).param != list2.get(i).param) {
                return false;
            }
        }

        return true;
    }

    private void __________________Kiểm_tra_điều_kiện_vật_phẩm______________() {
        //**********************************************************************
    }

    /**
     * Kiểm tra vật phẩm có phải là vật phẩm tăng chỉ số option hay không
     *
     * @param item
     * @return id option tăng chỉ số - param
     */
    private int[] isItemIncrementalOption(Item item) {
        for (Item.ItemOption io : item.itemOptions) {
            switch (io.optionTemplate.id) {
                case 1:
                    return new int[]{io.optionTemplate.id, io.param};
                case 31:
                    return new int[]{io.optionTemplate.id, io.param};
            }
        }
        return new int[]{-1, -1};
    }

    public Item findItemBagByTemp(Player player, int tempId) {
        return findItem(player.inventory.itemsBag, tempId);
    }

    private void __________________Kiểm_tra_danh_sách_còn_chỗ_trống_________() {
        //**********************************************************************
    }

    public byte getCountEmptyBag(Player player) {
        return getCountEmptyListItem(player.inventory.itemsBag);
    }

    public byte getCountEmptyListItem(List<Item> list) {
        byte count = 0;
        for (Item item : list) {
            if (!item.isNotNullItem()) {
                count++;
            }
        }
        return count;
    }

    public byte getIndexBag(Player pl, Item it) {
        for (byte i = 0; i < pl.inventory.itemsBag.size(); ++i) {
            Item item = pl.inventory.itemsBag.get(i);
            if (item != null && it.equals(item)) {
                return i;
            }
        }
        return -1;
    }

    public boolean finditemWoodChest(Player player) {
        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && item.template.id == 570) {
                return false;
            }
        }
        for (Item item : player.inventory.itemsBox) {
            if (item.isNotNullItem() && item.template.id == 570) {
                return false;
            }
        }
        return true;
    }

    public int getParam(Player player, int idoption, int itemID) {
        for (Item it : player.inventory.itemsBag) {
            if (it != null && it.itemOptions != null && it.isNotNullItem() && it.template.id == itemID) {
                for (ItemOption iop : it.itemOptions) {
                    if (iop.optionTemplate.id == idoption) {
                        return iop.param;
                    }
                }
            }
        }
        return 0;
    }

    public void subParamItemsBag(Player player, int itemID, int idoption, int param) {
        Item itemRemove = null;
        for (Item it : player.inventory.itemsBag) {
            if (it != null && it.template.id == itemID) {
                for (ItemOption op : it.itemOptions) {
                    if (op != null && op.optionTemplate.id == idoption) {
                        op.param -= param;
                        if (op.param <= 0) {
                            itemRemove = it;
                        }
                        break;
                    }
                }
                break;
            }
        }
        if (itemRemove != null) {
            removeItem(player.inventory.itemsBag, itemRemove);
        }
    }

    public boolean findItem(Player player, int id) {
        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && item.template.id == id) {
                return true;
            }
        }
        for (Item item : player.inventory.itemsBox) {
            if (item.isNotNullItem() && item.template.id == id) {
                return true;
            }
        }
        return false;
    }

    public boolean findItemBongTaiCap2(Player player) {
        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && item.template.id == 921) {
                return true;
            }
        }
        for (Item item : player.inventory.itemsBox) {
            if (item.isNotNullItem() && item.template.id == 921) {
                return true;
            }
        }
        return false;
    }

    public boolean findItemSkinQuyLaoKame(Player player) {
        for (Item item : player.inventory.itemsBody) {
            if (item.isNotNullItem() && item.template.id == 710) {
                return true;
            }
        }
        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && item.template.id == 710) {
                return true;
            }
        }
        for (Item item : player.inventory.itemsBox) {
            if (item.isNotNullItem() && item.template.id == 710) {
                return true;
            }
        }
        return false;
    }

    public boolean findItemNTK(Player player) {
        if (player.isPl()) {
            for (Item item : player.inventory.itemsBag) {
                if (item.isNotNullItem() && item.template.id == 992) {
                    return true;
                }
            }
            for (Item item : player.inventory.itemsBox) {
                if (item.isNotNullItem() && item.template.id == 992) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean findItemTVC(Player player) {
        if (player.isPl()) {
            for (Item item : player.inventory.itemsBag) {
                if (item.isNotNullItem() && item.template.id == 2077) {
                    return true;
                }
            }
            for (Item item : player.inventory.itemsBox) {
                if (item.isNotNullItem() && item.template.id == 2077) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean findItemTatVoGiangSinh(Player player) {
        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && item.template.id == 649) {
                return true;
            }
        }
        return false;
    }

    public boolean fullSetThan(Player player) {
        for (int i = 0; i < 5; i++) {
            Item item = player.inventory.itemsBody.get(i);
            if (item == null || item.template == null || item.template.level != 13) {
                return false;
            }
        }
        return true;
    }

    public boolean fullSetThienSu(Player player) {
        for (int i = 0; i < 5; i++) {
            Item item = player.inventory.itemsBody.get(i);
            if (item == null || item.template == null || item.template.level != 15) {
                return false;
            }
        }
        return true;
    }

    public boolean x99ThucAn(Player player) {
        Item doAn = player.inventory.itemsBag.stream().filter(it -> it != null && it.template != null && (it.template.id == 663 || it.template.id == 664 || it.template.id == 665 || it.template.id == 666 || it.template.id == 667) && it.quantity >= 99).findFirst().orElse(null);
        return doAn != null;
    }

    public boolean canOpenBillShop(Player player) {
        return fullSetThan(player) && x99ThucAn(player);
    }

    public boolean canOpenBerryShop(Player player) {
        return fullSetThienSu(player);
    }

    public boolean optionCanUpgrade(int id) {
        return id == 0 || id == 22 || id == 23 || id == 14 || id == 27 || id == 28 || id == 47;
    }

    public int getIndexItem(Player player, List<Item> items, Item item) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i) == item) {
                return i;
            }
        }
        return -1;
    }

    public int getIndexItemBag(Player player, Item item) {
        return getIndexItem(player, player.inventory.itemsBag, item);
    }

    public int getIndexItemBody(Player player, Item item) {
        return getIndexItem(player, player.inventory.itemsBody, item);
    }

    public int getIndexItemBox(Player player, Item item) {
        return getIndexItem(player, player.inventory.itemsBox, item);
    }

    public void addQuantityItemsBag(Player player, Item item, int quantity) {
        addQuantityItem(player.inventory.itemsBag, item, quantity);
    }

    public void addQuantityItem(List<Item> items, Item item, int quantity) {
        if (item != null) {
            for (Item it : items) {
                if (item.equals(it)) {
                    it.quantity += quantity;
                    break;
                }
            }
        }
    }

    private void handleOption210(Item item) {
        for (int i = 0; i < item.itemOptions.size(); i++) {
            Item.ItemOption io = item.itemOptions.get(i);
            if (io.optionTemplate.id == 210) {
                int option210Index = i;
                item.itemOptions.remove(i);
                int numberOfOptionsToAdd = io.param;
                int[] allOptions = {8, 14, 108, 94, 108, 16, 80, 81, 97, 100, 101, 104, 106};
                List<Integer> selectedOptions = new ArrayList<>();
                while (selectedOptions.size() < numberOfOptionsToAdd && selectedOptions.size() < allOptions.length) {
                    int randomIndex = (int) (Math.random() * allOptions.length);
                    int selectedOption = allOptions[randomIndex];
                    if (!selectedOptions.contains(selectedOption)) {
                        selectedOptions.add(selectedOption);
                    }
                }
                List<Item.ItemOption> newOptions = new ArrayList<>();
                for (int option : selectedOptions) {
                    int newParam = 0;
                    if (option == 8 || option == 14 || option == 108 || option == 94 || option == 108) {
                        newParam = 3 + (int) (Math.random() * 3);
                    } else if (option == 16 || option == 80 || option == 81 || option == 97 || option == 100 || option == 101 || option == 104) {
                        newParam = 10 + (int) (Math.random() * 16);
                    } else if (option == 106) {
                        newParam = 0;
                    }
                    newOptions.add(new Item.ItemOption(option, newParam));
                }
                item.itemOptions.addAll(option210Index, newOptions);
                break;
            }
        }
    }

    private void checkOption231(Item item) {
        for (int i = 0; i < item.itemOptions.size(); i++) {
            Item.ItemOption io = item.itemOptions.get(i);
            if (io.optionTemplate.id == 231) {
                item.itemOptions.remove(i);
                double randomValue = Math.random();
                if (randomValue <= 0.99) {
                    int[] validParams = {3, 7, 15, 21};
                    int selectedParam = validParams[(int) (Math.random() * validParams.length)];
                    item.itemOptions.add(new Item.ItemOption(93, selectedParam));
                }
                break;
            }
        }
    }

    public boolean findItemBongTai(Player player) {
        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && (item.template.id == 921 || item.template.id == 454)) {
                return true;
            }
        }
        for (Item item : player.inventory.itemsBox) {
            if (item.isNotNullItem() && (item.template.id == 921 || item.template.id == 454)) {
                return true;
            }
        }
        return false;
    }
}
