package boss.boss_manifest.Yardart;

/*
 *
 *
 * @author NTD
 */

import boss.BossID;
import boss.BossesData;
import static boss.BossType.YARDART;

public class TANBINH2 extends Yardart {

    public TANBINH2() throws Exception {
        super(YARDART, BossID.TAN_BINH_2, BossesData.TAN_BINH_2);
    }

    @Override
    protected void init() {
        x = 582;
        x2 = 652;
        y = 432;
        y2 = 432;
        range = 1000;
        range2 = 150;
        timeHoiHP = 25000;
        rewardRatio = 4;
    }
}
