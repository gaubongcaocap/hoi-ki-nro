package models.The23rdMartialArtCongress;

/*
 *
 *
 * @author NTD
 */

import boss.boss_manifest.The23rdMartialArtCongress.TauPayPay;
import boss.boss_manifest.The23rdMartialArtCongress.PonPut;
import boss.boss_manifest.The23rdMartialArtCongress.Pocolo;
import boss.boss_manifest.The23rdMartialArtCongress.LiuLiu;
import boss.boss_manifest.The23rdMartialArtCongress.ODo;
import boss.boss_manifest.The23rdMartialArtCongress.JackyChun;
import boss.boss_manifest.The23rdMartialArtCongress.ChaPa;
import boss.boss_manifest.The23rdMartialArtCongress.ThienXinHang;
import boss.boss_manifest.The23rdMartialArtCongress.Yamcha;
import boss.boss_manifest.The23rdMartialArtCongress.SoiHecQuyn;
import boss.boss_manifest.The23rdMartialArtCongress.ChanXu;
import boss.boss_manifest.The23rdMartialArtCongress.Xinbato;
import consts.ConstPlayer;
import boss.Boss;
import boss.BossStatus;
import player.Player;
import services.EffectSkillService;
import services.ItemTimeService;
import services.PlayerService;
import services.Service;
import lombok.Getter;
import lombok.Setter;
import map.Zone;
import matches.pvp.DHVT;
import utils.Util;

public class The23rdMartialArtCongress {

    @Setter
    @Getter
    private Player player;

    private Boss boss;

    @Setter
    private Player npc;

    private int time;
    @Setter
    private int round;
    private int timeWait;

    public boolean endChallenge;

    @Setter
    @Getter
    private Zone zone;

    public void update() {

        if (player.zone == null || !player.zone.equals(zone)) {
            this.endChallenge();
            return;
        }

        if (timeWait > 0) {
            switch (timeWait) {
                case 13:
                    if (round == 4 || round == 6 || round == 8 || round == 10) {
                        Service.gI().releaseCooldownSkill(player);
                    }
                    EffectSkillService.gI().startStun(boss, System.currentTimeMillis(), 14000);
                    EffectSkillService.gI().startStun(player, System.currentTimeMillis(), 14000);
                    ItemTimeService.gI().sendItemTime(player, 3779, 11000 / 1000);
                    player.nPoint.hp = player.nPoint.hpMax;
                    player.nPoint.mp = player.nPoint.mpMax;
                    Service.gI().Send_Info_NV(player);
                    Service.gI().sendInfoPlayerEatPea(boss);
                    break;
                case 11:
                    PlayerService.gI().playerMove(npc, npc.location.x, 264);
                    Service.gI().chat(npc, "Trận đấu giữa " + player.name + " vs " + boss.name + " sắp diễn ra"); // 7 STUN
                    break;
                case 7:
                    Service.gI().chat(npc, "Xin quý vị khán giả cho 1 tràng pháo tay cổ vũ cho 2 đấu thủ nào"); // 3 STUN
                    break;
                case 4:
                    Service.gI().chat(npc, "Mọi người hãy ổn định chỗ ngồi, trận đấu sẽ bắt đầu sau 3 giây nữa"); // 0 STUN
                    break;
                case 3:
                    Service.gI().chat(npc, "Trận đấu bắt đầu"); // -1 STUN
                    break;
                case 2:
                    PlayerService.gI().playerMove(npc, npc.location.x, 360);
                    Service.gI().chat(player, "OK");
                    Service.gI().chat(boss, "OK");
                    break;
                case 1:
                    The23rdMartialArtCongressService.gI().sendTypePK(player, boss);
                    PlayerService.gI().changeAndSendTypePK(this.player, ConstPlayer.PK_PVP);
                    boss.changeStatus(BossStatus.ACTIVE);
                    new DHVT(player, boss);
                    setTime(181);
                    break;
            }
            timeWait--;
            return;
        }

        if (time > 0) {
            time--;
            if (player.isDie() || player.lostByDeath) {
                die();
                return;
            }
            if (player.location != null && player.isPKDHVT && !player.isDie() && player != null && player.zone != null) {
                if (boss.isDie()) {
                    round++;
                    boss.leaveMap();
                    toTheNextRound();
                    reward();
                }
                if (player.location.y > 264 && !(player.location.x > 150 && player.location.x < 630)) {
                    leave();
                    return;
                }
                if (!player.isPKDHVT) {
                    leave();
                }
            } else {
                if (boss != null) {
                    boss.leaveMap();
                }
                The23rdMartialArtCongressManager.gI().remove(this);
            }

        } else {
            timeOut();
        }
    }

