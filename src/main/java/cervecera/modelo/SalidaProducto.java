package cervecera.modelo;

import java.math.BigDecimal;

public class SalidaProducto extends MovimientoProducto {

    public SalidaProducto(Producto producto, BigDecimal cantidad, String observaciones) {
        super(producto, cantidad, observaciones);
    }

    @Override
    public String obtenerTipoMovimiento() {
        return "S";
    }

    @Override
    public BigDecimal obtenerCantidadConSigno() {
        return getCantidad().negate();
    }
}