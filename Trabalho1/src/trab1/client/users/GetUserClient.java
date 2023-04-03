package trab1.client.users;

import trab1.User;
import trab1.client.ArgChecker;

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
