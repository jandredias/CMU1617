package pt.andred.cmu1617;

import java.io.IOException;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpClientOkHttp implements HttpClient {
    private static final String TAG = "HttpClientOkHttp";
    private final OkHttpClient _client = new OkHttpClient();

    @Override
    public ClientResponse handleHttpRequest(HttpRequest httpRequest) throws IOException {
        if (httpRequest.getHttpMethod() == HttpMethod.GET)
            return get(httpRequest);
        else if (httpRequest.getHttpMethod() == HttpMethod.POST)
            return post(httpRequest);
        else
            return null;
    }

    private ClientResponse get(HttpRequest httpRequest) throws IOException {
        Request request = new Request.Builder().
                url(httpRequest.getUrl()).
                build();

        Response response = _client.newCall(request).execute();
        String responseBody = response.body().string();
        return new ClientResponse(ClientResponse.Status.fromStatusCode(200), responseBody);
    }

    private ClientResponse post(HttpRequest httpRequest) throws IOException {
        FormBody.Builder requestBody = new FormBody.Builder();
        for (Map.Entry<String, String> entry : httpRequest.post().entrySet()) {
            requestBody.add(entry.getKey(), entry.getValue());
        }

        RequestBody body = requestBody.build();
        Request request = new Request.Builder()
                .url(httpRequest.getUrl())
                .post(body)
                .build();
        try {
            Response response = _client.newCall(request).execute();
            String responseBody = response.body().string();
            ClientResponse.Status status = ClientResponse.Status.fromStatusCode(response.code());
            return new ClientResponse(status, responseBody);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
