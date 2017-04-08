package pt.andred.cmu1617;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public interface LocMessAPIClient {

    Authorization refreshAccessToken(Authorization oldAuthorization);

//    User getUserDetails();

    boolean signup(String email, String password);

    boolean login(String userid, String password);

    void logout();

    JSONObject listLocations();

    void addLocation(String name, String latitude, String longitude, int radius);

    void addLocation(String name, String... sddid);

    void deleteLocation(String locationId);

    List<Map<String, Map<String, String>>> listMessages();

    void putMessage(String message);

    void editProfile(String a);

    void editProfileKeys(String name, String value);

    void removeProfileKeys(String name);

    Map<String, String> listProfileKeys(String userid);
}
