package com.glasscore.servicio;

import com.glasscore.dao.EmpleadoDAO;
import com.glasscore.dao.PlanillaDAO;
import com.glasscore.dao.impl.EmpleadoDAOImpl;
import com.glasscore.dao.impl.PlanillaDAOImpl;
import com.glasscore.modelo.Empleado;
import com.glasscore.modelo.Planilla;
import java.time.LocalDate;
import java.util.List;

public class PlanillaServicio {

    private final EmpleadoDAO empleadoDAO = new EmpleadoDAOImpl();
    private final PlanillaDAO planillaDAO = new PlanillaDAOImpl();

    public Planilla calcularYRegistrar(int empleadoId, double horasExtras, double viaticos,
                                       LocalDate fechaPago) throws Exception {
        Empleado emp = empleadoDAO.buscarPorId(empleadoId);
        if (emp == null || !emp.isActivo()) {
            throw new IllegalArgumentException("Empleado no encontrado o inactivo.");
        }
        if (horasExtras < 0 || viaticos < 0) {
            throw new IllegalArgumentException("Horas extras y viáticos no pueden ser negativos.");
        }

        double salarioBase = emp.getSalarioBase();
        double totalNeto = salarioBase + horasExtras + viaticos;

        Planilla p = new Planilla();
        p.setEmpleadoId(empleadoId);
        p.setEmpleadoNombre(emp.getNombreCompleto());
        p.setSalarioBase(salarioBase);
        p.setHorasExtras(horasExtras);
        p.setViaticos(viaticos);
        p.setTotalNeto(totalNeto);
        p.setFechaPago(fechaPago == null ? LocalDate.now() : fechaPago);

        int id = planillaDAO.insertar(p);
        p.setId(id);
        return p;
    }

    public List<Planilla> listarHistorico() throws Exception {
        return planillaDAO.listarTodas();
    }

    public Planilla buscar(int id) throws Exception {
        return planillaDAO.buscarPorId(id);
    }
}
