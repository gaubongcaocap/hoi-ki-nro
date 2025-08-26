package services;

/*
 *
 *
 * @author NTD
 */

import EMTI.Functions;
import player.Player;
import network.Message;
import utils.Logger;
import utils.TimeUtil;
import utils.Util;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import server.Maintenance;

public class ChatGlobalService implements Runnable {

    private static final int COUNT_CHAT = 100;
    private static final int COUNT_WAIT = 100;
    private static ChatGlobalService i;

    private final List<ChatGlobal> listChatting;
    private final List<ChatGlobal> waitingChat;

    private ChatGlobalService() {
        this.listChatting = new ArrayList<>();
        this.waitingChat = new LinkedList<>();
        this.start();
    }

    private void start() {
        Thread.startVirtualThread(this);
    }

    public static ChatGlobalService gI() {
        if (i == null) {
            i = new ChatGlobalService();
        }
        return i;
    }

    public void chatVip(Player player, String text) {
        waitingChat.add(new ChatGlobal(player, text.length() > 100 ? text.substring(0, 100) : text));
    }

    public void chat1(Player player, String text) {
        player.iDMark.setLastTimeChatGlobal(System.currentTimeMillis());
        waitingChat.add(new ChatGlobal(player, text.length() > 100 ? text.substring(0, 100) : text));
    }

    public void chat(Player player, String text) {
        if (waitingChat.size() >= COUNT_WAIT) {
            Service.gI().sendThongBao(player, "Kênh thế giới hiện đang quá tải, không thể chat lúc này");
            return;
        }
        boolean haveInChatting = false;
        for (ChatGlobal chat : listChatting) {
            if (chat.text.equals(text)) {
                haveInChatting = true;
                break;
            }
        }
        if (haveInChatting) {
            return;
        }
        if (player.inventory.gem >= 5) {
            if (player.isAdmin() || Util.canDoWithTime(player.iDMark.getLastTimeChatGlobal(), 30000)) {
                if (player.isAdmin() || player.nPoint.power > 2000000000) {
                    player.inventory.subGemAndRuby(50);
                    Service.gI().sendMoney(player);
                    player.iDMark.setLastTimeChatGlobal(System.currentTimeMillis());
                    waitingChat.add(new ChatGlobal(player, text.length() > 100 ? text.substring(0, 100) : text));
                } else {
                    Service.gI().sendThongBao(player, "Sức mạnh phải ít nhất 2tỷ mới có thể chat thế giới");
                }
            } else {
                Service.gI().sendThongBao(player, "Không thể chat thế giới lúc này, vui lòng đợi "
                        + TimeUtil.getTimeLeft(player.iDMark.getLastTimeChatGlobal(), 10));
            }
        } else {
            Service.gI().sendThongBao(player, "Không đủ ngọc chat thế giới");
        }
    }

    @Override
    public void run() {
        while (!Maintenance.isRunning) {
            try {
                if (!listChatting.isEmpty()) {
                    ChatGlobal chat = listChatting.get(0);
                    if (Util.canDoWithTime(chat.timeSendToPlayer, 5000)) {
                        listChatting.remove(0).dispose();
                    }
                }

                if (!waitingChat.isEmpty()) {
                    ChatGlobal chat = waitingChat.get(0);
                    if (listChatting.size() < COUNT_CHAT) {
                        waitingChat.remove(0);
                        chat.timeSendToPlayer = System.currentTimeMillis();
                        listChatting.add(chat);
                        chatGlobal(chat);
                    }
                }
                Thread.sleep(1000);
            } catch (Exception e) {
                Logger.logException(ChatGlobalService.class, e);
            }
        }
    }

    private void chatGlobal(ChatGlobal chat) {
        Message msg;
        try {
            msg = new Message(92);
            msg.writer().writeUTF(chat.playerName);
            msg.writer().writeUTF("|5|" + chat.text);
            msg.writer().writeInt((int) chat.playerId);
            msg.writer().writeShort(chat.head);
            msg.writer().writeShort(-1);
            msg.writer().writeShort(chat.body);
            msg.writer().writeShort(chat.bag); //bag
            msg.writer().writeShort(chat.leg);
            msg.writer().writeByte(0);
            Service.gI().sendMessAllPlayer(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    private class ChatGlobal {

        public String playerName;
        public int playerId;
        public short head;
        public short body;
        public short leg;
        public short bag;
        public String text;
        public long timeSendToPlayer;

        public ChatGlobal(String text) {
            Object[][] characters = {
                    { "Quy Lão Kame", 33, 34, 35 },
                    { "Lý Tiểu Nương", 487, 488, 489 },
                    { "Bò Mộng", 80, 81, 82 },
                    { "Thần Mèo Karin", 89, 90, 91 },
                    { "Thượng Đế", 86, 87, 88 },
                    { "Thần Vũ Trụ", 98, 99, 100 },
                    { "Bunma", 267, 268, 269 },
                    { "Ca Lích", 270, 271, 272 },
                    { "Bunma", 42, 43, 44 },
                    { "Santa", 300, 301, 302 }
            };

            Random random = new Random();
            int index = random.nextInt(characters.length);
            this.playerName = (String) characters[index][0];
            this.head = (short) (int) characters[index][1];
            this.body = (short) (int) characters[index][2];
            this.leg = (short) (int) characters[index][3];
            this.playerId = -1;
            this.bag = -1;
            this.text = text;
        }

        public ChatGlobal(Player player, String text) {
            if (!player.isAdmin()) {
                this.playerName = player.name;
            } else if (player.name.equals("Tấn Đạt")) {
                this.playerName = player.name + " - Quản Trị Viên";
            } else {
                this.playerName = player.name + " - Mod";
            }
            this.playerId = (int) player.id;
            this.head = player.getHead();
            this.body = player.getBody();
            this.leg = player.getLeg();
            this.bag = player.getFlagBag();
            this.text = text;
        }

        private void dispose() {
            this.playerName = null;
            this.text = null;
        }

    }

    public void autoChatGlobal(Player player, String message) {
        if (waitingChat.size() >= COUNT_WAIT) {
            if (player != null) {
                Service.gI().sendThongBao(player, "Kênh thế giới hiện đang quá tải, không thể gửi thông báo");
            }
            return;
        }

  
        if (player == null) {
            waitingChat.add(new ChatGlobal(message.length() > 100 ? message.substring(0, 100) : message));
        } else {
      
            waitingChat.add(new ChatGlobal(player, message.length() > 100 ? message.substring(0, 100) : message));
        }
    }

}
