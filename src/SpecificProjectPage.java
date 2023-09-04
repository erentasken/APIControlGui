import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class SpecificProjectPage extends JFrame {
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

    JMenuItem subscribeFileItem;
    JMenuItem getSubscriptionListItem;
    String page3Guide =
            """
                    Welcome to "Open Project" guide!
                    ----------------------------------------------------------------------------------------------------------------
                    In this page, in order to execute the processes initially, you need to
                    select a project ID by clicking on the "Select project" button.
                    ----------------------------------------------------------------------------------------------------------------
                    There is a menu item named "User Management" where you can execute the following processes:

                    "View User List In Project": Monitors the existing users in the project.
                    "View User Privileges In Project": Monitors the user rights in the project.
                    "Add New User To Project": Adds a new user to the project.
                    "Modify User Rights In Project": Modifies the privileges of a selected user in the project.
                    "Remove User From Project": Removes a selected user from the project.
                    ----------------------------------------------------------------------------------------------------------------
                    There is a menu item named "File Management" where you can execute the following processes:

                    "Upload A File": Uploads a file into the selected project. Then notifies the update to file subscribers.
                    "Delete A File": Deletes the file from the selected project.
                    "List Files": Lists the files in the selected project.
                    "Download A File": Downloads a file from the selected project.
                    ----------------------------------------------------------------------------------------------------------------
                    There is a menu item named "Subscription" where you can subscribe to file and list the subscribed files.
                    
                    "Subscribe To File": Subscribes to desired file in order to get notifications of file update.
                    "Get Subscribed File List": Lists the subscribed files. 
                    
                    """;

    public SpecificProjectPage(ActionExecutor action, ManageProjectPage manageProjectPageObj){
        this.manageProjectPageObj = manageProjectPageObj;
        this.action = action;
        initializeUI();

        setTheMenuBar();

        setInformationCorner();

        callTheActionListeners();

        setVisible(true);
    }

    private void setTheMenuBar(){
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

        JMenu subscriptionMenu = new JMenu("Subscription");

        subscribeFileItem = new JMenuItem("Subscribe To File");
        subscriptionMenu.add(subscribeFileItem);

        getSubscriptionListItem = new JMenuItem("Get Subscribed File List");
        subscriptionMenu.add(getSubscriptionListItem);

        menu.add(subscriptionMenu);

        openFileItem = new JMenuItem("Open File");
        menu.add(openFileItem);

        logoutItem = new JMenuItem("Logout");
        menu.add(logoutItem);

        closeThePageItem = new JMenuItem("Exit Screen");
        menu.add(closeThePageItem);

        menuBar3.add(menu);

        setJMenuBar(menuBar3);
    }

    private void initializeUI(){
        setTitle("Open Project");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setResizable(true);
    }

    private void setInformationCorner(){

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

    private void callTheActionListeners() {

        getSubscriptionListItem.addActionListener(e ->
                {
                    if(!MessageBroker.topicList.isEmpty())
                    JOptionPane.showMessageDialog(null, MessageBroker.topicList, "Project Selection", JOptionPane.INFORMATION_MESSAGE);
                    else if(MessageBroker.isConnected){
                        JOptionPane.showMessageDialog(null, "Subscription list is empty. ", "Project Selection", JOptionPane.INFORMATION_MESSAGE);
                    }
                });

        subscribeFileItem.addActionListener(e-> {
            if(currentProjectId==0){
                JOptionPane.showMessageDialog(null, "You need to select a project.", "Project Selection", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            String routingKey = JOptionPane.showInputDialog(null, "Subscribe to title : ", "Title Subscribing", JOptionPane.PLAIN_MESSAGE);
            if(routingKey == null){
                return;
            }
            MessageBroker.ListenTopic(routingKey);
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
            String projectIdInput= JOptionPane.showInputDialog(null, "Enter the project id. ", "Project Id Input", JOptionPane.PLAIN_MESSAGE);
            if(projectIdInput != null){
                currentProjectId = Integer.parseInt(projectIdInput);
                projectInfoArea.setText("Current project id : " + currentProjectId);
            }
        });

        appGuideButton.addActionListener(e -> JOptionPane.showMessageDialog(null, MainPage.mainHelpContent, "Help", JOptionPane.INFORMATION_MESSAGE));

        pageGuideButton.addActionListener(e -> JOptionPane.showMessageDialog(null, page3Guide, "Help", JOptionPane.INFORMATION_MESSAGE));

        listFilesItem.addActionListener(e -> {
            if(currentProjectId==0){
                JOptionPane.showMessageDialog(null, "You need to select a project.", "Project Selection", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            action.listFiles(textArea);
        });

        putFileItem.addActionListener(e -> {
            if(currentProjectId==0){
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
            if(currentProjectId==0){
                JOptionPane.showMessageDialog(null, "You need to select a project.", "Project Selection", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            action.getFile(textArea);
        });

        deleteFileItem.addActionListener(e -> {
            if(currentProjectId==0){
                JOptionPane.showMessageDialog(null, "You need to select a project.", "Project Selection", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            action.deleteFile(textArea);
        });

        viewUserItem.addActionListener(e -> {
            if(currentProjectId==0){
                JOptionPane.showMessageDialog(null, "You need to select a project.", "Project Selection", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            System.out.println("This is project id : " + currentProjectId);
            action.getUserListInProject(textArea);
        });

        viewUserPrivilegesItem.addActionListener(e -> {
            if(currentProjectId==0){
                JOptionPane.showMessageDialog(null, "You need to select a project.", "Project Selection", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            action.viewUserPrivilegesInProject(textArea);
        });

        addNewUserItem.addActionListener(e -> {
            if(currentProjectId==0){
                JOptionPane.showMessageDialog(null, "You need to select a project.", "Project Selection", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            action.addNewUserToProject(textArea);
        });

        modifyUserRightsItem.addActionListener(e -> {
            if(currentProjectId==0){
                JOptionPane.showMessageDialog(null, "You need to select a project.", "Project Selection", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            action.modifyUserRightsInProject(textArea);
        });

        removeUserItem.addActionListener(e -> {
            if(currentProjectId==0){
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
}
