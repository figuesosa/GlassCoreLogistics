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
                + "metros_aluminio, metros_metal, subtotal, isv, total, vigencia_dias, fecha_vencimiento, alerta_compra, "
                + "cliente_id, estado) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
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
            ps.setDouble(9, c.getIsv());
            ps.setDouble(10, c.getTotal());
            ps.setInt(11, c.getVigenciaDias());
            ps.setTimestamp(12, Timestamp.valueOf(c.getFechaVencimiento()));
            ps.setString(13, c.getAlertaCompra());
            if (c.getClienteId() == null) {
                ps.setNull(14, java.sql.Types.INTEGER);
            } else {
                ps.setInt(14, c.getClienteId());
            }
            ps.setString(15, c.getEstado() == null ? "VIGENTE" : c.getEstado());
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
    public void marcarConvertida(int id, String numeroFactura, String cai) throws Exception {
        try (Connection cn = ConexionDB.getConnection()) {
            marcarConvertida(cn, id, numeroFactura, cai);
        }
    }

    public void marcarConvertida(Connection cn, int id, String numeroFactura, String cai) throws Exception {
        try (PreparedStatement ps = cn.prepareStatement(
                "UPDATE cotizacion SET estado='CONVERTIDA_A_VENTA', numero_factura=?, cai_usado=? WHERE id=?")) {
            ps.setString(1, numeroFactura);
            ps.setString(2, cai);
            ps.setInt(3, id);
            ps.executeUpdate();
        }
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
        c.setIsv(rs.getDouble("isv"));
        c.setTotal(rs.getDouble("total"));
        c.setVigenciaDias(rs.getInt("vigencia_dias"));
        Timestamp venc = rs.getTimestamp("fecha_vencimiento");
        if (venc != null) {
            c.setFechaVencimiento(venc.toLocalDateTime());
        }
        c.setAlertaCompra(rs.getString("alerta_compra"));
        Timestamp ts = rs.getTimestamp("fecha");
        if (ts != null) {
            c.setFecha(ts.toLocalDateTime());
        }
        try {
            int cid = rs.getInt("cliente_id");
            c.setClienteId(rs.wasNull() ? null : cid);
            c.setEstado(rs.getString("estado"));
            c.setNumeroFactura(rs.getString("numero_factura"));
            c.setCaiUsado(rs.getString("cai_usado"));
        } catch (Exception ignored) {
            c.setEstado("VIGENTE");
        }
        return c;
    }
}
