package network.server;

public interface InEMTIServer extends Runnable {
  InEMTIServer init();
  
  InEMTIServer start(int paramInt) throws Exception;
  
  InEMTIServer setAcceptHandler(ISessionAcceptHandler paramISessionAcceptHandler);
  
  InEMTIServer close();
  
  InEMTIServer dispose();
  
  InEMTIServer randomKey(boolean paramBoolean);
  
  InEMTIServer setDoSomeThingWhenClose(IServerClose paramIServerClose);
  
  InEMTIServer setTypeSessioClone(@SuppressWarnings("rawtypes") Class paramClass) throws Exception;
  
  ISessionAcceptHandler getAcceptHandler() throws Exception;
  
  boolean isRandomKey();
  
  void stopConnect();
}


/* Location:              C:\Users\VoHoangKiet\Downloads\TEA_V5\lib\GirlkunNetwork.jar!\com\girlkun\network\server\InEMTIServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */