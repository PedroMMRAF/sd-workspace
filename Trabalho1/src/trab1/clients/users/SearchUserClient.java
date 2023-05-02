package trab1.clients.users;

import java.util.List;

import trab1.Discovery;
import trab1.api.User;
import trab1.api.java.Result;
import trab1.clients.ArgChecker;
import trab1.clients.UsersClientFactory;

public class SearchUserClient {
    public static void main(String[] args) throws InterruptedException {
        new ArgChecker(SearchUserClient.class)
                .setParams("domain", "pattern")
                .check(args);

        String domain = args[0];
        String pattern = args[1];

        System.out.println("Sending request to server.");

        Result<List<User>> result = UsersClientFactory.get(domain).searchUsers(pattern);

        if (!result.isOK()) {
            System.out.println(result);
            return;
        }

        result.value().forEach((u) -> System.out.printf("%s", u));

        Discovery.getInstance().kill();
    }
}
