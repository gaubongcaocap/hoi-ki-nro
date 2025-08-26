package server;

/*
 *
 *
 * @author Entidi (NTD - Tấn Đạt)
 */
// import Bot.NewBot;
import EMTI.FileRunner;
import EMTI.Functions;
import NTDManger.NTDManager;
import models.Consign.ConsignShopManager;
import models.DeathOrAliveArena.DeathOrAliveArenaManager;
import models.ShenronEvent.ShenronEventManager;
import jdbc.daos.HistoryTransactionDAO;
import boss.AnTromManager;
import boss.BossManager;
import boss.BrolyManager;
import boss.ChristmasEventManager;
import boss.FinalBossManager;
import boss.GasDestroyManager;
import boss.HalloweenEventManager;
import boss.HungVuongEventManager;
import boss.LunarNewYearEventManager;
import boss.OtherBossManager;
import boss.RedRibbonHQManager;
import boss.SkillSummonedManager;
import boss.SnakeWayManager;
import boss.TreasureUnderSeaManager;
import boss.TrungThuEventManager;
import boss.YardartManager;
import consts.ConstDataEventNAP;
import consts.ConstDataEventSM;

import java.io.IOException;

import network.inetwork.ISession;
import network.Network;
import server.io.MyKeyHandler;
import server.io.MySession;
import services.ClanService;
import services.NgocRongNamecService;
import services.PetService;
import utils.Logger;
import utils.TimeUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import Bot.NewBot;
import event.EventManager;
import item.DailyResetScheduler;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import javax.swing.JFrame;
import jdbc.daos.EventDAO;
import map.MapManager;
import minigame.DecisionMaker.DecisionMaker;
import minigame.LuckyNumber.LuckyNumber;
import network.MessageSendCollect;
import models.SuperRank.SuperRankManager;
import models.The23rdMartialArtCongress.The23rdMartialArtCongressManager;
import network.Message;
import network.inetwork.ISessionAcceptHandler;
import panel.PanelManager;

public class ServerManager {

    public static String timeStart;
    // PHẢI CÓ <String, Integer>
    private static final Map<String, Integer> CLIENTS = new ConcurrentHashMap<>();

    public static String NAME = "Local";
    public static String IP = "127.0.0.1";
    public static int PORT = 14445;

    private static ServerManager instance;

    public static boolean isRunning;
    public static String DOMAIN = "http://javhd.pro";

    public void init() {
        Manager.gI();
        HistoryTransactionDAO.deleteHistory();
    }

    public static ServerManager gI() {
        if (instance == null) {
            instance = new ServerManager();
            instance.init();
        }
        return instance;
    }

    public static void main(String[] args) {
        System.setProperty("file.encoding", "UTF-8");
        // panel.PanelManager.gI().openUI();
        // redirectSystemLogsToPanel();
        timeStart = TimeUtil.getTimeNow("dd/MM/yyyy HH:mm:ss");
        ServerManager.gI().run();
    }

    public void run() {
        isRunning = true;
        Logger.success("===== Khởi động máy chủ lúc " + timeStart + " =====\n");
        activeServerSocket();
        System.setProperty("file.encoding", "UTF-8"); // Đảm bảo encoding UTF-8 toàn hệ thống
        activeCommandLine();
        MapManager.loadAllEffMaps();
        Logger.success("Đã tải tất cả bản đồ hiệu ứng\n");
        NTDManager.getInstance().startAutoSave();
        Logger.success("Đã bắt đầu tự động lưu\n");
        startThreads();
        Logger.success("===== Khởi động máy chủ hoàn tất =====\n");
    }

