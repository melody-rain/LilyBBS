package com.os.utility;

import android.os.Bundle;

/**
 * Created by Jin on 2014/9/18.
 */
public class LogoutHelper {

    public static boolean Logout(Bundle bundle)
    {
        return DocParser.logout(bundle);
    }
}
