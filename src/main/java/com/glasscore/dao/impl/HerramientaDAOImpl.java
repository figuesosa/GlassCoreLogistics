package com.glasscore.dao.impl;

import com.glasscore.conexion.ConexionDB;
import com.glasscore.dao.HerramientaDAO;
import com.glasscore.modelo.Herramienta;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class HerramientaDAOImpl implements HerramientaDAO {

    private static final String SELECT_BASE =
            "SELECT h.*, CONCAT(e.nombre,' ',e.apellido) AS empleado_nombre "
            + "FROM herramienta h LEFT JOIN empleado e ON h.empleado_id = e.id ";

    @Override
    public int insertar(Herramienta h) throws Exception {
        String sql = "INSERT INTO herramienta (codigo, nombre, tipo, estado, empleado_id) VALUES (?,?,?,?,?)";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, h.getCodigo());
            ps.setString(2, h.getNombre());
            ps.setString(3, h.getTipo());
            ps.setString(4, h.getEstado() == null ? "DISPONIBLE" : h.getEstado());
            if (h.getEmpleadoId() == null) {
                ps.setNull(5, java.sql.Types.INTEGER);
            } else {
                ps.setInt(5, h.getEmpleadoId());
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
    public boolean actualizar(Herramienta h) throws Exception {
        String sql = "UPDATE herramienta SET codigo=?, nombre=?, tipo=?, estado=?, empleado_id=? WHERE id=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, h.getCodigo());
            ps.setString(2, h.getNombre());
            ps.setString(3, h.getTipo());
            ps.setString(4, h.getEstado());
            if (h.getEmpleadoId() == null) {
                ps.setNull(5, java.sql.Types.INTEGER);
            } else {
                ps.setInt(5, h.getEmpleadoId());
            }
            ps.setInt(6, h.getId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean eliminar(int id) throws Exception {
        String sql = "DELETE FROM herramienta WHERE id=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Herramienta buscarPorId(int id) throws Exception {
        String sql = SELECT_BASE + "WHERE h.id=?";
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
    public List<Herramienta> listarTodas() throws Exception {
        List<Herramienta> lista = new ArrayList<>();
        String sql = SELECT_BASE + "ORDER BY h.codigo";
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
    public List<Herramienta> listarDisponibles() throws Exception {
        List<Herramienta> lista = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE h.estado='DISPONIBLE' ORDER BY h.codigo";
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
    public List<Herramienta> listarPorEmpleado(int empleadoId) throws Exception {
        List<Herramienta> lista = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE h.empleado_id=? ORDER BY h.codigo";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, empleadoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(map(rs));
                }
            }
        }
        return lista;
    }

    /**
     * Regla: al asignar, estado pasa automáticamente de DISPONIBLE a ASIGNADA.
     */
    @Override
    public boolean asignar(int herramientaId, int empleadoId) throws Exception {
        String sql = "UPDATE herramienta SET estado='ASIGNADA', empleado_id=? "
                + "WHERE id=? AND estado='DISPONIBLE'";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, empleadoId);
            ps.setInt(2, herramientaId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean devolver(int herramientaId) throws Exception {
        String sql = "UPDATE herramienta SET estado='DISPONIBLE', empleado_id=NULL WHERE id=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, herramientaId);
            return ps.executeUpdate() > 0;
        }
    }

    private Herramienta map(ResultSet rs) throws Exception {
        Herramienta h = new Herramienta();
        h.setId(rs.getInt("id"));
        h.setCodigo(rs.getString("codigo"));
        h.setNombre(rs.getString("nombre"));
        h.setTipo(rs.getString("tipo"));
        h.setEstado(rs.getString("estado"));
        int empId = rs.getInt("empleado_id");
        h.setEmpleadoId(rs.wasNull() ? null : empId);
        h.setEmpleadoNombre(rs.getString("empleado_nombre"));
        return h;
    }
}
