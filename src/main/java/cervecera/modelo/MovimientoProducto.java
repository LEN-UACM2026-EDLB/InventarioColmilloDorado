package cervecera.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    public abstract String obtenerTipoMovimiento();

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