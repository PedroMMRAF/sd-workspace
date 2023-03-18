package api.client.users;

import api.Discovery;
import api.User;
import api.rest.UsersService;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import org.glassfish.jersey.client.ClientConfig;

public class GetUserClient {


    public static void main(String[] args) throws InterruptedException {

        if (args.length != 3) {
            System.err.println(
                    "Use: java trab1.client.users.GetUserClient service userName password");
            return;
        }

        String service = args[0];
        String userName = args[1];
        String password = args[2];

        String serverUrl = Discovery.getInstance().knownUrisOf(service, 1)[0].toString();

        System.out.println("Sending request to server.");

        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient(config);

        WebTarget target = client.target(serverUrl).path(UsersService.PATH);

        Response r = target.path(userName).queryParam(UsersService.PWD, password).request().accept(
                MediaType.APPLICATION_JSON).get();

        if (r.getStatus() == Status.OK.getStatusCode() && r.hasEntity()) {
            System.out.println("Success:");
            User user = r.readEntity(User.class);
            System.out.printf("User: %s\n", user);
        }
        else
            System.out.printf("Error, HTTP error status: %s\n", r.getStatus());

    }

}
