package trab1.clients.rest.users;

import trab1.api.User;
import trab1.clients.ArgChecker;

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
