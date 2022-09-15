package com.catpudding.pudding_keep_account.ui.login;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.catpudding.pudding_keep_account.data.LoginRepository;
import com.catpudding.pudding_keep_account.data.Result;
import com.catpudding.pudding_keep_account.data.model.LoggedInUser;
import com.catpudding.pudding_keep_account.R;
import com.catpudding.pudding_keep_account.utils.BaseConfiguration;

import org.json.JSONObject;

public class LoginViewModel extends AndroidViewModel {

    private RequestQueue requestQueue;
    private SavedStateHandle handle;

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private MutableLiveData<Bitmap> code = new MutableLiveData<>();
    private MutableLiveData<String> inputCode = new MutableLiveData<>();
    private String key;

    public LoginViewModel(@NonNull Application application,@NonNull SavedStateHandle savedStateHandle) {
        super(application);
        requestQueue = Volley.newRequestQueue(application);
        handle = savedStateHandle;
    }


    public MutableLiveData<String> getInputCode() {
        return inputCode;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    /**
     * 完成登录操作
     * @param username 用户名
     * @param password 密码
     * @param code 验证码
     */
    public void login(String username, String password,String code) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username",username);
            jsonObject.put("password",password);
        }catch (Exception e){
            e.printStackTrace();
            Log.d("requestError","json数据对象添加失败");
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                BaseConfiguration.BASE_URL + "sys/login",
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("code").equals("200")){
                                JSONObject userInfo = response.getJSONObject("result");
                                LoggedInUser user = new LoggedInUser();
                                user.setAvatar(userInfo.getString("avatar"));
                                user.setToken(userInfo.getString("token"));
                                user.setDisplayName(userInfo.getString("username"));

                                SharedPreferences sharedPreferences = getApplication().getSharedPreferences("user_info", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("avatar",user.getAvatar());
                                editor.putString("token",user.getToken());
                                editor.putString("username",user.getDisplayName());
                                editor.commit();
                                loginResult.setValue(new LoginResult(new LoggedInUserView(user.getDisplayName())));
                            }else{
                                Toast.makeText(getApplication(), response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("requestError",error.getMessage());
                        Toast.makeText(getApplication(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    public void loginDataChanged(String username, String password,String code) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null,null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password,null));
        } else if(code.length() == 4){
            loginFormState.setValue(new LoginFormState(null,null,R.string.inivalid_code));
        }else
        {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }

    public MutableLiveData<Bitmap> getCode() {
        return code;
    }

    /**
     * 获取验证码
     */
    public void requestCode(){
        String key = Integer.toString((int)(Math.random() * 100000));
        setKey(key);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                BaseConfiguration.BASE_URL + "sys/randomImage/" + key,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String result = response.getString("result");
                            //将base64编码格式的图片解析
                            byte[] decodedString = Base64.decode(result.split(",")[1], Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString,0,decodedString.length);
                            getCode().setValue(decodedByte);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("requestError",error.getMessage());
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }
}