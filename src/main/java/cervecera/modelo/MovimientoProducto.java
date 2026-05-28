package cervecera.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Clase abstracta base para los movimientos de productos terminados.
 *
 * Define los datos comunes de entrada y salida, dejando a las clases hijas la
 * definición del tipo de movimiento y el signo de la cantidad.
 */
public abstract class MovimientoProducto {

    private int id;
    private Producto producto;
    private BigDecimal cantidad;
    private LocalDateTime fechaMovimiento;
    private String observaciones;

    public MovimientoProducto(Producto producto, BigDecimal cantidad, String observaciones) {
        setProducto(producto);
        setCantidad(cantidad);

        this.observaciones = observaciones;
        this.fechaMovimiento = LocalDateTime.now();
    }

    /**
     * Obtiene la clave que representa el tipo de movimiento.
     *
     * @return E para entrada o S para salida.
     */
    public abstract String obtenerTipoMovimiento();

    /**
     * Obtiene la cantidad con signo para cálculos de inventario.
     *
     * @return cantidad positiva o negativa según el tipo de movimiento.
     */
    public abstract BigDecimal obtenerCantidadConSigno();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("El id no puede ser negativo.");
        }

        this.id = id;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo.");
        }

        this.producto = producto;
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