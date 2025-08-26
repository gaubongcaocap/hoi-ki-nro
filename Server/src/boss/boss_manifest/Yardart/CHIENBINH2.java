package boss.boss_manifest.Yardart;

/*
 *
 *
 * @author NTD
 */

import boss.BossID;
import boss.BossesData;
import static boss.BossType.YARDART;

public class CHIENBINH2 extends Yardart {

    public CHIENBINH2() throws Exception {
        super(YARDART, BossID.CHIEN_BINH_2, BossesData.CHIEN_BINH_2);
    }

    @Override
    protected void init() {
        x = 582;
        x2 = 652;
        y = 456;
        y2 = 456;
        range = 1000;
        range2 = 150;
        timeHoiHP = 20000;
        rewardRatio = 3;
    }
}
