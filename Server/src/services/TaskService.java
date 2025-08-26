package services;

/*
 *
 *
 * @author NTD
 */
import consts.ConstMob;
import consts.ConstNpc;
import consts.ConstPlayer;
import models.Training.TrainingService;
import player.Player;
import consts.ConstTask;
import boss.Boss;
import boss.BossID;
import clan.ClanMember;
import consts.ConstAchievement;
import consts.ConstTaskBadges;
import event.EventManager;
import item.Item;
import jdbc.daos.PlayerDAO;

import java.io.IOException;

import map.ItemMap;
import map.Zone;
import mob.Mob;
import npc.Npc;
import shop.ItemShop;
import shop.Shop;
import task.SideTaskTemplate;
import task.SubTaskMain;
import task.TaskMain;
import server.Manager;
import network.Message;
import utils.Logger;
import utils.Util;

import java.util.ArrayList;
import java.util.List;

import jdbc.daos.PlayerDAO;
import models.Achievement.AchievementService;
import server.Client;
import task.Badges.BadgesTaskService;
import task.ClanTaskTemplate;

public class TaskService {

    /**
     * Làm cùng số người trong bang
     */
    private static final byte NMEMBER_DO_TASK_TOGETHER = 0;
    private boolean canNhanCayThong = false;

    private static services.TaskService i;

    public static services.TaskService gI() {
        if (i == null) {
            i = new services.TaskService();
        }
        return i;
    }

    public TaskMain getTaskMainById(Player player, int id) {
        for (TaskMain tm : Manager.TASKS) {
            if (tm.id == id) {
                TaskMain newTaskMain = new TaskMain(tm);
                newTaskMain.detail = transformName(player, newTaskMain.detail);
                for (SubTaskMain stm : newTaskMain.subTasks) {
                    stm.mapId = (short) transformMapId(player, stm.mapId);
                    stm.npcId = (byte) transformNpcId(player, stm.npcId);
                    stm.notify = transformName(player, stm.notify);
                    stm.name = transformName(player, stm.name);
                }
                return newTaskMain;
            }
        }
        return player.playerTask.taskMain;
    }

    //gửi thông tin nhiệm vụ chính
    public void sendTaskMain(Player player) {
        Message msg = null;
        try {
            msg = new Message(40);
            msg.writer().writeShort(player.playerTask.taskMain.id);
            msg.writer().writeByte(player.playerTask.taskMain.index);
            msg.writer().writeUTF(player.playerTask.taskMain.name + "[" + player.playerTask.taskMain.id + "]");
            msg.writer().writeUTF(player.playerTask.taskMain.detail);
            msg.writer().writeByte(player.playerTask.taskMain.subTasks.size());
            for (SubTaskMain stm : player.playerTask.taskMain.subTasks) {
                msg.writer().writeUTF(stm.name);
                msg.writer().writeByte(stm.npcId);
                msg.writer().writeShort(stm.mapId);
                if (stm.notify.isEmpty()) {
                    msg.writer().writeUTF("");
                } else {
                    msg.writer().writeUTF(stm.notify);
                }
            }
            msg.writer().writeShort(player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).count);
            for (SubTaskMain stm : player.playerTask.taskMain.subTasks) {
                msg.writer().writeShort(stm.maxCount);
            }
            player.sendMessage(msg);
        } catch (Exception e) {
            Logger.logException(TaskService.class, e);
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    //chuyển sang task mới
    public void sendNextTaskMain(Player player) {
        rewardDoneTask(player); // Hoàn thành nhiệm vụ hiện tại
        switch (player.playerTask.taskMain.id) {
            case 3:
                player.playerTask.taskMain = TaskService.gI().getTaskMainById(player, player.gender + 4);
                break;
            case 4:
            case 5:
            case 6:
                player.playerTask.taskMain = TaskService.gI().getTaskMainById(player, 7);
                break;
            default:
                player.playerTask.taskMain = TaskService.gI().getTaskMainById(player, player.playerTask.taskMain.id + 1);
                break;
        }
        sendTaskMain(player); // Gửi thông tin nhiệm vụ tiếp theo
        Service.gI().sendThongBao(player, "Nhiệm vụ tiếp theo của bạn là "
                + player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).name);
    }

