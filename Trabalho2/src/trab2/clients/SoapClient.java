package trab2.clients;

import static trab2.api.java.Result.error;
import static trab2.api.java.Result.ok;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.logging.Logger;

import com.sun.xml.ws.client.BindingProviderProperties;

import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.WebServiceException;
import trab2.api.java.Result;
import trab2.api.java.Result.ErrorCode;

/**
 * 
 * Shared behavior among SOAP clients.
 * 
 * Holds endpoint information.
 * 
 * Translates soap responses/exceptions to Result<T> for interoperability.
 * 
 * @author smduarte
 *
 */
abstract class SoapClient {
    protected static final int READ_TIMEOUT = 5000;
    protected static final int CONNECT_TIMEOUT = 5000;

    protected static final int MAX_RETRIES = 10;
    protected static final int RETRY_SLEEP = 3000;

    private static Logger Log = Logger.getLogger(SoapClient.class.getName());

    protected static final String WSDL = "?wsdl";

    protected final URI serverURI;

    public SoapClient(URI uri) {
        this.serverURI = uri;
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
            return ok(supplier.get());
        } catch (WebServiceException x) {
            throw x;
        } catch (Exception e) {
            return error(getErrorCodeFrom(e));
        }
    }

    protected <R> Result<R> toJavaResult(VoidSupplier r) {
        try {
            r.run();
            return ok();
        } catch (WebServiceException x) {
            throw x;
        } catch (Exception e) {
            return error(getErrorCodeFrom(e));
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

    @Override
    public String toString() {
        return serverURI.toString();
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
}
