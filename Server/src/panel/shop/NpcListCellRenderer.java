package panel.shop;

import javax.swing.*;
import java.awt.*;
import models.Template.NpcTemplate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.File;
import java.io.IOException;

public class NpcListCellRenderer extends JPanel implements ListCellRenderer<NpcTemplate> {

    private final JLabel iconLabel;
    private final JLabel nameLabel;

    public NpcListCellRenderer() {
        setLayout(new BorderLayout(10, 0));
        setOpaque(true);

        iconLabel = new JLabel();
        nameLabel = new JLabel();
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nameLabel.setForeground(Color.WHITE);

        add(iconLabel, BorderLayout.WEST);
        add(nameLabel, BorderLayout.CENTER);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends NpcTemplate> list, NpcTemplate npc, int index,
            boolean isSelected, boolean cellHasFocus) {
        try {
            BufferedImage sprite = ImageIO.read(new File("data/icon/x4/" + npc.avatar + ".png"));
            Image scaledImg = sprite.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            iconLabel.setIcon(new ImageIcon(scaledImg));
        } catch (IOException | RasterFormatException e) {
            e.printStackTrace();
            iconLabel.setIcon(null);
        }

        nameLabel.setText("(ID: " + npc.id + ") " + npc.name);
        setToolTipText("NPC ID: " + npc.id + ", Avatar: " + npc.avatar);

        if (isSelected) {
            setBackground(new Color(70, 130, 180));
        } else {
            setBackground(new Color(45, 45, 45));
        }

        return this;
    }
}
