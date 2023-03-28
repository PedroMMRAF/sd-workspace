package trab1.client.users;

public class SearchUserClient {
    public static void main(String[] args) throws InterruptedException {
        if (args.length != 2) {
            System.err.println("Use: java trab1.client.users.SearchUserClient domain query");
            return;
        }

        String domain = args[0];
        String pattern = args[1];

        System.out.println("Sending request to server.");

        new RestUsersClient(domain).searchUsers(pattern);
    }
}
