package panel.shop;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.stream.Collectors;
import models.Template.NpcTemplate;
import server.Manager;
import shop.Shop;
import java.util.List;
import java.util.stream.Collectors;

public class ShopManagerPanel extends JPanel {

    private JList<NpcTemplate> npcList;
    private DefaultListModel<NpcTemplate> npcListModel;
    private JPanel shopButtonPanel;
    private JPanel tabButtonPanel;
    private JPanel itemDisplayPanel;
    private NpcSelectionHandler npcSelectionHandler;
    private java.util.List<JButton> shopButtons;

    public ShopManagerPanel() {
        setLayout(new BorderLayout(10, 10));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // === Trái: Danh sách NPC ===
        npcListModel = new DefaultListModel<>();
        for (NpcTemplate npc : Manager.NPC_TEMPLATES) {
            npcListModel.addElement(npc);
        }

        npcList = new JList<>(npcListModel);
        npcList.setCellRenderer(new NpcListCellRenderer());
        npcList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        npcList.setBackground(new Color(45, 45, 45, 180));
        npcList.setForeground(Color.WHITE);
        npcList.setSelectionBackground(new Color(70, 130, 180));
        npcList.setSelectionForeground(Color.WHITE);
        npcList.setFixedCellHeight(50);
        npcList.setBorder(BorderFactory.createTitledBorder("NPC"));

        npcList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int index = npcList.locationToIndex(e.getPoint());
                if (index >= 0) {
                    NpcTemplate selectedNpc = npcListModel.get(index);
                    npcSelectionHandler.onNpcSelected(selectedNpc);
                }
            }
        });

        JScrollPane npcScrollPane = new JScrollPane(npcList);
        npcScrollPane.setPreferredSize(new Dimension(220, 0));
        npcScrollPane.setOpaque(false);
        npcScrollPane.getViewport().setOpaque(false);
        npcScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(npcScrollPane, BorderLayout.WEST);
        npcSelectionHandler = new NpcSelectionHandler(this);
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setOpaque(false);

        // Panel shop
        shopButtonPanel = new JPanel(new GridLayout(2, 4, 5, 5));
        shopButtonPanel.setOpaque(false);

// Khởi tạo danh sách button để quản lý dễ hơn
        shopButtons = new ArrayList<>(); // <-- khai báo biến này ở đầu class

        for (int i = 1; i <= 8; i++) {
            JButton shopButton = createTabButton("S" + i);
            shopButton.setPreferredSize(new Dimension(50, 25));
            shopButtons.add(shopButton); // <-- thêm vào list
            shopButtonPanel.add(shopButton);
        }

        rightPanel.add(shopButtonPanel);

        // Panel tab
        tabButtonPanel = new JPanel(new GridLayout(1, 5, 5, 5));
        tabButtonPanel.setOpaque(false);
        for (int i = 1; i <= 5; i++) {
            JButton tabButton = createTabButton("[" + i + "]");
            tabButton.setPreferredSize(new Dimension(40, 25));
            tabButtonPanel.add(tabButton);
        }
        rightPanel.add(tabButtonPanel);

        // Panel items
        itemDisplayPanel = new JPanel();
        itemDisplayPanel.setLayout(new BoxLayout(itemDisplayPanel, BoxLayout.Y_AXIS));
        itemDisplayPanel.setBackground(new Color(50, 50, 50, 180));
        itemDisplayPanel.setBorder(BorderFactory.createTitledBorder("Danh sách vật phẩm"));

        JScrollPane itemScrollPane = new JScrollPane(itemDisplayPanel);
        itemScrollPane.setOpaque(false);
        itemScrollPane.getViewport().setOpaque(false);
        itemScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        itemScrollPane.setPreferredSize(new Dimension(0, 350));

        rightPanel.add(itemScrollPane);
        add(rightPanel, BorderLayout.CENTER);
    }

    private JButton createTabButton(String label) {
        JButton button = new JButton(label);
        button.setFocusPainted(false);
        button.setBackground(new Color(60, 60, 60, 200));
        button.setForeground(Color.WHITE);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JPanel createItemRow(String itemName) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setOpaque(false);
        panel.add(new JLabel(itemName));
        panel.add(createActionButton("Sửa"));
        panel.add(createActionButton("Xoá"));
        panel.add(createActionButton("-"));
        panel.add(createActionButton("+"));
        return panel;
    }

    private JButton createActionButton(String label) {
        JButton button = new JButton(label);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(new Color(70, 130, 180, 220));
        button.setForeground(Color.WHITE);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }


    public void loadShopsByNpc(NpcTemplate npc) {
        // Lọc các shop ứng với NPC
        List<Shop> npcShops = Manager.SHOPS.stream()
                .filter(shop -> shop.npcId == npc.id)
                .collect(Collectors.toList());

        // Cập nhật nút shop tương ứng
        for (int i = 0; i < shopButtons.size(); i++) {
            JButton button = shopButtons.get(i);
            if (i < npcShops.size()) {
                Shop shop = npcShops.get(i);
                button.setText(shop.tagName);
                button.setVisible(true);
                button.putClientProperty("shop", shop); // gắn shop để xử lý sau này
            } else {
                button.setText("S" + (i + 1));
                button.setVisible(false); // ẩn nếu vượt quá số shop
                button.putClientProperty("shop", null);
            }
        }

        // Clear tab & item nếu muốn
        tabButtonPanel.setVisible(false);
        itemDisplayPanel.removeAll();
        itemDisplayPanel.revalidate();
        itemDisplayPanel.repaint();
    }

    public void demoAddItems(java.util.List<String> items) {
        itemDisplayPanel.removeAll();
        for (String item : items) {
            itemDisplayPanel.add(createItemRow(item));
        }
        itemDisplayPanel.revalidate();
        itemDisplayPanel.repaint();
    }
}
