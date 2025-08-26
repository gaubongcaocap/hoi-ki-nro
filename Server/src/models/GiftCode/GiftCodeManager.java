package models.GiftCode;

/*
 *
 *
 * @author NTD
 */
import jdbc.DBConnecter;
import player.Player;
import services.NpcService;
import services.Service;
import java.util.ArrayList;
import services.InventoryService;

public class GiftCodeManager {

    public String name;
    public final ArrayList<GiftCode> listGiftCode = new ArrayList<>();

    private static GiftCodeManager instance;

    public static GiftCodeManager gI() {
        if (instance == null) {
            instance = new GiftCodeManager();
        }
        return instance;
    }

    public GiftCode checkUseGiftCode(Player player, String code) {
        for (GiftCode giftCode : listGiftCode) {
            if (giftCode.code.equals(code)) {
                if (giftCode.countLeft <= 0) {
                    Service.gI().sendThongBao(player, "Giftcode đã hết lượt nhập");
                    return null;
                }
                if (giftCode.isUsedGiftCode(player)) {
                    Service.gI().sendThongBao(player, "Bạn đã sử dụng GiftCode này rồi");
                    return null;
                }
                if (giftCode.type == 1 && !player.getSession().actived) { // Kiểm tra type và trạng thái thành viên
                    Service.gI().sendThongBao(player, "Bạn cần mở thành viên để sử dụng mã này.");
                     return null;
                }
                if (InventoryService.gI().getCountEmptyBag(player) < giftCode.detail.size()) {
                    Service.gI().sendThongBaoOK(player, "Cần tối thiểu " + giftCode.detail.size() + " ô hành trang trống");
                    return null;
                }

                giftCode.countLeft -= 1;
                player.giftCode.add(code);
                updateGiftCode(giftCode);
                return giftCode;
            }
        }
        return null;
    }

    public void updateGiftCode(GiftCode giftcode) {
        try {
            DBConnecter.executeUpdate("update giftcode set count_left = ? where id = ?", giftcode.countLeft, giftcode.id);
        } catch (Exception e) {
        }
    }

   public void checkInfomationGiftCode(Player p) {
    StringBuilder sb = new StringBuilder();
    for (GiftCode giftCode : listGiftCode) {
        sb.append("Code: ").append(giftCode.code).append(", Số lượng còn lại: ").append(giftCode.countLeft).append("\b")
          .append("Ngày tạo: ")
          .append(giftCode.datecreate).append(", Ngày hết hạn: ").append(giftCode.dateexpired)
          .append("\n");
    }
   
    if (sb.length() > 0) {
        sb.deleteCharAt(sb.length() - 1); // Xóa ký tự "\n" cuối cùng
    }
    
    NpcService.gI().createTutorial(p, 5073, sb.toString());
}


}
