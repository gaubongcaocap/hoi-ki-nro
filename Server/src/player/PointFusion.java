package player;

/**
 * @build by NTD
 */
public class PointFusion {

    private int HpFusion;
    private int MpFusion;
    private int DameFusion;
    private Player player;

    public PointFusion(Player player) {
        this.player = player;
        this.HpFusion = 0; // Giá trị mặc định
        this.MpFusion = 0;  // Giá trị mặc định
        this.DameFusion = 0; // Giá trị mặc định
    }

    public int getHpFusion() {
        return this.HpFusion;
    }

    public void setHpFusion(int HpFusion) {
        this.HpFusion = HpFusion;
    }

    public int getMpFusion() {
        return this.MpFusion;
    }

    public void setMpFusion(int MpFusion) {
        this.MpFusion = MpFusion;
    }

    public int getDameFusion() {
        return this.DameFusion;
    }

    public void setDameFusion(int DameFusion) {
        this.DameFusion = DameFusion;
    }

    public void update() {

    }

    public void dispose() {
        this.player = null;
    }
}
