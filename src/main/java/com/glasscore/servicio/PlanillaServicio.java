package com.glasscore.servicio;

import com.glasscore.dao.EmpleadoDAO;
import com.glasscore.dao.PlanillaDAO;
import com.glasscore.dao.impl.EmpleadoDAOImpl;
import com.glasscore.dao.impl.PlanillaDAOImpl;
import com.glasscore.modelo.Empleado;
import com.glasscore.modelo.Planilla;
import com.glasscore.util.DiasHabiles;
import java.time.LocalDate;
import java.util.List;

public class PlanillaServicio {

    private final EmpleadoDAO empleadoDAO = new EmpleadoDAOImpl();
    private final PlanillaDAO planillaDAO = new PlanillaDAOImpl();

    public Planilla calcularYRegistrar(int empleadoId, double horasExtras, double viaticos,
                                       LocalDate fechaPago) throws Exception {
        return calcularYRegistrar(empleadoId, horasExtras, viaticos, 0, false, false, fechaPago);
    }

    public Planilla calcularYRegistrar(int empleadoId, double horasExtras, double viaticos,
                                       double deducciones, boolean aplica14vo, boolean aplicaAguinaldo,
                                       LocalDate fechaPago) throws Exception {
        Empleado emp = empleadoDAO.buscarPorId(empleadoId);
        if (emp == null || !emp.isActivo()) {
            throw new IllegalArgumentException("Empleado no encontrado o inactivo.");
        }
        if (horasExtras < 0 || viaticos < 0 || deducciones < 0) {
            throw new IllegalArgumentException("Montos no pueden ser negativos.");
        }

        LocalDate pago = fechaPago == null ? LocalDate.now() : fechaPago;
        int dias = DiasHabiles.diasLaboradosEnAnio(emp.getFechaIngreso(), pago);
        double proporcional = Math.round(emp.getSalarioBase() * Math.min(1.0, dias / 360.0) * 100.0) / 100.0;
        double m14 = aplica14vo ? proporcional : 0;
        double mag = aplicaAguinaldo ? proporcional : 0;
        double neto = emp.getSalarioBase() + horasExtras + viaticos + m14 + mag - deducciones;

        Planilla p = new Planilla();
        p.setEmpleadoId(empleadoId);
        p.setEmpleadoNombre(emp.getNombreCompleto());
        p.setSalarioBase(emp.getSalarioBase());
        p.setHorasExtras(horasExtras);
        p.setViaticos(viaticos);
        p.setAplica14vo(aplica14vo);
        p.setAplicaAguinaldo(aplicaAguinaldo);
        p.setMonto14vo(m14);
        p.setMontoAguinaldo(mag);
        p.setDeducciones(deducciones);
        p.setTotalNeto(Math.round(neto * 100.0) / 100.0);
        p.setFechaPago(pago);

        int id = planillaDAO.insertar(p);
        p.setId(id);
        return p;
    }

    public List<Planilla> listarHistorico() throws Exception {
        return planillaDAO.listarTodas();
    }

    public boolean tienePlanilla(int empleadoId) throws Exception {
        return !planillaDAO.listarPorEmpleado(empleadoId).isEmpty();
    }

    public Planilla buscar(int id) throws Exception {
        return planillaDAO.buscarPorId(id);
    }
}
