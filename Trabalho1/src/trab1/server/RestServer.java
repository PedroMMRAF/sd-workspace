package trab1.server;

import java.util.logging.Logger;
import java.util.function.Supplier;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.ClientBuilder;

import trab1.Discovery;

public class RestServer {
    private static Logger Log = Logger.getLogger(RestServer.class.getName());

    protected static final int READ_TIMEOUT = 5000;
    protected static final int CONNECT_TIMEOUT = 5000;

    protected static final int RETRY_SLEEP = 3000;
    protected static final int MAX_RETRIES = 10;

    protected final Client client;
    protected final ClientConfig config;

    public RestServer() {
        this.config = new ClientConfig();

        this.config.property(ClientProperties.READ_TIMEOUT, READ_TIMEOUT);
        this.config.property(ClientProperties.CONNECT_TIMEOUT, CONNECT_TIMEOUT);

        this.client = ClientBuilder.newClient(config);
    }

    public WebTarget getServiceTarget(String domain, String service) {
        return client.target(Discovery.getInstance().knownUrisOf(domain, service, 1)[0].toString());
    }

    protected <T> T retry(Supplier<T> func) {
        for (int i = 0; i < MAX_RETRIES; i++) {
            try {
                return func.get();
            } catch (ProcessingException x) {
                System.err.println(x.getMessage());
                Log.fine("ProcessingException: " + x.getMessage());
                sleep(RETRY_SLEEP);
            }
        }
        return null;
    }

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException x) {
        }
    }
}
