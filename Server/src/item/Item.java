package item;

import models.Template;
import models.Template.ItemTemplate;
import services.ItemService;
import utils.Util;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import models.Combine.CombineUtil;

public class Item {

    public ItemTemplate template;

    public String info;

    public String content;

    public int quantity;

    public int quantityGD = 0;

    public List<ItemOption> itemOptions;

    public long createTime;

    public boolean isNotNullItem() {
        return this.template != null;
    }

    public Item() {
        this.itemOptions = new ArrayList<>();
        this.createTime = System.currentTimeMillis();
    }

    public Item(short itemId) {
        this.template = ItemService.gI().getTemplate(itemId);
        this.itemOptions = new ArrayList<>();
        this.createTime = System.currentTimeMillis();
    }

    public String getInfo() {
        String strInfo = "";
        for (ItemOption itemOption : itemOptions) {
            strInfo += itemOption.getOptionString();
        }
        return strInfo;
    }

    public String getContent() {
        return "Yêu cầu sức mạnh " + this.template.strRequire + " trở lên";
    }

    public void dispose() {
        this.template = null;
        this.info = null;
        this.content = null;
        if (this.itemOptions != null) {
            for (ItemOption io : this.itemOptions) {
                io.dispose();
            }
            this.itemOptions.clear();
        }
        this.itemOptions = null;
    }

    public boolean isBUg() {
        for (ItemOption itemOption : itemOptions) {
            if ((itemOption.optionTemplate.id != 249 && (itemOption.optionTemplate.id == 50 || itemOption.optionTemplate.id == 77 || itemOption.optionTemplate.id == 103 || itemOption.optionTemplate.id == 5))
                    && itemOption.param > 35) {

                return true;
            }
        }
        return false;
    }

    public static class ItemOption {

        public int param;

        public Template.ItemOptionTemplate optionTemplate;

        public ItemOption() {
        }

        public ItemOption(ItemOption io) {
            this.param = io.param;
            this.optionTemplate = io.optionTemplate;
        }

        public ItemOption(int tempId, int param) {
            this.optionTemplate = ItemService.gI().getItemOptionTemplate(tempId);
            this.param = param;
        }

        public ItemOption(Template.ItemOptionTemplate temp, int param) {
            this.optionTemplate = temp;
            this.param = param;
        }

        public String getOptionString() {
            return Util.replace(this.optionTemplate.name, "#", String.valueOf(this.param));
        }

        public boolean isOptionCanUpgrade() {
            int opId = this.optionTemplate.id;
            return opId == 0 || opId == 6 || opId == 7 || opId == 14 || opId == 22 || opId == 23 || opId == 27 || opId == 28 || opId == 47;
        }

        public void dispose() {
            this.optionTemplate = null;
        }

        @Override
        public String toString() {
            final String n = "\"";
            return "{"
                    + n + "id" + n + ":" + n + optionTemplate.id + n + ","
                    + n + "param" + n + ":" + n + param + n
                    + "}";
        }
    }

    public boolean isSKH() {
        for (ItemOption itemOption : itemOptions) {
            if (itemOption.optionTemplate.id >= 127 && itemOption.optionTemplate.id <= 135) {
                return true;
            }
        }
        return false;
    }

    public boolean isDTS() {
        return this.template.level == 15;
    }

    public boolean isVND() {
        if (isNotNullItem()) {
            return this.template.type == 38;
        }
        return false;
    }

    public boolean isDTL() {
        return this.template.level == 13;
    }

    public boolean isDHD() {
        return this.template.level == 14;
    }

    public boolean isManhThienSu() {
        return this.template.id >= 1066 && this.template.id <= 1070;
    }

    public boolean isDaMayMan() {
        return this.template.id >= 1079 && this.template.id <= 1083;
    }

    public boolean isDaNangCapTS() {
        return this.template.id >= 1074 && this.template.id <= 1078;
    }

    public boolean isCongThuc() {
        return this.template.id >= 1071 && this.template.id <= 1073;
    }

    public boolean isCongThucVip() {
        return this.template.id >= 1084 && this.template.id <= 1086;
    }

    public boolean isDaNangCap() {
        return this.template.type == 14;
    }

    public String typeName() {
        return switch (this.template.type) {
            case 0 ->
                "Áo";
            case 1 ->
                "Quần";
            case 2 ->
                "Găng";
            case 3 ->
                "Giày";
            case 4 ->
                "Rada";
            default ->
                "";
        };
    }

