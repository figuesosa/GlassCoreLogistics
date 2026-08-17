package com.glasscore.dao.impl;

import com.glasscore.conexion.ConexionDB;
import com.glasscore.modelo.Cliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAOImpl {

    public int insertar(Cliente c) throws Exception {
        String sql = "INSERT INTO cliente (nombre, identidad_rtn, telefono, email, direccion, activo) VALUES (?,?,?,?,?,1)";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getIdentidadRtn());
            ps.setString(3, c.getTelefono());
            ps.setString(4, c.getEmail());
            ps.setString(5, c.getDireccion());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }

    public boolean actualizar(Cliente c) throws Exception {
        String sql = "UPDATE cliente SET nombre=?, identidad_rtn=?, telefono=?, email=?, direccion=? WHERE id=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getIdentidadRtn());
            ps.setString(3, c.getTelefono());
            ps.setString(4, c.getEmail());
            ps.setString(5, c.getDireccion());
            ps.setInt(6, c.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public Cliente buscarPorId(int id) throws Exception {
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM cliente WHERE id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public Cliente buscarPorIdentidad(String doc) throws Exception {
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM cliente WHERE identidad_rtn=?")) {
            ps.setString(1, doc);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public List<Cliente> listar() throws Exception {
        List<Cliente> lista = new ArrayList<>();
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM cliente WHERE activo=1 ORDER BY nombre");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(map(rs));
            }
        }
        return lista;
    }

    private Cliente map(ResultSet rs) throws Exception {
        Cliente c = new Cliente();
        c.setId(rs.getInt("id"));
        c.setNombre(rs.getString("nombre"));
        c.setIdentidadRtn(rs.getString("identidad_rtn"));
        c.setTelefono(rs.getString("telefono"));
        c.setEmail(rs.getString("email"));
        c.setDireccion(rs.getString("direccion"));
        c.setActivo(rs.getBoolean("activo"));
        return c;
    }
}
