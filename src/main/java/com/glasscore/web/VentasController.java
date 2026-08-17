package com.glasscore.web;

import com.glasscore.dao.impl.CotizacionDAOImpl;
import com.glasscore.modelo.Cotizacion;
import com.glasscore.modelo.Venta;
import com.glasscore.servicio.VentaServicio;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/ventas")
public class VentasController {

    private final VentaServicio ventaServicio = new VentaServicio();
    private final CotizacionDAOImpl cotizacionDAO = new CotizacionDAOImpl();

    @GetMapping
    public String pagina(Model model) {
        try {
            model.addAttribute("ventas", ventaServicio.listar());
            List<Cotizacion> vigentes = new ArrayList<>();
            for (Cotizacion c : cotizacionDAO.listarTodas()) {
                if (!c.isConvertida() && !c.isVencida()) {
                    vigentes.add(c);
                }
            }
            model.addAttribute("vigentes", vigentes);
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
        }
        return "ventas";
    }

    @PostMapping("/convertir")
    public String convertir(@RequestParam int cotizacionId, RedirectAttributes ra) {
        try {
            Venta v = ventaServicio.convertirCotizacion(cotizacionId);
            ra.addFlashAttribute("mensaje",
                    "Orden #" + cotizacionId + " convertida. Factura " + v.getNumeroFactura()
                    + " · CAI " + v.getCai() + " · Total Lps " + String.format("%.2f", v.getTotal()));
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/ventas";
    }
}
