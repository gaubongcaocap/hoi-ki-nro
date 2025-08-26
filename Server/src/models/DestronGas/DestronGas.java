package models.DestronGas;

/*
 *
 *
 * @author NTD
 */
import EMTI.Functions;
import boss.Boss;
import boss.boss_manifest.DestronGas.DrLychee;
import clan.Clan;
import map.Zone;
import mob.Mob;
import player.Player;
import services.ItemTimeService;
import services.MapService;
import services.Service;
import services.func.ChangeMapService;
import utils.Util;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import server.Maintenance;
import services.ClanService;
import services.ItemMapService;
import utils.TimeUtil;

@Data
public class DestronGas implements Runnable {

    public static final long POWER_CAN_GO_TO_KHI_GAS_HUY_DIET = 2000000000;
    public static final int AVAILABLE = 50;
    public static final int TIME_KHI_GAS_HUY_DIET = 1800000;
    //bang hội đủ số người mới đc mở
    public static final int N_PLAYER_CLAN = 0;

    public int id;
    public byte level;
    public final List<Zone> zones;

    public Clan clan;
    public boolean isOpened;
    private long lastTimeOpen;
    private long lastTimeUpdateMessage;
    private boolean kickoutkghd;
    private long timeKickOutKGHD;
    public List<Boss> bosses = new ArrayList<>();
    private boolean callBoss;
    public boolean hatchiyatchDead;

    public DestronGas(int id) {
        this.id = id;
        this.zones = new ArrayList<>();
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

    public void update() {
        if (isOpened) {
            if (Util.canDoWithTime(lastTimeOpen, TIME_KHI_GAS_HUY_DIET) || (kickoutkghd && Util.canDoWithTime(timeKickOutKGHD, 60000))) {
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
            }

            if (allCharactersDead && !callBoss) {
                try {
                    long bossDamage = (10000 * level);
                    long bossMaxHealth = (15000000 * level);

                    bossDamage = Util.maxIntValue((bossDamage * 1.5));
                    bossMaxHealth = Util.maxIntValue((bossMaxHealth * 1.5));
                    bosses.add(new DrLychee(
                            getMapById(148),
                            clan,
                            level,
                             bossDamage,
                             bossMaxHealth
                    ));
                    callBoss = true;
                } catch (Exception exception) {
                }
            }

            if (!kickoutkghd && (hatchiyatchDead || Util.canDoWithTime(lastTimeOpen, TIME_KHI_GAS_HUY_DIET - 60000))) {
                kickoutkghd = true;
                timeKickOutKGHD = System.currentTimeMillis();
                for (Zone zone : zones) {
                    List<Player> players = zone.getPlayers();
                    for (Player pl : players) {
                        Service.gI().sendThongBao(pl, "Nơi này sắp nổ tung mau chạy đi");
                    }

                }
            }
            if (kickoutkghd && Util.canDoWithTime(lastTimeUpdateMessage, 10000)) {
                lastTimeUpdateMessage = System.currentTimeMillis();
                for (Zone zone : zones) {
                    List<Player> players = zone.getPlayers();
                    for (Player pl : players) {
                        Service.gI().sendThongBao(pl, "Về làng Aru sau " + TimeUtil.getTimeLeft(timeKickOutKGHD, 60) + " nữa");
                    }
                }
            }

        }
    }

    public void openKhiGasHuyDiet(Player plOpen, Clan clan, byte level) {
        try {
            this.level = level;
            this.lastTimeOpen = System.currentTimeMillis();
            this.clan = clan;
            this.clan.lastTimeOpenKhiGasHuyDiet = this.lastTimeOpen;
            this.clan.playerOpenKhiGasHuyDiet = plOpen;
            this.clan.KhiGasHuyDiet = this;
            this.callBoss = false;
            this.isOpened = true;
            this.init();
            sendTextKhiGasHuyDiet();
        } catch (Exception e) {
            plOpen.clan.lastTimeOpenKhiGasHuyDiet = 0;
            this.dispose();
        }
    }

    private void init() {
        //Hồi sinh quái
        for (Zone zone : this.zones) {
            List<Mob> mobs = zone.mobs;
            for (int i = 0; i < mobs.size(); i++) {
                Mob mob = mobs.get(i);
                if (((i == 5 || i == 10) && zone.map.mapId == 149) || ((i == 5 || i == 10 || i == 15)
                        && zone.map.mapId == 147) || ((i == 5 || i == 10 || i == 15 || i == 20 || i == 25)
                        && zone.map.mapId == 152) || (i == 5 && zone.map.mapId == 151)
                        || ((i == 5 || i == 10) && zone.map.mapId == 148)) {
                    mob.lvMob = 1;
                    mob.point.dame = Util.maxIntValue(level * 31 * 5 * mob.tempId * 10);
                    mob.point.maxHp = Util.maxIntValue(level * 3107 * 5 * mob.tempId * 10);
                    mob.hoiSinh();
                    mob.hoiSinhMobPhoBan();
                } else {
                    mob.lvMob = mob.tempId == 76 ? 1 : 0;
                    mob.point.dame = Util.maxIntValue(level * 31 * 5 * mob.tempId);
                    mob.point.maxHp = Util.maxIntValue(level * 3107 * 5 * mob.tempId);
                    mob.hoiSinh();
                    mob.hoiSinhMobPhoBan();
                }
            }
        }
        new Thread(this, "Khí Gas Hủy Diệt: " + this.clan.name).start();
    }

    //kết thúc khí gas hủy diệt
    public void finish() {
        for (Zone zone : zones) {
            for (int i = zone.getPlayers().size() - 1; i >= 0; i--) {
                if (i < zone.getPlayers().size()) {
                    Player pl = zone.getPlayers().get(i);
                    kickOutOfKGHD(pl);
                }
            }

        }
    }

    private void kickOutOfKGHD(Player player) {
        if (MapService.gI().isMapKhiGasHuyDiet(player.zone.map.mapId)) {
            ChangeMapService.gI().changeMapBySpaceShip(player, 0, -1, -1);
        }
    }

    public Zone getMapById(int mapId) {
        for (Zone zone : this.zones) {
            if (zone.map.mapId == mapId) {
                return zone;
            }
        }
        return null;
    }

    private void sendTextKhiGasHuyDiet() {
        for (Player pl : this.clan.membersInGame) {
            ItemTimeService.gI().sendTextKhiGasHuyDiet(pl);
        }
    }

    private void removeTextKhiGasHuyDiet() {
        for (Player pl : this.clan.membersInGame) {
            ItemTimeService.gI().removeTextKhiGasHuyDiet(pl);
        }
    }

    public void dispose() {
        for (Zone zone : zones) {
            for (int i = zone.items.size() - 1; i >= 0; i--) {
                if (i < zone.items.size()) {
                    ItemMapService.gI().removeItemMap(zone.items.get(i));
                }
            }
        }
        for (Boss boss : bosses) {
            if (!boss.isDie()) {
                boss.leaveMap();
            }
        }
        this.removeTextKhiGasHuyDiet();
        this.bosses.clear();
        this.isOpened = false;
        this.clan.KhiGasHuyDiet = null;
        this.clan = null;
        this.kickoutkghd = false;
        this.hatchiyatchDead = false;
    }
}
