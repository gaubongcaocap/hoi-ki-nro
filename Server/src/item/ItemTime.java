package item;

/*
 *
 *
 * @author NTD
 */
import player.NPoint;
import player.Player;
import services.Service;
import utils.Util;
import services.ItemTimeService;

public class ItemTime {

    //id item text
    public static final byte DOANH_TRAI = 0;
    public static final byte BAN_DO_KHO_BAU = 1;
    public static final byte CON_DUONG_RAN_DOC = 2;
    public static final byte KHI_GAS_HUY_DIET = 3;
    public static final byte TIME_KEO_BUA_BAO = 4;
    public static final byte TEXT_NHAN_BUA_MIEN_PHI = 5;

    public static final int TIME_ITEM = 600000;
    public static final int TIME_OPEN_POWER = 8640000;
    public static final int TIME_MAY_DO = 1800000;
    public static final int TIME_MAY_DO2 = 1800000;
    public static final int TIME_EAT_MEAL = 600000;
    public static final int TIME_EAT_MEAL3 = 1200000;
    public static final int TIME_CMS = 3600000;
    public static final int TIME_DK = 1800000;
    public static final int TIME_RK = 3600000;
    public static final int TIME_NCD = 1800000;
    public static final int TIME_GIANGHOA = 30000;
    public static final int TIME_TD = 1800000;
    public static final int TIME_10P = 600000;
    public static final int TIME_30P = 1800000;

    private Player player;

    public boolean isUseBoHuyet;
    public boolean isUseBoKhi;
    public boolean isUseGiapXen;
    public boolean isUseCuongNo;
    public boolean isUseAnDanh;
    public boolean isUseBoHuyet2;
    public boolean isUseBoKhi2;
    public boolean isUseGiapXen2;
    public boolean isUseCuongNo2;
    public boolean isUseAnDanh2;

    public boolean isUserBanhChung;

    public boolean isUseBuax2DeTu;

    public long lastTimeBoHuyet;
    public long lastTimeBoKhi;
    public long lastTimeGiapXen;
    public long lastTimeCuongNo;
    public long lastTimeAnDanh;

    public long lastTimeBanhChung;

    public long lastTimeBuax2DeTu;

    public long lastTimeBoHuyet2;
    public long lastTimeBoKhi2;
    public long lastTimeGiapXen2;
    public long lastTimeCuongNo2;
    public long lastTimeAnDanh2;

    public boolean isUseMayDo;
    public long lastTimeUseMayDo;
    public boolean isUseMayDo2;
    public long lastTimeUseMayDo2;

    public boolean isOpenPower;
    public long lastTimeOpenPower;

    public boolean isUseTDLT;
    public long lastTimeUseTDLT;
    public int timeTDLT;

    public boolean isUseRX;
    public long lastTimeUseRX;
    public int timeRX;

    public boolean isUseCMS;
    public long lastTimeUseCMS;

    public boolean isUseNCD;
    public long lastTimeUseNCD;

    public boolean isUseGTPT;
    public long lastTimeUseGTPT;

    public boolean isUseDK;
    public long lastTimeUseDK;
    public int iconDK;

    public boolean isEatMeal;
    public long lastTimeEatMeal;
    public int iconMeal;

    public boolean isEatMeal2;
    public long lastTimeEatMeal2;

    public boolean isEatMeal3;
    public long lastTimeEatMeal3;

    public boolean isEatThuocMo;
    public long lastTimeEatThuocMo;
    public int iconThuocMo;

    public boolean isEatThuocMo2;
    public long lastTimeEatThuocMo2;
    public int iconThuocMo2;

    public boolean isDrinkBiaTiger;
    public long lastTimeDrinkBiaTiger;

    public boolean isDrinkBia333;
    public long lastTimeDrinkBia333;

    public boolean isDrinkHeniken;
    public long lastTimeDrinkHeniken;

