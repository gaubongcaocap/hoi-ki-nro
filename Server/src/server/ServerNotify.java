package server;

/*
 *
 *
 * @author NTD
 */

import EMTI.Functions;
import player.Player;
import network.Message;
import services.Service;
import utils.Util;
import java.util.ArrayList;
import java.util.List;

public class ServerNotify extends Thread {

    private long lastNotifyTime;

    private final List<String> notifies;

    private int indexNotify;

    private final String notify[] = {"Dành cho người chơi trên 18 tuổi. Chơi quá 180 phút một ngày sẽ ảnh hưởng đến sức khỏe."
            , "Trò chơi không có bản quyền chính thức, hãy cân nhắc kỹ trước khi tham gia."
            , "Ngọc Rồng Online by Tấn Đạt - Trang Chủ: http://javhd.pro"};

    private static ServerNotify instance;

    private ServerNotify() {
        this.notifies = new ArrayList<>();
        this.start();
    }

    public static ServerNotify gI() {
        if (instance == null) {
            instance = new ServerNotify();
        }
        return instance;
    }

    @Override
    public void run() {
        while (!Maintenance.isRunning) {
            try {
//                while (!notifies.isEmpty()) {
//                    sendThongBaoBenDuoi(notifies.remove(0));
//                }
                if (!notifies.isEmpty()) {
                    sendChatVip(notifies.remove(0));
                }
//                if (Util.canDoWithTime(this.lastNotifyTime, 360000)) {
//                    sendChatVip(notify[indexNotify]);
//                    this.lastNotifyTime = System.currentTimeMillis();
//                    indexNotify++;
//                    if (indexNotify >= notify.length) {
//                        indexNotify = 0;
//                    }
//                }
            } catch (Exception ignored) {
            }
            try {
                Functions.sleep(1500);
            } catch (Exception ignored) {
            }
        }
    }

    private void sendChatVip(String text) {
        Message msg;
        try {
            msg = new Message(93);
            msg.writer().writeUTF(text);
            Service.gI().sendMessAllPlayer(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void notify(String text) {
        this.notifies.add(text);
    }

    public void sendNotifyTab(Player player) {
        Message msg;
        try {
            msg = new Message(50);
            msg.writer().writeByte(10);
            for (int i = 0; i < Manager.NOTIFY.size(); i++) {
                String[] arr = Manager.NOTIFY.get(i).split("<>");
                msg.writer().writeShort(i);
                msg.writer().writeUTF(arr[0]);
                msg.writer().writeUTF(arr[1]);
            }
            if (player.pet != null) {
                if (player.pet.pet != null) {
                    Player pet = player.pet.pet;
                    msg.writer().writeShort(Manager.NOTIFY.size());
                    msg.writer().writeUTF(pet.name);
                    msg.writer().writeUTF(pet.name
                            + "\nSM: " + Util.chiaNho(pet.nPoint.power)
                            + "\nTN: " + Util.chiaNho(pet.nPoint.tiemNang)
                            + "\nHP: " + Util.chiaNho(pet.nPoint.hp) + "/" + Util.chiaNho(pet.nPoint.hpMax)
                            + "\nMP: " + Util.chiaNho(pet.nPoint.mp) + "/" + Util.chiaNho(pet.nPoint.mpMax)
                            + "\nSD: " + Util.chiaNho(pet.nPoint.dame)
                            + "\n--------------------"
                            + "\nHPG: " + Util.chiaNho(pet.nPoint.hpg)
                            + "\nMPG: " + Util.chiaNho(pet.nPoint.mpg)
                            + "\nSDG: " + Util.chiaNho(pet.nPoint.dameg)
                            + "\nDEF: " + Util.chiaNho(pet.nPoint.defg)
                            + "\nCRIT: " + Util.chiaNho(pet.nPoint.critg));
                }
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception ignored) {
        }
    }
}
