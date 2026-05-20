package cervecera.dao;

import cervecera.conexion.ConexionBD;
import cervecera.modelo.HistorialMovimientoInsumo;
import cervecera.modelo.MovimientoInsumo;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovimientoInsumoDAO {

    public void registrar(MovimientoInsumo movimiento) throws SQLException {
        Connection conexion = null;

        try {
            conexion = ConexionBD.obtenerInstancia().obtenerConexion();
            conexion.setAutoCommit(false);

            if (movimiento.obtenerTipoMovimiento().equals("S")) {
                validarCantidadDisponible(
                        conexion,
                        movimiento.getInsumo().getId(),
                        movimiento.getCantidad()
                );
            }

            insertarMovimiento(conexion, movimiento);
            actualizarCantidadDisponible(conexion, movimiento);

            conexion.commit();

        } catch (SQLException | RuntimeException ex) {
            if (conexion != null) {
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