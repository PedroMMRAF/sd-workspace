package trab2.clients;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.net.ssl.HttpsURLConnection;

import trab2.Discovery;
import trab2.api.java.Feeds;
import trab2.tls.InsecureHostnameVerifier;

public class FeedsClientFactory {
	private static final String REST = "/rest";
	private static final String SOAP = "/soap";

	private static final Map<String, Feeds> clients = new ConcurrentHashMap<>();

	static {
		HttpsURLConnection.setDefaultHostnameVerifier(new InsecureHostnameVerifier());
	}

	public static Feeds get(String domain) {
		URI serverURI = Discovery.getInstance().knownUrisOf(domain, Feeds.NAME, 1)[0];
		String stringURI = serverURI.toString();

		Feeds client = clients.get(stringURI);

		if (client != null)
			return client;

		if (stringURI.endsWith(REST))
			client = new RestFeedsClient(serverURI);
		else if (stringURI.endsWith(SOAP))
			client = new SoapFeedsClient(serverURI);
		else
			throw new RuntimeException("Unknown service type... " + stringURI);

		clients.put(stringURI, client);

		return client;
	}
}
