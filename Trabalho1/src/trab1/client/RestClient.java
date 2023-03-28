package trab1.client;

import java.net.URI;
import java.util.function.Supplier;
import java.util.logging.Logger;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import trab1.Discovery;

public class RestClient {
	private static Logger Log = Logger.getLogger(RestClient.class.getName());

	protected static final int READ_TIMEOUT = 5000;
	protected static final int CONNECT_TIMEOUT = 5000;

	protected static final int RETRY_SLEEP = 3000;
	protected static final int MAX_RETRIES = 10;

	protected final URI serverURI;
	protected final Client client;
	protected final ClientConfig config;

	public RestClient(String service) {
		String serverURL = Discovery.getInstance().knownUrisOf(service, 1)[0].toString();

		this.serverURI = URI.create(serverURL);
		this.config = new ClientConfig();

		this.config.property(ClientProperties.READ_TIMEOUT, READ_TIMEOUT);
		this.config.property(ClientProperties.CONNECT_TIMEOUT, CONNECT_TIMEOUT);

		this.client = ClientBuilder.newClient(config);
	}

	protected <T> T retry(Supplier<T> func) {
		for (int i = 0; i < MAX_RETRIES; i++) {
			try {
				return func.get();
			} catch (ProcessingException x) {
				System.err.println(x.getMessage());
				Log.fine("ProcessingException: " + x.getMessage());
				sleep(RETRY_SLEEP);
			} catch (Exception x) {
				Log.fine("Exception: " + x.getMessage());
				x.printStackTrace();
				break;
			}
		}
		return null;
	}

	private void sleep(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException x) { // nothing to do...
		}
	}
}
