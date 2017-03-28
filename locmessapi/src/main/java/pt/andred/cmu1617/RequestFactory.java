package pt.andred.cmu1617;

class RequestFactory {
    static HttpRequest fromEndpoint(ApplicationConfiguration config, Endpoint endpoint,
                                    String... endpointArgs) {
        return new HttpRequest(endpoint.generateEndpoint(config), endpoint.getHttpMethod()).accepts(endpoint
                .getMediaType());
    }
}
