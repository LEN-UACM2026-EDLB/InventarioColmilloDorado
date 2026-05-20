package cervecera.modelo;

import java.math.BigDecimal;

public class EntradaProducto extends MovimientoProducto {

    public EntradaProducto(Producto producto, BigDecimal cantidad, String observaciones) {
        super(producto, cantidad, observaciones);
    }

    @Override
    public String obtenerTipoMovimiento() {
        return "E";
    }

    @Override
    public BigDecimal obtenerCantidadConSigno() {
        return getCantidad();
    }
}