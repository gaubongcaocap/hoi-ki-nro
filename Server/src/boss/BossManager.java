package boss;

/*
 *
 *
 * @author NTD
 */
import EMTI.Functions;
import boss.boss_manifest.AnTrom.AnTrom;
import boss.boss_manifest.AnTrom.AnTromTV;
import boss.boss_manifest.Black.BlackGoku;

import boss.boss_manifest.Nappa.Rambo;
import boss.boss_manifest.Nappa.MapDauDinh;
import boss.boss_manifest.Nappa.Kuku;
import boss.boss_manifest.Android.Android19;
import boss.boss_manifest.Android.Pic;
import boss.boss_manifest.Android.Android14;
import boss.boss_manifest.Android.Poc;
import boss.boss_manifest.Android.Android13;
import boss.boss_manifest.Android.KingKong;
import boss.boss_manifest.Android.DrKore;
import boss.boss_manifest.Android.Android15;
import boss.boss_manifest.GoldenFrieza.DeathBeam1;
import boss.boss_manifest.GoldenFrieza.DeathBeam2;
import boss.boss_manifest.GoldenFrieza.DeathBeam3;
import boss.boss_manifest.GoldenFrieza.DeathBeam4;
import boss.boss_manifest.GoldenFrieza.DeathBeam5;
import boss.boss_manifest.GoldenFrieza.GoldenFrieza;
import boss.boss_manifest.Cooler.Cooler;
import boss.boss_manifest.Cell.SieuBoHung;
import boss.boss_manifest.Cell.XenBoHung;
//import boss.boss_manifest.BrolyFix.Broly;
//import boss.boss_manifest.BrolyFix.BrolySuper;
import boss.boss_manifest.Broly.Broly;
import boss.boss_manifest.Broly.SuperBroly;
import boss.boss_manifest.ChristmasEvent.OngGiaNoel;
import boss.boss_manifest.TaoPaiPai.TaoPaiPai;
import boss.boss_manifest.ThanHuyDiet.*;
import boss.boss_manifest.Frieza.Fide;
import boss.boss_manifest.HungVuongEvent.SonTinh;
import boss.boss_manifest.HungVuongEvent.ThuyTinh;
import boss.boss_manifest.Kimetsu.*;
import boss.boss_manifest.HalloweenEvent.BiMa;
import boss.boss_manifest.HalloweenEvent.Doi;
import boss.boss_manifest.HalloweenEvent.MaTroi;
import boss.boss_manifest.TrungThuEvent.KhiDot;
import boss.boss_manifest.TrungThuEvent.NguyetThan;
import boss.boss_manifest.TrungThuEvent.NhatThan;
import boss.boss_manifest.MajinBuu12H.Mabu;
import boss.boss_manifest.MajinBuu12H.BuiBui;
import boss.boss_manifest.MajinBuu12H.BuiBui2;
import boss.boss_manifest.MajinBuu12H.Cadic;
import boss.boss_manifest.MajinBuu12H.Drabura;
import boss.boss_manifest.MajinBuu12H.Drabura2;
import boss.boss_manifest.MajinBuu12H.Drabura3;
import boss.boss_manifest.MajinBuu12H.Goku;
import boss.boss_manifest.MajinBuu12H.Yacon;
import boss.boss_manifest.MajinBuu14H.Mabu2H;
import boss.boss_manifest.MajinBuu14H.SuperBu;
import boss.boss_manifest.GinyuForce.SO1;
import boss.boss_manifest.GinyuForce.SO2;
import boss.boss_manifest.GinyuForce.SO3;
import boss.boss_manifest.GinyuForce.SO4;
import boss.boss_manifest.GinyuForce.TDT;
import boss.boss_manifest.NamekGinyuForce.SO1_NM;
import boss.boss_manifest.NamekGinyuForce.SO2_NM;
import boss.boss_manifest.NamekGinyuForce.SO3_NM;
import boss.boss_manifest.NamekGinyuForce.SO4_NM;
import boss.boss_manifest.NamekGinyuForce.TDT_NM;
import boss.boss_manifest.Earth.BIDO;
import boss.boss_manifest.Earth.BOJACK;
import boss.boss_manifest.Earth.BUJIN;
import boss.boss_manifest.Earth.KOGU;
import boss.boss_manifest.Earth.SUPER_BOJACK;
import boss.boss_manifest.Earth.ZANGYA;
import boss.boss_manifest.Yardart.CHIENBINH0;
import boss.boss_manifest.Yardart.CHIENBINH1;
import boss.boss_manifest.Yardart.CHIENBINH2;
import boss.boss_manifest.Yardart.CHIENBINH3;
import boss.boss_manifest.Yardart.CHIENBINH4;
import boss.boss_manifest.Yardart.CHIENBINH5;
import boss.boss_manifest.Yardart.DOITRUONG5;
import boss.boss_manifest.Yardart.TANBINH0;
import boss.boss_manifest.Yardart.TANBINH1;
import boss.boss_manifest.Yardart.TANBINH2;
import boss.boss_manifest.Yardart.TANBINH3;
import boss.boss_manifest.Yardart.TANBINH4;
import boss.boss_manifest.Yardart.TANBINH5;
import boss.boss_manifest.Yardart.TAPSU0;
import boss.boss_manifest.Yardart.TAPSU1;
import boss.boss_manifest.Yardart.TAPSU2;
import boss.boss_manifest.Yardart.TAPSU3;
import boss.boss_manifest.Yardart.TAPSU4;
import item.Item;
import boss.boss_manifest.Cell.XENCON1;
import boss.boss_manifest.Cell.XENCON2;
import boss.boss_manifest.Cell.XENCON3;
import boss.boss_manifest.Cell.XENCON4;
import boss.boss_manifest.Cell.XENCON5;
import boss.boss_manifest.Cell.XENCON6;
import boss.boss_manifest.Cell.XENCON7;
import boss.boss_manifest.Cumber.Cumber;
import boss.boss_manifest.LunarNewYearEvent.LanCon;
import player.Player;
import network.Message;
import services.MapService;

