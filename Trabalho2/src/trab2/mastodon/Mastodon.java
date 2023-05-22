package trab2.mastodon;

import static trab2.api.java.Result.error;
import static trab2.api.java.Result.ok;
import static trab2.api.java.Result.ErrorCode.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.gson.reflect.TypeToken;

import trab2.api.Message;
import trab2.api.User;
import trab2.api.java.Feeds;
import trab2.api.java.Result;
import trab2.api.java.Result.ErrorCode;
import trab2.mastodon.msgs.MastodonAccount;
import trab2.mastodon.msgs.PostStatusArgs;
import trab2.mastodon.msgs.PostStatusResult;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;

import utils.JSON;

public class Mastodon implements Feeds {
    static String MASTODON_SERVER_URI = "http://10.170.138.52:3000";
    private static final String clientKey = "w2UfiZoxOqkzUps8W1MFvf1taPC8bTDmOWBxg-M3beg";
    private static final String clientSecret = "AqPpXYzgMaYgPncqHfw86AsPnofxemz9jhQVi3ieBz8";
    private static final String accessTokenStr = "NFrxs6LG2g3LpE45EkOgmMsrDxV0egqoQeKtIV2RQRw";

    // static String MASTODON_SERVER_URI = "https://mastodon.social";
    // private static final String clientKey =
    // "9mOrILbUqWk9_nmUOV1PQtTxA-WAVG6IJNc9Uymp8hw";
    // private static final String clientSecret =
    // "yUHLW9fMXEZRn_tG1t2I-OPcPuvS4OfaoiMqjyucEQU";
    // private static final String accessTokenStr =
    // "rUK6u0yC2mt3KS8iaqOtJr36Vgv7jbdDk6eXjE55q1E";

    static final String STATUSES_PATH = "/api/v1/statuses";
    static final String MESSAGE_STATUSES_PATH = "/api/v1/statuses/%d";
    static final String TIMELINES_PATH = "/api/v1/timelines/home";
    static final String ACCOUNT_FOLLOWING_PATH = "/api/v1/accounts/%s/following";
    static final String VERIFY_CREDENTIALS_PATH = "/api/v1/accounts/verify_credentials";
    static final String SEARCH_ACCOUNTS_PATH = "/api/v1/accounts/search";
    static final String ACCOUNT_FOLLOW_PATH = "/api/v1/accounts/%s/follow";
    static final String ACCOUNT_UNFOLLOW_PATH = "/api/v1/accounts/%s/unfollow";

    private static final int HTTP_OK = 200;

    protected OAuth20Service service;
    protected OAuth2AccessToken accessToken;

    private static Mastodon impl;

    protected Mastodon() {
        try {
            service = new ServiceBuilder(clientKey).apiSecret(clientSecret).build(MastodonApi.instance());
            accessToken = new OAuth2AccessToken(accessTokenStr);
        } catch (Exception x) {
            x.printStackTrace();
            System.exit(0);
        }
    }

    synchronized public static Mastodon getInstance() {
        if (impl == null)
            impl = new Mastodon();
        return impl;
    }

    private String getEndpoint(String path, Object... args) {
        var fmt = MASTODON_SERVER_URI + path;
        return String.format(fmt, args);
    }

    private String searchAccount(String user) throws Exception {
        final OAuthRequest request = new OAuthRequest(Verb.GET, getEndpoint(SEARCH_ACCOUNTS_PATH));

        request.addQuerystringParameter("q", user);
        service.signRequest(accessToken, request);

        Response response = service.execute(request);

        if (response.getCode() == HTTP_OK) {
            List<MastodonAccount> res = JSON.decode(response.getBody(), new TypeToken<List<MastodonAccount>>() {
            });
            return res.get(0).id();
        }

        throw new Exception(Integer.toString(response.getCode()));
    }

    @Override
    public Result<Long> postMessage(String user, String pwd, Message msg) {
        try {
            final OAuthRequest request = new OAuthRequest(Verb.POST, getEndpoint(STATUSES_PATH));

            JSON.toMap(new PostStatusArgs(msg.getText())).forEach((k, v) -> {
                request.addBodyParameter(k, v.toString());
            });

            service.signRequest(accessToken, request);

            Response response = service.execute(request);

            if (response.getCode() == HTTP_OK) {
                var res = JSON.decode(response.getBody(), PostStatusResult.class);

                return ok(res.getId());
            }

            return error(ErrorCode.fromStatusCode(response.getCode()));
        } catch (Exception x) {
            x.printStackTrace();
        }
        return error(INTERNAL_ERROR);
    }

    @Override
    public Result<List<Message>> getMessages(String user, long time) {
        try {
            final OAuthRequest request = new OAuthRequest(Verb.GET, getEndpoint(TIMELINES_PATH));

            request.addQuerystringParameter("min_id", String.valueOf(time << 16));
            service.signRequest(accessToken, request);

            Response response = service.execute(request);

            if (response.getCode() == HTTP_OK) {
                List<PostStatusResult> res = JSON.decode(response.getBody(), new TypeToken<List<PostStatusResult>>() {
                });

                return ok(res.stream().map(PostStatusResult::toMessage).toList());
            }

            return error(ErrorCode.fromStatusCode(response.getCode()));
        } catch (Exception x) {
            x.printStackTrace();
        }
        return error(Result.ErrorCode.INTERNAL_ERROR);
    }