    //số lượng đã hoàn thành
    public void sendUpdateCountSubTask(Player player) {
        Message msg = null;
        try {
            msg = new Message(43);
            msg.writer().writeShort(player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).count);
            var wp = TaskDirections.NextMap(player);
            var point = TaskDirections.getPoint(player, wp);
            msg.writer().writeShort(point[0]);
            msg.writer().writeShort(point[1]);
            player.sendMessage(msg);
        } catch (IOException e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    //chuyển sub task tiếp theo
    public void sendNextSubTask(Player player) {
        Message msg = null;
        try {
            msg = new Message(41);
            player.sendMessage(msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    //gửi thông tin nhiệm vụ hiện tại
    public void sendInfoCurrentTask(Player player) {
        Service.gI().sendThongBao(player, "Nhiệm vụ hiện tại của bạn là "
                + player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).name);
    }

    public boolean checkDoneTaskTalkNpc(Player player, Npc npc) {
        switch (npc.tempId) {
            case ConstNpc.BERRY:
                return (doneTask(player, ConstTask.TASK_31_5));
            case ConstNpc.BARDOCK:
                return (doneTask(player, ConstTask.TASK_31_2)
                        || doneTask(player, ConstTask.TASK_31_4)
                        || doneTask(player, ConstTask.TASK_31_6)
                        || doneTask(player, ConstTask.TASK_31_9));
            case ConstNpc.QUY_LAO_KAME:
                return (doneTask(player, ConstTask.TASK_11_0)
                        || doneTask(player, ConstTask.TASK_12_0)
                        || doneTask(player, ConstTask.TASK_12_1)
                        || doneTask(player, ConstTask.TASK_13_3)
                        || doneTask(player, ConstTask.TASK_14_2)
                        || doneTask(player, ConstTask.TASK_15_4)
                        || doneTask(player, ConstTask.TASK_16_1)
                        || doneTask(player, ConstTask.TASK_17_4)
                        || doneTask(player, ConstTask.TASK_18_2)
                        || doneTask(player, ConstTask.TASK_20_6)
                        || doneTask(player, ConstTask.TASK_21_3)
                        || doneTask(player, ConstTask.TASK_22_4)
                        || doneTask(player, ConstTask.TASK_23_3)
                        || doneTask(player, ConstTask.TASK_24_2)
                        || doneTask(player, ConstTask.TASK_19_2));
            case ConstNpc.TRUONG_LAO_GURU:
                return player.gender == ConstPlayer.NAMEC && (doneTask(player, ConstTask.TASK_11_0)
                        || doneTask(player, ConstTask.TASK_12_0)
                        || doneTask(player, ConstTask.TASK_12_1)
                        || doneTask(player, ConstTask.TASK_13_3)
                        || doneTask(player, ConstTask.TASK_14_2)
                        || doneTask(player, ConstTask.TASK_15_4)
                        || doneTask(player, ConstTask.TASK_16_1)
                        || doneTask(player, ConstTask.TASK_17_4)
                        || doneTask(player, ConstTask.TASK_18_2)
                        || doneTask(player, ConstTask.TASK_20_6)
                        || doneTask(player, ConstTask.TASK_21_3)
                        || doneTask(player, ConstTask.TASK_22_4)
                        || doneTask(player, ConstTask.TASK_23_3)
                        || doneTask(player, ConstTask.TASK_19_2));
            case ConstNpc.VUA_VEGETA:
                return player.gender == ConstPlayer.XAYDA && (doneTask(player, ConstTask.TASK_11_0)
                        || doneTask(player, ConstTask.TASK_12_0)
                        || doneTask(player, ConstTask.TASK_12_1)
                        || doneTask(player, ConstTask.TASK_13_3)
                        || doneTask(player, ConstTask.TASK_15_4)
                        || doneTask(player, ConstTask.TASK_14_2)
                        || doneTask(player, ConstTask.TASK_16_1)
                        || doneTask(player, ConstTask.TASK_18_2)
                        || doneTask(player, ConstTask.TASK_17_4)
                        || doneTask(player, ConstTask.TASK_20_6)
                        || doneTask(player, ConstTask.TASK_21_3)
                        || doneTask(player, ConstTask.TASK_22_4)
                        || doneTask(player, ConstTask.TASK_23_3)
                        || doneTask(player, ConstTask.TASK_19_2));
            case ConstNpc.ONG_GOHAN:
            case ConstNpc.ONG_MOORI:
            case ConstNpc.ONG_PARAGUS:
                return (doneTask(player, ConstTask.TASK_0_2)
                        || doneTask(player, ConstTask.TASK_0_5)
                        || doneTask(player, ConstTask.TASK_1_1)
                        || doneTask(player, ConstTask.TASK_2_1)
                        || doneTask(player, ConstTask.TASK_3_2)
                        || doneTask(player, ConstTask.TASK_4_3)
                        || doneTask(player, ConstTask.TASK_5_3)
                        || doneTask(player, ConstTask.TASK_6_3)
                        || doneTask(player, ConstTask.TASK_7_3)
                        || doneTask(player, ConstTask.TASK_7_2)
                        || doneTask(player, ConstTask.TASK_8_2)
                        || doneTask(player, ConstTask.TASK_10_3)
                        || doneTask(player, ConstTask.TASK_11_1)
                        || doneTask(player, ConstTask.TASK_24_0));
            case ConstNpc.BO_MONG:
                return (doneTask(player, ConstTask.TASK_9_0)
                        || doneTask(player, ConstTask.TASK_10_1)
                        || doneTask(player, ConstTask.TASK_10_2));
            case ConstNpc.DR_DRIEF:
            case ConstNpc.CARGO:
            case ConstNpc.CUI:
                return (player.zone.map.mapId == 19 && doneTask(player, ConstTask.TASK_19_3)
                        || player.zone.map.mapId == 19 && doneTask(player, ConstTask.TASK_20_6)
                        || player.zone.map.mapId == 19 && doneTask(player, ConstTask.TASK_21_4));
            case ConstNpc.BUNMA:
            case ConstNpc.DENDE:
            case ConstNpc.APPULE:
                return doneTask(player, ConstTask.TASK_7_2);
            case ConstNpc.BUNMA_TL:
                return (doneTask(player, ConstTask.TASK_24_3)
                        || doneTask(player, ConstTask.TASK_24_5)
                        || doneTask(player, ConstTask.TASK_25_4)
                        || doneTask(player, ConstTask.TASK_26_4)
                        || doneTask(player, ConstTask.TASK_27_5)
                        || doneTask(player, ConstTask.TASK_28_5)
                        || doneTask(player, ConstTask.TASK_29_5));
            case ConstNpc.CALICK:
                return doneTask(player, ConstTask.TASK_24_1);
            case ConstNpc.THAN_MEO_KARIN:
                if (player.playerTask.taskMain.id == 29) {
                    if (player.nPoint.dameg >= 10000) {
                        return doneTask(player, ConstTask.TASK_29_0);
                    }
                }
                return doneTask(player, ConstTask.TASK_9_2);
            case ConstNpc.OSIN:
                return doneTask(player, ConstTask.TASK_30_7);
        }
        return false;
    }

    // kiểm tra hoàn thành nhiệm vụ gia nhập bang hội
    public void checkDoneTaskJoinClan(Player player) {
        if (!player.isBoss && !player.isPet) {
            doneTask(player, ConstTask.TASK_12_0);
        }
    }

    // kiểm tra hoàn thành nhiệm vụ lấy item từ rương
    public void checkDoneTaskGetItemBox(Player player) {
        if (!player.isBoss && !player.isPet) {
            doneTask(player, ConstTask.TASK_0_3);
        }
    }

    //kiểm tra hoàn thành nhiệm vụ sức mạnh
    public void checkDoneTaskPower(Player player, long power) {
        if (!player.isBoss && !player.isPet) {
            if (power >= 16000) {
                doneTask(player, ConstTask.TASK_7_0);
            }
            if (power >= 40000) {
                doneTask(player, ConstTask.TASK_8_0);
            }
            if (power >= 200000) {
                doneTask(player, ConstTask.TASK_14_0);
            }
            if (power >= 500000) {
                doneTask(player, ConstTask.TASK_15_0);
            }
            if (power >= 1500000) {
                doneTask(player, ConstTask.TASK_17_0);
            }
            if (power >= 5000000) {
                doneTask(player, ConstTask.TASK_18_0);
            }
            if (power >= 50000000) {
                doneTask(player, ConstTask.TASK_20_0);
            }
            if (power >= 15000000) {
                doneTask(player, ConstTask.TASK_19_0);
            }
        }
    }

    // kiểm tra hoàn thành nhiệm vụ khi player sử dụng tiềm năng
    public void checkDoneTaskUseTiemNang(Player player) {
        if (!player.isBoss && !player.isPet) {
            doneTask(player, ConstTask.TASK_3_0);
        }
    }

    // kiểm tra hoàn thành nhiệm vụ khi player sử dụng item
    public void checkDoneTaskUseItem(Player player, Item item) {
        if (player.isPl() && item.isNotNullItem()) {
            switch (item.template.id) {
                case 992 -> {
                    doneTask(player, ConstTask.TASK_31_1);
                }
            }
        }
    }

    //kiểm tra hoàn thành nhiệm vụ khi vào map nào đó
    public void checkDoneTaskGoToMap(Player player, Zone zoneJoin) {
        if (!player.isBoss && !player.isPet) {
            switch (zoneJoin.map.mapId) {
                case 39:
                case 40:
                case 41:
                    if (player.location.x >= 362) {
                        doneTask(player, ConstTask.TASK_0_0);
                    }
                    break;
                case 21:
                case 22:
                case 23:
                    doneTask(player, ConstTask.TASK_0_1);
                    break;
                case 47:
                    doneTask(player, ConstTask.TASK_8_3);
                    break;
                case 93:
                    doneTask(player, ConstTask.TASK_25_0);
                    break;
                case 104:
                    doneTask(player, ConstTask.TASK_26_0);
                    break;
                case 97:
                    doneTask(player, ConstTask.TASK_27_0);
                    break;
                case 100:
                    doneTask(player, ConstTask.TASK_28_0);
                    break;
                case 103:
                    doneTask(player, ConstTask.TASK_29_2);
                case 114:
                    doneTask(player, ConstTask.TASK_30_0);
                    break;
                case 46:
                    doneTask(player, ConstTask.TASK_9_1);
                    break;
            }
        }
    }

    // kiểm tra hoàn thành nhiệm vụ khi nhặt item
    public void checkDoneTaskPickItem(Player player, ItemMap item) {
        if (!player.isBoss && !player.isPet && item != null && item.itemTemplate != null) {
            switch (item.itemTemplate.id) {
                case 73: //đùi gà
                    doneTask(player, ConstTask.TASK_2_0);
                    break;
                case 78: //em bé
                    doneTask(player, ConstTask.TASK_3_1);
                    Service.gI().sendFlagBag(player);
                    break;
                case 20:
                    doneTask(player, ConstTask.TASK_8_1);
                    break;
                case 85:
                    doneTask(player, ConstTask.TASK_14_1);
                    break;
                case 380:
                    doneTask(player, ConstTask.TASK_29_1);
                    break;
                case 663, 664, 665, 666, 667:
                    doneTask(player, ConstTask.TASK_31_7);
                case 992: // ntk
                    doneTask(player, ConstTask.TASK_32_0);
                    break;
                case 77:
                    AchievementService.gI().checkDoneTask(player, ConstAchievement.TRUM_NHAT_NGOC);
                    break;

            }
        }
    }

    public void checkDoneTaskPK(Player player) {
        if (player.isPl()) {
            doneTask(player, ConstTask.TASK_16_0);
        }
    }

        //kiểm tra hoàn thành nhiệm vụ kết bạn
    public void checkDoneTaskMakeFriend(Player player, Player friend) {
    }

    public void checkDoneTaskTut(Player player) {
        if (player.isPl()) {
            doneTask(player, ConstTask.TASK_19_1);
        }
    }

    public void checkDoneTaskFind7Stars(Player player) {
        doneTask(player, ConstTask.TASK_8_1);
    }

    public void checkDoneTaskDHVT(Player player) {
        doneTask(player, ConstTask.TASK_18_1);
    }

    public void checkDoneTaskFindDoremon(Player player) {
        doneTask(player, ConstTask.TASK_14_1);
    }

    //kiểm tra hoàn thành nhiệm vụ khi xác nhận menu npc nào đó
    // kiểm tra hoàn thành nhiệm vụ khi xác nhận menu npc nào đó
    public void checkDoneTaskConfirmMenuNpc(Player player, Npc npc, byte select) {
        if (!player.isBoss && !player.isPet) {
            switch (npc.tempId) {
                case ConstNpc.DAU_THAN:
                    switch (player.iDMark.getIndexMenu()) {
                        case ConstNpc.MAGIC_TREE_NON_UPGRADE_LEFT_PEA:
                        case ConstNpc.MAGIC_TREE_NON_UPGRADE_FULL_PEA:
                            if (select == 0) {
                                doneTask(player, ConstTask.TASK_0_4);
                            }
                    }
                    break;
            }
        }
    }

    public void checkDoneTaskNangCS(Player player) {
        if (!player.isBoss && !player.isPet) {
            if (player.nPoint.dameg < 10000) {
                return;
            }
            doneTask(player, ConstTask.TASK_29_0);
        }
    }

    public void checkDoneTaskKillPlayer(Player player) {
        doneTask(player, ConstTask.TASK_16_0);
    }

    //kiểm tra hoàn thành nhiệm vụ khi tiêu diệt được boss
    public void checkDoneTaskKillBoss(Player player, Boss boss) {
        if (player != null && !player.isBoss && !player.isPet) {
            switch ((int) boss.id) {
                case BossID.TAUPAYPAY:
                    doneTask(player, ConstTask.TASK_10_1);
                    break;
                case BossID.KARIN:
                    doneTask(player, ConstTask.TASK_10_0);
                    break;
                case BossID.KUKU:
                    doneTask(player, ConstTask.TASK_21_0);
                    break;
                case BossID.MAP_DAU_DINH:
                    doneTask(player, ConstTask.TASK_21_1);
                    break;
                case BossID.RAMBO:
                    doneTask(player, ConstTask.TASK_21_2);
                    break;
                case BossID.SO_4:
                    doneTask(player, ConstTask.TASK_22_0);
                    break;
                case BossID.SO_3:
                    doneTask(player, ConstTask.TASK_22_1);
                    break;
                case BossID.SO_1:
                    doneTask(player, ConstTask.TASK_22_2);
                    break;
                case BossID.TIEU_DOI_TRUONG:
                    doneTask(player, ConstTask.TASK_22_3);
                    break;
                case BossID.FIDE:
                    switch (boss.currentLevel) {
                        case 0 ->
                            doneTask(player, ConstTask.TASK_23_0);
                        case 1 ->
                            doneTask(player, ConstTask.TASK_23_1);
                        case 2 ->
                            doneTask(player, ConstTask.TASK_23_2);
                    }
                    break;
                case BossID.ANDROID_19:
                    doneTask(player, ConstTask.TASK_25_1);
                    break;
                case BossID.DR_KORE:
                    doneTask(player, ConstTask.TASK_25_2);
                    break;
                case BossID.ANDROID_15:
                    doneTask(player, ConstTask.TASK_26_1);
                    break;
                case BossID.ANDROID_14:
                    doneTask(player, ConstTask.TASK_26_2);
                    break;
                case BossID.ANDROID_13:
                    doneTask(player, ConstTask.TASK_26_3);
                    break;
                case BossID.POC:
                    doneTask(player, ConstTask.TASK_27_2);
                    break;
                case BossID.PIC:
                    doneTask(player, ConstTask.TASK_27_1);
                    break;
                case BossID.KING_KONG:
                    doneTask(player, ConstTask.TASK_27_3);
                    break;
                case BossID.XEN_BO_HUNG:
                    switch (boss.currentLevel) {
                        case 0 ->
                            doneTask(player, ConstTask.TASK_28_1);
                        case 1 ->
                            doneTask(player, ConstTask.TASK_28_2);
                        case 2 ->
                            doneTask(player, ConstTask.TASK_28_3);
                    }
                    break;
                case BossID.XEN_CON_1, BossID.XEN_CON_2, BossID.XEN_CON_3, BossID.XEN_CON_4, BossID.XEN_CON_5, BossID.XEN_CON_6, BossID.XEN_CON_7:
                    doneTask(player, ConstTask.TASK_29_3);
                    break;
                case BossID.SIEU_BO_HUNG:
                    doneTask(player, ConstTask.TASK_29_4);
                    break;
                case BossID.DRABURA, BossID.DRABURA_2, BossID.DRABURA_3:
                    doneTask(player, ConstTask.TASK_30_1);
                    doneTask(player, ConstTask.TASK_30_5);
                    break;
                case BossID.BUI_BUI, BossID.BUI_BUI_2:
                    doneTask(player, ConstTask.TASK_30_2);
                    doneTask(player, ConstTask.TASK_30_3);
                    break;
                case BossID.YA_CON:
                    doneTask(player, ConstTask.TASK_30_4);
                    break;
                case BossID.MABU_12H:
                    doneTask(player, ConstTask.TASK_30_6);
                    break;
                case BossID.TRUNG_UY_TRANG:
                    doneTask(player, ConstTask.TASK_19_1);
                    break;
            }
        }
    }

    // kiểm tra hoàn thành nhiệm vụ khi giết được quái
    @SuppressWarnings("unused")
    public void checkDoneTaskKillMob(Player player, Mob mob) {
        if (!player.isBoss && !player.isPet) {
            switch (mob.tempId) {
                case ConstMob.MOC_NHAN:
                    doneTask(player, ConstTask.TASK_1_0);
                    break;
                case ConstMob.KHUNG_LONG_ME:
                    doneTask(player, ConstTask.TASK_4_0);
                    doneTask(player, ConstTask.TASK_5_1);
                    doneTask(player, ConstTask.TASK_6_1);
                    break;
                case ConstMob.LON_LOI_ME:
                    doneTask(player, ConstTask.TASK_4_1);
                    doneTask(player, ConstTask.TASK_5_0);
                    doneTask(player, ConstTask.TASK_6_2);
                    break;
                case ConstMob.QUY_DAT_ME:
                    doneTask(player, ConstTask.TASK_4_2);
                    doneTask(player, ConstTask.TASK_5_2);
                    doneTask(player, ConstTask.TASK_6_0);
                    break;
                case ConstMob.THAN_LAN_BAY:
                case ConstMob.PHI_LONG:
                case ConstMob.QUY_BAY:
                    doneTask(player, ConstTask.TASK_7_1);
                    break;

                case ConstMob.HEO_RUNG:
                    doneTask(player, ConstTask.TASK_13_0); // Hoàn thành nhiệm vụ TASK_13_0
                    break;
                case ConstMob.HEO_DA_XANH:
                    doneTask(player, ConstTask.TASK_13_1); // Hoàn thành nhiệm vụ TASK_13_1
                    break;
                case ConstMob.HEO_XAYDA:
                    doneTask(player, ConstTask.TASK_13_2); // Hoàn thành nhiệm vụ TASK_13_2
                    break;
                    // if (player.clan != null) {
                    //     List<Player> list = new ArrayList<>();
                    //     List<Player> playersMap = player.zone.getPlayers();
                    //     for (Player pl : playersMap) {
                    //         if (pl != null && pl.clan != null && pl.clan.equals(player.clan)) {
                    //             list.add(pl);
                    //         }
                    //     }

                    //     if (list.size() >= NMEMBER_DO_TASK_TOGETHER) {
                    //         for (Player pl : list) {
                    //             switch (mob.tempId) {
                    //                 case ConstMob.HEO_RUNG:
                    //                     doneTask(pl, ConstTask.TASK_13_0); // Hoàn thành nhiệm vụ TASK_13_0
                    //                     break;
                    //                 case ConstMob.HEO_DA_XANH:
                    //                     doneTask(pl, ConstTask.TASK_13_1); // Hoàn thành nhiệm vụ TASK_13_1
                    //                     break;
                    //                 case ConstMob.HEO_XAYDA:
                    //                     doneTask(pl, ConstTask.TASK_13_2); // Hoàn thành nhiệm vụ TASK_13_2
                    //                     break;
                    //             }
                    //         }
                    //     }
                    // }
                    // break;
                case ConstMob.BULON:
                    doneTask(player, ConstTask.TASK_15_1);
                    break;
                case ConstMob.UKULELE:
                    doneTask(player, ConstTask.TASK_15_2);
                    break;
                case ConstMob.QUY_MAP:
                    doneTask(player, ConstTask.TASK_15_3);
                    break;
                    // if (player.clan != null) {
                    //     List<Player> list = new ArrayList<>();
                    //     List<Player> playersMap = player.zone.getPlayers();
                    //     for (Player pl : playersMap) {
                    //         if (pl != null && pl.clan != null && pl.clan.equals(player.clan)) {
                    //             list.add(pl);
                    //         }
                    //     }
                    //     if (list.size() >= NMEMBER_DO_TASK_TOGETHER) {
                    //         for (Player pl : list) {
                    //             switch (mob.tempId) {
                    //                 case ConstMob.BULON:
                    //                     doneTask(pl, ConstTask.TASK_15_1);
                    //                     break;
                    //                 case ConstMob.UKULELE:
                    //                     doneTask(pl, ConstTask.TASK_15_2);
                    //                     break;
                    //                 case ConstMob.QUY_MAP:
                    //                     doneTask(pl, ConstTask.TASK_15_3);
                    //                     break;
                    //             }
                    //         }
                    //     }
                    // }
                    // break;
                case ConstMob.TAMBOURINE:
                    doneTask(player, ConstTask.TASK_17_2);
                    break;
                case ConstMob.DRUM:
                    doneTask(player, ConstTask.TASK_17_3);
                    break;
                case ConstMob.AKKUMAN:
                    doneTask(player, ConstTask.TASK_17_1);
                    break;
                case ConstMob.NAPPA:
                    doneTask(player, ConstTask.TASK_20_1);
                    break;
                case ConstMob.SOLDIER:
                    doneTask(player, ConstTask.TASK_20_2);
                    break;
                case ConstMob.APPULE:
                    doneTask(player, ConstTask.TASK_20_3);
                    break;
                case ConstMob.RASPBERRY:
                    doneTask(player, ConstTask.TASK_20_4);
                    break;
                case ConstMob.THAN_LAN_XANH:
                    doneTask(player, ConstTask.TASK_20_5);
                    break;
                case ConstMob.TOBI:
                    doneTask(player, ConstTask.TASK_31_3);
                    doneTask(player, ConstTask.TASK_31_8);
                    break;
                case ConstMob.CABIRA:
                    doneTask(player, ConstTask.TASK_31_3);
                    doneTask(player, ConstTask.TASK_31_8);
                    break;
                case ConstMob.XEN_CON_CAP_1:
                    doneTask(player, ConstTask.TASK_24_4);
                    break;
                case ConstMob.XEN_CON_CAP_3:
                    doneTask(player, ConstTask.TASK_25_3);
                    break;
                case ConstMob.XEN_CON_CAP_5:
                    doneTask(player, ConstTask.TASK_27_4);
                    break;
                case ConstMob.XEN_CON_CAP_8:
                    doneTask(player, ConstTask.TASK_28_4);
                    break;
                    // if (player.clan != null) {
                    //     List<Player> list = new ArrayList<>();
                    //     List<Player> playersMap = player.zone.getPlayers();
                    //     for (Player pl : playersMap) {
                    //         if (pl != null && pl.clan != null && pl.clan.equals(player.clan)) {
                    //             list.add(pl);
                    //         }
                    //     }
                    //     if (list.size() >= NMEMBER_DO_TASK_TOGETHER) {
                    //         for (Player pl : list) {
                    //             switch (mob.tempId) {
                    //                 case ConstMob.XEN_CON_CAP_1:
                    //                     doneTask(player, ConstTask.TASK_24_4);
                    //                     break;
                    //                 case ConstMob.XEN_CON_CAP_3:
                    //                     doneTask(player, ConstTask.TASK_25_3);
                    //                     break;
                    //                 case ConstMob.XEN_CON_CAP_5:
                    //                     doneTask(player, ConstTask.TASK_27_4);
                    //                     break;
                    //                 case ConstMob.XEN_CON_CAP_8:
                    //                     doneTask(player, ConstTask.TASK_28_4);
                    //                     break;
                    //             }
                    //         }
                    //     }
                    // }
            }
        }
    }

    //xong nhiệm vụ nào đó
    public boolean doneTask(Player player, int idTaskCustom) {
        PlayerDAO.updateLastTimeUpdateTask(player, System.currentTimeMillis());
        if (TaskService.gI().isCurrentTask(player, idTaskCustom)) {
            this.addDoneSubTask(player, 1);
            switch (idTaskCustom) {
                //--------------------------------------------------------------
                case ConstTask.TASK_0_0:
                    NpcService.gI().createTutorial(player, -1, "Hãy di chuyển đến Nhà Broly, ông Paragus đang chờ bạn ở đằng kia!");
                    break;
                case ConstTask.TASK_0_1:
                    NpcService.gI().createTutorial(player, -1, "ông Paragus đang chờ. Bạn hãy đi đến gần và chạm nhanh 2 lần vào để trò chuyện");
                    break;
                case ConstTask.TASK_0_2:
                    npcSay(player, ConstTask.NPC_NHA, "Con mới đi đâu về thế? Con hãy đến rương đồ để lấy rađa, sau đó lại thu hoạch những hạt đậu trên cây đậu thần kia!");
                    npcSay(player, ConstTask.NPC_NHA, "Rương đồ đằng kia, đi đến gần và chạm nhanh 2 lần vào rương để mở\nChọn lấy Rađa để sử dụng");
                    break;
                case ConstTask.TASK_0_3:
                    Service.gI().sendThongBao(player, "Bạn có thể thu hoạch đậu thần ở cây đậu đằng kia");
                    break;
                case ConstTask.TASK_0_4:
                    Service.gI().sendThongBao(player, "Nào, bây giờ bạn có thể gặp ông Paragus để báo cáo rồi!");
                    break;
                case ConstTask.TASK_0_5:
                    npcSay(player, ConstTask.NPC_NHA, "Tốt lắm, Rađa sẽ giúp con biết được HP và KI của mình ở góc trên màn hình\n"
                            + "Đậu thần sẽ giúp con phục hồi HP và KI khi con yếu đi\n"
                            + "Bây giờ, con hãy ra Làng Kakarot để tập luyện, hãy đánh ngã 5 mộc nhân, rồi trở về gặp ta, ta sẽ dạy con bay\n"
                            + "Đi đi, và về sớm con nhé!");
                    break;
                case ConstTask.TASK_1_0:
                    if (isCurrentTask(player, idTaskCustom)) {
                        Service.gI().sendThongBao(player, "Bạn đánh được "
                                + player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).count + "/"
                                + player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).maxCount + " mộc nhân");
                    } else {
                        if (player.gender == 0) {
                            Service.gI().sendThongBao(player, "Chúc mừng bạn đã hoàn thành nhiệm\nvụ. Nào, bây giờ bạn\n    có thể gặp ông\nGôhan để báo cáo rồi!");
                        } else if (player.gender == 1) {
                            Service.gI().sendThongBao(player, "Chúc mừng bạn đã hoàn thành nhiệm\nvụ. Nào, bây giờ bạn\n    có thể gặp ông\nMoori để báo cáo rồi!");
                        } else {
                            Service.gI().sendThongBao(player, "Chúc mừng bạn đã hoàn thành nhiệm\nvụ. Nào, bây giờ bạn\n    có thể gặp ông\nParagus để báo cáo rồi!");
                        }
                    }
                    break;
                case ConstTask.TASK_1_1:
                    npcSay(player, ConstTask.NPC_NHA,
                            "Thể lực của con cũng khá tốt\n"
                            + "Con à, dạo gần đây dân làng của chúng ta gặp phải vài chuyện\n"
                            + "Bên cạnh làng ta đột nhiên xuất hiện lũ quái vật\n"
                            + "Nó tàn sát dân làng và phá hoại nông sản làng ta\n"
                            + "Con hãy tìm đánh chúng và đem về đây 10 cái đùi gà, 2 ông cháu mình sẽ để dành ăn dần\n"
                            + "Đây là tấm bản đồ của vùng này, con hãy xem để tìm đến %3\n"
                            + "Con có thể sử dụng đậu thần khi hết HP hoặc KI, bằng cách nhấn vào nút có hình trái tim "
                            + "bên góc phải dưới màn hình\n"
                            + "Nhanh lên, ông đói lắm rồi");
                    break;
                //--------------------------------------------------------------
                case ConstTask.TASK_2_0:
                    if (isCurrentTask(player, idTaskCustom)) {
                        Service.gI().sendThongBao(player, "Bạn thu thập được "
                                + player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).count + "/"
                                + player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).maxCount + " đùi gà");
                    } else {
                        if (player.gender == 0) {
                            Service.gI().sendThongBao(player, "Chúc mừng bạn đã hoàn thành nhiệm\nvụ. Nào, bây giờ bạn\n    có thể gặp ông\nGôhan để báo cáo rồi!");
                        } else if (player.gender == 1) {
                            Service.gI().sendThongBao(player, "Chúc mừng bạn đã hoàn thành nhiệm\nvụ. Nào, bây giờ bạn\n    có thể gặp ông\nMoori để báo cáo rồi!");
                        } else {
                            Service.gI().sendThongBao(player, "Chúc mừng bạn đã hoàn thành nhiệm\nvụ. Nào, bây giờ bạn\n    có thể gặp ông\nParagus để báo cáo rồi!");
                        }
                    }
                    break;
                case ConstTask.TASK_2_1:
                    try {
                    InventoryService.gI().subQuantityItemsBag(player, InventoryService.gI().findItemBag(player, 73), 100);
                } catch (Exception ex) {
                }
                InventoryService.gI().sendItemBag(player);
                Service.gI().dropItemMapForMe(player, player.zone.getItemMapByTempId(74));
                npcSay(player, ConstTask.NPC_NHA,
                        "Tốt lắm, đùi gà đây rồi, haha. Ông sẽ nướng tại đống lửa gần kia con có thể ăn bất cứ lúc nào nếu muốn\n"
                        + "À cháu này, vừa nãy ông có nghe thấy 1 tiếng động lớn, hình như có 1 vật thể rơi tại %5, con hãy đến kiểm tra xem\n"
                        + "Con cũng có thể dùng tiềm năng bản thân để nâng HP, KI hoặc sức đánh");
                break;
                //--------------------------------------------------------------
                case ConstTask.TASK_3_0:
                    break;
                case ConstTask.TASK_3_1:

                    Service.gI().sendThongBao(player, "Hãy bế cậu bé về\nnhà!");

                    break;
                case ConstTask.TASK_3_2:
                    try {
                    InventoryService.gI().subQuantityItemsBag(player, InventoryService.gI().findItemBag(player, 78), 1);
                } catch (Exception ex) {
                }
                InventoryService.gI().sendItemBag(player);
                Service.gI().sendFlagBag(player);
                npcSay(player, ConstTask.NPC_NHA,
                        "Có em bé trong phi thuyền rơi xuống à, ông cứ tưởng là sao băng chứ\n"
                        + "Ông sẽ đặt tên cho em nó là Goku, từ giờ nó sẽ là thành viên trong gia đình ta\n"
                        + "Nãy ông mới nhận được tin có bầy mãnh thú xuất hiện tại Trạm phi thuyền\n"
                        + "Bọn chúng vừa đổ bộ xuống trái đất để trả thù việc con sát hại con chúng\n"
                        + "Con hãy đi tiêu diệt chúng để giúp dân làng tại đó luôn nhé");
                break;
                //--------------------------------------------------------------
                case ConstTask.TASK_4_0:
                    if (isCurrentTask(player, idTaskCustom)) {
                        Service.gI().sendThongBao(player, "Bạn đánh được "
                                + player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).count + "/"
                                + player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).maxCount
                                + transformName(player, " khủng long mẹ"));
                    } else {
                        Service.gI().sendThongBao(player, "Tiếp theo là lợn lòi mẹ");
                    }
                    break;
                case ConstTask.TASK_5_0:
                    if (isCurrentTask(player, idTaskCustom)) {
                        Service.gI().sendThongBao(player, "Bạn đánh được "
                                + player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).count + "/"
                                + player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).maxCount
                                + transformName(player, " lợn lòi mẹ"));
                    } else {
                        Service.gI().sendThongBao(player, "Tiếp theo là khủng long mẹ");
                    }
                    break;
                case ConstTask.TASK_6_0:
                    if (isCurrentTask(player, idTaskCustom)) {
                        Service.gI().sendThongBao(player, "Bạn đánh được "
                                + player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).count + "/"
                                + player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).maxCount
                                + transformName(player, " quỷ đất mẹ"));
                    } else {
                        Service.gI().sendThongBao(player, "Tiếp theo là khủng long mẹ");
                    }
                    break;

                case ConstTask.TASK_4_1:
                    if (isCurrentTask(player, idTaskCustom)) {
                        Service.gI().sendThongBao(player, "Bạn đánh được "
                                + player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).count + "/"
                                + player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).maxCount
                                + transformName(player, " lợn lòi mẹ"));
                    } else {
                        Service.gI().sendThongBao(player, "Tiếp theo là quỷ đất mẹ");
                    }
                    break;
                case ConstTask.TASK_5_1:
                    if (isCurrentTask(player, idTaskCustom)) {
                        Service.gI().sendThongBao(player, "Bạn đánh được "
                                + player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).count + "/"
                                + player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).maxCount
                                + transformName(player, " khủng long mẹ"));
                    } else {
                        Service.gI().sendThongBao(player, "Tiếp theo là quỷ đất mẹ");
                    }
                    break;
                case ConstTask.TASK_6_1:
                    if (isCurrentTask(player, idTaskCustom)) {
                        Service.gI().sendThongBao(player, "Bạn đánh được "
                                + player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).count + "/"
                                + player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).maxCount
                                + transformName(player, " khủng long mẹ"));
                    } else {
                        Service.gI().sendThongBao(player, "Tiếp theo là lợn lòi mẹ");
                    }
                    break;
                case ConstTask.TASK_4_2:
                case ConstTask.TASK_5_2:
                    if (isCurrentTask(player, idTaskCustom)) {
                        Service.gI().sendThongBao(player, "Bạn đánh được "
                                + player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).count + "/"
                                + player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).maxCount
                                + transformName(player, " quỷ đất mẹ"));
                    } else {
                        if (player.gender == 0) {
                            Service.gI().sendThongBao(player, "Xong việc rồi, về báo\n cáo với ông Gôhan nào!");
                        } else if (player.gender == 1) {
                            Service.gI().sendThongBao(player, "Xong việc rồi, về báo\n cáo với ông Moori nào!");
                        } else {
                            Service.gI().sendThongBao(player, "Xong việc rồi, về báo\n cáo với ông Paragus nào!");
                        }
                    }
                    break;
                case ConstTask.TASK_6_2:
                    if (isCurrentTask(player, idTaskCustom)) {
                        Service.gI().sendThongBao(player, "Bạn đánh được "
                                + player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).count + "/"
                                + player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).maxCount
                                + transformName(player, " lợn lòi mẹ"));
                    } else {
                        if (player.gender == 0) {
                            Service.gI().sendThongBao(player, "Xong việc rồi, về báo\n cáo với ông Gôhan nào!");
                        } else if (player.gender == 1) {
                            Service.gI().sendThongBao(player, "Xong việc rồi, về báo\n cáo với ông Moori nào!");
                        } else {
                            Service.gI().sendThongBao(player, "Xong việc rồi, về báo\n cáo với ông Paragus nào!");
                        }
                    }
                    break;
                case ConstTask.TASK_4_3:
                case ConstTask.TASK_5_3:
                case ConstTask.TASK_6_3:
                    npcSay(player, ConstTask.NPC_NHA,
                            "Ông rất tự hào về con\n"
                            + "Ông cho con cuốn bí kíp này để nâng cao võ học\n"
                            + "Hãy dùng sức mạnh của mình trừ gian diệt ác bảo vệ dân lành con nhé\n"
                            + "Bây giờ con hãy đi tập luyện đi, khi nào mạnh hơn thì quay về đây ông giao cho nhiệm vụ mới\n"
                            + "Đi đi..");
                    break;
                //--------------------------------------------------------------
                case ConstTask.TASK_7_0:
                    break;
                case ConstTask.TASK_7_1:
                    if (isCurrentTask(player, idTaskCustom)) {
                        Service.gI().sendThongBao(player, "Bạn đánh được "
                                + player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).count + "/"
                                + player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).maxCount
                                + transformName(player, " %9"));
                    } else {
                        if (player.gender == 0) {
                            Service.gI().sendThongBao(player, "Bạn vừa giải cứu được\nBunma hãy về Làng Aru và nói\nchuyện với Bunma");
                        } else if (player.gender == 1) {
                            Service.gI().sendThongBao(player, "Bạn vừa giải cứu được\nDende hãy về Làng Mori và nói\nchuyện với Dende");
                        } else {
                            Service.gI().sendThongBao(player, "Bạn vừa giải cứu được\nAppule hãy về Làng Kakarot và nói\nchuyện với Appule");
                        }
                    }
                    break;
                case ConstTask.TASK_7_2:
                    npcSay(player, ConstTask.NPC_SHOP_LANG,
                            "Cảm ơn ngươi đã cứu ta. Ta sẽ sẵn sàng phục vụ nếu ngươi cần mua vật dụng");
                    if (player.gender == 0) {
                        Service.gI().sendThongBao(player, "Xong việc rồi, về báo\n cáo với ông Gôhan nào!");
                    } else if (player.gender == 1) {
                        Service.gI().sendThongBao(player, "Xong việc rồi, về báo\n cáo với ông Moori nào!");
                    } else {
                        Service.gI().sendThongBao(player, "Xong việc rồi, về báo\n cáo với ông Paragus nào!");
                    }
                    break;
                case ConstTask.TASK_7_3:
                    npcSay(player, ConstTask.NPC_NHA,
                            "Tốt lắm, con làm ta bất ngờ đấy. Bây giờ hãy đi lấy lại ngọc đem về đây cho ta");
                    break;
                //--------------------------------------------------------------------
                case ConstTask.TASK_8_0:
                    break;
                case ConstTask.TASK_8_1:
                    Service.gI().sendThongBao(player, "Bạn đã tìm thấy ngọc rồng 7 sao rồi, hãy chạm nhanh 2 lần vào đối tượng để lấy");
                    if (player.gender == 0) {
                        Service.gI().sendThongBao(player, "Tìm thấy viên ngọc rồng 7 sao rồi, đem về cho ông GôHan nào!");
                    } else if (player.gender == 1) {
                        Service.gI().sendThongBao(player, "Tìm thấy viên ngọc rồng 7 sao rồi, đem về cho ông Moori nào!");
                    } else {
                        Service.gI().sendThongBao(player, "Tìm thấy viên ngọc rồng 7 sao rồi, đem về cho ông Paragus nào!");
                    }
                    break;
                case ConstTask.TASK_8_2:
                    npcSay(player, ConstTask.NPC_NHA,
                            "Tốt lắm, con làm ta bất ngờ đấy. Bây giờ hãy đi đến đảo Kame");
                    player.playerTask.taskMain.id = 11;
                    player.playerTask.taskMain.index = 1;
                    TaskService.gI().sendNextTaskMain(player);
