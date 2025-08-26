package boss.boss_manifest.Yardart;

/*
 *
 *
 * @author NTD
 */

import boss.BossID;
import boss.BossesData;
import static boss.BossType.YARDART;

public class CHIENBINH1 extends Yardart {

    public CHIENBINH1() throws Exception {
        super(YARDART, BossID.CHIEN_BINH_1, BossesData.CHIEN_BINH_1);
    }

    @Override
    protected void init() {
        x = 376;
        x2 = 446;
        y = 456;
        y2 = 456;
        range = 1000;
        range2 = 150;
        timeHoiHP = 20000;
        rewardRatio = 3;
    }
}
