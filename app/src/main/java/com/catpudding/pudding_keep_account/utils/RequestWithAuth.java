package com.catpudding.pudding_keep_account.utils;

import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class RequestWithAuth extends JsonObjectRequest  {

    private String token;

    public RequestWithAuth(String url, Response.Listener<JSONObject> listener, @Nullable Response.ErrorListener errorListener,String token) {
        super(url, listener, errorListener);
        this.token = token;
    }

    public RequestWithAuth(int method, String url, @Nullable JSONObject jsonRequest, Response.Listener<JSONObject> listener, @Nullable Response.ErrorListener errorListener,String token) {
        super(method, url, jsonRequest, listener, errorListener);
        this.token = token;
    }

    public Map<String,String> getHeaders() throws AuthFailureError{
        Map<String,String > headers = new LinkedHashMap<>();
        SharedPreferences sharedPreferences;
        //自定义请求头
        headers.put("X-Access-Token",token);
        return headers;
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {

        return super.parseNetworkResponse(response);
    }

}
