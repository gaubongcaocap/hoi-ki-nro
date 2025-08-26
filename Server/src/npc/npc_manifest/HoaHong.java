package npc.npc_manifest;

/**
 *
 * @author NTD
 */
import consts.ConstNpc;
import consts.ConstTask;
import item.Item;
import map.Map;
import npc.Npc;
import player.Player;
import services.InventoryService;
import services.ItemService;
import services.MapService;
import services.NpcService;
import services.Service;
import services.TaskService;
import services.func.ChangeMapService;
import utils.Util;

public class HoaHong extends Npc {

    private final byte COUNT_CHANGE = 7;
    private int count;

    public HoaHong(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    private void haiHoaHong(Player player) {
        if (InventoryService.gI().getCountEmptyBag(player) < 1) {
            services.Service.gI().sendThongBaoOK(player, "Cần 1 ô hành trang trống");
            return;
        }
        Item hoaHong = ItemService.gI().createNewItem((short) 1530);
        hoaHong.quantity = utils.Util.nextInt(1, 3);
        InventoryService.gI().addItemBag(player, hoaHong);
        services.Service.gI().sendThongBao(player, " " + hoaHong.template.name + " đẹp quá, đem đi tặng người yêu bao hợp lí");
        InventoryService.gI().sendItemBag(player);
        count++;
        if (this.count >= COUNT_CHANGE) {
            count = 0;
            this.map.npcs.remove(this);
            Map mapHoahong = MapService.gI().getMapForHoaHong();
            this.mapId = mapHoahong.mapId;
            this.cx = Util.nextInt(100, mapHoahong.mapWidth - 100);
            this.cy = mapHoahong.yPhysicInTop(this.cx, 0);
            this.map = mapHoahong;
            this.map.npcs.add(this);
            System.out.println("hoa hồng mọc tại map " + mapHoahong.mapName);
        }

    }

    @Override
    public void openBaseMenu(Player player) {

        player.iDMark.setIndexMenu(ConstNpc.BASE_MENU);
        if (this.mapId != player.zone.map.mapId) {
            Service.gI().sendThongBao(player, "Khóm hoa hồng không thể hái được nữa, vui lòng chuyển qua khóm hoa khác");
            Service.gI().hideWaitDialog(player);
            return;
        }
        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                "Hoa hồng", "Hái", "Bỏ qua");

    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (player.iDMark.isBaseMenu()) {
            switch (select) {
                case 0 -> {
                    haiHoaHong(player);
                }
                case 1 -> {

                }
                default ->
                    Service.gI().sendThongBao(player, "Không thể thực hiện");
            }
        }
    }
}
