package calculadora;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.Stack;

public class Calculadora extends JFrame {

    private JTextField vista_calculadora;
    private JLabel label;
    private JButton[] keys;

    public Calculadora() {
        initComponents();
        setSize(400, 500);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        vista_calculadora = new JTextField();
        label = new JLabel("Calculadora");

        String[] keyLabels = {"1", "2", "3", "+", "4", "5", "6", "-", "7", "8", "9", "*", "0", ".", "=", "/"};
        keys = new JButton[keyLabels.length];

        java.awt.Dimension buttonSize = new java.awt.Dimension(70, 70);

        for (int i = 0; i < keyLabels.length; i++) {
            keys[i] = new JButton(keyLabels[i]);
            keys[i].setPreferredSize(buttonSize);
        }

        setTitle("Calculadora");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new java.awt.BorderLayout());

        add(label, java.awt.BorderLayout.NORTH);
        add(vista_calculadora, java.awt.BorderLayout.CENTER);

        JPanel panel = new JPanel(new java.awt.GridLayout(4, 4));
        for (JButton key : keys) {
            panel.add(key);
        }
        add(panel, java.awt.BorderLayout.SOUTH);

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Opciones");
        JMenuItem nuevo = new JMenuItem("Nuevo");
        JMenuItem historial = new JMenuItem("Historial");
        JMenuItem ayuda = new JMenuItem("Ayuda");

        nuevo.addActionListener(e -> nuevo());
        historial.addActionListener(e -> mostrarHistorial());
        ayuda.addActionListener(e -> mostrarAyuda());

        menu.add(nuevo);
        menu.add(historial);
        menu.add(ayuda);
        menuBar.add(menu);
        setJMenuBar(menuBar);

        addActionListeners();
        vista_calculadora.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });

        pack();
    }

    private void addActionListeners() {
        for (JButton key : keys) {
            key.addActionListener(e -> {
                String text = ((JButton) e.getSource()).getText();
                if (text.equals("=")) {
                    calcularResultado();
                } else {
                    vista_calculadora.setText(vista_calculadora.getText() + text);
                }
            });
        }
    }

    private void handleKeyPress(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_0, KeyEvent.VK_NUMPAD0 -> vista_calculadora.setText(vista_calculadora.getText() + "0");
            case KeyEvent.VK_1, KeyEvent.VK_NUMPAD1 -> vista_calculadora.setText(vista_calculadora.getText() + "1");
            case KeyEvent.VK_2, KeyEvent.VK_NUMPAD2 -> vista_calculadora.setText(vista_calculadora.getText() + "2");
            case KeyEvent.VK_3, KeyEvent.VK_NUMPAD3 -> vista_calculadora.setText(vista_calculadora.getText() + "3");
            case KeyEvent.VK_4, KeyEvent.VK_NUMPAD4 -> vista_calculadora.setText(vista_calculadora.getText() + "4");
            case KeyEvent.VK_5, KeyEvent.VK_NUMPAD5 -> vista_calculadora.setText(vista_calculadora.getText() + "5");
            case KeyEvent.VK_6, KeyEvent.VK_NUMPAD6 -> vista_calculadora.setText(vista_calculadora.getText() + "6");
            case KeyEvent.VK_7, KeyEvent.VK_NUMPAD7 -> vista_calculadora.setText(vista_calculadora.getText() + "7");
            case KeyEvent.VK_8, KeyEvent.VK_NUMPAD8 -> vista_calculadora.setText(vista_calculadora.getText() + "8");
            case KeyEvent.VK_9, KeyEvent.VK_NUMPAD9 -> vista_calculadora.setText(vista_calculadora.getText() + "9");
            case KeyEvent.VK_ADD -> vista_calculadora.setText(vista_calculadora.getText() + "+");
            case KeyEvent.VK_SUBTRACT -> vista_calculadora.setText(vista_calculadora.getText() + "-");
            case KeyEvent.VK_MULTIPLY -> vista_calculadora.setText(vista_calculadora.getText() + "*");
            case KeyEvent.VK_DIVIDE -> vista_calculadora.setText(vista_calculadora.getText() + "/");
            case KeyEvent.VK_DECIMAL -> vista_calculadora.setText(vista_calculadora.getText() + ".");
            case KeyEvent.VK_ENTER -> calcularResultado();
            case KeyEvent.VK_BACK_SPACE -> {
                String text = vista_calculadora.getText();
                if (!text.isEmpty()) {
                    vista_calculadora.setText(text.substring(0, text.length() - 1));
                }
            }
        }
    }

    private void calcularResultado() {
        try {
            String expression = vista_calculadora.getText();
            double result = evaluateExpression(expression);
            vista_calculadora.setText(String.valueOf(result));
            guardarHistorial(expression + " = " + result);
        } catch (Exception e) {
            vista_calculadora.setText("Error");
        }
    }

    private double evaluateExpression(String expression) {
        Stack<Double> numbers = new Stack<>();
        Stack<Character> operations = new Stack<>();
        StringBuilder numberBuffer = new StringBuilder();

        for (char c : expression.toCharArray()) {
            if (Character.isDigit(c) || c == '.') {
                numberBuffer.append(c);
            } else {
                if (numberBuffer.length() > 0) {
                    numbers.push(Double.parseDouble(numberBuffer.toString()));
                    numberBuffer.setLength(0);
                }
                while (!operations.isEmpty() && precedence(c) <= precedence(operations.peek())) {
                    double b = numbers.pop();
                    double a = numbers.pop();
                    char op = operations.pop();
                    numbers.push(applyOperation(a, b, op));
                }
                operations.push(c);
            }
        }
        if (numberBuffer.length() > 0) {
            numbers.push(Double.parseDouble(numberBuffer.toString()));
        }
        while (!operations.isEmpty()) {
            double b = numbers.pop();
            double a = numbers.pop();
            char op = operations.pop();
            numbers.push(applyOperation(a, b, op));
        }
        return numbers.pop();
    }

    private int precedence(char op) {
        return switch (op) {
            case '+', '-' -> 1;
            case '*', '/' -> 2;
            default -> -1;
        };
    }

    private double applyOperation(double a, double b, char op) {
        return switch (op) {
            case '+' -> a + b;
            case '-' -> a - b;
            case '*' -> a * b;
            case '/' -> {
                if (b == 0) throw new ArithmeticException("Division by zero");
                yield a / b;
            }
            default -> 0;
        };
    }

    private void nuevo() {
        vista_calculadora.setText("");
    }

    private void mostrarHistorial() {
        try {
            File file = new File("historial.txt");
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                JTextArea textArea = new JTextArea();
                textArea.read(reader, null);
                reader.close();

                JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "Historial", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No hay historial disponible.", "Historial", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mostrarAyuda() {
        String mensaje = """
                Calculadora simple.

                Uso:
                - Ingrese números y operadores usando los botones o el teclado.
                - Presione '=' para calcular el resultado.
                - Use la opción 'Nuevo' para limpiar la pantalla.
                - Use la opción 'Historial' para ver operaciones anteriores.
                """;
        JOptionPane.showMessageDialog(this, mensaje, "Ayuda", JOptionPane.INFORMATION_MESSAGE);
    }

    private void guardarHistorial(String resultado) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("historial.txt", true))) {
            writer.write(resultado);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Calculadora().setVisible(true));
    }
}
