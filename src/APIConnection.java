import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class APIConnection {
    private final URL apiEndPoint;

    public APIConnection(String endPoint) throws IOException {
        this.apiEndPoint = new URL(endPoint);
    }

    public String sendFileToServer(File selectedFile, String projectId, String email, String password, String folder) {
        try {
            HttpURLConnection connection = (HttpURLConnection) this.apiEndPoint.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");

            String boundary = "*****" + System.currentTimeMillis() + "*****";
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            OutputStream outputStream = connection.getOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), true);

            writer.append("--").append(boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"action\"\r\n\r\n");
            writer.append("putFile").append("\r\n");

            writer.append("--").append(boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"email\"\r\n\r\n");
            writer.append(email).append("\r\n");

            writer.append("--").append(boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"password\"\r\n\r\n");
            writer.append(password).append("\r\n");

            writer.append("--").append(boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"project\"\r\n\r\n");
            writer.append(projectId).append("\r\n");

            writer.append("--").append(boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"folder\"\r\n\r\n");
            writer.append(folder).append("\r\n");

            writer.append("--").append(boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"newFile\"; filename=\"").append(selectedFile.getName()).append("\"\r\n");
            writer.append("Content-Type: text/plain; charset=UTF-8\r\n\r\n");

            BufferedReader fileReader = new BufferedReader(new FileReader(selectedFile));
            String line;
            while ((line = fileReader.readLine()) != null) {
                writer.append(line).append("\r\n");
            }
            fileReader.close();

            writer.append("--").append(boundary).append("--\r\n");
            writer.flush();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String lineRead;
                while ((lineRead = reader.readLine()) != null) {
                    response.append(lineRead);
                }
                reader.close();
                return response.toString();
            } else {
                System.out.println("File upload failed. Response Code: " + responseCode);
                return Integer.toString(responseCode);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to send POST request.", e);
        }
    }

    public String sendFileToServer(File selectedFile, String projectId, String email, String password) {
        try {
            HttpURLConnection connection = (HttpURLConnection) this.apiEndPoint.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");

            String boundary = "*****" + System.currentTimeMillis() + "*****";
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            OutputStream outputStream = connection.getOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), true);

            writer.append("--").append(boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"action\"\r\n\r\n");
            writer.append("putFile").append("\r\n");

            writer.append("--").append(boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"email\"\r\n\r\n");
            writer.append(email).append("\r\n");

            writer.append("--").append(boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"password\"\r\n\r\n");
            writer.append(password).append("\r\n");

            writer.append("--").append(boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"project\"\r\n\r\n");
            writer.append(projectId).append("\r\n");

            writer.append("--").append(boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"newFile\"; filename=\"").append(selectedFile.getName()).append("\"\r\n");
            writer.append("Content-Type: text/plain; charset=UTF-8\r\n\r\n");

            BufferedReader fileReader = new BufferedReader(new FileReader(selectedFile));
            String line;
            while ((line = fileReader.readLine()) != null) {
                writer.append(line).append("\r\n");
            }
            fileReader.close();

            writer.append("--").append(boundary).append("--\r\n");
            writer.flush();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String lineRead;
                while ((lineRead = reader.readLine()) != null) {
                    response.append(lineRead);
                }
                System.out.println("File upload completed. Response Code: " + responseCode);
                System.out.println("File upload completed. Response Message: " + response);
                reader.close();
                return response.toString();
            } else {
                System.out.println("File upload failed. Response Code: " + responseCode);
                return Integer.toString(responseCode);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to send POST request.", e);
        }
    }


    private byte[] getFileContentFromServer(JSONObject postParams) {
        try {
            HttpURLConnection con = (HttpURLConnection) this.apiEndPoint.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            try (OutputStream os = con.getOutputStream()) {
                os.write(postParams.toString().getBytes());
                os.flush();
            }

            int responseCode = con.getResponseCode();
            System.out.println("POST Response Code: " + responseCode);
            System.out.println("POST Response Message: " + con.getResponseMessage());

            if (responseCode == 200) { // success
                try (InputStream is = con.getInputStream()) {
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    byte[] data = new byte[1024];
                    int nRead;
                    while ((nRead = is.read(data, 0, data.length)) != -1) {
                        buffer.write(data, 0, nRead);
                    }
                    buffer.flush();
                    return buffer.toByteArray();
                }
            } else {
                System.out.println("POST Request Failed with Response Code: " + responseCode);
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to send POST request.", e);
        }
    }


    private String sendPostRequest(JSONObject postParams) {
        try {
            HttpURLConnection con = (HttpURLConnection) this.apiEndPoint.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            try (OutputStream os = con.getOutputStream()) {
                os.write(postParams.toString().getBytes());
                os.flush();
            }

            int responseCode = con.getResponseCode();
            System.out.println("POST Response Code: " + responseCode);
            System.out.println("POST Response Message: " + con.getResponseMessage());

            if (responseCode == 200) { // success
                try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    System.out.println("POST Response: " + response);
                    return response.toString();
                }
            } else {
                System.out.println("POST Request Failed with Response Code: " + responseCode);
                return Integer.toString(responseCode);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to send POST request.", e);
        }
    }

    public String post(String action, String email, String password) {
        JSONObject postParams = new JSONObject();
        postParams.put("action", action);
        postParams.put("email", email);
        postParams.put("password", password);
        return sendPostRequest(postParams);
    }


    public String post(String action, String email, String password, String param1) {
        JSONObject postParams = new JSONObject();
        postParams.put("action", action);
        postParams.put("email", email);
        postParams.put("password", password);
        if (action.equals("addProject")) {
            postParams.put("projectName", param1);
        } else if (action.equals("listFiles") || action.equals("listUsersInProject") || action.equals("listTables")) {
            postParams.put("project", param1);
        }
        return sendPostRequest(postParams);
    }

    public byte[] postX(String action, String email, String password, String param1, String param2) {
        JSONObject postParams = new JSONObject();
        postParams.put("action", action);
        postParams.put("email", email);
        postParams.put("password", password);
        postParams.put("project", param1);
        postParams.put("file", param2);

        return getFileContentFromServer(postParams);
    }

    public String post(String action, String email, String password, String param1, String param2) {
        JSONObject postParams = new JSONObject();
        postParams.put("action", action);
        postParams.put("email", email);
        postParams.put("password", password);

        switch (action) {
            case "getUserPrivilegesInProject":
            case "removeUserFromProject":
                postParams.put("project", param1);
                postParams.put("userid", param2);
                break;
            case "addProject":
                postParams.put("projectName", param1);
                postParams.put("description", param2);
                break;
            case "getTableRows":
                postParams.put("project", param1);
                postParams.put("table", param2);
                break;
            case "deleteFile":
                postParams.put("project", param1);
                postParams.put("name", param2);
                break;
            case "listFiles":
                postParams.put("project", param1);
                postParams.put("path", param2);
                break;
            case "getFile":
                postParams.put("project", param1);
                postParams.put("file", param2);
                break;
            default:
                // Handle default case if needed
        }

        return sendPostRequest(postParams);
    }

    public String post(String action, String email, String password, String param1, String param2, String param3) {
        JSONObject postParams = new JSONObject();
        postParams.put("action", action);
        postParams.put("email", email);
        postParams.put("password", password);

        switch (action) {
            case "addUser":
                postParams.put("newEmail", param1);
                postParams.put("newName", param2);
                postParams.put("newPass", param3);
                break;
            case "putFile":
                postParams.put("project", param1);
                postParams.put("path", param2);
                postParams.put("newFile", param3);
                break;
            case "putTableRows":
                postParams.put("project", param1);
                postParams.put("table", param2);
                postParams.put("rows", param3);
                break;
            default:
                // Handle default case if needed
        }

        return sendPostRequest(postParams);
    }

    public String post(String action, String email, String password, String projectId, String userId, boolean modify, boolean create, boolean delete) {
        JSONObject postParams = new JSONObject();
        postParams.put("action", action);
        postParams.put("email", email);
        postParams.put("password", password);
        postParams.put("project", projectId);
        postParams.put("userid", userId);
        postParams.put("modify", modify);
        postParams.put("create", create);
        postParams.put("delete", delete);
        return sendPostRequest(postParams);
    }
}