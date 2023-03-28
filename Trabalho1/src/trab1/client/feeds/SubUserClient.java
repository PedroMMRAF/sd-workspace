package trab1.client.feeds;

public class SubUserClient {
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

        new RestFeedsClient(domain).subUser(user, userSub, pwd);
    }
}
