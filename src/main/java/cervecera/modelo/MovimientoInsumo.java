package cervecera.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public abstract class MovimientoInsumo {

    private int id;
    private Insumo insumo;
    private BigDecimal cantidad;
    private LocalDateTime fechaMovimiento;
    private String observaciones;

    public MovimientoInsumo(Insumo insumo, BigDecimal cantidad, String observaciones) {
        setInsumo(insumo);
        setCantidad(cantidad);

        this.observaciones = observaciones;
        this.fechaMovimiento = LocalDateTime.now();
    }

    public abstract void aplicarMovimiento();

    public abstract String obtenerTipoMovimiento();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("El id no puede ser negativo.");
        }

        this.id = id;
    }

    public Insumo getInsumo() {
        return insumo;
    }

    public void setInsumo(Insumo insumo) {
        if (insumo == null) {
            throw new IllegalArgumentException("El insumo no puede ser nulo.");
        }

        this.insumo = insumo;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        if (cantidad == null) {
            throw new IllegalArgumentException("La cantidad no puede ser nula.");
        }

        if (cantidad.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }

        this.cantidad = cantidad;
    }

    public LocalDateTime getFechaMovimiento() {
        return fechaMovimiento;
    }

    public String getObservaciones() {
        return observaciones;
    }
}