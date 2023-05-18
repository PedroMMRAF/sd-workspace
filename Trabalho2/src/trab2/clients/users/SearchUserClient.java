package trab2.clients.users;

import java.util.List;

import trab2.Discovery;
import trab2.api.User;
import trab2.api.java.Result;
import trab2.clients.ArgChecker;
import trab2.clients.UsersClientFactory;

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
