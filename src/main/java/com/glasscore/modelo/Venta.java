package com.glasscore.modelo;

import java.time.LocalDateTime;

public class Venta {
    private int id;
    private int cotizacionId;
    private String cliente;
    private String numeroFactura;
    private String cai;
    private double subtotal;
    private double isv;
    private double total;
    private double retencion;
    private LocalDateTime fecha;
    private String tipoEstructura;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getCotizacionId() { return cotizacionId; }
    public void setCotizacionId(int cotizacionId) { this.cotizacionId = cotizacionId; }
    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }
    public String getNumeroFactura() { return numeroFactura; }
    public void setNumeroFactura(String numeroFactura) { this.numeroFactura = numeroFactura; }
    public String getCai() { return cai; }
    public void setCai(String cai) { this.cai = cai; }
    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
    public double getIsv() { return isv; }
    public void setIsv(double isv) { this.isv = isv; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public double getRetencion() { return retencion; }
    public void setRetencion(double retencion) { this.retencion = retencion; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public String getTipoEstructura() { return tipoEstructura; }
    public void setTipoEstructura(String tipoEstructura) { this.tipoEstructura = tipoEstructura; }
}
