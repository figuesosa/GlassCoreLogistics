package com.glasscore.dao;

import com.glasscore.modelo.Planilla;
import java.util.List;

public interface PlanillaDAO {
    int insertar(Planilla planilla) throws Exception;
    List<Planilla> listarTodas() throws Exception;
    List<Planilla> listarPorEmpleado(int empleadoId) throws Exception;
    Planilla buscarPorId(int id) throws Exception;
}
