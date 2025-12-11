/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package frontend;

import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;
import java.sql.SQLException;
import backend.Puente_Sql_Java;

/**
 *
 * @author royum
 */
public class Login extends JFrame
{
    
    private JTextField txtNombre;
    private JTextField txtCorreo;
    private final backend.Puente_Sql_Java p;

    public Login()
    {
        p=new backend.Puente_Sql_Java();
        initUI();
    }
    
   

    private void initUI()
    {
        setTitle("Presupuesto Personal - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(430, 520);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel fondo = new JPanel();
        fondo.setBackground(new Color(245, 247, 255));
        fondo.setLayout(new GridBagLayout());

        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        // Icono tipo "dinero"
        JPanel iconPanel = new JPanel()
        {
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
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 26));
                String s = "$";
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(s)) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 4;
                g2.drawString(s, x, y);
                g2.dispose();
            }
        };
        iconPanel.setPreferredSize(new Dimension(70, 70));
        iconPanel.setMaximumSize(new Dimension(70, 70));
        iconPanel.setOpaque(false);
        iconPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(iconPanel);
        card.add(Box.createVerticalStrut(15));

        JLabel lblTitulo = new JLabel("Presupuesto Personal");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblTitulo);

        JLabel lblSub = new JLabel("Inicia sesión en tu cuenta");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(120, 120, 120));
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblSub);
        card.add(Box.createVerticalStrut(20));

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 0, 5, 0);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.weightx = 1;

        // Nombre de usuario
        c.gridy = 0;
        form.add(crearLabel("Nombre de Usuario"), c);
        c.gridy = 1;
        txtNombre = crearTextField("Tu nombre");
        form.add(txtNombre, c);

        // Correo
        c.gridy = 2;
        form.add(crearLabel("Correo Electrónico"), c);
        c.gridy = 3;
        txtCorreo = crearTextField("tu@email.com");
        form.add(txtCorreo, c);

        card.add(form);
        card.add(Box.createVerticalStrut(20));

        // Botón Iniciar sesión
        JButton btnLogin = new JButton("➡  Iniciar Sesión") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0,
                        new Color(130, 88, 255),
                        getWidth(), getHeight(),
                        new Color(178, 67, 255));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnLogin.setContentAreaFilled(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setFocusPainted(false);
        btnLogin.setPreferredSize(new Dimension(200, 45));
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(this::onLogin);

        card.add(btnLogin);
        card.add(Box.createVerticalStrut(20));

        // Texto "¿No tienes cuenta? Crear una aquí"
        JPanel panelLink = new JPanel();
        panelLink.setOpaque(false);
        JLabel lblPregunta = new JLabel("¿No tienes cuenta? ");
        lblPregunta.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JButton btnCrear = new JButton("Crea una aquí");
        btnCrear.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCrear.setForeground(new Color(130, 88, 255));
        btnCrear.setContentAreaFilled(false);
        btnCrear.setBorderPainted(false);
        btnCrear.setFocusPainted(false);
        btnCrear.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCrear.addActionListener((ActionEvent e) -> {
            abrirRegistro();
        });

        panelLink.add(lblPregunta);
        panelLink.add(btnCrear);
        card.add(panelLink);

        GridBagConstraints rootC = new GridBagConstraints();
        rootC.gridx = 0;
        rootC.gridy = 0;
        rootC.anchor = GridBagConstraints.CENTER;
        fondo.add(card, rootC);

        setContentPane(fondo);
    }

    private JLabel crearLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return lbl;
    }

    private JTextField crearTextField(String placeholder) {
        JTextField txt = new JTextField();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        txt.setToolTipText(placeholder);
        return txt;
    }

    private void onLogin(ActionEvent e) {
    String nombre = txtNombre.getText().trim();
    String correo = txtCorreo.getText().trim();

    if (nombre.isEmpty() || correo.isEmpty()) {
        JOptionPane.showMessageDialog(this,
                "Debes ingresar tu nombre de usuario y correo electrónico.",
                "Validación",
                JOptionPane.WARNING_MESSAGE);
        return;
    }

        try {
            backend.Usuario u = p.BuscarUsuarioPorNombreYCorreo(nombre, correo);

            if (u == null) {
                JOptionPane.showMessageDialog(this,
                        "Usuario o correo incorrectos",
                        "Error de inicio de sesión",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

// 🔹 Reactivar siempre que el login sea correcto
            try {
                p.ReactivarUsuario(u.getId(), u.getCorreo());
            } catch (SQLException exReac) {
                JOptionPane.showMessageDialog(this,
                        "No se pudo reactivar la cuenta automáticamente:\n" + exReac.getMessage(),
                        "Aviso", JOptionPane.WARNING_MESSAGE);
            }

            JOptionPane.showMessageDialog(this,
                    "Bienvenido, " + u.getNombre() + ".",
                    "Inicio de sesión exitoso",
                    JOptionPane.INFORMATION_MESSAGE);

            abrirMenuPrincipal(u);
            dispose();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Ocurrió un error al conectarse a la base de datos:\n" + ex.getMessage(),
                    "Error de inicio de sesión",
                JOptionPane.ERROR_MESSAGE);
    }
}



    private void abrirRegistro() {
        dispose();
        new RegistroUsuario().setVisible(true);
    }

    private void abrirMenuPrincipal(backend.Usuario usuario) 
    {
        dispose();
        new Menu(usuario).setVisible(true);
    }

    // Punto de entrada de la app
    public static void main(String[] args) {
        // Opcional: Look & Feel de sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        EventQueue.invokeLater(() -> {
            new Login().setVisible(true);
        });
    }
}
