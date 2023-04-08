package trab1.clients.rest.users;

import trab1.api.User;
import trab1.clients.ArgChecker;

public class GetUserClient {
    public static void main(String[] args) throws InterruptedException {
        new ArgChecker(GetUserClient.class)
                .setParams("domain", "name", "pwd")
                .check(args);

        String domain = args[0];
        String name = args[1];
        String pwd = args[2];

        System.out.println("Sending request to server.");

        User user = new RestUsersClient(domain).getUser(name, pwd);

        System.out.println(user);
    }
}
