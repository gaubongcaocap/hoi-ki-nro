package player;

import item.Item;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

import java.io.IOException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import jdbc.DBConnecter;
import jdbc.daos.NDVSqlFetcher;

import network.Message;
import org.json.simple.JSONObject;
import services.InventoryService;
import services.ItemService;
import services.Service;
import utils.Logger;
import utils.Util;

public class ArchivementSucManh {

    public String info1;
    public String info2;
    public short money;
    public boolean isFinish;
    public boolean isRecieve;

    public String getInfo1() {
        return info1;
    }

    public void setInfo1(String info1) {
        this.info1 = info1;
    }

    public String getInfo2() {
        return info2;
    }

    public void setInfo2(String info2) {
        this.info2 = info2;
    }

    public short getMoney() {
        return money;
    }

    public void setMoney(short money) {
        this.money = money;
    }

    public boolean isFinish() {
        return isFinish;
    }

    public void setFinish(boolean finish) {
        isFinish = finish;
    }

    public boolean isRecieve() {
        return isRecieve;
    }

    public void setRecieve(boolean recieve) {
        isRecieve = recieve;
    }
    public static ArchivementSucManh gI = null;
    public final static long POWER = 100000000;
    public static long[] POWERGIFT = {POWER * 1, POWER * 2, POWER * 5, POWER * 10, POWER * 50, POWER * 100, POWER * 400};

    public static ArchivementSucManh gI() {
        if (gI == null) {
            return new ArchivementSucManh();
        }
        return gI;
    }

    public ArchivementSucManh() {
    }

    public ArchivementSucManh(String info1, String info2, short money, boolean isFinish, boolean isRecieve) {
        this.info1 = info1;
        this.info2 = info2;
        this.money = money;
        this.isFinish = isFinish;
        this.isRecieve = isRecieve;
    }

