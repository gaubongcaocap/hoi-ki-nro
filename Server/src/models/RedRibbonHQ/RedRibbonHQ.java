package models.RedRibbonHQ;

/*
 *
 *
 * @author NTD
 */
import EMTI.Functions;
import boss.Boss;
import boss.boss_manifest.RedRibbonHQ.NinjaAoTim;
import boss.boss_manifest.RedRibbonHQ.RobotVeSi;
import boss.boss_manifest.RedRibbonHQ.TrungUyThep;
import boss.boss_manifest.RedRibbonHQ.TrungUyTrang;
import boss.boss_manifest.RedRibbonHQ.TrungUyXanhLo;
import clan.Clan;
import map.Zone;
import mob.Mob;
import player.Player;
import services.func.ChangeMapService;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import map.ItemMap;
import server.Maintenance;
import services.ItemMapService;
import services.ItemTimeService;
import services.MapService;
import services.Service;
import utils.Util;

@Data
public class RedRibbonHQ implements Runnable {

    //bang hội đủ số người mới đc mở
    public static final int N_PLAYER_CLAN = 0;
    //số người đứng cùng khu
    public static final int N_PLAYER_MAP = 1;
    public static final int AVAILABLE = 50;
    public static final int TIME_DOANH_TRAI = 1800000;
    public static final int TIME_PICK_DOANH_TRAI = 300000;

    public int id;
    public final List<Zone> zones;
    private Clan clan;

    private long lastTimeOpen;
    public boolean isOpened;
    public boolean isTimePicking;
    public long lastTimePick;
    public boolean winDT;
    public List<Boss> bosses = new ArrayList<>();

    public RedRibbonHQ(int id) {
        this.id = id;
        this.zones = new ArrayList<>();
    }

    public void addZone(Zone zone) {
        this.zones.add(zone);
    }

    public Zone getMapById(int mapId) {
        for (Zone zone : this.zones) {
            if (zone.map.mapId == mapId) {
                return zone;
            }
        }
        return null;
    }

