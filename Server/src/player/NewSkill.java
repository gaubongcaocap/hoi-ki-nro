package player;

/*
 *
 *
 * @author NTD
 */
import mob.Mob;
import skill.Skill;
import services.SkillService;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NewSkill {

    public static final int TIME_GONG = 2000;
    public static final int TIME_END_24_25 = 3000;
    public static final int TIME_END_26 = 5000;

    private Player player;

    public NewSkill(Player player) {
        this.player = player;
        this.playersTaget = new ArrayList<>();
        this.mobsTaget = new ArrayList<>();
    }

    public Skill skillSelect;

    public byte dir;

    public short _xPlayer;

    public short _yPlayer;

    public short _xObjTaget;

    public short _yObjTaget;

    public List<Player> playersTaget;

    public List<Mob> mobsTaget;

    public boolean isStartSkillSpecial;

    public byte stepSkillSpecial;

    public long lastTimeSkillSpecial;

    public byte typePaint = 0;

    public byte typeItem = 0;

    private void update() {
        if (this.isStartSkillSpecial = true) {
            SkillService.gI().updateSkillSpecial(player);
        }
    }

    public void setSkillSpecial(byte dir, short _xPlayer, short _yPlayer, short _xObjTaget, short _yObjTaget) {
        // 1) Vật phẩm giảm hồi chiêu
        if (player.itemTime != null && player.itemTime.isUseNCD) {
            typeItem = 2;
        } else {
            typeItem = 0;
        }

        // 2) Lấy skill đang chọn & buff currLevel có kẹp trần
        this.skillSelect = this.player.playerSkill.skillSelect;
        if (this.player.isPl() && skillSelect.currLevel < 1000) {
            skillSelect.currLevel = (short) Math.min(1000, skillSelect.currLevel + 10);
            SkillService.gI().sendCurrLevelSpecial(player, skillSelect);
        }

        // 3) Ghi thông tin cơ bản
        this.dir = dir; // -1: trái, 1: phải (từ client)
        this._xPlayer = _xPlayer;
        this._yPlayer = _yPlayer;

        // 4) Tính tầm X theo cấp (dx * point) + padding nhỏ, clamp theo khoảng cách
        // thật
        final int PAD = 24; // bù hình/animation nhẹ
        int point = Math.max(1, skillSelect.point); // tránh 0
        int baseDx = Math.max(0, skillSelect.dx);
        int maxRange = baseDx * point + PAD; // tầm tối đa theo cấp
        int length = _xObjTaget - _xPlayer; // khoảng cách thật (có dấu)

        int desiredAbs;
        if (skillSelect.template.id == Skill.MA_PHONG_BA) {
            desiredAbs = 75; // case đặc biệt như code cũ
        } else {
            desiredAbs = Math.min(Math.abs(length), maxRange); // không vượt tầm, không overshoot
            desiredAbs = Math.max(1, desiredAbs); // tối thiểu 1 để có chuyển động
        }

        int signedDx = (dir >= 0 ? 1 : -1) * desiredAbs; // GIỮ HƯỚNG theo dir, KHÔNG abs()
        if (signedDx > Short.MAX_VALUE)
            signedDx = Short.MAX_VALUE;
        if (signedDx < Short.MIN_VALUE)
            signedDx = Short.MIN_VALUE;
        this._xObjTaget = (short) signedDx;

        // 5) Tính Y (giữ case đặc biệt Liên Hoàn Chưởng), có clamp short
        int yDelta = (skillSelect.template.id == Skill.LIEN_HOAN_CHUONG) ? 30 : skillSelect.dy;
        if (yDelta > Short.MAX_VALUE)
            yDelta = Short.MAX_VALUE;
        if (yDelta < Short.MIN_VALUE)
            yDelta = Short.MIN_VALUE;
        this._yObjTaget = (short) yDelta;

        // 6) Bắt đầu skill
        this.isStartSkillSpecial = true;
        this.stepSkillSpecial = 0;
        this.lastTimeSkillSpecial = System.currentTimeMillis();
        this.start(250); // Giữ delay 250ms như bản 2 (có thể đưa vào data skill nếu cần)
    }

    public void sonPhiPhai() {
        if (player.isAdmin()) {
            typePaint = -1;
        }
    }

    public void closeSkillSpecial() {
        this.isStartSkillSpecial = false;
        this.stepSkillSpecial = 0;
        this.playersTaget.clear();
        this.mobsTaget.clear();
        this.close();
    }

    private Timer timer;
    private TimerTask timerTask;
    private boolean isActive = false;

    private void close() {
        try {
            this.isActive = false;
            this.timer.cancel();
            this.timerTask.cancel();
            this.timer = null;
            this.timerTask = null;
        } catch (Exception e) {
            this.timer = null;
            this.timerTask = null;
        }
    }

    public void start(int leep) {
        if (this.isActive == false) {
            this.isActive = true;
            this.timer = new Timer();
            this.timerTask = new TimerTask() {
                @Override
                public void run() {
                    if (player == null || player.newSkill == null) {
                        close();
                        return;
                    }
                    NewSkill.this.update();
                }
            };
            this.timer.schedule(timerTask, leep, leep);
        }
    }

    public int timeEnd() {
        switch (skillSelect.template.id) {
            case Skill.LIEN_HOAN_CHUONG:
            case Skill.SUPER_KAME:
                return TIME_END_24_25;
            case Skill.MA_PHONG_BA:
                return TIME_END_26;
        }
        return -1;
    }

    public int getdx() {
        switch (skillSelect.template.id) {
            case Skill.LIEN_HOAN_CHUONG:
            case Skill.SUPER_KAME:
                return _xObjTaget;
            case Skill.MA_PHONG_BA:
                return 50;
        }
        return -1;
    }

    public void dispose() {
        this.player = null;
        this.skillSelect = null;
    }

}
