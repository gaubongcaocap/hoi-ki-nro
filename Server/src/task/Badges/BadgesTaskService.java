package task.Badges;

import player.Player;
import player.badges.BadgesData;
import server.Manager;

public class BadgesTaskService {

    public static void createAndResetTask(Player player) { // xử lý nếu chưa có nhiệm vụ hoặc reset hằng ngày.
        player.dataTaskBadges.clear();
        for (BadgesTaskTemplate BTT : Manager.TASKS_BADGES_TEMPLATE) {
            BadgesTask data = new BadgesTask();
            data.id = BTT.id;
            data.count = 0;
            data.countMax = BTT.count;
            data.idBadgesReward = BTT.idbadgesReward;
            player.dataTaskBadges.add(data);
        }
    }

    public static void updateDoneTask(Player player) { // xử lý nếu hoàn thành nhiệm vụ tự add vào người
        for (BadgesTask data : player.dataTaskBadges) {
            if (data.isDone()) {
                for (BadgesData bg : player.dataBadges) {
                    if (bg.idBadGes == data.idBadgesReward) {
                        return;
                    }
                }
                BadgesData danhHieu = new BadgesData(player, data.idBadgesReward, 30);
                player.dataBadges.add(danhHieu);
                data.count = 0;
            }
        }
    }

    public static void updateCountBagesTask(Player player, int id, int amount) {
        for (BadgesTask data : player.dataTaskBadges) {
            if (data.id == id) {
                data.count += amount;
                if (data.count > data.countMax) {
                    data.count = data.countMax;
                }
                break;
            }
        }
    }

    public static int sendPercenBadgesTask(Player player, int idBadgesReward) {
        for (BadgesTask data : player.dataTaskBadges) {
            if (data.idBadgesReward == idBadgesReward) {
                if (data.getPercentProcess() > 0) {
                    return data.getPercentProcess();
                } else {
                    return 0;
                }
            }
        }
        return 0;
    }

    public static int sendDay(Player player, int id) {
        for (BadgesData data : player.dataBadges) {
            if (data.idBadGes == id) {
                long timeDifference = data.timeofUseBadges - System.currentTimeMillis();
                return (int) (timeDifference / (24 * 60 * 60 * 1000L));
            }
        }
        return 0;
    }


}
