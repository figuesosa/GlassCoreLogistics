package com.glasscore.modelo;

import java.time.LocalDate;

public class ConfigFiscal {
    private String rtnEmpresa;
    private String cai;
    private LocalDate fechaLimiteEmision;
    private int rangoInicial;
    private int rangoFinal;
    private int correlativoActual;

    public String getRtnEmpresa() { return rtnEmpresa; }
    public void setRtnEmpresa(String rtnEmpresa) { this.rtnEmpresa = rtnEmpresa; }
    public String getCai() { return cai; }
    public void setCai(String cai) { this.cai = cai; }
    public LocalDate getFechaLimiteEmision() { return fechaLimiteEmision; }
    public void setFechaLimiteEmision(LocalDate fechaLimiteEmision) { this.fechaLimiteEmision = fechaLimiteEmision; }
    public int getRangoInicial() { return rangoInicial; }
    public void setRangoInicial(int rangoInicial) { this.rangoInicial = rangoInicial; }
    public int getRangoFinal() { return rangoFinal; }
    public void setRangoFinal(int rangoFinal) { this.rangoFinal = rangoFinal; }
    public int getCorrelativoActual() { return correlativoActual; }
    public void setCorrelativoActual(int correlativoActual) { this.correlativoActual = correlativoActual; }

    public int restantes() {
        return Math.max(0, rangoFinal - correlativoActual + 1);
    }

    public String formatoFactura(int n) {
        return String.format("000-001-01-%08d", n);
    }
}
