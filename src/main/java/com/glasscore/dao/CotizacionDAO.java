package com.glasscore.dao;

import com.glasscore.modelo.Cotizacion;
import java.util.List;

public interface CotizacionDAO {
    int insertar(Cotizacion cotizacion) throws Exception;
    List<Cotizacion> listarTodas() throws Exception;
    Cotizacion buscarPorId(int id) throws Exception;
}
