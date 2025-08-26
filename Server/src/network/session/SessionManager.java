/*    */ package network.session;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SessionManager
/*    */ {
/*    */   private static SessionManager i;
/*    */   private List<Session> sessions;
/*    */   
/*    */   public static SessionManager gI() {
/* 16 */     if (i == null) {
/* 17 */       i = new SessionManager();
/*    */     }
/* 19 */     return i;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public SessionManager() {
/* 25 */     this.sessions = new ArrayList<>();
/*    */   }
/*    */ }


/* Location:              C:\Users\VoHoangKiet\Downloads\TEA_V5\lib\GirlkunNetwork.jar!\com\girlkun\network\session\SessionManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */