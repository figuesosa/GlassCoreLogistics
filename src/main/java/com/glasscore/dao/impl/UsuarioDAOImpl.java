package com.glasscore.dao.impl;

import com.glasscore.conexion.ConexionDB;
import com.glasscore.dao.UsuarioDAO;
import com.glasscore.modelo.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAOImpl implements UsuarioDAO {

    private static final String SELECT_BASE =
            "SELECT u.*, CONCAT(e.nombre,' ',e.apellido) AS empleado_nombre "
            + "FROM usuario u LEFT JOIN empleado e ON u.empleado_id = e.id ";

    @Override
    public Usuario buscarPorUsername(String username) throws Exception {
        String sql = SELECT_BASE + "WHERE u.username=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    @Override
    public Usuario buscarPorId(int id) throws Exception {
        String sql = SELECT_BASE + "WHERE u.id=?";
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
    public int insertar(Usuario u) throws Exception {
        String sql = "INSERT INTO usuario (username, password_hash, rol, empleado_id, activo) VALUES (?,?,?,?,?)";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            bind(ps, u);
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
    public boolean actualizar(Usuario u) throws Exception {
        String sql = "UPDATE usuario SET username=?, password_hash=?, rol=?, empleado_id=?, activo=? WHERE id=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            bind(ps, u);
            ps.setInt(6, u.getId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean eliminar(int id) throws Exception {
        String sql = "DELETE FROM usuario WHERE id=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public List<Usuario> listarTodos() throws Exception {
        List<Usuario> lista = new ArrayList<>();
        String sql = SELECT_BASE + "ORDER BY u.username";
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
    public int contarAdminsActivos() throws Exception {
        String sql = "SELECT COUNT(*) FROM usuario WHERE rol='ADMIN' AND activo=1";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private void bind(PreparedStatement ps, Usuario u) throws Exception {
        ps.setString(1, u.getUsername());
        ps.setString(2, u.getPasswordHash());
        ps.setString(3, u.getRol());
        if (u.getEmpleadoId() == null) {
            ps.setNull(4, java.sql.Types.INTEGER);
        } else {
            ps.setInt(4, u.getEmpleadoId());
        }
        ps.setBoolean(5, u.isActivo());
    }

    private Usuario map(ResultSet rs) throws Exception {
        Usuario u = new Usuario();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setRol(rs.getString("rol"));
        int emp = rs.getInt("empleado_id");
        u.setEmpleadoId(rs.wasNull() ? null : emp);
        u.setActivo(rs.getBoolean("activo"));
        u.setEmpleadoNombre(rs.getString("empleado_nombre"));
        return u;
    }
}
