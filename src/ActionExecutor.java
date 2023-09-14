import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ActionExecutor {
    MainPage myApp;

    public ActionExecutor(MainPage myApp) {
        this.myApp = myApp;
    }

    public void haltTheMainApp() {
        myApp.dispose();
    }

    public String jsonBeautifier(String response) {
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

    public void listUsers(JTextArea textArea) { //listUsers
        String response = myApp.apiConnection.post("listUsers", MainPage.userName, myApp.userPassword);
        response = jsonBeautifier(response);
        response = "API response message: \n" + response;
        textArea.setText(response);
    }

    public void addNewUser(JTextArea textArea) {
        int resultF = JOptionPane.showConfirmDialog(
                null,
                "You are going to add a new user.",
                "Adding a new user.",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (resultF == JOptionPane.OK_OPTION) {
            System.out.println("process is going to execute.");
        } else {
            System.out.println("process canceled.");
            return;
        }

        JPanel panel = new JPanel(new GridLayout(4, 2));

        JLabel newEmailLabel = new JLabel("New Email:");
        JLabel newNameLabel = new JLabel("New Name:");
        JLabel newPasswordLabel = new JLabel("New Password:");

        JTextField newEmailField = new JTextField(15);
        JTextField newNameField = new JTextField(15);
        JTextField newPasswordField = new JTextField(15);

        panel.add(newEmailLabel);
        panel.add(newEmailField);
        panel.add(newNameLabel);
        panel.add(newNameField);
        panel.add(newPasswordLabel);
        panel.add(newPasswordField);

        int result = JOptionPane.showConfirmDialog(
                null,
                panel,
                "Update User Info",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            System.out.println("New user info successfully taken.");

        } else {
            System.out.println("Updating user info process is canceled.");
            return;
        }
        String newEmail = newEmailField.getText();
        String newName = newNameField.getText();
        String newPass = newPasswordField.getText();

        String response = myApp.apiConnection.post("addUser", MainPage.userName, myApp.userPassword, newEmail, newName, newPass);
        response = jsonBeautifier(response);
        response = "API response message: \n" + response;
        textArea.setText(response);
    }

    public void getUserListInProject(JTextArea textArea) {
        String response = myApp.apiConnection.post("listUsersInProject", MainPage.userName, myApp.userPassword, String.valueOf(SpecificProjectPage.currentProjectId));
        response = jsonBeautifier(response);
        response = "API response message: \n" + response;
        //myApp.responseArea3.setText(response);
        textArea.setText(response);
    }

    public void viewUserPrivilegesInProject(JTextArea textArea) {
        String userid = JOptionPane.showInputDialog(null, "User id: ", "User Id Input", JOptionPane.PLAIN_MESSAGE);
        if (userid == null) {
            System.out.println("Input canceled.");
            return; // Exit the actionPerformed method
        }

        String response = myApp.apiConnection.post("getUserPrivilegesInProject", MainPage.userName, myApp.userPassword, String.valueOf(SpecificProjectPage.currentProjectId), userid);
        response = jsonBeautifier(response);
        response = "API response message: \n" + response;
        textArea.setText(response);
    }

    public void addNewUserToProject(JTextArea textArea) {
        int resultF = JOptionPane.showConfirmDialog(
                null,
                "You are going to add a new user to project.",
                "Adding new user to project",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (resultF == JOptionPane.OK_OPTION) {
            System.out.println("process is going to execute.");
        } else {
            System.out.println("process canceled.");
            return;
        }
        JPanel panel = new JPanel(new BorderLayout()); // Outer panel with BorderLayout
        JPanel formPanel = new JPanel(new GridBagLayout()); // Inner panel for the form

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel userId = new JLabel("User ID:");
        JCheckBox createCheckBox = new JCheckBox();
        JCheckBox deleteCheckBox = new JCheckBox();
        JCheckBox modifyCheckBox = new JCheckBox();

        JTextField userIdField = new JTextField(15);

        formPanel.add(userId, gbc);
        gbc.gridx++;
        formPanel.add(userIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JSeparator(JSeparator.HORIZONTAL), gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        JLabel modifyLabel = new JLabel("Modify Permission:");
        formPanel.add(modifyLabel, gbc);
        gbc.gridx++;
        formPanel.add(modifyCheckBox, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        JLabel createLabel = new JLabel("Create Permission:");
        formPanel.add(createLabel, gbc);
        gbc.gridx++;
        formPanel.add(createCheckBox, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        JLabel deleteLabel = new JLabel("Delete Permission:");
        formPanel.add(deleteLabel, gbc);
        gbc.gridx++;
        formPanel.add(deleteCheckBox, gbc);

        panel.add(formPanel, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(
                null,
                panel,
                "Add User to Project",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        String idUser;
        boolean modifyPermission;
        boolean createPermission;
        boolean deletePermission;

        if (result == JOptionPane.OK_OPTION) { // user clicked OK
            System.out.println("User ID successfully taken.");
        } else {
            System.out.println("Taking the User ID process is canceled.");
            return;
        }
        idUser = userIdField.getText();
        modifyPermission = modifyCheckBox.isSelected();
        createPermission = createCheckBox.isSelected();
        deletePermission = deleteCheckBox.isSelected();
        String response = myApp.apiConnection.post("addUserToProject", MainPage.userName, myApp.userPassword, String.valueOf(SpecificProjectPage.currentProjectId), idUser, modifyPermission, createPermission, deletePermission);
        response = jsonBeautifier(response);
        response = "API response message: \n" + response;
        textArea.setText(response);
    }

    public void modifyUserRightsInProject(JTextArea textArea) {
        int resultF = JOptionPane.showConfirmDialog(
                null,
                "You are going to modify the user rights in project.",
                "Modifying the user rights in project",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (resultF == JOptionPane.OK_OPTION) {
            System.out.println("process is going to execute.");
        } else {
            System.out.println("process canceled.");
            return;
        }
        JPanel panel = new JPanel(new BorderLayout()); // Outer panel with BorderLayout
        JPanel formPanel = new JPanel(new GridBagLayout()); // Inner panel for the form

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel userId = new JLabel("User ID:");
        JCheckBox createCheckBox = new JCheckBox();
        JCheckBox deleteCheckBox = new JCheckBox();
        JCheckBox modifyCheckBox = new JCheckBox();

        JTextField userIdField = new JTextField(15);

        formPanel.add(userId, gbc);
        gbc.gridx++;
        formPanel.add(userIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JSeparator(JSeparator.HORIZONTAL), gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        JLabel modifyLabel = new JLabel("Modify Permission:");
        formPanel.add(modifyLabel, gbc);
        gbc.gridx++;
        formPanel.add(modifyCheckBox, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        JLabel createLabel = new JLabel("Create Permission:");
        formPanel.add(createLabel, gbc);
        gbc.gridx++;
        formPanel.add(createCheckBox, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        JLabel deleteLabel = new JLabel("Delete Permission:");
        formPanel.add(deleteLabel, gbc);
        gbc.gridx++;
        formPanel.add(deleteCheckBox, gbc);

        panel.add(formPanel, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(
                null,
                panel,
                "Add User to Project",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        String idUser;
        boolean modifyPermission;
        boolean createPermission;
        boolean deletePermission;

        if (result == JOptionPane.OK_OPTION) { // user clicked OK
            System.out.println("User ID successfully taken.");
        } else {
            System.out.println("Taking the User ID process is canceled.");
            return;
        }
        idUser = userIdField.getText();
        modifyPermission = modifyCheckBox.isSelected();
        createPermission = createCheckBox.isSelected();
        deletePermission = deleteCheckBox.isSelected();

        String response = myApp.apiConnection.post("modifyUserRightsInProject", MainPage.userName, myApp.userPassword, String.valueOf(SpecificProjectPage.currentProjectId), idUser, modifyPermission, createPermission, deletePermission);
        response = jsonBeautifier(response);
        response = "API response message: \n" + response;
        //myApp.responseArea3.setText(response);
        textArea.setText(response);
    }

    public void listTheProjects(JTextArea textArea) {

        String response = myApp.apiConnection.post("listProjects", MainPage.userName, myApp.userPassword);
        response = jsonBeautifier(response);
        response = "API response message: \n" + response;
        //myApp.responseArea2.setText(response);
        textArea.setText(response);
    }

    public void removeUserFromProject(JTextArea textArea) {
        int result = JOptionPane.showConfirmDialog(
                null,
                "You are going to remove the user from project.",
                "Remove the user from project",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            System.out.println("process is going to execute.");
        } else {
            System.out.println("process canceled.");
            return;
        }

        String userid = JOptionPane.showInputDialog(null, "User id: ", "User Id Input", JOptionPane.PLAIN_MESSAGE);
        if (userid == null) {
            System.out.println("Input canceled.");
            return; // Exit the actionPerformed method
        }

        String response = myApp.apiConnection.post("removeUserFromProject", MainPage.userName, myApp.userPassword, String.valueOf(SpecificProjectPage.currentProjectId), userid);
        response = jsonBeautifier(response);
        response = "API response message: \n" + response;
        //myApp.responseArea3.setText(response);
        textArea.setText(response);

    }

    public void createProject(JTextArea textArea) {

        int resultF = JOptionPane.showConfirmDialog(
                null,
                "You are about to create project.",
                "Creating project",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (resultF == JOptionPane.OK_OPTION) {
            System.out.println("process is going to execute.");
        } else {
            System.out.println("process canceled.");
            return;
        }

        JPanel panel = new JPanel(new BorderLayout());
        JPanel formPanel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel projectNameLabel = new JLabel("New project Name:");
        JTextField projectNameField = new JTextField(15);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(projectNameLabel, gbc);
        gbc.gridx++;
        formPanel.add(projectNameField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        JLabel descriptionLabel = new JLabel("Description:(not required)");
        formPanel.add(descriptionLabel, gbc);
        gbc.gridx++;
        JTextField descriptionField = new JTextField(15);
        formPanel.add(descriptionField, gbc);

        panel.add(formPanel, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(
                null,
                panel,
                "Add User to Project",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.CANCEL_OPTION) {
            System.out.println("Creating project is canceled. ");
            return;
        }

        String projectName = projectNameField.getText();
        String projectDescription = descriptionField.getText();
        String response;
        if (projectDescription.isEmpty()) {
            System.out.println("You didn't enter the project description");
            response = myApp.apiConnection.post("addProject", MainPage.userName, myApp.userPassword, projectName); // projectDescription is additional
        } else {
            response = myApp.apiConnection.post("addProject", MainPage.userName, myApp.userPassword, projectName, projectDescription); // projectDescription is additional
        }

        response = jsonBeautifier(response);
        response = "API response message: \n" + response;
        //myApp.responseArea3.setText(response);
        textArea.setText(response);
    }

    public void listFiles(JTextArea textArea) {
        JPanel inputPanel = new JPanel(new GridLayout(0, 2));
        JTextField pathField = new JTextField(20);

        inputPanel.add(new JLabel("Path:(Not required)"));
        inputPanel.add(pathField);

        int result = JOptionPane.showConfirmDialog(
                null,
                inputPanel,
                "List Files in Project",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );
        String path;
        String response;
        if (result == JOptionPane.OK_OPTION) {
            path = pathField.getText();
            if (path.isEmpty()) {
                System.out.println("Path input not being entered.");
                response = myApp.apiConnection.post("listFiles", MainPage.userName, myApp.userPassword, String.valueOf(SpecificProjectPage.currentProjectId));
            } else {
                System.out.println("You are about to list files in project: " + SpecificProjectPage.currentProjectId + " at path: " + path);
                response = myApp.apiConnection.post("listFiles", MainPage.userName, myApp.userPassword, String.valueOf(SpecificProjectPage.currentProjectId), path);
            }
            System.out.println("Project is going to execute.");
        } else {
            System.out.println("Process canceled.");
            return;
        }
        response = jsonBeautifier(response);
        response = "API response message: \n" + response;
        textArea.setText(response);
    }

    private Object handleUploadButtonClick() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(myApp);

        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        } else if (result == JFileChooser.CANCEL_OPTION) {
            System.out.println("Process canceled");
            return null;
        }
        return null;
    }

    public void uploadFile(JTextArea textArea) throws IOException {

        if (SpecificProjectPage.currentProjectId == 0) {
            JOptionPane.showMessageDialog(null, "Select project.", "Warning", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        final File[] myFile = new File[1];
        JPanel buttonPanel = new JPanel(new FlowLayout());

        int resultF = JOptionPane.showConfirmDialog(
                null,
                "You are about to upload a file.",
                "Uploading the file",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (resultF == JOptionPane.OK_OPTION) {
            System.out.println("Process is going to execute.");
        } else {
            System.out.println("Process canceled.");
            return;
        }

        JPanel inputPanel = new JPanel(new BorderLayout());
        JTextField folderField = new JTextField(20);

        JPanel folderPanel = new JPanel();
        folderPanel.setLayout(new BoxLayout(folderPanel, BoxLayout.X_AXIS));
        folderPanel.add(new JLabel("Path (Optional):"));
        folderPanel.add(Box.createHorizontalStrut(5));
        folderPanel.add(folderField);

        inputPanel.add(folderPanel, BorderLayout.SOUTH);

        JButton uploadButton = new JButton("Upload a file");
        JLabel selectedFileLabel = new JLabel("Selected file: ");
        selectedFileLabel.setFont(new Font("Arial", Font.BOLD, 12));
        uploadButton.addActionListener(e -> {
            myFile[0] = (File) handleUploadButtonClick();
            if (myFile[0] != null)
                selectedFileLabel.setText("Selected file:" + myFile[0].getName());
        });
        //myFile[0]
        buttonPanel.add(uploadButton);
        buttonPanel.add(selectedFileLabel);
        inputPanel.add(buttonPanel, BorderLayout.NORTH);

        int result = JOptionPane.showConfirmDialog(
                null,
                inputPanel,
                "Upload Files to Project",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.CANCEL_OPTION) {
            System.out.println("Process is canceled. ");
            return;
        }

        String response;
        String folder = folderField.getText();

        if (myFile[0] == null) {
            System.out.println("Please enter the required fields");
            return;
        }
        if (folder.isEmpty()) {
            response = myApp.apiConnection.sendFileToServer(myFile[0], String.valueOf(SpecificProjectPage.currentProjectId), MainPage.userName, myApp.userPassword);
        } else {
            response = myApp.apiConnection.sendFileToServer(myFile[0], String.valueOf(SpecificProjectPage.currentProjectId), MainPage.userName, myApp.userPassword, folder);
        }

        MessageBroker.stringToCheckboxList();
        MessageBroker.SendMessage(myFile[0].getName());

        System.out.println("You are going to upload the file at path: " + myFile[0].getAbsolutePath());
        System.out.println("Project is going to execute.");

        response = "API response message: \n" + response;
        textArea.setText(response);
    }


    public void getFile(JTextArea textArea) {
        int resultF = JOptionPane.showConfirmDialog(
                null,
                "You are about to download a file from data warehouse.",
                "Downloading a file",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (resultF == JOptionPane.OK_OPTION) {
            System.out.println("process is going to execute.");
        } else {
            System.out.println("process canceled.");
            return;
        }

        String file = JOptionPane.showInputDialog(null, "Enter the full server path: (returned by listTable)", "Get file", JOptionPane.PLAIN_MESSAGE);
        if (file == null) {
            System.out.println("Input canceled.");
            return;
        }

        byte[] response = myApp.apiConnection.postX("getFile", MainPage.userName, myApp.userPassword, String.valueOf(SpecificProjectPage.currentProjectId), file);
        String filePath = "downloadedFiles" + File.separator + file;

        if (new String(response).contains("error")) {
            textArea.setText(jsonBeautifier(new String(response)));
            return;
        }

        File directory = new File("downloadedFiles");
        if (!directory.exists()) {
            directory.mkdir();
        }

        try (OutputStream outputStream = new FileOutputStream(filePath)) {
            outputStream.write(response);
            textArea.setText("File has been taken from the server and saved locally.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        resultF = JOptionPane.showConfirmDialog(
                null,
                "Press OK in order to open the downloaded file, otherwise press CANCEL.",
                "File opening.",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (resultF == JOptionPane.CANCEL_OPTION) {
            System.out.println("process canceled.");
            return;
        }

        System.out.println("File is opening.");

        try {
            File openFile = new File(filePath);
            if (!Desktop.isDesktopSupported()) {
                System.out.println("not supported");
                return;
            }
            Desktop desktop = Desktop.getDesktop();
            if (openFile.exists())
                desktop.open(openFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteFile(JTextArea textArea) {
        int resultF = JOptionPane.showConfirmDialog(
                null,
                "You are about to delete the file.",
                "Deleting the file",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (resultF == JOptionPane.OK_OPTION) {
            System.out.println("process is going to execute.");
        } else {
            System.out.println("process canceled.");
            return;
        }

        String name = JOptionPane.showInputDialog(null, "Enter the full server path: (returned by List Files.)", "Delete file", JOptionPane.PLAIN_MESSAGE);
        if (name == null) {
            System.out.println("Input canceled.");
            return;
        }

        String response = myApp.apiConnection.post("deleteFile", MainPage.userName, myApp.userPassword, String.valueOf(SpecificProjectPage.currentProjectId), name);
        response = jsonBeautifier(response);
        response = "API response message: \n" + response;
        MessageBroker.stringToCheckboxList();
        textArea.setText(response);
    }

/*    public void getTableRows(){
        int resultF = JOptionPane.showConfirmDialog(
                null,
                "You are going to get table rows.",
                "Getting the table rows",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if(resultF == JOptionPane.OK_OPTION){
            System.out.println("process is going to execute.");
        }else{
            System.out.println("process canceled.");
            return;
        }

        String tableId = JOptionPane.showInputDialog(null, "Enter table id: (returned by listTable)", "Get table rows", JOptionPane.PLAIN_MESSAGE);
        if(tableId == null){
            System.out.println("Input canceled.");
            return;
        }

        String response = myApp.apiConnection.post("getTableRows", myApp.userName, myApp.userPassword, String.valueOf(myApp.currentProjectId), tableId);
        response = myApp.jsonBeautifier(response);
        response = "API response message: \n" + response;
        myApp.responseArea3.setText(response);
    }

    public void putTableRows() {
        JPanel panel = new JPanel(new GridLayout(0, 1));

        JPanel tableIdPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel tableIdLabel = new JLabel("Enter the table Id:");
        JTextField tableIdField = new JTextField(20);
        tableIdPanel.add(tableIdLabel);
        tableIdPanel.add(tableIdField);

        JPanel rowsJsonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel rowsJsonLabel = new JLabel("Enter the rows in JSON format:");
        JTextArea rowsJsonArea = new JTextArea(5, 20);
        rowsJsonPanel.add(rowsJsonLabel);
        rowsJsonPanel.add(new JScrollPane(rowsJsonArea));

        panel.add(tableIdPanel);
        panel.add(rowsJsonPanel);

        int resultF = JOptionPane.showConfirmDialog(
                null,
                panel,
                "Putting the table rows.",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );
        String tableId = null;
        String rowsJson = null;

        if (resultF == JOptionPane.OK_OPTION) {
            tableId = tableIdField.getText().trim();
            rowsJson = rowsJsonArea.getText().trim();

            if (tableId.isEmpty()) {
                System.out.println("Invalid tableId. Process canceled.");
                return;
            }

            if (rowsJson.isEmpty()) {
                System.out.println("Invalid rows data. Process canceled.");
                return;
            }

            System.out.println("Table ID: " + tableId);
            System.out.println("Rows (JSON): " + rowsJson);
            System.out.println("Process is going to execute.");
        } else {
            System.out.println("Process canceled.");
        }

        String response = myApp.apiConnection.post("putTableRows", myApp.userName, myApp.userPassword, String.valueOf(myApp.currentProjectId), tableId, rowsJson);
        response = myApp.jsonBeautifier(response);
        response = "API response message: \n" + response;
        myApp.responseArea3.setText(response);
    }

    public void getFileAsJSONPage(){ // That is not complete.
        int resultF = JOptionPane.showConfirmDialog(
                null,
                "You are going to get the files as JSON.",
                "Putting the files",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if(resultF == JOptionPane.OK_OPTION){
            System.out.println("process is going to execute.");
        }else{
            System.out.println("process canceled.");
            return;
        }
    }

    public void putJSONasFilePage(){ // That is not complete, it will be ignored.
        int resultF = JOptionPane.showConfirmDialog(
                null,
                "You are going to put JSON asa a file.",
                "Putting the files",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if(resultF == JOptionPane.OK_OPTION){
            System.out.println("process is going to execute.");
        }else{
            System.out.println("process canceled.");
            return;
        }

        JPanel inputPanel = new JPanel(new GridBagLayout());
        JTextField pathField = new JTextField(20);
        JTextField fileNameField = new JTextField(20);
        JTextField fileContent = new JTextField(20);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5); // Optional: Adds some padding around components

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("File Content:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        inputPanel.add(pathField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("File Name:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        inputPanel.add(fileNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(new JLabel("Path to file on the server:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        inputPanel.add(fileContent, gbc);

        String contentFile = pathField.getText();
        String fileName = fileNameField.getText();
        String folder = fileContent.getText();

        String response = myApp.apiConnection.post("putJSONasFile", myApp.userName, myApp.userPassword, String.valueOf(myApp.currentProjectId), folder, contentFile, fileName);
        response = myApp.jsonBeautifier(response);
        response = "API response message: \n" + response;
        myApp.responseArea3.setText(response);
    }
    public void listTablesPage(){
        int resultF = JOptionPane.showConfirmDialog(
                null,
                "You are going to list the tables in project.",
                "Listing the tables in project",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if(resultF == JOptionPane.OK_OPTION){
            System.out.println("process is going to execute.");
        }else{
            System.out.println("process canceled.");
            return;
        }

        System.out.println("project id : " + myApp.currentProjectId);

        String response = myApp.apiConnection.post("listTables", myApp.userName, myApp.userPassword, String.valueOf(myApp.currentProjectId)); // projectDescription is additional
        response = myApp.jsonBeautifier(response);
        response = "API response message: \n" + response;
        myApp.responseArea3.setText(response);

    }*/
}
