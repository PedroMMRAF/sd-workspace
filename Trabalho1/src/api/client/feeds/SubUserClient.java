package api.client.feeds;

import api.Discovery;
import api.rest.FeedsService;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import org.glassfish.jersey.client.ClientConfig;

public class SubUserClient {

    public static void main(String[] args) throws InterruptedException {

        if (args.length != 5) {
            System.err.println(
                    "Use: java trab1.client.users.CreateUserClient user userSub pwd");
            return;
        }

        String user = args[0];
        String userSub = args[1];
        String pwd = args[2];
        String[] userInfo = user.split("@");
        String[] userSubInfo = userSub.split("@");

        String serverUrl = Discovery.getInstance().knownUrisOf("service", 1)[0].toString();

        System.out.println("Sending request to server.");

        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient(config);

        WebTarget target = client.target(serverUrl).path(FeedsService.PATH);

        Response response = target.path("/sub/" + userInfo[0] + "/" + userSubInfo[0]).queryParam(FeedsService.PWD, pwd)
                .request().accept(MediaType.APPLICATION_JSON).post(Entity.entity(null, MediaType.APPLICATION_JSON));

        if (response.getStatus() == Status.OK.getStatusCode() && response.hasEntity())
            System.out.printf("Success, created user with id: %s\n",
                    response.readEntity(String.class));
        else
            System.out.printf("Error, HTTP error status: %s\n", response.getStatus());

    }

}
