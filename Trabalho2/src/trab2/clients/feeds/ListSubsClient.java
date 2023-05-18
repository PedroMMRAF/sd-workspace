package trab2.clients.feeds;

import trab2.Discovery;
import trab2.clients.ArgChecker;
import trab2.clients.FeedsClientFactory;

public class ListSubsClient {
    public static void main(String[] args) throws InterruptedException {
        new ArgChecker(GetMessageClient.class)
                .setParams("domain", "user").check(args);

        String domain = args[0];
        String user = args[1];

        System.out.println("Sending request to server.");

        System.out.println(FeedsClientFactory.get(domain).listSubs(user));

        Discovery.getInstance().kill();
    }
}
