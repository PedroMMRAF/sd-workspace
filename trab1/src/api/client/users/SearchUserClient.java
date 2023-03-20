package api.client.users;

import api.Discovery;
import api.User;
import api.rest.UsersService;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import org.glassfish.jersey.client.ClientConfig;

import java.util.List;

public class SearchUserClient {

    public static void main(String[] args) throws InterruptedException {

        if (args.length != 2) {
            System.err.println("Use: java trab1.client.users.SearchUserClient service query");
            return;
        }

        String service = args[0];
        String query = args[1];

        String serverUrl = Discovery.getInstance().knownUrisOf(service, 1)[0].toString();

        System.out.println("Sending request to server.");

        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient(config);

        WebTarget target = client.target(serverUrl).path(UsersService.PATH);

        Response response = target.path("/")
				.queryParam(UsersService.QUERY, query)
				.request()
				.accept(MediaType.APPLICATION_JSON).get();

        if (response.getStatus() == Status.OK.getStatusCode() && response.hasEntity()) {
            List<User> users = response.readEntity(new GenericType<List<User>>() {});
            System.out.printf("Success: (%d users)\n", users.size());
            users.forEach(System.out::println);
        }
        else
            System.out.printf("Error, HTTP error status: %s\n", response.getStatus());

    }

}
