package trab2.servers;

public class Domain {
    private static String domain;
    private static long sequence;
    private static String secret;

    public static void set(String domain, long sequence, String secret) {
        Domain.domain = domain;
        Domain.sequence = sequence;
        Domain.secret = secret;
    }

    public static String domain() {
        return Domain.domain;
    }

    public static long sequence() {
        return Domain.sequence;
    }

    public static String secret() {
        return Domain.secret;
    }

    public static boolean verify(String secret) {
        return Domain.secret.equals(secret);
    }
}
