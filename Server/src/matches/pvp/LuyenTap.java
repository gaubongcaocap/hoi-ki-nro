package matches.pvp;

/*
 *
 *
 * @author NTD
 */
import consts.ConstPlayer;
import matches.PVP;
import matches.TYPE_LOSE_PVP;
import matches.TYPE_PVP;
import player.Player;
import services.PlayerService;
import services.Service;

public class LuyenTap extends PVP {

    public LuyenTap(Player p1, Player p2) {
        super(TYPE_PVP.LUYEN_TAP, p1, p2);
    }

    @Override
    protected void changeToTypePK() {
        if (this.p1 != null && this.p2 != null) {
            PlayerService.gI().changeAndSendTypePK(this.p1, ConstPlayer.PK_PVP_2);
            PlayerService.gI().changeAndSendTypePK(this.p2, ConstPlayer.PK_PVP_2);
        }
    }

    @Override
    public void finish() {

    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public void update() {
    }

    @Override
    public void reward(Player plWin) {
    }

    @Override
    public void sendResult(Player plLose, TYPE_LOSE_PVP typeLose) {
        if (typeLose == TYPE_LOSE_PVP.RUNS_AWAY) {
            Service.gI().sendThongBao(p1.equals(plLose) ? p2 : p1, "Kết thúc luyện tập");
        } else if (typeLose == TYPE_LOSE_PVP.DEAD) {
            Service.gI().sendThongBao(p1.equals(plLose) ? p2 : p1, "Kết thúc luyện tập");
        }
    }

}
