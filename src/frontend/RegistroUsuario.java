/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package frontend;

import backend.Puente_Sql_Java;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.sql.SQLException;
import javax.swing.*;
import backend.Usuario;

/**
 *
 * @author royum
 */
public class RegistroUsuario extends JFrame
{

    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtCorreo;
    private JTextField txtSalario;
    private final Puente_Sql_Java puente;

    public RegistroUsuario() 
    {
        this.puente = new Puente_Sql_Java();
        initUI();
    }

    private void initUI() {
        setTitle("Crear Cuenta - Presupuesto Personal");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(690, 750);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel fondo = new JPanel();
        fondo.setBackground(new Color(245, 247, 255));
        fondo.setLayout(new GridBagLayout());

        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        // Botón "volver"
        JButton btnVolver = new JButton("←  Volver al inicio de sesión");
        btnVolver.setBorderPainted(false);
        btnVolver.setContentAreaFilled(false);
        btnVolver.setFocusPainted(false);
        btnVolver.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnVolver.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnVolver.addActionListener((ActionEvent e) -> {
            abrirLogin();
        });
        card.add(btnVolver);
        card.add(Box.createVerticalStrut(15));

        //Icono circular simple
        JPanel iconPanel=new JPanel() 
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
                FontMetrics fm = g2.getFontMetrics();
                String s = "👤";
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

        JLabel lblTitulo = new JLabel("Crear Cuenta");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblTitulo);

        JLabel lblSub = new JLabel("Completa tus datos para comenzar");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(120, 120, 120));
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblSub);
        card.add(Box.createVerticalStrut(20));

        // Panel de formulario
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 0, 5, 0);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.weightx = 1;

        // Nombre
        c.gridy = 0;
        form.add(crearLabel("Nombre"), c);
        c.gridy = 1;
        txtNombre = crearTextField("Juan");
        form.add(txtNombre, c);

        // Apellido
        c.gridy = 2;
        form.add(crearLabel("Apellido"), c);
        c.gridy = 3;
        txtApellido = crearTextField("Pérez");
        form.add(txtApellido, c);

        // Correo
        c.gridy = 4;
        form.add(crearLabel("Correo Electrónico"), c);
        c.gridy = 5;
        txtCorreo = crearTextField("tu@email.com");
        form.add(txtCorreo, c);

        // Salario
        c.gridy = 6;
        form.add(crearLabel("Salario Mensual"), c);
        c.gridy = 7;
        txtSalario = crearTextField("15000.00");
        form.add(txtSalario, c);

        card.add(form);
        card.add(Box.createVerticalStrut(20));

        // Botón crear cuenta (con gradiente sencillo)
        JButton btnCrear = new JButton("👤  Crear Cuenta") {
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
        btnCrear.setForeground(Color.WHITE);
        btnCrear.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnCrear.setContentAreaFilled(false);
        btnCrear.setBorderPainted(false);
        btnCrear.setFocusPainted(false);
        btnCrear.setPreferredSize(new Dimension(200, 45));
        btnCrear.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnCrear.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCrear.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnCrear.addActionListener(this::onCrearCuenta);

        card.add(btnCrear);

        GridBagConstraints rootC = new GridBagConstraints();
        rootC.gridx = 0;
        rootC.gridy = 0;
        rootC.fill = GridBagConstraints.NONE;
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
        txt.setText("");
        txt.setToolTipText(placeholder);
        return txt;
    }
private void onCrearCuenta(ActionEvent e) {
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String correo = txtCorreo.getText().trim();
        String salarioStr = txtSalario.getText().trim();

    
        if (nombre.isEmpty() || apellido.isEmpty() || correo.isEmpty() || salarioStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Todos los campos son obligatorios.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        
        if (!esCorreoValido(correo)) {
            JOptionPane.showMessageDialog(this,
                    "Incluye un signo \"@\" en la dirección de correo electrónico.\n" +
                    "La dirección \"" + correo + "\" no incluye un formato válido.\n\n" +
                    "Ejemplo: usuario@correo.com",
                    "Correo electrónico no válido",
                    JOptionPane.WARNING_MESSAGE);
            txtCorreo.requestFocus();
            return;
        }

        BigDecimal salario;
        try {
            salario = new BigDecimal(salarioStr);
            if (salario.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(this,
                        "El salario debe ser mayor que 0.",
                        "Validación",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "El salario no tiene un formato válido.",
                    "Error de formato",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // 1) Insertar usuario
            puente.Insertar_usuario(nombre, apellido, correo, salario);

            //Buscar el usuario recien creado para pasarlo al menu
            Usuario u=puente.BuscarUsuarioPorNombreYCorreo(nombre, correo);

            if(u == null) 
            {
                
                JOptionPane.showMessageDialog(this,"La cuenta se creo, pero no se pudo recuperar la informacion del usuario.","Aviso",JOptionPane.WARNING_MESSAGE);
                abrirLogin();
                return;
            }

            JOptionPane.showMessageDialog(this,"Cuenta creada correctamente.\nBienvenido, " + u.getNombre() + ".","Éxito",JOptionPane.INFORMATION_MESSAGE);

            abrirMenuPrincipal(u);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Error al crear cuenta",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean esCorreoValido(String correo)
    {
        String regex = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";
        return correo.matches(regex);
    }

    private void abrirLogin() {
        dispose();
        new Login().setVisible(true);
    }

    private void abrirMenuPrincipal(Usuario usuario) {
        dispose();
        new Menu(usuario).setVisible(true);   
    }
}