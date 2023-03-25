package aula3.clients;

import java.net.URI;


public class SearchUsersClient {
    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Use: java aula3.clients.SearchUsersClient url pattern ");
            return;
        }

        String serverUrl = args[0];
        String pattern = args[1];

        System.out.println("Sending request to server.");

        new RestUsersClient(URI.create(serverUrl)).searchUsers(pattern);
    }
}
