package services;

import java.util.HashMap;
import java.util.*;

import map.WayPoint;
import player.Player;

public class TaskDirections {

    class DirectionsData {

        private static HashMap<Integer, List<Integer>> MyLinkMaps;

        public static void LoadLinkMapBase() {
            if (MyLinkMaps != null) {
                return;
            }
            MyLinkMaps = new HashMap<>();
            LoadLinkMapsAutoWaypoint();
            LoadLinkMapsNPC();
        }

        private static void LoadLinkMapsAutoWaypoint() {
            try {
                String[] data = new String[]{
                        "21 0",
                        "22 7",
                        "23 13",
                        "84 24",
                        "84 25",
                        "84 26",
                        "19 109",
                        "42 0 1 2 3 4 5 6",
                        "3 27 28 29 30",
                        "2 24",
                        "1 47",
                        "5 29",
                        "43 7 8 9 11 12 13 10",
                        "11 31 32 33 34",
                        "9 25",
                        "13 33",
                        "52 44 14 15 16 17 18 20 19",
                        "17 35 36 37 38",
                        "16 26",
                        "20 37",
                        "68 69 70 71 72 64 65 63 66 67 73 74 75 76 77 81 82 83 79 80 105",
                        "102 92 93 94 96 97 98 99 100 103",
                        "109 108 107 110 106",
                        "109 105",
                        "109 106",
                        "106 107",
                        "108 105",
                        "131 132 133",
                        "80 105",
                        "47 46 45"
                };

                for (String text : data) {
                    int[] array = Arrays.stream(text.split(" "))
                            .mapToInt(Integer::parseInt)
                            .toArray();

                    for (int i = 0; i < array.length; i++) {
                        if (i != 0) {
                            LoadLinkMap(array[i], array[i - 1]);
                        }
                        if (i != array.length - 1) {
                            LoadLinkMap(array[i], array[i + 1]);
                        }
                    }
                }
            } catch (Exception ex) {
                MyLinkMaps = null;
                // Handle exception (assuming GameScr.info1.addInfo is for logging)
                System.err.println("Error loading LinkMapsAutoWaypoint: " + ex.getMessage());
            }
        }

        private static void LoadLinkMapsNPC() {
            try {
                String[] data = new String[]{
                        "24 25 1 10 0",
                        "25 24 1 11 0",
                        "24 26 1 10 1",
                        "26 24 1 12 0",
                        "25 26 1 11 1",
                        "26 25 1 12 1",
                        "24 84 1 10 2",
                        "25 84 1 11 2",
                        "26 84 1 12 2",
                        "19 68 1 12 1",
                        "68 19 1 12 0",
                        "80 131 1 60 0",
                        "19 109 1 12 0",
                        "27 102 1 38 1",
                        "28 102 1 38 1",
                        "29 102 1 38 1",
                        "102 24 1 38 1"
                };

                for (String text : data) {
                    int[] array = Arrays.stream(text.split(" "))
                            .mapToInt(Integer::parseInt)
                            .toArray();
                    int numNextMaps = array.length - 3;
                    int[] nextMapIds = Arrays.copyOfRange(array, 3, 3 + numNextMaps);
                    LoadLinkMap(array[0], array[1]);
                }
            } catch (Exception ex) {
                System.err.println("Error loading LinkMaps: " + ex.getMessage());
            }

        }

        private static void LoadLinkMap(int idMapStart, int idMapNext) {
            AddKeyLinkMaps(idMapStart);
            MyLinkMaps.get(idMapStart).add(idMapNext);
        }

        private static void AddKeyLinkMaps(int idMap) {
            if (!MyLinkMaps.containsKey(idMap)) {
                List<Integer> maps = new ArrayList<Integer>();
                MyLinkMaps.put(idMap, maps);
            }
        }

        public static List<Integer> GetMapNexts(int idMap) {
            if (CanGetMapNexts(idMap)) {
                for (Integer key : MyLinkMaps.keySet()) {
                    if (key == idMap) {
                        return MyLinkMaps.get(key);
                    }
                }
            }
            return null;
        }

        public static boolean CanGetMapNexts(int idMap) {
            return MyLinkMaps.containsKey(idMap);
        }
    }

    class DirectionsAlgorithm {

        static List<Integer> findWay(int idMapStart, int idMapEnd) {
            List<Integer> wayPassedStart = getWayPassedStart(idMapStart);
            return findWay(idMapEnd, wayPassedStart);
        }

