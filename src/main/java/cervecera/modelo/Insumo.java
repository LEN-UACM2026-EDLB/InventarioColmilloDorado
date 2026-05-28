package cervecera.modelo;

import java.math.BigDecimal;

/**
 * Modelo que representa un insumo utilizado por la cervecera.
 *
 * Incluye reglas de negocio básicas para impedir cantidades negativas o salidas
 * mayores a la cantidad disponible.
 */
public class Insumo {

    private int id;
    private String nombre;
    private String tipo;
    private BigDecimal cantidadDisponible;
    private String unidadMedida;

    public Insumo() {
        this.cantidadDisponible = BigDecimal.ZERO;
    }

    public Insumo(
            int id,
            String nombre,
            String tipo,
            BigDecimal cantidadDisponible,
            String unidadMedida
    ) {
        setId(id);
        setNombre(nombre);
        setTipo(tipo);
        setCantidadDisponible(cantidadDisponible);
        setUnidadMedida(unidadMedida);
    }

    /**
     * Incrementa la cantidad disponible del insumo.
     *
     * @param cantidad cantidad que se agregará al inventario.
     */
    public void aumentarCantidad(BigDecimal cantidad) {
        validarCantidadMovimiento(cantidad);
        this.cantidadDisponible = this.cantidadDisponible.add(cantidad);
    }

    /**
     * Disminuye la cantidad disponible del insumo si hay existencia suficiente.
     *
     * @param cantidad cantidad que se descontará del inventario.
     */
    public void disminuirCantidad(BigDecimal cantidad) {
        validarCantidadMovimiento(cantidad);

        if (cantidad.compareTo(this.cantidadDisponible) > 0) {
            throw new IllegalArgumentException("No hay suficiente cantidad disponible del insumo.");
        }

        this.cantidadDisponible = this.cantidadDisponible.subtract(cantidad);
    }

    /**
     * Valida que una cantidad de movimiento sea positiva y no nula.
     */
    private void validarCantidadMovimiento(BigDecimal cantidad) {
        if (cantidad == null) {
            throw new IllegalArgumentException("La cantidad no puede ser nula.");
        }

        if (cantidad.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }
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
            throw new IllegalArgumentException("El nombre del insumo es obligatorio.");
        }

        this.nombre = nombre.trim();
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        if (tipo == null || tipo.trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo del insumo es obligatorio.");
        }

        this.tipo = tipo.trim();
    }

    public BigDecimal getCantidadDisponible() {
        return cantidadDisponible;
    }

    public void setCantidadDisponible(BigDecimal cantidadDisponible) {
        if (cantidadDisponible == null) {
            throw new IllegalArgumentException("La cantidad disponible no puede ser nula.");
        }

        if (cantidadDisponible.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("La cantidad disponible no puede ser negativa.");
        }

        this.cantidadDisponible = cantidadDisponible;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        if (unidadMedida == null || unidadMedida.trim().isEmpty()) {
            throw new IllegalArgumentException("La unidad de medida es obligatoria.");
        }

        this.unidadMedida = unidadMedida.trim();
    }

    @Override
    public String toString() {
        return nombre + " (" + unidadMedida + ")";
    }
}