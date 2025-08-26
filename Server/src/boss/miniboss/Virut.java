//package boss.miniboss;
//
//import boss.Boss;
//import boss.BossData;
//import boss.BossID;
//import boss.BossStatus;
//import consts.ConstPlayer;
//import java.util.List;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.Map;
//import map.ItemMap;
//import player.Player;
//import server.Client;
//import services.ItemTimeService;
//import services.Service;
//import services.func.ChangeMapService;
//import skill.Skill;
//import utils.Util;
//
//public class Virut extends Boss {
//    private final Map<Long, Long> globalEffectTimers = new ConcurrentHashMap<>();
//
//  public Virut() throws Exception {
//        super(BossID.Virut, new BossData(
//                "Virut " + Util.nextInt(1, 49),
//                ConstPlayer.TRAI_DAT,
//                new short[]{651, 778, 779, -1, -1, -1},
//                10,
//                new long[]{100000000},
//                new int[]{5, 7, 0, 14},
//                new int[][]{{Skill.DRAGON, 7, 1000}},
//                new String[]{}, // Text chat 1
//                new String[]{}, // Text chat 2
//                new String[]{},
//                600));
//    }
//
////private void applyEffect(Player player) {
////    if (!player.hasEffect(player, 7143)) {
////        ItemTimeService.gI().sendItemTime(player, 7143, 10); 
////        this.chat("Khè Khè, " + player.name + " đã bị nhiễm virus!");        
////        long effectEndTime = System.currentTimeMillis() + 10000; // 10 giây
////        globalEffectTimers.put(player.id, effectEndTime);
////    }
//}
//
//
//
//   private void checkGlobalEffects() {
//    long currentTime = System.currentTimeMillis();
//    globalEffectTimers.forEach((playerId, effectEndTime) -> {
//        if (currentTime >= effectEndTime) {
//            Player player = Client.gI().getPlayer(playerId);
//            if (player != null && !player.isDie()) {
//                player.injured(null, player.nPoint.hp, true, false);
//                System.out.println(player.name + " đã chết vì hiệu ứng Virut!");
//            }
//            globalEffectTimers.remove(playerId);
//        }
//    });
//}
//
//
//  private void updateOdo() {
//    try {
//        if (Util.isTrue(30, 100)) {
//            List<Player> playersMap = this.zone.getNotBosses(); // Lấy danh sách player trong zone
//            for (int i = playersMap.size() - 1; i >= 0; i--) {
//                Player pl = playersMap.get(i);
//                if (pl != null && pl.nPoint != null && !this.equals(pl) && !pl.isBoss && !pl.isDie()
//                        && Util.getDistance(this, pl) <= 200) { // Kiểm tra khoảng cách giữa boss và player
//                    applyEffect(pl); // Áp dụng hiệu ứng cho player
//
//                }
//            }
//        }
//    } catch (Exception e) {
//        e.printStackTrace();
//    }
//}
//
//
//
//  @Override
//public void attack() {
//    if (Util.canDoWithTime(this.lastTimeAttack, 100) && this.typePk == ConstPlayer.PK_ALL) {
//        this.lastTimeAttack = System.currentTimeMillis();
//        try {
//            Player pl = this.getPlayerAttack();
//            if (pl == null || pl.isDie()) {
//                return;
//            }
//
//            this.playerSkill.skillSelect = this.playerSkill.skills.get(Util.nextInt(0, this.playerSkill.skills.size() - 1));
//
//            if (Util.getDistance(this, pl) <= 40) {
//                checkPlayerDie(pl);
//                if (!globalEffectTimers.containsKey(pl.id) || System.currentTimeMillis() >= globalEffectTimers.get(pl.id)) {
//                    this.updateOdo(); // Kiểm tra và lan hiệu ứng
//                }
//            } 
//            if(Util.isTrue(1, 3))
//            {
//                this.randomMove(pl);
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
//}
//private void randomMove(Player targetPlayer) {
//    try {
//        int dir = (this.location.x - targetPlayer.location.x < 0 ? 1 : -1);
//        int move = Util.nextInt(80, 100);
//        int newX = this.location.x + (dir == 1 ? move : -move);
//        int newY = this.zone.map.yPhysicInTop(newX, this.location.y);
//        newX = Math.max(0, Math.min(newX, this.zone.map.mapWidth - 1));
//        this.move(newX, newY);
//    } catch (Exception e) {
//        e.printStackTrace();
//    }
//}
//
//
//
//    @Override
//    public void reward(Player plKill) {
//        for (byte i = 0; i < 5; i++) {
//            ItemMap it = new ItemMap(this.zone, 457, (int) 5, this.location.x + i * 3,
//                    this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24), plKill.id);
//            Service.gI().dropItemMap(this.zone, it);
//        }
//    }
//
//    @Override
//    public void active() {
//        if (this.typePk == ConstPlayer.NON_PK) {
//            this.changeToTypePK();
//        }
//        this.attack();
//        this.checkGlobalEffects();
//    }
//
//    @Override
//    public void joinMap() {
//        super.joinMap();
//    }
//
//    @Override
//    public void leaveMap() {
//        ChangeMapService.gI().exitMap(this);
//        this.lastZone = null;
//        this.lastTimeRest = System.currentTimeMillis();
//        this.changeStatus(BossStatus.REST);
//    }
//}
