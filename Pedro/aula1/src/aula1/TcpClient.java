package aula1;

import java.net.Socket;
import java.net.URI;
import java.util.Scanner;

/**
 * Basic TCP client...
 */
public class TcpClient {

    private static final String SERVICE = "TCPService";

    private static final String QUIT = "!quit";

    public static void main(String[] args) throws Exception {
        // Use Discovery to obtain the hostname and port of the server;
        URI discovered = Discovery.getInstance().knownUrisOf(SERVICE, 1)[0];

        var port = discovered.getPort();
        var hostname = discovered.getHost();

        try (var cs = new Socket(hostname, port); var sc = new Scanner(System.in)) {
            String input;
            do {
                input = sc.nextLine();
                cs.getOutputStream().write((input + System.lineSeparator()).getBytes());
            }
            while (!input.equals(QUIT));
        }
    }

}
