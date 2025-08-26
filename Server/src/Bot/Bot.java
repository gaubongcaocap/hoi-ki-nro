package Bot;

import consts.ConstPlayer;

import java.util.List;
import java.util.Random;
import map.Map;
import map.Zone;
import models.MapBot;
import models.Template.SkillTemplate;
import player.Player;
import server.Manager;
import static server.Manager.CLANS;
import services.EffectSkillService;
import services.MapService;
import services.PlayerService;
import services.Service;
import services.SkillService;
import services.func.ChangeMapService;
import skill.NClass;
import skill.Skill;
import utils.Util;

public class Bot extends Player {

    private short head_;
    private short body_;
    private short leg_;
    private short flag_;
    private int type;
    private int index_ = 0;
    public ShopBot shop;
    public Sanb boss;
    public Mobb mo1;

    private long lastTimeIncrease;

    private MapBot mapBot;

    public Bot(MapBot mapBot) {
        this.mapBot = mapBot;
    }

    // private Player plAttack;
    private int[] TraiDat = new int[]{1, 2, 3, 4, 6, 29, 30, 28, 27, 42};
    private int[] Namec = new int[]{8, 9, 10, 11, 12, 13, 33, 34, 32, 31};
    private int[] XayDa = new int[]{15, 16, 17, 18, 19, 20, 37, 36, 35, 44, 52};

    public Bot(short head, short body, short leg, int type, String name, ShopBot shop, short flag) {
        this.head_ = head;
        this.body_ = body;
        this.leg_ = leg;
        this.shop = shop;
        this.name = name;
        this.id = new Random().nextInt(2000000000);
        this.type = type;
        this.isBot = true;
        this.flag_ = flag;
        this.itemTime.isUseTDLT = true;
        this.clan = null;
    }

    public int MapToPow() {
        Random random = new Random();
        long power = this.nPoint.power;
        int mapId = 0;

        int randomIndex = random.nextInt(Manager.mapBot.size());  // Lấy ngẫu nhiên chỉ số trong dãy
        mapId = Manager.mapBot.get(randomIndex) ; 

        // if (this.type == 0 ) { // Dưới 10 tỷ
        //     mapId = 0 + random.nextInt(44); 
        // } else if (this.type == 1) {
        //     mapId = 0 + random.nextInt(20); 
        // } else if (power < 40_000_000_000L) { // Dưới 10 tỷ
        //     mapId = 62 + random.nextInt(15); // Random từ 62 đến 76
        // } else if (power < 100_000_000_000L) { // 10 tỷ - 40 tỷ
        //     if (Util.isTrue(50, 100)) {
        //         mapId = 91 + random.nextInt(3); // 30% map từ 91 đến 93
        //     } else {
        //         mapId = 95 + random.nextInt(5); // 30% map từ 95 đến 99
        //     }
        // } else { // Trên 40 tỷ
        //     mapId = 104 + random.nextInt(6); // Random từ 104 đến 109
        // }

        return mapId;
    }

    public void joinMap() {
        Zone zone = getRandomZone(MapToPow());
        if (zone != null) {
            ChangeMapService.gI().goToMap(this, zone);

            this.zone.load_Me_To_Another(this);
            this.mo1.lastTimeChanM = System.currentTimeMillis();
        }
    }

