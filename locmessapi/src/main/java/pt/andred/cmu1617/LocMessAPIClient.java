package pt.andred.cmu1617;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public interface LocMessAPIClient {

    Authorization refreshAccessToken(Authorization oldAuthorization);

    void setAuth(String access_token, String refresh_token);

//    User getUserDetails();

    boolean signup(String email, String password);

    String[] login(String userid, String password);

    void logout();

    JSONObject listLocations();

    String addLocation(String name, String latitude, String longitude, String radius);

    String addLocation(String name, List<String> sddid);

    void deleteLocation(String locationId);

    JSONArray getMessages(String latitude, String longitude, List<String> ssid_list);

    String addMessage(String location_id,String message, String dateBegin, String dateEnd, List<MessageConstraint> list);

    String editProfileKeys(boolean add, String name, String value);

    JSONArray listKeywords();

//    JSONObject newGPSLocation(String name, String latitude, String longitude, String radius);
//    JSONObject newWIFILocation(String name, String ssid_list);
}
