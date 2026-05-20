package cervecera.vista;

import cervecera.dao.InsumoDAO;
import cervecera.dao.MovimientoInsumoDAO;
import cervecera.fabrica.FabricaMovimientoInsumo;
import cervecera.modelo.HistorialMovimientoInsumo;
import cervecera.modelo.Insumo;
import cervecera.modelo.MovimientoInsumo;

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

public class PanelMovimientosInsumos extends JPanel {

    private JComboBox<Insumo> cbInsumo;
    private JComboBox<String> cbTipoMovimiento;
    private JTextField txtCantidad;
    private JTextArea txtObservaciones;

    private JButton btnRegistrar;
    private JButton btnRecargar;

    private JTable tablaHistorial;
    private DefaultTableModel modeloTabla;

    private InsumoDAO insumoDAO;
    private MovimientoInsumoDAO movimientoInsumoDAO;
    private FabricaMovimientoInsumo fabricaMovimientoInsumo;

    public PanelMovimientosInsumos() {
        insumoDAO = new InsumoDAO();
        movimientoInsumoDAO = new MovimientoInsumoDAO();
        fabricaMovimientoInsumo = new FabricaMovimientoInsumo();

        configurarPanel();
        inicializarComponentes();
        cargarInsumos();
        cargarHistorial();
    }

    private void configurarPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    private void inicializarComponentes() {
        JPanel panelFormulario = new JPanel(new GridLayout(5, 2, 8, 8));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Movimiento de insumo"));

        cbInsumo = new JComboBox<>();
        cbTipoMovimiento = new JComboBox<>(new String[]{"Entrada", "Salida"});
        txtCantidad = new JTextField();
        txtObservaciones = new JTextArea(3, 20);

        btnRegistrar = new JButton("Registrar movimiento");
        btnRecargar = new JButton("Recargar datos");

        panelFormulario.add(new JLabel("Insumo:"));
        panelFormulario.add(cbInsumo);

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
                new Object[]{"ID", "Insumo", "Tipo", "Movimiento", "Cantidad", "Unidad", "Fecha", "Observaciones"},
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

    private void cargarInsumos() {
        try {
            cbInsumo.removeAllItems();

            List<Insumo> insumos = insumoDAO.listar();

            for (Insumo insumo : insumos) {
                cbInsumo.addItem(insumo);
            }

        } catch (SQLException ex) {
            mostrarError("Error al cargar insumos", ex);
        }
    }

    private void cargarHistorial() {
        try {
            modeloTabla.setRowCount(0);

            List<HistorialMovimientoInsumo> movimientos = movimientoInsumoDAO.listarHistorial();

            for (HistorialMovimientoInsumo movimiento : movimientos) {
                modeloTabla.addRow(new Object[]{
                        movimiento.getId(),
                        movimiento.getInsumo(),
                        movimiento.getTipo(),
                        movimiento.getTipoMovimiento(),
                        movimiento.getCantidad(),
                        movimiento.getUnidadMedida(),
                        movimiento.getFechaMovimiento(),
                        movimiento.getObservaciones()
                });
            }

        } catch (SQLException ex) {
            mostrarError("Error al cargar historial de insumos", ex);
        }
    }

    private void registrarMovimiento() {
        try {
            Insumo insumo = (Insumo) cbInsumo.getSelectedItem();

            if (insumo == null) {
                JOptionPane.showMessageDialog(this, "Debes seleccionar un insumo.");
                return;
            }

            String tipoMovimiento = obtenerTipoMovimientoSeleccionado();
            BigDecimal cantidad = convertirCantidad(txtCantidad.getText());
            String observaciones = txtObservaciones.getText();

            MovimientoInsumo movimiento = fabricaMovimientoInsumo.crearMovimiento(
                    tipoMovimiento,
                    insumo,
                    cantidad,
                    observaciones
            );

            movimientoInsumoDAO.registrar(movimiento);

            JOptionPane.showMessageDialog(this, "Movimiento de insumo registrado correctamente.");

            limpiarFormulario();
            cargarInsumos();
            cargarHistorial();

        } catch (SQLException ex) {
            mostrarError("Error al registrar movimiento de insumo", ex);

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
        cargarInsumos();
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