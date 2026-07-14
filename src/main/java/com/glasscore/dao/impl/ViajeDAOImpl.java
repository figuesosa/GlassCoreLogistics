package com.glasscore.dao.impl;

import com.glasscore.conexion.ConexionDB;
import com.glasscore.dao.ViajeDAO;
import com.glasscore.modelo.Viaje;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ViajeDAOImpl implements ViajeDAO {

    private static final String SELECT_BASE =
            "SELECT vi.*, ve.placa, CONCAT(e.nombre,' ',e.apellido) AS chofer_nombre "
            + "FROM viaje vi "
            + "INNER JOIN vehiculo ve ON vi.vehiculo_id = ve.id "
            + "INNER JOIN empleado e ON vi.chofer_id = e.id ";

    @Override
    public int insertar(Viaje v) throws Exception {
        try (Connection cn = ConexionDB.getConnection()) {
            return insertar(cn, v);
        }
    }

    @Override
    public int insertar(Connection cn, Viaje v) throws Exception {
        String sql = "INSERT INTO viaje (vehiculo_id, chofer_id, ruta, es_redondo, kilometros, "
                + "factor_rendimiento, precio_combustible, litros_estimados, gasto_combustible) "
                + "VALUES (?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, v.getVehiculoId());
            ps.setInt(2, v.getChoferId());
            ps.setString(3, v.getRuta());
            ps.setBoolean(4, v.isEsRedondo());
            ps.setInt(5, v.getKilometros());
            ps.setDouble(6, v.getFactorRendimiento());
            ps.setDouble(7, v.getPrecioCombustible());
            ps.setDouble(8, v.getLitrosEstimados());
            ps.setDouble(9, v.getGastoCombustible());
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
    public List<Viaje> listarTodos() throws Exception {
        List<Viaje> lista = new ArrayList<>();
        String sql = SELECT_BASE + "ORDER BY vi.fecha_salida DESC";
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
    public Viaje buscarPorId(int id) throws Exception {
        String sql = SELECT_BASE + "WHERE vi.id=?";
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

    private Viaje map(ResultSet rs) throws Exception {
        Viaje v = new Viaje();
        v.setId(rs.getInt("id"));
        v.setVehiculoId(rs.getInt("vehiculo_id"));
        v.setPlaca(rs.getString("placa"));
        v.setChoferId(rs.getInt("chofer_id"));
        v.setChoferNombre(rs.getString("chofer_nombre"));
        v.setRuta(rs.getString("ruta"));
        v.setEsRedondo(rs.getBoolean("es_redondo"));
        v.setKilometros(rs.getInt("kilometros"));
        v.setFactorRendimiento(rs.getDouble("factor_rendimiento"));
        v.setPrecioCombustible(rs.getDouble("precio_combustible"));
        v.setLitrosEstimados(rs.getDouble("litros_estimados"));
        v.setGastoCombustible(rs.getDouble("gasto_combustible"));
        Timestamp ts = rs.getTimestamp("fecha_salida");
        if (ts != null) {
            v.setFechaSalida(ts.toLocalDateTime());
        }
        return v;
    }
}
