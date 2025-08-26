package boss.boss_manifest.The23rdMartialArtCongress;

/*
 *
 *
 * @author NTD
 */

import boss.BossID;
import boss.BossesData;
import static boss.BossType.PHOBAN;
import player.Player;

public class JackyChun extends The23rdMartialArtCongress {

    public JackyChun(Player player) throws Exception {
        super(PHOBAN, BossID.JACKY_CHUN, BossesData.JACKY_CHUN);
        this.playerAtt = player;
    }
}
