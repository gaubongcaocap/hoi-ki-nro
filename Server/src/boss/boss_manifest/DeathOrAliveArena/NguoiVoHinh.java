package boss.boss_manifest.DeathOrAliveArena;

/*
 *
 *
 * @author NTD
 */

import boss.BossID;
import boss.BossesData;
import static boss.BossType.PHOBAN;
import player.Player;
import services.Service;
import utils.Util;

public class NguoiVoHinh extends DeathOrAliveArena {

    private long lastTimeTanHinh;
    private boolean goToPlayer;

    public NguoiVoHinh(Player player) throws Exception {
        super(PHOBAN, BossID.NGUOI_VO_HINH, BossesData.NGUOI_VO_HINH);
        this.playerAtt = player;
        lastTimeTanHinh = System.currentTimeMillis();
    }

    @Override
    public void tanHinh() {
        if (Util.canDoWithTime(lastTimeTanHinh, 15000)) {
            lastTimeTanHinh = System.currentTimeMillis();
        }

        if (!Util.canDoWithTime(this.lastTimeTanHinh, 5000)) {
            Service.gI().setPos2(this, playerAtt.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 200)),
                    10000);
            goToPlayer = false;
        } else {
            if (!goToPlayer) {
                goToPlayer = true;
                goToPlayer(playerAtt, false);
            }
        }

    }

}
