package com.glasscore.dao.impl;

import com.glasscore.conexion.ConexionDB;
import com.glasscore.dao.PlanillaDAO;
import com.glasscore.modelo.Planilla;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PlanillaDAOImpl implements PlanillaDAO {

    private static final String SELECT_BASE =
            "SELECT p.*, CONCAT(e.nombre,' ',e.apellido) AS empleado_nombre "
            + "FROM planilla p INNER JOIN empleado e ON p.empleado_id = e.id ";

    @Override
    public int insertar(Planilla p) throws Exception {
        String sql = "INSERT INTO planilla (empleado_id, salario_base, horas_extras, viaticos, total_neto, fecha_pago, "
                + "aplica_14vo, aplica_aguinaldo, monto_14vo, monto_aguinaldo, deducciones) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, p.getEmpleadoId());
            ps.setDouble(2, p.getSalarioBase());
            ps.setDouble(3, p.getHorasExtras());
            ps.setDouble(4, p.getViaticos());
            ps.setDouble(5, p.getTotalNeto());
            ps.setDate(6, Date.valueOf(p.getFechaPago()));
            ps.setBoolean(7, p.isAplica14vo());
            ps.setBoolean(8, p.isAplicaAguinaldo());
            ps.setDouble(9, p.getMonto14vo());
            ps.setDouble(10, p.getMontoAguinaldo());
            ps.setDouble(11, p.getDeducciones());
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
    public List<Planilla> listarTodas() throws Exception {
        List<Planilla> lista = new ArrayList<>();
        String sql = SELECT_BASE + "ORDER BY p.fecha_pago DESC, p.id DESC";
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
    public List<Planilla> listarPorEmpleado(int empleadoId) throws Exception {
        List<Planilla> lista = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE p.empleado_id=? ORDER BY p.fecha_pago DESC";
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

    @Override
    public Planilla buscarPorId(int id) throws Exception {
        String sql = SELECT_BASE + "WHERE p.id=?";
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

    private Planilla map(ResultSet rs) throws Exception {
        Planilla p = new Planilla();
        p.setId(rs.getInt("id"));
        p.setEmpleadoId(rs.getInt("empleado_id"));
        p.setEmpleadoNombre(rs.getString("empleado_nombre"));
        p.setSalarioBase(rs.getDouble("salario_base"));
        p.setHorasExtras(rs.getDouble("horas_extras"));
        p.setViaticos(rs.getDouble("viaticos"));
        p.setTotalNeto(rs.getDouble("total_neto"));
        Date d = rs.getDate("fecha_pago");
        if (d != null) {
            p.setFechaPago(d.toLocalDate());
        }
        try {
            p.setAplica14vo(rs.getBoolean("aplica_14vo"));
            p.setAplicaAguinaldo(rs.getBoolean("aplica_aguinaldo"));
            p.setMonto14vo(rs.getDouble("monto_14vo"));
            p.setMontoAguinaldo(rs.getDouble("monto_aguinaldo"));
            p.setDeducciones(rs.getDouble("deducciones"));
        } catch (Exception ignored) {
            // columnas nuevas
        }
        return p;
    }
}