    public String getGenderName() {
        return template.gender == 0 ? "Trái Đất" : template.gender == 1 ? "Namếc" : "Xay da";
    }

    public byte typeManh() {
        return switch (this.template.id) {
            case 1066 ->
                0;
            case 1067 ->
                1;
            case 1070 ->
                2;
            case 1068 ->
                3;
            case 1069 ->
                4;
            default ->
                -1;
        };
    }

    public boolean isSachTuyetKy() {
        return template.id == 1044 || template.id == 1211 || template.id == 1212;
    }

    public boolean isSachTuyetKy2() {
        return template.id >= 1278 && template.id <= 1280;
    }

    public boolean canNangCapWithNDC(Item daNangCap) {
        if (this.template.type == 0 && daNangCap.template.id == 223) {
            return true;
        } else if (this.template.type == 1 && daNangCap.template.id == 222) {
            return true;
        } else if (this.template.type == 2 && daNangCap.template.id == 224) {
            return true;
        } else if (this.template.type == 3 && daNangCap.template.id == 221) {
            return true;
        } else {
            return this.template.type == 4 && daNangCap.template.id == 220;
        }
    }

    public boolean isDaPhaLeEpSao() {
        return template != null && (template.type == 30 || (template.id >= 14 && template.id <= 20));
    }

    public boolean isDaPhaLeC1() {
        return template != null && template.id >= 411 && template.id <= 447;
    }

    public boolean isDaPhaLeC2() {
        return template != null && template.id >= 1416 && template.id <= 1422 || template.id == 964 || template.id == 965;
    }

    public boolean isDaPhaLeMoi() {
        return template != null && template.id >= 1416 && template.id <= 1422 || template.id == 964 || template.id == 965
                || template.id >= 1426 && template.id <= 1434;
    }

    public boolean isNgocRongVIP() {
        return template != null && template.id >= 1787 && template.id <= 1793 ;
    }

    public boolean isDaPhaLeCu() {
        return template != null && template.id >= 441 && template.id <= 447;
    }

    public boolean isTypeBody() {
        return template != null && (0 <= template.type && template.type < 6) || template.type == 32 || template.type == 35 || template.type == 11 || template.type == 23;
    }

    public boolean isHaveOption(int id) {
        for (int i = 0; i < this.itemOptions.size(); i++) {
            ItemOption itemOption = this.itemOptions.get(i);
            if (itemOption != null && itemOption.optionTemplate.id == id) {
                return true;
            }
        }
        return false;
    }

    public int getPercentOption() {
        int percent = 0;
        switch (this.template.type) {
            case 0 -> {
                int paramZin = ItemService.gI().getOptionParamItemShop(this.template.id, 47);
                int param = CombineUtil.reversePoint(getOptionParam(47), getOptionParam(72));
                percent = (param * 100) / paramZin;
            }
            case 1 -> {
                int paramZin = ItemService.gI().getOptionParamItemShop(this.template.id, 6);
                int param = CombineUtil.reversePoint(getOptionParam(6), getOptionParam(72));
                percent = (param * 100) / paramZin;
            }
            case 2 -> {
                int paramZin = ItemService.gI().getOptionParamItemShop(this.template.id, 0);
                int param = CombineUtil.reversePoint(getOptionParam(0), getOptionParam(72));
                percent = (param * 100) / paramZin;
            }
            case 3 -> {
                int paramZin = ItemService.gI().getOptionParamItemShop(this.template.id, 7);
                int param = CombineUtil.reversePoint(getOptionParam(7), getOptionParam(72));
                percent = (param * 100) / paramZin;
            }
            case 4 -> {
                int paramZin = ItemService.gI().getOptionParamItemShop(this.template.id, 14);
                int param = CombineUtil.reversePoint(getOptionParam(14), getOptionParam(72));
                percent = (param * 100) / paramZin;
            }
        }
        return percent;
    }

    public int getOptionParam(int id) {
        for (int i = 0; i < this.itemOptions.size(); i++) {
            ItemOption itemOption = this.itemOptions.get(i);
            if (itemOption != null && itemOption.optionTemplate.id == id) {
                return itemOption.param;
            }
        }
        return 0;
    }

    public void addOptionParam(int id, int param) {
        for (int i = 0; i < this.itemOptions.size(); i++) {
            ItemOption itemOption = this.itemOptions.get(i);
            if (itemOption != null && itemOption.optionTemplate.id == id) {
                itemOption.param += param;
                return;
            }
        }
        this.itemOptions.add(new ItemOption(id, param));
    }

