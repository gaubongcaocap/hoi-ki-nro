/*    */ package network.example;

/*    */

 /*    */ import java.io.DataInputStream;
/*    */ import java.io.DataOutputStream;
import network.handler.IMessageSendCollect;
import network.io.Message;
import network.session.ISession;

/*    */
 /*    */
 /*    */
 /*    */
 /*    */
 /*    */
 /*    */ public class DFSendCollect
        /*    */ implements IMessageSendCollect /*    */ {

    /*    */ public Message readMessage(ISession session, DataInputStream dis) throws Exception {
        /* 18 */ byte cmd = dis.readByte();
        /* 19 */ int size = dis.readInt();
        /* 20 */ byte[] data = new byte[size];
        /* 21 */ int len = 0;
        /* 22 */ int byteRead = 0;
        /* 23 */ while (len != -1 && byteRead < size) {
            /* 24 */ len = dis.read(data, byteRead, size - byteRead);
            /* 25 */ if (len > 0) {
                /* 26 */ byteRead += len;
                /*    */            }
            /*    */        }
        /* 29 */ return new Message(cmd, data);
        /*    */    }

    /*    */
 /*    */
 /*    */ public byte readKey(ISession session, byte b) {
        /* 34 */ return -1;
        /*    */    }

    /*    */
 /*    */
 /*    */ public void doSendMessage(ISession session, DataOutputStream dos, Message msg) throws Exception {
        /*    */ try {
            /* 40 */ byte[] data = msg.getData();
            /* 41 */ dos.writeByte(msg.command);
            /* 42 */ if (data != null) {
                /* 43 */ dos.writeInt(data.length);
                /* 44 */ dos.write(data);
                /*    */            } else {
                /* 46 */ dos.writeInt(0);
                /*    */            }
            /* 48 */ dos.flush();
            /* 49 */        } catch (Exception exception) {
        }
        /*    */    }

    /*    */
 /*    */
 /*    */
 /*    */ public byte writeKey(ISession session, byte b) {
        /* 55 */ return -1;
        /*    */    }
    /*    */ }


/* Location:              C:\Users\VoHoangKiet\Downloads\TEA_V5\lib\GirlkunNetwork.jar!\com\girlkun\network\example\DFSendCollect.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
