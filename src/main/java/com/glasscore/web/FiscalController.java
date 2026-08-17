package com.glasscore.web;

import com.glasscore.dao.impl.FiscalDAOImpl;
import com.glasscore.modelo.ConfigFiscal;
import java.time.LocalDate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/fiscal")
public class FiscalController {

    private final FiscalDAOImpl fiscalDAO = new FiscalDAOImpl();

    @GetMapping
    public String pagina(Model model) {
        try {
            model.addAttribute("fiscal", fiscalDAO.cargar());
            model.addAttribute("diasHabiles", fiscalDAO.diasHabilesDefault());
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
        }
        return "fiscal";
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam String rtnEmpresa,
                          @RequestParam String cai,
                          @RequestParam String fechaLimite,
                          @RequestParam int rangoInicial,
                          @RequestParam int rangoFinal,
                          @RequestParam int correlativoActual,
                          @RequestParam int diasHabiles,
                          RedirectAttributes ra) {
        try {
            if (rangoInicial < 1 || rangoFinal < rangoInicial) {
                throw new IllegalArgumentException("El rango fiscal es inválido.");
            }
            if (correlativoActual < rangoInicial || correlativoActual > rangoFinal) {
                throw new IllegalArgumentException("El correlativo debe estar dentro del rango autorizado.");
            }
            if (diasHabiles < 2 || diasHabiles > 15) {
                throw new IllegalArgumentException("Los días hábiles base deben estar entre 2 y 15.");
            }
            ConfigFiscal c = new ConfigFiscal();
            c.setRtnEmpresa(rtnEmpresa.trim());
            c.setCai(cai.trim());
            c.setFechaLimiteEmision(LocalDate.parse(fechaLimite));
            c.setRangoInicial(rangoInicial);
            c.setRangoFinal(rangoFinal);
            c.setCorrelativoActual(correlativoActual);
            fiscalDAO.guardar(c);
            fiscalDAO.guardarDiasHabiles(diasHabiles);
            ra.addFlashAttribute("mensaje", "Parámetros fiscales y vigencia de cotización actualizados.");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/fiscal";
    }
}
