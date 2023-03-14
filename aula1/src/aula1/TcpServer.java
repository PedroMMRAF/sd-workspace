package aula1;

import java.net.InetAddress;
import java.net.ServerSocket;

/**
 * Basic TCP server...
 */
public class TcpServer {

    private static final String SERVICE = "TCPService";

    private static final int PORT = 9000;
    private static final int BUF_SIZE = 1024;

    public static void main(String[] args) throws Exception {
        // Use Discovery to announce the uri of this server, in the form of (tcp://hostname:port)
        String serverURI = String.format("tcp://%s:%d",
                InetAddress.getLocalHost().getHostAddress(), PORT);
        Discovery.getInstance().announce(SERVICE, serverURI);

        try (var ss = new ServerSocket(PORT)) {
            System.err.println("Accepting connections at: " + ss.getLocalSocketAddress());

            while (true) {
                var cs = ss.accept();

                System.err.println(
                        "Accepted connection from client at: " + cs.getRemoteSocketAddress());

                int n;
                var buf = new byte[BUF_SIZE];
                while ((n = cs.getInputStream().read(buf)) > 0)
                    System.out.write(buf, 0, n);

                System.err.println("Connection closed.");
            }
        }
    }

}
