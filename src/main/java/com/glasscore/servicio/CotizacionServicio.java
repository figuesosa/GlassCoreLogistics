package com.glasscore.servicio;

import com.glasscore.dao.CotizacionDAO;
import com.glasscore.dao.MaterialDAO;
import com.glasscore.dao.impl.CotizacionDAOImpl;
import com.glasscore.dao.impl.MaterialDAOImpl;
import com.glasscore.dao.impl.ClienteDAOImpl;
import com.glasscore.dao.impl.FiscalDAOImpl;
import com.glasscore.modelo.Cliente;
import com.glasscore.modelo.Cotizacion;
import com.glasscore.modelo.Material;
import com.glasscore.util.DiasHabiles;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CotizacionServicio {

    public static final double TASA_ISV = 0.15;
    public static final int VIGENCIA_DIAS_DEFAULT = 5;

    private final CotizacionDAO cotizacionDAO = new CotizacionDAOImpl();
    private final MaterialDAO materialDAO = new MaterialDAOImpl();
    private final ClienteDAOImpl clienteDAO = new ClienteDAOImpl();
    private final FiscalDAOImpl fiscalDAO = new FiscalDAOImpl();

    public Cotizacion calcularYGuardar(String cliente, String tipoEstructura,
                                      double ancho, double alto) throws Exception {
        return calcularYGuardar(cliente, tipoEstructura, ancho, alto, VIGENCIA_DIAS_DEFAULT);
    }

    public Cotizacion calcularYGuardar(String cliente, String tipoEstructura,
                                      double ancho, double alto, int vigenciaDias) throws Exception {
        return calcularYGuardar(cliente, null, tipoEstructura, ancho, alto, vigenciaDias);
    }

    public Cotizacion calcularYGuardar(String cliente, Integer clienteId, String tipoEstructura,
                                      double ancho, double alto, int vigenciaDias) throws Exception {
        if (ancho <= 0 || alto <= 0) {
            throw new IllegalArgumentException("Ancho y alto deben ser mayores a cero.");
        }
        if (vigenciaDias < 2 || vigenciaDias > 15) {
            throw new IllegalArgumentException("La vigencia debe ser de 2 a 15 días hábiles.");
        }
        if (clienteId != null) {
            Cliente cl = clienteDAO.buscarPorId(clienteId);
            if (cl == null) {
                throw new IllegalArgumentException("Cliente no encontrado.");
            }
            cliente = cl.getNombre();
        }
        if (cliente == null || cliente.isBlank()) {
            throw new IllegalArgumentException("Debe indicar el cliente.");
        }

        double areaVidrio = redondear(ancho * alto);
        double perimetro = redondear(2 * (ancho + alto));
        double metrosAluminio = perimetro;
        double metrosMetal = perimetro;

        double stockVidrio = materialDAO.stockPorTipo("VIDRIO");
        double stockAluminio = materialDAO.stockPorTipo("ALUMINIO");
        double stockMetal = materialDAO.stockPorTipo("METAL");

        List<String> alertas = new ArrayList<>();
        if (areaVidrio > stockVidrio) {
            alertas.add(String.format(
                    "ALERTA DE COMPRA REQUERIDA: adquirir %.3f m² de vidrio (necesario %.3f, stock %.3f)",
                    areaVidrio - stockVidrio, areaVidrio, stockVidrio));
        }
        if (metrosAluminio > stockAluminio) {
            alertas.add(String.format(
                    "ALERTA DE COMPRA REQUERIDA: adquirir %.3f m lineales de aluminio (necesario %.3f, stock %.3f)",
                    metrosAluminio - stockAluminio, metrosAluminio, stockAluminio));
        }
        if (metrosMetal > stockMetal) {
            alertas.add(String.format(
                    "ALERTA DE COMPRA REQUERIDA: adquirir %.3f m lineales de metal (necesario %.3f, stock %.3f)",
                    metrosMetal - stockMetal, metrosMetal, stockMetal));
        }

        double precioVidrio = precioPromedio("VIDRIO");
        double precioAluminio = precioPromedio("ALUMINIO");
        double precioMetal = precioPromedio("METAL");
        double subtotal = redondearDinero(
                (areaVidrio * precioVidrio)
                + (metrosAluminio * precioAluminio)
                + (metrosMetal * precioMetal));
        double isv = redondearDinero(subtotal * TASA_ISV);
        double total = redondearDinero(subtotal + isv);
        LocalDateTime ahora = LocalDateTime.now();

        Cotizacion cot = new Cotizacion();
        cot.setCliente(cliente.trim());
        cot.setTipoEstructura(tipoEstructura);
        cot.setAncho(ancho);
        cot.setAlto(alto);
        cot.setAreaVidrio(areaVidrio);
        cot.setMetrosAluminio(metrosAluminio);
        cot.setMetrosMetal(metrosMetal);
        cot.setSubtotal(subtotal);
        cot.setIsv(isv);
        cot.setTotal(total);
        cot.setVigenciaDias(vigenciaDias);
        cot.setFecha(ahora);
        cot.setFechaVencimiento(DiasHabiles.vencerDesde(ahora, vigenciaDias));
        cot.setClienteId(clienteId);
        cot.setEstado("VIGENTE");
        cot.setAlertaCompra(alertas.isEmpty() ? null : String.join("\n", alertas));

        int id = cotizacionDAO.insertar(cot);
        cot.setId(id);
        return cot;
    }

    public List<Cotizacion> listar() throws Exception {
        return cotizacionDAO.listarTodas();
    }

    public int diasHabilesDefault() {
        try {
            return fiscalDAO.diasHabilesDefault();
        } catch (Exception ex) {
            return VIGENCIA_DIAS_DEFAULT;
        }
    }

    private double precioPromedio(String tipo) throws Exception {
        List<Material> materiales = materialDAO.listarTodos();
        double suma = 0;
        int n = 0;
        for (Material m : materiales) {
            if (tipo.equals(m.getTipo())) {
                suma += m.getPrecioUnitario();
                n++;
            }
        }
        return n == 0 ? 0 : suma / n;
    }

    private double redondear(double v) {
        return Math.round(v * 1000.0) / 1000.0;
    }

    private double redondearDinero(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
