package com.glasscore.servicio;

import com.glasscore.conexion.ConexionDB;
import com.glasscore.dao.impl.CotizacionDAOImpl;
import com.glasscore.dao.impl.FiscalDAOImpl;
import com.glasscore.dao.impl.MaterialDAOImpl;
import com.glasscore.modelo.ConfigFiscal;
import com.glasscore.modelo.Cotizacion;
import com.glasscore.modelo.Venta;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class VentaServicio {

    private final CotizacionDAOImpl cotizacionDAO = new CotizacionDAOImpl();
    private final FiscalDAOImpl fiscalDAO = new FiscalDAOImpl();
    private final MaterialDAOImpl materialDAO = new MaterialDAOImpl();

    public Venta convertirCotizacion(int cotizacionId) throws Exception {
        Cotizacion cot = cotizacionDAO.buscarPorId(cotizacionId);
        if (cot == null) {
            throw new IllegalArgumentException("No existe la cotización / orden #" + cotizacionId + ".");
        }
        if (cot.isConvertida()) {
            throw new IllegalArgumentException("La orden #" + cotizacionId + " ya fue convertida a venta.");
        }
        if (cot.isVencida()) {
            throw new IllegalArgumentException("La cotización #" + cotizacionId + " está vencida.");
        }
        ConfigFiscal cfg = fiscalDAO.cargar();
        if (cfg == null) {
            throw new IllegalArgumentException("Configure primero los parámetros fiscales SAR.");
        }

        Connection cn = ConexionDB.getConnection();
        try {
            cn.setAutoCommit(false);
            String numero = fiscalDAO.tomarCorrelativo(cn, cfg);
            materialDAO.descontarPorTipo(cn, "VIDRIO", cot.getAreaVidrio());
            materialDAO.descontarPorTipo(cn, "ALUMINIO", cot.getMetrosAluminio());
            materialDAO.descontarPorTipo(cn, "METAL", cot.getMetrosMetal());

            Venta v = new Venta();
            v.setCotizacionId(cot.getId());
            v.setNumeroFactura(numero);
            v.setCai(cfg.getCai());
            v.setSubtotal(cot.getSubtotal());
            v.setIsv(cot.getIsv());
            v.setTotal(cot.getTotal());
            v.setRetencion(0);
            v.setCliente(cot.getCliente());
            v.setTipoEstructura(cot.getTipoEstructura());
            int id = fiscalDAO.insertarVenta(cn, v);
            cotizacionDAO.marcarConvertida(cn, cot.getId(), numero, cfg.getCai());
            cn.commit();
            v.setId(id);
            v.setFecha(LocalDateTime.now());
            return v;
        } catch (Exception ex) {
            try {
                cn.rollback();
            } catch (Exception ignored) {
                // se relanza el error original
            }
            throw ex;
        } finally {
            try {
                cn.setAutoCommit(true);
            } catch (Exception ignored) {
                // cierre
            }
            cn.close();
        }
    }

    public List<Venta> listar() throws Exception {
        return fiscalDAO.listarVentas();
    }

    public List<Venta> listarPeriodo(String frecuencia, int anio, Integer mes, Integer trimestre)
            throws Exception {
        LocalDateTime[] rango = rango(frecuencia, anio, mes, trimestre);
        List<Venta> filtradas = new ArrayList<>();
        for (Venta v : listar()) {
            if (v.getFecha() == null) {
                continue;
            }
            if (!v.getFecha().isBefore(rango[0]) && v.getFecha().isBefore(rango[1])) {
                filtradas.add(v);
            }
        }
        return filtradas;
    }

    public static LocalDateTime[] rango(String frecuencia, int anio, Integer mes, Integer trimestre) {
        String f = frecuencia == null ? "MENSUAL" : frecuencia.toUpperCase();
        LocalDate inicio;
        LocalDate finExclusivo;
        if ("ANUAL".equals(f)) {
            inicio = LocalDate.of(anio, 1, 1);
            finExclusivo = inicio.plusYears(1);
        } else if ("TRIMESTRAL".equals(f)) {
            int t = trimestre == null ? 1 : Math.min(4, Math.max(1, trimestre));
            inicio = LocalDate.of(anio, (t - 1) * 3 + 1, 1);
            finExclusivo = inicio.plusMonths(3);
        } else {
            int m = mes == null ? LocalDate.now().getMonthValue() : mes;
            inicio = LocalDate.of(anio, m, 1);
            finExclusivo = inicio.plusMonths(1);
        }
        return new LocalDateTime[]{inicio.atStartOfDay(), finExclusivo.atStartOfDay()};
    }

    public Map<String, Object> resumenEjecutivo(List<Venta> ventas) {
        double facturado = 0;
        double isv = 0;
        double subtotal = 0;
        Map<String, Integer> porProducto = new LinkedHashMap<>();
        for (Venta v : ventas) {
            facturado += v.getTotal();
            isv += v.getIsv();
            subtotal += v.getSubtotal();
            String tipo = v.getTipoEstructura() == null ? "OTRO" : v.getTipoEstructura();
            porProducto.put(tipo, porProducto.getOrDefault(tipo, 0) + 1);
        }
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("cantidad", ventas.size());
        r.put("facturado", redondear(facturado));
        r.put("isv", redondear(isv));
        r.put("subtotal", redondear(subtotal));
        r.put("margen", redondear(subtotal));
        r.put("porProducto", porProducto);
        return r;
    }

    private static double redondear(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
