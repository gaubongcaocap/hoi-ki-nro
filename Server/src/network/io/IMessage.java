package network.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public interface IMessage extends IIOMessage {
  DataOutputStream writer();
  
  DataInputStream reader();
  
  byte[] getData();
  
  void cleanup();
  
  void dispose();
}


/* Location:              C:\Users\VoHoangKiet\Downloads\TEA_V5\lib\GirlkunNetwork.jar!\com\girlkun\network\io\IMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */