/*    */ package network.io;

/*    */

 /*    */ import java.io.DataOutputStream;
/*    */ import java.net.Socket;
/*    */ import java.util.ArrayList;
import network.handler.IMessageSendCollect;
import network.session.ISession;

/*    */
 /*    */
 /*    */
 /*    */
 /*    */
 /*    */
 /*    */
 /*    */
 /*    */ public class Sender
        /*    */ implements Runnable /*    */ {

    /*    */ private ISession session;
    /*    */    private ArrayList<Message> messages;
    /*    */    private DataOutputStream dos;
    /*    */    private IMessageSendCollect sendCollect;

    /*    */
 /*    */ public Sender(ISession session, Socket socket) {
        /*    */ try {
            /* 26 */ this.session = session;
            /* 27 */ this.messages = new ArrayList<>();
            /* 28 */ setSocket(socket);
            /* 29 */        } catch (Exception exception) {
        }
        /*    */    }

    /*    */
 /*    */
 /*    */ public Sender setSocket(Socket socket) {
        /*    */ try {
            /* 35 */ this.dos = new DataOutputStream(socket.getOutputStream());
            /* 36 */        } catch (Exception exception) {
        }
        /*    */
 /* 38 */ return this;
        /*    */    }

    /*    */
 /*    */
 /*    */
 /*    */
    public void run() {
        /* 44 */ while (this.session != null && this.session.isConnected()) {
            /*    */ try {
                /* 46 */ while (this.session != null && this.session != null && this.messages.size() > 0) {
                    /* 47 */ Message message = this.messages.remove(0);
                    /* 48 */ if (message != null) {
                        /* 49 */ doSendMessage(message);
                        /*    */                    }
                    /* 51 */ message = null;
                    /*    */                }
                /* 53 */ Thread.sleep(1L);
                /* 54 */            } catch (Exception e) {
                /* 55 */ e.printStackTrace();
                /*    */            }
            /*    */        }
        /*    */    }

    /*    */
 /*    */ public synchronized void doSendMessage(Message message) throws Exception {
        /* 61 */ this.sendCollect.doSendMessage(this.session, this.dos, message);
        /*    */    }

    /*    */
 /*    */ public synchronized void sendMessage(Message msg) {
        /* 65 */ if (this.session != null && this.session.isConnected()) {
            /* 66 */ this.messages.add(msg);
            /*    */        }
        /*    */    }

    /*    */
 /*    */ public void setSend(IMessageSendCollect sendCollect) {
        /* 71 */ this.sendCollect = sendCollect;
        /*    */    }

    /*    */
 /*    */ public int getNumMessages() {
        /* 75 */ if (this.messages != null) {
            /* 76 */ return this.messages.size();
            /*    */        }
        /* 78 */ return -1;
        /*    */    }

    /*    */
 /*    */ public void close() {
        /* 82 */ if (this.messages != null) {
            /* 83 */ this.messages.clear();
            /*    */        }
        /* 85 */ if (this.dos != null) {
            /*    */ try {
                /* 87 */ this.dos.close();
                /* 88 */            } catch (Exception exception) {
            }
            /*    */        }
        /*    */    }

    /*    */
 /*    */
 /*    */ public void dispose() {
        /* 94 */ this.session = null;
        /* 95 */ this.messages = null;
        /* 96 */ this.sendCollect = null;
        /* 97 */ this.dos = null;
        /*    */    }
    /*    */ }


/* Location:              C:\Users\VoHoangKiet\Downloads\TEA_V5\lib\GirlkunNetwork.jar!\com\girlkun\network\io\Sender.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
