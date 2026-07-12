package com.glasscore.util;

import java.awt.Image;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

public final class LogoUtil {

    private static final String PATH = "/images/glasscore_logo.png";
    private static ImageIcon original;

    private LogoUtil() {
    }

    public static ImageIcon load() {
        if (original == null) {
            URL url = LogoUtil.class.getResource(PATH);
            if (url == null) {
                return null;
            }
            original = new ImageIcon(url);
        }
        return original;
    }

    public static ImageIcon scaled(int maxWidth, int maxHeight) {
        ImageIcon src = load();
        if (src == null || src.getIconWidth() <= 0) {
            return null;
        }
        double scale = Math.min(
                (double) maxWidth / src.getIconWidth(),
                (double) maxHeight / src.getIconHeight());
        int w = Math.max(1, (int) Math.round(src.getIconWidth() * scale));
        int h = Math.max(1, (int) Math.round(src.getIconHeight() * scale));
        Image img = src.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    public static List<Image> iconImages() {
        List<Image> icons = new ArrayList<>();
        ImageIcon src = load();
        if (src == null) {
            return icons;
        }
        for (int size : new int[]{16, 32, 48, 64, 128}) {
            icons.add(src.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH));
        }
        return icons;
    }
}
