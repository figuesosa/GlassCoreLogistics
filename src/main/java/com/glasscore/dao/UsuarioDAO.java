package com.glasscore.dao;

import com.glasscore.modelo.Usuario;
import java.util.List;

public interface UsuarioDAO {
    Usuario buscarPorUsername(String username) throws Exception;
    Usuario buscarPorId(int id) throws Exception;
    int insertar(Usuario usuario) throws Exception;
    boolean actualizar(Usuario usuario) throws Exception;
    boolean eliminar(int id) throws Exception;
    List<Usuario> listarTodos() throws Exception;
    int contarAdminsActivos() throws Exception;
}