import java.util.ArrayList;
import java.util.List;

import map.Zone;
import server.Maintenance;
import utils.Logger;

public class BossManager implements Runnable {

    private static BossManager instance;
    public static byte ratioReward = 10;

    public static BossManager gI() {
        if (instance == null) {
            instance = new BossManager();
        }
        return instance;
    }

    public BossManager() {
        this.bosses = new ArrayList<>();
    }

    public List<Boss> getBosses() {
        return this.bosses;
    }

    protected final List<Boss> bosses;

    public void addBoss(Boss boss) {
        this.bosses.add(boss);
    }

    public void removeBoss(Boss boss) {
        this.bosses.remove(boss);
    }

    public void loadBoss() {
        this.createBoss(BossID.TIEU_DOI_TRUONG);
        this.createBoss(BossID.TIEU_DOI_TRUONG_NM);
        this.createBoss(BossID.BOJACK);
        this.createBoss(BossID.SUPER_BOJACK);
        this.createBoss(BossID.KING_KONG);
        this.createBoss(BossID.XEN_BO_HUNG);
        this.createBoss(BossID.SIEU_BO_HUNG);
        this.createBoss(BossID.KUKU, 5);
        this.createBoss(BossID.MAP_DAU_DINH, 5);
        this.createBoss(BossID.RAMBO, 5);
        this.createBoss(BossID.FIDE);
        this.createBoss(BossID.ANDROID_14);
        this.createBoss(BossID.DR_KORE);
        this.createBoss(BossID.COOLER);
        this.createBoss(BossID.BLACK_GOKU, 5);
        this.createBoss(BossID.GOLDEN_FRIEZA, 5);
        this.createBoss(BossID.BLACK_GOKU);
        this.createBoss(BossID.AN_TROM);
        this.createBoss(BossID.AN_TROM_TV);
        this.createBoss(BossID.BROLY, 50);
        // this.createBoss(BossID.SUPER_BROLY, 3);
        this.createBoss(BossID.CUMBER);
        this.createBoss(BossID.SOI_HEC_QUYN, 3);
        this.createBoss(BossID.GOKU_HUYDIET, 3);
        this.createBoss(BossID.TANJIRO, 1);
        this.createBoss(BossID.INOSUKE_PIG, 1);
        this.createBoss(BossID.INOSUKE, 1);
        this.createBoss(BossID.ZENITSU, 1);
        this.createBoss(BossID.NEZUKO, 1);

        this.createBoss(BossID.BILL2, 1);
        this.createBoss(BossID.WHIS2, 1);
    }

