package trab1.clients.feeds;

import trab1.api.Message;
import trab1.clients.ArgChecker;
import trab1.clients.FeedsClientFactory;

public class PostMessageClient {
    public static void main(String[] args) throws InterruptedException {
        new ArgChecker(GetMessageClient.class)
                .setParams("domain", "user", "pwd", "text").check(args);

        String domain = args[0];
        String user = args[1];
        String pwd = args[2];
        String text = args[3];

        Message msg = new Message(-1, user, domain, text);

        System.out.println("Sending request to server.");

        System.out.println(FeedsClientFactory.get(domain).postMessage(user, pwd, msg));
    }
}
