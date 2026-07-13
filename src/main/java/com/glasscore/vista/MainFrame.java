package com.glasscore.vista;

import com.glasscore.conexion.ConexionDB;
import com.glasscore.util.FontUtil;
import com.glasscore.util.LogoUtil;
import com.glasscore.util.UITheme;
import com.glasscore.util.WidgetsOnline;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.Connection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class MainFrame extends JFrame {

    private final CardLayout cards = new CardLayout();
    private final JPanel content = new JPanel(cards);
    private final PanelHerramientas panelHerramientas = new PanelHerramientas();
    private final PanelCotizaciones panelCotizaciones = new PanelCotizaciones();
    private final PanelPlanilla panelPlanilla = new PanelPlanilla();
    private final PanelLogistica panelLogistica = new PanelLogistica();

    private final JLabel lblClima = new JLabel("Cargando clima…");
    private final JLabel lblDivisas = new JLabel("Cargando divisas…");

    public MainFrame() {
        super("GlassCore Logistics - Vidrieria Industrial");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1180, 760));
        setLocationRelativeTo(null);
        aplicarIconosVentana();

        JPanel root = new JPanel(new BorderLayout());
        root.add(crearSidebar(), BorderLayout.WEST);
        root.add(crearHeader(), BorderLayout.NORTH);

        content.setBackground(UITheme.BG);
        content.add(crearHome(), "HOME");
        content.add(panelHerramientas, "HERRAMIENTAS");
        content.add(panelCotizaciones, "COTIZACIONES");
        content.add(panelPlanilla, "PLANILLA");
        content.add(panelLogistica, "LOGISTICA");
        root.add(content, BorderLayout.CENTER);

        setContentPane(root);
        cards.show(content, "HOME");
        cargarWidgets();
    }

    private void aplicarIconosVentana() {
        List<java.awt.Image> icons = LogoUtil.iconImages();
        if (!icons.isEmpty()) {
            setIconImages(icons);
        }
    }

    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.PRIMARY);
        header.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel brand = new JLabel("GlassCore");
        brand.setFont(FontUtil.title(28f));
        brand.setForeground(Color.WHITE);
        header.add(brand, BorderLayout.WEST);

        JLabel status = new JLabel(probarConexion(), SwingConstants.RIGHT);
        status.setForeground(new Color(0xD0, 0xD6, 0xE8));
        status.setFont(FontUtil.ui(13));
        header.add(status, BorderLayout.EAST);
        return header;
    }

    private String probarConexion() {
        try (Connection cn = ConexionDB.getConnection()) {
            return "MySQL: conectado";
        } catch (Exception e) {
            return "MySQL: sin conexion - revise db.properties";
        }
    }

    private JPanel crearSidebar() {
        JPanel side = new JPanel(new GridLayout(0, 1, 0, 8));
        side.setBackground(UITheme.SIDEBAR);
        side.setBorder(new EmptyBorder(20, 12, 20, 12));
        side.setPreferredSize(new Dimension(220, 0));

        side.add(navBtn("Inicio", "HOME", null));
        side.add(navBtn("Herramientas", "HERRAMIENTAS", panelHerramientas::refrescar));
        side.add(navBtn("Cotizaciones", "COTIZACIONES", panelCotizaciones::refrescar));
        side.add(navBtn("Empleados / Planilla", "PLANILLA", panelPlanilla::refrescar));
        side.add(navBtn("Logística", "LOGISTICA", panelLogistica::refrescar));

        JButton salir = UITheme.dangerButton("Salir");
        salir.addActionListener(e -> dispose());
        side.add(salir);
        return side;
    }

    private JButton navBtn(String texto, String card, Runnable onShow) {
        JButton b = UITheme.navButton(texto);
        b.addActionListener(e -> {
            cards.show(content, card);
            if (onShow != null) {
                onShow.run();
            }
        });
        return b;
    }

    private JPanel crearHome() {
        JPanel home = new JPanel(new BorderLayout(16, 16));
        home.setBackground(UITheme.BG);
        home.setBorder(new EmptyBorder(20, 32, 24, 32));

        JPanel hero = new JPanel(new BorderLayout(0, 10));
        hero.setOpaque(false);

        JPanel logoWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        logoWrap.setOpaque(false);
        ImageIcon logo = LogoUtil.scaled(220, 220);
        if (logo != null) {
            logoWrap.add(new JLabel(logo));
        }
        hero.add(logoWrap, BorderLayout.CENTER);

        JLabel hint = new JLabel("Seleccione un módulo en el menú lateral.", SwingConstants.CENTER);
        hint.setFont(FontUtil.body(15));
        hint.setForeground(UITheme.MUTED);
        hero.add(hint, BorderLayout.SOUTH);
        home.add(hero, BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(0, 16));
        centro.setOpaque(false);

        JPanel misionVision = new JPanel(new GridLayout(1, 2, 16, 16));
        misionVision.setOpaque(false);
        misionVision.add(textoInstitucional(
                "Misión",
                "Diseñar, fabricar e instalar soluciones de vidrio, aluminio y metal "
                + "con precisión y seguridad, acompañando a nuestros clientes desde la "
                + "cotización hasta el despacho e instalación en campo."));
        misionVision.add(textoInstitucional(
                "Visión",
                "Ser la vidriería industrial de referencia en Honduras por la calidad "
                + "de sus productos, la trazabilidad de sus operaciones y un servicio "
                + "logístico confiable entre Tegucigalpa y el interior del país."));
        centro.add(misionVision, BorderLayout.NORTH);

        JPanel widgets = new JPanel(new GridLayout(1, 2, 16, 16));
        widgets.setOpaque(false);
        widgets.add(widgetClima());
        widgets.add(widgetDivisas());
        centro.add(widgets, BorderLayout.CENTER);

        home.add(centro, BorderLayout.CENTER);
        return home;
    }

    private JPanel textoInstitucional(String titulo, String cuerpo) {
        JPanel p = UITheme.card();
        p.setLayout(new BorderLayout(8, 10));
        JLabel t = new JLabel(titulo);
        t.setFont(FontUtil.title(24f));
        t.setForeground(UITheme.PRIMARY);
        JLabel b = new JLabel("<html><body style='width:280px'>" + cuerpo + "</body></html>");
        b.setFont(FontUtil.body(14));
        b.setForeground(UITheme.TEXT);
        p.add(t, BorderLayout.NORTH);
        p.add(b, BorderLayout.CENTER);
        return p;
    }

    private JPanel widgetClima() {
        JPanel p = UITheme.card();
        p.setLayout(new BorderLayout(8, 10));
        JLabel t = new JLabel("Clima y ubicación");
        t.setFont(FontUtil.title(20f));
        t.setForeground(UITheme.PRIMARY);
        lblClima.setFont(FontUtil.body(14));
        lblClima.setVerticalAlignment(SwingConstants.TOP);
        JButton btn = UITheme.accentButton("Actualizar clima");
        btn.addActionListener(e -> cargarWidgets());
        p.add(t, BorderLayout.NORTH);
        p.add(lblClima, BorderLayout.CENTER);
        p.add(btn, BorderLayout.SOUTH);
        return p;
    }

    private JPanel widgetDivisas() {
        JPanel p = UITheme.card();
        p.setLayout(new BorderLayout(8, 10));
        JLabel t = new JLabel("Tipo de cambio (base USD)");
        t.setFont(FontUtil.title(20f));
        t.setForeground(UITheme.PRIMARY);
        lblDivisas.setFont(FontUtil.body(14));
        lblDivisas.setVerticalAlignment(SwingConstants.TOP);
        JButton btn = UITheme.accentButton("Actualizar divisas");
        btn.addActionListener(e -> cargarWidgets());
        p.add(t, BorderLayout.NORTH);
        p.add(lblDivisas, BorderLayout.CENTER);
        p.add(btn, BorderLayout.SOUTH);
        return p;
    }

    private void cargarWidgets() {
        lblClima.setText("<html>Consultando clima en Tegucigalpa…</html>");
        lblDivisas.setText("<html>Consultando tipos de cambio…</html>");

        CompletableFuture.supplyAsync(() -> {
            try {
                return WidgetsOnline.obtenerClimaTegucigalpa();
            } catch (Exception e) {
                return e;
            }
        }).thenAccept(result -> SwingUtilities.invokeLater(() -> {
            if (result instanceof WidgetsOnline.ClimaInfo c) {
                lblClima.setText("<html><b>" + c.ubicacion + "</b><br>"
                        + c.condicion + "<br>"
                        + "Temperatura: " + WidgetsOnline.fmt(c.temperaturaC) + " °C<br>"
                        + "Humedad: " + c.humedad + "%<br>"
                        + "Viento: " + WidgetsOnline.fmt(c.vientoKmh) + " km/h</html>");
            } else {
                Exception e = (Exception) result;
                lblClima.setText("<html>No se pudo cargar el clima.<br>"
                        + e.getMessage() + "<br>Verifique su conexión a Internet.</html>");
            }
        }));

        CompletableFuture.supplyAsync(() -> {
            try {
                return WidgetsOnline.obtenerDivisas();
            } catch (Exception e) {
                return e;
            }
        }).thenAccept(result -> SwingUtilities.invokeLater(() -> {
            if (result instanceof WidgetsOnline.DivisaInfo d) {
                lblDivisas.setText("<html>"
                        + "1 USD = <b>Lps " + WidgetsOnline.fmt(d.usdToHnl) + "</b><br>"
                        + "1 USD = € " + WidgetsOnline.fmt(d.usdToEur) + "<br>"
                        + "1 USD = Q " + WidgetsOnline.fmt(d.usdToGtq) + "<br><br>"
                        + "<span style='color:#666;font-size:11px;'>Actualizado: "
                        + d.actualizado + "</span></html>");
            } else {
                Exception e = (Exception) result;
                lblDivisas.setText("<html>No se pudieron cargar las divisas.<br>"
                        + e.getMessage() + "<br>Verifique su conexión a Internet.</html>");
            }
        }));
    }

    public static void launch() {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            try (Connection cn = ConexionDB.getConnection()) {
                // conexion OK
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "No se pudo conectar a MySQL.\n\n"
                        + "1) Ejecute sql/glasscore_db.sql en MySQL Workbench\n"
                        + "2) Configure usuario/clave en src/main/resources/db.properties\n\n"
                        + e.getMessage(),
                        "GlassCore - Conexion", JOptionPane.ERROR_MESSAGE);
            }
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
