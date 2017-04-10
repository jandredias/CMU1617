package pt.andred.cmu1617;

import org.json.JSONArray;
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
    public void setAuth(String access_token, String refresh_token) {
        _auth = new OAuthAuthorizationImpl(access_token,refresh_token);
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
            if (response.getInt("status") == 500) {
                return false;
            }
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
    public String[] login(String email, String password){
        Map<String, String> post = new HashMap<>();
        post.put("user_id", email);
        post.put("password", password);
        for (int i = 0; i < TRIES; i++) {
            JSONObject response = invoke(Endpoint.OAUTH_LOGIN, (Map<String, String>) null, post);
            if (response == null) {
                continue;
            }
            if (!response.has("status")) continue;
            if (response.getInt("status") == 401) {
                return new String[] {"false"};
            }
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
                return new String[] {response.getString("access_token") , response.getString("refresh_token") , "" + (response.getInt("status") == 200)};
            }
//            return response.getBoolean("success");
        }
        throw new APIException("Couldn't connect to server");
    }

    @Override
    public void logout(){
        _auth = null;
    }

    @Override
    public JSONObject  listLocations() {
        //        if (_auth == null) {
        //        }
        for (int i = 0; i < TRIES; i++) {

            JSONObject response = invoke(Endpoint.LIST_LOCATIONS, _auth, null, null);
            if (response == null) {
                continue;
            }
            if (!response.has("status")) continue;
            if (response.getInt("status") != 200) {
                throw new APIException("HTTP Error: " +
                        response.getInt("status") + " " +
                        (response.has("description") ? response.getString("description") : ""));
            }
            if (response.getInt("status") == 200) {
                if (!response.has("wifi")) {
                    throw new APIException("Response is malformed. wifi missing");
                } else if (!response.has("coordinates")) {
                    throw new APIException("Response is malformed. coordinates missing");
                }

                return response;

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

    public void addLocation(Map<String, String> post){
        for (int i = 0; i < TRIES; i++) {

        }
        throw new APIException("Couldn't connect to server");
    }
    @Override
    public void addLocation(String name, String latitude, String longitude, int radius) {
        Map<String, String> post = new HashMap<>();
        post.put("location_type", "coordinates");
        post.put("name", name);
        post.put("latitude", latitude);
        post.put("longitude", longitude);
        post.put("radius", radius + "");
        addLocation(post);
    }

    @Override
    public void addLocation(String name, String... sddid) {
        Map<String, String> post = new HashMap<>();
        post.put("location_type", "wifi");
        post.put("name", name);
        post.put("ssid_list", String.join(" ", sddid));
        addLocation(post);
    }

    @Override
    public void deleteLocation(String locationId) {
        // TODO: 02/04/17
    }

    @Override
    public List<Map<String, Map<String, String>>> listMessages() {
        return null;
    }

    @Override
    public void putMessage(String message) {

    }

    @Override
    public void editProfile(String a) {

    }

    @Override
    public void editProfileKeys(String name, String value) {

    }

    @Override
    public void removeProfileKeys(String name) {

    }

    @Override
    public JSONArray listKeywords() {
        for (int i = 0; i < TRIES; i++) {

            JSONObject response = invoke(Endpoint.LIST_KEYWORDS, _auth, null, null);
            if (response == null) {
                continue;
            }
            if (!response.has("status")) continue;
            if (response.getInt("status") != 200) {
                throw new APIException("HTTP Error: " +
                        response.getInt("status") + " " +
                        (response.has("description") ? response.getString("description") : ""));
            }
            if (response.getInt("status") == 200) {
                if (!response.has("keywords")) {
                    throw new APIException("Response is malformed. keywords missing");
                }

                return response.getJSONArray("keywords");

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
}
