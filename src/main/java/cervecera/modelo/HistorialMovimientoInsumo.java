package cervecera.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class HistorialMovimientoInsumo {

    private int id;
    private String insumo;
    private String tipo;
    private String tipoMovimiento;
    private BigDecimal cantidad;
    private String unidadMedida;
    private LocalDateTime fechaMovimiento;
    private String observaciones;

    public HistorialMovimientoInsumo(
            int id,
            String insumo,
            String tipo,
            String tipoMovimiento,
            BigDecimal cantidad,
            String unidadMedida,
            LocalDateTime fechaMovimiento,
            String observaciones
    ) {
        this.id = id;
        this.insumo = insumo;
        this.tipo = tipo;
        this.tipoMovimiento = tipoMovimiento;
        this.cantidad = cantidad;
        this.unidadMedida = unidadMedida;
        this.fechaMovimiento = fechaMovimiento;
        this.observaciones = observaciones;
    }

    public int getId() {
        return id;
    }

    public String getInsumo() {
        return insumo;
    }

    public String getTipo() {
        return tipo;
    }

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public LocalDateTime getFechaMovimiento() {
        return fechaMovimiento;
    }

    public String getObservaciones() {
        return observaciones;
    }
}