package trab1.clients;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import trab1.Discovery;
import trab1.api.java.Users;

public class UsersClientFactory {
	private static final String REST = "/rest";
	private static final String SOAP = "/soap";

	private static final Map<String, Users> clients = new ConcurrentHashMap<>();

	public static Users get(String domain) {
		URI serverURI = Discovery.getInstance().knownUrisOf(domain, Users.NAME, 1)[0];

		Users client = clients.get(serverURI.toString());

		if (client != null)
			return client;

		if (serverURI.toString().endsWith(REST))
			client = new RestUsersClient(serverURI);
		else if (serverURI.toString().endsWith(SOAP))
			client = new SoapUsersClient(serverURI);
		else
			throw new RuntimeException("Unknown service type... " + serverURI);

		clients.put(serverURI.toString(), client);

		return client;
	}
}
