package trab1.client.users;

import trab1.Discovery;

import java.net.URI;

public class SearchUserClient {
    public static void main(String[] args) throws InterruptedException {
        if (args.length != 2) {
            System.err.println("Use: java trab1.client.users.SearchUserClient service query");
            return;
        }

        String service = args[0];
        String pattern = args[1];

        String serverUrl = Discovery.getInstance().knownUrisOf(service, 1)[0].toString();

        System.out.println("Sending request to server.");

        new RestUsersClient(URI.create(serverUrl)).searchUsers(pattern);
    }
}
