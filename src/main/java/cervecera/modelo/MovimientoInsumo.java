package cervecera.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Clase abstracta base para los movimientos de insumos.
 *
 * Define los datos comunes de una entrada o salida y obliga a las clases hijas a
 * implementar la forma en que el movimiento afecta al inventario.
 */
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

    /**
     * Aplica el efecto concreto del movimiento sobre el inventario.
     */
    public abstract void aplicarMovimiento();

    /**
     * Obtiene la clave que representa el tipo de movimiento.
     *
     * @return E para entrada o S para salida.
     */
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