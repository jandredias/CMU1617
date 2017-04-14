package cmu1617.andred.pt.locmess;

import android.util.Log;

import cmu1617.andred.pt.locmess.Domain.UserProfile;
import pt.andred.cmu1617.LocMessAPIClientImpl;

/**
 * Created by miguel on 10/04/17.
 */

public class Login {
    private SQLDataStoreHelper _db;
    public Login(SQLDataStoreHelper db) {
        _db = db;
    }
    public boolean needNewLogin() {
        Log.wtf("Login","new login");
        UserProfile user = new UserProfile(_db);
        if (user.refreshToken() != null && user.accessToken() != null) {

            LocMessAPIClientImpl.getInstance().setAuth(user.accessToken(),user.refreshToken());
            return false;
        }
        return true;
    }

    public void registerLogin(String username,String accessToken,String refreshToken) {
        UserProfile user = new UserProfile(_db);
        user.newLogin(username,accessToken,refreshToken);
    }




}