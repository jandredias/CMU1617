package pt.andred.cmu1617;

class OAuthAuthorizationImpl implements OAuthAuthorization {

    private final String oAuthAccessToken;
    private final String oAuthRefreshToken;

    OAuthAuthorizationImpl(String oAuthAccessToken, String oAuthRefreshToken) {
        this.oAuthAccessToken = oAuthAccessToken;
        this.oAuthRefreshToken = oAuthRefreshToken;
    }

    @Override
    public String getOAuthAccessToken() {
        return oAuthAccessToken;
    }

    @Override
    public String getOAuthRefreshToken() {
        return oAuthRefreshToken;
    }

    @Override
    public HttpRequest authorize(HttpRequest httpRequest) {
        return httpRequest.withGet(OAuthAuthorization.ACCESS_TOKEN, getOAuthAccessToken());
    }

    @Override
    public OAuthAuthorization asOAuthAuthorization() {
        return this;
    }
}
