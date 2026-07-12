package com.glasscore.vista;

import com.glasscore.dao.impl.EmpleadoDAOImpl;
import com.glasscore.dao.impl.HerramientaDAOImpl;
import com.glasscore.modelo.Empleado;
import com.glasscore.modelo.Herramienta;
import com.glasscore.util.UITheme;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class PanelHerramientas extends JPanel {

    private final HerramientaDAOImpl herramientaDAO = new HerramientaDAOImpl();
    private final EmpleadoDAOImpl empleadoDAO = new EmpleadoDAOImpl();

    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new String[]{"ID", "Código", "Nombre", "Tipo", "Estado", "Responsable"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modeloTabla);
    private final JTextField txtCodigo = new JTextField(12);
    private final JTextField txtNombre = new JTextField(18);
    private final JTextField txtTipo = new JTextField(12);
    private final JComboBox<Empleado> cmbEmpleado = new JComboBox<>();
    private final JComboBox<Herramienta> cmbHerramienta = new JComboBox<>();

    public PanelHerramientas() {
        setLayout(new BorderLayout(12, 12));
        setBackground(UITheme.BG);
        setBorder(javax.swing.BorderFactory.createEmptyBorder(16, 16, 16, 16));

        add(UITheme.sectionTitle("Inventario de Herramientas y Asignación"), BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(12, 12));
        centro.setOpaque(false);

        JPanel form = UITheme.card();
        form.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;

        UITheme.styleField(txtCodigo);
        UITheme.styleField(txtNombre);
        UITheme.styleField(txtTipo);

        int row = 0;
        g.gridx = 0; g.gridy = row; form.add(new JLabel("Código:"), g);
        g.gridx = 1; form.add(txtCodigo, g);
        g.gridx = 2; form.add(new JLabel("Nombre:"), g);
        g.gridx = 3; form.add(txtNombre, g);
        row++;
        g.gridx = 0; g.gridy = row; form.add(new JLabel("Tipo:"), g);
        g.gridx = 1; form.add(txtTipo, g);

        JButton btnRegistrar = UITheme.primaryButton("Registrar herramienta");
        btnRegistrar.addActionListener(e -> registrar());
        g.gridx = 3; form.add(btnRegistrar, g);

        JPanel asignacion = UITheme.card();
        asignacion.setLayout(new BorderLayout(8, 8));

        JPanel filaCombos = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filaCombos.setOpaque(false);
        filaCombos.add(new JLabel("Herramienta:"));
        filaCombos.add(cmbHerramienta);
        filaCombos.add(new JLabel("Empleado (chofer/instalador):"));
        filaCombos.add(cmbEmpleado);

        JPanel filaBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filaBotones.setOpaque(false);
        JButton btnAsignar = UITheme.accentButton("Asignar a obra");
        btnAsignar.addActionListener(e -> asignar());
        JButton btnDevolver = UITheme.dangerButton("Devolver a bodega");
        btnDevolver.addActionListener(e -> devolver());
        filaBotones.add(btnAsignar);
        filaBotones.add(btnDevolver);

        asignacion.add(filaCombos, BorderLayout.NORTH);
        asignacion.add(filaBotones, BorderLayout.SOUTH);

        JPanel norteForms = new JPanel(new BorderLayout(8, 8));
        norteForms.setOpaque(false);
        norteForms.add(form, BorderLayout.NORTH);
        norteForms.add(asignacion, BorderLayout.SOUTH);

        UITheme.styleTable(tabla);
        centro.add(norteForms, BorderLayout.NORTH);
        centro.add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);

        JButton btnRefrescar = UITheme.primaryButton("Actualizar lista");
        btnRefrescar.addActionListener(e -> refrescar());
        JPanel sur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        sur.setOpaque(false);
        sur.add(btnRefrescar);
        add(sur, BorderLayout.SOUTH);

        refrescar();
    }

    private void registrar() {
        try {
            Herramienta h = new Herramienta();
            h.setCodigo(txtCodigo.getText().trim());
            h.setNombre(txtNombre.getText().trim());
            h.setTipo(txtTipo.getText().trim());
            h.setEstado("DISPONIBLE");
            if (h.getCodigo().isEmpty() || h.getNombre().isEmpty() || h.getTipo().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Complete código, nombre y tipo.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            herramientaDAO.insertar(h);
            txtCodigo.setText("");
            txtNombre.setText("");
            txtTipo.setText("");
            refrescar();
            JOptionPane.showMessageDialog(this, "Herramienta registrada en estado DISPONIBLE.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void asignar() {
        try {
            Herramienta h = (Herramienta) cmbHerramienta.getSelectedItem();
            Empleado e = (Empleado) cmbEmpleado.getSelectedItem();
            if (h == null || e == null) {
                JOptionPane.showMessageDialog(this, "Seleccione herramienta y empleado.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            boolean ok = herramientaDAO.asignar(h.getId(), e.getId());
            if (!ok) {
                JOptionPane.showMessageDialog(this,
                        "No se pudo asignar. Verifique que esté DISPONIBLE.",
                        "Asignación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(this,
                    "Herramienta " + h.getCodigo() + " asignada a " + e.getNombreCompleto()
                    + ".\nEstado actualizado: DISPONIBLE → ASIGNADA.");
            refrescar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void devolver() {
        try {
            int row = tabla.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Seleccione una herramienta en la tabla.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int id = (int) modeloTabla.getValueAt(row, 0);
            herramientaDAO.devolver(id);
            JOptionPane.showMessageDialog(this, "Herramienta devuelta. Estado: DISPONIBLE.");
            refrescar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refrescar() {
        try {
            modeloTabla.setRowCount(0);
            List<Herramienta> lista = herramientaDAO.listarTodas();
            for (Herramienta h : lista) {
                modeloTabla.addRow(new Object[]{
                    h.getId(), h.getCodigo(), h.getNombre(), h.getTipo(), h.getEstado(),
                    h.getEmpleadoNombre() == null ? "-" : h.getEmpleadoNombre()
                });
            }
            DefaultComboBoxModel<Herramienta> mh = new DefaultComboBoxModel<>();
            for (Herramienta h : herramientaDAO.listarDisponibles()) {
                mh.addElement(h);
            }
            cmbHerramienta.setModel(mh);

            DefaultComboBoxModel<Empleado> me = new DefaultComboBoxModel<>();
            for (Empleado e : empleadoDAO.listarTodos()) {
                if ("CHOFER".equals(e.getCargo()) || "INSTALADOR".equals(e.getCargo())) {
                    me.addElement(e);
                }
            }
            cmbEmpleado.setModel(me);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error cargando datos: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
