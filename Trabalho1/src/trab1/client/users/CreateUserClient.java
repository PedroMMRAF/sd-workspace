package trab1.client.users;

import trab1.User;
import trab1.client.ArgChecker;

public class CreateUserClient {
    public static void main(String[] args) throws InterruptedException {
        new ArgChecker(CreateUserClient.class)
                .setParams("domain", "name", "pwd", "displayName")
                .check(args);

        String domain = args[0];
        String name = args[1];
        String pwd = args[2];
        String displayName = args[3];

        User user = new User(name, pwd, domain, displayName);

        System.out.println("Sending request to server.");

        String result = new RestUsersClient(domain).createUser(user);

        System.out.printf("Created user with name %s\n", result);
    }
}
