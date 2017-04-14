package cmu1617.andred.pt.locmess.Domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Miguel on 14/04/2017.
 */

public class UserKeywordsDifference {
    Map<String, String> _begin;
    Map<String, String> _end;
    public UserKeywordsDifference(Map<String, String> begin, Map<String, String> end) {
        _begin = begin;
        _end = end;
    }

    public List<Action> getDifferences() {
        List<Action> toReturn = new ArrayList<>();
        for (String key : _begin.keySet()) {
            String value = _begin.get(key);
            if(_end.containsKey(key)) {
                if(!value.equals(_end.get(key))) {
                    toReturn.add(new Action(true,key,_end.get(key)));
                }
            } else {
                toReturn.add(new Action(false,key,null));
            }
        }
        for (String key : _end.keySet()) {
            if(!_begin.containsKey(key)) {
                toReturn.add(new Action(true,key,_end.get(key)));
            }
        }
        return toReturn;
    }

    public class Action {
        public boolean _add;
        public String _keywordName;
        public String _keywordValue;

        public Action (boolean add, String keywordName, String keywordValue) {
            _add = add;
            _keywordName = keywordName;
            _keywordValue = keywordValue;
        }
    }
}
