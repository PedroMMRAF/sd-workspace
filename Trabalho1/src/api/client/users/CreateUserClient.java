package api.client.users;

import api.Discovery;
import api.User;
import api.rest.UsersService;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import org.glassfish.jersey.client.ClientConfig;

public class CreateUserClient {

    public static void main(String[] args) throws InterruptedException {

        if (args.length != 5) {
            System.err.println(
                    "Use: java trab1.client.users.CreateUserClient service userName password " +
                            "domain displayName");
            return;
        }

        String service = args[0];
        String userName = args[1];
        String password = args[2];
        String domain = args[3];
        String displayName = args[4];

        String serverUrl = Discovery.getInstance().knownUrisOf(service, 1)[0].toString();

        User user = new User(userName, password, domain, displayName);

        System.out.println("Sending request to server.");

        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient(config);

        WebTarget target = client.target(serverUrl).path(UsersService.PATH);

        Response response = target.request().accept(MediaType.APPLICATION_JSON).post(
                Entity.entity(user, MediaType.APPLICATION_JSON));

        if (response.getStatus() == Status.OK.getStatusCode() && response.hasEntity())
            System.out.printf("Success, created user with id: %s\n",
                    response.readEntity(String.class));
        else
            System.out.printf("Error, HTTP error status: %s\n", response.getStatus());

    }

}
