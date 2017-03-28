package pt.andred.cmu1617;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by andre on 28/03/17.
 */

class LocMessAPIClientBase {
    private static final int TRIES = 1;

    private final ApplicationConfiguration config;

    private final HttpClient client;

    LocMessAPIClientBase(ApplicationConfiguration config) {
        this.config = config;
        client = new HttpClientOkHttp();
    }


    protected <T> T invoke(
            Endpoint endpoint,
            Authorization auth) {
        return invoke(endpoint, auth, null, null);
    }

    protected <T> T invoke(
            Endpoint endpoint,
            Map<String, String> get) {
        return invoke(endpoint, null, get, null);
    }

    protected <T> T invoke(
            Endpoint endpoint,
            Authorization authorization,
            Map<String, String> get) {
        return invoke(endpoint, authorization, get, null);
    }

    protected <T> T invoke(Endpoint endpoint, Map<String, String> get, Map<String, String> post) {
        return invoke(endpoint, null, get, post);
    }

    @SuppressWarnings("unchecked")
    protected <T> T invoke(Endpoint endpoint, Authorization authorization,
                           Map<String, String> get, Map<String, String> post) {
        if (get == null) get = new HashMap<>();
        if (post == null) post = new HashMap<>();
        get.put("lang", config.getLocale().getLanguage() + "-" + config.getLocale().getCountry());

        HttpRequest httpRequest = RequestFactory.fromEndpoint(config, endpoint);

        if (authorization != null) {
            httpRequest.withAuthorization(authorization);
        }

        httpRequest.withGet(get);
        httpRequest.withPost(post);

        try {

            ClientResponse clientResponse = client.handleHttpRequest(httpRequest);

            if (clientResponse.getStatusCode() == 401) {
                JSONObject jsonResponse;
                jsonResponse = new JSONObject(clientResponse.getResponse());
                String error = jsonResponse.get("error").toString();
                String errorDescription = jsonResponse.get("error_description").toString();
                throw new APIException(error, errorDescription);
            }
            if (endpoint.getResponseClass().equals(JSONArray.class)) {
                return (T) new JSONArray(clientResponse.getResponse());
            } else if (endpoint.getResponseClass().equals(JSONObject.class)) {
                return (T) new JSONObject(clientResponse.getResponse());
            } else if (endpoint.getResponseClass().equals(File.class)) {
                return (T) clientResponse.getResponse().getBytes();
            } else {
                throw new APIException("Could not identify return type", null);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            throw new APIException(e, e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            throw new APIException(e, e.getMessage());
        }
    }

    public Authorization refreshAccessToken(Authorization authorization) {
        //logger.debug("Refreshing OAuth Access Token using Refresh Token");
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("grant_type", "refresh_token");
        queryParams.put("client_id", this.config.getOAuthConsumerKey());
        queryParams.put("client_secret", getEncodedSecret());
        queryParams.put("refresh_token", authorization.asOAuthAuthorization().getOAuthRefreshToken());

        HttpRequest httpRequest =
                RequestFactory.fromEndpoint(config, Endpoint.OAUTH_REFRESH_ACCESS_TOKEN).withGet(
                        queryParams);

        ClientResponse clientResponse;
        try {
            clientResponse = client.handleHttpRequest(httpRequest);
        } catch (IOException e) {
            throw new APIException(e, e.getMessage());
        }
        if (clientResponse.getStatusCode() == 401) {
            JSONObject jsonResponse;
            try {
                jsonResponse = new JSONObject(clientResponse.getResponse());
                String error = jsonResponse.get("error").toString();
                throw new APIException(error, null);
            } catch (JSONException e) {
                throw new APIException(e.getMessage());
            }
        }
        if (Endpoint.OAUTH_REFRESH_ACCESS_TOKEN.getResponseClass().equals(JSONObject.class)) {
            try {
                JSONObject responseJson = new JSONObject(clientResponse.getResponse());
                String newAccessToken = responseJson.get("access_token").toString();
                return new OAuthAuthorizationImpl(newAccessToken, authorization.asOAuthAuthorization().getOAuthRefreshToken());
            } catch (JSONException e) {
                throw new APIException(e.getMessage());
            }
        } else {
            throw new APIException("Unexpected return type", null);
        }
    }

    private String getEncodedSecret() {
        String secret;
        try {
            secret = URLEncoder.encode(this.config.getOAuthConsumerSecret(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            secret = this.config.getOAuthConsumerSecret();
        }
        return secret;
    }

    public ApplicationConfiguration getApplicationConfiguration() {
        return config;
    }
}