    private void startThreads() {
        Logger.success("Đang khởi chạy các thread dịch vụ...\n");

        Thread.startVirtualThread(() -> NgocRongNamecService.gI());
        Logger.success("Đã khởi chạy thread Update NRNM\n");

        Thread.startVirtualThread(() -> SuperRankManager.gI());
        Logger.success("Đã khởi chạy thread Update Super Rank\n");

        Thread.startVirtualThread(() -> The23rdMartialArtCongressManager.gI());
        Logger.success("Đã khởi chạy thread DHVT23\n");

        Thread.startVirtualThread(() -> AutoMaintenance.gI());
        Logger.success("Đã khởi chạy thread Bảo Trì Tự Động\n");

        Thread.startVirtualThread(() -> ShenronEventManager.gI());
        Logger.success("Đã khởi chạy thread Shenron\n");

        Thread.startVirtualThread(() -> DeathOrAliveArenaManager.gI());
        Logger.success("Đã khởi chạy thread Võ Đài Sinh Tử\n");

        Logger.success("Đang tải boss...\n");
        BossManager.gI().loadBoss();
        Manager.MAPS.forEach(map.Map::initBoss);
        Logger.success("Đã tải xong boss\n");
        EventManager.gI().init();
        Logger.success("Đã khởi tạo sự kiện\n");
        
        Thread.startVirtualThread(() -> BossManager.gI().run());
        Thread.startVirtualThread(() -> YardartManager.gI());
        Thread.startVirtualThread(() -> FinalBossManager.gI());
        Thread.startVirtualThread(() -> SkillSummonedManager.gI());
        Thread.startVirtualThread(() -> BrolyManager.gI());
        Thread.startVirtualThread(() -> AnTromManager.gI());
        Thread.startVirtualThread(() -> OtherBossManager.gI());
        Thread.startVirtualThread(() -> RedRibbonHQManager.gI());
        Thread.startVirtualThread(() -> TreasureUnderSeaManager.gI());
        Thread.startVirtualThread(() -> SnakeWayManager.gI());
        Thread.startVirtualThread(() -> GasDestroyManager.gI());
        Thread.startVirtualThread(() -> GasDestroyManager.gI());
        // Thread.startVirtualThread(() -> TrungThuEventManager.gI());
        // Thread.startVirtualThread(() -> HalloweenEventManager.gI());
        // Thread.startVirtualThread(() -> ChristmasEventManager.gI());
        // Thread.startVirtualThread(() -> ChristmasEventManager.gI());
        // Thread.startVirtualThread(() -> HungVuongEventManager.gI());
        // Thread.startVirtualThread(() -> LunarNewYearEventManager.gI());
        // Thread.startVirtualThread(() -> LuckyNumber.gI());
        Thread.startVirtualThread(() -> DecisionMaker.gI());
        Thread.startVirtualThread(() -> DecisionMaker.gI());

        EventManager.gI().init();
        Logger.success("Đã khởi tạo sự kiện\n");

        DailyResetScheduler.startDailyReset();
        Logger.success("Tải xong Daily Reset\n");

        Thread.startVirtualThread(() -> {
            while (isRunning) {
                try {
                    long st = System.currentTimeMillis();
                    ConstDataEventSM.isRunningSK = ConstDataEventSM.isActiveEvent();
                    ConstDataEventNAP.isRunningSK = ConstDataEventNAP.isActiveEvent();
                    Functions.sleep(Math.max(500 - (System.currentTimeMillis() - st), 10));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //Khởi tạo bot săn boss, bot pem và bot shop. Có thể điều chỉnh số lượng tùy theo cấu hình.
        // Logger.success("Đang tạo bot săn boss\n");
        // NewBot.gI().runBot(2 , null , 50);
        // Logger.success("Tạo thành công bot săn boss\n");

        // Logger.success("Đang tạo bot pem\n");
        // NewBot.gI().runBot(0, null, 100);
        // Logger.success("Tạo thành công bot pem\n");

        Logger.success("Đang tạo bot shop\n");
        // NewBot.gI().runBot(1, null, 100);
        Logger.success("Tạo thành công bot shop\n");

        Logger.success("Đã khởi chạy tất cả các thread dịch vụ\n");
    }

    private void activeServerSocket() {
        try {
            Network.gI().init().setAcceptHandler(new ISessionAcceptHandler() {
                @Override
                public void sessionInit(ISession is) {
                    if (!canConnectWithIp(is.getIP())) {
                        is.disconnect();
                        return;
                    }
                    is.setMessageHandler(Controller.gI())
                            .setSendCollect(new MessageSendCollect() {
                                @Override
                                public void doSendMessage(ISession session, DataOutputStream dos, Message msg)
                                        throws Exception {
                                    try {
                                        byte[] data = msg.getData();
                                        if (session.sentKey()) {
                                            byte b = this.writeKey(session, msg.command);
                                            dos.writeByte(b);
                                        } else {
                                            dos.writeByte(msg.command);
                                        }
                                        if (data != null) {
                                            int size = data.length;
                                            if (msg.command == -32 || msg.command == -66 || msg.command == -74
                                                    || msg.command == 11 || msg.command == -67 || msg.command == -87
                                                    || msg.command == 66) {
                                                byte b2 = this.writeKey(session, (byte) size);
                                                dos.writeByte(b2 - 128);
                                                byte b3 = this.writeKey(session, (byte) (size >> 8));
                                                dos.writeByte(b3 - 128);
                                                byte b4 = this.writeKey(session, (byte) (size >> 16));
                                                dos.writeByte(b4 - 128);
                                            } else if (session.sentKey()) {
                                                byte byte1 = this.writeKey(session, (byte) (size >> 8));
                                                dos.writeByte(byte1);
                                                byte byte2 = this.writeKey(session, (byte) (size & 0xFF));
                                                dos.writeByte(byte2);
                                            } else {
                                                dos.writeShort(size);
                                            }
                                            if (session.sentKey()) {
                                                for (int i = 0; i < data.length; ++i) {
                                                    data[i] = this.writeKey(session, data[i]);
                                                }
                                            }
                                            dos.write(data);
                                        } else {
                                            dos.writeShort(0);
                                        }
                                        dos.flush();
                                        msg.cleanup();
                                    } catch (IOException iOException) {
                                        // empty catch block
                                    }
                                }
                            })
                            .setKeyHandler(new MyKeyHandler())
                            .startCollect();
                }

                @Override
                public void sessionDisconnect(ISession session) {
                    Client.gI().kickSession((MySession) session);
                }
            }).setTypeSessioClone(MySession.class)
                    .setDoSomeThingWhenClose(() -> {
                        Logger.error("SERVER CLOSE\n");
                        System.exit(0);
                    })
                    .start(PORT);
        } catch (Exception e) {
        }
    }

    private boolean canConnectWithIp(String ipAddress) {
        Object o = CLIENTS.get(ipAddress);
        if (o == null) {
            CLIENTS.put(ipAddress, 1);
            return true;
        } else {
            int n = Integer.parseInt(String.valueOf(o));
            if (n < Manager.MAX_PER_IP) {
                n++;
                CLIENTS.put(ipAddress, n);
                return true;
            } else {
                return false;
            }
        }
    }

    private void activeCommandLine() {
        if (System.console() == null) {
            System.out.println("Không có console, bỏ qua chế độ dòng lệnh.");
            return;
        }

        Thread.startVirtualThread(() -> {
            try (Scanner sc = new Scanner(System.in)) {
                while (true) {
                    try {
                        String line = sc.nextLine();
                        switch (line) {
                            case "baotri":
                                new Thread(() -> Maintenance.gI().start(5)).start();
                                break;
                            case "athread":
                                System.out.println("Số thread hiện tại của Server : " + Thread.activeCount());
                                break;
                            case "nplayer":
                                System.out.println(
                                        "Số lượng người chơi hiện tại của Server : " + Client.gI().getPlayers().size());
                                break;
                            case "shop":
                                Manager.gI().updateShop();
                                System.out.println(
                                        "===========================DONE UPDATE SHOP===========================");
                                break;
                            case "a":
                                new Thread(() -> Client.gI().close()).start();
                                break;
                            default:
                                System.out.println("Lệnh không hợp lệ: " + line);
                        }
                    } catch (NoSuchElementException e) {
                        System.out.println("Không thể đọc từ dòng lệnh, kết thúc luồng command line.");
                        break;
                    }
                }
            }
        });
    }

    // Trong ServerManager.java
    // Đảm bảo bạn đã khai báo CLIENTS ở trên:
    // private static final Map<String, Integer> CLIENTS = new
    // ConcurrentHashMap<>();

    // Trong ServerManager.java
    // Đảm bảo bạn đã khai báo CLIENTS ở trên:
    // private static final Map<String, Integer> CLIENTS = new
    // ConcurrentHashMap<>();

    public void disconnect(String ipAddress) {
        if (ipAddress == null || ipAddress.isEmpty()) {
            return;
        }
        CLIENTS.computeIfPresent(ipAddress, (key, value) -> {
            // SỬA Ở ĐÂY: Chuyển Integer 'value' thành int trước khi trừ
            int newValue = value.intValue() - 1;
            if (newValue <= 0) {
                return null; // Xóa entry
            } else {
                return newValue; // Cập nhật giá trị mới
            }
        });
    }

    public void close() {
        isRunning = false;
        try {
            ClanService.gI().close();
        } catch (Exception e) {
            Logger.error("Lỗi save clan!\n");
        }
        try {
            ConsignShopManager.gI().save();
        } catch (Exception e) {
            Logger.error("Lỗi save shop ký gửi!\n");
        }
        Client.gI().close();
        EventDAO.save();
        Logger.success("SUCCESSFULLY MAINTENANCE!\n");

        if (AutoMaintenance.isRunning) {
            AutoMaintenance.isRunning = false;
            Thread.startVirtualThread(() -> {
                try {
                    Thread.sleep(60000);
                    String batchFilePath = "run.bat";
                    FileRunner.runBatchFile(batchFilePath); // Gọi batch file để chạy lại ứng dụng
                } catch (IOException e) {
                    Logger.error("Lỗi khi chạy lại ứng dụng!\n");
                } catch (InterruptedException e) {
                    Logger.error("Thread bị gián đoạn khi chờ khởi động lại!\n");
                }
            });
        }

        System.exit(0);
    }

    private static void redirectSystemLogsToPanel() {
        OutputStream out = new OutputStream() {
            private StringBuilder buffer = new StringBuilder();

            @Override
            public void write(int b) {
                if (b == '\n') {
                    PanelManager.gI().log(buffer.toString());
                    buffer.setLength(0);
                } else {
                    buffer.append((char) b);
                }
            }
        };

        PrintStream panelStream = new PrintStream(out, true);
        System.setOut(panelStream);
        System.setErr(panelStream);
    }
}
