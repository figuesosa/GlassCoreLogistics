package com.glasscore.dao;

import com.glasscore.modelo.Herramienta;
import java.util.List;

public interface HerramientaDAO {
    int insertar(Herramienta herramienta) throws Exception;
    boolean actualizar(Herramienta herramienta) throws Exception;
    boolean eliminar(int id) throws Exception;
    Herramienta buscarPorId(int id) throws Exception;
    List<Herramienta> listarTodas() throws Exception;
    List<Herramienta> listarDisponibles() throws Exception;
    List<Herramienta> listarPorEmpleado(int empleadoId) throws Exception;
    boolean asignar(int herramientaId, int empleadoId) throws Exception;
    boolean devolver(int herramientaId) throws Exception;
}
