package trab1.clients;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import trab1.Discovery;
import trab1.api.java.Feeds;

public class FeedsClientFactory {
	private static final String REST = "/rest";
	private static final String SOAP = "/soap";

	private static final Map<String, Feeds> clients = new ConcurrentHashMap<>();

	public static Feeds get(String domain) {
		URI serverURI = Discovery.getInstance().knownUrisOf(domain, Feeds.NAME, 1)[0];

		Feeds client = clients.get(serverURI.toString());

		if (client != null)
			return client;

		if (serverURI.toString().endsWith(REST))
			client = new RestFeedsClient(serverURI);
		else if (serverURI.toString().endsWith(SOAP))
			client = new SoapFeedsClient(serverURI);
		else
			throw new RuntimeException("Unknown service type... " + serverURI);

		clients.put(serverURI.toString(), client);

		return client;
	}
}
