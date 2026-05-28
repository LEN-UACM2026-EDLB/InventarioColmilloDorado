package cervecera.fabrica;

import cervecera.modelo.EntradaProducto;
import cervecera.modelo.MovimientoProducto;
import cervecera.modelo.Producto;
import cervecera.modelo.SalidaProducto;

import java.math.BigDecimal;

/**
 * Fábrica responsable de crear movimientos de productos terminados.
 *
 * Centraliza la creación de entradas y salidas para mantener el código de las
 * pantallas más limpio y fácil de modificar.
 */
public class FabricaMovimientoProducto {

    /**
     * Crea una instancia concreta de movimiento de producto según el tipo recibido.
     *
     * @param tipoMovimiento E para entrada o S para salida.
     * @param producto producto afectado por el movimiento.
     * @param cantidad cantidad del movimiento.
     * @param observaciones comentario opcional del movimiento.
     * @return movimiento de producto correspondiente al tipo indicado.
     */
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