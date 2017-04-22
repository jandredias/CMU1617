package cmu1617.andred.pt.locmess.Domain;

/**
 * Created by miguel on 21/04/17.
 */

public class Settings {
    private static boolean _trueIfAskedUserAlready = false;
    private static int _periodicity_seconds = 300;

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
}
