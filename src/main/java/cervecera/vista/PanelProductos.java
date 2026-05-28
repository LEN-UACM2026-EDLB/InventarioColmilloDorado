package cervecera.vista;

import cervecera.dao.EstiloDAO;
import cervecera.dao.ProductoDAO;
import cervecera.modelo.Estilo;
import cervecera.modelo.Producto;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
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

/**
 * Panel Swing para administrar productos terminados.
 *
 * Coordina el formulario de captura, la tabla de consulta y la selección del estilo
 * asociado a cada producto.
 */
public class PanelProductos extends JPanel {

    private JTextField txtNombre;
    private JTextField txtDescripcion;
    private JTextField txtPrecio;
    private JComboBox<Estilo> cbEstilo;

    private JButton btnNuevo;
    private JButton btnGuardar;
    private JButton btnActualizar;
    private JButton btnDesactivar;

    private JTable tablaProductos;
    private DefaultTableModel modeloTabla;

    private ProductoDAO productoDAO;
    private EstiloDAO estiloDAO;

    private int productoSeleccionadoId;

    public PanelProductos() {
        productoDAO = new ProductoDAO();
        estiloDAO = new EstiloDAO();
        productoSeleccionadoId = 0;

        configurarPanel();
        inicializarComponentes();
        cargarEstilos();
        cargarProductos();
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
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Datos del producto"));

        txtNombre = new JTextField();
        txtDescripcion = new JTextField();
        txtPrecio = new JTextField();
        cbEstilo = new JComboBox<>();

        panelFormulario.add(new JLabel("Nombre:"));
        panelFormulario.add(txtNombre);

        panelFormulario.add(new JLabel("Descripción:"));
        panelFormulario.add(txtDescripcion);

        panelFormulario.add(new JLabel("Precio:"));
        panelFormulario.add(txtPrecio);

        panelFormulario.add(new JLabel("Estilo:"));
        panelFormulario.add(cbEstilo);

        JPanel panelBotones = new JPanel();

        btnNuevo = new JButton("Nuevo");
        btnGuardar = new JButton("Guardar");
        btnActualizar = new JButton("Actualizar");
        btnDesactivar = new JButton("Desactivar");

        panelBotones.add(btnNuevo);
        panelBotones.add(btnGuardar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnDesactivar);

        panelFormulario.add(new JLabel(""));
        panelFormulario.add(panelBotones);

        modeloTabla = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Descripción", "Precio", "Estilo"},
                0
        ) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tablaProductos = new JTable(modeloTabla);
        JScrollPane scrollTabla = new JScrollPane(tablaProductos);

        add(panelFormulario, BorderLayout.NORTH);
        add(scrollTabla, BorderLayout.CENTER);

        btnNuevo.addActionListener(e -> limpiarFormulario());
        btnGuardar.addActionListener(e -> guardarProducto());
        btnActualizar.addActionListener(e -> actualizarProducto());
        btnDesactivar.addActionListener(e -> desactivarProducto());

