package trab2.servers;

public class Domain {
    private static String domain;

    public static void set(String domain) {
        Domain.domain = domain;
    }

    public static String get() {
        return Domain.domain;
    }
}
