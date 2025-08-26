package boss.miniboss;

/*
 * @Author: NgocRongWhis
 * @Description: Ngọc Rồng Whis - Máy Chủ Chuẩn Teamobi 2024
 * @Group Zalo: https://zalo.me/g/qabzvn331
 */


import boss.*;
import java.util.ArrayList;
import java.util.List;
import map.Zone;
import player.Player;
import services.func.ChangeMapService;
import utils.Logger;
import utils.Util;

public class SoiHecQuyn extends Boss {

    private long lastTimeDrop;
    private long st;
    private int timeLeave=300000;
    private boolean Gwen_KiemTraNhatXuong = false;
    private long Gwen_ThoiGianNhatXuong = 0;
    private long lastTimRestPawn;

    public SoiHecQuyn() throws Exception {
        super(BossID.SOI_HEC_QUYN1, BossesData.SOI_HEC_QUYN);
    }

    @Override
    public void joinMap() {
        super.joinMap();
        st = System.currentTimeMillis();
    }


    @Override
    public void chatM() {
        if (this.data[this.currentLevel].getTextM().length == 0) {
            return;
        }
        if (!Util.canDoWithTime(this.lastTimeChatM, this.timeChatM)) {
            return;
        }
        String textChat = this.data[this.currentLevel].getTextM()[Util.nextInt(0, this.data[this.currentLevel].getTextM().length - 1)];
        int prefix = Integer.parseInt(textChat.substring(1, textChat.lastIndexOf("|")));
        textChat = textChat.substring(textChat.lastIndexOf("|") + 1);
        this.chat(prefix, textChat);
        this.lastTimeChatM = System.currentTimeMillis();
        this.timeChatM = Util.nextInt(3000, 20000);
    }

    @Override
    public void active() {
        this.attack();
    }

    @Override
    public void autoLeaveMap() {
        if (Util.canDoWithTime(st, timeLeave)) {
            this.leaveMapNew();
        }
    }

    @Override
    public void leaveMap() {
        ChangeMapService.gI().exitMap(this);
        this.lastZone = null;
        this.lastTimeRest = System.currentTimeMillis();
        this.changeStatus(BossStatus.REST);

    }

    public void NhatXuong() {
        Gwen_KiemTraNhatXuong = true;
        Gwen_ThoiGianNhatXuong = System.currentTimeMillis();

    }

    public boolean Gwen_KiemTraNhatXuong() {
        return Gwen_KiemTraNhatXuong;
    }

    @Override
    public void attack() {
        if (Util.canDoWithTime(this.lastTimeAttack, 100)) {
            this.lastTimeAttack = System.currentTimeMillis();
            try {
                Player pl = getPlayerAttack();
                if (pl == null || pl.location == null) {
                    return;
                }
                this.playerSkill.skillSelect = this.playerSkill.skills.get(Util.nextInt(0, this.playerSkill.skills.size() - 1));
                if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
                    if (Util.isTrue(5, 20) && Util.getDistance(this, pl) > 50) {
                        if (Util.isTrue(5, 20)) {
                            this.moveTo(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 200)),
                                    Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 70));
                        } else {
                            this.moveTo(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(10, 40)), pl.location.y);
                        }
                    } else if (Util.getDistance(this, pl) <= 50) {

                    }
                    checkPlayerDie(pl);
                } else {
                    if (Util.isTrue(1, 2)) {
                        this.moveToPlayer(pl);
                    }
                }
                if (Gwen_ThoiGianNhatXuong > 0) {
                    if (Util.canDoWithTime(Gwen_ThoiGianNhatXuong, 5000)) {
                        Gwen_ThoiGianNhatXuong = 0;
                        Gwen_KiemTraNhatXuong = false;
                    }
                }
            } catch (Exception ex) {
            }
        }
    }


    @Override
    public synchronized long injured(Player plAtt, long damage, boolean piercing, boolean isMobAttack) {
        return 0;
    }

    @Override
    public void reward(Player plKill) {
    }
}
