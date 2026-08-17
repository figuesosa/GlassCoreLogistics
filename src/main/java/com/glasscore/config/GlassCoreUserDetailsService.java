package com.glasscore.config;

import com.glasscore.dao.UsuarioDAO;
import com.glasscore.dao.impl.UsuarioDAOImpl;
import com.glasscore.modelo.Usuario;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class GlassCoreUserDetailsService implements UserDetailsService {

    private final UsuarioDAO usuarioDAO = new UsuarioDAOImpl();

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            Usuario u = usuarioDAO.buscarPorUsername(username);
            if (u == null || !u.isActivo()) {
                throw new UsernameNotFoundException("Usuario no encontrado");
            }
            return new User(
                    u.getUsername(),
                    u.getPasswordHash(),
                    List.of(new SimpleGrantedAuthority("ROLE_" + u.getRol()))
            );
        } catch (UsernameNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new UsernameNotFoundException("No se pudo validar el usuario", ex);
        }
    }
}
