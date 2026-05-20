package cervecera.vista;

import cervecera.dao.MovimientoProductoDAO;
import cervecera.dao.ProductoDAO;
import cervecera.fabrica.FabricaMovimientoProducto;
import cervecera.modelo.HistorialMovimientoProducto;
import cervecera.modelo.MovimientoProducto;
import cervecera.modelo.Producto;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class PanelMovimientosProductos extends JPanel {

    private JComboBox<Producto> cbProducto;
    private JComboBox<String> cbTipoMovimiento;
    private JTextField txtCantidad;
    private JTextArea txtObservaciones;

    private JButton btnRegistrar;
    private JButton btnRecargar;

    private JTable tablaHistorial;
    private DefaultTableModel modeloTabla;

    private ProductoDAO productoDAO;
    private MovimientoProductoDAO movimientoProductoDAO;
    private FabricaMovimientoProducto fabricaMovimientoProducto;

    public PanelMovimientosProductos() {
        productoDAO = new ProductoDAO();
        movimientoProductoDAO = new MovimientoProductoDAO();
        fabricaMovimientoProducto = new FabricaMovimientoProducto();

        configurarPanel();
        inicializarComponentes();
        cargarProductos();
        cargarHistorial();
    }

    private void configurarPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    private void inicializarComponentes() {
        JPanel panelFormulario = new JPanel(new GridLayout(5, 2, 8, 8));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Movimiento de producto"));

        cbProducto = new JComboBox<>();
        cbTipoMovimiento = new JComboBox<>(new String[]{"Entrada", "Salida"});
        txtCantidad = new JTextField();
        txtObservaciones = new JTextArea(3, 20);

        btnRegistrar = new JButton("Registrar movimiento");
        btnRecargar = new JButton("Recargar datos");

        panelFormulario.add(new JLabel("Producto:"));
        panelFormulario.add(cbProducto);

        panelFormulario.add(new JLabel("Tipo de movimiento:"));
        panelFormulario.add(cbTipoMovimiento);

        panelFormulario.add(new JLabel("Cantidad:"));
        panelFormulario.add(txtCantidad);

        panelFormulario.add(new JLabel("Observaciones:"));
        panelFormulario.add(new JScrollPane(txtObservaciones));

        JPanel panelBotones = new JPanel();
        panelBotones.add(btnRegistrar);
        panelBotones.add(btnRecargar);

        panelFormulario.add(new JLabel(""));
        panelFormulario.add(panelBotones);

        modeloTabla = new DefaultTableModel(
                new Object[]{"ID", "Producto", "Estilo", "Tipo", "Cantidad", "Fecha", "Observaciones"},
                0
        ) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tablaHistorial = new JTable(modeloTabla);
        JScrollPane scrollTabla = new JScrollPane(tablaHistorial);

        add(panelFormulario, BorderLayout.NORTH);
        add(scrollTabla, BorderLayout.CENTER);

        btnRegistrar.addActionListener(e -> registrarMovimiento());
        btnRecargar.addActionListener(e -> recargarDatos());
    }

    private void cargarProductos() {
        try {
            cbProducto.removeAllItems();

            List<Producto> productos = productoDAO.listarActivos();

            for (Producto producto : productos) {
                cbProducto.addItem(producto);
            }

        } catch (SQLException ex) {
            mostrarError("Error al cargar productos", ex);
        }
    }

    private void cargarHistorial() {
        try {
            modeloTabla.setRowCount(0);

            List<HistorialMovimientoProducto> movimientos = movimientoProductoDAO.listarHistorial();

            for (HistorialMovimientoProducto movimiento : movimientos) {
                modeloTabla.addRow(new Object[]{
                        movimiento.getId(),
                        movimiento.getProducto(),
                        movimiento.getEstilo(),
                        movimiento.getTipoMovimiento(),
                        movimiento.getCantidad(),
                        movimiento.getFechaMovimiento(),
                        movimiento.getObservaciones()
                });
            }

        } catch (SQLException ex) {
            mostrarError("Error al cargar historial de productos", ex);
        }
    }

    private void registrarMovimiento() {
        try {
            Producto producto = (Producto) cbProducto.getSelectedItem();

            if (producto == null) {
                JOptionPane.showMessageDialog(this, "Debes seleccionar un producto.");
                return;
            }

            String tipoMovimiento = obtenerTipoMovimientoSeleccionado();
            BigDecimal cantidad = convertirCantidad(txtCantidad.getText());
            String observaciones = txtObservaciones.getText();

            MovimientoProducto movimiento = fabricaMovimientoProducto.crearMovimiento(
                    tipoMovimiento,
                    producto,
                    cantidad,
                    observaciones
            );

            movimientoProductoDAO.registrar(movimiento);

            JOptionPane.showMessageDialog(this, "Movimiento de producto registrado correctamente.");

            limpiarFormulario();
            cargarHistorial();

        } catch (SQLException ex) {
            mostrarError("Error al registrar movimiento de producto", ex);

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private String obtenerTipoMovimientoSeleccionado() {
        String opcion = cbTipoMovimiento.getSelectedItem().toString();

        if (opcion.equals("Entrada")) {
            return "E";
        }

        return "S";
    }

    private BigDecimal convertirCantidad(String textoCantidad) {
        if (textoCantidad == null || textoCantidad.trim().isEmpty()) {
            throw new IllegalArgumentException("La cantidad es obligatoria.");
        }

        try {
            return new BigDecimal(textoCantidad.trim());

        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("La cantidad debe ser numérica.");
        }
    }

    private void limpiarFormulario() {
        txtCantidad.setText("");
        txtObservaciones.setText("");
        txtCantidad.requestFocus();
    }

    private void recargarDatos() {
        cargarProductos();
        cargarHistorial();
    }

    private void mostrarError(String mensaje, Exception ex) {
        JOptionPane.showMessageDialog(
                this,
                mensaje + ":\n" + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}