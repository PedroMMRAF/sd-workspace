package trab1.clients.users;

import trab1.api.User;
import trab1.api.java.Result;
import trab1.clients.ArgChecker;
import trab1.clients.UsersClientFactory;

public class UpdateUserClient {
    public static void main(String[] args) throws InterruptedException {
        new ArgChecker(UpdateUserClient.class)
                .setParams("domain", "name", "pwd", "newPwd", "newDisplayName")
                .check(args);

        String domain = args[0];
        String name = args[1];
        String pwd = args[2];
        String newPwd = args[3];
        String displayName = args[4];

        User user = new User(name, newPwd, domain, displayName);

        System.out.println("Sending request to server.");

        Result<User> result = UsersClientFactory.get(domain).updateUser(name, pwd, user);

        System.out.println(result);
    }
}
