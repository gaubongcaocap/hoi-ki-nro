package network.session;

import network.handler.IKeySessionHandler;
import network.handler.IMessageHandler;
import network.handler.IMessageSendCollect;
import network.io.Message;






public interface ISession extends IKey {

    TypeSession getTypeSession();

    ISession setSendCollect(IMessageSendCollect paramIMessageSendCollect);

    ISession setMessageHandler(IMessageHandler paramIMessageHandler);

    ISession setKeyHandler(IKeySessionHandler paramIKeySessionHandler);

    ISession startSend();

    ISession startCollect();

    ISession start();

    ISession setReconnect(boolean paramBoolean);

    void initThreadSession();

    void reconnect();

    String getIP();

    boolean isConnected();

    long getID();

    void sendMessage(Message paramMessage);

    void doSendMessage(Message paramMessage) throws Exception;

    void disconnect();

    void dispose();

    int getNumMessages();
}


/* Location:              C:\Users\VoHoangKiet\Downloads\TEA_V5\lib\GirlkunNetwork.jar!\com\girlkun\network\session\ISession.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
