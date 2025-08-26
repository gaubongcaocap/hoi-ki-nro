package server;

/*
 *
 *
 * @author Entidi (NTD - Tấn Đạt)
 */
import models.Card.OptionCard;
import models.Card.RadarService;
import models.Card.RadarCard;
import models.Consign.ConsignItem;
import models.Consign.ConsignShopManager;
import jdbc.DBConnecter;
import consts.ConstPlayer;
import consts.ConstMap;
import data.DataGame;
import jdbc.daos.ShopDAO;
import models.Template.*;
import clan.Clan;
import clan.ClanMember;
import consts.ConstSQL;
import encrypt.ImageUtil;
import models.GiftCode.GiftCode;
import models.GiftCode.GiftCodeManager;
import intrinsic.Intrinsic;
import item.Item;
import item.Item.ItemOption;
import map.WayPoint;
import npc.Npc;
import npc.NpcFactory;
import player.badges.BagesTemplate;
import shop.Shop;
import skill.NClass;
import skill.Skill;
import task.Badges.BadgesTaskTemplate;
import task.SideTaskTemplate;
import task.SubTaskMain;
import task.TaskMain;
import services.ItemService;
import services.MapService;
import utils.Logger;
import utils.Util;
import power.CaptionManager;
import power.PowerLimitManager;
import task.ClanTaskTemplate;

import java.io.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import matches.TOP;
import npc.NonInteractiveNPC;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import Bot.Bot;

public final class Manager {

    private static Manager instance;
    public static String PASS = "1";
    public static byte SERVER = 1;
    public static byte SECOND_WAIT_LOGIN = 5;
    public static int MAX_PER_IP = 5;
    public static int MAX_PLAYER = 2000;
    public static byte RATE_EXP_SERVER = 1;
    public static boolean LOCAL = false;
    public static boolean TEST = false;
    public static boolean DAO_AUTO_UPDATER = false;

    public static MapTemplate[] MAP_TEMPLATES;
    public static final List<map.Map> MAPS = new ArrayList<>();
    public static final List<ItemOptionTemplate> ITEM_OPTION_TEMPLATES = new ArrayList<>();
    public static final List<ArrHead2Frames> ARR_HEAD_2_FRAMES = new ArrayList<>();
    public static final Map<String, Byte> IMAGES_BY_NAME = new HashMap<>();
    public static final List<ItemTemplate> ITEM_TEMPLATES = new ArrayList<>();
    public static final List<MobTemplate> MOB_TEMPLATES = new ArrayList<>();
    public static final List<NpcTemplate> NPC_TEMPLATES = new ArrayList<>();
    public static final List<TaskMain> TASKS = new ArrayList<>();
    public static final List<SideTaskTemplate> SIDE_TASKS_TEMPLATE = new ArrayList<>();
    public static final List<ClanTaskTemplate> CLAN_TASKS_TEMPLATE = new ArrayList<>();
    public static final List<AchievementTemplate> ACHIEVEMENT_TEMPLATE = new ArrayList<>();
    public static final List<Intrinsic> INTRINSICS = new ArrayList<>();
    public static final List<Intrinsic> INTRINSIC_TD = new ArrayList<>();
    public static final List<Intrinsic> INTRINSIC_NM = new ArrayList<>();
    public static final List<Intrinsic> INTRINSIC_XD = new ArrayList<>();
    public static final List<HeadAvatar> HEAD_AVATARS = new ArrayList<>();
    public static final List<BgItem> BG_ITEMS = new ArrayList<>();
    public static final List<FlagBag> FLAGS_BAGS = new ArrayList<>();
    public static final List<NClass> NCLASS = new ArrayList<>();
    public static final List<Npc> NPCS = new ArrayList<>();
    public static List<Shop> SHOPS = new ArrayList<>();
    public static final List<Clan> CLANS = new ArrayList<>();
    public static final List<String> NOTIFY = new ArrayList<>();
    public static final List<BadgesTaskTemplate> TASKS_BADGES_TEMPLATE = new ArrayList<>();
    public static final List<BagesTemplate> BAGES_TEMPLATES = new ArrayList<>();

    public static List<TOP> topSM;
    public static List<TOP> topNap;
    // public static List<TOP> topDuaSM;
    // public static List<TOP> topDuaNap;
    public static List<TOP> topSD;
    public static List<TOP> topHP;
    public static List<TOP> topKI;
    public static List<TOP> topNV;
    public static List<TOP> topSK;
    public static List<TOP> topPVP;
    public static List<TOP> topNHS;
    public static List<TOP> topDC;
    public static List<TOP> topVDST;
    public static List<TOP> topWHIS;
    public static long timeRealTop = 0;

    public static List<Integer> mapBot = new ArrayList<Integer>();

    public static final short[][] doshoptd = {{0, 33, 3, 34, 136, 137, 138, 139, 230, 231, 232, 233},
    {6, 35, 9, 36, 140, 141, 142, 143, 242, 243, 244, 245},
    {27, 30, 39, 40, 148, 149, 150, 151, 266, 267, 268, 269},
    {12, 57, 58, 59, 184, 185, 186, 187, 278, 279, 280, 281}};

    public static final short[][] doshopnm = {
        {1, 41, 4, 42, 152, 153, 154, 155, 234, 235, 236, 237},
        {7, 43, 10, 44, 156, 157, 158, 159, 246, 247, 248, 249},
        {22, 46, 25, 45, 160, 161, 162, 163, 258, 259, 260, 261},
        {28, 47, 31, 48, 164, 165, 166, 167, 270, 271, 272, 273},
        {12, 57, 58, 59, 184, 185, 186, 187, 278, 279, 280, 281}
    };
    public static final short[][] doshopxd = {
        {2, 49, 5, 50, 168, 169, 170, 171, 238, 239, 240, 241},
        {8, 51, 11, 52, 172, 173, 174, 175, 250, 251, 252, 253},
        {23, 53, 26, 54, 176, 177, 178, 179, 262, 263, 264, 265},
        {29, 55, 32, 56, 180, 181, 182, 183, 274, 275, 276, 277},
        {12, 57, 58, 59, 184, 185, 186, 187, 278, 279, 280, 281}
    };

