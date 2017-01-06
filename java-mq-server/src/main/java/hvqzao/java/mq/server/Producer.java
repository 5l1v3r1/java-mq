package hvqzao.java.mq.server;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnectionFactory;

class Producer implements Runnable {

    @Override
    public void run() {
        System.out.println("Producer connecting...");
        try {
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://127.0.0.1:61616"); // apparently the vm part is all i need
            Connection connection = connectionFactory.createConnection("publisher", "password2");
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
        } catch (InterruptedException | JMSException ex) {
            System.out.println("Caught: " + ex);
            //ex.printStackTrace();
        }
    }

}
