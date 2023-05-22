package trab2.api.rest;

import java.util.List;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import trab2.api.Message;

@Path(FeedsService.PATH)
public interface FeedsService {
	String MID = "mid";
	String PWD = "pwd";
	String SUB = "sub";
	String USER = "user";
	String TIME = "time";
	String LIST = "list";
	String USERSUB = "userSub";
	String PROPAGATE = "propagate";

	String PATH = "/feeds";
	String NAMESPACE = "https://sd2223";

	/**
	 * Posts a new message in the feed, associating it to the feed of the specific
	 * user.
	 * A message should be identified before publish it, by assigning an ID.
	 * A user must contact the server of her domain directly (i.e., this operation
	 * should not be
	 * propagated to other domain)
	 *
	 * @param user user of the operation (format user@domain)
	 * @param pwd  password of the user sending the message
	 * @param msg  the message object to be posted to the server
	 * @return 200 the unique numerical identifier for the posted message;
	 *         403 if the publisher does not exist in the current domain or if the
	 *         pwd is not correct
	 *         400 otherwise
	 */
	@POST
	@Path("/{" + USER + "}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	long postMessage(@PathParam(USER) String user, @QueryParam(PWD) String pwd, Message msg);

	/**
	 * Posts a new message in the feed, associating it to the feed of the specific
	 * user.
	 * A message should be identified before publish it, by assigning an ID.
	 * A user must contact the server of her domain directly (i.e., this operation
	 * should not be
	 * propagated to other domain)
	 *
	 * @param user user of the operation (format user@domain)
	 * @param msg  the message object to be posted to the server
	 * @return 200 the unique numerical identifier for the posted message;
	 *         404 if the publisher does not exist in the current domain
	 *         403 if the pwd is not correct
	 *         400 otherwise
	 */
	@POST
	@Path("/" + PROPAGATE + "/{" + USER + "}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	long postMessageOtherDomain(@PathParam(USER) String user, Message msg);

	/**
	 * Removes the message identified by mid from the feed of user.
	 * A user must contact the server of her domain directly (i.e., this operation
	 * should not be
	 * propagated to other domain)
	 * 
	 * @param user user feed being accessed (format user@domain)
	 * @param mid  the identifier of the message to be deleted
	 * @param pwd  password of the user
	 * @return 204 if ok
	 *         403 if the pwd is not correct
	 *         404 is generated if the message does not exist in the server or if
	 *         the user does not exist
	 */
	@DELETE
	@Path("/{" + USER + "}/{" + MID + "}")
	void removeFromPersonalFeed(@PathParam(USER) String user, @PathParam(MID) long mid, @QueryParam(PWD) String pwd);

	/**
	 * Obtains the message with id from the feed of user (may be a remote user)
	 * 
	 * @param user user feed being accessed (format user@domain)
	 * @param mid  id of the message
	 *
	 * @return 200 the message if it exists;
	 *         404 if the user or the message does not exists
	 */
	@GET
	@Path("/{" + USER + "}/{" + MID + "}")
	@Produces(MediaType.APPLICATION_JSON)
	Message getMessage(@PathParam(USER) String user, @PathParam(MID) long mid);

	/**
	 * Returns a list of all messages stored in the server for a given user newer
	 * than time
	 * (note: may be a remote user)
	 * 
	 * @param user user feed being accessed (format user@domain)
	 * @param time the oldest time of the messages to be returned
	 * @return 200 a list of messages, potentially empty;
	 *         404 if the user does not exist.
	 */
	@GET
	@Path("/{" + USER + "}")
	@Produces(MediaType.APPLICATION_JSON)
	List<Message> getMessages(@PathParam(USER) String user, @QueryParam(TIME) long time);

	/**
	 * Subscribe a user.
	 * A user must contact the server of her domain directly (i.e., this operation
	 * should not be
	 * propagated to other domain)
	 *
	 * @param user    the user subscribing (following) other user (format
	 *                user@domain)
	 * @param userSub the user to be subscribed (followed) (format user@domain)
	 * @param pwd     password of the user
	 * @return 204 if ok
	 *         404 is generated if the user to be subscribed does not exist
	 *         403 is generated if the user does not exist or if the pwd is not
	 *         correct
	 */
	@POST
	@Path("/" + SUB + "/{" + USER + "}/{" + USERSUB + "}")
	void subUser(@PathParam(USER) String user, @PathParam(USERSUB) String userSub, @QueryParam(PWD) String pwd);

	/**
	 * Subscribe a user from another domain.
	 * A user must contact the server of her domain directly (i.e., this operation
	 * should not be
	 * propagated to other domain)
	 *
	 * @param user    the user subscribing (following) other user (format
	 *                user@domain)
	 * @param userSub the user to be subscribed (followed) (format user@domain)
	 * @return 204 if ok
	 *         404 is generated if the user to be subscribed does not exist
	 *         403 is generated if the user does not exist or if the pwd is not
	 *         correct
	 */
	@POST
	@Path("/" + PROPAGATE + "/{" + USER + "}/{" + USERSUB + "}")
	void subUserOtherDomain(@PathParam(USER) String user, @PathParam(USERSUB) String userSub);

	/**
	 * Unsubscribe a user
	 * A user must contact the server of her domain directly (i.e., this operation
	 * should not be
	 * propagated to other domain)
	 *
	 * @param user    the user unsubscribing (following) other user (format
	 *                user@domain)
	 * @param userSub the identifier of the user to be unsubscribed
	 * @param pwd     password of the user
	 * @return 204 if ok
	 *         403 is generated if the user does not exist or if the pwd is not
	 *         correct
	 *         404 is generated if the userSub is not subscribed
	 */
	@DELETE
	@Path("/" + SUB + "/{" + USER + "}/{" + USERSUB + "}")
	void unsubscribeUser(@PathParam(USER) String user, @PathParam(USERSUB) String userSub, @QueryParam(PWD) String pwd);

	/**
	 * Unsubscribe a user from another domain.
	 * A user must contact the server of her domain directly (i.e., this operation
	 * should not be
	 * propagated to other domain)
	 *
	 * @param user    the user subscribing (following) other user (format
	 *                user@domain)
	 * @param userSub the user to be subscribed (followed) (format user@domain)
	 * @return 204 if ok
	 *         404 is generated if the user to be subscribed does not exist
	 *         403 is generated if the user does not exist or if the pwd is not
	 *         correct
	 */
	@DELETE
	@Path("/" + PROPAGATE + "/{" + USER + "}/{" + USERSUB + "}")
	void unsubUserOtherDomain(@PathParam(USER) String user, @PathParam(USERSUB) String userSub);

	/**
	 * Subscribed users.
	 *
	 * @param user user being accessed (format user@domain)
	 * @return 200 if ok
	 *         404 is generated if the user does not exist
	 */
	@GET
	@Path("/" + SUB + "/" + LIST + "/{" + USER + "}")
	@Produces(MediaType.APPLICATION_JSON)
	List<String> listSubs(@PathParam(USER) String user);
}