    public static final short[][] trangBiKichHoat = {{0, 6, 21, 27}, {1, 7, 22, 28}, {2, 8, 23, 29}};
    public static final short[][] trangBiKichHoatVip = {{555, 556, 562, 563}, {557, 558, 564, 565}, {559, 560, 566, 567}};
    public static String TIME_VIP_START = "15/02/2025";
    public static String TIME_VIP_END = "15/03/2026";

    public static Manager gI() {
        if (instance == null) {
            instance = new Manager();
        }
        return instance;
    }

    private Manager() {
        try {
            loadProperties();
        } catch (IOException ex) {
            Logger.logException(Manager.class, ex, "Lỗi load properties");
            System.exit(0);
        }
        loadDatabase();
        NpcFactory.createNpcConMeo();
        NpcFactory.createNpcRongThieng();
        initMap();
        System.out.println("Finish connect Server: " + DBConnecter.DB_DATA);
    }

    private void initMap() {
        int[][] tileTyleTop = readTileIndexTileType(ConstMap.TILE_TOP);
        for (MapTemplate mapTemp : MAP_TEMPLATES) {
            int[][] tileMap = readTileMap(mapTemp.id);
            int[] tileTop = tileTyleTop[mapTemp.tileId - 1];
            map.Map map = new map.Map(mapTemp.id,
                    mapTemp.name, mapTemp.planetId, mapTemp.tileId, mapTemp.bgId,
                    mapTemp.bgType, mapTemp.type, tileMap, tileTop,
                    mapTemp.zones,
                    mapTemp.maxPlayerPerZone, mapTemp.wayPoints, mapTemp.effectMaps);
            MAPS.add(map);
            map.initMob(mapTemp.mobTemp, mapTemp.mobLevel, mapTemp.mobHp, mapTemp.mobX, mapTemp.mobY);
            map.initNpc(mapTemp.npcId, mapTemp.npcX, mapTemp.npcY);
            Thread.startVirtualThread(map);
        }
        new NonInteractiveNPC().initNonInteractiveNPC();
        Logger.success("Initialize map successfully!\n");
    }

    private void loadDatabase() {
        long st = System.currentTimeMillis();
        try (Connection con = DBConnecter.getConnectionServer()) {
            loadClans(con);
            loadItemOptionTemplates(con);
            loadConsignItems(con);
            loadGiftCodes(con);
            loadParts(con);
            loadBgItemTemplates(con);
            loadArrHead2Frames(con);
            loadSkills(con);
            loadHeadAvatars(con);
            loadFlagBags(con);
            loadIntrinsics(con);
            loadTasks(con);
            loadSideTasks(con);
            loadBadgesTaskTemplates(con);
            loadClanTaskTemplates(con);
            loadAchievements(con);
            loadItemTemplates(con);
            SHOPS = ShopDAO.getShops(con);
            Logger.success("Successfully loaded shop (" + SHOPS.size() + ")\n");
            loadNotifies(con);
            loadImagesByName(con);
            loadMobTemplates(con);
            loadNpcTemplates(con);
            loadMapTemplates(con);
            loadRadarTemplates(con);
            loadBadgesTemplates(con);
            loadTopRankings(con);
            loadMount();
            loadMapBot(con);
            PowerLimitManager.getInstance().load();
            CaptionManager.getInstance().load();
        } catch (Exception e) {
            Logger.logException(Manager.class, e, "Database loading error");
            System.exit(0);
        }
        Logger.log(Logger.PURPLE, "Total database loading time: " + (System.currentTimeMillis() - st) + " (ms)\n");
    }

