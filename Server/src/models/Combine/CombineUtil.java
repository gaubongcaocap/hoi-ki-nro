package models.Combine;

import item.Item;

public class CombineUtil {

    //_______________________________PHA_LÊ_HÓA_______________________________
    public static int pointUp(int n, int iters) {
        for (int i = 0; i < iters; i++) {
            n += Math.max(n / 10, 1);
        }
        return n;
    }

    public static int reversePoint(int n, int iters) {
        int[] interValues = new int[iters + 1];
        interValues[iters] = n;
        for (int i = iters; i > 0; i--) {
            int prevValue = interValues[i];
            int subValue = prevValue / 11;
            interValues[i - 1] = prevValue - subValue;
        }
        return interValues[0];
    }

    public static boolean isTrangBiPhaLeHoa(Item item) {
        if (item != null && item.isNotNullItem()) {
            return item.template.type < 5 || item.template.type == 32;
        } else {
            return false;
        }
    }

    public static boolean isDaPhaLe(Item item) {
        return item != null && (item.template.type == 30 || (item.template.id >= 14 && item.template.id <= 20));
    }

    public static boolean isTrangBiDacCau(Item item) {
        if (item != null && item.isNotNullItem()) {
            for (Item.ItemOption io : item.itemOptions) {
                if (io.optionTemplate.id == 220) {
                    return true;
                }
            }
        }
        return false;
    }

    public static int soLanNangCapConLai(Item item) {
        if (item != null && item.isNotNullItem()) {
            for (Item.ItemOption io : item.itemOptions) {
                if (io.optionTemplate.id == 221) {
                    return io.param;
                }
            }
        }
        return 0;
    }

    public static int getGemEpSao(int star) {
        switch (star) {
            case 0 -> {
                return 1;
            }
            case 1 -> {
                return 2;
            }
            case 2 -> {
                return 5;
            }
            case 3 -> {
                return 10;
            }
            case 4 -> {
                return 25;
            }
            case 5 -> {
                return 50;
            }
            case 6 -> {
                return 100;
            }
        }
        return 0;
    }

    public static int getOptionDaPhaLe(Item daPhaLe) {
        if (daPhaLe.template.type == 30) {
            return daPhaLe.itemOptions.get(0).optionTemplate.id;
        }
        return switch (daPhaLe.template.id) {
            case 20 ->
                77;
            case 19 ->
                103;
            case 18 ->
                80;
            case 17 ->
                81;
            case 16 ->
                50;
            case 15 ->
                94;
            case 14 ->
                108;
            default ->
                -1;
        };
    }

    public static int getParamDaPhaLe(Item daPhaLe) {
        if (daPhaLe.template.type == 30) {
            return daPhaLe.itemOptions.get(0).param;
        }
        return switch (daPhaLe.template.id) {
            case 20 ->
                5;
            case 19 ->
                5;
            case 18 ->
                5;
            case 17 ->
                5;
            case 16 ->
                3;
            case 15 ->
                2;
            case 14 ->
                2;
            default ->
                -1;
        };
    }

    public static int getGoldPhaLeHoa(int star) {
        switch (star) {
            case 0:
                return 5000000;
            case 1:
                return 10000000;
            case 2:
                return 20000000;
            case 3:
                return 30000000;
            case 4:
                return 40000000;
            case 5:
                return 50000000;
            case 6:
                return 60000000;
            case 7:
                return 70000000;
            case 8:
                return 80000000;
            case 9:
                return 90000000;
            case 10:
                return 100000000;
            case 11:
                return 110000000;
            case 12:
                return 120000000;
        }
        return 0;
    }

    public static float getRatioPhaLeHoa(int star) {
        switch (star) {
            case 0:
                return 80f;
            case 1:
                return 50f;
            case 2:
                return 30f;
            case 3:
                return 20f;
            case 4:
                return 10f;
            case 5:
                return 5f;
            case 6:
                return 2f;
            case 7:
                return 1.5f;
            case 8:
                return 1f;
            case 9:
                return 0.5f;
            case 10:
                return 0.2f;
            case 11:
                return 0.1f;
            case 12:
                return 0.1f;
        }

        return 0;
    }

    public static int getGemPhaLeHoa(int star) {
        switch (star) {
            case 0:
                return 5;
            case 1:
                return 10;
            case 2:
                return 20;
            case 3:
                return 30;
            case 4:
                return 40;
            case 5:
                return 50;
            case 6:
                return 60;
            case 7:
                return 70;
            case 8:
                return 140;
            case 9:
                return 280;
            case 10:
                return 560;
            case 11:
                return 1120;
            case 12:
                return 2240;
        }
        return 0;
    }

    public static boolean isCoupleItemNangCapCheck(Item trangBi, Item daNangCap) {
        if (trangBi != null && daNangCap != null) {
            if (trangBi.template.type == 0 && daNangCap.template.id == 223) {
                return true;
            } else if (trangBi.template.type == 1 && daNangCap.template.id == 222) {
                return true;
            } else if (trangBi.template.type == 2 && daNangCap.template.id == 224) {
                return true;
            } else if (trangBi.template.type == 3 && daNangCap.template.id == 221) {
                return true;
            } else {
                return trangBi.template.type == 4 && daNangCap.template.id == 220;
            }
        } else {
            return false;
        }
    }

    public static int getCountDaNangCapDo(int level) {
        switch (level) {
            case 0:
                return 3;
            case 1:
                return 7;
            case 2:
                return 11;
            case 3:
                return 17;
            case 4:
                return 23;
            case 5:
                return 35;
            case 6:
                return 50;
            case 7:
                return 70;
        }
        return 0;
    }

    public static int getCountDaBaoVe(int level) {
        return 1;
    }

    public static int getGoldNangCapDo(int level) {
        switch (level) {
            case 0:
                return 10000;
            case 1:
                return 70000;
            case 2:
                return 300000;
            case 3:
                return 1500000;
            case 4:
                return 7000000;
            case 5:
                return 23000000;
            case 6:
                return 100000000;
            case 7:
                return 250000000;
        }
        return 0;
    }

    public static double getTileNangCapDo(int level) {
        switch (level) {
            case 0:
                return 80;
            case 1:
                return 50;
            case 2:
                return 20;
            case 3:
                return 10;
            case 4:
                return 7;
            case 5:
                return 5;
            case 6:
                return 1;
            case 7: // 7 sao
                return 0.3;
            case 8:
                return 5;
            case 9:
                return 1;
            case 10: // 7 sao
                return 0.3;
            case 11: // 7 sao
                return 0.3;
            case 12: // 7 sao
                return 0.3;
        }
        return 0;
    }

    public static double getTileNangChanMenh(int id) {
        switch (id) {
            case 2139:
                return 80;
            case 2140:
                return 70;
            case 2141:
                return 60;
            case 2142:
                return 50;
            case 2143:
                return 40;
            case 2144:
                return 30;
            case 2145:
                return 20;
            case 2146:
                return 10;
        }
        return 0;
    }
}