    public void createBoss(int bossID, int total) {
        for (int i = 0; i < total; i++) {
            createBoss(bossID);
        }
    }

    public Boss createBoss(int bossID) {
        try {
            return switch (bossID) {
                case BossID.TAP_SU_0 ->
                    new TAPSU0();
                case BossID.TAP_SU_1 ->
                    new TAPSU1();
                case BossID.TAP_SU_2 ->
                    new TAPSU2();
                case BossID.TAP_SU_3 ->
                    new TAPSU3();
                case BossID.TAP_SU_4 ->
                    new TAPSU4();
                case BossID.TAN_BINH_5 ->
                    new TANBINH5();
                case BossID.TAN_BINH_0 ->
                    new TANBINH0();
                case BossID.TAN_BINH_1 ->
                    new TANBINH1();
                case BossID.TAN_BINH_2 ->
                    new TANBINH2();
                case BossID.TAN_BINH_3 ->
                    new TANBINH3();
                case BossID.TAN_BINH_4 ->
                    new TANBINH4();
                case BossID.CHIEN_BINH_5 ->
                    new CHIENBINH5();
                case BossID.CHIEN_BINH_0 ->
                    new CHIENBINH0();
                case BossID.CHIEN_BINH_1 ->
                    new CHIENBINH1();
                case BossID.CHIEN_BINH_2 ->
                    new CHIENBINH2();
                case BossID.CHIEN_BINH_3 ->
                    new CHIENBINH3();
                case BossID.CHIEN_BINH_4 ->
                    new CHIENBINH4();
                case BossID.DOI_TRUONG_5 ->
                    new DOITRUONG5();
                case BossID.SO_4 ->
                    new SO4();
                case BossID.SO_3 ->
                    new SO3();
                case BossID.SO_2 ->
                    new SO2();
                case BossID.SO_1 ->
                    new SO1();
                case BossID.TIEU_DOI_TRUONG ->
                    new TDT();
                case BossID.SO_4_NM ->
                    new SO4_NM();
                case BossID.SO_3_NM ->
                    new SO3_NM();
                case BossID.SO_2_NM ->
                    new SO2_NM();
                case BossID.SO_1_NM ->
                    new SO1_NM();
                case BossID.TIEU_DOI_TRUONG_NM ->
                    new TDT_NM();
                case BossID.BUJIN ->
                    new BUJIN();
                case BossID.KOGU ->
                    new KOGU();
                case BossID.ZANGYA ->
                    new ZANGYA();
                case BossID.BIDO ->
                    new BIDO();
                case BossID.BOJACK ->
                    new BOJACK();
                case BossID.SUPER_BOJACK ->
                    new SUPER_BOJACK();
                case BossID.KUKU ->
                    new Kuku();
                case BossID.MAP_DAU_DINH ->
                    new MapDauDinh();
                case BossID.RAMBO ->
                    new Rambo();
                case BossID.TAU_PAY_PAY_DONG_NAM_KARIN ->
                    new TaoPaiPai();
                case BossID.DRABURA ->
                    new Drabura();
                case BossID.BUI_BUI ->
                    new BuiBui();
                case BossID.BUI_BUI_2 ->
                    new BuiBui2();
                case BossID.YA_CON ->
                    new Yacon();
                case BossID.DRABURA_2 ->
                    new Drabura2();
                case BossID.GOKU ->
                    new Goku();
                case BossID.CADIC ->
                    new Cadic();
                case BossID.MABU_12H ->
                    new Mabu();
                case BossID.DRABURA_3 ->
                    new Drabura3();
                case BossID.MABU ->
                    new Mabu2H();
                case BossID.SUPERBU ->
                    new SuperBu();
                case BossID.FIDE ->
                    new Fide();
                case BossID.DR_KORE ->
                    new DrKore();
                case BossID.ANDROID_19 ->
                    new Android19();
                case BossID.ANDROID_13 ->
                    new Android13();
                case BossID.ANDROID_14 ->
                    new Android14();
                case BossID.ANDROID_15 ->
                    new Android15();
                case BossID.PIC ->
                    new Pic();
                case BossID.POC ->
                    new Poc();
                case BossID.KING_KONG ->
                    new KingKong();
                case BossID.XEN_BO_HUNG ->
                    new XenBoHung();
                case BossID.SIEU_BO_HUNG ->
                    new SieuBoHung();
                case BossID.XEN_CON_1 ->
                    new XENCON1();
                case BossID.XEN_CON_2 ->
                    new XENCON2();
                case BossID.XEN_CON_3 ->
                    new XENCON3();
                case BossID.XEN_CON_4 ->
                    new XENCON4();
                case BossID.XEN_CON_5 ->
                    new XENCON5();
                case BossID.XEN_CON_6 ->
                    new XENCON6();
                case BossID.XEN_CON_7 ->
                    new XENCON7();
                case BossID.COOLER ->
                    new Cooler();
                case BossID.BROLY ->
                    new Broly();
                case BossID.SUPER_BROLY ->
                    new SuperBroly();
                case BossID.AN_TROM ->
                    new AnTrom();
                case BossID.AN_TROM_TV ->
                    new AnTromTV();
                case BossID.KHIDOT ->
                    new KhiDot();
                case BossID.NGUYET_THAN ->
                    new NguyetThan();
                case BossID.NHATTHAN ->
                    new NhatThan();
                case BossID.GOLDEN_FRIEZA ->
                    new GoldenFrieza();
                case BossID.DEATH_BEAM_1 ->
                    new DeathBeam1();
                case BossID.DEATH_BEAM_2 ->
                    new DeathBeam2();
                case BossID.DEATH_BEAM_3 ->
                    new DeathBeam3();
                case BossID.DEATH_BEAM_4 ->
                    new DeathBeam4();
                case BossID.DEATH_BEAM_5 ->
                    new DeathBeam5();
                case BossID.BIMA ->
                    new BiMa();
                case BossID.MATROI ->
                    new MaTroi();
                case BossID.DOI ->
                    new Doi();
                case BossID.ONG_GIA_NOEL ->
                    new OngGiaNoel();
                case BossID.SON_TINH ->
                    new SonTinh();
                case BossID.THUY_TINH ->
                    new ThuyTinh();
                case BossID.LAN_CON ->
                    new LanCon();
                case BossID.BLACK_GOKU ->
                    new BlackGoku();
                case BossID.CUMBER ->
                    new Cumber();
                case BossID.GOKU_HUYDIET ->
                    new boss.boss_manifest.GokuVegeta.Goku();
                case BossID.CADIC_HUYDIET ->
                    new boss.boss_manifest.GokuVegeta.Cadic();
                case BossID.TANJIRO ->
                    new Tanjiro();
                case BossID.INOSUKE_PIG ->
                    new InosukePig();
                case BossID.INOSUKE ->
                    new Inosuke();
                case BossID.ZENITSU ->
                    new Zenitsu();
                case BossID.NEZUKO ->
                    new Nezuko();
                case BossID.BILL2 ->
                    new Bill2();
                case BossID.WHIS2 ->
                    new Whis2();
                default ->
                    null;
            };
        } catch (Exception e) {
            Logger.error(e + "\n");
            return null;
        }
    }

