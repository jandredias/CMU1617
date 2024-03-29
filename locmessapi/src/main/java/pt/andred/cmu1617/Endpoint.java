package pt.andred.cmu1617;

import org.json.JSONObject;

import okhttp3.MediaType;

/**
 * Created by andre on 28/03/17.
 */

enum Endpoint {
    SIGN_UP("/user/signup", Scope.AUTH, HttpMethod.POST, MediaType.parse("application/json; charset=utf-8"), JSONObject.class),
    OAUTH_LOGIN("/oauth/login", Scope.AUTH, HttpMethod.POST, MediaType.parse("application/json; charset=utf-8"), JSONObject.class),

    SET_KEYWORD("/user/keyword", Scope.AUTH, HttpMethod.POST, MediaType.parse("application/json; charset=utf-8"), JSONObject.class),
    DELETE_KEYWORD("/dummy/dummy", Scope.AUTH, HttpMethod.POST, MediaType.parse("application/json; charset=utf-8"), JSONObject.class),
    LIST_KEYWORDS("/keyword/list", Scope.AUTH, HttpMethod.POST, MediaType.parse("application/json; charset=utf-8"), JSONObject.class),
    LIST_LOCATIONS("/location/list", Scope.AUTH, HttpMethod.POST, MediaType.parse("application/json; charset=utf-8"), JSONObject.class),
//    CREATE_LOCATIONS("/dummy/dummy", Scope.AUTH, HttpMethod.POST, MediaType.parse("application/json; charset=utf-8"), JSONObject.class),
    DELETE_LOCATIONS("/location/delete", Scope.AUTH, HttpMethod.POST, MediaType.parse("application/json; charset=utf-8"), JSONObject.class),

    DELETE_MESSAGE("/message/delete", Scope.AUTH, HttpMethod.POST, MediaType.parse("application/json; charset=utf-8"), JSONObject.class),
    GET_MESSAGES("/message/list", Scope.AUTH, HttpMethod.POST, MediaType.parse("application/json; charset=utf-8"), JSONObject.class),
    PUT_MESSAGE("/message/new", Scope.AUTH, HttpMethod.POST, MediaType.parse("application/json; charset=utf-8"), JSONObject.class),

    OAUTH_USER_DIALOG("/oauth/userdialog", Scope.AUTH, HttpMethod.GET, MediaType.parse("application/json; charset=utf-8"), JSONObject.class),

    OAUTH_ACCESS_TOKEN("/oauth/access_token", Scope.AUTH, HttpMethod.POST, MediaType.parse("application/json; charset=utf-8"), JSONObject.class),

    GET_CERTIFICATE("/certificate/sign", Scope.AUTH, HttpMethod.GET, MediaType.parse("application/json; charset=utf-8"), JSONObject.class),

    OAUTH_REFRESH_ACCESS_TOKEN("/oauth/refresh_token", Scope.AUTH, HttpMethod.POST, MediaType.parse("application/json; charset=utf-8"), JSONObject.class),
    NEW_LOCATION("/location/new",    Scope.AUTH, HttpMethod.POST, MediaType.parse("application/json; charset=utf-8"), JSONObject.class);




    private final static String ENDPOINT_PREFIX = "/api/locmess/v1";
    private String pathRegex;
    private Scope scope;
    private HttpMethod httpMethod;
    private MediaType mediaType;
    private Class<?> responseClass;

    Endpoint(String path, Scope scope, HttpMethod httpMethod, MediaType mediaType, Class<?> responseClass) {
        this.pathRegex = path;
        this.scope = scope;
        this.httpMethod = httpMethod;
        this.mediaType = mediaType;
        this.responseClass = responseClass;
    }

    public String generateEndpoint(ApplicationConfiguration config) {
        return config.getBaseURL() + ENDPOINT_PREFIX + String.format(pathRegex);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public Scope getScope() {
        return scope;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public Class<?> getResponseClass() {
        return responseClass;
    }
}
