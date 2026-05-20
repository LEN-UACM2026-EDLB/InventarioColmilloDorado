package cervecera.dao;

import cervecera.conexion.ConexionBD;
import cervecera.modelo.HistorialMovimientoProducto;
import cervecera.modelo.MovimientoProducto;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovimientoProductoDAO {

    public void registrar(MovimientoProducto movimiento) throws SQLException {
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

    private void validarExistenciaSuficiente(int productoId, BigDecimal cantidadSalida) throws SQLException {
        BigDecimal existenciaActual = obtenerExistenciaProducto(productoId);

        if (cantidadSalida.compareTo(existenciaActual) > 0) {
            throw new IllegalArgumentException(
                    "No hay suficiente existencia del producto. Existencia actual: " + existenciaActual
            );
        }
    }

    private BigDecimal obtenerExistenciaProducto(int productoId) throws SQLException {
        String sql = """
                SELECT existencia
                FROM vwExistenciasProductos
                WHERE id = ?
                """;

        try (
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