    public void subOptionParam(int id, int param) {
        for (int i = 0; i < this.itemOptions.size(); i++) {
            ItemOption itemOption = this.itemOptions.get(i);
            if (itemOption != null && itemOption.optionTemplate.id == id) {
                itemOption.param -= param;
                return;
            }
        }
    }

    public void subOptionParamAndRemoveIfZero(int id, int param) {
        for (int i = 0; i < this.itemOptions.size(); i++) {
            ItemOption itemOption = this.itemOptions.get(i);
            if (itemOption != null && itemOption.optionTemplate.id == id) {
                itemOption.param -= param;
                if (param <= 0) {
                    this.itemOptions.remove(i);
                }
                break;
            }
        }
    }

    public void removeOption(int id) {
        for (int i = 0; i < this.itemOptions.size(); i++) {
            ItemOption itemOption = this.itemOptions.get(i);
            if (itemOption != null && itemOption.optionTemplate.id == id) {
                this.itemOptions.remove(i);
                break;
            }
        }
    }

    public ItemOption getOptionDaPhaLe() {
        return switch (template.id) {
            case 20 ->
                new ItemOption(77, 5);
            case 19 ->
                new ItemOption(103, 5);
            case 18 ->
                new ItemOption(80, 5);
            case 17 ->
                new ItemOption(81, 5);
            case 16 ->
                new ItemOption(50, 3);
            case 15 ->
                new ItemOption(94, 2);
            case 14 ->
                new ItemOption(108, 2);

            case 441 ->
                new ItemOption(95, 5);
            case 442 ->
                new ItemOption(96, 5);
            case 443 ->
                new ItemOption(97, 5);
            case 444 ->
                new ItemOption(98, 5);
            case 445 ->
                new ItemOption(99, 5);
            case 446 ->
                new ItemOption(100, 5);
            case 447 ->
                new ItemOption(101, 5);

            case 1416 ->
                new ItemOption(95, 5);
            case 1417 ->
                new ItemOption(96, 5);
            case 1418 ->
                new ItemOption(97, 5);
            case 1419 ->
                new ItemOption(98, 5);
            case 1420 ->
                new ItemOption(99, 5);
            case 1421 ->
                new ItemOption(100, 5);
            case 1422 ->
                new ItemOption(101, 5);

            case 1426 ->
                new ItemOption(95, 5);
            case 1427 ->
                new ItemOption(96, 5);
            case 1428 ->
                new ItemOption(97, 5);
            case 1429 ->
                new ItemOption(98, 5);
            case 1430 ->
                new ItemOption(99, 5);
            case 1431 ->
                new ItemOption(100, 5);
            case 1432 ->
                new ItemOption(101, 5);
            case 1433 ->
                new ItemOption(153, 5);
            case 1434 ->
                new ItemOption(160, 5);

            case 1787 ->
                new ItemOption(108, 4);
            case 1788 ->
                new ItemOption(94, 4);
            case 1789 ->
                new ItemOption(50, 6);
            case 1790 ->
                new ItemOption(81, 10);
            case 1791 ->
                new ItemOption(80, 10);
            case 1792 ->
                new ItemOption(103, 10);
            case 1793 ->
                new ItemOption(77, 10);
            default ->
                itemOptions.get(0);
        };
    }

    public String getOptionInfo(Item item) {
        boolean haveOption = false;
        StringJoiner optionInfo = new StringJoiner("\n");
        Item itC = this.cloneItem();
        ItemOption iodpl = item.getOptionDaPhaLe();
        for (ItemOption io : itC.itemOptions) {
            if (!haveOption && io.optionTemplate.id == iodpl.optionTemplate.id) {
                io.param += iodpl.param;
                haveOption = true;
            }
            if (io.optionTemplate.id != 72 && io.optionTemplate.id != 73 && io.optionTemplate.id != 102 && io.optionTemplate.id != 107) {
                optionInfo.add(io.getOptionString());
            }
        }
        if (!haveOption) {
            optionInfo.add(iodpl.getOptionString());
        }
        itC.dispose();
        return optionInfo.toString();
    }

