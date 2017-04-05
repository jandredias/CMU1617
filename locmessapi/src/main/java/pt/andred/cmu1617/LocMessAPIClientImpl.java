package pt.andred.cmu1617;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

;

/**
 * Created by andre on 28/03/17.
 */

public final class LocMessAPIClientImpl extends LocMessAPIClientBase implements LocMessAPIClient {

    private static final int TRIES = 5;
    private static LocMessAPIClient _instance;
    private OAuthAuthorization _auth;

    LocMessAPIClientImpl(ApplicationConfiguration config) {
        super(config);
        _auth = null;
    }

    public static LocMessAPIClient getInstance() {
        if (_instance == null) _instance =
                new LocMessAPIClientImpl(
                        new ApplicationConfiguration(
                                "http://newbie.rnl.tecnico.ulisboa.pt:31500",//baseurl
                                "",//Consumer Key
                                "")); //Secret Key
        return _instance;
    }

    @Override
    public Authorization refreshAccessToken(Authorization oldAuthorization) {

        return null;
    }

//    @Override
//    public User getUserDetails() {
//
//        return null;
//    }

    @Override
    public boolean signup(String email, String password) {
        Map<String, String> post = new HashMap<>();
        post.put("user_id", email);
        post.put("password", password);
        for (int i = 0; i < TRIES; i++) {
            JSONObject response = invoke(Endpoint.SIGN_UP, (Map<String, String>) null, post);
            if (response == null) {
                continue;
            }
            if (!response.has("status")) continue;
            if (response.getInt("status") != 200) {
                throw new APIException("HTTP Error: " +
                        response.getInt("status") + " " +
                        (response.has("description") ? response.getString("description") : ""));
            }
            return response.getInt("status") == 200;
        }
        throw new APIException("Couldn't connect to server");
    }

    @Override
    public boolean login(String email, String password){
        Map<String, String> post = new HashMap<>();
        post.put("user_id", email);
        post.put("password", password);
        for (int i = 0; i < TRIES; i++) {
            JSONObject response = invoke(Endpoint.OAUTH_LOGIN, (Map<String, String>) null, post);
            if (response == null) {
                continue;
            }
            if (!response.has("status")) continue;
            if (response.getInt("status") != 200) {
                throw new APIException("HTTP Error: " +
                        response.getInt("status") + " " +
                        (response.has("description") ? response.getString("description") : ""));
            } else {

                if(!response.has("access_token")){
                    throw new APIException("Response is malformed. access_token missing");
                }
                else if(!response.has("refresh_token")){
                    throw new APIException("Response is malformed. refresh_token missing");
                }
                _auth = new OAuthAuthorizationImpl(
                        response.getString("access_token"),
                        response.getString("refresh_token"));
                return response.getInt("status") == 200;
            }
//            return response.getBoolean("success");
        }
        throw new APIException("Couldn't connect to server");
    }

    @Override
    public void logout(String token){
        Map<String, String> post = new HashMap<>();
        post.put("token", token);
        for (int i = 0; i < TRIES; i++) {
            JSONObject response = invoke(Endpoint.SIGN_UP, _auth, null, post);
            if (response == null) {
                continue;
            }
            if (!response.has("status")) continue;
            if (response.getInt("status") != 200) {
                throw new APIException("HTTP Error: " +
                        response.getInt("status") + " " +
                        (response.has("description") ? response.getString("description") : ""));
            }
            _auth = null;
            return;
        }
        throw new APIException("Couldn't connect to server");
    }

    @Override
    public Map<String, Map<String, String>> listLocations(String token) {
        return null;
    }

    public void addLocation(Map<String, String> post){
        for (int i = 0; i < TRIES; i++) {
            JSONObject response = invoke(Endpoint.SIGN_UP, _auth, null, post);
            if (response == null) {
                continue;
            }
            if (!response.has("status")) continue;
            if (response.getInt("status") != 200) {
                throw new APIException("HTTP Error: " +
                        response.getInt("status") + " " +
                        (response.has("description") ? response.getString("description") : ""));
            }
            if (!response.has("success")) {
                throw new APIException("Response is malformed. Token missing");
            }
            if (response.getBoolean("success")) {
                if (!response.has("access_token")) {
                    throw new APIException("Response is malformed. access_token missing");
                } else if (!response.has("refresh_token")) {
                    throw new APIException("Response is malformed. refresh_token missing");
                }
            } else {
                if (!response.has("error")) {
                    throw new APIException("Response is malformed. error missing");
                }
                if (!response.has("description")) {
                    throw new APIException("Response is malformed. error missing");
                }
                throw new APIException(
                        response.getString("error"),
                        response.getString("description"));
            }
        }
        throw new APIException("Couldn't connect to server");
    }
    @Override
    public void addLocation(String token, String name, String latitude, String longitude, int radius) {
        Map<String, String> post = new HashMap<>();
        post.put("location_type", "coordinates");
        post.put("name", name);
        post.put("latitude", latitude);
        post.put("longitude", longitude);
        post.put("radius", radius + "");
        addLocation(post);
    }

    @Override
    public void addLocation(String token, String name, String... sddid) {
        Map<String, String> post = new HashMap<>();
        post.put("location_type", "wifi");
        post.put("name", name);
        post.put("ssid_list", String.join(" ", sddid));
        addLocation(post);
    }

    @Override
    public void deleteLocation(String token, String locationId) {
        // TODO: 02/04/17
    }

    @Override
    public List<Map<String, Map<String, String>>> listMessages(String token) {
        return null;
    }

    @Override
    public void putMessage(String token, String message) {

    }

    @Override
    public void editProfile(String token, String a) {

    }

    @Override
    public void editProfileKeys(String token, String name, String value) {

    }

    @Override
    public void removeProfileKeys(String token, String name) {

    }

    @Override
    public Map<String, String> listProfileKeys(String token, String userid) {
        return null;
    }
}
