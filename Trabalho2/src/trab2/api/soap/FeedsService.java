package trab2.api.soap;

import java.util.List;

import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import trab2.api.Message;

@WebService(serviceName = FeedsService.NAME, targetNamespace = FeedsService.NAMESPACE, endpointInterface = FeedsService.INTERFACE)
public interface FeedsService {
	static final String NAME = "feeds";
	static final String NAMESPACE = "http://sd2223";
	static final String INTERFACE = "trab2.api.soap.FeedsService";

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
	 * @throws FeedsException otherwise
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
	 * @throws FeedsException otherwise
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
	 * @throws FeedsException otherwise
	 */
	@WebMethod
	void removeFromPersonalFeed(String user, long mid, String pwd) throws FeedsException;

	/**
	 * Obtains the message with id from the feed of user (may be a remote user)
	 * 
	 * @param user user feed being accessed (format user@domain)
	 * @param mid  id of the message
	 * @throws FeedsException otherwise
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
	 * @throws FeedsException otherwise
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
	 * @throws FeedsException otherwise
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
	 * @throws FeedsException otherwise
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
	 * @throws FeedsException otherwise
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
	 * @throws FeedsException otherwise
	 */
	@WebMethod
	void unsubUserOtherDomain(String user, String userSub) throws FeedsException;

	/**
	 * Subscribed users.
	 *
	 * @param user user being accessed (format user@domain)
	 * @throws FeedsException otherwise
	 */
	@WebMethod
	List<String> listSubs(String user) throws FeedsException;
}
