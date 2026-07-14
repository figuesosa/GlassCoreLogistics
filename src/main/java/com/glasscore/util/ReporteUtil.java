package com.glasscore.util;

import com.glasscore.conexion.ConexionDB;
import java.awt.Desktop;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

public final class ReporteUtil {

    private ReporteUtil() {
    }

    public static void generarComprobantePlanilla(int empleadoId) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("EMPLEADO_ID", empleadoId);
        generar("reportes/ComprobantePlanilla.jasper", "reportes/ComprobantePlanilla.jrxml",
                params, "Comprobante_Planilla_Emp_" + empleadoId);
    }

    public static void generarHojaRuta(int viajeId) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("VIAJE_ID", viajeId);
        generar("reportes/HojaRutaDespacho.jasper", "reportes/HojaRutaDespacho.jrxml",
                params, "Hoja_Ruta_Viaje_" + viajeId);
    }

    private static void generar(String jasperPath, String jrxmlPath,
                                Map<String, Object> params, String nombreSalida) throws Exception {
        JasperReport report = cargarReporte(jasperPath, jrxmlPath);
        try (Connection cn = ConexionDB.getConnection()) {
            JasperPrint print = JasperFillManager.fillReport(report, params, cn);
            Path outDir = Path.of(System.getProperty("user.home"), "GlassCore_Reportes");
            Files.createDirectories(outDir);
            Path pdf = outDir.resolve(nombreSalida + ".pdf");
            JasperExportManager.exportReportToPdfFile(print, pdf.toString());

            if (print.getPages() == null || print.getPages().isEmpty()) {
                throw new IllegalStateException(
                        "El reporte no tiene datos (PDF en blanco). Verifique parámetros o registros en la base.");
            }

            JasperViewer viewer = new JasperViewer(print, false);
            viewer.setTitle("GlassCore - " + nombreSalida);
            viewer.setVisible(true);

            try {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(pdf.toFile());
                }
            } catch (Exception openEx) {
                System.err.println("PDF generado en " + pdf + " (visor del sistema no disponible): "
                        + openEx.getMessage());
            }
        }
    }

    private static JasperReport cargarReporte(String jasperPath, String jrxmlPath) throws Exception {
        InputStream jasperStream = ReporteUtil.class.getClassLoader().getResourceAsStream(jasperPath);
        if (jasperStream != null) {
            try (jasperStream) {
                return (JasperReport) JRLoader.loadObject(jasperStream);
            }
        }
        InputStream jrxmlStream = ReporteUtil.class.getClassLoader().getResourceAsStream(jrxmlPath);
        if (jrxmlStream == null) {
            throw new IllegalStateException("No se encontró el reporte: " + jasperPath + " / " + jrxmlPath);
        }
        try (jrxmlStream) {
            return JasperCompileManager.compileReport(jrxmlStream);
        }
    }

    public static void compilarTodos(Path carpetaReportes) throws Exception {
        Files.createDirectories(carpetaReportes);
        String[] nombres = {"ComprobantePlanilla", "HojaRutaDespacho"};
        for (String nombre : nombres) {
            Path jrxml = carpetaReportes.resolve(nombre + ".jrxml");
            Path jasper = carpetaReportes.resolve(nombre + ".jasper");
            if (!Files.exists(jrxml)) {
                InputStream in = ReporteUtil.class.getClassLoader()
                        .getResourceAsStream("reportes/" + nombre + ".jrxml");
                if (in != null) {
                    Files.copy(in, jrxml, StandardCopyOption.REPLACE_EXISTING);
                    in.close();
                }
            }
            if (Files.exists(jrxml)) {
                JasperCompileManager.compileReportToFile(jrxml.toString(), jasper.toString());
                System.out.println("Compilado: " + jasper);
            }
        }
    }
}
