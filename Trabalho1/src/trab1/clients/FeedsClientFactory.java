package trab1.clients;

import java.net.URI;

import trab1.Discovery;
import trab1.api.java.Feeds;

public class FeedsClientFactory {
	private static final String REST = "/rest";
	private static final String SOAP = "/soap";

	public static Feeds get(String domain) {
		URI serverURI = Discovery.getInstance().knownUrisOf(domain, Feeds.NAME, 1)[0];
		Discovery.getInstance().kill();

		if (serverURI.toString().endsWith(REST))
			return new RestFeedsClient(serverURI);
		else if (serverURI.toString().endsWith(SOAP))
			return new SoapFeedsClient(serverURI);
		else
			throw new RuntimeException("Unknown service type..." + serverURI);
	}
}
