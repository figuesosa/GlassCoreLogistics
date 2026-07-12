package com.glasscore.util;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;
import java.util.Locale;

public final class FontUtil {

    private static Font safiraBase;
    private static boolean loaded;

    private FontUtil() {
    }

    private static synchronized void ensureLoaded() {
        if (loaded) {
            return;
        }
        loaded = true;

        try (InputStream in = FontUtil.class.getResourceAsStream("/fonts/SafiraMarch.ttf")) {
            if (in != null) {
                safiraBase = Font.createFont(Font.TRUETYPE_FONT, in);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(safiraBase);
                return;
            }
        } catch (Exception ignored) {
        }
        try (InputStream in = FontUtil.class.getResourceAsStream("/fonts/Safira March Personal Use Only.ttf")) {
            if (in != null) {
                safiraBase = Font.createFont(Font.TRUETYPE_FONT, in);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(safiraBase);
                return;
            }
        } catch (Exception ignored) {
        }

        for (String family : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
            if (family.toLowerCase(Locale.ROOT).contains("safira")) {
                safiraBase = new Font(family, Font.PLAIN, 12);
                return;
            }
        }

        safiraBase = new Font("Cambria", Font.PLAIN, 12);
    }

    public static Font title(float size) {
        ensureLoaded();
        return safiraBase.deriveFont(Font.PLAIN, size);
    }

    public static Font titleBold(float size) {
        ensureLoaded();
        return safiraBase.deriveFont(Font.BOLD, size);
    }

    public static Font body(float size) {
        return new Font("Calibri", Font.PLAIN, Math.round(size));
    }

    public static Font bodyBold(float size) {
        return new Font("Calibri", Font.BOLD, Math.round(size));
    }

    public static Font data(float size) {
        return new Font("Cambria", Font.PLAIN, Math.round(size));
    }

    public static Font dataBold(float size) {
        return new Font("Cambria", Font.BOLD, Math.round(size));
    }

    public static Font ui(float size) {
        return new Font("Calibri", Font.PLAIN, Math.round(size));
    }

    public static Font uiBold(float size) {
        return new Font("Calibri", Font.BOLD, Math.round(size));
    }

    public static String tituloActivo() {
        ensureLoaded();
        return safiraBase.getFamily();
    }
}
