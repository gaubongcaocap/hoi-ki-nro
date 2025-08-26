package network.session;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import network.handler.IKeySessionHandler;
import network.handler.IMessageHandler;
import network.handler.IMessageSendCollect;
import network.io.Collector;
import network.io.Message;
import network.io.Sender;
import network.server.EMTIServer;
import network.server.EmtiSessionManager;
import utils.StringUtil;

public abstract class Session
        implements ISession {

    private static ISession I;
    private static int ID_INIT;
    public TypeSession typeSession;
    public byte timeWait = 50;

    public static ISession gI() throws Exception {
        /*  26 */
        if (I == null) {
            /*  27 */
            throw new Exception("Instance chưa được khởi tạo!");
        }
        /*  29 */
        return I;
    }

    public static ISession initInstance(String host, int port) throws Exception {
        /*  33 */
        if (I != null) {
            /*  34 */
            throw new Exception("Instance đã được khởi tạo!");
        }
        /*  36 */
        I = new Session(host, port) {
            @Override
            public void sendKey() throws Exception {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }
        };
        /*  37 */
        return I;
    }


    /*  44 */    private byte[] KEYS = "Girlkun75".getBytes();

    private boolean sentKey;

    public int id;

    private Socket socket;

    private boolean connected;

    private boolean reconnect;

    private Sender sender;

    private Collector collector;

    private Thread tSender;

    private Thread tCollector;

    private IKeySessionHandler keyHandler;

    private String ip;

    private String host;

    private int port;

    public Session(String host, int port) throws IOException {
        /*  73 */
        this.id = 752002;
        /*  74 */
        this.socket = new Socket(host, port);
        /*  75 */
        this.socket.setSendBufferSize(1048576);
        /*  76 */
        this.socket.setReceiveBufferSize(1048576);
        /*  77 */
        this.typeSession = TypeSession.CLIENT;
        /*  78 */
        this.connected = true;
        /*  79 */
        this.host = host;
        /*  80 */
        this.port = port;
        /*  81 */
        initThreadSession();
    }

    public Session(Socket socket) {
        /*  91 */
        this.id = ID_INIT++;
        /*  92 */
        this.typeSession = TypeSession.SERVER;
        /*  93 */
        this.socket = socket;
        try {
            /*  95 */
            this.socket.setSendBufferSize(1048576);
            /*  96 */
            this.socket.setReceiveBufferSize(1048576);
            /*  97 */
        } catch (Exception exception) {
            exception.printStackTrace();
        }


        /* 100 */
        this.connected = true;
        /* 101 */
        this.ip = ((InetSocketAddress) socket.getRemoteSocketAddress()).getAddress().toString().replace("/", "");
        /* 102 */
        initThreadSession();
    }

    public void sendMessage(Message msg) {
        /* 107 */
        if (this.sender != null && isConnected() && this.sender.getNumMessages() < 1000) {
            /* 108 */
            this.sender.sendMessage(msg);
        }
    }

    public ISession setSendCollect(IMessageSendCollect collect) {
        /* 114 */
        this.sender.setSend(collect);
        /* 115 */
        this.collector.setCollect(collect);
        /* 116 */
        return this;
    }

    public ISession setMessageHandler(IMessageHandler handler) {
        /* 121 */
        this.collector.setMessageHandler(handler);
        /* 122 */
        return this;
    }

    public ISession setKeyHandler(IKeySessionHandler handler) {
        /* 127 */
        this.keyHandler = handler;
        /* 128 */
        return this;
    }

    public ISession startSend() {
        /* 133 */

        this.tSender.start();
        /* 134 */
        return this;
    }

    public ISession startCollect() {
        /* 139 */
        this.tCollector.start();
        /* 140 */
        return this;
    }

    public String getIP() {
        /* 145 */
        return this.ip;
    }

    public long getID() {
        /* 150 */
        return this.id;
    }

    public void disconnect() {
        /* 155 */
        this.connected = false;
        /* 156 */
        this.sentKey = false;
        /* 157 */
        if (this.sender != null) {
            /* 158 */
            this.sender.close();
        }
        /* 160 */
        if (this.collector != null) {
            /* 161 */
            this.collector.close();
        }
        /* 163 */
//        if (this.socket != null) {
//            try {
//                /* 165 */
//                this.socket.close();
//                /* 166 */
//            } catch (IOException iOException) {
//            }
//        }
        if (this.socket != null) {
            try {
                // String ip = socket.getInetAddress().getHostAddress();
//                if (EMTIServer.firewall.containsKey(ip)) {
//                    int count = EMTIServer.firewall.get(ip);
//                    if (count > 0) {
//                        EMTIServer.firewall.put(ip, count - 1);
//                    } else {
//                        EMTIServer.firewall.remove(ip);
//                    }
//                }

                this.socket.close();
            } catch (IOException ex) {ex.printStackTrace();
                Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        /* 169 */
        if (this.reconnect) {
            /* 170 */
            reconnect();
            return;
        }
//        this.socket = null;
//        this.sender = null;
//        this.collector = null;

        /* 173 */
        dispose();
    }

    public void dispose() {
        /* 178 */
        if (this.sender != null) {
            /* 179 */
            this.sender.dispose();
        }
        /* 181 */
        if (this.collector != null) {
            /* 182 */
            this.collector.dispose();
        }
        /* 184 */
        this.socket = null;
        /* 185 */
        this.sender = null;
        /* 186 */
        this.collector = null;
        /* 187 */
        this.tSender = null;
        /* 188 */
        this.tCollector = null;
        /* 189 */
        this.ip = null;
        /* 190 */
        EmtiSessionManager.gI().removeSession(this);
    }

    public void setKey(Message message) throws Exception {
        /* 206 */
        if (this.keyHandler == null) {
            /* 207 */
            throw new Exception("Key handler chưa được khởi tạo!");
        }
        /* 209 */
        this.keyHandler.setKey(this, message);
    }

    public void setKey(byte[] key) {
        /* 214 */
        this.KEYS = key;
    }

    public boolean sentKey() {
        /* 219 */
        return this.sentKey;
    }

    public void setSentKey(boolean sent) {
        /* 224 */
        this.sentKey = sent;
    }

    public void doSendMessage(Message msg) throws Exception {
        /* 229 */
        this.sender.doSendMessage(msg);
    }

    public ISession start() {
        /* 234 */
        this.tSender.start();
        /* 235 */
        this.tCollector.start();
        /* 236 */
        return this;
    }

    public boolean isConnected() {
        /* 241 */
        return this != null && this.connected;
    }

    public byte[] getKey() {
        /* 246 */
        return this.KEYS;
    }

    public TypeSession getTypeSession() {
        /* 251 */
        return this.typeSession;
    }

    public ISession setReconnect(boolean b) {
        /* 256 */
        this.reconnect = b;
        /* 257 */
        return this;
    }

    public int getNumMessages() {
        /* 262 */
        if (isConnected()) {
            /* 263 */
            return this.sender.getNumMessages();
        }
        /* 265 */
        return -1;
    }

    public void reconnect() {
        /* 270 */
        if (this.typeSession == TypeSession.CLIENT && !isConnected()) {
            try {
                /* 272 */
                this.socket = new Socket(this.host, this.port);
                /* 273 */
                this.connected = true;
                /* 274 */
                initThreadSession();
                /* 275 */
                start();
                /* 276 */
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    /* 278 */
                    Thread.sleep(1000L);
                    /* 279 */
                    reconnect();
                    /* 280 */
                } catch (Exception ex) {
                    /* 281 */
                    ex.printStackTrace();
                }
            }
        }
    }

    public void initThreadSession() {
        Runnable senderRunnable = (this.sender != null)
                ? (Runnable) this.sender.setSocket(this.socket)
                : (Runnable) (this.sender = new Sender(this, this.socket));

        Runnable collectorRunnable = (this.collector != null)
                ? (Runnable) this.collector.setSocket(this.socket)
                : (Runnable) (this.collector = new Collector(this, this.socket));

        this.tSender = Thread.ofVirtual().start(senderRunnable);
        this.tCollector = Thread.ofVirtual().start(collectorRunnable);
    }

}
