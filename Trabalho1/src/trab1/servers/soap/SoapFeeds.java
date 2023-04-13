package trab1.servers.soap;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.Service;
import jakarta.xml.ws.WebServiceException;
import trab1.api.Message;
import trab1.api.User;
import trab1.api.java.Result;
import trab1.servers.java.JavaFeeds;
import com.sun.xml.ws.client.BindingProviderProperties;
import trab1.api.java.Result.ErrorCode;
import trab1.api.soap.FeedsService;
import trab1.api.soap.UsersService;
import javax.xml.namespace.QName;
import trab1.Discovery;

public class SoapFeeds extends JavaFeeds {
    protected static final int READ_TIMEOUT = 5000;
    protected static final int CONNECT_TIMEOUT = 5000;

    protected static final int MAX_RETRIES = 10;
    protected static final int RETRY_SLEEP = 3000;

    private static Logger Log = Logger.getLogger(SoapFeeds.class.getName());

    protected static final String WSDL = "?wsdl";

    public String getServiceTarget(String domain, String service) {
        return Discovery.getInstance().knownUrisOf(domain, service, 1)[0].toString();
    }

    synchronized private UsersService uStub(String domain) {
        QName QNAME = new QName(UsersService.NAMESPACE, UsersService.NAME);
        Service service = Service.create(toURL(getServiceTarget(domain, UsersService.NAME) + WSDL), QNAME);
        UsersService uStub = service.getPort(trab1.api.soap.UsersService.class);
        setTimeouts((BindingProvider) uStub);
        return uStub;
    }

    synchronized private FeedsService fStub(String domain) {
        QName QNAME = new QName(FeedsService.NAMESPACE, FeedsService.NAME);
        Service service = Service.create(toURL(getServiceTarget(domain, FeedsService.NAME) + WSDL), QNAME);
        FeedsService fStub = service.getPort(trab1.api.soap.FeedsService.class);
        setTimeouts((BindingProvider) fStub);
        return fStub;
    }

    protected void setTimeouts(BindingProvider port) {
        port.getRequestContext().put(BindingProviderProperties.CONNECT_TIMEOUT, CONNECT_TIMEOUT);
        port.getRequestContext().put(BindingProviderProperties.REQUEST_TIMEOUT, READ_TIMEOUT);
    }

    protected <T> Result<T> retry(ResultSupplier<Result<T>> func) {
        for (int i = 0; i < MAX_RETRIES; i++)
            try {
                return func.get();
            } catch (WebServiceException x) {
                x.printStackTrace();
                Log.fine("Timeout: " + x.getMessage());
                sleep(RETRY_SLEEP);
            } catch (Exception x) {
                x.printStackTrace();
                return Result.error(ErrorCode.INTERNAL_ERROR);
            }
        return Result.error(ErrorCode.TIMEOUT);
    }

    protected <R> Result<R> toJavaResult(ResultSupplier<R> supplier) {
        try {
            return Result.ok(supplier.get());
        } catch (WebServiceException x) {
            throw x;
        } catch (Exception e) {
            return Result.error(getErrorCodeFrom(e));
        }
    }

    protected <R> Result<R> toJavaResult(VoidSupplier r) {
        try {
            r.run();
            return Result.ok();
        } catch (WebServiceException x) {
            throw x;
        } catch (Exception e) {
            return Result.error(getErrorCodeFrom(e));
        }
    }

    static private ErrorCode getErrorCodeFrom(Exception e) {
        try {
            return ErrorCode.valueOf(e.getMessage());
        } catch (IllegalArgumentException x) {
            return ErrorCode.INTERNAL_ERROR;
        }
    }

    static interface ResultSupplier<T> {
        T get() throws Exception;
    }

    static interface VoidSupplier {
        void run() throws Exception;
    }

    public static URL toURL(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
        }
    }

    @Override
    public Result<User> getUser(String user, String pwd) {
        String[] userInfo = user.split("@");
        return retry(() -> toJavaResult(() -> uStub(userInfo[1]).getUser(userInfo[0], pwd)));
    }

    @Override
    public boolean hasUser(String user) {
        String[] userInfo = user.split("@");
        return retry(() -> {
           Result<List<User>> res=  toJavaResult(() -> uStub(userInfo[1]).searchUsers(userInfo[0]));
           if (!res.isOK())
                return Result.ok(false);
            return Result.ok(res.value().stream().map(u -> u.getName()).toList().contains(userInfo[0]));
        }).value();
    }

    @Override
    public void postMessagePropagate(String user, Message msg) {
        String[] userInfo = user.split("@");
        retry(() -> toJavaResult(() -> fStub(userInfo[1]).postMessageOtherDomain(user, msg)));
    }

    @Override
    public void subUserPropagate(String user, String userSub) {
        String[] userInfo = user.split("@");
        retry(() -> toJavaResult(() -> fStub(userInfo[1]).subUserOtherDomain(user, userSub)));
    }

    @Override
    public void unsubUserPropagate(String user, String userSub) {
        String[] userInfo = user.split("@");
        retry(() -> toJavaResult(() -> fStub(userInfo[1]).unsubUserOtherDomain(user, userSub)));
    }

    @Override
    public Result<Message> forwardGetMessage(String user, long msgId) {
        String[] userInfo = user.split("@");
        return retry(() -> toJavaResult(() -> fStub(userInfo[1]).getMessage(user, msgId)));
    }

    @Override
    public Result<List<Message>> forwardGetMessages(String user, long time) {
        String[] userInfo = user.split("@");
        return retry(() -> toJavaResult(() -> fStub(userInfo[1]).getMessages(user, time)));
    }

}