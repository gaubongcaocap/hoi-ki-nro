/*    */ package network.io;
/*    */ 
/*    */ import network.CommandMessage;
/*    */ import network.handler.IMessageHandler;
/*    */ import network.handler.IMessageSendCollect;
/*    */ import network.server.EMTIServer;
/*    */ import network.session.ISession;
/*    */ import network.session.TypeSession;
/*    */ import java.io.DataInputStream;
/*    */ import java.net.Socket;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Collector
/*    */   implements Runnable
/*    */ {
/*    */   private ISession session;
/*    */   private DataInputStream dis;
/*    */   private IMessageSendCollect collect;
/*    */   private IMessageHandler messageHandler;
/*    */   
/*    */   public Collector(ISession session, Socket socket) {
/* 26 */     this.session = session;
/* 27 */     setSocket(socket);
/*    */   }
/*    */   
/*    */   public Collector setSocket(Socket socket) {
/*    */     try {
/* 32 */       this.dis = new DataInputStream(socket.getInputStream());
/* 33 */     } catch (Exception exception) {}
/*    */     
/* 35 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */
   public void run() {
/*    */     try {
/*    */       while (true) {
/* 42 */         if (this.session.isConnected()) {
/* 43 */           Message msg = this.collect.readMessage(this.session, this.dis);
/*    */           
/* 45 */           if (msg.command == CommandMessage.REQUEST_KEY) {
/* 46 */             if (this.session.getTypeSession() == TypeSession.SERVER) {
/* 47 */               this.session.sendKey();
/*    */             } else {
/* 49 */               this.session.setKey(msg);
/*    */             } 
/*    */           } else {
/* 52 */             this.messageHandler.onMessage(this.session, msg);
/*    */           } 
/* 54 */           msg.cleanup();
/*    */         } 
/* 56 */         Thread.sleep(1L);
/*    */       } 
/* 58 */     } catch (Exception ex) {
/*    */       try {
/* 60 */         EMTIServer.gI().getAcceptHandler().sessionDisconnect(this.session);
/* 61 */       } catch (Exception exception) {}
/*    */       
/* 63 */       if (this.session != null) {
/* 64 */         System.out.println("Mất kết nối với session " + this.session.getIP() + "...");
/* 65 */         this.session.disconnect();
/*    */       } 
/*    */       return;
/*    */     } 
/*    */   }
/*    */   public void setCollect(IMessageSendCollect collect) {
/* 71 */     this.collect = collect;
/*    */   }
/*    */   
/*    */   public void setMessageHandler(IMessageHandler handler) {
/* 75 */     this.messageHandler = handler;
/*    */   }
/*    */   
/*    */   public void close() {
/* 79 */     if (this.dis != null) {
/*    */       try {
/* 81 */         this.dis.close();
/* 82 */       } catch (Exception exception) {}
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public void dispose() {
/* 88 */     this.session = null;
/* 89 */     this.dis = null;
/* 90 */     this.collect = null;
/*    */   }
/*    */ }


/* Location:              C:\Users\VoHoangKiet\Downloads\TEA_V5\lib\GirlkunNetwork.jar!\com\girlkun\network\io\Collector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */