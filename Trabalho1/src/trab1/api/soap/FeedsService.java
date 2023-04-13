package trab1.api.soap;

import java.util.List;

import trab1.api.Message;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;

@WebService(serviceName = FeedsService.NAME, targetNamespace = FeedsService.NAMESPACE, endpointInterface = FeedsService.INTERFACE)
public interface FeedsService {
	static final String NAME = "feeds";
	static final String NAMESPACE = "http://sd2223";
	static final String INTERFACE = "trab1.api.soap.FeedsService";

	String MID = "mid";
	String PWD = "pwd";
	String SUB = "sub";
	String USER = "user";
	String TIME = "time";
	String LIST = "list";
	String USERSUB = "userSub";
	String PROPAGATE = "propagate";

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
	@WebMethod
	long postMessage(String user, String pwd, Message msg) throws FeedsException;

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
	@WebMethod
	long postMessageOtherDomain(String user, Message msg) throws FeedsException;

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
	@WebMethod
	void removeFromPersonalFeed(String user, long mid, String pwd) throws FeedsException;

	/**
	 * Obtains the message with id from the feed of user (may be a remote user)
	 * 
	 * @param user user feed being accessed (format user@domain)
	 * @param mid  id of the message
	 *
	 * @return 200 the message if it exists;
	 *         404 if the user or the message does not exists
	 */
	@WebMethod
	Message getMessage(String user, long mid) throws FeedsException;

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
	@WebMethod
	List<Message> getMessages(String user, long time) throws FeedsException;

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
	@WebMethod
	void subUser(String user, String userSub, String pwd) throws FeedsException;

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
	@WebMethod
	void subUserOtherDomain(String user, String userSub) throws FeedsException;

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
	@WebMethod
	void unsubscribeUser(String user, String userSub, String pwd) throws FeedsException;

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
	@WebMethod
	void unsubUserOtherDomain(String user, String userSub) throws FeedsException;

	/**
	 * Subscribed users.
	 *
	 * @param user user being accessed (format user@domain)
	 * @return 200 if ok
	 *         404 is generated if the user does not exist
	 */
	@WebMethod
	List<String> listSubs(String user) throws FeedsException;
}
