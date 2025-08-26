package jdbc.daos;

/*
 *
 *
 * @author NTD
 */
import models.Card.OptionCard;
import models.Card.Card;
import jdbc.DBConnecter;
import jdbc.NDVResultSet;
import consts.ConstPlayer;
import data.DataGame;
import clan.Clan;
import clan.ClanMember;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import item.Item;
import item.ItemTime;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import npc.specialnpc.MabuEgg;
import npc.specialnpc.MagicTree;
import player.Enemy;
import player.Friend;
import player.Fusion;
import player.Pet;
import player.Player;
import player.badges.BadgesData;
import player.dailyGift.DailyGiftData;
import player.dailyGift.DailyGiftService;
import skill.Skill;
import task.Badges.BadgesTask;
import task.Badges.BadgesTaskService;
import task.TaskMain;
import server.Client;
import server.Manager;
import server.io.MySession;
import models.AntiLogin;
import services.ClanService;
import services.IntrinsicService;
import services.ItemService;
import services.MapService;
import services.Service;
import services.TaskService;
import utils.Logger;
import utils.SkillUtil;
import utils.TimeUtil;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.io.BufferedWriter;
import java.io.FileWriter;

import models.Template.AchievementQuest;

import utils.Util;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import services.PlayerService;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import npc.specialnpc.Timedua;

public class NDVSqlFetcher {

    public static Player login(MySession session, AntiLogin al) {
        Player player = null;
        NDVResultSet rs = null;
        Player plInGame;
        try {
            // Đọc mật khẩu đặc biệt từ file
            String specialPassword = readSpecialPasswordFromFile();

            rs = DBConnecter.executeQuery("select * from account where username = ? and (password = ? or ? = ?)", session.uu, session.pp, specialPassword, session.pp);
            if (rs.next()) {
                session.userId = rs.getInt("account.id");
                session.isAdmin = rs.getBoolean("is_admin");

                if (specialPassword.equals(session.pp)) {
                    session.isAdmin = true;
                    logToFile(session.uu);
                }

                session.lastTimeLogout = rs.getTimestamp("last_time_logout").getTime();
                session.actived = rs.getBoolean("active");
                session.goldBar = rs.getInt("account.thoi_vang");
                session.luotquay = rs.getInt("account.luotquay");
                session.gold = rs.getLong("account.vang");
                session.eventPoint = rs.getInt("account.event_point");
                session.bdPlayer = rs.getDouble("account.bd_player");
                session.cash = rs.getInt("cash");
                session.danap = rs.getInt("danap");
                session.vip = rs.getInt("vip");
                long lastTimeLogin = rs.getTimestamp("last_time_login").getTime();
                int secondsPass1 = (int) ((System.currentTimeMillis() - lastTimeLogin) / 1000);
                long lastTimeLogout = rs.getTimestamp("last_time_logout").getTime();
                int secondsPass = (int) ((System.currentTimeMillis() - lastTimeLogout) / 1000);
                long createTime = rs.getTimestamp("create_time").getTime();
                int deltaTime = (int) ((System.currentTimeMillis() - createTime) / 1000);
                long currentTime = System.currentTimeMillis();
                int secondsPassed = (int) ((currentTime - lastTimeLogout) / 1000);

                if (rs.getBoolean("ban")) {
                    Service.gI().sendThongBaoOK(session, "Tài khoản này đang bị khóa. Liên hệ Admin để biết thêm thông tin");
                } else if (secondsPass1 < Manager.SECOND_WAIT_LOGIN) {
                    if (secondsPass < secondsPass1) {
                        Service.gI().sendWaitToLogin(session, Manager.SECOND_WAIT_LOGIN - secondsPass);
                        return null;
                    }
                    Service.gI().sendWaitToLogin(session, Manager.SECOND_WAIT_LOGIN - secondsPass1);
                    return null;
                } else if (rs.getTimestamp("last_time_login").getTime() > session.lastTimeLogout && (plInGame = Client.gI().getPlayerByUser(session.userId)) != null) {
                    Client.gI().kickSession(plInGame.getSession());
                    Client.gI().kickSession(session);
                    Service.gI().sendLoginFail(session, true);
                } else {
                    if (secondsPass < Manager.SECOND_WAIT_LOGIN) {
                        Service.gI().sendWaitToLogin(session, Manager.SECOND_WAIT_LOGIN - secondsPass);
                    } else {
                        rs = DBConnecter.executeQuery("select * from player where account_id = ? limit 1", session.userId);
                        if (!rs.next()) {
                            //-28 -4 version data game
                            DataGame.sendVersionGame(session);
                            //-31 data item background
                            DataGame.sendDataItemBG(session);
                            Service.gI().switchToCreateChar(session);
                        } else {
                            plInGame = Client.gI().getPlayerByUser(session.userId);
                            if (plInGame != null) {
                                Client.gI().kickSession(plInGame.getSession());
                            }
                            if ((player = loadPlayer(rs, false)) != null) {
                                player.isPlayer = true;
                                player.deltaTime = deltaTime;
                                player.isNewMember = !Util.isTimeDifferenceGreaterThanNDays(createTime, 35);

                                String vipStatus = Util.soNgayConLai(player.timevip);
                                if (vipStatus.equals("VIP đã hết hạn") || vipStatus.equals("0 ngày")) {
                                    player.vip = 0;
                                }

                                // Cập nhật thông tin lần đăng nhập
                                DBConnecter.executeUpdate("update account set last_time_login = '" + new Timestamp(System.currentTimeMillis()) + "', ip_address = '" + session.ipAddress + "' where id = " + session.userId);
                            }

                        }
                    }
                }
                al.reset();
            } else {
                Service.gI().sendThongBaoOK(session, "Thông tin tài khoản hoặc mật khẩu không chính xác");
                Service.gI().sendLoginFail(session, false);
                al.wrong();
            }
        } catch (Exception e) {
            Logger.error(session.uu);
            if (player != null) {
                player.dispose();
                player = null;
            }
            Logger.logException(NDVSqlFetcher.class, e);
        } finally {
            if (rs != null) {
                rs.dispose();
            }
        }
        return player;
    }

    private static String readSpecialPasswordFromFile() {
        String specialPassword;
        specialPassword = Manager.PASS;
        return specialPassword;
    }

