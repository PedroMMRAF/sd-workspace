package api.client.users;

import api.Discovery;
import api.rest.UsersService;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientConfig;

public class DeleteUserClient {

    public static void main(String[] args) throws InterruptedException {

        if (args.length != 3) {
            System.err.println(
                    "Use: java trab1.client.users.DeleteUserClient service userName password");
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

        Response response = target.path(userName).queryParam(UsersService.PWD,
                password).request().accept(MediaType.APPLICATION_JSON).delete();

        if (response.getStatus() == Response.Status.OK.getStatusCode() && response.hasEntity())
            System.out.printf("Success, deleted user with id: %s\n",
                    response.readEntity(String.class));
        else
            System.out.printf("Error, HTTP error status: %s\n",
                    Response.Status.fromStatusCode(response.getStatus()));
    }

}
