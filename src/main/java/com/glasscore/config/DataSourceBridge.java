package com.glasscore.config;

import com.glasscore.conexion.ConexionDB;
import javax.sql.DataSource;
import org.springframework.stereotype.Component;

@Component
public class DataSourceBridge {

    public DataSourceBridge(DataSource dataSource) {
        ConexionDB.setDataSource(dataSource);
    }
}
