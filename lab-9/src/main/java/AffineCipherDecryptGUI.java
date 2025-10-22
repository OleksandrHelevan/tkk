import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class AffineCipherDecryptGUI extends JFrame {

    private final JTextField aField;
    private final JTextField bField;
    private final JButton decryptButton;
    private final JTextArea outputArea;

    public AffineCipherDecryptGUI() {
        setTitle("Affine Cipher Decrypt (English only)");
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

        decryptButton = new JButton("Decrypt File");
        decryptButton.addActionListener(this::decryptAction);
        keyPanel.add(decryptButton);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        add(keyPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void decryptAction(ActionEvent e) {
        try {
            int a = Integer.parseInt(aField.getText().trim());
            int b = Integer.parseInt(bField.getText().trim());

            if (!isCoprime(a, 26)) {
                JOptionPane.showMessageDialog(this,
                        "The coefficient 'a' must be coprime with 26.\nPossible values: 1,3,5,7,9,11,15,17,19,21,23,25",
                        "Invalid 'a' value", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Path inputFile = Path.of("target/output/encrypted.txt");
            if (!Files.exists(inputFile)) {
                JOptionPane.showMessageDialog(this,
                        "Encrypted file not found:\n" + inputFile.toAbsolutePath(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String text = Files.readString(inputFile);
            String decrypted = decrypt(text, a, b);

            Path outputDir = Path.of("target/output");
            Files.createDirectories(outputDir);
            Path outputFile = outputDir.resolve("decrypted_2.txt");
            Files.writeString(outputFile, decrypted);

            outputArea.setText(decrypted);

            JOptionPane.showMessageDialog(this,
                    "Text decrypted and saved to:\n" + outputFile.toAbsolutePath(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter valid numbers for a and b!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "File I/O error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String decrypt(String text, int a, int b) {
        StringBuilder result = new StringBuilder();
        int aInv = modInverse(a, 26);
        for (char ch : text.toCharArray()) {
            if (Character.isLetter(ch)) {
                boolean upper = Character.isUpperCase(ch);
                char base = upper ? 'A' : 'a';
                int y = ch - base;
                int dec = (aInv * (y - b + 26)) % 26;
                result.append((char) (dec + base));
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

    private int modInverse(int a, int m) {
        a = a % m;
        for (int x = 1; x < m; x++) {
            if ((a * x) % m == 1)
                return x;
        }
        throw new IllegalArgumentException("No modular inverse for a=" + a);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AffineCipherDecryptGUI().setVisible(true));
    }
}
