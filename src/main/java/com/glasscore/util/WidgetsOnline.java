package com.glasscore.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Locale;

public final class WidgetsOnline {

    private static final ObjectMapper JSON = new ObjectMapper();
    private static final Duration TTL = Duration.ofMinutes(10);
    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(8))
            .build();

    private static volatile ClimaInfo climaCache;
    private static volatile long climaCacheAt;
    private static volatile DivisaInfo divisaCache;
    private static volatile long divisaCacheAt;

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

        public String getUbicacion() {
            return ubicacion;
        }

        public String getCondicion() {
            return condicion;
        }

        public double getTemperaturaC() {
            return temperaturaC;
        }

        public int getHumedad() {
            return humedad;
        }

        public double getVientoKmh() {
            return vientoKmh;
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

        public double getUsdToHnl() {
            return usdToHnl;
        }

        public double getUsdToEur() {
            return usdToEur;
        }

        public double getUsdToGtq() {
            return usdToGtq;
        }

        public String getActualizado() {
            return actualizado;
        }
    }

    public static ClimaInfo obtenerClimaTegucigalpa() throws Exception {
        long now = System.currentTimeMillis();
        if (climaCache != null && now - climaCacheAt < TTL.toMillis()) {
            return climaCache;
        }
        try {
            climaCache = climaOpenMeteo();
            climaCacheAt = now;
            return climaCache;
        } catch (Exception primario) {
            try {
                climaCache = climaWttr();
                climaCacheAt = now;
                return climaCache;
            } catch (Exception ignored) {
                if (climaCache != null) {
                    return climaCache;
                }
                throw primario;
            }
        }
    }

    public static DivisaInfo obtenerDivisas() throws Exception {
        long now = System.currentTimeMillis();
        if (divisaCache != null && now - divisaCacheAt < TTL.toMillis()) {
            return divisaCache;
        }
        String body = get("https://open.er-api.com/v6/latest/USD");
        JsonNode root = JSON.readTree(body);
        JsonNode rates = root.path("rates");
        if (rates.isMissingNode()) {
            throw new IllegalStateException("Respuesta de divisas incompleta");
        }
        String fecha = root.path("time_last_update_utc").asText("ahora");
        divisaCache = new DivisaInfo(
                rates.path("HNL").asDouble(),
                rates.path("EUR").asDouble(),
                rates.path("GTQ").asDouble(),
                fecha);
        divisaCacheAt = now;
        return divisaCache;
    }

    private static ClimaInfo climaOpenMeteo() throws Exception {
        String url = "https://api.open-meteo.com/v1/forecast"
                + "?latitude=14.0723&longitude=-87.1921"
                + "&current=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m"
                + "&timezone=America%2FTegucigalpa&wind_speed_unit=kmh";
        JsonNode current = JSON.readTree(get(url)).path("current");
        if (current.isMissingNode() || current.path("temperature_2m").isMissingNode()) {
            throw new IllegalStateException("Open-Meteo sin datos current");
        }
        int code = current.path("weather_code").asInt();
        return new ClimaInfo(
                "Tegucigalpa, Honduras",
                describirClima(code),
                current.path("temperature_2m").asDouble(),
                current.path("relative_humidity_2m").asInt(),
                current.path("wind_speed_10m").asDouble());
    }

    private static ClimaInfo climaWttr() throws Exception {
        JsonNode cur = JSON.readTree(get("https://wttr.in/Tegucigalpa?format=j1"))
                .path("current_condition");
        if (!cur.isArray() || cur.isEmpty()) {
            throw new IllegalStateException("wttr.in sin datos");
        }
        JsonNode now = cur.get(0);
        String condicion = now.path("weatherDesc").isArray() && !now.path("weatherDesc").isEmpty()
                ? now.path("weatherDesc").get(0).path("value").asText("Variable")
                : "Variable";
        return new ClimaInfo(
                "Tegucigalpa, Honduras",
                condicion,
                now.path("temp_C").asDouble(),
                now.path("humidity").asInt(),
                now.path("windspeedKmph").asDouble());
    }

    private static String get(String url) throws Exception {
        HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(12))
                .header("User-Agent", "GlassCoreLogistics/1.0 (student demo)")
                .header("Accept", "application/json")
                .GET()
                .build();
        HttpResponse<String> res = CLIENT.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() < 200 || res.statusCode() >= 300) {
            throw new IllegalStateException("HTTP " + res.statusCode());
        }
        return res.body();
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
