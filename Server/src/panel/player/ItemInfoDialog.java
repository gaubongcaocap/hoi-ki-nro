package panel.player;

import item.Item;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import panel.UIUtils;
import javax.swing.JDialog;

public class ItemInfoDialog extends JDialog {

    // ... phần import và khai báo class giữ nguyên
public ItemInfoDialog(Frame owner, Item item, String source, String playerName, int playerId, List<Item.ItemOption> itemOptions) {
    super(owner, "Chi tiết vật phẩm", true);
    setSize(300, 420);
    setLocationRelativeTo(owner);
    setUndecorated(false);

    JPanel content = new JPanel();
    content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
    content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
    content.setBackground(new Color(40, 40, 40));

    // Thông tin người chơi
    JLabel playerLabel = new JLabel(playerName + " (ID: " + playerId + ")");
    playerLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
    playerLabel.setForeground(new Color(173, 216, 230));
    playerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

    // Nguồn
    JLabel sourceLabel = new JLabel(source);
    sourceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    sourceLabel.setForeground(new Color(255, 204, 102));
    sourceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

    // Icon vật phẩm
    ImageIcon icon = new UIUtils().getScaledIcon("data/icon/x4/" + item.template.iconID + ".png", 48, 48);
    JLabel iconLabel = new JLabel(icon);
    iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    iconLabel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

    // Xử lý tên + cấp nếu có option 72
    int capDo = -1;
    for (Item.ItemOption opt : itemOptions) {
        if (opt.optionTemplate.id == 72) {
            capDo = opt.param;
            break;
        }
    }

    String itemName = item.template.name;
    if (capDo > 0) {
        itemName += " +" + capDo;
    }

    // Tên vật phẩm
    JLabel nameLabel = new JLabel(itemName);
    nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
    nameLabel.setForeground(Color.YELLOW);
    nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

    // Tự format danh sách option, loại bỏ option 72
    StringBuilder infoBuilder = new StringBuilder();
    for (Item.ItemOption opt : itemOptions) {
        if (opt != null && opt.optionTemplate.id != 72&& opt.optionTemplate.id != 102&& opt.optionTemplate.id != 107) {
            infoBuilder.append(opt.getOptionString()).append("\n");
        }
    }

    JTextArea infoArea = new JTextArea(infoBuilder.toString().trim());
    infoArea.setEditable(false);
    infoArea.setOpaque(false);
    infoArea.setForeground(Color.LIGHT_GRAY);
    infoArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    infoArea.setLineWrap(true);
    infoArea.setWrapStyleWord(true);
    infoArea.setAlignmentX(Component.CENTER_ALIGNMENT);

    JScrollPane scrollPane = new JScrollPane(infoArea);
    scrollPane.setMaximumSize(new Dimension(260, 100));
    scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60)),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)
    ));
    scrollPane.setOpaque(false);
    scrollPane.getViewport().setOpaque(false);
    scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);

    // Số lượng
    JLabel qtyLabel = new JLabel("Số lượng: " + item.quantity);
    qtyLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
    qtyLabel.setForeground(Color.WHITE);
    qtyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

    // Layout từng phần
    content.add(playerLabel);
    content.add(Box.createVerticalStrut(4));
    content.add(sourceLabel);
    content.add(Box.createVerticalStrut(6));
    content.add(iconLabel);
    content.add(Box.createVerticalStrut(4));
    content.add(nameLabel);
    content.add(Box.createVerticalStrut(6));
    content.add(scrollPane);
    content.add(Box.createVerticalStrut(8));
    content.add(qtyLabel);
    content.add(Box.createVerticalStrut(6));

    add(content, BorderLayout.CENTER);
}

    public void showDialog() {
        setVisible(true);
    }

}
