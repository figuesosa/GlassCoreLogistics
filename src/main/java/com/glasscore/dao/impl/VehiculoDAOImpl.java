package com.glasscore.dao.impl;

import com.glasscore.conexion.ConexionDB;
import com.glasscore.dao.VehiculoDAO;
import com.glasscore.modelo.Vehiculo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class VehiculoDAOImpl implements VehiculoDAO {

    private static final String SELECT_BASE =
            "SELECT v.*, CONCAT(e.nombre,' ',e.apellido) AS chofer_nombre "
            + "FROM vehiculo v LEFT JOIN empleado e ON v.chofer_id = e.id ";

    @Override
    public int insertar(Vehiculo v) throws Exception {
        String sql = "INSERT INTO vehiculo (placa, marca, km_actual, km_limite_mantenimiento, chofer_id) "
                + "VALUES (?,?,?,?,?)";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, v.getPlaca());
            ps.setString(2, v.getMarca());
            ps.setInt(3, v.getKmActual());
            ps.setInt(4, v.getKmLimiteMantenimiento());
            if (v.getChoferId() == null) {
                ps.setNull(5, java.sql.Types.INTEGER);
            } else {
                ps.setInt(5, v.getChoferId());
            }
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
    public boolean actualizar(Vehiculo v) throws Exception {
        String sql = "UPDATE vehiculo SET placa=?, marca=?, km_actual=?, km_limite_mantenimiento=?, chofer_id=? WHERE id=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, v.getPlaca());
            ps.setString(2, v.getMarca());
            ps.setInt(3, v.getKmActual());
            ps.setInt(4, v.getKmLimiteMantenimiento());
            if (v.getChoferId() == null) {
                ps.setNull(5, java.sql.Types.INTEGER);
            } else {
                ps.setInt(5, v.getChoferId());
            }
            ps.setInt(6, v.getId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean eliminar(int id) throws Exception {
        String sql = "DELETE FROM vehiculo WHERE id=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Vehiculo buscarPorId(int id) throws Exception {
        String sql = SELECT_BASE + "WHERE v.id=?";
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

    @Override
    public List<Vehiculo> listarTodos() throws Exception {
        List<Vehiculo> lista = new ArrayList<>();
        String sql = SELECT_BASE + "ORDER BY v.placa";
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
    public boolean actualizarKilometraje(int vehiculoId, int nuevoKm) throws Exception {
        try (Connection cn = ConexionDB.getConnection()) {
            return actualizarKilometraje(cn, vehiculoId, nuevoKm);
        }
    }

    @Override
    public boolean actualizarKilometraje(Connection cn, int vehiculoId, int nuevoKm) throws Exception {
        String sql = "UPDATE vehiculo SET km_actual=? WHERE id=?";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, nuevoKm);
            ps.setInt(2, vehiculoId);
            return ps.executeUpdate() > 0;
        }
    }

    private Vehiculo map(ResultSet rs) throws Exception {
        Vehiculo v = new Vehiculo();
        v.setId(rs.getInt("id"));
        v.setPlaca(rs.getString("placa"));
        v.setMarca(rs.getString("marca"));
        v.setKmActual(rs.getInt("km_actual"));
        v.setKmLimiteMantenimiento(rs.getInt("km_limite_mantenimiento"));
        int chofer = rs.getInt("chofer_id");
        v.setChoferId(rs.wasNull() ? null : chofer);
        v.setChoferNombre(rs.getString("chofer_nombre"));
        return v;
    }
}
