package com.glasscore.servicio;

import com.glasscore.dao.CotizacionDAO;
import com.glasscore.dao.MaterialDAO;
import com.glasscore.dao.impl.CotizacionDAOImpl;
import com.glasscore.dao.impl.MaterialDAOImpl;
import com.glasscore.modelo.Cotizacion;
import com.glasscore.modelo.Material;
import java.util.ArrayList;
import java.util.List;

public class CotizacionServicio {

    private final CotizacionDAO cotizacionDAO = new CotizacionDAOImpl();
    private final MaterialDAO materialDAO = new MaterialDAOImpl();

    public Cotizacion calcularYGuardar(String cliente, String tipoEstructura,
                                      double ancho, double alto) throws Exception {
        if (ancho <= 0 || alto <= 0) {
            throw new IllegalArgumentException("Ancho y alto deben ser mayores a cero.");
        }
        if (cliente == null || cliente.isBlank()) {
            throw new IllegalArgumentException("Debe indicar el nombre del cliente.");
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
        double subtotal = redondear(
                (areaVidrio * precioVidrio)
                + (metrosAluminio * precioAluminio)
                + (metrosMetal * precioMetal));

        Cotizacion cot = new Cotizacion();
        cot.setCliente(cliente.trim());
        cot.setTipoEstructura(tipoEstructura);
        cot.setAncho(ancho);
        cot.setAlto(alto);
        cot.setAreaVidrio(areaVidrio);
        cot.setMetrosAluminio(metrosAluminio);
        cot.setMetrosMetal(metrosMetal);
        cot.setSubtotal(subtotal);
        cot.setAlertaCompra(alertas.isEmpty() ? null : String.join("\n", alertas));

        int id = cotizacionDAO.insertar(cot);
        cot.setId(id);
        return cot;
    }

    public List<Cotizacion> listar() throws Exception {
        return cotizacionDAO.listarTodas();
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
}
