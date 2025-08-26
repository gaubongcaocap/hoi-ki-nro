/*
 * Copyright by NTD
 */

package minigame;

import minigame.DecisionMaker.DecisionMakerData;
import minigame.LuckyNumber.LuckNumberData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.sql.Timestamp;

public class Test {
    public static List<LuckNumberData> listPlayer = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println(new Timestamp(System.currentTimeMillis()).getTime());
    }
}
