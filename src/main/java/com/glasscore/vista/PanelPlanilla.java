package com.glasscore.vista;

import com.glasscore.dao.impl.EmpleadoDAOImpl;
import com.glasscore.modelo.Empleado;
import com.glasscore.modelo.Planilla;
import com.glasscore.servicio.PlanillaServicio;
import com.glasscore.util.ReporteUtil;
import com.glasscore.util.UITheme;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class PanelPlanilla extends JPanel {

    private final EmpleadoDAOImpl empleadoDAO = new EmpleadoDAOImpl();
    private final PlanillaServicio planillaServicio = new PlanillaServicio();

    private final DefaultTableModel modeloEmp = new DefaultTableModel(
            new String[]{"ID", "Nombre", "Apellido", "Cargo", "Salario base", "Teléfono"}, 0) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };
    private final DefaultTableModel modeloPlanilla = new DefaultTableModel(
            new String[]{"ID", "Empleado", "Salario base", "Horas extras", "Viáticos", "Total neto", "Fecha"}, 0) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };

    private final JTextField txtNombre = new JTextField(12);
    private final JTextField txtApellido = new JTextField(12);
    private final JComboBox<String> cmbCargo = new JComboBox<>(
            new String[]{"CHOFER", "INSTALADOR", "ADMINISTRATIVO", "SUPERVISOR"});
    private final JTextField txtSalario = new JTextField(10);
    private final JTextField txtTelefono = new JTextField(10);
    private Integer empleadoEditando = null;

    private final JComboBox<Empleado> cmbEmpleadoPago = new JComboBox<>();
    private final JTextField txtExtras = new JTextField("0", 8);
    private final JTextField txtViaticos = new JTextField("0", 8);
    private final JLabel lblNeto = new JLabel("Neto: Lps 0.00");

    public PanelPlanilla() {
        setLayout(new BorderLayout(12, 12));
        setBackground(UITheme.BG);
        setBorder(javax.swing.BorderFactory.createEmptyBorder(16, 16, 16, 16));
        add(UITheme.sectionTitle("Gestión de Empleados y Cierre de Planilla"), BorderLayout.NORTH);

        JPanel formEmp = UITheme.card();
        formEmp.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.fill = GridBagConstraints.HORIZONTAL;

        UITheme.styleField(txtNombre);
        UITheme.styleField(txtApellido);
        UITheme.styleField(txtSalario);
        UITheme.styleField(txtTelefono);
        UITheme.styleField(txtExtras);
        UITheme.styleField(txtViaticos);

        int r = 0;
        g.gridx = 0; g.gridy = r; formEmp.add(new JLabel("Nombre:"), g);
        g.gridx = 1; formEmp.add(txtNombre, g);
        g.gridx = 2; formEmp.add(new JLabel("Apellido:"), g);
        g.gridx = 3; formEmp.add(txtApellido, g);
        r++;
        g.gridx = 0; g.gridy = r; formEmp.add(new JLabel("Cargo:"), g);
        g.gridx = 1; formEmp.add(cmbCargo, g);
        g.gridx = 2; formEmp.add(new JLabel("Salario base:"), g);
        g.gridx = 3; formEmp.add(txtSalario, g);
        r++;
        g.gridx = 0; g.gridy = r; formEmp.add(new JLabel("Teléfono:"), g);
        g.gridx = 1; formEmp.add(txtTelefono, g);

        JPanel botonesEmp = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        botonesEmp.setOpaque(false);
        JButton btnNuevo = UITheme.primaryButton("Guardar empleado");
        JButton btnActualizar = UITheme.accentButton("Actualizar seleccionado");
        JButton btnEliminar = UITheme.dangerButton("Desactivar");
        btnNuevo.addActionListener(e -> guardarEmpleado(false));
        btnActualizar.addActionListener(e -> guardarEmpleado(true));
        btnEliminar.addActionListener(e -> desactivar());
        botonesEmp.add(btnNuevo);
        botonesEmp.add(btnActualizar);
        botonesEmp.add(btnEliminar);

        JPanel formBlock = new JPanel(new BorderLayout(0, 8));
        formBlock.setOpaque(false);
        formBlock.add(formEmp, BorderLayout.CENTER);
        formBlock.add(botonesEmp, BorderLayout.SOUTH);

        JTable tablaEmp = new JTable(modeloEmp);
        UITheme.styleTable(tablaEmp);
        tablaEmp.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaEmp.getSelectedRow() >= 0) {
                int row = tablaEmp.getSelectedRow();
                empleadoEditando = (Integer) modeloEmp.getValueAt(row, 0);
                txtNombre.setText(String.valueOf(modeloEmp.getValueAt(row, 1)));
                txtApellido.setText(String.valueOf(modeloEmp.getValueAt(row, 2)));
                cmbCargo.setSelectedItem(String.valueOf(modeloEmp.getValueAt(row, 3)));
                txtSalario.setText(String.valueOf(modeloEmp.getValueAt(row, 4)));
                txtTelefono.setText(String.valueOf(modeloEmp.getValueAt(row, 5)));
            }
        });

        JPanel planillaCard = UITheme.card();
        planillaCard.setLayout(new BorderLayout(8, 8));

        JPanel filaDatos = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filaDatos.setOpaque(false);
        filaDatos.add(new JLabel("Empleado:"));
        filaDatos.add(cmbEmpleadoPago);
        filaDatos.add(new JLabel("Horas extras (Lps):"));
        filaDatos.add(txtExtras);
        filaDatos.add(new JLabel("Viáticos (Lps):"));
        filaDatos.add(txtViaticos);
        filaDatos.add(lblNeto);

        JPanel filaAcciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filaAcciones.setOpaque(false);
        JButton btnCalcular = UITheme.accentButton("Previsualizar neto");
        btnCalcular.addActionListener(e -> previsualizar());
        JButton btnCerrar = UITheme.primaryButton("Cerrar planilla");
        btnCerrar.addActionListener(e -> cerrarPlanilla());
        JButton btnReporte = UITheme.primaryButton("Comprobante Jasper");
        btnReporte.addActionListener(e -> imprimirComprobante());
        filaAcciones.add(btnCalcular);
        filaAcciones.add(btnCerrar);
        filaAcciones.add(btnReporte);

        planillaCard.add(filaDatos, BorderLayout.NORTH);
        planillaCard.add(filaAcciones, BorderLayout.SOUTH);

        JTable tablaPlanilla = new JTable(modeloPlanilla);
        UITheme.styleTable(tablaPlanilla);

        JScrollPane scrollEmp = new JScrollPane(tablaEmp);
        scrollEmp.setPreferredSize(new Dimension(100, 340));
        scrollEmp.setMinimumSize(new Dimension(100, 220));

        JScrollPane scrollPlanilla = new JScrollPane(tablaPlanilla);
        scrollPlanilla.setPreferredSize(new Dimension(100, 180));
        scrollPlanilla.setMinimumSize(new Dimension(100, 120));

        JPanel centro = new JPanel(new BorderLayout(8, 8));
        centro.setOpaque(false);
        centro.add(formBlock, BorderLayout.NORTH);
        centro.add(scrollEmp, BorderLayout.CENTER);

        JPanel sur = new JPanel(new BorderLayout(8, 8));
        sur.setOpaque(false);
        sur.add(planillaCard, BorderLayout.NORTH);
        sur.add(scrollPlanilla, BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, centro, sur);
        split.setResizeWeight(0.68);
        split.setContinuousLayout(true);
        split.setBorder(null);
        split.setOpaque(false);
        add(split, BorderLayout.CENTER);
        SwingUtilities.invokeLater(() -> split.setDividerLocation(0.68));

        cmbEmpleadoPago.addActionListener(e -> previsualizar());
        refrescar();
    }

    private void guardarEmpleado(boolean actualizar) {
        try {
            Empleado emp = new Empleado();
            emp.setNombre(txtNombre.getText().trim());
            emp.setApellido(txtApellido.getText().trim());
            emp.setCargo((String) cmbCargo.getSelectedItem());
            emp.setSalarioBase(Double.parseDouble(txtSalario.getText().trim().replace(',', '.')));
            emp.setTelefono(txtTelefono.getText().trim());
            emp.setActivo(true);
            if (emp.getNombre().isEmpty() || emp.getApellido().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nombre y apellido son obligatorios.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (actualizar) {
                if (empleadoEditando == null) {
                    JOptionPane.showMessageDialog(this, "Seleccione un empleado en la tabla.",
                            "Validación", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                emp.setId(empleadoEditando);
                empleadoDAO.actualizar(emp);
                JOptionPane.showMessageDialog(this, "Empleado actualizado.");
            } else {
                empleadoDAO.insertar(emp);
                JOptionPane.showMessageDialog(this, "Empleado registrado.");
            }
            limpiarForm();
            refrescar();
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Salario inválido.", "Validación", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void desactivar() {
        try {
            if (empleadoEditando == null) {
                JOptionPane.showMessageDialog(this, "Seleccione un empleado.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            empleadoDAO.eliminar(empleadoEditando);
            limpiarForm();
            refrescar();
            JOptionPane.showMessageDialog(this, "Empleado desactivado.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void previsualizar() {
        try {
            Empleado emp = (Empleado) cmbEmpleadoPago.getSelectedItem();
            if (emp == null) {
                return;
            }
            double extras = Double.parseDouble(txtExtras.getText().trim().replace(',', '.'));
            double viaticos = Double.parseDouble(txtViaticos.getText().trim().replace(',', '.'));
            double neto = emp.getSalarioBase() + extras + viaticos;
            lblNeto.setText(String.format("Neto: Lps %.2f  (base %.2f + extras %.2f + viáticos %.2f)",
                    neto, emp.getSalarioBase(), extras, viaticos));
        } catch (Exception ignored) {
            lblNeto.setText("Neto: -");
        }
    }

    private void cerrarPlanilla() {
        try {
            Empleado emp = (Empleado) cmbEmpleadoPago.getSelectedItem();
            if (emp == null) {
                JOptionPane.showMessageDialog(this, "Seleccione empleado.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            double extras = Double.parseDouble(txtExtras.getText().trim().replace(',', '.'));
            double viaticos = Double.parseDouble(txtViaticos.getText().trim().replace(',', '.'));
            Planilla p = planillaServicio.calcularYRegistrar(emp.getId(), extras, viaticos, LocalDate.now());
            JOptionPane.showMessageDialog(this,
                    "Planilla #" + p.getId() + " cerrada.\nTotal neto: Lps " + String.format("%.2f", p.getTotalNeto()));
            refrescar();
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Montos inválidos.", "Validación", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void imprimirComprobante() {
        try {
            Empleado emp = (Empleado) cmbEmpleadoPago.getSelectedItem();
            if (emp == null) {
                JOptionPane.showMessageDialog(this, "Seleccione empleado para el reporte.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            ReporteUtil.generarComprobantePlanilla(emp.getId());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error generando reporte Jasper:\n" + ex.getMessage(),
                    "JasperReports", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarForm() {
        empleadoEditando = null;
        txtNombre.setText("");
        txtApellido.setText("");
        txtSalario.setText("");
        txtTelefono.setText("");
    }

    public void refrescar() {
        try {
            modeloEmp.setRowCount(0);
            List<Empleado> empleados = empleadoDAO.listarTodos();
            DefaultComboBoxModel<Empleado> model = new DefaultComboBoxModel<>();
            for (Empleado e : empleados) {
                modeloEmp.addRow(new Object[]{
                    e.getId(), e.getNombre(), e.getApellido(), e.getCargo(),
                    e.getSalarioBase(), e.getTelefono()
                });
                model.addElement(e);
            }
            cmbEmpleadoPago.setModel(model);

            modeloPlanilla.setRowCount(0);
            for (Planilla p : planillaServicio.listarHistorico()) {
                modeloPlanilla.addRow(new Object[]{
                    p.getId(), p.getEmpleadoNombre(), p.getSalarioBase(),
                    p.getHorasExtras(), p.getViaticos(), p.getTotalNeto(), p.getFechaPago()
                });
            }
            previsualizar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
