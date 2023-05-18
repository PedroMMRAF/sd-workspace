package trab2.clients.users;

import trab2.Discovery;
import trab2.api.User;
import trab2.api.java.Result;
import trab2.clients.ArgChecker;
import trab2.clients.UsersClientFactory;

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

        Discovery.getInstance().kill();
    }
}
