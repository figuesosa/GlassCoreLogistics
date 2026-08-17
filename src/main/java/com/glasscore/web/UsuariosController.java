package com.glasscore.web;

import com.glasscore.dao.UsuarioDAO;
import com.glasscore.dao.impl.EmpleadoDAOImpl;
import com.glasscore.dao.impl.UsuarioDAOImpl;
import com.glasscore.modelo.Usuario;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuarios")
public class UsuariosController {

    private final UsuarioDAO usuarioDAO = new UsuarioDAOImpl();
    private final EmpleadoDAOImpl empleadoDAO = new EmpleadoDAOImpl();
    private final PasswordEncoder encoder;

    public UsuariosController(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    @GetMapping
    public String pagina(Model model) {
        try {
            model.addAttribute("usuarios", usuarioDAO.listarTodos());
            model.addAttribute("empleados", empleadoDAO.listarTodos());
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
        }
        return "usuarios";
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam(required = false) Integer id,
                          @RequestParam String username,
                          @RequestParam(required = false) String password,
                          @RequestParam String rol,
                          @RequestParam(required = false) Integer empleadoId,
                          @RequestParam(defaultValue = "true") boolean activo,
                          RedirectAttributes ra) {
        try {
            String user = username.trim().toLowerCase();
            if (user.isEmpty()) {
                throw new IllegalArgumentException("El usuario es obligatorio.");
            }
            if (!"ADMIN".equals(rol) && !"OPERADOR".equals(rol)) {
                throw new IllegalArgumentException("Rol inválido.");
            }
            boolean estaActivo = activo;
            if (id == null) {
                if (password == null || password.isBlank()) {
                    throw new IllegalArgumentException("La contraseña es obligatoria al crear.");
                }
                if (usuarioDAO.buscarPorUsername(user) != null) {
                    throw new IllegalArgumentException("Ese nombre de usuario ya existe.");
                }
                Usuario u = new Usuario();
                u.setUsername(user);
                u.setPasswordHash(encoder.encode(password));
                u.setRol(rol);
                u.setEmpleadoId(empleadoId);
                u.setActivo(estaActivo);
                usuarioDAO.insertar(u);
                ra.addFlashAttribute("mensaje", "Usuario " + user + " creado.");
            } else {
                Usuario actual = usuarioDAO.buscarPorId(id);
                if (actual == null) {
                    throw new IllegalArgumentException("Usuario no encontrado.");
                }
                Usuario otro = usuarioDAO.buscarPorUsername(user);
                if (otro != null && otro.getId() != id) {
                    throw new IllegalArgumentException("Ese nombre de usuario ya existe.");
                }
                actual.setUsername(user);
                actual.setRol(rol);
                actual.setEmpleadoId(empleadoId);
                actual.setActivo(estaActivo);
                if (password != null && !password.isBlank()) {
                    actual.setPasswordHash(encoder.encode(password));
                }
                usuarioDAO.actualizar(actual);
                ra.addFlashAttribute("mensaje", "Usuario actualizado.");
            }
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/usuarios";
    }

    @PostMapping("/borrar")
    public String borrar(@RequestParam int id, Authentication auth, RedirectAttributes ra) {
        try {
            Usuario u = usuarioDAO.buscarPorId(id);
            if (u == null) {
                throw new IllegalArgumentException("Usuario no encontrado.");
            }
            if (auth != null && u.getUsername().equalsIgnoreCase(auth.getName())) {
                throw new IllegalArgumentException("No puede borrar la sesión actual.");
            }
            if ("ADMIN".equals(u.getRol()) && u.isActivo() && usuarioDAO.contarAdminsActivos() <= 1) {
                throw new IllegalArgumentException("Debe quedar al menos un admin activo.");
            }
            usuarioDAO.eliminar(id);
            ra.addFlashAttribute("mensaje", "Usuario " + u.getUsername() + " borrado.");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/usuarios";
    }

    @PostMapping("/password")
    public String cambiarPassword(@RequestParam String actual,
                                  @RequestParam String nueva,
                                  @RequestParam String confirmar,
                                  Authentication auth,
                                  RedirectAttributes ra) {
        try {
            if (auth == null) {
                throw new IllegalArgumentException("No hay sesión.");
            }
            if (nueva == null || nueva.length() < 6) {
                throw new IllegalArgumentException("La nueva contraseña debe tener al menos 6 caracteres.");
            }
            if (!nueva.equals(confirmar)) {
                throw new IllegalArgumentException("La confirmación no coincide.");
            }
            Usuario u = usuarioDAO.buscarPorUsername(auth.getName());
            if (u == null) {
                throw new IllegalArgumentException("Usuario no encontrado.");
            }
            if (!encoder.matches(actual, u.getPasswordHash())) {
                throw new IllegalArgumentException("La contraseña actual no es correcta.");
            }
            u.setPasswordHash(encoder.encode(nueva));
            usuarioDAO.actualizar(u);
            ra.addFlashAttribute("mensaje", "Contraseña actualizada.");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/usuarios";
    }
}
