import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class AffineCipherGUI extends JFrame {

    private final JTextField aField;
    private final JTextField bField;
    private final JTextArea inputArea;
    private final JButton encryptButton;

    public AffineCipherGUI() {
        setTitle("Affine Cipher (English only)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        JPanel keyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        keyPanel.add(new JLabel("a:"));
        aField = new JTextField("5", 5);
        keyPanel.add(aField);

        keyPanel.add(new JLabel("b:"));
        bField = new JTextField("8", 5);
        keyPanel.add(bField);

        inputArea = new JTextArea();
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(inputArea);

        encryptButton = new JButton("Encrypt and Save");
        encryptButton.addActionListener(this::encryptAction);

        add(keyPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(encryptButton, BorderLayout.SOUTH);
    }

    private void encryptAction(ActionEvent e) {
        try {
            int a = Integer.parseInt(aField.getText().trim());
            int b = Integer.parseInt(bField.getText().trim());
            String text = inputArea.getText();

            if (!isCoprime(a, 26)) {
                JOptionPane.showMessageDialog(this,
                        "The coefficient 'a' must be coprime with 26.\nPossible values: 1,3,5,7,9,11,15,17,19,21,23,25",
                        "Invalid 'a' value", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String encrypted = encrypt(text, a, b);

            Path outputDir = Path.of("target/output");
            Files.createDirectories(outputDir);
            Path outputFile = outputDir.resolve("encrypted.txt");
            Files.writeString(outputFile, encrypted);

            JOptionPane.showMessageDialog(this,
                    "Text encrypted and saved to:\n" + outputFile.toAbsolutePath(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter valid numbers for a and b!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "File write error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String encrypt(String text, int a, int b) {
        StringBuilder result = new StringBuilder();
        for (char ch : text.toCharArray()) {
            if (Character.isLetter(ch)) {
                boolean upper = Character.isUpperCase(ch);
                char base = upper ? 'A' : 'a';
                int x = ch - base;
                int enc = (a * x + b) % 26;
                result.append((char) (enc + base));
            } else {
                result.append(ch);
            }
        }
        return result.toString();
    }

    private boolean isCoprime(int a, int m) {
        return gcd(Math.abs(a), m) == 1;
    }

    private int gcd(int x, int y) {
        while (y != 0) {
            int t = x % y;
            x = y;
            y = t;
        }
        return x;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AffineCipherGUI().setVisible(true));
    }
}
