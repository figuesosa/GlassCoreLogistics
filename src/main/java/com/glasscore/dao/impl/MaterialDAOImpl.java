package com.glasscore.dao.impl;

import com.glasscore.conexion.ConexionDB;
import com.glasscore.dao.MaterialDAO;
import com.glasscore.modelo.Material;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class MaterialDAOImpl implements MaterialDAO {

    @Override
    public List<Material> listarTodos() throws Exception {
        List<Material> lista = new ArrayList<>();
        String sql = "SELECT * FROM material ORDER BY tipo, nombre";
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
    public Material buscarPorTipo(String tipo) throws Exception {
        String sql = "SELECT * FROM material WHERE tipo=? ORDER BY id LIMIT 1";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, tipo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    @Override
    public double stockPorTipo(String tipo) throws Exception {
        String sql = "SELECT COALESCE(SUM(stock),0) AS total FROM material WHERE tipo=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, tipo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total");
                }
            }
        }
        return 0;
    }

    private Material map(ResultSet rs) throws Exception {
        Material m = new Material();
        m.setId(rs.getInt("id"));
        m.setNombre(rs.getString("nombre"));
        m.setTipo(rs.getString("tipo"));
        m.setUnidad(rs.getString("unidad"));
        m.setStock(rs.getDouble("stock"));
        m.setPrecioUnitario(rs.getDouble("precio_unitario"));
        return m;
    }
}
