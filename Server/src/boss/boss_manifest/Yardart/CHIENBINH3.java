package boss.boss_manifest.Yardart;

/*
 *
 *
 * @author NTD
 */

import boss.BossID;
import boss.BossesData;
import static boss.BossType.YARDART;

public class CHIENBINH3 extends Yardart {

    public CHIENBINH3() throws Exception {
        super(YARDART, BossID.CHIEN_BINH_3, BossesData.CHIEN_BINH_3);
    }

    @Override
    protected void init() {
        x = 787;
        x2 = 857;
        y = 456;
        y2 = 456;
        range = 1000;
        range2 = 150;
        timeHoiHP = 20000;
        rewardRatio = 3;
    }

}
