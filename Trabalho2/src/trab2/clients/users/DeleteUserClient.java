package trab2.clients.users;

import trab2.Discovery;
import trab2.api.User;
import trab2.api.java.Result;
import trab2.clients.ArgChecker;
import trab2.clients.UsersClientFactory;

public class DeleteUserClient {
    public static void main(String[] args) throws InterruptedException {
        new ArgChecker(DeleteUserClient.class)
                .setParams("domain", "name", "pwd")
                .check(args);

        String domain = args[0];
        String name = args[1];
        String pwd = args[2];

        System.out.println("Sending request to server.");

        Result<User> result = UsersClientFactory.get(domain).deleteUser(name, pwd);

        System.out.println(result);

        Discovery.getInstance().kill();
    }
}
