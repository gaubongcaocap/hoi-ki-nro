
package ToiUu;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class GameMapUpdater {
    
  public static BufferedImage resizeImageWithTransparency(BufferedImage originalImage, int targetWidth, int targetHeight) {
    if (targetWidth <= 0 || targetHeight <= 0) {
        return originalImage;
    }
    BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = resizedImage.createGraphics();
    g2d.setComposite(AlphaComposite.Src);
    g2d.drawImage(originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH), 0, 0, null);
    g2d.dispose();
    
    return resizedImage;
}

}