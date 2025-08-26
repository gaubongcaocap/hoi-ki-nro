package panel;

import javax.swing.*;
import java.awt.*;
import server.Manager;

public class ChangeExpPanel extends JPanel {

    private JTextField expField;
    private JLabel statusLabel;

    public ChangeExpPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        // Tiêu đề
        JLabel titleLabel = new JLabel("Cập nhật EXP Server", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 255, 180));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0)); // Lùi xuống
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        add(titlePanel, BorderLayout.NORTH);

        // Wrapper căn giữa
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        // Nội dung chính
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        JLabel inputLabel = new JLabel("Nhập EXP mới:");
        inputLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        inputLabel.setForeground(Color.WHITE);
        inputLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        expField = new JTextField(String.valueOf(Manager.RATE_EXP_SERVER));
        expField.setMaximumSize(new Dimension(200, 30));
        expField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        expField.setHorizontalAlignment(JTextField.CENTER);
        expField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton updateBtn = UIUtils.createFancyButton(" Cập nhật");
        updateBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        updateBtn.setMaximumSize(new Dimension(150, 35));
        updateBtn.addActionListener(e -> updateExp());

        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        statusLabel.setForeground(Color.YELLOW);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Thêm vào panel
        centerPanel.add(inputLabel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(expField);
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(updateBtn);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(statusLabel);

        wrapper.add(centerPanel, gbc);
        add(wrapper, BorderLayout.CENTER);
    }

    private void updateExp() {
        try {
            int newExp = Integer.parseInt(expField.getText().trim());
            if (newExp <= 0) {
                statusLabel.setText("⚠️ Giá trị EXP phải lớn hơn 0.");
                return;
            }
            Manager.RATE_EXP_SERVER = (byte) newExp;
            statusLabel.setText("✅ Đã cập nhật EXP server: " + newExp);
        } catch (NumberFormatException ex) {
            statusLabel.setText("❌ Vui lòng nhập một số nguyên hợp lệ.");
        }
    }
}
