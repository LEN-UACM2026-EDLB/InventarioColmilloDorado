package cervecera.modelo;

import java.math.BigDecimal;

/**
 * Representa un movimiento de salida de insumo.
 *
 * Al aplicarse, descuenta la cantidad utilizada, vendida o retirada del inventario
 * de insumos.
 */
public class SalidaInsumo extends MovimientoInsumo {

    public SalidaInsumo(Insumo insumo, BigDecimal cantidad, String observaciones) {
        super(insumo, cantidad, observaciones);
    }

    @Override
    /**
     * Aplica el efecto del movimiento sobre el objeto relacionado.
     */
    public void aplicarMovimiento() {
        getInsumo().disminuirCantidad(getCantidad());
    }

    @Override
    /**
     * Devuelve la clave usada en la base de datos para identificar el movimiento.
     *
     * @return E para entrada o S para salida.
     */
    public String obtenerTipoMovimiento() {
        return "S";
    }
}