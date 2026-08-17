package com.glasscore.web;

import com.glasscore.dao.impl.MaterialDAOImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/inventario")
public class InventarioController {

    private final MaterialDAOImpl materialDAO = new MaterialDAOImpl();

    @GetMapping
    public String pagina(Model model) {
        try {
            model.addAttribute("materiales", materialDAO.listarTodos());
            model.addAttribute("stockVidrio", materialDAO.stockPorTipo("VIDRIO"));
            model.addAttribute("stockAluminio", materialDAO.stockPorTipo("ALUMINIO"));
            model.addAttribute("stockMetal", materialDAO.stockPorTipo("METAL"));
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
        }
        return "inventario";
    }
}
