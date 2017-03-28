package pt.andred.cmu1617;

interface Authorization {

    HttpRequest authorize(HttpRequest webResource);

    OAuthAuthorization asOAuthAuthorization();
}
