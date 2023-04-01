package trab1.client.users;

import java.util.logging.Logger;

import trab1.User;

public class CreateUserClient {
    private static Logger Log = Logger.getLogger(CreateUserClient.class.getName());

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s\n");
    }

    public static void main(String[] args) throws InterruptedException {
        if (args.length != 4) {
            Log.severe("Use: java trab1.client.users.CreateUserClient name pwd domain displayName");
            return;
        }

        String name = args[0];
        String pwd = args[1];
        String domain = args[2];
        String displayName = args[3];

        User user = new User(name, pwd, domain, displayName);

        Log.info("Sending request to server.");

        String result = new RestUsersClient(domain).createUser(user);

        if (result != null)
            Log.info("Created user with name " + result);
    }
}
