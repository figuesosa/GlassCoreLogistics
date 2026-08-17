package com.glasscore.web;

import com.glasscore.dao.impl.CotizacionDAOImpl;
import com.glasscore.dao.impl.MaterialDAOImpl;
import com.glasscore.dao.impl.VehiculoDAOImpl;
import com.glasscore.modelo.Cotizacion;
import com.glasscore.modelo.Material;
import com.glasscore.modelo.Vehiculo;
import com.glasscore.util.WidgetsOnline;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioController {

    private final MaterialDAOImpl materialDAO = new MaterialDAOImpl();
    private final VehiculoDAOImpl vehiculoDAO = new VehiculoDAOImpl();
    private final CotizacionDAOImpl cotizacionDAO = new CotizacionDAOImpl();

    @GetMapping("/")
    public String inicio(Model model) {
        try {
            List<Material> bajo = new ArrayList<>();
            for (Material m : materialDAO.listarTodos()) {
                if (m.isStockBajo()) {
                    bajo.add(m);
                }
            }
            model.addAttribute("stockBajo", bajo);
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
        }
        try {
            List<Vehiculo> mant = new ArrayList<>();
            for (Vehiculo v : vehiculoDAO.listarTodos()) {
                if (v.isCercaMantenimiento()) {
                    mant.add(v);
                }
            }
            model.addAttribute("mantenimiento", mant);
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
        }
        try {
            List<Cotizacion> todas = cotizacionDAO.listarTodas();
            model.addAttribute("cotizacionesRecientes",
                    todas.size() > 5 ? todas.subList(0, 5) : todas);
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
        }
        try {
            model.addAttribute("clima", WidgetsOnline.obtenerClimaTegucigalpa());
        } catch (Exception ex) {
            model.addAttribute("climaError", "Clima no disponible (sin conexión a la API).");
        }
        try {
            model.addAttribute("divisa", WidgetsOnline.obtenerDivisas());
        } catch (Exception ex) {
            model.addAttribute("divisaError", "Tipo de cambio no disponible (sin conexión a la API).");
        }
        return "inicio";
    }
}
