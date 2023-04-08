package trab1.clients;

import java.net.URI;

import trab1.api.java.Users;
import trab1.clients.rest.users.RestUsersClient;
import trab1.clients.soap.users.SoapUsersClient;

public class UsersClientFactory {
	private static final String REST = "/rest";
	private static final String SOAP = "/soap";

	public static Users get(URI serverURI) {
		var uriString = serverURI.toString();

		if (uriString.endsWith(REST))
			return new RestUsersClient(serverURI);
		else if (uriString.endsWith(SOAP))
			return new SoapUsersClient(serverURI);
		else
			throw new RuntimeException("Unknown service type..." + uriString);
	}
}
