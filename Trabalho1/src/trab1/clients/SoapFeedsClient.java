package trab1.clients;

import java.net.URI;
import java.util.List;

import javax.xml.namespace.QName;

import trab1.api.Message;
import trab1.api.User;
import trab1.api.java.Feeds;
import trab1.api.java.Result;
import trab1.api.java.Users;
import trab1.api.rest.FeedsService;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.Service;

public class SoapFeedsClient extends SoapClient implements Feeds {

	public SoapFeedsClient(URI serverURI) {
		super(serverURI);
	}

	private FeedsService stub;

	synchronized private FeedsService stub() {
		if (stub == null) {
			QName QNAME = new QName(FeedsService.NAMESPACE, FeedsService.PATH);
			Service service = Service.create(super.toURL(super.uri + WSDL), QNAME);
			this.stub = service.getPort(trab1.api.rest.FeedsService.class);
			super.setTimeouts((BindingProvider) stub);
		}
		return stub;
	}

	@Override
	public Result<Long> postMessage(String user, String pwd, Message msg) {
		return super.reTry(() -> super.toJavaResult(() -> stub().postMessage(user, pwd, msg)));
	}

	@Override
	public Result<Long> postMessageOtherDomain(String user, Message msg) {
		throw new UnsupportedOperationException("Unimplemented method 'postMessageOtherDomain'");
	}

	@Override
	public Result<Void> removeFromPersonalFeed(String user, long mid, String pwd) {
		return super.reTry(() -> super.toJavaResult(() -> stub().removeFromPersonalFeed(user, mid, pwd)));
	}

	@Override
	public Result<Message> getMessage(String user, long mid) {
		return super.reTry(() -> super.toJavaResult(() -> stub().getMessage(user, mid)));
	}

	@Override
	public Result<List<Message>> getMessages(String user, long time) {
		return super.reTry(() -> super.toJavaResult(() -> stub().getMessages(user, time)));
	}

	@Override
	public Result<Void> subUser(String user, String userSub, String pwd) {
		return super.reTry(() -> super.toJavaResult(() -> stub().subUser(user, userSub, pwd)));
	}

	@Override
	public Result<Void> subUserOtherDomain(String user, String userSub) {
		throw new UnsupportedOperationException("Unimplemented method 'subUserOtherDomain'");
	}

	@Override
	public Result<Void> unsubscribeUser(String user, String userSub, String pwd) {
		return super.reTry(() -> super.toJavaResult(() -> stub().unsubscribeUser(user, userSub, pwd)));
	}

	@Override
	public Result<Void> unsubUserOtherDomain(String user, String userSub) {
		throw new UnsupportedOperationException("Unimplemented method 'unsubUserOtherDomain'");
	}

	@Override
	public Result<List<String>> listSubs(String user) {
		return super.reTry(() -> super.toJavaResult(() -> stub().listSubs(user)));
	}

	@Override
	public Result<User> getUser(String user, String pwd) {
		throw new UnsupportedOperationException("Unimplemented method 'getUser'");
	}

	@Override
	public boolean hasUser(String user) {
		throw new UnsupportedOperationException("Unimplemented method 'hasUser'");
	}

	@Override
	public void postMessagePropagate(String user, Message msg) {
		throw new UnsupportedOperationException("Unimplemented method 'postMessagePropagate'");
	}

	@Override
	public void subUserPropagate(String user, String userSub) {
		throw new UnsupportedOperationException("Unimplemented method 'subUserPropagate'");
	}

	@Override
	public void unsubUserPropagate(String user, String userSub) {
		throw new UnsupportedOperationException("Unimplemented method 'unsubUserPropagate'");
	}

	@Override
	public Result<Message> forwardGetMessage(String user, long msgId) {
		throw new UnsupportedOperationException("Unimplemented method 'forwardGetMessage'");
	}

	@Override
	public Result<List<Message>> forwardGetMessages(String user, long time) {
		throw new UnsupportedOperationException("Unimplemented method 'forwardGetMessages'");
	}

}
