package trab1;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * <p>
 * A class interface to perform service discovery based on periodic
 * announcements over multicast communication.
 * </p>
 */
public interface Discovery {
    /**
     * Used to announce the URI of the given service name.
     *
     * @param domain      - the name of the domain
     * @param serviceName - the name of the service
     * @param serviceURI  - the uri of the service
     */
    void announce(String domain, String serviceName, String serviceURI);

    /**
     * Get discovered URIs for a given service name
     *
     * @param serviceName - name of the service
     * @param minReplies  - minimum number of requested URIs. Blocks until the
     *                    number is satisfied.
     * @return array with the discovered URIs for the given service name.
     */
    URI[] knownUrisOf(String serviceName, int minReplies);

    /**
     * Get the instance of the Discovery service
     *
     * @return the singleton instance of the Discovery service
     */
    static Discovery getInstance() {
        return DiscoveryImpl.getInstance();
    }
}

/**
 * Implementation of the multicast discovery service
 */
class DiscoveryImpl implements Discovery {
    private static Logger Log = Logger.getLogger(Discovery.class.getName());

    // The pre-aggreed multicast endpoint assigned to perform discovery.
    static final int DISCOVERY_RETRY_TIMEOUT = 5000;
    static final int DISCOVERY_ANNOUNCE_PERIOD = 1000;

    // Replace with appropriate values...
    static final InetSocketAddress DISCOVERY_ADDR = new InetSocketAddress("225.225.225.225", 2255);

    // Used separate the two fields that make up a service announcement.
    private static final String DELIMITER = "\t";

    private static final int MAX_DATAGRAM_SIZE = 65536;

    private static Discovery singleton;

    synchronized static Discovery getInstance() {
        if (singleton == null) {
            singleton = new DiscoveryImpl();
        }
        return singleton;
    }

    private DiscoveryImpl() {
        this.startListener();

        discovered = new HashMap<>();
    }

    private final Map<String, Cache<String, URI>> discovered;

    @Override
    public void announce(String domain, String serviceName, String serviceURI) {
        Log.info(String.format("Starting Discovery announcements on: %s for: %s -> %s\n",
                DISCOVERY_ADDR, serviceName, serviceURI));

        var pktBytes = String.format("%s:%s%s%s", domain, serviceName, DELIMITER, serviceURI).getBytes();
        var pkt = new DatagramPacket(pktBytes, pktBytes.length, DISCOVERY_ADDR);

        // start thread to send periodic announcements
        new Thread(() -> {
            try (var ds = new DatagramSocket()) {
                while (true) {
                    try {
                        ds.send(pkt);
                        Thread.sleep(DISCOVERY_ANNOUNCE_PERIOD);
                    } catch (InterruptedException | IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public URI[] knownUrisOf(String serviceName, int minEntries) {
        var cache = discovered.get(serviceName);

        try {
            while (cache == null || cache.size() < minEntries) {
                Thread.sleep(DISCOVERY_RETRY_TIMEOUT);
                cache = discovered.get(serviceName);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }

        return cache.asMap().values().toArray(URI[]::new);
    }

    private void startListener() {
        Log.info(String.format("Starting discovery on multicast group: %s, port: %d\n",
                DISCOVERY_ADDR.getAddress(), DISCOVERY_ADDR.getPort()));

        new Thread(() -> {
            try (var ms = new MulticastSocket(DISCOVERY_ADDR.getPort())) {
                ms.joinGroup(DISCOVERY_ADDR,
                        NetworkInterface.getByInetAddress(InetAddress.getLocalHost()));
                while (true) {
                    try {
                        var pkt = new DatagramPacket(new byte[MAX_DATAGRAM_SIZE],
                                MAX_DATAGRAM_SIZE);
                        ms.receive(pkt);

                        var msg = new String(pkt.getData(), 0, pkt.getLength());
                        Log.info(String.format("Received: %s", msg));

                        var parts = msg.split(DELIMITER);
                        if (parts.length == 2) {
                            // Store this information
                            var serviceName = parts[0];
                            var uri = URI.create(parts[1]);

                            discovered.putIfAbsent(serviceName, CacheBuilder.newBuilder().build());
                            var cache = discovered.get(serviceName);
                            cache.put(parts[1], uri);
                        }
                    } catch (IOException x) {
                        x.printStackTrace();
                    }
                }
            } catch (IOException x) {
                x.printStackTrace();
            }
        }).start();
    }
}