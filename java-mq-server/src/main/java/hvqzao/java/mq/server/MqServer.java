package hvqzao.java.mq.server;

import java.util.ArrayList;
import java.util.List;
import org.apache.activemq.broker.BrokerPlugin;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.SslBrokerService;
import org.apache.activemq.broker.SslContext;
import org.apache.activemq.security.AuthenticationUser;
import org.apache.activemq.security.SimpleAuthenticationPlugin;

public class MqServer {

    public static void main(String[] args) throws Exception {
        SslContext sslContext = new SslContext();
        // http://activemq.apache.org/how-do-i-use-ssl.html
        // keytool -genkey -alias broker -keyalg RSA -keystore broker.ks
        // password: brokerpw
        // keytool -export -alias broker -keystore broker.ks -file broker.crt
        // keytool -genkey -alias client -keyalg RSA -keystore client.ks
        // password: clientpw
        // keytool -import -alias broker -keystore client.ts -file broker.crt
        //SslBrokerService broker = new SslBrokerService();
        // https://github.com/apache/activemq/blob/master/activemq-unit-tests/src/test/java/org/apache/activemq/transport/tcp/SslBrokerServiceTest.java#L59
        //broker.setSslContext(sslContext);
        BrokerService broker = new BrokerService();
        //broker.setBrokerName("asdf");
        broker.setUseShutdownHook(false);
        SimpleAuthenticationPlugin authentication = new SimpleAuthenticationPlugin();
        List<AuthenticationUser> users = new ArrayList<>();
        //users.add(new AuthenticationUser("admin", "password1", "admins,publishers,consumers"));
        users.add(new AuthenticationUser("publisher", "password2", "publishers,consumers"));
        users.add(new AuthenticationUser("consumer", "password3", "consumers"));
        //users.add(new AuthenticationUser("guest", "password4", "guests"));
        authentication.setUsers(users);
        broker.setPlugins(new BrokerPlugin[]{authentication});
        broker.addConnector("tcp://127.0.0.1:61616");
        //broker.addConnector("ssl://127.0.0.1:61613");
        broker.setUseJmx(false);
        broker.setPersistent(false);
        //KahaDBStore kaha = new KahaDBStore();
        //kaha.setDirectory(new File("kaha"));
        //broker.setPersistenceAdapter(kaha);
        broker.start();
        System.out.println("Started...");
        new Thread(new Consumer()).start();
        new Thread(new Producer()).start();
        while (true) {
            Thread.yield();
            Thread.sleep(Integer.MAX_VALUE);
        }
    }

}
