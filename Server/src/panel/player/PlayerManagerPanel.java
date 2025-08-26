package panel.player;

import item.Item;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import panel.UIUtils;
import panel.GlowPanel;
import jdbc.DBConnecter;
import server.Manager;
import models.Template.HeadAvatar;
import panel.PanelManager;

public class PlayerManagerPanel extends JPanel {

    static Iterable<Item> itemsBag;

   
    public static int currentPlayerId = -1;
    public static java.util.List<Item> currentPlayerItems = new java.util.ArrayList<>();

    private JTextField txtName;
    private JPanel topPlayersPanel;

    public PlayerManagerPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        // Title Label
        JLabel title = new JLabel("Tìm kiếm người chơi theo tên", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.CYAN);
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // Search Panel
        JPanel searchPanel = new JPanel();
        searchPanel.setOpaque(false);
        searchPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        txtName = new JTextField(15);
        txtName.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchPanel.add(txtName);

        JButton searchBtn = UIUtils.createFancyButton("Tìm kiếm");
        searchPanel.add(searchBtn);
        add(searchPanel, BorderLayout.NORTH);

        // Player List Panel
        topPlayersPanel = new JPanel();
        topPlayersPanel.setLayout(new BoxLayout(topPlayersPanel, BoxLayout.Y_AXIS));
        topPlayersPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(topPlayersPanel);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(new Dimension(600, 600));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);

        // Load top 10 players
        loadTop10Players(topPlayersPanel);

        // Search Button ActionListener
        searchBtn.addActionListener(e -> {
            String nameText = txtName.getText().trim();
            if (nameText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập tên người chơi!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }
            loadPlayerByName(nameText, topPlayersPanel);
        });

        // KeyListener for Enter key
        txtName.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    String nameText = txtName.getText().trim();
                    if (nameText.isEmpty()) {
                        JOptionPane.showMessageDialog(PlayerManagerPanel.this, "Vui lòng nhập tên người chơi!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    loadPlayerByName(nameText, topPlayersPanel);
                }
            }
        });
    }

    // Load top 10 players
    private void loadTop10Players(JPanel topPlayersPanel) {
        topPlayersPanel.removeAll();
        try (Connection con = DBConnecter.getConnectionServer(); PreparedStatement ps = con.prepareStatement(
                "SELECT id, name, data_point, head FROM player ORDER BY CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(data_point, ',', 2), ',', -1) AS UNSIGNED) DESC LIMIT 10"); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                renderPlayerPanel(rs, topPlayersPanel);
            }

            topPlayersPanel.revalidate();
            topPlayersPanel.repaint();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách top 10!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Load player by name
    private void loadPlayerByName(String name, JPanel topPlayersPanel) {
        topPlayersPanel.removeAll();
        try (Connection con = DBConnecter.getConnectionServer(); PreparedStatement ps = con.prepareStatement("SELECT id, name, data_point, head FROM player WHERE name LIKE ?");) {
            ps.setString(1, "%" + name + "%");
            ResultSet rs = ps.executeQuery();

            boolean found = false;
            while (rs.next()) {
                renderPlayerPanel(rs, topPlayersPanel);
                found = true;
            }

            if (!found) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy người chơi với tên: " + name, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }

            topPlayersPanel.revalidate();
            topPlayersPanel.repaint();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm người chơi!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Render player panel
    private void renderPlayerPanel(ResultSet rs, JPanel panel) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String dataPoint = rs.getString("data_point");
        int head = rs.getInt("head");
        int power = extractPower(dataPoint);

        JPanel playerPanel = new GlowPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
                g2d.setColor(new Color(0, 255, 180, 150));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2d.dispose();
                super.paintComponent(g);
            }
        };

        playerPanel.setLayout(new BorderLayout(10, 0));
        playerPanel.setPreferredSize(new Dimension(550, 120));
        playerPanel.setMaximumSize(new Dimension(550, 120));
        playerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        playerPanel.setOpaque(false);

        playerPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                PanelManager.gI().getDynamicPanel().setContent(new PlayerProfilePanel(id));
            }
        });

        JLabel headLabel = new JLabel(getHeadIcon(head));
        headLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headLabel.setPreferredSize(new Dimension(120, 120));
        playerPanel.add(headLabel, BorderLayout.WEST);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel nameLabel = new JLabel("ID: " + id + " - " + name);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLabel.setForeground(new Color(180, 0, 255));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        nameLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));

        JLabel powerLabel = new JLabel("Sức mạnh: " + String.format("%,d", power));
        powerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        powerLabel.setForeground(new Color(180, 0, 255));
        powerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(powerLabel);

        playerPanel.add(infoPanel, BorderLayout.CENTER);

        panel.add(Box.createVerticalStrut(15));
        panel.add(playerPanel);
    }

    // Extract power from data_point
    private int extractPower(String dataPoint) {
        try {
            String[] parts = dataPoint.replace("[", "").replace("]", "").split(",");
            return Integer.parseInt(parts[1].trim());
        } catch (Exception e) {
            return 0;
        }
    }

    // Get avatar icon
    private ImageIcon getHeadIcon(int headId) {
        for (HeadAvatar ha : Manager.HEAD_AVATARS) {
            if (ha.getHeadId() == headId) {
                int avatarId = ha.getAvatarId();
                String imagePath = "data/icon/x4/" + avatarId + ".png";
                ImageIcon icon = new ImageIcon(imagePath);
                Image scaled = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                return new ImageIcon(scaled);
            }
        }
        return new ImageIcon();
    }
}
