package trab2.servers.rest;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import trab2.Discovery;
import trab2.servers.Domain;

import java.net.InetAddress;
import java.net.URI;
import java.util.logging.Logger;

import javax.net.ssl.SSLContext;

public class RestFeedsServer {

    public static final int PORT = 8080;
    public static final String SERVICE = "feeds";
    private static final String SERVER_URI_FMT = "https://%s:%s/rest";
    private static final Logger Log = Logger.getLogger(RestFeedsServer.class.getName());

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s\n");
    }

    public static void main(String[] args) {
        try {
            Domain.set(args[0]);

            ResourceConfig config = new ResourceConfig();
            config.register(RestFeedsResource.class);

            String ip = InetAddress.getLocalHost().getHostAddress();
            String serverURI = String.format(SERVER_URI_FMT, ip, PORT);
            JdkHttpServerFactory.createHttpServer(URI.create(serverURI), config, SSLContext.getDefault());

            Log.info(String.format("%s Server ready @ %s\n", SERVICE, serverURI));

            Discovery.getInstance().announce(Domain.get(), SERVICE, serverURI);
        } catch (Exception e) {
            Log.severe(e.getMessage());
        }
    }
}
