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
		String stringURI = serverURI.toString();

		Users client = clients.get(stringURI);

		if (client != null)
			return client;

		if (stringURI.endsWith(REST))
			client = new RestUsersClient(serverURI);
		else if (stringURI.endsWith(SOAP))
			client = new SoapUsersClient(serverURI);
		else
			throw new RuntimeException("Unknown service type... " + stringURI);

		clients.put(stringURI, client);

		return client;
	}
}
