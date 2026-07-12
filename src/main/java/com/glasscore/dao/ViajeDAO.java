package com.glasscore.dao;

import com.glasscore.modelo.Viaje;
import java.util.List;

public interface ViajeDAO {
    int insertar(Viaje viaje) throws Exception;
    List<Viaje> listarTodos() throws Exception;
    Viaje buscarPorId(int id) throws Exception;
}
