package com.glasscore.modelo;

import java.time.LocalDate;

public class Planilla {

    private int id;
    private int empleadoId;
    private String empleadoNombre;
    private double salarioBase;
    private double horasExtras;
    private double viaticos;
    private double totalNeto;
    private LocalDate fechaPago;
    private boolean aplica14vo;
    private boolean aplicaAguinaldo;
    private double monto14vo;
    private double montoAguinaldo;
    private double deducciones;

    public Planilla() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEmpleadoId() {
        return empleadoId;
    }

    public void setEmpleadoId(int empleadoId) {
        this.empleadoId = empleadoId;
    }

    public String getEmpleadoNombre() {
        return empleadoNombre;
    }

    public void setEmpleadoNombre(String empleadoNombre) {
        this.empleadoNombre = empleadoNombre;
    }

    public double getSalarioBase() {
        return salarioBase;
    }

    public void setSalarioBase(double salarioBase) {
        this.salarioBase = salarioBase;
    }

    public double getHorasExtras() {
        return horasExtras;
    }

    public void setHorasExtras(double horasExtras) {
        this.horasExtras = horasExtras;
    }

    public double getViaticos() {
        return viaticos;
    }

    public void setViaticos(double viaticos) {
        this.viaticos = viaticos;
    }

    public double getTotalNeto() {
        return totalNeto;
    }

    public void setTotalNeto(double totalNeto) {
        this.totalNeto = totalNeto;
    }

    public LocalDate getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDate fechaPago) {
        this.fechaPago = fechaPago;
    }

    public boolean isAplica14vo() { return aplica14vo; }
    public void setAplica14vo(boolean aplica14vo) { this.aplica14vo = aplica14vo; }
    public boolean isAplicaAguinaldo() { return aplicaAguinaldo; }
    public void setAplicaAguinaldo(boolean aplicaAguinaldo) { this.aplicaAguinaldo = aplicaAguinaldo; }
    public double getMonto14vo() { return monto14vo; }
    public void setMonto14vo(double monto14vo) { this.monto14vo = monto14vo; }
    public double getMontoAguinaldo() { return montoAguinaldo; }
    public void setMontoAguinaldo(double montoAguinaldo) { this.montoAguinaldo = montoAguinaldo; }
    public double getDeducciones() { return deducciones; }
    public void setDeducciones(double deducciones) { this.deducciones = deducciones; }
}
