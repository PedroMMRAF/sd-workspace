package trab1.clients.feeds;

import trab1.Discovery;
import trab1.clients.ArgChecker;
import trab1.clients.FeedsClientFactory;

public class GetMessageClient {
    public static void main(String[] args) throws InterruptedException {
        new ArgChecker(GetMessageClient.class)
                .setParams("domain", "user", "mid").check(args);

        String domain = args[0];
        String user = args[1];
        long mid = Long.parseLong(args[2]);

        System.out.println("Sending request to server.");

        System.out.println(FeedsClientFactory.get(domain).getMessage(user, mid));

        Discovery.getInstance().kill();
    }
}
