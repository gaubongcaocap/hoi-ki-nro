/*     */ 
package network.server;

/*     */
/*     */ import network.session.ISession;
/*     */ import network.session.Session;
/*     */ import network.session.SessionFactory;
/*     */ import java.io.IOException;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.Socket;
import java.util.HashMap;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;

/*     */
/*     */
/*     */ public class EMTIServer
        /*     */ implements InEMTIServer /*     */ {

    /*     */ private static EMTIServer I;
    /*     */ private int port;
    /*     */ private ServerSocket serverListen;
    /*     */ @SuppressWarnings("rawtypes")
    private Class sessionClone;

    /*     */
    /*     */ public static EMTIServer gI() {
        /* 22 */ if (I == null) {
            /* 23 */ I = new EMTIServer();
            /*     */ }
        /* 25 */ return I;
        /*     */ }

    /*     */ private boolean start;
    private boolean randomKey;
    private IServerClose serverClose;
    /*     */ private ISessionAcceptHandler acceptHandler;
    /*     */ private Thread loopServer;

    /*     */
    /*     */ private EMTIServer() {
        /* 32 */ this.port = -1;
        /*     */ this.sessionClone = Session.class;
        /*     */ }
    /*     */
    /*     */
 /*     */ public static HashMap<String, Integer> firewall = new HashMap<>();
    public static HashMap<String, Integer> firewallDownDataGame = new HashMap<>();

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */ public InEMTIServer init() {
        /* 44 */ this.loopServer = new Thread(this);
        /* 45 */ return this;
        /*     */ }

    /*     */
    /*     */
    /*     */ public InEMTIServer start(int port) throws Exception {
        /* 50 */ if (port < 0) {
            /* 51 */ throw new Exception("Vui lòng khởi tạo port server!");
            /*     */ }
        /* 53 */ if (this.acceptHandler == null) {
            /* 54 */ throw new Exception("AcceptHandler chưa được khởi tạo!");
            /*     */ }
        /* 56 */ if (!ISession.class.isAssignableFrom(this.sessionClone)) {
            /* 57 */ throw new Exception("Type session clone không hợp lệ!");
            /*     */ }
        /*     */ try {
            /* 60 */ this.port = port;
            /* 61 */ this.serverListen = new ServerSocket(port);
            /* 62 */ } catch (IOException ex) {
            /* 63 */ System.out.println("Lỗi khởi tạo server tại port " + port);
            /* 64 */ System.exit(0);
            /*     */ }
        /* 66 */ this.start = true;
        /* 67 */ this.loopServer.start();
        /* 68 */ System.out.println("Server Girlkun đang chạy tại port " + this.port);
        /* 69 */ return this;
        /*     */ }

    /*     */
    /*     */
    /*     */ public InEMTIServer close() {
        /* 74 */ this.start = false;
        /* 75 */ if (this.serverListen != null) {
            /*     */ try {
                /* 77 */ this.serverListen.close();
                /* 78 */ } catch (IOException ex) {
                /* 79 */ ex.printStackTrace();
                /*     */ }
            /*     */ }
        /* 82 */ if (this.serverClose != null) {
            /* 83 */ this.serverClose.serverClose();
            /*     */ }
        /* 85 */ System.out.println("Server Girlkun đã đóng!");
        /* 86 */ return this;
        /*     */ }

    /*     */
    /*     */
    /*     */ public InEMTIServer dispose() {
        /* 91 */ this.acceptHandler = null;
        /* 92 */ this.loopServer = null;
        /* 93 */ this.serverListen = null;
        /* 94 */ return this;
        /*     */ }

    /*     */
    /*     */
    /*     */ public InEMTIServer setAcceptHandler(ISessionAcceptHandler handler) {
        /* 99 */ this.acceptHandler = handler;
        /* 100 */ return this;
        /*     */ }

    /*     */
    /*     */
    // @Override
    // public void run() {
    // while (start) {
    // try {
    // selector.select();
    // for (SelectionKey key : selector.selectedKeys()) {
    // if (key.isAcceptable()) {
    // ServerSocketChannel server = (ServerSocketChannel) key.channel();
    // Socket socket = server.accept().socket();
    // final ISession session = SessionFactory.gI().cloneSession(this.sessionClone,
    // socket);
    // this.acceptHandler.sessionInit(session);
    // SessionManager.gI().putSession(session);
    // }
    // }
    // selector.selectedKeys().clear();
    // } catch (IOException ex) {
    // } catch (Exception ex2) {
    // Logger.errorln(ex2.toString());
    // }
    // }
    // }

    @SuppressWarnings("unchecked")
    public void run() {
        while (this.start) {
            try {
                Socket socket = this.serverListen.accept();
                // String ip = socket.getInetAddress().getHostAddress();
                ISession session = SessionFactory.gI().cloneSession(this.sessionClone, socket);
                this.acceptHandler.sessionInit(session);
                EmtiSessionManager.gI().putSession(session);

            } catch (Exception ex) {
                Logger.getLogger(EMTIServer.class.getName()).log(Level.SEVERE, (String) null, ex);
            }
        }
    }
    // /* */ public void run() {
    // /* 105 */ while (this.start) {
    // /* */ try {
    // /* 107 */ Socket socket = this.serverListen.accept();
    // /* 108 */ ISession session =
    // SessionFactory.gI().cloneSession(this.sessionClone, socket);
    // /* 109 */ this.acceptHandler.sessionInit(session);
    // /* 110 */ GirlkunSessionManager.gI().putSession(session);
    // /* 111 */ } catch (IOException ex) {
    // /* 112 */ ex.printStackTrace();
    // /* 113 */ } catch (Exception ex) {
    // /* 114 */ Logger.getLogger(EMTIServer.class.getName()).log(Level.SEVERE,
    // (String) null, ex);
    // /* */ }
    // /* */ }
    // /* */ }

    /*     */
    /*     */ public InEMTIServer setDoSomeThingWhenClose(IServerClose serverClose) {
        /* 121 */ this.serverClose = serverClose;
        /* 122 */ return this;
        /*     */ }

    /*     */
    /*     */
    /*     */ public InEMTIServer randomKey(boolean isRandom) {
        /* 127 */ this.randomKey = isRandom;
        /* 128 */ return this;
        /*     */ }

    /*     */
    /*     */
    /*     */ public boolean isRandomKey() {
        /* 133 */ return this.randomKey;
        /*     */ }

    /*     */
    /*     */
    /*     */ public InEMTIServer setTypeSessioClone(@SuppressWarnings("rawtypes") Class clazz) throws Exception {
        /* 138 */ this.sessionClone = clazz;
        /* 139 */ return this;
        /*     */ }

    /*     */
    /*     */
    /*     */ public ISessionAcceptHandler getAcceptHandler() throws Exception {
        /* 144 */ if (this.acceptHandler == null) {
            /* 145 */ throw new Exception("AcceptHandler chưa được khởi tạo!");
            /*     */ }
        /* 147 */ return this.acceptHandler;
        /*     */ }

    /*     */
    /*     */
    /*     */ public void stopConnect() {
        /* 152 */ this.start = false;
        /*     */ }
    /*     */ }

/*
 * Location:
 * C:\Users\VoHoangKiet\Downloads\TEA_V5\lib\GirlkunNetwork.jar!\com\girlkun\
 * network\server\EMTIServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version: 1.1.3
 */
