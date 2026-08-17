package com.glasscore.web;

import com.glasscore.util.WidgetsOnline;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioController {

    @GetMapping("/")
    public String inicio(Model model) {
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
