package trab1.client.users;

import trab1.Discovery;

import java.net.URI;

public class GetUserClient {
    public static void main(String[] args) throws InterruptedException {
        if (args.length != 3) {
            System.err.println(
                    "Use: java trab1.client.users.GetUserClient service name pwd");
            return;
        }

        String service = args[0];
        String name = args[1];
        String pwd = args[2];

        String serverUrl = Discovery.getInstance().knownUrisOf(service, 1)[0].toString();

        System.out.println("Sending request to server.");

        new RestUsersClient(URI.create(serverUrl)).getUser(name, pwd);
    }
}