    public void toTheNextRound() {
        try {
            PlayerService.gI().changeAndSendTypePK(player, ConstPlayer.NON_PK);
            Boss bss = null;
            switch (round) {
                case 0:
                    bss = new SoiHecQuyn(player);
                    break;
                case 1:
                    bss = new ODo(player);
                    break;
                case 2:
                    bss = new Xinbato(player);
                    break;
                case 3:
                    bss = new ChaPa(player);
                    break;
                case 4:
                    bss = new PonPut(player);
                    break;
                case 5:
                    bss = new ChanXu(player);
                    break;
                case 6:
                    bss = new TauPayPay(player);
                    break;
                case 7:
                    bss = new Yamcha(player);
                    break;
                case 8:
                    bss = new JackyChun(player);
                    break;
                case 9:
                    bss = new ThienXinHang(player);
                    break;
                case 10:
                    bss = new LiuLiu(player);
                    break;
                case 11:
                    bss = new Pocolo(player);
                    break;
                case 12:
                    champion();
                    return;
                default:
                    return;
            }
            Service.gI().setPos(player, 335, 264);
            setTimeWait(13);
            setBoss(bss);
        } catch (Exception e) {
        }
    }

    public void setBoss(Boss boss) {
        this.boss = boss;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void setTimeWait(int timeWait) {
        this.timeWait = timeWait;
    }

    public void die() {
        player.lostByDeath = false;
        Service.gI().sendThongBao(player, "Thua rồi");
        Service.gI().chat(npc, boss.name + " đã chiến thắng");
        if (player.zone != null) {
            endChallenge();
        }
    }

    private void timeOut() {
        Service.gI().sendThongBao(player, "Thua rồi");
        Service.gI().chat(npc, "Hết thời gian thi đấu");
        Service.gI().chat(npc, boss.name + " đã chiến thắng");
        endChallenge();
    }

    private void champion() {
        Service.gI().sendThongBao(player, "Chúc mừng " + player.name + " vừa vô địch giải");
        endChallenge();
    }

    public void leave() {
        if (player.levelWoodChest != 12) {
            setTime(0);
            EffectSkillService.gI().removeStun(player);
            Service.gI().sendThongBao(player, "Thua rồi");
            Service.gI().chat(npc, "Đối thủ đã rơi khỏi võ đài, " + boss.name + " đã chiến thắng");
            Service.gI().chat(npc, boss.name + " đã chiến thắng");
            endChallenge();
        }
    }

    private void reward() {
        if (player.levelWoodChest < round) {
            player.levelWoodChest = round;
        }
    }

    public void endChallenge() {
        if (!endChallenge) {
            endChallenge = true;
            reward();
            if (player.zone != null) {
                player.nPoint.hp = player.nPoint.hpMax;
                player.nPoint.mp = player.nPoint.mpMax;
                Service.gI().Send_Info_NV(player);
//                PlayerService.gI().hoiSinh(player);
            }
            PlayerService.gI().changeAndSendTypePK(player, ConstPlayer.NON_PK);
            if (player != null && player.zone != null && player.zone.map.mapId == 129) {
                Service.gI().setPos(player, Util.nextInt(200, 500), 360);
            }
            player.isPKDHVT = false;
            Service.gI().sendPlayerVS(player, null, (byte) 0);
            if (boss != null) {
                boss.leaveMap();
            }
            zone = null;
            The23rdMartialArtCongressManager.gI().remove(this);
        }
    }
}
