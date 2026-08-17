package com.glasscore.servicio;

import com.glasscore.dao.EmpleadoDAO;
import com.glasscore.dao.HerramientaDAO;
import com.glasscore.dao.impl.EmpleadoDAOImpl;
import com.glasscore.dao.impl.HerramientaDAOImpl;
import com.glasscore.modelo.Empleado;
import com.glasscore.modelo.Herramienta;
import java.util.List;

public class HerramientaServicio {

    private final HerramientaDAO herramientaDAO = new HerramientaDAOImpl();
    private final EmpleadoDAO empleadoDAO = new EmpleadoDAOImpl();

    public List<Herramienta> listar() throws Exception {
        return herramientaDAO.listarTodas();
    }

    public List<Herramienta> listarDisponibles() throws Exception {
        return herramientaDAO.listarDisponibles();
    }

    public List<Empleado> empleadosAsignables() throws Exception {
        List<Empleado> lista = empleadoDAO.listarPorCargo("CHOFER");
        lista.addAll(empleadoDAO.listarPorCargo("INSTALADOR"));
        return lista;
    }

    public void registrar(String codigo, String nombre, String tipo) throws Exception {
        if (codigo == null || codigo.isBlank() || nombre == null || nombre.isBlank()
                || tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException("Código, nombre y tipo son obligatorios.");
        }
        Herramienta h = new Herramienta();
        h.setCodigo(codigo.trim().toUpperCase());
        h.setNombre(nombre.trim());
        h.setTipo(tipo.trim().toUpperCase());
        h.setEstado("DISPONIBLE");
        herramientaDAO.insertar(h);
    }

    public void actualizar(int id, String codigo, String nombre, String tipo) throws Exception {
        if (codigo == null || codigo.isBlank() || nombre == null || nombre.isBlank()
                || tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException("Código, nombre y tipo son obligatorios.");
        }
        Herramienta actual = herramientaDAO.buscarPorId(id);
        if (actual == null) {
            throw new IllegalArgumentException("Herramienta no encontrada.");
        }
        actual.setCodigo(codigo.trim().toUpperCase());
        actual.setNombre(nombre.trim());
        actual.setTipo(tipo.trim().toUpperCase());
        herramientaDAO.actualizar(actual);
    }

    public void eliminar(int id) throws Exception {
        boolean ok = herramientaDAO.eliminar(id);
        if (!ok) {
            throw new IllegalArgumentException("No se pudo borrar la herramienta.");
        }
    }

    public void asignar(int herramientaId, int empleadoId) throws Exception {
        boolean ok = herramientaDAO.asignar(herramientaId, empleadoId);
        if (!ok) {
            throw new IllegalArgumentException(
                    "No se pudo asignar. La herramienta debe estar DISPONIBLE.");
        }
    }

    public void devolver(int herramientaId) throws Exception {
        boolean ok = herramientaDAO.devolver(herramientaId);
        if (!ok) {
            throw new IllegalArgumentException("No se pudo devolver la herramienta.");
        }
    }
}
