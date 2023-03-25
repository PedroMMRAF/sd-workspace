package aula2.clients;

import aula2.api.User;
import aula2.api.service.RestUsers;
import aula2.server.Discovery;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import org.glassfish.jersey.client.ClientConfig;

import java.io.IOException;
import java.util.List;

public class SearchUserClient {

    public static void main(String[] args) throws InterruptedException {

        if (args.length != 2) {
            System.err.println("Use: java aula2.clients.SearchUserClient service query");
            return;
        }

        String service = args[0];
        String query = args[1];

        String serverUrl = Discovery.getInstance().knownUrisOf(service, 1)[0].toString();

        System.out.println("Sending request to server.");

        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient(config);

        WebTarget target = client.target(serverUrl).path(RestUsers.PATH);

        Response r = target.path("/")
				.queryParam(RestUsers.QUERY, query)
				.request()
				.accept(MediaType.APPLICATION_JSON).get();

        if (r.getStatus() == Status.OK.getStatusCode() && r.hasEntity()) {
            var users = r.readEntity(new GenericType<List<User>>() {});
            System.out.println("Success: (" + users.size() + " users)");
            users.forEach(System.out::println);
        }
        else
            System.out.println("Error, HTTP error status: " + r.getStatus());

    }

}