//                    Service.gI().sendThongBao(player, "Bạn đã được hỗ trợ bỏ qua nhiệm vụ Tàu 77, Thần mèo.");
                    break;
                case ConstTask.TASK_8_3:
                    player.playerTask.taskMain.id = 10;
                    player.playerTask.taskMain.index = 3;
                    TaskService.gI().sendNextTaskMain(player);
                    Service.gI().sendThongBao(player, "Bạn đã được hỗ trợ bỏ qua nhiệm vụ Tàu 77, Thần mèo.");
                    break;
                //-----------------------------------------------------------------------
                case ConstTask.TASK_9_0:
                    npcSay(player, ConstNpc.BO_MONG, "Hắn sắp đến đây, hãy giúp ta tiêu diệt hắn\nHãy giúp ta tiêu diệt hắn");
                    Service.gI().sendThongBao(player, "Hình như có người đang tới đây");
                    TrainingService.gI().callBoss(player, BossID.TAUPAYPAY, false);
                    break;
                case ConstTask.TASK_9_1:
                    Service.gI().sendThongBao(player, "Hãy nói chuyện với Thần Mèo Karin");
                    break;
                case ConstTask.TASK_9_2:
                    npcSay(player, ConstNpc.THAN_MEO_KARIN, "Có phải ngươi vừa chiến đấu với Tàu Pảy Pảy không?\n"
                            + "Ta tuy mù nhưng có thể đọc được ý nghĩ của ngươi\n"
                            + "Ngươi chưa phải là đối thủ của hắn đâu\n"
                            + "Tìm ta là đúng rồi, để ta dạy ngươi vài chiêu, nhưng phải chăm chỉ mới học được đấy nhé\n"
                            + "Ngươi sẵn sàng chưa");
                        Thread.startVirtualThread(() -> {
                        try {
                            Thread.sleep(3000);
                            TrainingService.gI().callBoss(player, BossID.KARIN, true);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
                    break;
                case ConstTask.TASK_10_1:
                    // Chỉ gọi boss, không hoàn thành nhiệm vụ ngay
                    npcSay(player, ConstNpc.BO_MONG, "Xem ra cậu đã mạnh hơn lần trước tôi gặp rồi đó cậu có muốn thách đấu tại tên Tàu Pảy Pảy không?");
                    Service.gI().sendThongBao(player, "Lười làm boss tàu 77");

//                    new Thread(() -> {
//                        try {
//                            Thread.sleep(1000);
//                            TrainingService.gI().callBoss(player, BossID.TAUPAYPAY, true);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }).start();
//                    break;
                case ConstTask.TASK_10_2:
                    npcSay(player, ConstNpc.BO_MONG, "Cám ơn đã giúp chúng tôi. Xin hãy nhận viên ngọc rồng 6 sao này như món quà cám ơn");
                    Item ngocrong6sao = ItemService.gI().createNewItem((short) 19);
                    InventoryService.gI().addItemBag(player, ngocrong6sao);
                    InventoryService.gI().sendItemBag(player);
                    Service.gI().sendThongBao(player, "Bạn vừa nhận được Ngọc Rồng 6 sao");
                    if (player.gender == 0) {
                        Service.gI().sendThongBao(player, "Viên ngọc rồng 6 sao đây rồi, đem về cho ông Gôhan nào!");
                    } else if (player.gender == 1) {
                        Service.gI().sendThongBao(player, "Viên ngọc rồng 6 sao đây rồi, đem về cho ông Moori nào!");
                    } else {
                        Service.gI().sendThongBao(player, "Viên ngọc rồng 6 sao đây rồi, đem về cho ông Paragus nào!");
                    }
                    break;
                case ConstTask.TASK_10_3:
                    npcSay(player, ConstTask.NPC_NHA, "Ta thật sự hãnh diện về con. Giờ ta không còn gì để dạy cho con, nhưng có người còn giỏi hơn ta\n"
                            + "Đó là sư phụ của ta " + (player.gender == ConstPlayer.TRAI_DAT ? "Quy Lão Kame" : (player.gender == ConstPlayer.NAMEC ? "Trưởng lão Guru" : "Vua Vegeta")) + ", ngài rất thích đọc truyện Đôrêmon, con hãy đem tới cho ngài\n"
                            + "Nhất định ngài sẽ thu nhận con làm đệ tử, con ráng học thành tài nhé");
                    Item truyentranh = ItemService.gI().createNewItem((short) 85);
                    InventoryService.gI().addItemBag(player, truyentranh);
                    InventoryService.gI().sendItemBag(player);
                    break;
                //-----------------------------------------------------------------------
                case ConstTask.TASK_11_0:
                    npcSay(player, ConstTask.NPC_QUY_LAO, "Ta không nhận đệ tử đâu. Ồ con tặng ta truyện Doremon hả, thôi được nhưng con phải cố gắng luyện tập đó nhé. Hãy gia nhập một bang hội để luyện tập, sau đó quay lại đây gặp ta");
                    doneTask(player, ConstTask.TASK_11_1);
                    InventoryService.gI().subQuantityItemsBag(player, InventoryService.gI().findItemBag(player, 85), 1);
                    InventoryService.gI().sendItemBag(player);
                case ConstTask.TASK_11_1:
                    break;
                //---------------------------------------------------------------------------
                case ConstTask.TASK_12_0:
                    break;

                case ConstTask.TASK_12_1:
                    npcSay(player, ConstTask.NPC_QUY_LAO,
                            "Con hãy cùng các thành viên trong bang tiêu diệt cho ta 30 con Heo rừng, 30 con Heo da xanh và 30 con Heo Xayda");
                    break;

                //=============================================================================
                case ConstTask.TASK_13_0:
                    if (isCurrentTask(player, idTaskCustom)) {
                        Service.gI().sendThongBao(player, "Bạn đánh được "
                                + player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).count + "/"
                                + player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).maxCount
                                + transformName(player, " Heo rừng"));
                    } else {
                        Service.gI().sendThongBao(player, "Tiếp theo là Heo da xanh");
                    }
                    break;
                case ConstTask.TASK_13_1:
                    if (isCurrentTask(player, idTaskCustom)) {
                        Service.gI().sendThongBao(player, "Bạn đánh được "
                                + player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).count + "/"
                                + player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).maxCount
                                + transformName(player, " Heo da xanh"));
                    } else {
                        Service.gI().sendThongBao(player, "Tiếp theo là Heo xayda");
                    }
                    break;
                case ConstTask.TASK_13_2:
                    if (isCurrentTask(player, idTaskCustom)) {
                        Service.gI().sendThongBao(player, "Bạn đánh được "
                                + player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).count + "/"
                                + player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).maxCount
                                + transformName(player, " Heo xayda"));
                    } else {
                        if (player.gender == 0) {
                            Service.gI().sendThongBao(player, "Nhiệm vụ hoàn\nthành, báo cáo với\nQuy Lão nào!");
                        } else if (player.gender == 1) {
                            Service.gI().sendThongBao(player, "Nhiệm vụ hoàn\nthành, báo cáo với\nTrưởng Lão Guru nào!");
                        } else {
                            Service.gI().sendThongBao(player, "Nhiệm vụ hoàn\nthành, báo cáo với\nVua Vegeta nào!");
                        }
                    }
                    break;
                case ConstTask.TASK_13_3:
                    npcSay(player, ConstTask.NPC_QUY_LAO, "Bang của con rất có tinh thần đồng đội, con hãy cùng các thành viên luyện tập chăm chỉ để thành tài nhé");
                    break;
                //===================================================================================
                case ConstTask.TASK_14_0:
                    break;
                case ConstTask.TASK_14_1:
                    break;
                case ConstTask.TASK_14_2:
                    npcSay(player, ConstTask.NPC_QUY_LAO, "Con đã tìm thấy truyện Doremon tập 2 rồi à, mau đưa cho ta nào");
                    break;
                //=====================================================================================
                case ConstTask.TASK_15_1:
                    if (isCurrentTask(player, idTaskCustom)) {
                        Service.gI().sendThongBao(player, "Bạn đánh được "
                                + player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).count + "/"
                                + player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).maxCount
                                + transformName(player, " Bulon"));
                    } else {
                        Service.gI().sendThongBao(player, "Tiếp theo là Ukulele");
                    }
                    break;
                case ConstTask.TASK_15_2:
                    if (isCurrentTask(player, idTaskCustom)) {
                        Service.gI().sendThongBao(player, "Bạn đánh được "
                                + player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).count + "/"
                                + player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).maxCount
                                + transformName(player, " Ukulele"));
                    } else {
                        Service.gI().sendThongBao(player, "Tiếp theo là Quỷ mập");
                    }
                    break;
                case ConstTask.TASK_15_3:
                    if (isCurrentTask(player, idTaskCustom)) {
                        Service.gI().sendThongBao(player, "Bạn đánh được "
                                + player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).count + "/"
                                + player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).maxCount
                                + transformName(player, " Quỷ mập"));
                    } else {
                        if (player.gender == 0) {
                            Service.gI().sendThongBao(player, "Nhiệm vụ hoàn\nthành, báo cáo với\nQuy Lão nào!");
                        } else if (player.gender == 1) {
                            Service.gI().sendThongBao(player, "Nhiệm vụ hoàn\nthành, báo cáo với\nTrưởng Lão Guru nào!");
                        } else {
                            Service.gI().sendThongBao(player, "Nhiệm vụ hoàn\nthành, báo cáo với\nVua Vegeta nào!");
                        }
                    }
                    break;
                case ConstTask.TASK_15_4:
                    npcSay(player, ConstTask.NPC_QUY_LAO, "Con và bang hội làm rất tốt, ta có quà dành cho con");
                    break;
                //=====================================================================
                case ConstTask.TASK_17_0:
                    Service.gI().sendThongBao(player, "Mau đến XayDa\nđánh bại\nAkkuman tại\nThành phố Vegeta");
                    break;
                case ConstTask.TASK_17_1:
                    Service.gI().sendThongBao(player, "Mau đến Trái Đất\nđánh bại\nTambourine tại\nĐông Karin");
                    break;
                case ConstTask.TASK_17_2:
                    Service.gI().sendThongBao(player, "Mau đến Na mếc\nđánh bại\nDrum tại\nThung lũng Namếc");
                case ConstTask.TASK_17_3:
                    if (player.gender == 0) {
                        Service.gI().sendThongBao(player, "Báo cáo với\nQuy Lão nào!");
                    } else if (player.gender == 1) {
                        Service.gI().sendThongBao(player, "Báo cáo với\nTrưởng Lão Guru nào!");
                    } else {
                        Service.gI().sendThongBao(player, "Báo cáo với\nVua Vegeta nào!");
                    }
                    break;
                case ConstTask.TASK_17_4:
                    npcSay(player, ConstTask.NPC_QUY_LAO, "Con làm rất tốt, ta có quà dành cho con");
                    break;
                //=========================================================================
                case ConstTask.TASK_19_1:
                    if (player.gender == 0) {
                        Service.gI().sendThongBao(player, "Báo cáo với\nQuy Lão nào!");
                    } else if (player.gender == 1) {
                        Service.gI().sendThongBao(player, "Báo cáo với\nTrưởng Lão Guru nào!");
                    } else {
                        Service.gI().sendThongBao(player, "Báo cáo với\nVua Vegeta nào!");
                    }
                    break;
                case ConstTask.TASK_19_2:
                    npcSay(player, ConstTask.NPC_QUY_LAO, "Con làm rất tốt, Trung Úy Trắng đã bị tiêu diệt. Ta có quà dành cho con");
                //==========================================================================
                case ConstTask.TASK_20_7:
                    npcSay(player, ConstTask.NPC_QUY_LAO, "Tốt lắm, giờ con hãy đi tiêu diệt lũ đệ tử của Fide cho ta");
                    break;
                //==========================================================================
                case ConstTask.TASK_21_0:
                    Service.gI().sendThongBao(player, "Tiếp theo là tìm diệt Mập Đầu Đinh");
                    break;
                case ConstTask.TASK_21_1:
                    Service.gI().sendThongBao(player, "Cuối cùng là tìm diệt Rambo");
                    break;
                case ConstTask.TASK_21_2:
                    if (player.gender == 0) {
                        Service.gI().sendThongBao(player, "Chúc mừng bạn, bọn\nđệ tử của Fide đã bị\ntiêu diệt, hãy báo\ncáo với Quy Lão nào!");
                    } else if (player.gender == 1) {
                        Service.gI().sendThongBao(player, "Chúc mừng bạn, bọn\nđệ tử của Fide đã bị\ntiêu diệt, hãy báo\ncáo với Trưởng Lão Guru nào!");
                    } else {
                        Service.gI().sendThongBao(player, "Chúc mừng bạn, bọn\nđệ tử của Fide đã bị\ntiêu diệt, hãy báo\ncáo với Vua Vegeta nào!");
                    }
                    break;
                case ConstTask.TASK_21_3:
                    npcSay(player, ConstTask.NPC_QUY_LAO, "Con làm rất tốt, ta có quà dành cho con");
                    break;

                //==========================================================================
                case ConstTask.TASK_22_0:
                    Service.gI().sendThongBao(player, "Tiếp theo là Số 3");
                    break;
                case ConstTask.TASK_22_1:
                    Service.gI().sendThongBao(player, "Tiếp theo là Số 1");
                    break;
                case ConstTask.TASK_22_2:
                    Service.gI().sendThongBao(player, "Tiếp theo là Tiểu đội trưởng");
                    break;
                case ConstTask.TASK_22_3:
                    if (player.gender == 0) {
                        Service.gI().sendThongBao(player, "Chúc mừng bạn, bọn\ntiểu đội sát thủ của\nFide đã bị tiêu diệt, hãy báo cáo với Quy\nLão nào!");
                    } else if (player.gender == 1) {
                        Service.gI().sendThongBao(player, "Chúc mừng bạn, bọn\ntiểu đội sát thủ của\nFide đã bị tiêu diệt, hãy báo cáo với Trưởng\nLão Guru nào!");
                    } else {
                        Service.gI().sendThongBao(player, "Chúc mừng bạn, bọn\ntiểu đội sát thủ của\nFide đã bị tiêu diệt, hãy báo cáo với Vua\nVegeta nào!");
                    }
                    break;
                case ConstTask.TASK_22_4:
                    npcSay(player, ConstTask.NPC_QUY_LAO, "Rất tốt, bọn Fide đã biết sức mạnh của chúng ta");
                    break;

                //==========================================================================
                case ConstTask.TASK_23_0:
                    Service.gI().sendThongBao(player, "Hãy tìm diệt Fide cấp 2");
                case ConstTask.TASK_23_1:
                    Service.gI().sendThongBao(player, "Hãy tìm diệt Fide cấp 3");
                case ConstTask.TASK_23_2:
                    if (player.gender == 0) {
                        Service.gI().sendThongBao(player, "Chúc mừng bạn, Fide\nđã bị tiêu diệt, hãy\nbáo cáo với Quy\nLão nào!");
                    } else if (player.gender == 1) {
                        Service.gI().sendThongBao(player, "Chúc mừng bạn, Fide\nđã bị tiêu diệt, hãy\nbáo cáo với Trưởng\nLão Guru nào!");
                    } else {
                        Service.gI().sendThongBao(player, "Chúc mừng bạn, Fide\nđã bị tiêu diệt, hãy\nbáo cáo với Vua\nVegeta nào!");
                    }
                    break;
                case ConstTask.TASK_23_3:
                    npcSay(player, ConstTask.NPC_QUY_LAO, "Rất tốt, bọn Fide đã biết sức mạnh thật sự của chúng ta");
                    break;
                //==========================================================================
                case ConstTask.TASK_24_0:
                    npcSay(player, ConstTask.NPC_NHA, "Con cố gắng theo %10 học thành tài, đừng lo cho ta.");
                    Service.gI().sendThongBao(player, "Vừa có 1 phi thuyền lạ vừa đáp xuống trái đất, mau đến xem");
                    break;
                case ConstTask.TASK_24_1:
                    npcSay(player, ConstNpc.CALICK, "Chào chú, thực ra cháu không phải là người của thời đại này mà là người của...\n"
                            + "Tương lai 20 năm sắp tới\n Tên cháu là Ca lích! người Xayda\nCháu đến đây bằng 'Cổ máy thời gian'\n"
                            + "Bố mẹ cháu vốn là bạn thân của chú\n Họ chính là Ca Đíc và Bunma!\n"
                            + "Đây là thuốc trợ tim dành cho chú Sôngôku\n nhờ chú đưa cho Quy Lão giùm cháu nhé, cám ơn chú");
                    break;
                case ConstTask.TASK_24_2:
                    npcSay(player, ConstNpc.QUY_LAO_KAME, "Hô hô hô, cám ơn cậu...\n"
                            + "lúc nãy nó trôm mắt kính của ta, cứ tưởng hồn ma bóng quế của nó hiện về chứ");
                    break;
                case ConstTask.TASK_24_3:
                    npcSay(player, ConstNpc.BUNMA_TL, "Cám ơn bạn đã đến giúp chúng tôi\n"
                            + "Chúng tôi bị bọn bọ hung bao vây\n"
                            + "Chúng đông đến hàng trăm con\n"
                            + "Hãy giúp chúng tôi tiêu diệt hết chúng nó.");
                    break;
                case ConstTask.TASK_24_4:
                    if (isCurrentTask(player, idTaskCustom)) {
                        Service.gI().sendThongBao(player, "Bạn đánh được "
                                + player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).count + "/"
                                + player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).maxCount
                                + transformName(player, " Xên con cấp 1"));
                    } else {
                        Service.gI().sendThongBao(player, "Về báo cáo với Bunma tương lai");
                    }
                    break;
                case ConstTask.TASK_24_5:
                    npcSay(player, ConstNpc.BUNMA_TL, "Cảm ơn cậu đã giải vây cho chúng tôi\n"
                            + "Hãy đến thành phố phía nam, đảo balê hoặc cao nguyên tìm và chặn đánh 2 Rôbốt Sát Thủ\n"
                            + "Cẩn thận vì bọn chúng rất mạnh");
                    break;
                //=============================================================================

                case ConstTask.TASK_25_0:
                    Service.gI().sendThongBao(player, "Tiếp theo là tiêu diệt Android 19");
                    break;
                case ConstTask.TASK_25_1:
                    Service.gI().sendThongBao(player, "Tiếp theo là tiêu diệt Android 20");
                    break;
                case ConstTask.TASK_25_2:
                    if (isCurrentTask(player, idTaskCustom)) {
                        Service.gI().sendThongBao(player, "Bạn đánh được "
                                + player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).count + "/"
                                + player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).maxCount
                                + transformName(player, " Xên con cấp 3"));
                    } else {
                        Service.gI().sendThongBao(player, "Về báo cáo với Bunma tương lai");
                    }
                    break;
                case ConstTask.TASK_25_4:
                    npcSay(player, ConstNpc.BUNMA_TL, "Số 1 chính là bác học Kôrê\n"
                            + "Ông ta đã tự biến mình thành Rôbốt để được bất tử\n"
                            + "2 tên Rôbốt này không phải là Rôbốt sát thủ mà chúng tôi nói đến\n"
                            + "Có thể quá khứ đã thay đổi từ khi cậu đến đây\n"
                            + "Mau trở về quá khứ xem chuyện gì đã xảy ra");

                    if (isCurrentTask(player, idTaskCustom)) {
                        Service.gI().sendThongBao(player, "Đến sân sau siêu thị");
                    }
                    break;
                //=============================================================
                case ConstTask.TASK_26_0:
                    if (isCurrentTask(player, idTaskCustom)) {
                        Service.gI().sendThongBao(player, "Tìm và tiêu diệt Android 15");
                    }
                    break;
                case ConstTask.TASK_26_1:
                    if (isCurrentTask(player, idTaskCustom)) {
                        Service.gI().sendThongBao(player, "Tìm và tiêu diệt Android 13");
                    }
                    break;
                case ConstTask.TASK_26_2:
                    if (isCurrentTask(player, idTaskCustom)) {
                        Service.gI().sendThongBao(player, "Báo cáo với Bunma tương lai");
                    }
                    break;
                case ConstTask.TASK_26_3:
                    npcSay(player, ConstNpc.BUNMA_TL, "Bác học Kôrê thật sự là thiên tài\n"
                            + "Cả máy tính của ông ta cũng có thể\n"
                            + "tự động tạo ra Rôbốt sát thủ\n"
                            + "2 đứa Rôbốt sát thủ mà chúng tôi nói\n"
                            + "cỡ 17, 18 tuổi, 1 trai 1 gái ăn mặc như cao bồi\n"
                            + "Bề ngoài thấy hiền lành nhưng ra tay cực kì tàn độc\n"
                            + "Cậu phải cẩn thận đừng khinh địch.");
                    if (isCurrentTask(player, idTaskCustom)) {
                        Service.gI().sendThongBao(player, "Đến thành phố, ngọn núi, thung lũng phía Bắc");
                    }
                    break;

                //====================================================//
                case ConstTask.TASK_27_0:
                    if (isCurrentTask(player, idTaskCustom)) {
                        Service.gI().sendThongBao(player, "Tiêu diệt Píc");
                    }
                    break;
                case ConstTask.TASK_27_1:
                    if (isCurrentTask(player, idTaskCustom)) {
                        Service.gI().sendThongBao(player, "Tiêu diệt Póc");
                    }
                    break;
                case ConstTask.TASK_27_2:
                    if (isCurrentTask(player, idTaskCustom)) {
                        Service.gI().sendThongBao(player, "Tiêu diệt King Kong");
                    }
                    break;
                case ConstTask.TASK_27_3:
                    if (isCurrentTask(player, idTaskCustom)) {
                        Service.gI().sendThongBao(player, "Tiêu diệt 800 xên con cấp 5");
                    }
                    break;
                case ConstTask.TASK_27_4:
                    if (isCurrentTask(player, idTaskCustom)) {
                        Service.gI().sendThongBao(player, "Bạn đánh được "
                                + player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).count + "/"
                                + player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).maxCount
                                + transformName(player, " Xên con cấp 5"));
                    } else {
                        Service.gI().sendThongBao(player, "Về báo cáo với Bunma tương lai");
                    }
                    break;
                case ConstTask.TASK_27_5:
                    npcSay(player, ConstNpc.BUNMA_TL, "Tôi và Ca Lích vừa phát hiện ra 1 vỏ trứng kì lạ đã nở\n"
                            + "Gần đó còn có vỏ của một con ve sầu rất to vừa lột xác\n"
                            + "Cậu hãy đến thị trấn Ginder tọa độ 213-xyz xem thử\n"
                            + "Tôi nghi ngờ nó là 1 tác phẩm nữa của lão Kôrê\n"
                            + "Cậu cầm lấy cái này, đó là rađa rò tìm Capsule kì bí\n"
                            + "Chúc cậu tìm được vật gì đó thú vị");

                    Service.gI().sendThongBao(player, "Đến thị trận Ginder");
                    break;
                //============================================================

                case ConstTask.TASK_28_0:
                    Service.gI().sendThongBao(player, "Tiêu diệt Xên bọ hung cấp 1");
                    break;
                case ConstTask.TASK_28_1:
                    Service.gI().sendThongBao(player, "Tiêu diệt Xên bọ hung cấp 2");
                    break;
                case ConstTask.TASK_28_2:
                    Service.gI().sendThongBao(player, "Tiêu diệt Xên bọ hung hoàn thiện");
                    break;
                case ConstTask.TASK_28_3:
                    Service.gI().sendThongBao(player, "Tiêu diệt 700 xên con cấp 8");
                    break;
                case ConstTask.TASK_28_4:
                    if (isCurrentTask(player, idTaskCustom)) {
                        Service.gI().sendThongBao(player, "Bạn đánh được "
                                + player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).count + "/"
                                + player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).maxCount
                                + transformName(player, " Xên con cấp 8"));
                    } else {
                        Service.gI().sendThongBao(player, "Về báo cáo với Bunma tương lai");
                    }
                    break;
                case ConstTask.TASK_28_5:
                    npcSay(player, ConstNpc.BUNMA_TL, "Hắn sợ chúng ta quá nên bày trò câu giờ đây mà\n"
                            + "Cậu hãy tranh thủ 3 ngày này tập luyện để nâng cao sức mạnh bản thân nhé\n"
                            + "Capsule kì bí không chừng lại có ích\n"
                            + "Hãy thu thập 1 ít để phòng thân");

                //=============================================================
                case ConstTask.TASK_29_0:
                    npcSay(player, ConstNpc.THAN_MEO_KARIN, "Wow sức mạnh của con giờ không thể đo đếm được nữa rồi\n"
                            + "Nhưng Xên Bọ Hung vẫn còn nhỉnh hơn con 1 tí");
                    Service.gI().sendThongBao(player, "Đến tương lai thu\nthập Capsule kì bí\nnào, nhớ sử dụng\nmáy dò");
                    break;
                case ConstTask.TASK_29_1:
                    Service.gI().sendThongBao(player, "Hãy đến võ đài xên bọ hung");
                    break;
                case ConstTask.TASK_29_2:
                    Service.gI().sendThongBao(player, "Tiêu diệt lũ bọ hung con");
                    break;
                case ConstTask.TASK_29_3:
                    Service.gI().sendThongBao(player, "Hãy tiêu diệt Siêu Bọ Hung");
                    break;
                case ConstTask.TASK_29_4:
                    Service.gI().sendThongBao(player, "Về báo cho Bunma nào");
                    break;
                case ConstTask.TASK_29_5:
                    npcSay(player, ConstNpc.BUNMA_TL, "Chúc mừng cậu đã chiến thắng Siêu Bọ Hung\n"
                            + "Cám ơn cậu rất nhiều\n"
                            + "nếu rảnh rỗi cậu hãy đến đây tìm Capsule kì bí nhá");
                    break;

                //=======================================================//
                case ConstTask.TASK_30_0:
                    Service.gI().sendThongBao(player, "Hạ vua địa ngục\nDrabura");
                    break;
                case ConstTask.TASK_30_1:
                    Service.gI().sendThongBao(player, "Hạ Pui Pui");
                    break;
                case ConstTask.TASK_30_2:
                    Service.gI().sendThongBao(player, "Hạ Pui Pui lần 2");
                    break;
                case ConstTask.TASK_30_3:
                    Service.gI().sendThongBao(player, "Hạ Yacôn");
                    break;
                case ConstTask.TASK_30_4:
                    Service.gI().sendThongBao(player, "Hạ Drabura lần 2");
                    break;
                case ConstTask.TASK_30_5:
                    Service.gI().sendThongBao(player, "Hạ Mabư");
                    break;
                case ConstTask.TASK_30_6:
                    Service.gI().sendThongBao(player, "Báo cáo với Ôsin");
                    break;
            }
            PlayerService.gI().sendInfoHpMpMoney(player);
            return true;
        }
        return false;
    }

    private void npcSay(Player player, int npcId, String text) {
        npcId = transformNpcId(player, npcId);
        text = transformName(player, text);
        int avatar = NpcService.gI().getAvatar(npcId);
        NpcService.gI().createTutorial(player, avatar, text);
    }

    //Thưởng nhiệm vụ
    private void rewardDoneTask(Player player) {
        switch (player.playerTask.taskMain.id) {
            case 0:
                Service.gI().addSMTN(player, (byte) 0, 500, false);
                Service.gI().addSMTN(player, (byte) 1, 500, false);
                break;
            case 1:
                Service.gI().addSMTN(player, (byte) 0, 1000, false);
                Service.gI().addSMTN(player, (byte) 1, 1000, false);
                break;
            case 2:
                Service.gI().addSMTN(player, (byte) 0, 1200, false);
                Service.gI().addSMTN(player, (byte) 1, 1200, false);
                break;
            case 3:
                Service.gI().addSMTN(player, (byte) 0, 3000, false);
                Service.gI().addSMTN(player, (byte) 1, 3000, false);
                break;
            case 4:
                Service.gI().addSMTN(player, (byte) 0, 7000, false);
                Service.gI().addSMTN(player, (byte) 1, 7000, false);
                break;
            case 5:
                Service.gI().addSMTN(player, (byte) 0, 20000, false);
                Service.gI().addSMTN(player, (byte) 1, 20000, false);
                break;
            case 11:
                int itemId = 190;
                Item item = ItemService.gI().createNewItem(((short) itemId));
                ItemShop it = new Shop().getItemShop(itemId);
                if (it != null && !it.options.isEmpty()) {
                    item.itemOptions.addAll(it.options);
                }
                item.quantity = 10;
                InventoryService.gI().addItemBag(player, item);
                InventoryService.gI().sendItemBag(player);
                break;
            case 13:
                itemId = 85;
                item = ItemService.gI().createNewItem(((short) itemId));
                it = new Shop().getItemShop(itemId);
                if (it != null && !it.options.isEmpty()) {
                    item.itemOptions.addAll(it.options);
                }
                InventoryService.gI().addItemBag(player, item);
                InventoryService.gI().sendItemBag(player);
                Service.gI().sendThongBao(player, "null");
                break;
            case 33:

                break;
        }

    }

    // vd: pem đc 1 mộc nhân -> +1 mộc nhân vào nv hiện tại
    private void addDoneSubTask(Player player, int numDone) {
        player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).count += numDone;
        if (player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).count
                >= player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).maxCount) {
            player.playerTask.taskMain.index++;
            if (player.playerTask.taskMain.index >= player.playerTask.taskMain.subTasks.size()) {
                this.sendNextTaskMain(player);
            } else {
                this.sendNextSubTask(player);
            }
        } else {
            this.sendUpdateCountSubTask(player);
        }
    }

    public int transformMapId(Player player, int id) {
        if (id == ConstTask.MAP_NHA) {
            return (short) (player.gender + 21);
        } else if (id == ConstTask.MAP_200) {
            return player.gender == ConstPlayer.TRAI_DAT
                    ? 1 : (player.gender == ConstPlayer.NAMEC
                            ? 8 : 15);
        } else if (id == ConstTask.MAP_VACH_NUI) {
            return player.gender == ConstPlayer.TRAI_DAT
                    ? 39 : (player.gender == ConstPlayer.NAMEC
                            ? 40 : 41);
        } else if (id == ConstTask.MAP_200) {
            return player.gender == ConstPlayer.TRAI_DAT
                    ? 2 : (player.gender == ConstPlayer.NAMEC
                            ? 9 : 16);
        } else if (id == ConstTask.MAP_TTVT) {
            return player.gender == ConstPlayer.TRAI_DAT
                    ? 24 : (player.gender == ConstPlayer.NAMEC
                            ? 25 : 26);
        } else if (id == ConstTask.MAP_QUAI_BAY_600) {
            return player.gender == ConstPlayer.TRAI_DAT
                    ? 3 : (player.gender == ConstPlayer.NAMEC
                            ? 11 : 17);
        } else if (id == ConstTask.MAP_LANG) {
            return player.gender == ConstPlayer.TRAI_DAT
                    ? 0 : (player.gender == ConstPlayer.NAMEC
                            ? 7 : 14);
        } else if (id == ConstTask.MAP_QUY_LAO) {
            return player.gender == ConstPlayer.TRAI_DAT
                    ? 5 : (player.gender == ConstPlayer.NAMEC
                            ? 13 : 20);
        }
        return id;
    }

    private int transformNpcId(Player player, int id) {
        if (id == ConstTask.NPC_NHA) {
            return player.gender == ConstPlayer.TRAI_DAT
                    ? ConstNpc.ONG_GOHAN : (player.gender == ConstPlayer.NAMEC
                            ? ConstNpc.ONG_MOORI : ConstNpc.ONG_PARAGUS);
        } else if (id == ConstTask.NPC_TTVT) {
            return player.gender == ConstPlayer.TRAI_DAT
                    ? ConstNpc.DR_DRIEF : (player.gender == ConstPlayer.NAMEC
                            ? ConstNpc.CARGO : ConstNpc.CUI);
        } else if (id == ConstTask.NPC_SHOP_LANG) {
            return player.gender == ConstPlayer.TRAI_DAT
                    ? ConstNpc.BUNMA : (player.gender == ConstPlayer.NAMEC
                            ? ConstNpc.DENDE : ConstNpc.APPULE);
        } else if (id == ConstTask.NPC_QUY_LAO) {
            return player.gender == ConstPlayer.TRAI_DAT
                    ? ConstNpc.QUY_LAO_KAME : (player.gender == ConstPlayer.NAMEC
                            ? ConstNpc.TRUONG_LAO_GURU : ConstNpc.VUA_VEGETA);
        }
        return id;
    }

    // replate %1 %2 -> chữ
    private String transformName(Player player, String text) {
        byte gender = player.gender;
        text = text.replaceAll(ConstTask.TEN_QUAI_1000, player.gender == ConstPlayer.XAYDA
                ? "thằn lằn mẹ" : (player.gender == ConstPlayer.TRAI_DAT
                        ? "phi long mẹ" : "quỷ bay mẹ"));
        text = text.replaceAll(ConstTask.TEN_MAP_600, player.gender == ConstPlayer.TRAI_DAT
                ? "Rừng nấm" : (player.gender == ConstPlayer.NAMEC
                        ? "Thung lũng Namếc" : "Rừng nguyên sinh"));
        text = text.replaceAll(ConstTask.TEN_NPC_QUY_LAO, player.gender == ConstPlayer.TRAI_DAT
                ? "Quy Lão Kame" : (player.gender == ConstPlayer.NAMEC
                        ? "Trưởng lão Guru" : "Vua Vegeta"));
        text = text.replaceAll(ConstTask.TEN_MAP_QUY_LAO, player.gender == ConstPlayer.TRAI_DAT
                ? "Đảo Kamê" : (player.gender == ConstPlayer.NAMEC
                        ? "Đảo Guru" : "Vách núi đen"));
        text = text.replaceAll(ConstTask.TEN_QUAI_3000, player.gender == ConstPlayer.TRAI_DAT
                ? "ốc mượn hồn" : (player.gender == ConstPlayer.NAMEC
                        ? "ốc sên" : "heo Xayda mẹ"));
        //----------------------------------------------------------------------
        text = text.replaceAll(ConstTask.TEN_LANG, player.gender == ConstPlayer.TRAI_DAT
                ? "Làng Aru" : (player.gender == ConstPlayer.NAMEC
                        ? "Làng Mori" : "Làng Kakarot"));
        text = text.replaceAll(ConstTask.TEN_NPC_NHA, player.gender == ConstPlayer.TRAI_DAT
                ? "ông Gôhan" : (player.gender == ConstPlayer.NAMEC
                        ? "ông Moori" : "ông Paragus"));
        text = text.replaceAll(ConstTask.TEN_QUAI_200, player.gender == ConstPlayer.TRAI_DAT
                ? "khủng long" : (player.gender == ConstPlayer.NAMEC
                        ? "lợn lòi" : "quỷ đất"));
        text = text.replaceAll(ConstTask.TEN_MAP_200, player.gender == ConstPlayer.TRAI_DAT
                ? "Đồi hoa cúc" : (player.gender == ConstPlayer.NAMEC
                        ? "Đồi nấm tím" : "Đồi hoang"));
        text = text.replaceAll(ConstTask.TEN_VACH_NUI, player.gender == ConstPlayer.TRAI_DAT
                ? "Vách núi Aru" : (player.gender == ConstPlayer.NAMEC
                        ? "Vách núi Moori" : "Vách núi Kakarot"));
        text = text.replaceAll(ConstTask.TEN_MAP_500, player.gender == ConstPlayer.TRAI_DAT
                ? "Thung lũng tre" : (player.gender == ConstPlayer.NAMEC
                        ? "Thị trấn Moori" : "Làng Plant"));
        text = text.replaceAll(ConstTask.TEN_NPC_TTVT, player.gender == ConstPlayer.TRAI_DAT
                ? "Dr. Brief" : (player.gender == ConstPlayer.NAMEC
                        ? "Cargo" : "Cui"));
        text = text.replaceAll(ConstTask.TEN_QUAI_BAY_600, player.gender == ConstPlayer.TRAI_DAT
                ? "thằn lằn bay" : (player.gender == ConstPlayer.NAMEC
                        ? "phi long" : "quỷ bay"));
        text = text.replaceAll(ConstTask.TEN_NPC_SHOP_LANG, player.gender == ConstPlayer.TRAI_DAT
                ? "Bunma" : (player.gender == ConstPlayer.NAMEC
                        ? "Dende" : "Appule"));
        return text;
    }

    private boolean isCurrentTask(Player player, int idTaskCustom) {
        return (player != null && player.playerTask != null && player.playerTask.taskMain != null && idTaskCustom == (player.playerTask.taskMain.id << 10) + player.playerTask.taskMain.index << 1);
    }

    public int getIdTask(Player player) {
        if (player.isPet || player.isBoss || player.playerTask == null || player.playerTask.taskMain == null) {
            return -1;
        }
        return (player.playerTask.taskMain.id << 10) + player.playerTask.taskMain.index << 1;
    }

    //========================SIDE TASK========================
    public SideTaskTemplate getSideTaskTemplateById(int id) {
        if (id != -1) {
            return Manager.SIDE_TASKS_TEMPLATE.get(id);
        }
        return null;
    }

    public void changeSideTask(Player player, byte level) {
        player.playerTask.sideTask.renew();
        if (player.playerTask.sideTask.leftTask > 0) {
            player.playerTask.sideTask.reset();
            SideTaskTemplate temp = Manager.SIDE_TASKS_TEMPLATE.get(Util.nextInt(0, Manager.SIDE_TASKS_TEMPLATE.size() - 1));
            player.playerTask.sideTask.template = temp;
            player.playerTask.sideTask.maxCount = Util.nextInt(temp.count[level][0], temp.count[level][1]);
            player.playerTask.sideTask.leftTask--;
            player.playerTask.sideTask.level = level;
            player.playerTask.sideTask.receivedTime = System.currentTimeMillis();
            Service.gI().sendThongBao(player, "Bạn nhận được nhiệm vụ: " + player.playerTask.sideTask.getName());
        } else {
            Service.gI().sendThongBao(player,
                    "Bạn đã nhận hết nhiệm vụ hôm nay. Hãy chờ tới ngày mai rồi nhận tiếp");
        }
    }

    public void removeSideTask(Player player) {
        Service.gI().sendThongBao(player, "Bạn vừa hủy bỏ nhiệm vụ " + player.playerTask.sideTask.getName());
        player.playerTask.sideTask.reset();
    }

    public void paySideTask(Player player) {
        if (player.playerTask.sideTask.template != null) {
            if (player.playerTask.sideTask.isDone()) {
                int goldReward = 0;

                // Tính phần thưởng vàng dựa trên độ khó của nhiệm vụ
                switch (player.playerTask.sideTask.level) {
                    case ConstTask.EASY:
                        goldReward = ConstTask.GOLD_EASY;
                        break;
                    case ConstTask.NORMAL:
                        goldReward = ConstTask.GOLD_NORMAL;
                        break;
                    case ConstTask.HARD:
                        goldReward = ConstTask.GOLD_HARD;
                        break;
                    case ConstTask.VERY_HARD:
                        goldReward = ConstTask.GOLD_VERY_HARD;

                        BadgesTaskService.updateCountBagesTask(player, ConstTaskBadges.NONG_DAN_CHAM_CHI, 1);
                        break;
                    case ConstTask.HELL:
                        goldReward = ConstTask.GOLD_HELL;

                        BadgesTaskService.updateCountBagesTask(player, ConstTaskBadges.NONG_DAN_CHAM_CHI, 1);
                        break;
                }

                if (player.mabuEgg != null) {
                    switch (player.playerTask.sideTask.level) {
                        case ConstTask.EASY:
                            player.mabuEgg.subTimeDone(0, 0, 30, 0);
                            break;
                        case ConstTask.NORMAL:
                            player.mabuEgg.subTimeDone(0, 1, 0, 0);
                            break;
                        case ConstTask.HARD:
                            player.mabuEgg.subTimeDone(0, 2, 0, 0);
                            break;
                        case ConstTask.VERY_HARD:
                            player.mabuEgg.subTimeDone(0, 4, 30, 0);
                            break;
                        case ConstTask.HELL:
                            player.mabuEgg.subTimeDone(0, 2, 0, 0);
                            break;
                    }
                } else {
                    // Thông báo lỗi hoặc xử lý khi mabuEgg null
                    System.out.println("Lỗi: mabuEgg không tồn tại.");
                }

                // Thêm vàng vào hành trang và gửi thông báo
                if (InventoryService.gI().getCountEmptyBag(player) > 1) {
                    player.inventory.addGold(goldReward);
                    Service.gI().sendMoney(player);
                    Service.gI().sendThongBao(player, "Bạn nhận được " + Util.numberToMoney(goldReward) + " vàng");
                    player.playerTask.sideTask.reset();
                } else {
                    Service.gI().sendThongBao(player, "Hành trang không đủ chỗ trống.");
                }
                InventoryService.gI().sendItemBag(player); // Cập nhật lại hành trang
            } else {
                // Thông báo nếu người chơi chưa hoàn thành nhiệm vụ
                Service.gI().sendThongBao(player, "Bạn chưa hoàn thành nhiệm vụ");
            }
        }
    }

    public void checkDoneSideTaskKillMob(Player player, Mob mob) {
        if (player.playerTask != null && player.playerTask.sideTask.template != null) {
            if ((player.playerTask.sideTask.template.id == 0 && mob.tempId == ConstMob.KHUNG_LONG)
                    || (player.playerTask.sideTask.template.id == 1 && mob.tempId == ConstMob.LON_LOI)
                    || (player.playerTask.sideTask.template.id == 2 && mob.tempId == ConstMob.QUY_DAT)
                    || (player.playerTask.sideTask.template.id == 3 && mob.tempId == ConstMob.KHUNG_LONG_ME)
                    || (player.playerTask.sideTask.template.id == 4 && mob.tempId == ConstMob.LON_LOI_ME)
                    || (player.playerTask.sideTask.template.id == 5 && mob.tempId == ConstMob.QUY_DAT_ME)
                    || (player.playerTask.sideTask.template.id == 6 && mob.tempId == ConstMob.THAN_LAN_BAY)
                    || (player.playerTask.sideTask.template.id == 7 && mob.tempId == ConstMob.PHI_LONG)
                    || (player.playerTask.sideTask.template.id == 8 && mob.tempId == ConstMob.QUY_BAY)
                    || (player.playerTask.sideTask.template.id == 9 && mob.tempId == ConstMob.THAN_LAN_ME)
                    || (player.playerTask.sideTask.template.id == 10 && mob.tempId == ConstMob.PHI_LONG_ME)
                    || (player.playerTask.sideTask.template.id == 11 && mob.tempId == ConstMob.QUY_BAY_ME)
                    || (player.playerTask.sideTask.template.id == 12 && mob.tempId == ConstMob.HEO_RUNG)
                    || (player.playerTask.sideTask.template.id == 13 && mob.tempId == ConstMob.HEO_DA_XANH)
                    || (player.playerTask.sideTask.template.id == 14 && mob.tempId == ConstMob.HEO_XAYDA)
                    || (player.playerTask.sideTask.template.id == 15 && mob.tempId == ConstMob.OC_MUON_HON)
                    || (player.playerTask.sideTask.template.id == 16 && mob.tempId == ConstMob.OC_SEN)
                    || (player.playerTask.sideTask.template.id == 17 && mob.tempId == ConstMob.HEO_XAYDA_ME)
                    || (player.playerTask.sideTask.template.id == 18 && mob.tempId == ConstMob.KHONG_TAC)
                    || (player.playerTask.sideTask.template.id == 19 && mob.tempId == ConstMob.QUY_DAU_TO)
                    || (player.playerTask.sideTask.template.id == 20 && mob.tempId == ConstMob.QUY_DIA_NGUC)
                    || (player.playerTask.sideTask.template.id == 21 && mob.tempId == ConstMob.HEO_RUNG_ME)
                    || (player.playerTask.sideTask.template.id == 22 && mob.tempId == ConstMob.HEO_XANH_ME)
                    || (player.playerTask.sideTask.template.id == 23 && mob.tempId == ConstMob.ALIEN)
                    || (player.playerTask.sideTask.template.id == 24 && mob.tempId == ConstMob.TAMBOURINE)
                    || (player.playerTask.sideTask.template.id == 25 && mob.tempId == ConstMob.DRUM)
                    || (player.playerTask.sideTask.template.id == 26 && mob.tempId == ConstMob.AKKUMAN)
                    || (player.playerTask.sideTask.template.id == 27 && mob.tempId == ConstMob.NAPPA)
                    || (player.playerTask.sideTask.template.id == 28 && mob.tempId == ConstMob.SOLDIER)
                    || (player.playerTask.sideTask.template.id == 29 && mob.tempId == ConstMob.APPULE)
                    || (player.playerTask.sideTask.template.id == 30 && mob.tempId == ConstMob.RASPBERRY)
                    || (player.playerTask.sideTask.template.id == 31 && mob.tempId == ConstMob.THAN_LAN_XANH)
                    || (player.playerTask.sideTask.template.id == 32 && mob.tempId == ConstMob.QUY_DAU_NHON)
                    || (player.playerTask.sideTask.template.id == 33 && mob.tempId == ConstMob.QUY_DAU_VANG)
                    || (player.playerTask.sideTask.template.id == 34 && mob.tempId == ConstMob.QUY_DA_TIM)
                    || (player.playerTask.sideTask.template.id == 35 && mob.tempId == ConstMob.QUY_GIA)
                    || (player.playerTask.sideTask.template.id == 36 && mob.tempId == ConstMob.CA_SAU)
                    || (player.playerTask.sideTask.template.id == 37 && mob.tempId == ConstMob.DOI_DA_XANH)
                    || (player.playerTask.sideTask.template.id == 38 && mob.tempId == ConstMob.QUY_CHIM)
                    || (player.playerTask.sideTask.template.id == 39 && mob.tempId == ConstMob.LINH_DAU_TROC)
                    || (player.playerTask.sideTask.template.id == 40 && mob.tempId == ConstMob.LINH_TAI_DAI)
                    || (player.playerTask.sideTask.template.id == 41 && mob.tempId == ConstMob.LINH_VU_TRU)
                    || (player.playerTask.sideTask.template.id == 42 && mob.tempId == ConstMob.KHI_LONG_DEN)
                    || (player.playerTask.sideTask.template.id == 43 && mob.tempId == ConstMob.KHI_GIAP_SAT)
                    || (player.playerTask.sideTask.template.id == 44 && mob.tempId == ConstMob.KHI_LONG_DO)
                    || (player.playerTask.sideTask.template.id == 45 && mob.tempId == ConstMob.KHI_LONG_VANG)
                    || (player.playerTask.sideTask.template.id == 46 && mob.tempId == ConstMob.XEN_CON_CAP_1)
                    || (player.playerTask.sideTask.template.id == 47 && mob.tempId == ConstMob.XEN_CON_CAP_2)
                    || (player.playerTask.sideTask.template.id == 48 && mob.tempId == ConstMob.XEN_CON_CAP_3)
                    || (player.playerTask.sideTask.template.id == 49 && mob.tempId == ConstMob.XEN_CON_CAP_4)
                    || (player.playerTask.sideTask.template.id == 50 && mob.tempId == ConstMob.XEN_CON_CAP_5)
                    || (player.playerTask.sideTask.template.id == 51 && mob.tempId == ConstMob.XEN_CON_CAP_6)
                    || (player.playerTask.sideTask.template.id == 52 && mob.tempId == ConstMob.XEN_CON_CAP_7)
                    || (player.playerTask.sideTask.template.id == 53 && mob.tempId == ConstMob.XEN_CON_CAP_8)
                    || (player.playerTask.sideTask.template.id == 54 && mob.tempId == ConstMob.TAI_TIM)
                    || (player.playerTask.sideTask.template.id == 55 && mob.tempId == ConstMob.ABO)
                    || (player.playerTask.sideTask.template.id == 56 && mob.tempId == ConstMob.KADO)
                    || (player.playerTask.sideTask.template.id == 57 && mob.tempId == ConstMob.DA_XANH)) {
                player.playerTask.sideTask.count++;
                notifyProcessSideTask(player);
            }
        }
    }

    public void checkDoneSideTaskPickItem(Player player, ItemMap item) {
        if (player.playerTask != null && player.playerTask.sideTask != null && player.playerTask.sideTask.template != null) {
            if ((player.playerTask.sideTask.template.id == 58 && item.itemTemplate.type == 9)) {
                player.playerTask.sideTask.count += item.quantity;
                notifyProcessSideTask(player);
            }
        }
    }

    private void notifyProcessSideTask(Player player) {
        int percentDone = player.playerTask.sideTask.getPercentProcess();
        boolean notify = false;
        if (percentDone != 100) {
            if (!player.playerTask.sideTask.notify90 && percentDone >= 90) {
                player.playerTask.sideTask.notify90 = true;
                notify = true;
            } else if (!player.playerTask.sideTask.notify80 && percentDone >= 80) {
                player.playerTask.sideTask.notify80 = true;
                notify = true;
            } else if (!player.playerTask.sideTask.notify70 && percentDone >= 70) {
                player.playerTask.sideTask.notify70 = true;
                notify = true;
            } else if (!player.playerTask.sideTask.notify60 && percentDone >= 60) {
                player.playerTask.sideTask.notify60 = true;
                notify = true;
            } else if (!player.playerTask.sideTask.notify50 && percentDone >= 50) {
                player.playerTask.sideTask.notify50 = true;
                notify = true;
            } else if (!player.playerTask.sideTask.notify40 && percentDone >= 40) {
                player.playerTask.sideTask.notify40 = true;
                notify = true;
            } else if (!player.playerTask.sideTask.notify30 && percentDone >= 30) {
                player.playerTask.sideTask.notify30 = true;
                notify = true;
            } else if (!player.playerTask.sideTask.notify20 && percentDone >= 20) {
                player.playerTask.sideTask.notify20 = true;
                notify = true;
            } else if (!player.playerTask.sideTask.notify10 && percentDone >= 10) {
                player.playerTask.sideTask.notify10 = true;
                notify = true;
            } else if (!player.playerTask.sideTask.notify0 && percentDone >= 0) {
                player.playerTask.sideTask.notify0 = true;
                notify = true;
            }
            if (notify) {
                Service.gI().sendThongBao(player, "Nhiệm vụ: "
                        + player.playerTask.sideTask.getName() + " đã hoàn thành: "
                        + player.playerTask.sideTask.count + "/" + player.playerTask.sideTask.maxCount + " ("
                        + percentDone + "%)");
            }
        } else {
            Service.gI().sendThongBao(player, "Chúc mừng bạn đã hoàn thành nhiệm vụ, "
                    + "bây giờ hãy quay về Bò Mộng trả nhiệm vụ.");
        }
    }

    //========================CLAN TASK========================
    public ClanTaskTemplate getClanTaskTemplateById(int id) {
        if (id != -1) {
            return Manager.CLAN_TASKS_TEMPLATE.get(id);
        }
        return null;
    }

    public void changeClanTask(Npc npc, Player player, byte level) {
        player.playerTask.clanTask.renew();
        if (player.playerTask.clanTask.leftTask > 0) {
            player.playerTask.clanTask.reset();
            ClanTaskTemplate temp = Manager.CLAN_TASKS_TEMPLATE.get(Util.nextInt(0, Manager.CLAN_TASKS_TEMPLATE.size() - 1));
            player.playerTask.clanTask.template = temp;
            player.playerTask.clanTask.maxCount = Util.nextInt(temp.count[level][0], temp.count[level][1]);
            player.playerTask.clanTask.level = level;
            player.playerTask.clanTask.receivedTime = System.currentTimeMillis();
            npc.createOtherMenu(player, ConstNpc.MENU_CLAN_TASK, "Nhiệm vụ hiện tại: " + player.playerTask.clanTask.getName() + ". Đã hạ được " + player.playerTask.clanTask.count, "OK", "Hủy bỏ\nNhiệm vụ\nnày");
            //Hạ abc tại def
        } else {
            npc.createOtherMenu(player, ConstNpc.MENU_CLAN_TASK, "Đã hết nhiệm vụ cho hôm nay, hãy chờ đến ngày mai", "OK", "Từ chối");
        }
    }

    public void removeClanTask(Player player) {
        Service.gI().sendThongBao(player, "Đã hủy nhiệm vụ bang.");
        player.playerTask.clanTask.leftTask--;
        player.playerTask.clanTask.reset();
    }

    public void payClanTask(Player player) {
        if (player.playerTask.clanTask.template != null) {
            if (player.playerTask.clanTask.isDone()) {
                int capsuleClan = (player.playerTask.clanTask.level + 1) * 10;
                player.playerTask.clanTask.leftTask--;
                player.playerTask.clanTask.reset();
                Service.gI().sendThongBao(player, "Bạn vừa nhận được "
                        + Util.numberToMoney(capsuleClan) + " capsule bang.");
                if (player.clan != null) {
                    player.clan.capsuleClan += capsuleClan;
                    for (ClanMember cm : player.clan.getMembers()) {
                        if (cm.id == player.id) {
                            cm.memberPoint += capsuleClan;
                            cm.clanPoint += capsuleClan;
                            break;
                        }
                    }
                    for (ClanMember cm : player.clan.getMembers()) {
                        Player pl = Client.gI().getPlayer(cm.id);
                        if (pl != null) {
                            ClanService.gI().sendMyClan(player);
                        }
                    }
                }
            } else {
                Service.gI().sendThongBao(player, "Bạn chưa hoàn thành nhiệm vụ");
            }
        }
    }

    public void checkDoneClanTaskKillMob(Player player, Mob mob) {
        if (player.playerTask != null && player.playerTask.clanTask.template != null) {
            if ((player.playerTask.clanTask.template.id == 0 && mob.tempId == ConstMob.KHUNG_LONG)
                    || (player.playerTask.clanTask.template.id == 1 && mob.tempId == ConstMob.LON_LOI)
                    || (player.playerTask.clanTask.template.id == 2 && mob.tempId == ConstMob.QUY_DAT)
                    || (player.playerTask.clanTask.template.id == 3 && mob.tempId == ConstMob.KHUNG_LONG_ME)
                    || (player.playerTask.clanTask.template.id == 4 && mob.tempId == ConstMob.LON_LOI_ME)
                    || (player.playerTask.clanTask.template.id == 5 && mob.tempId == ConstMob.QUY_DAT_ME)
                    || (player.playerTask.clanTask.template.id == 6 && mob.tempId == ConstMob.THAN_LAN_BAY)
                    || (player.playerTask.clanTask.template.id == 7 && mob.tempId == ConstMob.PHI_LONG)
                    || (player.playerTask.clanTask.template.id == 8 && mob.tempId == ConstMob.QUY_BAY)
                    || (player.playerTask.clanTask.template.id == 9 && mob.tempId == ConstMob.THAN_LAN_ME)
                    || (player.playerTask.clanTask.template.id == 10 && mob.tempId == ConstMob.PHI_LONG_ME)
                    || (player.playerTask.clanTask.template.id == 11 && mob.tempId == ConstMob.QUY_BAY_ME)
                    || (player.playerTask.clanTask.template.id == 12 && mob.tempId == ConstMob.HEO_RUNG)
                    || (player.playerTask.clanTask.template.id == 13 && mob.tempId == ConstMob.HEO_DA_XANH)
                    || (player.playerTask.clanTask.template.id == 14 && mob.tempId == ConstMob.HEO_XAYDA)
                    || (player.playerTask.clanTask.template.id == 15 && mob.tempId == ConstMob.OC_MUON_HON)
                    || (player.playerTask.clanTask.template.id == 16 && mob.tempId == ConstMob.OC_SEN)
                    || (player.playerTask.clanTask.template.id == 17 && mob.tempId == ConstMob.HEO_XAYDA_ME)
                    || (player.playerTask.clanTask.template.id == 18 && mob.tempId == ConstMob.KHONG_TAC)
                    || (player.playerTask.clanTask.template.id == 19 && mob.tempId == ConstMob.QUY_DAU_TO)
                    || (player.playerTask.clanTask.template.id == 20 && mob.tempId == ConstMob.QUY_DIA_NGUC)
                    || (player.playerTask.clanTask.template.id == 21 && mob.tempId == ConstMob.HEO_RUNG_ME)
                    || (player.playerTask.clanTask.template.id == 22 && mob.tempId == ConstMob.HEO_XANH_ME)
                    || (player.playerTask.clanTask.template.id == 23 && mob.tempId == ConstMob.ALIEN)
                    || (player.playerTask.clanTask.template.id == 24 && mob.tempId == ConstMob.TAMBOURINE)
                    || (player.playerTask.clanTask.template.id == 25 && mob.tempId == ConstMob.DRUM)
                    || (player.playerTask.clanTask.template.id == 26 && mob.tempId == ConstMob.AKKUMAN)
                    || (player.playerTask.clanTask.template.id == 27 && mob.tempId == ConstMob.NAPPA)
                    || (player.playerTask.clanTask.template.id == 28 && mob.tempId == ConstMob.SOLDIER)
                    || (player.playerTask.clanTask.template.id == 29 && mob.tempId == ConstMob.APPULE)
                    || (player.playerTask.clanTask.template.id == 30 && mob.tempId == ConstMob.RASPBERRY)
                    || (player.playerTask.clanTask.template.id == 31 && mob.tempId == ConstMob.THAN_LAN_XANH)
                    || (player.playerTask.clanTask.template.id == 32 && mob.tempId == ConstMob.QUY_DAU_NHON)
                    || (player.playerTask.clanTask.template.id == 33 && mob.tempId == ConstMob.QUY_DAU_VANG)
                    || (player.playerTask.clanTask.template.id == 34 && mob.tempId == ConstMob.QUY_DA_TIM)
                    || (player.playerTask.clanTask.template.id == 35 && mob.tempId == ConstMob.QUY_GIA)
                    || (player.playerTask.clanTask.template.id == 36 && mob.tempId == ConstMob.CA_SAU)
                    || (player.playerTask.clanTask.template.id == 37 && mob.tempId == ConstMob.DOI_DA_XANH)
                    || (player.playerTask.clanTask.template.id == 38 && mob.tempId == ConstMob.QUY_CHIM)
                    || (player.playerTask.clanTask.template.id == 39 && mob.tempId == ConstMob.LINH_DAU_TROC)
                    || (player.playerTask.clanTask.template.id == 40 && mob.tempId == ConstMob.LINH_TAI_DAI)
                    || (player.playerTask.clanTask.template.id == 41 && mob.tempId == ConstMob.LINH_VU_TRU)
                    || (player.playerTask.clanTask.template.id == 42 && mob.tempId == ConstMob.KHI_LONG_DEN)
                    || (player.playerTask.clanTask.template.id == 43 && mob.tempId == ConstMob.KHI_GIAP_SAT)
                    || (player.playerTask.clanTask.template.id == 44 && mob.tempId == ConstMob.KHI_LONG_DO)
                    || (player.playerTask.clanTask.template.id == 45 && mob.tempId == ConstMob.KHI_LONG_VANG)
                    || (player.playerTask.clanTask.template.id == 46 && mob.tempId == ConstMob.XEN_CON_CAP_1)
                    || (player.playerTask.clanTask.template.id == 47 && mob.tempId == ConstMob.XEN_CON_CAP_2)
                    || (player.playerTask.clanTask.template.id == 48 && mob.tempId == ConstMob.XEN_CON_CAP_3)
                    || (player.playerTask.clanTask.template.id == 49 && mob.tempId == ConstMob.XEN_CON_CAP_4)
                    || (player.playerTask.clanTask.template.id == 50 && mob.tempId == ConstMob.XEN_CON_CAP_5)
                    || (player.playerTask.clanTask.template.id == 51 && mob.tempId == ConstMob.XEN_CON_CAP_6)
                    || (player.playerTask.clanTask.template.id == 52 && mob.tempId == ConstMob.XEN_CON_CAP_7)
                    || (player.playerTask.clanTask.template.id == 53 && mob.tempId == ConstMob.XEN_CON_CAP_8)
                    || (player.playerTask.clanTask.template.id == 54 && mob.tempId == ConstMob.TAI_TIM)
                    || (player.playerTask.clanTask.template.id == 55 && mob.tempId == ConstMob.ABO)
                    || (player.playerTask.clanTask.template.id == 56 && mob.tempId == ConstMob.KADO)
                    || (player.playerTask.clanTask.template.id == 57 && mob.tempId == ConstMob.DA_XANH)) {
                player.playerTask.clanTask.count++;
                notifyProcessClanTask(player);
            }
        }
    }

    public void checkDoneClanTaskPickItem(Player player, ItemMap item) {
        if (player.playerTask != null && player.playerTask.clanTask != null && player.playerTask.clanTask.template != null && item != null && item.itemTemplate != null) {
            if ((player.playerTask.clanTask.template.id == 58 && item.itemTemplate.type == 9)) {
                player.playerTask.clanTask.count += item.quantity;
                notifyProcessClanTask(player);
            }
        }
    }

    private void notifyProcessClanTask(Player player) {
        int percentDone = player.playerTask.clanTask.getPercentProcess();
        boolean notify = false;
        if (percentDone != 100) {
            if (!player.playerTask.clanTask.notify90 && percentDone >= 90) {
                player.playerTask.clanTask.notify90 = true;
                notify = true;
            } else if (!player.playerTask.clanTask.notify80 && percentDone >= 80) {
                player.playerTask.clanTask.notify80 = true;
                notify = true;
            } else if (!player.playerTask.clanTask.notify70 && percentDone >= 70) {
                player.playerTask.clanTask.notify70 = true;
                notify = true;
            } else if (!player.playerTask.clanTask.notify60 && percentDone >= 60) {
                player.playerTask.clanTask.notify60 = true;
                notify = true;
            } else if (!player.playerTask.clanTask.notify50 && percentDone >= 50) {
                player.playerTask.clanTask.notify50 = true;
                notify = true;
            } else if (!player.playerTask.clanTask.notify40 && percentDone >= 40) {
                player.playerTask.clanTask.notify40 = true;
                notify = true;
            } else if (!player.playerTask.clanTask.notify30 && percentDone >= 30) {
                player.playerTask.clanTask.notify30 = true;
                notify = true;
            } else if (!player.playerTask.clanTask.notify20 && percentDone >= 20) {
                player.playerTask.clanTask.notify20 = true;
                notify = true;
            } else if (!player.playerTask.clanTask.notify10 && percentDone >= 10) {
                player.playerTask.clanTask.notify10 = true;
                notify = true;
            } else if (!player.playerTask.clanTask.notify0 && percentDone >= 0) {
                player.playerTask.clanTask.notify0 = true;
                notify = true;
            }
            if (notify) {
                Service.gI().sendThongBao(player, "Nhiệm vụ: "
                        + player.playerTask.clanTask.getName() + " đã hoàn thành: "
                        + player.playerTask.clanTask.count + "/" + player.playerTask.clanTask.maxCount + " ("
                        + percentDone + "%)");
            }
        } else {
            Service.gI().sendThongBao(player, "Tiếp theo hãy về Bang hội báo cáo.");
        }
    }

    public void checkDoneTask31(Player player) {
        doneTask(player, ConstTask.TASK_31_3);
         doneTask(player, ConstTask.TASK_31_2);
        doneTask(player, ConstTask.TASK_31_5);
        doneTask(player, ConstTask.TASK_31_6);
        InventoryService.gI().subQuantityItemsBag(player, InventoryService.gI().findItemBag(player, 993), 500);
    }

    public void checkDoneTask31_7(Player player) {
        doneTask(player, ConstTask.TASK_31_7);
        InventoryService.gI().subQuantityItemsBag(player, InventoryService.gI().findItemBag(player, 993), 500);
    }
}
