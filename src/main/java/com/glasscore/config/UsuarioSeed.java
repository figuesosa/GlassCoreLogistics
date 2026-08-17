package com.glasscore.config;

import com.glasscore.dao.UsuarioDAO;
import com.glasscore.dao.impl.UsuarioDAOImpl;
import com.glasscore.modelo.Usuario;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@DependsOn("schemaBootstrap")
public class UsuarioSeed {

    private final PasswordEncoder encoder;
    private final UsuarioDAO usuarioDAO = new UsuarioDAOImpl();

    public UsuarioSeed(PasswordEncoder encoder, DataSourceBridge bridge) {
        this.encoder = encoder;
    }

    @PostConstruct
    public void seed() throws Exception {
        crearSiFalta("admin", "admin123", "ADMIN", 5);
        crearSiFalta("operador", "operador123", "OPERADOR", 1);
        crearSiFalta("contador", "contador123", "CONTADOR", 5);
        crearSiFalta("cajero", "cajero123", "CAJERO", 4);
    }

    private void crearSiFalta(String username, String password, String rol, Integer empleadoId)
            throws Exception {
        if (usuarioDAO.buscarPorUsername(username) != null) {
            return;
        }
        Usuario u = new Usuario();
        u.setUsername(username);
        u.setPasswordHash(encoder.encode(password));
        u.setRol(rol);
        u.setEmpleadoId(empleadoId);
        u.setActivo(true);
        usuarioDAO.insertar(u);
    }
}
