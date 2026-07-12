package com.glasscore.dao;

import com.glasscore.modelo.Vehiculo;
import java.util.List;

public interface VehiculoDAO {
    int insertar(Vehiculo vehiculo) throws Exception;
    boolean actualizar(Vehiculo vehiculo) throws Exception;
    boolean eliminar(int id) throws Exception;
    Vehiculo buscarPorId(int id) throws Exception;
    List<Vehiculo> listarTodos() throws Exception;
    boolean actualizarKilometraje(int vehiculoId, int nuevoKm) throws Exception;
}
