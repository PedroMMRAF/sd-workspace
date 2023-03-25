package aula2.clients;

import java.io.IOException;

import aula2.api.User;
import aula2.api.service.RestUsers;
import aula2.server.Discovery;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientConfig;

public class UpdateUserClient {

    private static WebTarget response;

    public static void main(String[] args) throws InterruptedException {

        if (args.length != 6) {
            System.err.println(
                    "Use: java aula2.clients.UpdateUserClient service userId oldPwd fullName email password");
            return;
        }

        String service = args[0];
        String userId = args[1];
        String oldPwd = args[2];
        String fullName = args[3];
        String email = args[4];
        String password = args[5];

        String serverUrl = Discovery.getInstance().knownUrisOf(service, 1)[0].toString();

        var user = new User(userId, fullName, email, password);

        System.out.println("Sending request to server.");

        var config = new ClientConfig();
        var client = ClientBuilder.newClient(config);

        var target = client.target(serverUrl).path(RestUsers.PATH);

        var response = target.path(userId)
                .queryParam(RestUsers.PASSWORD, oldPwd)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .put(Entity.entity(user, MediaType.APPLICATION_JSON));

        if (response.getStatus() == Response.Status.OK.getStatusCode() && response.hasEntity()) {
            System.out.printf("Success! Updated user with id: %s\n",
                    response.readEntity(String.class));
        }
        else {
            System.out.printf("Error, HTTP error status: %s\n",
                    Response.Status.fromStatusCode(response.getStatus()));
        }
    }

}
