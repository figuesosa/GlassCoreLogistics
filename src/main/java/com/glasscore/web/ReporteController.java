package com.glasscore.web;

import com.glasscore.modelo.Venta;
import com.glasscore.servicio.PlanillaServicio;
import com.glasscore.servicio.VentaServicio;
import com.glasscore.util.ReporteUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/reportes")
public class ReporteController {

    private final PlanillaServicio planillaServicio = new PlanillaServicio();
    private final VentaServicio ventaServicio = new VentaServicio();

    @GetMapping
    public String pagina(@RequestParam(defaultValue = "MENSUAL") String frecuencia,
                         @RequestParam(required = false) Integer anio,
                         @RequestParam(required = false) Integer mes,
                         @RequestParam(required = false) Integer trimestre,
                         Model model) {
        int year = anio == null ? LocalDate.now().getYear() : anio;
        int month = mes == null ? LocalDate.now().getMonthValue() : mes;
        int trim = trimestre == null ? ((LocalDate.now().getMonthValue() - 1) / 3) + 1 : trimestre;
        model.addAttribute("ventas", java.util.List.of());
        model.addAttribute("planillas", java.util.List.of());
        model.addAttribute("kpiCantidad", 0);
        model.addAttribute("kpiFacturado", 0.0);
        model.addAttribute("kpiIsv", 0.0);
        model.addAttribute("kpiMargen", 0.0);
        model.addAttribute("porProducto", java.util.Map.of());
        try {
            List<Venta> ventas = ventaServicio.listarPeriodo(frecuencia, year, month, trim);
            java.util.Map<String, Object> resumen = ventaServicio.resumenEjecutivo(ventas);
            model.addAttribute("ventas", ventas);
            model.addAttribute("resumen", resumen);
            model.addAttribute("kpiCantidad", resumen.get("cantidad"));
            model.addAttribute("kpiFacturado", resumen.get("facturado"));
            model.addAttribute("kpiIsv", resumen.get("isv"));
            model.addAttribute("kpiMargen", resumen.get("margen"));
            model.addAttribute("porProducto", resumen.get("porProducto"));
            model.addAttribute("planillas", planillaServicio.listarHistorico());
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
        }
        model.addAttribute("frecuencia", frecuencia);
        model.addAttribute("anio", year);
        model.addAttribute("mes", month);
        model.addAttribute("trimestre", trim);
        return "reportes";
    }

    @GetMapping("/planilla")
    public Object comprobante(@RequestParam int empleadoId, RedirectAttributes ra) {
        try {
            if (!planillaServicio.tienePlanilla(empleadoId)) {
                ra.addFlashAttribute("error",
                        "Ese empleado no tiene planilla registrada. Cierre una planilla primero.");
                return "redirect:/planilla";
            }
            byte[] pdf = ReporteUtil.generarComprobantePlanillaPdf(empleadoId);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=Comprobante_Planilla_Emp_" + empleadoId + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);
        } catch (Exception ex) {
            ra.addFlashAttribute("error", "Error Jasper: " + ex.getMessage());
            return "redirect:/planilla";
        }
    }

    @GetMapping("/hoja-ruta")
    public Object hojaRuta(@RequestParam int viajeId, RedirectAttributes ra) {
        try {
            byte[] pdf = ReporteUtil.generarHojaRutaPdf(viajeId);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=Hoja_Ruta_Viaje_" + viajeId + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);
        } catch (Exception ex) {
            ra.addFlashAttribute("error", "Error Jasper: " + ex.getMessage());
            return "redirect:/logistica";
        }
    }

    @GetMapping("/ventas")
    public Object ventasPdf(@RequestParam(defaultValue = "MENSUAL") String frecuencia,
                            @RequestParam(required = false) Integer anio,
                            @RequestParam(required = false) Integer mes,
                            @RequestParam(required = false) Integer trimestre,
                            RedirectAttributes ra) {
        int year = anio == null ? LocalDate.now().getYear() : anio;
        int month = mes == null ? LocalDate.now().getMonthValue() : mes;
        int trim = trimestre == null ? 1 : trimestre;
        try {
            LocalDateTime[] rango = VentaServicio.rango(frecuencia, year, month, trim);
            byte[] pdf = ReporteUtil.generarVentasPdf(rango[0], rango[1], frecuencia);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=Ventas_" + frecuencia + "_" + year + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);
        } catch (Exception ex) {
            ra.addFlashAttribute("error", "Error Jasper: " + ex.getMessage());
            return "redirect:/reportes";
        }
    }
}
