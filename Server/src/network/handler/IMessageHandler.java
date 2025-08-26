package network.handler;

import network.io.Message;
import network.session.ISession;

public interface IMessageHandler {
  void onMessage(ISession paramISession, Message paramMessage) throws Exception;
}


/* Location:              C:\Users\VoHoangKiet\Downloads\TEA_V5\lib\GirlkunNetwork.jar!\com\girlkun\network\handler\IMessageHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */