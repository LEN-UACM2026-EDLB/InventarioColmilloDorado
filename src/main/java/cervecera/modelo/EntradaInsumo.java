package cervecera.modelo;

import java.math.BigDecimal;

/**
 * Representa un movimiento de entrada de insumo.
 *
 * Al aplicarse, incrementa la cantidad disponible del insumo relacionado.
 */
public class EntradaInsumo extends MovimientoInsumo {

    public EntradaInsumo(Insumo insumo, BigDecimal cantidad, String observaciones) {
        super(insumo, cantidad, observaciones);
    }

    @Override
    /**
     * Aplica el efecto del movimiento sobre el objeto relacionado.
     */
    public void aplicarMovimiento() {
        getInsumo().aumentarCantidad(getCantidad());
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
}