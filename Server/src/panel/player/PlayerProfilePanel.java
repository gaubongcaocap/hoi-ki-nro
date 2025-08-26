package panel.player;

import item.Item;
import models.Template.HeadAvatar;
import server.Manager;
import services.ItemService;
import jdbc.DBConnecter;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.border.CompoundBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import org.json.simple.JSONObject;
import panel.UIUtils;

public class PlayerProfilePanel extends JPanel {

    private final long playerId;
    private String name;
    private int head;
    private int gender;
    private long power;
    private final List<Item> petItemsBag = new ArrayList<>();
    private final List<Item> itemsBag = new ArrayList<>();
    private final List<Item> itemsBox = new ArrayList<>();
    private final List<Item> itemsBody = new ArrayList<>();
    private int currentBagPage = 0;
    private int currentBoxPage = 0;
    private long hpGoc, mpGoc, damageGoc;
    private int giapGoc;
    private byte crit;
    private long hp, mp;

    private JPanel bagPanel;
    private JPanel boxPanel;

    public PlayerProfilePanel(long playerId) {
        this.playerId = playerId;
        setLayout(new BorderLayout());
        setOpaque(false);

        fetchPlayerData();

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        mainPanel.add(createHeaderPanel());
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(createMiddlePanel());
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(createInventoryPanel());

        add(mainPanel, BorderLayout.CENTER);
    }

    private void fetchPlayerData() {
        try (
                Connection con = DBConnecter.getConnectionServer(); PreparedStatement ps = con.prepareStatement(
                "SELECT name, head, gender, data_point, items_bag, items_box, items_body, pet FROM player WHERE id = ?"
        )) {
            ps.setLong(1, playerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    name = rs.getString("name");
                    head = rs.getInt("head");
                    gender = rs.getInt("gender");
                    parsePower(rs.getString("data_point"));
                    parseItems(rs.getString("items_bag"), itemsBag);
                    parseItems(rs.getString("items_box"), itemsBox);
                    parseItemsBody(rs.getString("items_body"));
                    parsePetItems(rs.getString("pet"));

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseItems(String itemStr, List<Item> itemList) {
        JSONArray array = (JSONArray) JSONValue.parse(itemStr);
        for (Object obj : array) {
            itemList.add(parseItem((JSONArray) JSONValue.parse(obj.toString())));
        }
    }

    private void parsePower(String dataPointStr) {
        JSONArray dataPoint = (JSONArray) JSONValue.parse(dataPointStr);

        power = Long.parseLong(String.valueOf(dataPoint.get(1)));
        hp = Long.parseLong(String.valueOf(dataPoint.get(2)));
        mp = Long.parseLong(String.valueOf(dataPoint.get(4)));
        hpGoc = Long.parseLong(String.valueOf(dataPoint.get(5)));
        mpGoc = Long.parseLong(String.valueOf(dataPoint.get(6)));
        damageGoc = Long.parseLong(String.valueOf(dataPoint.get(7)));
        giapGoc = Integer.parseInt(String.valueOf(dataPoint.get(8)));
        crit = Byte.parseByte(String.valueOf(dataPoint.get(9)));
        // critDragon bỏ qua
    }

    private void parseItemsBody(String itemStr) {
        JSONArray dataArray = (JSONArray) JSONValue.parse(itemStr);
        for (Object obj : dataArray) {
            JSONArray dataItem = (JSONArray) JSONValue.parse(obj.toString());
            Item item = parseItem(dataItem);

            // Nếu hết hạn thì trả về item null
            if (ItemService.gI().isOutOfDateTime(item)) {
                item = ItemService.gI().createItemNull();
            }

            itemsBody.add(item);
        }

        // Bảo đảm đủ 10 ô
        while (itemsBody.size() < 10) {
            itemsBody.add(ItemService.gI().createItemNull());
        }
    }

    private Item parseItem(JSONArray dataItem) {
        short tempId = Short.parseShort(String.valueOf(dataItem.get(0)));
        if (tempId == -1) {
            return ItemService.gI().createItemNull();
        }

        Item item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataItem.get(1))));
        JSONArray options = (JSONArray) JSONValue.parse(String.valueOf(dataItem.get(2)).replaceAll("\"", ""));
        for (Object opt : options) {
            JSONArray optArray = (JSONArray) JSONValue.parse(opt.toString());
            item.itemOptions.add(new Item.ItemOption(
                    Integer.parseInt(String.valueOf(optArray.get(0))),
                    Integer.parseInt(String.valueOf(optArray.get(1)))
            ));
        }
        item.createTime = Long.parseLong(String.valueOf(dataItem.get(3)));
        return ItemService.gI().isOutOfDateTime(item) ? ItemService.gI().createItemNull() : item;
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setOpaque(false);

        JLabel avatar = new JLabel(getHeadIcon(head));
        avatar.setPreferredSize(new Dimension(100, 100));
        panel.add(avatar, BorderLayout.WEST);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);
        info.add(createLabel("Tên: " + name, 18, Color.CYAN));
        info.add(createLabel("Hành tinh: " + getPlanetName(gender), 14, Color.WHITE));
        info.add(createLabel("Sức mạnh: " + String.format("%,d", power), 14, Color.WHITE));

