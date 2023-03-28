package trab1.client.feeds;

import trab1.Message;

public class PostMessageClient {
    public static void main(String[] args) throws InterruptedException {
        if (args.length != 4) {
            System.err.println(
                    "Use: java trab1.client.users.CreateUserClient domain user pwd text");
            return;
        }

        String domain=args[0];
        String user = args[1];
        String pwd = args[2];
        String text = args[3];

        Message msg = new Message(-1, user, domain, text);

        System.out.println("Sending request to server.");

        new RestFeedsClient(domain).postMessage(user, pwd, msg);
    }
}
