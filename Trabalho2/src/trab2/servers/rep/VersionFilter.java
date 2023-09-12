package trab2.servers.rep;

import java.io.IOException;

import jakarta.inject.Singleton;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import trab2.kafka.api.FeedsService;

@Singleton
@Provider
public class VersionFilter implements ContainerResponseFilter {
    private RepManager repManager;

    public VersionFilter() {
        repManager = RepManager.getInstance();
    }

    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response)
            throws IOException {
        response.getHeaders().add(FeedsService.HEADER_VERSION, repManager.getCurrentVersion());
    }
}