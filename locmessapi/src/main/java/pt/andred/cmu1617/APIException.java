package pt.andred.cmu1617;

/**
 * Created by andre on 28/03/17.
 */

public final class APIException extends RuntimeException {

    String _description;

    APIException(String error) {
        super(error);
    }

    APIException(Exception e) {
        super(e);
    }

    APIException(Exception e, String description) {
        super(e);

        _description = description;
    }

    APIException(String error, String description) {
        super(error);
        _description = description;
    }
}
