package com.glasscore.modelo;

import java.time.LocalDateTime;

public class Cotizacion {

    private int id;
    private String cliente;
    private String tipoEstructura;
    private double ancho;
    private double alto;
    private double areaVidrio;
    private double metrosAluminio;
    private double metrosMetal;
    private double subtotal;
    private String alertaCompra;
    private LocalDateTime fecha;

    public Cotizacion() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getTipoEstructura() {
        return tipoEstructura;
    }

    public void setTipoEstructura(String tipoEstructura) {
        this.tipoEstructura = tipoEstructura;
    }

    public double getAncho() {
        return ancho;
    }

    public void setAncho(double ancho) {
        this.ancho = ancho;
    }

    public double getAlto() {
        return alto;
    }

    public void setAlto(double alto) {
        this.alto = alto;
    }

    public double getAreaVidrio() {
        return areaVidrio;
    }

    public void setAreaVidrio(double areaVidrio) {
        this.areaVidrio = areaVidrio;
    }

    public double getMetrosAluminio() {
        return metrosAluminio;
    }

    public void setMetrosAluminio(double metrosAluminio) {
        this.metrosAluminio = metrosAluminio;
    }

    public double getMetrosMetal() {
        return metrosMetal;
    }

    public void setMetrosMetal(double metrosMetal) {
        this.metrosMetal = metrosMetal;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public String getAlertaCompra() {
        return alertaCompra;
    }

    public void setAlertaCompra(String alertaCompra) {
        this.alertaCompra = alertaCompra;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
