package com.glasscore.web;

import com.glasscore.modelo.Viaje;
import com.glasscore.servicio.LogisticaServicio;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/campo")
public class CampoApiController {

    private final LogisticaServicio logisticaServicio = new LogisticaServicio();

    @GetMapping("/viajes")
    public List<Map<String, Object>> viajes() throws Exception {
        List<Map<String, Object>> lista = new ArrayList<>();
        for (Viaje v : logisticaServicio.listarViajes()) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", v.getId());
            m.put("placa", v.getPlaca());
            m.put("chofer", v.getChoferNombre());
            m.put("origen", v.getOrigen());
            m.put("destino", v.getDestino());
            m.put("ruta", v.getRuta());
            m.put("kilometros", v.getKilometros());
            m.put("gastoLps", v.getGastoCombustible());
            m.put("herramientas", v.getHerramientasCustodia());
            m.put("fecha", v.getFechaSalida() == null ? "" : v.getFechaSalida().toString());
            lista.add(m);
        }
        return lista;
    }
}
