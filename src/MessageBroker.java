import com.rabbitmq.client.*;

import javax.swing.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

public class MessageBroker {
    private static DeliverCallback deliverCallback;
    private static String queueNameInp;
    private static boolean goToElse = false;
    private static boolean initialListening = true;
    private static Channel channel;
    private static final ConnectionFactory factory;
    public static final String EXCHANGE_NAME = "__FileUpdate__";
    public static String queueName = "Queue";

    private static AtomicBoolean gotoElseBool;

    public static List<String> topicList;

    static {
        topicList = new ArrayList<String>();
        factory = new ConnectionFactory();
        factory.setHost("localhost");
        gotoElseBool = new AtomicBoolean(true);
        setupConnection();
    }
    public static void setupConnection(){
        try {

            Connection connection = factory.newConnection();
            channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, "topic");
            if(!initialListening) {
                String nameQueue;
                for(String str : topicList){
                    nameQueue = str + "Queue";
                    channel.basicConsume(nameQueue, true, deliverCallback, consumerTag -> {});
                }
            }
        } catch (IOException | TimeoutException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void SendMessage(String fileName) throws IOException {
        if(!channel.isOpen()){
            setupConnection();
        }

        String routingKey = SpecificProjectPage.currentProjectId + fileName;
        String message =  fileName + " is updated .";

        String declearedQueue = fileName + "Queue";
        try {
            channel.queueDeclare(declearedQueue, false, false, false, null);
            channel.queueBind(declearedQueue, EXCHANGE_NAME, routingKey);
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        } catch (AlreadyClosedException err ) {
            setupConnection();
        }
        channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes(StandardCharsets.UTF_8));
    }

    public static void ListenTopic(String routingKey){
        queueNameInp = routingKey + MessageBroker.queueName;
        if(!channel.isOpen()){
            setupConnection();
        }

        try {
            channel.queueDeclarePassive(queueNameInp);
        } catch (IOException ex) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, "File does not exist with name first exp: " + routingKey, "Info", JOptionPane.INFORMATION_MESSAGE);
            });
            System.out.println("Connection alive :" + channel.isOpen());
            return;
        }

        if(!topicList.contains(routingKey)) {
            JOptionPane.showMessageDialog(null, "The subscription : " + routingKey + " is added .", "Info", JOptionPane.INFORMATION_MESSAGE);
            topicList.add(routingKey);
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
        } catch (IOException ex ) {
            throw new RuntimeException(ex);
        } catch (AlreadyClosedException err ){
            System.out.println("connection has closed. queue name : " + queueNameInp);
        }
    }
}
