package com.glasscore.util;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Clima Tegucigalpa vía Open-Meteo (sin API key) y tipo de cambio vía open.er-api.com.
 */
public final class WidgetsOnline {

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(8))
            .build();

    private WidgetsOnline() {
    }

    public static final class ClimaInfo {
        public final String ubicacion;
        public final String condicion;
        public final double temperaturaC;
        public final int humedad;
        public final double vientoKmh;

        public ClimaInfo(String ubicacion, String condicion, double temperaturaC,
                         int humedad, double vientoKmh) {
            this.ubicacion = ubicacion;
            this.condicion = condicion;
            this.temperaturaC = temperaturaC;
            this.humedad = humedad;
            this.vientoKmh = vientoKmh;
        }
    }

    public static final class DivisaInfo {
        public final double usdToHnl;
        public final double usdToEur;
        public final double usdToGtq;
        public final String actualizado;

        public DivisaInfo(double usdToHnl, double usdToEur, double usdToGtq, String actualizado) {
            this.usdToHnl = usdToHnl;
            this.usdToEur = usdToEur;
            this.usdToGtq = usdToGtq;
            this.actualizado = actualizado;
        }
    }

    public static ClimaInfo obtenerClimaTegucigalpa() throws Exception {
        String url = "https://api.open-meteo.com/v1/forecast"
                + "?latitude=14.0723&longitude=-87.1921"
                + "&current=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m"
                + "&timezone=America%2FTegucigalpa&wind_speed_unit=kmh";
        String json = get(url);
        double temp = num(json, "temperature_2m");
        int humedad = (int) Math.round(num(json, "relative_humidity_2m"));
        int code = (int) Math.round(num(json, "weather_code"));
        double viento = num(json, "wind_speed_10m");
        return new ClimaInfo("Tegucigalpa, Honduras", describirClima(code), temp, humedad, viento);
    }

    public static DivisaInfo obtenerDivisas() throws Exception {
        String json = get("https://open.er-api.com/v6/latest/USD");
        double hnl = numAnidado(json, "HNL");
        double eur = numAnidado(json, "EUR");
        double gtq = numAnidado(json, "GTQ");
        String fecha = texto(json, "time_last_update_utc");
        if (fecha.isEmpty()) {
            fecha = "ahora";
        }
        return new DivisaInfo(hnl, eur, gtq, fecha);
    }

    private static String get(String url) throws Exception {
        HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(12))
                .GET()
                .build();
        HttpResponse<String> res = CLIENT.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() < 200 || res.statusCode() >= 300) {
            throw new IllegalStateException("HTTP " + res.statusCode());
        }
        return res.body();
    }

    private static double num(String json, String key) {
        Matcher m = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*(-?\\d+(?:\\.\\d+)?)")
                .matcher(json);
        if (!m.find()) {
            throw new IllegalStateException("No se encontró " + key);
        }
        return Double.parseDouble(m.group(1));
    }

    private static double numAnidado(String json, String key) {
        return num(json, key);
    }

    private static String texto(String json, String key) {
        Matcher m = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*\"([^\"]*)\"")
                .matcher(json);
        return m.find() ? m.group(1) : "";
    }

    public static String describirClima(int code) {
        if (code == 0) {
            return "Soleado / despejado";
        }
        if (code == 1) {
            return "Mayormente soleado";
        }
        if (code == 2) {
            return "Parcialmente nublado";
        }
        if (code == 3) {
            return "Nublado";
        }
        if (code == 45 || code == 48) {
            return "Neblina / bruma";
        }
        if (code >= 51 && code <= 57) {
            return "Llovizna";
        }
        if (code >= 61 && code <= 67) {
            return "Lluvia";
        }
        if (code >= 71 && code <= 77) {
            return "Nieve / granizo fino";
        }
        if (code >= 80 && code <= 82) {
            return "Chubascos";
        }
        if (code >= 95) {
            return "Tormenta eléctrica";
        }
        return "Condición variable (" + code + ")";
    }

    public static String fmt(double v) {
        return String.format(Locale.US, "%.2f", v);
    }
}
