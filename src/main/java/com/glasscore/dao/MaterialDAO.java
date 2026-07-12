package com.glasscore.dao;

import com.glasscore.modelo.Material;
import java.util.List;

public interface MaterialDAO {
    List<Material> listarTodos() throws Exception;
    Material buscarPorTipo(String tipo) throws Exception;
    double stockPorTipo(String tipo) throws Exception;
}
