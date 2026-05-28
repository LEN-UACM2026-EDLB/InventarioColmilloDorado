package cervecera.modelo;

import java.math.BigDecimal;

/**
 * Modelo que representa un producto terminado vendido por la cervecera.
 *
 * El producto no almacena existencia directamente; su inventario se calcula a
 * partir de los movimientos registrados en la base de datos.
 */
public class Producto {

    private int id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Estilo estilo;
    private boolean activo;

    public Producto() {
        this.precio = BigDecimal.ZERO;
        this.activo = true;
    }

    public Producto(
            int id,
            String nombre,
            String descripcion,
            BigDecimal precio,
            Estilo estilo,
            boolean activo
    ) {
        setId(id);
        setNombre(nombre);
        setDescripcion(descripcion);
        setPrecio(precio);
        setEstilo(estilo);
        setActivo(activo);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("El id no puede ser negativo.");
        }

        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio.");
        }

        this.nombre = nombre.trim();
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        if (precio == null) {
            throw new IllegalArgumentException("El precio no puede ser nulo.");
        }

        if (precio.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo.");
        }

        this.precio = precio;
    }

    public Estilo getEstilo() {
        return estilo;
    }

    public void setEstilo(Estilo estilo) {
        if (estilo == null) {
            throw new IllegalArgumentException("El estilo del producto es obligatorio.");
        }

        this.estilo = estilo;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return nombre;
    }
}