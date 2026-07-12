package com.glasscore.vista;

import com.glasscore.dao.impl.MaterialDAOImpl;
import com.glasscore.modelo.Cotizacion;
import com.glasscore.modelo.Material;
import com.glasscore.servicio.CotizacionServicio;
import com.glasscore.util.FontUtil;
import com.glasscore.util.UITheme;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
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

public class PanelCotizaciones extends JPanel {

    private final CotizacionServicio servicio = new CotizacionServicio();
    private final MaterialDAOImpl materialDAO = new MaterialDAOImpl();

    private final JTextField txtCliente = new JTextField(18);
    private final JComboBox<String> cmbTipo = new JComboBox<>(new String[]{"VENTANA", "PUERTA", "BALCON"});
    private final JTextField txtAncho = new JTextField(8);
    private final JTextField txtAlto = new JTextField(8);
    private final JTextArea txtAlerta = new JTextArea(4, 40);
    private final JLabel lblResumen = new JLabel(" ");
    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new String[]{"ID", "Cliente", "Tipo", "Ancho", "Alto", "m² Vidrio", "ML Alum.", "ML Metal", "Subtotal", "Alerta"}, 0) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };
    private final DefaultTableModel modeloStock = new DefaultTableModel(
            new String[]{"Material", "Tipo", "Unidad", "Stock", "Precio Lps"}, 0) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };

    public PanelCotizaciones() {
        setLayout(new BorderLayout(12, 12));
        setBackground(UITheme.BG);
        setBorder(javax.swing.BorderFactory.createEmptyBorder(16, 16, 16, 16));
        add(UITheme.sectionTitle("Ventas y Cotización (Manufactura Flexible)"), BorderLayout.NORTH);

        JPanel form = UITheme.card();
        form.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.WEST;

        UITheme.styleField(txtCliente);
        UITheme.styleField(txtAncho);
        UITheme.styleField(txtAlto);
        txtAlerta.setEditable(false);
        txtAlerta.setLineWrap(true);
        txtAlerta.setWrapStyleWord(true);
        txtAlerta.setForeground(UITheme.DANGER);
        txtAlerta.setBackground(new Color(0xFF, 0xF5, 0xF5));

        int r = 0;
        g.gridx = 0; g.gridy = r; form.add(new JLabel("Cliente:"), g);
        g.gridx = 1; form.add(txtCliente, g);
        g.gridx = 2; form.add(new JLabel("Estructura:"), g);
        g.gridx = 3; form.add(cmbTipo, g);
        r++;
        g.gridx = 0; g.gridy = r; form.add(new JLabel("Ancho (m):"), g);
        g.gridx = 1; form.add(txtAncho, g);
        g.gridx = 2; form.add(new JLabel("Alto (m):"), g);
        g.gridx = 3; form.add(txtAlto, g);
        r++;
        JButton btnCalcular = UITheme.primaryButton("Calcular y guardar cotización");
        btnCalcular.addActionListener(e -> guardar());
        g.gridx = 1; g.gridy = r; g.gridwidth = 2; form.add(btnCalcular, g);
        g.gridwidth = 1;
        r++;
        g.gridx = 0; g.gridy = r; g.gridwidth = 4; form.add(lblResumen, g);
        r++;
        g.gridx = 0; g.gridy = r; form.add(new JLabel("Alerta de compra requerida:"), g);
        r++;
        g.gridx = 0; g.gridy = r; form.add(new JScrollPane(txtAlerta), g);

        JTable tablaStock = new JTable(modeloStock);
        UITheme.styleTable(tablaStock);
        tablaStock.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        tablaStock.getColumnModel().getColumn(0).setPreferredWidth(180);
        tablaStock.getColumnModel().getColumn(1).setPreferredWidth(80);
        tablaStock.getColumnModel().getColumn(2).setPreferredWidth(60);
        tablaStock.getColumnModel().getColumn(3).setPreferredWidth(70);
        tablaStock.getColumnModel().getColumn(4).setPreferredWidth(90);

        JLabel lblStock = new JLabel("Stock bodega");
        lblStock.setFont(FontUtil.dataBold(14));
        lblStock.setForeground(UITheme.PRIMARY);

        JPanel stockCard = UITheme.card();
        stockCard.setLayout(new BorderLayout(6, 6));
        stockCard.add(lblStock, BorderLayout.NORTH);
        stockCard.add(new JScrollPane(tablaStock), BorderLayout.CENTER);
        stockCard.setPreferredSize(new java.awt.Dimension(520, 280));
        stockCard.setMinimumSize(new java.awt.Dimension(420, 220));

        JPanel arriba = new JPanel(new BorderLayout(8, 8));
        arriba.setOpaque(false);
        arriba.add(form, BorderLayout.CENTER);
        arriba.add(stockCard, BorderLayout.EAST);

        JTable tabla = new JTable(modeloTabla);
        UITheme.styleTable(tabla);

        JPanel abajo = new JPanel(new BorderLayout(8, 8));
        abajo.setOpaque(false);
        abajo.setPreferredSize(new java.awt.Dimension(100, 260));
        abajo.add(new JLabel("Cotizaciones guardadas"), BorderLayout.NORTH);
        abajo.add(new JScrollPane(tabla), BorderLayout.CENTER);
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.setOpaque(false);
        JButton btnRef = UITheme.accentButton("Actualizar");
        btnRef.addActionListener(e -> refrescar());
        botones.add(btnRef);
        abajo.add(botones, BorderLayout.SOUTH);

        add(arriba, BorderLayout.CENTER);
        add(abajo, BorderLayout.SOUTH);
        refrescar();
    }

    private void guardar() {
        try {
            double ancho = Double.parseDouble(txtAncho.getText().trim().replace(',', '.'));
            double alto = Double.parseDouble(txtAlto.getText().trim().replace(',', '.'));
            Cotizacion cot = servicio.calcularYGuardar(
                    txtCliente.getText(),
                    (String) cmbTipo.getSelectedItem(),
                    ancho, alto);

            lblResumen.setText(String.format(
                    "Área vidrio: %.3f m² | Aluminio: %.3f ML | Metal: %.3f ML | Subtotal: Lps %.2f",
                    cot.getAreaVidrio(), cot.getMetrosAluminio(), cot.getMetrosMetal(), cot.getSubtotal()));

            if (cot.getAlertaCompra() != null && !cot.getAlertaCompra().isBlank()) {
                txtAlerta.setForeground(UITheme.DANGER);
                txtAlerta.setText(cot.getAlertaCompra());
                JOptionPane.showMessageDialog(this,
                        "Cotización guardada con éxito.\n\n" + cot.getAlertaCompra(),
                        "Alerta de Compra Requerida", JOptionPane.WARNING_MESSAGE);
            } else {
                txtAlerta.setForeground(UITheme.SUCCESS);
                txtAlerta.setText("Stock suficiente. No se requiere compra de emergencia.");
                JOptionPane.showMessageDialog(this, "Cotización #" + cot.getId() + " guardada. Stock suficiente.");
            }
            refrescar();
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Ingrese medidas numéricas válidas.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refrescar() {
        try {
            modeloStock.setRowCount(0);
            for (Material m : materialDAO.listarTodos()) {
                modeloStock.addRow(new Object[]{
                    m.getNombre(), m.getTipo(), m.getUnidad(), m.getStock(), m.getPrecioUnitario()
                });
            }
            modeloTabla.setRowCount(0);
            List<Cotizacion> lista = servicio.listar();
            for (Cotizacion c : lista) {
                modeloTabla.addRow(new Object[]{
                    c.getId(), c.getCliente(), c.getTipoEstructura(),
                    c.getAncho(), c.getAlto(), c.getAreaVidrio(),
                    c.getMetrosAluminio(), c.getMetrosMetal(), c.getSubtotal(),
                    c.getAlertaCompra() == null ? "OK" : "COMPRA REQ."
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
