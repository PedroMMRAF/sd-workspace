package aula3.clients;

import java.net.URI;
import java.util.logging.Logger;


public class DeleteUserClient {
    private static Logger Log = Logger.getLogger(CreateUserClient.class.getName());

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    public static void main(String[] args) {
        if (args.length != 5) {
            System.err.println(
                    "Use: java aula3.clients.CreateUserClient url name pwd");
            return;
        }

        String serverUrl = args[0];
        String name = args[1];
        String pwd = args[2];


        Log.info("Sending request to server.");

        var result = new RestUsersClient(URI.create(serverUrl)).deleteUser(name, pwd);
        System.out.println("Result: " + result);
    }
}
