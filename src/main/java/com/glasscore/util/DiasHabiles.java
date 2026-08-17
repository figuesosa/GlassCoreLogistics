package com.glasscore.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

public final class DiasHabiles {

    private DiasHabiles() {
    }

    public static LocalDateTime vencerDesde(LocalDateTime inicio, int diasHabiles) {
        LocalDate d = inicio.toLocalDate();
        int leftover = diasHabiles;
        while (leftover > 0) {
            d = d.plusDays(1);
            if (d.getDayOfWeek() != DayOfWeek.SATURDAY && d.getDayOfWeek() != DayOfWeek.SUNDAY) {
                leftover--;
            }
        }
        return d.atTime(inicio.toLocalTime());
    }

    public static int diasLaboradosEnAnio(LocalDate ingreso, LocalDate hasta) {
        LocalDate inicioAnio = LocalDate.of(hasta.getYear(), 1, 1);
        LocalDate desde = ingreso != null && ingreso.isAfter(inicioAnio) ? ingreso : inicioAnio;
        if (desde.isAfter(hasta)) {
            return 0;
        }
        return (int) (hasta.toEpochDay() - desde.toEpochDay() + 1);
    }

    public static int vacacionesDerecho(LocalDate ingreso, LocalDate hoy) {
        if (ingreso == null) {
            return 0;
        }
        int anios = ingreso.until(hoy).getYears();
        if (anios < 1) {
            return 0;
        }
        return Math.min(20, 10 + (anios - 1));
    }
}
