package com.glasscore.web;

import com.glasscore.dao.impl.EmpleadoDAOImpl;
import com.glasscore.modelo.Empleado;
import com.glasscore.modelo.Planilla;
import com.glasscore.servicio.PlanillaServicio;
import java.time.LocalDate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/planilla")
public class PlanillaController {

    private final EmpleadoDAOImpl empleadoDAO = new EmpleadoDAOImpl();
    private final PlanillaServicio planillaServicio = new PlanillaServicio();

    @GetMapping
    public String pagina(Model model) {
        try {
            model.addAttribute("empleados", empleadoDAO.listarTodos());
            model.addAttribute("planillas", planillaServicio.listarHistorico());
            model.addAttribute("cargos", new String[]{"CHOFER", "INSTALADOR", "ADMINISTRATIVO", "SUPERVISOR"});
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
        }
        return "planilla";
    }

    @PostMapping("/empleado")
    public String guardarEmpleado(@RequestParam(required = false) Integer id,
                                  @RequestParam String nombre,
                                  @RequestParam String apellido,
                                  @RequestParam String cargo,
                                  @RequestParam String salarioBase,
                                  @RequestParam(required = false) String telefono,
                                  RedirectAttributes ra) {
        try {
            Empleado e = new Empleado();
            e.setNombre(nombre.trim());
            e.setApellido(apellido.trim());
            e.setCargo(cargo);
            e.setSalarioBase(Double.parseDouble(salarioBase.trim().replace(',', '.')));
            e.setTelefono(telefono == null ? "" : telefono.trim());
            e.setActivo(true);
            if (e.getNombre().isEmpty() || e.getApellido().isEmpty()) {
                throw new IllegalArgumentException("Nombre y apellido son obligatorios.");
            }
            if (id != null) {
                e.setId(id);
                empleadoDAO.actualizar(e);
                ra.addFlashAttribute("mensaje", "Empleado actualizado.");
            } else {
                empleadoDAO.insertar(e);
                ra.addFlashAttribute("mensaje", "Empleado registrado.");
            }
        } catch (NumberFormatException nfe) {
            ra.addFlashAttribute("error", "Salario inválido.");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/planilla";
    }

    @PostMapping("/empleado/borrar")
    public String borrarEmpleado(@RequestParam int id, RedirectAttributes ra) {
        try {
            empleadoDAO.eliminar(id);
            ra.addFlashAttribute("mensaje", "Empleado desactivado (sigue en la base, activo=0).");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/planilla";
    }

    @PostMapping("/cerrar")
    public String cerrar(@RequestParam int empleadoId,
                         @RequestParam String horasExtras,
                         @RequestParam String viaticos,
                         RedirectAttributes ra) {
        try {
            double extras = Double.parseDouble(horasExtras.trim().replace(',', '.'));
            double via = Double.parseDouble(viaticos.trim().replace(',', '.'));
            Planilla p = planillaServicio.calcularYRegistrar(empleadoId, extras, via, LocalDate.now());
            ra.addFlashAttribute("mensaje",
                    "Planilla #" + p.getId() + " cerrada. Neto Lps " + String.format("%.2f", p.getTotalNeto()));
        } catch (NumberFormatException nfe) {
            ra.addFlashAttribute("error", "Montos inválidos.");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/planilla";
    }
}
