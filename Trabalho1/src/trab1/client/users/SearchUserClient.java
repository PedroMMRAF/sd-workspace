package trab1.client.users;

import java.util.List;

import trab1.User;
import trab1.client.ArgChecker;

public class SearchUserClient {
    public static void main(String[] args) throws InterruptedException {
        new ArgChecker(SearchUserClient.class)
                .setParams("domain", "pattern")
                .check(args);

        String domain = args[0];
        String pattern = args[1];

        System.out.println("Sending request to server.");

        List<User> usrs = new RestUsersClient(domain).searchUsers(pattern);

        System.out.println("Users:");

        usrs.forEach((u) -> System.out.printf("- %s", u));
    }
}
