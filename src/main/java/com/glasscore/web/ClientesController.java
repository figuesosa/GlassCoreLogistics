package com.glasscore.web;

import com.glasscore.dao.impl.ClienteDAOImpl;
import com.glasscore.modelo.Cliente;
import com.glasscore.util.DocumentoUnico;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/clientes")
public class ClientesController {

    private final ClienteDAOImpl clienteDAO = new ClienteDAOImpl();

    @GetMapping
    public String pagina(Model model) {
        try {
            model.addAttribute("clientes", clienteDAO.listar());
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
        }
        return "clientes";
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam(required = false) Integer id,
                          @RequestParam String nombre,
                          @RequestParam String identidadRtn,
                          @RequestParam(required = false) String telefono,
                          @RequestParam(required = false) String email,
                          @RequestParam(required = false) String direccion,
                          RedirectAttributes ra) {
        try {
            DocumentoUnico.exigir(identidadRtn, "Identidad / RTN");
            String doc = identidadRtn.trim();
            Cliente otro = clienteDAO.buscarPorIdentidad(doc);
            if (otro != null && (id == null || otro.getId() != id)) {
                throw new IllegalArgumentException("Ya existe un cliente con ese documento / RTN.");
            }
            Cliente c = new Cliente();
            c.setNombre(nombre.trim());
            c.setIdentidadRtn(doc);
            c.setTelefono(telefono == null ? "" : telefono.trim());
            c.setEmail(email == null ? "" : email.trim());
            c.setDireccion(direccion == null ? "" : direccion.trim());
            if (c.getNombre().isEmpty()) {
                throw new IllegalArgumentException("El nombre del cliente es obligatorio.");
            }
            if (id == null) {
                clienteDAO.insertar(c);
                ra.addFlashAttribute("mensaje", "Cliente registrado.");
            } else {
                c.setId(id);
                clienteDAO.actualizar(c);
                ra.addFlashAttribute("mensaje", "Cliente actualizado.");
            }
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/clientes";
    }
}
