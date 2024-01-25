import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class SpecificProjectPage extends JFrame {

    public static List<JLabel> labelList;
    JButton myButton;
    static int currentProjectId;
    ActionExecutor action;
    ManageProjectPage manageProjectPageObj;
    JTextArea textArea;
    JTextArea projectInfoArea;
    JButton selectingProjectButton;
    JButton pageGuideButton;
    JButton appGuideButton;
    JMenuItem viewUserItem;
    JMenuItem viewUserPrivilegesItem;
    JMenuItem addNewUserItem;
    JMenuItem modifyUserRightsItem;
    JMenuItem removeUserItem;
    JMenuItem listFilesItem;
    JMenuItem putFileItem;
    JMenuItem getFileItem;
    JMenuItem deleteFileItem;
    JMenuItem openFileItem;
    JMenuItem logoutItem;
    JMenuItem closeThePageItem;

    JMenuItem manageSubscription;
    String page3Guide =
            "Welcome to \"Open Project\" guide!\n" +
                    "----------------------------------------------------------------------------------------------------------------\n" +
                    "In this page, in order to execute the processes initially, you need to\n" +
                    "select a project ID by clicking on the \"Select project\" button.\n" +
                    "----------------------------------------------------------------------------------------------------------------\n" +
                    "There is a menu item named \"User Management\" where you can execute the following processes:\n\n" +
                    "\"View User List In Project\": Monitors the existing users in the project.\n" +
                    "\"View User Privileges In Project\": Monitors the user rights in the project.\n" +
                    "\"Add New User To Project\": Adds a new user to the project.\n" +
                    "\"Modify User Rights In Project\": Modifies the privileges of a selected user in the project.\n" +
                    "\"Remove User From Project\": Removes a selected user from the project.\n" +
                    "----------------------------------------------------------------------------------------------------------------\n" +
                    "There is a menu item named \"File Management\" where you can execute the following processes:\n\n" +
                    "\"Upload A File\": Uploads a file into the selected project. Then notifies the update to file subscribers.\n" +
                    "\"Delete A File\": Deletes the file from the selected project.\n" +
                    "\"List Files\": Lists the files in the selected project.\n" +
                    "\"Download A File\": Downloads a file from the selected project.\n" +
                    "----------------------------------------------------------------------------------------------------------------\n" +
                    "There is a menu item named \"Subscription\" where you can subscribe to file and list the subscribed files.\n\n" +
                    "\"Subscribe To File\": Subscribes to desired file in order to get notifications of file update.\n" +
                    "\"Get Subscribed File List\": Lists the subscribed files.\n";


    public SpecificProjectPage(ActionExecutor action, ManageProjectPage manageProjectPageObj) {
        this.manageProjectPageObj = manageProjectPageObj;
        this.action = action;
        initializeUI();

        setTheMenuBar();

        setInformationCorner();

        callTheActionListeners();

        setVisible(true);
    }

    private void setTheMenuBar() {
        JMenuBar menuBar3 = new JMenuBar();

        JMenu menu = new JMenu("Menu");

        JMenu processMenu = new JMenu("User Management");
        viewUserItem = new JMenuItem("View User List In Project.");
        viewUserPrivilegesItem = new JMenuItem("View User Privileges In Project.");
        addNewUserItem = new JMenuItem("Add New User To Project.");
        modifyUserRightsItem = new JMenuItem("Modify User Rights In Project.");
        removeUserItem = new JMenuItem("Remove User From Project.");
        processMenu.add(viewUserItem);
        processMenu.add(viewUserPrivilegesItem);
        processMenu.add(addNewUserItem);
        processMenu.add(modifyUserRightsItem);
        processMenu.add(removeUserItem);

        menu.add(processMenu);

        JMenu processMenu1 = new JMenu("File Management");
        listFilesItem = new JMenuItem("List Files.");
        putFileItem = new JMenuItem("Upload File");
        getFileItem = new JMenuItem("Download File");
        deleteFileItem = new JMenuItem("Delete File");
        processMenu1.add(putFileItem);
        processMenu1.add(deleteFileItem);
        processMenu1.add(listFilesItem);
        processMenu1.add(getFileItem);

        menu.add(processMenu1);

        manageSubscription = new JMenuItem("Manage Subscription");

        menu.add(manageSubscription);

        openFileItem = new JMenuItem("Open File");
        menu.add(openFileItem);

        logoutItem = new JMenuItem("Logout");
        menu.add(logoutItem);

        closeThePageItem = new JMenuItem("Exit Screen");
        menu.add(closeThePageItem);

        menuBar3.add(menu);

        setJMenuBar(menuBar3);
    }

    private void initializeUI() {
        setTitle("Open Project");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setResizable(true);
    }

    private void setInformationCorner() {
        labelList = new ArrayList<JLabel>();
        JPanel pagePanel = new JPanel();
        selectingProjectButton = new JButton("Select Project. ");
        JLabel titleLabel = new JLabel("Open Project.");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        projectInfoArea = new JTextArea("There is no selected project.");
        pagePanel.add(titleLabel);
        pagePanel.add(selectingProjectButton);
        pagePanel.add(projectInfoArea);
        add(pagePanel, BorderLayout.NORTH);

        textArea = new JTextArea(page3Guide);
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

        JLabel loggedUser = new JLabel("Logged User: " + MainPage.userName);

        buttonPanel.add(pageGuideButton);
        buttonPanel.add(appGuideButton);
        buttonPanel.add(loggedUser);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setCheckBox() {
        int size = 0;
        JPanel checkboxPanel = new JPanel();
        checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));
        JFrame frame = new JFrame("SUBSCRIPTIONS");

        checkboxPanel.add(new JLabel("FILES"));
        checkboxPanel.add(new JSeparator());

        for (JCheckBox checkBox : MessageBroker.topicList) {
            size += 70;
            checkboxPanel.add(checkBox);
            frame.setSize(450, size);
        }

        JScrollPane scrollPane = new JScrollPane(checkboxPanel);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        JLabel selectedLabel = new JLabel("SELECTED SUBSCRIPTION");
        rightPanel.add(selectedLabel);

        JSeparator separator = new JSeparator();
        rightPanel.add(separator);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        myButton = new JButton("Update Subscriptions");
        buttonPanel.add(myButton);

        int mySize = 0;
        for (JLabel label : labelList) {
            mySize += 70;
            rightPanel.add(label);
            frame.setSize(450, mySize);
            System.out.println("our label : " + label.getText());
        }

        myButton.addActionListener(e -> {
            frame.revalidate();
            for (JCheckBox checkBox : MessageBroker.topicList) {
                boolean checkBoxSelected = checkBox.isSelected();
                if (checkBoxSelected) {
                    boolean labelExists = false;
                    for (JLabel label : labelList) {
                        if (Objects.equals(label.getText(), checkBox.getText())) {
                            labelExists = true;
                            break;
                        }
                    }

                    if (!labelExists || labelList.isEmpty()) {
                        JLabel selectedTopicLabel = new JLabel(checkBox.getText());
                        rightPanel.add(selectedTopicLabel);
                        labelList.add(selectedTopicLabel);
                        MessageBroker.ListenTopic(checkBox.getText());
                    }
                } else {
                    Iterator<JLabel> iterator = labelList.iterator();
                    while (iterator.hasNext()) {
                        JLabel label = iterator.next();
                        if (label.getText().equals(checkBox.getText())) {
                            try {
                                MessageBroker.channel.queueDelete(label.getText() + "Queue");
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                            iterator.remove();
                            rightPanel.remove(label);
                        }
                    }
                }
            }
            frame.revalidate();

        });

        frame.setLayout(new BorderLayout());
        frame.add(scrollPane, BorderLayout.WEST);
        frame.add(rightPanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.pack();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - frame.getWidth()) / 2;
        int y = (screenSize.height - frame.getHeight()) / 2;
        frame.setLocation(x, y);
        frame.setVisible(true);
    }


    private void callTheActionListeners() {
        manageSubscription.addActionListener(e ->
        {
            if (currentProjectId == 0) {
                JOptionPane.showMessageDialog(null, "You need to select a project.", "Project Selection", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            System.out.println("clicked on manageSubscriptions");
            try {
                if (MessageBroker.connection == null) {
                    MessageBroker.setupConnection();
                }

                if (!MessageBroker.channel.isOpen()) {
                    System.out.println("call the action setupconnection");
                    MessageBroker.setupConnection();
                }
                if (!MessageBroker.topicList.isEmpty()) {
                    setCheckBox();
                } else {
                    JOptionPane.showMessageDialog(null, "Subscription list is empty. ", "Project Selection", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (NullPointerException exp) {
                if (MessageBroker.channel == null) {
                    MessageBroker.setupConnection();
                }
                NullPointerException er = new NullPointerException();
                er.initCause(exp);
                System.out.println("null pointer exception");
                throw er;
            }


        });

        closeThePageItem.addActionListener(e -> dispose());

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
                manageProjectPageObj.dispose();
            }
        });

        selectingProjectButton.addActionListener(e -> {
            String projectIdInput = JOptionPane.showInputDialog(null, "Enter the project id. ", "Project Id Input", JOptionPane.PLAIN_MESSAGE);
            if (projectIdInput != null) {
                currentProjectId = Integer.parseInt(projectIdInput);
                projectInfoArea.setText("Current project id : " + currentProjectId);
            }
        });

        appGuideButton.addActionListener(e -> JOptionPane.showMessageDialog(null, MainPage.mainHelpContent, "Help", JOptionPane.INFORMATION_MESSAGE));

        pageGuideButton.addActionListener(e -> JOptionPane.showMessageDialog(null, page3Guide, "Help", JOptionPane.INFORMATION_MESSAGE));

        listFilesItem.addActionListener(e -> {
            if (currentProjectId == 0) {
                JOptionPane.showMessageDialog(null, "You need to select a project.", "Project Selection", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            action.listFiles(textArea);
        });

        putFileItem.addActionListener(e -> {
            if (currentProjectId == 0) {
                JOptionPane.showMessageDialog(null, "You need to select a project.", "Project Selection", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            try {
                action.uploadFile(textArea);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        getFileItem.addActionListener(e -> {
            if (currentProjectId == 0) {
                JOptionPane.showMessageDialog(null, "You need to select a project.", "Project Selection", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            action.getFile(textArea);
        });

        deleteFileItem.addActionListener(e -> {
            if (currentProjectId == 0) {
                JOptionPane.showMessageDialog(null, "You need to select a project.", "Project Selection", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            action.deleteFile(textArea);
        });

        viewUserItem.addActionListener(e -> {
            if (currentProjectId == 0) {
                JOptionPane.showMessageDialog(null, "You need to select a project.", "Project Selection", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            action.getUserListInProject(textArea);
        });

        viewUserPrivilegesItem.addActionListener(e -> {
            if (currentProjectId == 0) {
                JOptionPane.showMessageDialog(null, "You need to select a project.", "Project Selection", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            action.viewUserPrivilegesInProject(textArea);
        });

        addNewUserItem.addActionListener(e -> {
            if (currentProjectId == 0) {
                JOptionPane.showMessageDialog(null, "You need to select a project.", "Project Selection", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            action.addNewUserToProject(textArea);
        });

        modifyUserRightsItem.addActionListener(e -> {
            if (currentProjectId == 0) {
                JOptionPane.showMessageDialog(null, "You need to select a project.", "Project Selection", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            action.modifyUserRightsInProject(textArea);
        });

        removeUserItem.addActionListener(e -> {
            if (currentProjectId == 0) {
                JOptionPane.showMessageDialog(null, "You need to select a project.", "Project Selection", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            action.removeUserFromProject(textArea);
        });

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

            if (!Desktop.isDesktopSupported()) {
                System.out.println("not supported");
                return;
            }
            Desktop desktop = Desktop.getDesktop();
            if (selectedFile.exists()) {
                try {
                    desktop.open(selectedFile);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }
}
