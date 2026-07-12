package com.glasscore.dao;

import com.glasscore.modelo.Empleado;
import java.util.List;

public interface EmpleadoDAO {
    int insertar(Empleado empleado) throws Exception;
    boolean actualizar(Empleado empleado) throws Exception;
    boolean eliminar(int id) throws Exception;
    Empleado buscarPorId(int id) throws Exception;
    List<Empleado> listarTodos() throws Exception;
    List<Empleado> listarPorCargo(String cargo) throws Exception;
}
