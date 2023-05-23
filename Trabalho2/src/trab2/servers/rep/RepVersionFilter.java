package trab2.servers.rep;

import java.io.IOException;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import trab2.api.rest.FeedsService;

@Provider
public class RepVersionFilter implements ContainerResponseFilter {
    private int version;

    public RepVersionFilter() {
        version = -1;
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext response)
            throws IOException {
        response.getHeaders().add(FeedsService.HEADER_VERSION, version);
    }
}