    @Override
    public Result<Void> removeFromPersonalFeed(String user, long mid, String pwd) {
        try {
            final OAuthRequest request = new OAuthRequest(Verb.DELETE, getEndpoint(MESSAGE_STATUSES_PATH, mid));

            service.signRequest(accessToken, request);

            Response response = service.execute(request);

            if (response.getCode() == HTTP_OK) {
                return ok();
            }

            return error(ErrorCode.fromStatusCode(response.getCode()));
        } catch (Exception x) {
            x.printStackTrace();
        }
        return error(INTERNAL_ERROR);
    }

    @Override
    public Result<Message> getMessage(String user, long mid) {
        try {
            final OAuthRequest request = new OAuthRequest(Verb.GET, getEndpoint(MESSAGE_STATUSES_PATH, mid));

            service.signRequest(accessToken, request);

            Response response = service.execute(request);

            if (response.getCode() == HTTP_OK) {
                return ok(JSON.decode(response.getBody(), PostStatusResult.class).toMessage());
            }

            return error(ErrorCode.fromStatusCode(response.getCode()));
        } catch (Exception x) {
            x.printStackTrace();
        }
        return error(INTERNAL_ERROR);
    }

    @Override
    public Result<Void> subUser(String user, String userSub, String pwd) {
        try {
            final OAuthRequest request = new OAuthRequest(Verb.POST,
                    getEndpoint(ACCOUNT_FOLLOW_PATH, searchAccount(userSub)));
            service.signRequest(accessToken, request);

            Response response = service.execute(request);

            if (response.getCode() == HTTP_OK) {
                return ok();
            }

            return error(ErrorCode.fromStatusCode(response.getCode()));
        } catch (Exception x) {
            x.printStackTrace();
        }
        return error(INTERNAL_ERROR);
    }

    @Override
    public Result<Void> unsubscribeUser(String user, String userSub, String pwd) {
        try {
            final OAuthRequest request = new OAuthRequest(Verb.POST,
                    getEndpoint(ACCOUNT_UNFOLLOW_PATH, searchAccount(userSub)));

            service.signRequest(accessToken, request);

            Response response = service.execute(request);

            if (response.getCode() == HTTP_OK) {
                return ok();
            }

            return error(ErrorCode.fromStatusCode(response.getCode()));
        } catch (Exception x) {
            x.printStackTrace();
        }
        return error(INTERNAL_ERROR);
    }

    @Override
    public Result<List<String>> listSubs(String user) {
        return error(NOT_IMPLEMENTED);
    }

    @Override
    public Result<Long> postMessagePropagate(String user, Message msg) {
        return error(NOT_IMPLEMENTED);
    }

    @Override
    public Result<Long> postMessageOtherDomain(String user, Message msg) {
        return error(NOT_IMPLEMENTED);
    }

    @Override
    public Result<Void> subUserPropagate(String user, String userSub) {
        return error(NOT_IMPLEMENTED);
    }

    @Override
    public Result<Void> subUserOtherDomain(String user, String userSub) {
        return error(NOT_IMPLEMENTED);
    }

    @Override
    public Result<Void> unsubUserPropagate(String user, String userSub) {
        return error(NOT_IMPLEMENTED);
    }

    @Override
    public Result<Void> unsubUserOtherDomain(String user, String userSub) {
        return error(NOT_IMPLEMENTED);
    }

    @Override
    public Result<User> getUser(String user, String pwd) {
        return error(NOT_IMPLEMENTED);
    }

    @Override
    public Result<Boolean> hasUser(String user) {
        return error(NOT_IMPLEMENTED);
    }

    @Override
    public Result<Message> forwardGetMessage(String user, long msgId) {
        return error(NOT_IMPLEMENTED);
    }

    @Override
    public Result<List<Message>> forwardGetMessages(String user, long time) {
        return error(NOT_IMPLEMENTED);
    }

    public static void main(String[] args) throws Exception {
        Mastodon.getInstance().resetFeed();
    }

    private void resetFeed() throws InterruptedException, ExecutionException, IOException {
        OAuthRequest request = new OAuthRequest(Verb.GET, getEndpoint(TIMELINES_PATH));
        service.signRequest(accessToken, request);
        Response response = service.execute(request);

        if (response.getCode() != HTTP_OK) {
            System.err.println("nao deu");
        }

        List<PostStatusResult> res = JSON.decode(response.getBody(), new TypeToken<List<PostStatusResult>>() {
        });

        for (PostStatusResult msg : res) {
            request = new OAuthRequest(Verb.DELETE, getEndpoint(MESSAGE_STATUSES_PATH, msg.getId()));
            service.signRequest(accessToken, request);
            response = service.execute(request);

            if (response.getCode() != HTTP_OK) {
                System.err.println("falhou o " + msg.getId());
            } else {
                System.out.println("apagou o " + msg.getId());
            }
        }

        return;
    }
}
