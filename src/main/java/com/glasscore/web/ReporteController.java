package com.glasscore.web;

import com.glasscore.servicio.PlanillaServicio;
import com.glasscore.util.ReporteUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/reportes")
public class ReporteController {

    private final PlanillaServicio planillaServicio = new PlanillaServicio();

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
}
