package models.Training;

/*
 *
 *
 * @author NTD
 */

import boss.Boss;
import boss.BossID;
import boss.boss_manifest.Training.*;
import consts.ConstNpc;
import map.Zone;
import player.Player;
import services.MapService;
import services.NpcService;
import services.Service;
import services.func.ChangeMapService;
import utils.Logger;
import utils.Util;

public class TrainingService {

    private static TrainingService instance;

    public static TrainingService gI() {
        if (instance == null) {
            instance = new TrainingService();
        }
        return instance;
    }

    public Player getNonInteractiveNPC(Zone zone, int id) {
        for (Player pl : zone.getNonInteractiveNPCs()) {
            if (pl != null && pl.id == id) {
                return pl;
            }
        }
        return null;
    }

    public int getNpc(int BossId) {
        switch (BossId) {
            case BossID.KARIN -> {
                return ConstNpc.THAN_MEO_KARIN;
            }
            case BossID.THUONG_DE -> {
                return ConstNpc.THUONG_DE;
            }
            case BossID.THAN_VU_TRU -> {
                return ConstNpc.THAN_VU_TRU;
            }
            case BossID.TO_SU_KAIO -> {
                return ConstNpc.TO_SU_KAIO;
            }
            case BossID.WHIS -> {
                return ConstNpc.WHIS;
            }
        }
        return -1;
    }

    public void luyenTapEnd(Player pl, int bossID) {
        if (getNpc(bossID) != -1) {
            Service.gI().sendHideNpc(pl, getNpc(bossID), false);
        }
    }

    public Boss callBoss(Player pl, int bossID, boolean isThachDau) {
        try {
            pl.isThachDau = isThachDau;
            if (getNpc(bossID) != -1) {
                Service.gI().sendHideNpc(pl, getNpc(bossID), true);
            }
            switch (bossID) {
                case BossID.KARIN -> {
                    return new Karin(pl);
                }
                case BossID.TAUPAYPAY -> {
                    return new TauPayPay(pl);
                }
                case BossID.YAJIRO -> {
                    return new Yajiro(pl);
                }
                case BossID.MRPOPO -> {
                    return new MrPoPo(pl);
                }
                case BossID.THUONG_DE -> {
                    ChangeMapService.gI().changeMap(pl, MapService.gI().getMapCanJoin(pl, 49, 0), 362, 408);
                    return new ThuongDe(pl);
                }
                case BossID.KHI_BUBBLES -> {
                    return new KhiBubbles(pl);
                }
                case BossID.THAN_VU_TRU -> {
                    return new ThanVuTru(pl);
                }
                case BossID.TO_SU_KAIO -> {
                    return new ToSuKaio(pl);
                }
                case BossID.WHIS -> {
                    return new Whis(pl);
                }
            }
        } catch (Exception e) {
            Logger.logException(TrainingService.class, e);
        }
        return null;
    }

    public int getTnsmMoiPhut(Player player) {
        return switch (player.levelLuyenTap) {
            case 0 -> 20;
            case 1 -> 40;
            case 2 -> 80;
            case 3 -> 160;
            case 4 -> 320;
            case 5 -> 640;
            default -> player.tnsmLuyenTap > 1280 ? player.tnsmLuyenTap : 1280;
        };
    }

    public void tangTnsmLuyenTap(Player player, long tnsm) {
        if (player.isPl()) {
            player.tnsmLuyenTap += Math.max(100, tnsm / (100 * (Service.gI().getCurrLevel(player) + 1)));
            if (player.tnsmLuyenTap > 10_000_000) {
                player.tnsmLuyenTap = 10_000_000;
            }
        }
    }

    public void tnsmLuyenTapUp(Player player) {
        long tnsm;
        int time = (int) ((System.currentTimeMillis() - player.lastTimeOffline) / 1000);

        // Nếu thời gian offline > 60s thì mới tính
        if (time > 60) {
            int realTime = Math.min(time, 86400); // Giới hạn 1 ngày
            tnsm = (long) getTnsmMoiPhut(player) * realTime / 60;

            if (MapService.gI().isMapLuyenTap(player.zone.map.mapId)) {
                // Người chơi đang ở map luyện tập
                String message = "Bạn tăng được " + Util.numberToMoney(tnsm)
                        + " sức mạnh trong thời gian " + (time / 60) + " phút tập luyện Offline";
                NpcService.gI().createTutorial(player, -1, message);
                Service.gI().addSMTN(player, (byte) 2, tnsm, false);

            } else if (player.dangKyTapTuDong && time > 1800) {
                // Tự động luyện tập, điều kiện: đăng ký + offline > 30 phút
                if (player.inventory.getGemAndRuby() > 1) {
                    Thread.ofVirtual()
                            .name("Luyện Tập")
                            .start(() -> {
                                try {
                                    player.inventory.subGemAndRuby(1);
                                    Thread.sleep(1000); // delay 1s trước khi thực hiện chuyển map

                                    if (player.zone == null) {
                                        return;
                                    }

                                    // Lưu lại trạng thái
                                    player.lastMapOffline = player.zone.map.mapId;
                                    player.lastZoneOffline = player.zone.zoneId;
                                    player.lastXOffline = player.location.x;

                                    // Cộng sức mạnh
                                    Service.gI().addSMTN(player, (byte) 2, tnsm, false);
                                    player.teleTapTuDong = true;

                                    // Thông báo
                                    player.thongBaoTapTuDong = "Bạn tăng được " + Util.numberToMoney(tnsm)
                                            + " sức mạnh trong thời gian " + (time / 60) + " phút tập luyện Offline, -1 ngọc (phí đăng ký tập tự động)";

                                    // Chuyển map
                                    ChangeMapService.gI().changeMapBySpaceShip(
                                            player, player.mapIdDangTapTuDong, 0, Util.nextInt(200, 400));

                                    Service.gI().sendMoney(player);

                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            });
                } else {
                    // Không đủ ngọc
                    player.dangKyTapTuDong = false;
                    Service.gI().sendThongBao(player, "Bạn không đủ ngọc, đăng ký luyện tập tự động đã bị hủy");
                }
            }
        }

        // Reset mỗi 0h để giảm sức mạnh tích trữ
        if (Util.isAfterMidnight(player.lastTimeOffline)) {
            if (player.tnsmLuyenTap > 1) {
                player.tnsmLuyenTap -= player.tnsmLuyenTap / 3;
            }
            player.lastTimeOffline = System.currentTimeMillis();
        }
    }

}
