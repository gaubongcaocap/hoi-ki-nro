package player;
/**
 * @build by Emti
 */

public class PlayerEvent {

    private boolean isUseQuanHoa;

    public boolean isIsUseQuanHoa() {
        return isUseQuanHoa;
    }

    public void setIsUseQuanHoa(boolean isUseQuanHoa) {
        this.isUseQuanHoa = isUseQuanHoa;
    }

    public boolean isIsUseBonTam() {
        return isUseBonTam;
    }

    public void setIsUseBonTam(boolean isUseBonTam) {
        this.isUseBonTam = isUseBonTam;
    }

    public int getDiemTichLuy() {
        return diemTichLuy;
    }

    public void setDiemTichLuy(int diemTichLuy) {
        this.diemTichLuy = diemTichLuy;
    }

    public int getMocNapDaNhan() {
        return mocNapDaNhan;
    }

    public void setMocNapDaNhan(int mocNapDaNhan) {
        this.mocNapDaNhan = mocNapDaNhan;
    }

    public int getEventPoint() {
        return eventPoint;
    }

    public void setEventPoint(int eventPoint) {
        this.eventPoint = eventPoint;
    }

    public int getEventPointBHM() {
        return eventPointBHM;
    }

    public void setEventPointBHM(int eventPointBHM) {
        this.eventPointBHM = eventPointBHM;
    }

    public int getVnd() {
        return vnd;
    }

    public void setVnd(int vnd) {
        this.vnd = vnd;
    }

    public int getKillBossPoint() {
        return killBossPoint;
    }

    public void setKillBossPoint(int killBossPoint) {
        this.killBossPoint = killBossPoint;
    }

    public int getEventPointNHS() {
        return eventPointNHS;
    }

    public void setEventPointNHS(int eventPointNHS) {
        this.eventPointNHS = eventPointNHS;
    }

    public int getEventPointQuai() {
        return eventPointQuai;
    }

    public void setEventPointQuai(int eventPointQuai) {
        this.eventPointQuai = eventPointQuai;
    }

    public int getEventPointQuyLao() {
        return eventPointQuyLao;
    }

    public void setEventPointQuyLao(int eventPointQuyLao) {
        this.eventPointQuyLao = eventPointQuyLao;
    }

    public int getEventPointMoc() {
        return eventPointMoc;
    }

    public void setEventPointMoc(int eventPointMoc) {
        this.eventPointMoc = eventPointMoc;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public boolean isCookingChungCake() {
        return cookingChungCake;
    }

    public void setCookingChungCake(boolean cookingChungCake) {
        this.cookingChungCake = cookingChungCake;
    }

    public int getTimeCookChungCake() {
        return timeCookChungCake;
    }

    public void setTimeCookChungCake(int timeCookChungCake) {
        this.timeCookChungCake = timeCookChungCake;
    }

    public boolean isCookingTetCake() {
        return cookingTetCake;
    }

    public void setCookingTetCake(boolean cookingTetCake) {
        this.cookingTetCake = cookingTetCake;
    }

    public int getTimeCookTetCake() {
        return timeCookTetCake;
    }

    public void setTimeCookTetCake(int timeCookTetCake) {
        this.timeCookTetCake = timeCookTetCake;
    }

    public boolean isReceivedLuckyMoney() {
        return receivedLuckyMoney;
    }

    public void setReceivedLuckyMoney(boolean receivedLuckyMoney) {
        this.receivedLuckyMoney = receivedLuckyMoney;
    }
    private boolean isUseBonTam;
    private int diemTichLuy;
    private int mocNapDaNhan;
    private int eventPoint;
    private int eventPointBHM;
    private int killBossPoint;
    private int vnd;
    private int eventPointNHS;
    private int eventPointQuai;
    private int eventPointQuyLao;
    private int eventPointMoc;
    private Player player;
    private boolean cookingChungCake;
    private int timeCookChungCake;
    private boolean cookingTetCake;
    private int timeCookTetCake;
    private boolean receivedLuckyMoney;

    public PlayerEvent(Player player) {
        this.player = player;
    }

    public void addKillBossPoint(int num) {
        killBossPoint += num;
    }

    public void subKillBossPoint(int num) {
        killBossPoint -= num;
    }

    public void addEventPoint(int num) {
        eventPoint += num;
    }

    public void subEventPoint(int num) {
        eventPoint -= num;
    }

    public void addEventPointBHM(int num) {
        eventPointBHM += num;
    }

    public void subEventPointBHM(int num) {
        eventPointBHM -= num;
    }

    public void addEventPointNHS(int num) {
        eventPointNHS += num;
    }

    public void subEventPointNHS(int num) {
        eventPointNHS -= num;
    }

    public void addEventPointQuai(int num) {
        eventPointQuai += num;
    }

    public void subEventPointQuai(int num) {
        eventPointQuai -= num;
    }

    public void subEventPointQuyLao(int num) {
        eventPointQuyLao -= num;
    }

    public void addEventPointQuyLao(int num) {
        eventPointQuyLao += num;
    }

    public void subEventPointMoc(int num) {
        eventPointMoc -= num;
    }

    public void addEventPointMoc(int num) {
        eventPointMoc += num;
    }

    public void update() {

    }

}
