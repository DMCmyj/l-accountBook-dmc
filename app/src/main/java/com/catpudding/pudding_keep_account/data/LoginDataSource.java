package com.catpudding.pudding_keep_account.data;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.catpudding.pudding_keep_account.data.model.LoggedInUser;
import com.catpudding.pudding_keep_account.utils.VolleyRequestUtil;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password) {

        try {
            // TODO: handle loggedInUser authentication
            return null;
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }

    public Result<String> getCode(String key){
        RequestQueue requestQueue = VolleyRequestUtil.getRequestQueue();

        return null;
    }
}