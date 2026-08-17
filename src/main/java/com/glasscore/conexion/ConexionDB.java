package com.glasscore.conexion;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import javax.sql.DataSource;

public final class ConexionDB {

    private static final Properties PROPS = new Properties();
    private static volatile DataSource dataSource;

    static {
        try (InputStream in = ConexionDB.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                PROPS.load(in);
            }
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            throw new ExceptionInInitializerError("Error cargando configuracion DB: " + e.getMessage());
        }
    }

    private ConexionDB() {
    }

    public static void setDataSource(DataSource ds) {
        dataSource = ds;
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource != null) {
            return dataSource.getConnection();
        }
        return DriverManager.getConnection(
                PROPS.getProperty("db.url"),
                PROPS.getProperty("db.user"),
                PROPS.getProperty("db.password", "")
        );
    }
}
