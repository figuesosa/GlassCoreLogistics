package com.glasscore.dao.impl;

import com.glasscore.conexion.ConexionDB;
import com.glasscore.dao.CotizacionDAO;
import com.glasscore.modelo.Cotizacion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class CotizacionDAOImpl implements CotizacionDAO {

    @Override
    public int insertar(Cotizacion c) throws Exception {
        String sql = "INSERT INTO cotizacion (cliente, tipo_estructura, ancho, alto, area_vidrio, "
                + "metros_aluminio, metros_metal, subtotal, alerta_compra) VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getCliente());
            ps.setString(2, c.getTipoEstructura());
            ps.setDouble(3, c.getAncho());
            ps.setDouble(4, c.getAlto());
            ps.setDouble(5, c.getAreaVidrio());
            ps.setDouble(6, c.getMetrosAluminio());
            ps.setDouble(7, c.getMetrosMetal());
            ps.setDouble(8, c.getSubtotal());
            ps.setString(9, c.getAlertaCompra());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    @Override
    public List<Cotizacion> listarTodas() throws Exception {
        List<Cotizacion> lista = new ArrayList<>();
        String sql = "SELECT * FROM cotizacion ORDER BY fecha DESC";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(map(rs));
            }
        }
        return lista;
    }

    @Override
    public Cotizacion buscarPorId(int id) throws Exception {
        String sql = "SELECT * FROM cotizacion WHERE id=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    private Cotizacion map(ResultSet rs) throws Exception {
        Cotizacion c = new Cotizacion();
        c.setId(rs.getInt("id"));
        c.setCliente(rs.getString("cliente"));
        c.setTipoEstructura(rs.getString("tipo_estructura"));
        c.setAncho(rs.getDouble("ancho"));
        c.setAlto(rs.getDouble("alto"));
        c.setAreaVidrio(rs.getDouble("area_vidrio"));
        c.setMetrosAluminio(rs.getDouble("metros_aluminio"));
        c.setMetrosMetal(rs.getDouble("metros_metal"));
        c.setSubtotal(rs.getDouble("subtotal"));
        c.setAlertaCompra(rs.getString("alerta_compra"));
        Timestamp ts = rs.getTimestamp("fecha");
        if (ts != null) {
            c.setFecha(ts.toLocalDateTime());
        }
        return c;
    }
}
