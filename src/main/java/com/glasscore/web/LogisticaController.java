package com.glasscore.web;

import com.glasscore.dao.impl.EmpleadoDAOImpl;
import com.glasscore.dao.impl.VehiculoDAOImpl;
import com.glasscore.modelo.Vehiculo;
import com.glasscore.modelo.Viaje;
import com.glasscore.servicio.LogisticaServicio;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/logistica")
public class LogisticaController {

    private final VehiculoDAOImpl vehiculoDAO = new VehiculoDAOImpl();
    private final EmpleadoDAOImpl empleadoDAO = new EmpleadoDAOImpl();
    private final LogisticaServicio logisticaServicio = new LogisticaServicio();

    @GetMapping
    public String pagina(@RequestParam(value = "redondo", required = false) Boolean redondo,
                         Model model) {
        boolean esRedondo = Boolean.TRUE.equals(redondo);
        LogisticaServicio.ResultadoCalculo calc = logisticaServicio.calcularRuta(esRedondo);
        try {
            model.addAttribute("vehiculos", vehiculoDAO.listarTodos());
            model.addAttribute("choferes", empleadoDAO.listarPorCargo("CHOFER"));
            model.addAttribute("viajes", logisticaServicio.listarViajes());
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
        }
        model.addAttribute("redondo", esRedondo);
        model.addAttribute("calc", calc);
        return "logistica";
    }

    @PostMapping("/vehiculo")
    public String guardarVehiculo(@RequestParam(required = false) Integer id,
                                  @RequestParam String placa,
                                  @RequestParam String marca,
                                  @RequestParam String kmActual,
                                  @RequestParam String kmLimite,
                                  @RequestParam(required = false) Integer choferId,
                                  RedirectAttributes ra) {
        try {
            Vehiculo v = new Vehiculo();
            v.setPlaca(placa.trim().toUpperCase());
            v.setMarca(marca.trim());
            v.setKmActual(Integer.parseInt(kmActual.trim()));
            v.setKmLimiteMantenimiento(Integer.parseInt(kmLimite.trim()));
            v.setChoferId(choferId);
            if (v.getPlaca().isEmpty() || v.getMarca().isEmpty()) {
                throw new IllegalArgumentException("Placa y marca son obligatorias.");
            }
            if (id != null) {
                v.setId(id);
                vehiculoDAO.actualizar(v);
                ra.addFlashAttribute("mensaje", "Vehículo actualizado.");
            } else {
                vehiculoDAO.insertar(v);
                ra.addFlashAttribute("mensaje", "Vehículo registrado.");
            }
        } catch (NumberFormatException nfe) {
            ra.addFlashAttribute("error", "Kilometrajes deben ser enteros.");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/logistica";
    }

    @PostMapping("/vehiculo/borrar")
    public String borrarVehiculo(@RequestParam int id, RedirectAttributes ra) {
        try {
            vehiculoDAO.eliminar(id);
            ra.addFlashAttribute("mensaje", "Vehículo borrado.");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/logistica";
    }

    @PostMapping("/autorizar")
    public String autorizar(@RequestParam int vehiculoId,
                            @RequestParam(required = false) Boolean redondo,
                            RedirectAttributes ra) {
        try {
            Viaje viaje = logisticaServicio.autorizarYRegistrarViaje(vehiculoId, Boolean.TRUE.equals(redondo));
            ra.addFlashAttribute("mensaje",
                    "Salida autorizada. Viaje #" + viaje.getId()
                    + " | Gasto Lps " + String.format("%.2f", viaje.getGastoCombustible())
                    + " | Custodia: " + viaje.getHerramientasCustodia());
        } catch (LogisticaServicio.MantenimientoRequeridoException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/logistica" + (Boolean.TRUE.equals(redondo) ? "?redondo=true" : "");
    }
}
