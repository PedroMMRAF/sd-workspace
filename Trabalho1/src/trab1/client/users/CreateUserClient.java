package trab1.client.users;

import java.util.logging.Logger;

import trab1.User;

public class CreateUserClient {
    private static Logger Log = Logger.getLogger(UpdateUserClient.class.getName());

    public static void main(String[] args) throws InterruptedException {
        if (args.length != 4) {
            Log.severe("Use: java trab1.client.users.CreateUserClient domain name pwd displayName");
            return;
        }

        String domain = args[0];
        String name = args[1];
        String pwd = args[2];
        String displayName = args[3];

        User user = new User(name, pwd, domain, displayName);

        System.out.println(user);

        Log.info("Sending request to server.");

        String result = new RestUsersClient(domain).createUser(user);

        if (result != null)
            Log.info("Created user with name " + result);
    }
}
