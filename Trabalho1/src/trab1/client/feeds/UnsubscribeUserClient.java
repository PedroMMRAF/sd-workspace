package trab1.client.feeds;

public class UnsubscribeUserClient {
    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s\n");
    }

    public static void main(String[] args) throws InterruptedException {

        if (args.length != 4) {
            System.err.println(
                    "Use: java trab1.client.users.CreateUserClient user userSub pwd");
            return;
        }

        String domain = args[0];
        String user = args[1];
        String userSub = args[2];
        String pwd = args[3];

        System.out.println("Sending request to server.");

        new RestFeedsClient(domain).unsubscribeUser(user, userSub, pwd);
    }

}
