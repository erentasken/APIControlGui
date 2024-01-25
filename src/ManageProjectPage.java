import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class ManageProjectPage extends JFrame {
    ActionExecutor action;
    JTextArea textArea;
    JButton appGuideButton;
    JButton pageGuideButton;
    SpecificProjectPage SpecificProjectScreen;
    JMenuItem openProject;
    JMenuItem openFileItem;
    JMenuItem logoutItem;
    JMenuItem closeThePageItem;
    JMenuItem listProjects;
    JMenuItem createProject;

    String page1Guide =
            "Welcome to the Project Management Guide!\n" +
                    "----------------------------------------------------------------------------------------------------------------\n" +
                    "Here are the menu options:\n\n" +
                    "\"Open Project\": This option allows the user to access and work on a specific existing project.\n" +
                    "\"Create Project\": Choose this option to create a new project.\n" +
                    "\"List Projects\": This option enables the user to view a list of projects they are currently involved in.\n";


    public ManageProjectPage(ActionExecutor action){
        this.action = action;

        initializeUI();

        setMenuBar();

        setInformationCorner();

        callTheActionListeners();

        setVisible(true);
    }

    private void callTheActionListeners(){

        createProject.addActionListener(e -> action.createProject(textArea));

        listProjects.addActionListener(e -> action.listTheProjects(textArea));

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
                if(SpecificProjectScreen!=null)  SpecificProjectScreen.dispose();
                action.haltTheMainApp();
                dispose();
            }
        });

        closeThePageItem.addActionListener(e -> dispose());

        openProject.addActionListener(e -> SwingUtilities.invokeLater(() -> {
            SpecificProjectScreen = new SpecificProjectPage(action, ManageProjectPage.this);
            SpecificProjectScreen.setVisible(true);
        }));



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
    }

    private void initializeUI(){
        setTitle("Project Management");
        setSize(570, 500);
        setLocationRelativeTo(null);
        setResizable(true);
    }

    private void setMenuBar(){
        JMenuBar menuBar1 = new JMenuBar();

        JMenu menu = new JMenu("Menu");

        openProject = new JMenuItem("Open Project");
        menu.add(openProject);

        createProject = new JMenuItem("Create Project");
        menu.add(createProject);

        listProjects = new JMenuItem("List Projects");
        menu.add(listProjects);

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
        JLabel titleLabel = new JLabel("Project Management");
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
