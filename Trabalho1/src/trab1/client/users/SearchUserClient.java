package trab1.client.users;

import java.util.List;

import trab1.User;

public class SearchUserClient {
    public static void main(String[] args) throws InterruptedException {
        if (args.length != 2) {
            System.err.println("Use: java trab1.client.users.SearchUserClient domain query");
            return;
        }

        String domain = args[0];
        String pattern = args[1];

        System.out.println("Sending request to server.");

        List<User> usrs = new RestUsersClient(domain).searchUsers(pattern);

        usrs.forEach(System.out::println);
    }
}