        private static List<Integer> findWay(int idMapEnd, List<Integer> wayPassed) {
            int num = wayPassed.get(wayPassed.size() - 1);
            if (num == idMapEnd) {
                return wayPassed;
            }
            if (!DirectionsData.CanGetMapNexts(num)) {
                return null;
            }
            List<List<Integer>> list = new ArrayList<>();
            var maps = DirectionsData.GetMapNexts(num);
            for (int nextId : maps) {
                List<Integer> list2 = null;
                if (!wayPassed.contains(nextId)) {
                    List<Integer> wayPassedNext = getWayPassedNext(wayPassed, nextId);
                    list2 = findWay(idMapEnd, wayPassedNext);
                }
                if (list2 != null) {
                    list.add(list2);
                }
            }
            return getBestWay(list);
        }

        private static List<Integer> getBestWay(List<List<Integer>> ways) {
            if (ways.isEmpty()) {
                return null;
            }
            List<Integer> list = ways.get(0);
            for (int i = 1; i < ways.size(); i++) {
                if (isWayBetter(ways.get(i), list)) {
                    list = ways.get(i);
                }
            }
            return list;
        }

        private static List<Integer> getWayPassedStart(int idMapStart) {
            List<Integer> wayPassed = new ArrayList<>();
            wayPassed.add(idMapStart);
            return wayPassed;
        }

        private static List<Integer> getWayPassedNext(List<Integer> wayPassed, int idMapNext) {
            List<Integer> wayPassedNext = new ArrayList<>(wayPassed);
            wayPassedNext.add(idMapNext);
            return wayPassedNext;
        }

        private static boolean isWayBetter(List<Integer> way1, List<Integer> way2) {
            boolean isBadWay1 = isBadWay(way1);
            boolean isBadWay2 = isBadWay(way2);
            if (isBadWay1 && !isBadWay2) {
                return false;
            }
            if (!isBadWay1 && isBadWay2) {
                return true;
            }
            return way1.size() <= way2.size();
        }

        private static boolean isBadWay(List<Integer> way) {
            return isWayGoFutureAndBack(way);
        }

        private static boolean isWayGoFutureAndBack(List<Integer> way) {
            List<Integer> list = new ArrayList<>();
            list.addAll(Arrays.asList(27, 28, 29));
            for (int i = 1; i < way.size() - 1; i++) {
                if (way.get(i) == 102 && way.get(i + 1) == 24 && list.contains(way.get(i - 1))) {
                    return true;
                }
            }
            return false;
        }
    }

    public static WayPoint NextMap(Player player) {
        DirectionsData.LoadLinkMapBase();
        var task = player.playerTask.taskMain;
        var mapID = TaskService.gI().transformMapId(player, task.subTasks.get(task.index).mapId);
        var maps = DirectionsAlgorithm.findWay(player.zone.map.mapId, mapID);
        WayPoint wp = null;
        if (maps != null && maps.size() > 1) {
            if (player.zone != null) {
                for (var map : player.zone.map.wayPoints) {
                    if (map != null && map.goMap == maps.get(1)) {
                        wp = map;
                        break;
                    }
                }
            }
        }
        return wp;
    }

    public static int[] getPoint(Player player, WayPoint wp) {
        int[] point = new int[]{0, 0};
        if(player.zone == null){
            return point;
        }
        if (wp != null) {
            if (player.zone.map.mapId == 21 || player.zone.map.mapId == 22 || player.zone.map.mapId == 23) {
                point[0] = 0;
                point[1] = 0;
            } else {
                int dir = wp.maxX > 100 ? 1 : -1;
//            int x = wp.minX + (wp.maxX - wp.minX) / 2, y = wp.maxY - ((wp.minX <= 100) ? 48 : 24);
                int mapWidth = player.zone.map.mapWidth;
                int x_hint, y_hint;
                if (wp.isEnter) {
                    if (wp.maxX == mapWidth / 2) {
                        x_hint = mapWidth - (wp.minX + 60);
                    } else if (wp.maxX < mapWidth / 2) {
                        x_hint = mapWidth - (wp.minX + 660);
                    } else {
                        x_hint = mapWidth - 300;
                    }
                } else {
                    x_hint = wp.maxX < (mapWidth / 2) ? 60 : mapWidth - 60;
                }
                y_hint = wp.maxY;
                point[0] = x_hint;
                point[1] = y_hint;
            }
        } else if (player.zone.map.mapId == 45 || player.zone.map.mapId == 46) {
            point[0] = 276;
            point[1] = 528;
        }
        return point;
    }
}
