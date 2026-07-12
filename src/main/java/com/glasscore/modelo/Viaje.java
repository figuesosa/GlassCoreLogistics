package com.glasscore.modelo;

import java.time.LocalDateTime;

public class Viaje {

    private int id;
    private int vehiculoId;
    private String placa;
    private int choferId;
    private String choferNombre;
    private String ruta;
    private boolean esRedondo;
    private int kilometros;
    private double factorRendimiento;
    private double precioCombustible;
    private double litrosEstimados;
    private double gastoCombustible;
    private LocalDateTime fechaSalida;
    private String herramientasCustodia;

    public Viaje() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVehiculoId() {
        return vehiculoId;
    }

    public void setVehiculoId(int vehiculoId) {
        this.vehiculoId = vehiculoId;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public int getChoferId() {
        return choferId;
    }

    public void setChoferId(int choferId) {
        this.choferId = choferId;
    }

    public String getChoferNombre() {
        return choferNombre;
    }

    public void setChoferNombre(String choferNombre) {
        this.choferNombre = choferNombre;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public boolean isEsRedondo() {
        return esRedondo;
    }

    public void setEsRedondo(boolean esRedondo) {
        this.esRedondo = esRedondo;
    }

    public int getKilometros() {
        return kilometros;
    }

    public void setKilometros(int kilometros) {
        this.kilometros = kilometros;
    }

    public double getFactorRendimiento() {
        return factorRendimiento;
    }

    public void setFactorRendimiento(double factorRendimiento) {
        this.factorRendimiento = factorRendimiento;
    }

    public double getPrecioCombustible() {
        return precioCombustible;
    }

    public void setPrecioCombustible(double precioCombustible) {
        this.precioCombustible = precioCombustible;
    }

    public double getLitrosEstimados() {
        return litrosEstimados;
    }

    public void setLitrosEstimados(double litrosEstimados) {
        this.litrosEstimados = litrosEstimados;
    }

    public double getGastoCombustible() {
        return gastoCombustible;
    }

    public void setGastoCombustible(double gastoCombustible) {
        this.gastoCombustible = gastoCombustible;
    }

    public LocalDateTime getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(LocalDateTime fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public String getHerramientasCustodia() {
        return herramientasCustodia;
    }

    public void setHerramientasCustodia(String herramientasCustodia) {
        this.herramientasCustodia = herramientasCustodia;
    }
}
