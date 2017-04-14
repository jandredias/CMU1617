package pt.andred.cmu1617;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;


/**
 * Created by andre on 28/03/17.
 */

class LocMessAPIClientBase {
    private static final int TRIES = 1;

    private final ApplicationConfiguration config;

    private final HttpClient client;
    private final SecretKey secretKey;

    LocMessAPIClientBase(ApplicationConfiguration config) {
        this.config = config;
        client = new HttpClientOkHttp();
        KeyGenerator keyGenerator = null;
        try {
            keyGenerator = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        keyGenerator.init(128);
        secretKey = keyGenerator.generateKey();
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
//        get.put("lang", config.getLocale().getLanguage() + "-" + config.getLocale().getCountry());

        HttpRequest httpRequest = RequestFactory.fromEndpoint(config, endpoint);

        if (authorization != null) {
            httpRequest.withAuthorization(authorization);
        }


//        Map<String, String> getSecure = new HashMap<>();
//        Map<String, String> postSecure = new HashMap<>();
//        IvParameterSpec ivSpec = null;
//        try {
//            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
//            byte[] iv = new byte[cipher.getBlockSize()];
//            new SecureRandom().nextBytes(iv);
//            ivSpec = new IvParameterSpec(iv);
//            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
//            for (Map.Entry<String, String> entry : get.entrySet()) {
//                getSecure.put(
//                        Base64.getEncoder().encodeToString(cipher.doFinal(entry.getKey().getBytes())),
//                        Base64.getEncoder().encodeToString(cipher.doFinal(entry.getValue().getBytes()))
//                );
//            }
//            for (Map.Entry<String, String> entry : post.entrySet()) {
//                postSecure.put(
//                        Base64.getEncoder().encodeToString(cipher.doFinal(entry.getKey().getBytes())),
//                        Base64.getEncoder().encodeToString(cipher.doFinal(entry.getValue().getBytes()))
//                );
//            }
//            postSecure.put("key", Base64.getEncoder().encodeToString(secretKey.getEncoded()));
//            postSecure.put("iv", Base64.getEncoder().encodeToString(iv));
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (NoSuchProviderException e) {
//            e.printStackTrace();
//        } catch (NoSuchPaddingException e) {
//            e.printStackTrace();
//        } catch (InvalidKeyException e) {
//            e.printStackTrace();
//        } catch (BadPaddingException e) {
//            e.printStackTrace();
//        } catch (IllegalBlockSizeException e) {
//            e.printStackTrace();
//        } catch (InvalidAlgorithmParameterException e) {
//            e.printStackTrace();
//        }
//        httpRequest.withGet(getSecure);
//        httpRequest.withPost(postSecure);

        httpRequest.withGet(get);
        httpRequest.withPost(post);

        try {

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
//            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);

            ClientResponse clientResponse = client.handleHttpRequest(httpRequest);
            if (clientResponse == null) {
                throw new APIException("Couldn't connect to server");
            }
            if (clientResponse.getStatusCode() == 401) {
                JSONObject jsonResponse;
                jsonResponse = new JSONObject(clientResponse.getResponse());
                String error = jsonResponse.get("error").toString();
                String errorDescription = jsonResponse.get("error_description").toString();
                throw new APIException(error, errorDescription);
            }
            if (endpoint.getResponseClass().equals(JSONArray.class)) {
                byte[] byteResponse = Base64.getDecoder().decode(clientResponse.getResponse());
                String response = cipher.doFinal(byteResponse).toString();
                return (T) new JSONArray(response);
            } else if (endpoint.getResponseClass().equals(JSONObject.class)) {
//                byte[] byteResponse = Base64.getDecoder().decode(clientResponse.getResponse());
//                String response = cipher.doFinal(byteResponse).toString();
//                return (T) new JSONObject(response);
                return (T) new JSONObject(clientResponse.getResponse());
            } else if (endpoint.getResponseClass().equals(File.class)) {
                byte[] byteResponse = Base64.getDecoder().decode(clientResponse.getResponse());
                return (T) cipher.doFinal(byteResponse);
            } else {
                throw new APIException("Could not identify return type", null);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            throw new APIException(e, e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            throw new APIException(e, e.getMessage());
        } catch (NoSuchPaddingException |
                NoSuchAlgorithmException |
                NoSuchProviderException |
//                InvalidKeyException |
                BadPaddingException | IllegalBlockSizeException /*| InvalidAlgorithmParameterException*/ e) {
            e.printStackTrace();
            throw new APIException(e);
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
