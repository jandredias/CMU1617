package pt.andred.cmu1617;

interface OAuthAuthorization extends Authorization {

    String ACCESS_TOKEN = "access_token";
    String REFRESH_TOKEN = "refresh_token";

    String getOAuthAccessToken();

    String getOAuthRefreshToken();
}
