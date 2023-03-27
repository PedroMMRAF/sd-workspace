package trab1.client.users;

import trab1.Discovery;
import trab1.User;

import java.net.URI;

public class UpdateUserClient {
    public static void main(String[] args) throws InterruptedException {
        if (args.length != 6) {
            System.err.println(
                    "Use: java trab1.client.users.UpdateUserClient service name pwd newPwd domain displayName");
            return;
        }

        String service = args[0];
        String name = args[1];
        String pwd = args[2];
        String newPwd = args[3];
        String domain = args[4];
        String displayName = args[5];

        String serverUrl = Discovery.getInstance().knownUrisOf(service, 1)[0].toString();

        User user = new User(name, newPwd, domain, displayName);

        System.out.println("Sending request to server.");

        new RestUsersClient(URI.create(serverUrl)).updateUser(name, pwd, user);
    }
}
