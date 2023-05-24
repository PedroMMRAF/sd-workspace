package trab2.servers.rep;

import java.io.IOException;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import trab2.kafka.api.FeedsService;

@Provider
public class VersionFilter implements ContainerResponseFilter {
    RepManager repManager;

    public VersionFilter(RepManager repManager) {
        this.repManager = repManager;
    }

    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response)
            throws IOException {
        response.getHeaders().add(FeedsService.HEADER_VERSION, repManager.getCurrentVersion());
    }
}