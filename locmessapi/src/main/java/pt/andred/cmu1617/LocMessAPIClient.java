package pt.andred.cmu1617;

import java.util.List;
import java.util.Map;

public interface LocMessAPIClient {

    Authorization refreshAccessToken(Authorization oldAuthorization);

    void signup(String email, String password);

    String login(String userid, String password);

    void logout(String token);

    Map<String, Map<String, String>> listLocations(String token);

    String addLocation(String token, String latitude, String longitude);

    String addLocation(String token, String... sddid);

    void deleteLocation(String token, String locationId);

    List<Map<String, Map<String, String>>> listMessages(String token);

    void putMessage(String token, String message);

    void editProfile(String token, String a);

    void editProfileKeys(String token, String name, String value);

    void removeProfileKeys(String token, String name);

    Map<String, String> listProfileKeys(String token, String userid);
}