    public void Show(Player pl) {
        Message msg = null;
        try {
            msg = new Message(-76);
            msg.writer().writeByte(0); // action
            msg.writer().writeByte(pl.archivementListSM.size());
            for (int i = 0; i < pl.archivementListSM.size(); i++) {

                ArchivementSucManh archivement = pl.archivementListSM.get(i);
                if (pl.getSession().version <= 231 || pl.getSession().version > 235) {
                    msg.writer().writeUTF(archivement.getInfo1());
                    msg.writer().writeUTF(archivement.getInfo2());
                    msg.writer().writeShort(archivement.getMoney()); //money
                    msg.writer().writeBoolean(archivement.isFinish);
                    msg.writer().writeBoolean(archivement.isRecieve);

                } else {
                    msg.writer().writeUTF(archivement.getInfo1());
                    msg.writer().writeUTF(archivement.getInfo2());
                    msg.writer().writeShort(archivement.getMoney()); //money
                    msg.writer().writeUTF("");
                    msg.writer().writeBoolean(archivement.isFinish);
                    msg.writer().writeBoolean(archivement.isRecieve);
                    msg.writer().writeShort(10895);//res icon
                }

            }
            pl.sendMessage(msg);
            msg.cleanup();
            pl.typeRecvieArchiment = 2;
        } catch (IOException e) {

            e.getStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public boolean checktongnap(Player pl, int index) {
        if (index == 0 && pl.nPoint.power >= POWERGIFT[0]) {
            return true;
        }
        if (index == 1 && pl.nPoint.power >= POWERGIFT[1]) {
            return true;
        }
        if (index == 2 && pl.nPoint.power >= POWERGIFT[2]) {
            return true;
        }
        if (index == 3 && pl.nPoint.power >= POWERGIFT[3]) {
            return true;
        }
        if (index == 4 && pl.nPoint.power >= POWERGIFT[4]) {
            return true;
        }
        if (index == 5 && pl.nPoint.power >= POWERGIFT[5]) {
            return true;
        }
        if (index == 6 && pl.nPoint.power >= POWERGIFT[6]) {
            return true;
        }

        return false;
    }

    public void receiveGem(int index, Player pl) {
        ArchivementSucManh temp = pl.archivementListSM.get(index);
        if (temp.isRecieve) {
            Service.gI().sendThongBaoOK(pl, "Nhận rồi đừng nhận nữua");
            return;
        }
        if (temp != null) {
            Message msg = null;
            try {
                msg = new Message(-76);
                msg.writer().writeByte(1); // action
                msg.writer().writeByte(index); // index
                pl.sendMessage(msg);
                msg.cleanup();
            } catch (IOException e) {
                e.printStackTrace();
                Logger.logException(this.getClass(), e);
            } finally {
                if (msg != null) {
                    msg.cleanup();
                    msg = null;
                }
            }

            pl.archivementListSM.get(index).setRecieve(true);
            try {
                JSONArray dataArray = new JSONArray();

                for (ArchivementSucManh arr : pl.archivementListSM) {
                    dataArray.add(arr.isRecieve ? "1" : "0");
                }
                String inventory = dataArray.toJSONString();
                dataArray.clear();
                DBConnecter.executeUpdate("update player set Achievement_SucManh = ? where id = ?", inventory, pl.id);
                nhanQua(pl, index + 1);
                System.out.println("Player " + pl.name + " Nhận quà thành công");

            } catch (Exception e) {
                e.printStackTrace();
            }
            Service.gI().sendThongBao(pl, "Nhận thành công, vui lòng kiểm tra hòm thư ");
        } else {
            Service.gI().sendThongBao(pl, "Không có phần thưởng");
        }
    }

    private void nhanQua(Player pl, int index) throws SQLException {
        Item item = null;
        JSONArray dataArray;
        JSONObject dataObject;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try ( Connection con2 = DBConnecter.getConnectionServer()) {
            ps = con2.prepareStatement("SELECT * FROM moc_suc_manh WHERE id = ?");
            ps.setInt(1, index);
            rs = ps.executeQuery();
            while (rs.next()) {
                dataArray = (JSONArray) JSONValue.parse(rs.getString("detail"));
                for (int i = 0; i < dataArray.size(); i++) {
                    dataObject = (JSONObject) JSONValue.parse(String.valueOf(dataArray.get(i)));
                    int tempid = Integer.parseInt(String.valueOf(dataObject.get("temp_id")));
                    int quantity = Integer.parseInt(String.valueOf(dataObject.get("quantity")));
                    item = ItemService.gI().createNewItem((short) tempid);
                    item.quantity = quantity;
                    JSONArray optionsArray = (JSONArray) dataObject.get("options");
                    for (int j = 0; j < optionsArray.size(); j++) {
                        JSONObject optionObject = (JSONObject) optionsArray.get(j);
                        int param = Integer.parseInt(String.valueOf(optionObject.get("param")));
                        int optionId = Integer.parseInt(String.valueOf(optionObject.get("id")));
                        item.itemOptions.add(new Item.ItemOption(optionId, param));

                    }
                    pl.inventory.itemsMailBox.add(item);
                    if (NDVSqlFetcher.updateMailBox(pl)) {
                        Service.gI().sendThongBao(pl, "Bạn vừa nhận quà về mail thành công ");
                    }

                }
                InventoryService.gI().sendItemBag(pl);
                Service.gI().sendMoney(pl);
            }

        }

    }

    public void getAchievement(Player player) {
        try {
            if (player.getSession() == null) {
                return;
            }

            Connection con = null;
            PreparedStatement ps = null;
            JSONValue jv = new JSONValue();
            JSONArray dataArray = null;
            con = DBConnecter.getConnectionServer();
            ps = con.prepareStatement("SELECT `Achievement_SucManh` FROM `player` WHERE id = ? LIMIT 1");
            ps.setInt(1, (int) player.id);

            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String achievementData = rs.getString(1);
                    try {
                        dataArray = (JSONArray) jv.parse(achievementData);
                        if (dataArray != null && dataArray.size() != 7) {
                            if (dataArray.size() < 7) {
                                for (int j = dataArray.size(); j < 7; j++) {
                                    dataArray.add(0);
                                }
                            }

                            while (dataArray.size() > 14) {

                                dataArray.remove(14);

                            }

                        }
                        player.archivementListSM.clear();
                        if (dataArray != null) {

                            for (int i = 0; i < dataArray.size(); i++) {
                                try {
                                    ArchivementSucManh achievement = new ArchivementSucManh();
//                                    achievement.setInfo1("Mốc SM ");
                                    achievement.setInfo1("Mốc " + getNhiemVu(i) + " Sức mạnh");
                                    achievement.setInfo2("Đã đạt: " + getNhiemVu2(player, i) + "/" + getNhiemVu(i) + " Sức mạnh");
                                    achievement.setFinish(checktongnap(player, i));
                                    achievement.setMoney((short) getRuby(i));
                                    achievement.setRecieve(Integer.parseInt(String.valueOf(dataArray.get(i))) != 0);
                                    player.archivementListSM.add(achievement);

                                } catch (Exception ee) {
                                    ee.printStackTrace();
                                    return;
                                }
                            }

                        }
                        dataArray.clear();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("Player: " + player.name + " dang xem moc nap");
                Show(player);
                rs.close();
                ps.close();
                con.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getNhiemVu(int index) {
        switch (index) {
            case 0:
                return "" + Util.numberToMoney(POWERGIFT[0]);
            case 1:
                return "" + Util.numberToMoney(POWERGIFT[1]);
            case 2:
                return "" + Util.numberToMoney(POWERGIFT[2]);
            case 3:
                return "" + Util.numberToMoney(POWERGIFT[3]);
            case 4:
                return "" + Util.numberToMoney(POWERGIFT[4]);
            case 5:
                return "" + Util.numberToMoney(POWERGIFT[5]);
            case 6:
                return "" + Util.numberToMoney(POWERGIFT[6]);

            default:
                return "";
        }
    }

    public String getNhiemVu2(Player player, int index) {
        switch (index) {
            case 0:
                return " " + Util.numberToMoney(player.nPoint.power) + "";
            case 1:
                return " " + Util.numberToMoney(player.nPoint.power) + "";
            case 2:
                return " " + Util.numberToMoney(player.nPoint.power) + "";
            case 3:
                return " " +Util.numberToMoney(player.nPoint.power) + "";
            case 4:
                return " " + Util.numberToMoney(player.nPoint.power) + "";
            case 5:
                return " " + Util.numberToMoney(player.nPoint.power) + "";
            case 6:
                return " " + Util.numberToMoney(player.nPoint.power) + "";

            default:
                return "";
        }
    }

    public int getRuby(int index) {
        switch (index) {
            case 0:
                return 0;
            case 1:
                return 0;
            case 2:
                return 0;
            case 3:
                return 0;
            case 4:
                return 0;
            case 5:
                return 0;
            case 6:
                return 0;

            default:
                return -1;
        }
    }

}
