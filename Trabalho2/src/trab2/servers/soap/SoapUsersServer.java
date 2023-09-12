package trab2.servers.soap;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLContext;

import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;

import jakarta.xml.ws.Endpoint;
import trab2.Discovery;
import trab2.servers.Domain;

public class SoapUsersServer {
    public static final int PORT = 8081;
    public static final String SERVICE_NAME = "users";
    public static String SERVER_BASE_URI = "https://%s:%s/soap";

    private static Logger Log = Logger.getLogger(SoapUsersServer.class.getName());

    public static void main(String[] args) throws Exception {
        Domain.set(args[0], 0, null);

        // System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump",
        // "true");
        // System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump",
        // "true");
        // System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dump", "true");
        // System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dump",
        // "true");

        Log.setLevel(Level.INFO);

        String ip = InetAddress.getLocalHost().getHostName();
        String serverURI = String.format(SERVER_BASE_URI, ip, PORT);

        var server = HttpsServer.create(new InetSocketAddress(ip, PORT), 0);

        server.setExecutor(Executors.newCachedThreadPool());
        server.setHttpsConfigurator(new HttpsConfigurator(SSLContext.getDefault()));

        var endpoint = Endpoint.create(new SoapUsersWebService());
        endpoint.publish(server.createContext("/soap"));

        server.start();

        Log.info(String.format("%s Soap Server ready @ %s\n", SERVICE_NAME, serverURI));

        Discovery.getInstance().announce(Domain.domain(), SERVICE_NAME, serverURI);
    }
}
