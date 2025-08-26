/*    */ package network.example;
/*    */ 
/*    */ import network.handler.IMessageHandler;
/*    */ import network.io.Message;
/*    */ import network.session.ISession;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MessageHandler
/*    */   implements IMessageHandler
/*    */ {
/*    */   public void onMessage(ISession session, Message msg) throws Exception {
/* 16 */     System.out.println(msg.reader().readUTF());
/* 17 */     msg.cleanup();
/*    */   }
/*    */ }


/* Location:              C:\Users\VoHoangKiet\Downloads\TEA_V5\lib\GirlkunNetwork.jar!\com\girlkun\network\example\MessageHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */