package cervecera.modelo;

import java.math.BigDecimal;

/**
 * Representa un movimiento de entrada de producto terminado.
 *
 * Se utiliza para registrar producción o ingreso de unidades al inventario.
 */
public class EntradaProducto extends MovimientoProducto {

    public EntradaProducto(Producto producto, BigDecimal cantidad, String observaciones) {
        super(producto, cantidad, observaciones);
    }

    @Override
    /**
     * Devuelve la clave usada en la base de datos para identificar el movimiento.
     *
     * @return E para entrada o S para salida.
     */
    public String obtenerTipoMovimiento() {
        return "E";
    }

    @Override
    /**
     * Devuelve la cantidad con signo para cálculos de existencia.
     *
     * @return cantidad positiva para entradas y negativa para salidas.
     */
    public BigDecimal obtenerCantidadConSigno() {
        return getCantidad();
    }
}