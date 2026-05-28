package cervecera.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Modelo de consulta para mostrar el historial de movimientos de productos.
 *
 * Agrupa datos del producto, estilo, tipo de movimiento y fecha para presentarlos
 * en la tabla de historial.
 */
public class HistorialMovimientoProducto {

    private int id;
    private String producto;
    private String estilo;
    private String tipoMovimiento;
    private BigDecimal cantidad;
    private LocalDateTime fechaMovimiento;
    private String observaciones;

    public HistorialMovimientoProducto(
            int id,
            String producto,
            String estilo,
            String tipoMovimiento,
            BigDecimal cantidad,
            LocalDateTime fechaMovimiento,
            String observaciones
    ) {
        this.id = id;
        this.producto = producto;
        this.estilo = estilo;
        this.tipoMovimiento = tipoMovimiento;
        this.cantidad = cantidad;
        this.fechaMovimiento = fechaMovimiento;
        this.observaciones = observaciones;
    }

    public int getId() {
        return id;
    }

    public String getProducto() {
        return producto;
    }

    public String getEstilo() {
        return estilo;
    }

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public LocalDateTime getFechaMovimiento() {
        return fechaMovimiento;
    }

    public String getObservaciones() {
        return observaciones;
    }
}