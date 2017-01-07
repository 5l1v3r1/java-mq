package hvqzao.java.mq.server;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Connection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import org.apache.activemq.ActiveMQSslConnectionFactory;

class Consumer implements Runnable {

    @Override
    public void run() {
        System.out.println("Consumer connecting...");
        try {
            //ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://127.0.0.1:61616");
            ActiveMQSslConnectionFactory connectionFactory = new ActiveMQSslConnectionFactory("ssl://127.0.0.1:61617");

            String filename = "client";
            String password = "clientpw";
            KeyStore ks = KeyStore.getInstance("jks");
            ks.load(new FileInputStream(new File(filename + ".ks")), password.toCharArray());
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, password.toCharArray());
            KeyStore ts = KeyStore.getInstance("jks");
            ts.load(new FileInputStream(new File(filename + ".ts")), password.toCharArray());
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(ts);

            connectionFactory.setKeyAndTrustManagers(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());
            //Connection connection = connectionFactory.createConnection("consumer", "password3");
            Connection connection = connectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue("TEST.FOO");
            MessageConsumer consumer = session.createConsumer(destination);
            while (true) {
                Message message = consumer.receive();
                if (message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    String text = textMessage.getText();
                    System.out.println("* Received: " + text);
                } else {
                    System.out.println("* Received obj: " + message);
                }
            }
            //consumer.close();
            //session.close();
            //connection.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
