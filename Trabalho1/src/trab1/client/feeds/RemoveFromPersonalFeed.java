package trab1.client.feeds;

import trab1.Discovery;

import java.net.URI;

public class RemoveFromPersonalFeed {
    public static void main(String[] args) throws InterruptedException {
        if (args.length != 3) {
            System.err.println(
                    "Use: java trab1.client.users.CreateUserClient user mid pwd");
            return;
        }

        String user = args[0];
        long mid = Long.parseLong(args[1]);
        String pwd = args[2];
        String[] userInfo = user.split("@");

        String serverUrl = Discovery.getInstance().knownUrisOf("service", 1)[0].toString();

        System.out.println("Sending request to server.");

        new RestFeedsClient(URI.create(serverUrl)).removeFromPersonalFeed(user, mid, pwd);
    }
}
