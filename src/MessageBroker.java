import com.rabbitmq.client.*;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

public class MessageBroker {
    private static DeliverCallback deliverCallback;
    private static String queueNameInp;
    private static boolean goToElse = false;
    private static boolean initialListening = true;
    public static Channel channel;
    private static final ConnectionFactory factory;
    public static final String EXCHANGE_NAME = "__FileUpdate__";
    public static String queueName = "Queue";

    public static List<Channel> channelList; // delete it
    public static List<JCheckBox> topicList;
    public static List<String> queueList;

    static {
        queueList = new ArrayList<String>();
        channelList = new ArrayList<Channel>();
        topicList = new ArrayList<JCheckBox>();
        factory = new ConnectionFactory();
        factory.setHost("localhost");
        setupConnection();
        stringToCheckboxList();
    }
    public static void setupConnection(){
        try {
            System.out.println("Connection is creating ");
            Connection connection = factory.newConnection();
            channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, "topic");
            channelList.add(channel);
            if(!initialListening) {
                for(JLabel label : SpecificProjectPage.labelList){
                    ListenTopic(label.getText());
                }
            }
        } catch (IOException | TimeoutException ex) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, "RabbitMQ Client is not running. ", "Info", JOptionPane.INFORMATION_MESSAGE);
            });
        }
    }

    private static void stringToCheckboxList(){
        List<String> fileList = returnFileList();
        for(String str : fileList){
            topicList.add(new JCheckBox(str));
        }
    }

    private static List<String> returnFileList(){
        String response = MainPage.action.myApp.apiConnection.post("listFiles", MainPage.userName, MainPage.userPassword, String.valueOf(SpecificProjectPage.currentProjectId));

        JSONArray jsonArray = new JSONArray(response);

        List<String> pathsList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String path = jsonObject.getString("path");
            pathsList.add(path);
        }

        System.out.println( " this is the list : " + pathsList );

        return pathsList;
    }

    public static void SendMessage(String fileName) throws IOException {
        if(channel == null ) {
            System.out.println("channel is null ");
            return;
        }
        if(!channel.isOpen()){
            setupConnection();
        }

        String message =  fileName + " is updated .";

        String routingKey = createQueue(fileName);

        channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes(StandardCharsets.UTF_8));
    }

    private static String createQueue(String fileName){
        String declaredQueue = fileName + "Queue";
        String routingKey = SpecificProjectPage.currentProjectId + fileName;
        try {
            assert channel != null;
            channel.queueDeclare(declaredQueue, false, false, false, null);
            channel.queueBind(declaredQueue, EXCHANGE_NAME, routingKey);
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        } catch (AlreadyClosedException err ) {
            System.out.println("create queue exception ");
            setupConnection();
        }

        return routingKey;
    }


    public static void ListenTopic(String fileName){
        if(!channel.isOpen()){
            System.out.println("Channel is not open, so setupConnection() executed.");
            setupConnection();
        }

        queueNameInp = fileName + "Queue";

        try {
            channel.queueDeclarePassive(queueNameInp);
        } catch (Exception ex) {
            System.out.println("hello there. ");
            setupConnection();
            createQueue(fileName);
            System.out.println("Connection alive :" + channel.isOpen());
        }

        boolean containsRoutingKey = false;
        for (JCheckBox checkBox : topicList) {
            System.out.println(checkBox.getText());
            if (checkBox.getText().equals(fileName)) {
                containsRoutingKey = true;
                break;
            }
        }

        if (!containsRoutingKey) {
            JOptionPane.showMessageDialog(null, "The subscription : " + fileName + " is added .", "Info", JOptionPane.INFORMATION_MESSAGE);
            topicList.add(new JCheckBox(fileName));
        }

        StringBuilder messageBuilder = new StringBuilder();;
        Thread delayThread = new Thread( ()-> {
            try {
                Thread.sleep(1500);
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(null, messageBuilder.toString(), "Info", JOptionPane.INFORMATION_MESSAGE);
                });
                goToElse = true;

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            if (!goToElse) {
                messageBuilder.append(message).append(queueNameInp).append("\n");
                if(initialListening) {
                    System.out.println("in thread.");
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
            channel.basicConsume(queueNameInp, true, deliverCallback, consumerTag -> {});
            System.out.println("started to listen the file: " + fileName);
            queueList.add(queueNameInp);
        } catch (IOException ex ) {
            throw new RuntimeException(ex);
        } catch (AlreadyClosedException err ){
            System.out.println("connection has closed. queue name : " + queueNameInp);
        }
    }
}
