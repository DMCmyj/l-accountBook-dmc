package com.catpudding.pudding_keep_account.utils;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleyRequestUtil {
    private static volatile VolleyRequestUtil volleyRequestUtil;

    private static Context mContext;
    private static RequestQueue requestQueue;

    public static VolleyRequestUtil getInstance(Context context){
        if(volleyRequestUtil == null){
            synchronized (VolleyRequestUtil.class){
                if(volleyRequestUtil == null){
                    mContext = context;
                    volleyRequestUtil = new VolleyRequestUtil();
                    requestQueue = Volley.newRequestQueue(context);
                }
            }
        }
        return volleyRequestUtil;
    }

    private VolleyRequestUtil(){

    }

    public static RequestQueue getRequestQueue() {
        return requestQueue;
    }
}
