package boss.boss_manifest.Yardart;

/*
 *
 *
 * @author NTD
 */

import boss.BossID;
import boss.BossesData;
import static boss.BossType.YARDART;

public class TANBINH1 extends Yardart {

    public TANBINH1() throws Exception {
        super(YARDART, BossID.TAN_BINH_1, BossesData.TAN_BINH_1);
    }

    @Override
    protected void init() {
        x = 376;
        x2 = 446;
        y = 456;
        y2 = 432;
        range = 1000;
        range2 = 150;
        timeHoiHP = 25000;
        rewardRatio = 4;
    }
}
