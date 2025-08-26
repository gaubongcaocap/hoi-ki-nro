package consts;

/**
 *
 * @author NTD
 */

public class ConstTournament {

    public static final int NHI_DONG = 0;
    public static final int SIEU_CAP_1 = 1;
    public static final int SIEU_CAP_2 = 2;
    public static final int SIEU_CAP_3 = 3;
    public static final int NGOAI_HANG = 4;

    public static final int MINS_MAX_CAN_REG = 25;
    public static final int MINS_START = 30;
    public static final int MINS_END = 57;

    public static final String TEXT_TRUAT_QUYEN = "Bạn đã bị truất quyền thi đấu vì không đến đúng giờ";
    public static final String TEXT_DOI_THU_BO_CUOC = "Bạn đã thắng vì đối thủ đã bỏ cuộc, chờ tại đây để thi đấu vòng tiếp theo";
    public static final String TEXT_NPC_CHAT_ROI_DAI = "Đối thủ đã rơi khỏi võ đài, %1 đã thắng";
    public static final String TEXT_DANG_KY_THANH_CONG = "Bạn đã đăng ký thành công, nhớ có mặt tại đây trước %1h30\bBây giờ là %2, đến trễ coi như bỏ cuộc";
    public static final String TEXT_CHIA_BUON = "Bạn đã thua, hẹn gặp lại ở giải sau";
    public static final String TEXT_DOI_THU_BO_CUOC_ROI_MAP = "Đối thủ bỏ cuộc, bạn đã chiến thắng";
    public static final String TEXT_XU_THUA_BO_CHAY = "Bạn bị xử thua vì đã bỏ chạy";
    public static final String TEXT_NPC_CHAT_DOI_THU_BO_CUOC_ROI_MAP = "Đối thủ bỏ cuộc %1 đã thắng";
    public static final String TEXT_DOI_THU_KIET_SUC = "Đối thủ đã kiệt sức, bạn đã thắng";
    public static final String TEXT_XU_THUA_KIET_SUC = ".....";
    public static final String TEXT_NPC_CHAT_DOI_THU_KIET_SUC = "Đối thủ đã kiệt sức, %1 đã thắng";
    public static final String TEXT_HET_GIO = ".....";
    public static final String TEXT_XU_THUA_HET_GIO = ".....";
    public static final String TEXT_NPC_CHAT_HET_GIO = "Hết giờ thi đấu %1 đã chiến thắng vì bị thương ít hơn";
    public static final String TEXT_HUY_DANG_KY = "Bạn đã hủy đăng ký thành công";
    public static final String TEXT_THANG_VONG_NAY = "Bạn đã thắng vòng này, xin chờ tại đây ít phút để thi đấu vòng sau";
    public static final String TEXT_DA_VO_DICH = "Bạn đã vô địch giải gần đây, vui lòng đợi giải sau";
    public static final String TEXT_VO_DICH = "Bạn đã vô địch giải đấu, xin chúc mừng bạn, bạn được thưởng 5 viên đá nâng cấp";
    public static final String TEXT_KHOE_VO_DICH = "Chúc mừng %1 vừa vô địch giải %2";
    public static final String TEXT_CHI_CO_THE_THAM_GIA_GIAI_NHI_DONG = "Bạn chỉ có thể tham gia giải Ngoại hạng và Nhi Đồng";
    public static final String TEXT_CHI_CO_THE_THAM_GIA_GIAI_SIEU_CAP_1 = "Bạn chỉ có thể tham gia giải Ngoại hạng và Siêu cấp 1";
    public static final String TEXT_CHI_CO_THE_THAM_GIA_GIAI_SIEU_CAP_2 = "Bạn chỉ có thể tham gia giải Ngoại hạng và Siêu cấp 2";
    public static final String TEXT_CHI_CO_THE_THAM_GIA_GIAI_SIEU_CAP_3 = "Bạn chỉ có thể tham gia giải Ngoại hạng và Siêu cấp 3";
    public static final String TEXT_CHI_CO_THE_THAM_GIA_GIAI_NGOAI_HANG = "Bạn chỉ có thể tham gia giải Ngoại hạng";

    public static final String[] tournamentNames = {"Nhi đồng", "Siêu cấp 1", "Siêu cấp 2", "Siêu cấp 3", "Ngoại hạng"};
    public static final int[] tournamentGems = {200, 400, 600, 800, 0};
   public static final int[] tournamentGolds = {0, 0, 0, 0, 10000};
    public static final int[] tournamentThoiVangs = {0, 0, 0, 0, 5};
}
