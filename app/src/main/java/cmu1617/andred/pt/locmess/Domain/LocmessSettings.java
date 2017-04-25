package cmu1617.andred.pt.locmess.Domain;

/**
 * Created by miguel on 21/04/17.
 */

public class LocmessSettings {
    private static boolean _trueIfAskedUserAlready = false;
    private static int _periodicity_seconds = 60;
    private static int _periodicity_milli_seconds = 60000;

    public static boolean trueIfAskedUserAlready() {
        if(!_trueIfAskedUserAlready) {
            _trueIfAskedUserAlready = true;
            return false;
        }
        return true;
    }

    public static void logout() {
        _trueIfAskedUserAlready = false;
    }

    public static int getPeriodicitySeconds() {
        return _periodicity_seconds;
    }

    public static int getPeriodicityMilliSeconds() { return _periodicity_milli_seconds; }

    public static void setPeriodicitySeconds(int seconds) {
        _periodicity_milli_seconds = seconds *1000;
        _periodicity_seconds = seconds;
    }
}
