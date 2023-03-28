package trab1.client.users;

public class DeleteUserClient {
    public static void main(String[] args) throws InterruptedException {
        if (args.length != 3) {
            System.err.println(
                    "Use: java trab1.client.users.DeleteUserClient domain name pwd");
            return;
        }

        String domain = args[0];
        String name = args[1];
        String pwd = args[2];

        System.out.println("Sending request to server.");

        new RestUsersClient(domain).deleteUser(name, pwd);
    }
}
