package trab1.client.feeds;

public class GetMessagesClient {
    public static void main(String[] args) throws InterruptedException {
        if (args.length != 3) {
            System.err.println(
                    "Use: java trab1.client.users.CreateUserClient user time");
            return;
        }

        String domain = args[0];
        String user = args[1];
        long time = Long.parseLong(args[2]);

        System.out.println("Sending request to server.");

        new RestFeedsClient(domain).getMessages(user, time);
    }
}
