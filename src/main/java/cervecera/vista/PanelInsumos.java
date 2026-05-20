package cervecera.vista;

import cervecera.dao.InsumoDAO;
import cervecera.modelo.Insumo;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class PanelInsumos extends JPanel {

    private JTextField txtNombre;
    private JTextField txtTipo;
    private JTextField txtCantidadDisponible;
    private JTextField txtUnidadMedida;

    private JButton btnNuevo;
    private JButton btnGuardar;
    private JButton btnActualizar;

    private JTable tablaInsumos;
    private DefaultTableModel modeloTabla;

    private InsumoDAO insumoDAO;

    private int insumoSeleccionadoId;

    public PanelInsumos() {
        insumoDAO = new InsumoDAO();
        insumoSeleccionadoId = 0;

        configurarPanel();
        inicializarComponentes();
        cargarInsumos();
    }

    private void configurarPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    private void inicializarComponentes() {
        JPanel panelFormulario = new JPanel(new GridLayout(5, 2, 8, 8));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Datos del insumo"));

        txtNombre = new JTextField();
        txtTipo = new JTextField();
        txtCantidadDisponible = new JTextField();
        txtUnidadMedida = new JTextField();

        panelFormulario.add(new JLabel("Nombre:"));
        panelFormulario.add(txtNombre);

        panelFormulario.add(new JLabel("Tipo:"));
        panelFormulario.add(txtTipo);

        panelFormulario.add(new JLabel("Cantidad disponible:"));
        panelFormulario.add(txtCantidadDisponible);

        panelFormulario.add(new JLabel("Unidad de medida:"));
        panelFormulario.add(txtUnidadMedida);

        JPanel panelBotones = new JPanel();

        btnNuevo = new JButton("Nuevo");
        btnGuardar = new JButton("Guardar");
        btnActualizar = new JButton("Actualizar");

        panelBotones.add(btnNuevo);
        panelBotones.add(btnGuardar);
        panelBotones.add(btnActualizar);

        panelFormulario.add(new JLabel(""));
        panelFormulario.add(panelBotones);

        modeloTabla = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Tipo", "Cantidad disponible", "Unidad"},
                0
        ) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tablaInsumos = new JTable(modeloTabla);
        JScrollPane scrollTabla = new JScrollPane(tablaInsumos);

        add(panelFormulario, BorderLayout.NORTH);
        add(scrollTabla, BorderLayout.CENTER);

        btnNuevo.addActionListener(e -> limpiarFormulario());
        btnGuardar.addActionListener(e -> guardarInsumo());
        btnActualizar.addActionListener(e -> actualizarInsumo());

        tablaInsumos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarInsumo();
            }
        });
    }

    private void cargarInsumos() {
        try {
            modeloTabla.setRowCount(0);

            List<Insumo> insumos = insumoDAO.listar();

            for (Insumo insumo : insumos) {
                modeloTabla.addRow(new Object[]{
                        insumo.getId(),
                        insumo.getNombre(),
                        insumo.getTipo(),
                        insumo.getCantidadDisponible(),
                        insumo.getUnidadMedida()
                });
            }

        } catch (SQLException ex) {
            mostrarError("Error al cargar insumos", ex);
        }
    }

    private void guardarInsumo() {
        try {
            Insumo insumo = construirInsumoDesdeFormulario();
            insumoDAO.insertar(insumo);

            JOptionPane.showMessageDialog(this, "Insumo guardado correctamente.");

            limpiarFormulario();
            cargarInsumos();

        } catch (SQLException ex) {
            mostrarError("Error al guardar insumo", ex);

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private void actualizarInsumo() {
        if (insumoSeleccionadoId == 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un insumo para actualizar.");
            return;
        }

        try {
            Insumo insumo = construirInsumoDesdeFormulario();
            insumo.setId(insumoSeleccionadoId);

            insumoDAO.actualizar(insumo);

            JOptionPane.showMessageDialog(this, "Insumo actualizado correctamente.");

            limpiarFormulario();
            cargarInsumos();

        } catch (SQLException ex) {
            mostrarError("Error al actualizar insumo", ex);

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private Insumo construirInsumoDesdeFormulario() {
        String nombre = txtNombre.getText();
        String tipo = txtTipo.getText();
        BigDecimal cantidadDisponible = convertirCantidad(txtCantidadDisponible.getText());
        String unidadMedida = txtUnidadMedida.getText();

        return new Insumo(
                0,
                nombre,
                tipo,
                cantidadDisponible,
                unidadMedida
        );
    }

    private BigDecimal convertirCantidad(String textoCantidad) {
        if (textoCantidad == null || textoCantidad.trim().isEmpty()) {
            throw new IllegalArgumentException("La cantidad disponible es obligatoria.");
        }

        try {
            return new BigDecimal(textoCantidad.trim());

        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("La cantidad disponible debe ser numérica.");
        }
    }

    private void seleccionarInsumo() {
        int fila = tablaInsumos.getSelectedRow();

        if (fila < 0) {
            return;
        }

        insumoSeleccionadoId = Integer.parseInt(modeloTabla.getValueAt(fila, 0).toString());

        txtNombre.setText(modeloTabla.getValueAt(fila, 1).toString());
        txtTipo.setText(modeloTabla.getValueAt(fila, 2).toString());
        txtCantidadDisponible.setText(modeloTabla.getValueAt(fila, 3).toString());
        txtUnidadMedida.setText(modeloTabla.getValueAt(fila, 4).toString());
    }

    private void limpiarFormulario() {
        insumoSeleccionadoId = 0;

        txtNombre.setText("");
        txtTipo.setText("");
        txtCantidadDisponible.setText("");
        txtUnidadMedida.setText("");

        tablaInsumos.clearSelection();
        txtNombre.requestFocus();
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