package pt.andred.cmu1617;

import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

public class ApplicationConfiguration {
    private String _baseurl;
    private String _oauthConsumerKey;
    private String _oauthConsumerSecret;
    private Locale _locale;

    public ApplicationConfiguration(String baseurl, String OAuthConsumerKey, String OAuthConsumerSecret, Locale locale) {
        this._baseurl = baseurl;
        this._oauthConsumerKey = OAuthConsumerKey;
        this._oauthConsumerSecret = OAuthConsumerSecret;
        this._locale = locale;
    }

    public ApplicationConfiguration(String baseurl, String OAuthConsumerKey, String OAuthConsumerSecret) {
        this(baseurl, OAuthConsumerKey, OAuthConsumerSecret, new Locale("pt", "pt"));
    }

    public static ApplicationConfiguration fromProperties(Properties props) {
        return new ApplicationConfiguration(
                props.getProperty("base.url"),
                props.getProperty("oauth.consumer.key"),
                props.getProperty("oauth.consumer.secret"));
    }

    public static ApplicationConfiguration fromPropertyFilename(String filename) {
        Properties props = new Properties();
        try {
            props.load(ApplicationConfiguration.class.getResourceAsStream(filename));
            return new ApplicationConfiguration(
                    props.getProperty("base.url"),
                    props.getProperty("oauth.consumer.key"),
                    props.getProperty("oauth.consumer.secret"));
        } catch (IOException e) {
            //throw new SnapCityClientException("Could not load " + filename + " file.", e);
            //FIXME
            throw new RuntimeException("Could not load " + filename + " file.", e);
        }
    }

    /**
     * The base URL of the FenixEdu installation (without slash)
     *
     * @return the base URL of the FenixEdu installation
     */
    public String getBaseURL() {
        return this._baseurl;
    }

    /**
     * The application's OAuth Consumer Key
     *
     * @return the applications' OAuth Consumer Key
     */
    public String getOAuthConsumerKey() {
        return this._oauthConsumerKey;
    }

    /**
     * The application's OAuth Consumer Secret
     *
     * @return the applications' OAuth Consumer Secret
     */
    public String getOAuthConsumerSecret() {
        return this._oauthConsumerSecret;
    }

    public Locale getLocale() {
        return this._locale;
    }
}