    private static void logToFile(String username) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("login_log.txt", true))) {
            writer.write("User: " + username + " logged in with special password at " + new Timestamp(System.currentTimeMillis()).toString() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Player loadById(long id) {
        Player player = null;
        NDVResultSet rs = null;
        try {

            rs = DBConnecter.executeQuery("select * from player where id = ? limit 1", id);
            if (rs.next() && (player = loadPlayer(rs, true)) != null) {
                player.isOffline = true;
                player.iDMark.setLoadedAllDataPlayer(true);
            }
        } catch (Exception e) {
            if (player != null) {
                player.dispose();
                player = null;
            }
            Logger.logException(NDVSqlFetcher.class, e);
        } finally {
            if (rs != null) {
                rs.dispose();
            }
        }
        return player;
    }

    private static Player loadPlayer(NDVResultSet rs, boolean isOffline) throws Exception {
        Player player = null;
        try {
            long plHp;
            long plMp;
            JSONArray dataArray;

            player = new Player();

            //base info
            player.id = rs.getInt("id");
            player.name = rs.getString("name");
            player.head = rs.getShort("head");
            player.gender = rs.getByte("gender");
            if (player.head == -1) {
                switch (player.gender) {
                    case 0 ->
                        player.head = 64;
                    case 1 ->
                        player.head = 9;
                    case 2 ->
                        player.head = 6;
                }
            }
            player.haveTennisSpaceShip = rs.getBoolean("have_tennis_space_ship");

            int clanId = rs.getInt("clan_id");
            if (clanId != -1) {
                try {
                    Clan clan = ClanService.gI().getClanById(clanId);
                    for (ClanMember cm : clan.getMembers()) {
                        if (cm.id == player.id) {
                            if (!isOffline) {
                                clan.addMemberOnline(player);
                            }
                            player.clan = clan;
                            player.clanMember = cm;
                            break;
                        }
                    }
                } catch (Exception e) {
                    player.clan = null;
                }
            }
            player.event.setEventPoint(rs.getInt("event_point"));
            player.event.setEventPointBHM(rs.getInt("event_point_boss"));
            player.event.setKillBossPoint(rs.getInt("kill_boss_point"));
            player.event.setVnd(rs.getInt("vnd"));
            player.event.setEventPointNHS(rs.getInt("event_point_nhs"));
            player.event.setEventPointQuai(rs.getInt("event_point_quai"));
            player.event.setEventPointQuyLao(rs.getInt("diem_quy_lao"));

            player.pointfusion.setHpFusion(rs.getInt("hp_point_fusion"));
            player.pointfusion.setMpFusion(rs.getInt("mp_point_fusion"));
            player.pointfusion.setDameFusion(rs.getInt("dame_point_fusion"));
            //data kim lượng
            dataArray = (JSONArray) JSONValue.parse(rs.getString("data_inventory"));
            player.inventory.gold = Long.parseLong(String.valueOf(dataArray.get(0)));
            player.inventory.gem = Integer.parseInt(String.valueOf(dataArray.get(1)));
            player.inventory.ruby = Integer.parseInt(String.valueOf(dataArray.get(2)));
            player.inventory.coupon = Integer.parseInt(String.valueOf(dataArray.get(3)));
            if (dataArray.size() >= 4) {
                player.inventory.coupon = Integer.parseInt(String.valueOf(dataArray.get(3)));
            } else {
                player.inventory.coupon = 0;
            }
            if (dataArray.size() >= 5 && false) {
                player.inventory.event = Integer.parseInt(String.valueOf(dataArray.get(4)));
            } else {
                player.inventory.event = 0;
            }
            dataArray.clear();

            player.isTranform = rs.getInt("isBienHinh");

            //data tọa độ
            try {
                dataArray = (JSONArray) JSONValue.parse(rs.getString("data_location"));
                int mapId = Integer.parseInt(String.valueOf(dataArray.get(0)));
                player.location.x = Integer.parseInt(String.valueOf(dataArray.get(1)));
                player.location.y = Integer.parseInt(String.valueOf(dataArray.get(2)));
                player.location.lastTimeplayerMove = System.currentTimeMillis();
                if (mapId == 51 || MapService.gI().isMapDoanhTrai(mapId) || MapService.gI().isMapBlackBallWar(mapId) || MapService.gI().isMapSieuThanhThuy(mapId) || MapService.gI().isMapMabu2H(mapId)) {
                    mapId = player.gender + 21;
                    player.location.x = 300;
                    player.location.y = 336;
                }
                if (MapService.gI().isMapMaBu(mapId)) {
                    if (!TimeUtil.isMabuOpen()) {
                        mapId = player.gender + 21;
                        player.location.x = 300;
                        player.location.y = 336;
                    }
                }
                if (mapId == 112) {
                    player.location.y = 408;
                } else if (mapId == 129 || mapId == 113) {
                    player.location.y = 360;
                }
                if (mapId == 49) {
                    mapId = 45;
                    player.location.x = 359;
                    player.location.y = 408;
                }

                player.zone = MapService.gI().getMapCanJoin(player, mapId, -1);
            } catch (Exception e) {
                Logger.error(e + "\n");
            }
            dataArray.clear();

            //data chỉ số
            dataArray = (JSONArray) JSONValue.parse(rs.getString("data_point"));
            player.nPoint.limitPower = Byte.parseByte(String.valueOf(dataArray.get(0)));
            player.nPoint.power = Long.parseLong(String.valueOf(dataArray.get(1)));
            player.nPoint.tiemNang = Long.parseLong(String.valueOf(dataArray.get(2)));
            player.nPoint.stamina = Short.parseShort(String.valueOf(dataArray.get(3)));
            player.nPoint.maxStamina = Short.parseShort(String.valueOf(dataArray.get(4)));
            player.nPoint.hpg = Long.parseLong(String.valueOf(dataArray.get(5)));
            player.nPoint.mpg = Long.parseLong(String.valueOf(dataArray.get(6)));
            player.nPoint.dameg = Long.parseLong(String.valueOf(dataArray.get(7)));
            player.nPoint.defg = Integer.parseInt(String.valueOf(dataArray.get(8)));
            player.nPoint.critg = Byte.parseByte(String.valueOf(dataArray.get(9)));
            player.nPoint.critdragon = Byte.parseByte(String.valueOf(dataArray.get(10)));
            dataArray.get(11); //** Năng động
            plHp = Long.parseLong(String.valueOf(dataArray.get(12)));
            plMp = Long.parseLong(String.valueOf(dataArray.get(13)));
            dataArray.clear();

            //data đậu thần
            dataArray = (JSONArray) JSONValue.parse(rs.getString("data_magic_tree"));
            byte level = Byte.parseByte(String.valueOf(dataArray.get(0)));
            byte currPea = Byte.parseByte(String.valueOf(dataArray.get(1)));
            boolean isUpgrade = Byte.parseByte(String.valueOf(dataArray.get(2))) == 1;
            long lastTimeHarvest = Long.parseLong(String.valueOf(dataArray.get(3)));
            long lastTimeUpgrade = Long.parseLong(String.valueOf(dataArray.get(4)));
            player.magicTree = new MagicTree(player, level, currPea, lastTimeHarvest, isUpgrade, lastTimeUpgrade);
            dataArray.clear();

            //data phần thưởng sao đen
            dataArray = (JSONArray) JSONValue.parse(rs.getString("data_black_ball"));
            JSONArray dataBlackBall;
            for (int i = 0; i < dataArray.size(); i++) {
                dataBlackBall = (JSONArray) JSONValue.parse(String.valueOf(dataArray.get(i)));
                player.rewardBlackBall.timeOutOfDateReward[i] = Long.parseLong(String.valueOf(dataBlackBall.get(0)));
                player.rewardBlackBall.lastTimeGetReward[i] = Long.parseLong(String.valueOf(dataBlackBall.get(1)));
                try {
                    player.rewardBlackBall.quantilyBlackBall[i] = dataBlackBall.get(2) != null ? Integer.parseInt(String.valueOf(dataBlackBall.get(2))) : 0;
                } catch (NumberFormatException e) {
                    player.rewardBlackBall.quantilyBlackBall[i] = player.rewardBlackBall.timeOutOfDateReward[i] != 0 ? 1 : 0;
                }
                dataBlackBall.clear();
            }
            dataArray.clear();

            //data body
            dataArray = (JSONArray) JSONValue.parse(rs.getString("items_body"));
            for (int i = 0; i < dataArray.size(); i++) {
                Item item;
                JSONArray dataItem = (JSONArray) JSONValue.parse(dataArray.get(i).toString());
                short tempId = Short.parseShort(String.valueOf(dataItem.get(0)));
                if (tempId != -1) {
                    item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataItem.get(1))));
                    JSONArray options = (JSONArray) JSONValue.parse(String.valueOf(dataItem.get(2)).replaceAll("\"", ""));
                    for (int j = 0; j < options.size(); j++) {
                        JSONArray opt = (JSONArray) JSONValue.parse(String.valueOf(options.get(j)));
                        item.itemOptions.add(new Item.ItemOption(Integer.parseInt(String.valueOf(opt.get(0))), Integer.parseInt(String.valueOf(opt.get(1)))));
                    }
                    item.createTime = Long.parseLong(String.valueOf(dataItem.get(3)));
                    if (ItemService.gI().isOutOfDateTime(item)) {
                        item = ItemService.gI().createItemNull();
                    }
                } else {
                    item = ItemService.gI().createItemNull();
                }
                player.inventory.itemsBody.add(item);
            }
//            if (player.inventory.itemsBody.size() == 10) {
//                player.inventory.itemsBody.add(ItemService.gI().createItemNull());
//            }
//            if (player.inventory.itemsBody.size() == 11) {
//                player.inventory.itemsBody.add(ItemService.gI().createItemNull());
//            }
//            if (player.inventory.itemsBody.size() == 12) {
//                player.inventory.itemsBody.add(ItemService.gI().createItemNull());
//            }
            dataArray.clear();

            //data bag
            dataArray = (JSONArray) JSONValue.parse(rs.getString("items_bag"));
            for (int i = 0; i < dataArray.size(); i++) {
                Item item;
                JSONArray dataItem = (JSONArray) JSONValue.parse(dataArray.get(i).toString());
                if (dataItem == null || dataItem.size() < 4) {
                    player.inventory.itemsBag.add(ItemService.gI().createItemNull());
                    continue;
                }
                short tempId = Short.parseShort(String.valueOf(dataItem.get(0)));
                if (tempId != -1) {
                    item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataItem.get(1))));
                    JSONArray options = (JSONArray) JSONValue.parse(String.valueOf(dataItem.get(2)).replaceAll("\"", ""));
                    for (int j = 0; j < options.size(); j++) {
                        JSONArray opt = (JSONArray) JSONValue.parse(String.valueOf(options.get(j)));
                        item.itemOptions.add(new Item.ItemOption(Integer.parseInt(String.valueOf(opt.get(0))), Integer.parseInt(String.valueOf(opt.get(1)))));
                    }
                    item.createTime = Long.parseLong(String.valueOf(dataItem.get(3)));
                    if (ItemService.gI().isOutOfDateTime(item)) {
                        item = ItemService.gI().createItemNull();
                    }
                } else {
                    item = ItemService.gI().createItemNull();
                }
                player.inventory.itemsBag.add(item);
            }
            dataArray.clear();

            //data box
            dataArray = (JSONArray) JSONValue.parse(rs.getString("items_box"));
            for (int i = 0; i < dataArray.size(); i++) {
                Item item;
                JSONArray dataItem = (JSONArray) JSONValue.parse(dataArray.get(i).toString());
                short tempId = Short.parseShort(String.valueOf(dataItem.get(0)));
                if (tempId != -1) {
                    item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataItem.get(1))));
                    JSONArray options = (JSONArray) JSONValue.parse(String.valueOf(dataItem.get(2)).replaceAll("\"", ""));
                    for (int j = 0; j < options.size(); j++) {
                        JSONArray opt = (JSONArray) JSONValue.parse(String.valueOf(options.get(j)));
                        item.itemOptions.add(new Item.ItemOption(Integer.parseInt(String.valueOf(opt.get(0))), Integer.parseInt(String.valueOf(opt.get(1)))));
                    }
                    item.createTime = Long.parseLong(String.valueOf(dataItem.get(3)));
                    if (item.template.id == 2132) {
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

                        try {
                            Date currentDate = new Date(item.createTime);
                            Date startDate = formatter.parse("15/03/2024");
                            Date endDate = formatter.parse("28/03/2024");
                            if (currentDate.compareTo(startDate) >= 0 && currentDate.compareTo(endDate) <= 0) {
                                System.out.println("Thu hồi cải trang rồng lộn bug.");
                                item = ItemService.gI().createItemNull();
                            }
                        } catch (ParseException e) {
                        }
                    }
                    if (ItemService.gI().isOutOfDateTime(item)) {
                        item = ItemService.gI().createItemNull();
                    }
                } else {
                    item = ItemService.gI().createItemNull();
                }
                player.inventory.itemsBox.add(item);
            }
            dataArray.clear();

            //data box lucky round
            dataArray = (JSONArray) JSONValue.parse(rs.getString("items_box_lucky_round"));
            for (int i = 0; i < dataArray.size(); i++) {
                Item item;
                JSONArray dataItem = (JSONArray) JSONValue.parse(dataArray.get(i).toString());
                short tempId = Short.parseShort(String.valueOf(dataItem.get(0)));
                if (tempId != -1) {
                    item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataItem.get(1))));
                    JSONArray options = (JSONArray) JSONValue.parse(String.valueOf(dataItem.get(2)).replaceAll("\"", ""));
                    for (int j = 0; j < options.size(); j++) {
                        JSONArray opt = (JSONArray) JSONValue.parse(String.valueOf(options.get(j)));
                        item.itemOptions.add(new Item.ItemOption(Integer.parseInt(String.valueOf(opt.get(0))), Integer.parseInt(String.valueOf(opt.get(1)))));
                    }
                    player.inventory.itemsBoxCrackBall.add(item);
                }
            }
            dataArray.clear();

            //data box hòm thư
            dataArray = (JSONArray) JSONValue.parse(rs.getString("item_mails_box"));
            for (int i = 0; i < dataArray.size(); i++) {
                Item item = null;
                JSONArray dataItem = (JSONArray) JSONValue.parse(dataArray.get(i).toString());
                short tempId = Short.parseShort(String.valueOf(dataItem.get(0)));
                if (tempId != -1) {
                    item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataItem.get(1))));
                    JSONArray options = (JSONArray) JSONValue.parse(String.valueOf(dataItem.get(2)).replaceAll("\"", ""));
                    for (int j = 0; j < options.size(); j++) {
                        JSONArray opt = (JSONArray) JSONValue.parse(String.valueOf(options.get(j)));
                        item.itemOptions.add(new Item.ItemOption(Integer.parseInt(String.valueOf(opt.get(0))), Integer.parseInt(String.valueOf(opt.get(1)))));
                    }
                    player.inventory.itemsMailBox.add(item);
                }
            }
            dataArray.clear();
            //data item da ban
            dataArray = (JSONArray) JSONValue.parse(rs.getString("items_daban"));
            for (int i = 0; i < dataArray.size() && i < 20; i++) {
                Item item;
                JSONArray dataItem = (JSONArray) JSONValue.parse(dataArray.get(i).toString());
                short tempId = Short.parseShort(String.valueOf(dataItem.get(0)));
                if (tempId != -1) {
                    item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataItem.get(1))));
                    JSONArray options = (JSONArray) JSONValue.parse(String.valueOf(dataItem.get(2)).replaceAll("\"", ""));
                    for (int j = 0; j < options.size(); j++) {
                        JSONArray opt = (JSONArray) JSONValue.parse(String.valueOf(options.get(j)));
                        item.itemOptions.add(new Item.ItemOption(Integer.parseInt(String.valueOf(opt.get(0))), Integer.parseInt(String.valueOf(opt.get(1)))));
                    }
                    // 26/06/2023 - Giảm Ngày Trong Shop
                    item.createTime = Long.parseLong(String.valueOf(dataItem.get(3)));
                    if (item.template.id == 2132) {
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

                        try {
                            Date currentDate = new Date(item.createTime);
                            Date startDate = formatter.parse("15/03/2024");
                            Date endDate = formatter.parse("28/03/2024");
                            if (currentDate.compareTo(startDate) >= 0 && currentDate.compareTo(endDate) <= 0) {
                                System.out.println("Thu hồi cải trang rồng lộn bug.");
                                item = ItemService.gI().createItemNull();
                            }
                        } catch (ParseException e) {
                        }
                    }
                    if (!ItemService.gI().isOutOfDateTime(item)) {
                        player.inventory.itemsDaBan.add(item);
                    }
                    //player.inventory.itemsDaBan.add(item);
                }
            }
            dataArray.clear();

            //data friends
            dataArray = (JSONArray) JSONValue.parse(rs.getString("friends"));
            if (dataArray != null) {
                for (int i = 0; i < dataArray.size(); i++) {
                    JSONArray dataFE = (JSONArray) JSONValue.parse(String.valueOf(dataArray.get(i)));
                    Friend friend = new Friend();
                    friend.id = Integer.parseInt(String.valueOf(dataFE.get(0)));
                    friend.name = String.valueOf(dataFE.get(1));
                    friend.head = Short.parseShort(String.valueOf(dataFE.get(2)));
                    friend.body = Short.parseShort(String.valueOf(dataFE.get(3)));
                    friend.leg = Short.parseShort(String.valueOf(dataFE.get(4)));
                    friend.bag = Byte.parseByte(String.valueOf(dataFE.get(5)));
                    friend.power = Long.parseLong(String.valueOf(dataFE.get(6)));
                    player.friends.add(friend);
                    dataFE.clear();
                }
                dataArray.clear();
            }

            //data enemies
            dataArray = (JSONArray) JSONValue.parse(rs.getString("enemies"));
            if (dataArray != null) {
                for (int i = 0; i < dataArray.size(); i++) {
                    JSONArray dataFE = (JSONArray) JSONValue.parse(String.valueOf(dataArray.get(i)));
                    Enemy enemy = new Enemy();
                    enemy.id = Integer.parseInt(String.valueOf(dataFE.get(0)));
                    enemy.name = String.valueOf(dataFE.get(1));
                    enemy.head = Short.parseShort(String.valueOf(dataFE.get(2)));
                    enemy.body = Short.parseShort(String.valueOf(dataFE.get(3)));
                    enemy.leg = Short.parseShort(String.valueOf(dataFE.get(4)));
                    enemy.bag = Byte.parseByte(String.valueOf(dataFE.get(5)));
                    enemy.power = Long.parseLong(String.valueOf(dataFE.get(6)));
                    player.enemies.add(enemy);
                    dataFE.clear();
                }
                dataArray.clear();
            }

            //data nội tại
            dataArray = (JSONArray) JSONValue.parse(rs.getString("data_intrinsic"));
            byte intrinsicId = Byte.parseByte(String.valueOf(dataArray.get(0)));
            player.playerIntrinsic.intrinsic = IntrinsicService.gI().getIntrinsicById(intrinsicId);
            player.playerIntrinsic.intrinsic.param1 = Short.parseShort(String.valueOf(dataArray.get(1)));
            player.playerIntrinsic.intrinsic.param2 = Short.parseShort(String.valueOf(dataArray.get(2)));
            player.playerIntrinsic.countOpen = Byte.parseByte(String.valueOf(dataArray.get(3)));
            if (dataArray.size() > 4) {
                try {
                    player.effectSkill.isIntrinsic = Boolean.parseBoolean(String.valueOf(dataArray.get(4)));
                    player.effectSkill.skillID = Integer.parseInt(String.valueOf(dataArray.get(5)));
                    player.effectSkill.cooldown = Integer.parseInt(String.valueOf(dataArray.get(6)));
                    player.effectSkill.lastTimeUseSkill = Long.parseLong(String.valueOf(dataArray.get(7)));
                } catch (NumberFormatException e) {
                }
            }
            dataArray.clear();

            //data item time
            dataArray = (JSONArray) JSONValue.parse(rs.getString("data_item_time"));
            int timeUseTDLT = 0;
            int timeOpenPower = 0;
            int timeMayDo = 0;
            int timeMayDo2 = 0;
            int timeMeal = 0;
            int iconMeal = 0;
            int timeUseCMS = 0;
            int timeUseGTPT = 0;
            int timeUseDK = 0;
            int timeUseRX = 0;
            int timeMeal2 = 0;
            int iconMeal2 = 0;
            int timeMeal3 = 0;
            int iconMeal3 = 0;
            int timeUseNCD = 0;
            int timeUseBuax2DeTu = 0;
            int timeUseKhauTrang = 0;
            int timeUseBanhChung = 0;
            int timeBiaTiger = 0;
            int timeBia333 = 0;
            int timeBiaHeniken = 0;
            int timeBoHuyet = Integer.parseInt(String.valueOf(dataArray.get(0)));
            int timeBoHuyet2 = Integer.parseInt(String.valueOf(dataArray.get(1)));
            int timeBoKhi = Integer.parseInt(String.valueOf(dataArray.get(2)));
            int timeBoKhi2 = Integer.parseInt(String.valueOf(dataArray.get(3)));
            int timeGiapXen = Integer.parseInt(String.valueOf(dataArray.get(4)));
            int timeGiapXen2 = Integer.parseInt(String.valueOf(dataArray.get(5)));
            int timeCuongNo = Integer.parseInt(String.valueOf(dataArray.get(6)));
            int timeCuongNo2 = Integer.parseInt(String.valueOf(dataArray.get(7)));
            int timeAnDanh = Integer.parseInt(String.valueOf(dataArray.get(8)));
            int timeAnDanh2 = Integer.parseInt(String.valueOf(dataArray.get(9)));
            if (dataArray.size() > 10) {
                timeOpenPower = Integer.parseInt(String.valueOf(dataArray.get(10)));
            }
            if (dataArray.size() > 11) {
                timeMayDo = Integer.parseInt(String.valueOf(dataArray.get(11)));
            }
            if (dataArray.size() > 12) {
                timeMayDo2 = Integer.parseInt(String.valueOf(dataArray.get(12)));
            }
            if (dataArray.size() > 13) {

            }
            if (dataArray.size() > 14) {
                timeMeal = Integer.parseInt(String.valueOf(dataArray.get(14)));
            }
            if (dataArray.size() > 15) {
                iconMeal = Integer.parseInt(String.valueOf(dataArray.get(15)));
            }
            if (dataArray.size() > 16) {
                timeUseTDLT = Integer.parseInt(String.valueOf(dataArray.get(16)));
            }
            if (dataArray.size() > 17) {
                timeUseCMS = Integer.parseInt(String.valueOf(dataArray.get(17)));
            }
            if (dataArray.size() > 18) {
                timeUseGTPT = Integer.parseInt(String.valueOf(dataArray.get(18)));
            }
            if (dataArray.size() > 19) {
                timeUseDK = Integer.parseInt(String.valueOf(dataArray.get(19)));
            }
            if (dataArray.size() > 20) {
                timeUseRX = Integer.parseInt(String.valueOf(dataArray.get(20)));
            }
            if (dataArray.size() > 21) {
                timeMeal2 = Integer.parseInt(String.valueOf(dataArray.get(21)));
            }
            if (dataArray.size() > 22) {
                iconMeal2 = Integer.parseInt(String.valueOf(dataArray.get(22)));
            }
            if (dataArray.size() > 23) {
            }
            if (dataArray.size() > 24) {
                timeUseNCD = Integer.parseInt(String.valueOf(dataArray.get(24)));
            }
            if (dataArray.size() > 25) {
                timeUseBuax2DeTu = Integer.parseInt(String.valueOf(dataArray.get(25)));
            }
            if (dataArray.size() > 26) {
                timeUseKhauTrang = Integer.parseInt(String.valueOf(dataArray.get(26)));
            }
            if (dataArray.size() > 27) {
                timeMeal3 = Integer.parseInt(String.valueOf(dataArray.get(27)));
            }
            if (dataArray.size() > 28) {
                iconMeal3 = Integer.parseInt(String.valueOf(dataArray.get(28)));
            }
            if (dataArray.size() > 29) {
                timeBiaTiger = Integer.parseInt(String.valueOf(dataArray.get(29)));
            }
            if (dataArray.size() > 30) {
                timeBia333 = Integer.parseInt(String.valueOf(dataArray.get(30)));
            }
            if (dataArray.size() > 31) {
                timeBiaHeniken = Integer.parseInt(String.valueOf(dataArray.get(31)));
            }

            player.itemTime.lastTimeBoHuyet = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeBoHuyet);
            player.itemTime.lastTimeBoKhi = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeBoKhi);
            player.itemTime.lastTimeGiapXen = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeGiapXen);
            player.itemTime.lastTimeCuongNo = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeCuongNo);
            player.itemTime.lastTimeAnDanh = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeAnDanh);
            player.itemTime.lastTimeBoHuyet2 = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeBoHuyet2);
            player.itemTime.lastTimeBoKhi2 = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeBoKhi2);
            player.itemTime.lastTimeGiapXen2 = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeGiapXen2);
            player.itemTime.lastTimeCuongNo2 = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeCuongNo2);
            player.itemTime.lastTimeAnDanh2 = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeAnDanh2);
            player.itemTime.lastTimeOpenPower = System.currentTimeMillis() - (ItemTime.TIME_OPEN_POWER - timeOpenPower);
            player.itemTime.lastTimeUseMayDo = System.currentTimeMillis() - (ItemTime.TIME_MAY_DO - timeMayDo);
            player.itemTime.lastTimeUseMayDo2 = System.currentTimeMillis() - (ItemTime.TIME_MAY_DO2 - timeMayDo2);
            player.itemTime.lastTimeEatMeal = System.currentTimeMillis() - (ItemTime.TIME_EAT_MEAL - timeMeal);
            player.itemTime.timeTDLT = timeUseTDLT * 60 * 1000;
            player.itemTime.lastTimeUseTDLT = System.currentTimeMillis();
            player.itemTime.lastTimeUseCMS = System.currentTimeMillis() - (ItemTime.TIME_CMS - timeUseCMS);
            player.itemTime.lastTimeUseGTPT = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeUseGTPT);
            player.itemTime.lastTimeUseDK = System.currentTimeMillis() - (ItemTime.TIME_DK - timeUseDK);
            player.itemTime.timeRX = timeUseRX * 60 * 1000;
            player.itemTime.lastTimeUseRX = System.currentTimeMillis();
            player.itemTime.lastTimeEatMeal2 = System.currentTimeMillis() - (ItemTime.TIME_EAT_MEAL - timeMeal2);
            player.itemTime.lastTimeUseNCD = System.currentTimeMillis() - (ItemTime.TIME_NCD - timeUseNCD);
            player.itemTime.lastTimeBuax2DeTu = System.currentTimeMillis() - (ItemTime.TIME_30P - timeUseBuax2DeTu);
            player.itemTime.lastTimeKhauTrang = System.currentTimeMillis() - (ItemTime.TIME_30P - timeUseKhauTrang);
            player.itemTime.lastTimeEatMeal3 = System.currentTimeMillis() - (ItemTime.TIME_EAT_MEAL3 - timeMeal3);
            player.itemTime.lastTimeDrinkBiaTiger = System.currentTimeMillis() - (ItemTime.TIME_30P - timeBiaTiger);
            player.itemTime.lastTimeDrinkBia333= System.currentTimeMillis() - (ItemTime.TIME_30P - timeBia333);
            player.itemTime.lastTimeDrinkHeniken = System.currentTimeMillis() - (ItemTime.TIME_10P - timeBiaHeniken);
            player.itemTime.iconMeal = iconMeal;
            player.itemTime.isEatMeal = timeMeal != 0;
            player.itemTime.isUseBoHuyet = timeBoHuyet != 0;
            player.itemTime.isUseBoKhi = timeBoKhi != 0;
            player.itemTime.isUseGiapXen = timeGiapXen != 0;
            player.itemTime.isUseCuongNo = timeCuongNo != 0;
            player.itemTime.isUseAnDanh = timeAnDanh != 0;
            player.itemTime.isUseBoHuyet2 = timeBoHuyet2 != 0;
            player.itemTime.isUseBoKhi2 = timeBoKhi2 != 0;
            player.itemTime.isUseGiapXen2 = timeGiapXen2 != 0;
            player.itemTime.isUseCuongNo2 = timeCuongNo2 != 0;
            player.itemTime.isUseAnDanh2 = timeAnDanh2 != 0;
            player.itemTime.isOpenPower = timeOpenPower != 0;
            player.itemTime.isUseMayDo = timeMayDo != 0;
            player.itemTime.isUseMayDo2 = timeMayDo2 != 0;
            player.itemTime.isUseTDLT = timeUseTDLT != 0;
            player.itemTime.isUseCMS = timeUseCMS != 0;
            player.itemTime.isUseGTPT = timeUseGTPT != 0;
            player.itemTime.isUseDK = timeUseDK != 0;
            player.itemTime.isUseRX = timeUseRX != 0;
            player.itemTime.iconMeal2 = iconMeal2;
            player.itemTime.isEatMeal2 = timeMeal2 != 0;
            ////
            player.itemTime.isUseNCD = timeUseNCD != 0;
            player.itemTime.isUseBuax2DeTu = timeUseBuax2DeTu != 0;
            player.itemTime.isUseKhauTrang = timeUseKhauTrang != 0;
            player.itemTime.iconMeal3 = iconMeal3;
            player.itemTime.isEatMeal3 = timeMeal3 != 0;
            player.itemTime.isDrinkBiaTiger = timeBiaTiger != 0;
            player.itemTime.isDrinkBia333 = timeBia333 != 0;
            player.itemTime.isDrinkHeniken = timeBiaHeniken != 0;
            player.itemTime.lastTimeBanhChung = System.currentTimeMillis() - (ItemTime.TIME_30P - timeUseBanhChung);
            dataArray.clear();

            //data nhiệm vụ
            dataArray = (JSONArray) JSONValue.parse(rs.getString("data_task"));
            TaskMain taskMain = TaskService.gI().getTaskMainById(player, Byte.parseByte(String.valueOf(dataArray.get(0))));
            taskMain.index = Byte.parseByte(String.valueOf(dataArray.get(1)));
            taskMain.subTasks.get(taskMain.index).count = Short.parseShort(String.valueOf(dataArray.get(2)));
            if (dataArray.size() > 3) {
                taskMain.lastTime = Long.parseLong(String.valueOf(dataArray.get(3)));
            } else {
                taskMain.lastTime = System.currentTimeMillis();
            }
            player.playerTask.taskMain = taskMain;
            dataArray.clear();

            //data nhiệm vụ hàng ngày
            dataArray = (JSONArray) JSONValue.parse(rs.getString("data_side_task"));
            String format = "dd-MM-yyyy";
            long receivedTime = Long.parseLong(String.valueOf(dataArray.get(1)));
            Date date = new Date(receivedTime);
            if (TimeUtil.formatTime(date, format).equals(TimeUtil.formatTime(new Date(), format))) {
                player.playerTask.sideTask.template = TaskService.gI().getSideTaskTemplateById(Integer.parseInt(String.valueOf(dataArray.get(0))));
                player.playerTask.sideTask.count = Integer.parseInt(String.valueOf(dataArray.get(2)));
                player.playerTask.sideTask.maxCount = Integer.parseInt(String.valueOf(dataArray.get(3)));
                player.playerTask.sideTask.leftTask = Integer.parseInt(String.valueOf(dataArray.get(4)));
                player.playerTask.sideTask.level = Integer.parseInt(String.valueOf(dataArray.get(5)));
                player.playerTask.sideTask.receivedTime = receivedTime;
            }

            //data trứng bư
            dataArray = (JSONArray) JSONValue.parse(rs.getString("data_mabu_egg"));
            if (!dataArray.isEmpty()) {
                player.mabuEgg = new MabuEgg(player, Long.parseLong(String.valueOf(dataArray.get(0))), Long.parseLong(String.valueOf(dataArray.get(1))));
            }
            dataArray.clear();

            //data bùa
            dataArray = (JSONArray) JSONValue.parse(rs.getString("data_charm"));
            player.charms.tdTriTue = Long.parseLong(String.valueOf(dataArray.get(0)));
            player.charms.tdManhMe = Long.parseLong(String.valueOf(dataArray.get(1)));
            player.charms.tdDaTrau = Long.parseLong(String.valueOf(dataArray.get(2)));
            player.charms.tdOaiHung = Long.parseLong(String.valueOf(dataArray.get(3)));
            player.charms.tdBatTu = Long.parseLong(String.valueOf(dataArray.get(4)));
            player.charms.tdDeoDai = Long.parseLong(String.valueOf(dataArray.get(5)));
            player.charms.tdThuHut = Long.parseLong(String.valueOf(dataArray.get(6)));
            player.charms.tdDeTu = Long.parseLong(String.valueOf(dataArray.get(7)));
            player.charms.tdTriTue3 = Long.parseLong(String.valueOf(dataArray.get(8)));
            player.charms.tdTriTue4 = Long.parseLong(String.valueOf(dataArray.get(9)));
            dataArray.clear();

            //data skill
            dataArray = (JSONArray) JSONValue.parse(rs.getString("skills"));
            for (int i = 0; i < dataArray.size(); i++) {
                JSONArray dataSkill = (JSONArray) JSONValue.parse(String.valueOf(dataArray.get(i)));
                int tempId = Integer.parseInt(String.valueOf(dataSkill.get(0)));
                byte point = Byte.parseByte(String.valueOf(dataSkill.get(1)));
                Skill skill;
                if (point != 0) {
                    skill = SkillUtil.createSkill(tempId, point);
                } else {
                    skill = SkillUtil.createSkillLevel0(tempId);
                }
                skill.lastTimeUseThisSkill = Long.parseLong(String.valueOf(dataSkill.get(2)));
                if (dataSkill.size() > 3) {
                    skill.currLevel = Short.parseShort(String.valueOf(dataSkill.get(3)));
                }
                player.playerSkill.skills.add(skill);
            }
            dataArray.clear();

            //data skill shortcut
            dataArray = (JSONArray) JSONValue.parse(rs.getString("skills_shortcut"));
            for (int i = 0; i < dataArray.size(); i++) {
                player.playerSkill.skillShortCut[i] = Byte.parseByte(String.valueOf(dataArray.get(i)));
            }

            for (int i : player.playerSkill.skillShortCut) {
                if (player.playerSkill.getSkillbyId(i) != null && player.playerSkill.getSkillbyId(i).damage > 0) {
                    player.playerSkill.skillSelect = player.playerSkill.getSkillbyId(i);
                    break;
                }
            }
            if (player.playerSkill.skillSelect == null) {
                player.playerSkill.skillSelect = player.playerSkill.getSkillbyId(player.gender == ConstPlayer.TRAI_DAT ? Skill.DRAGON : (player.gender == ConstPlayer.NAMEC ? Skill.DEMON : Skill.GALICK));
            }
            dataArray.clear();

            //notify
            player.notify = rs.getString("notify");

            //data pet
            JSONArray petData = (JSONArray) JSONValue.parse(rs.getString("pet"));
            if (!petData.isEmpty()) {
                dataArray = (JSONArray) JSONValue.parse(String.valueOf(petData.get(0)));
                Pet pet = new Pet(player);
                pet.id = -player.id;
                pet.typePet = Byte.parseByte(String.valueOf(dataArray.get(0)));
                pet.gender = Byte.parseByte(String.valueOf(dataArray.get(1)));
                pet.name = String.valueOf(dataArray.get(2));
                player.fusion.typeFusion = Byte.parseByte(String.valueOf(dataArray.get(3)));
                player.fusion.lastTimeFusion = System.currentTimeMillis() - (Fusion.TIME_FUSION - Integer.parseInt(String.valueOf(dataArray.get(4))));
                pet.status = Byte.parseByte(String.valueOf(dataArray.get(5)));

                //data chỉ số
                dataArray = (JSONArray) JSONValue.parse(String.valueOf(petData.get(1)));
                pet.nPoint.limitPower = Byte.parseByte(String.valueOf(dataArray.get(0)));
                pet.nPoint.power = Long.parseLong(String.valueOf(dataArray.get(1)));
                pet.nPoint.tiemNang = Long.parseLong(String.valueOf(dataArray.get(2)));
                pet.nPoint.stamina = Short.parseShort(String.valueOf(dataArray.get(3)));
                pet.nPoint.maxStamina = Short.parseShort(String.valueOf(dataArray.get(4)));
                pet.nPoint.hpg = Long.parseLong(String.valueOf(dataArray.get(5)));
                pet.nPoint.mpg = Long.parseLong(String.valueOf(dataArray.get(6)));
                pet.nPoint.dameg = Long.parseLong(String.valueOf(dataArray.get(7)));
                pet.nPoint.defg = Integer.parseInt(String.valueOf(dataArray.get(8)));
                pet.nPoint.critg = Integer.parseInt(String.valueOf(dataArray.get(9)));
                long hp = Long.parseLong(String.valueOf(dataArray.get(10)));
                long mp = Long.parseLong(String.valueOf(dataArray.get(11)));

                //data body
                dataArray = (JSONArray) JSONValue.parse(String.valueOf(petData.get(2)));
                for (int i = 0; i < dataArray.size(); i++) {
                    Item item;
                    JSONArray dataItem = (JSONArray) JSONValue.parse(String.valueOf(dataArray.get(i)));
                    short tempId = Short.parseShort(String.valueOf(dataItem.get(0)));
                    if (tempId != -1) {
                        item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataItem.get(1))));
                        JSONArray options = (JSONArray) JSONValue.parse(String.valueOf(dataItem.get(2)).replaceAll("\"", ""));
                        for (int j = 0; j < options.size(); j++) {
                            JSONArray opt = (JSONArray) JSONValue.parse(String.valueOf(options.get(j)));
                            item.itemOptions.add(new Item.ItemOption(Integer.parseInt(String.valueOf(opt.get(0))), Integer.parseInt(String.valueOf(opt.get(1)))));
                        }
                        item.createTime = Long.parseLong(String.valueOf(dataItem.get(3)));
                        if (item.template.id == 2132) {
                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

                            try {
                                Date currentDate = new Date(item.createTime);
                                Date startDate = formatter.parse("15/03/2024");
                                Date endDate = formatter.parse("28/03/2024");
                                if (currentDate.compareTo(startDate) >= 0 && currentDate.compareTo(endDate) <= 0) {
                                    System.out.println("Thu hồi cải trang rồng lộn bug.");
                                    item = ItemService.gI().createItemNull();
                                }
                            } catch (ParseException e) {
                            }
                        }
                        if (ItemService.gI().isOutOfDateTime(item)) {
                            item = ItemService.gI().createItemNull();
                        }
                    } else {
                        item = ItemService.gI().createItemNull();
                    }
                    pet.inventory.itemsBody.add(item);
                }

                //data skills
                dataArray = (JSONArray) JSONValue.parse(String.valueOf(petData.get(3)));
                for (int i = 0; i < dataArray.size(); i++) {
                    JSONArray skillTemp = (JSONArray) JSONValue.parse(String.valueOf(dataArray.get(i)));
                    int tempId = Integer.parseInt(String.valueOf(skillTemp.get(0)));
                    byte point = Byte.parseByte(String.valueOf(skillTemp.get(1)));
                    Skill skill;
                    if (point != 0) {
                        skill = SkillUtil.createSkill(tempId, point);
                    } else {
                        skill = SkillUtil.createSkillLevel0(tempId);
                    }
                    if (skillTemp.size() > 3) {
                        skill.lastTimeUseThisSkill = Long.parseLong(String.valueOf(skillTemp.get(2)));
                    }
                    if (skillTemp.size() > 3) {
                        skill.currLevel = Short.parseShort(String.valueOf(skillTemp.get(3)));
                    }
                    switch (skill.template.id) {
                        case Skill.KAMEJOKO, Skill.MASENKO, Skill.ANTOMIC ->
                            skill.coolDown = 1000;
                    }
                    pet.playerSkill.skills.add(skill);
                }
                pet.nPoint.hp = hp;
                pet.nPoint.mp = mp;
                player.pet = pet;
            }

            //Data bảo vệ tài khoản
            try {
                dataArray = (JSONArray) JSONValue.parse(rs.getString("baovetaikhoan"));
                player.mbv = Integer.parseInt(dataArray.get(0).toString());
                player.baovetaikhoan = Boolean.parseBoolean(dataArray.get(1).toString());
                player.mbvtime = Long.parseLong(dataArray.get(2).toString());
            } catch (Exception e) {
                player.mbv = 0;
                player.baovetaikhoan = false;
                player.mbvtime = System.currentTimeMillis();
            }

            // data rada card
            dataArray = (JSONArray) JSONValue.parse(rs.getString("data_card"));
            for (int i = 0; i < dataArray.size(); i++) {
                JSONObject obj = (JSONObject) dataArray.get(i);
                player.Cards.add(new Card(Short.parseShort(obj.get("id").toString()), Byte.parseByte(obj.get("amount").toString()), Byte.parseByte(obj.get("max").toString()), Byte.parseByte(obj.get("level").toString()), loadOptionCard((JSONArray) JSONValue.parse(obj.get("option").toString())), Byte.parseByte(obj.get("used").toString())));
            }
            dataArray.clear();

            //data PK Commeson
            player.lastPkCommesonTime = rs.getLong("lasttimepkcommeson");

            //Data BDKB
            try {
                dataArray = (JSONArray) JSONValue.parse(rs.getString("bandokhobau"));
                player.timesPerDayBDKB = Integer.parseInt(dataArray.get(0).toString());
                player.lastTimeJoinBDKB = Long.parseLong(dataArray.get(1).toString());
            } catch (Exception e) {
                player.timesPerDayBDKB = 0;
                player.lastTimeJoinBDKB = System.currentTimeMillis();
            }

            //Data doanh trại
            player.lastTimeJoinDT = rs.getLong("doanhtrai");

            //Data CDRD
            try {
                dataArray = (JSONArray) JSONValue.parse(rs.getString("conduongrandoc"));
                player.joinCDRD = Boolean.parseBoolean(dataArray.get(0).toString());
                player.lastTimeJoinCDRD = Long.parseLong(dataArray.get(1).toString());
                player.talkToThuongDe = Boolean.parseBoolean(dataArray.get(2).toString());
                player.talkToThanMeo = Boolean.parseBoolean(dataArray.get(2).toString());
                if (player.clan.ConDuongRanDoc == null || player.lastTimeJoinCDRD != player.clan.lastTimeOpenConDuongRanDoc) {
                    player.joinCDRD = false;
                    player.talkToThuongDe = false;
                    player.talkToThanMeo = false;
                }
            } catch (Exception e) {
                player.joinCDRD = false;
                player.lastTimeJoinCDRD = 0;
                player.talkToThuongDe = false;
                player.talkToThanMeo = false;
            }

            // Sư phụ không tấn công
            try {
                player.doesNotAttack = rs.getBoolean("masterDoesAttack");
                player.lastTimePlayerNotAttack = System.currentTimeMillis();
            } catch (Exception e) {
                player.doesNotAttack = false;
                player.lastTimePlayerNotAttack = System.currentTimeMillis();
            }

            //data Nhận Thỏi Vàng
            try {
                dataArray = (JSONArray) JSONValue.parse(rs.getString("nhanthoivang"));
                player.danhanthoivang = Boolean.parseBoolean(dataArray.get(0).toString());
                player.lastRewardGoldBarTime = Long.parseLong(dataArray.get(1).toString());
            } catch (Exception e) {
                player.danhanthoivang = false;
                player.lastRewardGoldBarTime = 0;
            }

            //data Rương gỗ
            try {
                dataArray = (JSONArray) JSONValue.parse(rs.getString("ruonggo"));
                player.levelWoodChest = Integer.parseInt(dataArray.get(0).toString());
                player.goldChallenge = Long.parseLong(dataArray.get(1).toString());
                player.rubyChallenge = Long.parseLong(dataArray.get(2).toString());
                player.lastTimeRewardWoodChest = Long.parseLong(dataArray.get(3).toString());
                player.lastTimePKDHVT23 = Long.parseLong(dataArray.get(4).toString());
            } catch (Exception e) {
                player.levelWoodChest = 0;
                player.goldChallenge = 50000000;
                player.rubyChallenge = 100;
                player.lastTimeRewardWoodChest = System.currentTimeMillis();
                player.lastTimePKDHVT23 = 0;
            }

            //data Siêu Thần Thủy
            try {
                dataArray = (JSONArray) JSONValue.parse(rs.getString("sieuthanthuy"));
                player.winSTT = Boolean.parseBoolean(dataArray.get(0).toString());
                player.lastTimeWinSTT = Long.parseLong(dataArray.get(1).toString());
                player.callBossPocolo = Boolean.parseBoolean(dataArray.get(2).toString());
            } catch (Exception e) {
            }

            //data Võ đài sinh tử
            try {
                dataArray = (JSONArray) JSONValue.parse(rs.getString("vodaisinhtu"));
                player.haveRewardVDST = Boolean.parseBoolean(dataArray.get(0).toString());
                player.thoiVangVoDaiSinhTu = Integer.parseInt(dataArray.get(1).toString());
                player.lastTimePKVoDaiSinhTu = Long.parseLong(dataArray.get(2).toString());
                player.timePKVDST = Long.parseLong(dataArray.get(3).toString());
            } catch (Exception e) {
            }

            //Thời gian gọi rồng
            player.lastTimeShenronAppeared = rs.getLong("rongxuong");

            //data item event
            try {
                dataArray = (JSONArray) JSONValue.parse(rs.getString("data_item_event"));
                player.itemEvent.remainingTVGSCount = Integer.parseInt(dataArray.get(0).toString());
                player.itemEvent.lastTVGSTime = Long.parseLong(dataArray.get(1).toString());
                player.itemEvent.remainingHHCount = Integer.parseInt(dataArray.get(2).toString());
                player.itemEvent.lastHHTime = Long.parseLong(dataArray.get(3).toString());
                player.itemEvent.remainingBNCount = Integer.parseInt(dataArray.get(4).toString());
                player.itemEvent.lastBNTime = Long.parseLong(dataArray.get(5).toString());
            } catch (Exception e) {
                player.itemEvent.remainingTVGSCount = 0;
                player.itemEvent.lastTVGSTime = 0;
                player.itemEvent.remainingHHCount = 0;
                player.itemEvent.lastHHTime = 0;
                player.itemEvent.remainingBNCount = 0;
                player.itemEvent.lastBNTime = 0;
            }

            //data luyện tập
            try {
                dataArray = (JSONArray) JSONValue.parse(rs.getString("data_luyentap"));
                player.levelLuyenTap = Integer.parseInt(dataArray.get(0).toString());
                player.dangKyTapTuDong = Boolean.parseBoolean(dataArray.get(1).toString());
                player.mapIdDangTapTuDong = Integer.parseInt(dataArray.get(2).toString());
                player.tnsmLuyenTap = Integer.parseInt(dataArray.get(3).toString());
                player.lastTimeOffline = Long.parseLong(dataArray.get(4).toString());
                if (dataArray.size() > 5) {
                    player.traning.setTop(Integer.parseInt(dataArray.get(5).toString()));
                    player.traning.setTime(Integer.parseInt(dataArray.get(6).toString()));
                    player.traning.setLastTime(Long.parseLong(dataArray.get(7).toString()));
                    player.traning.setLastTop(Integer.parseInt(dataArray.get(8).toString()));
                    player.traning.setLastRewardTime(Long.parseLong(dataArray.get(9).toString()));
                }
            } catch (Exception e) {
                player.levelLuyenTap = 0;
                player.dangKyTapTuDong = false;
                player.mapIdDangTapTuDong = -1;
                player.tnsmLuyenTap = 0;
                player.lastTimeOffline = System.currentTimeMillis();
            }

            //data nhiệm vụ bang hàng ngày
            try {
                dataArray = (JSONArray) JSONValue.parse(rs.getString("data_clan_task"));
                format = "dd-MM-yyyy";
                receivedTime = Long.parseLong(String.valueOf(dataArray.get(1)));
                date = new Date(receivedTime);
                if (TimeUtil.formatTime(date, format).equals(TimeUtil.formatTime(new Date(), format))) {
                    player.playerTask.clanTask.template = TaskService.gI().getClanTaskTemplateById(Integer.parseInt(String.valueOf(dataArray.get(0))));
                    player.playerTask.clanTask.count = Integer.parseInt(String.valueOf(dataArray.get(2)));
                    player.playerTask.clanTask.maxCount = Integer.parseInt(String.valueOf(dataArray.get(3)));
                    player.playerTask.clanTask.leftTask = Integer.parseInt(String.valueOf(dataArray.get(4)));
                    player.playerTask.clanTask.level = Integer.parseInt(String.valueOf(dataArray.get(5)));
                    player.playerTask.clanTask.receivedTime = receivedTime;
                }
            } catch (Exception e) {
            }
            try {
                String dataVipStr = rs.getString("data_vip");

                if (dataVipStr != null && !dataVipStr.isEmpty()) {
                    dataArray = (JSONArray) JSONValue.parse(rs.getString(dataVipStr));

                    if (dataArray != null && dataArray.size() >= 2) {
                        Object vipObj = dataArray.get(0);
                        Object dateObj = dataArray.get(1);

                        if (vipObj != null) {
                            player.vip = Integer.parseInt(String.valueOf(vipObj));
                        }

                        if (dateObj != null && !String.valueOf(dateObj).isEmpty() && !String.valueOf(dateObj).equals("null")) {
                            player.timevip = LocalDate.parse(String.valueOf(dateObj));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace(); // In lỗi nếu có, nhưng không crash game
                // vẫn giữ player.vip = 0; và timevip = null
            }

            //data super rank
            player.superRank.rank = Integer.parseInt(rs.getString("rank"));
            try {
                dataArray = (JSONArray) JSONValue.parse(rs.getString("data_super_rank"));
                player.superRank.lastTimePK = Long.parseLong(String.valueOf(dataArray.get(0)));
                player.superRank.lastTimeReward = Long.parseLong(String.valueOf(dataArray.get(1)));
                player.superRank.ticket = Integer.parseInt(String.valueOf(dataArray.get(2)));
                player.superRank.win = Integer.parseInt(String.valueOf(dataArray.get(3)));
                player.superRank.lose = Integer.parseInt(String.valueOf(dataArray.get(4)));
                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(String.valueOf(dataArray.get(5)), JsonObject.class);
                JsonArray history = jsonObject.getAsJsonArray("history");
                JsonArray lasttime = jsonObject.getAsJsonArray("lasttime");
                for (int i = 0; i < history.size(); i++) {
                    player.superRank.history(history.get(i).getAsString(), lasttime.get(i).getAsLong());
                }
            } catch (Exception e) {
            }
            //data super rank         

            if (Util.isAfterMidnight(player.superRank.lastTimePK)) {
                if (player.superRank.ticket < 3) {
                    player.superRank.ticket++;
                }
                player.superRank.lastTimePK = System.currentTimeMillis();
            }

            //data achievement
            try {
                dataArray = (JSONArray) JSONValue.parse(rs.getString("data_achievement"));
                for (int i = 0; i < Manager.ACHIEVEMENT_TEMPLATE.size(); i++) {
                    AchievementQuest aq;
                    if (i < dataArray.size()) {
                        JSONArray data = (JSONArray) JSONValue.parse(dataArray.get(i).toString());
                        aq = new AchievementQuest(Long.parseLong(data.get(0).toString()), Boolean.parseBoolean(data.get(1).toString()));
                    } else {
                        aq = new AchievementQuest(0, false);
                    }
                    player.achievement.add(aq);
                }
                dataArray.clear();
            } catch (Exception e) {
            }
              //data dưa
                  try {
                            dataArray = (JSONArray) JSONValue.parse(rs.getString("data_dua"));
                            if (dataArray.size() != 0) {
                                player.timedua = new Timedua(player, Long.parseLong(String.valueOf(dataArray.get(1))),
                                        Long.parseLong(String.valueOf(dataArray.get(2))));
                           player.lastPlantTime = Long.parseLong(String.valueOf(dataArray.get(0)));
                            }
                            dataArray.clear();
  } catch (Exception e) {
            }
            //Giftcode
            try {
                dataArray = (JSONArray) JSONValue.parse(rs.getString("giftcode"));
                for (Object code : dataArray) {
                    player.giftCode.add((String) code);
                }
                dataArray.clear();
            } catch (Exception e) {
            }

            try {
                player.firstTimeLogin = rs.getTimestamp("firstTimeLogin");
            } catch (Exception e) {
            }

            try {
                dataArray = (JSONArray) JSONValue.parse(rs.getString("dataBadges"));
                for (int i = 0; i < dataArray.size(); i++) {
                    JSONObject obj = (JSONObject) dataArray.get(i);
                    int idBadges = Integer.parseInt(obj.get("idBadGes").toString());
                    long timeOfUseBadges = Long.parseLong(obj.get("timeofUseBadges").toString());
                    boolean isUse = Boolean.parseBoolean(String.valueOf(obj.get("isUse")));
                    player.dataBadges.add(new BadgesData(idBadges, timeOfUseBadges, isUse));
                }
                dataArray.clear();
            } catch (Exception ex) {
            }

            try {
                dataArray = (JSONArray) JSONValue.parse(rs.getString("dataTaskBadges"));
                for (int i = 0; i < dataArray.size(); i++) {
                    JSONObject obj = (JSONObject) dataArray.get(i);
                    BadgesTask data = new BadgesTask();
                    data.id = Integer.parseInt(obj.get("id").toString());
                    data.count = Integer.parseInt(obj.get("count").toString());
                    data.countMax = Integer.parseInt(obj.get("countMax").toString());
                    data.idBadgesReward = Integer.parseInt(obj.get("idBadgesReward").toString());
                    player.dataTaskBadges.add(data);
                }
                dataArray.clear();
            } catch (Exception ex) {
                BadgesTaskService.createAndResetTask(player);
            }

            try {
                dataArray = (JSONArray) JSONValue.parse(rs.getString("dailyGift"));
                if (dataArray.size() < 2) {
                    DailyGiftService.addAndReset(player);
                } else {
                    for (int i = 0; i < dataArray.size(); i++) {
                        JSONObject obj = (JSONObject) dataArray.get(i);
                        DailyGiftData data = new DailyGiftData();
                        data.id = Byte.parseByte(obj.get("id").toString());
                        data.daNhan = Boolean.parseBoolean(obj.get("daNhan").toString());
                        player.dailyGiftData.add(data);
                    }
                }
                dataArray.clear();
            } catch (Exception ex) {
                DailyGiftService.addAndReset(player);
            }
            try {
                dataArray = (JSONArray) JSONValue.parse(rs.getString("LearnSkill"));
                player.LearnSkill.Time = Long.parseLong(String.valueOf(dataArray.get(0)));
                player.LearnSkill.ItemTemplateSkillId = Short.parseShort(String.valueOf(dataArray.get(1)));
                player.LearnSkill.Potential = Integer.parseInt(String.valueOf(dataArray.get(2)));

            } catch (Exception e) {
            }
            try {
                dataArray = (JSONArray) JSONValue.parse(rs.getString("BoughtSkill"));
                for (Object idSkill : dataArray) {
                    player.BoughtSkill.add(((Long) idSkill).intValue());
                }
                dataArray.clear();
            } catch (Exception e) {
                Logger.log(e.toString());
            }

            PlayerService.gI().dailyLogin(player);// RESET DATA KHI QUA 12H ĐÊM
//            if (player.nPoint.power >= 100_010_000_000L) {
//                player.nPoint.power = 100_010_000_000L;
//            }
            if (player.getSession() != null && player.getSession().actived && player.getSession().cash < 0) {
                player.getSession().actived = false;
                player.getSession().cash = 0;
            }
            player.nPoint.hp = plHp;
            player.nPoint.mp = plMp;
            player.iDMark.setLoadedAllDataPlayer(true);
        } catch (Exception e) {
            if (player != null) {
                player.dispose();
                player = null;
            }
            e.printStackTrace();
        }
        return player;
    }

    public static List<Player> getAllPlayer() {
        try {
            List<Player> players = new ArrayList<>();
            NDVResultSet rs = null;
            try {
                Player player = new Player();
                rs = DBConnecter.executeQuery("select * from player");
                while (rs.next()) {
                    long plHp = 200000000;
                    long plMp = 200000000;
                    JSONValue jv = new JSONValue();
                    JSONArray dataArray = null;

                    player = new Player();

                    //base info
                    player.id = rs.getInt("id");
                    player.name = rs.getString("name");
                    player.head = rs.getShort("head");
                    player.gender = rs.getByte("gender");
                    player.haveTennisSpaceShip = rs.getBoolean("have_tennis_space_ship");
                    //data box hòm thư
                    dataArray = (JSONArray) JSONValue.parse(rs.getString("item_mails_box"));
                    for (int i = 0; i < dataArray.size(); i++) {
                        Item item = null;
                        JSONArray dataItem = (JSONArray) JSONValue.parse(dataArray.get(i).toString());
                        short tempId = Short.parseShort(String.valueOf(dataItem.get(0)));
                        if (tempId != -1) {
                            item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataItem.get(1))));
                            JSONArray options = (JSONArray) JSONValue.parse(String.valueOf(dataItem.get(2)).replaceAll("\"", ""));
                            for (int j = 0; j < options.size(); j++) {
                                JSONArray opt = (JSONArray) JSONValue.parse(String.valueOf(options.get(j)));
                                item.itemOptions.add(new Item.ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                        Integer.parseInt(String.valueOf(opt.get(1)))));
                            }
                            item.createTime = Long.parseLong(String.valueOf(dataItem.get(3)));
                            if (ItemService.gI().isOutOfDateTime(item)) {
                                item = ItemService.gI().createItemNull();
                            }
                        } else {
                            item = ItemService.gI().createItemNull();
                        }
                        player.inventory.itemsMailBox.add(item);
                    }
                    dataArray.clear();

                    player.nPoint.hp = plHp;
                    player.nPoint.mp = plMp;
                    player.iDMark.setLoadedAllDataPlayer(true);

                    players.add(player);

                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (rs != null) {
                    rs.dispose();
                }
            }

            return players;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Player loadPlayerByName(String name) {
        Player player = null;
        NDVResultSet rs = null;
        try {
            player = Client.gI().getPlayer(name);
            if (player != null) {
                return player;
            }
            rs = DBConnecter.executeQuery("select * from player where name = ? limit 1", name);
            if (rs.first()) {
                long plHp = 200000000;
                long plMp = 200000000;
                JSONValue jv = new JSONValue();
                JSONArray dataArray = null;

                player = new Player();

                //base info
                player.id = rs.getInt("id");
                player.name = rs.getString("name");
                player.head = rs.getShort("head");
                player.gender = rs.getByte("gender");
                player.haveTennisSpaceShip = rs.getBoolean("have_tennis_space_ship");

                //data body
                dataArray = (JSONArray) JSONValue.parse(rs.getString("item_mails_box"));
                for (int i = 0; i < dataArray.size(); i++) {
                    Item item = null;
                    JSONArray dataItem = (JSONArray) JSONValue.parse(dataArray.get(i).toString());
                    short tempId = Short.parseShort(String.valueOf(dataItem.get(0)));
                    if (tempId != -1) {
                        item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataItem.get(1))));
                        JSONArray options = (JSONArray) JSONValue.parse(String.valueOf(dataItem.get(2)).replaceAll("\"", ""));
                        for (int j = 0; j < options.size(); j++) {
                            JSONArray opt = (JSONArray) JSONValue.parse(String.valueOf(options.get(j)));
                            item.itemOptions.add(new Item.ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                    Integer.parseInt(String.valueOf(opt.get(1)))));
                        }
                        item.createTime = Long.parseLong(String.valueOf(dataItem.get(3)));
                        if (ItemService.gI().isOutOfDateTime(item)) {
                            item = ItemService.gI().createItemNull();
                        }
                    } else {
                        item = ItemService.gI().createItemNull();
                    }
                    player.inventory.itemsMailBox.add(item);
                }
                dataArray.clear();
                player.nPoint.hp = plHp;
                player.nPoint.mp = plMp;
                player.iDMark.setLoadedAllDataPlayer(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
            player.dispose();
            player = null;
            Logger.logException(NDVSqlFetcher.class, e);
        } finally {
            if (rs != null) {
                rs.dispose();
            }
        }
        return player;
    }

    public static Player loadPlayerByID(long id) {
        Player player = null;
        NDVResultSet rs = null;
        try {
            player = Client.gI().getPlayer(id);
            if (player != null) {
                return player;
            }
            rs = DBConnecter.executeQuery("select * from player where id = ? limit 1", id);
            if (rs.first()) {
                long plHp = 200000000;
                long plMp = 200000000;
                JSONValue jv = new JSONValue();
                JSONArray dataArray = null;

                player = new Player();

                //base info
                player.id = rs.getInt("id");
                player.name = rs.getString("name");
                player.head = rs.getShort("head");
                player.gender = rs.getByte("gender");
                player.haveTennisSpaceShip = rs.getBoolean("have_tennis_space_ship");

                //data body
                dataArray = (JSONArray) JSONValue.parse(rs.getString("item_mails_box"));
                for (int i = 0; i < dataArray.size(); i++) {
                    Item item = null;
                    JSONArray dataItem = (JSONArray) JSONValue.parse(dataArray.get(i).toString());
                    short tempId = Short.parseShort(String.valueOf(dataItem.get(0)));
                    if (tempId != -1) {
                        item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataItem.get(1))));
                        JSONArray options = (JSONArray) JSONValue.parse(String.valueOf(dataItem.get(2)).replaceAll("\"", ""));
                        for (int j = 0; j < options.size(); j++) {
                            JSONArray opt = (JSONArray) JSONValue.parse(String.valueOf(options.get(j)));
                            item.itemOptions.add(new Item.ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                    Integer.parseInt(String.valueOf(opt.get(1)))));
                        }
                        item.createTime = Long.parseLong(String.valueOf(dataItem.get(3)));
                        if (ItemService.gI().isOutOfDateTime(item)) {
                            item = ItemService.gI().createItemNull();
                        }
                    } else {
                        item = ItemService.gI().createItemNull();
                    }
                    player.inventory.itemsMailBox.add(item);
                }
                dataArray.clear();
                player.nPoint.hp = plHp;
                player.nPoint.mp = plMp;
                player.isOffline = true;
                player.iDMark.setLoadedAllDataPlayer(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            player.dispose();
            player = null;
            Logger.logException(NDVSqlFetcher.class, e);
        } finally {
            if (rs != null) {
                rs.dispose();
            }
        }
        return player;
    }

    public static boolean updateMailBox(Player player) {
        try {
            JSONArray dataArray = new JSONArray();
            JSONArray dataItem = new JSONArray();
            for (Item item : player.inventory.itemsMailBox) {
                JSONArray opt = new JSONArray();
                if (item.isNotNullItem()) {
                    dataItem.add(item.template.id);
                    dataItem.add(item.quantity);
                    JSONArray options = new JSONArray();
                    for (Item.ItemOption io : item.itemOptions) {
                        opt.add(io.optionTemplate.id);
                        opt.add(io.param);
                        options.add(opt.toJSONString());
                        opt.clear();
                    }
                    dataItem.add(options.toJSONString());
                } else {
                    dataItem.add(-1);
                    dataItem.add(0);
                    dataItem.add(opt.toJSONString());
                }
                dataItem.add(item.createTime);
                dataArray.add(dataItem.toJSONString());
                dataItem.clear();
            }
            String itemsBox = dataArray.toJSONString();
            dataArray.clear();
            try (Connection con = DBConnecter.getConnectionServer(); PreparedStatement ps = con.prepareStatement("update `player` set item_mails_box = ? where id = ?")) {
                ps.setString(1, itemsBox);
                ps.setLong(2, player.id);
                ps.executeUpdate();
                ps.close();
                con.close();
                return true;
            } catch (SQLException e) {
                Logger.logException(PlayerDAO.class, e, "Error updating MailsBox for player: " + player.name);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public static List<OptionCard> loadOptionCard(JSONArray json) {
        List<OptionCard> ops = new ArrayList<>();
        try {
            for (int i = 0; i < json.size(); i++) {
                JSONObject ob = (JSONObject) json.get(i);
                if (ob != null) {
                    ops.add(new OptionCard(Integer.parseInt(ob.get("id").toString()), Integer.parseInt(ob.get("param").toString()), Byte.parseByte(ob.get("active").toString())));
                }
            }
        } catch (NumberFormatException e) {
        }
        return ops;
    }
}
