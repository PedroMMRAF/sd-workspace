package aula2.clients;

import aula2.api.service.RestUsers;
import aula2.server.Discovery;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientConfig;

import java.io.IOException;
import java.net.URI;

public class DeleteUserClient {

    public static void main(String[] args) throws InterruptedException {

        if (args.length != 3) {
            System.err.println("Use: java aula2.clients.DeleteUserClient service userId password");
            return;
        }

        String service = args[0];
        String userId = args[1];
        String password = args[2];

        String serverUrl = Discovery.getInstance().knownUrisOf(service, 1)[0].toString();

        System.out.println("Sending request to server.");

        var config = new ClientConfig();
        var client = ClientBuilder.newClient(config);

        var target = client.target(serverUrl).path(RestUsers.PATH);

        var response = target.path(userId)
                .queryParam(RestUsers.PASSWORD, password)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .delete();

        if (response.getStatus() == Response.Status.OK.getStatusCode() && response.hasEntity())
            System.out.printf("Success, deleted user with id: %s\n",
                    response.readEntity(String.class));
        else
            System.out.printf("Error, HTTP error status: %s\n",
                    Response.Status.fromStatusCode(response.getStatus()));
    }

}
