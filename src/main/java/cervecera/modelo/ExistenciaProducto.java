package cervecera.modelo;

import java.math.BigDecimal;

/**
 * Modelo de solo lectura usado para mostrar existencias de productos.
 *
 * Sus datos provienen de una vista SQL, por lo que no modifica información del
 * inventario; únicamente transporta datos hacia la interfaz.
 */
public class ExistenciaProducto {

    private int id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private String estilo;
    private BigDecimal existencia;

    public ExistenciaProducto(
            int id,
            String nombre,
            String descripcion,
            BigDecimal precio,
            String estilo,
            BigDecimal existencia
    ) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.estilo = estilo;
        this.existencia = existencia;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public String getEstilo() {
        return estilo;
    }

    public BigDecimal getExistencia() {
        return existencia;
    }
}