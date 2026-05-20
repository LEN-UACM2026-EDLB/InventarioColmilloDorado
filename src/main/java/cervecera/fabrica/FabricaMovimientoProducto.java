package cervecera.fabrica;

import cervecera.modelo.EntradaProducto;
import cervecera.modelo.MovimientoProducto;
import cervecera.modelo.Producto;
import cervecera.modelo.SalidaProducto;

import java.math.BigDecimal;

public class FabricaMovimientoProducto {

    public MovimientoProducto crearMovimiento(
            String tipoMovimiento,
            Producto producto,
            BigDecimal cantidad,
            String observaciones
    ) {
        if (tipoMovimiento == null || tipoMovimiento.trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de movimiento es obligatorio.");
        }

        String tipo = tipoMovimiento.trim().toUpperCase();

        return switch (tipo) {
            case "E" -> new EntradaProducto(producto, cantidad, observaciones);
            case "S" -> new SalidaProducto(producto, cantidad, observaciones);
            default -> throw new IllegalArgumentException("Tipo de movimiento de producto no válido: " + tipoMovimiento);
        };
    }
}