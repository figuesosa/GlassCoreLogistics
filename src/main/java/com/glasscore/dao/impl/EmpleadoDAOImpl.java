package com.glasscore.dao.impl;

import com.glasscore.conexion.ConexionDB;
import com.glasscore.dao.EmpleadoDAO;
import com.glasscore.modelo.Empleado;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class EmpleadoDAOImpl implements EmpleadoDAO {

    @Override
    public int insertar(Empleado e) throws Exception {
        String sql = "INSERT INTO empleado (nombre, apellido, cargo, salario_base, telefono, activo, identidad, fecha_ingreso, vacaciones_gozadas) "
                + "VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getNombre());
            ps.setString(2, e.getApellido());
            ps.setString(3, e.getCargo());
            ps.setDouble(4, e.getSalarioBase());
            ps.setString(5, e.getTelefono());
            ps.setBoolean(6, e.isActivo());
            ps.setString(7, e.getIdentidad());
            if (e.getFechaIngreso() != null) {
                ps.setDate(8, java.sql.Date.valueOf(e.getFechaIngreso()));
            } else {
                ps.setDate(8, java.sql.Date.valueOf(java.time.LocalDate.now()));
            }
            ps.setInt(9, e.getVacacionesGozadas());
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
    public boolean actualizar(Empleado e) throws Exception {
        String sql = "UPDATE empleado SET nombre=?, apellido=?, cargo=?, salario_base=?, telefono=?, activo=?, "
                + "identidad=?, fecha_ingreso=?, vacaciones_gozadas=? WHERE id=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, e.getNombre());
            ps.setString(2, e.getApellido());
            ps.setString(3, e.getCargo());
            ps.setDouble(4, e.getSalarioBase());
            ps.setString(5, e.getTelefono());
            ps.setBoolean(6, e.isActivo());
            ps.setString(7, e.getIdentidad());
            if (e.getFechaIngreso() != null) {
                ps.setDate(8, java.sql.Date.valueOf(e.getFechaIngreso()));
            } else {
                ps.setNull(8, java.sql.Types.DATE);
            }
            ps.setInt(9, e.getVacacionesGozadas());
            ps.setInt(10, e.getId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean eliminar(int id) throws Exception {
        String sql = "UPDATE empleado SET activo=0 WHERE id=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Empleado buscarPorId(int id) throws Exception {
        String sql = "SELECT * FROM empleado WHERE id=?";
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
    public List<Empleado> listarTodos() throws Exception {
        List<Empleado> lista = new ArrayList<>();
        String sql = "SELECT * FROM empleado WHERE activo=1 ORDER BY apellido, nombre";
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
    public List<Empleado> listarPorCargo(String cargo) throws Exception {
        List<Empleado> lista = new ArrayList<>();
        String sql = "SELECT * FROM empleado WHERE activo=1 AND cargo=? ORDER BY apellido, nombre";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, cargo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(map(rs));
                }
            }
        }
        return lista;
    }

    @Override
    public Empleado buscarPorIdentidad(String identidad) throws Exception {
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM empleado WHERE identidad=?")) {
            ps.setString(1, identidad);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    private Empleado map(ResultSet rs) throws Exception {
        Empleado e = new Empleado();
        e.setId(rs.getInt("id"));
        e.setNombre(rs.getString("nombre"));
        e.setApellido(rs.getString("apellido"));
        e.setCargo(rs.getString("cargo"));
        e.setSalarioBase(rs.getDouble("salario_base"));
        e.setTelefono(rs.getString("telefono"));
        e.setActivo(rs.getBoolean("activo"));
        try {
            e.setIdentidad(rs.getString("identidad"));
            java.sql.Date ing = rs.getDate("fecha_ingreso");
            if (ing != null) {
                e.setFechaIngreso(ing.toLocalDate());
            }
            e.setVacacionesGozadas(rs.getInt("vacaciones_gozadas"));
        } catch (Exception ignored) {
            // columnas nuevas aún no presentes
        }
        return e;
    }
}
