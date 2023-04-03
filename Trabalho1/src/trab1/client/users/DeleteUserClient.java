package trab1.client.users;

import trab1.User;
import trab1.client.ArgChecker;

public class DeleteUserClient {
    public static void main(String[] args) throws InterruptedException {
        new ArgChecker(DeleteUserClient.class)
                .setParams("domain", "name", "pwd")
                .check(args);

        String domain = args[0];
        String name = args[1];
        String pwd = args[2];

        System.out.println("Sending request to server.");

        User result = new RestUsersClient(domain).deleteUser(name, pwd);

        System.out.printf("Deleted %s\n", result);
    }
}