    public boolean isEatDuoiKhiXanh;
    public long lastTImeEatDuoiKhiXanh;
    public int iconDKXanh;

    public int iconMeal2;
    public int iconMeal3;
    public long lastTimeKhauTrang;
    public boolean isUseKhauTrang;
    public long lastTimeLoX2;
    public boolean isUseLoX2;
    public long lastTimeLoX5;
    public boolean isUseLoX5;
    public long lastTimeLoX7;
    public boolean isUseLoX7;
    public long lastTimeLoX10;
    public boolean isUseLoX10;
    public long lastTimeLoX15;
    public boolean isUseLoX15;
    public boolean checkLoXTNSM;

    public ItemTime(Player player) {
        this.player = player;
    }

    public void update() {
        if (isUseLoX2) {
            if (Util.canDoWithTime(lastTimeLoX2, TIME_30P)) {
                isUseLoX2 = false;
                checkLoXTNSM = true;
                Service.gI().point(player);
            }
        }
        if (isUseLoX5) {
            if (Util.canDoWithTime(lastTimeLoX5, TIME_30P)) {
                isUseLoX5 = false;
                checkLoXTNSM = true;
                Service.gI().point(player);
            }
        }
        if (isUseLoX7) {
            if (Util.canDoWithTime(lastTimeLoX7, TIME_30P)) {
                isUseLoX7 = false;
                checkLoXTNSM = true;
                Service.gI().point(player);
            }
        }
        if (isUseLoX10) {
            if (Util.canDoWithTime(lastTimeLoX10, TIME_30P)) {
                isUseLoX10 = false;
                checkLoXTNSM = true;
                Service.gI().point(player);
            }
        }
        if (isUseLoX15) {
            if (Util.canDoWithTime(lastTimeLoX15, TIME_30P)) {
                isUseLoX15 = false;
                checkLoXTNSM = true;
                Service.gI().point(player);
            }
        }
        if (isUseKhauTrang) {
            if (Util.canDoWithTime(lastTimeKhauTrang, TIME_10P)) {
                isUseKhauTrang = false;
                Service.gI().point(player);
            }
        }
        if (isUseBuax2DeTu) {
            if (Util.canDoWithTime(lastTimeBuax2DeTu, TIME_30P)) {
                isUseBuax2DeTu = false;
                Service.gI().point(player);
            }
        }
        if (isEatMeal) {
            if (Util.canDoWithTime(lastTimeEatMeal, TIME_EAT_MEAL)) {
                isEatMeal = false;
                Service.gI().point(player);
            }
        }
        if (isEatMeal2) {
            if (Util.canDoWithTime(lastTimeEatMeal2, TIME_EAT_MEAL)) {
                isEatMeal2 = false;
                Service.gI().point(player);
            }
        }
        if (isEatMeal3) {
            if (Util.canDoWithTime(lastTimeEatMeal3, TIME_EAT_MEAL3)) {
                isEatMeal3 = false;
                Service.gI().point(player);
            }
        }
        if (isUseBoHuyet) {
            if (Util.canDoWithTime(lastTimeBoHuyet, TIME_ITEM)) {
                isUseBoHuyet = false;
                Service.gI().point(player);
//                Service.gI().Send_Info_NV(this.player);
            }
        }

        if (isUseBoKhi) {
            if (Util.canDoWithTime(lastTimeBoKhi, TIME_ITEM)) {
                isUseBoKhi = false;
                Service.gI().point(player);
            }
        }

        if (isUseGiapXen) {
            if (Util.canDoWithTime(lastTimeGiapXen, TIME_ITEM)) {
                isUseGiapXen = false;
            }
        }
        if (isUseCuongNo) {
            if (Util.canDoWithTime(lastTimeCuongNo, TIME_ITEM)) {
                isUseCuongNo = false;
                Service.gI().point(player);
            }
        }
        if (isUseAnDanh) {
            if (Util.canDoWithTime(lastTimeAnDanh, TIME_ITEM)) {
                isUseAnDanh = false;
            }
        }

        if (isUseBoHuyet2) {
            if (Util.canDoWithTime(lastTimeBoHuyet2, TIME_ITEM)) {
                isUseBoHuyet2 = false;
                Service.gI().point(player);
//                Service.gI().Send_Info_NV(this.player);
            }
        }

        if (isUseBoKhi2) {
            if (Util.canDoWithTime(lastTimeBoKhi2, TIME_ITEM)) {
                isUseBoKhi2 = false;
                Service.gI().point(player);
            }
        }
        if (isUseGiapXen2) {
            if (Util.canDoWithTime(lastTimeGiapXen2, TIME_ITEM)) {
                isUseGiapXen2 = false;
            }
        }
        if (isUseCuongNo2) {
            if (Util.canDoWithTime(lastTimeCuongNo2, TIME_ITEM)) {
                isUseCuongNo2 = false;
                Service.gI().point(player);
            }
        }
        if (isUseAnDanh2) {
            if (Util.canDoWithTime(lastTimeAnDanh2, TIME_ITEM)) {
                isUseAnDanh2 = false;
            }
        }
        if (isUserBanhChung) {
            if (Util.canDoWithTime(lastTimeBanhChung, TIME_30P)) {
                isUserBanhChung = false;
                Service.gI().point(player);
            }
        }
        if (isUseCMS) {
            if (Util.canDoWithTime(lastTimeUseCMS, TIME_CMS)) {
                isUseCMS = false;
            }
        }
        if (isUseGTPT) {
            if (Util.canDoWithTime(lastTimeUseGTPT, TIME_ITEM)) {
                isUseGTPT = false;
            }
        }
        if (isUseDK) {
            if (Util.canDoWithTime(lastTimeUseDK, TIME_DK)) {
                isUseDK = false;
            }
        }
        if (isOpenPower) {
            if (Util.canDoWithTime(lastTimeOpenPower, TIME_OPEN_POWER)) {
                player.nPoint.limitPower++;
                if (player.nPoint.limitPower > NPoint.MAX_LIMIT) {
                    player.nPoint.limitPower = NPoint.MAX_LIMIT;
                }
                player.nPoint.initPowerLimit();
                Service.gI().sendThongBao(player, "Giới hạn sức mạnh của bạn đã được tăng lên 1 bậc");
                isOpenPower = false;
            }
        }
        if (isUseMayDo) {
            if (Util.canDoWithTime(lastTimeUseMayDo, TIME_MAY_DO)) {
                isUseMayDo = false;
            }
        }
        if (isUseMayDo2) {
            if (Util.canDoWithTime(lastTimeUseMayDo2, TIME_MAY_DO2)) {
                isUseMayDo2 = false;
            }
        }
        if (isUseTDLT) {
            if (Util.canDoWithTime(lastTimeUseTDLT, timeTDLT)) {
                this.isUseTDLT = false;
                ItemTimeService.gI().sendCanAutoPlay(this.player);
            }
        }
        if (isUseRX) {
            if (Util.canDoWithTime(lastTimeUseRX, timeRX)) {
                isUseRX = false;
            }
        }
        if (isDrinkBiaTiger) {
            if (Util.canDoWithTime(lastTimeDrinkBiaTiger, TIME_MAY_DO2)) {
                isDrinkBiaTiger = false;
            }
        }
        if (isDrinkBia333) {
            if (Util.canDoWithTime(lastTimeDrinkBia333, TIME_MAY_DO2)) {
                isDrinkBia333 = false;
            }
        }
        if (isDrinkHeniken) {
            if (Util.canDoWithTime(lastTimeDrinkHeniken, TIME_ITEM)) {
                isDrinkHeniken = false;
            }
        }
    }

    public void dispose() {
        this.player = null;
    }
}
