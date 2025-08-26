package Bot;

import java.util.Random;
import mob.Mob;
import services.PlayerService;
import services.SkillService;
import utils.Util;

public class Mobb {

    private Mob mAttack;

    public long lastTimeChanM;

    public Bot bot;

    public Mobb(Bot b) {
        this.bot = b;
    }

    public void update() {
        this.Attack();
        this.chanGeMap();
    }

    public void GetMobAttack() {
        if (this.bot.zone.mobs.size() >= 1) {
            if (this.mAttack == null || this.mAttack.isDie()) {
                mAttack = this.bot.zone.mobs.get(new Random().nextInt(this.bot.zone.mobs.size()));
            }
        }
    }

    public void Attack() {
        this.GetMobAttack();
        // if (Util.isTrue(50, 100)) {
        //     this.bot.playerSkill.skillSelect = this.bot.playerSkill.skills.get(0);
        // } else {
        //     this.bot.playerSkill.skillSelect = this.bot.playerSkill.skills.get(0);
        // }

        if (this.bot.playerSkill != null && this.bot.playerSkill.skills != null && !this.bot.playerSkill.skills.isEmpty()) {
            int index = Util.nextInt(0, bot.playerSkill.skills.size() - 1);
            this.bot.playerSkill.skillSelect = this.bot.playerSkill.skills.get(index);
        }

        if (this.mAttack != null && this.bot.UseLastTimeSkill()) {
            PlayerService.gI().playerMove(this.bot, this.mAttack.location.x, this.mAttack.location.y);
            SkillService.gI().useSkill(this.bot, null, this.mAttack, -1, null);
        }
    }

  public void chanGeMap() {
    long currentTime = System.currentTimeMillis();
    
    // Giảm thời gian đổi map xuống 6–8 phút để bot di chuyển linh hoạt hơn
    long randomTime = 6L * 60 * 1000 + new Random().nextInt(2 * 60 * 1000); // Ngẫu nhiên khoảng 6-8 phút

    // Kiểm tra xem thời gian đã trôi qua đủ chưa
    if (currentTime - this.lastTimeChanM >= randomTime) {
        // Đổi map
        this.bot.joinMap();

        // Cập nhật lại thời gian thay đổi map lần cuối
        this.lastTimeChanM = currentTime;
    }
}

}



