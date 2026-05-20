package cervecera.modelo;

import java.math.BigDecimal;

public class EntradaInsumo extends MovimientoInsumo {

    public EntradaInsumo(Insumo insumo, BigDecimal cantidad, String observaciones) {
        super(insumo, cantidad, observaciones);
    }

    @Override
    public void aplicarMovimiento() {
        getInsumo().aumentarCantidad(getCantidad());
    }

    @Override
    public String obtenerTipoMovimiento() {
        return "E";
    }
}