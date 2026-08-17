package com.glasscore.modelo;

public class Cliente {
    private int id;
    private String nombre;
    private String identidadRtn;
    private String telefono;
    private String email;
    private String direccion;
    private boolean activo = true;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getIdentidadRtn() { return identidadRtn; }
    public void setIdentidadRtn(String identidadRtn) { this.identidadRtn = identidadRtn; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
