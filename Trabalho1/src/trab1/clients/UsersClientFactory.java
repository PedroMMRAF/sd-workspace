package trab1.clients;

import java.net.URI;

import trab1.Discovery;
import trab1.api.java.Users;

public class UsersClientFactory {
	private static final String REST = "/rest";
	private static final String SOAP = "/soap";

	public static Users get(String domain) {
		URI serverURI = Discovery.getInstance().knownUrisOf(domain, Users.NAME, 1)[0];
		Discovery.getInstance().kill();

		if (serverURI.toString().endsWith(REST))
			return new RestUsersClient(serverURI);
		else if (serverURI.toString().endsWith(SOAP))
			return new SoapUsersClient(serverURI);
		else
			throw new RuntimeException("Unknown service type..." + serverURI);
	}
}
