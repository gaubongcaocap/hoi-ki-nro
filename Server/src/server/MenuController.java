package server;

/*
 *
 *
 * @author NTD
 */

import java.io.IOException;
import consts.ConstNpc;
import npc.Npc;
import npc.NpcManager;
import server.io.MySession;
import player.Player;
import services.Service;
import services.func.TransactionService;

public class MenuController {

    private static MenuController instance;

    public static MenuController gI() {
        if (instance == null) {
            instance = new MenuController();
        }
        return instance;
    }

    public void openMenuNPC(MySession session, int idnpc, Player player) {
        TransactionService.gI().cancelTrade(player);
        Npc npc;
        if (idnpc == ConstNpc.CALICK && player.zone.map.mapId != 102) {
            npc = NpcManager.getNpc(ConstNpc.CALICK);
        } else if (idnpc == ConstNpc.LY_TIEU_NUONG) {
            npc = NpcManager.getNpc(ConstNpc.LY_TIEU_NUONG);
        }  else if (idnpc == ConstNpc.HOA_HONG) {
            npc = NpcManager.getNpc(ConstNpc.HOA_HONG);
        } else {
            npc = player.zone.map.getNpc(player, idnpc);
        }
        if (npc != null) {
            npc.openBaseMenu(player);
        } else {
            Service.gI().hideWaitDialog(player);
        }
    }

    public void doSelectMenu(Player player, int npcId, int select) throws IOException {
        TransactionService.gI().cancelTrade(player);
        switch (npcId) {
            case ConstNpc.RONG_THIENG, ConstNpc.CON_MEO ->
                NpcManager.getNpc((byte) npcId).confirmMenu(player, select);
            default -> {
                Npc npc = null;
                if (npcId == ConstNpc.CALICK && player.zone.map.mapId != 102) {
                    npc = NpcManager.getNpc(ConstNpc.CALICK);
                } else if (npcId == ConstNpc.LY_TIEU_NUONG) {
                    npc = NpcManager.getNpc(ConstNpc.LY_TIEU_NUONG);
                }else if (npcId == ConstNpc.HOA_HONG) {
                    npc = NpcManager.getNpc(ConstNpc.HOA_HONG);
                } else if (player.zone != null) {
                    npc = player.zone.map.getNpc(player, npcId);
                }
                if (npc != null) {
                    npc.confirmMenu(player, select);
                } else {
                    Service.gI().hideWaitDialog(player);
                }
            }
        }

    }
}
