import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class MainPage extends JFrame {
    protected APIConnection apiConnection;

    public static String userName;
    public static String userPassword;

    //private final ActionExecutor action;
    public static ActionExecutor action = null;

    private JButton appGuideButton;

    private JMenuItem logoutItem;
    private JMenuItem manageProjects;
    private JMenuItem openFileItem;
    private JMenuItem manageUsers;

    String helpContent =
            "<html>" +
                    "<body style='font-family: Arial, sans-serif; font-size: 14px;'>" +
                    "<div style='background-color: #f5f5f5; padding: 20px; text-align: center;'>" +
                    "<h2 style='color: #000000;'>Welcome To The Main Page!</h2><hr style='border-top: dotted 150px; width: 150px;'>" +
                    "<p>In this page, you can select your desired option from the \"Menu\".</p>" +
                    "<ul>" +
                    "<li>Project Management: Includes project management options.</li>" +
                    "<li>User Management: Includes user management options.</li>" +
                    "</ul>" +
                    "<hr style='border-top: dotted 150px; width: 350px;'>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

    static public String mainHelpContent =
            "<html>" +
                    "<body style='font-family: Arial, sans-serif; font-size: 14px;'>" +
                    "<div style='background-color: #f5f5f5; padding: 20px; text-align: center;'>" +
                    "<h2 style='color: #000000;'>Welcome To The App Guide!</h2><hr style='border-top: dotted 150px; width: 150px;'>" +
                    "<p>There is a guide for each page:</p>" +
                    "<p>When you open each page for the first time, a guide will appear in the middle of the screen.</p>" +
                    "<p>You can also access the page's guide at the bottom of the pages.</p>" +
                    "<p>Each page has three common menu items:</p>" +
                    "<ul>" +
                    "<li><strong>Open File:</strong> Open a file from the 'downloadedFiles' folder. When you download a file from the data warehouse, it will be saved in this folder.</li>" +
                    "<li><strong>Logout:</strong> Log out from your current user account and return to the login screen.</li>" +
                    "<li><strong>Exit Screen:</strong> Close the current page. In the main page, this option exits the application.</li>" +
                    "</ul>" +
                    "<hr style='border-top: dotted 150px; width: 350px;'>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

    public MainPage(String userName, String userPassword) {
        MainPage.userName = userName;
        MainPage.userPassword = userPassword;
        action = new ActionExecutor(this);

        initializeAPIConnection();

        initializeUI();

        setTheMenuBar();

        setInformationCorner();

        callTheActionListeners();

        setVisible(true);
    }

    private void setInformationCorner(){
        JPanel informationPanel = new JPanel();

        Icon questionIcon = UIManager.getIcon("OptionPane.questionIcon");

        appGuideButton = new JButton("App Guide");
        appGuideButton.setIcon(questionIcon);
        appGuideButton.setToolTipText("Click here for help!");
        appGuideButton.setBorderPainted(false);
        appGuideButton.setContentAreaFilled(false);

        JLabel loggedUser = new JLabel("Logged User : " + userName);

        informationPanel.add(appGuideButton);
        informationPanel.add(loggedUser);

        add(informationPanel, BorderLayout.SOUTH);


        JPanel helpPanel = new JPanel();
        helpPanel.setLayout(new BorderLayout());
        helpPanel.setBorder(BorderFactory.createTitledBorder("Guide"));

        JTextPane helpTextPane = new JTextPane();
        helpTextPane.setContentType("text/html");
        helpTextPane.setText(helpContent);
        helpTextPane.setEditable(false);

        helpPanel.add(helpTextPane);

        add(helpPanel, BorderLayout.CENTER);

        JPanel mainHelpPanel = new JPanel();
        mainHelpPanel.setLayout(new BorderLayout());
        mainHelpPanel.setBorder(BorderFactory.createTitledBorder("Guide"));

        JTextPane helpTextPaneMain = new JTextPane();
        helpTextPaneMain.setContentType("text/html");
        helpTextPaneMain.setText(mainHelpContent);
        helpTextPaneMain.setEditable(false);
    }

    private void initializeAPIConnection(){
        String endPoint = "https://active-registry.aitoc.eu/api.php";
        try {
            apiConnection = new APIConnection(endPoint);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initializeUI(){
        setTitle("AIToc Warehouse Moderating App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(875, 500);
        setLocationRelativeTo(null);
        setResizable(true);
        setLayout(new BorderLayout());
    }

    private void setTheMenuBar(){
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");

        manageProjects = new JMenuItem("Manage Project");
        manageUsers = new JMenuItem("Manage Users");
        menu.add(manageProjects);
        menu.add(manageUsers);

        openFileItem = new JMenuItem("Open File");
        menu.add(openFileItem);

        logoutItem = new JMenuItem("Logout");
        menu.add(logoutItem);
        menuBar.add(menu);
        setJMenuBar(menuBar);


    }

    private void callTheActionListeners(){

        appGuideButton.addActionListener(e -> JOptionPane.showMessageDialog(
                MainPage.this, // Parent component
                mainHelpContent,     // Content to display
                "App Guide",   // Title
                JOptionPane.INFORMATION_MESSAGE
        ));

        openFileItem.addActionListener(e -> {
            File downloadedFileDirectory = new File(System.getProperty("user.dir"), "downloadedFiles");
            JFileChooser fileChooser = new JFileChooser(downloadedFileDirectory);

            File selectedFile;
            int result = fileChooser.showOpenDialog(null);

            if (result == JFileChooser.CANCEL_OPTION) {
                System.out.println("Process canceled");
                return;
            }

            selectedFile = fileChooser.getSelectedFile();

            if(!Desktop.isDesktopSupported())
            {
                System.out.println("not supported");
                return;
            }
            Desktop desktop = Desktop.getDesktop();
            if(selectedFile.exists()) {
                try {
                    desktop.open(selectedFile);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        logoutItem.addActionListener(e -> {

            int choice = JOptionPane.showConfirmDialog(
                    null,
                    "You are going to close the app.",
                    "Shutdown.",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (choice == JOptionPane.OK_OPTION) {
                new LoginPage();
                dispose();
            }
        });

        manageUsers.addActionListener(e -> SwingUtilities.invokeLater(() -> {
            ViewCreateUserPage screen = new ViewCreateUserPage(action);
            screen.setVisible(true);
        }));

        manageProjects.addActionListener(e -> SwingUtilities.invokeLater(() -> {
            ManageProjectPage screen = new ManageProjectPage(action);
            screen.setVisible(true);
        }));


    }
}