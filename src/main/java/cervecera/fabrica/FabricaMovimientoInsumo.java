package cervecera.fabrica;

import cervecera.modelo.EntradaInsumo;
import cervecera.modelo.Insumo;
import cervecera.modelo.MovimientoInsumo;
import cervecera.modelo.SalidaInsumo;

import java.math.BigDecimal;

/**
 * Fábrica responsable de crear movimientos de insumos.
 *
 * Aplica Factory Method para decidir si debe construirse una entrada o una salida
 * sin delegar esa decisión a la interfaz gráfica.
 */
public class FabricaMovimientoInsumo {

    /**
     * Crea una instancia concreta de movimiento de insumo según el tipo recibido.
     *
     * @param tipoMovimiento E para entrada o S para salida.
     * @param insumo insumo afectado por el movimiento.
     * @param cantidad cantidad del movimiento.
     * @param observaciones comentario opcional del movimiento.
     * @return movimiento de insumo correspondiente al tipo indicado.
     */
    public MovimientoInsumo crearMovimiento(
            String tipoMovimiento,
            Insumo insumo,
            BigDecimal cantidad,
            String observaciones
    ) {
        if (tipoMovimiento == null || tipoMovimiento.trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de movimiento es obligatorio.");
        }

        String tipo = tipoMovimiento.trim().toUpperCase();

        return switch (tipo) {
            case "E" -> new EntradaInsumo(insumo, cantidad, observaciones);
            case "S" -> new SalidaInsumo(insumo, cantidad, observaciones);
            default -> throw new IllegalArgumentException("Tipo de movimiento de insumo no válido: " + tipoMovimiento);
        };
    }
}