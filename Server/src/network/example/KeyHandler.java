/*    */ package network.example;

/*    */

import network.CommandMessage;
import network.handler.IKeySessionHandler;
import network.io.Message;
import network.session.ISession;


/*    */
 /*    */
 /*    */
 /*    */
 /*    */
 /*    */
 /*    */ public class KeyHandler
        /*    */ implements IKeySessionHandler /*    */ {

    /*    */ public void sendKey(ISession session) {
        /* 17 */ Message msg = new Message(CommandMessage.REQUEST_KEY);
        /*    */ try {
            /* 19 */ byte[] KEYS = session.getKey();
            /* 20 */ msg.writer().writeByte(KEYS.length);
            /* 21 */ msg.writer().writeByte(KEYS[0]);
            /* 22 */ for (int i = 1; i < KEYS.length; i++) {
                /* 23 */ msg.writer().writeByte(KEYS[i] ^ KEYS[i - 1]);
                /*    */            }
            /* 25 */ session.doSendMessage(msg);
            /* 26 */ msg.cleanup();
            /* 27 */ session.setSentKey(true);
            /* 28 */        } catch (Exception exception) {
        }
        /*    */    }

    /*    */
 /*    */
 /*    */
 /*    */ public void setKey(ISession session, Message message) throws Exception {
        /*    */ try {
            /* 35 */ byte b = message.reader().readByte();
            /* 36 */ byte[] KEYS = new byte[b];
            /* 37 */ for (int i = 0; i < b; i++) {
                /* 38 */ KEYS[i] = message.reader().readByte();
                /*    */            }
            /* 40 */ for (int j = 0; j < KEYS.length - 1; j++) {
                /* 41 */ KEYS[j + 1] = (byte) (KEYS[j + 1] ^ KEYS[j]);
                /*    */            }
            /* 43 */ session.setKey(KEYS);
            /* 44 */ session.setSentKey(true);
            /* 45 */        } catch (Exception exception) {
        }
        /*    */    }
    /*    */ }


/* Location:              C:\Users\VoHoangKiet\Downloads\TEA_V5\lib\GirlkunNetwork.jar!\com\girlkun\network\example\KeyHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
