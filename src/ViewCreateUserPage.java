import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class ViewCreateUserPage extends JFrame {
    ActionExecutor action;

    JTextArea textArea;

    JButton appGuideButton;
    JButton pageGuideButton;

    JMenuItem openFileItem;
    JMenuItem logoutItem;
    JMenuItem listUsersItem;
    JMenuItem addNewUserItem;
    JMenuItem closeThePageItem;

    String page1Guide =
            """
                    Welcome to "User Management" guide!

                    ----------------------------------------------------------------------------------------------------------------
                    "List Users"= Monitors the users in the data warehouse.
                    "Add New User"= Adds a new user to the data warehouse.""";

    public ViewCreateUserPage(ActionExecutor action){
        this.action = action;

        initializeUI();

        setMenuBar();

        setInformationCorner();

        callTheActionListeners();

        setVisible(true);
    }

    private void callTheActionListeners(){
        logoutItem.addActionListener(e -> {

            int choice = JOptionPane.showConfirmDialog(
                    null,
                    "Are you sure you want to log out?",
                    "Logout",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (choice == JOptionPane.OK_OPTION) {
                new LoginPage();
                dispose();
                action.haltTheMainApp();
            }
        });

        closeThePageItem.addActionListener(e -> dispose());

        pageGuideButton.addActionListener(e -> JOptionPane.showMessageDialog(null, page1Guide, "Help", JOptionPane.INFORMATION_MESSAGE));

        appGuideButton.addActionListener(e -> JOptionPane.showMessageDialog(null, MainPage.mainHelpContent, "Help", JOptionPane.INFORMATION_MESSAGE));

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

        listUsersItem.addActionListener(e -> action.listUsers(textArea));

        addNewUserItem.addActionListener(e -> action.addNewUser(textArea));
    }

    private void initializeUI(){
        setTitle("User Management");
        setSize(500, 500);
        setLocationRelativeTo(null);
        setResizable(true);
    }

    private void setMenuBar(){
        JMenuBar menuBar1 = new JMenuBar();

        JMenu menu = new JMenu("Menu");

        listUsersItem = new JMenuItem("List Users");
        addNewUserItem = new JMenuItem("Add New User");
        menu.add(listUsersItem);
        menu.add(addNewUserItem);

        openFileItem = new JMenuItem("Open File");
        menu.add(openFileItem);

        logoutItem = new JMenuItem("Logout");
        menu.add(logoutItem);

        closeThePageItem = new JMenuItem("Exit Screen");
        menu.add(closeThePageItem);

        menuBar1.add(menu);

        setJMenuBar(menuBar1);
    }

    private void setInformationCorner(){
        JLabel titleLabel = new JLabel("User Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(titleLabel, BorderLayout.NORTH);

        textArea = new JTextArea(page1Guide);
        textArea.setEditable(false);
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        pageGuideButton = new JButton("Page Guide");

        Icon questionIcon = UIManager.getIcon("OptionPane.questionIcon");
        pageGuideButton.setIcon(questionIcon);
        pageGuideButton.setToolTipText("Click here for help!");
        pageGuideButton.setBorderPainted(false);
        pageGuideButton.setContentAreaFilled(false);

        appGuideButton = new JButton("App Guide");
        appGuideButton.setIcon(questionIcon);
        appGuideButton.setToolTipText("Click here for help!");
        appGuideButton.setBorderPainted(false);
        appGuideButton.setContentAreaFilled(false);

        JLabel loggedUser = new JLabel("Logged user: " + MainPage.userName);

        buttonPanel.add(pageGuideButton);
        buttonPanel.add(appGuideButton);
        buttonPanel.add(loggedUser);

        add(buttonPanel, BorderLayout.SOUTH);
    }
}
