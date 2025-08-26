package panel;

import server.Manager; // thêm ở đầu file nếu chưa có
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import panel.player.PlayerManagerPanel;
import panel.shop.ShopManagerPanel;
public class FunctionPanel extends JPanel {

    private SystemInfoPanel systemInfoPanel;

    public FunctionPanel() {
        setLayout(null); // layout tự do
        setOpaque(false);

        // Panel thông tin hệ thống
        systemInfoPanel = new SystemInfoPanel();
        systemInfoPanel.setBounds(0, 0, 220, 110);
        add(systemInfoPanel);

        // Tên nút và hành động tương ứng
        String[] buttons = {
            "Quản lý Player",
            "Thu vật phẩm",
            "Bảng Xếp Hạng",
            "Bảo trì / Shutdown",
            "Thay đổi exp",
            "Check Giao Dịch",
            "GIFCODE ",
            "Quản lý Shop",};

        int yPos = 120;

        for (String name : buttons) {
            JButton btn = createFancyButton(name);
            btn.setBounds(25, yPos, 220, 35);

            // Thêm action cho mỗi nút
            btn.addActionListener(new ButtonHandler(name));

            add(btn);
            yPos += 40;
        }

        // Nút "Quay lại" – chuyển về log
        JButton backBtn = createFancyButton("⟳ Quay lại");
        backBtn.setBounds(25, yPos + 5, 220, 35);
        backBtn.addActionListener(e -> {
            PanelManager.gI().getDynamicPanel().resetToLogPanel();
        });
        add(backBtn);
    }

    private JButton createFancyButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(new Color(70, 130, 180));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(60, 60, 60));
                } else {
                    g2.setColor(new Color(40, 40, 40));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(new Color(0, 255, 180));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // Bộ xử lý action theo từng nút
    private static class ButtonHandler implements ActionListener {

        private final String action;

        public ButtonHandler(String action) {
            this.action = action;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (action) {
                case "Thay đổi exp":
                    PanelManager.gI().getDynamicPanel().setContent(new ChangeExpPanel());
                    break;
                case "Quản lý Player":
                    PanelManager.gI().getDynamicPanel().setContent(new PlayerManagerPanel());
                    break;
                case "Thu vật phẩm":
                    // PanelManager.gI().getDynamicPanel().setContent(new CollectItemPanel());
                    break;
                case "Bảng Xếp Hạng":
                    // PanelManager.gI().getDynamicPanel().setContent(new ChangeEventPanel());
                    break;
                case "Bảo trì / Shutdown":
                    // PanelManager.gI().getDynamicPanel().setContent(new MaintenancePanel());
                    break;
                case "Quản lý Shop":
                    PanelManager.gI().getDynamicPanel().setContent(new ShopManagerPanel());
                    break;

                default:
                    JOptionPane.showMessageDialog(null, "Chức năng chưa hỗ trợ!");
                    break;
            }
        }
    }
}
