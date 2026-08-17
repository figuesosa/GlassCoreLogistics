package com.glasscore.modelo;

public class Empleado {

    private int id;
    private String nombre;
    private String apellido;
    private String cargo;
    private double salarioBase;
    private String telefono;
    private boolean activo;
    private String identidad;
    private java.time.LocalDate fechaIngreso;
    private int vacacionesGozadas;

    public Empleado() {
    }

    public Empleado(int id, String nombre, String apellido, String cargo,
                    double salarioBase, String telefono, boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.cargo = cargo;
        this.salarioBase = salarioBase;
        this.telefono = telefono;
        this.activo = activo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public double getSalarioBase() {
        return salarioBase;
    }

    public void setSalarioBase(double salarioBase) {
        this.salarioBase = salarioBase;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getIdentidad() { return identidad; }
    public void setIdentidad(String identidad) { this.identidad = identidad; }
    public java.time.LocalDate getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(java.time.LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; }
    public int getVacacionesGozadas() { return vacacionesGozadas; }
    public void setVacacionesGozadas(int vacacionesGozadas) { this.vacacionesGozadas = vacacionesGozadas; }

    public int getVacacionesDerecho() {
        return com.glasscore.util.DiasHabiles.vacacionesDerecho(fechaIngreso, java.time.LocalDate.now());
    }

    public int getVacacionesPendientes() {
        return Math.max(0, getVacacionesDerecho() - vacacionesGozadas);
    }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    @Override
    public String toString() {
        return getNombreCompleto() + " (" + cargo + ")";
    }
}
