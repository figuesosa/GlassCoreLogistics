package com.glasscore.util;

import java.util.LinkedHashMap;
import java.util.Map;

public final class RutaCatalogo {

    private static final Map<String, Integer> KM = new LinkedHashMap<>();

    static {
        put("Tegucigalpa", "Comayagua", 85);
        put("Tegucigalpa", "San Pedro Sula", 250);
        put("Tegucigalpa", "Choluteca", 142);
        put("Tegucigalpa", "Danlí", 92);
        put("Tegucigalpa", "La Ceiba", 395);
        put("Tegucigalpa", "Siguatepeque", 118);
        put("Tegucigalpa", "Juticalpa", 178);
        put("Comayagua", "San Pedro Sula", 175);
        put("Comayagua", "Siguatepeque", 35);
        put("San Pedro Sula", "La Ceiba", 200);
        put("San Pedro Sula", "Siguatepeque", 145);
        put("Choluteca", "Danlí", 210);
    }

    private RutaCatalogo() {
    }

    private static void put(String a, String b, int km) {
        KM.put(clave(a, b), km);
    }

    private static String clave(String a, String b) {
        return a.compareToIgnoreCase(b) <= 0 ? a + "|" + b : b + "|" + a;
    }

    public static String[] ciudades() {
        return new String[]{
                "Tegucigalpa", "Comayagua", "San Pedro Sula", "Choluteca",
                "Danlí", "La Ceiba", "Siguatepeque", "Juticalpa"
        };
    }

    public static int kmSimple(String origen, String destino) {
        if (origen == null || destino == null) {
            throw new IllegalArgumentException("Origen y destino son obligatorios.");
        }
        if (origen.equalsIgnoreCase(destino)) {
            throw new IllegalArgumentException("Origen y destino no pueden ser la misma ciudad.");
        }
        Integer km = KM.get(clave(origen, destino));
        if (km == null) {
            throw new IllegalArgumentException("No hay distancia cargada para " + origen + " → " + destino + ".");
        }
        return km;
    }

    public static int kmViaje(String origen, String destino, boolean redondo) {
        int simple = kmSimple(origen, destino);
        return redondo ? simple * 2 : simple;
    }
}
