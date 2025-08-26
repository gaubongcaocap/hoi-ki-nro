/*     */ package network.example;
/*     */ 
/*     */ import network.handler.IMessageSendCollect;
/*     */ import network.io.Message;
/*     */ import network.session.ISession;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MessageSendCollect
/*     */   implements IMessageSendCollect
/*     */ {
/*  16 */   private int curR = 0;
/*  17 */   private int curW = 0;
/*     */   
/*     */   public Message readMessage(ISession session, DataInputStream dis) throws Exception {
/*     */     int size;
/*  21 */     byte cmd = dis.readByte();
/*  22 */     if (session.sentKey()) {
/*  23 */       cmd = readKey(session, cmd);
/*     */     }
/*     */     
/*  26 */     if (session.sentKey()) {
/*  27 */       byte b1 = dis.readByte();
/*  28 */       byte b2 = dis.readByte();
/*  29 */       size = (readKey(session, b1) & 0xFF) << 8 | readKey(session, b2) & 0xFF;
/*     */     } else {
/*  31 */       size = dis.readUnsignedShort();
/*     */     } 
/*  33 */     byte[] data = new byte[size];
/*  34 */     int len = 0;
/*  35 */     int byteRead = 0;
/*  36 */     while (len != -1 && byteRead < size) {
/*  37 */       len = dis.read(data, byteRead, size - byteRead);
/*  38 */       if (len > 0) {
/*  39 */         byteRead += len;
/*     */       }
/*     */     } 
/*  42 */     if (session.sentKey()) {
/*  43 */       for (int i = 0; i < data.length; i++) {
/*  44 */         data[i] = readKey(session, data[i]);
/*     */       }
/*     */     }
/*  47 */     return new Message(cmd, data);
/*     */   }
/*     */ 
/*     */   
/*     */   public byte readKey(ISession session, byte b) {
/*  52 */     byte i = (byte)(session.getKey()[this.curR++] & 0xFF ^ b & 0xFF);
/*  53 */     if (this.curR >= (session.getKey()).length) {
/*  54 */       this.curR %= (session.getKey()).length;
/*     */     }
/*  56 */     return i;
/*     */   }
/*     */ 
/*     */   
/*     */   public void doSendMessage(ISession session, DataOutputStream dos, Message msg) throws Exception {
/*     */     try {
/*  62 */       byte[] data = msg.getData();
/*  63 */       if (session.sentKey()) {
/*  64 */         byte b = writeKey(session, msg.command);
/*  65 */         dos.writeByte(b);
/*     */       } else {
/*  67 */         dos.writeByte(msg.command);
/*     */       } 
/*  69 */       if (data != null) {
/*  70 */         int size = data.length;
/*  71 */         if (msg.command == -32 || msg.command == -66 || msg.command == -74 || msg.command == 11 || msg.command == -67 || msg.command == -87 || msg.command == 66) {
/*  72 */           byte b = writeKey(session, (byte)size);
/*  73 */           dos.writeByte(b - 128);
/*  74 */           byte b2 = writeKey(session, (byte)(size >> 8));
/*  75 */           dos.writeByte(b2 - 128);
/*  76 */           byte b3 = writeKey(session, (byte)(size >> 16));
/*  77 */           dos.writeByte(b3 - 128);
/*  78 */         } else if (session.sentKey()) {
/*  79 */           int byte1 = writeKey(session, (byte)(size >> 8));
/*  80 */           dos.writeByte(byte1);
/*  81 */           int byte2 = writeKey(session, (byte)(size & 0xFF));
/*  82 */           dos.writeByte(byte2);
/*     */         } else {
/*  84 */           dos.writeShort(size);
/*     */         } 
/*  86 */         if (session.sentKey()) {
/*  87 */           for (int i = 0; i < data.length; i++) {
/*  88 */             data[i] = writeKey(session, data[i]);
/*     */           }
/*     */         }
/*  91 */         dos.write(data);
/*     */       } else {
/*  93 */         dos.writeShort(0);
/*     */       } 
/*  95 */       dos.flush();
/*  96 */       msg.cleanup();
/*  97 */     } catch (Exception exception) {}
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public byte writeKey(ISession session, byte b) {
/* 103 */     byte i = (byte)(session.getKey()[this.curW++] & 0xFF ^ b & 0xFF);
/* 104 */     if (this.curW >= (session.getKey()).length) {
/* 105 */       this.curW %= (session.getKey()).length;
/*     */     }
/* 107 */     return i;
/*     */   }
/*     */ }


/* Location:              C:\Users\VoHoangKiet\Downloads\TEA_V5\lib\GirlkunNetwork.jar!\com\girlkun\network\example\MessageSendCollect.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */