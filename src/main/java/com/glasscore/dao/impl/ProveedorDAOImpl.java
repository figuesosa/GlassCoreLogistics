package com.glasscore.dao.impl;

import com.glasscore.conexion.ConexionDB;
import com.glasscore.modelo.Proveedor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProveedorDAOImpl {

    public int insertar(Proveedor p) throws Exception {
        String sql = "INSERT INTO proveedor (nombre, rtn, direccion, activo) VALUES (?,?,?,1)";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getRtn());
            ps.setString(3, p.getDireccion());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }

    public Proveedor buscarPorRtn(String rtn) throws Exception {
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM proveedor WHERE rtn=?")) {
            ps.setString(1, rtn);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public List<Proveedor> listar() throws Exception {
        List<Proveedor> lista = new ArrayList<>();
        String sql = "SELECT p.*, "
                + "(SELECT GROUP_CONCAT(telefono SEPARATOR ', ') FROM proveedor_telefono t WHERE t.proveedor_id=p.id) AS tels, "
                + "(SELECT GROUP_CONCAT(nombre SEPARATOR ', ') FROM proveedor_contacto c WHERE c.proveedor_id=p.id) AS cons "
                + "FROM proveedor p WHERE p.activo=1 ORDER BY p.nombre";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Proveedor p = map(rs);
                p.setTelefonosResumen(rs.getString("tels"));
                p.setContactosResumen(rs.getString("cons"));
                lista.add(p);
            }
        }
        return lista;
    }

    public void agregarTelefono(int proveedorId, String telefono) throws Exception {
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "INSERT INTO proveedor_telefono (proveedor_id, telefono) VALUES (?,?)")) {
            ps.setInt(1, proveedorId);
            ps.setString(2, telefono);
            ps.executeUpdate();
        }
    }

    public void agregarContacto(int proveedorId, String nombre, String cargo, String telefono, String email)
            throws Exception {
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "INSERT INTO proveedor_contacto (proveedor_id, nombre, cargo, telefono, email) VALUES (?,?,?,?,?)")) {
            ps.setInt(1, proveedorId);
            ps.setString(2, nombre);
            ps.setString(3, cargo);
            ps.setString(4, telefono);
            ps.setString(5, email);
            ps.executeUpdate();
        }
    }

    private Proveedor map(ResultSet rs) throws Exception {
        Proveedor p = new Proveedor();
        p.setId(rs.getInt("id"));
        p.setNombre(rs.getString("nombre"));
        p.setRtn(rs.getString("rtn"));
        p.setDireccion(rs.getString("direccion"));
        p.setActivo(rs.getBoolean("activo"));
        return p;
    }
}
