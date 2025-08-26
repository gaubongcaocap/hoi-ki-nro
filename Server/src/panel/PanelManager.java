package panel;

import javax.swing.*;

public class PanelManager {

    private static PanelManager instance;
    private JFrame frame;
    private DynamicPanel dynamicPanel;
    private FunctionPanel functionPanel;

    // Singleton pattern
    public static PanelManager gI() {
        if (instance == null) {
            instance = new PanelManager();
        }
        return instance;
    }

    // Mở giao diện chính
    public void openUI() {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("SERVER MANAGER TOOL");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 600);
            frame.setLocationRelativeTo(null);

            // Panel nền
            BackgroundPanel bg = new BackgroundPanel("data/logo.png");
            bg.setLayout(null);

            // Panel hiển thị log hoặc dynamic content
            dynamicPanel = new DynamicPanel();
            dynamicPanel.setBounds(320, 40, 660, 510);
            dynamicPanel.redirectSystemOutToPanel(); // redirect System.out
            bg.add(dynamicPanel);

            // Panel các nút chức năng bên trái
            functionPanel = new FunctionPanel();
            functionPanel.setBounds(20, 50, 280, 500);
            bg.add(functionPanel);

            // Hiển thị frame
            frame.setContentPane(bg);
            frame.setVisible(true);
        });
    }

    // In log vào panel động
    public void log(String msg) {
        if (dynamicPanel != null) {
            dynamicPanel.appendLog(msg);
        }
    }

    // ✅ Trả về panel động để các phần khác có thể thay đổi nội dung
    public DynamicPanel getDynamicPanel() {
        return dynamicPanel;
    }
}
