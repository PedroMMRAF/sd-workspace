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
import org.glassfish.jersey.client.ClientConfig;

public class UpdateUserClient {
    public static void main(String[] args) throws InterruptedException {
        if (args.length != 6) {
            System.err.println(
                    "Use: java trab1.client.users.UpdateUserClient service userName oldPwd newPwd domain displayName");
            return;
        }

        String service = args[0];
        String userName = args[1];
        String oldPwd = args[2];
        String newPwd = args[3];
        String domain = args[4];
        String displayName = args[5];

        String serverUrl = Discovery.getInstance().knownUrisOf(service, 1)[0].toString();

        User user = new User(null, newPwd, domain, displayName);

        System.out.println("Sending request to server.");

        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient(config);

        WebTarget target = client.target(serverUrl).path(UsersService.PATH);

        Response response = target.path(userName)
                .queryParam(UsersService.PWD, oldPwd)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .put(Entity.entity(user, MediaType.APPLICATION_JSON));

        if (response.getStatus() == Response.Status.OK.getStatusCode() && response.hasEntity()) {
            System.out.printf("Success! Updated user with id: %s\n",
                    response.readEntity(String.class));
        } else {
            System.out.printf("Error, HTTP error status: %s\n",
                    Response.Status.fromStatusCode(response.getStatus()));
        }
    }
}
