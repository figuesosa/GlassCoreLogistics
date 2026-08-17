package com.glasscore.util;

import java.nio.file.Files;
import java.nio.file.Path;
import net.sf.jasperreports.engine.JasperCompileManager;

public final class CompilarReportes {

    private CompilarReportes() {
    }

    public static void main(String[] args) throws Exception {
        Path dir = Path.of("src/main/resources/reportes");
        if (!Files.isDirectory(dir)) {
            throw new IllegalStateException("No existe " + dir.toAbsolutePath());
        }
        String[] nombres = {"ComprobantePlanilla", "HojaRutaDespacho"};
        for (String nombre : nombres) {
            Path jrxml = dir.resolve(nombre + ".jrxml");
            Path jasper = dir.resolve(nombre + ".jasper");
            JasperCompileManager.compileReportToFile(jrxml.toString(), jasper.toString());
            Path extra = Path.of("resources/reportes", nombre + ".jasper");
            if (Files.isDirectory(extra.getParent())) {
                Files.copy(jasper, extra, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
            System.out.println("OK " + jasper);
        }
    }
}
