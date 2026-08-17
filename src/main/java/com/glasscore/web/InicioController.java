package com.glasscore.web;

import com.glasscore.dao.impl.CotizacionDAOImpl;
import com.glasscore.dao.impl.FiscalDAOImpl;
import com.glasscore.dao.impl.MaterialDAOImpl;
import com.glasscore.dao.impl.VehiculoDAOImpl;
import com.glasscore.modelo.ConfigFiscal;
import com.glasscore.modelo.Cotizacion;
import com.glasscore.modelo.Material;
import com.glasscore.modelo.Venta;
import com.glasscore.modelo.Vehiculo;
import com.glasscore.servicio.PlanillaServicio;
import com.glasscore.servicio.VentaServicio;
import com.glasscore.util.WidgetsOnline;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioController {

    private final MaterialDAOImpl materialDAO = new MaterialDAOImpl();
    private final VehiculoDAOImpl vehiculoDAO = new VehiculoDAOImpl();
    private final CotizacionDAOImpl cotizacionDAO = new CotizacionDAOImpl();
    private final FiscalDAOImpl fiscalDAO = new FiscalDAOImpl();
    private final VentaServicio ventaServicio = new VentaServicio();
    private final PlanillaServicio planillaServicio = new PlanillaServicio();

    @GetMapping("/")
    public String inicio(Model model) {
        try {
            List<Material> bajo = new ArrayList<>();
            for (Material m : materialDAO.listarTodos()) {
                if (m.isStockBajo()) {
                    bajo.add(m);
                }
            }
            model.addAttribute("stockBajo", bajo);
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
        }
        try {
            List<Vehiculo> mant = new ArrayList<>();
            for (Vehiculo v : vehiculoDAO.listarTodos()) {
                if (v.isCercaMantenimiento()) {
                    mant.add(v);
                }
            }
            model.addAttribute("mantenimiento", mant);
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
        }
        try {
            List<Cotizacion> todas = cotizacionDAO.listarTodas();
            model.addAttribute("cotizacionesRecientes",
                    todas.size() > 5 ? todas.subList(0, 5) : todas);
            int vencidas = 0;
            int vigentes = 0;
            for (Cotizacion c : todas) {
                if (c.isConvertida()) {
                    continue;
                }
                if (c.isVencida()) {
                    vencidas++;
                } else {
                    vigentes++;
                }
            }
            model.addAttribute("cotizacionesVencidas", vencidas);
            model.addAttribute("kpiCotizaciones", vigentes);
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
        }
        try {
            List<Venta> ventas = ventaServicio.listarPeriodo(
                    "MENSUAL", LocalDate.now().getYear(), LocalDate.now().getMonthValue(), null);
            model.addAttribute("kpiVentasMes", ventaServicio.resumenEjecutivo(ventas).get("facturado"));
            model.addAttribute("kpiVentasCantidad", ventas.size());
        } catch (Exception ex) {
            model.addAttribute("kpiVentasMes", 0);
            model.addAttribute("kpiVentasCantidad", 0);
        }
        try {
            ConfigFiscal fiscal = fiscalDAO.cargar();
            model.addAttribute("kpiCaiRestantes", fiscal == null ? 0 : fiscal.restantes());
            model.addAttribute("kpiCai", fiscal == null ? "—" : fiscal.getCai());
        } catch (Exception ex) {
            model.addAttribute("kpiCaiRestantes", 0);
        }
        try {
            model.addAttribute("kpiPlanillas", planillaServicio.listarHistorico().size());
        } catch (Exception ex) {
            model.addAttribute("kpiPlanillas", 0);
        }
        try {
            model.addAttribute("clima", WidgetsOnline.obtenerClimaTegucigalpa());
        } catch (Exception ex) {
            model.addAttribute("climaError", "Clima no disponible (sin conexión a la API).");
        }
        try {
            model.addAttribute("divisa", WidgetsOnline.obtenerDivisas());
        } catch (Exception ex) {
            model.addAttribute("divisaError", "Tipo de cambio no disponible (sin conexión a la API).");
        }
        return "inicio";
    }
}
