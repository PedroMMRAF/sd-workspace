package trab1.client.users;

import trab1.Discovery;
import trab1.User;

import java.net.URI;

public class CreateUserClient {
    public static void main(String[] args) throws InterruptedException {
        if (args.length != 5) {
            System.err.println(
                    "Use: java trab1.client.users.CreateUserClient service userName password domain displayName");
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

        new RestUsersClient(URI.create(serverUrl)).createUser(user);
    }
}
