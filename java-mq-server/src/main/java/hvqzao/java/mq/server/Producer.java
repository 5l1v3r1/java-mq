package hvqzao.java.mq.server;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import org.apache.activemq.ActiveMQSslConnectionFactory;

class Producer implements Runnable {

    @Override
    public void run() {
        System.out.println("Producer connecting...");
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
            //Connection connection = connectionFactory.createConnection("publisher", "password2");
            Connection connection = connectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue("TEST.FOO");
            MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            while (true) {
                String text = "Hello world! From: " + Thread.currentThread().getName() + " : " + this.hashCode();
                TextMessage message = session.createTextMessage(text);
                System.out.println("Sent message: " + message.hashCode() + " : " + Thread.currentThread().getName());
                producer.send(message);
                Thread.sleep(1000);
            }
            //producer.close();
            //session.close();
            //connection.close();
        } catch (Exception ex) {
            System.out.println("Caught: " + ex);
            //ex.printStackTrace();
        }
    }

}
