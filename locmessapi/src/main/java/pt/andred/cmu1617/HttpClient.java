package pt.andred.cmu1617;

import java.io.IOException;


interface HttpClient {
    ClientResponse handleHttpRequest(HttpRequest httpRequest) throws IOException;
}
