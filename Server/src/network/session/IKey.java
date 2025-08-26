package network.session;

import network.io.Message;

public interface IKey {
  void sendKey() throws Exception;
  
  void setKey(Message paramMessage) throws Exception;
  
  void setKey(byte[] paramArrayOfbyte);
  
  byte[] getKey();
  
  boolean sentKey();
  
  void setSentKey(boolean paramBoolean);
}


/* Location:              C:\Users\VoHoangKiet\Downloads\TEA_V5\lib\GirlkunNetwork.jar!\com\girlkun\network\session\IKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */