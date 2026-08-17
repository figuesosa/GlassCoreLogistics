package com.glasscore.web;

import com.glasscore.servicio.HerramientaServicio;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/herramientas")
public class HerramientasController {

    private final HerramientaServicio servicio = new HerramientaServicio();

    @GetMapping
    public String pagina(Model model) {
        try {
            model.addAttribute("herramientas", servicio.listar());
            model.addAttribute("disponibles", servicio.listarDisponibles());
            model.addAttribute("empleados", servicio.empleadosAsignables());
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
        }
        return "herramientas";
    }

    @PostMapping("/registrar")
    public String registrar(@RequestParam(required = false) Integer id,
                            @RequestParam String codigo,
                            @RequestParam String nombre,
                            @RequestParam String tipo,
                            RedirectAttributes ra) {
        try {
            if (id != null) {
                servicio.actualizar(id, codigo, nombre, tipo);
                ra.addFlashAttribute("mensaje", "Herramienta actualizada.");
            } else {
                servicio.registrar(codigo, nombre, tipo);
                ra.addFlashAttribute("mensaje", "Herramienta registrada en estado DISPONIBLE.");
            }
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/herramientas";
    }

    @PostMapping("/borrar")
    public String borrar(@RequestParam int id, RedirectAttributes ra) {
        try {
            servicio.eliminar(id);
            ra.addFlashAttribute("mensaje", "Herramienta borrada.");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/herramientas";
    }

    @PostMapping("/asignar")
    public String asignar(@RequestParam int herramientaId,
                          @RequestParam int empleadoId,
                          RedirectAttributes ra) {
        try {
            servicio.asignar(herramientaId, empleadoId);
            ra.addFlashAttribute("mensaje", "Asignada: estado DISPONIBLE → ASIGNADA.");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/herramientas";
    }

    @PostMapping("/devolver")
    public String devolver(@RequestParam int herramientaId, RedirectAttributes ra) {
        try {
            servicio.devolver(herramientaId);
            ra.addFlashAttribute("mensaje", "Devuelta a bodega: estado ASIGNADA → DISPONIBLE.");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/herramientas";
    }
}
