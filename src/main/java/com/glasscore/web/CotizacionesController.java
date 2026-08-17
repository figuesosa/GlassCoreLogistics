package com.glasscore.web;

import com.glasscore.dao.impl.MaterialDAOImpl;
import com.glasscore.modelo.Cotizacion;
import com.glasscore.servicio.CotizacionServicio;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cotizaciones")
public class CotizacionesController {

    private final CotizacionServicio servicio = new CotizacionServicio();
    private final MaterialDAOImpl materialDAO = new MaterialDAOImpl();

    @GetMapping
    public String pagina(Model model) {
        model.addAttribute("materiales", java.util.List.of());
        model.addAttribute("cotizaciones", java.util.List.of());
        model.addAttribute("stockVidrio", 0.0);
        try {
            model.addAttribute("materiales", materialDAO.listarTodos());
            model.addAttribute("cotizaciones", servicio.listar());
            model.addAttribute("stockVidrio", materialDAO.stockPorTipo("VIDRIO"));
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
        }
        return "cotizaciones";
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam String cliente,
                          @RequestParam String tipoEstructura,
                          @RequestParam String ancho,
                          @RequestParam String alto,
                          RedirectAttributes ra) {
        try {
            double a = Double.parseDouble(ancho.trim().replace(',', '.'));
            double h = Double.parseDouble(alto.trim().replace(',', '.'));
            Cotizacion cot = servicio.calcularYGuardar(cliente, tipoEstructura, a, h);
            if (cot.getAlertaCompra() != null) {
                ra.addFlashAttribute("alerta", cot.getAlertaCompra());
                ra.addFlashAttribute("mensaje",
                        "Cotización #" + cot.getId() + " guardada con alerta de compra (la venta no se bloquea). Subtotal Lps "
                        + String.format("%.2f", cot.getSubtotal()));
            } else {
                ra.addFlashAttribute("mensaje",
                        "Cotización #" + cot.getId() + " guardada. Subtotal Lps "
                        + String.format("%.2f", cot.getSubtotal()));
            }
        } catch (NumberFormatException nfe) {
            ra.addFlashAttribute("error", "Ancho y alto deben ser numéricos.");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/cotizaciones";
    }
}
