package com.glasscore.web;

import com.glasscore.dao.impl.ProveedorDAOImpl;
import com.glasscore.modelo.Proveedor;
import com.glasscore.util.DocumentoUnico;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/proveedores")
public class ProveedoresController {

    private final ProveedorDAOImpl proveedorDAO = new ProveedorDAOImpl();

    @GetMapping
    public String pagina(Model model) {
        try {
            model.addAttribute("proveedores", proveedorDAO.listar());
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
        }
        return "proveedores";
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam String nombre,
                          @RequestParam String rtn,
                          @RequestParam(required = false) String direccion,
                          @RequestParam(required = false) String telefono,
                          RedirectAttributes ra) {
        try {
            DocumentoUnico.exigir(rtn, "RTN");
            if (proveedorDAO.buscarPorRtn(rtn.trim()) != null) {
                throw new IllegalArgumentException("Ya existe un proveedor con ese RTN.");
            }
            Proveedor p = new Proveedor();
            p.setNombre(nombre.trim());
            p.setRtn(rtn.trim());
            p.setDireccion(direccion == null ? "" : direccion.trim());
            if (p.getNombre().isEmpty()) {
                throw new IllegalArgumentException("El nombre del proveedor es obligatorio.");
            }
            int id = proveedorDAO.insertar(p);
            if (telefono != null && !telefono.isBlank()) {
                proveedorDAO.agregarTelefono(id, telefono.trim());
            }
            ra.addFlashAttribute("mensaje", "Proveedor registrado.");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/proveedores";
    }

    @PostMapping("/telefono")
    public String telefono(@RequestParam int proveedorId, @RequestParam String telefono, RedirectAttributes ra) {
        try {
            if (telefono == null || telefono.isBlank()) {
                throw new IllegalArgumentException("Indique el teléfono.");
            }
            proveedorDAO.agregarTelefono(proveedorId, telefono.trim());
            ra.addFlashAttribute("mensaje", "Teléfono agregado.");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/proveedores";
    }

    @PostMapping("/contacto")
    public String contacto(@RequestParam int proveedorId,
                           @RequestParam String nombre,
                           @RequestParam(required = false) String cargo,
                           @RequestParam(required = false) String telefono,
                           @RequestParam(required = false) String email,
                           RedirectAttributes ra) {
        try {
            if (nombre == null || nombre.isBlank()) {
                throw new IllegalArgumentException("El nombre del agente es obligatorio.");
            }
            proveedorDAO.agregarContacto(proveedorId, nombre.trim(),
                    cargo == null ? "" : cargo.trim(),
                    telefono == null ? "" : telefono.trim(),
                    email == null ? "" : email.trim());
            ra.addFlashAttribute("mensaje", "Agente comercial asociado.");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/proveedores";
    }
}
