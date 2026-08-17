package com.glasscore.dao.impl;

import com.glasscore.conexion.ConexionDB;
import com.glasscore.modelo.ConfigFiscal;
import com.glasscore.modelo.Venta;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FiscalDAOImpl {

    public ConfigFiscal cargar() throws Exception {
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM configuracion_fiscal WHERE id=1");
             ResultSet rs = ps.executeQuery()) {
            if (!rs.next()) {
                return null;
            }
            ConfigFiscal c = new ConfigFiscal();
            c.setRtnEmpresa(rs.getString("rtn_empresa"));
            c.setCai(rs.getString("cai"));
            Date f = rs.getDate("fecha_limite_emision");
            if (f != null) {
                c.setFechaLimiteEmision(f.toLocalDate());
            }
            c.setRangoInicial(rs.getInt("rango_inicial"));
            c.setRangoFinal(rs.getInt("rango_final"));
            c.setCorrelativoActual(rs.getInt("correlativo_actual"));
            return c;
        }
    }

    public void guardar(ConfigFiscal c) throws Exception {
        String sql = "UPDATE configuracion_fiscal SET rtn_empresa=?, cai=?, fecha_limite_emision=?, "
                + "rango_inicial=?, rango_final=?, correlativo_actual=? WHERE id=1";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, c.getRtnEmpresa());
            ps.setString(2, c.getCai());
            ps.setDate(3, Date.valueOf(c.getFechaLimiteEmision()));
            ps.setInt(4, c.getRangoInicial());
            ps.setInt(5, c.getRangoFinal());
            ps.setInt(6, c.getCorrelativoActual());
            ps.executeUpdate();
        }
    }

    public int diasHabilesDefault() throws Exception {
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "SELECT dias_habiles_default FROM configuracion_cotizacion WHERE id=1");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 5;
        }
    }

    public void guardarDiasHabiles(int dias) throws Exception {
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "UPDATE configuracion_cotizacion SET dias_habiles_default=? WHERE id=1")) {
            ps.setInt(1, dias);
            ps.executeUpdate();
        }
    }

    public String tomarCorrelativo(Connection cn, ConfigFiscal cfg) throws Exception {
        if (cfg.getFechaLimiteEmision() != null && LocalDate.now().isAfter(cfg.getFechaLimiteEmision())) {
            throw new IllegalArgumentException("CAI vencido: fecha límite de emisión superada.");
        }
        int n = cfg.getCorrelativoActual();
        if (n < cfg.getRangoInicial() || n > cfg.getRangoFinal()) {
            throw new IllegalArgumentException("Se agotó el rango autorizado de comprobantes fiscales.");
        }
        try (PreparedStatement ps = cn.prepareStatement(
                "UPDATE configuracion_fiscal SET correlativo_actual=? WHERE id=1")) {
            ps.setInt(1, n + 1);
            ps.executeUpdate();
        }
        return cfg.formatoFactura(n);
    }

    public int insertarVenta(Connection cn, Venta v) throws Exception {
        String sql = "INSERT INTO venta (cotizacion_id, numero_factura, cai, subtotal, isv, total, retencion) "
                + "VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, v.getCotizacionId());
            ps.setString(2, v.getNumeroFactura());
            ps.setString(3, v.getCai());
            ps.setDouble(4, v.getSubtotal());
            ps.setDouble(5, v.getIsv());
            ps.setDouble(6, v.getTotal());
            ps.setDouble(7, v.getRetencion());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }

    public List<Venta> listarVentasEntre(LocalDateTime desde, LocalDateTime hasta) throws Exception {
        List<Venta> lista = new ArrayList<>();
        String sql = "SELECT v.*, c.cliente, c.tipo_estructura FROM venta v "
                + "INNER JOIN cotizacion c ON c.id = v.cotizacion_id "
                + "WHERE v.fecha >= ? AND v.fecha < ? ORDER BY v.fecha DESC";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(desde));
            ps.setTimestamp(2, Timestamp.valueOf(hasta));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapVenta(rs));
                }
            }
        }
        return lista;
    }

    public List<Venta> listarVentas() throws Exception {
        List<Venta> lista = new ArrayList<>();
        String sql = "SELECT v.*, c.cliente, c.tipo_estructura FROM venta v "
                + "INNER JOIN cotizacion c ON c.id = v.cotizacion_id ORDER BY v.fecha DESC";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapVenta(rs));
            }
        }
        return lista;
    }

    private Venta mapVenta(ResultSet rs) throws Exception {
        Venta v = new Venta();
        v.setId(rs.getInt("id"));
        v.setCotizacionId(rs.getInt("cotizacion_id"));
        v.setNumeroFactura(rs.getString("numero_factura"));
        v.setCai(rs.getString("cai"));
        v.setSubtotal(rs.getDouble("subtotal"));
        v.setIsv(rs.getDouble("isv"));
        v.setTotal(rs.getDouble("total"));
        v.setRetencion(rs.getDouble("retencion"));
        v.setCliente(rs.getString("cliente"));
        v.setTipoEstructura(rs.getString("tipo_estructura"));
        Timestamp ts = rs.getTimestamp("fecha");
        if (ts != null) {
            v.setFecha(ts.toLocalDateTime());
        }
        return v;
    }
}
