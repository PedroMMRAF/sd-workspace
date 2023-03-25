package aula2.clients;

import java.io.IOException;

import aula2.server.Discovery;
import org.glassfish.jersey.client.ClientConfig;

import aula2.api.User;
import aula2.api.service.RestUsers;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

public class GetUserClient {


	public static void main(String[] args) throws InterruptedException {
		
		if( args.length != 3) {
			System.err.println( "Use: java aula2.clients.GetUserClient service userId password");
			return;
		}
		
		String service = args[0];
		String userId = args[1];
		String password = args[2];

		String serverUrl = Discovery.getInstance().knownUrisOf(service, 1)[0].toString();
		
		System.out.println("Sending request to server.");
		
		ClientConfig config = new ClientConfig();
		Client client = ClientBuilder.newClient(config);
		
		WebTarget target = client.target( serverUrl ).path( RestUsers.PATH );
		
		Response r = target.path( userId )
				.queryParam(RestUsers.PASSWORD, password).request()
				.accept(MediaType.APPLICATION_JSON)
				.get();

		if( r.getStatus() == Status.OK.getStatusCode() && r.hasEntity() ) {
			System.out.println("Success:");
			var u = r.readEntity(User.class);
			System.out.println( "User : " + u);
		} else
			System.out.println("Error, HTTP error status: " + r.getStatus() );

	}
	
}
