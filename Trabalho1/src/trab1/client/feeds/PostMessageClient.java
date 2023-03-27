package trab1.client.feeds;

import trab1.Message;
import trab1.Discovery;

import java.net.URI;

public class PostMessageClient {
    public static void main(String[] args) throws InterruptedException {
        if (args.length != 3) {
            System.err.println(
                    "Use: java trab1.client.users.CreateUserClient user pwd text");
            return;
        }

        String user = args[0];
        String pwd = args[1];
        String text = args[2];
        String[] userInfo = user.split("@");

        String serverUrl = Discovery.getInstance().knownUrisOf("service", 1)[0].toString();

        Message msg = new Message(-1, userInfo[0], userInfo[1], text);

        System.out.println("Sending request to server.");

        new RestFeedsClient(URI.create(serverUrl)).postMessage(user, pwd, msg);
    }
}
