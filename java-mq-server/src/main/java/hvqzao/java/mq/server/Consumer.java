package hvqzao.java.mq.server;

import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Connection;
import javax.jms.JMSException;
import org.apache.activemq.ActiveMQConnectionFactory;

class Consumer implements Runnable {

    @Override
    public void run() {
        System.out.println("Consumer connecting...");
        try {
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://127.0.0.1:61616");
            Connection connection = connectionFactory.createConnection("consumer", "password3"); // exception happens here...
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
        } catch (JMSException ex) {
            System.out.println("Caught: " + ex);
            //ex.printStackTrace();
        }
    }

}
