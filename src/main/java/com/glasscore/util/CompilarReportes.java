package com.glasscore.util;

import java.nio.file.Path;

public class CompilarReportes {

    public static void main(String[] args) throws Exception {
        Path destino = Path.of("src", "main", "resources", "reportes");
        ReporteUtil.compilarTodos(destino);
        System.out.println("Reportes Jasper compilados en " + destino.toAbsolutePath());
    }
}
