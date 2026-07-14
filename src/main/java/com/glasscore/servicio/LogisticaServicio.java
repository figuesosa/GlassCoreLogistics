package com.glasscore.servicio;

import com.glasscore.conexion.ConexionDB;
import com.glasscore.dao.HerramientaDAO;
import com.glasscore.dao.VehiculoDAO;
import com.glasscore.dao.ViajeDAO;
import com.glasscore.dao.impl.HerramientaDAOImpl;
import com.glasscore.dao.impl.VehiculoDAOImpl;
import com.glasscore.dao.impl.ViajeDAOImpl;
import com.glasscore.modelo.Herramienta;
import com.glasscore.modelo.Vehiculo;
import com.glasscore.modelo.Viaje;
import java.sql.Connection;
import java.util.List;
import java.util.stream.Collectors;

public class LogisticaServicio {

    public static final String RUTA_FIJA = "Tegucigalpa - Comayagua";
    public static final int KM_SIMPLE = 85;
    public static final int KM_REDONDO = 170;
    public static final double FACTOR_RENDIMIENTO = 0.12;
    public static final double PRECIO_COMBUSTIBLE_LPS = 32.50;

    private final VehiculoDAO vehiculoDAO = new VehiculoDAOImpl();
    private final ViajeDAO viajeDAO = new ViajeDAOImpl();
    private final HerramientaDAO herramientaDAO = new HerramientaDAOImpl();

    public static class ResultadoCalculo {
        public final int kilometros;
        public final double litros;
        public final double gastoLps;

        public ResultadoCalculo(int kilometros, double litros, double gastoLps) {
            this.kilometros = kilometros;
            this.litros = litros;
            this.gastoLps = gastoLps;
        }
    }

    public ResultadoCalculo calcularRuta(boolean redondo) {
        int km = redondo ? KM_REDONDO : KM_SIMPLE;
        double litros = Math.round(km * FACTOR_RENDIMIENTO * 1000.0) / 1000.0;
        double gasto = Math.round(litros * PRECIO_COMBUSTIBLE_LPS * 100.0) / 100.0;
        return new ResultadoCalculo(km, litros, gasto);
    }

    public Viaje autorizarYRegistrarViaje(int vehiculoId, boolean redondo) throws Exception {
        Vehiculo veh = vehiculoDAO.buscarPorId(vehiculoId);
        if (veh == null) {
            throw new IllegalArgumentException("Vehículo no encontrado.");
        }
        if (veh.getChoferId() == null) {
            throw new IllegalArgumentException("El vehículo no tiene chofer asignado.");
        }

        ResultadoCalculo calc = calcularRuta(redondo);
        int proyeccion = veh.getKmActual() + calc.kilometros;

        if (proyeccion >= veh.getKmLimiteMantenimiento()) {
            throw new MantenimientoRequeridoException(
                    "BLOQUEO DE SEGURIDAD: el vehículo " + veh.getPlaca()
                    + " debe ingresar obligatoriamente a mantenimiento preventivo.\n"
                    + "Km actual: " + veh.getKmActual()
                    + " + ruta " + calc.kilometros + " km = " + proyeccion
                    + " (límite: " + veh.getKmLimiteMantenimiento() + " km)."
            );
        }

        Viaje viaje = new Viaje();
        viaje.setVehiculoId(veh.getId());
        viaje.setPlaca(veh.getPlaca());
        viaje.setChoferId(veh.getChoferId());
        viaje.setChoferNombre(veh.getChoferNombre());
        viaje.setRuta(RUTA_FIJA + (redondo ? " (Redondo)" : " (Simple)"));
        viaje.setEsRedondo(redondo);
        viaje.setKilometros(calc.kilometros);
        viaje.setFactorRendimiento(FACTOR_RENDIMIENTO);
        viaje.setPrecioCombustible(PRECIO_COMBUSTIBLE_LPS);
        viaje.setLitrosEstimados(calc.litros);
        viaje.setGastoCombustible(calc.gastoLps);

        List<Herramienta> herramientas = herramientaDAO.listarPorEmpleado(veh.getChoferId());
        String custodia = herramientas.isEmpty()
                ? "Sin herramientas asignadas"
                : herramientas.stream()
                    .map(h -> h.getCodigo() + " " + h.getNombre())
                    .collect(Collectors.joining(", "));
        viaje.setHerramientasCustodia(custodia);

        Connection cn = ConexionDB.getConnection();
        try {
            cn.setAutoCommit(false);
            int id = viajeDAO.insertar(cn, viaje);
            viaje.setId(id);
            vehiculoDAO.actualizarKilometraje(cn, veh.getId(), proyeccion);
            cn.commit();
            return viaje;
        } catch (Exception ex) {
            try {
                cn.rollback();
            } catch (Exception ignored) {
                // ya se re-lanza el error original
            }
            throw ex;
        } finally {
            try {
                cn.setAutoCommit(true);
            } catch (Exception ignored) {
                // cierre final
            }
            cn.close();
        }
    }

    public List<Viaje> listarViajes() throws Exception {
        return viajeDAO.listarTodos();
    }

    public Viaje buscarViaje(int id) throws Exception {
        Viaje v = viajeDAO.buscarPorId(id);
        if (v != null) {
            List<Herramienta> herramientas = herramientaDAO.listarPorEmpleado(v.getChoferId());
            String custodia = herramientas.isEmpty()
                    ? "Sin herramientas asignadas"
                    : herramientas.stream()
                        .map(h -> h.getCodigo() + " " + h.getNombre())
                        .collect(Collectors.joining(", "));
            v.setHerramientasCustodia(custodia);
        }
        return v;
    }

    public static class MantenimientoRequeridoException extends Exception {
        public MantenimientoRequeridoException(String message) {
            super(message);
        }
    }
}
