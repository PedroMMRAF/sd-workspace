package trab1.clients.users;

import trab1.api.User;
import trab1.api.java.Result;
import trab1.clients.ArgChecker;
import trab1.clients.UsersClientFactory;

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

        Result<String> result = UsersClientFactory.get(domain).createUser(user);

        System.out.println(result);
    }
}
