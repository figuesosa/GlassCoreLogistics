package com.glasscore.web;

import com.glasscore.dao.impl.ClienteDAOImpl;
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
    private final ClienteDAOImpl clienteDAO = new ClienteDAOImpl();

    @GetMapping
    public String pagina(Model model) {
        model.addAttribute("materiales", java.util.List.of());
        model.addAttribute("cotizaciones", java.util.List.of());
        model.addAttribute("clientes", java.util.List.of());
        model.addAttribute("stockVidrio", 0.0);
        model.addAttribute("diasDefault", servicio.diasHabilesDefault());
        try {
            model.addAttribute("materiales", materialDAO.listarTodos());
            model.addAttribute("cotizaciones", servicio.listar());
            model.addAttribute("clientes", clienteDAO.listar());
            model.addAttribute("stockVidrio", materialDAO.stockPorTipo("VIDRIO"));
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
        }
        return "cotizaciones";
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam(required = false) Integer clienteId,
                          @RequestParam(required = false) String cliente,
                          @RequestParam String tipoEstructura,
                          @RequestParam String ancho,
                          @RequestParam String alto,
                          @RequestParam(required = false) Integer vigenciaDias,
                          RedirectAttributes ra) {
        try {
            double a = Double.parseDouble(ancho.trim().replace(',', '.'));
            double h = Double.parseDouble(alto.trim().replace(',', '.'));
            int dias = vigenciaDias == null ? servicio.diasHabilesDefault() : vigenciaDias;
            Cotizacion cot = servicio.calcularYGuardar(cliente, clienteId, tipoEstructura, a, h, dias);
            String totales = String.format(
                    "Subtotal Lps %.2f + ISV 15%% Lps %.2f = Total Lps %.2f (vigencia %d días hábiles)",
                    cot.getSubtotal(), cot.getIsv(), cot.getTotal(), cot.getVigenciaDias());
            if (cot.getAlertaCompra() != null) {
                ra.addFlashAttribute("alerta", cot.getAlertaCompra());
                ra.addFlashAttribute("mensaje",
                        "Cotización #" + cot.getId() + " guardada con alerta de compra (no se bloquea). " + totales);
            } else {
                ra.addFlashAttribute("mensaje", "Cotización #" + cot.getId() + " guardada. " + totales);
            }
        } catch (NumberFormatException nfe) {
            ra.addFlashAttribute("error", "Ancho y alto deben ser numéricos.");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/cotizaciones";
    }
}
