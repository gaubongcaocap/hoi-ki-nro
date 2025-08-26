package boss.boss_manifest.Yardart;

/*
 *
 *
 * @author NTD
 */

import boss.BossID;
import boss.BossesData;
import static boss.BossType.YARDART;

public class CHIENBINH0 extends Yardart {

    public CHIENBINH0() throws Exception {
        super(YARDART, BossID.CHIEN_BINH_0, BossesData.CHIEN_BINH_0);
    }

    @Override
    protected void init() {
        x = 170;
        x2 = 240;
        y = 456;
        y2 = 456;
        range = 1000;
        range2 = 150;
        timeHoiHP = 20000;
        rewardRatio = 3;
    }

}
