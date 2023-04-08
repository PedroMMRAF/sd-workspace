package trab1.clients;

import java.net.URISyntaxException;

public class ArgChecker {
    private String program;
    private String[] params;

    public ArgChecker(String program) {
        this.program = program;
    }

    public <T> ArgChecker(Class<T> cls) {
        String file;

        try {
            String[] path = cls
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI()
                    .getPath()
                    .split("/");

            file = path[path.length - 1];
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        String className = cls.getCanonicalName();

        this.program = String.format("java -cp %s %s", file, className);
    }

    public ArgChecker setParams(String... params) {
        this.params = params;
        return this;
    }

    public void check(String[] argv) {
        if (argv.length != params.length) {
            System.err.printf("Usage: %s %s\n", program, String.join(" ", params));
            System.exit(-1);
        }
    }
}
