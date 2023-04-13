package trab1.servers.soap;

import java.util.List;
import java.util.logging.Logger;

import trab1.api.Message;
import trab1.api.soap.FeedsException;
import trab1.api.soap.FeedsService;
import trab1.servers.java.JavaFeeds;
import trab1.api.java.Feeds;
import jakarta.jws.WebService;

@WebService(serviceName = FeedsService.NAME, targetNamespace = FeedsService.NAMESPACE, endpointInterface = FeedsService.INTERFACE)
public class SoapFeedsWebService extends SoapWebService<FeedsException> implements FeedsService {

	static Logger Log = Logger.getLogger(SoapUsersWebService.class.getName());

	final Feeds impl;

	public SoapFeedsWebService() {
		super((result) -> new FeedsException(result.error().toString()));
		this.impl = new JavaFeeds();
	}

	@Override
	public long postMessage(String user, String pwd, Message msg) throws FeedsException {
		return fromJavaResult(impl.postMessage(user, pwd, msg));
	}

	@Override
	public long postMessageOtherDomain(String user, Message msg) throws FeedsException {
		return fromJavaResult(impl.postMessageOtherDomain(user, msg));
	}

	@Override
	public void removeFromPersonalFeed(String user, long mid, String pwd) throws FeedsException {
		fromJavaResult(impl.removeFromPersonalFeed(user, mid, pwd));
	}

	@Override
	public Message getMessage(String user, long mid) throws FeedsException {
		return fromJavaResult(impl.getMessage(user, mid));
	}

	@Override
	public List<Message> getMessages(String user, long time) throws FeedsException {
		return fromJavaResult(impl.getMessages(user, time));
	}

	@Override
	public void subUser(String user, String userSub, String pwd) throws FeedsException {
		fromJavaResult(impl.subUser(user, userSub, pwd));
	}

	@Override
	public void subUserOtherDomain(String user, String userSub) throws FeedsException {
		fromJavaResult(impl.subUserOtherDomain(user, userSub));
	}

	@Override
	public void unsubscribeUser(String user, String userSub, String pwd) throws FeedsException {
		fromJavaResult(impl.unsubscribeUser(user, userSub, pwd));
	}

	@Override
	public void unsubUserOtherDomain(String user, String userSub) throws FeedsException {
		fromJavaResult(impl.unsubUserOtherDomain(user, userSub));
	}

	@Override
	public List<String> listSubs(String user) throws FeedsException {
		return fromJavaResult(impl.listSubs(user));
	}

}
