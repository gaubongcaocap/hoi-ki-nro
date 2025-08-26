package boss.boss_manifest.Android;

/*
 *
 *
 * @author NTD
 */

import boss.Boss;
import boss.BossID;
import boss.BossStatus;
import boss.BossesData;
import item.Item;
import java.util.List;
import java.util.Random;

import map.ItemMap;
import player.Player;
import services.ItemService;
import services.Service;
import services.TaskService;
import utils.Util;

public class Pic extends Boss {

    public Pic() throws Exception {
        super(BossID.PIC, BossesData.PIC);
    }

     @Override
    public void reward(Player plKill) {
        int x = this.location.x;
        int y = this.zone.map.yPhysicInTop(x, this.location.y - 24);
     
        int quantity = Util.nextInt(20000, 30000);
        ItemMap itemMap = new ItemMap(this.zone, 190, quantity, x, y, plKill.id);
        Item item = ItemService.gI().createNewItem((short) 190);
        Service.gI().dropItemMap(zone, itemMap);

        if (Util.isTrue(10, 100)) {
            int group = Util.nextInt(1, 100) <= 70 ? 0 : 1;  // 70% chọn Áo Quần Giày (group = 0), 30% chọn Găng Rada (group = 1)

            // Các vật phẩm rơi từ nhóm Áo Quần Giày và Găng Rada
            int[][] drops = {
                {230, 231, 232, 234, 235, 236, 238, 239, 240, 242, 243, 244, 246, 247, 248, 250, 251, 252, 266, 267, 268, 270, 271, 272, 274, 275, 276}, // Áo Quần Giày
                {254, 255, 256, 258, 259, 260, 262, 263, 264, 278, 279, 280} // Găng Rada
            };

            
            int dropOptional = drops[group][Util.nextInt(0, drops[group].length - 1)];

            ItemMap optionalItemMap = new ItemMap(this.zone, dropOptional, 1, x, y, plKill.id);
            Item optionalItem = ItemService.gI().createNewItem((short) dropOptional);
            List<Item.ItemOption> optionalOps = ItemService.gI().getListOptionItemShop((short) dropOptional);
            optionalOps.forEach(option -> option.param = (int) (option.param * Util.nextInt(100, 115) / 100.0));
            optionalItemMap.options.addAll(optionalOps);         
            int rand = Util.nextInt(1, 100);
            int value = 0;
            if (rand <= 80) {
                value = Util.nextInt(1, 3); 
            } 
            optionalItemMap.options.add(new Item.ItemOption(107, value));

            Service.gI().dropItemMap(zone, optionalItemMap);
        }

        if (Util.isTrue(80, 100)) {
            int[] items = Util.isTrue(50, 100) ? new int[] { 18, 19, 20 }
                    : new int[] { 1066, 1067, 1068, 1069, 1070, 1229 };
            int randomItem = items[new Random().nextInt(items.length)];
            Service.gI().dropItemMap(this.zone, new ItemMap(this.zone, randomItem, 1,
                    this.location.x, this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24), plKill.id));
        }

        Service.gI().dropItemMap(this.zone, new ItemMap(this.zone, 1790, Util.nextInt(1, 5),
                this.location.x, this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24), plKill.id));
 
        TaskService.gI().checkDoneTaskKillBoss(plKill, this);
    }
    @Override
    public void autoLeaveMap() {
        if (Util.canDoWithTime(st, 900000)) {
            this.leaveMapNew();
        }
        if (this.zone != null && this.zone.getNumOfPlayers() > 0) {
            st = System.currentTimeMillis();
        }
    }

    @Override
    public void joinMap() {
        super.joinMap(); //To change body of generated methods, choose Tools | Templates.
        st = System.currentTimeMillis();
    }
    private long st;

    @Override
    public void doneChatS() {
        this.changeStatus(BossStatus.AFK);
    }

    @Override
    public void doneChatE() {
        if (this.parentBoss == null) {
            return;
        }
        this.parentBoss.changeStatus(BossStatus.ACTIVE);
    }

}