    public String getOptionInfoCuongHoa(Item item) {
        StringJoiner optionInfo = new StringJoiner("\n");
        Item itC = this.cloneItem();
        ItemOption iodpl = item.getOptionDaPhaLe();
        for (ItemOption io : itC.itemOptions) {
            if (io.optionTemplate.id != 72 && io.optionTemplate.id != 73 && io.optionTemplate.id != 102 && io.optionTemplate.id != 107 && io.optionTemplate.id != 218) {
                optionInfo.add(io.getOptionString());
            }
        }
        optionInfo.add(iodpl.getOptionString());
        itC.dispose();
        return optionInfo.toString();
    }

    public String getOptionInfoChuyenHoa(Item item, int level) {
        StringJoiner optionInfo = new StringJoiner("\n");
        Item itC = this.cloneItem();
        int percent = item.getPercentOption();
        for (ItemOption io : itC.itemOptions) {
            if (io.isOptionCanUpgrade()) {
                io.param = CombineUtil.pointUp(io.param * percent / 100, level);
            }
            if (io.optionTemplate.id != 72 && io.optionTemplate.id != 73 && io.optionTemplate.id != 102 && io.optionTemplate.id != 107 && io.optionTemplate.id != 218) {
                optionInfo.add(io.getOptionString());
            }
        }
        for (ItemOption io : item.itemOptions) {
            if (!io.isOptionCanUpgrade() && io.optionTemplate.id != 72 && io.optionTemplate.id != 73 && io.optionTemplate.id != 102 && io.optionTemplate.id != 107 && io.optionTemplate.id != 218) {
                optionInfo.add(io.getOptionString());
            }
        }
        itC.dispose();
        return optionInfo.toString();
    }

    public String getOptionInfo() {
        StringJoiner optionInfo = new StringJoiner("\n");
        for (ItemOption io : this.itemOptions) {
            if (io.optionTemplate.id != 72 && io.optionTemplate.id != 73 && io.optionTemplate.id != 102 && io.optionTemplate.id != 107 && io.optionTemplate.id != 218) {
                optionInfo.add(io.getOptionString());
            }
        }
        return optionInfo.toString();
    }

    public String getOptionInfoUpgrade() {
        StringJoiner optionInfo = new StringJoiner("\n");
        for (ItemOption io : this.itemOptions) {
            if (io.isOptionCanUpgrade() || io.optionTemplate.id == 21 || io.param == 30 && io.optionTemplate.id != 218) {
                optionInfo.add(io.getOptionString());
            }
        }
        return optionInfo.toString();
    }

    public boolean haveOption(int idOption) {
        if (this != null && this.isNotNullItem()) {
            return this.itemOptions.stream().anyMatch(op -> op != null && op.optionTemplate.id == idOption);
        }
        return false;
    }

    public boolean isTrangBiPSH() {
        if (this.template.type == 21 || this.template.type == 11 || this.template.type == 25 || this.template.type == 72) {
            return true;
        }

        return false;
    }

    public boolean isTrangBiHSD() {
        for (ItemOption itemOption : itemOptions) {
            if (itemOption.optionTemplate.id == 93 && itemOption.param >= 0) {

                return true;
            }
        }
        return false;
    }

    public boolean isTrangBiKhoaGd() {
        if (this.template.type == 11 || this.template.id != 457 || this.template.type == 30 || this.template.type == 12 || this.template.type == 21 || this.template.type == 27 || this.template.type == 72) {
            return true;
        }

        return false;
    }

    public boolean isTrangBiHacHoa() {
        if (this.template.type <= 5 || this.template.type == 32 || this.template.type == 21 || this.template.type == 23 || this.template.type == 23 || this.template.type == 11 || this.template.type == 72) {
            return true;
        }

        return false;
    }

    public String getOptionInfoUpgradeFinal() {
        StringJoiner optionInfo = new StringJoiner("\n");
        Item clone = this.cloneItem();
        for (ItemOption io : clone.itemOptions) {
            if (io.isOptionCanUpgrade()) {
                io.param = CombineUtil.pointUp(io.param, 1);
            }
            if (io.isOptionCanUpgrade() || io.param == 30) {
                optionInfo.add(io.getOptionString());
            }
        }
        return optionInfo.toString();
    }

    public boolean canPhaLeHoa() {
        return this.template != null && (this.template.type < 5 || this.template.type == 32);
    }

    public Item cloneItem() {
        Item item = new Item();
        item.itemOptions = new ArrayList<>();
        item.template = this.template;
        item.info = this.info;
        item.content = this.content;
        item.quantity = this.quantity;
        item.createTime = this.createTime;
        for (Item.ItemOption io : this.itemOptions) {
            item.itemOptions.add(new Item.ItemOption(io));
        }
        return item;
    }
}
