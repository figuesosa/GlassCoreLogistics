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
    private double isv;
    private double total;
    private int vigenciaDias;
    private LocalDateTime fechaVencimiento;
    private String alertaCompra;
    private LocalDateTime fecha;
    private Integer clienteId;
    private String estado = "VIGENTE";
    private String numeroFactura;
    private String caiUsado;

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

    public double getIsv() {
        return isv;
    }

    public void setIsv(double isv) {
        this.isv = isv;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public int getVigenciaDias() {
        return vigenciaDias;
    }

    public void setVigenciaDias(int vigenciaDias) {
        this.vigenciaDias = vigenciaDias;
    }

    public LocalDateTime getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDateTime fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public boolean isVencida() {
        return fechaVencimiento != null && LocalDateTime.now().isAfter(fechaVencimiento);
    }

    public String getEstadoVigencia() {
        return isVencida() ? "VENCIDA" : "VIGENTE";
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

    public Integer getClienteId() { return clienteId; }
    public void setClienteId(Integer clienteId) { this.clienteId = clienteId; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getNumeroFactura() { return numeroFactura; }
    public void setNumeroFactura(String numeroFactura) { this.numeroFactura = numeroFactura; }
    public String getCaiUsado() { return caiUsado; }
    public void setCaiUsado(String caiUsado) { this.caiUsado = caiUsado; }

    public boolean isConvertida() {
        return "CONVERTIDA_A_VENTA".equals(estado);
    }

    public String getEstadoMostrado() {
        if (isConvertida()) {
            return "CONVERTIDA_A_VENTA";
        }
        return isVencida() ? "VENCIDA" : "VIGENTE";
    }
}
