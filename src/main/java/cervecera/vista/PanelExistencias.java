package cervecera.vista;

import cervecera.dao.ProductoDAO;
import cervecera.modelo.ExistenciaProducto;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.sql.SQLException;
import java.util.List;

/**
 * Panel Swing para consultar existencias de productos terminados.
 *
 * Muestra datos calculados desde la vista de SQL Server, evitando duplicar el stock
 * dentro de la tabla Productos.
 */
public class PanelExistencias extends JPanel {

    private JTable tablaExistencias;
    private DefaultTableModel modeloTabla;
    private JButton btnRecargar;

    private ProductoDAO productoDAO;

    public PanelExistencias() {
        productoDAO = new ProductoDAO();

        configurarPanel();
        inicializarComponentes();
        cargarExistencias();
    }

    /**
     * Define el layout y márgenes generales del panel.
     */
    private void configurarPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    /**
     * Crea componentes visuales, los agrega al contenedor y registra eventos.
     */
    private void inicializarComponentes() {
        btnRecargar = new JButton("Recargar existencias");

        modeloTabla = new DefaultTableModel(
                new Object[]{"ID", "Producto", "Descripción", "Precio", "Estilo", "Existencia"},
                0
        ) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tablaExistencias = new JTable(modeloTabla);

        add(btnRecargar, BorderLayout.NORTH);
        add(new JScrollPane(tablaExistencias), BorderLayout.CENTER);

        btnRecargar.addActionListener(e -> cargarExistencias());
    }

    /**
     * Consulta la vista de existencias y actualiza la tabla del panel.
     */
    private void cargarExistencias() {
        try {
            modeloTabla.setRowCount(0);

            List<ExistenciaProducto> existencias = productoDAO.listarExistencias();

            for (ExistenciaProducto existencia : existencias) {
                modeloTabla.addRow(new Object[]{
                        existencia.getId(),
                        existencia.getNombre(),
                        existencia.getDescripcion(),
                        existencia.getPrecio(),
                        existencia.getEstilo(),
                        existencia.getExistencia()
                });
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error al cargar existencias:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}