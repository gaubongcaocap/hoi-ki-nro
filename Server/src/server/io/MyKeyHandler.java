package server.io;

/*
 *
 *
 * @author NTD
 */

import network.KeyHandler;
import data.DataGame;
import network.inetwork.ISession;

public class MyKeyHandler extends KeyHandler {

    @Override
    public void sendKey(ISession session) {
        super.sendKey(session);
        DataGame.sendDataImageVersion((MySession) session);
        DataGame.sendVersionRes((MySession) session);
    }

}
