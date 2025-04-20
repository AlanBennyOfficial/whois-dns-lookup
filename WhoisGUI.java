import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class WhoisGUI extends JFrame {
    private JTextField domainField;
    private JTextArea resultArea;

    public WhoisGUI() {
        setTitle("WHOIS Lookup Tool");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        domainField = new JTextField();
        JButton lookupButton = new JButton("Lookup");
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JLabel("Enter Domain: "), BorderLayout.WEST);
        topPanel.add(domainField, BorderLayout.CENTER);
        topPanel.add(lookupButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        lookupButton.addActionListener( e -> {
            String domain = domainField.getText().trim();
            resultArea.setText("Looking up " + domain + "...\n");
            new Thread(() -> {
                try {
                    String response = queryWhoisServer("whois.verisign-grs.com", domain);
                    resultArea.append("\n--- Registry WHOIS Response ---\n" + response);

                    String registrar = extractRegistrarWhoisServer(response);
                    if (registrar != null) {
                        resultArea.append("\n--- Registrar WHOIS Server: " + registrar + " ---\n");
                        String registrarResponse = queryWhoisServer(registrar, domain);
                        resultArea.append("\n--- Registrar WHOIS Response ---\n" + registrarResponse);
                    }
                } catch (IOException ex) {
                    resultArea.append("\nError: " + ex.getMessage());
                }
            }).start();
        });

        setVisible(true);
    }

    private String queryWhoisServer(String server, String domain) throws IOException {
        StringBuilder result = new StringBuilder();
        try (Socket socket = new Socket(server, 43)) {
            OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream());
            writer.write(domain + "\r\n");
            writer.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }
        }
        return result.toString();
    }

    private String extractRegistrarWhoisServer(String response) {
        for (String line : response.split("\n")) {
            if (line.toLowerCase().startsWith("registrar whois server:")) {
                return line.split(":", 2)[1].trim();
            }
        }
        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(WhoisGUI::new);
    }
}