        tablaProductos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarProducto();
            }
        });
    }

    private void cargarEstilos() {
        try {
            cbEstilo.removeAllItems();

            List<Estilo> estilos = estiloDAO.listarActivos();

            for (Estilo estilo : estilos) {
                cbEstilo.addItem(estilo);
            }

        } catch (SQLException ex) {
            mostrarError("Error al cargar estilos", ex);
        }
    }

    /**
     * Consulta productos desde la base de datos y actualiza los componentes visuales.
     */
    private void cargarProductos() {
        try {
            modeloTabla.setRowCount(0);

            List<Producto> productos = productoDAO.listarActivos();

            for (Producto producto : productos) {
                modeloTabla.addRow(new Object[]{
                        producto.getId(),
                        producto.getNombre(),
                        producto.getDescripcion(),
                        producto.getPrecio(),
                        producto.getEstilo().getNombre()
                });
            }

        } catch (SQLException ex) {
            mostrarError("Error al cargar productos", ex);
        }
    }

    /**
     * Valida los datos capturados y solicita al DAO guardar un producto nuevo.
     */
    private void guardarProducto() {
        try {
            Producto producto = construirProductoDesdeFormulario();
            productoDAO.insertar(producto);

            JOptionPane.showMessageDialog(this, "Producto guardado correctamente.");

            limpiarFormulario();
            cargarProductos();

        } catch (SQLException ex) {
            mostrarError("Error al guardar producto", ex);

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
     * Actualiza el producto seleccionado usando los datos actuales del formulario.
     */
    private void actualizarProducto() {
        if (productoSeleccionadoId == 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto para actualizar.");
            return;
        }

        try {
            Producto producto = construirProductoDesdeFormulario();
            producto.setId(productoSeleccionadoId);

            productoDAO.actualizar(producto);

            JOptionPane.showMessageDialog(this, "Producto actualizado correctamente.");

            limpiarFormulario();
            cargarProductos();

        } catch (SQLException ex) {
            mostrarError("Error al actualizar producto", ex);

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
     * Realiza una baja lógica del producto seleccionado tras confirmación del usuario.
     */
    private void desactivarProducto() {
        if (productoSeleccionadoId == 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto para desactivar.");
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Deseas desactivar el producto seleccionado?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );

        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            productoDAO.desactivar(productoSeleccionadoId);

            JOptionPane.showMessageDialog(this, "Producto desactivado correctamente.");

            limpiarFormulario();
            cargarProductos();

        } catch (SQLException ex) {
            mostrarError("Error al desactivar producto", ex);
        }
    }

    /**
     * Convierte los valores del formulario en un objeto Producto válido.
     *
     * @return producto listo para ser enviado al DAO.
     */
    private Producto construirProductoDesdeFormulario() {
        String nombre = txtNombre.getText();
        String descripcion = txtDescripcion.getText();
        BigDecimal precio = convertirPrecio(txtPrecio.getText());

        Estilo estilo = (Estilo) cbEstilo.getSelectedItem();

        if (estilo == null) {
            throw new IllegalArgumentException("Debes seleccionar un estilo.");
        }

        return new Producto(
                0,
                nombre,
                descripcion,
                precio,
                estilo,
                true
        );
    }

    /**
     * Convierte y valida el precio capturado por el usuario.
     *
     * @param textoPrecio valor escrito en el campo de texto.
     * @return precio convertido a BigDecimal.
     */
    private BigDecimal convertirPrecio(String textoPrecio) {
        if (textoPrecio == null || textoPrecio.trim().isEmpty()) {
            throw new IllegalArgumentException("El precio es obligatorio.");
        }

        try {
            return new BigDecimal(textoPrecio.trim());

        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("El precio debe ser numérico.");
        }
    }

    /**
     * Carga en el formulario los datos del producto seleccionado en la tabla.
     */
    private void seleccionarProducto() {
        int fila = tablaProductos.getSelectedRow();

        if (fila < 0) {
            return;
        }

        productoSeleccionadoId = Integer.parseInt(modeloTabla.getValueAt(fila, 0).toString());

        try {
            Producto producto = productoDAO.obtenerPorId(productoSeleccionadoId);

            if (producto == null) {
                return;
            }

            txtNombre.setText(producto.getNombre());
            txtDescripcion.setText(producto.getDescripcion());
            txtPrecio.setText(producto.getPrecio().toString());

            seleccionarEstilo(producto.getEstilo().getId());

        } catch (SQLException ex) {
            mostrarError("Error al seleccionar producto", ex);
        }
    }

    /**
     * Selecciona en el combo el estilo que coincide con el identificador indicado.
     *
     * @param estiloId identificador del estilo que se debe seleccionar.
     */
    private void seleccionarEstilo(int estiloId) {
        for (int i = 0; i < cbEstilo.getItemCount(); i++) {
            Estilo estilo = cbEstilo.getItemAt(i);

            if (estilo.getId() == estiloId) {
                cbEstilo.setSelectedIndex(i);
                return;
            }
        }
    }

    /**
     * Limpia los campos y reinicia la selección del formulario.
     */
    private void limpiarFormulario() {
        productoSeleccionadoId = 0;

        txtNombre.setText("");
        txtDescripcion.setText("");
        txtPrecio.setText("");

        if (cbEstilo.getItemCount() > 0) {
            cbEstilo.setSelectedIndex(0);
        }

        tablaProductos.clearSelection();
        txtNombre.requestFocus();
    }

    /**
     * Recarga los catálogos y la tabla del panel para reflejar cambios recientes.
     */
    public void recargarDatos() {
        cargarEstilos();
        cargarProductos();
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