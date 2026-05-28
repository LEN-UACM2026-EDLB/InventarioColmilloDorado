package cervecera.dao;

import cervecera.conexion.ConexionBD;
import cervecera.modelo.HistorialMovimientoInsumo;
import cervecera.modelo.MovimientoInsumo;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase DAO encargada de registrar y consultar movimientos de insumos.
 *
 * Usa transacciones para garantizar que el historial del movimiento y la cantidad
 * disponible del insumo se actualicen como una sola operación lógica.
 */
public class MovimientoInsumoDAO {

    /**
     * Registra un movimiento de insumo dentro de una transacción.
     *
     * La transacción garantiza que el historial y la cantidad disponible se mantengan
     * sincronizados, incluso si ocurre un error durante el proceso.
     *
     * @param movimiento movimiento que se desea guardar.
     * @throws SQLException si ocurre un error al consultar, insertar o actualizar datos.
     */
    public void registrar(MovimientoInsumo movimiento) throws SQLException {
        Connection conexion = null;

        try {
            conexion = ConexionBD.obtenerInstancia().obtenerConexion();
            // Se desactiva el autocommit para controlar manualmente la transacción.
            conexion.setAutoCommit(false);

            // Las salidas requieren validación previa para evitar inventario negativo.
            if (movimiento.obtenerTipoMovimiento().equals("S")) {
                validarCantidadDisponible(
                        conexion,
                        movimiento.getInsumo().getId(),
                        movimiento.getCantidad()
                );
            }

            insertarMovimiento(conexion, movimiento);
            actualizarCantidadDisponible(conexion, movimiento);

            // Si todas las operaciones terminan correctamente, se confirma la transacción.
            conexion.commit();

        } catch (SQLException | RuntimeException ex) {
            if (conexion != null) {
                // Ante cualquier error, se revierten los cambios para no dejar datos inconsistentes.
                conexion.rollback();
            }

            throw ex;

        } finally {
            if (conexion != null) {
                conexion.setAutoCommit(true);
                conexion.close();
            }
        }
    }

    /**
     * Consulta el historial de movimientos de insumos.
     *
     * @return lista de movimientos ordenados del más reciente al más antiguo.
     * @throws SQLException si ocurre un error al consultar la vista.
     */
    public List<HistorialMovimientoInsumo> listarHistorial() throws SQLException {
        List<HistorialMovimientoInsumo> movimientos = new ArrayList<>();

        String sql = """
                SELECT
                    id,
                    insumo,
                    tipo,
                    tipo_movimiento,
                    cantidad,
                    unidad_medida,
                    fecha_movimiento,
                    observaciones
                FROM vwHistorialMovimientosInsumos
                ORDER BY fecha_movimiento DESC
                """;

        try (
                // try-with-resources cierra automáticamente la conexión y evita fugas de recursos.
                Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
                PreparedStatement comando = conexion.prepareStatement(sql);
                ResultSet resultado = comando.executeQuery()
        ) {
            while (resultado.next()) {
                Timestamp fecha = resultado.getTimestamp("fecha_movimiento");

                movimientos.add(new HistorialMovimientoInsumo(
                        resultado.getInt("id"),
                        resultado.getString("insumo"),
                        resultado.getString("tipo"),
                        resultado.getString("tipo_movimiento"),
                        resultado.getBigDecimal("cantidad"),
                        resultado.getString("unidad_medida"),
                        fecha.toLocalDateTime(),
                        resultado.getString("observaciones")
                ));
            }
        }

        return movimientos;
    }

    /**
     * Inserta el registro histórico del movimiento usando la conexión transaccional.
     */
    private void insertarMovimiento(Connection conexion, MovimientoInsumo movimiento) throws SQLException {
        String sql = """
                INSERT INTO MovimientosInsumos
                    (insumo_id, tipo_movimiento, cantidad, observaciones)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement comando = conexion.prepareStatement(sql)) {
            comando.setInt(1, movimiento.getInsumo().getId());
            comando.setString(2, movimiento.obtenerTipoMovimiento());
            comando.setBigDecimal(3, movimiento.getCantidad());
            comando.setString(4, movimiento.getObservaciones());

            comando.executeUpdate();
        }
    }

    /**
     * Actualiza la cantidad disponible del insumo según sea entrada o salida.
     */
    private void actualizarCantidadDisponible(Connection conexion, MovimientoInsumo movimiento) throws SQLException {
        String sql;

        if (movimiento.obtenerTipoMovimiento().equals("E")) {
            sql = """
                    UPDATE Insumos
                    SET cantidad_disponible = cantidad_disponible + ?
                    WHERE id = ?
                    """;
        } else {
            sql = """
                    UPDATE Insumos
                    SET cantidad_disponible = cantidad_disponible - ?
                    WHERE id = ?
                    """;
        }

        try (PreparedStatement comando = conexion.prepareStatement(sql)) {
            comando.setBigDecimal(1, movimiento.getCantidad());
            comando.setInt(2, movimiento.getInsumo().getId());

            comando.executeUpdate();
        }
    }

    /**
     * Verifica que el insumo tenga cantidad suficiente antes de registrar una salida.
     */
    private void validarCantidadDisponible(
            Connection conexion,
            int insumoId,
            BigDecimal cantidadSalida
    ) throws SQLException {
        String sql = """
                SELECT cantidad_disponible
                FROM Insumos
                WHERE id = ?
                """;

        try (PreparedStatement comando = conexion.prepareStatement(sql)) {
            comando.setInt(1, insumoId);

            try (ResultSet resultado = comando.executeQuery()) {
                if (resultado.next()) {
                    BigDecimal cantidadDisponible = resultado.getBigDecimal("cantidad_disponible");

                    if (cantidadSalida.compareTo(cantidadDisponible) > 0) {
                        throw new IllegalArgumentException(
                                "No hay suficiente cantidad disponible del insumo. Disponible: " + cantidadDisponible
                        );
                    }

                    return;
                }
            }
        }

        throw new IllegalArgumentException("No se encontró el insumo indicado.");
    }
}