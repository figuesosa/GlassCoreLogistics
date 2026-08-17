package com.glasscore.util;

public final class DocumentoUnico {

    private DocumentoUnico() {
    }

    public static String normalizar(String doc) {
        if (doc == null) {
            return "";
        }
        return doc.trim().replace("-", "").replace(" ", "").toUpperCase();
    }

    public static void exigir(String doc, String etiqueta) {
        if (normalizar(doc).length() < 7) {
            throw new IllegalArgumentException(etiqueta + " inválido (mínimo 7 caracteres).");
        }
    }
}
