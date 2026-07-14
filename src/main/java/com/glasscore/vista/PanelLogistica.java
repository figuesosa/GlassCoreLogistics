package com.glasscore.vista;

import com.glasscore.dao.impl.EmpleadoDAOImpl;
import com.glasscore.dao.impl.VehiculoDAOImpl;
import com.glasscore.modelo.Empleado;
import com.glasscore.modelo.Vehiculo;
import com.glasscore.modelo.Viaje;
import com.glasscore.servicio.LogisticaServicio;
import com.glasscore.util.ReporteUtil;
import com.glasscore.util.UITheme;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class PanelLogistica extends JPanel {

    private final VehiculoDAOImpl vehiculoDAO = new VehiculoDAOImpl();
    private final EmpleadoDAOImpl empleadoDAO = new EmpleadoDAOImpl();
    private final LogisticaServicio logisticaServicio = new LogisticaServicio();

    private final DefaultTableModel modeloVeh = new DefaultTableModel(
            new String[]{"ID", "Placa", "Marca", "Km actual", "Km límite mant.", "Chofer"}, 0) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };
    private final DefaultTableModel modeloViajes = new DefaultTableModel(
            new String[]{"ID", "Placa", "Chofer", "Ruta", "Km", "Litros", "Gasto Lps", "Fecha"}, 0) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };

    private final JTextField txtPlaca = new JTextField(10);
    private final JTextField txtMarca = new JTextField(12);
    private final JTextField txtKmActual = new JTextField(8);
    private final JTextField txtKmLimite = new JTextField(8);
    private final JComboBox<Empleado> cmbChofer = new JComboBox<>();
    private Integer vehiculoEditando = null;

    private final JComboBox<Vehiculo> cmbVehiculoViaje = new JComboBox<>();
    private final JCheckBox chkRedondo = new JCheckBox("Viaje redondo (170 km)");
    private final JLabel lblCalculo = new JLabel("Ruta fija: Tegucigalpa → Comayagua (85 km)");

    public PanelLogistica() {
        setLayout(new BorderLayout(12, 12));
        setBackground(UITheme.BG);
        setBorder(javax.swing.BorderFactory.createEmptyBorder(16, 16, 16, 16));
        add(UITheme.sectionTitle("Logística: Vehículos, Rutas y Mantenimiento"), BorderLayout.NORTH);

        UITheme.styleField(txtPlaca);
        UITheme.styleField(txtMarca);
        UITheme.styleField(txtKmActual);
        UITheme.styleField(txtKmLimite);

        JPanel form = UITheme.card();
        form.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.fill = GridBagConstraints.HORIZONTAL;

        int r = 0;
        g.gridx = 0; g.gridy = r; form.add(new JLabel("Placa:"), g);
        g.gridx = 1; form.add(txtPlaca, g);
        g.gridx = 2; form.add(new JLabel("Marca:"), g);
        g.gridx = 3; form.add(txtMarca, g);
        r++;
        g.gridx = 0; g.gridy = r; form.add(new JLabel("Km actual:"), g);
        g.gridx = 1; form.add(txtKmActual, g);
        g.gridx = 2; form.add(new JLabel("Km límite mantenimiento:"), g);
        g.gridx = 3; form.add(txtKmLimite, g);
        r++;
        g.gridx = 0; g.gridy = r; form.add(new JLabel("Chofer:"), g);
        g.gridx = 1; form.add(cmbChofer, g);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        botones.setOpaque(false);
        JButton btnGuardar = UITheme.primaryButton("Guardar vehículo");
        JButton btnActualizar = UITheme.accentButton("Actualizar seleccionado");
        btnGuardar.addActionListener(e -> guardarVehiculo(false));
        btnActualizar.addActionListener(e -> guardarVehiculo(true));
        botones.add(btnGuardar);
        botones.add(btnActualizar);

        JPanel formBlock = new JPanel(new BorderLayout(0, 8));
        formBlock.setOpaque(false);
        formBlock.add(form, BorderLayout.CENTER);
        formBlock.add(botones, BorderLayout.SOUTH);

        JTable tablaVeh = new JTable(modeloVeh);
        UITheme.styleTable(tablaVeh);
        tablaVeh.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaVeh.getSelectedRow() >= 0) {
                int row = tablaVeh.getSelectedRow();
                vehiculoEditando = (Integer) modeloVeh.getValueAt(row, 0);
                txtPlaca.setText(String.valueOf(modeloVeh.getValueAt(row, 1)));
                txtMarca.setText(String.valueOf(modeloVeh.getValueAt(row, 2)));
                txtKmActual.setText(String.valueOf(modeloVeh.getValueAt(row, 3)));
                txtKmLimite.setText(String.valueOf(modeloVeh.getValueAt(row, 4)));
                sincronizarChoferSeleccionado(vehiculoEditando);
            }
        });

        JPanel viajeCard = UITheme.card();
        viajeCard.setLayout(new BorderLayout(8, 8));

        JPanel filaDatos = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        filaDatos.setOpaque(false);
        filaDatos.add(new JLabel("Vehículo:"));
        filaDatos.add(cmbVehiculoViaje);
        filaDatos.add(chkRedondo);
        filaDatos.add(lblCalculo);

        JPanel filaAcciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filaAcciones.setOpaque(false);
        JButton btnCalcular = UITheme.accentButton("Calcular combustible");
        btnCalcular.addActionListener(e -> actualizarCalculo());
        JButton btnAutorizar = UITheme.primaryButton("Autorizar salida");
        btnAutorizar.addActionListener(e -> autorizarSalida());
        JButton btnReporte = UITheme.primaryButton("Hoja de ruta Jasper");
        btnReporte.addActionListener(e -> imprimirHojaRuta());
        filaAcciones.add(btnCalcular);
        filaAcciones.add(btnAutorizar);
        filaAcciones.add(btnReporte);

        viajeCard.add(filaDatos, BorderLayout.NORTH);
        viajeCard.add(filaAcciones, BorderLayout.SOUTH);

        chkRedondo.addActionListener(e -> actualizarCalculo());
        cmbVehiculoViaje.addActionListener(e -> actualizarCalculo());

        JTable tablaViajes = new JTable(modeloViajes);
        UITheme.styleTable(tablaViajes);

        JPanel centro = new JPanel(new BorderLayout(8, 8));
        centro.setOpaque(false);
        centro.add(formBlock, BorderLayout.NORTH);
        centro.add(new JScrollPane(tablaVeh), BorderLayout.CENTER);

        JPanel sur = new JPanel(new BorderLayout(8, 8));
        sur.setOpaque(false);
        sur.add(viajeCard, BorderLayout.NORTH);
        sur.add(new JScrollPane(tablaViajes), BorderLayout.CENTER);

        add(centro, BorderLayout.CENTER);
        add(sur, BorderLayout.SOUTH);

        refrescar();
        actualizarCalculo();
    }

    private void sincronizarChoferSeleccionado(Integer vehiculoId) {
        try {
            cmbChofer.setSelectedItem(null);
            if (vehiculoId == null) {
                return;
            }
            Vehiculo v = vehiculoDAO.buscarPorId(vehiculoId);
            if (v == null || v.getChoferId() == null) {
                return;
            }
            for (int i = 0; i < cmbChofer.getItemCount(); i++) {
                Empleado e = cmbChofer.getItemAt(i);
                if (e != null && e.getId() == v.getChoferId()) {
                    cmbChofer.setSelectedIndex(i);
                    return;
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "No se pudo cargar el chofer: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardarVehiculo(boolean actualizar) {
        try {
            Vehiculo v = new Vehiculo();
            v.setPlaca(txtPlaca.getText().trim().toUpperCase());
            v.setMarca(txtMarca.getText().trim());
            v.setKmActual(Integer.parseInt(txtKmActual.getText().trim()));
            v.setKmLimiteMantenimiento(Integer.parseInt(txtKmLimite.getText().trim()));
            Empleado chofer = (Empleado) cmbChofer.getSelectedItem();
            v.setChoferId(chofer == null ? null : chofer.getId());

            if (v.getPlaca().isEmpty() || v.getMarca().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Placa y marca son obligatorias.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (actualizar) {
                if (vehiculoEditando == null) {
                    JOptionPane.showMessageDialog(this, "Seleccione un vehículo.",
                            "Validación", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                v.setId(vehiculoEditando);
                vehiculoDAO.actualizar(v);
                JOptionPane.showMessageDialog(this, "Vehículo actualizado.");
            } else {
                vehiculoDAO.insertar(v);
                JOptionPane.showMessageDialog(this, "Vehículo registrado.");
            }
            vehiculoEditando = null;
            txtPlaca.setText("");
            txtMarca.setText("");
            txtKmActual.setText("");
            txtKmLimite.setText("");
            refrescar();
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Kilometrajes deben ser enteros.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarCalculo() {
        LogisticaServicio.ResultadoCalculo calc = logisticaServicio.calcularRuta(chkRedondo.isSelected());
        lblCalculo.setText(String.format(
                "Ruta fija TGU-CMY: %d km | Consumo est.: %.3f L | Gasto proyectado: Lps %.2f",
                calc.kilometros, calc.litros, calc.gastoLps));
    }

    private void autorizarSalida() {
        try {
            Vehiculo v = (Vehiculo) cmbVehiculoViaje.getSelectedItem();
            if (v == null) {
                JOptionPane.showMessageDialog(this, "Seleccione un vehículo.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Viaje viaje = logisticaServicio.autorizarYRegistrarViaje(v.getId(), chkRedondo.isSelected());
            JOptionPane.showMessageDialog(this,
                    "Salida autorizada. Viaje #" + viaje.getId()
                    + "\nGasto combustible: Lps " + String.format("%.2f", viaje.getGastoCombustible())
                    + "\nHerramientas en custodia: " + viaje.getHerramientasCustodia(),
                    "Despacho", JOptionPane.INFORMATION_MESSAGE);
            refrescar();
        } catch (LogisticaServicio.MantenimientoRequeridoException mre) {
            JOptionPane.showMessageDialog(this, mre.getMessage(),
                    "Bloqueo de Seguridad - Mantenimiento Preventivo",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void imprimirHojaRuta() {
        try {
            if (modeloViajes.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No hay viajes registrados.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int viajeId = (int) modeloViajes.getValueAt(0, 0);
            int seleccion = JOptionPane.showConfirmDialog(this,
                    "¿Imprimir hoja de ruta del viaje más reciente (#" + viajeId + ")?\n"
                    + "También puede indicar otro ID.",
                    "Hoja de Ruta", JOptionPane.YES_NO_CANCEL_OPTION);
            if (seleccion == JOptionPane.CANCEL_OPTION || seleccion == JOptionPane.CLOSED_OPTION) {
                return;
            }
            if (seleccion == JOptionPane.NO_OPTION) {
                String input = JOptionPane.showInputDialog(this, "ID del viaje:", viajeId);
                if (input == null) {
                    return;
                }
                viajeId = Integer.parseInt(input.trim());
            }
            ReporteUtil.generarHojaRuta(viajeId);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error generando reporte Jasper:\n" + ex.getMessage(),
                    "JasperReports", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refrescar() {
        try {
            modeloVeh.setRowCount(0);
            DefaultComboBoxModel<Vehiculo> mv = new DefaultComboBoxModel<>();
            for (Vehiculo v : vehiculoDAO.listarTodos()) {
                modeloVeh.addRow(new Object[]{
                    v.getId(), v.getPlaca(), v.getMarca(), v.getKmActual(),
                    v.getKmLimiteMantenimiento(),
                    v.getChoferNombre() == null ? "-" : v.getChoferNombre()
                });
                mv.addElement(v);
            }
            cmbVehiculoViaje.setModel(mv);

            DefaultComboBoxModel<Empleado> mc = new DefaultComboBoxModel<>();
            for (Empleado e : empleadoDAO.listarPorCargo("CHOFER")) {
                mc.addElement(e);
            }
            cmbChofer.setModel(mc);

            modeloViajes.setRowCount(0);
            List<Viaje> viajes = logisticaServicio.listarViajes();
            for (Viaje vi : viajes) {
                modeloViajes.addRow(new Object[]{
                    vi.getId(), vi.getPlaca(), vi.getChoferNombre(), vi.getRuta(),
                    vi.getKilometros(), vi.getLitrosEstimados(), vi.getGastoCombustible(),
                    vi.getFechaSalida()
                });
            }
            actualizarCalculo();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
