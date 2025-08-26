package panel.shop;

import models.Template.NpcTemplate;

import javax.swing.*;

public class NpcSelectionHandler {

    private final ShopManagerPanel parentPanel;

    public NpcSelectionHandler(ShopManagerPanel parentPanel) {
        this.parentPanel = parentPanel;
    }

    public void onNpcSelected(NpcTemplate npc) {
    parentPanel.loadShopsByNpc(npc);
}

}
