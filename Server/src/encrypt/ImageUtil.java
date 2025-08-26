package encrypt;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */



/**
 *
 * @author Administrator
 */
public class ImageUtil {

    public static ConcurrentHashMap<Byte, ConcurrentHashMap<Integer, IconEncrypt>> ICON_IMAGE = new ConcurrentHashMap<>();
    public static String key = generateRandomKey(16);

    public static void initImage() {
        for (byte i = 1; i < 5; i++) {
            ICON_IMAGE.put(i, new ConcurrentHashMap<>());

        }

    }

    public static byte[] encryptImage(File image, String key) throws Exception {
        BufferedImage originalImage = ImageIO.read(image);
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        long seed = key.hashCode();
        Random random = new Random(seed);
        BufferedImage encryptedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color originalColor = new Color(originalImage.getRGB(x, y), true);
                int alpha = originalColor.getAlpha();
                if (alpha == 0) {
                    encryptedImage.setRGB(x, y, new Color(0, 0, 0, 0).getRGB());
                } else {
                    int red = (originalColor.getRed() + random.nextInt(256)) % 256;
                    int green = (originalColor.getGreen() + random.nextInt(256)) % 256;
                    int blue = (originalColor.getBlue() + random.nextInt(256)) % 256;
                    Color encryptedColor = new Color(red, green, blue, alpha);
                    encryptedImage.setRGB(x, y, encryptedColor.getRGB());
                }
            }
        }
        ImageIO.write(encryptedImage, "png", baos);
        return baos.toByteArray();
    }

    public static String getFileNameWithoutExtension(File file) {
        String fileName = file.getName();
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return fileName.substring(0, lastDotIndex);
        }
        return fileName;
    }

    public static String encryptString(String plainText) throws Exception {
        byte[] keyData = key.getBytes();
        SecretKey secretKey = new SecretKeySpec(keyData, "AES");

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] byteArray = plainText.getBytes();
        for (int i = 0; i < 4; i++) {
            byteArray = cipher.doFinal(byteArray);
        }
        String encodedString = Base64.getEncoder().encodeToString(byteArray);
        return encodedString;
    }

    public static String generateRandomKey(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(length);
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length());
            sb.append(characters.charAt(randomIndex));
        }
        return sb.toString();
    }
}
