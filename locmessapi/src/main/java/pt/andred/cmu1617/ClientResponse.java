package pt.andred.cmu1617;


public class ClientResponse {
    private final Status status;
    private final String response;

    ClientResponse(Status status, String response) {
        this.status = status;
        this.response = response;
    }

    public int getStatusCode() {
        return status.getStatusCode();
    }

    public String getResponse() {
        return response;
    }

    public enum Status {
        OK(200, "OK"),
        CREATED(201, "Created"),
        BAD_REQUEST(400, "Bad Request"),
        UNAUTHORIZED(401, "Unauthorized"),
        PRECONDITION_FAILED(412, "Precondition Failed"),
        INTERNAL_SERVER_ERROR(500, "Internal Server Error");

        private final int statusCode;
        private final String description;

        Status(int statusCode, String description) {
            this.statusCode = statusCode;
            this.description = description;
        }

        public static Status fromStatusCode(int responseCode) {
            switch (responseCode) {
                case 200:
                    return OK;
                case 201:
                    return CREATED;
                case 400:
                    return BAD_REQUEST;
                case 401:
                    return UNAUTHORIZED;
                case 412:
                    return PRECONDITION_FAILED;
                case 500:
                    return INTERNAL_SERVER_ERROR;
            }
            return null;
        }

        public String getDescription() {
            return description;
        }

        public int getStatusCode() {
            return statusCode;
        }
    }
}
