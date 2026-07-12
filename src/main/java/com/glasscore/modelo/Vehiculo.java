package com.glasscore.modelo;

public class Vehiculo {

    private int id;
    private String placa;
    private String marca;
    private int kmActual;
    private int kmLimiteMantenimiento;
    private Integer choferId;
    private String choferNombre;

    public Vehiculo() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public int getKmActual() {
        return kmActual;
    }

    public void setKmActual(int kmActual) {
        this.kmActual = kmActual;
    }

    public int getKmLimiteMantenimiento() {
        return kmLimiteMantenimiento;
    }

    public void setKmLimiteMantenimiento(int kmLimiteMantenimiento) {
        this.kmLimiteMantenimiento = kmLimiteMantenimiento;
    }

    public Integer getChoferId() {
        return choferId;
    }

    public void setChoferId(Integer choferId) {
        this.choferId = choferId;
    }

    public String getChoferNombre() {
        return choferNombre;
    }

    public void setChoferNombre(String choferNombre) {
        this.choferNombre = choferNombre;
    }

    @Override
    public String toString() {
        return placa + " - " + marca;
    }
}
