package player;

/**
 *
 * @author NTD
 */
import consts.ConstTaskBadges;
import item.Item;
import java.util.ArrayList;
import java.util.List;
import jdbc.daos.NDVSqlFetcher;
import models.SuperRank.SuperRankService;
import services.InventoryService;
import services.ItemService;
import services.NpcService;
import services.Service;
import task.Badges.BadgesTaskService;
import utils.TimeUtil;

public class SuperRank {

    private Player player;
    public int rank;
    public int win;
    public int lose;
    public List<String> history;
    public List<Long> lastTime;
    public long lastTimePK;
    public long lastTimeReward;
    public int ticket = 3;

    public SuperRank(Player player) {
        this.player = player;
        this.history = new ArrayList<>();
        this.lastTime = new ArrayList<>();
    }

    public void history(String text, long lastTime) {
        if (this.history.size() > 4) {
            this.history.remove(0);
            this.lastTime.remove(0);
        }
        this.history.add(text);
        this.lastTime.add(lastTime);
    }

    public void reward() {
        int rw = SuperRankService.gI().reward(rank);
        if (rw != -1) {
            NpcService.gI().createTutorial(player, -1, "Bạn đang ở TOP " + rank + " võ đài Siêu Hạng, được thưởng " + rw + " Thỏi vàng");
            if (rank == 1) {
                BadgesTaskService.updateCountBagesTask(player, ConstTaskBadges.CAO_THU_SIEU_HANG, 1);
            }
            rwThoiVang(player, rw);
        }
        lastTimeReward = TimeUtil.currentTimeMillisPlus11();
    }

    public void rwThoiVang(Player pl, int sl) {
        Item thoivang = ItemService.gI().createNewItem((short) 457);
        thoivang.quantity = sl;
        thoivang.addOptionParam(30, 1);
        if (InventoryService.gI().getCountEmptyBag(pl) <= 0) {
            pl.inventory.itemsMailBox.add(thoivang);
            NDVSqlFetcher.updateMailBox(pl);
            Service.gI().sendThongBao(pl, "Hành trang đầy, Bạn đã nhận " + thoivang.template.name + " về hộp thư");
        } else {
            InventoryService.gI().addItemBag(pl, thoivang);
        }
        InventoryService.gI().sendItemBag(pl);
    }

    public void dispose() {
        history.clear();
        lastTime.clear();
        win = -1;
        lose = -1;
        lastTimePK = -1;
        player = null;
    }
}
