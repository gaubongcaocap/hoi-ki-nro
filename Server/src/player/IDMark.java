package player;

/*
 *
 *
 * @author NTD
 */
import consts.ConstNpc;
import npc.Npc;
import shop.Shop;
import lombok.Data;
import map.Zone;

@Data
public class IDMark {

    private byte isTranhNgoc = -1;
    private int idItemUpTop;
    private int typeChangeMap; //capsule, ngọc rồng đen...
    private int indexMenu; //menu npc
    private int typeInput; //input
    private byte typeLuckyRound; //type lucky round

    private long idPlayThachDau; //id người chơi được mời thách đấu
    private int goldThachDau; //vàng thách đấu
    private long killCharId = -9999;

    private long idEnemy; //id kẻ thù - trả thù

    private Shop shopOpen; //shop người chơi đang mở
    private String tagNameShop; //thẻ tên shop đang mở

    /**
     * loại tàu vận chuyển dùng ;0 - Không dùng ;1 - Tàu vũ trụ ;2 - Dịch chuyển
     * tức thời ;3 - Tàu tenis
     */
    private byte idSpaceShip;

    private int mbv;

    private String captcha;
    private long recaptcha;

    private long lastTimeBan;
    private boolean isBan;

    private int ott;

    //giao dịch
    private int playerTradeId = -1;
    private Player playerTrade;
    private long lastTimeTrade;

    private long lastTimeNotifyTimeHoldBlackBall;
    private long lastTimeHoldBlackBall;
    private int tempIdBlackBallHold = -1;
    private boolean holdBlackBall;

    private int tempIdNamecBallHold = -1;
    private boolean holdNamecBall;

    private boolean loadedAllDataPlayer; //load thành công dữ liệu người chơi từ database

    private long lastTimeChangeFlag;

    //xoc dia
    private int typeDatXD;
    private int slDatXD;
    private Npc npcXD;

    //Tai Xiu
    private int typeDatTX;
    private Npc npcTX;

    //Bau cua
    private int typeDatBC;
    private Npc npcBC;

    //tới tương lai
    private boolean gotoFuture;
    private long lastTimeGoToFuture;

    //ChangeMap Khi gas
    private Zone zoneKhiGasHuyDiet;
    private int xMapKhiGasHuyDiet;
    private int yMapKhiGasHuyDiet;
    private boolean goToKGHD;
    private long lastTimeGoToKGHD;

    private long lastTimeChangeZone;
    private long lastTimeChatGlobal;
    private long lastTimeChatPrivate;

    private long lastTimePickItem;

    private boolean goToBDKB;
    private long lastTimeGoToBDKB;
    private long lastTimeAnXienTrapBDKB;
    private int shopId; //shop ope
    private int shenronType = -1;

    private Npc npcChose; //npc mở

    private byte loaiThe; //loại thẻ nạp

    private boolean acpTrade;

    private long damePST;

    private long lastTimeRevenge;

    private int menuType;

    private int tangHoaType;

    private boolean transactionWP;

    private boolean transactionWVP;

    private long lastTimeCombine;

    private int moneyKeoBuaBao;
    private long timePlayKeoBuaBao;

    private byte keoBuaBaoPlayer;
    private byte keoBuaBaoServer;

    private boolean isGemCSMM;

    public boolean isBaseMenu() {
        return this.indexMenu == ConstNpc.BASE_MENU;
    }

    public byte getTranhNgoc() {
        return isTranhNgoc;
    }

    public void setTranhNgoc(byte tn) {
        this.isTranhNgoc = tn;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public int getShopId() {
        return this.shopId;
    }

    public void dispose() {
        if (this.shopOpen != null) {
            this.shopOpen.dispose();
            this.shopOpen = null;
        }
        this.npcChose = null;
        this.tagNameShop = null;
        this.playerTrade = null;
        this.npcXD = null;
        this.npcTX = null;
        this.npcBC = null;
        this.zoneKhiGasHuyDiet = null;
    }
}