    public Zone getRandomZone(int mapId) {
        Map map = MapService.gI().getMapById(mapId);
        Zone zone = null;

        try {
            if (map != null) {
                int attempts = 0;
                do {
                    zone = map.zones.get(Util.nextInt(0, map.zones.size() - 1));
                    attempts++;
                } while (zone.hasBot() && attempts < 10); // Tránh khu có bot khác
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (zone != null) {
            this.index_ = 0;
            return zone;
        } else {
            this.index_ += 1;
            if (this.index_ >= 20) {
                BotManager.gI().bot.remove(this);
                ChangeMapService.gI().exitMap(this);
                return null;
            } else {
                return getRandomZone(MapToPow()); // Thử lại với map khác
            }
        }
    }

    @Override
    public short getHead() {
        if (effectSkill.isMonkey) {
            return (short) ConstPlayer.HEADMONKEY[effectSkill.levelMonkey - 1];
        } else {
            return this.head_;
        }
    }

    @Override
    public short getBody() {
        if (effectSkill.isMonkey) {
            return 193;
        } else {
            return this.body_;
        }
    }

    @Override
    public short getLeg() {
        if (effectSkill.isMonkey) {
            return 194;
        } else {
            return this.leg_;
        }
    }

    @Override
    public short getFlagBag() {
        return this.flag_;
    }

    @Override
    public void update() {
        super.update();

        if (System.currentTimeMillis() - lastTimeIncrease > 30000) {
            increasePoint();
            lastTimeIncrease = System.currentTimeMillis();
        }

        switch (type) {
            case 0 -> mo1.update();
            case 1 -> shop.update();
            case 2 -> boss.update();
        }
        // Hồi sinh nếu chết
        if (isDie()) {
            Service.gI().hsChar(this, nPoint.hpMax, nPoint.mpMax);
        }
    }

    public void leakSkill() {
        // for (NClass n : Manager.gI().NCLASS) {
        for (NClass n : Manager.NCLASS) {
            if (n.classId == this.gender) {
                for (SkillTemplate Template : n.skillTemplatess) {
                    for (Skill skills : Template.skillss) {
                        Skill cloneSkill = new Skill(skills);
                        this.playerSkill.skills.add(cloneSkill);
                        break;
                    }
                }
                break;
            }
        }
    }

    public boolean UseLastTimeSkill() {
        // Nếu chưa có kỹ năng thì không tung skill
        if (this.playerSkill == null || this.playerSkill.skillSelect == null) {
            return false;
        }
        long now = System.currentTimeMillis();
        if (this.playerSkill.skillSelect.lastTimeUseThisSkillbot < 
            (now - this.playerSkill.skillSelect.coolDown)) {
            this.playerSkill.skillSelect.lastTimeUseThisSkillbot = now;
            return true;
        }
        return false;
    }

    private void increasePoint() {
        if (nPoint != null) {
            boolean tangHp = Util.isTrue(50, 100);
            int point = tangHp ? 100 : 10;
            long tienNang = tangHp
                    ? point * (2 * (nPoint.hpg + 1000) + point * 20 - 20) / 2
                    : point * (2 * nPoint.dameg + point - 1) / 2 * 100;

            if (doUseTiemNang(tienNang)) {
                if (tangHp) {
                    nPoint.hpMax += point;
                    nPoint.hpg += point;
                } else {
                    nPoint.dameg += point;
                }
                Service.gI().point(this);
            }
        }
    }

    private boolean doUseTiemNang(long tiemNang) {
        if (this.nPoint.tiemNang < tiemNang) {
            return false;
        } else {
            this.nPoint.tiemNang -= tiemNang;
            return true;
        }
    }

    public void useSkill(int skillId) {
        Thread.startVirtualThread(() -> {
            switch (skillId) {
                case Skill.BIEN_KHI:
                    EffectSkillService.gI().sendEffectMonkey(this);
                    EffectSkillService.gI().setIsMonkey(this);
                    EffectSkillService.gI().sendEffectMonkey(this);

                    Service.gI().sendSpeedPlayer(this, 0);
                    Service.gI().Send_Caitrang(this);
                    Service.gI().sendSpeedPlayer(this, -1);
                    PlayerService.gI().sendInfoHpMp(this);
                    Service.gI().point(this);
                    Service.gI().Send_Info_NV(this);
                    Service.gI().sendInfoPlayerEatPea(this);
                    break;
                case Skill.QUA_CAU_KENH_KHI:
                    this.playerSkill.prepareQCKK = !this.playerSkill.prepareQCKK;
                    this.playerSkill.lastTimePrepareQCKK = System.currentTimeMillis();
                    SkillService.gI().sendPlayerPrepareSkill(this, 1000);
                    break;
                case Skill.MAKANKOSAPPO:
                    this.playerSkill.prepareLaze = !this.playerSkill.prepareLaze;
                    this.playerSkill.lastTimePrepareLaze = System.currentTimeMillis();
                    SkillService.gI().sendPlayerPrepareSkill(this, 3000);
                    break;
            }
        });
    }

}
