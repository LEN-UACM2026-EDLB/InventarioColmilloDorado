package cervecera.modelo;

import java.math.BigDecimal;

public class SalidaInsumo extends MovimientoInsumo {

    public SalidaInsumo(Insumo insumo, BigDecimal cantidad, String observaciones) {
        super(insumo, cantidad, observaciones);
    }

    @Override
    public void aplicarMovimiento() {
        getInsumo().disminuirCantidad(getCantidad());
    }

    @Override
    public String obtenerTipoMovimiento() {
        return "S";
    }
}