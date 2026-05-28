package cervecera.dao;

import cervecera.conexion.ConexionBD;
import cervecera.modelo.HistorialMovimientoProducto;
import cervecera.modelo.MovimientoProducto;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase DAO encargada de registrar y consultar movimientos de productos terminados.
 *
 * Antes de registrar una salida, valida que exista inventario suficiente según la
 * vista de existencias calculada en la base de datos.
 */
public class MovimientoProductoDAO {

    /**
     * Registra un movimiento de producto terminado.
     *
     * Si el movimiento es una salida, valida previamente que exista inventario
     * suficiente para no permitir existencias negativas.
     *
     * @param movimiento movimiento que se desea guardar.
     * @throws SQLException si ocurre un error al consultar o insertar datos.
     */
    public void registrar(MovimientoProducto movimiento) throws SQLException {
        // Las salidas requieren validación previa para evitar inventario negativo.
        if (movimiento.obtenerTipoMovimiento().equals("S")) {
            validarExistenciaSuficiente(
                    movimiento.getProducto().getId(),
                    movimiento.getCantidad()
            );
        }

        String sql = """
                INSERT INTO MovimientosProductos
                    (producto_id, tipo_movimiento, cantidad, observaciones)
                VALUES (?, ?, ?, ?)
                """;

        try (
                // try-with-resources cierra automáticamente la conexión y evita fugas de recursos.
                Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
                PreparedStatement comando = conexion.prepareStatement(sql)
        ) {
            comando.setInt(1, movimiento.getProducto().getId());
            comando.setString(2, movimiento.obtenerTipoMovimiento());
            comando.setBigDecimal(3, movimiento.getCantidad());
            comando.setString(4, movimiento.getObservaciones());

            comando.executeUpdate();
        }
    }

    /**
     * Consulta el historial de movimientos de productos.
     *
     * @return lista de movimientos ordenados del más reciente al más antiguo.
     * @throws SQLException si ocurre un error al consultar la vista.
     */
    public List<HistorialMovimientoProducto> listarHistorial() throws SQLException {
        List<HistorialMovimientoProducto> movimientos = new ArrayList<>();

        String sql = """
                SELECT
                    id,
                    producto,
                    estilo,
                    tipo_movimiento,
                    cantidad,
                    fecha_movimiento,
                    observaciones
                FROM vwHistorialMovimientosProductos
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

                movimientos.add(new HistorialMovimientoProducto(
                        resultado.getInt("id"),
                        resultado.getString("producto"),
                        resultado.getString("estilo"),
                        resultado.getString("tipo_movimiento"),
                        resultado.getBigDecimal("cantidad"),
                        fecha.toLocalDateTime(),
                        resultado.getString("observaciones")
                ));
            }
        }

        return movimientos;
    }

    /**
     * Verifica que el producto tenga existencia suficiente antes de registrar una salida.
     */
    private void validarExistenciaSuficiente(int productoId, BigDecimal cantidadSalida) throws SQLException {
        BigDecimal existenciaActual = obtenerExistenciaProducto(productoId);

        if (cantidadSalida.compareTo(existenciaActual) > 0) {
            throw new IllegalArgumentException(
                    "No hay suficiente existencia del producto. Existencia actual: " + existenciaActual
            );
        }
    }

    /**
     * Consulta la existencia actual de un producto desde la vista SQL.
     */
    private BigDecimal obtenerExistenciaProducto(int productoId) throws SQLException {
        String sql = """
                SELECT existencia
                FROM vwExistenciasProductos
                WHERE id = ?
                """;

        try (
                // try-with-resources cierra automáticamente la conexión y evita fugas de recursos.
                Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
                PreparedStatement comando = conexion.prepareStatement(sql)
        ) {
            comando.setInt(1, productoId);

            try (ResultSet resultado = comando.executeQuery()) {
                if (resultado.next()) {
                    return resultado.getBigDecimal("existencia");
                }
            }
        }

        return BigDecimal.ZERO;
    }
}