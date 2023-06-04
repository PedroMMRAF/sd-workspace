package trab2.kafka.api;

import java.util.List;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import trab2.api.Message;

@Path(FeedsService.PATH)
public interface FeedsService {
    String HEADER_VERSION = "X-FEEDS-VERSION";

    String MID = "mid";
    String PWD = "pwd";
    String SUB = "sub";
    String USER = "user";
    String TIME = "time";
    String LIST = "list";
    String SECRET = "secret";
    String USERSUB = "userSub";
    String PROPAGATE = "propagate";

    String PATH = "/feeds";
    String NAMESPACE = "https://sd2223";

    @POST
    @Path("/{" + USER + "}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    long postMessage(@HeaderParam(HEADER_VERSION) Long version, @PathParam(USER) String user,
            @QueryParam(PWD) String pwd, Message msg);

    @DELETE
    @Path("/{" + USER + "}/{" + MID + "}")
    void removeFromPersonalFeed(@HeaderParam(HEADER_VERSION) Long version, @PathParam(USER) String user,
            @PathParam(MID) long mid, @QueryParam(PWD) String pwd);

    @GET
    @Path("/{" + USER + "}/{" + MID + "}")
    @Produces(MediaType.APPLICATION_JSON)
    Message getMessage(@HeaderParam(HEADER_VERSION) Long version, @PathParam(USER) String user,
            @PathParam(MID) long mid);

    @GET
    @Path("/{" + USER + "}")
    @Produces(MediaType.APPLICATION_JSON)
    List<Message> getMessages(@HeaderParam(HEADER_VERSION) Long version, @PathParam(USER) String user,
            @QueryParam(TIME) long time);

    @POST
    @Path("/" + SUB + "/{" + USER + "}/{" + USERSUB + "}")
    void subUser(@HeaderParam(HEADER_VERSION) Long version, @PathParam(USER) String user,
            @PathParam(USERSUB) String userSub, @QueryParam(PWD) String pwd);

    @DELETE
    @Path("/" + SUB + "/{" + USER + "}/{" + USERSUB + "}")
    void unsubscribeUser(@HeaderParam(HEADER_VERSION) Long version, @PathParam(USER) String user,
            @PathParam(USERSUB) String userSub, @QueryParam(PWD) String pwd);

    @GET
    @Path("/" + SUB + "/" + LIST + "/{" + USER + "}")
    @Produces(MediaType.APPLICATION_JSON)
    List<String> listSubs(@HeaderParam(HEADER_VERSION) Long version, @PathParam(USER) String user);

    // Internal methods

    @POST
    @Path("/" + PROPAGATE + "/{" + USER + "}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    long postMessageOtherDomain(@HeaderParam(HEADER_VERSION) Long version, @PathParam(USER) String user,
            @QueryParam(SECRET) String secret, Message msg);

    @POST
    @Path("/" + PROPAGATE + "/{" + USER + "}/{" + USERSUB + "}")
    void subUserOtherDomain(@HeaderParam(HEADER_VERSION) Long version, @PathParam(USER) String user,
            @PathParam(USERSUB) String userSub, @QueryParam(SECRET) String secret);

    @DELETE
    @Path("/" + PROPAGATE + "/{" + USER + "}/{" + USERSUB + "}")
    void unsubUserOtherDomain(@HeaderParam(HEADER_VERSION) Long version, @PathParam(USER) String user,
            @PathParam(USERSUB) String userSub, @QueryParam(SECRET) String secret);
}
