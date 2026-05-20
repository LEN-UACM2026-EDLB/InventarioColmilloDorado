package cervecera.fabrica;

import cervecera.modelo.EntradaInsumo;
import cervecera.modelo.Insumo;
import cervecera.modelo.MovimientoInsumo;
import cervecera.modelo.SalidaInsumo;

import java.math.BigDecimal;

public class FabricaMovimientoInsumo {

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