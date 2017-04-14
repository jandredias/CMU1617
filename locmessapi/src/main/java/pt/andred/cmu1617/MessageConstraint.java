package pt.andred.cmu1617;

/**
 * Created by Miguel on 14/04/2017.
 */

public class MessageConstraint {
    String _id;
    String _keywordValue;
    Boolean _equal;
    final String separator ="[===]-'";

    public MessageConstraint (String id, String keywordValue, Boolean equal) {
        _id = id;
        _keywordValue = keywordValue;
        _equal = equal;
    }

    public String getID() {
        return _id;
    }

    public String getKeywordValue(){
        return _keywordValue;
    }

    public boolean getEqual(){
        return _equal;
    }

    @Override
    public String toString() {
        return (_equal ? "==" : "!=" ) + separator + _id + separator + _keywordValue;
    }
}