        panel.add(info, BorderLayout.CENTER);

        // Dùng hàm chung hiển thị hành trang pet
        JPanel petPanel = createItemEquipPanel(petItemsBag, "Hành trang Pet");
        petPanel.setPreferredSize(new Dimension(300, 130));
        panel.add(petPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createMiddlePanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 0));
        panel.setOpaque(false);

        panel.add(createStatsPanel());
        panel.add(createItemEquipPanel(itemsBody, "Trang bị")); // dùng lại hàm chung

        return panel;
    }

    private JPanel createItemEquipPanel(List<Item> items, String title) {
        JPanel panel = new JPanel(new GridLayout(2, 5, 5, 5));
        panel.setOpaque(false);

        // Tạo viền + tiêu đề
        TitledBorder titled = BorderFactory.createTitledBorder(title);
        titled.setTitleColor(Color.WHITE);
        titled.setTitleFont(new Font("Segoe UI", Font.BOLD, 14));

        MatteBorder matte = new MatteBorder(1, 1, 1, 1, new Color(255, 255, 255, 100));
        CompoundBorder border = BorderFactory.createCompoundBorder(matte, titled);
        panel.setBorder(border);

        for (int i = 0; i < 10; i++) {
            JPanel slot = new JPanel(new BorderLayout());
            slot.setPreferredSize(new Dimension(48, 48));
            slot.setBackground(new Color(50, 50, 50)); // nền xám đậm nhẹ
            slot.setBorder(BorderFactory.createLineBorder(Color.WHITE));

            if (i < items.size()) {
                Item item = items.get(i);
                if (item != null && item.template != null) {
                    String path = "data/icon/x4/" + item.template.iconID + ".png";
                    ImageIcon icon = new UIUtils().getScaledIcon(path, 25, 25);

                    JLabel iconLabel = new JLabel(icon);
                    iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    iconLabel.setToolTipText(item.getInfo());
                    iconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                    Item finalItem = item;
                    iconLabel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            Window window = SwingUtilities.getWindowAncestor(panel);
                            if (window instanceof Frame) {
                                ItemInfoDialog dialog = new ItemInfoDialog(
                                        (Frame) window,
                                        finalItem,
                                        title,
                                        name,
                                        (int) playerId,
                                        finalItem.itemOptions
                                );
                                dialog.showDialog();
                            }
                        }
                    });
                    slot.add(iconLabel, BorderLayout.CENTER);

                    // Tính tổng sao từ option 102 và 107
                    int starCount = 0;
                    for (Item.ItemOption opt : finalItem.itemOptions) {
                        if (opt.optionTemplate.id == 107) {
                            starCount += opt.param;
                        }
                    }

                    JLabel starLabel = new JLabel(starCount + "s");
                    starLabel.setForeground(Color.CYAN);
                    starLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    starLabel.setHorizontalAlignment(SwingConstants.CENTER);

                    JPanel starPanel = new JPanel(new BorderLayout());
                    starPanel.setOpaque(false);
                    starPanel.add(starLabel, BorderLayout.CENTER);
                    slot.add(starPanel, BorderLayout.SOUTH);

                }
            }

            panel.add(slot);
        }

        return panel;
    }

    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setOpaque(false);

        TitledBorder titled = BorderFactory.createTitledBorder("Thông số nhân vật");
        titled.setTitleColor(Color.WHITE);
        titled.setTitleFont(new Font("Segoe UI", Font.BOLD, 14));

        MatteBorder matte = new MatteBorder(1, 1, 1, 1, new Color(255, 255, 255, 100));
        CompoundBorder border = BorderFactory.createCompoundBorder(matte, titled);
        statsPanel.setBorder(border);

        // Các chỉ số gốc có icon (kéo sát khoảng cách)
        statsPanel.add(createStatRow("data\\icon\\x4\\567.png", "HP gốc: " + String.format("%,d", hpGoc), new Color(0, 102, 255)));
        statsPanel.add(Box.createVerticalStrut(1));

        statsPanel.add(createStatRow("data\\icon\\x4\\569.png", "KI gốc: " + String.format("%,d", mpGoc), new Color(0, 153, 255)));
        statsPanel.add(Box.createVerticalStrut(1));

        statsPanel.add(createStatRow("data\\icon\\x4\\568.png", "Sức đánh gốc: " + String.format("%,d", damageGoc), new Color(255, 51, 0)));
        statsPanel.add(Box.createVerticalStrut(1));

        statsPanel.add(createStatRow("data\\icon\\x4\\719.png", "Giáp gốc: " + giapGoc, new Color(255, 153, 0)));
        statsPanel.add(Box.createVerticalStrut(1));

        statsPanel.add(createStatRow("data\\icon\\x4\\721.png", "Chí mạng gốc: " + crit + "%", new Color(255, 102, 0)));

        return statsPanel;
    }

    private JLabel createStatLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return label;
    }

    private JPanel createInventoryPanel() {
        JPanel inventoryPanel = new JPanel();
        inventoryPanel.setLayout(new BoxLayout(inventoryPanel, BoxLayout.Y_AXIS));
        inventoryPanel.setOpaque(false);

        inventoryPanel.add(Box.createVerticalStrut(10)); // Khoảng cách

        // Tạo nút chuyển giữa Hành trang và Rương đồ
        JPanel switchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        switchPanel.setOpaque(false);

        JToggleButton btnBag = new JToggleButton("Hành trang");
        JToggleButton btnBox = new JToggleButton("Rương đồ");

        btnBag.setPreferredSize(new Dimension(100, 28));
        btnBox.setPreferredSize(new Dimension(100, 28));
        btnBag.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnBox.setFont(new Font("Segoe UI", Font.BOLD, 12));

        ButtonGroup group = new ButtonGroup();
        group.add(btnBag);
        group.add(btnBox);
        btnBag.setSelected(true);

        JPanel dynamicPanel = new JPanel(new BorderLayout());
        dynamicPanel.setOpaque(false);

        // Ban đầu là hiển thị bag
        refreshItemGrid(itemsBag, currentBagPage, dynamicPanel, true);

        btnBag.addActionListener(e -> {
            currentBagPage = 0;
            refreshItemGrid(itemsBag, currentBagPage, dynamicPanel, true);
        });

        btnBox.addActionListener(e -> {
            currentBoxPage = 0;
            refreshItemGrid(itemsBox, currentBoxPage, dynamicPanel, false);
        });

        switchPanel.add(btnBag);
        switchPanel.add(btnBox);

        inventoryPanel.add(switchPanel);
        inventoryPanel.add(dynamicPanel);

        return inventoryPanel;
    }

    private void refreshItemGrid(List<Item> items, int currentPage, JPanel panel, boolean isBag) {
        panel.removeAll();

        int totalPages = (items.size() + 23) / 24;
        int start = currentPage * 24;
        int end = Math.min(start + 24, items.size());
        List<Item> pageItems = items.subList(start, end);

        String source = isBag ? "Hành trang" : "Rương đồ";
        JPanel grid = createItemGrid(pageItems, source, isBag);

        JPanel gridContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        gridContainer.setOpaque(false);
        gridContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        gridContainer.add(grid);

        // Navigation panel
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 4));
        navPanel.setOpaque(false);

        JButton prev = new JButton("←");
        JButton next = new JButton("→");

        prev.setEnabled(currentPage > 0);
        next.setEnabled(currentPage < totalPages - 1);

        prev.addActionListener(e -> {
            if (isBag) {
                currentBagPage--;
            } else {
                currentBoxPage--;
            }
            refreshItemGrid(items, isBag ? currentBagPage : currentBoxPage, panel, isBag);
        });

        next.addActionListener(e -> {
            if (isBag) {
                currentBagPage++;
            } else {
                currentBoxPage++;
            }
            refreshItemGrid(items, isBag ? currentBagPage : currentBoxPage, panel, isBag);
        });

        navPanel.add(prev);
        for (int i = 0; i < totalPages; i++) {
            final int page = i;
            JButton btn = new JButton(String.valueOf(i + 1));
            btn.setEnabled(i != currentPage);
            btn.addActionListener(e -> {
                if (isBag) {
                    currentBagPage = page;
                } else {
                    currentBoxPage = page;
                }
                refreshItemGrid(items, page, panel, isBag);
            });
            navPanel.add(btn);
        }
        navPanel.add(next);

        // Layout chính
        panel.setLayout(new BorderLayout());
        panel.setOpaque(false);
        panel.add(navPanel, BorderLayout.NORTH);
        panel.add(gridContainer, BorderLayout.CENTER);

        panel.revalidate();
        panel.repaint();
    }

    private JPanel createItemGrid(List<Item> items, String source, boolean isBag) {
        int rows = 3;
        int cols = 8;
        int cellSize = 44;
        int spacing = 4;

        JPanel grid = new JPanel(new GridLayout(rows, cols, spacing, spacing));
        grid.setOpaque(false);

        int gridWidth = cols * cellSize + (cols - 1) * spacing;
        int gridHeight = rows * cellSize + (rows - 1) * spacing;

        grid.setPreferredSize(new Dimension(gridWidth, gridHeight));
        grid.setMinimumSize(grid.getPreferredSize());
        grid.setMaximumSize(grid.getPreferredSize());

        for (int i = 0; i < rows * cols; i++) {
            JPanel slot = new JPanel(null); // absolute layout
            slot.setPreferredSize(new Dimension(cellSize, cellSize));
            slot.setBackground(new Color(40, 40, 40));
            slot.setBorder(BorderFactory.createLineBorder(Color.GRAY));

            if (i < items.size()) {
                Item item = items.get(i);
                if (item != null && item.template != null) {
                    String path = "data/icon/x4/" + item.template.iconID + ".png";
                    ImageIcon scaledIcon = new UIUtils().getScaledIcon(path, 24, 24);

                    JLabel iconLabel = new JLabel(scaledIcon);
                    iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    iconLabel.setVerticalAlignment(SwingConstants.CENTER);
                    iconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                    Item finalItem = item;
                    iconLabel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            Window window = SwingUtilities.getWindowAncestor(grid);
                            if (window instanceof Frame) {
                                ItemInfoDialog dialog = new ItemInfoDialog((Frame) window, finalItem, source, name, (int) playerId, finalItem.itemOptions);
                                dialog.showDialog();
                            }
                        }
                    });

                    // Center icon in the slot
                    iconLabel.setBounds((cellSize - 24) / 2, (cellSize - 24) / 2, 24, 24);
                    slot.add(iconLabel);

                    // Show star count (option 107)
                    int starCount = 0;
                    for (Item.ItemOption opt : item.itemOptions) {
                        if (opt.optionTemplate.id == 107) {
                            starCount += opt.param;
                        }
                    }
                    if (starCount > 0) {
                        JLabel starLabel = new JLabel(starCount + "s");
                        starLabel.setForeground(Color.YELLOW);
                        starLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
                        starLabel.setBounds(2, cellSize - 14, cellSize / 2 - 2, 12); // bottom-left
                        slot.add(starLabel);
                    }

                    // Always show quantity
                    String qtyText = "x" + item.quantity;
                    if (qtyText.length() > 4) {
                        qtyText = qtyText.substring(0, 4) + "...";
                    }
                    JLabel qtyLabel = new JLabel(qtyText);
                    qtyLabel.setForeground(Color.WHITE);
                    qtyLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
                    qtyLabel.setHorizontalAlignment(SwingConstants.RIGHT);
                    qtyLabel.setBounds(cellSize / 2, cellSize - 14, cellSize / 2 - 2, 12); // bottom-right
                    slot.add(qtyLabel);

                }
            }

            grid.add(slot);
        }

        return grid;
    }

    private JLabel createLabel(String text, int size, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, size));
        lbl.setForeground(color);
        return lbl;
    }

    private ImageIcon getHeadIcon(int headId) {
        for (HeadAvatar ha : Manager.HEAD_AVATARS) {
            if (ha.getHeadId() == headId) {
                String path = "data/icon/x4/" + ha.getAvatarId() + ".png";
                Image img = new ImageIcon(path).getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                return new ImageIcon(img);
            }
        }
        return new ImageIcon();
    }

    private String getPlanetName(int gender) {
        return switch (gender) {
            case 0 ->
                "Trái Đất";
            case 1 ->
                "Namek";
            case 2 ->
                "Xayda";
            default ->
                "Không rõ";
        };
    }

    private void parsePetItems(String petStr) {
        if (petStr == null || petStr.isEmpty()) {
            return;
        }
        try {
            JSONArray petData = (JSONArray) JSONValue.parse(petStr);
            if (petData.size() > 2) {
                JSONArray bodyArray = (JSONArray) JSONValue.parse(String.valueOf(petData.get(2)));
                for (Object obj : bodyArray) {
                    JSONArray itemArray = (JSONArray) JSONValue.parse(obj.toString());
                    Item item = parseItem(itemArray);
                    petItemsBag.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Lớp phụ trợ giữ cứng size
    private static class FixedSizePanel extends JPanel {

        public FixedSizePanel(int width, int height, Component content) {
            setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
            setPreferredSize(new Dimension(width, height));
            setMaximumSize(getPreferredSize());
            setMinimumSize(getPreferredSize());
            setOpaque(false);
            add(content);
        }
    }

    private JPanel createStatRow(String iconPath, String mainText, Color mainColor) {
        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 1));
        rowPanel.setOpaque(false);
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22)); // giảm độ cao dòng

        JLabel iconLabel = new JLabel(UIUtils.getScaledIcon(iconPath, 16, 16));
        rowPanel.add(iconLabel);

        JLabel mainLabel = new JLabel(mainText);
        mainLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        mainLabel.setForeground(mainColor);
        rowPanel.add(mainLabel);

        return rowPanel;
    }

}
