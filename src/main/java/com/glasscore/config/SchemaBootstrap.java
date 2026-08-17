package com.glasscore.config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

@Component
public class SchemaBootstrap {

    private static final Logger LOG = LoggerFactory.getLogger(SchemaBootstrap.class);

    private final DataSource dataSource;

    public SchemaBootstrap(DataSource dataSource, DataSourceBridge bridge) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void init() throws Exception {
        boolean vacia;
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "SELECT COUNT(*) FROM information_schema.tables "
                             + "WHERE table_schema = DATABASE() AND table_name = 'usuario'")) {
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                vacia = rs.getInt(1) == 0;
            }
        }

        if (vacia) {
            LOG.info("Base vacia: creando tablas y datos demo");
            try (Connection cn = dataSource.getConnection()) {
                ScriptUtils.executeSqlScript(cn, new ClassPathResource("schema-bootstrap.sql"));
            }
        }

        try (Connection cn = dataSource.getConnection()) {
            asegurarColumnasCotizacion(cn);
            asegurarEnunciado(cn);
        }
    }

    private void asegurarEnunciado(Connection cn) throws Exception {
        try (Statement st = cn.createStatement()) {
            st.executeUpdate("ALTER TABLE usuario MODIFY rol ENUM('ADMIN','OPERADOR','CONTADOR','CAJERO') NOT NULL");
        } catch (Exception ignored) {
            LOG.warn("No se pudo ampliar enum de roles: {}", ignored.getMessage());
        }
        agregarColumnaSiFalta(cn, "empleado", "identidad", "VARCHAR(20) NULL");
        agregarColumnaSiFalta(cn, "empleado", "fecha_ingreso", "DATE NULL");
        agregarColumnaSiFalta(cn, "empleado", "vacaciones_gozadas", "INT NOT NULL DEFAULT 0");
        agregarColumnaSiFalta(cn, "cotizacion", "cliente_id", "INT NULL");
        agregarColumnaSiFalta(cn, "cotizacion", "estado", "VARCHAR(30) NOT NULL DEFAULT 'VIGENTE'");
        agregarColumnaSiFalta(cn, "cotizacion", "numero_factura", "VARCHAR(30) NULL");
        agregarColumnaSiFalta(cn, "cotizacion", "cai_usado", "VARCHAR(50) NULL");
        agregarColumnaSiFalta(cn, "planilla", "aplica_14vo", "TINYINT(1) NOT NULL DEFAULT 0");
        agregarColumnaSiFalta(cn, "planilla", "aplica_aguinaldo", "TINYINT(1) NOT NULL DEFAULT 0");
        agregarColumnaSiFalta(cn, "planilla", "monto_14vo", "DECIMAL(12,2) NOT NULL DEFAULT 0");
        agregarColumnaSiFalta(cn, "planilla", "monto_aguinaldo", "DECIMAL(12,2) NOT NULL DEFAULT 0");
        agregarColumnaSiFalta(cn, "planilla", "deducciones", "DECIMAL(12,2) NOT NULL DEFAULT 0");
        agregarColumnaSiFalta(cn, "viaje", "origen", "VARCHAR(80) NULL");
        agregarColumnaSiFalta(cn, "viaje", "destino", "VARCHAR(80) NULL");
        try (Statement st = cn.createStatement()) {
            st.executeUpdate("UPDATE empleado SET identidad = CONCAT('0801-1990-', LPAD(id,5,'0')) "
                    + "WHERE identidad IS NULL OR identidad=''");
            st.executeUpdate("UPDATE empleado SET fecha_ingreso = '2024-01-15' WHERE fecha_ingreso IS NULL");
        }
        try (Statement st = cn.createStatement()) {
            st.executeUpdate("ALTER TABLE empleado ADD UNIQUE KEY uk_empleado_identidad (identidad)");
        } catch (Exception ignored) {
            // ya existe o hay nulos residuales
        }
        ScriptUtils.executeSqlScript(cn, new ClassPathResource("schema-enunciado.sql"));
        LOG.info("Esquema del enunciado verificado");
    }

    private void asegurarColumnasCotizacion(Connection cn) throws Exception {
        agregarColumnaSiFalta(cn, "cotizacion", "isv", "DECIMAL(12,2) NOT NULL DEFAULT 0");
        agregarColumnaSiFalta(cn, "cotizacion", "total", "DECIMAL(12,2) NOT NULL DEFAULT 0");
        agregarColumnaSiFalta(cn, "cotizacion", "vigencia_dias", "INT NOT NULL DEFAULT 15");
        agregarColumnaSiFalta(cn, "cotizacion", "fecha_vencimiento", "DATETIME NULL");
        try (Statement st = cn.createStatement()) {
            st.executeUpdate(
                    "UPDATE cotizacion SET isv = ROUND(subtotal * 0.15, 2), "
                            + "total = ROUND(subtotal * 1.15, 2), "
                            + "vigencia_dias = IF(vigencia_dias < 1, 15, vigencia_dias), "
                            + "fecha_vencimiento = COALESCE(fecha_vencimiento, DATE_ADD(fecha, INTERVAL 15 DAY)) "
                            + "WHERE total = 0 AND subtotal > 0");
        }
    }

    private void agregarColumnaSiFalta(Connection cn, String tabla, String columna, String definicion)
            throws Exception {
        try (PreparedStatement ps = cn.prepareStatement(
                "SELECT COUNT(*) FROM information_schema.columns "
                        + "WHERE table_schema = DATABASE() AND table_name = ? AND column_name = ?")) {
            ps.setString(1, tabla);
            ps.setString(2, columna);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                if (rs.getInt(1) > 0) {
                    return;
                }
            }
        }
        try (Statement st = cn.createStatement()) {
            st.executeUpdate("ALTER TABLE " + tabla + " ADD COLUMN " + columna + " " + definicion);
            LOG.info("Columna {}.{} agregada", tabla, columna);
        }
    }
}
