package com.glasscore.util;

import com.glasscore.conexion.ConexionDB;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

public final class ReporteUtil {

    private ReporteUtil() {
    }

    public static byte[] generarComprobantePlanillaPdf(int empleadoId) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("EMPLEADO_ID", empleadoId);
        return generarPdf("reportes/ComprobantePlanilla.jasper", "reportes/ComprobantePlanilla.jrxml", params);
    }

    public static byte[] generarHojaRutaPdf(int viajeId) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("VIAJE_ID", viajeId);
        return generarPdf("reportes/HojaRutaDespacho.jasper", "reportes/HojaRutaDespacho.jrxml", params);
    }

    public static byte[] generarVentasPdf(LocalDateTime desde, LocalDateTime hasta, String periodo)
            throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("FECHA_DESDE", Timestamp.valueOf(desde));
        params.put("FECHA_HASTA", Timestamp.valueOf(hasta));
        params.put("PERIODO", periodo);
        return generarPdf("reportes/VentasContable.jasper", "reportes/VentasContable.jrxml", params);
    }

    private static byte[] generarPdf(String jasperPath, String jrxmlPath,
                                     Map<String, Object> params) throws Exception {
        JasperReport report = cargarReporte(jasperPath, jrxmlPath);
        try (Connection cn = ConexionDB.getConnection()) {
            JasperPrint print = JasperFillManager.fillReport(report, params, cn);
            if (print.getPages() == null || print.getPages().isEmpty()) {
                throw new IllegalStateException(
                        "El reporte no tiene datos (PDF en blanco). Verifique parámetros o registros en la base.");
            }
            return JasperExportManager.exportReportToPdf(print);
        }
    }

    private static JasperReport cargarReporte(String jasperPath, String jrxmlPath) throws Exception {
        InputStream jrxml = ReporteUtil.class.getClassLoader().getResourceAsStream(jrxmlPath);
        if (jrxml != null) {
            try (InputStream in = jrxml) {
                return JasperCompileManager.compileReport(in);
            }
        }
        InputStream jasper = ReporteUtil.class.getClassLoader().getResourceAsStream(jasperPath);
        if (jasper != null) {
            try (InputStream in = jasper) {
                return (JasperReport) JRLoader.loadObject(in);
            }
        }
        throw new IllegalStateException("No se encontró el reporte: " + jasperPath + " / " + jrxmlPath);
    }
}
