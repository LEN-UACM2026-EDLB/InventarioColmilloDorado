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

/**
 * Panel Swing para registrar entradas y salidas de insumos.
 *
 * Utiliza una fábrica para construir el movimiento correcto y un DAO para persistir
 * la operación en SQL Server.
 */
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

    /**
     * Consulta insumos desde la base de datos y actualiza los componentes visuales.
     */
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

    /**
     * Recarga la tabla de historial desde la vista correspondiente en SQL Server.
     */
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

    /**
     * Construye y registra un movimiento a partir de los datos capturados en pantalla.
     */
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

    /**
     * Traduce la opción visible del combo a la clave usada por la base de datos.
     *
     * @return E para entrada o S para salida.
     */
    private String obtenerTipoMovimientoSeleccionado() {
        String opcion = cbTipoMovimiento.getSelectedItem().toString();

        // La interfaz muestra texto descriptivo, pero la base de datos usa claves cortas.
        if (opcion.equals("Entrada")) {
            return "E";
        }

        return "S";
    }

    /**
     * Convierte y valida una cantidad capturada por el usuario.
     *
     * @param textoCantidad valor escrito en el campo de texto.
     * @return cantidad convertida a BigDecimal.
     */
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

    /**
     * Limpia los campos y reinicia la selección del formulario.
     */
    private void limpiarFormulario() {
        txtCantidad.setText("");
        txtObservaciones.setText("");
        txtCantidad.requestFocus();
    }

    /**
     * Actualiza los datos mostrados en combos y tablas del panel.
     */
    private void recargarDatos() {
        cargarInsumos();
        cargarHistorial();
    }

    /**
     * Muestra errores técnicos en un cuadro de diálogo para informar al usuario.
     *
     * @param mensaje descripción general del error.
     * @param ex excepción original con el detalle técnico.
     */
    private void mostrarError(String mensaje, Exception ex) {
        JOptionPane.showMessageDialog(
                this,
                mensaje + ":\n" + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}