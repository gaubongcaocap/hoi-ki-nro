package network.server;

import network.session.ISession;

public interface ISessionAcceptHandler {
  void sessionInit(ISession paramISession);
  
  void sessionDisconnect(ISession paramISession);
}


/* Location:              C:\Users\VoHoangKiet\Downloads\TEA_V5\lib\GirlkunNetwork.jar!\com\girlkun\network\server\ISessionAcceptHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */