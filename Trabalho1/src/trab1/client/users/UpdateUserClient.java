package trab1.client.users;

import trab1.User;

public class UpdateUserClient {
    public static void main(String[] args) throws InterruptedException {
        if (args.length != 5) {
            System.err.println(
                    "Use: java trab1.client.users.UpdateUserClient domain name pwd newPwd displayName");
            return;
        }

        String domain = args[0];
        String name = args[1];
        String pwd = args[2];
        String newPwd = args[3];
        String displayName = args[4];

        User user = new User(name, newPwd, domain, displayName);

        System.out.println("Sending request to server.");

        new RestUsersClient(domain).updateUser(name, pwd, user);
    }
}