    public Boss getBoss(int id) {
        try {
            Boss boss = this.bosses.get(id);
            if (boss != null) {
                return boss;
            }
        } catch (Exception e) {
        }
        return null;
    }

    public void showListBoss(Player player, Item item) {
        if (!player.isAdmin() && item == null) {
            return;
        }

        player.iDMark.setMenuType(3);
        Message msg;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Boss");
            msg.writer()
                    .writeByte((int) bosses.stream()
                            .filter(boss -> !MapService.gI().isMapBossFinal(boss.data[0].getMapJoin()[0])
                                    && !MapService.gI().isMapHuyDiet(boss.data[0].getMapJoin()[0])
                                    && !MapService.gI().isMapYardart(boss.data[0].getMapJoin()[0])
                                    && !MapService.gI().isMapMaBu(boss.data[0].getMapJoin()[0])
                                    && !MapService.gI().isMapBlackBallWar(boss.data[0].getMapJoin()[0]))
                            .count());
            for (int i = 0; i < bosses.size(); i++) {
                Boss boss = this.bosses.get(i);
                if (MapService.gI().isMapBossFinal(boss.data[0].getMapJoin()[0])
                        || MapService.gI().isMapYardart(boss.data[0].getMapJoin()[0])
                        || MapService.gI().isMapHuyDiet(boss.data[0].getMapJoin()[0])
                        || MapService.gI().isMapMaBu(boss.data[0].getMapJoin()[0])
                        || MapService.gI().isMapBlackBallWar(boss.data[0].getMapJoin()[0])) {
                    continue;
                }
                msg.writer().writeInt(i);
                msg.writer().writeInt(i);
                msg.writer().writeShort(boss.data[0].getOutfit()[0]);
                if (player.getSession().version >= 214) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(boss.data[0].getOutfit()[1]);
                msg.writer().writeShort(boss.data[0].getOutfit()[2]);
                msg.writer().writeUTF(boss.data[0].getName());
                if (boss.zone != null) {
                    msg.writer().writeUTF(boss.bossStatus.toString());
                    msg.writer().writeUTF(
                            boss.zone.map.mapName + "(" + boss.zone.map.mapId + ") khu " + boss.zone.zoneId + "");
                } else {
                    msg.writer().writeUTF(boss.bossStatus.toString());
                    msg.writer().writeUTF("Chết rồi");
                }
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public Boss getBossById(int bossId) {
        return this.bosses.stream().filter(boss -> boss.id == bossId && !boss.isDie()).findFirst().orElse(null);
    }

    public boolean checkBosses(Zone zone, int BossID) {
        return this.bosses.stream()
                .filter(boss -> boss.id == BossID && boss.zone != null && boss.zone.equals(zone) && !boss.isDie())
                .findFirst().orElse(null) != null;
    }

    public Player findBossClone(Player player) {
        return player.zone.getBosses().stream().filter(boss -> boss.id < -100_000_000 && !boss.isDie()).findFirst()
                .orElse(null);
    }

    public Boss getBossById(int bossId, int mapId, int zoneId) {
        return this.bosses.stream().filter(boss -> boss.id == bossId && boss.zone != null
                && boss.zone.map.mapId == mapId && boss.zone.zoneId == zoneId && !boss.isDie()).findFirst()
                .orElse(null);
    }

    @Override
    public void run() {
        while (!Maintenance.isRunning) {
            try {
                int delay = 150;
                long st = System.currentTimeMillis();
                for (int i = this.bosses.size() - 1; i >= 0; i--) {
                    try {
                        this.bosses.get(i).update();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                // if (delay - (System.currentTimeMillis() - st) > 0) {
                // Thread.sleep(delay - (System.currentTimeMillis() - st));
                // }
                Functions.sleep(Math.max(delay - (System.currentTimeMillis() - st), 10));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
