package pt.andred.cmu1617;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;

/**
 * Created by andre on 05/11/16.
 */

public class HttpRequest {
    private final String baseUrl;
    private final HttpMethod httpMethod;
    private MediaType acceptedMediaType;

    private Map<String, String> _get;
    private Map<String, String> _post;

    public HttpRequest(String baseUrl, HttpMethod httpMethod) {
        this.baseUrl = baseUrl;
        this.httpMethod = httpMethod;
        this._get = new HashMap<>();
        this._post = new HashMap<>();
    }

    public HttpRequest withGet(Map<String, String> get) {
        this._get.putAll(get);
        return this;
    }

    public HttpRequest withPost(Map<String, String> post) {
        this._post.putAll(post);
        return this;
    }

    public HttpRequest withGet(String name, String value) {
        this._get.put(name, value);
        return this;
    }

    public HttpRequest withPost(String name, String value) {
        this._post.put(name, value);
        return this;
    }

    public HttpRequest accepts(MediaType mediaType) {
        this.acceptedMediaType = mediaType;
        return this;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public Map<String, String> get() {
        return this._get;
    }

    public Map<String, String> post() {
        return this._post;
    }

    public String getUrl() {
        String url = baseUrl;
        if (this._get != null && this._get.size() > 0) {
            url += "?";
            for (Map.Entry<String, String> entry : _get.entrySet())
                url += entry.getKey() + "=" + entry.getValue() + "&";
        }
        return url;
    }

    public MediaType getAcceptedMediaType() {
        return acceptedMediaType;
    }

    public HttpRequest withAuthorization(Authorization authorization) {
        return authorization.authorize(this);
    }
}
