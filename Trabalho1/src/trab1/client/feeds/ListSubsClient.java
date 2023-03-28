package trab1.client.feeds;

public class ListSubsClient {
    public static void main(String[] args) throws InterruptedException {
        if (args.length != 2) {
            System.err.println(
                    "Use: java trab1.client.users.CreateUserClient user");
            return;
        }

        String domain = args[0];
        String user = args[1];

        System.out.println("Sending request to server.");

        new RestFeedsClient(domain).listSubs(user);

    }
}
