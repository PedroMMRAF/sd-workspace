package trab1.clients.feeds;

import trab1.Discovery;
import trab1.clients.ArgChecker;
import trab1.clients.FeedsClientFactory;

public class GetMessagesClient {
    public static void main(String[] args) throws InterruptedException {
        new ArgChecker(GetMessageClient.class)
                .setParams("domain", "user", "time").check(args);

        String domain = args[0];
        String user = args[1];
        long time = Long.parseLong(args[2]);

        System.out.println("Sending request to server.");

        System.out.println(FeedsClientFactory.get(domain).getMessages(user, time));

        Discovery.getInstance().kill();
    }
}
