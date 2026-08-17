package com.glasscore.modelo;

public class Proveedor {
    private int id;
    private String nombre;
    private String rtn;
    private String direccion;
    private boolean activo = true;
    private String telefonosResumen;
    private String contactosResumen;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getRtn() { return rtn; }
    public void setRtn(String rtn) { this.rtn = rtn; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public String getTelefonosResumen() { return telefonosResumen; }
    public void setTelefonosResumen(String telefonosResumen) { this.telefonosResumen = telefonosResumen; }
    public String getContactosResumen() { return contactosResumen; }
    public void setContactosResumen(String contactosResumen) { this.contactosResumen = contactosResumen; }
}
