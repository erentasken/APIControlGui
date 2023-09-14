import com.rabbitmq.client.*;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class MessageBroker {
    private static final String HOST = "localhost";
    public static final String EXCHANGE_NAME = "__FileUpdate__";
    private static final int DELAY_MS = 1500;
    private static String queueNameInp;
    private static boolean goToElse = false;
    private static boolean initialListening = true;
    public static Channel channel;
    public static Connection connection;
    private static final ConnectionFactory factory;
    public static List<JCheckBox> topicList;
    public static List<String> queueList;

    static {
        queueList = new ArrayList<String>();
        topicList = new ArrayList<JCheckBox>();
        factory = new ConnectionFactory();
        factory.setHost(HOST);

        try {
            connection = factory.newConnection();
        } catch (IOException | TimeoutException e) {
            handleConnectionError(e);
        }

        setupConnection();
        stringToCheckboxList();
    }

    private static void handleConnectionError(Exception e) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, "RabbitMQ Client is not running. ", "Info", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    public static void setupConnection() {
        try {
            assert connection != null;
            channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, "topic");

            for (JLabel label : SpecificProjectPage.labelList) {
                System.out.println("setupConnection listening setup : " + label.getText() + "is started to being listening" + "size of list : " + SpecificProjectPage.labelList.size());
                ListenTopic(label.getText());
            }

        } catch (IOException ex) {
            handleConnectionError(ex);
        }
    }

    public static void stringToCheckboxList() {
        if (!topicList.isEmpty()) {
            topicList.clear();
        }
        List<String> fileList = returnFileList();
        for (String str : fileList) {
            topicList.add(new JCheckBox(str));
        }
    }

    private static List<String> returnFileList() {
        String response = MainPage.action.myApp.apiConnection.post("listFiles", MainPage.userName, MainPage.userPassword, String.valueOf(SpecificProjectPage.currentProjectId));

        JSONArray jsonArray = new JSONArray(response);

        List<String> pathsList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String path = jsonObject.getString("path");
            pathsList.add(path);
        }

        return pathsList;
    }

    public static void SendMessage(String fileName) throws IOException {
        if (channel == null) {
            return;
        }
        if (!channel.isOpen()) {
            setupConnection();
        }

        String message = fileName + " is updated .";

        String routingKey = createQueue(fileName);

        channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes(StandardCharsets.UTF_8));
    }

    private static String createQueue(String fileName) {
        String declaredQueue = fileName + "Queue";
        String routingKey = SpecificProjectPage.currentProjectId + fileName;
        try {
            channel.queueDeclare(declaredQueue, false, false, false, null);
            channel.queueBind(declaredQueue, EXCHANGE_NAME, routingKey);
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        } catch (AlreadyClosedException err) {
            setupConnection();
        }
        return routingKey;
    }

    public static void ListenTopic(String fileName) {
        if (!channel.isOpen()) {
            setupConnection();
        }

        queueNameInp = fileName + "Queue";

        createQueue(fileName);

        boolean containsRoutingKey = false;
        for (JCheckBox checkBox : topicList) {
            if (checkBox.getText().equals(fileName)) {
                containsRoutingKey = true;
                break;
            }
        }

        if (!containsRoutingKey) {
            JOptionPane.showMessageDialog(null, "The subscription : " + fileName + " is added .", "Info", JOptionPane.INFORMATION_MESSAGE);
            topicList.add(new JCheckBox(fileName));
        }

        StringBuilder messageBuilder = new StringBuilder();
        Thread delayThread = new Thread(() -> {
            try {
                Thread.sleep(DELAY_MS);
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(null, messageBuilder.toString(), "Info", JOptionPane.INFORMATION_MESSAGE);
                });
                goToElse = true;

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            if (!goToElse) {
                messageBuilder.append(message).append(queueNameInp).append("\n");
                if (initialListening) {
                    delayThread.start();
                    initialListening = false;
                }
            } else {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(null, message, "Info", JOptionPane.INFORMATION_MESSAGE);
                });
            }
        };

        try {
            channel.basicConsume(queueNameInp, true, deliverCallback, consumerTag -> {
            });
            queueList.add(queueNameInp);
        } catch (IOException | AlreadyClosedException er) {
            System.out.println("could not listen the channel " + queueNameInp);
        }

    }
}
