/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package frontend;

import backend.Usuario;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.*;

/**
 *
 * @author royum
 */
public class Menu extends JFrame 
{
   
    private final Usuario usuario;
    private JPanel cardPanel;
    private CardLayout cardLayout;

    public Menu(Usuario usuario) {
        this.usuario = usuario;
        initUI();
    }

    private void initUI() {
        setTitle("Presupuesto Personal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 720);
        setLocationRelativeTo(null);
        setResizable(false);

        getContentPane().setLayout(new BorderLayout());

        JPanel sidebar = crearSidebar();
        JPanel main = crearMainContent();

        getContentPane().add(sidebar, BorderLayout.WEST);
        getContentPane().add(main, BorderLayout.CENTER);
    }

    // ==========================================================
    //  SIDEBAR
    // ==========================================================
    private JPanel crearSidebar() {
        JPanel side = new JPanel();
        side.setPreferredSize(new Dimension(240, getHeight()));
        side.setBackground(Color.WHITE);
        side.setLayout(new BorderLayout());

        // --- Logo / título ---
        JPanel panelLogo = new JPanel();
        panelLogo.setBackground(Color.WHITE);
        panelLogo.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelLogo.setLayout(new BoxLayout(panelLogo, BoxLayout.X_AXIS));

        JPanel iconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0,
                        new Color(130, 88, 255),
                        getWidth(), getHeight(),
                        new Color(178, 67, 255));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 20));
                String s = "$";
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(s)) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 4;
                g2.drawString(s, x, y);
                g2.dispose();
            }
        };
        iconPanel.setPreferredSize(new Dimension(40, 40));
        iconPanel.setMaximumSize(new Dimension(40, 40));
        iconPanel.setOpaque(false);

        JPanel txtLogo = new JPanel();
        txtLogo.setBackground(Color.WHITE);
        txtLogo.setLayout(new BoxLayout(txtLogo, BoxLayout.Y_AXIS));
        JLabel lblTitulo = new JLabel("Presupuesto");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JLabel lblSub = new JLabel("Personal");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtLogo.add(lblTitulo);
        txtLogo.add(lblSub);

        panelLogo.add(iconPanel);
        panelLogo.add(Box.createHorizontalStrut(10));
        panelLogo.add(txtLogo);

        side.add(panelLogo, BorderLayout.NORTH);

        // --- Navegación ---
        JPanel nav = new JPanel();
        nav.setBackground(Color.WHITE);
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        nav.add(crearNavButton("📊  Resumen", "resumen", false));
        nav.add(crearNavButton("🏷️  Categorías", "categorias", false));
        nav.add(crearNavButton("📅  Presupuesto", "presupuesto", false));
        nav.add(crearNavButton("💸  Transacciones", "transacciones", false));
        nav.add(crearNavButton("📆  Obligaciones Fijas", "obligaciones", false));
        nav.add(crearNavButton("🎯  Metas de Ahorro", "metas", false));
        nav.add(Box.createVerticalStrut(10));
        nav.add(crearNavButton("👤  Perfil", "perfil", true));  // seleccionado

        side.add(nav, BorderLayout.CENTER);

        // --- Tarjeta de usuario abajo ---
        JPanel userPanel = new JPanel();
        userPanel.setBackground(Color.WHITE);
        userPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        userPanel.setLayout(new BorderLayout());

        JPanel card = new JPanel();
        card.setBackground(new Color(247, 248, 252));
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        card.setLayout(new BoxLayout(card, BoxLayout.X_AXIS));

        String iniciales = obtenerInicialesUsuario();
        JLabel lblIni = new JLabel(iniciales, SwingConstants.CENTER);
        lblIni.setOpaque(true);
        lblIni.setBackground(new Color(130, 88, 255));
        lblIni.setForeground(Color.WHITE);
        lblIni.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblIni.setPreferredSize(new Dimension(32, 32));
        lblIni.setMaximumSize(new Dimension(32, 32));
        lblIni.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel datos = new JPanel();
        datos.setOpaque(false);
        datos.setLayout(new BoxLayout(datos, BoxLayout.Y_AXIS));

        JLabel lblNombre = new JLabel(usuario.getNombre() + " " + usuario.getApellido());
        lblNombre.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JLabel lblCorreo = new JLabel(usuario.getCorreo());
        lblCorreo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblCorreo.setForeground(new Color(120, 120, 120));

        datos.add(lblNombre);
        datos.add(lblCorreo);

        card.add(lblIni);
        card.add(Box.createHorizontalStrut(8));
        card.add(datos);

        userPanel.add(card, BorderLayout.CENTER);
        side.add(userPanel, BorderLayout.SOUTH);

        return side;
    }

    private JButton crearNavButton(String texto, String cardName, boolean seleccionado) {
        JButton btn = new JButton(texto);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(8, 14, 8, 14));
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        if (seleccionado) {
            btn.setForeground(Color.WHITE);
            btn.setContentAreaFilled(true);
            btn.setBackground(new Color(130, 88, 255));
        } else {
            btn.setForeground(new Color(60, 60, 60));
            btn.setBackground(new Color(248, 249, 252));
            btn.setContentAreaFilled(true);
        }

        btn.addActionListener(e -> cardLayout.show(cardPanel, cardName));

        return btn;
    }

    private String obtenerInicialesUsuario() {
        String n = usuario.getNombre() != null ? usuario.getNombre().trim() : "";
        String a = usuario.getApellido() != null ? usuario.getApellido().trim() : "";
        char c1 = n.isEmpty() ? 'U' : Character.toUpperCase(n.charAt(0));
        char c2 = a.isEmpty() ? 'S' : Character.toUpperCase(a.charAt(0));
        return "" + c1 + c2;
    }

    // ==========================================================
    //  MAIN CONTENT (CardLayout)
    // ==========================================================
    private JPanel crearMainContent() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(new Color(245, 247, 255));

        JPanel header = crearHeader();
        main.add(header, BorderLayout.NORTH);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(new Color(245, 247, 255));

        // Perfil con scroll
        JScrollPane scrollPerfil = new JScrollPane(crearPerfilPanel());
        scrollPerfil.setBorder(null);
        scrollPerfil.getVerticalScrollBar().setUnitIncrement(16);
        scrollPerfil.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        cardPanel.add(scrollPerfil, "perfil");
        cardPanel.add(crearPlaceholderPanel("Resumen"), "resumen");
        cardPanel.add(crearPlaceholderPanel("Categorías"), "categorias");
        cardPanel.add(crearPlaceholderPanel("Presupuesto"), "presupuesto");
        cardPanel.add(crearPlaceholderPanel("Transacciones"), "transacciones");
        cardPanel.add(crearPlaceholderPanel("Obligaciones Fijas"), "obligaciones");
        cardPanel.add(crearPlaceholderPanel("Metas de Ahorro"), "metas");

        main.add(cardPanel, BorderLayout.CENTER);
        cardLayout.show(cardPanel, "perfil");

        return main;
    }

    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(245, 247, 255));
        header.setBorder(BorderFactory.createEmptyBorder(15, 25, 10, 25));

        JLabel lblTitulo = new JLabel("Perfil");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        JLabel lblNom = new JLabel(usuario.getNombre() + " " + usuario.getApellido());
        lblNom.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblNom.setHorizontalAlignment(SwingConstants.RIGHT);

        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("es", "HN"));
        String salario = usuario.getSalario() != null ? nf.format(usuario.getSalario()) : "L. 0.00";
        JLabel lblSal = new JLabel(salario);
        lblSal.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSal.setForeground(new Color(120, 120, 120));
        lblSal.setHorizontalAlignment(SwingConstants.RIGHT);

        info.add(lblNom);
        info.add(lblSal);

        header.add(lblTitulo, BorderLayout.WEST);
        header.add(info, BorderLayout.EAST);

        return header;
    }

    // ==========================================================
    //  PANEL PERFIL
    // ==========================================================
    private JPanel crearPerfilPanel() {
        JPanel perfil = new JPanel();
        perfil.setBackground(new Color(245, 247, 255));
        perfil.setBorder(BorderFactory.createEmptyBorder(0, 25, 25, 25));
        perfil.setLayout(new BoxLayout(perfil, BoxLayout.Y_AXIS));

        // --- Tarjeta superior "Mi Perfil" ---
        JPanel cardPerfil = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0,
                        new Color(130, 88, 255),
                        getWidth(), getHeight(),
                        new Color(178, 67, 255));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                g2.dispose();
            }
        };
        cardPerfil.setOpaque(false);
        cardPerfil.setLayout(new BorderLayout());
        cardPerfil.setPreferredSize(new Dimension(0, 140));
        cardPerfil.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        cardPerfil.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        String ini = obtenerInicialesUsuario();
        JLabel lblCirc = new JLabel(ini, SwingConstants.CENTER);
        lblCirc.setOpaque(true);
        lblCirc.setBackground(new Color(135, 112, 255));
        lblCirc.setForeground(Color.WHITE);
        lblCirc.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblCirc.setPreferredSize(new Dimension(70, 70));
        lblCirc.setMaximumSize(new Dimension(70, 70));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.X_AXIS));
        left.add(lblCirc);
        left.add(Box.createHorizontalStrut(15));

        JPanel datos = new JPanel();
        datos.setOpaque(false);
        datos.setLayout(new BoxLayout(datos, BoxLayout.Y_AXIS));

        JLabel lblMiPerfil = new JLabel("Mi Perfil");
        lblMiPerfil.setForeground(Color.WHITE);
        lblMiPerfil.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JLabel lblCorreoTop = new JLabel(usuario.getCorreo());
        lblCorreoTop.setForeground(Color.WHITE);
        lblCorreoTop.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        datos.add(lblMiPerfil);
        datos.add(lblCorreoTop);

        left.add(datos);
        cardPerfil.add(left, BorderLayout.WEST);

        perfil.add(cardPerfil);
        perfil.add(Box.createVerticalStrut(15));

        // --- Información personal ---
        JPanel infoPersonal = new JPanel();
        infoPersonal.setBackground(Color.WHITE);
        infoPersonal.setBorder(BorderFactory.createEmptyBorder(20, 25, 25, 25));
        infoPersonal.setLayout(new BoxLayout(infoPersonal, BoxLayout.Y_AXIS));

        JPanel headerInfo = new JPanel(new BorderLayout());
        headerInfo.setOpaque(false);
        JLabel lblInfo = new JLabel("Información Personal");
        lblInfo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        JButton btnEditar = new JButton("Editar Perfil");
        btnEditar.setBorderPainted(false);
        btnEditar.setContentAreaFilled(false);
        btnEditar.setForeground(new Color(130, 88, 255));
        btnEditar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnEditar.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Funcionalidad de edición de perfil pendiente de implementar.",
                "Info", JOptionPane.INFORMATION_MESSAGE));

        headerInfo.add(lblInfo, BorderLayout.WEST);
        headerInfo.add(btnEditar, BorderLayout.EAST);

        infoPersonal.add(headerInfo);
        infoPersonal.add(Box.createVerticalStrut(10));

        // 🔹 Panel que centra los campos
        JPanel panelCampos = new JPanel();
        panelCampos.setOpaque(false);
        panelCampos.setLayout(new BoxLayout(panelCampos, BoxLayout.Y_AXIS));
        panelCampos.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelCampos.setMaximumSize(new Dimension(800, Integer.MAX_VALUE)); // ancho máximo
        panelCampos.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Fila Nombre / Apellido
        JPanel fila1 = new JPanel(new GridLayout(1, 2, 15, 0));
        fila1.setOpaque(false);
        fila1.setAlignmentX(Component.CENTER_ALIGNMENT);
        fila1.add(crearCampoInfo("Nombre", usuario.getNombre()));
        fila1.add(crearCampoInfo("Apellido", usuario.getApellido()));

        panelCampos.add(fila1);
        panelCampos.add(Box.createVerticalStrut(10));

        // Correo ancho completo dentro de panelCampos
        JPanel panelCorreo = crearCampoInfo("Correo Electrónico", usuario.getCorreo());
        panelCorreo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelCampos.add(panelCorreo);
        panelCampos.add(Box.createVerticalStrut(10));

        // Salario ancho completo dentro de panelCampos
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("es", "HN"));
        String salarioTxt = usuario.getSalario() != null ? nf.format(usuario.getSalario()) : "L. 0.00";
        JPanel panelSalario = crearCampoInfo("Salario Mensual", salarioTxt);
        panelSalario.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelCampos.add(panelSalario);

        infoPersonal.add(panelCampos);
        perfil.add(infoPersonal);
        perfil.add(Box.createVerticalStrut(15));

        // --- Estadísticas de uso ---
        JPanel estadisticas = new JPanel();
        estadisticas.setBackground(Color.WHITE);
        estadisticas.setBorder(BorderFactory.createEmptyBorder(20, 25, 25, 25));
        estadisticas.setLayout(new BoxLayout(estadisticas, BoxLayout.Y_AXIS));

        JLabel lblEst = new JLabel("Estadísticas de Uso");
        lblEst.setFont(new Font("Segoe UI", Font.BOLD, 15));
        estadisticas.add(lblEst);
        estadisticas.add(Box.createVerticalStrut(10));

        JPanel filaStats = new JPanel(new GridLayout(1, 3, 15, 0));
        filaStats.setOpaque(false);
        filaStats.add(crearStatCard("Presupuestos Creados", "0"));
        filaStats.add(crearStatCard("Transacciones Registradas", "0"));
        filaStats.add(crearStatCard("Metas de Ahorro", "0"));

        estadisticas.add(filaStats);
        perfil.add(estadisticas);
        perfil.add(Box.createVerticalStrut(15));

        // --- Gestión de cuenta ---
        JPanel gestion = new JPanel();
        gestion.setBackground(Color.WHITE);
        gestion.setBorder(BorderFactory.createEmptyBorder(20, 25, 25, 25));
        gestion.setLayout(new BoxLayout(gestion, BoxLayout.Y_AXIS));

        JLabel lblGest = new JLabel("Gestión de Cuenta");
        lblGest.setFont(new Font("Segoe UI", Font.BOLD, 15));
        gestion.add(lblGest);
        gestion.add(Box.createVerticalStrut(10));

        JPanel wrapCerrar = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapCerrar.setOpaque(false);

        JButton btnCerrarSesion = new JButton("Cerrar Sesión");
        btnCerrarSesion.setFocusPainted(false);
        btnCerrarSesion.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        btnCerrarSesion.setBackground(new Color(245, 245, 245));
        btnCerrarSesion.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCerrarSesion.setHorizontalAlignment(SwingConstants.CENTER);
        btnCerrarSesion.setPreferredSize(new Dimension(260, 40));
        btnCerrarSesion.addActionListener(e -> {
            int r = JOptionPane.showConfirmDialog(this,
                    "¿Deseas cerrar sesión?",
                    "Confirmar",
                    JOptionPane.YES_NO_OPTION);
            if (r == JOptionPane.YES_OPTION) {
                dispose();
                new Login().setVisible(true);
            }
        });

        wrapCerrar.add(btnCerrarSesion);
        gestion.add(wrapCerrar);
        gestion.add(Box.createVerticalStrut(8));

        JPanel wrapDesactivar = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapDesactivar.setOpaque(false);

        JButton btnDesactivar = new JButton("Desactivar Cuenta");
        btnDesactivar.setFocusPainted(false);
        btnDesactivar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        btnDesactivar.setBackground(new Color(255, 235, 235));
        btnDesactivar.setForeground(new Color(200, 40, 40));
        btnDesactivar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnDesactivar.setHorizontalAlignment(SwingConstants.CENTER);
        btnDesactivar.setPreferredSize(new Dimension(260, 40));
        btnDesactivar.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "La opción de desactivar cuenta se implementará con el procedimiento sp_eliminar_usuario.",
                "Pendiente", JOptionPane.INFORMATION_MESSAGE));

        wrapDesactivar.add(btnDesactivar);
        gestion.add(wrapDesactivar);

        perfil.add(gestion);

        return perfil;
    }

    // ======================= Helpers ===========================
    private JPanel crearCampoInfo(String etiqueta, String valor) {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(248, 249, 252));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel lblEt = new JLabel(etiqueta);
        lblEt.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblEt.setForeground(new Color(120, 120, 120));

        JLabel lblVal = new JLabel(valor != null ? valor : "");
        lblVal.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        panel.add(lblEt);
        panel.add(Box.createVerticalStrut(3));
        panel.add(lblVal);

        Dimension pref = panel.getPreferredSize();
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, pref.height));

        return panel;
    }

    private JPanel crearStatCard(String titulo, String valor) {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(245, 247, 255));

        JLabel lblVal = new JLabel(valor);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTit.setForeground(new Color(120, 120, 120));

        panel.add(lblVal);
        panel.add(Box.createVerticalStrut(4));
        panel.add(lblTit);

        return panel;
    }

    private JPanel crearPlaceholderPanel(String titulo) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 247, 255));
        JLabel lbl = new JLabel(titulo + " (en construcción)", SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        panel.add(lbl, BorderLayout.CENTER);
        return panel;
    }
}