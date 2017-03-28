package pt.andred.cmu1617;

import java.util.List;
import java.util.Map;

/**
 * Created by andre on 28/03/17.
 */

public final class LocMessAPIClientImpl extends LocMessAPIClientBase implements LocMessAPIClient {

    private static LocMessAPIClient _instance;
    private final OAuthAuthorization _auth;

    LocMessAPIClientImpl(ApplicationConfiguration config) {
        super(config);
        _auth = null;
    }

    public static LocMessAPIClient getInstance() {
        if (_instance == null) _instance =
                new LocMessAPIClientImpl(
                        new ApplicationConfiguration(
                                "http://marge.rnl.tecnico.ulisboa.pt:31000",//baseurl
                                "",//Consumer Key
                                "")); //Secret Key
        return _instance;
    }

    @Override
    public Authorization refreshAccessToken(Authorization oldAuthorization) {
        return null;
    }

    @Override
    public void signup(String email, String password) {

    }

    @Override
    public String login(String userid, String password) {
        return null;
    }

    @Override
    public void logout(String token) {

    }

    @Override
    public Map<String, Map<String, String>> listLocations(String token) {
        return null;
    }

    @Override
    public String addLocation(String token, String latitude, String longitude) {
        return null;
    }

    @Override
    public String addLocation(String token, String... sddid) {
        return null;
    }

    @Override
    public void deleteLocation(String token, String locationId) {

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
