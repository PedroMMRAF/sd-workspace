package trab1.client.users;

import java.util.logging.Logger;

import trab1.User;

public class UpdateUserClient {
    private static Logger Log = Logger.getLogger(UpdateUserClient.class.getName());

    public static void main(String[] args) throws InterruptedException {
        if (args.length != 5) {
            Log.severe("Use: java trab1.client.users.UpdateUserClient domain name pwd newPwd displayName");
            return;
        }

        String domain = args[0];
        String name = args[1];
        String pwd = args[2];
        String newPwd = args[3];
        String displayName = args[4];

        User user = new User(name, newPwd, domain, displayName);

        Log.info("Sending request to server.");

        new RestUsersClient(domain).updateUser(name, pwd, user);
    }
}
