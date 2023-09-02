import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class LoginPage extends JFrame {

    private final JTextField usernameField;
    private final JPasswordField passwordField;

    public LoginPage() {
        setTitle("Login Page");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        String infoMessage =
                "<html>" +
                    "<body style='font-family: Arial, sans-serif; font-size: 14px;'>" +
                        "<div style='background-color: #f5f5f5; padding: 20px; text-align: center;'>" +
                            "<h2 style='color: #000000;'>Welcome to AITOC data warehouse GUI</h2><hr style='border-top: dotted 150px; width: 150px;'>" +
                            "<p>This is a GUI for performing various actions related to the AITOC data warehouse.</p>" +
                            "<hr style='border-top: dotted 150px; width: 250px;'>" +
                            "<p>It has the capabilities to:</p>" +
                            "<ul style='text-align: left;'>" +
                            "<li>Modify, edit, delete, and fetch data of users, projects, and files in the AITOC warehouse.</li>" +
                            "<li>Monitor the data in the AITOC warehouse.</li>" +
                            "</ul>" +
                            "<hr style='border-top: dotted 150px; width: 350px;'>" +
                            "<p>In order to perform these actions, you need to be logged in to the system. </p>" +
                        "</div>" +
                    "</body>" +
                "</html>";

        JEditorPane infoEditorPane = new JEditorPane("text/html", infoMessage);
        infoEditorPane.setEditable(false);
        mainPanel.add(infoEditorPane, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel usernameLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(usernameLabel, gbc);

        usernameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        inputPanel.add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        inputPanel.add(passwordField, gbc);

        mainPanel.add(inputPanel, BorderLayout.CENTER);

        JPanel loginButtonPanel = new JPanel();
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            boolean isAuthenticated;
            try {
                isAuthenticated = authenticate(username, password);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            if (isAuthenticated) {
                dispose();
                new MainPage(username, password);
            } else {
                JOptionPane.showMessageDialog(LoginPage.this, "Invalid username or password!", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });
        loginButtonPanel.add(loginButton, BorderLayout.CENTER);
        mainPanel.add(loginButtonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        pack();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public String jsonBeautifier(String response){
        try {
            Object json = new JSONTokener(response).nextValue();

            if (json instanceof JSONObject) {
                response = ((JSONObject) json).toString(4);
            } else if (json instanceof JSONArray) {
                response = ((JSONArray) json).toString(4);
            }
        } catch (Exception ignored) {
        }
        return response;
    }

    private boolean authenticate(String username, String password) throws IOException {
        APIConnection checkerPing = new APIConnection("https://active-registry.aitoc.eu/api.php");
        String response = checkerPing.post("listUsers", username, password);
        response = jsonBeautifier(response);
        return !response.contains("Bad username/email or password");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginPage::new);
    }
}


