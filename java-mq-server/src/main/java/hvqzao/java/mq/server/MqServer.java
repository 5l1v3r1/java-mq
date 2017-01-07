package hvqzao.java.mq.server;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import org.apache.activemq.broker.SslBrokerService;
import org.apache.activemq.broker.SslContext;

public class MqServer {

    public static void main(String[] args) throws Exception {

        // http://activemq.apache.org/how-do-i-use-ssl.html
        // keytool -genkey -alias broker -keyalg RSA -keystore broker.ks
        // password: brokerpw
        // keytool -export -alias broker -keystore broker.ks -file broker.crt
        // keytool -genkey -alias client -keyalg RSA -keystore client.ks
        // password: clientpw
        // keytool -import -alias broker -keystore client.ts -file broker.crt
        //
        // keytool -export -alias client -keystore client.ks -file client.crt
        // keytool -import -alias client -keystore broker.ts -file client.crt
        //
        // https://github.com/apache/activemq/blob/master/activemq-unit-tests/src/test/java/org/apache/activemq/transport/tcp/SslBrokerServiceTest.java#L59
        
        String filename = "broker";
        String password = "brokerpw";
        KeyStore ks = KeyStore.getInstance("jks");
        ks.load(new FileInputStream(new File(filename + ".ks")), password.toCharArray());
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(ks, password.toCharArray());
        KeyStore ts = KeyStore.getInstance("jks");
        ts.load(new FileInputStream(new File(filename + ".ts")), password.toCharArray());
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ts);
        SslContext sslContext = new SslContext(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());

        sslContext.getSSLContext().getDefaultSSLParameters().setNeedClientAuth(true);
        sslContext.getSSLContext().getDefaultSSLParameters().setWantClientAuth(true);

        SslBrokerService broker = new SslBrokerService();
        broker.setSslContext(sslContext);
        //BrokerService broker = new BrokerService();
        //broker.setBrokerName("asdf");
        broker.setUseShutdownHook(false);
        //SimpleAuthenticationPlugin authentication = new SimpleAuthenticationPlugin();
        //List<AuthenticationUser> users = new ArrayList<>();
        ////users.add(new AuthenticationUser("admin", "password1", "admins,publishers,consumers"));
        //users.add(new AuthenticationUser("publisher", "password2", "publishers,consumers"));
        //users.add(new AuthenticationUser("consumer", "password3", "consumers"));
        ////users.add(new AuthenticationUser("guest", "password4", "guests"));
        //authentication.setUsers(users);
        //broker.setPlugins(new BrokerPlugin[]{authentication});
        //broker.addConnector("tcp://127.0.0.1:61616");
        broker.addConnector("ssl://127.0.0.1:61617");
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