    private void loadMapBot(Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("select id from map_template where bot_map = 1"); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                while (rs.next()) {
                    int id = rs.getInt("id"); 
                    mapBot.add(id);
                }
                mapBot.add(0);
            }
        }
    }

    // ---------- Các hàm load dữ liệu từ DB ----------
    private void loadTopRankings(Connection con) throws SQLException {
        topNV = realTop(ConstSQL.TOP_NV, con);
        Logger.success("Successfully loaded task top (" + topNV.size() + ")\n");
        topSM = realTop(ConstSQL.TOP_SM, con);
        Logger.success("Successfully loaded power top (" + topSM.size() + ")\n");
        topNap = realTop(ConstSQL.TOP_NAP, con);
        Logger.success("Successfully loaded nạp top (" + topNap.size() + ")\n");
        topWHIS = realTop(ConstSQL.TOP_WHIS, con);
        Logger.success("Successfully loaded WHIS top (" + topWHIS.size() + ")\n");
        topVDST = realTop(ConstSQL.TOP_VDST, con);
        Logger.success("Successfully loaded VDST top (" + topVDST.size() + ")\n");
    }

    // Hàm thực hiện truy vấn top theo query đã cho.
    public static List<TOP> realTop(String query, Connection con) {
        List<TOP> tops = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                // Tùy thuộc vào query, xây dựng đối tượng TOP
                TOP top = TOP.builder()
                        .name(rs.getString("name"))
                        .gender(rs.getByte("gender"))
                        .build();
                // Ví dụ: nếu query là TOP_SM, thiết lập power
                if (query.equals(ConstSQL.TOP_SM)) {
                    top.setPower(rs.getLong("sm"));
                }
                // Xử lý các trường khác theo query
                tops.add(top);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tops;
    }

    // Hàm đọc cấu hình server từ file properties.
    private void loadProperties() throws IOException {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream("data/config/config.properties")) {
            properties.load(fis);
        }
        Object value;
        if ((value = properties.get("server.sv")) != null) {
            SERVER = Byte.parseByte(String.valueOf(value));
        }
        if ((value = properties.get("server.name")) != null) {
            ServerManager.NAME = String.valueOf(value);
        }
        if ((value = properties.get("server.port")) != null) {
            ServerManager.PORT = Integer.parseInt(String.valueOf(value));
        }
        StringBuilder linkServer = new StringBuilder();
        if ((value = properties.get("server.ip")) != null) {
            ServerManager.IP = String.valueOf(value);
            linkServer.append(ServerManager.NAME).append(":")
                    .append(ServerManager.IP).append(":")
                    .append(ServerManager.PORT).append(":0,");
        }
        for (int i = 1; i <= 10; i++) {
            value = properties.get("server.sv" + i);
            if (value != null) {
                linkServer.append(value).append(":0,");
            }
        }
        if (linkServer.length() > 0) {
            DataGame.LINK_IP_PORT = linkServer.substring(0, linkServer.length() - 1);
        }
        if ((value = properties.get("server.waitlogin")) != null) {
            SECOND_WAIT_LOGIN = Byte.parseByte(String.valueOf(value));
        }
        if ((value = properties.get("server.maxperip")) != null) {
            MAX_PER_IP = Integer.parseInt(String.valueOf(value));
        }
        if ((value = properties.get("server.maxplayer")) != null) {
            MAX_PLAYER = Integer.parseInt(String.valueOf(value));
        }
        if ((value = properties.get("server.expserver")) != null) {
            RATE_EXP_SERVER = Byte.parseByte(String.valueOf(value));
        }
        if ((value = properties.get("server.local")) != null) {
            LOCAL = String.valueOf(value).toLowerCase().equals("true");
        }
        if ((value = properties.get("server.test")) != null) {
            TEST = String.valueOf(value).toLowerCase().equals("true");
        }
        if ((value = properties.get("server.daoautoupdater")) != null) {
            DAO_AUTO_UPDATER = String.valueOf(value).toLowerCase().equals("true");
        }
        if ((value = properties.get("server.pass")) != null) {
            PASS = String.valueOf(value);
        }
    }

    private void loadClans(Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("select * from clan"); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Clan clan = new Clan();
                clan.id = rs.getInt("id");
                clan.name = rs.getString("name");
                clan.name2 = rs.getString("name_2");
                clan.slogan = rs.getString("slogan");
                clan.imgId = rs.getByte("img_id");
                clan.powerPoint = rs.getLong("power_point");
                clan.maxMember = rs.getByte("max_member");
                clan.capsuleClan = rs.getInt("clan_point");

                clan.level = rs.getByte("level");
                if (clan.level < 1) {
                    clan.level = 1;
                }
                clan.createTime = (int) (rs.getTimestamp("create_time").getTime() / 1000);
                JSONArray dataArray = (JSONArray) JSONValue.parse(rs.getString("members"));
                for (Object memberObj : dataArray) {
                    JSONObject dataObject = (JSONObject) JSONValue.parse(String.valueOf(memberObj));
                    ClanMember cm = new ClanMember();
                    cm.clan = clan;
                    cm.id = Integer.parseInt(String.valueOf(dataObject.get("id")));
                    cm.name = String.valueOf(dataObject.get("name"));
                    cm.head = Short.parseShort(String.valueOf(dataObject.get("head")));
                    cm.body = Short.parseShort(String.valueOf(dataObject.get("body")));
                    cm.leg = Short.parseShort(String.valueOf(dataObject.get("leg")));
                    cm.role = Byte.parseByte(String.valueOf(dataObject.get("role")));
                    cm.donate = Integer.parseInt(String.valueOf(dataObject.get("donate")));
                    cm.receiveDonate = Integer.parseInt(String.valueOf(dataObject.get("receive_donate")));
                    cm.memberPoint = Integer.parseInt(String.valueOf(dataObject.get("member_point")));
                    cm.clanPoint = Integer.parseInt(String.valueOf(dataObject.get("clan_point")));
                    cm.joinTime = Integer.parseInt(String.valueOf(dataObject.get("join_time")));
                    cm.timeAskPea = Long.parseLong(String.valueOf(dataObject.get("ask_pea_time")));
                    try {
                        cm.powerPoint = Long.parseLong(String.valueOf(dataObject.get("power")));
                    } catch (NumberFormatException e) {
                    }

                    clan.addClanMember(cm);
                }
                // clan.hpClan = rs.getLong("hp");
                // clan.kiClan = rs.getLong("ki");
                // clan.dameClan = rs.getLong("dame");
                // clan.critClan = rs.getInt("crit");
                // clan.defClan = rs.getLong("def");
                CLANS.add(clan);
            }
        }
        try (PreparedStatement ps = con.prepareStatement("select id from clan order by id desc limit 1");
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                Clan.NEXT_ID = rs.getInt("id") + 1;
            }
        }
        Logger.success("Successfully loaded clan (" + CLANS.size() + "), clan next id: " + Clan.NEXT_ID + "\n");
    }

    private void loadItemOptionTemplates(Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("select id, name from item_option_template");
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ItemOptionTemplate optionTemp = new ItemOptionTemplate();
                optionTemp.id = rs.getInt("id");
                optionTemp.name = rs.getString("name");
                ITEM_OPTION_TEMPLATES.add(optionTemp);
            }
        }
        Logger.success("Successfully loaded map item option template (" + ITEM_OPTION_TEMPLATES.size() + ")\n");
    }

    private void loadConsignItems(Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("select * from shop_ky_gui");
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int i = rs.getInt("id");
                int idPl = rs.getInt("player_id");
                byte tab = rs.getByte("tab");
                short itemId = rs.getShort("item_id");
                int gold = rs.getInt("gold");
                int gem = rs.getInt("gem");
                int quantity = rs.getInt("quantity");
                long isTime = rs.getLong("lasttime");
                boolean isBuy = rs.getByte("isBuy") == 1;
                List<ItemOption> op = new ArrayList<>();
                // Phân tích itemOption nếu cần
                ConsignShopManager.gI().listItem
                        .add(new ConsignItem(i, itemId, idPl, tab, gold, gem, quantity, isTime, op, isBuy));
            }
        }
        Logger.success("Successfully loaded Consign Item (" + ConsignShopManager.gI().listItem.size() + ")\n");
    }

    private void loadGiftCodes(Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM giftcode"); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                GiftCode giftcode = new GiftCode();
                giftcode.code = rs.getString("code");
                giftcode.id = rs.getInt("id");
                giftcode.countLeft = rs.getInt("count_left");
                if (giftcode.countLeft == -1) {
                    giftcode.countLeft = 999999999;
                }
                giftcode.datecreate = rs.getTimestamp("datecreate");
                giftcode.dateexpired = rs.getTimestamp("expired");
                JSONArray jar = (JSONArray) JSONValue.parse(rs.getString("detail"));
                if (jar != null) {
                    for (Object obj : jar) {
                        JSONObject jsonObj = (JSONObject) JSONValue.parse(String.valueOf(obj));
                        int tempId = Integer.parseInt(jsonObj.get("temp_id").toString());
                        int quantity = Integer.parseInt(jsonObj.get("quantity").toString());
                        JSONArray option = (JSONArray) jsonObj.get("options");
                        ArrayList<ItemOption> optionList = new ArrayList<>();
                        if (option != null) {
                            for (Object opObj : option) {
                                JSONObject jsonobject = (JSONObject) opObj;
                                int optionId = Integer.parseInt(jsonobject.get("id").toString());
                                int param = Integer.parseInt(jsonobject.get("param").toString());
                                optionList.add(new ItemOption(optionId, param));
                            }
                        }
                        giftcode.option.put(tempId, optionList);
                        giftcode.detail.put(tempId, quantity);
                    }
                }
                GiftCodeManager.gI().listGiftCode.add(giftcode);
            }
        }
        Logger.success("Successfully loaded giftcode (" + GiftCodeManager.gI().listGiftCode.size() + ")\n");
    }

    private void loadParts(Connection con) throws SQLException, IOException {
        try (PreparedStatement ps = con.prepareStatement("select * from part"); ResultSet rs = ps.executeQuery()) {
            List<Part> parts = new ArrayList<>();
            while (rs.next()) {
                Part part = new Part();
                part.id = rs.getShort("ID");
                part.type = rs.getByte("TYPE");
                JSONArray dataArray = (JSONArray) JSONValue.parse(rs.getString("DATA").replaceAll("\\\"", ""));
                for (Object obj : dataArray) {
                    JSONArray pd = (JSONArray) JSONValue.parse(String.valueOf(obj));
                    part.partDetails.add(new PartDetail(
                            Short.parseShort(String.valueOf(pd.get(0))),
                            Byte.parseByte(String.valueOf(pd.get(1))),
                            Byte.parseByte(String.valueOf(pd.get(2)))
                    ));
                    pd.clear();
                }
                parts.add(part);
            }
            try (DataOutputStream dos = new DataOutputStream(new FileOutputStream("data/update_data/part"))) {
                dos.writeShort(parts.size());
                for (Part part : parts) {
                    dos.writeByte(part.type);
                    for (PartDetail partDetail : part.partDetails) {
                        dos.writeShort(partDetail.iconId);
                        dos.writeByte(partDetail.dx);
                        dos.writeByte(partDetail.dy);
                    }
                }
                dos.flush();
            }
            Logger.success("Successfully loaded part (" + parts.size() + ")\n");
        }
    }

    private void loadBgItemTemplates(Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("select * from bg_item_template");
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                BgItem bgItem = new BgItem();
                bgItem.id = rs.getInt("id");
                bgItem.layer = rs.getByte("layer");
                bgItem.dx = rs.getShort("dx");
                bgItem.dy = rs.getShort("dy");
                bgItem.idImage = rs.getShort("image_id");
                BG_ITEMS.add(bgItem);
            }
        }
        Logger.success("Successfully loaded bg item template (" + BG_ITEMS.size() + ")\n");
    }

    private void loadArrHead2Frames(Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("select * from array_head_2_frames");
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ArrHead2Frames arrHead2Frames = new ArrHead2Frames();
                JSONArray dataArray = (JSONArray) JSONValue.parse(rs.getString("data"));
                for (Object obj : dataArray) {
                    arrHead2Frames.frames.add(Integer.valueOf(obj.toString()));
                }
                ARR_HEAD_2_FRAMES.add(arrHead2Frames);
            }
        }
        Logger.success("Successfully loaded arr head 2 frames (" + ARR_HEAD_2_FRAMES.size() + ")\n");
    }

    private void loadSkills(Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("select * from skill_template order by nclass_id, slot");
                ResultSet rs = ps.executeQuery()) {
            byte nClassId = -1;
            NClass nClass = null;
            while (rs.next()) {
                byte id = rs.getByte("nclass_id");
                if (id != nClassId) {
                    nClassId = id;
                    nClass = new NClass();
                    nClass.name = (id == ConstPlayer.TRAI_DAT) ? "Trái Đất"
                            : (id == ConstPlayer.NAMEC ? "Namếc" : "Xayda");
                    nClass.classId = nClassId;
                    NCLASS.add(nClass);
                }
                SkillTemplate skillTemplate = new SkillTemplate();
                skillTemplate.classId = nClassId;
                skillTemplate.id = rs.getByte("id");
                skillTemplate.name = rs.getString("name");
                skillTemplate.maxPoint = rs.getByte("max_point");
                skillTemplate.manaUseType = rs.getByte("mana_use_type");
                skillTemplate.type = rs.getByte("type");
                skillTemplate.iconId = rs.getShort("icon_id");
                skillTemplate.damInfo = rs.getString("dam_info");
                nClass.skillTemplatess.add(skillTemplate);

                // Lấy chuỗi skills
                String skillsStr = rs.getString("skills");
                if (skillsStr == null || skillsStr.trim().isEmpty()) {
                    continue; // Không có dữ liệu, bỏ qua
                }
                // Tiền xử lý chuỗi nếu cần (theo logic ban đầu)
                skillsStr = skillsStr.replaceAll("\\[\"", "[")
                        .replaceAll("\"\\[", "[")
                        .replaceAll("\"\\]", "]")
                        .replaceAll("\\]\"", "]")
                        .replaceAll("\\}\\,\\{", "},{");
                JSONArray dataArray = (JSONArray) JSONValue.parse(skillsStr);
                if (dataArray == null) {
                    continue;
                }
                for (Object obj : dataArray) {
                    if (obj == null) {
                        continue;
                    }
                    JSONObject dts = (JSONObject) JSONValue.parse(String.valueOf(obj));
                    if (dts == null) {
                        continue;
                    }
                    Skill skill = new Skill();
                    skill.template = skillTemplate;
                    skill.skillId = Short.parseShort(String.valueOf(dts.get("id")));
                    skill.point = Byte.parseByte(String.valueOf(dts.get("point")));
                    skill.powRequire = Long.parseLong(String.valueOf(dts.get("power_require")));
                    skill.manaUse = Integer.parseInt(String.valueOf(dts.get("mana_use")));
                    skill.coolDown = Integer.parseInt(String.valueOf(dts.get("cool_down")));
                    skill.dx = Integer.parseInt(String.valueOf(dts.get("dx")));
                    skill.dy = Integer.parseInt(String.valueOf(dts.get("dy")));
                    skill.maxFight = Integer.parseInt(String.valueOf(dts.get("max_fight")));
                    skill.damage = Short.parseShort(String.valueOf(dts.get("damage")));
                    skill.price = Short.parseShort(String.valueOf(dts.get("price")));
                    skill.moreInfo = String.valueOf(dts.get("info"));
                    skillTemplate.skillss.add(skill);
                }
            }
        }
        Logger.success("Successfully loaded skill (" + NCLASS.size() + ")\n");
    }

    private void loadHeadAvatars(Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("select * from head_avatar");
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                HeadAvatar headAvatar = new HeadAvatar(rs.getInt("head_id"), rs.getInt("avatar_id"));
                HEAD_AVATARS.add(headAvatar);
            }
        }
        Logger.success("Successfully loaded head avatar (" + HEAD_AVATARS.size() + ")\n");
    }

    private void loadFlagBags(Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("select * from flag_bag"); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                FlagBag flagBag = new FlagBag();
                flagBag.id = rs.getInt("id");
                flagBag.name = rs.getString("name");
                flagBag.gold = rs.getInt("gold");
                flagBag.gem = rs.getInt("gem");
                flagBag.iconId = rs.getShort("icon_id");
                String[] iconData = rs.getString("icon_data").split(",");
                flagBag.iconEffect = new short[iconData.length];
                for (int j = 0; j < iconData.length; j++) {
                    flagBag.iconEffect[j] = Short.parseShort(iconData[j].trim());
                }
                FLAGS_BAGS.add(flagBag);
            }
        }
        Logger.success("Successfully loaded flag bag (" + FLAGS_BAGS.size() + ")\n");
    }

    private void loadIntrinsics(Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("select * from intrinsic"); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Intrinsic intrinsic = new Intrinsic();
                intrinsic.id = rs.getByte("id");
                intrinsic.name = rs.getString("name");
                intrinsic.paramFrom1 = rs.getShort("param_from_1");
                intrinsic.paramTo1 = rs.getShort("param_to_1");
                intrinsic.paramFrom2 = rs.getShort("param_from_2");
                intrinsic.paramTo2 = rs.getShort("param_to_2");
                intrinsic.icon = rs.getShort("icon");
                intrinsic.gender = rs.getByte("gender");
                switch (intrinsic.gender) {
                    case ConstPlayer.TRAI_DAT ->
                        INTRINSIC_TD.add(intrinsic);
                    case ConstPlayer.NAMEC ->
                        INTRINSIC_NM.add(intrinsic);
                    case ConstPlayer.XAYDA ->
                        INTRINSIC_XD.add(intrinsic);
                    default -> {
                        INTRINSIC_TD.add(intrinsic);
                        INTRINSIC_NM.add(intrinsic);
                        INTRINSIC_XD.add(intrinsic);
                    }
                }
                INTRINSICS.add(intrinsic);
            }
        }
        Logger.success("Successfully loaded intrinsic (" + INTRINSICS.size() + ")\n");
    }

    private void loadTasks(Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT id, task_main_template.name, detail, "
                        + "task_sub_template.name AS 'sub_name', max_count, notify, npc_id, map "
                        + "FROM task_main_template JOIN task_sub_template ON task_main_template.id = task_sub_template.task_main_id");
                ResultSet rs = ps.executeQuery()) {
            int taskId = -1;
            TaskMain task = null;
            while (rs.next()) {
                int id = rs.getInt("id");
                if (id != taskId) {
                    taskId = id;
                    task = new TaskMain();
                    task.id = taskId;
                    task.name = rs.getString("name");
                    task.detail = rs.getString("detail");
                    TASKS.add(task);
                }
                SubTaskMain subTask = new SubTaskMain();
                subTask.name = rs.getString("sub_name");
                subTask.maxCount = rs.getShort("max_count");
                subTask.notify = rs.getString("notify");
                subTask.npcId = rs.getByte("npc_id");
                subTask.mapId = rs.getShort("map");
                task.subTasks.add(subTask);
            }
        }
        Logger.success("Successfully loaded task (" + TASKS.size() + ")\n");
    }

    private void loadSideTasks(Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("select * from side_task_template");
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                SideTaskTemplate sideTask = new SideTaskTemplate();
                sideTask.id = rs.getInt("id");
                sideTask.name = rs.getString("name");
                String[] mc1 = rs.getString("max_count_lv1").split("-");
                String[] mc2 = rs.getString("max_count_lv2").split("-");
                String[] mc3 = rs.getString("max_count_lv3").split("-");
                String[] mc4 = rs.getString("max_count_lv4").split("-");
                String[] mc5 = rs.getString("max_count_lv5").split("-");
                sideTask.count[0][0] = Integer.parseInt(mc1[0]);
                sideTask.count[0][1] = Integer.parseInt(mc1[1]);
                sideTask.count[1][0] = Integer.parseInt(mc2[0]);
                sideTask.count[1][1] = Integer.parseInt(mc2[1]);
                sideTask.count[2][0] = Integer.parseInt(mc3[0]);
                sideTask.count[2][1] = Integer.parseInt(mc3[1]);
                sideTask.count[3][0] = Integer.parseInt(mc4[0]);
                sideTask.count[3][1] = Integer.parseInt(mc4[1]);
                sideTask.count[4][0] = Integer.parseInt(mc5[0]);
                sideTask.count[4][1] = Integer.parseInt(mc5[1]);
                SIDE_TASKS_TEMPLATE.add(sideTask);
            }
        }
        Logger.success("Successfully loaded side task (" + SIDE_TASKS_TEMPLATE.size() + ")\n");
    }

    private void loadBadgesTaskTemplates(Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("select * from task_badges_template");
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                BadgesTaskTemplate badgesTaskTemplate = new BadgesTaskTemplate();
                badgesTaskTemplate.id = rs.getInt("id");
                badgesTaskTemplate.name = rs.getString("NAME");
                badgesTaskTemplate.count = rs.getInt("maxCount");
                badgesTaskTemplate.idbadgesReward = rs.getInt("idbadgesReward");
                TASKS_BADGES_TEMPLATE.add(badgesTaskTemplate);
            }
        }
        Logger.success("Successfully loaded task badges (" + TASKS_BADGES_TEMPLATE.size() + ")\n");
    }

    private void loadClanTaskTemplates(Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("select * from clan_task_template");
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ClanTaskTemplate clanTask = new ClanTaskTemplate();
                clanTask.id = rs.getInt("id");
                clanTask.name = rs.getString("name");
                String[] mc1 = rs.getString("max_count_lv1").split("-");
                String[] mc2 = rs.getString("max_count_lv2").split("-");
                String[] mc3 = rs.getString("max_count_lv3").split("-");
                String[] mc4 = rs.getString("max_count_lv4").split("-");
                String[] mc5 = rs.getString("max_count_lv5").split("-");
                clanTask.count[0][0] = Integer.parseInt(mc1[0]);
                clanTask.count[0][1] = Integer.parseInt(mc1[1]);
                clanTask.count[1][0] = Integer.parseInt(mc2[0]);
                clanTask.count[1][1] = Integer.parseInt(mc2[1]);
                clanTask.count[2][0] = Integer.parseInt(mc3[0]);
                clanTask.count[2][1] = Integer.parseInt(mc3[1]);
                clanTask.count[3][0] = Integer.parseInt(mc4[0]);
                clanTask.count[3][1] = Integer.parseInt(mc4[1]);
                clanTask.count[4][0] = Integer.parseInt(mc5[0]);
                clanTask.count[4][1] = Integer.parseInt(mc5[1]);
                CLAN_TASKS_TEMPLATE.add(clanTask);
            }
        }
        Logger.success("Successfully loaded clan task (" + CLAN_TASKS_TEMPLATE.size() + ")\n");
    }

    private void loadAchievements(Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("select * from achievement_template");
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ACHIEVEMENT_TEMPLATE.add(new AchievementTemplate(rs.getString("info1"), rs.getString("info2"),
                        rs.getInt("money"), rs.getLong("max_count")));
            }
        }
        Logger.success("Successfully loaded achievement (" + ACHIEVEMENT_TEMPLATE.size() + ")\n");
    }

    private void loadItemTemplates(Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("select * from item_template");
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ItemTemplate itemTemp = new ItemTemplate();
                itemTemp.id = rs.getShort("id");
                itemTemp.type = rs.getByte("type");
                itemTemp.gender = rs.getByte("gender");
                itemTemp.name = rs.getString("name");
                itemTemp.description = rs.getString("description");
                itemTemp.level = rs.getByte("level");
                itemTemp.iconID = rs.getShort("icon_id");
                itemTemp.part = rs.getShort("part");
                itemTemp.isUpToUp = rs.getBoolean("is_up_to_up");
                itemTemp.strRequire = rs.getInt("power_require");
                itemTemp.gold = rs.getInt("gold");
                itemTemp.gem = rs.getInt("gem");
                itemTemp.head = rs.getInt("head");
                itemTemp.body = rs.getInt("body");
                itemTemp.leg = rs.getInt("leg");
                ITEM_TEMPLATES.add(itemTemp);
            }
        }
        Logger.success("Successfully loaded map item template (" + ITEM_TEMPLATES.size() + ")\n");
    }

    private void loadNotifies(Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("select * from notify order by id desc");
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                NOTIFY.add(rs.getString("name") + "<>" + rs.getString("text"));
            }
        }
        Logger.success("Successfully loaded notify (" + NOTIFY.size() + ")\n");
    }

    private void loadImagesByName(Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("select name, n_frame from img_by_name");
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                IMAGES_BY_NAME.put(rs.getString("name"), rs.getByte("n_frame"));
            }
        }
        Logger.success("Successfully loaded images by name (" + IMAGES_BY_NAME.size() + ")\n");
    }

    @SuppressWarnings("unchecked")
    private void loadMount() {
        for (ItemTemplate item : ITEM_TEMPLATES) {
            if (item.type == 23 && getNFrameImageByName("mount_" + item.part + "_0") != 0) {
                DataGame.MAP_MOUNT_NUM.put(item.id, (short) (item.part + 30000));
            }
        }
        Logger.success("Successfully loaded mount (" + DataGame.MAP_MOUNT_NUM.size() + ")\n");
    }

    private void loadMobTemplates(Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("select * from mob_template");
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                MobTemplate mobTemp = new MobTemplate();
                mobTemp.id = rs.getByte("id");
                mobTemp.type = rs.getByte("type");
                mobTemp.name = rs.getString("name");
                mobTemp.hp = rs.getInt("hp");
                mobTemp.rangeMove = rs.getByte("range_move");
                mobTemp.speed = rs.getByte("speed");
                mobTemp.dartType = rs.getByte("dart_type");
                mobTemp.percentDame = rs.getByte("percent_dame");
                mobTemp.percentTiemNang = rs.getByte("percent_tiem_nang");
                MOB_TEMPLATES.add(mobTemp);
            }
        }
        Logger.success("Successfully loaded mob template (" + MOB_TEMPLATES.size() + ")\n");
    }

    private void loadNpcTemplates(Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("select * from npc_template");
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                NpcTemplate npcTemp = new NpcTemplate();
                npcTemp.id = rs.getByte("id");
                npcTemp.name = rs.getString("name");
                npcTemp.head = rs.getShort("head");
                npcTemp.body = rs.getShort("body");
                npcTemp.leg = rs.getShort("leg");
                npcTemp.avatar = rs.getInt("avatar");
                NPC_TEMPLATES.add(npcTemp);
            }
        }
        Logger.success("Successfully loaded npc template (" + NPC_TEMPLATES.size() + ")\n");
    }

    private void loadMapTemplates(Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("select count(id) from map_template");
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                int countRow = rs.getShort(1);
                MAP_TEMPLATES = new MapTemplate[countRow];
            }
        }
        try (PreparedStatement ps = con.prepareStatement("select * from map_template");
                ResultSet rs = ps.executeQuery()) {
            short i = 0;
            while (rs.next()) {
                MapTemplate mapTemplate = new MapTemplate();
                int mapId = rs.getInt("id");
                mapTemplate.id = mapId;
                mapTemplate.name = rs.getString("name");
                mapTemplate.type = rs.getByte("type");
                mapTemplate.planetId = rs.getByte("planet_id");
                mapTemplate.bgType = rs.getByte("bg_type");
                mapTemplate.tileId = rs.getByte("tile_id");
                mapTemplate.bgId = rs.getByte("bg_id");
                mapTemplate.zones = rs.getByte("zones");
                mapTemplate.maxPlayerPerZone = rs.getByte("max_player");
                JSONArray dataArray = (JSONArray) JSONValue.parse(rs.getString("waypoints")
                        .replaceAll("\\[\"\\[", "[[")
                        .replaceAll("\\]\"\\]", "]]")
                        .replaceAll("\",\"", ","));
                for (Object obj : dataArray) {
                    JSONArray dtwp = (JSONArray) JSONValue.parse(String.valueOf(obj));
                    WayPoint wp = new WayPoint();
                    wp.name = String.valueOf(dtwp.get(0));
                    wp.minX = Short.parseShort(String.valueOf(dtwp.get(1)));
                    wp.minY = Short.parseShort(String.valueOf(dtwp.get(2)));
                    wp.maxX = Short.parseShort(String.valueOf(dtwp.get(3)));
                    wp.maxY = Short.parseShort(String.valueOf(dtwp.get(4)));
                    wp.isEnter = Byte.parseByte(String.valueOf(dtwp.get(5))) == 1;
                    wp.isOffline = Byte.parseByte(String.valueOf(dtwp.get(6))) == 1;
                    wp.goMap = Short.parseShort(String.valueOf(dtwp.get(7)));
                    wp.goX = Short.parseShort(String.valueOf(dtwp.get(8)));
                    wp.goY = Short.parseShort(String.valueOf(dtwp.get(9)));
                    mapTemplate.wayPoints.add(wp);
                    dtwp.clear();
                }
                dataArray.clear();
                dataArray = (JSONArray) JSONValue.parse(rs.getString("mobs").replaceAll("\\\"", ""));
                mapTemplate.mobTemp = new byte[dataArray.size()];
                mapTemplate.mobLevel = new byte[dataArray.size()];
                mapTemplate.mobHp = new int[dataArray.size()];
                mapTemplate.mobX = new short[dataArray.size()];
                mapTemplate.mobY = new short[dataArray.size()];
                for (int j = 0; j < dataArray.size(); j++) {
                    JSONArray dtm = (JSONArray) JSONValue.parse(String.valueOf(dataArray.get(j)));
                    mapTemplate.mobTemp[j] = Byte.parseByte(String.valueOf(dtm.get(0)));
                    mapTemplate.mobLevel[j] = Byte.parseByte(String.valueOf(dtm.get(1)));
                    mapTemplate.mobHp[j] = Integer.parseInt(String.valueOf(dtm.get(2)));
                    mapTemplate.mobX[j] = Short.parseShort(String.valueOf(dtm.get(3)));
                    mapTemplate.mobY[j] = Short.parseShort(String.valueOf(dtm.get(4)));
                    dtm.clear();
                }
                dataArray.clear();
                dataArray = (JSONArray) JSONValue.parse(rs.getString("npcs").replaceAll("\\\"", ""));
                mapTemplate.npcId = new byte[dataArray.size()];
                mapTemplate.npcX = new short[dataArray.size()];
                mapTemplate.npcY = new short[dataArray.size()];
                for (int j = 0; j < dataArray.size(); j++) {
                    JSONArray dtn = (JSONArray) JSONValue.parse(String.valueOf(dataArray.get(j)));
                    mapTemplate.npcId[j] = Byte.parseByte(String.valueOf(dtn.get(0)));
                    mapTemplate.npcX[j] = Short.parseShort(String.valueOf(dtn.get(1)));
                    mapTemplate.npcY[j] = Short.parseShort(String.valueOf(dtn.get(2)));
                    dtn.clear();
                }
                dataArray.clear();
                MAP_TEMPLATES[i++] = mapTemplate;
            }
            Logger.success("Successfully loaded map template (" + MAP_TEMPLATES.length + ")\n");
        }
    }

    private void loadRadarTemplates(Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("select * from radar"); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                RadarCard rd = new RadarCard();
                rd.Id = rs.getShort("id");
                rd.IconId = rs.getShort("iconId");
                rd.Rank = rs.getByte("rank");
                rd.Max = rs.getByte("max");
                rd.Type = rs.getByte("type");
                rd.Template = rs.getShort("mob_id");
                rd.Name = rs.getString("name");
                rd.Info = rs.getString("info");
                JSONArray arr = (JSONArray) JSONValue.parse(rs.getString("body"));
                for (Object obj : arr) {
                    JSONObject ob = (JSONObject) obj;
                    if (ob != null) {
                        rd.Head = Short.parseShort(ob.get("head").toString());
                        rd.Body = Short.parseShort(ob.get("body").toString());
                        rd.Leg = Short.parseShort(ob.get("leg").toString());
                        rd.Bag = Short.parseShort(ob.get("bag").toString());
                    }
                }
                rd.Options.clear();
                arr = (JSONArray) JSONValue.parse(rs.getString("options"));
                for (Object obj : arr) {
                    JSONObject ob = (JSONObject) obj;
                    if (ob != null) {
                        rd.Options.add(new OptionCard(
                                Integer.parseInt(ob.get("id").toString()),
                                Short.parseShort(ob.get("param").toString()),
                                Byte.parseByte(ob.get("activeCard").toString())));
                    }
                }
                rd.AuraId = rs.getShort("aura_id");
                RadarService.gI().RADAR_TEMPLATE.add(rd);
            }
        }
        Logger.success("Successfully loaded radar template (" + RadarService.gI().RADAR_TEMPLATE.size() + ")\n");
    }

    private void loadBadgesTemplates(Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("select * from data_badges");
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                BagesTemplate template = new BagesTemplate();
                template.id = rs.getInt("id");
                template.idEffect = rs.getInt("idEffect");
                template.idItem = rs.getInt("idItem");
                template.NAME = rs.getString("NAME");
                JSONArray option = (JSONArray) JSONValue.parse(rs.getString("Options"));
                if (option != null) {
                    for (Object obj : option) {
                        JSONObject jsonobject = (JSONObject) obj;
                        int optionId = Integer.parseInt(jsonobject.get("id").toString());
                        int param = Integer.parseInt(jsonobject.get("param").toString());
                        template.options.add(new ItemOption(optionId, param));
                    }
                }
                BAGES_TEMPLATES.add(template);
            }
        }
        Logger.success("Successfully loaded badges template (" + BAGES_TEMPLATES.size() + ")\n");
    }

    // Hàm đọc danh sách index tile theo tileTypeFocus từ file tile_set_info.
    private int[][] readTileIndexTileType(int tileTypeFocus) {
        int[][] tileIndexTileType = null;
        try (DataInputStream dis = new DataInputStream(new FileInputStream("data/map/tile_set_info"))) {
            int numTileMap = dis.readByte();
            tileIndexTileType = new int[numTileMap][];
            for (int i = 0; i < numTileMap; i++) {
                int numTileOfMap = dis.readByte();
                for (int j = 0; j < numTileOfMap; j++) {
                    int tileType = dis.readInt();
                    int numIndex = dis.readByte();
                    if (tileType == tileTypeFocus) {
                        tileIndexTileType[i] = new int[numIndex];
                    }
                    for (int k = 0; k < numIndex; k++) {
                        int typeIndex = dis.readByte();
                        if (tileType == tileTypeFocus) {
                            tileIndexTileType[i][k] = typeIndex;
                        }
                    }
                }
            }
        } catch (IOException e) {
            Logger.logException(Manager.class, e);
        }
        return tileIndexTileType;
    }

    // Hàm đọc tile map từ file tương ứng với mapId.
    private int[][] readTileMap(int mapId) {
        int[][] tileMap = null;
        try {
            try (DataInputStream dis = new DataInputStream(new FileInputStream("data/map/tile_map_data/" + mapId))) {
                int w = dis.readByte();
                int h = dis.readByte();
                tileMap = new int[h][w];
                for (int[] tm : tileMap) {
                    for (int j = 0; j < tm.length; j++) {
                        tm[j] = dis.readByte();
                    }
                }
            }
        } catch (IOException e) {
        }
        return tileMap;
    }

    // -----------------------------------
    // Các hàm xử lý menu Top, các hàm tiện ích khác…
    // -----------------------------------
    public static Clan getClanById(int id) throws Exception {
        for (Clan clan : CLANS) {
            if (clan.id == id) {
                return clan;
            }
        }
        throw new Exception("Không tìm thấy clan id: " + id);
    }

    public static void addClan(Clan clan) {
        CLANS.add(clan);
    }

    public static int getNumClan() {
        return CLANS.size();

    }

    public static MobTemplate getMobTemplateByTemp(int mobTempId) {
        for (MobTemplate mobTemp : MOB_TEMPLATES) {
            if (mobTemp.id == mobTempId) {
                return mobTemp;
            }
        }
        return null;
    }

    public static byte getNFrameImageByName(String name) {
        Object n = IMAGES_BY_NAME.get(name);
        if (n != null) {
            return Byte.parseByte(String.valueOf(n));
        } else {
            return 0;
        }
    }

    // Hàm xử lý thời gian sự kiện top
    public static Timestamp timeSuKienDuaTop = Timestamp.valueOf("2024-06-10 23:59:59");
    public static String timeStartDuaTop = "10h ngày 25/5/2024";
    public static String timeEndDuaTop = "23h59 ngày 10/6/2024";

    public static String demTimeSuKien() {
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime eventTime = timeSuKienDuaTop.toLocalDateTime();
        long daysRemaining = ChronoUnit.DAYS.between(currentTime, eventTime);
        return (daysRemaining > 0) ? "(" + daysRemaining + " ngày nữa)" : "(Đã kết thúc)";
    }

    // Updateshop
    public void updateShop() {
        try (Connection con = DBConnecter.getConnectionServer()) {
            SHOPS = ShopDAO.getShops(con);
            Logger.success("Successfully updated shops (" + SHOPS.size() + ")");
        } catch (Exception ex) {
            Logger.logException(Manager.class, ex, "Error updating shop");
        }
    }
    // End xử lý menu top
}
