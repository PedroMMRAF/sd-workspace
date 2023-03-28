package trab1.client.users;

import trab1.User;

public class CreateUserClient {
    public static void main(String[] args) throws InterruptedException {
        if (args.length != 4) {
            System.err.println(
                    "Use: java trab1.client.users.CreateUserClient domain name pwd displayName");
            return;
        }

        String domain = args[0];
        String name = args[1];
        String pwd = args[2];
        String displayName = args[3];

        User user = new User(name, pwd, domain, displayName);

        System.out.println("Sending request to server.");

        new RestUsersClient(domain).createUser(user);
    }
}
