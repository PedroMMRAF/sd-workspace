package trab1.clients;

import java.net.URI;

import trab1.api.java.Users;
import trab1.clients.rest.feeds.RestFeedsClient;
import trab1.clients.soap.feeds.SoapFeedsClient;

public class FeedsClientFactory {
	private static final String REST = "/rest";
	private static final String SOAP = "/soap";

	public static Users get(URI serverURI) {
		var uriString = serverURI.toString();

		if (uriString.endsWith(REST))
			return new RestFeedsClient(serverURI);
		else if (uriString.endsWith(SOAP))
			return new SoapFeedsClient(serverURI);
		else
			throw new RuntimeException("Unknown service type..." + uriString);
	}
}