    @Override
    public void run() {
        while (!Maintenance.isRunning && isOpened) {
            try {
                long startTime = System.currentTimeMillis();
                update();
                long elapsedTime = System.currentTimeMillis() - startTime;
                long sleepTime = 150 - elapsedTime;
                if (sleepTime > 0) {
                    Functions.sleep(sleepTime);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void openDoanhTrai(Player player) {
        try {
            this.lastTimeOpen = System.currentTimeMillis();
            this.clan = player.clan;
            player.clan.doanhTrai = this;
            player.clan.playerOpenDoanhTrai = player;
            player.clan.lastTimeOpenDoanhTrai = this.lastTimeOpen;
            player.clan.haveGoneDoanhTrai = false;
            sendTextDoanhTrai();
            //Khởi tạo quái, boss
            this.isOpened = true;
            this.init();
        } catch (Exception e) {
            e.printStackTrace();
            player.clan.lastTimeOpenDoanhTrai = 0;
            player.clan.haveGoneDoanhTrai = false;
            this.dispose();
            return;
        }
        List<Player> plJoinDT = new ArrayList();
        //Đưa thành viên vào doanh trại
        for (Player pl : player.zone.getPlayers()) {
            if (pl != null && !pl.equals(player) && pl.clan != null
                    && pl.clan.equals(player.clan) && pl.location.x >= 1285
                    && pl.location.x <= 1645) {
                plJoinDT.add(pl);
            }
        }
        for (Player pl : plJoinDT) {
            if (pl.clanMember.getNumDateFromJoinTimeToToday() < 1 || pl.isDie()) {
                continue;
            }
            pl.lastTimeJoinDT = System.currentTimeMillis();
            ChangeMapService.gI().changeMapInYard(pl, 53, -1, 60);
        }
        player.lastTimeJoinDT = System.currentTimeMillis();
        ChangeMapService.gI().changeMapInYard(player, 53, -1, 60);
    }

    private void init() {
        long totalDamage = 0;
        long totalHp = 0;

        for (Player player : this.clan.membersInGame) {
            if (player != null && player.nPoint != null) {
                totalDamage += player.nPoint.dame;
                totalHp += player.nPoint.hpMax;
            }
        }

        // Hồi sinh quái
        for (Zone zone : this.zones) {
            for (Mob mob : zone.mobs) {
                long mobTempId = mob.tempId;
                mob.point.dame = Util.maxIntValue((mobTempId != 0) ? totalHp / mobTempId : 0);
                mob.point.maxHp = Util.maxIntValue((mobTempId != 0) ? totalDamage * mobTempId : 0);
                mob.lvMob = 0;
                mob.hoiSinh();
                mob.hoiSinhMobPhoBan();
            }

            long dame = totalHp / 40; // giảm sát thương của boss
            long hp = totalDamage * 25; // giảm HP của boss

            if (zone.map.mapId == 59) {
                try {
                    long bossDamage = Util.maxIntValue(dame);
                    long bossMaxHealth = Util.maxIntValue(hp);
                    bosses.add(new TrungUyTrang(
                            zone,
                            bossDamage,
                            bossMaxHealth
                    ));
                } catch (Exception e) {
                }
            }
            if (zone.map.mapId == 62) {
                try {
                    long bossDamage = Util.maxIntValue((dame * 1.1));
                    long bossMaxHealth = Util.maxIntValue((hp * 1.1));
                    bosses.add(new TrungUyXanhLo(
                            zone,
                            bossDamage,
                            bossMaxHealth
                    ));
                } catch (Exception e) {
                }
            }
            if (zone.map.mapId == 55) {
                try {
                    long bossDamage = Util.maxIntValue((dame * 1.15));
                    long bossMaxHealth = Util.maxIntValue((hp * 1.15));
                    bosses.add(new TrungUyThep(
                            zone,
                            bossDamage,
                            bossMaxHealth
                    ));
                } catch (Exception e) {
                }
            }
            if (zone.map.mapId == 54) {
                try {
                    long bossDamage = Util.maxIntValue((dame * 1.2));
                    long bossMaxHealth = Util.maxIntValue((hp * 1.2));
                    bosses.add(new NinjaAoTim(
                            zone,
                            clan,
                            bossDamage,
                            bossMaxHealth
                    ));
                } catch (Exception e) {
                }
            }

            if (zone.map.mapId == 57) {
                try {
                    long bossDamage = Util.maxIntValue((dame * 1.3));
                    long bossMaxHealth = Util.maxIntValue((hp * 1.3));
                    for (int i = 0; i < 4; i++) {
                        bosses.add(new RobotVeSi(
                                zone,
                                i,
                                bossDamage,
                                bossMaxHealth
                        ));
                    }
                } catch (Exception e) {
                }
            }
        }
        Thread.ofVirtual()
                .name("Doanh Trại: " + this.clan.name)
                .start(this);

    }

    public void update() {
        if (isOpened) {
            if ((!isTimePicking && Util.canDoWithTime(lastTimeOpen, TIME_DOANH_TRAI)) || (isTimePicking && Util.canDoWithTime(lastTimePick, TIME_PICK_DOANH_TRAI))) {
                finish();
                dispose();
            }

            boolean allCharactersDead = true;
            for (Zone zone : zones) {
                for (Mob mob : zone.mobs) {
                    if (!mob.isDie()) {
                        allCharactersDead = false;
                        break;
                    }
                }

                if (allCharactersDead) {
                    for (Player boss : zone.getBosses()) {
                        if (!boss.isDie()) {
                            allCharactersDead = false;
                            break;
                        }
                    }
                }
            }
            if (allCharactersDead && !winDT) {
                winDT = true;
                for (Zone zone : zones) {
                    List<Player> players = zone.getPlayers();
                    for (Player pl : players) {
                        Service.gI().sendThongBao(pl, "Mau đi tìm Độc Nhãn");
                    }
                }
            }

        }
    }

    public ItemMap NR(Zone zone) {
        int x = Util.nextInt(100, zone.map.mapWidth - 100);
        int y = zone.map.yPhysicInTop(x, 100);
        int nr = Util.isTrue(1, 500) ? Util.nextInt(14, 18) : Util.nextInt(16, 20);
        ItemMap it = new ItemMap(zone, nr, 1, x, y, -1);
        return it;
    }

    public void randomNR() {
        for (Zone zone : zones) {
            Service.gI().dropItemMap(zone, NR(zone));
            Service.gI().dropItemMap(zone, NR(zone));
            Service.gI().dropItemMap(zone, NR(zone));
            if (Util.isTrue(1, 2)) {
                Service.gI().dropItemMap(zone, NR(zone));
            }
            if (Util.isTrue(1, 3)) {
                Service.gI().dropItemMap(zone, NR(zone));
            }
            if (Util.isTrue(1, 4)) {
                Service.gI().dropItemMap(zone, NR(zone));
            }
            if (Util.isTrue(1, 5)) {
                Service.gI().dropItemMap(zone, NR(zone));
            }
        }
    }

    public void finish() {
        for (Zone zone : zones) {
            for (int i = zone.getPlayers().size() - 1; i >= 0; i--) {
                if (i < zone.getPlayers().size()) {
                    Player pl = zone.getPlayers().get(i);
                    kickOutOfDT(pl);
                }
            }
        }
    }

    private void kickOutOfDT(Player player) {
        if (MapService.gI().isMapDoanhTrai(player.zone.map.mapId)) {
            Service.gI().sendThongBao(player, "Đã hết thời gian, bạn sẽ được đưa về nhà");
            ChangeMapService.gI().changeMapBySpaceShip(player, 21 + player.gender, -1, -1);
        }
    }

    private void sendTextDoanhTrai() {
        for (Player pl : this.clan.membersInGame) {
            ItemTimeService.gI().sendTextDoanhTrai(pl);
        }
    }

    public void sendTextTimePickDoanhTrai() {
        for (Player pl : this.clan.membersInGame) {
            ItemTimeService.gI().sendTextTimePickDoanhTrai(pl);
        }
    }

    private void removeTextDoanhTrai() {
        for (Player pl : this.clan.membersInGame) {
            ItemTimeService.gI().removeTextDoanhTrai(pl);
        }
    }

    public void updateHPDame() {
        long totalDame = 0;
        long totalHp = 0;
        for (Player pl : this.clan.membersInGame) {
            if (pl != null && pl.nPoint != null) {
                totalDame += pl.nPoint.dame;
                totalHp += pl.nPoint.hpMax;
            }
        }

        //update hp dame quái
        for (Zone zone : this.zones) {
            for (Mob mob : zone.mobs) {
                if (mob.isDie()) {
                    continue;
                }
                mob.point.dame = Util.maxIntValue(totalHp / mob.tempId);
                mob.point.maxHp = Util.maxIntValue(totalDame * mob.tempId);
                mob.point.hp = mob.point.maxHp;
                mob.setTiemNang();
            }
        }

        long dame = totalHp / 40;
        long hp = totalDame * 25;
        for (Boss boss : bosses) {
            if (boss.isDie()) {
                continue;
            }
            if (boss.zone.map.mapId == 59) {
                try {
                    long bossDamage = (dame);
                    long bossMaxHealth = (hp);
                    bossDamage = Util.maxIntValue(bossDamage);
                    bossMaxHealth = Util.maxIntValue(bossMaxHealth);
                    boss.nPoint.hpMax = bossMaxHealth;
                    boss.nPoint.dame = bossDamage;
                    boss.nPoint.hp = boss.nPoint.hpMax;
                } catch (Exception exception) {
                }
            }
            if (boss.zone.map.mapId == 62) {
                try {
                    long bossDamage = (long) (dame * 1.1);
                    long bossMaxHealth = (long) (hp * 1.1);
                    bossDamage = Util.maxIntValue(bossDamage);
                    bossMaxHealth = Util.maxIntValue(bossMaxHealth);
                    boss.nPoint.hpMax = bossMaxHealth;
                    boss.nPoint.dame = bossDamage;
                    boss.nPoint.hp = boss.nPoint.hpMax;
                } catch (Exception exception) {
                }
            }
            if (boss.zone.map.mapId == 55) {
                try {
                    long bossDamage = (long) (dame * 1.15);
                    long bossMaxHealth = (long) (hp * 1.15);
                    bossDamage = Util.maxIntValue(bossDamage);
                    bossMaxHealth = Util.maxIntValue(bossMaxHealth);
                    boss.nPoint.hpMax = bossMaxHealth;
                    boss.nPoint.dame = bossDamage;
                    boss.nPoint.hp = boss.nPoint.hpMax;
                } catch (Exception exception) {
                }
            }
            if (boss.zone.map.mapId == 54) {
                try {
                    long bossDamage = (long) (dame * 1.2);
                    long bossMaxHealth = (long) (hp * 1.2);
                    bossDamage = Util.maxIntValue(bossDamage);
                    bossMaxHealth = Util.maxIntValue(bossMaxHealth);
                    if (boss.id >= -14 && boss.id <= -9) {
                        bossDamage /= 10;
                        bossMaxHealth /= 10;
                    }
                    boss.nPoint.hpMax = bossMaxHealth;
                    boss.nPoint.dame = bossDamage;
                    boss.nPoint.hp = boss.nPoint.hpMax;
                } catch (Exception exception) {
                }
            }

            if (boss.zone.map.mapId == 57) {
                try {
                    long bossDamage = (long) (dame * 1.3);
                    long bossMaxHealth = (long) (hp * 1.3);
                    bossDamage = Util.maxIntValue(bossDamage);
                    bossMaxHealth = Util.maxIntValue(bossMaxHealth);
                    boss.nPoint.hpMax = bossMaxHealth;
                    boss.nPoint.dame = bossDamage;
                    boss.nPoint.hp = boss.nPoint.hpMax;
                } catch (Exception exception) {
                }
            }
        }

    }

    private void dispose() {
        this.removeTextDoanhTrai();
        // remove bosses
        for (Boss boss : bosses) {
            if (!boss.isDie()) {
                boss.leaveMap();
            }
        }
        // remove itemmap
        for (Zone zone : zones) {
            for (int i = zone.items.size() - 1; i >= 0; i--) {
                if (i < zone.items.size()) {
                    ItemMapService.gI().removeItemMap(zone.items.get(i));
                }
            }
        }
        this.bosses.clear();
        this.winDT = false;
        this.isOpened = false;
        this.clan.haveGoneDoanhTrai = true;
        this.clan.doanhTrai = null;
        this.clan = null;
        this.isTimePicking = false;
    }
}
