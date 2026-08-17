package com.glasscore.config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "SELECT COUNT(*) FROM information_schema.tables "
                             + "WHERE table_schema = DATABASE() AND table_name = 'usuario'")) {
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                if (rs.getInt(1) > 0) {
                    LOG.info("Esquema ya existe, no se importa");
                    return;
                }
            }
        }

        LOG.info("Base vacia: creando tablas y datos demo");
        try (Connection cn = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(cn, new ClassPathResource("schema-bootstrap.sql"));
        }
    }
}
