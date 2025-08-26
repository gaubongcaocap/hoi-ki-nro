/*
 * Copyright by NTD
 */

package minigame.DecisionMaker;

import player.Player;

public class DecisionMakerService {

    public static int getTotalMoney(int TYPE, boolean isNormal) {
        int total = 0;
        for (DecisionMakerData data : DecisionMaker.gI().listPlayer) {
            if (data.type == TYPE && data.isNormal == isNormal) {
                total += data.money;
            }
        }
        return total;
    }

    public static String getPercent(Player player, int TYPE, boolean isNormal) {
        float percent = 0;
        for (DecisionMakerData data : DecisionMaker.gI().listPlayer) {
            if (data.id == player.id && data.isNormal == isNormal) {
                percent = (float) data.money / getTotalMoney(TYPE, isNormal) * 100;
                break;
            }
        }
        int intPart = (int) percent;
        if (percent - intPart == 0) {
            return String.valueOf((int) intPart);
        } else {
            return String.valueOf((double) Math.ceil(percent));
        }
    }

    public static void newDataResul(Player player, byte TYPE, long Money) {
        DecisionMakerData.resulPlayer data = new DecisionMakerData.resulPlayer();
        data.name = player.name;
        data.type = TYPE;
        data.money = Money;
        DecisionMaker.gI().listResulPlayer.add(data);
    }


    public static void newData(Player player, long Money, byte TYPE, boolean isNormal) {
        for (DecisionMakerData pl : DecisionMaker.gI().listPlayer) {
            if (pl.id == player.id && pl.type == TYPE && pl.isNormal == isNormal) {
                pl.money += Money;
                return;
            }
        }
        DecisionMakerData data = new DecisionMakerData();
        data.id = player.id;
        data.type = TYPE;
        data.money = Money;
        data.isNormal = isNormal;
        DecisionMaker.gI().listPlayer.add(data);
    }
}
