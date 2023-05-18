package trab2.clients.feeds;

import trab2.Discovery;
import trab2.clients.ArgChecker;
import trab2.clients.FeedsClientFactory;

public class RemoveFromPersonalFeed {
    public static void main(String[] args) throws InterruptedException {
        new ArgChecker(GetMessageClient.class)
                .setParams("domain", "user", "mid", "pwd").check(args);

        String domain = args[0];
        String user = args[1];
        long mid = Long.parseLong(args[2]);
        String pwd = args[3];

        System.out.println("Sending request to server.");

        System.out.println(FeedsClientFactory.get(domain).removeFromPersonalFeed(user, mid, pwd));

        Discovery.getInstance().kill();
    }
}
